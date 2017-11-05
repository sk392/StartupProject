<?
require_once dirname(__FILE__).'/TPAY.LIB.php';
$parentDir = dirname(__DIR__ . '..');  
	// 상위 폴더의 a.php파일을 include
include_once($parentDir . './lib/json.php');
	  // mysql을 사용하기위해 lib 참조
include_once($parentDir . './lib/api_library.php');
include_once($parentDir . './lib/jwt.php');
connect();

	$mid = "id_dummy";	//상점id
    $merchantKey = "key_dummy";	//상점키
   	//$amt = $_POST['amt'];	 //결제금액
 	$amt = "1009";	 //결제금액
	$moid = $_POST['reservation_id']; //예약 번호 
	$goodsName = $_POST['goodsName'];
	$buyerEmail = $_POST['buyerEmail'];
	$buyerName = $_POST['buyerName'];
	$tel = $_POST['tel'];
	$tid = $_POST['tid'];//결제자 userinfo_id
    //$ediDate, $mid, $merchantKey, $amt    
	$encryptor = new Encryptor($merchantKey);

	$encryptData = $encryptor->encData($amt.$mid.$moid);
	$ediDate = $encryptor->getEdiDate();	
	$vbankExpDate = $encryptor->getVBankExpDate();	

	$payActionUrl = "https://mtx.tpay.co.kr";
	$payLocalUrl = "http://111.111.111.111:80/";
	?>
	<!DOCTYPE html>
	<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="apple-mobile-web-app-status-bar-style" content="black" />
		<link rel="apple-touch-icon" href=""/>
		<link rel="apple-touch-startup-image" href="" />
		<script type="text/javascript">
			function changeAmt(){
				frm = document.transMgr;
				frm.action = "mainPay.php";
				frm.target = "_self";
				frm.submit();
			}

			function submitForm(){

				frm = document.transMgr;

				if(frm.transType[1].checked){

					if(frm.payMethod.value != "CARD"){
						alert("에스크로에서 지원하지 않는 결제수단입니다.");
						return;
					}else{
						frm.action = "https://mtx.tpay.co.kr/webTxInit";
						frm.submit();
					}

				}else{
					frm.action = "https://mtx.tpay.co.kr/webTxInit";
					frm.submit();
				}
			}
			window.onload = function () {
				<?
					$query = "UPDATE reservation SET state=2 WHERE reservation_id='$moid'";

					if(mysql_query($query)) {
						?>
						submitForm();
						<?

					}

				?>
			}

		</script>
		<title>tPay::인터넷결제</title>
	</head>
	<body>
		<div style="display:none">

			<form id="transMgr" name="transMgr" method="post">
				<p class="TitleBar">결제 상점 데모 프로그램</p>
				<hr>
				<div class="selectList">
					<ul>
						<li class="selectBar">
							<label for="">결제수단</label>
							<select name="payMethod" id="payMethod">
								<option value="CARD">[신용카드]</option>
							</select>
							<input type="button" id="submitBtn" value="결제 전송" onclick="submitForm();">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">결제타입(*)</label>
							<label>일반</label><input type="radio" id="transTypeN" name="transType" value="0" checked="checked">
							<label>에스크로</label><input type="radio" id="transTypeE" name="transType" value="1">				
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">상품명(*)</label><br>
							<input type="text" name="goodsName" value="<?echo $goodsName?>">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">상품가격(*)</label><br>
							<input type="tel" pattern="[0-9]*" name="amt" value="<?echo $amt?>"> 원
							<!-- <input type="button" value="금액 변경" onclick="changeAmt();" /> -->
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">상품주문번호(*)</label><br>
							<input type="text" name="moid" value="<?echo $moid?>">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">회원사고객ID</label><br>
							<input type="text" name="mallUserId" value="<?echo $tid?>">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">결제 예약 번호</label><br>
							<input type="text" name="tid" value="<?echo $tid?>">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">구매자명(*)</label><br>
							<input type="text" name="buyerName" value="<?echo $buyerName?>">
						</li>
					</ul>	
					<ul>
						<li class="selectBar">
							<label for="">구매자연락처((-)없이 입력)</label><br>
							<input type="tel" pattern="[0-9]*" maxlength="11" name="buyerTel" value="<?echo $tel?>">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">구매자메일주소(*)</label><br>
							<input type="text" name="buyerEmail" value="<?echo $buyerEmail?>">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">제공기간</label><br>
							<input type="text" name="prdtExpDate" value="">
						</li>
					</ul>
					<hr>
					<ul>
						<li class="selectBar">
							<label for="">회원사아이디(*)</label><br>
							<input type="text" name="mid" value="<?echo $mid ?>" readonly="readonly">
						</li>
					</ul>		
					<ul>
						<li class="selectBar">
							<label for="">결제결과 전송 URL(*)</label><br>
							<input type="text" name="returnUrl" class="largeInput" value="<?echo $payLocalUrl?>/returnPay.php" readonly="readonly">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">결제취소 URL(*)</label><br>
							<input type="text" name="cancelUrl" class="largeInput" value="<?echo $payLocalUrl?>/mainPay.php" readonly="readonly">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">가상계좌입금기한(*)</label><br>
							<input type="text" name="vbankExpDate" value="<?echo $vbankExpDate?>" readonly="readonly">
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">접속방식(*)</label><br>
							<select name="connType" id="connType">
								<option value="2">App(WebViewController)</option>
							</select>
						</li>
					</ul>
					<ul>
						<li class="selectBar">
							<label for="">앱 스키마</label><br>
							<input type="text" name="appPrefix" value="ibWebTest">
						</li>
					</ul>
				</div>

				<input type="hidden" name="payType" value="1"><!-- 결제형태 -->
				<input type="hidden" name="ediDate"	value="<?echo $ediDate?>"><!-- 결제일 -->
				<input type="hidden" name="encryptData" value="<?echo $encryptData?>"><!-- 암호화 검증 데이터 -->
				<input type="hidden" name="userIp"	value="<?echo $request.getRemoteAddr()?>"><!-- User IP Address -->
				<input type="hidden" name="browserType" id="browserType" value="">
				<input type="hidden" name="mallReserved" value="MallReserved">
			</form>
		</div>
	</body>
	</html>