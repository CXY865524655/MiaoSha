package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.service.model.OrderModel;

/**
 * Author: 陈祥煜
 * Date: 2019-06-01 12:09
 * Description: <描述>
 */
public interface OrderService {
    /** 两种方式：
     *  1.通过前端URL上传过来的秒杀活动id，然后下单接口内校验对应id是否属于对应
     *    商品且活动是否已开始
     *  2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
     *
     *  推荐使用1方案
     */

    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;



}
