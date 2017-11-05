<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();

//POST 형태로 보내온 정보를 받아온다.
$sktoken=$_POST['sktoken'];
$owner_id=$_POST['owner_id'];
$carinfo_id=$_POST['carinfo_id'];
$start_date =$_POST['start_date'];
$end_date =$_POST['end_date'];
$cost =$_POST['cost'];
$late_cost =$_POST['late_cost'];
$delivery_cost =$_POST['delivery_cost'];
$oneday_cost =$_POST['oneday_cost'];
$location =$_POST['location'];


  //결과 값을 받기 위한 배열
$result_array = array();

  $jwt_error='0';

if(!empty($sktoken) &&!empty($owner_id)&&!empty($carinfo_id)&&!empty($start_date)&&!empty($end_date)&&!empty($cost)&&!empty($delivery_cost)&&(!empty($oneday_cost)||$oneday_cost==0)&&(!empty($late_cost)||$late_cost==0)&&!empty($location)){
 
  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "set_reservation : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];

  //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
  $query = "INSERT INTO reservation(owner_id,userinfo_id,carinfo_id,start_date,end_date,cost,delivery_cost,oneday_cost,late_cost,location) values('$owner_id','$user_id','$carinfo_id','$start_date','$end_date','$cost','$delivery_cost','$oneday_cost','$late_cost','$location')" ; //list

//결과 값을 데이터 이름으로 받아 배열에 저장한다.
  if($info=mysql_query($query))
  {
      $query = "SELECT reservation_id FROM reservation ORDER BY reservation_id DESC LIMIT 1" ; //list

      //결과 값을 데이터 이름으로 받아 배열에 저장한다.
      if($info=mysql_query($query))
      {
        if($info_result = mysql_fetch_object($info))
        {
          $result_array['err']='0';
          $result_array['reservation_id']=$info_result->reservation_id;
        }else{
          $result_array['err']='011';
        }
      }else{
        $result_array['err']='012';
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
  $result_array['err_result']=get_err_result("set_reservation",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;
?>