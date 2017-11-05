<doctype html>
	<meta charset="utf-8">
	<html>
	<head>

		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
		<script type="text/javascript" src="https://service.iamport.kr/js/iamport.payment-x.y.z.js"></script>
		<title>차량 리뷰 삽입 테스트</title>

		<script>
			$(document).ready(function(){
				IMP.init('imp95578985');
s
				function pgTest(){
					IMP.request_pay({
						pg : 'html5_inicis',
						pay_method : 'card',
						merchant_uid : 'merchant_' + new Date().getTime(),
						name : '주문명:결제테스트',
						amount : 14000,
						buyer_email : 'iamport@siot.do',
						buyer_name : '구매자이름',
						buyer_tel : '010-1234-5678',
						buyer_addr : '서울특별시 강남구 삼성동',
						buyer_postcode : '123-456'
					}, function(rsp) {
						if ( rsp.success ) {
							var msg = '결제가 완료되었습니다.';
							msg += '고유ID : ' + rsp.imp_uid;
							msg += '상점 거래ID : ' + rsp.merchant_uid;
							msg += '결제 금액 : ' + rsp.paid_amount;
							msg += '카드 승인번호 : ' + rsp.apply_num;
						} else {
							var msg = '결제에 실패하였습니다.';
							msg += '에러내용 : ' + rsp.error_msg;
						}

						alert(msg);
					});

				}
				
			});
		</script>
	</head>
	<body>
		<form name="pgForm" onsubmit ="return pgTest">
			<input type="hidden" name="Amt" value="1000">
			<input type="hidden" name="BuyerName" value="홍길동">
			<input type="hidden" name="OrderName" value="결제테스트">
			<input type="submit" name="Submit" value="Submit">
		</form>
	</body>
	</html>