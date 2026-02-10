package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人脸抓拍库
 */
@TableName("walk_capture")
@Entity
@Table(name = "walk_capture")
@Data
public class WalkCapture  implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_CAPTURE_ID", sequenceName = "SEQ_WALK_CAPTURE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_CAPTURE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 抓拍设备id
     */
    private int equipmentId;

    /**
     * 抓拍时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime captureTime;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 所属机构
     */
    private Integer deptId;

    /**
     * 是否已处理
     */
    private Integer hasDeal;

    private Integer isDelete;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Transient
    private String startTime;

    @Transient
    private String endTime;



}
