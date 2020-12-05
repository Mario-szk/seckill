package com.jerusalem.seckill.dao;

import com.jerusalem.seckill.dataobject.ItemDO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/****
 * 持久层
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
public interface ItemDOMapper {

    List<ItemDO> listItem();

    int deleteByPrimaryKey(Integer id);

    int insert(ItemDO record);

    int insertSelective(ItemDO record);

    ItemDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);

    int increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);
}