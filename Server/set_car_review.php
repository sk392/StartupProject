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
$score=$_POST['score'];
$content=$_POST['content'];
$jwt_error='0';
$writer_name=$_POST['writer_name'];
$date = date("Y-m-d H:i:s");


	//결과 값을 받기 위한 배열
$result_array = array();


if(!empty($sktoken) &&!empty($carinfo_id) &&!empty($score) &&!empty($content)&&!empty($writer_name)){


	try{
		$jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
	} catch(Exception $e){
		$jwt_error = $e->getCode();
		$jwt_error_result = "set_car_review : ".$e->getMessage();
	}

                //jwt 에러가 0인경우 user_id 값을 가져온다.
	if(!strcmp($jwt_error,"0")){

		$jwt_decoded_array = (array) $jwt_decoded;
		$jwt_data_array = (array)$jwt_decoded_array['data'];
		$user_id=$jwt_data_array['user_id'];
 //userinfo 테이블에 있는 유저 정보를 추출하기 위한 query문
	$query = "INSERT INTO carreview(carinfo_id,writer_id,writer_name,content,date,score) values('$carinfo_id','$user_id','$writer_name','$content','$date','$score')" ; //list

	//결과 값을 데이터 이름으로 받아 배열에 저장한다.
	if($info=mysql_query($query)){
		//userinfo에 있는 review관련 컬럼을 업데이트한다.

	//차량 리뷰정보 추출 
		$query = "SELECT score FROM carreview where carinfo_id ='$carinfo_id'";
		if($result = mysql_query($query)){ 
	$review_cnt = mysql_num_rows($result);//결과의 행 갯수를 통해 총 리뷰 수를 구한다.
	$score_result =0;

	//각 스코어를 더한 후 총 리뷰수로 나누어 유저의 평균 값을 구한다.
	while($info=mysql_fetch_array($result)){
		$score_result = $score_result + $info['score']; //
	}
	$car_score = $score_result / $review_cnt;
	$car_score = round($car_score,1);//반올림으로 소수점 한자리까지 만든다.

	$query = "update carinfo set carinfo_review_cnt='$review_cnt',carinfo_review_score='$car_score' where carinfo_id='$carinfo_id'" ;
	if($result = mysql_query($query)){

		$result_array['err']='0';
	}else{
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
	$result_array['err_result']=get_err_result("set_car_review",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;
?>