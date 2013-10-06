<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>Метро для всех</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!-- Bootstrap -->
	<link href="vendor/bootstrap-3.0.0/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="css/metro4all-promo.css" rel="stylesheet" media="screen">
	<link href='http://fonts.googleapis.com/css?family=Andika&subset=cyrillic,latin' rel='stylesheet' type='text/css'>

	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
	<script src="vendor/html5shiv/dist/html5shiv.js"></script>
	<script src="vendor/respond/respond.min.js"></script>
	<![endif]-->
</head>
<body>

<div class="container">

	<div class="header">
		<ul class="nav nav-pills pull-right">
			<li class="date">23 сентября, 2013</a></li>
		</ul>
		<h3 class="text-muted title">Метро для всех</h3>
	</div>

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
				<img src="img/part-1.jpg">
				<div class="carousel-caption">
					Две столицы
				</div>
				<div class="photo-copyright">Фото: Wikipedia <a href="http://commons.wikimedia.org/wiki/File:Metro_SPB_Line1_Lesnaya_pavilion.jpg?uselang=ru">1</a> <a href="http://commons.wikimedia.org/wiki/File:Kunts_12.jpg">2</a></div>
			</div>
			<div class="item">
				<img src="img/part-2.jpg">
				<div class="carousel-caption">
					150 эскалаторов
				</div>
				<div class="photo-copyright">Фото: Артём Светлов//NextGIS</div>
			</div>
			<div class="item">
				<img src="img/part-3.jpg">
				<div class="carousel-caption">
					300 рамп, 500 аппарелей
				</div>
				<div class="photo-copyright">Фото: Максим Дубинин//NextGIS</div>
			</div>
			<div class="item">
				<img src="img/part-4.jpg">
				<div class="carousel-caption">
					20 000 ступенек
				</div>
				<div class="photo-copyright">Фото: Максим Дубинин//NextGIS</div>
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

	<div class="row">
		<p class="slogan">Скоро на ваших экранах — <img src="img/firefox-disabled.png" /> <img src="img/android-disabled.png" /></p>
	</div>

	<div class="row subscribe">
		<div class="col-xs-5 col-md-4 col-md-offset-2 subscribe-label">Узнайте больше первыми, подпишитесь на&#160;новости</div>
		<div class="col-md-6 subscribe-form">

			<form class="form-inline" role="form" action="subscription.php" method="post">
				<div class="form-group">
					<label class="sr-only" for="exampleInputEmail2">Электронная почта</label>
					<input name="email" type="email" class="form-control" id="exampleInputEmail2" placeholder="Электронная почта">
				</div>
				<button type="submit" class="btn btn-default">Подписаться</button>
			</form>

		</div>
	</div>

	<div class="footer">
		<p class="nextgis-link"><a href="http://nextgis.ru/"><img src="img/nextgis-logo.png" width="240" height="63" /></a></p>
		<p style="text-align: center"><a href="mailto:info@nextgis.ru">info@nextgis.ru</a></p>
	</div>

</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="vendor/jquery/jquery-1.10.2.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="vendor/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>

<script type='text/javascript'>

	$(document).ready(function () {
		$('.carousel').carousel({
			interval: 5000,
			cycle: true
		})
	});

</script>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-43922103-1', 'metro4all.ru');
  ga('send', 'pageview');

</script>

<!-- Yandex.Metrika counter -->
<script type="text/javascript">
(function (d, w, c) {
    (w[c] = w[c] || []).push(function() {
        try {
            w.yaCounter22271707 = new Ya.Metrika({id:22271707,
                    clickmap:true,
                    trackLinks:true,
                    accurateTrackBounce:true});
        } catch(e) { }
    });

    var n = d.getElementsByTagName("script")[0],
        s = d.createElement("script"),
        f = function () { n.parentNode.insertBefore(s, n); };
    s.type = "text/javascript";
    s.async = true;
    s.src = (d.location.protocol == "https:" ? "https:" : "http:") + "//mc.yandex.ru/metrika/watch.js";

    if (w.opera == "[object Opera]") {
        d.addEventListener("DOMContentLoaded", f, false);
    } else { f(); }
})(document, window, "yandex_metrika_callbacks");
</script>
<noscript><div><img src="//mc.yandex.ru/watch/22271707" style="position:absolute; left:-9999px;" alt="" /></div></noscript>
<!-- /Yandex.Metrika counter -->

</body>
</html>
