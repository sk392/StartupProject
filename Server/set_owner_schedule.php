<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();

//POST 형태로 보내온 정보를 받아온다.
$sktoken=$_POST['sktoken'];
$carinfo_id=$_POST['carinfo_id'];
$start_date =$_POST['start_date'];
$end_date =$_POST['end_date']; 


  //결과 값을 받기 위한 배열
$result_array = array();


if(!empty($sktoken)&&!empty($carinfo_id)&&!empty($start_date)&&!empty($end_date)){
  $jwt_error='0';

  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "set_owner_schedule : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
    //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
    $query = "INSERT INTO reservation(owner_id,carinfo_id,start_date,end_date,state) values('$user_id','$carinfo_id','$start_date','$end_date','1')" ; //list
    //스테이트 값을 1로 줌. 유효한 값인 것을 체크하기위해.

      //결과 값을 데이터 이름으로 받아 배열에 저장한다.
      if($info=mysql_query($query))
      {

      $result_array['err']='0';
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
  $result_array['err_result']=get_err_result("set_owner_schedule",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;
?>