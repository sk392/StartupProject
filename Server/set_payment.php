<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();

//POST 형태로 보내온 정보를 받아온다.
$sktoken=$_POST['sktoken'];
$reservation_id=$_POST['reservation_id'];
$coupon_id=$_POST['coupon_id'];
$usercoupon_id=$_POST['usercoupon_id'];
$owner_id=$_POST['owner_id'];
$origin_cost =$_POST['origin_cost'];
$finally_cost =$_POST['finally_cost'];
$payment_date =date("Y-m-d H:i:s");

  //결과 값을 받기 위한 배열
$result_array = array();
$jwt_error='0';


if(!empty($sktoken) &&!empty($reservation_id)&&(!empty($coupon_id) ||$coupon_id==0)&&(!empty($usercoupon_id) ||$usercoupon_id==0)&&!empty($origin_cost)&&!empty($finally_cost)&&!empty($payment_date)){

  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "set_payment : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];

  //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
  $query = "INSERT INTO payment(userinfo_id,coupon_id,reservation_id,origin_cost,finally_cost,payment_date,usercoupon_id,owner_id) values('$user_id','$coupon_id','$reservation_id','$origin_cost','$finally_cost','$payment_date','$usercoupon_id','$owner_id')" ; //list

//결과 값을 데이터 이름으로 받아 배열에 저장한다.
  if($info=mysql_query($query))
  { 
    $query = "UPDATE usercoupon SET coupon_state=1 where usercoupon_id ='$usercoupon_id'" ; //list
    if(mysql_query($query)){
     $result_array['err']='0';
   }else{
  $result_array['err']='011';

   }
 }
 else
 {
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
  $result_array['err_result']=get_err_result("set_payment",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;
?>