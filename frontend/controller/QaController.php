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

		$page = new PageCommon(s('Q&A'));
		
		$html .= $page->start();

		/*
		if (core::$config['current_language'] == 'en') {
			$html .= '<div class="alert alert-info">Please let us know if you have any questions  <a href="mailto:info@gisconf.ru">info@gisconf.ru</a></div>';
		}
		*/

		$html .= '<div class="row"><div class="col-md-offset-1 col-md-7">';
		
		$html .= '<h1>' . s('Q&A') . '</h1>';

		if ($this->is_admin) {
			$html .= '<p><a class="btn btn-primary" href="?action=insert">' . s('Add') . '</a></p>';
		}

        $data = core::$sql->get('*'
                , DB . 'qa'
                . ' order by group_title_ru, title_ru'
        );

		if (count($data)) {
		
			$last_group_title = '';
			
			$i = 0;
			
			$html .= '<ul>';
			
			foreach ($data as $row) {

                translateFields(array('group_title', 'title', 'description'), $row);

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

                translateFields(array('group_title', 'title', 'description'), $row);
				
				if ($last_group_title != $row['group_title']) {
					
					$html .= '<h3><a name="' . crc32($row['group_title']) . '"></a>' . escape($row['group_title']) . '</h3>';
					
					$last_group_title = $row['group_title'];
				}
				
				$html .= '<div id="question-block-' . $row['id'] . '" class="question-block"> <p style="margin-bottom:10px;"><strong><a name="' . $row['id'] . '"></a>' . escape($row['title']) . '</strong>';
	
				if ($this->is_admin) {
					$html .= ' <a class="btn btn-xs" href="?action=update&id=' . $row['id'] . '"><spam>' . s('Edit') . '</a>'
							. ' <a class="btn btn-xs btn-danger btn-confirm" confirm-href="?action=delete&id='.$row['id'].'" confirm-question="' . s('Really delete?') . '"><spam>' . s('Delete') . '</a>';
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
                $fields = array();
                foreach (Core::$config['languages'] as $url => $languages) {
                    $fields['title_' . $url] = core::$sql->s(request_str('title_' . $url));
                    $fields['group_title_' . $url] = core::$sql->s(request_str('group_title_' . $url));
                    $fields['description_' . $url] = core::$sql->s(request_str('description_' . $url));
                }

			    core::$sql->insert($fields, DB.'qa');
			    
				go(core::$config['http_home'].'faq/');
			}
		}
		
		$page = new PageCommon(s('Добавить вопрос'));
		
		$html .= $page->start();
		
		$html .= '
				<p><a href="./">' . s('Q&A') . '</a> &rarr;</p>
				<h2>'.s('Добавить вопрос').'</h2>';
		
		if (count($errors)) {
			$html .= '<div class="alert alert-error"><p>' . escape($errors[0]) . '</p></div>';
		}
		
		$form = new Form('insert_qa', false, 'post');
		
		$html .= '<div class="well">'
		    . $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addVariable('action', 'insert');

        foreach (Core::$config['languages'] as $url => $language) {
            $html .= $form->addString('title_' . $url, s('Вопрос') . ' ' . $language['title'], $is_posted ? request_str('title_' . $url) : '', array('class' => 'span7'));
        }

        foreach (Core::$config['languages'] as $url => $language) {
            $html .= $form->addString('group_title_' . $url, s('Группа') . ' ' . $language['title'], $is_posted ? request_str('group_title_' . $url) : '', array('class' => 'span7'));
        }

        foreach (Core::$config['languages'] as $url => $language) {
            $html .= $form->addString('description_' . $url, s('Ответ') . ' ' . $language['title'], $is_posted ? request_str('description_' . $url) : '', array('class' => 'span7', 'style' => 'height:250px;'));
        }

		$html .= $form->submit(s('Add'))
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
                $fields = array();
                foreach (Core::$config['languages'] as $url => $languages) {
                    $fields['title_' . $url] = core::$sql->s(request_str('title_' . $url));
                    $fields['group_title_' . $url] = core::$sql->s(request_str('group_title_' . $url));
                    $fields['description_' . $url] = core::$sql->s(request_str('description_' . $url));
                }
			    core::$sql->update($fields, DB.'qa', 'id='.core::$sql->i($item['id']));
			    
				go(core::$config['http_home'].'faq/');
			}
		}
		
		$page = new PageCommon(s('Изменить вопрос'));
		
		$html .= $page->start();
		
		$html .= '<p><a href="./">' . s('Q&A') . '</a> &rarr;</p>
				<h2>'.s('Изменить вопрос').'</h2>';
		
		if (count($errors)) {
			$html .= '<div class="alert alert-error"><p>' . escape($errors[0]) . '</p></div>';
		}
		
		$form = new Form('update_qa', false, 'post');
		
		$html .= '<div class="well">'
		    . $form->start()
			. $form->addVariable('is_posted', 1)
			. $form->addVariable('action', 'update');

        foreach (Core::$config['languages'] as $url => $language) {
            $html .= $form->addString('title_' . $url, s('Вопрос') . ' ' . $language['title'], $is_posted ? request_str('title_' . $url) : $item['title_' . $url], array('class' => 'span7'));
        }

        foreach (Core::$config['languages'] as $url => $language) {
            $html .= $form->addString('group_title_' . $url, s('Группа') . ' ' . $language['title'], $is_posted ? request_str('group_title_' . $url) : $item['group_title_' . $url], array('class' => 'span7'));
        }

        foreach (Core::$config['languages'] as $url => $language) {
            $html .= $form->addString('description_' . $url, s('Ответ') . ' ' . $language['title'], $is_posted ? request_str('description_' . $url) : $item['description_' . $url], array('class' => 'span7', 'style' => 'height:250px;'));
        }

        $html .= $form->submit(s('Update'))
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
