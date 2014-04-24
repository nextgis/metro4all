<?php

class QaController
{
    private $user;
    private $is_admin;
    
	function actions() {
		
		$this->is_admin = core::$user->info['is_admin'];
		
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

		$page = new PageCommon(s('Вопросы и ответы'));
		
		$html .= $page->start();

		/*
		if (core::$config['current_language'] == 'en') {
			$html .= '<div class="alert alert-info">Please let us know if you have any questions  <a href="mailto:info@gisconf.ru">info@gisconf.ru</a></div>';
		}
		*/

		$html .= '<div class="row"><div class="col-md-offset-1 col-md-7">';
		
		$html .= '<h1>' . s('Вопросы и ответы') . '</h1>';

		if ($this->is_admin) {
			$html .= '<p><a class="btn btn-primary" href="?action=insert">' . s('Добавить') . '</a></p>';
		}
		
		switch (core::$config['current_language']) {
			case 'ru':
				$data = core::$sql->get('id, title, group_title, description'
						, DB . 'qa'
						, 'title <> "" and group_title <> "" and description <> ""'
						. ' order by group_title, title'
				);
				break;

			case 'en':
				$data = core::$sql->get('id, title_en as title, group_title_en as group_title, description_en as description'
						, DB . 'qa'
						, 'title_en <> "" and group_title_en <> "" and description_en <> ""'
						. ' order by group_title_en, title_en'
				);
				break;

			case 'pl':
				$data = core::$sql->get('id, title_en as title, group_title_en as group_title, description_en as description'
						, DB . 'qa'
						, 'title_pl <> "" and group_title_pl <> "" and description_pl <> ""'
						. ' order by group_title_pl, title_pl'
				);
				break;
		}
		
		if (count($data)) {
		
			$last_group_title = '';
			
			$i = 0;
			
			$html .= '<ul>';
			
			foreach ($data as $row) {
					
				if ($last_group_title != $row['group_title']) {
					
					if ($i > 0) {
						$html .= '</ul></li>';
					}
			
					$html .= '<li><a href="#' . crc32($row['group_title']) . '">' . escape($row['group_title']) . '</a><ul>';
			
					$last_group_title = $row['group_title'];
				}
					
				$html .= '<li><a class="question-link" data-id="' . $row['id'] . '" href="#' . $row['id'] . '">' . escape($row['title']) . '</a></li>';
				
				$i ++;
			}
			
			$html .= '</ul></li></ul>';
			
			$last_group_title = '';
			
			foreach ($data as $row) {
				
				if ($last_group_title != $row['group_title']) {
					
					$html .= '<h3><a name="' . crc32($row['group_title']) . '"></a>' . escape($row['group_title']) . '</h3>';
					
					$last_group_title = $row['group_title'];
				}
				
				$html .= '<div id="question-block-' . $row['id'] . '" class="question-block"> <p style="margin-bottom:10px;"><strong><a name="' . $row['id'] . '"></a>' . escape($row['title']) . '</strong>';
	
				if ($this->is_admin) {
					$html .= ' <a class="btn btn-xs" href="?action=update&id=' . $row['id'] . '"><spam>' . s('Изменить') . '</a>'
							. ' <a class="btn btn-xs btn-danger btn-confirm" confirm-href="?action=delete&id='.$row['id'].'" confirm-question="Действительно удалить?"><spam>' . s('Удалить') . '</a>';
				}
							
				$html .= '</p>
						<div class="qa-question-body" style="margin-left:10px;">
							<p>' . str_replace("\r\n", '<br>',  str_replace("\r\n\r\n", '</p><p>', $row['description'])) . '</p>
						</div>
					</div>';
			}
		}
		
		$html .= '<script>
			$(document).ready(function () {
				$(".question-link").click(function () {
					$(".question-block").removeClass("hilite");
					$("#question-block-" + $(this).data("id")).addClass("hilite");
				});
				
				if (window.location.hash != "") {
					$(".question-block").removeClass("hilite");
					$("#question-block-" + parseInt(window.location.hash.substr(1))).addClass("hilite");
				}
			});
		</script>';
		
		$html .= '</div></div>';

		$html .= $page->stop();
		
		return $html;
	}
	
	function insertAction()
	{
		if (! $this->is_admin) {
			go(core::$config['http_home'] . 'faq/');
		}
		
		$html = '';
		$errors = array();
		
		$is_posted = request_int('is_posted');
		$jump_to = 'insert_qa_title';
		
		if ($is_posted) {
			if (! count($errors) && ! request_str('title')) {
		        $errors []= s('Пожалуйста, укажите вопрос.');
		        $jump_to = 'insert_qa_title';
		    }

			if (! count($errors) && ! request_str('group_title')) {
		        $errors []= s('Пожалуйста, укажите группу.');
		        $jump_to = 'insert_qa_group_title';
		    }
		    
		    if (! count($errors)) {
			    core::$sql->insert(array(
			    	'title' => core::$sql->s(request_str('title')),
			    	'title_en' => core::$sql->s(request_str('title_en')),
			    	'group_title' => core::$sql->s(request_str('group_title')),
			    	'group_title_en' => core::$sql->s(request_str('group_title_en')),
			    	'description' => core::$sql->s(request_str('description')),
			    	'description_en' => core::$sql->s(request_str('description_en')),
			    ), DB.'qa');
			    
				go(core::$config['http_home'].'faq/');
			}
		}
		
		$page = new PageCommon(s('Добавить вопрос'));
		
		$html .= $page->start();
		
		$html .= '
				<p><a href="./">' . s('Вопросы и ответы') . '</a> &rarr;</p>
				<h2>'.s('Добавить вопрос').'</h2>';
		
		if (count($errors)) {
			$html .= '<div class="alert alert-error"><p>' . escape($errors[0]) . '</p></div>';
		}
		
		$form = new Form('insert_qa', false, 'post');
		
		$html .= '<div class="well">'
		    . $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addVariable('action', 'insert')
			. $form->addString('title', s('Вопрос') . ' Rus', $is_posted ? request_str('title') : '', array('class' => 'span7'))
			. $form->addString('title_en', s('Вопрос') . ' Eng', $is_posted ? request_str('title_en') : '', array('class' => 'span7'))
			. $form->addString('group_title', s('Группа') . ' Rus', $is_posted ? request_str('group_title') : '', array('class' => 'span7'))
			. $form->addString('group_title_en', s('Группа') . ' Eng', $is_posted ? request_str('group_title_en') : '', array('class' => 'span7'))
			. $form->addText('description', s('Ответ') . ' Rus', $is_posted ? request_str('description') : '', array('class' => 'span7', 'style' => 'height:250px;'))
			. $form->addText('description_en', s('Ответ') . ' Eng', $is_posted ? request_str('description_en') : '', array('class' => 'span7', 'style' => 'height:250px;'))
			. $form->submit(s('Добавить'))
			. '</div>';
		
		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';
		
		$html .= $page->stop();
		
		return $html;
	}
	
	function updateAction()
	{
		if (! $this->is_admin) {
			go(core::$config['http_home'] . 'faq/');
		}
		
		if (($item = $this->getQa(request_int('id'))) === false) {
	        go(core::$config['http_home']);
	    }
	    
		$html = '';
		$errors = array();
		
		$is_posted = request_int('is_posted');
		$jump_to = 'update_qa_title';
		
		if ($is_posted) {
			if (! count($errors) && ! request_str('title')) {
		        $errors []= s('Пожалуйста, укажите вопрос.');
		        $jump_to = 'update_qa_title';
		    }

			if (! count($errors) && ! request_str('group_title')) {
		        $errors []= s('Пожалуйста, укажите группу.');
		        $jump_to = 'insert_qa_group_title';
		    }
		    		    
		    if (! count($errors)) {
			    core::$sql->update(array(
				    'title' => core::$sql->s(request_str('title')),
				    'title_en' => core::$sql->s(request_str('title_en')),
				    'group_title' => core::$sql->s(request_str('group_title')),
				    'group_title_en' => core::$sql->s(request_str('group_title_en')),
				    'description' => core::$sql->s(request_str('description')),
				    'description_en' => core::$sql->s(request_str('description_en')),
			    ), DB.'qa', 'id='.core::$sql->i($item['id']));
			    
				go(core::$config['http_home'].'faq/');
			}
		}
		
		$page = new PageCommon(s('Изменить вопрос'));
		
		$html .= $page->start();
		
		$html .= '<p><a href="./">' . s('Вопросы и ответы') . '</a> &rarr;</p>
				<h2>'.s('Изменить вопрос').'</h2>';
		
		if (count($errors)) {
			$html .= '<div class="alert alert-error"><p>' . escape($errors[0]) . '</p></div>';
		}
		
		$form = new Form('update_qa', false, 'post');
		
		$html .= '<div class="well">'
		    . $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addVariable('action', 'update')
			. $form->addString('title', s('Вопрос') . ' Rus', $is_posted ? request_str('title') : $item['title'], array('class' => 'span7'))
			. $form->addString('title_en', s('Вопрос') . ' Eng', $is_posted ? request_str('title_en') : $item['title_en'], array('class' => 'span7'))
			. $form->addString('group_title', s('Группа') . ' Rus', $is_posted ? request_str('group_title') : $item['group_title'], array('class' => 'span7'))
			. $form->addString('group_title_en', s('Группа') . ' Eng', $is_posted ? request_str('group_title_en') : $item['group_title_en'], array('class' => 'span7'))
			. $form->addText('description', s('Ответ') . ' Rus', $is_posted ? request_str('description') : $item['description'], array('class' => 'span7', 'style' => 'height:250px;'))
			. $form->addText('description_en', s('Ответ') . ' Eng', $is_posted ? request_str('description_en') : $item['description_en'], array('class' => 'span7', 'style' => 'height:250px;'))
			. $form->submit(s('Сохранить'))
			. '</div>';
		
		$html .= '<script> $(document).ready(function() { $("#'.$jump_to.'").focus(); }); </script>';
		
		$html .= $page->stop();
		
		return $html;
	}
	
	function deleteAction()
	{
		if (! $this->is_admin) {
			go(core::$config['http_home'] . 'faq/');
		}
		
		if (($item = $this->getQa(request_int('id'))) === false) {
			go(core::$config['http_home']);
		}
			
		core::$sql->delete(DB . 'qa', 'id=' . core::$sql->i($item['id']));
		
		go(core::$config['http_home'] . 'faq/');
	}
	
	function getQa($id) {
	    return core::$sql->row('*', DB.'qa', 'id='.core::$sql->i($id));
	}
}
