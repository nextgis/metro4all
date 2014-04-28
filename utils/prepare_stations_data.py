# -*- encoding: utf-8 -*-

# example: python prepare_stations_data.py input.csv

import csv
import os
import sys

csv_path = sys.argv[1]

fieldmap = (
    ('id_station', 'id_station'),
    ('id_line', 'id_line'),
    ('id_node', 'id_node'),
    ('name_en', 'name_en'),
    ('name_ru', 'name_ru'),
    ('name_pl', 'name_pl'),
    ('lat', 'lat'),
    ('lon', 'lon')
)

input_f = csv.DictReader(open(csv_path, 'rb'), delimiter=',')
output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'stations.csv'), 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')

output_f.writeheader()

for row in input_f:
    station = dict()
    for source_name, target_name in fieldmap:
        if source_name in row.keys():
            station[target_name] = row[source_name]
        else:
            if source_name.startswith('name'):
                station[target_name] = row['name_en']
            else:
                station[target_name] = ''
    output_f.writerow(station)
