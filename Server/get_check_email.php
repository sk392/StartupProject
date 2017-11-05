<?php 
  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');
connect();


$email=$_POST['email'];


$result_array = array();

if(!empty($email)){
  $query = "SELECT  * FROM userinfo WHERE email ='$email' ";
  //정상적으로 쿼리가 작동했는지 확인.
  if($result = mysql_query($query)){
    if(mysql_num_rows($result)==0){
      //중복이 없다면.
      $result_array['err']='0';

    }else{
      $result_array['err']='010';

    }
  }
  else{
      $result_array['err']='011';

  }
}
else{
  //전달된 변수 값 에러
  $result_array['err']='030';
}



$result_array['err_result']=get_err_result("register_user",$result_array['err']);
$result_array = json_encode($result_array);
echo $result_array;
?>