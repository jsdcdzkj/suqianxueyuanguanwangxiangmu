package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 会议室预约关联人员
 */
@Entity
@TableName("MEETING_ROOM_RESERVATION_USER")
@Table(name = "MEETING_ROOM_RESERVATION_USER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomReservationUser {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private String id;//主键
    /**
     * 会议室预约id
     */
    private String reservationId;
    /**
     * 参会人员id
     */
    private Integer userId;
    /**
     * 参会人员名称  在插入的时候和Id一起入库
     */
    private String userName;
    /**
     * 参会人员部门id
     */
    private Integer deptId;
    //0已签到 , 1未签到
    private int status;
    //0 或者 null 初始参会人员 , 1非初始参会人员参加会议
    private int userType;
}
