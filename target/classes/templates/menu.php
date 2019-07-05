<?php
    $appid="wxdacf5adda9fa429a";
    $appsecret="d9d7939cbd5b9a6d9a962d0dffe63b6f";
    $url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appid&secret=$appsecret";
    $output=https_request($url);
    $jsoninfo=json_decode($output,true);
    $access_token = $jsoninfo["access_token"];
    $jsonmenu='{
                   "button": [
                       {
                           "name": "天气预报",
                           "sub_button": [
                               {
                                   "type": "click",
                                   "name": "广州天气",
                                   "key": "天气广州"
                               },
                               {
                                   "type": "click",
                                   "name": "深圳天气",
                                   "key": "天气深圳"
                               },
                               {
                                   "type": "click",
                                   "name": "北京天气",
                                   "key": "天气北京"
                               },
                               {
                                   "type": "click",
                                   "name": "上海天气",
                                   "key": "天气上海"
                               },
                               {
                                   "type": "view",
                                   "name": "本地天气",
                                   "url": "http://m.hao123.com/a/tianqi"
                               }
                           ]
                       },
                       {
                           "name": "军哥课堂",
                           "sub_button": [
                               {
                                   "type": "click",
                                   "name": "课堂简介",
                                   "key": "jgjyclass"
                               },
                               {
                                   "type": "click",
                                   "name": "军哥推荐",
                                   "key": "jgtj"
                               },
                               {
                                   "type": "click",
                                   "name": "你好啊",
                                   "key": "hello"
                               },
                               {
                                   "type": "click",
                                   "name": "创业分享",
                                   "key": "创业"
                               }
                           ]
                       }
                   ]
               }';
    $url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=".$access_token;
    $result=https_request($url, $jsonmenu);
    var_dump($result);
    function https_request($url, $data = null){
        $curl = curl_init();
        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, FALSE);
        curl_setopt($curl, CURLOPT_VERIFYHOST, FALSE);
        if(!empty($data)){
            curl_setopt($curl, CURLOPT_POST, 1);
            curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
        }
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
        $output = curl_exec($curl);
        curl_close($curl);
        return $output;
    }
?>


