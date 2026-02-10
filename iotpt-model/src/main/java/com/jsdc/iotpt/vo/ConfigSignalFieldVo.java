package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigSignalField;
import lombok.Data;

/**
 * @authon thr
 * @describe 信号字段管理
 */
@Data
public class ConfigSignalFieldVo extends ConfigSignalField {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
