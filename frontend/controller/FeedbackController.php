<?php

class FeedbackController
{
	function actions()
	{
		$action = request_str('action');
		if(method_exists($this, $action.'Action')) {
			echo call_user_func(array($this, $action.'Action'));
		} else {
			echo $this->defaultAction();
		}
	}

	function defaultAction()
	{
		$subjects = array(
			1 => array('id' => 1, 'title' => s('Общие вопросы')),
			2 => array('id' => 2, 'title' => s('Сообщение об ошибке')),
			3 => array('id' => 3, 'title' => s('Предложение о сотрудничестве')),
			4 => array('id' => 4, 'title' => s('Идея')),
			5 => array('id' => 5, 'title' => s('Другое')),
		);

	    $html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		$jump_to = 'feedback_name';

		if ($is_posted) {
			if (! count($errors) && ! request_str('email')) {
		        $errors []= s('Пожалуйста, укажите адрес электронной почты.');
		        $jump_to = 'feedback_email';
		    }

		    if (! count($errors) && request_str('email') && ! filter_var(request_str('email'), FILTER_VALIDATE_EMAIL)) {
		    	$errors []= s('Пожалуйста, укажите корректный адрес электронной почты. Например: john@gmail.com');
		    	$jump_to = 'feedback_email';
		   	}

		    if (! count($errors) && ! request_str('message')) {
		        $errors []= s('Пожалуйста, укажите текст сообщения.');
		        $jump_to = 'feedback_password';
		    }

			if (! count($errors)) {

				$data = array(
					'{name}' => request_str('name'),
					'{email}' => request_str('email'),
					'{subject}' => $subjects[request_int('subject_id')]['title'],
					'{message}' => request_str('message'),
				);

				$message = str_replace(array_keys($data), array_values($data),
'Имя: {name}
Адрес электронной почты: {email}

Тема: {subject}

{message}


' . $_SERVER['REMOTE_ADDR'] . ' ' . date('r'));

				core::$sql->insert(array(
					'message' => core::$sql->s($message),
					'insert_stamp' => core::$sql->i(time()),
				), DB . 'feedback');

				require_once('../mod/lib.mail.php');

				foreach (array('info@metro4all.ru') as $email) {
					mail_send(request_str('name'), request_str('email'), $email,
						'Metro4all.org - ' . $subjects[request_int('subject_id')]['title'], $message, false);
				}

				go(Core::$config['http_home'] . 'feedback/?action=ok');
			}
		}

		$page = new PageCommon(s('Обратная связь'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-2 col-md-8"><h2>'.s('Обратная связь').'</h2>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>'.escape($errors[0]).'</p></div>';
		}

		$form = new Form('feedback', false, 'post');

		$html .= '<div class="well">'
			. $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addString('name', s('Имя'), $is_posted ? request_str('name') : '')
			. $form->addString('email', s('Адрес электронной почты'), $is_posted ? request_str('email') : '', array('is_required' => true))
			. $form->addSelect('subject_id', s('Тема'), $is_posted ? request_int('subject_id') : 1, array('data' => $subjects))
			. $form->addText('message', s('Сообщение'), $is_posted ? request_str('message') : '', array('is_required' => true, 'style' => 'height:200px'))
			. $form->submit(s('Отправить'))
			. '</div>';

		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function okAction() {
		$html = '';

		$page = new PageCommon(s('Обратная связь'));

		$html .= $page->start();

		$html .= '<div class="row" style="margin-bottom:200px"><div class="col-md-offset-2 col-md-8"><h2>'.s('Спасибо за письмо').'</h2>';

		$html .= '<p>'.s('Мы обязательно ответим в течение дня.').'</p>
			<p><a href="' . Core::$config['http_home'] . '">'.s('Перейти на главную').'</a></p>
			</div></div>';

		$html .= $page->stop();

		return $html;
	}
}
