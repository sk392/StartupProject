<?
require_once dirname(__FILE__).'/TPAY.LIB.php';

$mid = "tpaytest0m";	//����id
$merchantKey = "VXFVMIZGqUJx29I/k52vMM8XG4hizkNfiapAkHHFxq0RwFzPit55D3J3sAeFSrLuOnLNVCIsXXkcBfYK1wv8kQ==";	//����Ű
$cancelAmt = "1004";	 //�����ݾ�
$moid = "toid1234567890";

//$ediDate, $mid, $merchantKey, $amt    
$encryptor = new Encryptor($merchantKey);

$encryptData = $encryptor->encData($cancelAmt.$mid.$moid);
$ediDate = $encryptor->getEdiDate();	

$payActionUrl = "http://webtx.tpay.co.kr";
$payLocalUrl = "http://shop.tpay.co.kr";  //�� ���� �������� ���� �ϼ���.  ex)http://shop.tpay.co.kr
?>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<link rel="stylesheet" href="css/sample.css" type="text/css" media="screen" />
<title>tPay ���ͳݰ���</title>
<script language="javascript">
<!--
function goCancelCard() {
	var formNm = document.tranMgr;
	
	// tid validation
	if(formNm.tid.value == "") {
		alert("tid�� Ȯ���ϼ���.");
		return false;
	} else if(formNm.tid.value.length > 30 || formNm.tid.value.length < 30) {
		alert("tid ���̸� Ȯ���ϼ���.");
		return false;
	}
	// ��ұݾ�
	if(formNm.cancelAmt.value == "") {
		alert("�ݾ��� �Է��ϼ���.");
		return false;
	} else if(formNm.cancelAmt.value.length > 12 ) {
		alert("�ݾ� �Է� ���� �ʰ�.");
		return false;
	}
	var PartialValue = "";
	// �κ���ҿ��� üũ - �ſ�ī��, ������ü �κ���� ����
	for(var idx = 0 ; idx < formNm.partialCancelCode.length ; idx++){
		if(formNm.partialCancelCode[idx].checked){
			PartialValue = formNm.partialCancelCode[idx].value;
			break;
		}
	}
	
	if(PartialValue == '1'){
		if(formNm.tid.value.substring(10,12) != '01' &&  formNm.tid.value.substring(10,12) != '02' &&  formNm.tid.value.substring(10,12) != '03'){
			alert("�ſ�ī�����, ������ü, ������¸� �κ����/�κ�ȯ���� �����մϴ�");
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
				<tr><td colspan="2">��� ���� ���� ���α׷�</strong></td></tr>
			</thead>
			<tbody>
				<tr>
					<td>���������̵�(mid)</td>
					<td><input type="text" name="mid" maxlength="30" size="30" value="<?=$mid?>"></td>
				</tr>				
				<tr>
					<td>�ŷ����̵�(tid)</td>
					<td><input type="text" name="tid" maxlength="30" size="30" value=""></td>
				</tr>
				<tr>
					<td>��ǰ�ֹ���ȣ(moid)</td>
					<td><input type="text" name="moid" maxlength="30" size="30" value="<?=$moid?>"></td>
				</tr>				
				<tr>
					<td>����н�����</td>
					<td><input type="password" name="cancelPw" size="20" value="" style="height:25px;"> * ����� ���Է�</td>
				</tr>				
				<tr>
					<td>��ұݾ�</td>
					<td><input type="text" name="cancelAmt" size="20" value="<?=$cancelAmt?>"></td>
				</tr>				
				<tr>
					<td>��һ���</td>
					<td><input type="text" name="cancelMsg" size="20" value="����û"></td>
				</tr>				
				<tr>
					<td>�κ���� ����</td>
					<td><input type="radio" name="partialCancelCode" value="0" checked="checked" />��ü��� <input type="radio" name="partialCancelCode" value="1" />�κ����</td>
				</tr>		
				<tr>
					<td>��Ұ��� ������(html/json)</td>
					<td><input type="radio" name="dataType" value="html" checked="checked" />html<input type="radio" name="dataType" value="json" />json</td>
				</tr>					
				<tr>
				<td colspan="2" style="text-align:center; padding: 5px;"><input type="button" id="submitBtn" value="���� ���(btn)" onclick="goCancelCard()" class="button blue medium"></td>
			</tr>
			</tbody>
		</table>
	</div>	
<input type="hidden" name="returnUrl" value="<?=$payLocalUrl?>/sample/payCancelResult.php" />
</form>

</body>
</html>