package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报警信息对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmInfo {

    /**
     * 报警状态 0:无报警,默认值(取消报警) 1:视频遮挡 3:占用双车位/压线停车 4:非机动车/摩托车 5:车头/车尾方向不一致 7:无牌车 9：行人滞留(预留) （10-99:保留） ==“占用双车位/压线停车”和“车 头/车尾方向不一致”,报警属于证 据链==
     */
    private String alarm_status;

    /**
     * 白名单/准 入车型状态 1:在白名单/准入车型内，且有 效 2:不在白名单/准入车型内，或 验证未通过
     */
    private String alarm_whitelist;
}
