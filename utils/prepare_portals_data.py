# -*- encoding: utf-8 -*-

# example: python prepare_portals_data.py input.csv

import csv
import os
import sys

csv_path = sys.argv[1]

fieldmap = (
    ('id2', 'id_entrance'),
    ('Код для встречи', 'meetcode'),
    ('name_ru', 'name_ru'),
    ('name_en', 'name_en'),
    ('name_pl', 'name_pl'),
    ('name_be', 'name_be'),
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
    ('Макс. угол', 'max_angle'),
    ('Количество эскалаторов на маршруте', 'escalator')
)

#create new list of fieldnames with only langs present in the source file
input_f = csv.DictReader(open(csv_path, 'rb'), delimiter=',')
input_fnames = input_f.fieldnames
output_fnames = []
for source_name, target_name in fieldmap:
    if source_name in input_fnames:
        output_fnames.append(target_name)

#output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'portals.csv'), 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')
output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'portals.csv'), 'wb'), output_fnames, delimiter=';')

output_f.writeheader()

for row in input_f:
    portal = dict()
    if row['Закрыт'] == '':
        for source_name, target_name in fieldmap:
            if source_name in row.keys():
                portal[target_name] = row[source_name]
            else:
                pass
            #    if source_name.startswith('name'):
            #        portal[source_name] = row['name_en']
            #    else:
            #        portal[target_name] = ''
        if portal['lat'] != '':
            output_f.writerow(portal)
