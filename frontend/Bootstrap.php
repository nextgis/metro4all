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

foreach (Core::$config['languages'] as $url => $language) {
    if ($url !== 'en') {
        require_once(ROOT . 'lang/' . $url . '.php');
    }
}
/*
$temp = array();
foreach ($lang['ru'] as $en => $ru) {
    if (isset($lang['pl'][$ru])) {
        $temp[$en] = $lang['pl'][$ru];
    }
}

$lang['pl'] = $temp;

var_export($lang['pl']);
die();
*/

Core::$lang = $lang;

function s($str) {
    if (isset(Core::$lang[Core::$config['current_language']][$str])) {
        return Core::$lang[Core::$config['current_language']][$str];
    } else {
        return $str;
    } 
}

function selectFields($prefix, $fields) {
    $result = array();
    foreach (Core::$config['languages'] as $url => $language) {
        $result []= $fields[$prefix . '_' . $url];
    }
    return $result;
}

function getNotEmpty($value, $fallback) {
    $result = $value;
    if ($result == '') {
        foreach ($fallback as $text) {
            if ($text != '') {
                $result = $text;
                break;
            }
        }
    }
    return $result;
}

function translateFields($prefixes, &$fields) {
    foreach ($prefixes as $prefix) {
        $fields[$prefix] = getNotEmpty($fields[$prefix . '_' . Core::$config['current_language']],
            selectFields($prefix, $fields));
    }
}

require_once(ROOT . 'mod/lib.db.php');
require_once(ROOT . 'mod/lib.db_mysql.php');
$sql = new db_mysql($config['database']);
Core::$sql = $sql;

require_once(ROOT . 'mod/User.php');
$user = new User();
Core::$user = $user;
    
