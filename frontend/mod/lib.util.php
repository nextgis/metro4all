<?php

function escape($str) {
	return htmlspecialchars($str, ENT_COMPAT, 'UTF-8');
}

function url_encode_entire_callback($matches){
	return urlencode($matches[0]);
}
function url_encode_entire($url){
	return preg_replace_callback('/[А-я ]+/u', 'url_encode_entire_callback', $url);
}

function url_encode_entire_spchars($url){
	return htmlspecialchars(preg_replace_callback('/[А-я ]+/u', 'url_encode_entire_callback', $url));
}

function go($value) { 
	if (headers_sent())
		print "<script>window.location.href='".mysql_escape_string($value)."'</script>";
	else{
		header('Location: '.$value); 
	}
	exit(); 
}
function go_permanent($value) { 
	if (headers_sent())
		print "<script>window.location.href='".mysql_escape_string($value)."'</script>";
	else{
		header("HTTP/1.1 301 Moved Permanently");
		header('Location: '.$value); 
	}
	exit(); 
}
function go_back($param = false) { 
	if (!isset($_SERVER['HTTP_REFERER']) || $_SERVER['HTTP_REFERER']=='')
		out();
	$url = $_SERVER['HTTP_REFERER'];
	if ($param!=false){
		$url.=(mb_strpos($url, '?')===false?'?':'&');
		$url.=$param;
	}
		go($url); 
}
function out() {
	if (defined('PROFILER_NOTICE')){
		print "<h2>Out called. stack:</h2>";
		debug_print_backtrace();
		die();
	}  
	header("HTTP/1.0 404 Not Found");
	go('/'); 
}

function _GET($key){
	return (isset($_GET[$key])?$_GET[$key]:'');
}
function _POST($key){
	return (isset($_POST[$key])?$_POST[$key]:'');
}
function _REQUEST($key){
	return (isset($_REQUEST[$key])?$_REQUEST[$key]:'');
}

function error_page($name = 'error'){
    header('Content-type: text/html; charset=windows-1251'); 
    if (file_exists(ROOT.'static/'.$name.'.html'))
		readfile(ROOT.'static/'.$name.'.html');
	print '<!--apache-->';
	if (isset($_COOKIE['enable_profiling'])){
		print "<!--\r\n\r\n";
		debug_print_backtrace();
		print "\r\n\r\n-->";
	}
	die();
}

function checked2int($value) { return ($value=='on')?1:0; };

function request_int($name, $is_required = false)
{
    if(isset($_REQUEST[$name]))
        return (int)$_REQUEST[$name];
    elseif($is_required)
        die();
    else
        return 0;
}

function request_float($name, $is_required = false) {
	if (isset($_REQUEST[$name]))
		return (float)$_REQUEST[$name];
	elseif ($is_required)
		die();
	else 
		return 0;
}

function request_str($name, $is_required = false)
{
    if(isset($_REQUEST[$name]))
        return trim($_REQUEST[$name]);
    elseif($is_required)
        die();
    else
        return '';
}

function request_datetime($name, $is_required = false)
{
	if(isset($_REQUEST[$name.'_date']) && isset($_REQUEST[$name.'_time']))
	{
		return time_parse_datetime($_REQUEST[$name.'_date'].' '.$_REQUEST[$name.'_time']);
	}
	elseif($is_required)
		die();
	else
		return 0;
}

function request_date($name, $is_required = false)
{
	if (isset($_REQUEST[$name]))
		return time_parse_date($_REQUEST[$name]);
	elseif($is_required)
		die();
	else
		return 0;
}

function request_bool($name, $is_required = false)
{
	return isset($_REQUEST[$name])?true:false;
}

function request_checked($name, $is_required = false)
{
	$value = request_str($name, $is_required);
	return ($value == 'on') ? 1 : 0;
}

function request_array($name, $is_required = false)
{
	if (isset($_REQUEST[$name]) && is_array($_REQUEST[$name]))
		return $_REQUEST[$name];
	elseif ($is_required)
		die();
	else
		return array();
}

/*

function str2datetime($value)
{
    $temp = strptime($value, FORMAT_DATETIME);
    return mktime($temp['tm_hour'], $temp['tm_min'], $temp['tm_sec'], 1+$temp['tm_mon'], $temp['tm_mday'], 1900+$temp['tm_year']);
}

function str2date($value)
{
    $temp = strptime($value, FORMAT_DATE);
    return mktime($temp['tm_hour'], $temp['tm_min'], $temp['tm_sec'], 1+$temp['tm_mon'], $temp['tm_mday'], 1900+$temp['tm_year']);
}

*/

/*
function error($message) { if(error_reporting() >= E_ERROR) echo $message; die(); }
function location($value) { header('location: '.$value); exit(); }
function checked2int($value) { return ($value=='on')?1:0; };

function clean_string($value) { return str_replace('javascript:', '', htmlspecialchars($value, ENT_COMPAT, 'UTF-8')); }

function text($string, $is_multiline=false, $is_allow_html=false)
{
    $result = trim($string);
    
    if(!$is_allow_html) $result = clean_string($string);
    
    $text = '';
    if($is_multiline)
    {        
        foreach(explode("\r\n\r\n", $result) as $paragraph)
        {
            $text .= '<p>';
            foreach(explode("\r\n", $paragraph) as $line) $text .= $line.'<br />'."\r\n";
            $text .= '</p>'."\r\n";
        }
    }
    else
        foreach(explode("\r\n", $result) as $line) $text .= $line.'<br />'."\r\n";

    $result = str_replace('&amp;emdash;', '&#150;', $result);
    $result = str_replace('<clear/>', '<div style="clear:both"></div>', $result);

    return $result;
}

function str2text($value, $is_typografica=false, $is_multiline=false, $is_safe=true)
{
    $result = $value;

    if($is_safe) $result = clean_string($result);

    if($is_multiline)
    {
        $text = '';
        
        $is_html = false;
        
        foreach(explode("\r\n\r\n", $result) as $paragraph)
        {
            if($paragraph == '<html>') { $is_html = true; continue; }
            if($paragraph == '</html>') { $is_html = false; continue; }
            
            if(!$is_html)
            {
                $is_list = false;
                
                $paragraph_text = '';
                foreach(explode("\r\n", $paragraph) as $row)
                {
                    if(mb_substr(trim($row), 0, 2) == "* ")
                    {
                        $paragraph_text .= '<li>'.mb_substr($row, 2).'</li>'."\r\n";
                        $is_list = true;
                        continue;
                    }
                    
                    $paragraph_text .= $row."<br/>\r\n";
                }
                
                if($is_list)
                    $text .= '<ul>'.$paragraph_text.'</ul>';
                else
                    $text .= "<p>".mb_substr($paragraph_text, 0, -7)."</p>\r\n";
            }
            else
                $text .= $paragraph."\r\n";
        }
        
        $result = $text;
    }

    $result = str_replace('&amp;emdash;', '&#150;', $result);
    $result = str_replace('<clear/>', '<div style="clear:both"></div>', $result);

    return $result;
}

function str2input($value) { return str2text($value); }


function size2str($value)
{
    global $lang;
    
    if(intval($value) < 0) return $lang['size_no_data'];
    if(intval($value) < 1024) return intval($value).' '.$lang['size_byte'];
    if((intval($value) > 1024) && (intval($value) < 1048576)) return round(intval($value)/1024).' '.$lang['size_kilobyte'];
    if(intval($value) > 1048576) return round(intval($value)/1048576,2).' '.$lang['size_megabyte'];
    return $value;
}

*/

/*

function mb_str_ireplace($co, $naCo, $wCzym)
{
    $wCzymM = mb_strtolower($wCzym);
    $coM    = mb_strtolower($co);
    $offset = 0;
   
   while(!is_bool($poz = mb_strpos($wCzymM, $coM, $offset)))
    {
        $offset = $poz + mb_strlen($naCo);
        $wCzym = mb_substr($wCzym, 0, $poz). $naCo .mb_substr($wCzym, $poz+mb_strlen($co));
        $wCzymM = mb_strtolower($wCzym);
    }
   
    return $wCzym;
}

*/

function last_modified_headers($update_stamp)
{
	if(@isset($_SERVER['HTTP_IF_MODIFIED_SINCE']))
	{
		$if_modified_since = strtotime($_SERVER['HTTP_IF_MODIFIED_SINCE']);

		if($if_modified_since >= $update_stamp)
		{
			header('HTTP/1.0 304 Not Modified');
//			header('Cache-Control: max-age=86400, must-revalidate');
			return false;
		}
	}

	$last_modified = gmdate('D, d M Y H:i:s', $update_stamp).' GMT';

//	header('Cache-Control: max-age=86400, must-revalidate');
	header('Last-Modified: '.$last_modified);        
}

function strip_tags_ex_callback($arg){
	$entity = mb_strtolower($arg[1]);
	$s = '';
	switch ($entity){
		case 'nbsp': 	$s = ' '; break;
		case 'ndash': 	$s = '-'; break;
		case 'mdash': 	$s = '-'; break;
		//case 'lt': 	$s = '<'; break;
		//case 'gt': 	$s = '>'; break;
		case 'amp': 	$s = '&'; break;
		case 'copy': 	$s = '©'; break;
		case 'reg': 	$s = '®'; break;
		case 'trade': 	$s = '™'; break;
	}
	return $s; 
}

function strip_tags_ex($str, $allowable_tags = ''){
	$str = strip_tags($str, $allowable_tags);
	$str = preg_replace_callback('/&([A-z]{2,6});/ui', 'strip_tags_ex_callback', $str);
	return $str;
}

/**
 * Выводит сумму прописью
 * @param int|float $inn
 * @param bool $stripkop
 * @return string сумма прописью
 */
function num2str($inn, $stripkop=false) {
    $nol = 'ноль';
    $str[100]= array('','сто','двести','триста','четыреста','пятьсот','шестьсот', 'семьсот', 'восемьсот','девятьсот');
    $str[11] = array('','десять','одиннадцать','двенадцать','тринадцать', 'четырнадцать','пятнадцать','шестнадцать','семнадцать', 'восемнадцать','девятнадцать','двадцать');
    $str[10] = array('','десять','двадцать','тридцать','сорок','пятьдесят', 'шестьдесят','семьдесят','восемьдесят','девяносто');
    $sex = array(
        array('','один','два','три','четыре','пять','шесть','семь', 'восемь','девять'),// m
        array('','одна','две','три','четыре','пять','шесть','семь', 'восемь','девять') // f
    );
    $forms = array(
        array('копейка', 'копейки', 'копеек', 1), // 10^-2
        array('рубль', 'рубля', 'рублей',  0), // 10^ 0
        array('тысяча', 'тысячи', 'тысяч', 1), // 10^ 3
        array('миллион', 'миллиона', 'миллионов',  0), // 10^ 6
        array('миллиард', 'миллиарда', 'миллиардов',  0), // 10^ 9
        array('триллион', 'триллиона', 'триллионов',  0), // 10^12
    );
    $out = $tmp = array();
    // Поехали!
    $tmp = explode('.', str_replace(',','.', $inn));
    $rub = number_format($tmp[ 0], 0,'','-');
    if ($rub== 0) $out[] = $nol;
    // нормализация копеек
    $kop = isset($tmp[1]) ? substr(str_pad($tmp[1], 2, '0', STR_PAD_RIGHT), 0,2) : '00';
    $segments = explode('-', $rub);
    $offset = sizeof($segments);
    if ((int)$rub== 0) { // если 0 рублей
        $o[] = $nol;
        $o[] = morph( 0, $forms[1][ 0],$forms[1][1],$forms[1][2]);
    }
    else {
        foreach ($segments as $k=>$lev) {
            $sexi= (int) $forms[$offset][3]; // определяем род
            $ri = (int) $lev; // текущий сегмент
            if ($ri== 0 && $offset>1) {// если сегмент==0 & не последний уровень(там Units)
                $offset--;
                continue;
            }
            // нормализация
            $ri = str_pad($ri, 3, '0', STR_PAD_LEFT);
            // получаем циферки для анализа
            $r1 = (int)substr($ri, 0,1); //первая цифра
            $r2 = (int)substr($ri,1,1); //вторая
            $r3 = (int)substr($ri,2,1); //третья
            $r22= (int)$r2.$r3; //вторая и третья
            // разгребаем порядки
            if ($ri>99) $o[] = $str[100][$r1]; // Сотни
            if ($r22>20) {// >20
                $o[] = $str[10][$r2];
                $o[] = $sex[ $sexi ][$r3];
            }
            else { // <=20
                if ($r22>9) $o[] = $str[11][$r22-9]; // 10-20
                elseif($r22> 0) $o[] = $sex[ $sexi ][$r3]; // 1-9
            }
            // Рубли
            $o[] = morph($ri, $forms[$offset][ 0],$forms[$offset][1],$forms[$offset][2]);
            $offset--;
        }
    }
    // Копейки
    if (!$stripkop) {
        $o[] = $kop;
        $o[] = morph($kop,$forms[ 0][ 0],$forms[ 0][1],$forms[ 0][2]);
    }
    return preg_replace("/\s{2,}/",' ',implode(' ',$o));
}
 
/**
 * Склоняем словоформу
 */
function morph($n, $f1, $f2, $f5) {
    $n = abs($n) % 100;
    $n1= $n % 10;
    if ($n>10 && $n<20) return $f5;
    if ($n1>1 && $n1<5) return $f2;
    if ($n1==1) return $f1;
    return $f5;
}

/**
 * Преобразует первый символ к верхнему регистру
 * @param string $string исходная строка
 * @param string $e кодировка, по умолчанию utf-8
 * @return string строка с заглавным первым символом
 */
function ucfirst_util($string, $e ='utf-8') {
	if (function_exists('mb_strtoupper') && function_exists('mb_substr') && !empty($string)) {
    	$string = mb_strtolower($string, $e);
        $upper = mb_strtoupper($string, $e);
        preg_match('#(.)#us', $upper, $matches);
        $string = $matches[1] . mb_substr($string, 1, mb_strlen($string, $e), $e);
	} else {
    	$string = ucfirst($string);
	}
	return $string;
} 

/**
 * Возвращает сумму НДС, которая содержится в указанной сумме
 * @param int|float $sum_with_vat сумма, которая содержит в себе НДС
 * @param int|float $vat процент НДС, по умолчанию 18%
 * @return float сумма НДС
 */
function vat_sum_from_sum($sum_with_vat, $vat = 18) {
	$sum_with_vat = (float)$sum_with_vat;
	$vat = (float)$vat;
	
	$vat_sum = $sum_with_vat - $sum_with_vat/(1 + $vat/100);
	
    return round($vat_sum,2);
}

/**
 * Функция проверяет правильность инн
 * http://anton-pribora.ru/articles/php/php-javascript-inn/
 *
 * @param string $inn
 * @return bool
 */
function is_valid_inn( $inn )
{
    if ( preg_match('/\D/', $inn) ) return false;
    
    $inn = (string) $inn;
    $len = strlen($inn);
    
    if ( $len === 10 )
    {
        return $inn[9] === (string) (((
            2*$inn[0] + 4*$inn[1] + 10*$inn[2] + 
            3*$inn[3] + 5*$inn[4] +  9*$inn[5] + 
            4*$inn[6] + 6*$inn[7] +  8*$inn[8]
        ) % 11) % 10);
    }
    elseif ( $len === 12 )
    {
        $num10 = (string) (((
             7*$inn[0] + 2*$inn[1] + 4*$inn[2] +
            10*$inn[3] + 3*$inn[4] + 5*$inn[5] + 
             9*$inn[6] + 4*$inn[7] + 6*$inn[8] +
             8*$inn[9]
        ) % 11) % 10);
        
        $num11 = (string) (((
            3*$inn[0] +  7*$inn[1] + 2*$inn[2] +
            4*$inn[3] + 10*$inn[4] + 3*$inn[5] +
            5*$inn[6] +  9*$inn[7] + 4*$inn[8] +
            6*$inn[9] +  8*$inn[10]
        ) % 11) % 10);
        
        return $inn[11] === $num11 && $inn[10] === $num10;
    }
    
    return false;
}