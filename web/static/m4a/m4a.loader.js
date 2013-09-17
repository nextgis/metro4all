var m4a = {};

m4a.viewmodel = {};
m4a.view = {};

(function ($, m4a) {
    m4a.loader = {};

    $.extend(m4a.loader, {
        init: function() {
            this.setDomOptions();
            m4a.viewmodel.url={'proxy' : typeof ajax !== 'undefined' ? ajax : ''};
            m4a.url.init();
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
        }
    })
}) (jQuery, m4a)