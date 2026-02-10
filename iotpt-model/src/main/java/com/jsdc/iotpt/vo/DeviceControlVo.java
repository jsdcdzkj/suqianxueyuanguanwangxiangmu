package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigSignalTypeItem;
import com.jsdc.iotpt.model.DeviceControl;
import lombok.Data;

import java.util.List;

/**
 * ClassName: DeviceControlVo
 * Description:
 * date: 2023/12/27 9:59
 *
 * @author bn
 */
@Data
public class DeviceControlVo extends DeviceControl {

    // 信号名
    private String signalTypeName;

    // 信号
    private String signalTypeCode;

    // 信号子项
    private ConfigSignalTypeItem item;

    private String deviceStateName;

    // 开关按钮
    private Integer switchValue;

    private Integer isFlag;


}
