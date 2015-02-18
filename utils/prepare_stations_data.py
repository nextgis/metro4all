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
    ('name_be', 'name_be'),
    ('lat', 'lat'),
    ('lon', 'lon')
)

#create new list of fieldnames with only langs present in the source file
input_f = csv.DictReader(open(csv_path, 'rb'), delimiter=',')
input_fnames = input_f.fieldnames
output_fnames = []
for source_name, target_name in fieldmap:
    if source_name in input_fnames:
        output_fnames.append(target_name)

#output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'stations.csv'), 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')
output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'stations.csv'), 'wb'), output_fnames, delimiter=';')

output_f.writeheader()

for row in input_f:
    station = dict()
    if row['closed'] == '':
        for source_name, target_name in fieldmap:
            if source_name in row.keys():
                station[target_name] = row[source_name]
            else:
                pass
                #Needed only if we want to replicate missing name_[lang] with English
                #if source_name.startswith('name'):
                #    station[target_name] = row['name_en']
                #else:
                #    station[target_name] = ''
        output_f.writerow(station)
