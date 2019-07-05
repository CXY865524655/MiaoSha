package com.miaoshaproject.miaosha.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: 陈祥煜
 * Date: 2019-05-29 10:10
 * Description: <描述>
 */
public class ValidationResult {
    // 校验结果是否有错
    private boolean hasErrors = false;

    // 存放错误信息的map
    private Map<String, String> errorMsgMap = new HashMap<>();

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    /**
     * 将所有错误信息整合为一个输出ErrMag
     * @return
     */
    public String getMsg(){
        return StringUtils.join(errorMsgMap.values().toArray(), ",");
    }
}
