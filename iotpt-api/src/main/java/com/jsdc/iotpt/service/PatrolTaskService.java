package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.PatrolDeviceOrPointMapper;
import com.jsdc.iotpt.mapper.PatrolTaskMapper;
import com.jsdc.iotpt.model.PatrolClock;
import com.jsdc.iotpt.model.PatrolDeviceOrPoint;
import com.jsdc.iotpt.model.PatrolReport;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PatrolTaskService extends BaseService<PatrolTask> {
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;

    @Autowired
    private PatrolClockService patrolClockService;

    @Autowired
    private PatrolDeviceOrPointMapper patrolDeviceOrPointMapper ;

    @Autowired
    private PatrolPeopleMapper patrolPeopleMapper ;

    @Autowired
    private SysUserMapper sysUserMapper ;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PatrolReportMapper patrolReportMapper ;

    @Autowired
    private PatrolClockMapper patrolClockMapper ;

    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;
    @Autowired
    private SysBuildMapper sysBuildMapper;
    @Autowired
    private SysFileMapper sysFileMapper;

    public List<PatrolTaskVideoVo> getVideo(String id) {
        List<PatrolTaskVideoVo> list = patrolTaskMapper.getVideo(id);
        Integer userId = sysUserService.getUser().getId();

        list.forEach(p->{

            List<PatrolClock> selectList = patrolClockMapper.selectList(new LambdaQueryWrapper<PatrolClock>().eq(PatrolClock::getPatrolTaskId, id).eq(PatrolClock::getDeviceId, p.getDeviceId()).eq(PatrolClock::getCreateUser, userId));
            p.setPatrolClockList(selectList);

            List<PatrolReport> patrolReports = patrolReportMapper.selectList(new LambdaQueryWrapper<PatrolReport>().eq(PatrolReport::getPatrolTaskId, id).eq(PatrolReport::getDeviceId, p.getDeviceId()).eq(PatrolReport::getUserId, userId));
            p.setPatrolReportList(patrolReports);

            //用区域查楼层 物理位置
            if (StringUtils.isNotNull(p.getAreaId())) {
                SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(p.getAreaId());
                if (StringUtils.isNotNull(sysBuildArea)) {
                    //查楼宇
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        p.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea.getAreaName());
                    }
                }
            }

            //用区域查楼层 逻辑位置
            if (StringUtils.isNotNull(p.getAreaId()) && p.getAreaId() != 0) {
                SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(p.getAreaId());
                if (StringUtils.isNotNull(sysBuildArea2)) {
                    //查楼层
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        //查楼宇
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        p.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                    }
                }
            } else if (StringUtils.isNotNull(p.getFloorId()) && p.getFloorId() != 0) {
                //查楼层
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(p.getFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    p.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
                }
            } else if (StringUtils.isNotNull(p.getBuildId()) && p.getBuildId() != 0) {
                //查楼宇
                SysBuild sysBuild = sysBuildMapper.selectById(p.getBuildId());
                p.setAreaNames(sysBuild.getBuildName());
            }



        });

        return list;
    }

    /**
     * 巡更任务列表
     * Author wzn
     * Date 2024/1/9 9:46
     */
    public Page<PatrolTask> selectStallList(PatrolTaskVo patrolTaskVo) {
        Page<PatrolTask> page = new Page<>(patrolTaskVo.getPageNo(), patrolTaskVo.getPageSize());
        page.setOptimizeCountSql(false);
        Page<PatrolTask> patrolTaskPage = patrolTaskMapper.getPatrolTaskList(page, patrolTaskVo);
        if(CollectionUtil.isNotEmpty(patrolTaskPage.getRecords())){
            for(PatrolTask p:patrolTaskPage.getRecords()){
                //任务类型翻译 1视频巡更 2电子巡更
                if("1".equals(p.getTaskType())){
                    p.setTaskTypeName("视频巡更");
                }else if("2".equals(p.getTaskType())){
                    p.setTaskTypeName("电子巡更");
                }

                //任务状态 0待处理 1已处理 2超时未处理 3缺卡
                if("0".equals(p.getTaskStatus())){
                    p.setTaskStatusName("待处理");
                }else if("1".equals(p.getTaskStatus())){
                    p.setTaskStatusName("已处理");
                }else if("2".equals(p.getTaskStatus())){
                    p.setTaskStatusName("超时未处理");
                }else if("3".equals(p.getTaskStatus())){
                    p.setTaskStatusName("缺卡");
                }
            }
        }
        return patrolTaskPage;
    }

    public List<PointVo> getPoint(String id) {
        return patrolTaskMapper.getPoint(id);
    }

    public PatrolTask clock(Integer id, Integer deviceId) {
        if (null == id) {
            throw new RuntimeException("ID不能为空");
        }
        if (null == deviceId) {
            throw new RuntimeException("设备ID不能为空");
        }
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);
        if (null == patrolTask) {
            throw new RuntimeException("任务不存在");
        }


        //打卡
        PatrolClock patrolClock = new PatrolClock();
        patrolClock.setDeviceId(deviceId);
        patrolClock.setPatrolTaskId(id);
        patrolClock.setSignTime(DateUtil.now());
        patrolClock.setCreateUser(sysUserService.getUser().getId());
        patrolClock.insert();

        List<PatrolTaskVideoVo> list = patrolTaskMapper.getVideo(String.valueOf(id));
        List<PatrolClock> list2 = patrolClockMapper.selectList(new LambdaQueryWrapper<PatrolClock>().select(PatrolClock::getDeviceId).eq(PatrolClock::getPatrolTaskId, id).groupBy(PatrolClock::getDeviceId));
        if (list.size() == list2.size()) {
            patrolTask.setTaskStatus("1");
        }

        patrolTask.setId(id);
        
        if(null == patrolTask.getClockNum()){
            patrolTask.setClockNum(1);
        }else{
            patrolTask.setClockNum(patrolTask.getClockNum() + 1);
        }


        patrolTaskMapper.updateById(patrolTask);
        return patrolTask;
    }



    /**
    *视频巡更查看
    * Author wzn
    * Date 2024/1/11 13:55
    */
        public Map<String,Object> videoTourInfo(String taskId,String userId){
        PatrolTask patrolTask =patrolTaskMapper.selectById(taskId) ;

        Map<String,Object> map = new HashMap<>() ;
        QueryWrapper<PatrolDeviceOrPoint> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("planId",patrolTask.getPlanId()) ;
        queryWrapper.eq("isDel","0") ;

        List<PatrolDeviceOrPoint> patrolDeviceOrPointList = patrolDeviceOrPointMapper.selectList(queryWrapper) ;
        //左侧设备信息

            if(CollectionUtil.isNotEmpty(patrolDeviceOrPointList)){
                for(PatrolDeviceOrPoint p:patrolDeviceOrPointList){
                }
            }

        map.put("patrolDeviceOrPointList",patrolDeviceOrPointList) ;

        QueryWrapper<PatrolPeople> queryWrapper2 = new QueryWrapper<>() ;
        queryWrapper2.eq("planId",patrolTask.getPlanId()) ;
        queryWrapper2.eq("userId",userId) ;
        queryWrapper2.eq("isDel",0) ;
        PatrolPeople patrolPeople = patrolPeopleMapper.selectOne(queryWrapper2) ;
        PatrolInfoVo patrolInfoVo = new PatrolInfoVo() ;
        if(StringUtils.isNotEmpty(patrolTask.getCycleStartTime())){
            patrolInfoVo.setStartTime(patrolTask.getCycleStartTime());
        }
        if(StringUtils.isNotEmpty(patrolTask.getCycleEndTime())){
            patrolInfoVo.setEndTime(patrolTask.getCycleEndTime());
        }
        if(null != patrolPeople){
            SysUser sysUser = sysUserMapper.selectById(patrolPeople.getUserId()) ;
            if(null != sysUser){
                patrolInfoVo.setName(sysUser.getRealName());
            }
        }
        map.put("patrolInfoVo",patrolInfoVo) ;

        QueryWrapper<PatrolReport> queryWrapper3 = new QueryWrapper<>() ;
        queryWrapper3.eq("patrolTaskId",taskId) ;
        queryWrapper3.eq("userId", userId);
        List<PatrolReport> patrolReportList = patrolReportMapper.selectList(queryWrapper3);
        for (PatrolReport report : patrolReportList) {
            report.setFileList(sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizType, "6").eq(SysFile::getBizId, String.valueOf(report.getId()))));
        }
        map.put("patrolReportList",patrolReportList) ;

        QueryWrapper<PatrolClock> queryWrapper4 = new QueryWrapper<>() ;
        queryWrapper4.eq("patrolTaskId",taskId) ;
        List<PatrolClock> patrolClockList = patrolClockMapper.selectList(queryWrapper4) ;
        map.put("patrolClockList",patrolClockList) ;



        return map ;
    }
}
