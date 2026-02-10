package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.service.AppVersionInfoService;
import com.jsdc.iotpt.vo.AppVersionInfoVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller控制器
 */
@RestController
@RequestMapping("/appVersionInfo")
@Api(tags = "app版本信息管理")
public class AppVersionInfoController {

    @Autowired
    AppVersionInfoService appVersionInfoService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @PostMapping("/getPageList")
    @ApiOperation("列表查询")
    public ResultInfo getPageList(@RequestBody AppVersionInfoVo bean) {
        return ResultInfo.success(appVersionInfoService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation("新增/编辑")
    public ResultInfo saveOrUpdateAppVersionInfo(@RequestBody AppVersionInfoVo bean) {
        return appVersionInfoService.saveOrUpdateAppVersionInfo(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @PostMapping("/getEntity")
    @ApiOperation("根据id获取实体类")
    public ResultInfo getEntity(@RequestBody AppVersionInfoVo bean) {
        return appVersionInfoService.getEntityById(bean.getId());
    }

}
