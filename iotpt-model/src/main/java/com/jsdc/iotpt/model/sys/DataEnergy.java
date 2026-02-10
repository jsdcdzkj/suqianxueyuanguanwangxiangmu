package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@TableName("DATA_ENERGY")
@Table(name = "DATA_ENERGY")
@KeySequence(value = "DATA_ENERGY_ID")
@Data
public class DataEnergy extends Model<DataEnergy> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "energyId", sequenceName = "DATA_ENERGY_ID", allocationSize = 1)
    @GeneratedValue(generator = "energyId", strategy = GenerationType.SEQUENCE)
    private Integer id;



    //1楼宇 2楼层 3区域 4分项用电
    private String type;


    private String floorId;

    private String areaId;

    private String subitemId;

    private String buildId;
    //电量
    private String value;

    //日期
    private String dateStr;

}
