package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.AppMessageConfig;
import com.jsdc.iotpt.service.AppMessageConfigService;
import com.jsdc.iotpt.vo.AppMessageConfigVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo pageList(@RequestBody AppMessageConfigVo vo) {
        return ResultInfo.success(appMessageConfigService.pageList(vo));
    }

    @PostMapping("/list")
    @ApiOperation("列表（不分页）")
    public ResultInfo list(@RequestBody AppMessageConfig config) {
        return ResultInfo.success(appMessageConfigService.list(config));
    }

    @PostMapping("/save")
    @ApiOperation("新增或修改")
    public ResultInfo edit(@RequestBody AppMessageConfig config) {
        appMessageConfigService.edit(config);
        return ResultInfo.success();
    }

    @GetMapping("/delete")
    @ApiOperation("根据ID删除")
    public ResultInfo delete(@RequestParam Integer id) {
        appMessageConfigService.delete(id);
        return ResultInfo.success();
    }

    @GetMapping("/byId")
    @ApiOperation("根据ID查询")
    public ResultInfo queryById(@RequestParam Integer id) {
        return ResultInfo.success(appMessageConfigService.queryById(id));
    }


}
