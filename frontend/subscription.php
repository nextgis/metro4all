<?php

if (isset($_GET['action']) && $_GET['action'] == 'ok') {

?><!DOCTYPE html>
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

	<div class="row">
		<h3>Спасибо за подписку!</h3>
		<p>Ваш адрес электронной почты успешно добавлен в список рассылки новостей проекта.</p>
		<p><a href="/">Вернуться на главную</a></p>
	</div>

</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="vendor/jquery/jquery-1.10.2.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="vendor/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>

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


<?

} else {
	if ($_POST['email'] != '') {
		file_put_contents('data/subscription.txt', "\r\n" . $_POST['email'], FILE_APPEND | LOCK_EX);
		Header('Location: subscription.php?action=ok');
		exit();
	} else {
		Header('Location: /');
		exit();
	}
}

