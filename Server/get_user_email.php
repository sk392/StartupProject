<?php
// php.ini 파일에서 allow_url_fopen 값이 On으로 되어있어야 합니다.
// return_url 변수와 return_var 변수는 사용되지 않습니다.

   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();
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

// 아래 두 값은 사용되지 않습니다.
//$return_url = "";
//$return_var = "";


if(!empty($user_phone)){


 $query = "SELECT email FROM userinfo where phone='$user_phone' ORDER BY userinfo_id DESC limit 1" ; //list

	//결과 값을 데이터 이름으로 받아 배열에 저장한다.
 if($result=mysql_query($query))
 	{	$info = mysql_fetch_object($result);
 		$result_array = array();
 		$user_email = $info->email;
 		$msg = urlencode("[카모니] 아이디 찾기\n 해당 아이디는 [".$user_email."]입니다.");

 		$url = "http://link.smsceo.co.kr/sendsms_utf8.php?userkey=" . $userkey;
 		$url .= "&userid=" . $userid;
 		$url .= "&phone=" . $user_phone;
 		$url .= "&callback=" . $callback;
 		$url .= "&msg=" . $msg;


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
		}
		else
		{
			$result_array['err']='010';
		}
	}else{
		$result_array['err']= "030";
	}		
	$result_array = json_encode($result_array);
	echo $result_array;

	?>
