<?php

class Page
{
	 var $title, $breadcrumbs;

	function __construct($title = false, $breadcrumbs = false) {
		$this->title = $title;
		$this->breadcrumbs = $breadcrumbs;
	}
	
	function start() {
		header('Content-type: text/html; charset=utf-8');
		
		return '<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	' . $this->formatInfo() .'

	<script src="/vendor/jquery/jquery-1.10.2.min.js"></script>

	<!-- Bootstrap -->
	<link href="/vendor/bootstrap-3.0.0/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<script src="/vendor/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>

	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
	<script src="/vendor/html5shiv/dist/html5shiv.js"></script>
	<script src="/vendor/respond/respond.min.js"></script>
	<![endif]-->

	' . $this->formatResources() . '
</head>
<body>
	<div class="container">
		' . $this->formatHeader() . '
		' . $this->formatBreadcrumbs() . '
		' . $this->formatTitle() . '
';
	}

	function stop() {
	    return '
	        ' . $this->formatFooter() . '
	</div>

' . $this->formatCounters() . '

</body>
</html>			
';
	}
	
	function formatInfo() {
		return '<title>'.escape($this->title).'</title>';
	}
	
	function formatHeader() {
	}
	
	function formatFooter() {
	}
	
	function formatBreadcrumbs() {
		$html = '';
		if ($this->breadcrumbs && count($this->breadcrumbs)) {
			$last = count($this->breadcrumbs) - 1;
			$i = 0;
			foreach($this->breadcrumbs as $row) {
			    $html .= '<li'.(($i == $last) ? ' class="active"' : '').'><a title="'.escape($row[0]).'" href="'.escape($row[1]).'">'.escape($row[0]).'</a> &rarr;</li>';
			    $i ++;
			}
			$html .= '<ul class="breadcrumb">'.$html.'</ul>';
		}
		return $html;
	}
	
	function formatTitle() {
	    if ($this->title != '' && (! isset(Core::$config['page']['is_custom_title']) || Core::$config['page']['is_custom_title'] == false)) {
	    	return '<h1>'.escape($this->title).'</h1>';
	    }
	}
		
    static function addResource($type, $path, $conditional = false, $weight = 0) {
		Core::$config['page'][$type][$weight] []= array('path' => $path, 'conditional' => $conditional);
    }
    
	function formatResources() {
		$html = '';

		if (isset(Core::$config['page']['style'])) {
		    ksort(Core::$config['page']['style']);

			foreach (Core::$config['page']['style'] as $by_weight) {
			    foreach ($by_weight as $row) {
				    if ($row['conditional'] != '') {
						$html .= '<!--[if '.$row['conditional'].']>';
					}

					if (mb_substr($row['path'], 0, 7) == 'http://')
					{
						$html .= '<link type="text/css" rel="stylesheet" href="' . $row['path'] . '" />'
							. "\r\n";
					} else {
						$mtime = @filemtime($row['path']);
						$url = $row['path'];
						$html .= '<link type="text/css" rel="stylesheet" href="http://'
							. Core::$config['http_domain']
							. Core::$config['http_root']
							. ($mtime ? str_replace('.css', '.' . $mtime . '.css', $url) : $url) . '" />'
							. "\r\n";

						if($row['conditional'] != '') {
							$html .= '<![endif]-->';
						}
					}
			    }
			}
		}

		if(isset(Core::$config['page']['script'])) {
		    ksort(core::$config['page']['script']);

			foreach(core::$config['page']['script'] as $by_weight) {
			    foreach($by_weight as $row) {
				    if($row['conditional'] != '') {
				        $html .= '<!--[if '.$row['conditional'].']>';
				    }
				    
				    if(substr($row['path'], 0, 7) != 'http://') {
						$mtime = @filemtime($row['path']);
						$url = $row['path'];
						$html .= '<script type="text/javascript" src="http://'.core::$config['http_domain'].core::$config['http_root'].($mtime?str_replace('.js', '.'.$mtime.'.js', $url):$url).'"></script>'."\r\n";
					} else {
						$html .= '<script type="text/javascript" src="'.$row['path'].'"></script>'."\r\n";
					}
					
				    if($row['conditional'] != '') {
				        $html .= '<![endif]-->';
				    }
			    }
			}
		}

		if(isset(core::$config['page']['link'])) {
		    ksort(core::$config['page']['link']);
			foreach(core::$config['page']['link'] as $by_weight) {
			    foreach($by_weight as $row) {
				    $html .= '<link rel="'.$row['rel'].'" href="'.$row['href'].'">'."\r\n";
			    }
			}
		}

		if(isset(core::$config['page']['meta'])) {
			foreach(core::$config['page']['meta'] as $by_weight) {
				foreach($by_weight as $meta) {
					$html .= $meta['path'];
				}
			}
		}

		return $html;
	}
    
    static function addCanononical($url) {
    	if (mb_substr($url, 0, 1) == '/')
    		$url = 'http://'.core::$config['http_domain'].core::$config['http_root'].$url;
		self::addResource('meta', '<link rel="canonical" href="'.$url.'"/>');    	
    }

	function formatCounters() {
	}
}
