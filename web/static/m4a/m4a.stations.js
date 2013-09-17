(function ($, m4a) {
    m4a.stations = {};

    $.extend(m4a.stations, {
        setStartStation: function (station_id) {
            var view = m4a.view;
            view.$metroStartInputID.val("");
            view.$metroStartInputName.val("");
            view.$document.triggerHandler('/url/update', ['start', '']);
            view.$document.triggerHandler('/url/update', ['stat-start', station_id]);
        },

        updateInputsData: function(station_id) {
            $.ajax({
              dataType: "json",
              url: m4a.viewmodel.url.proxy + "portals/search",
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
                                return L.marker(
                                    latlng, {
                                        icon: L.icon({
                                            iconUrl: '/static/img/in.png'
                                        })
                                    }
                                )
                            },
                            onEachFeature: function(feature, layer) {
                                layer.on('click', function (e) {
                                    var view = m4a.view;
                                    view.$metroStartInputID.val(feature.id);
                                    view.$metroStartInputName.val(feature.properties.name || feature.id);
                                    view.$document.triggerHandler('/url/update', ['start', feature.id]);
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
            view.$document.triggerHandler('/url/update', ['end', '']);
            view.$document.triggerHandler('/url/update', ['stat-end', station_id]);
        },


        updateOutputsData: function(station_id) {
            $.ajax({
              dataType: "json",
              url: m4a.viewmodel.url.proxy + "portals/search",
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
                                return L.marker(
                                    latlng, {
                                        icon: L.icon({
                                            iconUrl: '/static/img/out.png',
                                        })
                                    }
                                )
                            },
                            onEachFeature: function(feature, layer) {
                                layer.on('click', function (e) {
                                    var view = m4a.view;
                                    view.$metroEndInputID.val(feature.id);
                                    view.$metroEndInputName.val(feature.properties.name || feature.id);
                                    view.$document.triggerHandler('/url/update', ['end', feature.id]);
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