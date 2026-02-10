package com.jsdc.iotpt.service;

import cn.hutool.cron.CronUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.task.TaskManager;
import com.jsdc.iotpt.mapper.JobPlanAreaMapper;
import com.jsdc.iotpt.mapper.JobPlanMapper;
import com.jsdc.iotpt.mapper.TeamGroupsMapper;
import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.model.operate.JobPlanArea;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.vo.JobPlanVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * ClassName: JobPlanService
 * Description:
 * date: 2023/8/23 9:18
 *
 * @author bn
 */
@Service
@Transactional
public class JobPlanService extends BaseService<JobPlan> {


    @Autowired
    private JobPlanMapper mapper;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private MissionItemRecordService itemRecordService;

    @Autowired
    private TeamGroupsMapper groupsMapper;

    @Autowired
    private JobPlanAreaMapper planAreaMapper;

    @Autowired
    private SysUserService sysUserService;

    public QueryWrapper getWrapper(JobPlan bean){
        QueryWrapper<JobPlan> queryWrapper=new QueryWrapper<>();

        if (Strings.isNotEmpty(bean.getPlanName())){
            queryWrapper.like("planName",bean.getPlanName());
        }

        if (null!=bean.getGroupId()){
            queryWrapper.eq("groupId",bean.getGroupId());
        }

        if (null!=bean.getPlanType()){
            queryWrapper.eq("planType",bean.getPlanType());
        }

        if (null!=bean.getExecuteCycle()){
            queryWrapper.eq("executeCycle",bean.getExecuteCycle());
        }

        queryWrapper.eq("isDel",0);

        queryWrapper.orderByAsc("planStatus").orderByDesc("planStartTime");

        return queryWrapper;
    }

    public ResultInfo getPageList(Integer pageIndex, Integer pageSize, JobPlan bean) {

        Page page = new Page(pageIndex, pageSize);

        Page<JobPlan> p = mapper.selectPage(page, getWrapper(bean));

        HashMap<String, SysDict>  planType=RedisDictDataUtil.getDictByType("planType");

        p.getRecords().forEach(x->{
            x.setGroupName(groupsMapper.selectById(x.getGroupId()).getName());

            SysDict type=planType.get(String.valueOf(x.getPlanType()));
            if (type!=null){
                x.setTypeName(type.getDictLabel());
            }

        });

        return ResultInfo.success(p);
    }


    public ResultInfo toAdd(JobPlanVo bean) {

        bean.setIsDel(0);


        try {
            bean.setPlanStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getStartTime()));
            bean.setPlanEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getEndTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }



        if (1==bean.getExecuteType()){
            if (bean.getExecuteCycle()==1){
                bean.setCronExpression(String.format("0 0 %s * * ?",bean.getCycleTime()));
            }else if(bean.getExecuteType()==2){
                bean.setCronExpression(String.format("0 0 0 ? * %s",bean.getCycleTime()));
            }else {
                bean.setCronExpression(String.format("0 0 0 %s * ?",bean.getCycleTime()));
            }
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            bean.setCronExpression(sdf.format(bean.getPlanStartTime()));
        }

        bean.setCreateUser(sysUserService.getUser().getId());
        mapper.insert(bean);

        bean.getAreaList().forEach(x->{
            if (x.getType().equals("buildId")){
                x.setBuildId(x.getRealId());
            }else if (x.getType().equals("floorId")){
                x.setFloorId(x.getRealId());
            }else {
                x.setAreaId(x.getRealId());
            }
            x.setPlanId(bean.getId());
            x.setIsDel(0);
            planAreaMapper.insert(x);
        });


        if (bean.getPlanStatus()==0){
            // 此处只针对当前时间处于计划开始与结束时间范围内的单次执行；其余根据动态cron生成，校验在cron内部做
            Date currentTime = new Date();

            if (bean.getExecuteType()==2&&currentTime.after(bean.getPlanStartTime())&&currentTime.before(bean.getPlanEndTime())){
                Mission mission=new Mission();
                mission.setNotes(bean.getPlanName());
                mission.setTitle(bean.getPlanName());
                mission.setSubstance(bean.getPlanName());
                mission.setManageId(bean.getManageId());
                mission.setSource(4);
                mission.setLevels(bean.getPlanLevel());
                mission.setUserId(bean.getCreateUser());
                mission.setIs_del(0);
                mission.setCreateUser(bean.getCreateUser());
                mission.setStates(2);
                mission.setSourceId(bean.getId());
                mission.setReportingTime(currentTime);
                itemRecordService.missionItemRecordSave(mission,bean.getId());

            }else {
                taskManager.createTask(G.JOB_PLAN,String.valueOf(bean.getId()),bean.getCronExpression());
            }
        }



        return ResultInfo.success();
    }



    public ResultInfo isEnablePlan(JobPlanVo bean){

        mapper.updateById(bean);

        if (bean.getPlanStatus()==1){
            // 停用
            CronUtil.remove(G.JOB_PLAN+String.valueOf(bean.getId()));

            return ResultInfo.success("巡检计划已停止，后续将不会生成任务！");
        }else {
            // 启用
            taskManager.createTask(G.JOB_PLAN,String.valueOf(bean.getId()),bean.getCronExpression());
            return ResultInfo.success("巡检计划已启用，后续将生成任务！");
        }
    }

    public ResultInfo toEdit(JobPlanVo bean) throws ParseException {



        bean.setPlanStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getStartTime()));

        bean.setPlanEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getEndTime()));

        if (1==bean.getExecuteType()){
            if (bean.getExecuteCycle()==1){
                bean.setCronExpression(String.format("0 0 %s * * ?",bean.getCycleTime()));
            }else if(bean.getExecuteType()==2){
                bean.setCronExpression(String.format("0 0 0 ? * %s",bean.getCycleTime()));
            }else {
                bean.setCronExpression(String.format("0 0 0 %s * ?",bean.getCycleTime()));
            }
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            bean.setCronExpression(sdf.format(bean.getPlanStartTime()));
        }

        mapper.updateById(bean);

        planAreaMapper.update(null,Wrappers.<JobPlanArea>lambdaUpdate().
                eq(JobPlanArea::getIsDel,0).
                eq(JobPlanArea::getPlanId,bean.getId()).
                set(JobPlanArea::getIsDel,1));

        bean.getAreaList().forEach(x->{
            if (x.getType().equals("buildId")){
                x.setBuildId(x.getRealId());
            }else if (x.getType().equals("floorId")){
                x.setFloorId(x.getRealId());
            }else {
                x.setAreaId(x.getRealId());
            }
            x.setPlanId(bean.getId());
            x.setIsDel(0);
            planAreaMapper.insert(x);
        });



        if (bean.getPlanStatus()==0){
            // 此处只针对当前时间处于计划开始与结束时间范围内的单次执行；其余根据动态cron生成，校验在cron内部做
            Date currentTime = new Date();

            if (bean.getExecuteType()==2&&currentTime.after(bean.getPlanStartTime())&&currentTime.before(bean.getPlanEndTime())){
                Mission mission=new Mission();
                mission.setSubstance(bean.getPlanName());
                mission.setManageId(bean.getManageId());
                mission.setSource(4);
                mission.setLevels(bean.getPlanLevel());
                mission.setUserId(bean.getCreateUser());
                mission.setStates(2);
                mission.setSourceId(bean.getId());
                mission.setReportingTime(currentTime);
                mission.setNotes(bean.getPlanName());
                mission.setTitle(bean.getPlanName());
                mission.setIs_del(0);
                mission.setCreateUser(bean.getCreateUser());
                itemRecordService.missionItemRecordSave(mission,bean.getId());

            }else {
                taskManager.createTask(G.JOB_PLAN,String.valueOf(bean.getId()),bean.getCronExpression());
            }
        }


        return ResultInfo.success("编辑成功！");
    }

    public ResultInfo getJobPlanById(Integer id) {
        JobPlan jobPlan=mapper.selectById(id);

        List<JobPlanArea> jobPlanAreas=planAreaMapper.selectList(Wrappers.<JobPlanArea>lambdaQuery().
                eq(JobPlanArea::getPlanId,id).
                eq(JobPlanArea::getIsDel,0));

        jobPlan.setAreaList(jobPlanAreas);


        return ResultInfo.success(jobPlan);
    }

    public ResultInfo toDel(JobPlanVo bean) {

        mapper.updateById(bean);
        return ResultInfo.success("巡检计划已删除！");
    }
}
