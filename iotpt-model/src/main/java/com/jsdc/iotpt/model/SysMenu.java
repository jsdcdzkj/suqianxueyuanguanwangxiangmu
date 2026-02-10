package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @projectName: IOT
 * @className: SysMenu
 * @author: wp
 * @description: 菜单表
 * @date: 2023/5/8 16:57
 */
@Entity
@TableName("SYS_MENU")
@Table(name = "SYS_MENU")
@KeySequence(value = "SEQ_SYS_MENU_ID")
@Data
@ApiModel(value = "菜单")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_MENU_ID", sequenceName = "SEQ_SYS_MENU_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_MENU_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 父级菜单
     */
    @ApiModelProperty(value = "父级菜单")
    private Integer parentId;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 路由名称
     */
    @ApiModelProperty(value = "路由名称")
    private String routerName;

    /**
     * 路由链接
     */
    @ApiModelProperty(value = "路由链接")
    private String routerUrl;

    /**
     * vue文件路径
     */
    @ApiModelProperty(value = "vue文件路径")
    private String vueUrl;

    /**
     * 重定向类型
     */
    @ApiModelProperty(value = "重定向类型")
    private String redirectType;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;

    /**
     * 是否显示 0：不显示 1：显示
     */
    @ApiModelProperty(value = "是否显示 0：不显示 1：显示")
    private Integer isShow;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 菜单类型 1:菜单 2:按钮
     */
    @ApiModelProperty(value = "单类型 1:菜单 2:按钮")
    private Integer menuType;

    /**
     * 所属系统
     */
    @ApiModelProperty(value = "所属系统")
    private Integer systemId;

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
     * 编码标识（唯一）
     */
    @ApiModelProperty(value = "编码标识")
    private String code;
}
