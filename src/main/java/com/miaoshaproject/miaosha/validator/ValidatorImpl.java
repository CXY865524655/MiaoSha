package com.miaoshaproject.miaosha.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Set;


/**
 * Author: 陈祥煜
 * Date: 2019-05-30 20:20
 * Description: <描述>
 */
@Component
public class ValidatorImpl implements InitializingBean {
    private Validator validator;

    //实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        /**
         * constraintViolationSet.size() > 0: 说明有错误
         * forEach(): java8 特有的表达式
         */
        if (constraintViolationSet.size() > 0){
            // 有错误
            result.setHasErrors(true);
            constraintViolationSet.forEach(constraintViolation->{
                String errMsg = constraintViolation.getMessage();
                String propertyName = constraintViolation.getPropertyPath().toString();
               result.getErrorMsgMap().put(propertyName, errMsg);
            });
        }
        return  result;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        // 将Hibernate validator 通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }
}
