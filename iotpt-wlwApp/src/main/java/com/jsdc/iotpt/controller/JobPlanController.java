package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.service.JobPlanService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.JobPlanVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * ClassName: JobPlanController
 * Description:
 * date: 2023/8/23 9:17
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/jobPlan")
public class JobPlanController {

    @Autowired
    private JobPlanService jobPlanService;


    /**
     *  分页
     * @param pageIndex
     * @param pageSize
     * @param bean
     * @return
     */
    @RequestMapping(value = "getPageList.do",method = RequestMethod.POST)
    public ResultInfo getPageList(@RequestParam(value = "pageNum",required = false) Integer pageIndex,
                                  @RequestParam(value = "pageSize",required = false) Integer pageSize, JobPlan bean){

        try {
            return jobPlanService.getPageList(pageIndex,pageSize,bean);
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


    /**
     *  根据id获取计划以及巡检范围
     * @return
     */
    @RequestMapping(value = "getJobPlanById.do",method = RequestMethod.POST)
    public ResultInfo getJobPlanById(Integer id){
        try {
            return jobPlanService.getJobPlanById(id);
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }


    /**
     *  新增
     * @return
     */
    @RequestMapping(value = "toAdd.do",method = RequestMethod.POST)
    public ResultInfo toAdd(@RequestBody JobPlanVo bean){

        try {
            return jobPlanService.toAdd(bean);
        } catch (Exception e) {
            throw new CustomException("新增失败");
        }

    }

    /**
     *  删除
     * @return
     */
    @RequestMapping(value = "toDel.do",method = RequestMethod.POST)
    public ResultInfo toDel(JobPlanVo bean){

        try {
            return jobPlanService.toDel(bean);
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }


    /**
     *  停用、启用
     * @return
     */
    @RequestMapping(value = "isEnablePlan.do",method = RequestMethod.POST)
    public ResultInfo isEnablePlan(JobPlanVo bean){

        try {
            return jobPlanService.isEnablePlan(bean);
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }


    /**
     *  编辑
     * @return
     */
    @RequestMapping(value = "toEdit.do",method = RequestMethod.POST)
    public ResultInfo toEdit(@RequestBody JobPlanVo bean){

        try {
            return jobPlanService.toEdit(bean);
        } catch (ParseException e) {
            throw new CustomException("编辑失败");
        }
    }

}
