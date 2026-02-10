package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

import java.util.List;

/**
 * 泰杰赛智慧照明系统 组合设备信息
 */

@Data
public class TerminalDeviceTreeDto {

    private String id;

    private String deviceName;

    private String nickName;

    private String sort;

    private Integer layer;

    private String projectId;

    private boolean canOperate;

    private String remark;

    private String terminalDeviceTypeId;

    private String parentId;

    private List<TerminalDeviceTreeDto> children;

}
