package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigProtocol;
import lombok.Data;

/**
 * @authon thr
 * @describe 协议管理
 */
@Data
public class ConfigProtocolVo extends ConfigProtocol {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
