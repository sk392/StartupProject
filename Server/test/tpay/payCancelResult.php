<?
require_once dirname(__FILE__).'/TPAY.LIB.php';
?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
<link rel="stylesheet" href="css/sample.css" type="text/css" media="screen" />
<title>tPay ���ͳݰ���</title>
</head>
<body>
<?
//����(ȸ����) �������� EUC-KR�� ��� �ѱ۱�������
encoding("UTF-8", "EUC-KR", &$_POST); 

print_r($_POST);

$payMethod = $_POST['payMethod'];
$ediDate = $_POST['ediDate'];
$returnUrl = $_POST['returnUrl'];
$resultMsg = $_POST['resultMsg'];
$cancelDate = $_POST['cancelDate'];
$cancelTime = $_POST['cancelTime'];
$resultCd = $_POST['resultCd'];
$cancelNum = $_POST['cancelNum'];
$cancelAmt = $_POST['cancelAmt'];
$moid = $_POST['moid'];

$mid = "tpaytest0m";	//����id
$merchantKey = "VXFVMIZGqUJx29I/k52vMM8XG4hizkNfiapAkHHFxq0RwFzPit55D3J3sAeFSrLuOnLNVCIsXXkcBfYK1wv8kQ==";	//����Ű

$encryptor = new Encryptor($merchantKey, $ediDate);
$decAmt = $encryptor->decData($cancelAmt);
$decMoid = $encryptor->decData($moid);

$cancelAmt = "1004";	 //������û�ݾ�
$moid = "toid1234567890";

if($decAmt!=$cancelAmt || $decMoid!=$moid){
	echo "������ �����͸� �����Դϴ�.";
	exit;
}

//����(ȸ����) ���ó��

?>
</body>
</html>