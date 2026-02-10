package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.DeviceCollectService;
import com.jsdc.iotpt.service.SysOrgManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zln
 */
@RestController
@RequestMapping("/app/deviceCollect")
@Api(tags = "设备监控")
public class DeviceCollectController {

    @Autowired
    private SysOrgManageService sysOrgManageService;
    DeviceCollectService deviceCollectService;

}
