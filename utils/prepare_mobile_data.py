# -*- encoding: utf-8 -*-
#prepare data for mobile app, see https://github.com/nextgis/metro4all/issues/58

# example: python prepare_mobile_data.py city

import csv
import os
import sys
import json
import codecs


def split_stations(stations_csv_in):

    langs = ("ru","en","pl")

    for lang in langs:
        input_f = csv.DictReader(open(stations_csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            stations_csv_out = "temp/" + "stations_" + lang + ".csv"

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

def update_meta(city):

    meta = "android/metro4all/data/meta.json"
    json_content = json.load(codecs.open(meta, 'r', 'utf-8-sig'))
    packages = [item for item in json_content["packages"]]
    for package in packages:
        if package["path"] == city:
            package["ver"] = packages[0]["ver"] + 1

    j = json.dumps(json_content, ensure_ascii=False).encode('utf8')
    f = open(meta, 'w')
    print >> f, j
    f.close()

if __name__ == '__main__':

    repo_root_path = ".."
    os.chdir(repo_root_path)
    if not os.path.exists("temp"):
        os.makedirs("temp")

    city = sys.argv[1]
    stations_csv_in = "data/" + city + "/" + "stations.csv"

    split_stations(stations_csv_in)
    update_meta(city)



