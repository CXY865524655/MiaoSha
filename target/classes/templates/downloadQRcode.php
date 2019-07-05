<?php
    $ticket="gQGK7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyekFVRkYwZGRjUGwxWG5NX05zMXUAAgRXdvZcAwSAOgkA";
    $url="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=".urlencode($ticket);
    $imageInfo=downloadWeixinFile($url);

    $filename="qrcode.jpg";
    $local_file=fopen($filename,'w');
    if (false !== $local_file){
        if (false !== fwrite($local_file, $imageInfo["body"])){
            fclose($local_file);
        }
    }
    function downloadWeixinFile($url){
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_HEADER, 0);
        curl_setopt($ch, CURLOPT_NOBODY, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $package = curl_exec($ch);
        $httpinfo = curl_getinfo($ch);
        curl_close($ch);
        return array_merge(array('body'=>$package), array('header'=>$httpinfo));
    }