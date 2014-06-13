<?php

date_default_timezone_set('Europe/Moscow');

$config['http_root'] = '/';
$config['http_domain'] = 'metro4all.org';

$config['site'] = array(
	'url' => 'http://metro4all.org/',
	'title' => 'metro4all.org',
	'email_title' => 'metro4all.org',
	'email' => 'info@metro4all.ru',
);

$config['database'] = array(
    'server' 	=> 'localhost',
    'database' 	=> 'metro4all',
    'username' 	=> 'keliones',
    'password' 	=> 'denmapper234',
    'codepage' 	=> 'utf8',
    'collate' 	=> 'utf8_bin',
);

$config['user'] = array(
	'cookie_name' => 'metro4all_user',
	'cookie_domain' => $config['http_domain'],
	'cookie_path' => $config['http_root'],
	'cookie_secure' => 0,
	'cookie_expire' => 86400, // one week
	'cookie_password_salt' => 'hd9123h;pfhsfosdf89dudf978%^#o12M@D#*()N@FPuj',
   	'password_salt' => 'hO*&YOL!#Y[F@H*U{@#$JI(FU*(P#$@FHN@#)F(',
	'lost_password_salt' => 'M)@(!$U{N!BRYFY{P(YF)EJ:NCLOHEE~(PC#()UIN$)@*',
);

define('DB', 'm4a_');

define('M_USER', 1);

$config['languages'] = array(
    'ru' => array( 'menu' => 'Ру' ),
    'en' => array( 'menu' => 'En' ),
    'pl' => array( 'menu' => 'Pl' ),
    'by' => array( 'menu' => 'Бр' )
);

$config['cities'] = array(
    'msk' => array(
        'title' => 'Москва',
        'lat' => 55.75,
        'lon' => 37.62,
        'route_css_class' => 'city-1'
    ),
    'spb' => array(
        'title' => 'Санкт-Петербург',
        'lat' => 59.95,
        'lon' => 30.316667,
        'route_css_class' => 'city-2'
    ),
    'waw' => array(
        'title' => 'Варшава',
        'lat' => 52.233333,
        'lon' => 21.016667,
        'route_css_class' => 'city-3'
    ),
    'min' => array(
        'title' => 'Минск',
        'lat' => 53.9013964,
        'lon' => 27.5603287,
        'route_css_class' => 'city-4'
    )
);

/*

$config[M_USER] = array(
	'upload_path' => ROOT . 'up/user/',
	'upload_url' => 'http://'.$config['http_domain'].$config['http_root'].'up/user/',
);

*/
