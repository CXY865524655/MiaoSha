<?php
header("Content-Type:text/html; charset=utf-8");
//$conn=mysqli_connect("localhost:3306","root","123456","vx");
$mysqliObj = new mysqli("w.rdc.sae.sina.com.cn:3306","o0xz4m0w30","xjhyw1x013ymwiw4zmzxjkiyxzly540l5ymy4li0","app_393198212");
//mysqli_query($conn,"set names 'utf8'");
$mysqliObj ->query("set names 'utf8'");


//$row=mysqli_fetch_assoc($result);
$studentname=$_GET["studentname"];
$ksnumber=$_GET["ksnumber"];
echo $studentname;
echo $ksnumber;


$sql="SELECT COUNT(*) ,stnum FROM student WHERE ksnum='$ksnumber' AND stname='$studentname'";
//$result=mysqli_query($conn,$sql);
$result=$mysqliObj ->query($sql);
$arr=$result->fetch_array();

//var_dump($arr);
echo $arr[1];

if($arr[0]>0){
    echo $arr[1];
    if($arr[1]==null){
        $stuNum=$mysqliObj ->query("SELECT MAX(stnum) FROM student");
        $num = $stuNum->fetch_array();
        $max = $num[0] + 1;
        //echo $st;
        $mysqliObj ->query("UPDATE student SET stnum=$max WHERE ksnum=$ksnumber");
        echo'信息认证完毕！请完善个人信息！';
        header("Location:");

    }else{
        echo'已完成认证';
        echo $arr[1];
    }
}else{

    echo '信息认证错误！请认真核对信息！';
    echo $arr[1];
}
?>