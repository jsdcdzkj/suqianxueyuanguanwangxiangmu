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
 * ClassName: ChargingStation
 * Description: 充电桩表
 * date: 2023/11/1 10:45
 *
 * @author bn
 */
@Entity
@TableName("charge_station")
@Table(name = "charge_station")
@KeySequence(value = "SEQ_CHARGE_STATION_ID")
@Data
@ApiModel(value = "充电桩表")
public class ChargeStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "chargeStationId", sequenceName = "SEQ_CHARGE_STATION_ID", allocationSize = 1)
    @GeneratedValue(generator = "chargeStationId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "充电桩表id")
    private Integer id;


    // 编号
    @ApiModelProperty(value = "编号")
    private String stationCode;

    // 型号
    @ApiModelProperty(value = "型号")
    private String stationModel;

    // 电压
    @ApiModelProperty(value = "电压")
    private String stationVoltage;

    // 电流
    @ApiModelProperty(value = "电流")
    private String stationCurrent;

    // 品牌
    @ApiModelProperty(value = "品牌")
    private String stationBrand;

    // 充电类型
    @ApiModelProperty(value = "充电类型")
    private String chargeType;

    // 位置
    @ApiModelProperty(value = "位置")
    private String stationPosition;

    // 备注
    @ApiModelProperty(value = "备注")
    private String stationRemark;

    // 运行状态 0 充电 1.空闲
    @ApiModelProperty(value = "运行状态")
    private String status;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

}
