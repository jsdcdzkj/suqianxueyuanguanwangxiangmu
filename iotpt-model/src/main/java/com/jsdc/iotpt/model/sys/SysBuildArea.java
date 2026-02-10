package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 区域表(sys_build_area)
 */
@Entity
@TableName("sys_build_area")
@Table(name = "sys_build_area")
@KeySequence(value = "SEQ_BUILD_AREA_ID")
@Data
public class SysBuildArea extends Model<SysBuildArea> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "buildAreaId", sequenceName = "SEQ_BUILD_AREA_ID", allocationSize = 1)
    @GeneratedValue(generator = "buildAreaId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    //区域名称
    private String areaName;
    //区域编码
    private String code;
    //区域类型
    private String areaType;
    //安装地址
    private String address;

    //所属楼宇 最父级默认-1
    private Integer buildFloorId;

    //所属楼层
    private Integer floorId;

    @Transient
    @TableField(exist = false)
    private String floorName;

    @Transient
    @TableField(exist = false)
    private String buildName;

    @Transient
    @TableField(exist = false)
    private List<SysBuildFloor> sysBuildFloorList;
    //描述
    private String memo;

    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 电话|手机号
     */
    private String phones;

    /**
     * 创建人id
     */
    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;

    /**
     * 是否大屏展示 0：否 1：是
     */
    private Integer isLargeScreenDisplay;

    /**
     * 排序
     */
    private Integer sort;

    @Transient
    @TableField(exist = false)
    private List<SysBuildArea> children;
}
