package com.miaoshaproject.miaosha.response;

/**
 * Author: 陈祥煜
 * Date: 2019-05-24 20:56
 * Description: <描述>
 */
public class CommonReturnType {

    private String status;

    private Object data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static CommonReturnType create(Object data){
        return create(data, "success");
    }

    public static CommonReturnType create(Object data, String status){
        CommonReturnType commonReturnType = new CommonReturnType();
        if (data != null){
            commonReturnType.setData(data);
        }
        commonReturnType.setStatus(status);
        return commonReturnType;
    }

}
