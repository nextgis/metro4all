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
    lines = "data/" + city + "/lines.csv"
    portals = "data/" + city + "/portals.csv"
    schemes = "data/" + city + "/schemes/"
    icons = "data/" + city + "/icons/"

    shutil.copy(graph,"temp/graph.csv")
    shutil.copy(interchanges,"temp/interchanges.csv")
    shutil.copy(lines,"temp/lines.csv")
    shutil.copy(portals,"temp/portals.csv")
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

    split_stations(stations_csv_in)
    copyfiles()
    createzip(city)
    get_meta()
    ver = update_meta(city)
    upload(city,ver,USERNAME,PASSWORD)


