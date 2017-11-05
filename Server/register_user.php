<?php 
  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');
include_once('./lib/jwt.php');
connect();


$user_name=$_POST['user_name'];
$email=$_POST['email'];
$user_phone=$_POST['user_phone'];
$user_sex=$_POST['user_sex'];
$user_path=$_POST['user_path'];
$user_recom=$_POST['user_recom'];
$kakao_id=$_POST['kakao_id'];
$facebook_id=$_POST['facebook_id'];
$passwd=$_POST['passwd'];
$userinfo_img_url = $_POST['userinfo_img_url'];
$date = date("Y-m-d H:i:s");
//프로필 이미지가 설정된 url이 없을 경우 기본 이미지로 삽입하게 된다. 
if(!empty($userinfo_img_url)){
  $userinfo_img_url = "http://fj01-qb5024.ktics.co.kr/user_base_profile.png";
}

//userinfo에 필요한 데이터를 삽입하기 위한 쿼리문.
$sql = "insert into userinfo (name, email,passwd, sex,phone,path,type,date,kakao_id,facebook_id) values('$user_name','$email',PASSWORD('$passwd'),'$user_sex','$user_phone','$user_path','0','$date','$kakao_id','$facebook_id')";

$result_array = array();

if(!empty($user_name) &&!empty($email) &&!empty($user_phone)  &&!empty($user_sex) &&!empty($user_path) &&!empty($user_recom) &&!empty($passwd)&&!empty($kakao_id)&&!empty($facebook_id)){

//삽입이 잘되 었는지 체크
  if($result=mysql_query($sql)){

  //삽입된 데이터를 추출(이메일을 기반으로 찾되 중복가능 여부에 대해 고려(탈퇴 후 재가입 등)해 가장 최근의 데이터 추출)
  $query = "SELECT * FROM userinfo where email='$email' order by date desc limit 1" ; //list
  //정상적으로 쿼리가 작동했는지 확인.
  if($result = mysql_query($query)){

    //쿼리에서 정상적으로 데이터를 추출했는지 체크
    if($info=mysql_fetch_object($result)){
      $result_array['ret'] = $info;
    $result_array['err']='0';
      
    }
    else{

      $result_array['err']='020';

    }
  }else{

    $result_array['err']='011';
  }
}else{

  $result_array['err']='010';
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