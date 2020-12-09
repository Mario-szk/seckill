package com.jerusalem.seckill.utils;

import lombok.Data;

/****
 * 返回数据信息封装
 * @author jerusalem
 * @email 3276586184@qq.com
 * @date 2020-04-12 15:22:02
 */
@Data
public class CommonReturnType {
    //表明对应请求的返回处理结果 "success" 或 "fail"
    private String status;

    //若status=success,则data内返回前端需要的json数据
    //若status=fail，则data内使用通用的错误码格式
    private Object data;

    //定义一个通用的创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
}
