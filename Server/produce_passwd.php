<?php
$uppers = str_shuffle('ABCDEFGHIJKLMNOPQRSTUVWXYZ');
$lowers = str_shuffle('abcdefghijklmnopqrstuvwxyz');
$numbers = str_shuffle('1234567890');
$specials = str_shuffle('!@#$%^&*()');
$complex_password = substr($lowers,-4).substr($numbers,-4);
$more_complex_password = str_shuffle($complex_password);
echo "$more_complex_password";
?>