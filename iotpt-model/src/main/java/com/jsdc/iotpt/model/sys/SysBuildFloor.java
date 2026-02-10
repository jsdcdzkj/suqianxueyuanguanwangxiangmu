package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 楼层表(sys_build_floor)
 */
@Entity
@TableName("sys_build_floor")
@Table(name = "sys_build_floor")
@KeySequence(value = "SEQ_BUILD_FLOOR_ID")
@Data
public class SysBuildFloor extends Model<SysBuildFloor> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "buildFloorId", sequenceName = "SEQ_BUILD_FLOOR_ID", allocationSize = 1)
    @GeneratedValue(generator = "buildFloorId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    //楼层名称
    private String floorName;


    //所属楼宇主键 父级0
    private Integer dictBuilding;

    @Transient
    @TableField(exist = false)
    private String parentName;
    //描述
    private String memo;
    //排序
    private Integer sort;

    //是否删除图片
    @Transient
    @TableField(exist = false)
    private Boolean delPic;


    @Transient
    @TableField(exist = false)
    private Integer pageNo;

    @Transient
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "文件名")
    private String filename;
    @ApiModelProperty(value = "图片原文件名")
    private String originalFilename;

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

    @Transient
    @TableField(exist = false)
    private MultipartFile file;

    /**
     * 是否大屏展示 0：否 1：是
     */
    private Integer isLargeScreenDisplay;

    /**
     * 设备数量
     */
    @Transient
    @TableField(exist = false)
    private Long deviceCount;

    /**
     * 人员数量
     */
    private Integer personnelNumber;

    /**
     * 楼层面积
     */
    private String floorArea;

    /**
     * 人员密度
     */
    private String occupantDensity;


}
