package com.miaoshaproject.miaosha.service.impl;

import com.miaoshaproject.miaosha.dao.ItemDOMapper;
import com.miaoshaproject.miaosha.dao.ItemStockDOMapper;
import com.miaoshaproject.miaosha.dataobject.ItemDO;
import com.miaoshaproject.miaosha.dataobject.ItemStockDO;
import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.error.EnBusinessError;
import com.miaoshaproject.miaosha.service.ItemService;
import com.miaoshaproject.miaosha.service.PromoService;
import com.miaoshaproject.miaosha.service.model.ItemModel;
import com.miaoshaproject.miaosha.service.model.PromoModel;
import com.miaoshaproject.miaosha.validator.ValidationResult;
import com.miaoshaproject.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: 陈祥煜
 * Date: 2019-05-31 11:24
 * Description: <描述>
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;


    private ItemDO covertItemDOFromItemModel(ItemModel itemModel) throws BusinessException {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private ItemStockDO covertItemStockDOFromItemModel(ItemModel itemModel) throws BusinessException {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    private ItemModel covertItemModelFromDataobject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR, result.getMsg());
        }
        // 实现ItemModel -> dataobject
        ItemDO itemDO = covertItemDOFromItemModel(itemModel);

        // 写入数据库
        /**
         * itemStockDO.setItemId(itemDO.getId()):
         *      执行完itemDOMapper.insertSelective(itemDO)后会返回ID给itemDO对象，此时要更新itemStockDO中的item_id
         */
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = covertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        // 返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }


    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> list = itemDOMapper.itemList();
        List<ItemModel> itemModels = list.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

            ItemModel itemModel = this.covertItemModelFromDataobject(itemDO, itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModels;
    }

    @Override
    public ItemModel getItemById(Integer id) throws BusinessException {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            return null;
        }
        // 获得库存
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        // 将dataobject -> model
        ItemModel itemModel = covertItemModelFromDataobject(itemDO, itemStockDO);

        // 获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if (promoModel != null && promoModel.getStatus().intValue() != 3){
            itemModel.setPromoModel(promoModel);

        }
        return itemModel;
    }

    /**
     * affectedRow: 受影响的行数
     *
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        if (affectedRow > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId, amount);
    }
}
