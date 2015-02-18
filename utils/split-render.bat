SET utils_path=d:\Thematic\metro4all\repo\utils
SET python_bin=c:\python27\python
SET ink_bin="c:\Program Files\Inkscape\inkscape.com"

mkdir schemes
mkdir schemes\numbers

for %i in (*.svg) do (
    copy %i %~ni_main.svg
    copy %i %~ni_num.svg
    %python_bin% %utils_path%\turn-svglayers-off.py %~ni_main.svg photos,pointers,element-ids,exits,name_en,numbers
    %python_bin% %utils_path%\turn-svglayers-off.py %~ni_num.svg photos,pointers,element-ids,exits,name_en,name_ru,scheme,meetcode,copyright
    %ink_bin% -d 150 -b white -f %~ni_main.svg -e schemes\%~ni.png
    %ink_bin% -d 150 -f %~ni_num.svg -e schemes\numbers\%~ni.png
    DEL %~ni_main.svg
    DEL %~ni_num.svg
)