<?php 

$config['image_cache'] = array(
	'list' => array('resize_crop' => array('width'=>140, 'height'=>100), 'unsharp' => true, ),
	'small' => array('resize_fit_max' => array('width'=>200, 'height'=>200), 'unsharp' => true, ),
	'medium' => array('resize_fit_max' => array('width'=>350, 'height'=>350), 'unsharp' => true, ),
	'full' => array('resize_fit_max' => array('width'=>800, 'height'=>800), 'unsharp' => true, ),
);

function image_cache_clear($upload_path, $file_name, $format_name='*')
{
	global $config;
	
	$file_id = file_filename($file_name);
	
	foreach((strtoupper(substr(PHP_OS, 0, 3)) === 'WIN')?
		array_keys($config['image_cache']):
		glob($upload_path.'cache/'.$format_name.'/', GLOB_ONLYDIR) as $format_path)
	{
		$path = $format_path.$file_id.'.jpg';
		if(file_exists($path) && is_writable($path))
			unlink($path);
	}
}

function image_cache_get($module_name, $file_id, $format_name)
{
	global $config;
	
	$upload_path = $config[$module_name]['upload_path'];
	$upload_url = $config[$module_name]['upload_url'];
	
	// jpg или png
	$format = (isset($config['image_cache'][$format_name]['format']) ? $config['image_cache'][$format_name]['format'] : 'jpg');
	
	$cache_file_path = $upload_path.'cache/'.$format_name.'/'.$file_id.'.'.$format;
	
	if(file_exists($cache_file_path))
	{
		$filemtime = filemtime($cache_file_path);
		if(!isset($config['image_cache'][$format_name]['update_before'])
			|| ($config['image_cache'][$format_name]['update_before'] < $filemtime))
				return $upload_url.'cache/'.$format_name.'/'.$file_id.'.'.$filemtime.'.'.$format;
	}
	
	$extension = '';
	foreach(array('jpg', 'gif', 'png', 'bmp') as $temp)
	{
		if(file_exists($upload_path.$file_id.'.'.$temp))
		{
			$extension = $temp;
			break;
		}
	}

	$source_file_path = $upload_path.$file_id.'.'.$extension;

	if(!$extension)
	{
//		user_error('image_cache_get() source file not exists "'.$source_file_path.'"', E_USER_WARNING);
		return false;
	}

	if(!is_file_type('image', $source_file_path))
	{
		user_error('image_cache_get() source file is not image '.$source_file_path, E_USER_WARNING);
		return false;
	}

	if(!isset($config['image_cache'][$format_name]))
	{
		user_error('image_cache_get() format not exists "'.$format_name.'"', E_USER_WARNING);
		return false;
	}
	
	if(!file_exists($upload_path.'cache/'))
	mkdir($upload_path.'cache/');
	
	if(!file_exists($upload_path.'cache/'.$format_name.'/'))
		mkdir($upload_path.'cache/'.$format_name.'/');

	ini_set('memory_limit', '1024M');
	$parts = @getimagesize($source_file_path);
	
	// если фотография слишком большая пропускаем создание превью	

	if($parts[0] > 6000 || $parts[1] > 6000)
	{
		return false;
	}	

	switch($parts['mime'])
	{
		case 'image/jpeg': $image = imageCreateFromJPEG($source_file_path); break;
		case 'image/png': $image = 	imageCreateFromPNG($source_file_path); 	break;
		case 'image/gif': $image = 	imageCreateFromGIF($source_file_path); 	break;
/*		
		case 'image/bmp':
		case 'image/x-ms-bmp':
			$image = imageCreateFromBMP($source_file_path); break; 
*/
		default:
			// user_error('image_cache_get() non supported mime "'.$parts['mime'].'" - "'.$source_file_path.'"', E_USER_WARNING);
			return false;
	}
	
	$is_alfa = ($format == 'png' ? true : false);
	foreach($config['image_cache'][$format_name] as $action => $params)
	{
		switch($action)
		{
			case 'resize_crop': image_cache_resize_crop($image, $params['width'], $params['height']); break;
			case 'resize_fit_height': image_cache_resize_fit_height($image, $params['height']); break;
			case 'resize_fit': image_cache_resize_fit($image, $params['width'], $params['height']); break;
			case 'resize_fit_max': image_cache_resize_fit_max($image, $params['width'], $params['height'], $is_alfa); break;
			case 'unsharp_mask': image_cache_unsharp_mask($image); break;
			case 'hd_sharpen': image_cache_hd_sharpen($image); break;
			case 'watermark': image_cache_watermark($image, $params['path'], $params['random_shift']); break;
			case 'quality': $quality = $params;
		}
	}
	
	if ($format == 'png')
	{
		imagePNG($image, $cache_file_path);
	}
	else
	{
		imageJPEG($image, $cache_file_path, JPEG_QUALITY);
	}

	imageDestroy($image);

	$filemtime = filemtime($cache_file_path);
	
	$new_image_path = $upload_path.'cache/'.$format_name.'/'.$file_id.'.'.$format;
	
	return $upload_url.'cache/'.$format_name.'/'.$file_id.'.'.$filemtime.'.'.$format;
}

function image_cache_clean_path($path)
{
	return preg_replace('/^(.*)\.[\d]{10}\.([^.]*)$/', '$1.$2', $path);
}

function image_cache_resize_fit_height(&$src_image, $height)
{
	$src_width = imageSX($src_image);
	$src_height = imageSY($src_image);
	
	$div = $height/$src_height;
	
	$new_height = round($src_height*$div);
	$new_width = round($src_width*$div);

	$new_image = imageCreateTrueColor($new_width, $new_height);
	imageCopyResampled($new_image, $src_image, 0,0,0,0, $new_width, $new_height, $src_width, $src_height);

	imageDestroy($src_image);
	$src_image = $new_image;
}

function image_cache_resize_fit(&$src_image, $width, $height)
{
	$src_width = imageSX($src_image);
	$src_height = imageSY($src_image);

	$width = ($width <= 0)?$src_width:$width;
	$height = ($height <= 0)?$src_height:$height;

	$prop_width = $width/$src_width;
	$prop_height = $height/$src_height;

	$prop = min($prop_width, $prop_height);

	$new_width = round($src_width*$prop);
	$new_height = round($src_height*$prop);

	$new_image = imageCreateTrueColor($new_width, $new_height);
	imageCopyResampled($new_image, $src_image, 0,0,0,0, $new_width, $new_height, $src_width, $src_height);

	imageDestroy($src_image);
	$src_image = $new_image;
}

function image_cache_resize_crop(&$src_image, $width, $height)
{
	$src_width = imageSX($src_image);
	$src_height = imageSY($src_image);
	
	if(($width/$height) < ($src_width/$src_height))
	{
		$scale_height = $height;
		$scale_y = 0;
		$scale_width = round($height / $src_height * $src_width);
		$scale_x = round(($width - $scale_width) / 2);
	}
	else
	{
		$scale_width = $width;
		$scale_x = 0;
		$scale_height = round($width / $src_width * $src_height);
		$scale_y = round(($height - $scale_height) / 2);
	}
	
	$new_image = imageCreateTrueColor($width, $height);
	imageCopyResampled($new_image, $src_image, $scale_x, $scale_y, 0, 0, $scale_width, $scale_height, $src_width, $src_height);
	imageDestroy($src_image);
	$src_image = $new_image;
}

function image_cache_unsharp_mask(&$img)
{
	imageConvolution($img, 
			array(	array(-1, -1, -1), 
					array(-1, 25, -1), 
					array(-1, -1, -1)), 
			16, 0);
}

function image_cache_watermark(&$img, $path, $random_shift=false)
{
	$wm_image = imageCreateFromPNG($path);
	
	if($random_shift !== false)
	{
		$offset_x = mt_rand(0,$random_shift)-$random_shift/2;
		$offset_y = mt_rand(0,$random_shift)-$random_shift/2;
	}
	else
	{
		$offset_x = 0;
		$offset_y = 0;
	}
	
	imagecopy($img, $wm_image, imagesx($img)-imagesx($wm_image)+$offset_x, imagesy($img)-imagesy($wm_image)+$offset_y,
		0,0, imagesx($wm_image), imagesy($wm_image));
	
	imagedestroy($wm_image);
}

function image_cache_resize_fit_max(&$src_image, $width, $height, $alfa = false)
{
	$src_width = imageSX($src_image);
	$src_height = imageSY($src_image);

	if(($src_width > $width) || ($src_height > $height))
	{
		$width = ($width <= 0)?$src_width:$width;
		$height = ($height <= 0)?$src_height:$height;

		$prop_width = $width/$src_width;
		$prop_height = $height/$src_height;

		$prop = min($prop_width, $prop_height);

		$new_width = round($src_width*$prop);
		$new_height = round($src_height*$prop);

		$new_image = imageCreateTrueColor($new_width, $new_height);
		if ($alfa)
		{
			$c = imagecolorallocatealpha( $new_image, 0, 0, 0, 127 );
			imagefill( $new_image, 0, 0, $c );
		}
		imageCopyResampled($new_image, $src_image, 0,0,0,0, $new_width, $new_height, $src_width, $src_height);
		if ($alfa)
		{
			imagealphablending($new_image, false);
			imagesavealpha($new_image, true);
		}

		imageDestroy($src_image);
		$src_image = $new_image;
	}
}
