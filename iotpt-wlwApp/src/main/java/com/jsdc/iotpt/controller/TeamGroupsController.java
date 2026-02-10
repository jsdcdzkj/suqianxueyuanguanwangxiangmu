package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.TeamGroupsService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TeamGroupsController
 * Description:
 * date: 2023/9/15 10:17
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/teamGroups")
public class TeamGroupsController {

    @Autowired
    private TeamGroupsService teamGroupsService;


    @PostMapping("getAllList.do")
    @ApiOperation("获取所有班组")
    public ResultInfo getAllList(){
        return ResultInfo.success(teamGroupsService.getAllList());
    }

}
