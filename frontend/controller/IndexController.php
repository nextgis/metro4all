<?php

class IndexController
{
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
		$page = new PageCommon(s('Metro4all'));

		$page->addResource('style', 'css/metro4all-promo.css');
		$page->addResource('style', 'css/metro4all-index.css');

		$html = '

	<div id="carousel-example-generic" class="carousel slide">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>
			<li data-target="#carousel-example-generic" data-slide-to="1"></li>
			<li data-target="#carousel-example-generic" data-slide-to="2"></li>
			<li data-target="#carousel-example-generic" data-slide-to="3"></li>
		</ol>

		<!-- Wrapper for slides -->
		<div class="carousel-inner">
			<div class="item active">
				<img src="/img/part-1.jpg">
				<div class="carousel-caption">
					' . s('We surveyed all subway stations of Moscow and Saint-Petersburg') . '
				</div>
				<div class="photo-copyright">Фото: Wikipedia <a href="http://commons.wikimedia.org/wiki/File:Metro_SPB_Line1_Lesnaya_pavilion.jpg?uselang=ru">1</a> <a href="http://commons.wikimedia.org/wiki/File:Kunts_12.jpg">2</a></div>
			</div>
			<div class="item">
				<img src="/img/part-4.jpg">
				<div class="carousel-caption">
					 ' . s('...and measured all elements of infrastructure important for movement of people...') . '
				</div>
				<div class="photo-copyright">Фото: Максим Дубинин//NextGIS</div>
			</div>
			<div class="item">
				<img src="/img/part-3.jpg">
				<div class="carousel-caption">
					' . s('...especially for people with special needs ...') . '
				</div>
				<div class="photo-copyright">Фото: Максим Дубинин//NextGIS</div>
			</div>
			<div class="item">
				<img src="/img/part-2.jpg">
				<div class="carousel-caption">
					' . s('...start using it now...') . '
				</div>
			</div>
		</div>

		<!-- Controls -->
		<a class="left carousel-control" href="#carousel-example-generic" data-slide="prev">
			<span class="icon-prev"></span>
		</a>
		<a class="right carousel-control" href="#carousel-example-generic" data-slide="next">
			<span class="icon-next"></span>
		</a>
	</div>
';
        /*
$html .= '
	<div class="row">
		<div class="col-md-8 col-md-offset-2">
			<div class="news">
				<h2><a href="news/" style="color: #D8A300;">' . s('News') . '</a></h2>
				<ul>
';

		foreach (Core::$sql->get('*', DB . 'news order by datetime_stamp desc limit 3') as $row)
		{
			$html .= '<li><div class="item-date">' . time_format_date($row['datetime_stamp']) . '</div>'
				. '<h3><a href="news/#' . $row['id'] . '">' . ($row['title_' . Core::$config['current_language']] ? $row['title_' . Core::$config['current_language']] : $row['title_en']) . '</a></h3>'
				. '<p>' . ($row['description_' . Core::$config['current_language']] ? $row['description_' . Core::$config['current_language']] : $row['title_en']) . '</p>'
				. '</li>';
		}

$html .= '
				</ul>
			</div>
		</div>
	</div>
';
        */

$html .= '
<script type="text/javascript">

	$(document).ready(function () {
		$(".carousel").carousel({
			interval: 5000,
			cycle: true
		})
	});

</script>

	';

		return $page->start() . $html . $page->stop();
	}
}

