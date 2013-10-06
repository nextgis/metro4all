<?php

define('R_FIT', 0);
define('R_CROP', 1);
define('R_NONE', 2);
define('R_FIT_MIN', 3);

if(!defined('JPEG_QUALITY')) define('JPEG_QUALITY', 90);

if (defined('__DIR__'))
	require_once __DIR__.'/lib.image_cache.php';
	

function resize_image_fit($src_image, $width, $height)
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

	image_unsharp_mask($new_image);

	return $new_image;
}

function resize_image_crop($src_image, $width, $height)
{
	$src_width = imageSX($src_image);
	$src_height = imageSY($src_image);

	$width = ($width <= 0)?$src_width:$width;
	$height = ($height <= 0)?$src_height:$height;

	$prop_width = $src_width/$width;
	$prop_height = $src_height/$height;

	if($prop_height > $prop_width)
	{
		$crop_width = $src_width;
		$crop_height = round($prop_width*$height);
		$srcX = 0;
		$srcY = round($src_height/2)-round($crop_height/2);
	}
	else
	{
		$crop_width = round($prop_height*$width);
		$crop_height = $src_height;
		$srcX = round($src_width/2)-round($crop_width/2);
		$srcY = 0;
	}

    $new_image = imageCreateTrueColor($width, $height);
    $tmp_image = imageCreateTrueColor($crop_width, $crop_height);
	imageCopy($tmp_image, $src_image,0,0,$srcX,$srcY,$crop_width,$crop_height);
	imageCopyResampled($new_image,$tmp_image,0,0,0,0,$width,$height,$crop_width,$crop_height);
	imagedestroy($tmp_image);

	image_unsharp_mask($new_image);

	return $new_image;
}

function resize_image_file($path, $width, $height, $mode, $is_watermark=false)
{
//	image_memOverflowProtect(&$path, $overload = 0);

    ini_set('memory_limit','512M');

	$parts = getimagesize($path);
	if($parts['mime']!='image/jpeg') return false;
	
	// если фотография слишком большая пропускаем создание превью    
    if($parts[0] > 4000 || $parts[1] > 4000)
    {
    	return false;
    }

	switch($mode)
	{
		case R_FIT_MIN:
			$size = getimagesize($path);
			if ($size[0] <= $width && $size[1] <= $height ){
				return;
			}
			$src_image = imageCreateFromJPEG($path);
			$new_image = resize_image_fit($src_image, $width, $height);

			if($is_watermark!==false)
				apply_watermark($new_image, $is_watermark);

			imageJPEG($new_image, $path, JPEG_QUALITY);
			imagedestroy($src_image);
			imagedestroy($new_image);
			break;
		case R_FIT:
			$src_image = imageCreateFromJPEG($path);
			$new_image = resize_image_fit($src_image, $width, $height);

			if($is_watermark!==false)
				apply_watermark($new_image, $is_watermark);

			imageJPEG($new_image, $path, JPEG_QUALITY);
			imagedestroy($src_image);
			imagedestroy($new_image);
			break;

		case R_CROP:
			$src_image = imageCreateFromJPEG($path);
			$new_image = resize_image_crop($src_image, $width, $height);

			if($is_watermark!==false)
				apply_watermark($new_image, $is_watermark);

			imageJPEG($new_image, $path, JPEG_QUALITY);
			imagedestroy($src_image);
			imagedestroy($new_image);
			break;

		case R_NONE:
			break;
	}
}

function image_unsharp_mask(&$img)
{
	global $config;

	if(!$config['image_unsharp']) return;

	if(function_exists('imageconvolution'))
	{
		$matrix = array(array(-1, -1, -1), array(-1, 25, -1), array(-1, -1, -1));
		$divisor = 16;
		$offset = 0;
		imageconvolution($img, $matrix, $divisor, $offset);
	}
	else
	{
		$amount = 1000;

        $amount = min($amount, 500);
        $amount = $amount * 0.016;
        if ($amount == 0) return true;
    
        $w = ImageSX($img);
        $h = ImageSY($img);
        $imgCanvas  = ImageCreateTrueColor($w, $h);
        $imgCanvas2 = ImageCreateTrueColor($w, $h);
        $imgBlur    = ImageCreateTrueColor($w, $h);
        $imgBlur2   = ImageCreateTrueColor($w, $h);
        ImageCopy($imgCanvas,  $img, 0, 0, 0, 0, $w, $h);
        ImageCopy($imgCanvas2, $img, 0, 0, 0, 0, $w, $h);
    
        ImageCopy     ($imgBlur, $imgCanvas, 0, 0, 1, 1, $w - 1, $h - 1);
        ImageCopyMerge($imgBlur, $imgCanvas, 1, 1, 0, 0, $w,     $h,     50);
        ImageCopyMerge($imgBlur, $imgCanvas, 0, 1, 1, 0, $w - 1, $h,     33.33333);
        ImageCopyMerge($imgBlur, $imgCanvas, 1, 0, 0, 1, $w,     $h - 1, 25);
        ImageCopyMerge($imgBlur, $imgCanvas, 0, 0, 1, 0, $w - 1, $h,     33.33333);
        ImageCopyMerge($imgBlur, $imgCanvas, 1, 0, 0, 0, $w,     $h,     25);
        ImageCopyMerge($imgBlur, $imgCanvas, 0, 0, 0, 1, $w,     $h - 1, 20 );
        ImageCopyMerge($imgBlur, $imgCanvas, 0, 1, 0, 0, $w,     $h,     16.666667);
        ImageCopyMerge($imgBlur, $imgCanvas, 0, 0, 0, 0, $w,     $h,     50);
        ImageCopy     ($imgCanvas, $imgBlur, 0, 0, 0, 0, $w,     $h);
    
        ImageCopy     ($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 50);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 33.33333);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 25);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 33.33333);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 25);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 20 );
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 16.666667);
        ImageCopyMerge($imgBlur2, $imgCanvas2, 0, 0, 0, 0, $w, $h, 50);
        ImageCopy     ($imgCanvas2, $imgBlur2, 0, 0, 0, 0, $w, $h);
    
        for ($x = 0; $x < $w; $x++)    {
            for ($y = 0; $y < $h; $y++)    {
    
                $rgbOrig = ImageColorAt($imgCanvas2, $x, $y);
                $rOrig = (($rgbOrig >> 16) & 0xFF);
                $gOrig = (($rgbOrig >>  8) & 0xFF);
                $bOrig =  ($rgbOrig        & 0xFF);
    
                $rgbBlur = ImageColorAt($imgCanvas, $x, $y);
                $rBlur = (($rgbBlur >> 16) & 0xFF);
                $gBlur = (($rgbBlur >>  8) & 0xFF);
                $bBlur =  ($rgbBlur        & 0xFF);
    
                $rNew = max(0, min(255, ($amount * ($rOrig - $rBlur)) + $rOrig));
                $gNew = max(0, min(255, ($amount * ($gOrig - $gBlur)) + $gOrig));
                $bNew = max(0, min(255, ($amount * ($bOrig - $bBlur)) + $bOrig));
    
                ImageSetPixel($img, $x, $y, ImageColorAllocate($img, $rNew, $gNew, $bNew));
            }
        }
        ImageDestroy($imgCanvas);
        ImageDestroy($imgCanvas2);
        ImageDestroy($imgBlur);
        ImageDestroy($imgBlur2);
	}
}

function apply_watermark(&$im, $path)
{
	$wm_image = imageCreateFromPNG($path);
	imagecopy($im, $wm_image, imagesx($im)-imagesx($wm_image),imagesy($im)-imagesy($wm_image), 0,0, imagesx($wm_image),imagesy($wm_image));
	imagedestroy($wm_image);
}

function apply_watermark_on_file($im_path, $path)
{
	$im = imageCreateFromJPEG($im_path);
	apply_watermark($im, $path);
	imageJPEG($im, $im_path, JPEG_QUALITY);
	imagedestroy($im);
}
