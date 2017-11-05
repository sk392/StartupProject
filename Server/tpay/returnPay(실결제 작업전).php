<?
require_once dirname(__FILE__).'/TPAY.LIB.php';
$parentDir = dirname(__DIR__ . '..');  
	// 상위 폴더의 a.php파일을 include
include_once($parentDir . './lib/json.php');
	  // mysql을 사용하기위해 lib 참조
include_once($parentDir . './lib/api_library.php');
include_once($parentDir . './lib/jwt.php');
connect();
		//tpay smart 에서 받은 결과값들é
$payMethod = $_POST['payMethod'];
$mid = $_POST['mid'];
$tid = $_POST['tid'];
$mallUserId = $_POST['mallUserId'];
$amt = $_POST['amt'];
$buyerName = $_POST['buyerName'];
$buyerTel = $_POST['buyerTel'];
$buyerEmail = $_POST['buyerEmail'];
$mallReserved = $_POST['mallReserved'];
$goodsName = $_POST['goodsName'];
$moid = $_POST['moid'];
$authDate = $_POST['authDate'];
$authCode = $_POST['authCode'];
$fnCd = $_POST['fnCd'];
$fnName = $_POST['fnName'];
$resultCd = $_POST['resultCd'];
$resultMsg = $_POST['resultMsg'];
$errorCd = $_POST['errorCd'];
$errorMsg = $_POST['errorMsg'];
$vbankNum = $_POST['vbankNum'];
$vbankExpDate = $_POST['vbankExpDate'];
$ediDate = $_POST['ediDate'];

	//회원사 DB에 저장되어있던 값
	$amtDb = "1010";//금액
	//$moidDb = "210203";//moid
	$mKey = "VXFVMIZGqUJx29I/k52vMM8XG4hizkNfiapAkHHFxq0RwFzPit55D3J3sAeFSrLuOnLNVCIsXXkcBfYK1wv8kQ==";//상점키
	
	$encryptor = new Encryptor($mKey, $ediDate);
	$decAmt = $encryptor->decData($amt);
	$decMoid = $encryptor->decData($moid);
	//에러코드 정상인지 체크.
	//3001 카드 결제 성공
	//4000 계좌이체 성공
	//A000 휴대폰 결제 처리 성공.
	//4110 가상 계좌 입금 성공.
	if($resultCd =='3001' || $resultCd =='4000' || $resultCd =='A000'|| $resultCd =='4110'){
		// 위변조 테스트.
	// moid가 변조되지 않았는지만 체크한다.
		$query = "SELECT reservation_id FROM reservation WHERE reservation_id='$decMoid' AND state=2";

		if($result = mysql_query($query)){
			if(mysql_num_rows($result)==1){
		    	//결제 결과 수신이 정상적이다.
				$query = "UPDATE reservation SET state=1 WHERE  reservation_id='$decMoid'";
		    	//결제가 완료되었다는 쿼리 셋.

				if($result = mysql_query($query)){
					ResultConfirm::send($tid, "000");
		        	//정상 결과.
				}
			}else{
		    	//위변조 데이터 오류 입니다.
			}
		}

	}else if($resultCd =='4100'){
		//가상 계좌 발급 성공 시엔 아무 것도 하지 않는다.

	}else{
		//그 외에 에러가 떴을 경우엔 reservation의 state 값을 2(결제 신청)에서 0(예약 신청)으로 되돌려 놓는다.
		$query = "UPDATE reservation SET state=0 WHERE  reservation_id='$decMoid'";
		    	//결제가 에러났으니 결제 신청 상태를 신청 전으로 되돌린다.

		if($result = mysql_query($query)){
			ResultConfirm::send($tid, "999");
		        	//정상 결과.
		}


	}

	//결제 완료처리가 되지 않고, 결제가 되었을 경우 를 위한 결제 리퀘스트를 전부 저장한다.

	$query = "INSERT INTO payrequest(payMethod,mid,tid,mallUserId,amt,buyerName,buyerTel,buyerEmail,mallReserved,goodsName,moid,authDate,authCode,fnCd,fnName,resultCd,resultMsg,errorCd,errorMsg,vbankNum,vbankExpDate,ediDate,decAmt,decMoid) VALUES ('$payMethod','$mid','$tid','$mallUserId','$amt','$buyerName','$buyerTel','$buyerEmail','$mallReserved','$goodsName','$moid','$authDate','$authCode','$fnCd','$fnName','$resultCd','$resultMsg','$errorCd','$errorMsg','$vbankNum','$vbankExpDate','$ediDate','$decAmt','$decMoid')";
	
	mysql_query($query);

	//기존 위변조 처리 루틴.
	/*if( $decAmt!=$amtDb  || $decMoid!=$moid ){
		echo "위변조 데이터를 오류입니다.";
		die("결제가 실패하였습니다. 다시 시도해주세요.");
	}else{
		//결제결과 수신 여부 알림
		ResultConfirm::send($tid, "000");
		//DB처리 
	}*/
	?>
	<!--브라우져에서 앱켜기. host = webview_test, scheme = carmonyapp package = com.carmony.sk392.kr.webviewtest -->
<!--
<a href="intent://webview_test#Intent;scheme=carmonyapp;package=com.carmony.sk392.kr.webviewtest;end">Open your Activity directly (just open your Activity, without a choosing dialog). </a>
-->
