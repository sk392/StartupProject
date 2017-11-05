<?php

function get_err_result($source,$err_code){
	$result ="";
	$err_code_description ="";
	if ($err_code =="010") 
	{
		$err_code_description = "SQL문 생성 에러";
	}
	else if($err_code =="0") 
	{
		$err_code_description = "정상 출력";
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

	$result = $source." - ".$err_code." : ".$err_code_description;
	return $result;
}


?>