package com.miaoshaproject.miaosha.controller;

import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.error.EnBusinessError;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: 陈祥煜
 * Date: 2019-05-25 20:12
 * Description:
 *      所有Controller都想要的通用异常处理类，所以独立出来
 */
public class BaseController {
    public static final String CONTENT_TYPE_FROMED = "application/x-www-form-urlencoded";
    // 定义@ExceptionHandler解决未被controller层吸收的Exception异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String, Object> responseData = new HashMap<>();
        if (ex instanceof BusinessException){
            // 捕获到的是BusinessException的反序列化的JSON格式，所以我们要强转一下
            BusinessException exception = (BusinessException)ex;
            responseData.put("errCode", exception.getErrCode());
            responseData.put("errMsg", exception.getErrMsg());
        }else {
            // 捕获到的是BusinessException的反序列化的JSON格式，所以我们要强转一下
            responseData.put("errCode", EnBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EnBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData, "fail");
    }
}
