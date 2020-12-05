package com.jerusalem.seckill.dataobject;

import lombok.Data;

/****
 * 实体类
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
@Data
public class ItemStockDO {

    private Integer id;

    private Integer stock;

    private Integer itemId;
}