package com.jsdc.iotpt.operationmgmt.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.operate.AbnormalPatrol;
import com.jsdc.iotpt.model.operate.TeamGroups;
import com.jsdc.iotpt.service.AbnormalPatrolService;
import com.jsdc.iotpt.service.MissionService;
import com.jsdc.iotpt.service.TeamGroupsService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/abnormalPatrol")
public class AbnormalPatrolController {
    @Autowired
    private AbnormalPatrolService service;

    @Autowired
    private TeamGroupsService teamGroupsService;

    @Autowired
    private MissionService missionService;

    /**
     * @Author: yc
     * @Params:
     * @Return:
     * @Description：分页查询
     * @Date ：2023/8/24 上午 10:28
     * @Modified By：
     */
    @RequestMapping("getPageList")
    public ResultInfo getPageList(AbnormalPatrol vo, @RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {
        Page<AbnormalPatrol> page = this.service.getPageList(vo, pageIndex, pageSize);
        return ResultInfo.success(page);
    }

    @RequestMapping("getTeamGroupList")
    public ResultInfo getTeamGroupList() {
        List<TeamGroups> allList = this.teamGroupsService.getAllList();
        return ResultInfo.success(allList);
    }

    /**
     * @Author: yc
     * @Params:
     * @Return:
     * @Description：
     * @Date ：2023/8/24 下午 4:36
     * @Modified By：
     */

    @RequestMapping("saveOperation")
    public ResultInfo saveOperation(@RequestBody AbnormalPatrol bean) {
        this.service.saveOperation(bean);
        return ResultInfo.success();
    }
}
