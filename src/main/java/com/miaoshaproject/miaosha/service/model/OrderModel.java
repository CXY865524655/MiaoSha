package com.miaoshaproject.miaosha.service.model;

/**
 * Author: 陈祥煜
 * Date: 2019-06-01 11:45
 * Description: <描述>
 */

import java.math.BigDecimal;

/**
 * 用户下单的交易模型
 */
public class OrderModel {

    // 一般企业及开发都是有明确的日期+其他标识组成的String id, eg:2018102112828
    private String id;

    // 购买商品的用户ID
    private Integer userId;

    // 购买的商品ID
    private Integer itemId;

    // 购买的数量
    private Integer amount;

    // 购买的总价,若promoId非空，则表示秒杀商品价格
    private BigDecimal orderPrice;

    // 若非空，则表示是以秒杀商品方式下单
    private Integer promoId;

    // 购买商品的单价,若promoId非空，则表示秒杀商品价格
    private BigDecimal itemPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderAmount() {
        return orderPrice;
    }

    public void setOrderAmount(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", amount=" + amount +
                ", orderPrice=" + orderPrice +
                ", promoId=" + promoId +
                ", itemPrice=" + itemPrice +
                '}';
    }
}
