package com.jsdc.iotpt.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * UserRegisterVo
 *
 * @author 许森森
 * @date 2024/9/12
 */

@Data
@ApiModel(value = "用户注册信息")
public class UserRegisterVo {

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "公司名称")
    private String company;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "确认密码")
    private String repassword;



}
