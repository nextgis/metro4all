# -*- encoding: utf-8 -*-

# example: python prepare_interchanges_data.py input.csv

import csv
import os
import sys

csv_path = sys.argv[1]

fieldmap = (
    ('Код станции откуда', 'station_from'),
    ('Код станции куда', 'station_to'),
    ('Мин. ширина', 'max_width'),
    ('Мин. ступенек пешком', 'min_step'),
    ('Мин. ступенек по рельсам и рампам', 'min_step_ramp'),
    ('Лифт', 'lift'),
    ('Лифт отнимает ступенек', 'lift_minus_step'),
    ('Мин. ширина рельс', 'min_rail_width'),
    ('Макс. ширина рельс', 'max_rail_width'),
    ('Макс. угол', 'max_angle')
)

input_f = csv.DictReader(open(csv_path, 'rb'), delimiter=',')
output_f = csv.DictWriter(open(os.path.join(os.path.dirname(csv_path), 'interchanges.csv'), 'wb'), [target_name for source_name, target_name in fieldmap], delimiter=';')

output_f.writeheader()

for row in input_f:
    interchange = dict()
    for source_name, target_name in fieldmap:
        if source_name in row.keys():
            interchange[target_name] = row[source_name]
        else:
            interchange[target_name] = ''
    output_f.writerow(interchange)
