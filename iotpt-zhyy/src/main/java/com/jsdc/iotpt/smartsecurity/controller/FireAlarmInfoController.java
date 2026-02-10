package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.service.FireAlarmInfoService;
import com.jsdc.iotpt.service.TeamGroupsService;
import com.jsdc.iotpt.vo.FireAlarmInfoVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/fireAlarmInfo")
@Api(tags = "消防报警")
public class FireAlarmInfoController {

    @Autowired
    FireAlarmInfoService fireAlarmInfoService;

    @Autowired
    TeamGroupsService teamGroupsService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("分页查询")
    public ResultInfo getPageList(FireAlarmInfoVo bean) {
        return ResultInfo.success(fireAlarmInfoService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("新增修改")
    public ResultInfo saveOrUpdateFireAlarmInfo(@RequestBody FireAlarmInfoVo bean) {
        return fireAlarmInfoService.saveOrUpdateFireAlarmInfo(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("XXXX")
    public ResultInfo getEntity(FireAlarmInfoVo bean) {
        return fireAlarmInfoService.getEntityById(bean.getId());
    }


    /**
     * 获取班组列表
     * @param
     * @return
     */
    @RequestMapping("/getTeamlist")
    @ApiOperation("获取班组列表")
    public ResultInfo getTeamlist() {
        return ResultInfo.success(teamGroupsService.getTeamlist());
    }


    /**
     * 删除 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/del")
    @ApiOperation("删除")
    public ResultInfo del(FireAlarmInfoVo bean) {
        fireAlarmInfoService.del(bean);
        return ResultInfo.success();
    }



    /**
     * 导出
     */
    @RequestMapping(value = "/toExportTemplate", method = RequestMethod.GET)
    @ApiOperation("导出")
    public void toExportTemplate(FireAlarmInfoVo bean, HttpServletResponse response) {
        fireAlarmInfoService.export(bean, response);
    }


    /**
     * 折线
     */
    @RequestMapping(value = "/line")
    @ApiOperation("折线")
    public ResultInfo line  (Integer type) {
        return ResultInfo.success(fireAlarmInfoService.line(type));
    }


    /**
     * 柱
     */
    @RequestMapping(value = "/column")
    @ApiOperation("柱")
    public ResultInfo column  (Integer type) {
        return ResultInfo.success(fireAlarmInfoService.column(type));
    }

}
