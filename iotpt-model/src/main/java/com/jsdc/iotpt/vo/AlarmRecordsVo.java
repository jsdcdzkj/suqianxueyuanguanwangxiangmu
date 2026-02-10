package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import lombok.Data;

import java.util.List;

@Data
public class AlarmRecordsVo extends AlarmRecords {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private String radio;// 日/月/年
    //请求参数
    private String startTime;
    private String endTime;
    private String ids;//报警id
    private String reason;//原因
    private String remark;//备注
    /**
     * 1:误识
     * 2:图像质量不佳
     * 3:环境影响
     * 4:设备原因
     */
    private String judge;//判定


    //接收参数
    private String num;//数量
    private List<String> handleStatusList; // 处理状态
}
