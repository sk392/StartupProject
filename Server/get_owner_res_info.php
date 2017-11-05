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

  //결과 값을 받기 위한 배열
  $result_array = array();
  $jwt_error='0';

if(!empty($sktoken)){


  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_owner_res_info : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
 //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
  $query = "SELECT name,response,ow_review_cnt,ow_review_score FROM userinfo where userinfo_id='$user_id'" ; //list


  if($result=mysql_query($query)){
    $result_array['err']='0';
    $info_user = mysql_fetch_object($result);

    $query = "SELECT cost,start_date FROM reservation where owner_id='$user_id' AND state ==1" ; //list
    $array = array();


    if($result=mysql_query($query)){
      $res_cnt =0;
      $month_cnt =0;
      $month_profit =0;
      $res_profit =0;
 $res_cnt = mysql_num_rows($result);//결과의 행 갯수를 통해 총 예약 수를 구한다.

      while($info= mysql_fetch_object($result)){

         $res_profit = $res_profit + $info->cost;
        //현재의 달(1~12)을 구하고 그 값이 저장된 데이터의 달(1~12)값과 같은지 비교한다.
        $date = date("n",strtotime($info->start_date));
        if($date==date("n")){
         $month_cnt = $month_cnt + 1;
         $month_profit = $month_profit + $info->cost;
         }
      }      
     

      $array['res_cnt'] = $res_cnt;
      $array['res_profit'] = $res_profit;
      $array['month_cnt'] = $month_cnt;
      $array['month_profit'] = $month_profit;

      $result_array['ret']=array_merge((array)$info_user,(array)$array);

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
  $result_array['err_result']=get_err_result("get_owner_res_info",$result_array['err']);
}
  $result_array = json_encode($result_array);

  echo $result_array."<br>";

  ?>