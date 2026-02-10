package com.jsdc.iotpt.vo;

import cn.hutool.core.util.StrUtil;
import com.jsdc.iotpt.model.ConfigSignalTypeItem;
import com.jsdc.iotpt.model.DeviceCollect;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * @authon thr
 * @describe 采集设备表
 */
@Data
public class WarnVo  implements Serializable {



    private String alertTime;
    private String name;
    private String warnContent;
    private String DICTLABEL;


}
