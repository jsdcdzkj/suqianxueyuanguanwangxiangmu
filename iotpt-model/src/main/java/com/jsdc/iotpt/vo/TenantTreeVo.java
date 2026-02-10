package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantInfo;
import lombok.Data;

import java.util.List;


@Data
public class TenantTreeVo {

    private String tenantName;

    private String floorName;

    private String statusStr;

    private Integer tenantId;

    private Integer roomId;

//    private List<TenantInfo> children;

}
