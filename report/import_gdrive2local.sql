-- скрипт для импорта статистики по метро из сохранёных гуглодоков в локальную БД Postgresql 
-- sudo mount -t vboxsf -o uid=user,rw GIS /home/user/GIS
-- createdb project13
-- psql -d project13
-- postgres=# create extension postgis;
DROP TABLE IF EXISTS exits;

CREATE TABLE exits
(
id1 varchar,
id2 varchar,
name varchar,
line varchar,
exitname varchar,
stationcode1 varchar,
stationcode2 varchar,
line_id varchar,
x double precision,
y double precision,
direction varchar,
min_width_pass varchar,
min_steps_foot varchar,
min_steps_incline varchar,
lift varchar,
lift_covers_steps varchar,
min_width_rails varchar,
max_width_rails varchar,
max_incline varchar,
operator_name varchar,
comment varchar,
escalators_count varchar
);

COPY exits FROM '/home/user/GIS/project13/report/Выходы с кодами - Выходы_с_кодами.csv' DELIMITER ',' CSV HEADER;


DROP TABLE IF EXISTS interchanges;

CREATE TABLE interchanges
(
name_from varchar,
name_to varchar,
id_from_1 varchar,
id_to_1 varchar,
id_from_2 varchar,
id_to_2 varchar,
min_width_pass varchar,
min_steps_foot varchar,
min_steps_incline varchar,
lift varchar,
lift_covers_steps varchar,
min_width_rails varchar,
max_width_rails varchar,
max_incline varchar,
operator_name varchar,
comment varchar,
escalators_count varchar
);

COPY interchanges FROM '/home/user/GIS/project13/report/Переходы - Sheet1.csv' DELIMITER ',' CSV HEADER;

