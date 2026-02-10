package com.jsdc.iotpt.smartenergy.controller;

import com.jsdc.iotpt.service.DataModelDaysService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大数据模型数据
 * 每日生成数据
 */
@RestController
@RequestMapping("/dataModelDays")
public class DataModelDaysController {
    @Autowired
    private DataModelDaysService dataModelDaysService;

    /**
     * 大数据模型数据
     * 每日生成数据
     */
    @RequestMapping("/createData.do")
    public ResultInfo createData() {
        dataModelDaysService.createData();
        return ResultInfo.success();
    }
}
