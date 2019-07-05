package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.service.model.UserModel;

/**
 * Author: 陈祥煜
 * Date: 2019-05-24 19:28
 * Description: <描述>
 */
public interface UserService {

    UserModel getUserById(Integer id) throws BusinessException;

    void register(UserModel userModel) throws BusinessException;

    /**
     *
     * @param telephone：用户注册的手机
     * @param password：加密后的密码
     * @throws BusinessException
     */
    UserModel validataLogin(String telephone, String encrptPassword) throws BusinessException;
}
