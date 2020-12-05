package com.jerusalem.seckill.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/****
 * 用户领域模型
 * @author jerusalem
 * @date 2020-04-21 10:21:52
 */
@Data
public class UserModel implements Serializable{

    private Integer id;

    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotNull(message = "性别不能不填写")
    private Byte gender;

    @NotNull(message = "年龄不能不填写")
    @Min(value = 0,message = "年龄必须大于0岁")
    @Max(value = 150,message = "年龄必须小于150岁")
    private Integer age;

    @NotBlank(message = "手机号不能为空")
    private String telphone;

    private String registerMode;

    private String thirdPartyId;

    @NotBlank(message = "密码不能为空")
    private String encrptPassword;

}
