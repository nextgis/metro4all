(function ($, m4a) {
    m4a.stations = {};

    $.extend(m4a.stations, {
        setStartStation: function (station_id) {
            var view = m4a.view;
            view.$metroStartInputID.val("");
            view.$metroStartInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-start', '']);
            view.$document.triggerHandler('/url/update', ['stat-start', station_id]);
        },

        portalsSelected: {
            in: null,
            out: null
        },

        updateInputsData: function(station_id) {
            var context = this;

            $.ajax({
              dataType: "json",
              url: m4a.viewmodel.url.proxy + global_config.city + "/portals/search",
              data: {
                station: station_id,
                direction: "in"
              }
            }).done(function(data) {
                // Очищаем слой входов
                if (typeof inPortals !== 'undefined') {
                    m4a.viewmodel.metroStartInputMap.removeLayer(inPortals);
                }
                if (data.features.length != 0) {

                    // Добавляем входы на карту
                    inPortals = L.geoJson(
                        data, {
                            pointToLayer: function(feature, latlng) {
                                context.portalsSelected.in = null;
                                if (m4a.profiles.validateStation(feature)) {
                                    return L.marker( latlng, { icon: L.icon({ iconUrl: '/static/img/in.png' }) } );
                                } else {
                                    return L.marker( latlng, { icon: L.icon({ iconUrl: '/static/img/invalid.png' }) } );
                                }
                            },
                            onEachFeature: function(feature, layer) {
                                layer.on('click', function (e) {
                                    var view = m4a.view;
                                    view.$metroStartInputID.val(feature.id);
                                    view.$metroStartInputName.val(feature.properties.name || feature.id);

                                    if (context.portalsSelected.in) {
                                        context.portalsSelected.in.setIcon(L.icon({iconUrl: '/static/img/in.png'}));
                                    }
                                    context.portalsSelected.in = e.target;
                                    e.target.setIcon(L.icon({iconUrl: '/static/img/check.png'}));

                                    view.$document.triggerHandler('/url/update', ['portal-start', feature.id]);
                                });
                            }
                        }
                    ).addTo(m4a.viewmodel.metroStartInputMap);

                    // Устанавливаем новый охват
                    m4a.viewmodel.metroStartInputMap.fitBounds(inPortals.getBounds(), {padding: [0, 10]});
                }
            });
        },


        setEndStation: function(station_id) {
            var view = m4a.view;
            view.$metroEndInputID.val("");
            view.$metroEndInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-end', '']);
            view.$document.triggerHandler('/url/update', ['stat-end', station_id]);
        },


        updateOutputsData: function(station_id) {
            var context = this;

            $.ajax({
              dataType: "json",
              url: m4a.viewmodel.url.proxy + global_config.city + "/portals/search",
              data: {
                station: station_id,
                direction: "out"
              }
            }).done(function(data) {
                // Очищаем слой выходов
                if (typeof outPortals !== 'undefined') {
                    m4a.viewmodel.metroEndInputMap.removeLayer(outPortals);
                }
                if (data.features.length != 0) {

                    // Добавляем выходы на карту
                    outPortals = L.geoJson(
                        data, {
                            pointToLayer: function(feature, latlng) {
                                context.portalsSelected.out = null;
                                if (m4a.profiles.validateStation(feature)) {
                                    return L.marker( latlng, { icon: L.icon({ iconUrl: '/static/img/out.png' }) } );
                                } else {
                                    return L.marker( latlng, { icon: L.icon({ iconUrl: '/static/img/invalid.png' }) } );
                                }
                            },
                            onEachFeature: function(feature, layer) {
                                layer.on('click', function (e) {
                                    var view = m4a.view;
                                    view.$metroEndInputID.val(feature.id);
                                    view.$metroEndInputName.val(feature.properties.name || feature.id);

                                    if (context.portalsSelected.out) {
                                        context.portalsSelected.out.setIcon(L.icon({iconUrl: '/static/img/out.png'}));
                                    }
                                    context.portalsSelected.out = e.target;
                                    e.target.setIcon(L.icon({iconUrl: '/static/img/check.png'}));

                                    view.$document.triggerHandler('/url/update', ['portal-end', feature.id]);
                                });
                            }
                        }
                    ).addTo(m4a.viewmodel.metroEndInputMap);

                    // Устанавливаем новый охват
                    m4a.viewmodel.metroEndInputMap.fitBounds(outPortals.getBounds(), {padding: [0, 10]});
                }
            });
        }
    })
}) (jQuery, m4a)