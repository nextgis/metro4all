<?php

$file_extension_mime = array(
	'pdf' => 'application/pdf',
	'cdr' => 'application/cdr',
    'ai' => 'application/ai',
    'eps' => 'application/eps',

	'jpg' => 'image/jpeg',
	'jpeg' => 'image/jpeg',
	'jpe' => 'image/jpeg',
	'gif' => 'image/gif',
	'png' => 'image/png',
    'tif' => 'image/tiff',
    'tiff' => 'image/tiff',

	'doc' => 'application/msword',
	'xls' => 'application/vnd.ms-excel',
    'pps' => 'application/vnd.ms-powerpoint',
    'ppt' => 'application/vnd.ms-powerpoint',
	'xlsx' => 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
	'docx' => 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',

    'mp3' => 'audio/mpeg',

    'rar' => 'application/rar',
    'zip' => 'application/zip',

    'txt' => 'text/plain',

    'swf' => 'application/x-shockwave-flash',
	'flv' => 'video/x-flv',
	'f4v' => 'video/x-f4v',
	'mp4' => 'video/mp4',
	'mpg' => 'video/mpeg',
	'mpeg' => 'video/mpeg',
    'mov' => 'video/quicktime',
    'avi' => 'video/x-msvideo',
    'wmv' => 'video/x-ms-wmv',
);

$file_extension_type = array(
	'document' => array('f4v', 'mp4', 'mpg', 'mpeg', 'pdf', 'doc', 'xls', 'jpg', 'jpeg', 'gif', 'png', 'cdr',
        'tif', 'tiff', 'mov', 'avi', 'wmv', 'swf', 'pps', 'mp3', 'ai', 'eps', 'rar', 'zip', 'ppt', 'txt',
        'flv', 'xlsx', 'docx', 'odt'),
	
	'flash' => array('swf'),

    'flash_video' => array('f4v', 'mp4', 'flv'),

	'image' => array('jpeg', 'jpg', 'png', 'gif', 'bmp'),
	
	'firmware' => array('pkg')
);

function file_filename($path)
{
	return pathinfo($path, PATHINFO_FILENAME);
}

function file_extension($path)
{
	return mb_strtolower(pathinfo($path, PATHINFO_EXTENSION));
}

function file_mimetype($path)
{
	global $file_extension_mime;
	
	$ext = file_extension($path);
	return isset($file_extension_mime[$ext])?$file_extension_mime[$ext]:'';
}

function is_file_type($type, $path)
{
	global $file_extension_type;
	
	return in_array(file_extension($path), $file_extension_type[$type]);
}

function upload_file($var, $upload_path, $file_name_prefix)
{
	if(isset($_FILES[$var]) && ($_FILES[$var]['error'] == UPLOAD_ERR_OK)
		&& is_file_type('document', $_FILES[$var]['name']))
	{
		$ext = file_extension($_FILES[$var]['name']);
		
		if($ext == 'jpeg') $ext = 'jpg';
		
		if(move_uploaded_file($_FILES[$var]['tmp_name'],
			$upload_path.$file_name_prefix.'.'.$ext))
		{
			return $file_name_prefix.'.'.$ext;
		}
	}
    
    return '';
}

function delete_file($upload_path, $file_name)
{
	$path = $upload_path.$file_name;
	
	if(file_exists($path) && is_file($path) && is_writable($path))
	{
		if(is_file_type('image', $file_name))
			image_cache_clear($upload_path, $file_name);
			
		unlink($path);
	}
}


function insert_file($var, $upload_path, $file_name_prefix)
{
	if(isset($_REQUEST['is_'.$var]) && $_REQUEST['is_'.$var] && isset($_FILES[$var]))
		return upload_file($var, $upload_path, $file_name_prefix);
	
	return '';
}

function update_file($var, $upload_path, $file_name_prefix, $old_file_name)
{
	if(isset($_REQUEST['is_'.$var]) && $_REQUEST['is_'.$var] && isset($_FILES[$var]))
	{
		if($_FILES[$var]['error'] == UPLOAD_ERR_OK)
		{
			if($old_file_name != '')
				delete_file($upload_path, $old_file_name);
			return insert_file($var, $upload_path, $file_name_prefix);
		}
		else
			return $old_file_name;
	}
	else
    {
		if($old_file_name != '')
			delete_file($upload_path, $old_file_name);
		return '';
    }
}

// Загрузка файла на удаленный FTP

function upload_file_ftp($host, $name, $pass, $var, $upload_path, $file_name_prefix)
{
	
	if(isset($_FILES[$var]) && ($_FILES[$var]['error'] == UPLOAD_ERR_OK)
			&& is_file_type('firmware', $_FILES[$var]['name']))
	{
		
		$ext = file_extension($_FILES[$var]['name']);
		
		if($ext == 'jpeg') $ext = 'jpg';
		
		$conn_id = ftp_connect($host);
		$login_result = ftp_login($conn_id, $name, $pass);
		ftp_pasv($conn_id, true);
		
		
		if(ftp_put($conn_id, $upload_path.$file_name_prefix.'.'.$ext, $_FILES[$var]['tmp_name'], FTP_BINARY))
		{
			
			ftp_quit($conn_id);
			return $file_name_prefix.'.'.$ext;
		}
	}

	return '';
}

function delete_file_ftp($host, $name, $pass, $upload_path, $file_name)
{
	$path = $upload_path.$file_name;

	if(file_exists($path) && is_file($path) && is_writable($path))
	{
		$conn_id = ftp_connect($host);
		$login_result = ftp_login($conn_id, $name, $pass);
		ftp_pasv($conn_id, true);
		
		if(is_file_type('image', $file_name))
			image_cache_clear($upload_path, $file_name);
			
		ftp_delete($conn_id, $path);
		ftp_quit($conn_id);
		unlink($path);
	}
}

function insert_file_ftp($host, $name, $pass, $var, $upload_path, $file_name_prefix)
{
	if(isset($_REQUEST['is_'.$var]) && $_REQUEST['is_'.$var] && isset($_FILES[$var]))
		
		return upload_file_ftp($host, $name, $pass, $var, $upload_path, $file_name_prefix);

	return '';
}

function update_file_ftp($host, $name, $pass, $var, $upload_path, $file_name_prefix, $old_file_name)
{
	if(isset($_REQUEST['is_'.$var]) && $_REQUEST['is_'.$var] && isset($_FILES[$var]))
	{
		if($_FILES[$var]['error'] == UPLOAD_ERR_OK)
		{
			if($old_file_name != '')
				delete_file_ftp($host, $name, $pass, $upload_path, $old_file_name);
			return insert_file_fpt($host, $name, $pass, $var, $upload_path, $file_name_prefix);
		}
		else
			return $old_file_name;
	}
	else
	{
		if($old_file_name != '')
			delete_file($upload_path, $old_file_name);
		return '';
	}
}


function insert_attachments($var, $upload_path, $file_name_prefix, $max_count)
{
	return update_attachments($var, $upload_path, $file_name_prefix, $max_count, array());
}	

function update_attachments($var, $upload_path, $file_name_prefix, $max_count, $old_attachments)
{
	$temp = $old_attachments;
	
	for($i=1; $i<=$max_count; $i++)
	{
		if(isset($_REQUEST['is_'.$var.'_'.$i]))
		{
			$old_file_name = '';
			if(isset($temp[$i]['file']))	
				$old_file_name = $temp[$i]['file'];

			$file_name = update_file($var.'_'.$i, $upload_path, $file_name_prefix.'_'.$i, $old_file_name);
			
			if(!isset($temp[$i])) $temp[$i] = array('file' => '', 'mime_type' => '', 'text' => '');
		
			$temp[$i]['file'] = $file_name;
			$temp[$i]['mime_type'] = file_mimetype($file_name);
		}
		else
		{
			$old_file_name = '';
			if(isset($temp[$i]['file']))	
				$old_file_name = $temp[$i]['file'];

			if($old_file_name != '')
				delete_file($upload_path, $old_file_name);
				
			unset($temp[$i]);
		}

		if(isset($_REQUEST[$var.'_'.$i.'_text']))
		{
			if(!isset($temp[$i])) $temp[$i] = array('file' => '', 'mime_type' => '', 'text' => '');
			
			$temp[$i]['text'] = $_REQUEST[$var.'_'.$i.'_text'];
		}
		
/*		
		if($temp[$i]['text'] == '' && isset($_REQUEST['is_'.$var.'_'.$i]) &&
			isset($_FILES[$var.'_'.$i]) && ($_FILES[$var.'_'.$i]['error'] == UPLOAD_ERR_OK))
				$temp[$i]['text'] = file_filename($_FILES[$var.'_'.$i]['name']);
*/				
	}
		
	$result = array();
	foreach($temp as $i => $row)
	{
		if($row['file'] || $row['text'])
			$result[$i] = $row;
	}
	
	ksort($result);

	return $result;
}

function delete_attachments($upload_path, $attachments)
{
	foreach($attachments as $row)
		delete_file($upload_path, $row['file']);
}

