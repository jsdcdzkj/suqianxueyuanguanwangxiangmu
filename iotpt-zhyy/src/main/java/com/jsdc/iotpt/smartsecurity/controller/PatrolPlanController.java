package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.PatrolPlanService;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.vo.PatrolPlanVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 巡更计划
 *
 * @author bn
 */
@RestController
@RequestMapping("/patrolPlan")
@Api(tags = "巡更计划")
public class PatrolPlanController {

    @Autowired
    private PatrolPlanService patrolPlanService;

    @Autowired
    private SysDictService sysDictService;
    /**
     *  分页
     * @param bean
     * @return
     */
    @RequestMapping(value = "getPageList.do",method = RequestMethod.POST)
    @ApiOperation(value = "分页",httpMethod = "POST")
    public ResultInfo getPageList(@RequestBody PatrolPlanVo bean){

        return patrolPlanService.getPageList(bean);
    }


    /**
     *  根据id获取详情
     * @param bean
     * @return
     */
    @RequestMapping(value = "getPatrolPlanById.do",method = RequestMethod.POST)
    @ApiOperation(value = "根据id获取详情",httpMethod = "POST")
    public ResultInfo getPatrolPlanById(@RequestBody PatrolPlan bean){
        return patrolPlanService.getPatrolPlanById(bean);
    }


    /**
     *  新增
     * @param bean
     * @return
     */
    @RequestMapping(value = "toAdd.do",method = RequestMethod.POST)
    @ApiOperation(value = "新增",httpMethod = "POST")
    public ResultInfo toAdd(@RequestBody PatrolPlanVo bean){
        return patrolPlanService.toAdd(bean);
    }

    /**
     *  编辑
     * @param bean
     * @return
     */
    @RequestMapping(value = "toEdit.do",method = RequestMethod.POST)
    @ApiOperation(value = "编辑",httpMethod = "POST")
    public ResultInfo toEdit(@RequestBody PatrolPlanVo bean){
        return patrolPlanService.toEdit(bean);
    }


    /**
     *  删除
     * @param bean
     * @return
     */
    @RequestMapping(value = "del.do",method = RequestMethod.POST)
    @ApiOperation(value = "删除",httpMethod = "POST")
    public ResultInfo del(@RequestBody PatrolPlanVo bean){
        return patrolPlanService.del(bean);
    }

    /**
     *  获取字典
     * @param bean
     * @return
     */
    @RequestMapping(value = "getDict.do",method = RequestMethod.POST)
    @ApiOperation(value = "获取字典",httpMethod = "POST")
    public ResultInfo getDict(SysDict bean){
        return ResultInfo.success(sysDictService.selectByType(bean.getDictType()));
    }


    /**
     *  获取用户
     * @param bean
     * @return
     */
    @RequestMapping(value = "getUsers.do",method = RequestMethod.POST)
    @ApiOperation(value = "获取用户",httpMethod = "POST")
    public ResultInfo getUsers(SysUser bean){

        return patrolPlanService.getUsers(bean);
    }



}
