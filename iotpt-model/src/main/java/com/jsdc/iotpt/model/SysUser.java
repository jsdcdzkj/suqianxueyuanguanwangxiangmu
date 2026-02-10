package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: SysUser
 * @author: wp
 * @description: 系统用户表
 * @date: 2023/5/8 13:55
 */
@Entity
@TableName("SYS_USER")
@Table(name = "SYS_USER")
@KeySequence(value = "SEQ_SYS_USER_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统用户")
public class SysUser extends Model<SysUser> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_USER_ID", sequenceName = "SEQ_SYS_USER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_USER_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "系统用户id")
    private Integer id;
    /**
     * 登录名
     */
    @ApiModelProperty(value = "登录名")
    private String loginName;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    private String realName;

    /**
     * 所属单位
     */
    @ApiModelProperty(value = "所属单位id")
    private Integer unitId;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门id")
    private Integer deptId;
    /**
     * 所属区域
     */
    // 字段调整, 暂时不用, 查找部门,一个部门对应多个区域
    @ApiModelProperty(value = "所属区域id")
    private Integer areaId;
    /**
     * 电话号码
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;
    /**
     * 是否启用 0:禁用 1:启用
     */
    @ApiModelProperty(value = "是否启用 0:禁用 1:启用")
    private Integer isEnable;



    @ApiModelProperty(value = "微信openId")
    private String wxOpenId;


    /**
     * 头像(只存储文件名称)
     */
    @ApiModelProperty(value = "头像")
    private String headPortrait;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    /**
     * 身份证号码
     */
    @ApiModelProperty(value = "身份证号码")
    private String cardId;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String company;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 人脸
     */
    @ApiModelProperty(value = "人脸")
    private String facePic;

    /**
     * 人脸id
     */
    @ApiModelProperty(value = "人脸id")
    private String facePicId;


    /**
     * 权限 区域ids
     */
    @ApiModelProperty(value = "权限 区域ids")
    private String authorityAreaIds;

    /**
     * 大华userid
     */
    @ApiModelProperty(value = "大华userid")
    private long dahuaUserId;
    /**
     * 大华code
     */
    @ApiModelProperty(value = "大华userid")
    private String dahuaCode;

    /**
     * 姓名全拼
     */
    @ApiModelProperty(value = "姓名全拼")
    private String nameSpelling;

    @ApiModelProperty(value = "是否领导 0是 1 否")
    private Integer isLeader;

    @ApiModelProperty(value = "cId")
    private String cId;

    @ApiModelProperty(value = "登录设备ID")
    private String loginCid;

    /**
     * 是否同步统一登录 0,否, 1,是
     */
    @ApiModelProperty(value = "是否同步统一登录")
    private String isloginty;

    /**
     * APP注册的游客
     */
    @ApiModelProperty(value = "APP注册的游客")
    private String isAppTourist;

    /**
     * M3oaId
     */
    @ApiModelProperty(value = "M3oaId")
    private String oaId;


    // 头像预览地址
    @Transient
    @TableField(exist = false)
    private String avatarUrl;
    // 头像下载地址
    @Transient
    @TableField(exist = false)
    private String avatarDownUrl;

    // 头像预览地址
    @Transient
    @TableField(exist = false)
    private String facePicUrl;
    // 头像下载地址
    @Transient
    @TableField(exist = false)
    private String facePicDownUrl;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String deptName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Integer> roleIds;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Integer> aiMenuIds;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<String> roleFlags;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<String> roleNames;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<String> aiMenuNames;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String roleNamesStr;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String aiMenuNamesStr;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String createUserName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String updateUserName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer roleId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    List<String> authority = new ArrayList<>();

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String notIds;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String oldPassWord;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer doorId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    List<Integer> ids = new ArrayList<>();
    // 权限 区域ids
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Integer> authorityAreaIdList;
    @Transient
    @TableField(exist = false)
    private List<String> doorIds;

    @Transient
    @TableField(exist = false)
    private List<String> mealsDishes;
    //是否预定 1:是 0：否
    @Transient
    @TableField(exist = false)
    private Integer isReserve;
    //是否就餐 1:是 0：否
    @Transient
    @TableField(exist = false)
    private Integer haveMeal;

    @Transient
    @TableField(exist = false)
    private String username;
    @Transient
    @TableField(exist = false)
    private String userId;
    @Transient
    @TableField(exist = false)
    private Integer recordId;
    @Transient
    @TableField(exist = false)
    private Integer foodId;
    //是否拥有包厢备餐权限 0否  1是
    @Transient
    @TableField(exist = false)
    private Integer boxTrue;
    //是否拥有包厢审批权限 0 否  1是
    @Transient
    @TableField(exist = false)
    private Integer isApproval;

    /**
     * 所属单位
     */
    @Transient
    @TableField(exist = false)
    private String unitName;
}
