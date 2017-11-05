<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

$sktoken=$_POST['sktoken'];


  //결과 값을 받기 위한 배열
$result_array = array();

$jwt_error='0';

if(!empty($sktoken)){

  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "set_reservation : ".$e->getMessage();
  }

                //jwt 에러가 0나, 052경우 토큰을 업데이트 시켜준다.
  if(!strcmp($jwt_error,"0") || !strcmp($jwt_error,"52")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];

    $user_id=$jwt_data_array['user_id'];
    $tokenId = base64_encode($token_id);
    $issuedAt = time();
    $notBefore = $issuedAt;
    $expire = $notBefore + 60 * 60 * 24 * 14;// expire되면 안에 데이터 날라가니까 30일로 설정
    $server_no = 1;

    $data = array(
      'iat' => $issuedAt,
      'jti' => $tokenId,
      'iss' => $serverName,
      'nbf' => $notBefore,
      'exp' => $expire,
      'data' => array
      ( 'user_id' => $user_id,
        'server_no' => $server_no)
      );

    if($jwt = JWT::encode($data, $secret_key)){

      $result_array['err']='0';
      $result_array['jwt_sktoken'] = $jwt;
    }
    else{
            //JWT 생성에러
      $result_array['err']='040';
    }

  }else{
    $result_array['err']=$jwt_error;
    $result_array['err_result']=$jwt_error_result;
  }

}
else{
  //전달된 변수 값 에러
  $result_array['err']='030';
}
if(empty($result_array['err_result'])){
  //jwt가 아닌 경우 에러 결과 값이 없다.
  $result_array['err_result']=get_err_result("set_reservation",$result_array['err']);
}
$result_array['user_id'] = $user_id;
$result_array = json_encode($result_array);
echo $result_array;

