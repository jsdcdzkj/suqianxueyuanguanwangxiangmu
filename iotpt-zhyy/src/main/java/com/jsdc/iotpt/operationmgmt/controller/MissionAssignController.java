package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.model.operate.MissionAssign;
import com.jsdc.iotpt.service.MissionAssignService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

/**
 * 任务指派信息表
 *
 * @author zln
 */
@RestController
@RequestMapping("/missionAssign")
@Api(tags = "任务指派信息表")
public class MissionAssignController {

    @Autowired
    private MissionAssignService assignService;


    @PostMapping("tobeAssigned.do")
    @ApiOperation("待指派")
    public ResultInfo tobeAssigned(MissionAssign bean, String deadlineTime) {
        try {
            bean.setDeadline(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadlineTime));
            return ResultInfo.success(assignService.tobeAssigned(bean));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("待指派失败");
        }
    }


    @PostMapping("selectTermQuery.do")
    @ApiOperation("根据条件查询查询数据")
    public ResultInfo selectTermQuery(MissionAssign bean) {
        try {
            return ResultInfo.success(assignService.selectTermQuery(bean));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("查询失败");
        }
    }
}
