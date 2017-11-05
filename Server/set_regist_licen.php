<?php 

require_once("./lib/PHPMailer/PHPMailerAutoload.php");
  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');

connect();

$sktoken=$_POST['sktoken'];
$licen=$_POST['licen'];
$licen_name=$_POST['licen_name'];
$licen_num_one=$_POST['licen_num_one'];
$licen_num_two=$_POST['licen_num_two'];
$licen_num_three=$_POST['licen_num_three'];
$licen_birth=$_POST['licen_birth'];
$licen_date=$_POST['licen_date'];
$licen_loc=$_POST['licen_loc'];


$result_array = array();
  $jwt_error='0';


if(!empty($sktoken) &&!empty($licen) &&!empty($licen_name) &&!empty($licen_num_one) &&!empty($licen_num_two) &&!empty($licen_num_three) &&!empty($licen_birth) &&!empty($licen_date) &&!empty($licen_loc)){
  $licen_num = $licen_num_one. $licen_num_two. $licen_num_three;
  
  try{
    $jwt_decoded = JWT::decode($sktoken, $secret_key, array('HS256'));
  } catch(Exception $e){
    $jwt_error = $e->getCode();
    $jwt_error_result = "set_owner_intro : ".$e->getMessage();
  }

                //jwt 에러가 0인경우 user_id 값을 가져온다.
  if(!strcmp($jwt_error,"0")){

    $jwt_decoded_array = (array) $jwt_decoded;
    $jwt_data_array = (array)$jwt_decoded_array['data'];
    $user_id=$jwt_data_array['user_id'];
    $query = "UPDATE userinfo SET licen='$licen',licen_loc='$licen_loc',licen_num='$licen_num',licen_date='$licen_date',licen_name='$licen_name',licen_birth ='$licen_birth' WHERE userinfo_id='$user_id'";

    if(mysql_query($query)) {

      //요청이 완료되면 이메일을 보낸다.
      $user_email = "carmony.kr@gmail.com";
      $mail = new PHPMailer;

      $mail->SMTPDebug = 0;                               // Enable verbose debug output
      $mail->CharSet = 'UTF-8';
      $mail->isSMTP();                                      // Set mailer to use SMTP
      $mail->Host = 'smtp.gmail.com';  // Specify main and backup SMTP servers
      $mail->SMTPAuth = true;                               // Enable SMTP authentication
      $mail->Username = 'carmony.kr@gmail.com';                 // SMTP username
      $mail->Password = 'bookcar0113';                           // SMTP password
      $mail->SMTPSecure = 'ssl';                            // Enable TLS encryption, `ssl` also accepted
      $mail->Port = 465;                                    // TCP port to connect to tls = 587, ssl =465

      $mail->setFrom('carmony.kr@gmail.com', 'Carmony');
      $mail->addAddress($user_email);     // Add a recipient
      $mail->addReplyTo('info@example.com', 'Information');
      $mail->isHTML(true);                                  // Set email format to HTML

      $mail->Subject = 'Carmony Regist License';
      $mail->Body    = '면허증 요청한 계정 정보 <br><b> userinfo_id : '.$user_id.' <br>이름 : '.$licen_name
      .' <br>면허증 지역 : '.$licen_loc
      .' <br>면허증 넘버 : '.$licen_num
      .' <br>면허증 생년월일 : '.$licen_birth
      .' <br>면허증 일련번호 : '.$licen
      .' <br>면허증 발급일자 : '.$licen_date.'</b> 입니다.';
      
      if($mail->send()) {
          $result_array['err']='0';
        } else {
          $result_array['err']='070';
        }

    }else{
      $result_array['err']= '010';
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
  $result_array['err_result']=get_err_result("set_owner_intro",$result_array['err']);
}

$result_array = json_encode($result_array);

echo $result_array;

?>