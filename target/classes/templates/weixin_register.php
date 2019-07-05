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
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1" charset="utf-8">
    <link rel="stylesheet" href="https://apps.bdimg.com/libs/jquerymobile/1.4.5/jquery.mobile-1.4.5.min.css">
    <script src="https://apps.bdimg.com/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="https://apps.bdimg.com/libs/jquerymobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
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
<div data-role="page">
    <div data-role="header">
        <h1>完善信息</h1>
    </div>

    <div data-role="main" class="ui-content">
        <form method="post" action="demo_form.php">
            <div class="ui-field-contain">
                <label for="name">姓名：</label>
                <input type="text" name="name" id="name" value="<?php echo $userinfo['nickname'] ?>">
            </div>

            <div class="ui-field-contain">
                <label for="xuehao">学号：</label>
                <input type="text" name="xuehao" id="xuehao">
            </div>

            <div class="ui-field-contain">
                <label for="weixin">微信号：</label>
                <input type="text" name="weixin" id="weixin" value="<?php echo $userinfo['openid'] ?>">
            </div>

            <div class="ui-field-contain">
                <label for="sex">性别：</label>
                <input type="text" name="sex" id="sex">
            </div>

            <div class="ui-field-contain">
                <label for="phone">手机：</label>
                <input type="text" name="phone" id="phone">
            </div>

            <div class="ui-field-contain">
                <label for="sushe">宿舍：</label>
                <input type="text" name="sushe" id="sushe">
            </div>

            <div class="ui-field-contain">
                <label for="junfu">军服：</label>
                <input type="text" name="junfu" id="junfu" placeholder="size">
            </div>

            <div class="ui-field-contain">
                <label for="img0">照片：</label>
                <input type="file" name="file0" id="file0" multiple="multiple" />
            </div>
            <div class="ui-field-contain">
                <img src="" id="img0">
            </div>

            <input type="submit" data-inline="true" value="保存">
        </form>
    </div>
</div>
<script>
    $("#file0").change(function(){
        // getObjectURL是自定义的函数，见下面
        // this.files[0]代表的是选择的文件资源的第一个，因为上面写了 multiple="multiple" 就表示上传文件可能不止一个
        // ，但是这里只读取第一个
        var objUrl = getObjectURL(this.files[0]) ;
        // 这句代码没什么作用，删掉也可以
        // console.log("objUrl = "+objUrl) ;
        if (objUrl) {
            // 在这里修改图片的地址属性
            $("#img0").attr("src", objUrl) ;
            $("#img0").attr("width", 200) ;
            $("#img0").attr("height", 200) ;
        }
    }) ;
    //建立一個可存取到該file的url
    function getObjectURL(file) {
        var url = null ;
        // 下面函数执行的效果是一样的，只是需要针对不同的浏览器执行不同的 js 函数而已
        if (window.createObjectURL!=undefined) { // basic
            url = window.createObjectURL(file) ;
        } else if (window.URL!=undefined) { // mozilla(firefox)
            url = window.URL.createObjectURL(file) ;
        } else if (window.webkitURL!=undefined) { // webkit or chrome
            url = window.webkitURL.createObjectURL(file) ;
        }
        return url ;
    }
</script>
</body>
</html>
