<meta charset="utf-8">
<?php 


   // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();


  //결과 값을 받기 위한 배열
$result_array = array();

$member_code = "46554";
$google_no="abcd55";
$email="gadigi@gmail.com";

$mainfield = $_POST["mainfield"];
$birthdate = $_POST["birthdate"];
$phone = $_POST["hp1"]."-".$_POST["hp2"]."-".$_POST["hp3"];
$detailfield = $_POST["detailfield"];

echo $member_code;
echo $google_no;
echo $email;
echo $mainfield;
echo $birthdate;
echo $phone;
echo $detailfield;



	    $sql = "insert into member(member_code, google_no, mainfield, birthdate, phone, email, detailfield) values('$member_code', '$google_no', '$mainfield', '$birthdate', '$phone','$email', '$detailfield')";

		mysql_query($sql);  
  

   mysql_close();              
?>