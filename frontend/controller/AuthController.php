<?php

class AuthController
{
	function loginAction()
	{
		$html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		if ($is_posted) {
			// $captcha_code = request_str('captcha_code');

			$email = request_str('email');
			$password = request_str('password');

			// if(captcha_compare(request_str('captcha_code'))) {
			//	captcha_close();

				if (($id = core::$user->login($email, $password)) === false) {
					$errors []= s('Неверная пара email/пароль. Попробуйте еще раз.');
				}

				if (! count($errors)) {
					go(core::$config['http_home'].'u'.$id.'/');
				}
			// }
			// else
			//	$errors []= 'Неверный код подтверждения';
		}

		$page = new PageCommon(s('Вход'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-4 col-md-4"><h2>'.s('Вход').'</h2>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>'.escape($errors[0]).'</p></div>';
		}

		$form = new Form('login', false, 'post');

		$html .= '<div class="well">'
		    . $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addString('email', s('E-mail'), $is_posted ? request_str('email') : '', array('class' => 'span3'))
			. $form->addPassword('password', s('Пароль'), '', array('class' => 'span3'))
			// . $form->add_captcha('Код на картинке', array('style' => 'width:300px;'))
			. $form->submit(s('Войти'))
			. '</div>';

		$html .= '<ul>
				<li><a href="'.core::$config['http_home'].'register/">' . s('Регистрация') . '</a></li>
				<li><a href="'.core::$config['http_home'].'lost_password/">' . s('Забыли пароль?') . '</a></li>
			</ul>';

		$html .= '<script> $(document).ready(function() { $("#login_email").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function logoutAction()
	{
	    core::$user->logout();
	    go(core::$config['http_home']);
	}

	function registerAction()
	{
	    $html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		$jump_to = 'register_title';

		if ($is_posted) {
			// $captcha_code = request_str('captcha_code');

		    if (! count($errors) && ! request_int('language_id')) {
		        $errors []= s('Пожалуйста, выберите язык.');
		        $jump_to = 'register_language_id';
		    }

			if (! count($errors) && ! request_str('email')) {
		        $errors []= s('Пожалуйста, укажите e-mail.');
		        $jump_to = 'register_email';
		    }

		    if (! count($errors) && request_str('email') && ! filter_var(request_str('email'), FILTER_VALIDATE_EMAIL)) {
		    	$errors []= s('Пожалуйста, укажите корректный e-mail. Например: john@gmail.com');
		    	$jump_to = 'register_email';
		   	}

			if (! count($errors) && core::$sql->value('count(*)', DB.'user', 'lower(email)=lower('.core::$sql->s(request_str('email')).')')) {
		        $errors []= s('Пользователь с таким e-mail уже зарегистрирован. Пожалуйста, укажите другой.');
		        $jump_to = 'register_email';
			}

		    if (! count($errors) && ! request_str('password')) {
		        $errors []= s('Пожалуйста, укажите пароль.');
		        $jump_to = 'register_password';
		    }

			if (! count($errors) && request_str('password') != request_str('password2')) {
		        $errors []= s('Введенные пароли не совпадают. Пожалуйста, попробуйте еще раз.');
		        $jump_to = 'register_password';
		    }

		    // if(captcha_compare(request_str('captcha_code'))) {
			//	captcha_close();

 				if (! count($errors)) {

 				    $id = core::$user->register(request_str('email'), request_str('password'));

 				    core::$sql->update(array(
 				    	'language_id' => core::$sql->i(request_int('language_id')),
 				    ), DB.'user', 'id='.core::$sql->i($id));

					/*
			        switch (request_int('language_id')) {
			        	case 1: mail('minimalist@gisconf.ru', 'subscribe gisconf '.request_str('email'), '*password: Oov4eeph', 'From: news@gisconf.ru'); break;
			        	case 2: mail('minimalist@gisconf.ru', 'subscribe gisconf-en '.request_str('email'), '*password: Oov4eeph', 'From: news-en@gisconf.ru'); break;
			        }
					*/

					go(core::$config['http_home'] . 'u' . $id . '/');
				}

			// }
			// else
			//	$errors []= 'Неверный код подтверждения';
		}

		$page = new PageCommon(s('Регистрация'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-4 col-md-4"><h2>'.s('Регистрация').'</h2>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>'.escape($errors[0]).'</p></div>';
		}

		$form = new Form('register', false, 'post');

		$html .= '<div class="well">'
			. $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addString('email', s('E-mail (будет использоваться для входа)'), $is_posted ? request_str('email') : '', array('is_required' => true))
			. $form->addPassword('password', s('Пароль'), '', array('is_required' => true))
			. $form->addPassword('password2', s('Подтверждение пароля'), '', array('is_required' => true))
			. $form->addSelect('language_id', s('Язык'), $is_posted ? request_str('language_id') : (core::$config['current_language'] == 'ru' ? 1 : (core::$config['current_language'] == 'en' ? 2 : 0)), array('is_required' => true, 'data' => array(
			    	array('id' => 0, 'title' => '—'),
			    	array('id' => 1, 'title' => s('Русский')),
			    	array('id' => 2, 'title' => s('English')),
			    	array('id' => 3, 'title' => s('Polski')),
			    )))
			// . $form->add_captcha('Код на картинке', array('style' => 'width:300px;'))
//			. '<p>' . s('Вы будете подписаны на рассылку новости конференции о чем вам придет уведомление.') . '</p>'
			. $form->submit(s('Зарегистрироваться'))
			. '</div>';

		$html .= '<ul>
				<li><a href="'.core::$config['http_home'].'login/">'.s('Вход для зарегистрированных').'</a></li>
				<li><a href="'.core::$config['http_home'].'lost_password/">' . s('Забыли пароль?') . '</a></li>
			</ul>';

		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function lostPasswordAction()
	{
		$html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		$jump_to = 'lost_password_email';

		if ($is_posted) {
			// $captcha_code = request_str('captcha_code');

			if (! count($errors) && ! request_str('email')) {
				$errors []= s('Пожалуйста, укажите e-mail.');
			}

			$email = request_str('email');

			if (! count($errors)) {
				if (($user = core::$sql->row('id, password', DB.'user',
							'email=' . core::$sql->s($email) . ' and id<>' . core::$sql->i(User::ANONIMOUS) . ' and is_disabled=0')) === false) {
					$errors[] = s('Пользователь с таким e-mail адресом не зарегистрирован.');
				}
			}

			// if(captcha_compare(request_str('captcha_code'))) {
			//	captcha_close();

			if (! count($errors)) {

				$hash = core::$user->getHash(core::$config['user']['lost_password_salt'], $user['password']);

				$template_vars = array(
						'{site_url}' => core::$config['site']['url'],
						'{site_title}' => core::$config['site']['title'],
						'{site_email}' => core::$config['site']['email'],
						'{change_password_url}' => 'http://' . core::$config['http_domain'] . '/' . core::$config['current_language'] . '/lost_password/change/?id=' . $user['id'] . '&code=' . $hash,
					);

				$message = str_replace(array_keys($template_vars), array_values($template_vars),
						s('Здравствуйте!

Ваш адрес был указан при запросе смены пароля на сайте {site_url}

Если вы не делали такой запрос проигнорируйте это письмо.

Для смены пароля перейдите по ссылке:
{change_password_url}

C уважением,
Администрация сайта
{site_title}
{site_email}
'));

				mail_send(core::$config['site']['email_title'], core::$config['site']['email'], $email,
						s('Смена пароля'), $message);

				go(core::$config['http_home'] . 'lost_password/sent/');
			}

			// }
			// else
			//	$errors []= 'Неверный код подтверждения';
		}

		$page = new PageCommon(s('Забыли пароль?'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-4 col-md-4"><h2>'.s('Забыли пароль?').'</h2>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>'.escape($errors[0]).'</p></div>';
		}

		$form = new Form('lost_password', false, 'post');

		$html .= '<div class="well">'
			. $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addString('email', s('E-mail'), $is_posted ? request_str('email') : '', array('is_required' => true))
			// . $form->add_captcha('Код на картинке', array('style' => 'width:300px;'))
			. $form->submit(s('Восстановить пароль'))
			. '</div>';

		/*
		$html .= '<ul>
				<li><a href="'.core::$config['http_home'].'login/">'.s('Вход для зарегистрированных').'</a></li>
			</ul>';
		*/

		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function lostPasswordSentAction()
	{
		$html = '';

		$page = new PageCommon(s('Забыли пароль?'));

		$html .= $page->start();

		$html .= '<div class="offset2 span6 text">
			<h3>' . s('Забыли пароль?') . '</strong></h3>
			<p>' . s('На указанный вами e-mail адрес отправлено письмо с инструкциями по смене пароля.') . '</p>
			<p><a href="' . core::$config['http_home'] . '">' . s('Вернуться на главную') . '</a></p>
		</div>';

		$html .= $page->stop();

		return $html;
	}

	function lostPasswordChangeAction()
	{
		if (($user = core::$sql->row('id, password', DB.'user',
				'id=' . core::$sql->i(request_int('id', true)) . ' and id<>' . core::$sql->i(User::ANONIMOUS) . ' and is_disabled=0')) === false) {
			out();
		}

		if (($hash = core::$user->getHash(core::$config['user']['lost_password_salt'], $user['password'])) != request_str('code', true)) {
			out();
		}

		$html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		$jump_to = 'lost_password_change_password';

		if ($is_posted) {
			// $captcha_code = request_str('captcha_code');

			if (! count($errors) && ! request_str('password')) {
				$errors []= s('Пожалуйста, укажите пароль.');
			}

			if (! count($errors) && request_str('password') != request_str('password2')) {
				$errors []= s('Введенные пароли не совпадают. Пожалуйста, попробуйте еще раз.');
		    }

			// if(captcha_compare(request_str('captcha_code'))) {
			//	captcha_close();

			if (! count($errors)) {

				$password_hash = core::$user->getHash(core::$config['user']['password_salt'], request_str('password'));

				core::$sql->update(array(
						'password' => core::$sql->s($password_hash),
					), DB.'user', 'id=' . core::$sql->i($user['id']));

				core::$user->logout();

				go(core::$config['http_home'] . 'lost_password/changed/');
			}

			// }
			// else
			//	$errors []= 'Неверный код подтверждения';
		}

		$page = new PageCommon(s('Смена пароля'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="span4 offset4"><h2>' . s('Смена пароля') . '</h2>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>' . escape($errors[0]) . '</p></div>';
		}

		$form = new Form('lost_password_change', false, 'post');

		$html .= '<div class="well">'
			. $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addVariable('id', request_int('id'))
			. $form->addVariable('code', request_str('code'))
			. $form->addPassword('password', s('Пароль'), '', array('is_required' => true))
			. $form->addPassword('password2', s('Подтверждение пароля'), '', array('is_required' => true))
			// . $form->add_captcha('Код на картинке', array('style' => 'width:300px;'))
			. $form->submit(s('Сохранить'))
			. '</div>';

		/*
		$html .= '<ul>
				<li><a href="'  .core::$config['http_home'] . 'login/">' . s('Вход для зарегистрированных') . '</a></li>
			</ul>';
		*/

		$html .= '<script> $(document).ready(function() { $("#' . $jump_to . '").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function lostPasswordChangedAction()
	{
		$html = '';

		$page = new PageCommon(s('Смена пароля'));

		$html .= $page->start();

		$html .= '<div class="offset2 span6 text">
			<h3>' . s('Смена пароля') . '</strong></h3>
			<p>' . s('Пароль успешно изменен.') . '</p>
			<ul>
				<li><a href="' . core::$config['http_home'] . 'login/">' . s('Войти') . '</a></li>
				<li><a href="' . core::$config['http_home'] . '">' . s('Вернуться на главную') . '</a></li>
			</ul>
		</div>';

		$html .= $page->stop();

		return $html;
	}
}
