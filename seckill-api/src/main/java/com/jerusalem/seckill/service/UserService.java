package com.jerusalem.seckill.service;

import com.jerusalem.seckill.error.BusinessException;
import com.jerusalem.seckill.model.UserModel;

/****
 * 用户接口
 * @author jerusalem
 * @date 2020-04-19 13:44:19
 */
public interface UserService {

    /***
     * 通过用户ID获取用户对象的方法
     * @param id
     * @return
     */
    UserModel getUserById(Integer id);

    /***
     * 通过缓存获取用户对象
     * @param id
     * @return
     */
    UserModel getUserByIdInCache(Integer id);

    /***
     * 注册
     * @param userModel
     * @throws BusinessException
     */
    void register(UserModel userModel) throws BusinessException;

    /***
     * 验证登录
     * @param telphone 用户注册手机
     * @param encrptPassword 用户加密后的密码
     * @return
     * @throws BusinessException
     */
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
}
