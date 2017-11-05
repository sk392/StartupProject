<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();
//배열뒤에 배열 붙이고, 그걸로 
//POST 형태로 보내온 id 및 type값을 입력 받는다
$sktoken=$_POST['sktoken'];
$reservation_id=$_POST['reservation_id'];
$jwt_error='0';

  //결과 값을 받기 위한 배열
$result_array = array();


if(!empty($sktoken) &&!empty($reservation_id)){


  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_reservation : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
    //carinfo 테이블에 있는 차량 정보를 추출하기 위한 query문
    $res_query = "SELECT state,start_date FROM reservation where reservation_id='$reservation_id'" ; //list

    //결과 값을 데이터 이름으로 받아 배열에 저장한다.
    if($res_result = mysql_query($res_query)){
      $res_info=mysql_fetch_object($res_result);
      $result_array['ret'] = $res_info;
      $result_array['err']='0';
    }else{

      $result_array['err']='010';  
    }

  }else{
    $result_array['err']=$jwt_error;
    $result_array['err_result']=$jwt_error_result;
  }

}else{
  //전달된 변수 값 에러
  $result_array['err']='030';
}
if(empty($result_array['err_result'])){
  //jwt가 아닌 경우 에러 결과 값이 없다.
  $result_array['err_result']=get_err_result("get_reservation_state",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array."<br>";

?>