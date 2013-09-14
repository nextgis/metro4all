# -*- encoding: utf-8 -*-
import csv
import networkx as nx
from geojson import Feature, FeatureCollection, dumps
from bottle import route, response, request, run, static_file, HTTPResponse
from helpers import STATIONS

G = nx.Graph(nx.read_dot('graph.dot'))


@route('/static/<path:path>')
def static(path):
    return static_file(path, root='/home/rykov/nextgis/projects/metroaccess/webapp/static')


@route('/portals/search')
def get_portals():

    id_station = request.query.station

    # ['in', 'out']
    direction = request.query.direction

    portals = []
    store = csv.DictReader(
        open('backend_data/portals.csv', 'rb'),
        delimiter=';'
    )
    for row in store:
        d = row['direction'] if row['direction'] != '' else 'both'
        if (row['id_station']) == id_station and d in [direction, 'both']:
            feature = Feature(
                id=row['id_entrance'],
                geometry=dict(
                    type='Point',
                    coordinates=[row['lon'], row['lat']]
                ),
                properties=dict(direction=row['direction'])
            )
            portals.append(feature)

    response.content_type = 'application/json'
    return dumps(FeatureCollection(portals))


@route('/routes/search')
def get_routes(delta=5, limit=3):

    # Заполнение информации о препятствиях на входах и выходах
    def fill_portal_barriers(portal_id, u):
        barriers_data_store = csv.DictReader(
            open('backend_data/portals.csv', 'rb'),
            delimiter=';'
        )
        for row in barriers_data_store:
            if int(row['id_entrance']) == portal_id:
                u['barriers'] = dict(
                    min_width=row['min_width'],
                    min_step=row['min_step'],
                    min_step_ramp=row['min_step_ramp'],
                    lift=row['lift'],
                    lift_minus_step=row['lift_minus_step'],
                    min_rail_width=row['min_rail_width'],
                    max_rail_width=row['max_rail_width'],
                    max_angle=row['max_angle']
                )

    # Заполнение информации о препятствиях на переходах
    def fill_interchange_barriers(station_from, station_to, u):
        barriers_data_store = csv.DictReader(
            open('backend_data/interchanges.csv', 'rb'),
            delimiter=';'
        )
        for row in barriers_data_store:
            if (int(row['station_from']) == station_from) and (int(row['station_to']) == station_to):
                u['barriers'] = dict(
                    min_width=row['min_width'],
                    min_step=row['min_step'],
                    min_step_ramp=row['min_step_ramp'],
                    lift=row['lift'],
                    lift_minus_step=row['lift_minus_step'],
                    min_rail_width=row['min_rail_width'],
                    max_rail_width=row['max_rail_width'],
                    max_angle=row['max_angle']
                )

    station_from = int(request.query.station_from) if request.query.station_from else None
    station_to = int(request.query.station_to) if request.query.station_to else None
    portal_from = int(request.query.portal_from) if request.query.portal_from else None
    portal_to = int(request.query.portal_to) if request.query.portal_to else None

    if ((station_from is not None) and (station_to is not None)):

        station_from_name = get_station_name(station_from)
        station_to_name = get_station_name(station_to)

        minlength = nx.shortest_path_length(G, station_from_name, station_to_name)
        # generator
        simple_paths = nx.all_simple_paths(G, station_from_name, station_to_name, minlength+delta)
        simple_paths_list = [p for p in simple_paths]
        simple_paths_list_sorted = sorted(simple_paths_list, key=lambda path: len(path))

        routes = []
        for index in range(min(limit, len(simple_paths_list))):
            route = []
            for station in simple_paths_list_sorted[index]:
                station_index = simple_paths_list_sorted[index].index(station)
                same_line = check_the_same_line(station, get_next_item(simple_paths_list_sorted[index], station))

                if station_index == 0:
                    station_type = "start"

                elif station_index == len(simple_paths_list_sorted[index])-1:
                    station_type = "end"

                elif same_line:
                    station_type = "regular"

                else:
                    station_type = "interchange"

                unit = dict(
                    station_type=station_type,
                    station_id=get_station_id(station),
                    station_name=station,
                    station_line=get_station_line(station)
                )

                if station_type == "start":
                    if portal_from is not None:
                        fill_portal_barriers(portal_from, unit)
                    else:
                        unit['barriers'] = None

                elif station_type == "end":
                    if portal_to is not None:
                        fill_portal_barriers(portal_to, unit)
                    else:
                        unit['barriers'] = None

                elif station_type == "interchange":
                    unit['barriers'] = None
                    fill_interchange_barriers(get_station_id(station), get_station_id(get_next_item(simple_paths_list_sorted[index], station)), unit)

                elif station_type == "regular":
                    unit['barriers'] = None

                route.append(unit)

            routes.append(route)

        response.content_type = 'application/json'
        return dumps(dict(routes=routes))

    else:
        return HTTPResponse(status=400)


# Получение названия станции по идентификатору
def get_station_name(station_id):
    for id, line, name in STATIONS:
        if id == station_id:
            return name


# Получение идентификатора станции по имени
def get_station_id(name):
    for id, line, n in STATIONS:
        if name == n:
            return id


# Получение идентификатора линии по имени станции
def get_station_line(name):
    for id, line, n in STATIONS:
        if name == n:
            return line


# Проверка на принадлежность станций (по имени) к одной линии
def check_the_same_line(node1, node2):
    node1_line = get_station_line(node1)
    node2_line = get_station_line(node2)
    return node1_line == node2_line


# Извлечение следующего элемента в списке
def get_next_item(array, item):
        item_index = array.index(item)
        try:
            next_item = array[item_index + 1]
        except:
            next_item = array[item_index]
        return next_item


if __name__ == "__main__":
    run(host='0.0.0.0', port=8087, server='waitress')
