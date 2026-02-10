package com.jsdc.iotpt.smartsecurity.controller;


import com.jsdc.iotpt.model.new_alarm.AlarmContentConfig;
import com.jsdc.iotpt.service.AlarmContentConfigService;
import com.jsdc.iotpt.vo.AlarmContentConfigVO;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/alarm/content")
@Api(tags = "告警配置-告警内容")
public class AlarmContentConfigController {

    @Autowired
    private AlarmContentConfigService alarmContentConfigService;

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo getPageList(@RequestBody AlarmContentConfigVO vo) {
        return ResultInfo.success(alarmContentConfigService.getPageList(vo));
    }

    @GetMapping("/list")
    @ApiOperation("列表查询")
    public ResultInfo getList() {
        return ResultInfo.success(alarmContentConfigService.getList());
    }

    @PostMapping("/save")
    @ApiOperation("新增或编辑或删除")
    public ResultInfo save(@RequestBody AlarmContentConfig entity) {
        alarmContentConfigService.saveOrUp(entity);
        return ResultInfo.success();
    }

    @GetMapping("/tree")
    @ApiOperation("告警分组告警内容树形结构（disable：0：可编辑，1：不可编辑）")
    public ResultInfo treeByGroup(@RequestParam(required = false) Integer planId, @RequestParam(required = false, defaultValue = "0") String disable) {
        return ResultInfo.success(alarmContentConfigService.treeByGroup(planId, disable));
    }

    @GetMapping("/template")
    @ApiOperation("下载导入模板")
    public void downloadTemplate(HttpServletResponse response) {
        alarmContentConfigService.downloadTemplate(response);
    }

    @PostMapping("/upload/data")
    @ApiOperation("导入数据")
    public ResultInfo uploadData(@RequestParam(value = "file")MultipartFile file) {
        alarmContentConfigService.uploadData(file);
        return ResultInfo.success();
    }

    @PostMapping("/export")
    @ApiOperation("导出数据")
    public void exportData(@RequestBody AlarmContentConfigVO vo, HttpServletResponse response) {
        alarmContentConfigService.exportData(vo, response);
    }

}
