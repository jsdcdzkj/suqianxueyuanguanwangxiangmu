package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@TableName("INDICATOR_CONFIG")
@Table(name = "INDICATOR_CONFIG")
@KeySequence(value = "SEQ_INDICATOR_CONFIG_ID")
@Data
@ApiModel(value ="指标配置")
public class IndicatorConfig extends Model<IndicatorConfig> implements Serializable {


    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_INDICATOR_CONFIG_ID", sequenceName = "SEQ_INDICATOR_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_INDICATOR_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "数据服务id")
    private Integer id;

    // 1：卫生间指标
    @ApiModelProperty(value = "业务类型")
    private Integer bizType;

    // 温度：TEMP | 湿度：HUMIDITY | 氨气：AMMONIA | PM2.5：PM25
    @ApiModelProperty(value = "子类型")
    private String subCode;

    @ApiModelProperty(value = "最低限度")
    private String minimum;

    @ApiModelProperty(value = "最大限度")
    private String maximum;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "RGB颜色")
    private String color;

    @ApiModelProperty(value = "描述")
    private String explain;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

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

}
