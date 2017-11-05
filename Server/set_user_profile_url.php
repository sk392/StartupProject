	<?php 
  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
	include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
	include_once('./lib/api_library.php');
	include_once('./lib/jwt.php');

	connect();

	$sktoken=$_POST['sktoken'];
	$profile_url=$_POST['profile_url'];


	$result_array = array();


	if(!empty($sktoken) &&!empty($profile_url)){
		$jwt_error='0';
		
		try{
			$jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
		} catch(Exception $e){
			$jwt_error = $e->getCode();
			$jwt_error_result = "set_user_intro : ".$e->getMessage();
		}

                //jwt 에러가 0인경우 user_id 값을 가져온다.
		if(!strcmp($jwt_error,"0")){

			$jwt_decoded_array = (array) $jwt_decoded;
			$jwt_data_array = (array)$jwt_decoded_array['data'];
			$user_id=$jwt_data_array['user_id'];

			$query = "UPDATE  userinfo SET userinfo_img_url='$profile_url' WHERE userinfo_id='$user_id'";

			if(mysql_query($query)){
				$result_array['err']= '0';
			}else{ $result_array['err']= '010';
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
	$result_array['err_result']=get_err_result("set_user_intro",$result_array['err']);
}
$result_array = json_encode($result_array);
echo $result_array;

?>