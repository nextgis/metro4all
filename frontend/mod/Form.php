<?php

class Form
{
    public $html_id, $url, $action, $id, $method, $class;

	function __construct($html_id=false, $url=false, $action=false, $id=false, $method='post', $class='')
	{
        $this->html_id = $html_id;
        $this->url = $url;
        $this->action = $action;
        $this->id = $id;
        $this->method = $method;
        $this->class = $class;
	}
	
	function start()
	{
		$html = '<form role="form" id="'.$this->html_id.'" class="'.$this->class.'" enctype="multipart/form-data" '.
			(($this->url === false)?'':('action="'.$this->url.'"')).' method="'.$this->method.'">';
			
		if($this->action !== false) $html .= '<input type="hidden" name="action" value="'.$this->action.'"/>';
		if($this->id !== false) $html .= '<input type="hidden" name="id" value="'.$this->id.'"/>';

		$html .= '<fieldset>';
		
		return $html;
	}

	function addVariable($var, $value)
	{
		$id = $this->html_id.'_'.$var;

		return '<input type="hidden" id="'.$id.'" name="'.$var.'" value="'.$value.'"/>';
	}

	function addString($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false,
				'maxlength' => 255, 'is_required' => false);
		$options = array_merge($default_options, $options);

		$id = $this->html_id.'_'.$var;

		$html = '<div class="form-group">';
		if($label)
			$html .= '<label for="'.$id.'">'.$label.($options['is_required'] ? '<span class="required">*</span>' : '').'</label>';
		$html .= '<input class="form-control" id="'.$id.'" name="'.$var.'" type="text" value="'.escape($value).'" '.($options['style']?' style="'.$options['style'].'"':'').' '.($options['class']?' class="'.$options['class'].'"':'').' '.($options['maxlength']?' maxlength="'.$options['maxlength'].'"':'').'/>';
		if($options['help'])
			$html .= '<p class="help-block">'.$options['help'].'</p>';
		$html .= '</div>';

        return $html;
	}
	
	function addLocation($var, $label, $value, $options = array()) {
		$default_options = array('style' => 'width:700px;height:400px;', 'help' => false, 'class' => false);
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label) $html .= '<label class="control-label" for="'.$id.'">'.$label.'</label>';
		$html .= '<div class="controls">';
		$html .= '<script> if(typeof form_location_default == "undefined") var form_location_default = {}; form_location_default[\''.$id.'\'] = {lat:'.str_replace(',', '.', floatval($value['default_lat'])).',lng:'.str_replace(',', '.', floatval($value['default_lng'])).',zoom:'.(int)$value['default_zoom'].'}; </script>';
		$html .= '<input class="string" id="'.$id.'_lat" type="text" name="'.$var.'_lat" value="'.str_replace(',', '.', floatval($value['lat'])).'"/> ';
		$html .= '<input class="string" id="'.$id.'_lng" type="text" name="'.$var.'_lng" value="'.str_replace(',', '.', floatval($value['lng'])).'"/> ';
		$html .= '<input id="'.$id.'_zoom" type="hidden" name="'.$var.'_zoom" value="'.(int)$value['zoom'].'"/> ';
		$html .= '<a class="btn btn-tiny btn-primary" href="javascript:form_location_show(\''.$id.'\')">Показать на карте</a> ';
		$html .= '<a class="btn btn-tiny btn-danger" href="javascript:form_location_clean(\''.$id.'\')">Очистить</a><br/>';
		$html .= '<div class="map" id="'.$id.'_map" '.($options['style']?' style="'.$options['style'].'"':'').'></div>';
		$html .= '</div></div>';
		
		return $html;
	}
	
	function addPassword($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false,
				'maxlength' => 255, 'is_required' => false);
		$options = array_merge($default_options, $options);
	
		$id = $this->html_id.'_'.$var;
	
		$html = '<div class="form-group">';
		if($label)
			$html .= '<label for="'.$id.'">'.$label.($options['is_required'] ? '<span class="required">*</span>' : '').'</label>';
		$html .= '<input class="form-control" id="'.$id.'" name="'.$var.'" type="password" value="'.escape($value).'" '.($options['style']?' style="'.$options['style'].'"':'').' '.($options['class']?' class="'.$options['class'].'"':'').' '.($options['maxlength']?' maxlength="'.$options['maxlength'].'"':'').'/>';
		if($options['help'])
			$html .= '<p class="help-block">'.$options['help'].'</p>';
		$html .= '</div>';
	
		return $html;
	}
	
	
	function add_captcha($label, $width) {
		$id = $this->html_id.'_captcha';

		$text = '<div class="field captcha">';
		$text .= '<label for="'.$id.'">'.$label.'</label>';
		$text .= '<script>r = Math.random(); document.write("<img align=\'absmiddle\' src=\'http://shamora.info/captcha2/?"); document.write(r); document.write("\'/>");</script><noscript><img align=\'absmiddle\' src="http://shamora.info/captcha2/?'.mt_rand().'"/></noscript>';
		$text .= '<input class="string" type="text" id="'.$id.'" name="captcha_code" value="" style="width:'.$width.'"/></div>';

        return $text;
	}

	function addText($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false,
				'maxlength' => 32000, 'disabled' => false, 'is_required' => false);
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="form-group">';
		if($label) $html .= '<label for="'.$id.'">'.$label.($options['is_required'] ? '<span class="required">*</span>' : '').'</label>';
		$html .= '<textarea class="form-control" '.($options['disabled'] ? 'disabled=""' : '').' id="'.$id.'" name="'.$var.'" type="text" value="'.escape($value).'" '.($options['style']?' style="'.$options['style'].'"':'').' '.($options['class']?' class="'.$options['class'].'"':'').' '.($options['maxlength']?' maxlength="'.$options['maxlength'].'"':'').' >'.$value.'</textarea>';
		if($options['help']) $html .= '<p class="help-block">'.$options['help'].'</p>';
		$html .= '</div>';

        return $html;
	}
	
	function addSelect($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false,
		    	'is_required' => false, 'data' => array());
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="form-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'">'.$label.($options['is_required'] ? '<span class="required">*</span>' : '').'</label>';
		$html .= '<select class="form-control" id="'.$id.'" name="'.$var.'" '.($options['style']?' style="'.$options['style'].'"':'').' '.($options['class']?' class="'.$options['class'].'"':'').'>';
		
		$is_first = true;
		$last_group_title = '';
		foreach($options['data'] as $row) {
			if(isset($row['group_title']) && $last_group_title != $row['group_title']) {
				if(!$is_first) $text .= '</optgroup>';
				
				$html .= '<optgroup label="'.$row['group_title'].'">';
				$last_group_title = $row['group_title'];
				$is_first = false;
			}
		
			$level = isset($row['level'])?$row['level']:0;
		    $html .= '<option '.(($row['id']==$value)?'selected ':'').'value="'.escape($row['id']).'">'.str_repeat('&#160; ', $level).$row['title'].'</option>';
		}

		$html .= '</select></div>';

        return $html;
	}
	
	function add_check_tree($var, $label, $value, $data, $is_disabled_groups=false, $skip_ids=false, $is_show_root=false, $width, $height)
	{
		$id = $this->html_id.'_'.$var;
		
		$text = '<div class="field scroll">';
		$text .= '<label>'.$label.'</label>';
		
		$text .= '<div class="fields-container" style="width:'.$width.';height:'.$height.'">';
		$text .= '<ul class="check_list">';
		
		if($is_show_root)
		{
			$text .= '<li>';
			$text .= '<input '.(isset($value[0])?'checked="true"':'').' id="'.$var.'[0]" name="'.$var.'[0]" type="checkbox" />';
			$text .= '<label for="'.$var.'[0]"> / </label>';
			$text .= '</li>';
		    
		    $text .= $this->add_check_tree_process($value, $data, 0, $is_disabled_groups, $skip_ids, 1, $var);
		}
		else
		    $text .= $this->add_check_tree_process($value, $data, 0, $is_disabled_groups, $skip_ids, 0, $var);

		$text .= '</ul>';
		$text .= '</div>';
		
		$text .= '</div>';

        return $text;
	}

	function add_check_tree_process($value, $data, $parent_id, $is_disabled_groups=false, $skip_ids=false, $level=0, $var)
	{
        $text = '';
        
		$is_start = ($level != 0);
	
		foreach($data as $row)
		{
		    if($row['parent_id'] != $parent_id) continue;
		    
		    if($skip_ids !== false)
		        if(in_array($row['id'], $skip_ids)) continue;
		    
		    $is_disabled = false;
		    
		    if($is_disabled_groups)
		    {
		        foreach($data as $temp_row)
		            if($temp_row['parent_id'] == $row['id'])
		            {
						if($skip_ids !== false)
							if(in_array($temp_row['id'], $skip_ids)) continue;
		            
		                $is_disabled = true;
		                break;
		            }
		    }
		    
			if($is_start) $text .= '<ul>';

	    	$is_start = false;
	    	
			$text .= '<li>';
			if(!$is_disabled) $text .= '<input '.(isset($value[$row['id']])?'checked="true"':'').' id="'.$var.'['.$row['id'].']" name="'.$var.'['.$row['id'].']" type="checkbox"/>';
			$text .= '<label for="'.$var.'['.$row['id'].']">'.text($row['title']).'</label>';
			$text .= '</li>';
			
		    if(!$is_disabled_groups || $is_disabled)
		        $text .= $this->add_check_tree_process($value, $data, $row['id'], $is_disabled_groups, $skip_ids, $level+1, $var);
		}
		
		if(!$is_start) $text .= '</ul>';

        return $text;
	}

	function addSelectTree($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false,
				'data' => array(), 'is_disabled_groups' => false, 'skip_ids' => false, 'is_show_root' => false);
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label) $html .= '<label class="control-label" for="'.$id.'">'.$label.'</label>';
		$html .= '<div class="controls"><select id="'.$id.'" name="'.$var.'" '.($options['style']?' style="'.$options['style'].'"':'').' '.($options['class']?' class="'.$options['class'].'"':'').'>';
		
		if($options['is_show_root']) {
		    $html .= '<option'.(($value == 0)?' selected="selected"':'').' value="0">/</option>';
		    $html .= $this->add_select_tree_process($value, $options['data'], 0, '/ ', $options['is_disabled_groups'], $options['skip_ids'], 1);
		} else {
		    $html .= $this->add_select_tree_process($value, $options['data'], 0, '', $options['is_disabled_groups'], $options['skip_ids'], 0);
		}

		$html .= '</select></div></div>';

        return $html;
	}

	function add_select_tree_process($value, $data, $parent_id, $title, $is_disabled_groups=false, $skip_ids=false, $level=0)
	{
	    $text = '';
	    
		foreach($data as $row)
		{
		    if($row['parent_id'] != $parent_id) continue;
		    
		    if($skip_ids !== false)
		        if(in_array($row['id'], $skip_ids)) continue;
		    
		    $is_disabled = false;
		    
		    if($is_disabled_groups)
		    {
		        foreach($data as $temp_row)
		            if($temp_row['parent_id'] == $row['id'])
		            {
						if($skip_ids !== false)
							if(in_array($temp_row['id'], $skip_ids)) continue;
		                $is_disabled = true;
		                break;
		            }
		    }
		    
		    if(!$is_disabled)
		        $text .= '<option'.(($row['id']==$value)?' selected="selected"':'').' value="'.$row['id'].'">'.str_repeat('&#160;', $level*3).text($row['title']).'</option>';
		        
		    if(!$is_disabled_groups || $is_disabled)
		        $text .= $this->add_select_tree_process($value, $data, $row['id'], $title.$row['title'].' / ', $is_disabled_groups, $skip_ids, $level+1);
		}

        return $text;
	}

	function add_datetime($var, $label, $value)
	{
		$id = $this->html_id.'_'.$var;
		
		$text = '<div class="field">';
		$text .= '<label for="'.$id.'">'.$label.'</label>';
		$text .= '<input class="string date datepicker" type="text" name="'.$var.'_date" style="width:80px" value="'.trim(time_format_dateyear($value, true)).'"/> ';
		$text .= '<input class="string time" type="text" name="'.$var.'_time" style="width:50px" value="'.time_format_time($value).'"/>';
		$text .= '</div>';

        return $text;
	}
	
	function addDatetime($var, $label, $value) {
		$id = $this->html_id.'_'.$var;

		$html = '<div class="control-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'_date">'.$label.'</label>';
		$html .= '<div class="controls">';
		$html .= '<input id="'.$id.'_date" class="string" type="text" name="'.$var.'_date" style="width:80px" value="'.trim(time_format_dateyear($value, true)).'"/> ';
		$html .= '<input class="string time" type="text" name="'.$var.'_time" style="width:50px" value="'.time_format_time($value).'"/>';
		$html .= '</div></div>';
		
		$html .= '<script> $(document).ready(function(){ $("#'.$id.'_date").datepicker({ format:"d.mm.yyyy", weekStart:1 }); }); </script>'; 

        return $html;
	}
	
	function addDatetimeCheck($var, $var_check, $label, $value, $value_check) {
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'">'.$label.'</label>';
		$html .= '<div class="controls">';
		$html .= '<input class="checkbox" type="checkbox" '.($value_check?' checked=""':'').' name="is_'.$var.'" onclick="$(\'input[name='.$var.'_date]\').attr(\'disabled\', !this.checked);$(\'input[name='.$var.'_time]\').attr(\'disabled\', !this.checked);"/> ';
		$html .= '<input id="'.$id.'_date" class="string" '.(!$value_check?' disabled=""':'').' type="text" name="'.$var.'_date" style="width:80px" value="'.trim(time_format_dateyear($value, true)).'"/> ';
		$html .= '<input class="string time" '.(!$value_check?' disabled=""':'').' type="text" name="'.$var.'_time" style="width:50px" value="'.time_format_time($value).'"/>';
		$html .= '</div></div>';
		
		$html .= '<script> $(document).ready(function(){ $("#'.$id.'_date").datepicker({ format:"d.mm.yyyy", weekStart:1 }); }); </script>';

        return $html;
	}
	
	function addDateIntervalCheck($var, $label, $value_from, $value_to, $value_check) {
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'_check">'.$label.'</label>';
		$html .= '<div class="controls">';
		$html .= '<input id="'.$id.'_check" class="checkbox" type="checkbox" '.($value_check?' checked=""':'').' name="is_'.$var.'" onclick="$(\'input[name='.$var.'_from]\').attr(\'disabled\', !this.checked);$(\'input[name='.$var.'_to]\').attr(\'disabled\', !this.checked);"/> ';
		$html .= 'от <input id="'.$id.'_from" class="string" '.(!$value_check?' disabled=""':'').' type="text" name="'.$var.'_from" style="width:80px" value="'.trim(time_format_dateyear($value_from, true)).'"/> ';
		$html .= 'до <input id="'.$id.'_to" class="string" '.(!$value_check?' disabled=""':'').' type="text" name="'.$var.'_to" style="width:80px" value="'.trim(time_format_dateyear($value_to, true)).'"/> ';
		$html .= '</div></div>';
		
		$html .= '<script> $(document).ready(function(){ 
				$("#'.$id.'_from").datepicker({ format:"d.mm.yyyy", weekStart:1 });
				$("#'.$id.'_to").datepicker({ format:"d.mm.yyyy", weekStart:1 });
			}); </script>';

        return $html;
	}
	
	function addStringInterval($var, $label, $value_from, $value_to, $options) {
		$default_options = array('from_label' => 'от', 'to_label' => 'до', 'type_label' => '');
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'_check">'.$label.'</label>';
		$html .= '<div class="controls">'.
			$options['from_label'].' <input id="'.$id.'_from" class="string" type="text" name="'.$var.'_from" style="width:80px" value="'.$value_from.'"/> '.
			$options['to_label'].' <input id="'.$id.'_to" class="string" type="text" name="'.$var.'_to" style="width:80px" value="'.$value_to.'"/> '.
			$options['type_label'].
			'</div></div>';

        return $html;
	}
	
	function add_datetime_check($var, $var_check, $label, $value, $value_check)
	{
		$id = $this->html_id.'_'.$var;
		
		$text = '<div class="field">';
		$text .= '<label for="'.$id.'">'.$label.'</label>';
		$text .= '<input class="checkbox" type="checkbox" '.($value_check?' checked=""':'').' name="is_'.$var.'" onclick="$(\'input[name='.$var.'_date]\').attr(\'disabled\', !this.checked);$(\'input[name='.$var.'_time]\').attr(\'disabled\', !this.checked);"/> ';
		$text .= '<input class="string date datepicker" '.(!$value_check?' disabled=""':'').' type="text" name="'.$var.'_date" style="width:80px" value="'.trim(time_format_dateyear($value, true)).'"/> ';
		$text .= '<input class="string time" '.(!$value_check?' disabled=""':'').' type="text" name="'.$var.'_time" style="width:50px" value="'.time_format_time($value).'"/>';
		$text .= '</div>';

        return $text;
	}

	function addFile($var, $label, $value, $options = array()) {
		global $config;
	
		global $config;
		
		$default_options = array('style' => 'overflow-y:auto;height:200px;width:670px;', 'help' => false, 'class' => false,
				'module_id' => false);
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'">'.$label.'</label>';
		$html .= '<div class="controls">';
		
		$html .= '<p><td class="check"><input id="is_'.$id.'" type="checkbox" name="is_'.$var.'" '.@(($value!='')?'checked':'').' onchange="$(\'#'.$id.'_preview\').toggle($(\'#is_'.$id.'\').attr(\'checked\'))"/> ';
		$html .= '<td class="file"><input id="'.$id.'_file" class="file" type="file" name="'.$var.'" onchange="if($(\'#'.$id.'_file\').val()!=\'\') { $(\'#is_'.$id.'\').attr(\'checked\',true); $(\'#'.$id.'_preview\').remove() }"/></p>';
		
		if($options['module_id'] !== false && $value != '') {
			list($file_id, $file_ext) = explode('.', $value);
			
			$html .= '<p class="preview">';
			
        	$url = $config[$options['module_id']]['upload_url'].$value;
        	
			if(is_file_type('image', $value)) {
				$path_image_list = image_cache_get($options['module_id'], $file_id, 'list_tiny');
				if($path_image_list !== false) $html .= '<a target="_blank" href="'.$url.'"><img id="'.$id.'_preview" src="'.$path_image_list.'" /></a>';
			} else {
            	$path = $config[$options['module_id']]['upload_path'].$value;
                $stat = file_extension($path).', '.str_format_human_file_size(filesize($path));
                $html .= '<a target="_blank" href="'.$url.'">Документ</a> ('.$stat.')';
			}

            $html .= '</p>';
		}
		
		$html .= '</div></div>';
		
        return $html;
	}

	function addAttachments($var, $label, $value, $options) {
		global $config;
		
		$default_options = array('style' => 'overflow-y:auto;height:200px;width:670px;', 'help' => false, 'class' => false,
				'module_id' => false);
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label)
			$html .= '<label class="control-label" for="'.$id.'">'.$label.'</label>';
		$html .= '<div class="controls">';
				
		$html .= '<div class="well well-scrollable" style="'.$options['style'].'">';

		for($i=1; $i<=$config[$options['module_id']]['ill_count']; $i++) {
			if(isset($value[$i])) $row = $value[$i];
				else $row = array('file' => '', 'text' => '');
			$html .= $this->attachmentElement($var.'_'.$i, $i, $row['file'], $row['text'], $options);
		}
		
		if($options['help'])
			$html .= '<p class="help-block">'.$options['help'].'</p>';
		$html .= '</div></div>';
		
		return $html;
	}
	
	function attachmentElement($var, $label, $value_file, $value_text, $options) {
		global $config;
	
		$default_options = array('style' => false, 'help' => false, 'class' => false,
				'module_id' => false);
		$options = array_merge($default_options, $options);
	
		$id = $this->html_id.'_'.$var;
	
		$html = '<div class="attachment-element">';

		$html .= '<p class="attachment-element-label">'.escape($label).'</p>';
		$html .= '<p class="attachment-element-file"><input id="is_'.$id.'" type="checkbox" name="is_'.$var.'" '.@(($value_file!='')?'checked':'').' onchange="$(\'#'.$id.'_preview\').toggle($(\'#is_'.$id.'\').attr(\'checked\'))" /> ';
		$html .= '<input id="'.$id.'_file" class="file" type="file" name="'.$var.'" onchange="if($(\'#'.$id.'_file\').val()!=\'\') { $(\'#is_'.$id.'\').attr(\'checked\',true); $(\'#'.$id.'_preview\').remove() }" /></p>';
		$html .= '<p class="attachment-element-text"><input id="'.$id.'_text" class="string" type="text" name="'.$var.'_text" value="'.escape($value_text).'" /></p>';
	
		if($options['module_id'] != false && $value_file != '') {
			list($file_id, $file_ext) = explode('.', $value_file);
		
			$html .= '<p class="attachment-element-preview">';
		
			$url = $config[$options['module_id']]['upload_url'].$value_file;
		
			if(is_file_type('image', $value_file)) {
				$path_image_list = image_cache_get($options['module_id'], $file_id, 'list_tiny');
				if($path_image_list !== false) $html .= '<a href="'.$url.'"><img id="'.$id.'_preview" src="'.$path_image_list.'" /></a>';
			} else {
				$path = $config[$options['module_id']]['upload_path'].$value_file;
				$title = ($value_text != '')?$value_text:'Документ';
				$stat = file_extension($path).', '.str_format_human_file_size(filesize($path));
				$html .= '<a href="'.$url.'">'.escape($title).'</a> ('.$stat.')';
			}
			$html .= '</p>';
		}
		
		$html .= '</div>';
	
		return $html;
	}
	
			
/*			
	function addAttachments		$width, $height, $module_name = false, $text_width = '380px')
	{
		global $config;
	
		$html = $this->scroll_start($label, $width, $height);
	
		for($i=1; $i<=$config[$module_name]['ill_count']; $i++)
		{
			if(isset($value[$i])) $row = $value[$i];
				else $row = array('file' => '', 'text' => '');
				
			$html .= $this->add_ill($var.'_'.$i, $i, $row['file'], $row['text'], $text_width, $module_name);
		}

		$html .= $this->scroll_stop();
	
		return $html;
	}
*/
		
	function addCheckList($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false,
				'data' => array(), 'is_disabled_groups' => false, 'skip_ids' => false, 'is_show_root' => false);
		$options = array_merge($default_options, $options);
		
		$id = $this->html_id.'_'.$var;
		
		$html = '<div class="control-group">';
		if($label) $html .= '<label class="control-label" for="'.$id.'">'.$label.'</label>';
		$html .= '<div class="controls">';
	
	    $last = '';

		foreach($options['data'] as $row) {
	        if(isset($data[0]['group_title']) && $row['group_title'] != $last) {
	            $html .= '<p>'.$row['group_title'].'</li>';
	            $last = $row['group_title'];
	        }

			if(isset($row['level'])) $row['title'] = str_repeat('&#160; ', $row['level']).text($row['title']);
			$html .= '<label class="checkbox" for="'.$var.'['.$row['id'].']">';
			$html .= '<input '.(isset($value[$row['id']])?'checked="true"':'').' id="'.$var.'['.$row['id'].']" name="'.$var.'['.$row['id'].']" type="checkbox" /> '.escape($row['title']);
			$html .= '</label>';
		}

		$html .= '</div></div>';

        return $html;
	}
	
	function add_check_options($var, $label, $value, $data, $width, $height)
	{
		return $this->add_check_list($var, $label, $value, $data, $width, $height, true);
	}

	function addBool($var, $label, $value, $options = array()) {
		$default_options = array('style' => false, 'help' => false, 'class' => false);
		$options = array_merge($default_options, $options);
	
		$id = $this->html_id.'_'.$var;
	
		$html = '<div class="control-group">';
		$html .= '<div class="controls"><label class="checkbox" for="'.$id.'"><input type="checkbox" id="'.$id.'" name="'.$var.'" '.((int)$value?'checked':'').' /> '.$label.'</label>';
		if($options['help']) $html .= '<p class="help-block">'.$options['help'].'</p>';
		$html .= '</div></div>';
	
		return $html;
	}

	function addSection($label)
	{
		$text = '<div class="section">';
		$text .= '<label>'.$label.'</label>';
		$text .= '</div>';

        return $text;
	}

	function list_start()
	{
        return '<div class="list">';
    }

	function list_stop()
	{
        return '</div>';
    }
    
    function scroll_start($title = false, $width, $height)
    {
        $text = '<div class="scroll">';
        if($title) $text .= '<label>'.text($title).'</label>';
        $text .= '<div class="fields-container" style="width:'.$width.';height:'.$height.';">';

        return $text;
    }

	function scroll_stop()
	{
        return '</div></div>';
    }
    
	function submit($label = 'Ok', $icon = false, $cancel_action = false)
	{
		$text = '<div class="actions">';
	    $text .= '<button class="btn btn-primary" onclick="$(this).disable()" type="submit"><span'.(($icon !== false)?' class="'.$icon.'"':'').'></span>'.$label.'</button>';
	    if($cancel_action !== false)
	    	$text .= ' <button class="btn" type="button" onclick="'.$cancel_action.'" autocomplete="off">Отмена</button>';
		$text .= '</div>';

	    $text .= '</fieldset>';
		$text .= '</form>';

        return $text;
	}

	function close()
	{
		$text = '</div>';
		
	    $text .= '<input type="submit" style="display:none" />';

	    $text .= '</form>';
		$text .= '</div>';

        return $text;
	}
	
	function add_birthday($var, $label, $value)
	{
		global $time;
	
		if($value == '')
		{
		    $day = 0;
		    $month = 0;
		    $year = 0;
		}
		else
		{
		    list($year, $month, $day) = explode('-', $value);

		    $year = (int)$year;
		    $month = (int)$month;
		    $day = (int)$day;
		}
		
		$days = array(array('id' => 0, 'title' => '—'));
		for($i=1; $i<=31; $i++) $days []= array('id' => $i, 'title' => $i);

		$months = array(array('id' => 0, 'title' => '—'));
		for($i=1; $i<=12; $i++) $months []= array('id'=> $i, 'title' => $i);

		$years = array(array('id'=>0, 'title'=>'—'));
		for($i=strftime('%Y')-7; $i>=1950; $i--) $years []= array('id' => $i, 'title' => $i);
		    
		$html = '<div class="field"><label>'.$label.'</label>';

		$html .= '<select name="'.$var.'_day">';
		foreach($days as $row)
		    $html .= '<option '.(($row['id'] == $day)?'selected ':'').'value='.$row['id'].'>'.$row['title'].'</option>';
		$html .= '</select>';

		$html .= ' / <select name="'.$var.'_month">';
		foreach($months as $row)
		    $html .= '<option '.(($row['id'] == $month)?'selected ':'').'value='.$row['id'].'>'.($row['id']?$time['name_month'][$row['id']]:'—').'</option>';
		$html .= '</select>';

		$html .= ' / <select name="'.$var.'_year">';
		foreach($years as $row)
		    $html .= '<option '.(($row['id'] == $year)?'selected ':'').'value='.$row['id'].'>'.$row['title'].'</option>';
		$html .= '</select>';

		$html .= '</div>';
		
		return $html;
	}
}
