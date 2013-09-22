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
                start = this.getURLParameter('portal-start'),
                stat_end = this.getURLParameter('stat-end'),
                end = this.getURLParameter('portal-end'),
                route = this.getURLParameter('route');

            if (stat_start) {
                this.urlParameters['stat-start'] = stat_start;
                view.$metroStartStation.select2('val', stat_start);
                m4a.stations.updateInputsData(stat_start);
                if (start) {
                    this.urlParameters['portal-start'] = start;
                    view.$metroStartInputID.val(start);
                    view.$metroStartInputName.val(start);
                }
            }

            if (stat_end) {
                this.urlParameters['stat-end'] = stat_end;
                view.$metroEndStation.select2('val', stat_end);
                m4a.stations.updateOutputsData(stat_start);
                if (end) {
                    this.urlParameters['portal-end'] = end;
                    view.$metroEndInputID.val(end);
                    view.$metroEndInputName.val(end);
                }
            }

            if (stat_start && start && stat_end && end) {
                $("#mainform").submit();
            }
        },


        getURLParameter: function (name) {
            return decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(window.location.href) || [, ''])[1]);
        }
    })
})(jQuery, m4a)