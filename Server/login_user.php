
<? 
  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();


$email=$_POST['email'];
$passwd=$_POST['passwd'];


$query = "SELECT userinfo_id,email,name,type FROM userinfo WHERE email='$email' AND passwd=PASSWORD('$passwd')";


 //결과 값을 받기 위한 배열
$result_array = array();
  //쿼리가 정상적이었는지 확인
if($result = mysql_query($query)){
	//결과 값을 올바르게 추출하여 배열에 저장 
	if($info=mysql_fetch_object($result)){

		$tokenId = base64_encode("skcarmony_tokenID");

		$issuedAt = time();
		$notBefore = $issuedAt;
		$expire = $notBefore + 60*60 * 24 * 14;
		$serverName = "skcarmony_server";

		$user_id = "user_id";
		$server_no = 1;

		$data = array(
			'iat' => $issuedAt,
			'jti' => $tokenId,
			'iss' => $serverName,
			'nbf' => $notBefore,
			'exp' => $expire,
			'data' => array
			( 'user_id' => $user_id,
				'server_no' => $server_no)

			);

		$jwt = JWT::encode($data, $secret_key);


		$result_array['err']='0';
		$result_array['ret'] =$info; 
		$result_array['sk_token'] = $jwt;
	}
	else{
		$result_array['err']='2';

	}
}else{
	$result_array['err']='1';

}
$result_array = json_encode($result_array);
echo $result_array;
?>