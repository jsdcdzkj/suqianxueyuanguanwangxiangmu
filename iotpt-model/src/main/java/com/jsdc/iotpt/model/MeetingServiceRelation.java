package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 会议室预约服务选项关联表
 */
@Entity
@TableName("MEETING_SERVICE_RELATION")
@Table(name = "MEETING_SERVICE_RELATION")
@KeySequence(value = "MEETING_SERVICE_RELATION_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  MeetingServiceRelation {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "MEETINGSERVICERELATIONID", sequenceName = "MEETING_SERVICE_RELATION_ID", allocationSize = 1)
    @GeneratedValue(generator = "MEETINGSERVICERELATIONID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "会议室预约服务选项关联表id")
    private Integer id;

    /**
     * 会议室预约id
     */
    @ApiModelProperty(value = "会议室预约id")
    private String meetingRoomReservationId;

    /**
     * 服务选项id
     */
    @ApiModelProperty(value = "服务选项id")
    private Integer serviceOptionsId;
}
