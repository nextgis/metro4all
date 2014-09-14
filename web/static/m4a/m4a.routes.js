(function ($, m4a) {
    m4a.routes = {};

    $.extend(m4a.routes, {

        COLORS: {
            "#ed1b35": 1,
            "#44b85c": 2,
            "#0078bf": 3,
            "#19c1f3": 4,
            "#894e35": 5,
            "#f58631": 6,
            "#8e479c": 7,
            "#ffcb31": 8,
            "#a1a2a3": 9,
            "#b3d445": 10,
            "#79cdcd": 11,
            "#acbfe1": 12
        },

        buildRoutes: function (data) {
            var context = this,
                routes = data.result;

            // Кнопки переключения маршрутов
            $.each(routes, function (i, item) {
                $('.pagination').append('<li data-route-id="' + i +
                    '"><a href="javascript:void(0)">' + (i + 1) + '</a></li>');
            });

            // Обработчики нажатия кнопок
            $('.pagination li').click(function (event, zoom) {
                var $this = $(this),
                    route_index = parseInt($this.data('route-id'), 10);
                $('.pagination li').removeClass('active');
                $this.addClass('active');
                m4a.view.$routePanel.empty();
                context.showRoute(routes, route_index);
                if (zoom === undefined) {
                    context.zoomRoute(routes[route_index].route);
                }
                // m4a.view.$document.triggerHandler('/url/update', ['route', route_index + 1]);
            });
        },

        clickPageRoute: function (event, zoom) {

        },

        indexForHidden: 0,
        fillBarriers: function (barriers) {
            var isStationAvailable = true,
                isIndicatorAvailable,
                context = this,
                c = "",
                profileName = m4a.viewmodel.profile.name,
                profileBarriersIndicators = m4a.profiles.barriersIndicatorsByProfile[profileName];

            $.each(profileBarriersIndicators.visible, function (index, indicatorName) {
                if (context.barriersIndicators[indicatorName]) {
                    if (m4a.profiles.profileBarriersRestrictions[profileName][indicatorName]) {
                        isIndicatorAvailable = m4a.profiles.profileBarriersRestrictions[profileName][indicatorName](barriers);
                        if (!isIndicatorAvailable && isStationAvailable) {
                            isStationAvailable = false;
                        }
                    }
                    c += context.barriersIndicators[indicatorName](barriers, isIndicatorAvailable);

                } else {
                    console.log('Barrier indicator is not found: ' + indicatorName);
                }
            });

            this.indexForHidden += 1;
            c += '<li class="show-hidden-params" data-state="hidden" data-hidden-id="' + this.indexForHidden + '">' +
                m4a.resources.routes.show + '</li>'

            c += '<ul class="barries-hidden-' + this.indexForHidden + '" style="display: none;">';

            $.each(profileBarriersIndicators.hidden, function (index, controlName) {
                if (context.barriersIndicators[controlName]) {
                    c += context.barriersIndicators[controlName](barriers);
                } else {
                    console.log('Control is not found: ' + controlName);
                }
            });

            c += '</ul>';

            return '<ul class="' + (isStationAvailable ? 'obstacles available' : 'obstacles unavailable') + '">' + c + '</ul>';
        },


        barriersIndicators: {
            max_width: function (barriers, isIndicatorAvailable) {
                return (isIndicatorAvailable ? "<li><strong>" : "<li class='invalid'><strong>") + m4a.resources.routes.wch_w + "</strong> " +
                    m4a.resources.routes.wch_w1 + barriers['max_width'] + m4a.resources.routes.wch_w2 + "</li>";
            },

            min_step: function (barriers, isIndicatorAvailable) {
                var c = '';

                if ((barriers['min_step'] == 0) && (barriers['min_step_ramp'] == 0)) {
                    c += "<li class='empty'>" + m4a.resources.routes.n_str + "</li>";
                } else {
                    c += "<li><strong>" + m4a.resources.routes.stps + "</strong> " + barriers['min_step'] + "</li>";
                    c += "<li><strong>" + m4a.resources.routes.n_ramp + "</strong> " + barriers['min_step_ramp'] + "</li>";
                }

                return c;
            },

            min_step_min_step_ramp: function (barriers, isIndicatorAvailable) {
                if ((barriers['min_step'] == 0) && (barriers['min_step_ramp'] == 0)) {
                    return "<li class='empty'>" + m4a.resources.routes.n_str + "</li>";
                } else {
                    return isIndicatorAvailable ? "<li>" : "<li class='invalid'>" + "<strong>" + m4a.resources.routes.stps + "</strong> " + barriers['min_step'] + "</li>" +
                        isIndicatorAvailable ? "<li>" : "<li class='invalid'>" + m4a.resources.routes.n_ramp + "</strong> " + barriers['min_step_ramp'] + "</li>";
                }
            },


            lift: function (barriers, isIndicatorAvailable) {
                var c = "<li>" + {true: m4a.resources.routes.elev_y, false: m4a.resources.routes.elev_n}[barriers['lift']];

                if (barriers['lift']) {
                    c += m4a.resources.routes.elev_y_1 + barriers['lift_minus_step'] + m4a.resources.routes.elev_y_2;
                }
                c += "</li>";

                return c;
            },

            min_max_rail_width: function (barriers, isIndicatorAvailable) {
                var c = '';

                if ((barriers['min_rail_width']) && (barriers['max_rail_width'])) {
                    c += "<li><strong>" + m4a.resources.routes.min_max + "</strong> "
                        + barriers['min_rail_width'] + " &ndash; " + barriers['max_rail_width']
                        + m4a.resources.routes.cm;
                } else {
                    c += "<li class='empty'>" + m4a.resources.routes.no_r;
                }
                c += "</li>";

                return c;
            },

            max_angle: function (barriers, isIndicatorAvailable) {
                var c = '';

                if (barriers['max_angle']) {
                    c += "<li><strong>" + m4a.resources.routes.slope + "</strong> "
                        + barriers['max_angle'] + "&deg;";
                } else {
                    c += "<li class='empty'>" + m4a.resources.routes.no_lev_surf;
                }

                c += "</li>";

                return c;
            }
        },

        schemeIconTemplate: Mustache.compile('{{#schemeExists}}<a class="scheme"' +
            ' href="{{path}}" data-lightbox="{{schemeExists}}" title="{{name}}"></a>{{/schemeExists}}'),

        zoomRoute: function (route) {
            // Охват на маршрут
            var xmin = route[0].coordinates[1],
                ymin = route[0].coordinates[0],
                xmax = route[0].coordinates[1],
                ymax = route[0].coordinates[0];
            $.each(route, function (i, item) {
                xmin = (item.coordinates[1]) < xmin ? item.coordinates[1] : xmin;
                ymin = (item.coordinates[0]) < ymin ? item.coordinates[0] : ymin;
                xmax = (item.coordinates[1]) > xmax ? item.coordinates[1] : xmax;
                ymax = (item.coordinates[0]) > ymax ? item.coordinates[0] : ymax;
            });
            m4a.viewmodel.mainMap.fitBounds([
                [ymin, xmin],
                [ymax, xmax]
            ]);
        },

        showRoute: function (routes, index) {
            // Вывод списка станций, входящих в маршрут
            var context = this,
                content = "<ul class='route'>",
                currentRoute = routes[index].route;

            content += this._addTerminalStation(0, 'enter', 'entr', routes[index].portals['portal_from'], currentRoute);

            $.each(currentRoute, function (i, item) {
                var condition = (i == 0) ? item.station_type == 'regular' :
                    (item.station_type == 'regular' && currentRoute[i - 1].station_type != 'interchange');

                if (condition) {
                    content += "<li class='station line-" + context._getLineIndexForRouteItem(item) + "'>" + item.station_name +
                        context.schemeIconTemplate({
                            schemeExists: item.schema,
                            path: m4a.viewmodel.pathToSchemes + item.schema,
                            name: item.station_name
                        }) +
                        "</li>"
                } else if (item.station_type == 'interchange') {
                    var nextStation = currentRoute[i + 1];
                    content += "<li class=" + "'transition from-line-" + context._getLineIndexForRouteItem(item) + " to-line-" +
                        context._getLineIndexForRouteItem(nextStation) + "'>" + item.station_name +
                        " (" + item.station_line.name + ")" + " &rarr; " + nextStation.station_name +
                        " (" + nextStation.station_line.name + ")" +
                        context.schemeIconTemplate({
                            schemeExists: nextStation.schema,
                            path: m4a.viewmodel.pathToSchemes + nextStation.schema,
                            name: nextStation.station_name
                        });
                    if (item.barriers) {
                        content += context.fillBarriers(item.barriers);
                    }
                    content += "</li>"
                }
            });

            content += this._addTerminalStation(currentRoute.length - 1, 'exit', 'exit', routes[index].portals['portal_to'], currentRoute);
            content += "</ul>";
            content += '<a href="' + m4a.resources.routes.help_link + '" target="_blank">' + m4a.resources.routes.help + '</a>';

            m4a.view.$routePanel.append(content);

            this.bindIndicatorsEvents();

            // Отображение маршрута на карте
            if (typeof route !== 'undefined') {
                m4a.viewmodel.mainMap.removeLayer(route);
            }
            route = L.layerGroup();
            $.each(currentRoute, function (i, item) {
                // Маркеры станций
                route.addLayer(L.marker(
                        item.coordinates,
                        {
                            icon: L.divIcon({
                                className: 'marker-station marker-line-' + context._getLineIndexForRouteItem(item) +
                                    (i == 0 ? ' marker-enter' : (i == (currentRoute.length - 1) ? ' marker-exit' : '')),
                                iconSize: [16, 16]
                            })
                        }).bindLabel(item.station_name)
                ).addTo(m4a.viewmodel.mainMap);

                // Сегменты маршрута
                if (i != 0) {
                    route.addLayer(
                        L.polyline(
                            [currentRoute[i - 1].coordinates, item.coordinates],
                            {
                                color: item.station_line.color,
                                opacity: 1
                            })
                    ).addTo(m4a.viewmodel.mainMap);
                }
            });
        },

        _addTerminalStation: function (i, className, resourceName, portal, currentRoute) {
            var lineClass = currentRoute && currentRoute.length > 0 ?
                    ' line-' + this._getLineIndexForRouteItem(currentRoute[i]) : '',
                fullClassName = [className, lineClass].join(' '),
                result = "<li class='" + fullClassName + "'>" + m4a.resources.routes[resourceName];

            if (portal) {
                var barriers = portal.barriers;
                if (barriers) {
                    result += this.fillBarriers(barriers);
                }
            } else {
                result += "<ul class='obstacles'>";
                result += "<li>" + m4a.resources.routes.obt_arent_sh_en + "</li>";
                result += "</ul>";
            }
            result += "</li>";
            return result;
        },

        _getLineIndexByStationColor: function (stationColor) {
            return m4a.routes.COLORS[stationColor];
        },

        _getLineIndexForRouteItem: function (routeItem) {
            return this._getLineIndexByStationColor(routeItem.station_line.color);
        },

        bindIndicatorsEvents: function () {
            $('li.show-hidden-params').off('click').on('click', function () {
                $('ul.barries-hidden-' + $(this).data('hidden-id')).toggle("start", function () {
                    var $toggleHiddenIndicators = $(this).siblings('li.show-hidden-params'),
                        previousState = $toggleHiddenIndicators.data('state');
                    if (previousState === 'hidden') {
                        $toggleHiddenIndicators.text(m4a.resources.routes.hide);
                        $toggleHiddenIndicators.data('state', 'visible');
                    } else if (previousState === 'visible') {
                        $toggleHiddenIndicators.text(m4a.resources.routes.show);
                        $toggleHiddenIndicators.data('state', 'hidden');
                    }
                });
            });
        }
    })
})
(jQuery, m4a)
