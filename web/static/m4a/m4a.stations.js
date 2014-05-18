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

        setStartStation: function (station_id) {
            var view = m4a.view;
            view.$metroStartInputID.val("");
            view.$metroStartInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-in', '']);
            view.$document.triggerHandler('/url/update', ['stat-start', station_id]);
        },


        setEndStation: function(station_id) {
            var view = m4a.view;
            view.$metroEndInputID.val("");
            view.$metroEndInputName.val("");
            view.$document.triggerHandler('/url/update', ['portal-out', '']);
            view.$document.triggerHandler('/url/update', ['stat-end', station_id]);
        },


        updateLocalPortals: function() {
            if (this.portals.in.data) { this.updatePortalsLayer('in', this.portals.in.data); }
            if (this.portals.out.data) { this.updatePortalsLayer('out', this.portals.out.data); }
        },


        updatePortalsByAjax: function (stationId, type, callback) {
            var context = this;
            return $.ajax({
              dataType: "json",
              url: m4a.viewmodel.url.proxy + global_config.language +'/' + global_config.city + "/portals/search",
              data: {
                station: stationId,
                direction: type
              }
            }).done(function(data) {
                context.portals[type]['data'] = data;
                context.portals[type]['markers'] = {};
                context.updatePortalsLayer(type, data);
                context.portalsSelected[type] = { feature: null, marker: null };
                if (callback) { callback.f.apply(context, callback.args); }
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
                                marker = L.marker(latlng, { icon: context.buildSelectedIcon() });
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
            }
        },


        selectPortal: function(type, feature, marker) {
            var view = m4a.view,
                domPrefix = 'Start';
            if (type === 'out') { domPrefix = 'End'; }
            view['$metro' + domPrefix + 'InputID'].val(feature.id);
            view['$metro' + domPrefix + 'InputName'].val(feature.properties.name || feature.id);

            if (this.portalsSelected[type].marker && this.portalsSelected[type].feature) {
                this.portalsSelected[type].marker.setIcon(
                    this.buildIconStation(this.portalsSelected[type].feature, type));
            }
            this.portalsSelected[type].marker = marker;
            this.portalsSelected[type].feature = feature;
            this.portalsSelected[type].marker.setIcon(this.buildSelectedIcon());

            view.$document.triggerHandler('/url/update', ['portal-' + type, feature.id]);
        },


        isFeatureSelected: function(feature, type) {
            if (this.portalsSelected[type].feature) {
                return this.portalsSelected[type].feature.id === feature.id;
            }
        },


        buildIconStation: function(station, type) {
            if (m4a.profiles.validateStation(station)) {
                return L.icon({ iconUrl: '/static/img/' + type + '.png', iconAnchor: [8, 8]});
            } else {
                return L.icon({ iconUrl: '/static/img/invalid.png', iconAnchor: [8, 8]});
            }
        },


        buildSelectedIcon: function() {
            return L.icon({iconUrl: '/static/img/check.png', iconAnchor: [8, 8]});
        },


        selectPortalByFeatureId: function(id, type) {
            if (this.portals[type].markers[id]) {
                var feature = $.grep(this.portals[type].data.features, function(feature){ return feature.id == id; });
                if (feature.length > 0) {
                    this.selectPortal(type, feature[0], this.portals[type].markers[id]);
                    return true;
                }
            }
            return false;
        }
    })
}) (jQuery, m4a)