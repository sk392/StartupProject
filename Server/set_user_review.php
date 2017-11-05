<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
  // jwt를 사용하기 위해 lib 참조
include_once('./lib/jwt.php');

connect();

//POST 형태로 보내온 정보를 받아온다.
$target_id=$_POST['target_id'];
$score=$_POST['score'];
$content=$_POST['content'];
$sktoken=$_POST['sktoken'];
$type=$_POST['type'];//대여자 ->차주,차주->대여자 인지 구분한다. 0이면 차주 -> 대여자, 1이면 대여자 -> 차주 (리뷰 대상의 타입을 기준sf으로한다.)

$date = date("Y-m-d H:i:s");
$writer_name=$_POST['writer_name'];
  //결과 값을 받기 위한 배열
$result_array = array();

  $result_array['target_id']=$target_id;
  $result_array['score']=$score;
  $result_array['sktoken']=$sktoken;
  $result_array['content']=$content;
  $result_array['type']=$type;
  $result_array['writer_name']=$writer_name;

if(!empty($target_id) &&!empty($score) &&!empty($content) &&(!empty($type) || $type==0)&&!empty($writer_name)&&!empty($sktoken)){
  $jwt_error='0';

  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "set_user_review : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];

    //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
  $query = "INSERT INTO userreview(owner_id,writer_id,writer_name,content,date,score,type) values('$target_id','$user_id','$writer_name','$content','$date','$score','$type')" ; //list
   $result_array['query']=$query;

  
  //결과 값을 데이터 이름으로 받아 배열에 저장한다.
  if($result=mysql_query($query)){
    //userinfo에 있는 review관련 컬럼을 업데이트한다.

  //유저 리뷰정보 추출 
    $query = "SELECT score FROM userreview where owner_id ='$target_id' and type=$type";
    if($result = mysql_query($query)){
  $review_cnt = mysql_num_rows($result);//결과의 행 갯수를 통해 총 리뷰 수를 구한다.
  $score_result =0;

  //각 스코어를 더한 후 총 리뷰수로 나누어 유저의 평균 값을 구한다.
  while($info=mysql_fetch_array($result)){
    $score_result = $score_result + $info['score']; //
  }
  $user_score = $score_result / $review_cnt;
  $user_score = round($user_score,1);//반올림으로 소수점 한자리까지 만든다.
  if($type==0){

    $query = "update userinfo set userinfo_review_cnt='$review_cnt',userinfo_review_score='$user_score' where userinfo_id='$target_id'" ;
  }else{
    $query = "update userinfo set ow_review_cnt='$review_cnt',ow_review_score='$user_score' where userinfo_id='$target_id'" ;

  }
  if($result = mysql_query($query)){
   $result_array['err']='0';
 }else{
  //SQL문 생성 에러
   $result_array['err']='012';
 }
}else{
  //SQL문 생성 에러
 $result_array['err']='011';

}
}else{
  //SQL문 생성 에러 
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
  $result_array['err_result']=get_err_result("set_user_review",$result_array['err']);
}

$result_array = json_encode($result_array);
echo $result_array;
?>