package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.service.ConfigSignalTypeService;
import com.jsdc.iotpt.vo.ConfigSignalTypeVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author lb
 * @Date 2023/6/1 16:16
 * @Description 信号类描述
 **/
@RestController
@RequestMapping("/configSignalType")
@Api(tags = "信号类型管理")
public class ConfigSignalTypeController {

    @Autowired
    ConfigSignalTypeService  configSignalTypeService;

    @RequestMapping("getPage")
    @ApiOperation("分页查询信号类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public ResultInfo getPage(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              ConfigSignalType configSignalType)
    {
        Page<ConfigSignalType> page = configSignalTypeService.getPage(pageNo, pageSize, configSignalType);
        return ResultInfo.success(page);
    }

    @RequestMapping("getList")
    @ApiOperation("列表查询信号类型")
    public ResultInfo getList(ConfigSignalType configSignalType){
        List<ConfigSignalType> list = configSignalTypeService.getList(configSignalType);
        return ResultInfo.success(list);
    }

    @RequestMapping("saveOrUpdate")
    @LogInfo(value = LogEnums.LOG_CONFIG_SIGNAL, model = Constants.MODEL_YYZT)
    @ApiOperation("新增/更新信号类型")
    public ResultInfo saveOrUpdate(@RequestBody ConfigSignalTypeVo configSignalType){
        Boolean isFlag= configSignalTypeService.saveOrUpdateSignType(configSignalType);
        if (isFlag){
            return ResultInfo.success();
        }else {
            return ResultInfo.error("添加失败信号编码重复！");
        }

    }

    @RequestMapping("delete")
    @ApiOperation("删除信号类型")
    @LogInfo(value = LogEnums.LOG_CONFIG_SIGNAL, model = Constants.MODEL_YYZT)
    public ResultInfo delete(ConfigSignalType configSignalType){
        return ResultInfo.success(configSignalTypeService.delete(configSignalType));
    }

    @RequestMapping("getEntity")
    @ApiOperation("对象查询")
    public ResultInfo getEntity(ConfigSignalType configSignalType){
        return ResultInfo.success(configSignalTypeService.getEntity(configSignalType));
    }

    @RequestMapping("/getListByDeviceTypeCode")
    @ApiOperation("根据设备类型编码获取信号类型")
    public ResultInfo getPageList(ConfigDeviceType bean) {
        return ResultInfo.success(configSignalTypeService.getEntityByTCode(bean));
    }

}
