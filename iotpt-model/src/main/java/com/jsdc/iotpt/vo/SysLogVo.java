package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.sys.SysLog;
import lombok.Data;

/**
 * (SysLog)表封装数据
 *
 * @author wangYan
 * @since 2023-05-10
 */
@Data
public class SysLogVo extends SysLog {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String deviceCode;
}

