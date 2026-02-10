package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.vo.DataSheetVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @authon thr
 * @describe 设备采集数据
 */
@RestController
@RequestMapping("/dataSheet")
@Api(tags = "设备采集数据")
public class DataSheetController {

    @Autowired
    DataSheetService dataSheetService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("设备采集数据-分页查询")
    public ResultInfo getPageList(@RequestBody DataSheetVo bean) {
        return ResultInfo.success(dataSheetService.getPageList2(bean));
    }

    /**
     * 设备采集数据列表 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("设备采集数据列表")
    public ResultInfo getList(@RequestBody DataSheetVo bean) {
        return ResultInfo.success(dataSheetService.getList(bean));
    }


}
