(function ($, m4a) {
    m4a.stations = {};

    $.extend(m4a.stations, {
        portals: {
            in: {
                layer: null,
                data: null
            },
            out: {
                layer: null,
                data: null
            }
        },

        portalsSelected: {
            in: { feature: null, marker: null },
            out: { feature: null, marker: null }
        },

        setStartStation: function (station_id) {
            var view = m4a.view;
            view.$metroStartInputID.val("");
            view.$metroStartInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-start', '']);
            view.$document.triggerHandler('/url/update', ['stat-start', station_id]);
        },


        setEndStation: function(station_id) {
            var view = m4a.view;
            view.$metroEndInputID.val("");
            view.$metroEndInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-end', '']);
            view.$document.triggerHandler('/url/update', ['stat-end', station_id]);
        },


        updateLocalPortals: function() {
            if (this.portals.in.data) { this.updatePortalsLayer('in', this.portals.in.data); }
            if (this.portals.out.data) { this.updatePortalsLayer('out', this.portals.out.data); }
        },


        updatePortalsByAjax: function (stationId, type) {
            var context = this;
            $.ajax({
              dataType: "json",
              url: m4a.viewmodel.url.proxy + global_config.city + "/portals/search",
              data: {
                station: stationId,
                direction: type
              }
            }).done(function(data) {
                context.portals[type]['data'] = data;
                context.updatePortalsLayer(type, data);
                context.portalsSelected[type] = { feature: null, marker: null };
            });
        },


        updatePortalsLayer: function (type, data) {
            var context = this;

            if (this.portals[type]['layer']) { // Очищаем слой выходов
                m4a.viewmodel.miniMaps[type].removeLayer(this.portals[type]['layer']);
            }

            if (data.features.length != 0) {
                this.portals[type]['layer'] = L.geoJson(data, {
                        pointToLayer: function (feature, latlng) {
                            if (context.isFeatureSelected(feature, type)) {
                                return L.marker(latlng, { icon: context.buildSelectedIcon() });
                            }
                            return L.marker(latlng, { icon: context.buildIconStation(feature, type)});
                        },
                        onEachFeature: function (feature, layer) {
                             layer.on('click', function (e) {
                                var view = m4a.view;
                                view.$metroEndInputID.val(feature.id);
                                view.$metroEndInputName.val(feature.properties.name || feature.id);

                                if (context.portalsSelected[type].marker && context.portalsSelected[type].feature) {
                                    context.portalsSelected[type].marker.setIcon(
                                        context.buildIconStation(context.portalsSelected[type].feature, type));
                                }
                                context.portalsSelected[type].marker = e.target;
                                context.portalsSelected[type].feature = feature;
                                context.portalsSelected[type].marker.setIcon(context.buildSelectedIcon());

                                view.$document.triggerHandler('/url/update', ['portal-' + type, feature.id]);
                            });
                        }
                    }
                ).addTo(m4a.viewmodel.miniMaps[type]);
                m4a.viewmodel.miniMaps[type].fitBounds(this.portals[type]['layer'].getBounds(), {padding: [0, 10]});
            }
        },


        isFeatureSelected: function(feature, type) {
            if (this.portalsSelected[type].feature) {
                return this.portalsSelected[type].feature.id === feature.id;
            }
        },


        buildIconStation: function(station, type) {
            if (m4a.profiles.validateStation(station)) {
                return L.icon({ iconUrl: '/static/img/' + type + '.png' });
            } else {
                return L.icon({ iconUrl: '/static/img/invalid.png' });
            }
        },


        buildSelectedIcon: function() {
            return L.icon({iconUrl: '/static/img/check.png'});
        }
    })
}) (jQuery, m4a)