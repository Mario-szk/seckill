package com.jerusalem.seckill.dao;

import com.jerusalem.seckill.dataobject.PromoDO;
import org.springframework.stereotype.Repository;

/****
 * 持久层
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
@Repository
public interface PromoDOMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PromoDO record);

    int insertSelective(PromoDO record);

    PromoDO selectByPrimaryKey(Integer id);

    PromoDO selectByItemId(Integer itemId);

    int updateByPrimaryKeySelective(PromoDO record);

    int updateByPrimaryKey(PromoDO record);
}