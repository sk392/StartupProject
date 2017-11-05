<?
require_once dirname(__FILE__).'/TPAY.LIB.php';

$mid = "tpaytest0m";	//상점id
$merchantKey = "VXFVMIZGqUJx29I/k52vMM8XG4hizkNfiapAkHHFxq0RwFzPit55D3J3sAeFSrLuOnLNVCIsXXkcBfYK1wv8kQ==";	//상점키
$cancelAmt = "1004";	 //결제금액
$moid = "toid1234567890";

//$ediDate, $mid, $merchantKey, $amt    
$encryptor = new Encryptor($merchantKey);

$encryptData = $encryptor->encData($cancelAmt.$mid.$moid);
$ediDate = $encryptor->getEdiDate();	

$payActionUrl = "http://webtx.tpay.co.kr";
$payLocalUrl = "http://shop.tpay.co.kr";  //각 상점 도메인을 설정 하세요.  ex)http://shop.tpay.co.kr
?>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<link rel="stylesheet" href="css/sample.css" type="text/css" media="screen" />
<title>tPay 인터넷결제</title>
<script language="javascript">
<!--
function goCancelCard() {
	var formNm = document.tranMgr;
	
	// tid validation
	if(formNm.tid.value == "") {
		alert("tid를 확인하세요.");
		return false;
	} else if(formNm.tid.value.length > 30 || formNm.tid.value.length < 30) {
		alert("tid 길이를 확인하세요.");
		return false;
	}
	// 취소금액
	if(formNm.cancelAmt.value == "") {
		alert("금액을 입력하세요.");
		return false;
	} else if(formNm.cancelAmt.value.length > 12 ) {
		alert("금액 입력 길이 초과.");
		return false;
	}
	var PartialValue = "";
	// 부분취소여부 체크 - 신용카드, 계좌이체 부분취소 가능
	for(var idx = 0 ; idx < formNm.partialCancelCode.length ; idx++){
		if(formNm.partialCancelCode[idx].checked){
			PartialValue = formNm.partialCancelCode[idx].value;
			break;
		}
	}
	
	if(PartialValue == '1'){
		if(formNm.tid.value.substring(10,12) != '01' &&  formNm.tid.value.substring(10,12) != '02' &&  formNm.tid.value.substring(10,12) != '03'){
			alert("신용카드결제, 계좌이체, 가상계좌만 부분취소/부분환불이 가능합니다");
			return false;
		}
	}
	formNm.submit();
	return true;
}
-->
</script>
</head>
<body>
<form name="tranMgr" method="post" action="<?=$payActionUrl ?>/payCancel">
<input type="hidden" name="cc_ip" size="20" value="<?=$_SERVER['REMOTE_ADDR']?>">
<input type="hidden" name="ediDate" value="<?=$ediDate?>" />
<input type="hidden" name="encryptData" value="<?=$encryptData?>" />
	<div style="border-color: aqua; border: aqua ">
		<table cellspacing="1" border="0" cellpadding="0">
			<thead>
				<tr><td colspan="2">취소 상점 데모 프로그램</strong></td></tr>
			</thead>
			<tbody>
				<tr>
					<td>가맹점아이디(mid)</td>
					<td><input type="text" name="mid" maxlength="30" size="30" value="<?=$mid?>"></td>
				</tr>				
				<tr>
					<td>거래아이디(tid)</td>
					<td><input type="text" name="tid" maxlength="30" size="30" value=""></td>
				</tr>
				<tr>
					<td>상품주문번호(moid)</td>
					<td><input type="text" name="moid" maxlength="30" size="30" value="<?=$moid?>"></td>
				</tr>				
				<tr>
					<td>취소패스워드</td>
					<td><input type="password" name="cancelPw" size="20" value="" style="height:25px;"> * 데모시 미입력</td>
				</tr>				
				<tr>
					<td>취소금액</td>
					<td><input type="text" name="cancelAmt" size="20" value="<?=$cancelAmt?>"></td>
				</tr>				
				<tr>
					<td>취소사유</td>
					<td><input type="text" name="cancelMsg" size="20" value="고객요청"></td>
				</tr>				
				<tr>
					<td>부분취소 여부</td>
					<td><input type="radio" name="partialCancelCode" value="0" checked="checked" />전체취소 <input type="radio" name="partialCancelCode" value="1" />부분취소</td>
				</tr>		
				<tr>
					<td>취소결제 응답방식(html/json)</td>
					<td><input type="radio" name="dataType" value="html" checked="checked" />html<input type="radio" name="dataType" value="json" />json</td>
				</tr>					
				<tr>
				<td colspan="2" style="text-align:center; padding: 5px;"><input type="button" id="submitBtn" value="결제 취소(btn)" onclick="goCancelCard()" class="button blue medium"></td>
			</tr>
			</tbody>
		</table>
	</div>	
<input type="hidden" name="returnUrl" value="<?=$payLocalUrl?>/sample/payCancelResult.php" />
</form>

</body>
</html>