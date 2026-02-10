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

/**
 * ClassName: DataParam
 * Description: 服务参数
 * date: 2023/5/8 15:02
 *
 * @author bn
 */
@Entity
@TableName("data_param")
@Table(name = "data_param")
@KeySequence(value = "SEQ_DATA_PARAM_ID")
@Data
@ApiModel(value ="服务参数")
public class DataParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "dataParamId", sequenceName = "SEQ_DATA_PARAM_ID", allocationSize = 1)
    @GeneratedValue(generator = "dataParamId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="服务参数主键")
    private Integer id;//主键
    @ApiModelProperty(value ="服务参数名称")
    private String key;//参数名称
    @ApiModelProperty(value ="服务参数值")
    private String value;//参数值
    @ApiModelProperty(value ="服务参数类型")
    private String type;//数据类型
    @ApiModelProperty(value ="服务参数说明")
    private String paramDesc;//说明
    @ApiModelProperty(value ="服务参数关联数据服务ID")
    private Integer serviceId;//数据服务ID

}
