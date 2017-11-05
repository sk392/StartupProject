<doctype html>
<html>
<head>
<title>sign up page</title>
</head>
<body>
<form name="join" method="post" action="register_user.php">
 <h1> 테스트 회원가입 페이지</h1>
 <table border="1">
 <tr>
   <td>이름</td>
   <td><input type="text" size="12" maxlength="10" name="user_name"></td>
  </tr>
  <tr>
   <td>이메일</td>
   <td><input type="text" size="30" name="email"></td>
  </tr>
  <tr>
   <td>휴대폰 번호</td>
   <td><input type="test" size="30" name="user_phone"></td>
  </tr>
  <tr>
   <td>비밀번호</td>
   <td><input type="password" size="30" name="passwd"></td>
  </tr>
    <tr>
   <td>성별</td>
   <td><input type="text" size="5" name="user_sex"></td>
  </tr>
  <tr>
   <td>우편번호</td>
   <td><input type="text" size="40" name="user_zipcode"></td>
  </tr>
  <tr>
   <td>주소 </td>
   <td><input type="text" size="40" name="user_addr"></td>
  </tr>
  <tr>
   <td>상세 주소 </td>
   <td><input type="text" size="40" name="user_addr_de"></td>
  </tr>
  <tr>
   <td>가입 경로</td>
   <td><input type="text" size="40" name="user_path"></td>
  </tr>
  <tr>
   <td>추천인 ID </td>
   <td><input type="text" size="30" maxlength="30" name="user_recom"></td>
  </tr>
  <tr>
   <td>운전면허지역</td>
   <td><input type="text" size="30" maxlength="30" name="user_licen_loc"></td>
  </tr>
  <tr>
   <td>운전면허번호 </td>
   <td><input type="text" size="30" maxlength="30" name="user_licen"></td>
  </tr>
  <tr>
   <td>생년 월일</td>
   <td><input type="text" size="30" name="user_birth"></td>
  </tr>
  <tr>
   <td>일련 번호 </td>
   <td><input type="text" size="30" name="user_licen_num"></td>
  </tr>
  <tr>
   <td>발급일 </td>
   <td><input type="text" size="30" name="user_licen_date"></td>
  </tr>
 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form>
</body>
</html>