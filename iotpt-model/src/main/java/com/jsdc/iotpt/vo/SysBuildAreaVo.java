package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.sys.SysBuildArea;
import lombok.Data;


/**
 * @authon thr
 * @describe 区域
 */
@Data
public class SysBuildAreaVo extends SysBuildArea {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer buildId;

}
