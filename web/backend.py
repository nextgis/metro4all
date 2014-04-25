# -*- encoding: utf-8 -*-
import glob
import csv
import networkx as nx
import bottle
from geojson import Feature, FeatureCollection, dumps
from bottle import view, route, response, request, run, static_file, HTTPResponse
import os

# for Apache - http://bottlepy.org/docs/dev/faq.html
# “TEMPLATE NOT FOUND” IN MOD_WSGI/MOD_PYTHON
bottle.TEMPLATE_PATH.insert(0, '/home/karavanjow/projects/metro4all/metroaccess/web/views/')


# Инициализация графа
def init_graph(city):

    graph = nx.Graph()
    nodes = csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/%s/stations.csv' % city), 'rb'),delimiter=';')
    edges = csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/%s/graph.csv' % city), 'rb'), delimiter=';')

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


# Извлечение информации о препятствиях
def get_barriers(item):
    return dict(
        max_width=int(item['max_width'])/10 if item['max_width'].isdigit() else item['max_width'],
        min_step=int(item['min_step']) if (item['min_step'].isdigit()) else 0,
        min_step_ramp=int(item['min_step_ramp']) if (item['min_step_ramp'].isdigit()) else 0,
        lift=False if item['lift'] in ['', '0'] else True,
        lift_minus_step=item['lift_minus_step'],
        min_rail_width=int(item['min_rail_width'])/10 if (item['min_rail_width'].isdigit() and item['min_rail_width'] != '0') else None,
        max_rail_width=int(item['max_rail_width'])/10 if (item['max_rail_width'].isdigit() and item['max_rail_width'] != '0') else None,
        max_angle=int(item['max_angle']) if (item['max_angle'].isdigit() and item['max_angle'] != '0') else None
    )


LINES = {
    'msk': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/msk/lines.csv'), 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/spb/lines.csv'), 'rb'), delimiter=';')],
    'waw': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/waw/lines.csv'), 'rb'), delimiter=';')]
}

STATIONS = {
    'msk': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/msk/stations.csv'), 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/spb/stations.csv'), 'rb'), delimiter=';')],
    'waw': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/waw/stations.csv'), 'rb'), delimiter=';')]
}

PORTALS = {
    'msk': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/msk/portals.csv'), 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/spb/portals.csv'), 'rb'), delimiter=';')],
    'waw': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/waw/portals.csv'), 'rb'), delimiter=';')]
}

INTERCHANGES = {
    'msk': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/msk/interchanges.csv'), 'rb'), delimiter=';')],
    'spb': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/spb/interchanges.csv'), 'rb'), delimiter=';')],
    'waw': [i for i in csv.DictReader(open(os.path.join(os.path.dirname(__file__), '../data/waw/interchanges.csv'), 'rb'), delimiter=';')]
}

GRAPH = {
    'msk': init_graph('msk'),
    'spb': init_graph('spb'),
    'waw': init_graph('waw')
}

msk_schemes = [os.path.basename(n) for n in glob.glob(os.path.join(os.path.dirname(__file__), '../data/msk/schemes/*.png'))]
spb_schemes = [os.path.basename(n) for n in glob.glob(os.path.join(os.path.dirname(__file__), '../data/spb/schemes/*.png'))]
waw_schemes = [os.path.basename(n) for n in glob.glob(os.path.join(os.path.dirname(__file__), '../data/waw/schemes/*.png'))]
SCHEMAS = {
    'msk': dict(zip([os.path.splitext(s)[0] for s in msk_schemes], msk_schemes)),
    'spb': dict(zip([os.path.splitext(s)[0] for s in spb_schemes], spb_schemes)),
    'waw': dict(zip([os.path.splitext(s)[0] for s in spb_schemes], spb_schemes))
}


@route('/<city>')
@view('index')
def main(city):
    config = {
        'msk': dict(
            mainmap=dict(center=[55.75, 37.62], zoom=10),
            city='msk',
            route_css_class='city-1'
        ),
        'spb': dict(
            mainmap=dict(center=[59.95, 30.316667], zoom=10),
            city='spb',
            route_css_class='city-2'
        ),
        'waw': dict(
            mainmap=dict(center=[52.2286, 21.0491], zoom=10),
            city='waw',
            route_css_class='city-3'
        )
    }
    city = city if city in ['msk', 'spb', 'waw'] else 'msk'
    return dict(config=config[city])


@route('/static/<path:path>')
def static(path):
    import os
    return static_file(path, root=os.path.join(os.path.dirname(__file__), 'static'))


@route('/data/<path:path>')
def schemes(path):
    return static_file(path, root=os.path.join(os.path.dirname(__file__), '../data'))


# Получение списка станций для выпадающих списков
@route('/<lang>/<city>/stations')
def get_stations(lang, city):
    results = []
    for line in LINES[city]:
        group = []
        for station in STATIONS[city]:
            if line['id_line'] == station['id_line']:
                station_json = {
                    'id':   station['id_station'],
                    'text': station['name_' + lang]
                }
                if station['id_station'] in SCHEMAS[city]:
                    station_json['sch'] = SCHEMAS[city][station['id_station']]
                group.append(station_json)
        group = sorted(group, key=lambda i: i['text'])
        results.append({
            'text': line['name_' + lang],
            'children': group
        })

    response.content_type = 'application/json'
    return dumps(dict(results=results))


# Получение списка входов для заданной станции
@route('/<lang>/<city>/portals/search')
def get_portals(lang, city):

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
                    coordinates=[float(portal['lon']), float(portal['lat'])]
                ),
                properties=dict(
                    name=portal['name'],
                    direction=portal['direction'],
                    barriers=get_barriers(portal)
                )
            )
            portals.append(feature)

    response.content_type = 'application/json'
    return dumps(FeatureCollection(portals))


@route('/<lang>/<city>/routes/search')
def get_routes(lang, city, delta=5, limit=3):

    station_from = int(request.query.station_from) if request.query.station_from else None
    station_to = int(request.query.station_to) if request.query.station_to else None
    portal_from = int(request.query.portal_from) if request.query.portal_from else None
    portal_to = int(request.query.portal_to) if request.query.portal_to else None

    # Извлечение информации о станции
    def get_station_info(station_id):
        for station in STATIONS[city]:
            if station['id_station'] == str(station_id):
                return dict(
                    name=station['name_' + lang],
                    line=int(station['id_line']),
                    coords=(float(station['lat']), float(station['lon'])),
                    node_id=station['id_node']
                )

    # Извлечение информации о линии
    def get_line_info(line_id):
        for line in LINES[city]:
            if line['id_line'] == str(line_id):
                return dict(
                    name=line['name_' + lang],
                    color=line['color']
                )

    # Проверка на принадлежность станций к одной линии
    def check_the_same_line(node1, node2):
        node1_line = get_station_info(node1)['line']
        node2_line = get_station_info(node2)['line']
        return node1_line == node2_line

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
                station_info = get_station_info(station)
                line_id = station_info['line']
                same_line = check_the_same_line(station, get_next_item(simple_paths_list_sorted[index], station))

                station_type = "regular" if same_line else "interchange"
                node_id=station_info['node_id']

                unit = dict(
                    station_type=station_type,
                    station_id=station,
                    station_name=station_info['name'],
                    coordinates=station_info['coords'],
                    station_line=dict(
                        id=line_id,
                        name=get_line_info(line_id)['name'],
                        color=get_line_info(line_id)['color']
                    ),
                    schema=SCHEMAS[city][str(node_id)] if str(node_id) in SCHEMAS[city] else None
                )

                if station_type == "interchange":
                    unit['barriers'] = None
                    unit['barriers'] = interchange_barriers(station, get_next_item(simple_paths_list_sorted[index], station))

                elif station_type == "regular":
                    unit['barriers'] = None

                route.append(unit)

            # Заполняем информацию о входах
            portals = dict(
                portal_from=dict(barriers=portal_barriers(portal_from)) if portal_from else None,
                portal_to=dict(barriers=portal_barriers(portal_to)) if portal_to else None
            )

            routes.append(dict(route=route, portals=portals))

        response.content_type = 'application/json'
        return dumps(dict(result=routes))

    else:
        return HTTPResponse(status=400)


def run_fcgi():
    app = bottle.default_app()
    from flup.server.fcgi import WSGIServer
    WSGIServer(app).run()


if __name__ == "__main__":
    run(host='0.0.0.0', port=8088, server='waitress')