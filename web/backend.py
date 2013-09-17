# -*- encoding: utf-8 -*-
import csv
import networkx as nx
from geojson import Feature, FeatureCollection, dumps
from bottle import route, response, request, run, static_file, HTTPResponse


# Инициализация графа
def init_graph(city):

    graph = nx.Graph()
    nodes = csv.DictReader(open('../data/%s/stations.csv' % city, 'rb'), delimiter=';')
    edges = csv.DictReader(open('../data/%s/graph.csv' % city, 'rb'), delimiter=';')

    for node in nodes:
        graph.add_node(int(node['id_station']))

    for edge in edges:
        graph.add_edge(int(edge['id_from']), int(edge['id_to']))

    return graph


# Извлечение следующего элемента в списке
def get_next_item(array, item):
    item_index = array.index(item)
    try:
        next_item = array[item_index + 1]
    except:
        next_item = array[item_index]
    return next_item


LINES = {
    'msk': [i for i in csv.DictReader(open('../data/msk/lines.csv', 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open('../data/spb/lines.csv', 'rb'), delimiter=';')]
}

STATIONS = {
    'msk': [i for i in csv.DictReader(open('../data/msk/stations.csv', 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open('../data/spb/stations.csv', 'rb'), delimiter=';')]
}

PORTALS = {
    'msk': [i for i in csv.DictReader(open('../data/msk/portals.csv', 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open('../data/spb/portals.csv', 'rb'), delimiter=';')]
}

INTERCHANGES = {
    'msk': [i for i in csv.DictReader(open('../data/msk/interchanges.csv', 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open('../data/spb/interchanges.csv', 'rb'), delimiter=';')]
}

GRAPH = {
    'msk': init_graph('msk'),
    'spb': init_graph('spb')
}


@route('/static/<path:path>')
def static(path):
    return static_file(path, root='/home/tenzorr/projects/metroaccess/metroaccess/web/static')


# Получение списка станций для выпадающих списков
@route('/stations')
def get_stations():

    city = 'msk'

    results = []
    for line in LINES[city]:
        group = []
        for station in STATIONS[city]:
            if line['id_line'] == station['id_line']:
                group.append(dict(
                    id=station['id_station'],
                    text=station['name']
                ))
        group = sorted(group, key=lambda i: i['text'])
        results.append(dict(text=line['name'], children=group))

    response.content_type = 'application/json'
    return dumps(dict(results=results))


# Получение списка входов для заданной станции
@route('/portals/search')
def get_portals():

    city = 'msk'

    id_station = request.query.station

    # ['in', 'out']
    direction = request.query.direction

    portals = []
    for portal in PORTALS[city]:
        d = portal['direction'] if portal['direction'] != '' else 'both'
        if (portal['id_station']) == id_station and d in [direction, 'both']:
            feature = Feature(
                id=portal['id_entrance'],
                geometry=dict(
                    type='Point',
                    coordinates=[portal['lon'], portal['lat']]
                ),
                properties=dict(
                    name=portal['name'],
                    direction=portal['direction'],
                    barriers=dict()
                )
            )
            portals.append(feature)

    response.content_type = 'application/json'
    return dumps(FeatureCollection(portals))


@route('/routes/search')
def get_routes(delta=5, limit=3):

    city = 'msk'
    station_from = int(request.query.station_from) if request.query.station_from else None
    station_to = int(request.query.station_to) if request.query.station_to else None
    portal_from = int(request.query.portal_from) if request.query.portal_from else None
    portal_to = int(request.query.portal_to) if request.query.portal_to else None

    # Извлечение информации о станции
    def get_station_info(station_id):
        for station in STATIONS[city]:
            if station['id_station'] == str(station_id):
                return dict(
                    name=station['name'],
                    line=int(station['id_line']),
                    coords=(float(station['lat']), float(station['lon']))
                )

    # Извлечение информации о линии
    def get_line_info(line_id):
        for line in LINES[city]:
            if line['id_line'] == str(line_id):
                return dict(
                    name=line['name'],
                    color=line['color']
                )

    # Проверка на принадлежность станций к одной линии
    def check_the_same_line(node1, node2):
        node1_line = get_station_info(node1)['line']
        node2_line = get_station_info(node2)['line']
        return node1_line == node2_line

    # Получение информации о препятствиях
    def get_barriers(item):
        return dict(
            max_width=int(item['max_width'])/10 if item['max_width'].isdigit() else item['max_width'],
            min_step=int(item['min_step']) if (item['min_step'].isdigit()) else 0,
            min_step_ramp=int(item['min_step_ramp']) if (item['min_step_ramp'].isdigit()) else 0,
            lift=False if item['lift'] in ['', '0'] else True,
            lift_minus_step=item['lift_minus_step'],
            min_rail_width=int(item['min_rail_width'])/10 if (item['min_rail_width'].isdigit() and item['min_rail_width'] != '0') else None,
            max_rail_width=int(item['max_rail_width'])/10 if (item['max_rail_width'].isdigit() and item['max_rail_width'] != '0') else None,
            max_angle=int(item['max_angle'])/10 if (item['max_angle'].isdigit() and item['max_angle'] != '0') else None
        )

    # Заполнение информации о препятствиях на входах и выходах
    def portal_barriers(portal_id):
        for portal in PORTALS[city]:
            if int(portal['id_entrance']) == portal_id:
                return get_barriers(portal)

    # Заполнение информации о препятствиях на переходах
    def interchange_barriers(station_from, station_to):
        for interchange in INTERCHANGES[city]:
            if (int(interchange['station_from']) == station_from) and (int(interchange['station_to']) == station_to):
                return get_barriers(interchange)

    if ((station_from is not None) and (station_to is not None)):

        minlength = nx.shortest_path_length(GRAPH[city], station_from, station_to)
        # generator
        simple_paths = nx.all_simple_paths(GRAPH[city], station_from, station_to, minlength+delta)
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

                line_id = get_station_info(station)['line']
                unit = dict(
                    station_type=station_type,
                    station_id=station,
                    station_name=get_station_info(station)['name'],
                    coordinates=get_station_info(station)['coords'],
                    station_line=dict(
                        id=line_id,
                        name=get_line_info(line_id)['name'],
                        color=get_line_info(line_id)['color']
                    )
                )

                if station_type == "start":
                    if portal_from is not None:
                        unit['barriers'] = portal_barriers(portal_from)
                    else:
                        unit['barriers'] = None

                elif station_type == "end":
                    if portal_to is not None:
                        unit['barriers'] = portal_barriers(portal_to)
                    else:
                        unit['barriers'] = None

                elif station_type == "interchange":
                    unit['barriers'] = None
                    unit['barriers'] = interchange_barriers(station, get_next_item(simple_paths_list_sorted[index], station))

                elif station_type == "regular":
                    unit['barriers'] = None

                route.append(unit)

            routes.append(route)

        response.content_type = 'application/json'
        return dumps(dict(routes=routes))

    else:
        return HTTPResponse(status=400)


if __name__ == "__main__":
    run(host='0.0.0.0', port=8087, server='waitress')
