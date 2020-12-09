package com.jerusalem.seckill.vo;

import lombok.Data;

/****
 * 用户
 * @author jerusalem
 * @email 3276586184@qq.com
 * @date 2020-04-18 11:12:47
 */
@Data
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
}
