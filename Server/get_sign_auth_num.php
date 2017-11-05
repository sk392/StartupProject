<?php
// php.ini 파일에서 allow_url_fopen 값이 On으로 되어있어야 합니다.
// return_url 변수와 return_var 변수는 사용되지 않습니다.

$auth_num = $_POST['auth_num'];
$user_phone = $_POST['user_phone'];
function sendsms($url)
{
	$result = file_get_contents($url);
	$result = trim($result);
	parse_str($result, $result_var);
	return $result_var;
}

$userkey = "BDkMPwo8VWdVYFV6VGQEN1RzBm5RaAZnA3tcdw==";
$userid = "carmony";
$callback = "07088600113";
$msg = urlencode("[카모니] [".$auth_num."]\n 휴대폰본인인증 인증번호 입니다.");

// 아래 두 값은 사용되지 않습니다.
//$return_url = "";
//$return_var = "";

$url = "http://link.smsceo.co.kr/sendsms_utf8.php?userkey=" . $userkey;
$url .= "&userid=" . $userid;
$url .= "&phone=" . $user_phone;
$url .= "&callback=" . $callback;
$url .= "&msg=" . $msg;
$result_array = array();

if(!empty($auth_num) &&!empty($user_phone)){


	$result = array();
	$result = sendsms($url); // 결과 출력형식을 참고하세요.
	// print_r($result);	
	if($result[result_code] == "1") // 전송성공
	{
		$result_array['err'] = "0";
		$result_array['sms_result']= $result[result_code]. $result[result_msg]. $result[total_count]. $result[succ_count]. $result[fail_count]. $result[money];

	}
	else 
	{
		$result_array['err']= "060";
		$result_array['sms_result']= $result[result_code]. $result[result_msg];
	}
}else{
	$result_array['err']= "030";

}		
$result_array = json_encode($result_array);
echo $result_array;

?>
