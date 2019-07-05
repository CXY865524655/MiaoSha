<?php
$appid="wxdacf5adda9fa429a";
$appsecret="d9d7939cbd5b9a6d9a962d0dffe63b6f";
$url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appid&secret=$appsecret";
$ch=curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
curl_setopt($ch, CURLOPT_VERIFYHOST, FALSE);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$output=curl_exec($ch);
curl_close($ch);
$jsoninfo=json_decode($output, true);
$access_token=$jsoninfo["access_token"];
$expires_in=$jsoninfo["expires_in"];
var_dump($access_token);
var_dump($expires_in);
?>


