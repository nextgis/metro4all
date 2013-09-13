# -*- encoding: utf-8 -*-

# example: python prepare_portals_data.py input.csv

import csv
import os
import sys

csv_path = sys.argv[1]

fieldmap = (
    ('id2', 'id_entrance'),
    ('Название выхода', 'name'),
    ('Код станции2', 'id_station'),
    ('Направление', 'direction'),
    ('0_y', 'lat'),
    ('0_x', 'lon'),
    ('Мин. ширина', 'min_width'),
    ('Мин. Ступенек пешком', 'min_step'),
    ('Мин. ступенек по рельсам и рампам', 'min_step_ramp'),
    ('Лифт', 'lift'),
    ('Лифт отнимает ступенек', 'lift_minus_step'),
    ('Мин. ширина рельс', 'min_rail_width'),
    ('Макс. ширина рельс', 'max_rail_width'),
    ('Макс. угол', 'max_angle')
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
    output_f.writerow(portal)
