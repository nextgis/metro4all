# -*- coding: utf-8 -*-

# should be installed:
# 1) pydot (http://stackoverflow.com/a/17902926/813758)
# 2) networkx

# example: python -i moscow_graph_prepare.py moscow_subway_ru.dot
# get_csv_output()

import sys
import networkx as nx

from moscow_subway_helpers import STATIONS

G = nx.Graph(nx.read_dot(sys.argv[1]))


def get_node_id(name, stations=STATIONS):
    '''Получение идентификатора станции по имени.
    '''

    for id, line, n, coords in stations:
        if name == n:
            return id


def get_node_line(name, stations=STATIONS):
    '''Получение идентификатора линии по имени станции.
    '''

    for id, line, n, coords in stations:
        if name == n:
            return line


def check_the_same_line(node1, node2):
    '''Проверка на принадлежность станций (по имени) к одной линии.
    '''

    node1_line = get_node_line(node1)
    node2_line = get_node_line(node2)
    return node1_line == node2_line


def get_routes(start, end, delta=10, limit=10):
    '''Печать маршрутов.
    '''

    def get_next_item(array, item):
        item_index = array.index(item)
        try:
            next_item = array[item_index + 1]
        except:
            next_item = array[item_index]
        return next_item

    minlength = nx.shortest_path_length(G, start, end)
    simple_paths = nx.all_simple_paths(G, start, end, minlength + delta)
    simple_paths_list = [p for p in simple_paths]
    simple_paths_list_sorted = sorted(simple_paths_list, key=lambda path: len(path))
    for index in range(min(limit, len(simple_paths_list))):
        print "\nPATH%s:" % index
        for station in simple_paths_list_sorted[index]:
            same_line = check_the_same_line(station, get_next_item(simple_paths_list_sorted[index], station))
            print station + {False: '*', True: ''}[same_line]


def get_csv_output():
    '''Печать графа для мобильного приложения.
    '''

    edges = G.edges()
    print "id_from;id_to;name_from;name_to;cost"
    for i in range(len(edges)):
        start_node_name = edges[i][0]
        end_node_name = edges[i][1]
        start_node_id = get_node_id(start_node_name)
        end_node_id = get_node_id(end_node_name)
        cost = {True: "3", False: "5"}[check_the_same_line(start_node_name, end_node_name)]
        print "%s;%s;%s;%s;%s;" % (start_node_id, end_node_id, start_node_name, end_node_name, cost)


def build_opt_group(line=None):
    '''Печать упорядоченных списков станций для Web-приложения.
    '''

    storage = []
    for id, numline, name, coords in STATIONS:
        if numline == line:
            storage.append((id, name))

    storage_sorted = sorted(storage, key=lambda i: i[1])
    for id, name in storage_sorted:
        print '<option value="%s">%s</option>' % (str(id), name)
