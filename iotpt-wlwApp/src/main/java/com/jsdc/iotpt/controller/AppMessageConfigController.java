package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.AppMessageConfig;
import com.jsdc.iotpt.service.AppMessageConfigService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AppMessageConfigController
 *
 * @author 许森森
 * @date 2024/11/18
 */
@RestController
@RequestMapping("/app/message/config")
@Api(tags = "APP消息配置")
public class AppMessageConfigController {

    @Autowired
    private AppMessageConfigService appMessageConfigService;

    @PostMapping("/list")
    @ApiOperation("列表（不分页）")
    public ResultInfo list(@RequestBody AppMessageConfig config) {
        return ResultInfo.success(appMessageConfigService.list(config));
    }

}
