package com.jsdc.iotpt.operationmgmt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.MissionAssignUser;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.MissionAssignUserService;
import com.jsdc.iotpt.service.MissionService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务处理人指派
 */
@RestController
@RequestMapping("/assignUser")
@Api(tags = "任务指派信息表")
public class MissionAssignUserController {

    @Autowired
    private MissionAssignUserService assignUserService;
    @Autowired
    private TeamGroupUserService groupUserService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private MissionService missionService;

    @PostMapping("saveAssignUser.do")
    @ApiOperation("保存指派人员")
    public ResultInfo saveAssignUser(@RequestBody MissionAssignUser bean) {
        try {
            bean.getUsers().forEach(a -> {
                MissionAssignUser user = new MissionAssignUser();
                user.setMissionId(bean.getMissionId());
                user.setUserId(a);
                user.setGroupId(bean.getGroupId());
                assignUserService.save(user);
            });
            Mission mission = missionService.getById(bean.getMissionId());
            mission.setStates(2);//待处理
            return ResultInfo.success(missionService.updateById(mission));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("保存指派人员失败");
        }
    }

    @PostMapping("teamGroupsList.do")
    @ApiOperation("根据班组id获取班组人员")
    public ResultInfo teamGroupsList(Integer groupId) {
        try {
            List<TeamGroupUser> list = groupUserService.list(new LambdaQueryWrapper<TeamGroupUser>()
                    .eq(TeamGroupUser::getIsDel, 0).eq(TeamGroupUser::getGroupId, groupId));
            list.forEach(a -> {
                SysUser users = userService.getById(a.getUserId());
                a.setRealName(users.getRealName());
            });
            return ResultInfo.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取班组人员失败");
        }
    }
}
