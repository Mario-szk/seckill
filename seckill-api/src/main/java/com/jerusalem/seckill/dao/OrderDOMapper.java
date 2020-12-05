package com.jerusalem.seckill.dao;

import com.jerusalem.seckill.dataobject.OrderDO;

/****
 * 持久层
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
public interface OrderDOMapper {

    int deleteByPrimaryKey(String id);

    int insert(OrderDO record);

    int insertSelective(OrderDO record);

    OrderDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderDO record);

    int updateByPrimaryKey(OrderDO record);
}