<?php

require_once(ROOT . 'conf/Config.php');

if (file_exists(ROOT . 'conf/ConfigLocal.php')) {
	require_once(ROOT . 'conf/ConfigLocal.php');
}

require_once(ROOT . 'mod/lib.util.php');
require_once(ROOT . 'mod/lib.string.php');
require_once(ROOT . 'mod/lib.time.php');
time_init();
timer_start();

$action = '';
if(isset($_GET['action'])) $action = $_GET['action'];
if(isset($_POST['action'])) $action = $_POST['action'];

require_once(ROOT . 'mod/lib.upload.php');
require_once(ROOT . 'mod/lib.image.php');
require_once(ROOT . 'mod/lib.mail.php');
require_once(ROOT . 'mod/Page.php');
require_once(ROOT . 'mod/Form.php');
require_once(ROOT . 'mod/lib.image_cache.php');

class Core {
	/**
	 * @var db_mysql
	 */
	static public $sql;

	/**
	 * @var CACHE
	 */
	static public $cache;
	
	/**
	 * @var users
	 */
	static public $user;
	
	static public $config;
	static public $time;
	static public $lang;
}

Core::$time = $time;
Core::$config = &$config;

require_once(ROOT . 'lang/en.php');
require_once(ROOT . 'lang/pl.php');
Core::$lang = &$lang;

function s($str) {
    if (isset(Core::$lang[Core::$config['current_language']][$str])) {
        return Core::$lang[Core::$config['current_language']][$str];
    } else {
        return $str;
    } 
}

require_once(ROOT . 'mod/lib.db.php');
require_once(ROOT . 'mod/lib.db_mysql.php');
$sql = new db_mysql($config['database']);
Core::$sql = $sql;

require_once(ROOT . 'mod/User.php');
$user = new User();
Core::$user = $user;
    
