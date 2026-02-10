package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Entity
@TableName("STALL_CAR_VIDEO")
@Table(name = "STALL_CAR_VIDEO")
@KeySequence(value = "STALL_CAR_VIDEO_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StallCarVideo extends Model<StallCarVideo> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "stallCarVideoId", sequenceName = "STALL_CAR_VIDEO_ID", allocationSize = 1)
    @GeneratedValue(generator = "stallCarVideoId", strategy = GenerationType.SEQUENCE)
    private Integer id;



    //车牌号码
    private String busNumber;
    //车牌类型 0 蓝牌 1绿牌 2黄牌
    private String plateType;
    //停入时间
    private String stopTime;
    //驶离时间
    private String departureTime;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    //停车时长
    @Transient
    @TableField(exist = false)
    private String residenceTime;




}
