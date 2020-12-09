package com.jerusalem.seckill.dao;

import com.jerusalem.seckill.dataobject.UserPasswordDO;
import org.springframework.stereotype.Repository;

/****
 * 持久层
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
@Repository
public interface UserPasswordDOMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UserPasswordDO record);

    int insertSelective(UserPasswordDO record);

    UserPasswordDO selectByPrimaryKey(Integer id);

    UserPasswordDO selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserPasswordDO record);

    int updateByPrimaryKey(UserPasswordDO record);
}