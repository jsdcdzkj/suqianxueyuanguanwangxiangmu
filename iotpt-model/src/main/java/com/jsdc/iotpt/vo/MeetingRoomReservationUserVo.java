package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.MeetingRoomReservation;
import lombok.Data;

/**
 * @Author lb
 * @Date 2023/11/30 19:09
 * @Description 类描述
 **/
@Data
public class MeetingRoomReservationUserVo extends MeetingRoomReservation {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
