package com.jsdc.iotpt.task;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.MissionAssign;
import com.jsdc.iotpt.model.operate.MissionAssignUser;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.service.AppPushMsgService;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: jxl
 * @create: 2025-03-17 15:57
 **/

@Slf4j
@Component
public class MissionTask {
    @Autowired
    private MissionAssignMapper missionAssignMapper;
    @Autowired
    private MissionAssignUserMapper missionAssignUserMapper;
    @Autowired
    private MissionMapper missionMapper;
    @Autowired
    private TeamGroupUserMapper teamGroupUserMapper;
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private AppPushMsgService appPushMsgService;

    //半小时执行一次
    @Scheduled(cron = "0 0/30 * * * ?")
//    一分钟一次--测试使用
//    @Scheduled(cron = "0 0/1 * * * ?")
    public void agencyTask() {
        //获取非挂起的、超期 工单
        List<MissionAssign> missiontListCrew = missionAssignMapper.selecMissiontList();
        for (MissionAssign missionAssign : missiontListCrew) {
            Mission mission = missionMapper.selectById(missionAssign.getMissionId());

            if (mission.getStates() == 4) {
                //未开启工单
                List<TeamGroupUser> groupUsers = teamGroupUserMapper.selectList(new LambdaQueryWrapper<TeamGroupUser>()
                        .eq(TeamGroupUser::getGroupId, missionAssign.getTeamGroupsId())
                        .eq(TeamGroupUser::getTeamRole, "1")
                        .eq(TeamGroupUser::getIsDel, 0)
                );
                //组长
                sendMessage(mission, groupUsers.get(0).getUserId());
                sendZpR(mission, groupUsers.stream().map(TeamGroupUser::getUserId).collect(Collectors.toList()));
            } else if (mission.getStates() == 2) {
                //获取处理组的处理人信息
                List<MissionAssignUser> missionAssignUsers = missionAssignUserMapper.selectList(new LambdaQueryWrapper<MissionAssignUser>()
                        .eq(MissionAssignUser::getMissionId, mission.getId())
                );
                for (MissionAssignUser missionAssignUser : missionAssignUsers) {
                    List<TeamGroupUser> groupUsers = teamGroupUserMapper.selectList(new LambdaQueryWrapper<TeamGroupUser>()
                            .eq(TeamGroupUser::getUserId, missionAssignUser.getUserId())
                            .eq(TeamGroupUser::getGroupId, missionAssignUser.getGroupId())
                    );
                    if (CollUtil.isNotEmpty(groupUsers)) {
                        //组员
                        sendMessage(mission, Integer.parseInt(missionAssignUser.getUserId()));
                    }
                }
                List<String> collect = missionAssignUsers.stream().map(MissionAssignUser::getUserId).collect(Collectors.toList());
                List<Integer> userIds = new ArrayList<>();
                for (String s : collect) {
                    userIds.add(Integer.parseInt(s));
                }
                sendZpR(mission, userIds);
            }
            mission.setIs_remind(1);
            mission.setUpdateTime(new Date());
            missionMapper.updateById(mission);
        }
    }

    public void sendZpR(Mission mission, List<Integer> userIds) {
        //通知审核上报工单(指派到维修组)
        AppPushMsgVo msgVo = new AppPushMsgVo();
        List<String> list = new ArrayList<>();
        for (Integer userId : userIds) {
            list.add(userMapper.selectById(userId).getRealName());
        }
        msgVo.setName(String.join(",", list));
        msgVo.setResons(mission.getTitle());
        msgVo.setAppConfigCode("TEAMWORK_REPAIRS_TOSOLVE_B");
        List<SysUser> sysUsers = userMapper.getUsersByRole("app_rw_zp");
        msgVo.setUserIdList(sysUsers.stream().map(SysUser::getId).collect(Collectors.toList()));
        appPushMsgService.pushMsg(msgVo);
    }

    //如果工单状态为未开启状态  发给组长 如果为未处理状态 发给组员
    private void sendMessage(Mission mission, Integer userId) {
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setResons(mission.getTitle());
        //处理人
        msgVo.setUserIdList(Collections.singletonList(userId));
        msgVo.setAppConfigCode("SYSTEM_REPAIRS_NOTICE_D");
        appPushMsgService.pushMsg(msgVo);
    }


}
