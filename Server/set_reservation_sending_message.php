<?php
// php.ini 파일에서 allow_url_fopen 값이 On으로 되어있어야 합니다.
// return_url 변수와 return_var 변수는 사용되지 않습니다.

  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');

$user_type = $_POST['user_type'];
$car_model = $_POST['car_model'];
$rent_date = $_POST['rent_date'];
$rent_location = $_POST['rent_location'];
$cost_online = $_POST['cost_online'];
$cost_oneday = $_POST['cost_oneday'];
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
$subject = "[카모니]예약안내";
if($user_type=="0")
{
	//대여자인 경우 현장 준비물 및 원데이 보험료가 추가로 나온다.
	if($cost_oneday =="0"){
		$msg_orignal = $car_model."차량의 예약이 완료 되었습니다.\n\n * 변경사항이 있는 경우에는 반드시 차주에게 알려주세요!\n\n ㄴ 대여차량 : ".$car_model."\n ㄴ 대여기간 : ".$rent_date." \n ㄴ 대여장소 : ".$rent_location."\n ㄴ 결제 금액 : ".$cost_online."원\n ㄴ 현장준비물 : 면허증";

	}else{
		$msg_orignal = $car_model."차량의 예약이 완료 되었습니다.\n\n * 변경사항이 있는 경우에는 반드시 차주에게 알려주세요!\n\n ㄴ 대여차량 : ".$car_model."\n ㄴ 대여기간 : ".$rent_date." \n ㄴ 대여장소 : ".$rent_location."\n ㄴ 결제 금액 : ".$cost_online."원\n ㄴ 원데이보험료 : 약 ".$cost_oneday."원\n ㄴ 현장준비물 : \n   1.면허증\n   2.원데이보험 앱(The-K손해보험 에듀카) 설치\n   3.공인인증서가 포함된 휴대폰\n   4.원데이보험결제를 위한 본인명의신용카드(단, 카카오페이나 휴대폰 결제로 진행할 시 불필요)";

	}

	$msg = urlencode($msg_orignal);


}else{
	//차주의 경우
	$msg = urlencode($car_model."차량의 예약이 완료 되었습니다.\n\n * 변경사항이 있는 경우에는 반드시 이용 드라이버에게 알려주세요!\n\n ㄴ 대여차량 : ".$car_model."\n ㄴ 대여기간 : ".$rent_date." \n ㄴ 대여장소 : ".$rent_location."\n ㄴ 결제 금액 : ".$cost_online."원");
}
/*
ㄴ 대여차량 : 스파크
ㄴ 대여기간 : 1월 16일 오전 10시 ~ 1월 19일 오전 10시
ㄴ 대여장소 : 안암오거리
ㄴ 총 금액 : 85000원
ㄴ 할인금액 : 10000원
ㄴ 원데이보험료 : 약20000원
ㄴ 예약금 : 20000원
ㄴ 현장준비물 : 현금잔금 3만원, 원데이보험 앱 설치와 공인인증서복사 완료해주신 휴대폰, 원데이보험결제를 위한 본인명의신용카드(다른방법일 시 필요없습니다.), 면허증 */

// 아래 두 값은 사용되지 않습니다.
//$return_url = "";
//$return_var = "";

$url = "http://link.smsceo.co.kr/sendlms_utf8.php?userkey=" . $userkey;
$url .= "&userid=" . $userid;
$url .= "&phone=" . $user_phone;
$url .= "&callback=" . $callback;
$url .= "&msg=" . $msg;
$url .= "&subject=" . $subject;
$result_array = array();

if(!empty($car_model)&&!empty($rent_date)&&!empty($rent_location)&&!empty($cost_online)&&!empty($user_phone)){
	$result = array();
	$result = sendsms($url); // 결과 출력형식을 참고하세요.
	$result_array = json_encode($result);
	if($result['result_code'] == "1") // 전송성공
	{
		$result_array['err'] = "0";
		$result_array['sms_result']= $result['result_code']. $result['result_msg']. $result['total_count']. $result['succ_count']. $result['fail_count']. $result['money'];

	}
	else 
	{
		$result_array['err']= "060";
		$result_array['sms_result']= $result['result_code']. $result['result_msg'];
	}
}else{
	$result_array['err']= "030";

}		
$result_array = json_encode($result_array);
echo $result_array;

?>
