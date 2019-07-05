package com.miaoshaproject.miaosha.controller;

import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.error.EnBusinessError;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.service.OrderService;
import com.miaoshaproject.miaosha.service.model.OrderModel;
import com.miaoshaproject.miaosha.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: 陈祥煜
 * Date: 2019-06-01 15:07
 * Description: <描述>
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;


    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROMED})
    @ResponseBody
    // 封装下单请求
    public CommonReturnType createOrder(
            @RequestParam(name = "itemId")Integer itemId,
            @RequestParam(name = "amount")Integer amount,
            @RequestParam(name = "promoId", required = false)Integer promoId) throws BusinessException {
        // 获取用户的登陆信息
        Boolean is_login = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (is_login == null || !is_login.booleanValue()){
            throw new BusinessException(EnBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        httpServletRequest.getSession().getAttribute("");
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        return CommonReturnType.create(null);
    }
}
