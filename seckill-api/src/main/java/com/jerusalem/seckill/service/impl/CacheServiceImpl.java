package com.jerusalem.seckill.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jerusalem.seckill.service.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/****
 * 本地热点缓存操作接口实现
 * @author jerusalem
 * @date 2020-04-20 15:30:29
 */
@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String,Object> commonCache = null;

    /***
     * 初始化定义Cache配置
     */
    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                //设置缓存容器的初始容量为10
                .initialCapacity(10)
                //设置缓存中最大可以存储100个KEY,超过100个之后会按照LRU的策略移除缓存项
                .maximumSize(100)
                //设置写缓存后的过期时间
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    /***
     * 存值方法
     * @param key
     * @param value
     */
    @Override
    public void setCommonCache(String key, Object value) {
            commonCache.put(key,value);
    }

    /***
     * 取值方法
     * @param key
     * @return
     */
    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
