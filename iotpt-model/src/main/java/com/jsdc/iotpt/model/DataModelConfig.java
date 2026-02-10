package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 大数据模型
 * 参数设置
 */
@Entity
@TableName("DATA_MODEL_CONFIG")
@Table(name = "DATA_MODEL_CONFIG")
@KeySequence(value = "SEQ_DATA_MODEL_CONFIG_ID")
@Data
@ApiModel(value = "大数据模型参数设置")
public class DataModelConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_DATA_MODEL_CONFIG_ID", sequenceName = "SEQ_DATA_MODEL_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DATA_MODEL_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 工作时长 小时
     */
    private String workingHours;

    /**
     * 会议预约总时长
     */
    private String meeting_reservation_duration;

    /**
     * 包间预定次数
     */
    private Integer room_reserve_count;

}
