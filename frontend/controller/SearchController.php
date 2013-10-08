<?php

class SearchController
{
	var $currentCity;

	function actions()
	{
		$action = request_str('action');
		if(method_exists($this, $action.'Action')) {
			return call_user_func(array($this, $action.'Action'));
		} else {
			return $this->defaultAction();
		}
	}

	function setCity($currentCity)
	{
		$this->currentCity = $currentCity;
	}

	function defaultAction()
	{
		$page = new PageCommon(s('Метро для всех'));

		$page->addResource('style', 'vendor/select2-3.4.2/select2.css');
		$page->addResource('script', 'vendor/select2-3.4.2/select2.js');
		$page->addResource('script', 'vendor/select2-3.4.2/select2_locale_ru.js');

		$page->addResource('style', 'vendor/leaflet-0.6.4/leaflet.css');
		$page->addResource('style', 'vendor/leaflet-0.6.4/leaflet.ie.css', 'lte IE 8');
		$page->addResource('script', 'vendor/leaflet-0.6.4/leaflet.js');

		$page->addResource('style', 'http://demo.nextgis.ru/metro4all/static/leaflet.label.css');

		$page->addResource('style', 'css/m4a.css');

		switch ($this->currentCity)
		{
			case 1:
				$globalConfig = 'var global_config = {
					  minimap: {"center": [55.75, 37.62], "zoom": 11},
					  mainmap: {"center": [55.75, 37.62], "zoom": 10},
					  city: "msk"
				}';
				break;

			case 2:
				$globalConfig = 'var global_config = {
					  minimap: {"center": [59.95, 30.316667], "zoom": 11},
					  mainmap: {"center": [59.95, 30.316667], "zoom": 10},
					  city: "spb"
				}';
				break;
		}

		$html = '
	<div class="row">
      <!-- Left panel -->
      <div class="col-md-3">
      <form id="mainform" role="form">
        <legend>' . s('Ограничения') . '</legend>
        <div class="btn-group profiles" data-toggle="buttons">
          <label id="profile_man" class="btn btn-default profile" data-profile="man" data-type="sample" title="' . s('Я просто иду') . '">
            <input type="radio" name="options" id="option1">
          </label>
          <label id="profile_wheelchair" class="btn btn-default profile" data-profile="wheelchair" data-type="input" title="' . s('Я на коляске') . '">
            <input type="radio" name="options" id="option2">
          </label>
          <label id="profile_trolley" class="btn btn-default profile" data-profile="trolley" data-type="input" title="' . s('Я с тележкой') . '">
            <input type="radio" name="options" id="option3">
          </label>
        </div>
        <div class="profile-descr"></div>
        <legend>2. ' . s('Откуда') . '</legend>
        <div class="form-group">
          <label for="metroStartStation">' . s('Выберите станцию:') . '</label>
          <div>
            <input id="metroStartStation" name="station_from" type="hidden">
          </div>
        </div>
        <div class="form-group">
          <label for="metroStartInputID">' . s('Выберите вход:') . '</label>
          <input style="display: none;" class="form-control" id="metroStartInputName" type="text" placeholder="' . s('Выберите выход на карте...') . '" disabled>
          <input name="portal_from" class="form-control" id="metroStartInputID" type="hidden">
          <div id="metroStartInput" style="height: 150px;"></div>
          <em>' . s('Нажмите на значок входа, чтобы его выбрать') . '</em>
        </div>
        <legend>3. ' . s('Куда') . '</legend>
        <div class="form-group">
          <label for="metroEndStation">' . s('Выберите станцию:') . '</label>
          <div>
            <input id="metroEndStation" name="station_to" type="hidden">
          </div>
        </div>
        <div class="form-group">
          <label for="metroEndInputID">' . s('Выберите выход:') . '</label>
          <input style="display: none;" class="form-control" id="metroEndInputName" type="text" placeholder="' . s('Выберите выход на карте...') . '" disabled>
          <input name="portal_to" class="form-control" id="metroEndInputID" type="hidden">
          <div id="metroEndInput" style="height: 150px;"></div>
          <em>' . s('Нажмите на значок выхода, чтобы его выбрать') . '</em>
        </div>
        <button type="submit" class="btn btn-primary">' . s('Проложить маршрут') . '</button>
      </form>
      </div>

      <div id="map" class="col-md-6">
          <legend>4. ' . s('Карта') . '</legend>
          <div id="mainMap" style="height: 480px;" class="city-' . $this->currentCity . '"></div>
          <p class="font-size:11px;">' . s('Эта карта только для просмотра маршрута. Выбирать по ней станции пока нельзя') . '</p>
      </div>

      <!-- Right panel -->
      <div class="col-md-3">
        <legend>5. ' . s('Маршрут') . '</legend>
        <ul class="route-paging pagination pagination-sm"></ul>
        <div id="routePanel" class="city-' . $this->currentCity . '"></div>
      </div>
    </div>

	<div id="popup">
		<div class="wrapper"></div>
		<div class="loader">
			<img class="loader" src="static/img/loader.gif"/>
			<p>Загрузка схемы...</p>
		</div>
		<div class="content">
			<span class="close"></span>
			<div class="data"></div>
		</div>
	</div>

	<script> var ajax="http://' . Core::$config['http_domain'] .  '/ajax/"; </script>

	<script>' . $globalConfig . '</script>
	<script src="http://demo.nextgis.ru/metro4all/static/TileLayer.Grayscale.js"></script>
	<script src="http://demo.nextgis.ru/metro4all/static/mustache/mustache.js"></script>
	<script src="http://demo.nextgis.ru/metro4all/static/imagesloaded/imagesloaded.pkgd.min.js"></script>
	<script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.config.js"></script>

	<script>
		m4a.viewmodel.pathToSchemes = "/schemes/";
	</script>

    <script src="http://demo.nextgis.ru/metro4all/static/TileLayer.Grayscale.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/leaflet.label.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.loader.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.stations.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.url.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.profiles.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.routes.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/m4a.popup.js"></script>
    <script src="http://demo.nextgis.ru/metro4all/static/m4a/inline.js"></script>';

		return $page->start() . $html . $page->stop();
	}
}

