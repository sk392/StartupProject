<doctype html>
<meta charset="utf-8">
<html>
<head>
<title>사용자 예약 삽입 테스트</title>
</head>
<body>
<form name="join" method="post" action="set_reservation.php">
 <h1> 사용자 예약 삽입 페이지</h1>
 <table border="1">
  <tr>
   <td>유저 아이디 : </td>
   <td><input type="text" size="30" name="user_id"></td>
  </tr>
  <tr>
   <td>차주 아이디 : </td>
   <td><input type="text" size="30" name="owner_id"></td>
  </tr>
  <tr>
   <td>차량 아이디 : </td>
   <td><input type="text" size="30" name="car_id"></td>
  </tr>
  <tr>
   <td>예약 시작일자(yyyy-mm-dd hh:mm:ss) : </td>
   <td><input type="text" size="30" name="start_date"></td>
  </tr>
  <tr>
   <td>예약 종료일자(yyyy-mm-dd hh:mm:ss) : </td>
   <td><input type="text" size="30" name="end_date"></td>
  </tr>


 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form
></body>
</html>