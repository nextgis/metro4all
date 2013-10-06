<?php

class db_mysql extends db
{
	var $debug_log;

	function __construct($string)
	{
		$this->lid = mysql_connect($string['server'], $string['username'], $string['password']);
		if($this->lid === false && (error_reporting() & E_ERROR))
		{
		    echo 'database connection error';
            die();
        }
		mysql_select_db($string['database'], $this->lid);
        if(@isset($string['codepage']))
            $this->exec('set names "'.$string['codepage'].'"'.(@isset($string['collate'])?' collate "'.$string['collate'].'"':''));
	}

	function get_last_id()
	{
		return mysql_insert_id($this->lid);
	}

	function close()
	{
		return mysql_close($this->lid);
	}

	function query($query)
	{
		global $config;

		if(isset($config['debug_sql']) && $config['debug_sql'])
		{
			$t = microtime(true);
			$result = mysql_query($query, $this->lid);
			
			$this->debug_log [] = array(
				'query' => $query,
				'time' => microtime(true) - $t,
				'trace' => debug_backtrace(true),
			);
		}
		else
			$result = mysql_query($query, $this->lid);
	
		if(!$result && (error_reporting() & E_ERROR))
		{
			$this->show_error($query);
			return false;
		}
  
		$data = array();
		while($row = mysql_fetch_assoc($result))
			$data[] = $row;

		mysql_free_result($result);
		return $data;
	}

	function exec($query)
	{
		global $config;
	
		if(isset($config['debug_sql']) && $config['debug_sql'])
		{
			$t = microtime(true);
			$result = mysql_query($query, $this->lid);
			
			$this->debug_log [] = array(
				'query' => $query,
				'time' => microtime(true) - $t,
				'trace' => debug_backtrace(true),
			);
		}
		else
			$result = mysql_query($query, $this->lid);
		
        if(!$result && (error_reporting() & E_ERROR))
		{
			$this->show_error($query);
			return false;
		}
		return true;
	}

	function as_int($value) { return intval($value); }
	function as_str($value) { return "'".mysql_real_escape_string(stripslashes(trim($value)), $this->lid)."'"; }
	function as_typostr($value) { return "'".mysql_real_escape_string(stripslashes(trim($value)), $this->lid)."'"; }
	function as_blob($value) { return "'".mysql_real_escape_string($value, $this->lid)."'"; }
	function as_datetime($value) { return "'".strftime('%Y-%m-%d %H:%M:%S', $value)."'"; } // date time 'Y-m-d G:i:s'
	function as_date($value) { return "'".strftime('%Y-%m-%d', $value)."'"; } // date 'Y-m-d'
	function as_float($value) { return mysql_real_escape_string(sprintf('%F', floatval(str_replace(',', '.', $value)))); }

	function escape_string($value) { return mysql_real_escape_string(stripslashes($value), $this->lid); }
}

