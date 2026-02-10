package com.jsdc.iotpt.operationmgmt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.operate.CheckTemplate;
import com.jsdc.iotpt.service.CheckTemplateService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkTemlate")
@Api("检查模板项")
public class CheckTemplateController {

    @Autowired
    private CheckTemplateService checkTemplateService;

    @PostMapping("getCheckPage.do")
    @ApiOperation("分页查询检查项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public ResultInfo getTeamPage(CheckTemplate bean, @RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        Page<CheckTemplate> page =checkTemplateService.getCheckTemPage(bean, pageIndex, pageSize);
        return ResultInfo.success(page);
    }


    @PostMapping("addCheck.do")
    @ApiOperation("新增编辑检查项")
    public ResultInfo addTeam(@RequestBody CheckTemplate bean){
        return checkTemplateService.editCheckTem(bean);
    }

    @PostMapping("setIsAble.do")
    @ApiOperation("启用禁用检查项")
    public ResultInfo setIsAble(CheckTemplate bean){
        return checkTemplateService.setIsAble(bean);
    }


    @PostMapping("delCheckTem.do")
    @ApiOperation("删除检查项")
    public ResultInfo delCheckTem( CheckTemplate bean){
        return checkTemplateService.delCheckTem(bean.getId());
    }


    @PostMapping("getOneDetail.do")
    @ApiOperation("检查项详情")
    public ResultInfo getOneDetail(CheckTemplate bean){
        return ResultInfo.success(checkTemplateService.getOneDetail(bean.getId()));
    }


    @PostMapping("copyTemplate.do")
    @ApiOperation("复制检查项")
    public ResultInfo copyTemplate(CheckTemplate bean){
        return checkTemplateService.copyTemplate(bean.getId());
    }


    @PostMapping("getAllList.do")
    @ApiOperation("获取所有模板")
    public ResultInfo getAllList(){
        return ResultInfo.success(checkTemplateService.getAllList());
    }


    @PostMapping("getAllTypeList.do")
    @ApiOperation("获取所有模板类型")
    public ResultInfo getAllTypeList(){
        return ResultInfo.success(checkTemplateService.getTypeDicts());
    }


    @PostMapping("getAllDeviceType.do")
    @ApiOperation("获取所有设备类型")
    public ResultInfo getAllDeviceType(){
        return ResultInfo.success(checkTemplateService.getAllDeviceType());
    }

}
