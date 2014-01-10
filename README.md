metroaccess
===========

Доступность метрополитена для людей с ограниченными возможностями


Обновление данных:
--------
1. Скачать в формате .csv документы из Metro4All => msk => data
  * данные по переходам - это interchanges.csv
  * данные по выходам - это portals.csv

2. Запустить
  * python prepare_interchanges_data.py interchanges.csv
  * python prepare_portals_data.py portals.csv

3. Заменить interchanges.csv и portals.csv в папке metroaccess/data/msk

4. Закоммитать изменения и загрузить на сервер.


Обновление схем:
--------
Схемы загружаются простым копированием в директории проекта:
  * metroaccess/data/msk/schemes - для Москвы
  * metroaccess/data/spb/schemes - для Санкт-Петербурга