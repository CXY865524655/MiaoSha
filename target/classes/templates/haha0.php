<?php

$openid=$_GET["openid"];
echo $openid;

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
<div data-role="page">
    <div style="width: 100%;height: 150px;"></div>
    <div data-role="main" class="ui-content" align="center">
        <div style="width: 400px;" align="center">
            <form action="http://393198212.applinzi.com/select.php" method="get" style="width: auto">
                <div class="ui-field-contain">
                    <label for="studentname">姓名：</label>
                    <input type="text" name="studentname" id="studentname">
                </div>
                <div class="ui-field-contain">
                    <label for="ksnumber">考生号：</label>
                    <input type="text" name="ksnumber" id="ksnumber">
                </div>
                <div class="ui-field-contain">
                    <input type="submit" value="信息认证">
                    <input type="hidden" name="openid" value="<?php echo $openid; ?>">
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>