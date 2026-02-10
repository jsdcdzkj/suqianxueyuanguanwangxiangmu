package com.jsdc.iotpt.smartsecurity.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.model.IndicatorConfig;
import com.jsdc.iotpt.service.IndicatorConfigService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.IndicatorConfigVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/indicator/config")
@Api(tags = "指标配置")
public class IndicatorConfigController {

    @Autowired
    private IndicatorConfigService indicatorConfigService;


    @PostMapping("/page")
    @ApiOperation(value = "指标配置分页查询")
    public ResultInfo page(@RequestBody IndicatorConfigVo vo) {
        return ResultInfo.success(indicatorConfigService.pageList(vo));
    }


    @PostMapping("/list")
    @ApiOperation(value = "指标配置分页查询")
    public ResultInfo list(@RequestBody(required = false) IndicatorConfig vo) {
        LambdaQueryWrapper<IndicatorConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IndicatorConfig::getIsDel, 0);
        queryWrapper.eq(StringUtils.isNotNull(vo.getBizType()), IndicatorConfig::getBizType, vo.getBizType());
        queryWrapper.eq(StringUtils.isNotBlank(vo.getSubCode()), IndicatorConfig::getSubCode, vo.getSubCode());
        return ResultInfo.success(indicatorConfigService.list(queryWrapper));
    }

    @PostMapping("/edit")
    @ApiOperation(value = "新增/编辑指标配置")
    public ResultInfo edit(@RequestBody IndicatorConfig bean) {
        indicatorConfigService.edit(bean);
        return ResultInfo.success();
    }


    @PostMapping("/del")
    @ApiOperation(value = "删除指标配置")
    public ResultInfo del(@RequestParam Integer id) {
        indicatorConfigService.del(id);
        return ResultInfo.success();
    }


}
