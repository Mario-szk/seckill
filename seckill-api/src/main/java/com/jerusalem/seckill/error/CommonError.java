package com.jerusalem.seckill.error;

/**
 * Created by hzllb on 2018/11/13.
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);


}
