package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.AppVersionInfo;
import lombok.Data;

@Data
public class AppVersionInfoVo extends AppVersionInfo {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
