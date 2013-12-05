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

    <!-- lightbox2 styles -->
    <link href="static/lightbox2/css/lightbox.css" rel="stylesheet">

  </head>

  <body>
    <div class="container">
    <div class="row">

      <!-- Left panel -->
      <div class="col-md-3">
      <form id="mainform" role="form">
        <legend>Мои ограничения</legend>
        <div class="btn-group profiles" data-toggle="buttons">
          <label id="profile_man" class="btn btn-default profile" data-profile="man" data-type="sample" title="Я просто иду">
            <input type="radio" name="options" id="option1">
          </label>
          <label id="profile_wheelchair" class="btn btn-default profile" data-profile="wheelchair" data-type="input" title="Я на коляске">
            <input type="radio" name="options" id="option2">
          </label>
          <label id="profile_trolley" class="btn btn-default profile" data-profile="trolley" data-type="input" title="Я с тележкой">
            <input type="radio" name="options" id="option3">
          </label>
        </div>
        <div class="profile-descr"></div>
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
          city: "{{config['city']}}",
          language: "ru"
        }
    </script>
    <script src="static/bootstrap-3.0.0/assets/js/jquery.js"></script>
    <script src="static/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>
    <script src="static/select2-3.4.2/select2.js"></script>
    <script src="static/select2-3.4.2/select2_locale_ru.js"></script>
    <script src="static/leaflet-0.6.4/leaflet.js"></script>
    <script src="static/leaflet.label.js"></script>
    <script src="static/TileLayer.Grayscale.js"></script>
    <script src="static/mustache/mustache.js"></script>
    <script src="static/imagesloaded/imagesloaded.pkgd.min.js"></script>
    <script src="static/m4a/m4a.config.js"></script>
    <script src="static/m4a/translations/m4a.ru.js"></script>
    <script src="static/m4a/m4a.loader.js"></script>
    <script src="static/m4a/m4a.stations.js"></script>
    <script src="static/m4a/m4a.url.js"></script>
    <script src="static/m4a/m4a.profiles.js"></script>
    <script src="static/m4a/m4a.routes.js"></script>
    <script src="static/m4a/inline.js"></script>
    <script src="static/lightbox2/js/lightbox.js"></script>
  </body>
</html>
