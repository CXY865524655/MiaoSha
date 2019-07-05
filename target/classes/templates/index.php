<?php

define("TOKEN", "weixin");
$wechatObj = new wechatCallbackapiTest();
if (!isset($_GET['echostr'])) {
    $wechatObj->responseMsg();
}else{
    $wechatObj->valid();
}

class wechatCallbackapiTest
{
    public function valid()
    {
        $echoStr = $_GET["echostr"];
        if($this->checkSignature()){
            echo $echoStr;
            exit;
        }
    }

    private function checkSignature()
    {
        $signature = $_GET["signature"];
        $timestamp = $_GET["timestamp"];
        $nonce = $_GET["nonce"];

        $token = TOKEN;
        $tmpArr = array($token, $timestamp, $nonce);
        sort($tmpArr);
        $tmpStr = implode( $tmpArr );
        $tmpStr = sha1( $tmpStr );

        if( $tmpStr == $signature ){
            return true;
        }else{
            return false;
        }
    }

    public function responseMsg()
    {
        $postStr = $GLOBALS["HTTP_RAW_POST_DATA"];
        if (!empty($postStr)){
            $this->logger("R".$postStr);
            $postObj = simplexml_load_string($postStr, 'SimpleXMLElement', LIBXML_NOCDATA);
            $RX_TYPE = trim($postObj->MsgType);
            switch ($RX_TYPE)
            {
                case "text":
                    $resultStr = $this->receiveText($postObj);
                    break;
                case "event":
                    $resultStr = $this->receiveEvent($postObj);//处理事件
                    break;
                case "location":
                    $resultStr = $this->receiveLocation($postObj);
                    break;
            }
            $this->logger("T".$resultStr);
            echo $resultStr;
        }else {
            echo "";
            exit;
        }
    }

    private function receiveText($object)
    {$keyword = trim($object->Content);
        $category = substr($keyword, 0, 6);
        $entity = trim(substr($keyword, 6, strlen($keyword)));
        switch ($category){
            case "附近":
                include("location.php");
                $location = getLocation($object->FromUserName);
                if (is_array($location)){
                    include("mapbaidu.php");
                    $content = catchEntitiesFromLocation($entity, $location["locationX"], $location["locationY"], "5000");

                }else{
                    $content = $location;
                }
                break;
            default:
                $content  = $object->FromUserName;
                break;
        }
        if (is_array($content)){
            $resultStr = $this->transmitNews($object, $content);
        }else{
            $resultStr = $this->transmitText($object, $content);
        }
        return $resultStr;

    }

    //处理接收事件
    private function receiveEvent($object)
    {
        $contentStr = "";
        switch ($object->Event)
        {
            case "subscribe"://关注事件
                $contentStr = "欢迎关注军哥精英课堂！可先发送地理位置，再发送“附近+关键词”查询相关内容！";
                break;
            case "unsubscribe"://取消关注
                $contentStr = "取消关注";
                break;
        }
        $resultStr = $this->transmitText($object, $contentStr);
        return $resultStr;
    }

    //文本消息回复
    private function transmitText($object, $content)
    {
        $textTpl="<xml>
            <ToUserName><![CDATA[%s]]></ToUserName>
            <FromUserName><![CDATA[%s]]></FromUserName>
            <CreateTime>%s</CreateTime>
            <MsgType><![CDATA[text]]></MsgType>
            <Content><![CDATA[%s]]></Content>
            </xml>";

        $resultStr = sprintf($textTpl, $object->FromUserName, $object->ToUserName, time(), $content);
        return $resultStr;
    }
private function receiveLocation($object){
        include("location.php");
        $content = setLocation($object->FromUserName, (string)$object->Location_X,(string)$object->Location_Y);
        $result = $this->transmitText($object, $content);
        return $result;
}
    //图文消息回复
    private function transmitNews($object, $arr_item)
    {
        if(!is_array($arr_item))
            return;
        $itemTpl = " <item>
             <Title><![CDATA[%s]]></Title>
             <Description><![CDATA[%s]]></Description>
             <PicUrl><![CDATA[%s]]></PicUrl>
             <Url><![CDATA[%s]]></Url>
        </item>";
        $item_str = "";
        foreach ($arr_item as $item)
            $item_str .= sprintf($itemTpl, $item['Title'], $item['Description'],$item['PicUrl'], $item['Url']);
        $newsTpl = "<xml>
            <ToUserName><![CDATA[%s]]></ToUserName>
            <FromUserName><![CDATA[%s]]></FromUserName>
            <CreateTime>%s</CreateTime>
            <MsgType><![CDATA[news]]></MsgType>
            <Content><![CDATA[]]></Content>
            <ArticleCount>%s</ArticleCount>
            <Articles>
            $item_str
            </Articles>
            </xml>";
        $resultStr = sprintf($newsTpl, $object->FromUserName, $object->ToUserName, time(),
            count($arr_item));
        return $resultStr;
    }

    private function logger($log_content){
        if (isset($_SERVER['HTTP_BAE_ENV_APPID'])){
            require_once "BaeLog.class.php";
            $logger = BaeLog::getInstance();
            $logger -> logDebug($log_content);

        }else if (isset($_SERVER['HTTP_APPNAME'])){
            sae_set_display_errors(false);
            sae_debug($log_content);
            sae_set_display_errors(true);
        }else if ($_SERVER['REMOTE_ADDR'] != "127.0.0.1"){
            $max_size=10000;
            $log_filename="log.xml";
            if (file_exists($log_filename) and (abs(filesize($log_filename))>$max_size)){
                unlink($log_filename);
            }
            file_put_contents($log_filename, date('H:i:s')."".$log_content."\r\n", FILE_APPEND);
        }
    }
}
?>