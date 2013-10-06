<?php

error_reporting(E_ALL);

require_once('../Bootstrap.php');
require_once('../class/PageCommon.php');

$url = $_SERVER['REQUEST_URI'];

if (preg_match('/^\/ajax\/(.*)$/Uu', $url, $matches)) {
	Header('Content-type: application/json');
	echo @file_get_contents('http://demo.nextgis.ru/metro4all/' . $matches[1]);
	die();
}

if (preg_match('/([^\/]+)\?/Uui', $url)) {
	go(preg_replace('/([^\/]+)\?/Uui', '$1/?', $url));
}

if (preg_match('/^([^?]*[^\/?]+)$/Uui', $url)) {
	go(preg_replace('/^([^?]*[^\/?]+)$/Uui', '$1/', $url));
}

if(preg_match('/^' . addcslashes(core::$config['http_root'], '\/') . '(en|ru)\/(.*)$/Uu', $url, $matches)) {
    Core::$config['current_language'] = $matches[1];
    $url = $matches[2];

	/*
	if (Core::$config['current_language'] == 'en') {
		go('/ru/');
	}
	*/

    core::$config['http_home'] = core::$config['http_root'] . core::$config['current_language'] . '/';
    
	if (preg_match('/^login\/(|\?.*)$/Uu', $url, $matches)) {
    	require_once('AuthController.php');
		$controller = new AuthController();
		echo $controller->loginAction();
		die();
	}
	
	if (preg_match('/^logout\/(|\?.*)$/Uu', $url, $matches)) {
    	require_once('AuthController.php');
		$controller = new AuthController();
		echo $controller->logoutAction();
		die();
	}
	
	if (preg_match('/^register\/(|\?.*)$/Uu', $url, $matches)) {
    	require_once('AuthController.php');
		$controller = new AuthController();
		echo $controller->registerAction();
		die();
	}
	
	if (preg_match('/^lost_password\//Uu', $url, $matches)) {
    	require_once('AuthController.php');
    	$controller = new AuthController();
    	
	    if (preg_match('/^lost_password\/(|\?.*)$/Uu', $url, $matches)) {
    		echo $controller->lostPasswordAction();
    		die();
    	}
    	
    	if (preg_match('/^lost_password\/sent\/(|\?.*)$/Uu', $url, $matches)) {
    		echo $controller->lostPasswordSentAction();
    		die();
    	}
		
	    if (preg_match('/^lost_password\/change\/(|\?.*)$/Uu', $url, $matches)) {
    		echo $controller->lostPasswordChangeAction();
    		die();
    	}
    	
		if (preg_match('/^lost_password\/changed\/(|\?.*)$/Uu', $url, $matches)) {
    		echo $controller->lostPasswordChangedAction();
    		die();
    	}
	}

	if (preg_match('/^subscription\/(|\?.*)$/Uu', $url, $matches)) {
    	require_once('SubscriptionController.php');
		$controller = new SubscriptionController();
		echo $controller->actions();
		die();
	}
/*
	if (preg_match('/^u([0-9]+)\/(|\?.*)$/Uu', $url, $matches)) {
    	require_once('HomeController.php');
		$controller = new HomeController();
		$controller->setUser($matches[1]);
		echo $controller->actions();
		die();
	}
	
	if (preg_match('/^presentations_howto\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('IndexController.php');
		$controller = new IndexController();
		echo $controller->reportRulesAction();
		die();
	}
	
	if (preg_match('/^presentations_howto_osm\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('IndexController.php');
		$controller = new IndexController();
		echo $controller->osmReportRulesAction();
		die();
	}
	
	if (preg_match('/^promo\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('IndexController.php');
		$controller = new IndexController();
		echo $controller->promoAction();
		die();
	}
	
	if (preg_match('/^seminars_howto\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('IndexController.php');
		$controller = new IndexController();
		echo $controller->seminarRulesAction();
		die();
	}
	
	if (preg_match('/^participants\/$/Uu', $url, $matches)) {
		require_once('ParticipantsController.php');
		$controller = new ParticipantsController();
		echo $controller->actions();
		die();
	}
	
	if (preg_match('/^participants-seminars\/$/Uu', $url, $matches)) {
		require_once('ParticipantsController.php');
		$controller = new ParticipantsController();
		echo $controller->seminarsAction();
		die();
	}
	
	if (preg_match('/^participants-plain\/$/Uu', $url, $matches)) {
		require_once('ParticipantsController.php');
		$controller = new ParticipantsController();
		echo $controller->plainAction();
		die();
	}
	
	if (preg_match('/^organizers\/$/Uu', $url, $matches)) {
		require_once('OrganizersController.php');
		$controller = new OrganizersController();
		echo $controller->defaultAction();
		die();
	}
	
	*/

	if (preg_match('/^msk\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('SearchController.php');
		$controller = new SearchController();
		$controller->setCity(1);
		echo $controller->actions();
		die();
	}

	if (preg_match('/^spb\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('SearchController.php');
		$controller = new SearchController();
		$controller->setCity(2);
		echo $controller->actions();
		die();
	}

	if (preg_match('/^faq\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('QaController.php');
		$controller = new QaController();
		echo $controller->actions();
		die();
	}

	if (preg_match('/^about\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('StaticController.php');
		$controller = new StaticController('about');
		echo $controller->actions();
		die();
	}

	if (preg_match('/^feedback\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('FeedbackController.php');
		$controller = new FeedbackController();
		echo $controller->actions();
		die();
	}

	if (preg_match('/^(|\?.*)$/Uu', $url, $matches)) {
		require_once('IndexController.php');
		$controller = new IndexController();
		echo $controller->actions();
		die();
	}

	if (preg_match('/^news\/(|\?.*)$/Uu', $url, $matches)) {
		require_once('NewsController.php');
		$controller = new NewsController();
		echo $controller->actions();
		die();
	}
}

go(core::$config['http_root'].'ru/');

