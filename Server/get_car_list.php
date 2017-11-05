<?php 


  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');


connect();

//POST 형태로 보내온 id 및 type값을 입력 받는다
$sktoken=$_POST['sktoken'];
$offset=$_POST['offset'];
$row_cnt=$_POST['row_cnt'];
$start_date=$_POST['start_date'];
$end_date=$_POST['end_date'];
$option_cnt=$_POST['option_cnt'];
$option =$_POST['option'];//옵션은 슬래시(/)를 구분자로 cnt에 있는 만큼만 보낸다.
$car_shape=$_POST['car_shape'];
$car_shape_cnt=$_POST['car_shape_cnt'];
//현재는 모든 차종이 검색되록.
//$location=$_POST['location'];
//모든 지역이 검색되도록.

  //결과 값을 받기 위한 배열
$result_array = array();
$jwt_error='0';

// 모든 차종이 검색되도록.
//&&!empty($location) 모든 지역이 검색되도록.
if(!empty($sktoken) &&(!empty($offset)||$offset==0)&&!empty($row_cnt) &&!empty($start_date) &&!empty($end_date)&&!empty($car_shape)  &&(!empty($option_cnt)||$option_cnt==0) ){
  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_car_list : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
 //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문

    $query ="SELECT carinfo_id,isoneday,model,model_year,carinfo.isdelivery,carinfo.carinfo_review_cnt,carinfo.carinfo_review_score,carinfo.carinfo_img_t,userinfo.userinfo_img_url,cost_deli_wday,cost_deli_wday_add,cost_deli_wend_add,cost_deli_wend,cost_rent,userinfo.name,userinfo.userinfo_id,userinfo.ow_review_cnt,userinfo.ow_review_score,car_shape FROM carinfo join userinfo on carinfo.userinfo_id=userinfo.userinfo_id";
    
    //AND model ='$model' 모델 검색 가능하도록.
    //where (location1='$location' OR location2='$location' OR location3='$location'
    //모든 지역이 검색가능하도록.

    if($option_cnt!=0){
      $optiondiv = explode('/',$option);
      for($i = 0; $i<(int)$option_cnt; $i++){

        $query = $query." AND ".$optiondiv[$i]."='y'";

      }
    }
    if($car_shape_cnt != 0){
      $carshapediv = explode('/',$car_shape);
      if($car_shape_cnt >1){
        $query = $query." AND ( car_shape='".$carshapediv[0]."'";
        for($i=1; $i<(int)$car_shape_cnt;$i++){
        
          $query = $query." OR car_shape='".$carshapediv[$i]."'";
        }
        $query = $query." )";
      }else{
          if($carshapediv[0] !="-"){
          $query = $query." AND car_shape='".$carshapediv[0]."'";
        }
      }
    }
    //현재는 차 평점 위주로 검색.
    $query = $query. " ORDER BY carinfo.carinfo_review_score DESC LIMIT $offset,$row_cnt";

  $result_array['query'] = $query;

 //결과 값을 데이터 이름으로 받아 배열에 저장한다.
    if($result = mysql_query($query)){
      $result_array['cnt']=0;
      $result_array['err']='0';
      while($info=mysql_fetch_object($result)){ 
            //carinfo_id 값을 이용해 reservation(예약)페이지에서 일정이 비는 car를 특정짓는다.
            //검색 시간에 예약이 있으며, 그 예약이 결제가 이루어졌거나, 차주 스케쥴인 경우 결과에 더하지 않는다.
        $query = "SELECT start_date,end_date from reservation WHERE carinfo_id ='$info->carinfo_id'AND ((start_date >='$start_date') || (end_date >='$start_date')) && ((start_date <='$end_date') || (end_date<='$end_date')) AND ((state = 1) || (state = 2))";
        if($result_res=mysql_query($query)){
          //0: 예약 신청 (O) , 1: 차주 스케쥴 (x), 2: 결제 완료 (x), 3: 예약 취소 (o)
          if(!($info_res=mysql_fetch_object($result_res))){
              //검색 시간에 예약이 없는 경우 결과에 더해준다.
            $result_array['cnt']= $result_array['cnt']+1;
            $result_array['ret'][] =$info;
          }
        }else{
          $result_array['err']='011';
        }
      }

    }else{
      $result_array['result']=$query;
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
  $result_array['err_result']=get_err_result("get_car_list",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;
?>