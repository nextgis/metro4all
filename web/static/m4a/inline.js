$(document).ready(function () {
    m4a.loader.init();

    var url = m4a.viewmodel.url.proxy,
        viewmodel = m4a.viewmodel,
        view = m4a.view;
    viewmodel.mainMap = L.map('mainMap').setView(global_config.mainmap.center, global_config.mainmap.zoom);
    viewmodel.miniMaps = {}
    viewmodel.miniMaps.in = L.map('metroStartInput', {zoomControl: false, attributionControl: false}).setView(global_config.minimap.center, global_config.minimap.zoom);
    viewmodel.miniMaps.out = L.map('metroEndInput', {zoomControl: false, attributionControl: false}).setView(global_config.minimap.center, global_config.minimap.zoom);

    // Заполнение выпадающих списков
    $.ajax(url + global_config.city + "/stations").done(function (data) {
        m4a.view.$metroStartStation.select2({width: "100%", data: data});
        m4a.view.$metroEndStation.select2({width: "100%", data: data});

        // Поле выбора станции входа
        view.$metroStartStation.on("change", function () {
            m4a.stations.setStartStation(this.value);
            m4a.stations.updatePortalsByAjax(this.value, 'in');
        });

        // Поле выбора станции выхода
        view.$metroEndStation.on("change", function () {
            m4a.stations.setEndStation(this.value);
            m4a.stations.updatePortalsByAjax(this.value, 'out');
        });

        // Карта для выбора входов
        L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
            maxZoom: 18
        }).addTo(m4a.viewmodel.miniMaps.in);

        // Карта для выбора выходов
        L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
            maxZoom: 18
        }).addTo(m4a.viewmodel.miniMaps.out);

        // Карта для отображения маршрутов
        L.tileLayer.grayscale('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
            maxZoom: 18
        }).addTo(m4a.viewmodel.mainMap);

        $("#mainform").submit(function () {
            var view = m4a.view,
                start_station = view.$metroStartStation.val(),
                end_station = view.$metroEndStation.val();

            if (start_station.length == 0 || end_station.length == 0) {
                // todo: use bootstrap
                alert(m4a.resources.inline.start_st);
            } else if (start_station == end_station) {
                alert(m4a.resources.inline.eq_st);
            } else {
                $('.pagination').empty();
                $('#routePanel').empty();
                $.ajax({
                    dataType: "json",
                    url: url + global_config.city + "/routes/search",
                    data: $("#mainform").serialize()
                }).done(function (data) {
                        m4a.routes.buildRoutes(data);
                });
            }
            return false;
        });

        m4a.url.parse();
    });
});