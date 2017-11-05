<?php
include_once('./lib/jwt.php');
include_once('./lib/json.php');
  // mysql을 사용하기위해 lib 참조
include_once('./lib/api_library.php');

connect();

$email=$_POST['email'];
$passwd=$_POST['passwd'];




 //결과 값을 받기 위한 배열
$result_array = array();

if(!empty($email) &&!empty($passwd)){

$query = "SELECT userinfo_id,email,name,type FROM userinfo WHERE email='$email' AND passwd=PASSWORD('$passwd')";
  //쿼리가 정상적이었는지 확인
    if($result = mysql_query($query)){
    //결과 값을 올바르게 추출하여 배열에 저장 
        if($info=mysql_fetch_object($result)){

            $tokenId = base64_encode($token_id);

            $issuedAt = time();
            $notBefore = $issuedAt;
            $expire = $notBefore + 60 * 60 * 24 * 14;

            $user_id = $info->userinfo_id;
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

            if($jwt = JWT::encode($data, $secret_key)){

                $result_array['err']='0';
                $result_array['ret'] =$info; 

                $result_array['jwt_sktoken'] = $jwt;

                
            }
            else{
            //JWT 생성에러
                $result_array['err']='040';
            }
        }
        else{
        //SQL문 결과 에러
            $result_array['err']='020';

        }
    }else{
    //SQL문 생성 에러 
        $result_array['err']='010';

    }            
}
else{
    //전달된 변수 값 에러
    $result_array['err']='030';
}


$result_array['err_result']=get_err_result("user_auth",$result_array['err']);
$result_array = json_encode($result_array);
echo $result_array;
//print_r(json_decode($result_array));

?>