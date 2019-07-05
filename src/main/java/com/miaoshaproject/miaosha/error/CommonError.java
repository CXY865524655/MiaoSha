package com.miaoshaproject.miaosha.error;

/**
 * Author: 陈祥煜
 * Date: 2019-05-25 11:35
 * Description: <描述>
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
