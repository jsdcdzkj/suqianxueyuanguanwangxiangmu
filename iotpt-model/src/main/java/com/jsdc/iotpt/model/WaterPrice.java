package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@TableName("WATER_PRICE")
@Table(name = "WATER_PRICE")
@KeySequence(value = "WATER_PRICE_ID")
@Data
public class WaterPrice extends Model<WaterPrice> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "waterPriceId", sequenceName = "WATER_PRICE_ID", allocationSize = 1)
    @GeneratedValue(generator = "waterPriceId", strategy = GenerationType.SEQUENCE)
    private Integer id;


    /**
     * 车牌号码
     */
    private String price;


    @Transient
    @TableField(exist = false)
    private String endTime ;
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

}
