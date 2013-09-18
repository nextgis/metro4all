<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="static/img/favicon.ico" type="image/x-icon" />

    <title>Metro4all</title>

    <!-- Bootstrap core CSS -->
    <link href="static/bootstrap-3.0.0/dist/css/bootstrap.css" rel="stylesheet">

    <!-- Select2 plugin -->
    <link href="static/select2-3.4.2/select2.css" rel="stylesheet"/>

    <!-- Leaflet -->
    <link href="static/leaflet-0.6.4/leaflet.css" rel="stylesheet"/>
    <link href="static/leaflet.label.css" rel="stylesheet"/>
    <!--[if lte IE 8]>
      <link href="leaflet-0.6.4/leaflet.ie.css" rel="stylesheet"/>
    <![endif]-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../../assets/js/html5shiv.js"></script>
      <script src="../../assets/js/respond.min.js"></script>
    <![endif]-->

    <!-- m4a CSS -->
    <link href="static/css/m4a.css" rel="stylesheet">

  </head>

  <body>

    <div class="container">
    <div class="row">

      <!-- Left panel -->
      <div class="col-md-3">
      <form id="mainform" role="form">
        <legend>Откуда</legend>
        <div class="form-group">
          <label for="metroStartStation">Станция:</label>
          <div>
            <input id="metroStartStation" name="station_from" type="hidden">
          </div>
        </div>
        <div class="form-group">
          <label for="metroStartInputID">Вход:</label>
          <input class="form-control" id="metroStartInputName" type="text" placeholder="Выберите выход на карте..." disabled>
          <input name="portal_from" class="form-control" id="metroStartInputID" type="hidden">
          <div id="metroStartInput" style="height: 150px;"></div>
          <em>Нажмите на значок входа, чтобы его выбрать</em>
        </div>
        <legend>Куда</legend>
        <div class="form-group">
          <label for="metroEndStation">Станция:</label>
          <div>
            <input id="metroEndStation" name="station_to" type="hidden">
          </div>
        </div>
        <div class="form-group">
          <label for="metroEndInputID">Выход:</label>
          <input class="form-control" id="metroEndInputName" type="text" placeholder="Выберите выход на карте..." disabled>
          <input name="portal_to" class="form-control" id="metroEndInputID" type="hidden">
          <div id="metroEndInput" style="height: 150px;"></div>
          <em>Нажмите на значок выхода, чтобы его выбрать</em>
        </div>
        <button type="submit" class="btn btn-primary">Проложить маршрут</button>
      </form>
      </div>

      <div id="map" class="col-md-6">
          <legend>Карта</legend>
          <div id="mainMap" style="height: 480px;"></div>
      </div>

      <!-- Right panel -->
      <div class="col-md-3">
        <legend>Маршрут</legend>
        <ul class="route-paging pagination pagination-sm"></ul>
        <div id="routePanel" class="{{config['route_css_class']}}"></div>
      </div>
    </div>
    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script>
        var global_config = {
          minimap: {'center': {{config['minimap']['center']}}, 'zoom': {{config['minimap']['zoom']}}},
          mainmap: {'center': {{config['mainmap']['center']}}, 'zoom': {{config['mainmap']['zoom']}}},
          city: "{{config['city']}}"
        }
    </script>
    <script src="static/bootstrap-3.0.0/assets/js/jquery.js"></script>
    <script src="static/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>
    <script src="static/select2-3.4.2/select2.js"></script>
    <script src="static/select2-3.4.2/select2_locale_ru.js"></script>
    <script src="static/leaflet-0.6.4/leaflet.js"></script>
    <script src="static/leaflet.label.js"></script>
    <script src="static/TileLayer.Grayscale.js"></script>
    <script src="static/m4a/m4a.loader.js"></script>
    <script src="static/m4a/m4a.stations.js"></script>
    <script src="static/m4a/m4a.url.js"></script>
    <script>

        function fillBarriers(barriers) {
            c = "";
            c+="<ul class='obstacles'>";
            c+="<li><strong>Ширина коляски</strong> " + "до " + barriers['max_width'] + " см" + "</li>";
            c+="<li><strong>Ступенек</strong> " + barriers['min_step'] + "</li>";
            c+="<li><strong>Из них без рельс и рамп</strong> " + (barriers['min_step'] - barriers['min_step_ramp']) + "</li>";
            c+="<li><strong>Лифт</strong> " + {true: 'есть', false: 'нет'}[barriers['lift']];

            // Лифт
            if (barriers['lift']) {
                c+=", экономит " + barriers['lift_minus_step'] + " ступенек";
            }
            c+="</li>";

            // Аппарели
            c+="<li><strong>Мин-макс. расстояние между колесами</strong> "
            if ((barriers['min_rail_width']) && (barriers['max_rail_width'])) {
                c+=barriers['min_rail_width'] + " &ndash; " + barriers['max_rail_width'] + " см";
            } else {
                c+="аппарели отсутствуют";
            }
            c+="</li>";

            // Наклонные поверхности
            c+="<li><strong>Угол наклона</strong> "
            if (barriers['max_angle']) {
                c+=barriers['max_angle'] + "&deg;";
            } else {
                c+="наклонных поверхностей нет";
            }
            c+="</li>";
            c+="</ul>";
            return c;
        }


        function showRoute(routes, index) {

            // Вывод списка станций, входящих в маршрут
            var content = "<ul class='route'>";
            
            content+="<li class='enter'>Вход" + " &rarr; " + routes[index].route[0].station_name 
            if (routes[index].portals.portal_from) {
                var barriers = routes[index].portals.portal_from.barriers;
                if (barriers) {
                  content+=fillBarriers(barriers);
                }
            } else {
              content+="<ul class='obstacles'>";
              content+="<li>Препятствия не отображаются, так как не выбран вход</li>";
              content+="</ul>";
            }
            content+="</li>";

            $.each(routes[index].route, function(i, item){
                var condition = (i == 0) ? item.station_type == 'regular' : (item.station_type == 'regular' && routes[index].route[i-1].station_type != 'interchange')
                if (condition) {
                  content+="<li class=" + "'station line-" + item.station_line.id + "'>" + item.station_name + "</li>"
                } else if (item.station_type == 'interchange') {
                  content+="<li class=" + "'transition from-line-" + item.station_line.id + " " + "to-line-" + routes[index].route[i+1].station_line.id + "'>" + item.station_name + " (" + item.station_line.name +")" +" &rarr; " + routes[index].route[i+1].station_name + " (" + routes[index].route[i+1].station_line.name +")"
                  if (item.barriers) {
                      content+=fillBarriers(item.barriers);
                  }
                  content+="</li>"
                }

            });

            content+="<li class='exit'>" + routes[index].route[routes[index].route.length - 1].station_name + " &rarr; " + "Выход";
            if (routes[index].portals.portal_to) {
                var barriers = routes[index].portals.portal_to.barriers;
                if (barriers) {
                  content+=fillBarriers(barriers);
                }
            } else {
              content+="<ul class='obstacles'>";
              content+="<li>Препятствия не отображаются, так как не выбран выход</li>";
              content+="</ul>";
            }
            content+="</li>";

            content+="</ul>";
            $('#routePanel').append(content);
            
            // Отображение маршрута на карте
            if (typeof route !== 'undefined') {
                m4a.viewmodel.mainMap.removeLayer(route);
            }
            route = L.layerGroup();
            $.each(routes[index].route, function(i, item){
                // Маркеры станций
                route.addLayer(L.marker(
                  item.coordinates,
                  {
                    icon: L.icon({
                      iconUrl: '/static/img/station.png',
                      iconAnchor: [3, 3]
                    })
                  }).bindLabel(item.station_name)
                ).addTo(m4a.viewmodel.mainMap);
                // Сегменты маршрута
                if (i != 0) {
                    route.addLayer(
                        L.polyline(
                            [routes[index].route[i-1].coordinates, item.coordinates],
                            {
                                color: item.station_line.color,
                                opacity: 1
                            })
                    ).addTo(m4a.viewmodel.mainMap);
                }
            });
        }


        $(document).ready(function() {
            m4a.loader.init();

            var url = m4a.viewmodel.url.proxy,
                viewmodel = m4a.viewmodel,
                view = m4a.view;
            viewmodel.mainMap = L.map('mainMap').setView(global_config.mainmap.center, global_config.mainmap.zoom);
            viewmodel.metroStartInputMap = L.map('metroStartInput', {zoomControl:false, attributionControl: false}).setView(global_config.minimap.center, global_config.minimap.zoom);
            viewmodel.metroEndInputMap = L.map('metroEndInput', {zoomControl:false, attributionControl: false}).setView(global_config.minimap.center, global_config.minimap.zoom);

          // Заполнение выпадающих списков
          $.ajax(url + global_config.city + "/stations").done(function(data){
              m4a.view.$metroStartStation.select2({width: "100%", data: data});
              m4a.view.$metroEndStation.select2({width: "100%", data: data});

              // Поле выбора станции входа
              view.$metroStartStation.on("change", function() {
                m4a.stations.setStartStation(this.value);
                m4a.stations.updateInputsData(this.value);
              });

              // Поле выбора станции выхода
              view.$metroEndStation.on("change", function() {
                m4a.stations.setEndStation(this.value);
                m4a.stations.updateOutputsData(this.value);
              });

              // Карта для выбора входов
              L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
                  maxZoom: 18
              }).addTo(m4a.viewmodel.metroStartInputMap);

              // Карта для выбора выходов
              L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
                  maxZoom: 18
              }).addTo(m4a.viewmodel.metroEndInputMap);

              // Карта для отображения маршрутов
              L.tileLayer.grayscale('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  attribution: "Map data &copy; <a href='http://osm.org'>OpenStreetMap</a> contributors",
                  maxZoom: 18
              }).addTo(m4a.viewmodel.mainMap);

              $("#mainform").submit(function() {
                  var view = m4a.view,
                      start_station = view.$metroStartStation.val(),
                      end_station = view.$metroEndStation.val();

                  if (start_station.length == 0 || end_station.length == 0) {
                    // todo: use bootstrap
                    alert('Не выбрана входная или выходная станция!');
                  } else if (start_station == end_station) {
                    alert('Выбраны одинаковые станции!');
                  } else {
                      $('.pagination').empty();
                      $('#routePanel').empty();
                      $.ajax({
                          dataType: "json",
                          url: url + global_config.city + "/routes/search",
                          data: $("#mainform").serialize()
                      }).done(function(data) {
                        var routes = data.result;

                        // Кнопки переключения маршрутов
                        $.each(routes, function(i, item){
                            $('.pagination').append('<li data-route-id="' + i + '"><a href="javascript:void(0)">'+(i+1)+'</a></li>');
                        });

                        // Обработчики нажатия кнопок
                        $('.pagination li').click(function() {
                            var $this = $(this),
                                route_index = parseInt($this.data('route-id'), 10);
                            $('.pagination li').removeClass('active');
                            $this.addClass('active');
                            $('#routePanel').empty();
                            showRoute(routes, route_index);
                            m4a.view.$document.triggerHandler('/url/update', ['route',route_index + 1]);
                        });

                        // Активируем первый маршрут
                        $('.pagination li').first().trigger('click');

                      });
                  }
                  return false;
              });

              m4a.url.parse();
          });
        });
    </script>
  </body>
</html>
