<?php

class SubscriptionController
{
	function actions() {
		$action = request_str('action');
		if(method_exists($this, $action.'Action')) {
			echo call_user_func(array($this, $action.'Action'));
		} else {
			echo $this->defaultAction();
		}
	}

	function defaultAction()
	{
	    $html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		$jump_to = 'subscription_email';

		if ($is_posted) {
			// $captcha_code = request_str('captcha_code');

			if (! count($errors) && ! request_str('email')) {
		        $errors []= s('Пожалуйста, укажите адрес электронной почты.');
		        $jump_to = 'register_email';
		    }

		    if (! count($errors) && request_str('email') && ! filter_var(request_str('email'), FILTER_VALIDATE_EMAIL)) {
		    	$errors []= s('Пожалуйста, укажите корректный адрес электронной почты. Например: john@gmail.com');
		    	$jump_to = 'register_email';
		   	}

		    // if(captcha_compare(request_str('captcha_code'))) {
			//	captcha_close();

 				if (! count($errors)) {
					// file_put_contents('data/subscription.txt', "\r\n" . request_str('email'), FILE_APPEND | LOCK_EX);

					core::$sql->insert(array(
						'email' => core::$sql->s(request_str('email')),
						'insert_stamp' => core::$sql->i(time()),
					), DB . 'subscription');

					/*
			        switch (request_int('language_id')) {
			        	case 1: mail('minimalist@gisconf.ru', 'subscribe gisconf '.request_str('email'), '*password: Oov4eeph', 'From: news@gisconf.ru'); break;
			        	case 2: mail('minimalist@gisconf.ru', 'subscribe gisconf-en '.request_str('email'), '*password: Oov4eeph', 'From: news-en@gisconf.ru'); break;
			        }
					*/

					go(core::$config['http_home'] . 'subscription/?action=ok');
				}

			// }
			// else
			//	$errors []= 'Неверный код подтверждения';
		}

		$page = new PageCommon(s('Подписка'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-1 col-md-6"><h1>'.s('Подписка на новости').'</h1>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>'.escape($errors[0]).'</p></div>';
		}

		$form = new Form('subscription', false, 'post');

		$html .= '<div class="well">'
			. $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addString('email', s('Адрес электронной почты'), $is_posted ? request_str('email') : '', array('is_required' => true))
			// . $form->add_captcha('Код на картинке', array('style' => 'width:300px;'))
			. $form->submit(s('Подписаться'))
			. '</div>';

		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function okAction() {
		$html = '';

		$page = new PageCommon(s('Подписка на новости'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-1 col-md-6">
			<h3>' . s('Спасибо за подписку!') . '</strong></h3>
			<p>' . s('Ваш адрес электронной почты успешно добавлен в список рассылки.') . '</p>
			<p><a href="../">' . s('Вернуться на главную') . '</a></p>
		</div></div>';

		$html .= $page->stop();

		return $html;
	}
}
