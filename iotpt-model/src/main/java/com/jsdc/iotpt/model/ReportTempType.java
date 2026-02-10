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


@Entity
@TableName("REPORT_TEMP_TYPE")
@Table(name = "REPORT_TEMP_TYPE")
@KeySequence(value = "SEQ_REPORT_TEMP_TYPE_ID")
@Data
@ApiModel(value = "报告模版关联表")
public class ReportTempType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_REPORT_TEMP_TYPE_ID", sequenceName = "SEQ_REPORT_TEMP_TYPE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_REPORT_TEMP_TYPE_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ApiModelProperty(value = "报告类型Id")
    private String reportTypeId;

    @ApiModelProperty(value = "模版Id")
    private Integer tempId;

}
