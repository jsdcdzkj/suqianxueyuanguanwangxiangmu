package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.sys.SysDict;
import lombok.Data;

/**
 * (SysDict)表封装数据
 *
 * @author wangYan
 * @since 2023-05-10
 */
@Data
public class SysDictVo extends SysDict {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}

