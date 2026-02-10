package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: ConfigDeviceSubitem
 * @author: wp
 * @description: 设备分项表
 * @date: 2023/5/9 8:40
 */
@Entity
@TableName("CONFIG_DEVICE_SUBITEM")
@Table(name = "CONFIG_DEVICE_SUBITEM")
@KeySequence(value = "SEQ_CONFIG_DEVICE_SUBITEM_ID")
@Data
@ApiModel(value = "设备分项")
public class ConfigDeviceSubitem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CONFIG_DEVICE_SUBITEM_ID", sequenceName = "SEQ_CONFIG_DEVICE_SUBITEM_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIG_DEVICE_SUBITEM_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 分项
     */
    @ApiModelProperty(value = "分项名称")
    private String subitemName;

    /**
     * 分项编码
     */
    @ApiModelProperty(value = "分项编码")
    private String subitemCode;

    /**
     * 能源类型
     * 关联字典表数据
     * （device_energy_type）
     */
    @ApiModelProperty(value = "能源类型")
    private String energy_type;

    /**
     * 分项说明
     */
    @ApiModelProperty(value = "分项说明")
    private String illustrate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

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
    private Double val = 0D;


    //定额值
    @Transient
    @TableField(exist = false)
    private String ratedValue ;

    //去年定额值
    @Transient
    @TableField(exist = false)
    private String lastRatedValue ;


    //剩余额度
    @Transient
    @TableField(exist = false)
    private String residualCredit ;




    //实际能耗
    @Transient
    @TableField(exist = false)
    private String actualValue ;

    //今日
    @Transient
    @TableField(exist = false)
    private String nowValue ;

    //去年今日
    @Transient
    @TableField(exist = false)
    private String lastValue ;

    //昨日
    @Transient
    @TableField(exist = false)
    private String yesterdayValue ;

    //同比增长率
    @Transient
    @TableField(exist = false)
    private String yearOnYearGrowthRate ;

    //能耗环比
    @Transient
    @TableField(exist = false)
    private String chainRatio ;

    //占比
    @Transient
    @TableField(exist = false)
    private String percentage ;

    @Transient
    @TableField(exist = false)
    private List<SubmiteValue> child ;


    @Transient
    @TableField(exist = false)
    private List<Double> spreadList = new ArrayList<>();
}
