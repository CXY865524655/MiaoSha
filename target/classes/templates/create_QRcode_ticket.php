<?php
    $access_token="22_3Lf8YQ6HrfZ3uA9lKY8RUzO6tLDeASWlx36noY3a095y8TrvJs0ESwqsjuOnUbCjkBzjMUQDrNdRPZrRWJze7Q7hZBlUieWb8A3bn8G6ibPvZAxQCVVaK4hHH67DeFEPai97Z1hfe0-F6i4NVUReABAGFC";
    $qrcode="{expire_seconds: 604800, action_name: QR_SCENE, action_info : {scene:{ scene_id : 12345}}}
";
    $url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=$access_token";
    $result=https_request($url, $qrcode);
    var_dump($result);
    $jsoninfo=json_decode($result, true);
    $ticket=$jsoninfo["ticket"];
    var_dump($ticket);
    function https_request($url, $data=null){
        $curl = curl_init();
        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, FALSE);
        curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, FALSE);
        if (!empty($data)){
            curl_setopt($curl, CURLOPT_POST, 1);
            curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
        }
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
        $output=curl_exec($curl);
        var_dump($output);
        curl_close($curl);
        return $output;
    }
?>