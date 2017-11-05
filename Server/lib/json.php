<?php 


/* ------------------------------------------------------*/
  // php 5.1.6버전에서 JSON을 사용하기 위해 lib참조 
/* ------------------------------------------------------*/ 
	include_once('JSON/JSON.php');//

  // json_decode를 사용하기 위해 선언
 if (!function_exists('json_decode')) {
  function json_decode($content, $assoc=false) {
    if ($assoc) {
      $json = new Services_JSON(SERVICES_JSON_LOOSE_TYPE);
    }
    else {
      $json = new Services_JSON;
    }
    return $json->decode($content);
  }
}
  // json_encode를 사용하기 위해 선언
if (!function_exists('json_encode')) {
  function json_encode($content) {
    $json = new Services_JSON;
    return $json->encode($content);
  }
}
?>