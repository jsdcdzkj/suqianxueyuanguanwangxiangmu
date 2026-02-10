package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lb
 * @description: 告警信息处理表
 * @date: 2023/5/8 17:55
 */
@Entity
@TableName("WARNING_DISPOSE")
@Table(name = "WARNING_DISPOSE")
@KeySequence(value = "SEQ_WARNING_DISPOSE_ID")
@Data
@ApiModel(value = "告警配置表")
public class WarningDispose implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WARNING_DISPOSE_ID", sequenceName = "SEQ_WARNING_DISPOSE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WARNING_DISPOSE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "配置名称主键id")
    private Integer id;
    @ApiModelProperty(value = "告警信息ID")
    private Integer warningInfoId;
    @ApiModelProperty(value = "处理人")
    private Integer disposeUserId;
    @ApiModelProperty(value = "处理时间")
    private Date disposeTime;
    @ApiModelProperty(value = "处理操作")
    private int disposeOperate;
    @ApiModelProperty(value = "处理说明")
    private String disposeIllustrate;
}
