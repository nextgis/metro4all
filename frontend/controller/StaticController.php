<?php

class StaticController
{
	var $id;

	function __construct($id)
	{
		$this->id = $id;
	}

	function actions()
	{
		$action = request_str('action');
		if(method_exists($this, $action.'Action')) {
			return call_user_func(array($this, $action.'Action'));
		} else {
			return $this->defaultAction();
		}
	}

	function defaultAction()
	{
		$item = Core::$sql->row('*', DB . 'content', 'id=' . Core::$sql->s($this->id));
        translateFields(array('title', 'description'), $item);

		$page = new PageCommon($item['title']);

		$html = '<div class="row"><div class="col-md-offset-1 col-md-7">'
			. '<h1>' . escape($item['title']) . '</h1>'
			. $item['description'] . '</div></div>';

		return $page->start() . $html . $page->stop();
	}
}

