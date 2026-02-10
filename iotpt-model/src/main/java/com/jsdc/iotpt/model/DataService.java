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
 * ClassName: DataService
 * Description: 数据服务
 * date: 2023/5/8 14:55
 *
 * @author bn
 */
@Entity
@TableName("data_service")
@Table(name = "data_service")
@KeySequence(value = "SEQ_DATA_SERVICE_ID")
@Data
@ApiModel(value ="数据服务")
public class DataService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "dataServiceId", sequenceName = "SEQ_DATA_SERVICE_ID", allocationSize = 1)
    @GeneratedValue(generator = "dataServiceId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="数据服务id")
    private Integer id;//主键
    @ApiModelProperty(value ="数据服务名称")
    private String name;//服务名称
    @ApiModelProperty(value ="数据服务类型")
    private Integer type;//服务类型
    @ApiModelProperty(value ="数据服务地址")
    private String address;//服务地址
    @ApiModelProperty(value ="数据服务请求方式")
    private String requestMethod;//请求方式
    @ApiModelProperty(value ="数据服务请求头")
    private String heads;//请求头
    @ApiModelProperty(value ="数据服务描述")
    private String serviceDesc;//服务描述
    @ApiModelProperty(value ="数据服务状态")
    private Integer state;//服务状态 0：开启 1：关闭
    @ApiModelProperty(value ="数据服务是否是自定义接口")
    private Integer isCustom;//自定义 是否是自定义接口 0：是 1：否

}
