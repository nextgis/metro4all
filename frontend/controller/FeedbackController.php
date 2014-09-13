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
			1 => array('id' => 1, 'title' => s('General question')),
			2 => array('id' => 2, 'title' => s('Bug report')),
			3 => array('id' => 3, 'title' => s('Collaboration or partership')),
			4 => array('id' => 4, 'title' => s('Idea')),
			5 => array('id' => 5, 'title' => s('Other')),
		);

	    $html = '';
		$errors = array();

		$is_posted = request_int('is_posted');

		$jump_to = 'feedback_name';

		if ($is_posted) {
			if (! count($errors) && ! request_str('email')) {
		        $errors []= s('Please, enter your email');
		        $jump_to = 'feedback_email';
		    }

		    if (! count($errors) && request_str('email') && ! filter_var(request_str('email'), FILTER_VALIDATE_EMAIL)) {
		    	$errors []= s('Please, provide correct email address. For example: john@gmail.com');
		    	$jump_to = 'feedback_email';
		   	}

		    if (! count($errors) && ! request_str('message')) {
		        $errors []= s('Enter the message.');
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
'Name: {name}
Email: {email}

Subject: {subject}

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

		$page = new PageCommon(s('Feedback'));

		$html .= $page->start();

		$html .= '<div class="row"><div class="col-md-offset-2 col-md-8"><h2>'.s('Feedback').'</h2>';

		if (count($errors)) {
			$html .= '<div class="alert alert-danger"><p>'.escape($errors[0]).'</p></div>';
		}

		$form = new Form('feedback', false, 'post');

		$html .= '<div class="well">'
			. $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addString('name', s('Name'), $is_posted ? request_str('name') : '')
			. $form->addString('email', s('E-mail'), $is_posted ? request_str('email') : '', array('is_required' => true))
			. $form->addSelect('subject_id', s('Subject'), $is_posted ? request_int('subject_id') : 1, array('data' => $subjects))
			. $form->addText('message', s('Message'), $is_posted ? request_str('message') : '', array('is_required' => true, 'style' => 'height:200px'))
			. $form->submit(s('Send'))
			. '</div>';

		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';

		$html .= '</div></div>';

		$html .= $page->stop();

		return $html;
	}

	function okAction() {
		$html = '';

		$page = new PageCommon(s('Feedback'));

		$html .= $page->start();

		$html .= '<div class="row" style="margin-bottom:200px"><div class="col-md-offset-2 col-md-8"><h2>'.s('Thanks for subscribing!').'</h2>';

		$html .= '<p>'.s('We will answer shortly.').'</p>
			<p><a href="' . Core::$config['http_home'] . '">'.s('Home').'</a></p>
			</div></div>';

		$html .= $page->stop();

		return $html;
	}
}
