package com.jsdc.iotpt.vo.operate;

import lombok.Data;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-14 09:49
 **/
@Data
public class AssignedCountDTO {
    private Integer userId;
    private Integer isAdmin;
    private Integer publicity;
    private Integer home_tab_type;
}
