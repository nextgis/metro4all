(function ($, m4a) {
    m4a.stations = {};

    $.extend(m4a.stations, {
        portals: {
            in: {
                layer: null,
                data: null,
                markers: {}
            },
            out: {
                layer: null,
                data: null,
                markers: {}
            }
        },

        portalsSelected: {
            in: { feature: null, marker: null },
            out: { feature: null, marker: null }
        },

        popupSelected: null,

        selectStationFromMap: function (obj) {
            var context = this,
                popupSettings = {
                    closeButton: false,
                    className: 'station-selected-popup'
                };

            this.popupSelected = L.popup(popupSettings)
                .setLatLng(obj.latlng)
                .setContent('<button id="btnHence" type="button" class="btn">' + m4a.resources.routes.from +
                    '</button><button id="btnThere" type="button" class="btn">' + m4a.resources.routes.to +
                    '</button>')
                .openOn(m4a.viewmodel.mainMap);

            $('#btnHence').on('click', function () {
                m4a.view.$metroStartStation.select2("val", context.id).trigger('change');
                if (context.popupSelected) {
                    m4a.viewmodel.mainMap.closePopup(context.popupSelected);
                    context.popupSelected = null;
                }
            });

            $('#btnThere').on('click', function () {
                m4a.view.$metroEndStation.select2("val", context.id).trigger('change');
                if (context.popupSelected) {
                    m4a.viewmodel.mainMap.closePopup(context.popupSelected);
                    context.popupSelected = null;
                }
            });
        },

        setStartStation: function (station_id) {
            var view = m4a.view;
            view.$metroStartInputID.val("");
            view.$metroStartInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-in', '']);
            view.$document.triggerHandler('/url/update', ['stat-start', station_id]);
        },


        setEndStation: function (station_id) {
            var view = m4a.view;
            view.$metroEndInputID.val("");
            view.$metroEndInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-out', '']);
            view.$document.triggerHandler('/url/update', ['stat-end', station_id]);
        },


        updateLocalPortals: function () {
            if (this.portals.in.data) {
                this.updatePortalsLayer('in', this.portals.in.data);
            }
            if (this.portals.out.data) {
                this.updatePortalsLayer('out', this.portals.out.data);
            }
        },


        updatePortalsByAjax: function (stationId, type, callback) {
            var context = this;
            return $.ajax({
                dataType: "json",
                url: m4a.viewmodel.url.proxy + global_config.language + '/' + global_config.city + "/portals/search",
                data: {
                    station: stationId,
                    direction: type
                }
            }).done(function (data) {
                context.portals[type]['data'] = data;
                context.portals[type]['markers'] = {};
                context.updatePortalsLayer(type, data);
                context.portalsSelected[type] = { feature: null, marker: null };
                if (callback) {
                    callback.f.apply(context, callback.args);
                }
            });
        },


        updatePortalsLayer: function (type, data) {
            var context = this;

            if (this.portals[type]['layer']) { // Очищаем слой выходов
                m4a.viewmodel.mainMap.removeLayer(this.portals[type]['layer']);
            }

            if (data.features.length != 0) {
                this.portals[type]['layer'] = L.geoJson(data, {
                        pointToLayer: function (feature, latlng) {
                            var marker;
                            if (context.isFeatureSelected(feature, type)) {
                                marker = L.marker(latlng, { icon: context.buildSelectedIcon(feature) });
                                context.portalsSelected[type] = { feature: feature, marker: marker };
                            } else {
                                marker = L.marker(latlng, { icon: context.buildIconStation(feature, type)});
                            }
//                            context.portals[type].markers[feature.id] = marker;
                            return marker;
                        },
                        onEachFeature: function (feature, layer) {
                            context.portals[type].markers[feature.id] = layer;
                            layer.on('click', function (e) {
                                context.selectPortal(type, feature, e.target);
                                $("#mainform").submit();
                            });
                        }
                    }
                ).addTo(m4a.viewmodel.mainMap);
                this.bindPortalMarkersTooltips(type);
            }
        },

        bindPortalMarkersTooltips: function (type) {
            var portalMarkerId,
                portalMarker,
                $portalMarkerIcon;

            for (portalMarkerId in this.portals[type].markers) {
                    if (this.portals[type].markers.hasOwnProperty(portalMarkerId)) {
                        portalMarker = this.portals[type].markers[portalMarkerId];

                        if (!portalMarker._icon || !portalMarker.feature.properties || !portalMarker.feature.properties.name || !portalMarker.feature.properties.meetcode) {
                            continue;
                        }

                        $portalMarkerIcon = $(portalMarker._icon);
                        new Opentip(
                            $portalMarkerIcon, '#' + portalMarker.feature.properties.meetcode + ': ' + portalMarker.feature.properties.name,
                            {
                                background: '#d6f1f8',
                                borderColor: '#85d4e9'
                            });
//                        debugger;
                    }
                }
        },

        selectPortal: function (type, feature, marker) {
            var view = m4a.view,
                domPrefix = 'Start';
            if (type === 'out') {
                domPrefix = 'End';
            }
            view['$metro' + domPrefix + 'InputID'].val(feature.id);
            view['$metro' + domPrefix + 'InputName'].val(feature.properties.name || feature.id);

            if (this.portalsSelected[type].marker && this.portalsSelected[type].feature) {
                this.portalsSelected[type].marker.setIcon(
                    this.buildIconStation(this.portalsSelected[type].feature, type));
            }
            this.portalsSelected[type].marker = marker;
            this.portalsSelected[type].feature = feature;
            this.portalsSelected[type].marker.setIcon(this.buildSelectedIcon(feature));

            view.$document.triggerHandler('/url/update', ['portal-' + type, feature.id]);
        },


        isFeatureSelected: function (feature, type) {
            if (this.portalsSelected[type].feature) {
                return this.portalsSelected[type].feature.id === feature.id;
            }
        },


        buildIconStation: function (station, type) {
            if (m4a.profiles.validateStation(station)) {
                return L.divIcon({ 
                                   iconSize: [24, 24],
                                   className: "marker-station-portal",
                                   html: "<span>" + station.properties.meetcode + "</span>",
                                   iconAnchor: [12, 12]
                                });
            } else {
                return L.divIcon({
                                   iconSize: [24, 24],
                                   className: "marker-station-portal-inaccessible",
                                   html: "<span>" + station.properties.meetcode + "</span>",
                                   iconAnchor: [12, 12]
                                });
            }
        },


        buildSelectedIcon: function (station) {
            return L.divIcon({
                               iconSize: [24, 24],
                               className: "marker-station-portal-checked",
                               html: "<span>" + station.properties.meetcode + "</span>",
                               iconAnchor: [12, 12]
                            });
        },


        selectPortalByFeatureId: function (id, type) {
            if (this.portals[type].markers[id]) {
                var feature = $.grep(this.portals[type].data.features, function (feature) {
                    return feature.id == id;
                });
                if (feature.length > 0) {
                    this.selectPortal(type, feature[0], this.portals[type].markers[id]);
                    return true;
                }
            }
            return false;
        }
    })
})(jQuery, m4a)
