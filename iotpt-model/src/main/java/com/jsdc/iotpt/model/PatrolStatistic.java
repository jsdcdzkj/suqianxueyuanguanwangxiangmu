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

/**
 * ClassName: PatrolStatistic
 * Description: 电子巡更统计
 * date: 2024/11/28 10:22
 *
 * @author bn
 */
@Entity
@TableName("PATROL_STATISTIC")
@Table(name = "PATROL_STATISTIC")
@KeySequence(value = "PATROL_STATISTIC_ID")
@Data
public class PatrolStatistic extends Model<PatrolStatistic> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PATROL_STATISTIC_ID", sequenceName = "PATROL_STATISTIC_ID", allocationSize = 1)
    @GeneratedValue(generator = "PATROL_STATISTIC_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "id")
    private Integer id;

    // 线路名称
    private String lineName;

    // 巡更日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date patrolDate;


    // 计划巡更时长 60
    public String planTime;

    // 实际巡更时长
    public String actualTime;

    // 巡更结果 0正常 1.漏巡
    public Integer patrolResult;

    // 增量id
    private Integer patrolDataId;

    // 巡更人员 安全组
    public String userName;

    // 同步时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date syncTime;

}
