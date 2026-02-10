package com.jsdc.iotpt.smartsecurity.controller;


import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import com.jsdc.iotpt.service.AlarmGroupService;
import com.jsdc.iotpt.vo.AlarmCategoryVO;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm/group")
@Api(tags = "告警内容-告警分组")
public class AlarmGroupController {


    @Autowired
    private AlarmGroupService alarmGroupService;

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo getPage(@RequestBody AlarmCategoryVO vo) {
        return ResultInfo.success(alarmGroupService.getPage(vo));
    }

    @GetMapping("/list")
    @ApiOperation("分页查询")
    public ResultInfo getList() {
        return ResultInfo.success(alarmGroupService.getList());
    }


    @PostMapping("/save")
    @ApiOperation("新增或编辑或删除")
    public ResultInfo save(@RequestBody AlarmGroup entity) {
        alarmGroupService.save(entity);
        return ResultInfo.success();
    }


}
