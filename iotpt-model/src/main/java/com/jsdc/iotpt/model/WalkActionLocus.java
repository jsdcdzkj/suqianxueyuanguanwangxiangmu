package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 行为轨迹
 */
@TableName("walk_action_locus")
@Entity
@Table(name = "walk_action_locus")
@Data
public class WalkActionLocus {
    /**
     * 自增id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ACTION_LOCUS_ID", sequenceName = "SEQ_ACTION_LOCUS_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ACTION_LOCUS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 人脸id
     */
    private String faceId;

    /**
     * 身份证号
     */
    private String idcard;

    /**
     * 人脸类型
     */
    private String label;

    /**
     * 拍摄设备id
     */
    private Integer equipmentId;

    /**
     *  设备类型
     */
    private String equipmentType;

    /**
     *  设备值
     */
    private String equipmentValue;

    /**
     * 区域id
     */
    private Integer areaId;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 图片路径
     */
    private String filePath;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     *  星期
     */
    private Integer week;

    /**
     *  时分
     */
    private Float timeDivision;

    /**
     *  闸机 出:1,入:0 区分
     */
    private Integer turnstileType;

    /**
     *  轨迹类型：0:正常人;1:非法人
     */
    private Integer warnFlag;

    private Integer isDelete;

    // 是否处置 0未1处置
    private Integer isFlag;

    @Transient
    @TableField(exist = false)
    private Integer isWarn;

    @Transient
    @TableField(exist = false)
    private String base64;

    @Transient
    @TableField(exist = false)
    private String areaName;
}
