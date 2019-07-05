<?php
    define("TOKEN", "weixin");
    $wechatObj=new wechatCallbackapiTest();
    if(!isset($_GET['echostr'])){
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
            $tmpArr= array($token, $timestamp, $nonce);
            sort($tmpArr);
            $tmpStr = implode($tmpArr);
            $tmpStr = shal($tmpStr);

            if($tmpStr == $signature)
            {
                return true;
            }else{
                return false;
            }
        }
        public function responseMsg()
        {
            $postStr = $GLOBALS["HTTP_RAW_POST_DATA"];
            if(!empty($postStr))
            {
                $postObj = simplexml_load_string($postStr. 'SimpleXMLElement', LIBXML_NOCDATA);
                $RX_TYPE=trim($postObj->MsgType);
                switch($RX_TYPE)
                {
                    case "text":
                        $resultStr = $this->receiveText($postObj);
                        break;
                    case "event":
                        $resultStr = $this->receiveEvent($postObj);
                        break;
                    default:
                        $resultStr = "";
                        break;
                }
                echo $resultStr;
            }else{
                echo "";
                exit;
            }
        }
        private function reciveText($object)
        {
            $funFlag = 0;
            $contentStr = "你发送的内容为：".$object->Content;
            $resultStr = $this->transmitText($object, $contentStr, $funFlag);
            return $resultStr;
        }

        private function receiveEvent($object)
        {
            $contentStr ="";
            switch($object->Event)
            {
                   case "subscribe":
                    $contentStr = "欢迎关注军哥精英课堂！";
                   case "unsubscribe":
                    break;
                   case "CLICK":
                    switch($object->EventKey)
                    {
                        case "jgjyclass":
                            $contentStr[] = array("Title"=>"课堂简介",
                            "Description"=>"军哥精英课堂专注移动互联网应用开发和在线教育培训。",
                            "PicUrl"=>"http://discuz.comli.com/weixin/weather/icon/cartoon.jpg",
                            "Url"=>"http://www.chuanke.com/s4098217.html");
                            break;
                        case "jgtj":
                            $contentStr[] = array("Title"=>"军哥推荐",
                                                   "Description"=>"军哥精英课堂专注移动互联网应用开发和在线教育培训。",
                                                   "PicUrl"=>"http://discuz.comli.com/weixin/weather/icon/cartoon.jpg",
                                                   "Url"=>"http://www.chuanke.com/s4098217.html");
                            break;
                        default:
                            $contentStr[]=array("Title"=>"默认菜单回复",
                                                                                                                                                    "Description"=>"您正在访问的是军哥百度卓越IT在线教育培训网",
                                                                                                                                                    "PicUrl"=>"http://discuz.comli.com/weixin/weather/icon/cartoon.jpg",
                                                                                                                                                    "Url"=>"http://www.chuanke.com/s4098217.html");
                            break;
                    }
                    break;
                  default:
                    break;
            }
            if(is_array($contentStr)){
                $resultStr = $this->transmitNews($object, $contentStr);
            }else{
                $resultStr = $this->transmitText($object,$contentStr);
            }
            return $resultStr;
        }

        private function transmitText($object, $content, $funcFlag = 0)
        {
            $textTpl = "
                <xml>
                    <ToUserName><![CDATA[%s]]></ToUserName>
                    <FromUserName><![CDATA[%s]]></FromUserName>
                    <CreateTime>%s</CreateTime>
                    <MsgType><![CDATA[text]]></MsgType>
                    <Content><![CDATA[%s]]></Content>
                    <FuncFlag>%d</FuncFlag>
                </xml>
            ";
            $resultStr = sprintf($textTpl, $object->FromUserName, $object->ToUserName,time(), $content, $funcFlag);
            return $resultStr;
        }
        private function transmitNews($object, $arr_item, $funcFlag=0)
        {
            if(!is_array($arr_item))
                return;
            $itemTpl = "
                <item>
                     <Title><![CDATA[%s]]></Title>
                     <Description><![CDATA[%s]]></Description>
                     <PicUrl><![CDATA[%s]]></CreateTime>
                     <Url><![CDATA[%s]]></Url>
                </item>
            ";
            $item_str = "";
            foreach($arr_item as $item)
                $item_str.=sprintf($itemTpl, $item['Title'], $item['Description'], $item['PicUrl'], $item['Url']);
            $newsTpl="
                <xml>
                     <ToUserName><![CDATA[%s]]></ToUserName>
                     <FromUserName><![CDATA[%s]]></FromUserName>
                     <CreateTime>%s</CreateTime>
                     <MsgType><![CDATA[news]]></MsgType>
                     <Content><![CDATA[]]></Content>
                     <ArticleCount>%s</ArticleCount>
                     <Articles>$item_str</Articles>
                     <FuncFlag>%s</FuncFlag>
                </xml>
            ";
            $resultStr = sprintf($newsTpl, $object->FromUserName, $object->ToUserName,time(),count($arr_item), $funcFlag);
            return $resultStr;
        }
    }
?>


