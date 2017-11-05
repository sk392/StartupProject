<doctype html>
<meta charset="utf-8">
<html>
<head>
<title>예약 리스트 호출 테스트</title>
</head>
<body>
<form name="join" method="post" action="get_reservation_list.php">
 <h1> 예약 리스트 호출 페이지</h1>
 <table border="1">
  <tr>
   <td>토큰 : </td>
   <td><input type="text" size="30" name="sktoken"></td>
  </tr>
  <tr>
   <td>type (0: 대여자, 1: 차주) : </td>
   <td><input type="text" size="30" name="type"></td>
  </tr>

 </table>
 <input type=submit value="submit"><input type=reset value="rewrite">
</form
></body>
</html>
