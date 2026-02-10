package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigGroup;
import lombok.Data;

/**
 * ClassName: ConfigGroupVo
 * Description:
 * date: 2024/2/8 14:18
 *
 * @author bn
 */
@Data
public class ConfigGroupVo extends ConfigGroup {

    private String startTime;

    private String endTime;
}
