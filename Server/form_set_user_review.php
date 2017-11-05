<doctype html>
<meta charset="utf-8">
<html>
<head>
<title>사용자 리뷰 삽입 테스트</title>
</head>
<body>
<form name="join" method="post" action="set_user_review.php">
 <h1> 사용자 리뷰 삽입 페이지</h1>
 <table border="1">
  <tr>
   <td>대상 아이디 : </td>
   <td><input type="text" size="30" name="target_id"></td>
  </tr>
  <tr>
   <td>작성자 아이디 : </td>
   <td><input type="text" size="30" name="writer_id"></td>
  </tr>
  <tr>
   <td>점수(1~10) : </td>
   <td><input type="text" size="30" name="score"></td>
  </tr>
  <tr>
   <td>내용 : </td>
   <td><input type="text" size="30" name="content"></td>
  </tr>
  <tr>
   <td>작성자 이름 : </td>
   <td><input type="text" size="30" name="writer_name"></td>
  </tr>
  <tr>
   <td>type : </td>
   <td><input type="text" size="30" name="type"></td>
  </tr>

 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form
></body>
</html>