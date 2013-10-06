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

		$page = new PageCommon($item['title_' . Core::$config['current_language']]);

		$html = '<div class="row"><div class="col-md-offset-1 col-md-7">'
			. '<h1>' . escape($item['title_' . Core::$config['current_language']]) . '</h1>'
			. $item['description_' . Core::$config['current_language']] . '</div></div>';

		return $page->start() . $html . $page->stop();
	}
}

