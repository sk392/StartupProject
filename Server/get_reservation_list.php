<?php 


  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();

//POST 형태로 보내온 id 및 type값을 입력 받는다
$sktoken=$_POST['sktoken'];
$type=$_POST['type'];
  $jwt_error='0';


  //결과 값을 받기 위한 배열
$result_array = array();

if(!empty($sktoken) &&(!empty($type)||$type==0)){

  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_reservation_list : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];

 //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
    if($type==0){
//대여자로서 예약 검색
      $query ="SELECT reservation_id,owner_id,carinfo.carinfo_id,model,model_year,start_date,end_date,carinfo_img_t,state
        ,reservation.userinfo_id,reservation.cost,reservation.delivery_cost,reservation.oneday_cost,reservation.late_cost,carinfo.isoneday, carinfo.isdelivery FROM reservation join carinfo on reservation.carinfo_id = carinfo.carinfo_id where reservation.userinfo_id = $user_id AND reservation.state NOT IN('3') ORDER BY reservation.start_date DESC";
    }else{
//차주로서 예약 검색
      $query ="SELECT reservation_id,owner_id,carinfo.carinfo_id,model,model_year,start_date,end_date,carinfo_img_t,state 
        ,reservation.userinfo_id,reservation.cost,reservation.delivery_cost,reservation.oneday_cost,reservation.late_cost,carinfo.isoneday, carinfo.isdelivery FROM reservation join carinfo on reservation.carinfo_id = carinfo.carinfo_id where reservation.owner_id =$user_id AND reservation.userinfo_id IS NOT NULL AND reservation.state NOT IN('3') ORDER BY reservation.start_date DESC";
    }

  //결과 값을 데이터 이름으로 받아 배열에 저장한다.
    if($result = mysql_query($query)){
      $result_array['err']='0';
      $result_array['cnt']=mysql_num_rows($result);
      while($info=mysql_fetch_object($result)){
        $result_array['ret'][] =$info;  
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
    $result_array['jwt_decoded_array']=$jwt_decoded_array;
if(empty($result_array['err_result'])){
  //jwt가 아닌 경우 에러 결과 값이 없다.
  $result_array['err_result']=get_err_result("get_reservation_list",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;
?>