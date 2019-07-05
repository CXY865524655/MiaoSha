<?php
$code = $_GET['code'];
$userinfo = getUserInfo($code);
function getUserInfo($code)
{
    $appid = "wxede727c82cac3adb";//appid
    $appsecret = "78fbfd1e463c5ebde4261c2d45cd48dd";//appsecret
    //oauth2的方式获得openid
    $access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=$appid&secret=$appsecret&code=$code&grant_type=authorization_code";
    $access_token_json = https_request($access_token_url);
    $access_token_array = json_decode($access_token_json, true);
    $openid = $access_token_array['openid'];
    //非oauth2的方式获得全局access_token
    $new_access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appid&secret=$appsecret";
    $new_access_token_json = https_request($new_access_token_url);
    $new_access_token_array = json_decode($new_access_token_json, true);
    $new_access_token = $new_access_token_array['access_token'];
    //全局access_token获得用户基本信息
    $userinfo_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=$new_access_token&openid=$openid";
    $userinfo_json = https_request($userinfo_url);
    $userinfo_array = json_decode($userinfo_json, true);
    return $userinfo_array;
}

function https_request($url)
{
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);//初始化CURL会话链接地址，设置要抓取的URL
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, FALSE);//对认证证书来源的检查，FALSE表示阻止对证书合法性的检查
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, FALSE);//从证书检查SSL加密算法是否存在
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);//设置将获得的结果保存在字符串中还是输出到屏幕上，0为直接输出屏幕，非0则不输出
    $output = curl_exec($curl);//执行请求，获取返回结果
    if (curl_errno($curl)) {
        return 'error' . curl_error($curl);
    }
    curl_close($curl);//关闭curl会话
    return $output;
}

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
    <title>OAuth2.0认证</title>
    <meta charset=utf-8>
    <meta name="viewport" content="width=device-width,user-scalable=no,initial-scale=1">
    <link rel="stylesheet" href="css/jquery.mobile-1.3.2.css">
    <script src="js/jquery-1.10.2.js"></script>
    <script src="js/jquery.mobile-1.3.2.js"></script>
</head>
<body>
<script type="text/javascript">
    document.addEventListener('WeixinJSBridgeReady', function onBirdgeReady() {
        WeixinJSBridge.call('hideOptionMenu');
    });
    document.addEventListener('WeixinJSBridgeReady', function onBirdgeReady() {
        WeixinJSBridge.call('hideToolbar');
    });
</script>
<div data-role="page" id="page1">
    <div data-role="content">
        <div style="text-align: center">
            <img style="width: 99%;height: %" src="<?php echo $userinfo['headimgurl'] ?>" alt="">
        </div>
        <ul data-role="listview" data-inset="true">
            <li>
                <p>
                <div class="fieldcontain">
                    <label for="subscribe">是否关注</label>
                    <input type="text" name="subscribe" id="subscribe" value="<?php echo $userinfo['subscribe'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="openid">OpenID</label>
                    <input type="text" name="openid" id="openid" value="<?php echo $userinfo['openid'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="nickname">昵称</label>
                    <input type="text" name="nickname" id="nickname" value="<?php echo $userinfo['nickname'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="sex">性别</label>
                    <input type="text" name="sex" id="sex" value="<?php echo $userinfo['sex'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="country">国家</label>
                    <input type="text" name="country" id="country" value="<?php echo $userinfo['country'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="'province">省份</label>
                    <input type="text" name="'province" id="'province" value="<?php echo $userinfo['province'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="city">城市</label>
                    <input type="text" name="city" id="city" value="<?php echo $userinfo['city'] ?>">
                </div>
                <div class="fieldcontain">
                    <label for="subscribe_time">关注时间</label>
                    <input type="text" name="subscribe_time" id="subscribe_time"
                           value="<?php echo $userinfo['subscribe_time'] ?>">
                </div>
                </p>
            </li>
        </ul>
    </div>
    <div data-theme="a" data-role="footer" data-position="fixed">
        <h3>军哥精英课堂</h3>
    </div>
</div>
</body>
</html>