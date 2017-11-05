<doctype html>
<meta charset="utf-8">
<html>
<head>
<title>차량 리스트 호출 테스트</title>
</head>
<body>
<form name="join" method="post" action="get_car_list.php">
 <h1> 차량 리스트 호출 페이지</h1>
 <table border="1">
  <tr>
   <td>유저 아이디 : </td>
   <td><input type="text" size="30" name="user_id"></td>
  </tr>
  <tr>
   <td>offset: </td>
   <td><input type="text" size="30" name="offset"></td>
  </tr>
  <tr>
   <td>row_cnt : </td>
   <td><input type="text" size="30" name="row_cnt"></td>
  </tr>
  <tr>
   <td>start_Date : </td>
   <td><input type="text" size="30" name="start_date"></td>
  </tr>
  <tr>
   <td>end_Date : </td>
   <td><input type="text" size="30" name="end_date"></td>
  </tr>
  <tr>
   <td>model : </td>
   <td><input type="text" size="30" name="model"></td>
  </tr>
  <tr>
   <td>option_cnt : </td>
   <td><input type="text" size="30" name="option_cnt"></td>
  </tr>
  <tr>
   <td>option( '/'를 구분자로 cnt 개수 만큼 입력) : </td>
   <td><input type="text" size="30" name="option"></td>
  </tr>
  <tr>
   <td>loaction : </td>
   <td><input type="text" size="30" name="location"></td>
  </tr>

 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form
></body>
</html>