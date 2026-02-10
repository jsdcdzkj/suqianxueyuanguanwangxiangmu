package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigLink;
import lombok.Data;


/**
 * @authon thr
 * @describe 连接管理
 */
@Data
public class ConfigLinkVo extends ConfigLink {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    /**
     * 传输协议
     */
    private String protocolName;

}
