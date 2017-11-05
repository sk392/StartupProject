<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();


//POST 형태로 보내온 id 및 type값을 입력 받는다
$sktoken=$_POST['sktoken'];


  //결과 값을 받기 위한 배열
$result_array = array();
  $jwt_error='0';

if(!empty($sktoken)){


  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_owner_info : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
 //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
  $query = "SELECT name,owner_content,is_email,is_company,is_face,is_phone,ow_review_cnt,ow_review_score FROM userinfo where userinfo_id='$user_id'" ; //list

  if($result = mysql_query($query)){
  //결과 값을 데이터 이름으로 받아 배열에 저장한다.
    if($info=mysql_fetch_object($result)){
      $result_array['err']='0';
      $array = array();
  //차주 보유 차량 수
      $query = "SELECT * FROM carinfo where userinfo_id ='$user_id'";
      if($result = mysql_query($query)){ 
  $car_cnt = mysql_num_rows($result);//결과의 행 갯수를 통해 총 차량 수를 구한다.
  $array['car_cnt']=$car_cnt;
  $result_array['ret'] = array_merge((array)$info,(array)$array);

}else{
  $result_array['err']='011';

}

}else{
  $result_array['err']='010';
  
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
  $result_array['err_result']=get_err_result("get_owner_info",$result_array['err']);
}

$result_array = json_encode($result_array);
echo $result_array;
?>