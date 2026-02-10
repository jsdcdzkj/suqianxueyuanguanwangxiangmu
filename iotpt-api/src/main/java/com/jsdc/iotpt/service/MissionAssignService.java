package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.mapper.SysUserRoleMapper;
import com.jsdc.iotpt.mapper.TeamGroupUserMapper;
import com.jsdc.iotpt.model.SysUserRole;
import com.jsdc.iotpt.model.operate.*;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import com.jsdc.iotpt.vo.operate.MissionAssignVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务指派信息表功能
 */
@Service
@Transactional
public class MissionAssignService extends BaseService<MissionAssign> {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private AppPushMsgService appPushMsgService;
    @Autowired
    private MissionService missionService;
    @Autowired
    private SysDictService dictService;
    @Autowired
    private TeamGroupsService groupsService;
    @Autowired
    private TeamGroupUserMapper groupUserMapper;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private JobPlanService jobPlanService;

    /**
     * 待指派
     *
     * @param bean
     * @return
     */
    public boolean tobeAssigned(MissionAssign bean) {
        List<MissionAssign> list = list(new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, bean.getMissionId())
                .eq(MissionAssign::getState, 1));
        //如果存在启用数据,将原有数据更改为过期数据
        if (!list.isEmpty() && list.size() > 0) {
            list.forEach(a -> {
                a.setState(0);
                updateById(a);
            });
        }
        bean.setMissionStates(4);//开启
        bean.setState(1);
        bean.setCreateTime(new Date());
        bean.setCreateUser(userService.getUser().getId());
        //更新任务表中的状态数据
        Mission mi = missionService.getById(bean.getMissionId());
        mi.setStates(bean.getMissionStates());
        //服务类型 1：公区服务 2：有偿服务
        mi.setServiceType(bean.getServiceType());
        mi.setAssignedTime(new Date());
        mi.setAssignerId(bean.getId());
        missionService.updateById(mi);
        //根据班组获取任务巡检计划名称
        List<JobPlan> jobPlans = jobPlanService.list(new LambdaQueryWrapper<JobPlan>().eq(JobPlan::getIsDel, 0)
                .eq(JobPlan::getGroupId, bean.getTeamGroupsId()));
        if (!jobPlans.isEmpty() && jobPlans.size() > 0) {
            JobPlan jobPlan = jobPlans.get(0);
            bean.setJobPlanName(jobPlan.getPlanName());
        }
        //通知响应(开启)被指派的工单
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setAppConfigCode("TEAMWORK_REPAIRS_TOSOLVE");
        List<TeamGroupUser> teamGroupUsers = groupUserMapper.selectList(new LambdaQueryWrapper<TeamGroupUser>()
                //班组角色 1 为组长
                .eq(TeamGroupUser::getTeamRole, "1")
                .eq(TeamGroupUser::getIsDel, 0).eq(TeamGroupUser::getGroupId, bean.getTeamGroupsId()));
        if (CollUtil.isNotEmpty(teamGroupUsers)) {
            TeamGroupUser groupUser = teamGroupUsers.get(0);
            //roleId 40207为 app_yunwei
            List<SysUserRole> sysUserRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getIsDel, 0).eq(SysUserRole::getUserId, groupUser.getUserId()).eq(SysUserRole::getRoleId, "40207"));
            if (CollUtil.isNotEmpty(sysUserRoles)) {
                msgVo.setUserIdList(sysUserRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList()));
                appPushMsgService.pushMsg(msgVo);
            }
        }
        return save(bean);
    }

    //巡检计划生成指派班组
    public boolean jobPlanAssigned(MissionAssign bean, Integer jobId) {
        List<MissionAssign> list = list(new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, bean.getMissionId())
                .eq(MissionAssign::getState, 1));
        //如果存在启用数据,将原有数据更改为过期数据
        if (!list.isEmpty() && list.size() > 0) {
            list.forEach(a -> {
                a.setState(0);
                updateById(a);
            });
        }
        bean.setMissionStates(4);//开启
        bean.setState(1);
        bean.setCreateTime(new Date());
        //更新任务表中的状态数据
        Mission mi = missionService.getById(bean.getMissionId());
        mi.setStates(bean.getMissionStates());
        mi.setAssignedTime(new Date());
        mi.setAssignerId(bean.getId());
        missionService.updateById(mi);
        //根据班组获取任务巡检计划名称
        JobPlan jobPlan = jobPlanService.getById(jobId);
        if (StringUtils.isNotNull(jobPlan)) {
            bean.setJobPlanName(jobPlan.getPlanName());
            bean.setTeamGroupsId(jobPlan.getGroupId());
            bean.setTaskType(jobPlan.getPlanType());
            bean.setUrgency(jobPlan.getPlanLevel());
            //处理期限需要计算
            if (null != jobPlan.getExecuteType()) {
                if (2 == jobPlan.getExecuteType()) {//单次执行
                    bean.setDeadline(jobPlan.getPlanEndTime());
                }
            }
        }
        return save(bean);
    }

    public static void main(String[] args) {

    }

    /**
     * 根据条件查询查询数据
     *
     * @param bean
     * @return
     */
    public List<MissionAssign> selectTermQuery(MissionAssign bean) {
        LambdaQueryWrapper<MissionAssign> queryWrapper = new LambdaQueryWrapper<>();
        if (null != bean.getMissionId()) {
            queryWrapper.eq(MissionAssign::getMissionId, bean.getMissionId());
        }
        return list(queryWrapper);
    }

    /**
     * 获取指派信息
     *
     * @param missionId
     * @param state
     * @return
     */
    public List<MissionAssignVo> selectAssignsList(Integer missionId, Integer state) {
        LambdaQueryWrapper<MissionAssign> queryWrapper = new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, missionId)
                .eq(MissionAssign::getState, state);
        List<MissionAssign> assigns = list(queryWrapper);//指派信息
        List<MissionAssignVo> vos = new ArrayList<>();
        assigns.forEach(a -> {
            MissionAssignVo vo = new MissionAssignVo();
            BeanUtils.copyProperties(a, vo);
            List<SysDict> taskTypes = dictService.list(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, "taskTypes")
                    .eq(SysDict::getDictValue, a.getTaskType().toString()));
            if (!taskTypes.isEmpty() && taskTypes.size() > 0) {
                vo.setTaskTypeName(taskTypes.get(0).getDictLabel());
            }
            if (null != vo.getUrgency()) {
                if (1 == vo.getUrgency()) {
                    vo.setUrgencyName("一般");
                } else {
                    vo.setUrgencyName("紧急");
                }
            }
            if (null != a.getTeamGroupsId()) {
                TeamGroups groups = groupsService.getById(a.getTeamGroupsId());
                vo.setTeamGroupsName(groups.getName());
            }
            vos.add(vo);
        });
        return vos;
    }

    public boolean tobeAppAssigned(MissionAssign bean) {
        List<MissionAssign> list = list(new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, bean.getMissionId())
                .eq(MissionAssign::getState, 1));
        //如果存在启用数据,将原有数据更改为过期数据
        if (!list.isEmpty() && list.size() > 0) {
            list.forEach(a -> {
                a.setState(0);
                updateById(a);
            });
        }
        bean.setMissionStates(4);//开启
        bean.setState(1);
        bean.setCreateTime(new Date());
        bean.setCreateUser(bean.getCreateUser());
        //更新任务表中的状态数据
        Mission mi = missionService.getById(bean.getMissionId());
        mi.setStates(bean.getMissionStates());
        mi.setAssignedTime(new Date());
        //服务类型 1：公区服务 2：有偿服务
        mi.setServiceType(bean.getServiceType());
        mi.setAssignerId(bean.getId());
        missionService.updateById(mi);
        //根据班组获取任务巡检计划名称
        List<JobPlan> jobPlans = jobPlanService.list(new LambdaQueryWrapper<JobPlan>().eq(JobPlan::getIsDel, 0)
                .eq(JobPlan::getGroupId, bean.getTeamGroupsId()));
        if (!jobPlans.isEmpty() && jobPlans.size() > 0) {
            JobPlan jobPlan = jobPlans.get(0);
            bean.setJobPlanName(jobPlan.getPlanName());
        }
        //通知响应(开启)被指派的工单
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setAppConfigCode("TEAMWORK_REPAIRS_TOSOLVE");
        List<TeamGroupUser> teamGroupUsers = groupUserMapper.selectList(new LambdaQueryWrapper<TeamGroupUser>()
                //班组角色 1 为组长
                .eq(TeamGroupUser::getTeamRole, "1")
                .eq(TeamGroupUser::getIsDel, 0).eq(TeamGroupUser::getGroupId, bean.getTeamGroupsId()));
        if (CollUtil.isNotEmpty(teamGroupUsers)) {
            TeamGroupUser groupUser = teamGroupUsers.get(0);
            //roleId 40207为 app_yunwei
            List<SysUserRole> sysUserRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getIsDel, 0).eq(SysUserRole::getUserId, groupUser.getUserId()).eq(SysUserRole::getRoleId, "40207"));
            if (CollUtil.isNotEmpty(sysUserRoles)) {
                msgVo.setUserIdList(sysUserRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList()));
                appPushMsgService.pushMsg(msgVo);
            }
        }
        return save(bean);
    }
}
