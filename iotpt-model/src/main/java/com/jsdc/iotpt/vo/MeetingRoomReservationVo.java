package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.MeetingRoomReservation;
import lombok.Data;

import java.util.List;

/**
 * @Author lb
 * @Date 2023/10/19 17:45
 * @Description 类描述
 **/
@Data
public class MeetingRoomReservationVo extends MeetingRoomReservation {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    /**
     * 今日会议flag 0,1
     */
    private String todayFlag = "0";

    // 1 全部 2 我发起的 3 我参与的
    private Integer listType;

    // 0正序 1 倒序
    private Integer sequence;
}
