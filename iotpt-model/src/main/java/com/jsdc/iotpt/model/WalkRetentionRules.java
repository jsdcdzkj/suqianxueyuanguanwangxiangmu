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
 * 滞留规则表
 */
@TableName("walk_retention_rules")
@Entity
@Table(name = "walk_retention_rules")
@Data
public class WalkRetentionRules implements Serializable {


    /**
     * 自增id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_RETENTION_RULES_ID", sequenceName = "SEQ_WALK_RETENTION_RULES_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_RETENTION_RULES_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    // 滞留名称
    private String retentionName;

    // 滞留区域
    private String retentionArea;

    // 滞留人数上限
    private Integer retentionPersonMax;

    // 滞留时间上限
    private long retentionTimeMax;

    // 预警方式
    private String earlyWarnType;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 时间组id
    private Integer timesId;

    // 状态 0启用 1禁用
    private Integer isFlag;

    // 逻辑删除
    private Integer isDelete;

    // 部门id
    private Integer deptId;

}
