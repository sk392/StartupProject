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
            $ch = curl_init();
         echo "transType=0
                                    &payMethod=CARD
                                    &goodsName=K5 2016
                                    &amt=1004
                                    &moid=latte3392
                                    &mallUserId=ttat
                                    &buyerName=라떼
                                    &buyerTel=01027287339
                                    &buyerEmail=latte@test.com
                                    &prdtExpDate=2017년 12월 31일 까지
                                    &mid=tpaytest0m
                                    &returnUrl=http://14.63.225.26/api/test/tpay/returnPay.php
                                    &cancelUrl=http://14.63.225.26/api/test/tpay/mainPay.php
                                    &vbankExpDate=".$vbankExpDate."
                                    &connType=2
                                    &appPrefix=ibWebTest
                                    &payType=1
                                    &ediDate=".$ediDate."
                                    &encryptData=".$encryptData."
                                    &browserType=
                                    &mallReserved=MallReserved";

            curl_setopt ($ch, CURLOPT_URL,"http://14.63.225.26:2780/api/test/tpay/TT.php"); //접속할 URL 주소
            curl_setopt ($ch, CURLOPT_SSL_VERIFYPEER, TRUE); // 인증서 체크같은데 true 시 안되는 경우가 많다.
            // default 값이 true 이기때문에 이부분을 조심 (https 접속시에 필요)
            curl_setopt ($ch, CURLOPT_SSLVERSION,3); // SSL 버젼 (https 접속시에 필요)
            curl_setopt ($ch, CURLOPT_HEADER, 0); // 헤더 출력 여부
            curl_setopt ($ch, CURLOPT_POST, 1); // Post Get 접속 여부
            curl_setopt ($ch, CURLOPT_POSTFIELDS, "transType=0"); // Post 값 Get 방식처럼적는다.
            
      curl_setopt ($ch, CURLOPT_TIMEOUT, 30); // TimeOut 값
            curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1); // 결과값을 받을것인지
            $result = curl_exec($ch);
            curl_close ($ch);
            ?>
