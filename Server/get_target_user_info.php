<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();


//POST 형태로 보내온 id 및 type값을 입력 받는다
$sktoken=$_POST['sktoken'];
$target_id=$_POST['target_id'];
$target_type=$_POST['target_type']; 

$result_array = array();
if(!is_null($sktoken)&&!is_null($target_id)){


  $jwt_error='0';
  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_user_info : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
 //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
    $query = "SELECT * FROM userinfo where userinfo_id='$target_id'" ;
  //결과 값을 받기 위한 배열

  //list
    if($result = mysql_query($query)){ 

  //결과 값을 데이터 이름으로 받아 배열에 저장한다.
      if($info_user=mysql_fetch_object($result)){
  //차량 이용정보 
        if(!strcmp($target_type,"0")){
          $query = "SELECT mileage, isaccident FROM reservation where userinfo_id ='$target_id' AND state =1";
        }else{
          $query = "SELECT mileage, isaccident FROM reservation where owner_id ='$target_id' AND state =1";
        }
        if($result = mysql_query($query)){
          $result_array['err']='0';

          $accident_cnt=0;
          $mileage=0;
          $res_cnt=0;
          while($info=mysql_fetch_array($result)){
            $res_cnt = $res_cnt +1;
    //사고가 있다면 datetime 형태로 저장되고, 데이터가 있다면 1을 증가시킨다.
            if($info['isaccident']) $accident_cnt = $accident_cnt +1;
            $mileage = $mileage + $info['mileage'];
          }
          $array = array();

          $array['accident'] = $accident_cnt;
          $array['mileage'] = $mileage;
          $array['res_cnt'] = $res_cnt;
          $result_array['ret'] = array_merge((array)$info_user,$array);

        }else{

          $result_array['err']='011';
        }
      }else{

        $result_array['err']='020';
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
  $result_array['err_result']=get_err_result("get_user_info",$result_array['err']);
}
$result_array = json_encode($result_array);

echo $result_array;
?>