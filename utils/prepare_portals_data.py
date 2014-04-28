# -*- encoding: utf-8 -*-

# example: python prepare_portals_data.py input.csv

import csv
import os
import sys

csv_path = sys.argv[1]

fieldmap = (
    ('id_entrance', 'id_entrance'),
    ('name_ru', 'name_ru'),
    ('name_en', 'name_en'),
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
    ('max_angle', 'max_angle')
)

input_f = csv.DictReader(open(csv_path, 'rb'), delimiter=',')
output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'portals.csv'), 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')

output_f.writeheader()

for row in input_f:
    portal = dict()
    for source_name, target_name in fieldmap:
        if source_name in row.keys():
            portal[target_name] = row[source_name]
        else:
            portal[target_name] = ''
    if portal['lat'] != '':
        output_f.writerow(portal)
