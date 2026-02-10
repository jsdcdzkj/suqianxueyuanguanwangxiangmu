package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@TableName("DCYC_PARKING_NOTICE")
@Table(name = "DCYC_PARKING_NOTICE")
@KeySequence(value = "DCYC_PARKING_NOTICE_ID")
@Data
public class DcycParkingNotice extends Model<DcycParkingNotice> implements Serializable {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "DCYC_PARKING_NOTICE_ID", sequenceName = "DCYC_PARKING_NOTICE_ID", allocationSize = 1)
    @GeneratedValue(generator = "DCYC_PARKING_NOTICE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    /**
     * 消息ID
     */
    @ApiModelProperty(value = "消息ID")
    private Integer messageId;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    private String carPlate;

    /**
     * 派车时间
     */
    @ApiModelProperty(value = "派车时间")
    private String sendCardTime;

    /**
     * 使用人
     */
    @ApiModelProperty(value = "使用人")
    private String useUser;

    /**
     * 应停放地点
     */
    @ApiModelProperty(value = "应停放地点")
    private String parkingLocation;

    /**
     * 当前停放地点
     */
    @ApiModelProperty(value = "当前停放地点")
    private String currentLocation;

    /**
     * 偏差距离（km）
     */
    @ApiModelProperty(value = "偏差距离（km）")
    private String offsetDistance;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
