package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

/**
 * 泰杰赛智慧照明系统 设置点位值，状态反馈
 */

@Data
public class PointValueStatusDto {

    private String pointId;

    private String setValue;

    /**
     * 1-设值成功，2-影子状态，3-虚拟点，无法设置，4-点不存在
     */
    private Integer state;

}
