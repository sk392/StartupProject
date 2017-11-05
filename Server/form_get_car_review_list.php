<doctype html>
<meta charset="utf-8">
<html>
<head>
<title>리뷰 리스트 가져오기 테스트</title>
</head>
<body>
<form name="join" method="post" action="get_car_review_list.php">
 <h1> 리뷰 리스트 가져오기 테스트 페이지</h1>
 <table border="1">
  <tr>
   <td>유저 아이디 : </td>
   <td><input type="text" size="30" name="user_id"></td>
  </tr>

  <tr>
   <td>차량 아이디 : </td>
   <td><input type="text" size="30" name="car_id"></td>
  </tr>

  <tr>
   <td>offset : </td>
   <td><input type="text" size="30" name="offset"></td>
  </tr>
  <tr>
   <td>row_cnt : </td>
   <td><input type="text" size="30" name="row_cnt"></td>
  </tr>

 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form>
</body>
</html>