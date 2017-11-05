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
$carinfo_id=$_POST['carinfo_id'];

  //결과 값을 받기 위한 배열
$result_array = array();
  $jwt_error='0';

if(!empty($sktoken) &&!empty($carinfo_id)){
try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "get_car_detail_info : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
  $query = "SELECT * FROM carinfo where carinfo_id='$carinfo_id'" ; //list
 //결과 값을 데이터 이름으로 받아 배열에 저장한다.
    if( $result = mysql_query($query)){
      $info_car=mysql_fetch_object($result);
      $owner_id = $info_car->userinfo_id;//차주 정보 추출
  //유저 리뷰정보 추출 
      $query = "SELECT carimage_img_o,carimage_img_t FROM carimage where carinfo_id ='$carinfo_id'";
      if($result = mysql_query($query)){ 
    $img_cnt = mysql_num_rows($result);//결과의 행 갯수를 통해 총 이미지 수를 구한다.

  //차량 총 이미지 개수  
    $array_img['img_cnt']=$img_cnt;

  //img 배열안에 
    while($info_img=mysql_fetch_object($result)){

      $array_img['img'][] = $info_img;

    }
    //유저 리뷰정보 추출 
    $query = "SELECT userinfo_id,name,ow_review_cnt,ow_review_score,userinfo_img_url FROM userinfo where userinfo_id ='$owner_id'";
    if($result = mysql_query($query)){
      $info_user = mysql_fetch_object($result);


    //유저 리뷰정보 추출 
      $query = "SELECT * FROM payment where owner_id ='$owner_id'";
      if($result = mysql_query($query)){
        $result_array['err']='0';

      //단순 어레이로 받으면 같은 값이 2번씩 출력된다, 인덱스-값 / 키-값 쌍으로 2번씩 출력된다
      //오브젝트로 받아야 이러한 문제가 없어지며, 형변환을 통해 어레이 머지를 실행한다.
      //그럼 마치 하나의 어레이처럼 받아들어져 제이슨으로 형변환하여 반환해주면 키 값이 중복된다. 
        $result_array['ret'] = array_merge((array)$array_img,(array)$info_user,(array)$info_car);
        $result_array['ret']['res_cnt'] = mysql_num_rows($result);

      } 
      else{

        $result_array['err']='013';
      }
    } else{

      $result_array['err']='012';
    }

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
  $result_array['err_result']=get_err_result("get_car_detail_info",$result_array['err']);
}

$result_array = json_encode($result_array);
echo $result_array;

?>