package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.sys.SysConfig;
import lombok.Data;

import java.util.List;

/**
 * (SysLog)表封装数据
 *
 * @author wangYan
 * @since 2023-05-10
 */
@Data
public class SysConfigVo extends SysConfig {
    private String deviceName;
    private String createUserName;
    private String startTime;
    private String endTime;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}

