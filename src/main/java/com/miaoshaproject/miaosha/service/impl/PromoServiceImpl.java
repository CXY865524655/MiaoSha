package com.miaoshaproject.miaosha.service.impl;

import com.miaoshaproject.miaosha.dao.PromoDOMapper;
import com.miaoshaproject.miaosha.dataobject.PromoDO;
import com.miaoshaproject.miaosha.service.PromoService;
import com.miaoshaproject.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Author: 陈祥煜
 * Date: 2019-06-03 12:18
 * Description: <描述>
 */
@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        // 获取秒杀商品
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        // dataobject -> model
        PromoModel promoModel = covertModelFromDataObject(promoDO);

        if(promoModel == null){
            return null;
        }
        // 判断当前时间是否为秒杀活动即将开始或正在进行
        DateTime dateTime = new DateTime();
        if (promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else {
            promoModel.setStatus(2);
        }
        return promoModel;
    }
    private PromoModel covertModelFromDataObject(PromoDO promoDO){
        if (promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;

    }
}
