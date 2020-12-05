package com.jerusalem.seckill.dao;

import com.jerusalem.seckill.dataobject.SequenceDO;

/****
 * 持久层
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
public interface SequenceDOMapper {

    int deleteByPrimaryKey(String name);

    int insert(SequenceDO record);

    int insertSelective(SequenceDO record);

    SequenceDO selectByPrimaryKey(String name);

    SequenceDO getSequenceByName(String name);

    int updateByPrimaryKeySelective(SequenceDO record);

    int updateByPrimaryKey(SequenceDO record);
}