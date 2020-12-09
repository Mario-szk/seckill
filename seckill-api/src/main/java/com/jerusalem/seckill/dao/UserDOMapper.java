package com.jerusalem.seckill.dao;

import com.jerusalem.seckill.dataobject.UserDO;
import org.springframework.stereotype.Repository;

/****
 * 持久层
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
@Repository
public interface UserDOMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Integer id);

    UserDO selectByTelphone(String telphone);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}