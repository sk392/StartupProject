<?

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
</head>
<body>
<form name= "member_form" method="post" action="insert.php">

<ul>
<li> 직군 </li>
<li> 세부태그 </li>
<li> 출생년도 </li>
<li> 전화번호 </li>
</ul>
<ul>
<li> <input type = "text" name = "mainfield"> </li>
<li> <input type = "text" name = "detailfield"> </li>
<li> <select name="birthdate">
<option name="birthdate">1985</option>
<option name="birthdate">1986</option>
<option name="birthdate">1987</option>
<option name="birthdate">1988</option>
<option name="birthdate">1989</option>
<option name="birthdate">1990</option>
<option name="birthdate">1991</option>
<option name="birthdate">1992</option></select></li>
<li> <select name="hp1">
<option name="hp1" value="010">010</option>
</select> - <input type="text" name="hp2"> - <input type= "text" name="hp3"></li>
</ul>

<input type="submit" value=submit>
</form>
</body>
</html>
