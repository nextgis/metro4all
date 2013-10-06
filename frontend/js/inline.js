function fillBarriers(barriers) {
	var c = "";
	c += "<ul class='obstacles'>";
	c += "<li><strong>Ширина коляски</strong> " + "до " + barriers['max_width'] + " см" + "</li>";

	if ((barriers['min_step'] == 0) && (barriers['min_step_ramp'] == 0)) {
		c += "<li class='empty'>Лестниц нет</li>";
	} else {
		c += "<li><strong>Ступенек</strong> " + barriers['min_step'] + "</li>";
		c += "<li><strong>Из них без рельс и рамп</strong> " + barriers['min_step_ramp'] + "</li>";
	}

	c += "<li>" + {true: 'Лифт есть', false: 'Лифта нет'}[barriers['lift']];

	// Лифт
	if (barriers['lift']) {
		c += ", экономит " + barriers['lift_minus_step'] + " ступенек";
	}
	c += "</li>";

	// Аппарели
	if ((barriers['min_rail_width']) && (barriers['max_rail_width'])) {
		c += "<li><strong>Мин-макс. расстояние между колесами</strong> "
			+ barriers['min_rail_width'] + " &ndash; " + barriers['max_rail_width'] + " см";
	} else {
		c += "<li class='empty'>Аппарели отсутствуют";
	}
	c += "</li>";

	// Наклонные поверхности
	if (barriers['max_angle']) {
		c += "<li><strong>Угол наклона</strong> "
			+ barriers['max_angle'] + "&deg;";
	} else {
		c += "<li class='empty'>Наклонных поверхностей нет";
	}
	c += "</li>";
	c += "</ul>";
	return c;
}


function showRoute(routes, index) {

	// Вывод списка станций, входящих в маршрут
	var content = "<ul class='route'>",
        lineClass = routes[index].route && routes[index].route.length > 0 ? ' line-' + routes[index].route[0].station_line.id : '';

	content += "<li class='enter" + lineClass + "'>Вход";
	if (routes[index].portals.portal_from) {
		var barriers = routes[index].portals.portal_from.barriers;
		if (barriers) {
			content += fillBarriers(barriers);
		}
	} else {
		content += "<ul class='obstacles'>";
		content += "<li>Препятствия не отображаются, так как не выбран вход</li>";
		content += "</ul>";
	}
	content += "</li>";

	$.each(routes[index].route, function (i, item) {
		var condition = (i == 0) ? item.station_type == 'regular' : (item.station_type == 'regular' && routes[index].route[i - 1].station_type != 'interchange')
		if (condition) {
			content += "<li class=" + "'station line-" + item.station_line.id + "'>" + item.station_name + "</li>"
		} else if (item.station_type == 'interchange') {
			content += "<li class=" + "'transition from-line-" + item.station_line.id + " " + "to-line-" + routes[index].route[i + 1].station_line.id + "'>" + item.station_name + " (" + item.station_line.name + ")" + " &rarr; " + routes[index].route[i + 1].station_name + " (" + routes[index].route[i + 1].station_line.name + ")"
			if (item.barriers) {
				content += fillBarriers(item.barriers);
			}
			content += "</li>"
		}

	});

	content += "<li class='exit'>Выход";
	if (routes[index].portals.portal_to) {
		var barriers = routes[index].portals.portal_to.barriers;
		if (barriers) {
			content += fillBarriers(barriers);
		}
	} else {
		content += "<ul class='obstacles'>";
		content += "<li>Препятствия не отображаются, так как не выбран выход</li>";
		content += "</ul>";
	}
	content += "</li>";

	content += "</ul>";
	$('#routePanel').append(content);

	// Отображение маршрута на карте
	if (typeof route !== 'undefined') {
		m4a.viewmodel.mainMap.removeLayer(route);
	}
	route = L.layerGroup();
	$.each(routes[index].route, function (i, item) {
		// Маркеры станций
		route.addLayer(L.marker(
			item.coordinates,
			{
				icon: L.divIcon({
					className: 'marker-station marker-line-' + item.station_line.id + (i == 0 ? ' marker-enter' : (i == (routes[index].route.length - 1) ? ' marker-exit' : '')),
					iconSize: [16, 16]
				})
			}).bindLabel(item.station_name)
		).addTo(m4a.viewmodel.mainMap);
		// Сегменты маршрута
		if (i != 0) {
			route.addLayer(
				L.polyline(
					[routes[index].route[i - 1].coordinates, item.coordinates],
					{
						color: item.station_line.color,
						opacity: 1
					})
			).addTo(m4a.viewmodel.mainMap);
		}
	});

	// Охват на маршрут
	var xmin = routes[index].route[0].coordinates[1],
		ymin = routes[index].route[0].coordinates[0],
		xmax = routes[index].route[0].coordinates[1],
		ymax = routes[index].route[0].coordinates[0];
	$.each(routes[index].route, function (i, item) {
		xmin = (item.coordinates[1]) < xmin ? item.coordinates[1] : xmin;
		ymin = (item.coordinates[0]) < ymin ? item.coordinates[0] : ymin;
		xmax = (item.coordinates[1]) > xmax ? item.coordinates[1] : xmax;
		ymax = (item.coordinates[0]) > ymax ? item.coordinates[0] : ymax;
	});
	m4a.viewmodel.mainMap.fitBounds([
		[ymin, xmin],
		[ymax, xmax]
	]);
}


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
				}).done(function (data) {
						var routes = data.result;

						// Кнопки переключения маршрутов
						$.each(routes, function (i, item) {
							$('.pagination').append('<li data-route-id="' + i +
								'"><a href="javascript:void(0)">' + (i + 1) + '</a></li>');
						});

						// Обработчики нажатия кнопок
						$('.pagination li').click(function () {
							var $this = $(this),
								route_index = parseInt($this.data('route-id'), 10);
							$('.pagination li').removeClass('active');
							$this.addClass('active');
							$('#routePanel').empty();
							showRoute(routes, route_index);
//                            m4a.view.$document.triggerHandler('/url/update', ['route', route_index + 1]);
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