package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.service.ConfigDeviceTypeService;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigDeviceTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/configDeviceType")
@Api(tags = "设备类型管理")
public class ConfigDeviceTypeController {

    @Autowired
    ConfigDeviceTypeService configDeviceTypeService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @GetMapping("/getPageList")
    @ApiOperation("查询设备类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTypeName",value = "设备类型名称",dataType = "String"),
            @ApiImplicitParam(name = "deviceTypeCode",value = "设备类型编码",dataType = "String"),
            @ApiImplicitParam(name = "pageNo",value = "页数",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "条数",dataType = "Integer")
    })
    public ResultInfo getPageList(ConfigDeviceTypeVo bean) {
        return ResultInfo.success(configDeviceTypeService.getPageList(bean));
    }

    /**
     * 列表
     * @param bean
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("查询设备类型列表")
    public ResultInfo getList(ConfigDeviceTypeVo bean) {
        return ResultInfo.success(configDeviceTypeService.getList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation("添加设备类型")
    @LogInfo(value = LogEnums.LOG_CONFIG_DEVICE, model = Constants.MODEL_YYZT)
    public ResultInfo saveOrUpdateConfigDeviceType(@RequestBody ConfigDeviceTypeVo bean) {
        return configDeviceTypeService.saveOrUpdateConfigDeviceType(bean);
    }

    @GetMapping("/getEntity")
    @ApiOperation("查看设备类型")
    @ApiImplicitParam(name = "id",value = "设备类型id",dataType = "Integer")
    public ResultInfo getEntity(Integer id) {
        return ResultInfo.success(configDeviceTypeService.getEntityById(id));
    }

    @PostMapping("/delConfigDeviceType")
    @ApiOperation("删除设备类型")
    @LogInfo(value = LogEnums.LOG_CONFIG_DEVICE, model = Constants.MODEL_YYZT)
    @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    public ResultInfo delConfigDeviceType(Integer id) {
        return configDeviceTypeService.delConfigDeviceType(id);
    }

    @PostMapping("/uploadDeviceTypeFile")
    @ApiOperation("上传设备类型图片")
    public ResultInfo uploadDeviceTypeFile(MultipartFile files)  {
        return ResultInfo.success(configDeviceTypeService.uploadDeviceTypeFile(files));
    }
}
