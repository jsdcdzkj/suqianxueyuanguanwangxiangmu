package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * 楼宇管理表(sys_build)
 */
@Entity
@TableName("sys_build")
@Table(name = "sys_build")
@KeySequence(value = "SEQ_SYS_BUILD_ID")
@Data
@ApiModel(value = "楼宇管理")
public class SysBuild extends Model<SysBuild> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "sysBuildId", sequenceName = "SEQ_SYS_BUILD_ID", allocationSize = 1)
    @GeneratedValue(generator = "sysBuildId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    //楼宇名称
    @ApiModelProperty(value = "楼宇名称")
    private String buildName;



    //所属楼宇主键 父级0
    @ApiModelProperty(value = "所属楼宇主键")
    private Integer dictBuilding;

    //父级楼宇
    @Transient
    @TableField(exist = false)
    private String parentName ;

    //位置
    private String place;
    //描述
    private String memo;


    @ApiModelProperty(value = "项目名称")
    private String xxmc;
    @ApiModelProperty(value = "建设单位（业主）")
    private String jsdw;
    @ApiModelProperty(value = "项目地址（详细地址）")
    private String xmdz;
    @ApiModelProperty(value = "总建筑面积")
    private String zjzmj;
    @ApiModelProperty(value = "地上层数")
    private String dscs;
    @ApiModelProperty(value = "地下层数")
    private String dxcs;
    @ApiModelProperty(value = "建设性质（新建、改建、扩建）")
    private String jsxz;
    @ApiModelProperty(value = "工程立项批准文号")
    private String gclxpzwh;
    @ApiModelProperty(value = "工程立项日期")
    private String gclxrq;
    @ApiModelProperty(value = "设计单位名称")
    private String sjdwmc;
    @ApiModelProperty(value = "施工单位名称")
    private String sgdwmc;
    //字典（buildingType）
    @ApiModelProperty(value = "建筑用途")
    private String jsyt;
    //字典（structureType）
    @ApiModelProperty(value = "结构类型")
    private String jglx;
    @ApiModelProperty(value = "建筑高度")
    private String jsgd;
    @ApiModelProperty(value = "层高")
    private String cg;
    @ApiModelProperty(value = "建筑耐火等级")
    private String jznhdj;
    @ApiModelProperty(value = "使用年限")
    private String synx;
    @ApiModelProperty(value = "染料种类及消耗量")
    private String rlzljxhl;
    //能源效率指标
    @ApiModelProperty(value = "节能标准")
    private String nyxlzb;
    @ApiModelProperty(value = "绿色建筑星级")
    private String lsjzxj;
    //供热（冷）面积及其相关能耗数据
    @ApiModelProperty(value = "供冷面积")
    private String glmj;
    @ApiModelProperty(value = "供冷能耗")
    private String glnh;
    @ApiModelProperty(value = "供热面积")
    private String grmj;
    @ApiModelProperty(value = "供热能耗")
    private String grnh;
    @ApiModelProperty(value = "环保设施配置情况")
    private String hbsspzqk;
    @ApiModelProperty(value = "容积率")
    private String rjl;
    @ApiModelProperty(value = "绿地率")
    private String ldl;
    @ApiModelProperty(value = "停车场配置数量")
    private String tccpzsl;
    @ApiModelProperty(value = "投资总额")
    private String tzze;
    @ApiModelProperty(value = "开工日期")
    private String kgrq;
    @ApiModelProperty(value = "竣工日期")
    private String jgrq;
    @ApiModelProperty(value = "验收日期")
    private String ysri;
    @ApiModelProperty(value = "验收结论")
    private String ysjl;
    @ApiModelProperty(value = "楼宇图片文件名")
    private String filename;
    @ApiModelProperty(value = "楼宇图片原文件名")
    private String originalFilename;


    @ApiModelProperty(value = "物业")
    private String propertyManagement;

    @ApiModelProperty(value = "物业费")
    private String propertyManagementFee;

 

    @ApiModelProperty(value = "车位月租金")
    private String parkingSpaceFee;

    @ApiModelProperty(value = "空调")
    private String airConditioner;

    @ApiModelProperty(value = "空调费用")
    private String airConditionerFee;

    @ApiModelProperty(value = "空调开放时长")
    private String airConditionerTime;

    @ApiModelProperty(value = "电梯数量")
    private String theNumberOfElevators;

    @ApiModelProperty(value = "网络")
    private String network;

    @ApiModelProperty(value = "月租金范围")
    private String money;

    @ApiModelProperty(value = "负责人")
    private String personInCharge;

    @ApiModelProperty(value = "负责人电话")
    private String phone;

    //是否删除图片
    @Transient
    @TableField(exist = false)
    private Boolean delPic ;

    @Transient
    @TableField(exist = false)
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    private Integer pageSize ;
    @Transient
    @TableField(exist = false)
    private MultipartFile file;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 更新人id
     */
    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;
}
