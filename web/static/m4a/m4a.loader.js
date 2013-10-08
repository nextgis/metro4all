

(function ($, m4a) {
    m4a.loader = {};

    $.extend(m4a.loader, {
        init: function() {
            this.setDomOptions();
            m4a.url.init();
            m4a.profiles.init();
            m4a.popup.init();
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

            view.$routePanel = $('#routePanel');
        }
    })
}) (jQuery, m4a)