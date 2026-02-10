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
 * ClassName: PatrolData
 * Description: 历史记录
 * date: 2024/11/25 15:13
 *
 * @author bn
 */
@Entity
@TableName("PATROL_DATA")
@Table(name = "PATROL_DATA")
@KeySequence(value = "PATROL_DATA_ID")
@Data
public class PatrolData extends Model<PatrolData> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PATROL_DATA_ID", sequenceName = "PATROL_DATA_ID", allocationSize = 1)
    @GeneratedValue(generator = "PATROL_DATA_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "id")
    private Integer id;
    // 统计表id
    private Integer statisticId;
    // 增量id
    private Integer patrolDataId;
    // 巡更人员
    private String userName;
    //巡检时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date patrolTime;

    //巡检地点
    public String placeName;
    // 是否巡检 0已巡，1未巡
    public Integer isPatrol;
}
