# -*- encoding: utf-8 -*-
#prepare data for mobile app, see https://github.com/nextgis/metro4all/issues/58

# example: python prepare_mobile_data.py city USERNAME PASSWORD

import csv
import os
import sys
import json
import codecs
import shutil
import zipfile
import ftplib
import urllib2


def split_stations(csv_in):

    langs = ("ru","en","pl")

    for lang in langs:
        input_f = csv.DictReader(open(csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            stations_csv_out = "temp/" + "stations_" + lang + ".csv"

            fieldmap = (
                ('id_station', 'id_station'),
                ('id_line', 'id_line'),
                ('id_node', 'id_node'),
                ('name_' + lang, 'name'),
                ('lat', 'lat'),
                ('lon', 'lon')
            )

            output_f = csv.DictWriter(open(csv_out, 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')

            output_f.writeheader()

            for row in input_f:
                item = dict()
                for source_name, target_name in fieldmap:
                    if source_name in row.keys():
                        item[target_name] = row[source_name]
                    else:
                        item[target_name] = ''
                output_f.writerow(item)

def split_lines(csv_in):

    langs = ("ru","en","pl")

    for lang in langs:
        input_f = csv.DictReader(open(csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            lines_csv_out = "temp/" + "lines_" + lang + ".csv"

            fieldmap = (
                ('id_line', 'id_line'),
                ('name_' + lang, 'name')
            )

            output_f = csv.DictWriter(open(csv_out, 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')

            output_f.writeheader()

            for row in input_f:
                item = dict()
                for source_name, target_name in fieldmap:
                    if source_name in row.keys():
                        item[target_name] = row[source_name]
                    else:
                        item[target_name] = ''
                output_f.writerow(item)

def split_portals(csv_in):

    langs = ("ru","en","pl")

    for lang in langs:
        input_f = csv.DictReader(open(csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            csv_out = "temp/" + "portals_" + lang + ".csv"

            fieldmap = (
                ('id2', 'id_entrance'),
                ('name_' + lang, 'name'),
                ('Код станции', 'id_station'),
                ('Направление', 'direction'),
                ('0_y', 'lat'),
                ('0_x', 'lon'),
                ('Мин. ширина', 'max_width'),
                ('Мин. Ступенек пешком', 'min_step'),
                ('Мин. ступенек по рельсам и рампам', 'min_step_ramp'),
                ('Лифт', 'lift'),
                ('Лифт отнимает ступенек', 'lift_minus_step'),
                ('Мин. ширина рельс', 'min_rail_width'),
                ('Макс. ширина рельс', 'max_rail_width'),
                ('Макс. угол', 'max_angle')
            )

            output_f = csv.DictWriter(open(csv_out, 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')

            output_f.writeheader()

            for row in input_f:
                item = dict()
                for source_name, target_name in fieldmap:
                    if source_name in row.keys():
                        item[target_name] = row[source_name]
                    else:
                        item[target_name] = ''
                output_f.writerow(item)


def get_meta():
    u = urllib2.urlopen("http://metro4all.org/data/v2/meta.json")
    r = u.read()
    f = open("temp/meta.json","wb")
    f.write(r)
    f.close()

def update_meta(city):
    meta = "temp/meta.json"
    
    json_content = json.load(codecs.open(meta, 'r', 'utf-8-sig'))
    packages = [item for item in json_content["packages"]]
    for package in packages:
        if package["path"] == city:
            package["ver"] = package["ver"] + 1
            package["size"] = os.stat("temp/" + city + ".zip").st_size/1024
            ver = package["ver"]
            print "New version: " + ver

    j = json.dumps(json_content, ensure_ascii=False).encode('utf8')
    f = open(meta, 'w')
    print >> f, j
    f.close()

    return ver

def copyfiles():
    
    graph = "data/" + city + "/graph.csv"
    interchanges = "data/" + city + "/interchanges.csv"
    schemes = "data/" + city + "/schemes/"
    icons = "data/" + city + "/icons/"

    shutil.copy(graph,"temp/graph.csv")
    shutil.copy(interchanges,"temp/interchanges.csv")
    shutil.copytree(schemes,"temp/schemes")
    shutil.copytree(icons,"temp/icons")

def createzip(city):
    os.chdir("temp")
    zf = zipfile.ZipFile(city + ".zip", "w")
    zf.write("graph.csv")
    zf.write("interchanges.csv")
    zf.write("lines.csv")
    zf.write("portals.csv")

    for dirname, subdirs, files in os.walk("schemes"):
        zf.write("schemes")
        for filename in files:
            zf.write(os.path.join(dirname, filename))

    for dirname, subdirs, files in os.walk("icons"):
        zf.write("schemes")
        for filename in files:
            zf.write(os.path.join(dirname, filename))

    zf.close()
    os.chdir("..")

def upload(city,ver,USERNAME,PASSWORD):
    print "Uploading: " + city + ".zip"

    session = ftplib.FTP('metro4all.ru',USERNAME,PASSWORD)
    session.cwd('metro4all.ru/data.old/data/v2')

    file = open("temp/" + city + ".zip",'rb')
    session.storbinary('STOR ' + city + ".zip", file)
    file.close()

    session.cwd('archive')
    file = open("temp/" + city + ".zip",'rb')
    session.storbinary('STOR ' + city + "_" + str(ver) + ".zip", file)
    file.close()

    session.quit()

if __name__ == '__main__':

    repo_root_path = ".."
    os.chdir(repo_root_path)
    if not os.path.exists("temp"):
        os.makedirs("temp")
    else:
        shutil.rmtree("temp/")
        os.makedirs("temp")

    city = sys.argv[1]
    USERNAME = sys.argv[2]
    PASSWORD = sys.argv[3]

    stations_csv_in = "data/" + city + "/" + "stations.csv"
    stations_csv_in = "data/" + city + "/" + "portals.csv"

    split_stations(stations_csv_in)
    split_portals(portals_csv_in)
    copyfiles()
    createzip(city)
    get_meta()
    ver = update_meta(city)
    #upload(city,ver,USERNAME,PASSWORD)


