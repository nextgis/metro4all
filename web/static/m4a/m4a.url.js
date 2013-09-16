(function ($, m4a) {
    m4a.url = {};

    $.extend(m4a.url, {
        init: function () {
            this.bindEvents();
        },


        bindEvents: function() {
            var context = this,
                $document = m4a.view.$document;

            $document.on('/url/update', function(e, key, value) {
                var uri = context.updateQueryStringParameter(window.location.href, key, value);
                window.location.href = uri;
            })
        },


        // from http://stackoverflow.com/questions/5999118/add-or-update-query-string-parameter
        updateQueryStringParameter: function (uri, key, value) {
            uri.replace('?', '#');
            var re = new RegExp("([#|&])" + key + "=.*?(&|$)", "i"),
                separator = uri.indexOf('#') !== -1 ? "&" : "#";

            if (uri.match(re)) {
                return uri.replace(re, '$1' + key + "=" + value + '$2');
            }
            else {
                return uri + separator + key + "=" + value;
            }
        },


        parse: function () {
            var view = m4a.view,
                stat_start = this.getURLParameter('stat-start'),
                start = this.getURLParameter('start'),
                stat_end = this.getURLParameter('stat-end'),
                end = this.getURLParameter('end'),
                route = this.getURLParameter('route');

            if (stat_start) {
                view.$metroStartStation.select2('val', stat_start);
                m4a.stations.updateInputsData(stat_start);
                if (start) {
                    view.$metroStartInputID.val(start);
                    view.$metroStartInputName.val(start);
                }
            }

            if (stat_end) {
                view.$metroEndStation.select2('val', stat_end);
                m4a.stations.updateOutputsData(stat_start);
                if (end) {
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