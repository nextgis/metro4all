<?php

define('USER_COOKIE_NAME', 'sh_user');
define('USER_COOKIE_DOMAIN', $config['cookie_domain']);
define('USER_COOKIE_PATH', '/');
define('USER_COOKIE_SECURE', 0);
define('USER_COOKIE_EXPIRE', 31536000);

define('USER_TIMEOUT_VISIT', 600);

define('USER_CACHE_TIMEOUT', 3600);

define('U_ANONIMOUS', 1);
define('U_GUEST', 1);

define('U_ROLE_GUEST', 1);
define('U_ROLE_REGISTERED', 2);

$user_category_name = array(
	5 => 'Модератор',
	6 => 'Эксперт',
	4 => 'Гуру',
	3 => 'Мастер',
	2 => 'Продвинутый',
	1 => 'Новичок',
	7 => 'Сотрудник',
);

class users
{
    var $sql;
    var $info = array();
    var $permissions = array();
    var $regions = array();

    function __construct($sql)
    {
    	global $time;
    
        $this->sql = $sql;
        
        $this->define_permissions();
        
		$this->info = array(
			'id' => U_ANONIMOUS,
			'title' => 'Гость',
		);
		
		if(($cookie = $this->get_cookie()) !== false)
		{
			if(isset($_SESSION[BOX]['current_user']['info'])
				&& isset($_SESSION[BOX]['current_user']['info_stamp'])
				&& ($time['current_time'] - $_SESSION[BOX]['current_user']['info_stamp']) < USER_CACHE_TIMEOUT
				&& ($_SESSION[BOX]['current_user']['info']['id'] == $cookie['user_id']))
					$info = $_SESSION[BOX]['current_user']['info'];
			else
				$info = $this->sql->row('*', DB.'users', 'is_disabled=0 and id='.$this->sql->i($cookie['user_id']));
				
			if($info !== false && (($cookie['user_id'] == U_ANONIMOUS)
				|| ($this->get_hash(USER_COOKIE_PASSWORD_SEED, $info['pass']) == $cookie['cookie_password_hash'])))
			{
				$this->info = $info;
				$_SESSION[BOX]['current_user']['info'] = $info;
				$_SESSION[BOX]['current_user']['info_stamp'] = $time['current_time'];
			}
		}
		else
			$this->set_cookie(U_ANONIMOUS, mt_rand());
		
		if(!isset($_SESSION[BOX]['current_user']))
		{
			$_SESSION[BOX]['current_user']['info'] = $this->info;
			$_SESSION[BOX]['current_user']['info_stamp'] = $time['current_time'];
		}
		
		$user_agent = isset($_SERVER['HTTP_USER_AGENT'])?$_SERVER['HTTP_USER_AGENT']:'Unknown';
		$ident = ($this->info['id'] != U_ANONIMOUS)?$this->info['login']:($_SERVER['REMOTE_ADDR'].' '.crc32($user_agent));
		$request_uri = isset($_SERVER['REQUEST_URI'])?'http://'.$_SERVER['SERVER_NAME'].$_SERVER['REQUEST_URI']:'Unknown';

		$this->sql->exec('insert into '.DB.'users_online (ident,user_id,insert_stamp,last_access_stamp,user_agent,last_page_url,hits) '.
			'values ('.$this->sql->s($ident).','.$this->info['id'].','.$time['current_time'].','.$time['current_time'].','.$this->sql->s($user_agent).','.$this->sql->s($request_uri).',1) '.
			'on duplicate key update last_access_stamp=values(last_access_stamp),last_page_url=values(last_page_url),hits=hits+1');
			
		if($this->is_login() && !$this->info['is_online'])
		{
			$this->info['is_online'] = 1;
			$_SESSION[BOX]['current_user']['info'] = $this->info;

		    $this->sql->update(array(
		        'is_online' => 1,
		    ), DB.'users', 'id='.$this->info['id']);
		}
		
		$this->permissions = $this->get_permissions($this->info['id']);
		$this->regions = $this->get_regions($this->info['id']);
	}

    static function update_online()
    {
    	global $time, $sql;
    	
		if (!defined('DB_READONLY')){
	    	$sql->exec('update '.DB.'users u join '.DB.'users_online o on u.id=o.user_id '.
	    		' set u.is_online=0, u.last_visit_stamp=o.last_access_stamp where o.user_id<>'.U_ANONIMOUS.' and o.last_access_stamp<'.($time['current_time']-USER_TIMEOUT_VISIT));
			
			$sql->exec('delete from '.DB.'users_online where last_access_stamp<'.($time['current_time']-USER_TIMEOUT_VISIT));
		}
	}
    
    function define_permissions()
    {
    	global $cache;
    	
		$cache_key = 'permissions.ser';
	
		$permissions_serialized = '';

		if(($permissions_serialized = $cache->get($cache_key)) === false)
		{
			$permissions = $this->sql->get('id, name', DB.'permission');
			$cache->set($cache_key, serialize($permissions), array('table:permission'), 3600*24);
		}
		else
			$permissions = unserialize($permissions_serialized);

		foreach($permissions as $row)
			define('P_'.$row['name'], $row['id']);
    }

	function get_cookie()
	{
		if(isset($_COOKIE[USER_COOKIE_NAME]))
		{
			$temp = explode('_', $_COOKIE[USER_COOKIE_NAME]);

			if(count($temp) == 2) return array(
				'user_id' => (int)$temp[0],
				'cookie_password_hash' => trim($temp[1]),
			);
		}

		return false;
	}

    function set_cookie($user_id, $pass)
    {
    	global $time;
    	
    	$cookie_password_hash = $this->get_hash(USER_COOKIE_PASSWORD_SEED, $pass);
    	
        if(version_compare(PHP_VERSION, '5.2.0', '>='))
            setcookie(USER_COOKIE_NAME, $user_id.'_'.$cookie_password_hash, $time['current_time'] + USER_COOKIE_EXPIRE,
            	USER_COOKIE_PATH, USER_COOKIE_DOMAIN, USER_COOKIE_SECURE, true);
        else
            setcookie(USER_COOKIE_NAME, $user_id.'_'.$cookie_password_hash, $time['current_time'] + USER_COOKIE_EXPIRE,
            	USER_COOKIE_PATH.'; HttpOnly', USER_COOKIE_DOMAIN, USER_COOKIE_SECURE);
    }

    function is_login()
    {
        return $this->info['id'] != U_ANONIMOUS;
    }
    
    function get_hash($seed, $value)
    {
    	return sha1($seed.$value);
    }

    function login($login, $pass, $is_using_hashed_password=false)
    {
        $login = trim($login);
        $pass = trim($pass);

        $info = $this->sql->row('id, pass', DB.'users',
        	'is_disabled=0 and is_activated=1 and login='.$this->sql->s($login));
        	
        if($login == '' || $pass == '')	return false;

    	if(!$is_using_hashed_password)
	        $password_hash = $this->get_hash(USER_PASSWORD_SEED, $pass);
	    else
	    	$password_hash = $pass;
		    
        if($info['pass'] != $password_hash)
        	return false;
        
        if(isset($_SESSION[BOX]['current_user'])) unset($_SESSION[BOX]['current_user']);
		
        $this->sql->delete(DB.'users_online', 'ident='.$this->sql->s($_SERVER['REMOTE_ADDR']));
        
		$this->set_cookie($info['id'], $info['pass']);

        return $info['id'];
    }

    function logout()
    {
    	global $time;
    
    	if($this->info['id'] != U_ANONIMOUS)
    	{
		    $this->sql->delete(DB.'users_online', 'user_id='.$this->info['id']);
        
		    $this->sql->update(array(
		        'last_visit_stamp' => $time['current_time'],
		        'is_online' => 0,
		    ), DB.'users', 'id='.$this->info['id']);
		}
		
	    if(isset($_SESSION[BOX]['current_user'])) unset($_SESSION[BOX]['current_user']);
	    
		$this->set_cookie(U_ANONIMOUS, mt_rand());
		
        return true;
    }

    function clear_user_cache()
    {
	    if(isset($_SESSION[BOX]['current_user']))
	    	unset($_SESSION[BOX]['current_user']);
    }

    function register($login, $title, $pass, $email)
    {    
    	global $time;
    
        $password_hash = $this->get_hash(USER_PASSWORD_SEED, $pass);
        
        $i=0;
        do
        {
		    $title_url = str_format_url($title).($i?$i:'');
		    $i++;
		}
		while($this->sql->value('count(*)', DB.'users', 'lower(title_url)='.$this->sql->s(mb_strtolower($title_url))));

        $this->sql->insert(array(
            'login' => $this->sql->s($login),
            'title' => $this->sql->s($title),
            'title_url' => $this->sql->s($title_url),
            'pass' => $this->sql->s($password_hash),
            'email' => $this->sql->s($email),
            'registered_stamp' => $time['current_time'],
            'last_visit_stamp' => $time['current_time'],
            'is_disabled' => 0,
            'category_id' => 1, // Новичок
        ), DB.'users');
        
        $user_id = $this->sql->get_last_id();

        return $user_id;
    }

    function get_permissions($user_id)
    {
    	global $time;
    	
    	if($user_id == $this->info['id']
    		&& isset($_SESSION[BOX]['current_user']['permissions'])
    		&& isset($_SESSION[BOX]['current_user']['permissions_stamp'])
    		&& ($time['current_time'] - $_SESSION[BOX]['current_user']['permissions_stamp']) < USER_CACHE_TIMEOUT)
		{
			return $_SESSION[BOX]['current_user']['permissions'];
		}
		else
    	{
			$permissions = array();

		    if($user_id == U_ANONIMOUS)
		    {
		        // guest
		        foreach($this->sql->get('permission_id', DB.'role_to_permission', 'role_id='.$this->sql->i(U_ROLE_GUEST)) as $row)
		            $permissions[$row['permission_id']] = $row['permission_id'];
		    }
		    else
		    {
		        // registered
		        foreach($this->sql->get('permission_id', DB.'role_to_permission', 'role_id='.$this->sql->i(U_ROLE_REGISTERED)) as $row)
		            $permissions[$row['permission_id']] = $row['permission_id'];
		    
				// user role permissions
				foreach($this->sql->get('rp.permission_id', DB.'users_to_role ur, '.DB.'role_to_permission rp', 'ur.user_id='.$this->sql->i($user_id).' and ur.role_id=rp.role_id') as $row)
				    $permissions[$row['permission_id']] = $row['permission_id'];

				// user custom permissions
				foreach($this->sql->get('permission_id', DB.'users_to_permission', 'user_id='.$this->sql->i($user_id)) as $row)
				    $permissions[$row['permission_id']] = $row['permission_id'];
		    }
            
		    if($user_id == $this->info['id'])
		    {
	            $_SESSION[BOX]['current_user']['permissions'] = $permissions;
	            $_SESSION[BOX]['current_user']['permissions_stamp'] = $time['current_time'];
		    }

			return $permissions;
		}
    }
    
    function have_region($region_id, $user_id=false)
    {
        if($user_id === false)
        	return isset($this->regions[$region_id]); 
        else
        {
        	$temp = $this->get_regions($user_id);
        	return isset($temp[$region_id]);
        }
    }

    function have_permission($permission_id, $user_id=false)
    {
        if($user_id === false)
        	return isset($this->permissions[$permission_id]); 
        else
        {
        	$temp = $this->get_permissions($user_id);
        	return isset($temp[$permission_id]);
        }
    }

    function have_any_permission($permissions, $user_id=false)
    {
        if($user_id === false)
        	$temp = $this->permissions;
        else
        	$temp = $this->get_permissions($user_id);
        
        $result = false;
        foreach($permissions as $permission_id)
            if(isset($temp[$permission_id]))
            {
                $result = true;
                break;
            }

        return $result;
    }

    function have_all_permissions($permissions, $user_id=false)
    {
        if($user_id === false)
        	$temp = $this->permissions;
        else
        	$temp = $this->get_permissions($user_id);
        
        $result = true;
        foreach($permissions as $permission_id)
            if(!isset($temp[$permission_id]))
            {
                $result = false;
                break;
            }
        
        return $result;
    }

    function have_custom_permission($permission_all, $permission_own, $owner_id)
    {
        return $this->have_permission($permission_all) ||
            ($this->have_permission($permission_own) && ($owner_id == $this->info['id']));
    }

	function get_id_by_title_url($title_url)
	{
		if(isset($this->info['title_url']) && $title_url == $this->info['title_url'])
			return $this->info['id'];
			
		return $this->sql->value('id', DB.'users', 'title_url='.$this->sql->s($title_url));
	}
	
	function get_home_info()
	{
		$title_url = request_str('user_title_url', true);
		if($title_url == '') out();
		
		$id = $this->get_id_by_title_url($title_url);
		if($id == U_GUEST) out();
		
		$info = array();
		
		$is_my = ($id == $this->info['id']);

		if(!$is_my)
		{
			$info = $this->sql->row('*', DB.'users', 'id='.$this->sql->i($id));
			if($info === false) out();
		}
		else
			$info = $this->info;
		
		$info['is_my'] = $is_my;
		$info['home_url'] = '/club/'.$info['title_url'].'/';
		
		return $info;
	}
	
    function get_regions($user_id)
    {
    	global $time;
    	
    	if($user_id == U_ANONIMOUS)
    		return array();
    	else
    	{
			if($user_id == $this->info['id']
				&& isset($_SESSION[BOX]['current_user']['regions'])
				&& isset($_SESSION[BOX]['current_user']['regions_stamp'])
				&& ($time['current_time'] - $_SESSION[BOX]['current_user']['regions_stamp']) < USER_CACHE_TIMEOUT)
			{
				return $_SESSION[BOX]['current_user']['regions'];
			}
			else
			{
				$regions = array();

				foreach($this->sql->get('region_id', DB.'users_to_region', 'user_id='.$this->sql->i($user_id)) as $row)
				    $regions[$row['region_id']] = $row['region_id'];

				if($user_id == $this->info['id'])
				{
				    $_SESSION[BOX]['current_user']['regions'] = $regions;
				    $_SESSION[BOX]['current_user']['regions_stamp'] = $time['current_time'];
				}
		        
		        return $regions;
			}
		}
	}

	function update_category($id)
	{
        $roles = $this->sql->dict('role_id', 'role_id', DB.'users_to_role', 'user_id='.$this->sql->i($id));

        $category_id = 1; // Новичок
/*
        if(isset($roles[6])) $category_id = 2; // Продвинутый
        if(isset($roles[7])) $category_id = 3; // Мастер
        if(isset($roles[8])) $category_id = 4; // Гуру
		if(isset($roles[9])) $category_id = 6; // Эксперт
		if(isset($roles[10])) $category_id = 7; // Сотрудник
		if(isset($roles[5])) $category_id = 5; // Модератор
*/		
        
        $this->sql->update(array(
            'category_id' => $this->sql->i($category_id),
        ), DB.'users', 'id='.$this->sql->i($id));
	}
	
	function log($module_id, $object_id, $event, $comment='')
	{
		global $time;
		
		if(is_array($comment))
			$comment = serialize($comment);
		
		$this->sql->insert(array(
			'user_id' => $this->sql->i($this->info['id']),
			'module_id' => $this->sql->i($module_id),
			'object_id' => $this->sql->i($object_id),
			'event' => $this->sql->s($event),
			'comment' => $this->sql->s($comment),
			'stamp' => $this->sql->i($time['current_time']),
			'ip_address' => @ip2long($_SERVER['REMOTE_ADDR']),
		), DB.'users_log');
	}
	
	function generate_password($length)
	{
		$consonant = 'bcdfghjklmnpqrstvwxz';
		$consonant_length = mb_strlen($consonant);

		$vowel = 'aeiouy';
		$vowel_length = mb_strlen($vowel);
	
		$number = '0123456789';
		$number_length = 10;
	
		$pass = '';
		$is_last_vowel = true;
		for($i=0; $i<$length; $i++)
		{
			if(!$is_last_vowel)
			{
				$pass .= $vowel{mt_rand(0, $vowel_length - 1)};
				$is_last_vowel = true;
			}
			else
			{
				if(mt_rand(0,5) > 1)
				{
					$pass .= $consonant{mt_rand(0, $consonant_length - 1)};
					$is_last_vowel = false;
				}
				else
				{
					$pass .= $number{mt_rand(0, $number_length - 1)};
				}
			}
		}
	
		return $pass;
	}
}

