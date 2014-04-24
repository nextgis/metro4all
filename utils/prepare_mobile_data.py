# -*- encoding: utf-8 -*-
#prepare data for mobile app, see https://github.com/nextgis/metro4all/issues/58

# example: python prepare_mobile_data.py city

import csv
import os
import sys

repo_root_path = ".."
os.chdir(repo_root_path)

city = sys.argv[1]
stations_csv_in = "data/" + city + "/" + "stations.csv"

langs = ("ru","en","pl")

for lang in langs:
    input_f = csv.DictReader(open(stations_csv_in, 'rb'), delimiter=';')
    if 'name_' + lang in input_f.fieldnames:
        stations_csv_out = stations_csv_in.replace(".csv","_" + lang + ".csv")

        stations_fieldmap = (
            ('id_station', 'id_station'),
            ('id_line', 'id_line'),
            ('id_node', 'id_node'),
            ('name_' + lang, 'name'),
            ('lat', 'lat'),
            ('lon', 'lon')
        )

        output_f = csv.DictWriter(open(stations_csv_out, 'wb'), [target_name for source_name, target_name in stations_fieldmap], delimiter=';')

        output_f.writeheader()

        for row in input_f:
            station = dict()
            for source_name, target_name in stations_fieldmap:
                if source_name in row.keys():
                    station[target_name] = row[source_name]
                else:
                    station[target_name] = ''
            output_f.writerow(station)
