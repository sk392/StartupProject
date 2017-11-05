<!doctype html>
<html lang="ko">
<head>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
	<title>tPay Smart</title>
	<link rel="stylesheet" type="text/css" href="/css/skinB/common.css">
	<!--[if IE]>
	<script type="text/javascript" src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->		
	<script type="text/javascript" src="../js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="../js/jquery.validate.js"></script>
	<script type="text/javascript" src="../js/common.js"></script>
	<script type="text/javascript" src="../js/json2.js"></script>
	<script type="text/javascript" src="../js/mnwebtx.cellphone.js"></script>
<script type="text/javascript">
	function changeAmt(){
		frm = document.transMgr;
		frm.action = "mainPay.jsp";
		frm.target = "_self";
		frm.submit();
	}

	function submitForm(){
		frm = document.transMgr;
		
		if(frm.transType[1].checked){
			
			if(frm.payMethod.value != "CARD" && frm.payMethod.value != "BANK" && frm.payMethod.value != "VBANK"){
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
	
	function appBrowerCheck(){
				
		
	}
</script>
<title>tPay 인터넷결제</title>
</head>
<body onload="appBrowerCheck();">
<form id="transMgr" name="transMgr" method="post">
<div id="wrap">
<!-- header -->
	<header id="header">
		<h1 class="tit"><span>결제 상점 데모 프로그램</span></h1>
		<div class="shop_name">(주)제이티넷</div>
	</header>
	
	<!-- container -->
	<section id="container">
		<!-- 결제할상품정보 -->
		<div class="product_box" style="border:0px">
			<table summary="상품명 및 결제금액 리스트입니다">
				<caption>상품명 및 결제금액 리스트</caption>
				<colgroup>
					<col style="width:25%;" />
					<col style="width:auto;" />
				</colgroup>
				<tbody>	
					<tr>
						<th class="shop_logo" style="border:0px"><img src="https://mms.tpay.co.kr/mer_file/mer_image/tpaytest0m/tPayPG_for_bright_bg.png" /></th>
						<td class="product_info">
							<dl>
								<dd><strong class="pointclr">(주)제이티넷 결제 서비스</strong></dd>
							</dl>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		
		<div style="padding-top: 20px"></div>
		<div class="product_box">
			<table border="1" summary="상품명 및 결제금액 리스트입니다">
				<caption>상품명 및 결제금액 리스트</caption>
				<colgroup>
					<col style="width:40%;" />
					<col style="width:auto;" />
				</colgroup>
				<tbody>	
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="right"><strong>결제수단</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<select name="payMethod" id="payMethod">
										<option value="">[선택]</option>
										<option value="CARD">[신용카드]</option>
										<option value="BANK">[계좌이체]</option>
										<option value="VBANK">[가상계좌]</option>
										<option value="CELLPHONE">[휴대폰결제]</option>
										<option value="GLOBAL">[글로벌페이]</option>
<!-- 										<option value="GLOBAL">[텐페이]</option>
 -->									</select>
								</dt>
							</dl>
						</td>
					</tr>
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>결제타입(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<label>일반</label><input type="radio" id="transTypeN" name="transType" value="0" checked="checked">
									<label>에스크로</label><input type="radio" id="transTypeE" name="transType" value="1">
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>스킨타입</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<select name="skinType" id="skinType">
										<option value="skinA">skinA</option>
										<option value="skinB" selected>skinB</option>
									</select>									

								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>접속방식(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<select name="connType" id="connType">
										<option value="0">Web(M-browser)</option>
										<option value="1" >App(WebViewController)</option>
									</select>		
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>상품명(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="goodsName" value="t_상품명" style="width: 100px">								
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>상품가격(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="tel" pattern="[0-9]*" name="amt" value="1004" style="width: 100px"> 원						

								</dt>
							</dl>
						</td>
					</tr>
					<tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>결제요청통화</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<select name="payCurrency" id="payCurrency">
										<option value="KRW">KRW</option>
										<option value="USD">USD</option>
										
									</select>					

								</dt>
							</dl>
						</td>
					</tr>
					
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>공급가액</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="tel" pattern="[0-9]*" name="supplyAmt" value="" style="width: 100px"> 원						

								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>부가세</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="tel" pattern="[0-9]*" name="vat" value="" style="width: 100px"> 원						

								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>봉사료</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="tel" pattern="[0-9]*" name="svsAmt" value="" style="width: 100px"> 원						

								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>상품주문번호(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="moid" value="test_20170239" style="width: 100px">					

								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>회원사고객ID</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="mallUserId" value="t_id" style="width: 100px">			
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>구매자명(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="buyerName" value="t_구매자명" style="width: 100px"> 		
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>구매자연락처</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="tel" pattern="[0-9]*" maxlength="11" name="buyerTel" value="0212345678" style="width: 100px">(-)생략	
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>구매자이메일(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="buyerEmail" value="test@test.com" style="width: 100px">	
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>회원사아이디(*)</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="mid" value="tpaytest0m" readonly="readonly" style="width: 100px">	
								</dt>
							</dl>
						</td>
					</tr>
					<tr>
						<td  class="product_info shop_logo" style="text-align: left">
							<dl>
								<dt style="left"><strong>tx 사용</strong></dt>
							</dl>
						</td>
						<td class="product_info">
							<dl>
								<dt style="right">
									<select name="socketYn" id="socketYn">
										<option value="N">미사용</option>
										<option value="Y">사용</option>
										
									</select>		
								</dt>
							</dl>
						</td>						
					</tr>
					<tr>
						<td class="product_info shop_logo" style="text-align: left">
							<dl>	
								<dt style="left"><strong>소켓리턴URL</strong></dt>
							</dl>
						</td>
						
						<td class="product_info">
							<dl>
								<dt style="right">
									<input type="text" name="socketReturnURL" value="http://shop.tpay.co.kr/sampleJSP/txTransReal.jsp" readonly="readonly" style="width: 200px">	
								</dt>
							</dl>
						</td>
					</tr>
					
					<tr>
						<td  class="product_info shop_logo" style="text-align: left">
							<dl>
								<dt style="left"> <strong>언어선택</strong></dt>
							</dl>
						</td>
						<td class="product_info">
							<dl>
								<dt style="right">
									<select name="storeGlobalPayLang" id="storeGlobalPayLang">
										<option value="">선택안함</option>
										<option value="kr">한국어</option>
										<option value="en">영어</option>
										<option value="cn">중국어</option>
										
									</select>	
								</dt>
							</dl>
						</td>						
					</tr>
					
					
					
				</tbody>
			</table>
		</div>
		
		<br/>
		<div style="text-align: center; padding-top: 10px">
			<a id="getOtpBtn" onclick="submitForm(); return false;"><img src="../images/skinB/btn_pay.png" alt="결제" height="30px"/></a>
		</div>
	</section>
	<!-- //container -->
	
</div>

<!-- 	<p class="TitleBar">결제 상점 데모 프로그램</p> -->
<!-- 	<hr> -->
<!-- 	<div class="selectList"> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">결제수단</label> -->
<!-- 				<select name="payMethod" id="payMethod"> -->
<!-- 					<option value="">[선택]</option> -->
<!-- 					<option value="CARD">[신용카드]</option> -->
<!-- 					<option value="BANK">[계좌이체]</option> -->
<!-- 					<option value="VBANK">[가상계좌]</option> -->
<!-- 					<option value="CELLPHONE">[휴대폰결제]</option> -->
<!-- 				</select> -->
<!-- 				<input type="button" id="submitBtn" value="결제 전송" onclick="submitForm();"> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">결제타입(*)</label> -->
<!-- 				<label>일반</label><input type="radio" id="transTypeN" name="transType" value="0" checked="checked"> -->
<!-- 				<label>에스크로</label><input type="radio" id="transTypeE" name="transType" value="1">				 -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">스킨타입</label> -->
<!-- 				<select name="skinType" id="skinType"> -->
<!-- 					<option value="skinA">skinA</option> -->
<!-- 					<option value="skinB" selected>skinB</option> -->
<!-- 				</select> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">접속방식(*)</label><br> -->
<!-- 				<select name="connType" id="connType"> -->
<!-- 					<option value="0">Web(M-browser)</option> -->
<!-- 					<option value="1">App(WebViewController)</option> -->
<!-- 				</select> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">상품명(*)</label><br> -->
<!-- 				<input type="text" name="goodsName" value="t_상품명"> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">상품가격(*)</label><br> -->

<!-- 				<input type="button" value="금액 변경" onclick="changeAmt();" /> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">상품주문번호(*)</label><br> -->

<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">회원사고객ID</label><br> -->
<!-- 				<input type="text" name="mallUserId" value="t_id"> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">구매자명(*)</label><br> -->
<!-- 				<input type="text" name="buyerName" value="t_구매자명"> -->
<!-- 			</li> -->
<!-- 		</ul>	 -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">구매자연락처((-)없이 입력)</label><br> -->
<!-- 				<input type="tel" pattern="[0-9]*" maxlength="11" name="buyerTel" value="0212345678"> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">구매자메일주소(*)</label><br> -->
<!-- 				<input type="text" name="buyerEmail" value="test@test.com"> -->
<!-- 			</li> -->
<!-- 		</ul> -->
<!-- 		<hr> -->
<!-- 		<ul> -->
<!-- 			<li class="selectBar"> -->
<!-- 				<label for="">회원사아이디(*)</label><br> -->

<!-- 			</li> -->
<!-- 		</ul>	 -->
<!-- 	</div> -->
	
	<input type="hidden" name="vbankExpDate" value="20170209" readonly="readonly">
	<input type="hidden" name="returnUrl" class="largeInput" value="<? echo dirname(__FILE__).'/returnPay.php'?>" readonly="readonly">
	<input type="hidden" name="cancelUrl" class="largeInput" value="https://mtx.tpay.co.kr/mainPay.jsp" readonly="readonly">
	
	<input type="hidden" name="prdtExpDate" value="2015년 12월 31일 까지">
	<input type="hidden" name="resultYn" value="Y">
	
	<input type="hidden" name="payType" value="1"><!-- 결제형태 -->
	<input type="hidden" name="ediDate"	value="20170208114835"><!-- 결제일 -->
	<input type="hidden" name="encryptData" value="s6JciLIgtUGvYpnCk4A4Lz2BiNEHvpGSxd9MylXHZlQ="><!-- 암호화 검증 데이터 -->
	<input type="hidden" name="userIp"	value="220.75.204.95"><!-- User IP Address -->
	<input type="hidden" name="browserType" id="browserType" value=""><!-- SmartPhone Payment Gateway -->
	<input type="hidden" name="mallReserved" value="MallReserved">
	
	<input type="hidden" name="mallIp" value="172.16.10.25">
</form>
</body>
</html>