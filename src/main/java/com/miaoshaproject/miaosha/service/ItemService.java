package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.service.model.ItemModel;

import java.util.List;

/**
 * Author: 陈祥煜
 * Date: 2019-05-31 11:21
 * Description: <描述>
 */
public interface ItemService {

    // 创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    // 商品列表浏览
    List<ItemModel> listItem();

    // 商品详情浏览
    ItemModel getItemById(Integer id) throws BusinessException;

    // 库存扣减
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    // 销量增加
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;

}
