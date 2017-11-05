<doctype html>
<meta charset="utf-8">
<html>
<head>
<title>비밀번호 변경 테스트</title>
</head>
<body>
<form name="join" method="post" action="set_user_passwd.php">
 <h1> 비밀번호 변경 페이지</h1>
 <table border="1">
  <tr>
   <td>유저 아이디 : </td>
   <td><input type="text" size="30" name="user_id"></td>
  </tr>
  <tr>
   <td>비밀번호: </td>
   <td><input type="text" size="30" name="passwd"></td>
  </tr>
  <tr>
   <td>새롭게 바뀌는 비밀번호 : </td>
   <td><input type="text" size="30" name="new_passwd"></td>
  </tr>
  <tr>
   <td>새롭게 바뀌는 비밀번호2 : </td>
   <td><input type="text" size="30" name="new_passwd2"></td>
  </tr>
 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form
></body>
</html>