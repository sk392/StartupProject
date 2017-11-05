<?php

$secret_key = "secretkey_sk!carmony";
$serverName = "sk!carmony_server";
$token_id = "skcarmony_tokenID";

/*
	for database connecting

*/
function connect(){
	$host = 'localhost';
	$user = 'root';
	$pw = 'uy871431';
	$dbName = 'carmonydb';
	$connect = mysql_connect($host,$user,$pw);

	//PHP -> mysql로 데이터를 보낼 때 utf8로 보내기위해 선언
	mysql_query("SET character_set_results = 'utf8', character_set_client = 'utf8', character_set_connection = 'utf8', character_set_database = 'utf8', character_set_server = 'utf8'", $connect);

	//사용할 데이터베이스 선택
	mysql_select_db($dbName,$connect);

}
/*
	Server Exception
     * @param String  $source         PHP String error location
	 * @param String  $err_code       PHP String error code
	
	 * @return String  $err_result    PHP String error result
	
*/
function get_err_result($source,$err_code){
	$result ="";
	$err_code_description ="";
	if ($err_code =="0") 
	{
		$err_code_description = "정상 출력";
	}

	else if($err_code =="010") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	else if($err_code =="011") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	else if($err_code =="012") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	else if($err_code =="013") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	else if($err_code =="014") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	else if($err_code =="015") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	
	else if($err_code =="020") 
	{
		$err_code_description = "SQL문 결과 에러";
	}
	else if($err_code =="021") 
	{
		$err_code_description = "SQL문 결과 에러";
	}
	else if($err_code =="022") 
	{
		$err_code_description = "SQL문 결과 에러";
	}
	else if($err_code =="023") 
	{
		$err_code_description = "SQL문 결과 에러";
	}
	else if($err_code =="024") 
	{
		$err_code_description = "SQL문 결과 에러";
	}
	else if($err_code =="025") 
	{
		$err_code_description = "SQL문 결과 에러";
	}
	else if($err_code =="030") 
	{
		$err_code_description = "전달된 변수 값 에러";
	}
	else if($err_code =="040") 
	{
		$err_code_description = "jwt 토큰 에러";
	}
	else if($err_code =="070") 
	{
		$err_code_description = "이메일 전송 오류";
	}
	//041~ 052까진 jwt 에러 
	

	$err_result = $source." - ".$err_code." : ".$err_code_description;

	return $err_result;
}

?>