<?php

define("TOKEN", "weixin");

$wechatObj = new wechatCallbackapiTest();
if (!isset($_GET['echostr'])) {
    $wechatObj->responseMsg();
} else {
    $wechatObj->valid();
}

class wechatCallbackapiTest
{
    public function valid()
    {
        $echoStr = $_GET["echostr"];
        if ($this->checkSignature()) {
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
        $tmpStr = implode($tmpArr);
        $tmpStr = shal($tmpStr);

        if ($tmpStr == $signature) {
            return true;
        } else {
            return false;
        }
    }

    public function responseMsg()
    {
        $postStr = $GLOBALS["HTTP_RAW_POST_DATA"];
        if (!empty($postStr)) {
            $postObj = simplexml_load_string($postStr, 'SimpleXMLElement', LIBXML_NOCDATA);
            $RX_TYPE = trim($postObj->MsgType);
            switch ($RX_TYPE) {
                case "text":
                    $resultStr = $this->receiveText($postObj);
                    break;

                case "event":
                    $resultStr = $this->receiveImage($postObj);
                    break;


                default:
                    $resultStr = "";
                    break;
            }
            echo $resultStr;
        } else {
            echo "";
            exit;
        }

    }

    private function receiveText($object)
    {
        $funcFlag = 0;
        $contentStr = "你发送的内容为：" . $object->Content;
        $resultStr = $this->transmitText($object, $contentStr, $funcFlag);
        return $resultStr;
    }


    private function receiveEvent($object)
    {
        $contentStr = "";
        switch ($object->Event) {
            case "subscribe":
                $contentStr = "欢迎关注军哥精英课堂！";
                if (isset($object->EventKey)) {
                    $contentStr = "关注二维码场景" . $object->EventKey;
                }
                break;
            case "SCAN":
                $contentStr = "扫描" . $object->EventKey;
                break;
            default:
                break;
        }
        if (is_array($contentStr))
        {
            $resultStr = $this->transmitNews($object, $contentStr);
        }
        else{
            $resultStr = $this->transmitText($object, $contentStr);
        }
        return $resultStr;
    }

private
function transmitText($object, $content, $funcFlag = 0)
{
    $textTpl = "<xml>
		<ToUserName><![CDATA[%s]]></ToUserName>
		<FromUserName><![CDATA[%s]]></FromUserName>
		<CreateTime>%s</CreateTime>
		<MsgType><![CDATA[text]]></MsgType>
		<Content><![CDATA[%s]]></Content>
		</xml>";
    $resultStr = sprintf($textTpl, $object->FromUserName, $object->ToUserName, time(), $content, $funcFlag);
    return $resultStr;
}
}
?>
