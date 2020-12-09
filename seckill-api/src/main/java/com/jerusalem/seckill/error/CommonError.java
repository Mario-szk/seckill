package com.jerusalem.seckill.error;

/****
 * 通用错误封装
 * @author jerusalem
 * @email 3276586184@qq.com
 * @date 2020-04-15 15:22:02
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
