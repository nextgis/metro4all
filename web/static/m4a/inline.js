$(document).ready(function () {
    m4a.loader.init();

    var url = m4a.viewmodel.url.proxy,
        viewmodel = m4a.viewmodel,
        view = m4a.view;
    
    viewmodel.mainMap = L.map('mainMap').setView(global_config.mainmap.center, global_config.mainmap.zoom);
    viewmodel.stationMarkers = [];
    viewmodel.lineSegments = null;

    // Отрисовываем станции на карте
    $.ajax(url + "data/" + global_config.city + "/stations.csv").done(function (data) {
        csv2geojson.csv2geojson(data, {
            latfield: "lat",
            lonfield: "lon",
            delimiter: ";"
        }, function(err, geojson) {
            L.geoJson(
                geojson, {
                    pointToLayer: function(feature, lonlat) {
                        var stationMarker = L.marker(lonlat, {
                            icon: L.divIcon({
                                iconSize: [16, 16],
                                className: "marker-station marker-line-" + feature.properties.id_line
                            }),
                            riseOnHover: true
                        })
                        .bindLabel(feature.properties['name_' + global_config.language] || feature.properties['name_en'])
                        .on('click', m4a.stations.selectStationFromMap.bind(feature.properties));
                        viewmodel.stationMarkers.push(stationMarker);
                        return stationMarker;
                    }
                }
            ).addTo(viewmodel.mainMap);
        });
    });

    // Отрисовываем линии на карте
    $.ajax(url + "data/" + global_config.city + "/lines.geojson").done(function (data) {
        var swappedColors = Object.keys(m4a.routes.COLORS).reduce(function(obj,key) {
            obj[ m4a.routes.COLORS[key] ] = key;
            return obj;
        }, {});
        viewmodel.lineSegments = L.geoJson(JSON.parse(data), {
            style: function(feature) {
                return {
                    opacity: 1,
                    color: swappedColors[feature.properties.id_line]
                };
            }
        });
        viewmodel.lineSegments.addTo(viewmodel.mainMap);
    });

    // Заполнение выпадающих списков
    $.ajax(url + global_config.language + "/" + global_config.city + "/stations").done(function (data) {
        var sortResults = function(results, container, query) {
            var filteredResults = [];
            for (var i=0; i<results.length; i++) {
                var r = results[i];
                if (!r.children || r.children.length > 0) filteredResults.push(r);
            }
            return filteredResults;
        }
        m4a.view.$metroStartStation.select2({width: "100%", data: data, placeholder: m4a.resources.inline.st_st, sortResults: sortResults});
        m4a.view.$metroEndStation.select2({width: "100%", data: data, placeholder: m4a.resources.inline.end_st, sortResults: sortResults});

        // Поле выбора станции входа
        view.$metroStartStation.on("change", function () {
            m4a.stations.setStartStation(this.value);
            m4a.stations.updatePortalsByAjax(this.value, 'in')
                .done(function(data){
                    view.$metroStartStationExtent.trigger('click');
                });
            view.$metroStartStationExtent.prop("disabled", false);
            $("#mainform").submit();
        });

        // Поле выбора станции выхода
        view.$metroEndStation.on("change", function () {
            m4a.stations.setEndStation(this.value);
            m4a.stations.updatePortalsByAjax(this.value, 'out')
                .done(function(data){
                    view.$metroEndStationExtent.trigger('click');
                });
            view.$metroEndStationExtent.prop("disabled", false);
            $("#mainform").submit();
        });

        // Кнопка перехода к охвату станции входа
        view.$metroStartStationExtent.on("click", function () {
            m4a.viewmodel.mainMap.fitBounds(m4a.stations.portals['in']['layer'].getBounds());
        });

        // Кнопка перехода к охвату станции выхода
        view.$metroEndStationExtent.on("click", function () {
            m4a.viewmodel.mainMap.fitBounds(m4a.stations.portals['out']['layer'].getBounds());
        });

        // Карта для отображения маршрутов
        L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
            maxZoom: 18
        }).addTo(m4a.viewmodel.mainMap);

        $("#mainform").submit(function (from_url) {
            var view = m4a.view,
                start_station = view.$metroStartStation.val(),
                end_station = view.$metroEndStation.val();
                portal_in = view.$metroStartInputID.val();
                portal_out = view.$metroEndInputID.val();

            if (start_station.length == 0 || end_station.length == 0) {
                // todo: use bootstrap
                console.log(m4a.resources.inline.start_st);
            } else if (start_station == end_station) {
                console.log(m4a.resources.inline.eq_st);
            } else {
                $('.pagination').empty();
                $('#routePanel').empty();

                $.blockUI({ message: 'Processing...' });
                $.ajax({
                    dataType: "json",
                    url: url + global_config.language + "/" + global_config.city + "/routes/search",
                    data: $("#mainform").serialize()
                }).done(function (data) {
                    m4a.routes.buildRoutes(data);

                    // Активируем первый маршрут
                    // Охват на маршрут включаем только в случае, если выбраны оба выхода
                    if ((start_station && end_station) && !((portal_in && !portal_out) || (!portal_in && portal_out))) {
                        $('.pagination li').first().trigger('click');
                    } else {
                        $('.pagination li').first().trigger('click', [false]);
                    }

                    $.unblockUI();
                });
            }
            return false;
        });

        m4a.url.parse();
    });
});