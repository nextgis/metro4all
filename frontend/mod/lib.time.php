<?php

define('TIME_DAYFORMAT', stristr(PHP_OS, 'win') ? '%#d' : '%-d');

$time = array(); 

function time_1c_to_stamp($str){
        $temp = explode('T', $str);                                                                                                                                                                 
        $date = explode('-', $temp[0]);                                                                                                                                                                
        $time = explode(':', $temp[1]);                                                                                                                                                                
        if(mb_strpos($time[2], 'Z') !== false)                                                                                                                                                         
            return gmmktime((int)$time[0], $time[1], str_replace('Z', '', $time[2]), $date[1], $date[2], $date[0]);                                                                                         
        else                                                                                                                                                                                           
            return mktime((int)$time[0], $time[1], $time[2], $date[1], $date[2], $date[0]);                                                                                                                 
}

function time_stamp_to_1c($stamp)
{
//	return gmstrftime('%Y-%m-%dT%H:%M:%SZ', $stamp);
	return strftime('%Y-%m-%dT%H:%M:%S', $stamp);
}

/**
 * 7.09.2011 -> 2011-09-07
 * false если входной не подходит  
 */
function date_human_to_mysql($in){
	$in = preg_replace('/[^0-9.]/', '', $in);
	if (!preg_match('/^(\d{1,2})\.(\d{1,2})\.(\d{4})$/', $in, $matches))
		return false;
	$out = $matches[3].'-'.$matches[2].'-'.$matches[1];
	return $out;
}

/**
 * 2011-09-07 -> 7.09.2011
 * '' если входной 0000-00-00 или не подходит
 */
function date_mysql_to_human($in){
	if (!preg_match('/^(\d{4})-(\d{1,2})-(\d{1,2})$/', $in, $matches))
		return '';
	if ($matches[1]=='0000')
		return '';
	$out = intval($matches[3]).'.'.$matches[2].'.'.$matches[1];
	return $out;
}
function date_mysql_to_stamp($in){
	if (!preg_match('/^(\d{4})-(\d{1,2})-(\d{1,2})$/', $in, $matches))
		return false;
	if ($matches[1]=='0000')
		return false;
	$out = mktime(0, 0, 0, $matches[2], $matches[3], $matches[1]); 
	return $out;
}
function date_stamp_to_mysql($stamp){
	return date("Y-m-d", $stamp);
}
function date_human_to_stamp($in){
	if (!preg_match('/^(\d{1,2}).(\d{1,2}).(\d{4})$/', $in, $matches))
		return false;
	if ($matches[1]=='0000')
		return false;
	$out = mktime(0, 0, 0, $matches[2], $matches[1], $matches[3]); 
	return $out;
}

function time_init()
{
    global $time;
    
    $time['current_time'] = time();
    
    $time['current_time_msk'] = $time['current_time'] - 7*60*60;

    $time['current_year'] = strftime('%Y', $time['current_time']);
    $time['current_month'] = intval(strftime('%m', $time['current_time']));

    $time['current_month_length'] = time_get_month_length($time['current_year'], $time['current_month']);

    $time['current_day'] = strftime(TIME_DAYFORMAT, $time['current_time']);

    $time['current_hour'] = intval(strftime('%H', $time['current_time']));
    $time['current_minute'] = intval(strftime('%M', $time['current_time']));
    $time['current_second'] = intval(strftime('%S', $time['current_time']));

    $time['today_begin'] = mktime(0, 0, 0);
    $time['today_end'] = $time['today_begin'] + 86400;

    $time['week_day'] = (date('w') == 0)?6:(date('w') - 1); 

    $time['week_begin'] = $time['today_begin'] - 86400 * $time['week_day'];
    $time['week_end'] = $time['week_begin'] + 86400 * 7;

    $time['hour_begin'] = mktime($time['current_hour'], 0, 0);
    $time['hour_end'] = $time['hour_begin'] + 3600;

    $time['last_7_days'] = $time['today_begin'] - 86400 * 7;
    $time['last_14_days'] = $time['today_begin'] - 86400 * 14;
    $time['last_21_days'] = $time['today_begin'] - 86400 * 21;
    $time['last_28_days'] = $time['today_begin'] - 86400 * 28;

    $time['month_begin'] = mktime(0, 0, 0, $time['current_month'], 1, $time['current_year']);
    
    $time['week_ind'] = date('W')+($time['current_year']-2011)*53;
    $time['dayofweek_ind'] = date('N');
}

function time_get_month_length($year, $month)
{
   return strftime(TIME_DAYFORMAT, mktime(0, 0, 0, $month + 1, 0, $year));
}

function time_get_month($stamp) { return (int)strftime('%m', $stamp); }
function time_get_year($stamp) { return (int)strftime('%Y', $stamp); }

function time_get_day_of_the_week($year, $month, $day)
{
    return strftime('%u', mktime(0, 0, 0, $month, $day, $year));
}

function time_get_week_number($year, $month, $day)
{
    return strftime('%V', mktime(0, 0, 0, $month, $day, $year));
}

function time_get_calendar($year, $month)
{
    $data = array();
    
    $stamp = mktime(0, 0, 0, $month, 1, $year);
    
    $month_start = -date('N', $stamp);

    for($week=0; $week<7; $week++)
    {
        for($day=1; $day<8; $day++)
        {
            $stamp = mktime(0,0,0, $month, 1 + $week*7 + $day + $month_start, $year);
            $data[$week][$day] = array('month' => intval(strftime('%m', $stamp)), 'day' => strftime(TIME_DAYFORMAT, $stamp), 'stamp' => $stamp);
        }
    }

    return $data;
}

function time_stamp2date($stamp) {
    return mktime(0,0,0, date('n', $stamp), date('j', $stamp), date('Y', $stamp));
}

function time_stamp2yearmonth($stamp) {
    return mktime(0,0,0, date('n', $stamp), 1, date('Y', $stamp));
}

/*

function time_str2time($format, $stamp)
{
    $temp = strptime($stamp, $format);
    return mktime($temp['tm_hour'], $temp['tm_min'], $temp['tm_sec'], $temp['tm_mon']+1, $temp['tm_mday'], $temp['tm_year']+1900);
}

*/

function time_format_human_datetime_interval($interval)
{
    $result = '';

    $hour = (int)gmstrftime('%H', $interval);
    $day = (int)gmstrftime(TIME_DAYFORMAT, $interval) - 1;
    $month = (int)gmstrftime('%m', $interval) - 1;
    $year = gmstrftime('%Y', $interval) - 1970;

    if(($year > 0) || ($month > 0) || ($day > 0))
    {
        if($year > 0) $result .= ' '.$year.' '.str_format_human_number($year, array('год', 'года', 'лет'));
        if($month > 0) $result .= ' '.$month.' '.str_format_human_number($month, array('месяц', 'месяца', 'месяцев'));
        if((($year > 0) || ($month > 0)) && ($day > 0)) $result .= ' и';
        if($day > 0) $result .= ' '.$day.' '.str_format_human_number($day, array('день', 'дня', 'дней'));
    }
    else
    {
        $result .= $hour.' '.str_format_human_number($hour, array('час', 'часа', 'часов'));
    }

    return $result;
}

function time_format_time($stamp)
{
    global $time;
    return strftime($time['format_time'], $stamp);
}

function time_format_date($stamp)
{
    global $time;
    return strftime($time['format_date'], $stamp);
}

function time_format_dateyear($stamp)
{
    global $time;
    return strftime($time['format_dateyear'], $stamp);
}

function time_format_datetime($stamp)
{
    global $time;
    return trim(strftime($time['format_datetime'], $stamp));
}

function time_parse_datetime($str)
{
    global $time;
    $temp = sscanf($str, $time['parse_datetime']);
    return @mktime($temp[3], $temp[4], 0, $temp[1], $temp[0], $temp[2]);
}

function time_parse_date($str)
{
    global $time;
    $temp = sscanf($str, $time['parse_date']);
    return @mktime(0, 0, 0, $temp[1], $temp[0], $temp[2]);
}

function time_format_date_human($stamp)
{
    global $time;
    return strftime(str_replace('%mh', $time['name_month_day'][(int)strftime('%m', $stamp)], $time['format_date_human']), $stamp);
}

function time_format_dateyear_human($stamp)
{
    global $time;
    return strftime(str_replace('%mh', $time['name_month_day'][(int)strftime('%m', $stamp)], $time['format_dateyear_human']), $stamp);
}

function time_format_datetimeyear_human($stamp)
{
    global $time;
    return strftime(str_replace('%mh', $time['name_month_day'][(int)strftime('%m', $stamp)], $time['format_datetimeyear_human']), $stamp);
}

function time_format_age($stamp, $is_daily = false)
{
    $current_time = time();
    $age = $current_time - $stamp;
    
    if($is_daily)
        $text = 'Сегодня';
    else
        $text = 'Только что';
        
    if($age >= 60 && $age < 60*60 && !$is_daily)
    {
        $interval = $age/60;
        $text = intval($interval).' '.str_format_human_number($interval, array('минуту', 'минуты', 'минут')).' назад';
    }
    elseif($age >= 60*60 && $age < 60*60*24 && !$is_daily)
    {
        $interval = $age/(60*60);
        $text = intval($interval).' '.str_format_human_number($interval, array('час', 'часа', 'часов')).' назад';
    }
    elseif($age >= 60*60*24 && $age < 60*60*24*1)
    {
        $text = 'вчера '.time_format_time($stamp);
    }
    elseif($age >= 60*60*24*1)
    {
        $text = time_format_dateyear_human($stamp).' '.time_format_time($stamp);
    }
    
    return $text;
}

function time_is_holyday($year, $month, $day)
{
    global $time;
    time_init_calendar($year);
    return isset($time['calendar_holyday'][$year][$month][$day]);
}

function time_is_short($year, $month, $day)
{
    global $time;
    time_init_calendar($year);
    return isset($time['calendar_holyday'][$year][$month][$day]);
}

function time_init_calendar($year)
{
    global $time;
    
    if(!isset($time['calendar_holyday']))
    {
        $time['calendar_holyday'] = array();
        foreach(explode("\n", trim(file_get_contents('calendar/'.$year.'_holyday.txt'))) as $row)
        {
            list($row_month, $row_days) = explode(':', $row);
            foreach(explode(' ', trim($row_days)) as $row_day)
                $time['calendar_holyday'][$year][$row_month][$row_day] = true;
        }
    }

    if(!isset($time['calendar_short']))
    {
        $time['calendar_short'] = array();
        foreach(explode("\r\n", trim(file_get_contents('calendar/'.$year.'_short.txt'))) as $row)
        {
            list($row_month, $row_days) = explode(':', $row);
            foreach(explode(' ', trim($row_days)) as $row_day)
                $time['calendar_short'][$year][$row_month][$row_day] = true;
        }
    }
}

/* names */

$time['name_week_day_short'] = array(
0 => 'Вс',
1 => 'Пн',
2 => 'Вт',
3 => 'Ср',
4 => 'Чт',
5 => 'Пт',
6 => 'Сб',
7 => 'Вс',
);

$time['name_week_day_full'] = array(
0 => 'воскресенье',
1 => 'понедельник',
2 => 'вторник',
3 => 'среда',
4 => 'четверг',
5 => 'пятница',
6 => 'суббота',
7 => 'воскресенье',
);

$time['name_week_day_short_lower'] = array(
0 => 'вс',
1 => 'пн',
2 => 'вт',
3 => 'ср',
4 => 'чт',
5 => 'пт',
6 => 'сб',
7 => 'вс',
);

$time['name_month_day'] = array(
1 => 'января',
2 => 'февраля',
3 => 'марта',
4 => 'апреля',
5 => 'мая',
6 => 'июня',
7 => 'июля',
8 => 'августа',
9 => 'сентября',
10 => 'октября',
11 => 'ноября',
12 => 'декабря',
);

$time['name_month'] = array(
1 => 'Январь',
2 => 'Февраль',
3 => 'Март',
4 => 'Апрель',
5 => 'Май',
6 => 'Июнь',
7 => 'Июль',
8 => 'Август',
9 => 'Сентябрь',
10 => 'Октябрь',
11 => 'Ноябрь',
12 => 'Декабрь',
);

$time['name_month_short'] = array(
1 => 'Янв',
2 => 'Фев',
3 => 'Мар',
4 => 'Апр',
5 => 'Май',
6 => 'Июн',
7 => 'Июл',
8 => 'Авг',
9 => 'Сен',
10 => 'Окт',
11 => 'Ноя',
12 => 'Дек',
);

/* formats */

$time['format_date'] = TIME_DAYFORMAT.'.%m';
$time['format_dateyear'] = TIME_DAYFORMAT.'.%m.%Y';
$time['format_time'] = '%H:%M';
$time['format_datetime'] = TIME_DAYFORMAT.'.%m.%Y %H:%M';
$time['format_date_human'] = '%e %mh';
$time['format_dateyear_human'] = TIME_DAYFORMAT.' %mh %Y г.';
$time['format_datetimeyear_human'] = TIME_DAYFORMAT.' %mh %Y г. %H:%M';
$time['parse_datetime'] = '%u.%u.%u %u:%u';
$time['parse_date'] = '%u.%u.%u';

if (stripos(php_uname('s'), 'windows')!==false):
	foreach ($time as $k=>$val)
		if (is_string($val)) $time[$k] = str_replace('%e', '%d', $val);
endif;

/* timer */

$_timer = 0;

function getmicrotime() 
{ 
	if(version_compare(phpversion(), '5.0.0', '<'))
	{
	    list($usec, $sec) = explode(" ", microtime()); 
	    return ((float)$usec + (float)$sec); 
	}
	else
	{
		return microtime(1);
	}
}

function timer_start() { global $_timer; $_timer = getmicrotime(); }
function timer_stop() { global $_timer; $_timer = getmicrotime() - $_timer; }

function timer_format() { global $_timer; return sprintf('%0.3f sec', round($_timer,3)); }

function time_to_zone($stamp, $from_gmc, $to_gmc)
{
	if((int)$from_gmc && (int)$to_gmc)
		return $stamp - $from_gmc*3600 + $to_gmc*3600;
	else
		return $stamp;
}
