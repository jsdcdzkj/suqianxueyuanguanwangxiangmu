package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.model.PatrolStatistic;
import com.jsdc.iotpt.service.PatrolStatisticService;
import com.jsdc.iotpt.vo.PatrolDataVo;
import com.jsdc.iotpt.vo.PatrolStatisticVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: PatrolStatisticController
 * Description:
 * date: 2024/11/28 13:45
 *
 * @author bn
 */
@RestController
@RequestMapping("/patrolStatistic")
@Api(tags = "巡更计划")
public class PatrolStatisticController {

    @Autowired
    private PatrolStatisticService statisticService;


    @RequestMapping(value = "getPageList.do",method = RequestMethod.POST)
    @ApiOperation(value = "分页",httpMethod = "POST")
    public ResultInfo getPageList(@RequestBody PatrolStatisticVo bean){

        return ResultInfo.success(statisticService.getPageList(bean));
    }

    @RequestMapping(value = "getStatisticView.do",method = RequestMethod.POST)
    @ApiOperation(value = "分页",httpMethod = "POST")
    public ResultInfo getStatisticView(@RequestBody PatrolStatisticVo bean){

        return ResultInfo.success(statisticService.getStatisticView(bean));
    }




}
