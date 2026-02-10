package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 巡更打卡
 */
@Entity
@TableName("PATROL_CLOCK")
@Table(name = "PATROL_CLOCK")
@KeySequence(value = "PATROL_CLOCK_ID")
@Data
public class PatrolClock extends Model<PatrolClock> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "patrolClockId", sequenceName = "PATROL_CLOCK_ID", allocationSize = 1)
    @GeneratedValue(generator = "patrolClockId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "任务ID")
    private Integer patrolTaskId;

    @ApiModelProperty(value = "设备id")
    private Integer deviceId;

    @ApiModelProperty(value = "打卡时间")
    private String signTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;
}
