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
 *  重点人员预警库 借助字典表将各种各样的预警都存入到预警库中 取值时通过字典将对应的的预警取出来
 */
@TableName("walk_focal_person_early_warn")
@Entity
@Table(name = "walk_focal_person_early_warn")
@Data
public class WalkFocalPersonEarlyWarn implements Serializable {

    /**
     * 自增id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_FOCAL_PERSON_EARLY_WARN_ID", sequenceName = "SEQ_FOCAL_PERSON_EARLY_WARN_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_FOCAL_PERSON_EARLY_WARN_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    // 人脸id
    private String faceId;


    // 人员类型
    private String label;

    // 是否处置 0 不处置;1 处置
    private Integer isManage;

    // 处置人
    private Integer userId;

    // 处置时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime manageTime;

    // 处置方式
    private String manageType;


    // 预警类型 dict
    private String earlyWarnType;

    // 预警值
    private String earlyWarnValue;

    // 部门id
    private Integer deptId;

    // 设备id
    private Integer equipmentId;

    // 备注
    private String remarks;

    // 拍摄时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime shootTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 星期
    private Integer week;

    // 时分
    private Float timeDivision;

    private String filePath;

    // 逻辑删除
    private Integer isDelete;




}
