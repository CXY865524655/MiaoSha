package com.miaoshaproject.miaosha.service.impl;

import com.alibaba.druid.sql.visitor.functions.Now;
import com.miaoshaproject.miaosha.dao.OrderDOMapper;
import com.miaoshaproject.miaosha.dao.SequenceDOMapper;
import com.miaoshaproject.miaosha.dataobject.OrderDO;
import com.miaoshaproject.miaosha.dataobject.SequenceDO;
import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.error.EnBusinessError;
import com.miaoshaproject.miaosha.service.ItemService;
import com.miaoshaproject.miaosha.service.OrderService;
import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.ItemModel;
import com.miaoshaproject.miaosha.service.model.OrderModel;
import com.miaoshaproject.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Author: 陈祥煜
 * Date: 2019-06-01 12:11
 * Description: <描述>
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        // 校验下单状态：1.下单的商品是否存在 2.用户是否合法 3.购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null){
            throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR,"商品不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null){
            throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR,"用户不存在");
        }
        if (amount <= 0 || amount > 99){
            throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量不能小于0或者超过99件");
        }

        // 校验活动信息
        if (promoId != null){
            // 校验对应活动是否存在这个商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            }
            // 校验活动是否正在进行
            if (itemModel.getPromoModel().getStatus().intValue() != 2){
                throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }
        // 落单减库存
        boolean result =  itemService.decreaseStock(itemId, amount);
        if (!result){
            throw new BusinessException(EnBusinessError.STOCK_NOT_ENOUGH);
        }

        // 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setPromoId(promoId);
        orderModel.setAmount(amount);
        if (promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }

        orderModel.setOrderAmount(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        // 生成交易流水号-订单号
        orderModel.setId(generateOrderNumber());
        OrderDO orderDO = covertDataObjectFromModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        // 加上商品的销量
        itemService.increaseSales(itemId, amount);
        //返回结果
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
     String generateOrderNumber(){
        // 规则：订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        // 前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        
        // 中间6位为自增序列
        // 获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i=0;i<6-sequenceStr.length();i++){
            stringBuilder.append("0");
        }
        stringBuilder.append(sequenceStr);
        // 最后2位为分库分表位,暂时这样写
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
    private OrderDO covertDataObjectFromModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderAmount().doubleValue());
        return orderDO;
    }
}
