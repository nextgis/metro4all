<?php

function str_ellipse($str, $max_length)
{
    return (mb_strlen($str) > $max_length)?(trim(mb_substr($str, 0, $max_length)).'...'):$str;
}

function text($string, $is_multiline=false, $is_allow_html=false)
{
    $result = trim($string);
    
    if(!$is_allow_html)
        $result = str_replace('javascript:', '', htmlspecialchars($result, ENT_COMPAT, 'UTF-8'));
        
    if($is_multiline)
    {
    	$result = str_replace("&nbsp;", " ", $result);
        $result = str_replace("<br>", "{br}", $result);
        $result = str_replace("<br/>", "{br}", $result);
        $result = str_replace("<br />", "{br}", $result);
        $result = str_replace("\r\n", "{br}", $result);
        $result = str_replace("\r", "{br}", $result);
        $result = str_replace("\n", "{br}", $result);
        $result = str_replace("{br}", "\r\n", $result);
        
        $temp = '';
        foreach(explode("\r\n", $result) as $line)
            $temp .= trim($line)."\r\n";
            
        $result = trim($temp);

        $text = '';
        foreach(explode("\r\n\r\n", $result) as $paragraph)
        {
            if(trim($paragraph) == '') continue;
            
            $lines = explode("\r\n", trim($paragraph));
            
            $last = count($lines) - 1;
            
            $text .= '<p>';
            for($i=0; $i<count($lines); $i++)
            {
                $line = trim($lines[$i]);
            
                if($line == '') continue;
            
                $text .= $line;
                
                if($i != $last)
                    $text .= '<br>';
                
                $text .= "\r\n";
            }
                
            $text .= '</p>'."\r\n";
        }
        $result = $text;
    }

    $result = str_replace('&amp;emdash;', '&#150;', $result);
    $result = str_replace('<clear/>', '<div style="clear:both"></div>', $result);

    return $result;
}

function url($string)
{
    return str_replace('javascript:', '', htmlspecialchars(trim($string), ENT_COMPAT, 'UTF-8'));
}

function str_format_human_file_size($value)
{
    if(intval($value) < 0) return '';
    if(intval($value) > 1073741824) return round(intval($value)/1073741824, 2).' Гб';
    if(intval($value) > 1048576) return round(intval($value)/1048576, 2).' Мб';
    if(intval($value) > 1024) return round(intval($value)/1024).' Кб';
    return intval($value).' байт';
}

function str_format_human_datetime_age($stamp, $is_daily = false)
{
    $current_time = time();
    $age = $current_time - $stamp;
    
    if($is_daily)
        $text = 'сегодня';
    else
        $text = 'только что';
        
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
    elseif($age >= 60*60*24 && $age < 60*60*24*2)
    {
        $text = 'вчера';
    }
    elseif($age >= 60*60*24*2)
    {
        $text = format_date($stamp);
    }

    return $text;
}

function str_format_human_number($number, $titles)
{
    // 1, 2, 5
    $cases = array (2, 0, 1, 1, 1, 2);
    return $titles[ ($number%100>4 && $number%100<20)? 2 : $cases[min($number%10, 5)] ];
}

/* multibyte */

function mb_str_ireplace($search, $replace, $subject)
{
    $subject_lower = mb_strtolower($subject);
    $search_lower = mb_strtolower($search);
    $search_length = mb_strlen($search);
    $replace_length = mb_strlen($replace);
    $offset = 0;
   
    while(!is_bool($pos = mb_strpos($subject_lower, $search_lower, $offset)))
    {
        $offset = $pos + $replace_length;
        $subject = mb_substr($subject, 0, $pos).$replace.mb_substr($subject, $pos + $search_length);
        $subject_lower = mb_strtolower($subject);
    }
   
    return $subject;
}

function mb_highlite($search, $subject, $max_length=false)
{
	$subject = text($subject);

    $subject_lower = mb_strtolower($subject);
    $search_lower = mb_strtolower($search);
    $search_length = mb_strlen($search);
    $replace_length = mb_strlen('<strong>'.$search.'</strong>');
    $offset = 0;
    
    if($max_length !== false)
    {
        if(mb_strlen($subject) > $max_length)
        {
            $start_pos = mb_strpos($subject_lower, $search_lower, 0);
            $stop_pos = mb_strrpos($subject_lower, $search_lower, 0) + $search_length;
            
            if(($stop_pos - $start_pos) > $max_length)
                $stop_pos = $start_pos;

            $bounds = round(abs($max_length/2 - ($stop_pos - $start_pos)));
            
            if($start_pos !== false)
            {
                $subject = ((($start_pos - $bounds) > 0)?'...':'').
                    trim(mb_substr($subject, (($start_pos - $bounds) >= 0)?($start_pos - $bounds):0, ($stop_pos - $start_pos) + $bounds*2)).
                    ((($stop_pos + $bounds) < mb_strlen($subject))?'...':'');
            }
        }
    }
   
    $subject_lower = mb_strtolower($subject);
    
    while(!is_bool($pos = mb_strpos($subject_lower, $search_lower, $offset)))
    {
        $offset = $pos + $replace_length;
        $subject = mb_substr($subject, 0, $pos).'<strong>'.mb_substr($subject, $pos, $search_length).'</strong>'.mb_substr($subject, $pos + $search_length);
        $subject_lower = mb_strtolower($subject);
    }

    return $subject;
}

function str_format_url($str)
{
//	$str = mb_strtolower($str, 'UTF-8');
	$str = mb_substr($str, 0, 64);
	$str = preg_replace('/[^A-Za-zА-Яа-я0-9]+/iu', ' ', $str);
	$str = preg_replace('/[\s]+/iu', '-', trim($str));
	return $str;
}

