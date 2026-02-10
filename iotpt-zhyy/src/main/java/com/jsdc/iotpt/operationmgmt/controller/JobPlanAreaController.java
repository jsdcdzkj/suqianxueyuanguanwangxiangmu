package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.service.JobPlanAreaService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: JobPlanAreaController
 * Description:
 * date: 2023/8/24 9:56
 *
 * @author bn
 */
@RestController
@RequestMapping("/planArea")
public class JobPlanAreaController {


    @Autowired
    private JobPlanAreaService jobPlanAreaService;

    @RequestMapping(value = "getTreeBuild.do",method = RequestMethod.GET)
    public ResultInfo getTreeBuild(){


        try {
            return ResultInfo.success(jobPlanAreaService.getTreeBuild());
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }


    @RequestMapping(value = "getTreeBuildDisable.do",method = RequestMethod.GET)
    public ResultInfo getTreeBuildDisable(){


        try {
            return ResultInfo.success(jobPlanAreaService.getTreeBuildDisable());
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }


}
