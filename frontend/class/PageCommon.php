<?php

class PageCommon extends Page
{
	function __construct($title = false, $breadcrumbs = false) {
		self::addResource('style', 'http://fonts.googleapis.com/css?family=Andika&subset=cyrillic,latin');
		self::addResource('style', 'css/metro4all.css');
		self::addResource('meta', '<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />');
		self::addResource('script', 'js/util.js');

		parent::__construct($title, $breadcrumbs);
	}

	function formatTitle() {
	    return '';
	}

	function formatHeader() {
	    $html = '';

		/*
	    if (core::$user->isLogin()) {
			$html .= '
			  	<div class="auth-panel">
			    	<a href="'.core::$config['http_home'].'u'.core::$user->info['id'].'/" class="user"><span class="icon icon-user"></span> '.escape(core::$user->info['title']).'</a>
			  		<a href="'.core::$config['http_home'].'logout/">'.s('Выход').'</a>
			    </div>';
	    } else {
			$html .= '
			  	<div class="auth-panel">
			  		<a href="'.core::$config['http_home'].'login/">'.s('Вход').'</a>
			  		<a href="'.core::$config['http_home'].'register/">'.s('Регистрация').'</a>
			  	</div>';
	    }

		 */

		$html .= '

		<nav class="navbar" role="navigation">
		  <div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
			  <span class="sr-only">Toggle navigation</span>
			  <span class="icon-bar"></span>
			  <span class="icon-bar"></span>
			  <span class="icon-bar"></span>
			</button>
		  </div>
		  <div class="collapse navbar-collapse navbar-ex1-collapse">
			<ul class="nav navbar-nav">
			  <li><a href="/' . Core::$config['current_language'] . '/"><span class="glyphicon glyphicon-home"></span></a><//li>
			  <li class="metro-logo metro-logo-msk"><a href="/' . Core::$config['current_language'] . '/msk/">' . s('Москва') . '</a></li>
			  <li class="metro-logo metro-logo-spb"><a href="/' . Core::$config['current_language'] . '/spb/">' . s('Санкт-Петербург') . '</a></li>
			  <li><a href="/' . Core::$config['current_language'] . '/faq/">' . s('Вопросы и ответы') . '</a></li>
			  <li><a href="/' . Core::$config['current_language'] . '/about/">' . s('О проекте') . '</a></li>
			  <li><a href="https://play.google.com/store/apps/details?id=com.nextgis.metroaccess" style="color:red;padding-top:9px;padding-bottom:9px;"><span style="background-image:url(/img/android.png);display:inline-block;height:32px;margin-right:5px;vertical-align:middle;width:32px;"></span></a></li>
			</ul>
			<ul class="nav navbar-nav navbar-social">
				<li><a target="_blank" href="https://twitter.com/metro4all_ru"><img src="/img/icon-social-twitter.png" /></a></li>
				<li><a target="_blank" href="http://vk.com/metro4all"><img src="/img/icon-social-vk.png" /></a></li>
				<li><a target="_blank" href="https://www.facebook.com/pages/Metro4All/730617493632187"><img src="/img/icon-social-fb.png" /></a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li' . (Core::$config['current_language'] == 'ru' ? ' class="active"' : '') . '><a href="' . escape(str_replace(core::$config['http_root'].'en/',  core::$config['http_root'].'ru/', $_SERVER['REQUEST_URI'])) . '">' . s('Ру') . '</a></li>
				<li' . (Core::$config['current_language'] == 'en' ? ' class="active"' : '') . '><a href="' . escape(str_replace(core::$config['http_root'].'ru/',  core::$config['http_root'].'en/', $_SERVER['REQUEST_URI'])) . '">' . s('En') . '</a></li>
			</ul>
		  </div>
		</nav>

		<div class="header">
			<h1 class="col-md-11 title">' . s('Метро для всех') . ' <sup class="title-beta">Beta</sup></h1>
			<div class="col-md-1"><a class="btn btn-danger" style="margin-top:20px" href="http://forum.metro4all.ru/">' . s('Форум') . '</a></div>
		</div>
';

		return $html;
	}

	function formatCounters()
	{
		return file_get_contents('../conf/Counters.html');
	}

	function formatFooter()
	{
		$html = '';

		$share_url = 'http://metro4all.ru/';

		$html .= '<div class=""></div>';

		$html .= '<ul class="social-share">'
			. '<li class="vk" style="width:130px;"><script type="text/javascript" src="http://vk.com/js/api/share.js?86" charset="windows-1251"></script><script type="text/javascript"> document.write(VK.Share.button({url: "' . $share_url . '"},{type: "round", text: "Share"})); </script></li>'
			. '<li class="tw" style="width:85px"><a href="https://twitter.com/share" class="twitter-share-button" data-url="' . $share_url . '">Tweet</a><script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?"http":"https";if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document, "script", "twitter-wjs");</script></li>'
			. '<li class="fb" style="width:130px;"><div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_EN/all.js#xfbml=1";
  fjs.parentNode.insertBefore(js, fjs);
}(document, "script", "facebook-jssdk"));</script>'
			. '<div class="fb-like" data-href="' . $share_url . '" data-width="130" data-layout="button_count" data-show-faces="false"></div></li></ul>';

		return '
		<div class="footer">
			' . $html . '
			<p class="nextgis-link"><a href="http://nextgis.ru/"><img src="/img/nextgis-logo.png" width="150" /></a></p>
			<p class="feedback-email"><a class="btn btn-primary" style="margin-right:10px;" href="/' . Core::$config['current_language'] . '/feedback/">' . s('Обратная связь') . '</a>
				<a class="btn btn-primary" href="/' . Core::$config['current_language'] . '/subscription/">' . s('Подписка на новости') . '</a></p>
		</div>

		';
	}
}

