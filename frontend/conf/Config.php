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

/*

$config[M_USER] = array(
	'upload_path' => ROOT . 'up/user/',
	'upload_url' => 'http://'.$config['http_domain'].$config['http_root'].'up/user/',
);

*/
