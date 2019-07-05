package com.miaoshaproject.miaosha.error;

/**
 * Author: 陈祥煜
 * Date: 2019-05-25 11:38
 * Description: <描述>
 */
public enum  EnBusinessError implements CommonError {
    // 通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),

    UNKNOWN_ERROR(10002,"未知错误"),

    // 20000 开头的为用户信息相关错误定义
    USER_NOT_EXITS(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户名或者密码错误"),
    USER_NOT_LOGIN(20003, "用户未登录"),


    // 30000 开头的为交易信息错误定义
    STOCK_NOT_ENOUGH(30001,"库存不足"),



    ;

    private int errCode;
    private String errMsg;

    private EnBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
