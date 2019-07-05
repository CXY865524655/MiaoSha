package com.miaoshaproject.miaosha.service.impl;

import com.miaoshaproject.miaosha.dao.UserDOMapper;
import com.miaoshaproject.miaosha.dao.UserPasswordDOMapper;
import com.miaoshaproject.miaosha.dataobject.UserDO;
import com.miaoshaproject.miaosha.dataobject.UserPasswordDO;
import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.error.EnBusinessError;
import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.UserModel;
import com.miaoshaproject.miaosha.validator.ValidationResult;
import com.miaoshaproject.miaosha.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Author: 陈祥煜
 * Date: 2019-05-24 19:26
 * Description: <描述>
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper passwordDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }

        /**
         * 根据用户ID获取加密的密码信息
         */
        UserPasswordDO passwordDO = passwordDOMapper.selectByUserId(userDO.getId());
        return covertFromDataObject(userDO, passwordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
            if (userModel == null) {
                throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR, "请输入对应的用户信息");
            }
            //if (StringUtils.isEmpty(userModel.getName())
            //        ||userModel.getAge() == null
            //        ||userModel.getGender() == null
            //        ||StringUtils.isEmpty(userModel.getTelphone())
            //){
            //    throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR, "请将信息填写完整");
            //}
            ValidationResult result = validator.validate(userModel);
            if (result.isHasErrors()){
                throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR, result.getMsg());
            }
            // 实现model->dataobject: 因为dao层认的是dataobject
            UserDO userDO = covertFromModel(userModel);
            try {
                userDOMapper.insertSelective(userDO);
            }catch (DuplicateKeyException e){
                throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已经注册");
            }
            userModel.setId(userDO.getId());
            UserPasswordDO userPasswordDO = covertPasswordFromModel(userModel);
            passwordDOMapper.insertSelective(userPasswordDO);
    }

    @Override
    public UserModel validataLogin(String telephone, String encrptPassword) throws BusinessException {

        // 通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telephone);
        if (userDO == null){
            throw new BusinessException(EnBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = passwordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = covertFromDataObject(userDO, userPasswordDO);
        // 比对用户信息内加密的密码是否和传输进来的密码相匹配
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())){
            throw new BusinessException(EnBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;

    }

    private UserDO covertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private UserPasswordDO covertPasswordFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }
    /**
     * 将两张表整合为真正处理业务的Model对象
     *
     * @param userDO          :用户表
     * @param userPasswordDO: 用户表对应的密码表
     * @return
     */
    public UserModel covertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);

        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
