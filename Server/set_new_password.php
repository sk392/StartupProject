<?php
require_once("./lib/PHPMailer/PHPMailerAutoload.php");

   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();

$user_email=$_POST['user_email'];

$mail = new PHPMailer;

$mail->SMTPDebug = 0;                               // Enable verbose debug output
$mail->CharSet = 'UTF-8';
$mail->isSMTP();                                      // Set mailer to use SMTP
$mail->Host = 'smtp.gmail.com';  // Specify main and backup SMTP servers
$mail->SMTPAuth = true;                               // Enable SMTP authentication
$mail->Username = 'carmony.kr@gmail.com';                 // SMTP username
$mail->Password = 'bookcar0113';                           // SMTP password
$mail->SMTPSecure = 'ssl';                            // Enable TLS encryption, `ssl` also accepted
$mail->Port = 465;                                    // TCP port to connect to tls = 587, ssl =465

$mail->setFrom('carmony.kr@gmail.com', 'Carmony');
$mail->addAddress($user_email);     // Add a recipient
//$mail->addAddress('sdhfdpdp7s@naver.com');               // Name is optional
$mail->addReplyTo('info@example.com', 'Information');
//$mail->addCC('cc@example.com');
//$mail->addBCC('bcc@example.com');

//$mail->addAttachment('/var/tmp/file.tar.gz');         // Add attachments
//$mail->addAttachment('/tmp/image.jpg', 'new.jpg');    // Optional name
$mail->isHTML(true);                                  // Set email format to HTML
//비밀번호 랜덤 생성.
$uppers = str_shuffle('ABCDEFGHIJKLMNOPQRSTUVWXYZ');
$lowers = str_shuffle('abcdefghijklmnopqrstuvwxyz');
$numbers = str_shuffle('1234567890');
$specials = str_shuffle('!@#$%^&*()');
$complex_password = substr($uppers,-2).substr($lowers,-2).substr($numbers,-2).substr($specials,-2);
$more_complex_password = str_shuffle($complex_password);

$mail->Subject = 'Carmony Find Password';
$mail->Body    = '요청하신 계정의 새로운 비밀번호는 <br><b>'.$more_complex_password.'</b> 입니다. ';

//데이터 전달 여부
if(!empty($user_email)){
	$query = "SELECT * FROM userinfo where email = '$user_email'" ;
	//적정 쿼리 여부
	if($result=mysql_query($query))
	{
		//쿼리 결과 여부
		if(mysql_num_rows($result)==1){

			$query = "UPDATE userinfo SET passwd=PASSWORD('$more_complex_password') where email = '$user_email'" ;
			//적정 쿼리 여부
			if($result=mysql_query($query))
			{
			//이메일 전송 여부
				if($mail->send()) {
					$result_array['err']='0';
				} else {
					$result_array['err']='070';
				}

			}
			else
			{
				$result_array['err']='010';
			}
		}else{
			$result_array['err']='021';
		}
	}
	else
	{
		$result_array['err']='020';
	}
}else
{
	$result_array['err']='030';
}
$result_array['err_result']=get_err_result("set_new_password",$result_array['err']); 
$result_array = json_encode($result_array);
echo $result_array;


?>