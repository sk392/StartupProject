

<?
require_once dirname(__FILE__).'/TPAY.LIB.php';

      $mid = "tpaytest0m";    //상점id
    $merchantKey = "VXFVMIZGqUJx29I/k52vMM8XG4hizkNfiapAkHHFxq0RwFzPit55D3J3sAeFSrLuOnLNVCIsXXkcBfYK1wv8kQ==";    //상점키
      $amt = "1004";     //결제금액
      $moid = "toid1234567890";
      
    //$ediDate, $mid, $merchantKey, $amt    
      $encryptor = new Encryptor($merchantKey);

      $encryptData = $encryptor->encData($amt.$mid.$moid);
      $ediDate = $encryptor->getEdiDate();      
      $vbankExpDate = $encryptor->getVBankExpDate();    

      $payActionUrl = "https://mtx.tpay.co.kr";
      $payLocalUrl = "http://14.63.225.26/api/test/tpay";
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
            <style type="text/css">

                  body{
                        font-family:나눔고딕, 돋움, Tahoma, Geneva, sans-serif;
                        font-size:13px;
                        padding:0;
                        margin:0;
                        background: #F0F1F3;
                        background-repeat: repeat-x;  
                        width:99%;

                        a{text-decoration:none;}
                        a:link            {color:#000; cursor:pointer;} 
                        a:visited   {color:#000; cursor:pointer;} 
                        a:hover           {color:#c8c8c8; cursor:pointer;} 
                        a:active    {color:#000; cursor:pointer;}
                  }

                  .TitleBar{font-size:17px; color:#000; font-weight:bold;}

                  @-webkit-keyframes zoom {
                       from {
                           opacity: 0.1;
                           font-size: 100%;
                     }
                     to {
                           opacity: 1.0;
                           font-size: 130%;
                     }
               }

               .selectBar{
                  padding:6px 3px;
            }
            .selectBar label{
                  font-size:14px;
                  padding:3px;
                  color:#374c83;
                  font-weight:bold;
            }
            .selectBar span{
                  font-size:14px;
                  padding:3px;
            }
            .selectBar input{
                  font-family: 나눔고딕, Tahoma, Geneva, sans-serif;
                  font-size:14px;
                  padding:6px;
                  margin:0px;
                  text-align:left;
                  border:1px solid #ccc;
                  /* 테두리 곡선처리 관련 */
                  border-radius:5px;
                  -webkit-border-radius:5px;
                  -moz-border-radius:5px;
                  -o-border-radius:5px;
                  background:#EEEEEE;
                  /* 그림자 */
                  box-shadow:inset 0 0 5px #ccc; 
                  -moz-box-shadow:inset 0 0 5px #ccc; 
                  -webkit-box-shadow:inset 0 0 5px #ccc;
            }
            .selectBar .listInput{
                  width:90px;
                  text-align:left;
            }

            .selectBar .largeInput{
                  width:95%;
                  text-align:left;
            }     

            .selectBar div {
                  float:right;
                  padding:0;
                  margin:0;
            }

            .selectBar select{
                  width:140px; 
                  font-size:15px;
            }

            .selectList ul{
                  list-style:none;
                  margin:5px; 
                  padding:0; 
            }
      </style>

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
            window.onload = function () {
                  submitForm();
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
                                          <option value="">[선택]</option>
                                          <option value="CARD">[신용카드]</option>
                                          <option value="BANK">[계좌이체]</option>
                                          <option value="VBANK">[가상계좌]</option>
                                          <option value="CELLPHONE">[휴대폰결제]</option>
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
                                    <input type="text" name="goodsName" value="test">
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
                                    <input type="text" name="mallUserId" value="t_id">
                              </li>
                        </ul>
                        <ul>
                              <li class="selectBar">
                                    <label for="">구매자명(*)</label><br>
                                    <input type="text" name="buyerName" value="t_구매자명">
                              </li>
                        </ul> 
                        <ul>
                              <li class="selectBar">
                                    <label for="">구매자연락처((-)없이 입력)</label><br>
                                    <input type="tel" pattern="[0-9]*" maxlength="11" name="buyerTel" value="0212345678">
                              </li>
                        </ul>
                        <ul>
                              <li class="selectBar">
                                    <label for="">구매자메일주소(*)</label><br>
                                    <input type="text" name="buyerEmail" value="test@test.com">
                              </li>
                        </ul>
                        <ul>
                              <li class="selectBar">
                                    <label for="">제공기간</label><br>
                                    <input type="text" name="prdtExpDate" value="2015년 12월 31일 까지">
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
                                    <input type="text" name="returnUrl" class="largeInput" value="carmonyapp://webview_test" readonly="readonly">
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
                                    <input type="text" name="appPrefix" value="carmonyapp://webview_test">
                              </li>
                        </ul>
                  </div>

                  <input type="hidden" name="payType" value="1"><!-- 결제형태 -->
                  <input type="hidden" name="ediDate" value="<?echo $ediDate?>"><!-- 결제일 -->
                  <input type="hidden" name="encryptData" value="<?echo $encryptData?>"><!-- 암호화 검증 데이터 -->
                  <input type="hidden" name="userIp"  value="<?echo $request.getRemoteAddr()?>"><!-- User IP Address -->
                  <input type="hidden" name="browserType" id="browserType" value="">
                  <input type="hidden" name="mallReserved" value="MallReserved">
            </form>
      </div>
</body>
</html>