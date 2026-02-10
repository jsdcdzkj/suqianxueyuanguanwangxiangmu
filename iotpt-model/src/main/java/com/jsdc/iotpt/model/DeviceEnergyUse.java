package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 *
 *用能规划
 * @author wzn
 * @date 2024/11/26 15:15
 */


@Entity
@TableName("DEVICE_ENERGYUSE")
@Table(name = "DEVICE_ENERGYUSE")
@KeySequence(value = "DEVICE_ENERGYUSE_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEnergyUse implements Serializable {



    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "deviceEnergyUseId", sequenceName = "DEVICE_ENERGYUSE_ID", allocationSize = 1)
    @GeneratedValue(generator = "deviceEnergyUseId", strategy = GenerationType.SEQUENCE)
    private Integer id;



    //定额值
    private String ratedValue;



    //时间
    private String searchDate;

    //能耗比管控值
    private String controlValue;

    @Transient
    @TableField(exist = false)
    private String year;


    //实际值
    @Transient
    @TableField(exist = false)
    private Double val;



}
