<?php

require_once('../vendor/phpmailer/class.phpmailer-5.2.1.php');

function mail_send($from_name, $from_address, $to_address, $subject, $message, $is_html = false)
{ 
    $mail = new PHPMailer();
    
    $mail->CharSet = 'windows-1251';
    
    $mail->From = iconv('UTF-8', 'windows-1251', $from_address);
    $mail->FromName = iconv('UTF-8', 'windows-1251', $from_name);
    $mail->AddAddress(iconv('UTF-8', 'windows-1251', $to_address));

    $mail->Subject = iconv('UTF-8', 'windows-1251', $subject);
    
    $mail->IsHTML($is_html);

    $mail->Body = iconv('UTF-8', 'windows-1251', $message);
	$mail->Send();
}

