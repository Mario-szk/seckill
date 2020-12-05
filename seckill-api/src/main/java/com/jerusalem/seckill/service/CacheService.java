package com.jerusalem.seckill.service;

/****
 * 本地热点缓存操作接口
 * @author jerusalem
 * @date 2020-04-20 15:28:19
 */
public interface CacheService {

    /***
     * 存值方法
     * @param key
     * @param value
     */
    void setCommonCache(String key, Object value);

    /***
     * 取值方法
     * @param key
     * @return
     */
    Object getFromCommonCache(String key);
}
