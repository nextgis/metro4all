metroaccess
===========

Доступность метрополитена для людей с ограниченными возможностями

Рабочие таблицы хранятся в Google Docs в папках по городам.

На Github данные лежат также в папках по городам:

  * metroaccess/data/msk - Москва
  * metroaccess/data/spb - Санкт-Петербург
  * metroaccess/data/kzn - Казань

На сервере сервис и данные расположены здесь:

  * /home/karavanjow/projects/metroaccess/metro4all 

Процесс обновления (действия производятся локально):
--------

1. Скачать в формате .csv документы из Google Docs Metro4All => [город] => data
  * данные по станциям - stations.csv 
  * данные по переходам - interchanges.csv
  * данные по выходам - portals.csv

2. Запустить инструменты подготовки для приведения CSV к формальному виду (названия полей и т.п.):
  * python utils/prepare_stations_data.py stations.csv
  * python utils/prepare_interchanges_data.py interchanges.csv
  * python utils/prepare_portals_data.py portals.csv

3. Заменить обновленные файлы в папке metroaccess/data/[город]

4. Обновить схемы в папке [город]/schemes 

5. Повторить, если нужно для других городов

6. Закоммитить обновленные файлы в репозиторий и загрузить на сервер.

7. Перезапустить сервис:
  
  * supervisorctl -c /home/karavanjow/supervisor/supervisor.conf restart metro4all:*
