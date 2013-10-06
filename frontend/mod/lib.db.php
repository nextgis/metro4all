<?php

class db
{
	var $lid;

	function __construct($string) { }
	function get_last_id() { }
	function close() { }
	function query($query) { }
	function exec($query) { }

	function as_int($value) { }
	function as_str($value) { }
	function as_typostr($value) { }
	function as_blob($value) { }
	function as_float($value) { }

	function as_bool($value) { return ($value=='on')?1:0; }

	function i($value) { return $this->as_int($value); }
	function f($value) { return $this->as_float($value); }
	function s($value, $length=false) { return ($length == false)?$this->as_str($value):$this->as_str(substr($value, 0, $length)); }
	function ts($value) { return $this->as_typostr($value); }

	function insert($values, $table)
	{
	    $this->exec(sprintf('insert into %s (%s) values (%s)', $table, join(', ', array_keys($values)), join(', ', array_values($values))));
	}

	function update($values, $table, $conditions)
	{
		$list = array(); foreach($values as $key => $value) $list[] = $key.'='.$value;
	    $this->exec(sprintf('update %s set %s where %s', $table, join(', ', $list), $conditions));
	}

	function value($column, $table, $conditions='')
	{
		$temp = $this->query(sprintf('select %s from %s %s limit 1', $column, $table, ($conditions!=''?'where '.$conditions:'')));
//		return array_pop($temp[0]);
		return (!count($temp))?false:array_pop($temp[0]);
	}

	function get($columns, $table, $conditions='', $order='')
	{
	    return $this->query(sprintf('select %s from %s %s %s', $columns, $table,
	    	($conditions!=''?'where '.$conditions:''), ($order!=''?'order by '.$order:'')));
	}

	function row($columns, $table, $conditions='', $order='')
	{
	    $data = $this->query(sprintf('select %s from %s %s %s limit 1', $columns, $table,
	    	($conditions!=''?'where '.$conditions:''), ($order!=''?'order by '.$order:'')));
	    return (!count($data))?false:$data[0];
	}

	function dict($column, $columns, $table, $conditions='', $order='')
	{
	    $result = array();
	    foreach($this->query(sprintf('select %s from %s %s %s', $columns, $table,
	    	($conditions!=''?'where '.$conditions:''), ($order!=''?'order by '.$order:''))) as $row)
	            $result[$row[$column]] = $row;
	    return $result;
	}

	function delete($table, $conditions)
	{
	    return $this->exec(sprintf('delete from %s %s', $table, ($conditions!=''?'where '.$conditions:'')));
	}

	function show_error($query)
	{
		if(error_reporting() >= E_ERROR)
		{
			echo '<hr><b style="font-size:15px;">MySQL error '.mysql_errno().'</b>: <i style="font-size:15px;">'.mysql_error().'</i><br>';
			echo '<font face="monospace" style="font-size:14px;">'.$query.'</font><hr>';
			echo '<font face="monospace" style="font-size:14px;"><pre>';
			debug_print_backtrace();
			echo '</font></pre>';
			echo '<hr></div>';
		}

		exit();
	}
}

?>
