import urllib
import csv
from lxml import etree
from collections import namedtuple

stations = csv.DictReader(open("stations.csv", 'rb'), delimiter=',')
input_fnames = stations.fieldnames
output_f = csv.DictWriter(open('stations_new.csv', 'wb'), input_fnames, delimiter=';')
output_f.writeheader()

msk_bnd = "37.1867,55.4465,38.275,55.9842"
spb_bnd = "29.6617,59.7592,30.817,60.1743"


city_bnd = spb_bnd
request = "http://www.overpass-api.de/api/xapi_meta?*%5Bstation=subway%5D%5Bbbox=" + city_bnd + "%5D"

tree = etree.parse(urllib.urlopen(request))
root = tree.getroot()

nodes = []
node_t = namedtuple('node','lat,lon,id')

for node in root.iter('node'):
        node = node_t(float(node.attrib['lat']),float(node.attrib['lon']),int(node.attrib['id']))
        nodes.append(node)

for station in stations:
    pos = [i for i, v in enumerate(nodes) if v[2] == int(station['id_osm'])][0]
    new_lat = nodes[pos].lat
    new_lon = nodes[pos].lon

    new_station = dict()
    for source_fname in input_fnames:
        if source_fname == 'lat':
            new_station['lat'] = new_lat
        elif source_fname == 'lon':
            new_station['lon'] = new_lon
        else:
            new_station[source_fname] = station[source_fname]

    print(station['name_ru'])
    output_f.writerow(new_station)