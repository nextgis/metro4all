(function ($, m4a) {
    m4a.loader = {};

    $.extend(m4a.loader, {
        init: function() {
            this.setDomOptions();
            m4a.url.init();
            m4a.profiles.init();
        },


        setDomOptions: function() {
            var view = m4a.view;

            view.$document = $(document);

            view.$metroStartStation = $('#metroStartStation');
            view.$metroStartInputName = $('#metroStartInputName');
            view.$metroStartInputID = $('#metroStartInputID');

            view.$metroEndStation = $('#metroEndStation');
            view.$metroEndInputName = $('#metroEndInputName');
            view.$metroEndInputID = $('#metroEndInputID');

            view.$metroStartStationExtent = $('#metroStartStationExtent');
            view.$metroEndStationExtent = $('#metroEndStationExtent');

            view.$routePanel = $('#routePanel');
        }
    })
}) (jQuery, m4a)