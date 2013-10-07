<?php
// Для запуска нужно ввести php -f metroreport.php, этот скрипт внутри себя записывает все echo в буфер (применено ob_start), потом делает простую запись в файл


//интегральный показатель
//по годам 
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=project13 user=user password=user")
    or die('Could not connect: ' . pg_last_error());

ob_start();
	
	
function execute_query_for_report($element)
{
// Performing SQL query
$query = $element['sql'];
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// Printing results in HTML

?>
<p>
<div class="name">
<?= $element['text']?>
</div>
<table>
<tr>
<?

foreach($element['table_header'] AS $text)
{
echo '<th>'.$text.'</th>';
}
echo '</tr>';
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    echo "\t<tr>\n";
    foreach ($line as $col_value) {
        echo "\t\t<td>$col_value</td>\n";
    }
    echo "\t</tr>\n";
}
echo "</table>\n";
?>
<div class="query">
<?= $element['sql']?>
</div>
&nbsp;

<?

}	
	
?>
<!DOCTYPE html>
<html>

<head>
    <title></title>
    <meta
    http-equiv="Content-Type"
    content="text/html; charset=utf-8"
    />

    <meta
    charset="utf-8"
    />

<style>
 <!-- CSS goes in the document HEAD or added to your external stylesheet -->
<style>
table {
	font-family: verdana,arial,sans-serif;
	font-size:11px;
	color:#333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}
table th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}
table td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}

.query 
	{
		font-size: 10px;
		font-color:gray;
	}
 .name
	{
		font-weight: bold;
	}
</style>

</head>
<body>

Содержание обновляется автоматически
<?
	
	
	
	
$element['text']='Количество выходов с различным числом эскалаторов';
$element['sql']="
SELECT escalators_count::integer AS escalators_count, COUNT(escalators_count::integer)
FROM 
exits
WHERE escalators_count IS NOt NULL
GROUP BY escalators_count::integer
ORDER BY escalators_count
;
";
$element['table_header']=array('Количество эскалаторов','Количество выходов');
execute_query_for_report($element);	
	
		
$element['text']='Список станций, на которых имеется лифт';
$element['sql']="
SELECT name
FROM exits
WHERE lift::integer >0
GROUP BY stationcode1, name
ORDER BY name
;
";
$element['table_header']=array('Название станции');
execute_query_for_report($element);	
	
			
$element['text']='Распределение станций по наличию лифтов';
$element['sql']="
SELECT lift, count(stationcode2) FROM
(
SELECT DISTINCT lift::integer,  stationcode2::integer
FROM exits
) AS sub
GROUP BY lift
;
";
$element['table_header']=array('Есть лифт','Количество станций');
execute_query_for_report($element);	


$element['text']='Количество станций в системе';
$element['sql']="
SELECT  count(DISTINCT stationcode2::integer) 
FROM exits
;
";
$element['table_header']=array('Количество станций');
execute_query_for_report($element);	



$element['text']='Список станций, на которых можно совершить высадку и посадку, используя только лестницы с аппарелями, и не используя эскалаторы, то есть можно удобно ЗАКАТИТЬ коляску. Включая те, что с короткими лестницами в 1-4 ступеньки на входе в вестибюль.';
$element['sql']="
SELECT  name
FROM exits
WHERE min_steps_incline::integer<=4
AND escalators_count::integer=0
group by name, stationcode2
order by name
";
$element['table_header']=array('Название станции');
execute_query_for_report($element);	


$element['text']='Количество станций, на которых можно совершить высадку и посадку, используя только лестницы с аппарелями, и не используя эскалаторы, то есть можно удобно ЗАКАТИТЬ коляску. Включая те, что с короткими лестницами в 1-4 ступеньки на входе в вестибюль..';
$element['sql']="
SELECT  name
FROM exits
WHERE min_steps_incline::integer<=4
AND escalators_count::integer=0
group by name, stationcode2
order by name
;
";
$element['table_header']=array('Количество станций');
execute_query_for_report($element);	


$element['text']='Список станций, где на посадку есть ТОЛЬКО узкие турникеты, (и ты через них не пролезешь). Взято максимальная ширина прохода со всех выходов станции';
$element['sql']="
SELECT  name, max(min_width_pass::integer), stationcode2
FROM exits
where min_width_pass::integer<650
AND direction IN ('both','in')
group by name, stationcode2
order by max
;
";
$element['table_header']=array('Количество станций');
execute_query_for_report($element);	


$element['text']='Количество станций, где на посадку есть ТОЛЬКО узкие турникеты,';
$element['sql']="
SELECT count(  stationcode2)
FROM exits
where min_width_pass::integer<650
AND direction IN ('both','in')
;
";
$element['table_header']=array('Количество станций');
execute_query_for_report($element);	
	
	

$element['text']='Список станций, где на ВЫСАДКУ есть ТОЛЬКО узкие турникеты, (то есть по человечески не вылезешь).';
$element['sql']="
SELECT  name, max(min_width_pass::integer), stationcode2
FROM exits
where min_width_pass::integer<650
AND direction IN ('out')
group by name, stationcode2
order by max
;
";
$element['table_header']=array('Количество станций');
execute_query_for_report($element);	


$element['text']='Количество станций, где на ВЫСАДКУ есть ТОЛЬКО узкие турникеты, (то есть по человечески не вылезешь).';
$element['sql']="
SELECT count(  stationcode2)
FROM exits
where min_width_pass::integer<650
AND direction IN ('out')
;
";
$element['table_header']=array('Количество станций');
execute_query_for_report($element);	
	

	
	


	
	
	
?>
<p>
Коллея аппарелей: от 300 до 800 или 900
</p>
<?	
	
	/*
	сделать таблицу: 
	только на эскалаторе
	переход по аппарелям
	тоолько с переносом по ступенькам
	
	*/
$element['text']='Количество пересадок, где не надо идти по лестнице, а только ехать на эскалаторе.';
$element['sql']="
SELECT count(*)/2
FROM interchanges
WHERE min_steps_foot::integer=0
";
$element['table_header']=array('Количество пар пересадок');
execute_query_for_report($element);	

$element['text']='Количество пересадок, где можно провести тележку по аппарелям, и нет эскалаторов';
$element['sql']="
SELECT count(*)/2
FROM interchanges
WHERE min_steps_incline::integer=0
AND escalators_count::integer=0
";
$element['table_header']=array('Количество пар пересадок');
execute_query_for_report($element);	
	
	
$element['text']='Количество пересадок, где обязателен перенос по ступенькам.';
$element['sql']="
SELECT  ceil((count(*)/2)/2::real)*2
FROM interchanges
WHERE min_steps_incline::integer >0


";
$element['table_header']=array('Количество пар пересадок');
execute_query_for_report($element);			


$element['text']='Количество пересадок, всего';
$element['sql']="
SELECT  ceil((count(*)/2)/2::real)*2
FROM interchanges

";
$element['table_header']=array('Количество пар пересадок');
execute_query_for_report($element);		
	
	
/*

SELECT  name, direction, min_width_pass::integer, stationcode2, id2
FROM exits
where min_width_pass::integer<65000
AND name IN( 'Студенческая','Багратионовская')
order by id2, min_width_pass::integer

;



Бульвар Дмитрия Донского
Студенческая
Багратионовская

<p>
Количество станций, где на посадку турникеты широкие, а на высадку - узкие.
</p>

<p>
Количество станций, где на вход или выход можно ехать только по эскалатору.
</p>

<p>
Количество СТАНЦИЙ, где на выход ИЛИ вход надо обязательно идти по лестнице.
</p>

<p>
Количество СТАНЦИЙ, где на выход ИЛИ вход надо обязательно идти по лестнице, и нигде нет аппарелей.
</p>

<p>
Предыдущие два пункта, но с группировкой по направлениям (вход/выход)
</p>

<p>
Распределение неудобных станций из прошлого пункта по годам постройки.
</p>

<p>
Средняя минимально допустимая коллея коляски или тележки (взята половина минимальных значений этого параметра)
</p>

<p>
Средняя максимально допустимая коллея коляски или тележки (взята половина максимальных значений этого параметра)
</p>

<p>

</p>

<p>
Количество маршрутов пересадок, где обязательно надо идти по лестнице.
</p>

<p>
Количество маршрутов пересадок, где надо идти по лестнице, а аппарелей нет.
</p>

<p>
Рейтинг станций по некоторому комплексному показателю. Не знаю как их считать.
</p>

<p>	
	

<p>
Количество станций, на которых в одну сторону есть эскалатор, а в другую - нет.
</p>
	
	
	
	
*/

// Free resultset
pg_free_result($result);

// Closing connection
pg_close($dbconn);





file_put_contents('report_from_php.htm',ob_get_contents());
?>
