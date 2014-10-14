# -*- encoding: utf-8 -*-
# prepare data for mobile app, see https://github.com/nextgis/metro4all/issues/58
# run from util folder
# Data will be put here: /usr/home/karavanjow/projects/metro4all/metroaccess/frontend/data/v2/
#                    or: metro4all.org/data/v2/
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
import paramiko

def cleanup(val):
    if val == '':
        val = '-1'

    if val[0] == '"':
        print '" found'
    return val

def split_stations(csv_in):

    for lang in langs:
        input_f = csv.DictReader(open(csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            csv_out = 'temp/' + 'stations_' + lang + '.csv'

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
                        item[target_name] = cleanup(item[target_name])
                    else:
                        item[target_name] = ''
                output_f.writerow(item)

def split_lines(csv_in):

    langs = ('ru','en','pl')

    for lang in langs:
        input_f = csv.DictReader(open(csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            csv_out = 'temp/' + 'lines_' + lang + '.csv'

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
                        item[target_name] = cleanup(item[target_name])
                    else:
                        item[target_name] = ''
                output_f.writerow(item)

def split_portals(csv_in):


    for lang in langs:
        input_f = csv.DictReader(open(csv_in, 'rb'), delimiter=';')
        if 'name_' + lang in input_f.fieldnames:
            csv_out = 'temp/' + 'portals_' + lang + '.csv'

            fieldmap = (
                ('id_entrance', 'id_entrance'),
                ('name_' + lang, 'name'),
                ('id_station', 'id_station'),
                ('direction', 'direction'),
                ('lat', 'lat'),
                ('lon', 'lon'),
                ('max_width', 'max_width'),
                ('min_step', 'min_step'),
                ('min_step_ramp', 'min_step_ramp'),
                ('lift', 'lift'),
                ('lift_minus_step', 'lift_minus_step'),
                ('min_rail_width', 'min_rail_width'),
                ('max_rail_width', 'max_rail_width'),
                ('max_angle', 'max_angle'),
                ('escalator', 'escalator')
            )

            output_f = csv.DictWriter(open(csv_out, 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';', quotechar = '|', quoting = csv.QUOTE_NONE)

            output_f.writeheader()

            for row in input_f:
                item = dict()
                for source_name, target_name in fieldmap:
                    if source_name in row.keys():
                        item[target_name] = row[source_name]
                        item[target_name] = cleanup(item[target_name])
                    else:
                        item[target_name] = ''
                output_f.writerow(item)


def get_meta():
    u = urllib2.urlopen('http://metro4all.org/data/v' + data_minimum_version + '/meta.json')
    r = u.read()
    f = open('temp/meta.json','wb')
    f.write(r)
    f.close()

def update_meta(city):
    meta = 'temp/meta.json'
    
    json_content = json.load(codecs.open(meta, 'r', 'utf-8-sig'))
    packages = [item for item in json_content['packages']]
    for package in packages:
        if package['path'] == city:
            package['ver'] = package['ver'] + 1
            package['size'] = os.stat('temp/' + city + '.zip').st_size/1024
            ver = package['ver']
            print 'New version: ' + str(ver)

    j = json.dumps(json_content, ensure_ascii=False).encode('utf8')
    f = open(meta, 'w')
    print >> f, j
    f.close()

    return ver

def copyfiles():
    
    graph = 'data/' + city + '/graph.csv'
    interchanges = 'data/' + city + '/interchanges.csv'
    schemes = 'data/' + city + '/schemes/'
    icons = 'data/' + city + '/icons/'

    shutil.copy(graph,'temp/graph.csv')
    shutil.copy(interchanges,'temp/interchanges.csv')
    shutil.copytree(schemes,'temp/schemes')
    shutil.copytree(icons,'temp/icons')

def createzip(city):
    os.chdir('temp')
    zf = zipfile.ZipFile(city + '.zip', 'w')
    zf.write('graph.csv')
    zf.write('interchanges.csv')

    for dirname, subdirs, files in os.walk('schemes'):
        #zf.write('schemes')
        for filename in files:
            zf.write(os.path.join(dirname, filename))

    for dirname, subdirs, files in os.walk('icons'):
        #zf.write('icons')
        for filename in files:
            zf.write(os.path.join(dirname, filename))

    items = ('stations','lines','portals')
    for lang in langs:
        for item in items:
            fn = item + '_' + lang + '.csv'
            if os.path.exists(fn):
                zf.write(fn)

    zf.close()
    os.chdir('..')

def upload_ftp(city,ver,USERNAME,PASSWORD):
    print 'Uploading: ' + city + '.zip'

    session = ftplib.FTP('metro4all.ru',USERNAME,PASSWORD)
    session.cwd('metro4all.ru/data.old/data/v' + data_minimum_version)

    file = open('temp/' + city + '.zip','rb')
    session.storbinary('STOR ' + city + '.zip', file)
    file.close()
    
    session.cwd('archive')
    file = open('temp/' + city + '.zip','rb')
    session.storbinary('STOR ' + city + '_' + str(ver) + '.zip', file)
    file.close()

    session.quit()

def upload_sftp(city,ver,USERNAME,PASSWORD):
    print 'Uploading: ' + city + '.zip'

    transport = paramiko.Transport(('gis-lab.info', 22222))
    transport.connect(username = USERNAME, password = PASSWORD)
    sftp = paramiko.SFTPClient.from_transport(transport)

    sftp.put('temp/' + city + '.zip', data_path + city + '.zip')
    
    print 'Uploading: archive/' + city + '_' + str(ver) + '.zip'
    sftp.put('temp/' + city + '.zip',data_path + 'archive/' + city + '_' + str(ver) + '.zip')

    print 'Uploading: meta.json'
    sftp.put('temp/meta.json', data_path + 'meta.json')

    sftp.close()
    transport.close()


if __name__ == '__main__':

    repo_root_path = '..'
    data_minimum_version = '2.3'
    os.chdir(repo_root_path)
    if not os.path.exists('temp'):
        os.makedirs('temp')
    else:
        shutil.rmtree('temp/')
        os.makedirs('temp')

    city = sys.argv[1]
    USERNAME = sys.argv[2]
    PASSWORD = sys.argv[3]
    langs = ('ru','en','pl','be')
    data_path = '/usr/local/www/metro4all.ru/data/data/v' + data_minimum_version + '/'

    split_stations('data/' + city + '/' + 'stations.csv')
    split_portals('data/' + city + '/' + 'portals.csv')
    split_lines('data/' + city + '/' + 'lines.csv')
    copyfiles()
    createzip(city)
    get_meta()
    #shutil.copy('/home/sim/work/meta.json','/home/sim/work/metro4all/repo/temp/meta.json')

    ver = update_meta(city)
    upload_sftp(city,ver,USERNAME,PASSWORD)
    
    #clean up everything
    shutil.rmtree('temp/')
