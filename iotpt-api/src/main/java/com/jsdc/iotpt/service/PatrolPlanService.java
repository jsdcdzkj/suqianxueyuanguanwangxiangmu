package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.task.TaskManager;
import com.jsdc.iotpt.mapper.PatrolDeviceOrPointMapper;
import com.jsdc.iotpt.mapper.PatrolPeopleMapper;
import com.jsdc.iotpt.mapper.PatrolPlanMapper;
import com.jsdc.iotpt.mapper.PatrolTimesMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.model.operate.JobPlanArea;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.vo.PatrolPlanVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ClassName: PatrolPlanService
 * Description:
 * date: 2024/1/9 15:52
 *
 * @author bn
 */
@Service
@Transactional
public class PatrolPlanService extends BaseService<PatrolPlan> {

    @Autowired
    private PatrolPlanMapper mapper;

    @Autowired
    private PatrolPeopleMapper patrolPeopleMapper;

    @Autowired
    private PatrolDeviceOrPointMapper patrolDeviceOrPointMapper;

    @Autowired
    private PatrolTimesMapper patrolTimesMapper;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private SysUserService sysUserService;

    public QueryWrapper getWrapper(PatrolPlanVo bean){
        QueryWrapper<PatrolPlan> queryWrapper=new QueryWrapper<>();

        if (Strings.isNotEmpty(bean.getPlanName())){
            queryWrapper.like("planName",bean.getPlanName());
        }

        if (Strings.isNotEmpty(bean.getPlanType())){
            queryWrapper.eq("planType",bean.getPlanType());
        }

        queryWrapper.eq("isDel",0);

        queryWrapper.orderByAsc("planStatus").orderByDesc("cycleStartTime");

        return queryWrapper;
    }


    public ResultInfo getPageList(PatrolPlanVo bean) {

        Page page = new Page(bean.getPageNum(), bean.getPageSize());

        Page<PatrolPlan> p = mapper.selectPage(page, getWrapper(bean));


        p.getRecords().forEach(x->{
            if(1==x.getExecuteType()){
                PatrolTimes patrolTime=new PatrolTimes();
                patrolTime.setPlanId(x.getId());
                List<PatrolTimes> patrolTimes=patrolTimesMapper.getList(patrolTime);
                x.setPatrolTimes(patrolTimes);
            }
        });


        return ResultInfo.success(p);
    }

    public ResultInfo toEdit(PatrolPlanVo bean) {
        if (null==bean.getCycleStartTime()&&bean.getPatrolTimes().isEmpty()){
            return ResultInfo.error("执行时间不可为空");
        }


        // 根据执行类型
        if (0==bean.getExecuteType()){
            SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            bean.setCronExpression(sdf.format(bean.getCycleStartTime()));
        }

        mapper.updateById(bean);

        PatrolPlan patrolPlan=mapper.selectById(bean.getId());

        patrolTimesMapper.update(null, Wrappers.<PatrolTimes>lambdaUpdate().
                eq(PatrolTimes::getIsDel,0).
                eq(PatrolTimes::getPlanId,patrolPlan.getId()).
                set(PatrolTimes::getIsDel,1));

        // 执行时间
        for (PatrolTimes item:bean.getPatrolTimes()) {
            String[] iii=item.getCycleStartTime().split(":");

            if (0==bean.getPlanCycle()){// 日
                item.setCronExpression(String.format("0 %s %s * * ?",Integer.parseInt(iii[1]),Integer.parseInt(iii[0])));
            }else if (2==bean.getPlanCycle()){// 周
                item.setCronExpression(String.format("0 %s %s ? * %s",Integer.parseInt(iii[1]),Integer.parseInt(iii[0]),bean.getCycleDate()));
            }else { // 月
                item.setCronExpression(String.format("0 %s %s %s * ?",Integer.parseInt(iii[1]),Integer.parseInt(iii[0]),bean.getCycleDate()));
            }

            item.setIsDel(0);
            item.setPlanId(bean.getId());
            patrolTimesMapper.insert(item);
        }



        patrolPeopleMapper.update(null, Wrappers.<PatrolPeople>lambdaUpdate().
                eq(PatrolPeople::getIsDel,0).
                eq(PatrolPeople::getPlanId,patrolPlan.getId()).
                set(PatrolPeople::getIsDel,1));

        // 执行人
        bean.getPatrolPeoples().forEach(x->{
            x.setIsDel(0);
            x.setPlanId(bean.getId());
            patrolPeopleMapper.insert(x);
        });


        patrolDeviceOrPointMapper.update(null, Wrappers.<PatrolDeviceOrPoint>lambdaUpdate().
                eq(PatrolDeviceOrPoint::getIsDel,0).
                eq(PatrolDeviceOrPoint::getPlanId,patrolPlan.getId()).
                set(PatrolDeviceOrPoint::getIsDel,1));

        // 设备或者点位
        bean.getPatrolDeviceOrPoints().forEach(x->{
            x.setIsDel(0);
            x.setPlanId(bean.getId());
            patrolDeviceOrPointMapper.insert(x);
        });


        if (bean.getPlanStatus()==0){
            if (0==bean.getExecuteType()){
                taskManager.createTask(G.PATROL_PLAN,String.valueOf(bean.getId()),bean.getCronExpression());
            }else {
                for (PatrolTimes item:bean.getPatrolTimes()) {
                    taskManager.createTask(G.PATROL_TIMES,String.valueOf(item.getId()),item.getCronExpression());
                }
            }

        }

        return ResultInfo.success();


    }


    public ResultInfo toAdd(PatrolPlanVo bean) {

        if (null==bean.getCycleStartTime()&&bean.getPatrolTimes().isEmpty()){
            return ResultInfo.error("执行时间不可为空");
        }

        bean.setIsDel(0);

        // 根据执行类型
        if (0==bean.getExecuteType()){
            SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            bean.setCronExpression(sdf.format(bean.getCycleStartTime()));
        }
        mapper.insert(bean);

        // 执行时间
        for (PatrolTimes item:bean.getPatrolTimes()) {
            String[] iii=item.getCycleStartTime().split(":");

            if (0==bean.getPlanCycle()){// 日
                item.setCronExpression(String.format("0 %s %s * * ?",Integer.parseInt(iii[1]),Integer.parseInt(iii[0])));
            }else if (2==bean.getPlanCycle()){// 周
                item.setCronExpression(String.format("0 %s %s ? * %s",Integer.parseInt(iii[1]),Integer.parseInt(iii[0]),bean.getCycleDate()));
            }else { // 月
                item.setCronExpression(String.format("0 %s %s %s * ?",Integer.parseInt(iii[1]),Integer.parseInt(iii[0]),bean.getCycleDate()));
            }

            item.setIsDel(0);
            item.setPlanId(bean.getId());
            patrolTimesMapper.insert(item);
        }

        // 执行人
        bean.getPatrolPeoples().forEach(x->{
            x.setIsDel(0);
            x.setPlanId(bean.getId());
            patrolPeopleMapper.insert(x);
        });

        // 设备或者点位
        bean.getPatrolDeviceOrPoints().forEach(x->{
            x.setIsDel(0);
            x.setPlanId(bean.getId());
            patrolDeviceOrPointMapper.insert(x);
        });

        if (bean.getPlanStatus()==0){
            Date currentTime = new Date();
            if (bean.getExecuteType()==0&&currentTime.after(bean.getCycleStartTime())&&currentTime.before(bean.getCycleEndTime())){

                PatrolTask patrolTask=new PatrolTask();
                patrolTask.setPlanId(String.valueOf(bean.getId()));
                patrolTask.setClockInCount(bean.getPatrolDeviceOrPoints().size() * bean.getPatrolPeoples().size());
                patrolTask.setClockNum(0);
                patrolTask.setTaskType(bean.getPlanType());
                patrolTask.setTaskStatus("0");
                patrolTask.setIsDel(0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                patrolTask.setCycleStartTime(dateFormat.format(bean.getCycleStartTime()));
                patrolTask.setCycleEndTime(dateFormat.format(bean.getCycleEndTime()));

                patrolTaskService.getBaseMapper().insert(patrolTask);


            }else if(bean.getExecuteType()==0){
                taskManager.createTask(G.PATROL_PLAN,String.valueOf(bean.getId()),bean.getCronExpression());
            }else {
                for (PatrolTimes item:bean.getPatrolTimes()) {
                    taskManager.createTask(G.PATROL_TIMES,String.valueOf(item.getId()),item.getCronExpression());
                }
            }

        }




        return  ResultInfo.success();

    }





    public static void main(String[] args) {
        List<String> cycleTimes= Arrays.asList("20:08,31:05,".split(","));

        String hours = "";
        String mins="";
        for (String item:cycleTimes) {
            String[] iii=item.split(":");
            hours=hours+Integer.parseInt(iii[0])+",";
            mins=mins+Integer.parseInt(iii[1])+",";
        }
        hours=hours.replaceAll(",$", "");
        mins=  mins.replaceAll(",$", "");
        System.out.println(hours);
        System.out.println(mins);



    }

    public ResultInfo getPatrolPlanById(PatrolPlan bean) {
        PatrolPlanVo patrolPlanVo=mapper.getPatrolPlanById(bean);

        PatrolPeople patrolPeople=new PatrolPeople();
        patrolPeople.setPlanId(bean.getId());
        List<PatrolPeople> peopleList=patrolPeopleMapper.getList(patrolPeople);
        patrolPlanVo.setPatrolPeoples(peopleList);


        PatrolDeviceOrPoint patrolDeviceOrPoint=new PatrolDeviceOrPoint();
        patrolDeviceOrPoint.setPlanId(bean.getId());
        List<PatrolDeviceOrPoint> patrolDeviceOrPoints=patrolDeviceOrPointMapper.getList(patrolDeviceOrPoint);
        patrolPlanVo.setPatrolDeviceOrPoints(patrolDeviceOrPoints);

        if(1==patrolPlanVo.getExecuteType()){
            PatrolTimes patrolTime=new PatrolTimes();
            patrolTime.setPlanId(bean.getId());
            List<PatrolTimes> patrolTimes=patrolTimesMapper.getList(patrolTime);
            patrolPlanVo.setPatrolTimes(patrolTimes);
        }


        return ResultInfo.success(patrolPlanVo);
    }

    public ResultInfo del(PatrolPlanVo bean) {
        bean.setIsDel(G.ISDEL_YES);

        mapper.updateById(bean);
        return ResultInfo.success("巡更计划已删除！");
    }

    public ResultInfo getUsers(SysUser bean) {
        List<SysUser> sysUsers=sysUserService.getBaseMapper().
                selectList(Wrappers.<SysUser>lambdaQuery().
                        eq(SysUser::getIsDel,0).
                        eq(SysUser::getIsEnable,1));


        return ResultInfo.success(sysUsers);
    }
}
