package com.miaoshaproject.miaosha.controller;

import com.miaoshaproject.miaosha.controller.viewobject.ItemVO;
import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.service.ItemService;
import com.miaoshaproject.miaosha.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: 陈祥煜
 * Date: 2019-05-31 12:24
 * Description: <描述>
 */
@Controller("item")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // 创建商品的controller
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType createItem(
            @RequestParam(name = "title")String title,
            @RequestParam(name = "description")String description,
            @RequestParam(name = "price") BigDecimal price,
            @RequestParam(name = "stock")Integer stock,
            @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {
        // 封装service请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        /**
         *  ItemModel 指的是执行完插入操作之后从数据库查询到的刚刚插入的数据
         */
        ItemModel itemForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = covertItemVOFromItemModel(itemForReturn);

        return CommonReturnType.create(itemVO);
    }

    /**
     * 显示商品信息
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getItemMessage")
    @ResponseBody
    private CommonReturnType getItemMessage(@RequestParam(name = "id")Integer id) throws BusinessException {
        ItemModel item = itemService.getItemById(id);
        ItemVO itemVO = covertItemVOFromItemModel(item);

        return CommonReturnType.create(itemVO);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/itemMessagePage")
    private String itemMessagePage(@RequestParam(name = "id")Integer id){
        return "getitem";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    @ResponseBody
    private CommonReturnType listItem(){
        List<ItemModel> modelList = itemService.listItem();
        List<ItemVO> itemVOList = modelList.stream().map(itemModel -> {
            ItemVO itemVO = this.covertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    private ItemVO covertItemVOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        // 判断是否有进行的秒杀活动
        if (itemModel.getPromoModel() != null){
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }

    @RequestMapping("/createItemPage")
    private String createItemPage(){
        return "createitem";
    }

    @RequestMapping("/listItemPage")
    private String listItemPage(){
        return "listItem";
    }
}
