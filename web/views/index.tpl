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

    <link href="static/opentip/opentip.css" rel="stylesheet">

  </head>

  <body>
    <div class="container">
    <div class="row">
      <div class="col-md-9">
      <form id="mainform" role="form">
      <div class="col-md-4">
        <legend>Откуда</legend>
        <div class="form-group">
          <div>
            <input id="metroStartStation" name="station_from" type="hidden">
            <button id="metroStartStationExtent" type="button" class="btn btn-primary" disabled>Перейти</button>
          </div>
        </div>
        <div class="form-group">
          <input class="form-control" id="metroStartInputName" type="text" placeholder="Выберите выход на карте..." disabled>
          <input name="portal_from" class="form-control" id="metroStartInputID" type="hidden">
        </div>
      </div>
      <div class="col-md-4">
        <legend>Куда</legend>
        <div class="form-group">
          <div>
            <input id="metroEndStation" name="station_to" type="hidden">
            <button id="metroEndStationExtent" type="button" class="btn btn-primary" disabled>Перейти</button>
          </div>
        </div>
        <div class="form-group">
          <input class="form-control" id="metroEndInputName" type="text" placeholder="Выберите выход на карте..." disabled>
          <input name="portal_to" class="form-control" id="metroEndInputID" type="hidden">
        </div>
      </div>
              <div class="col-md-4">
        <legend>Ограничения</legend>
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
      </div>
      </form>
      <div class="clearfix"></div>
      <span>&nbsp;</spn>
      <div id="map">
          <div id="mainMap" style="height: 480px;"></div>
      </div>
      </div>
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
          mainmap: {'center': {{config['mainmap']['center']}}, 'zoom': {{config['mainmap']['zoom']}}},
          city: "{{config['city']}}",
          language: "ru"
        }
    </script>
    <script src="static/jquery-1.11.1.min.js"></script>
    <script src="static/opentip/opentip-jquery-excanvas.js"></script>
    <script src="static/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>
    <script src="static/select2-3.4.2/select2.js"></script>
    <script src="static/select2-3.4.2/select2_locale_ru.js"></script>
    <script src="static/leaflet-0.6.4/leaflet.js"></script>
    <script src="static/leaflet.label.js"></script>
    <script src="static/mustache/mustache.js"></script>
    <script src="static/imagesloaded/imagesloaded.pkgd.min.js"></script>
    <script src="static/m4a/m4a.config.js"></script>
    <script src="static/m4a/translations/m4a.ru.js"></script>
    <script src="static/m4a/m4a.loader.js"></script>
    <script src="static/m4a/m4a.stations.js"></script>
    <script src="static/m4a/m4a.url.js"></script>
    <script src="static/m4a/m4a.profiles.js"></script>
    <script src="static/m4a/m4a.routes.js"></script>
    <script src="static/m4a/mapControls/m4a.extent.js"></script>
    <script src="static/m4a/inline.js"></script>
    <script src="static/lightbox2/js/lightbox.js"></script>
    <script src="static/blockui/jquery.blockUI.js"></script>
    <script src="static/leaflet.polylineoffset/contrib/jsts/lib/javascript.util.js"></script>
    <script src="static/leaflet.polylineoffset/contrib/jsts/lib/jsts.js"></script>
    <script src="static/leaflet.polylineoffset/leaflet.polylineoffset.js"></script>


    <script>
        % if request.environ['HTTP_HOST'] == 'demo.nextgis.ru':
            m4a.viewmodel.url = 'http://demo.nextgis.ru/metro4all/'
        % end
    </script>
  </body>
</html>
