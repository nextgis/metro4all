(function ($, m4a) {
    m4a.url = {};

    $.extend(m4a.url, {
        url: null,
        urlParameters: { },

        init: function () {
            this.bindEvents();
        },


        bindEvents: function() {
            var context = this,
                $document = m4a.view.$document;

            $document.on('/url/update', function(e, key, value) {
                var uri = context.updateQueryStringParameter(key, value);
                window.location.href = uri;
            })
        },


        updateUrl: function(key, value) {
            var uri = this.updateQueryStringParameter(key, value);
            window.location.href = uri;
        },


        // from http://stackoverflow.com/questions/5999118/add-or-update-query-string-parameter
        updateQueryStringParameter: function (key, value) {
            var urlCompiled = location.protocol + '//' + location.host + location.pathname + "#",
                delimiter = '&',
                firstIteration = true;

            if (value) {
                this.urlParameters[key] = value;
            } else {
                delete this.urlParameters[key];
            }

            for (var key in this.urlParameters) {
                if (this.urlParameters.hasOwnProperty(key)) {
                    if (firstIteration) {
                        firstIteration = false;
                        urlCompiled += key + '=' + this.urlParameters[key];
                        continue;
                    }
                    urlCompiled += delimiter + key + '=' + this.urlParameters[key];
                }
            }

            return urlCompiled;
        },


        parse: function () {
            var view = m4a.view,
                stat_start = this.getURLParameter('stat-start'),
                portalIn = this.getURLParameter('portal-in'),
                stat_end = this.getURLParameter('stat-end'),
                portalOut = this.getURLParameter('portal-out'),
                route = this.getURLParameter('route'),
                portalInSelectCallback = null,
                portalOutSelectCallback = null;

            if (stat_start) {
                this.urlParameters['stat-start'] = stat_start;
                view.$metroStartStation.select2('val', stat_start);
                if (portalIn) {
                    this.urlParameters['portal-in'] = portalIn;
                    view.$metroStartInputID.val(portalIn);
                    view.$metroStartInputName.val(portalIn);
                    portalInSelectCallback = {f: m4a.stations.selectPortalByFeatureId, args: [portalIn, 'in']};
                }
                m4a.stations.updatePortalsByAjax(stat_start, 'in', portalInSelectCallback);
            }

            if (stat_end) {
                this.urlParameters['stat-end'] = stat_end;
                view.$metroEndStation.select2('val', stat_end);
                if (portalOut) {
                    this.urlParameters['portal-out'] = portalOut;
                    view.$metroEndInputID.val(portalOut);
                    view.$metroEndInputName.val(portalOut);
                    portalOutSelectCallback = {f: m4a.stations.selectPortalByFeatureId, args: [portalOut, 'out']};
                }
                m4a.stations.updatePortalsByAjax(stat_end, 'out', portalOutSelectCallback);
            }

            if (stat_start && portalIn && stat_end && portalOut) {
                $("#mainform").submit();
            }
        },


        getURLParameter: function (name) {
            return decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(window.location.href) || [, ''])[1]);
        }
    })
})(jQuery, m4a)