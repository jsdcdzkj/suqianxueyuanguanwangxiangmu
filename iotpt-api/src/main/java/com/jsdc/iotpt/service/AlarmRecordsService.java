package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.new_alarm.*;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.AliSmsUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import com.jsdc.iotpt.vo.operate.MissionItemRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AlarmRecordsService extends BaseService<AlarmRecords> {

    @Autowired
    private AlarmRecordsMapper alarmRecordsMapper;

    @Autowired
    private SysBuildAreaMapper areaMapper;

    @Autowired
    private SysBuildFloorMapper floorMapper;

    @Autowired
    private SysBuildMapper sysBuildMapper;

    @Autowired
    private SysDictService dictService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private FalseAlarmRecordsMapper falseAlarmRecordsMapper;

    @Autowired
    private SysUserService userService;

    @Autowired
    private AlarmContentConfigMapper alarmContentConfigMapper;

    @Autowired
    private AlarmCategoryMapper alarmCategoryMapper;

    @Autowired
    private AlarmPlanConfigMapper alarmPlanConfigMapper;

    @Autowired
    private AlarmPlanTimeConfigMapper alarmPlanTimeConfigMapper;

    @Autowired
    private AppPushMsgService appPushMsgService;

    @Autowired
    private TeamGroupUserMapper teamGroupUserMapper;

    @Value("${linkmqtt.ip}")
    private String mqttUrl;

    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;
    @Autowired
    private AlarmGroupMapper alarmGroupMapper;

    @Autowired
    private SysBuildAreaMapper buildAreaMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<AlarmRecords> getPageList(AlarmRecordsVo vo) {
        LambdaQueryWrapper<AlarmRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmRecords::getIsDel,0).eq(StringUtils.isNotNull(vo.getAlarmType()),AlarmRecords::getAlarmType,vo.getAlarmType())
                .eq(StringUtils.isNotNull(vo.getFloorId()),AlarmRecords::getFloorId,vo.getFloorId())
                .eq(StringUtils.isNotNull(vo.getAreaId()),AlarmRecords::getAreaId,vo.getAreaId())
                .eq(StringUtils.isNotNull(vo.getAlarmContentId()),AlarmRecords::getAlarmContentId,vo.getAlarmContentId())
                .eq(StringUtils.isNotNull(vo.getDeviceSource()),AlarmRecords::getDeviceSource,vo.getDeviceSource())
                .eq(StringUtils.isNotNull(vo.getAlarmLevel()),AlarmRecords::getAlarmLevel,vo.getAlarmLevel())
                .eq(StringUtils.isNotNull(vo.getAlarmCategory()),AlarmRecords::getAlarmCategory,vo.getAlarmCategory())
                .eq(StringUtils.isNotNull(vo.getHandleStatus()),AlarmRecords::getHandleStatus,vo.getHandleStatus())
                .like(StringUtils.isNotNull(vo.getDeviceName()), AlarmRecords::getDeviceName,vo.getDeviceName())
                .between(StringUtils.isNotNull(vo.getStartTime()),AlarmRecords::getAlarmTime,vo.getStartTime()+" 00:00:00",vo.getEndTime()+" 23:59:59")
                .orderByDesc(AlarmRecords::getAlarmTime);
        Page<AlarmRecords> alarmRecordsPage = alarmRecordsMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        alarmRecordsPage.getRecords().forEach(record -> {
            // 排除暂未回调的回放视频
            if (StringUtils.isNotBlank(record.getAlarmVideo()) && !record.getAlarmVideo().startsWith("http")) {
                record.setAlarmVideo(null);
            }
            //区域
            SysBuildArea sysBuildArea = areaMapper.selectById(record.getAreaId());
            record.setAreaName(sysBuildArea.getAreaName());
            //楼层
            SysBuildFloor sysBuildFloor = floorMapper.selectById(record.getFloorId());
            record.setFloorName(sysBuildFloor.getFloorName());
            //建筑
            SysBuild sysBuild = sysBuildMapper.selectById(record.getBuildId());
            record.setBuildName(sysBuild.getBuildName());
            //告警等级
            SysDict alarmLevel = dictService.selectByValueAndType("alarmLevel", String.valueOf(record.getAlarmLevel()));
            record.setAlarmLevelName(alarmLevel.getDictLabel());
            //告警类型
            AlarmCategory alarmCategory = alarmCategoryMapper.selectById(record.getAlarmCategory());
            record.setAlarmCategoryName(alarmCategory.getName());
            //报警状态
            SysDict alarmStatus = dictService.selectByValueAndType("alarmStatus", String.valueOf(record.getAlarmStatus()));
            record.setAlarmStatusName(alarmStatus.getDictLabel());
            //报警来源
            SysDict deviceSource = dictService.selectByValueAndType("deviceSource", String.valueOf(record.getDeviceSource()));
            record.setDeviceSourceName(deviceSource.getDictLabel());
            //处理状态
            SysDict handleStatus = dictService.selectByValueAndType("handleStatus", String.valueOf(record.getHandleStatus()));
            record.setHandleStatusName(handleStatus.getDictLabel());
        });
        return alarmRecordsPage;

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<AlarmRecords> getList(AlarmRecords entity) {
        QueryWrapper<AlarmRecords> queryWrapper = new QueryWrapper<>();
        return alarmRecordsMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateAlarmRecords(AlarmRecords bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public AlarmRecords getEntityById(Integer id) {
        AlarmRecords record = getById(id);
        //区域
        SysBuildArea sysBuildArea = areaMapper.selectById(record.getAreaId());
        record.setAreaName(sysBuildArea.getAreaName());
        //楼层
        SysBuildFloor sysBuildFloor = floorMapper.selectById(record.getFloorId());
        record.setFloorName(sysBuildFloor.getFloorName());
        //建筑
        SysBuild sysBuild = sysBuildMapper.selectById(record.getBuildId());
        record.setBuildName(sysBuild.getBuildName());
        //告警等级
        SysDict alarmLevel = dictService.selectByValueAndType("alarmLevel", String.valueOf(record.getAlarmLevel()));
        record.setAlarmLevelName(alarmLevel.getDictLabel());
        //告警类型
        AlarmCategory alarmCategory = alarmCategoryMapper.selectById(record.getAlarmCategory());
        record.setAlarmCategoryName(alarmCategory.getName());
        //报警状态
        SysDict alarmStatus = dictService.selectByValueAndType("alarmStatus", String.valueOf(record.getAlarmStatus()));
        record.setAlarmStatusName(alarmStatus.getDictLabel());
        //报警来源
        SysDict deviceSource = dictService.selectByValueAndType("deviceSource", String.valueOf(record.getDeviceSource()));
        record.setDeviceSourceName(deviceSource.getDictLabel());
        //处理状态
        SysDict handleStatus = dictService.selectByValueAndType("handleStatus", String.valueOf(record.getHandleStatus()));
        record.setHandleStatusName(handleStatus.getDictLabel());
        //告警编码
        AlarmContentConfig alarmContentConfig = alarmContentConfigMapper.selectById(record.getAlarmContentId());
        record.setAlarmCode(alarmContentConfig.getAlarmCode());
        return record;
    }


    /**
     * 角标
     * @return
     */
    public List<AlarmRecordsVo> getsubscript(AlarmRecordsVo bean){
        List<AlarmRecordsVo> subscript = alarmRecordsMapper.getSubscript(bean);
        subscript.forEach(alarmRecordsVo -> {
            //告警类型
            AlarmCategory alarmCategory = alarmCategoryMapper.selectById(alarmRecordsVo.getAlarmCategory());
            alarmRecordsVo.setAlarmCategoryName(alarmCategory.getName());
        });
        return subscript;
    }




    /**
     * 统计数量根据程度
     * @return
     */
    public List<AlarmRecordsVo> groupAlarmLevel(AlarmRecordsVo bean){
        List<AlarmRecordsVo> groupAlarmLevel = alarmRecordsMapper.groupAlarmLevel( bean);
        groupAlarmLevel.forEach(alarmRecordsVo -> {
            //告警等级
            SysDict alarmLevel = dictService.selectByValueAndType("alarmLevel", String.valueOf(alarmRecordsVo.getAlarmLevel()));
            alarmRecordsVo.setAlarmLevelName(alarmLevel.getDictLabel());
        });
        return groupAlarmLevel;
    }

    /**
     * 折线图
     * @return
     */
    public Map<String,Object> line(AlarmRecordsVo bean){
        List<Map<String, Object>> line = alarmRecordsMapper.line(bean);
        List<String> lastSomeday = DateUtils.getLastSomeday(new Date(), 6);
        Map<String,Object> map = new HashMap<>();
        map.put("line",line);
        map.put("day",lastSomeday);
        return map;
    }


    /**
     * 批量处理报警
     * 1=误报
     * 2=上报
     * 3=忽略
     * @return
     */
    public void batchProcessing(AlarmRecordsVo bean){
        List<String> list = Arrays.asList(bean.getIds().split(","));
        SysUser user = userService.getUser();
        if (bean.getHandleStatus().equals("1")){
            LambdaUpdateWrapper<AlarmRecords> wrapper=new LambdaUpdateWrapper<>();
            wrapper.in(AlarmRecords::getId,list).set(AlarmRecords::getHandleStatus,bean.getHandleStatus()).set(AlarmRecords::getHandleUser,user.getId())
                    .set(AlarmRecords::getHandleTime,DateUtils.dateToStr(new Date()));
            alarmRecordsMapper.update(null,wrapper);
            list.forEach(id->{
                AlarmRecords records = this.getById(id);
                FalseAlarmRecords alarmRecords=new FalseAlarmRecords(records,bean.getJudge());
                falseAlarmRecordsMapper.insert(alarmRecords);//直接新增
            });
        }else if (bean.getHandleStatus().equals("2")){
            LambdaUpdateWrapper<AlarmRecords> wrapper=new LambdaUpdateWrapper<>();
            wrapper.in(AlarmRecords::getId,list).set(AlarmRecords::getHandleStatus,bean.getHandleStatus()).set(AlarmRecords::getHandleUser,user.getId())
                    .set(AlarmRecords::getHandleTime,DateUtils.dateToStr(new Date()));
            alarmRecordsMapper.update(null,wrapper);
            list.forEach(id->{
            AlarmRecords records = this.getById(id);
            Mission mission = getMission(bean, records);
            String areaName = buildAreaMapper.selectById(records.getAreaId()).getAreaName();
            mission.setTitle(areaName+records.getDeviceName()+"告警上报");
            this.missionService.saveMission(mission);
            if (StringUtils.isNotEmpty(records.getAlarmImg())){
                SysFile one = new SysFile();
                one.setIsDel(0);
                one.setFileName(records.getAlarmImg());
                one.setBizType("4");
                one.setCreateTime(new Date());
                one.setFileUrl(records.getAlarmImg());
                one.setBizId(mission.getId()+"");
                one.insert();
            }
            });
        }else if (bean.getHandleStatus().equals("3")){
            LambdaUpdateWrapper<AlarmRecords> wrapper=new LambdaUpdateWrapper<>();
            wrapper.in(AlarmRecords::getId,list).set(AlarmRecords::getHandleStatus,bean.getHandleStatus()).set(AlarmRecords::getHandleUser,user.getId())
                    .set(AlarmRecords::getHandleTime,DateUtils.dateToStr(new Date()));
            alarmRecordsMapper.update(null,wrapper);
        }else{
            LambdaUpdateWrapper<AlarmRecords> wrapper=new LambdaUpdateWrapper<>();
            wrapper.in(AlarmRecords::getId,list).set(AlarmRecords::getIsDel,1).set(AlarmRecords::getDelUser,user.getId());
            alarmRecordsMapper.update(null,wrapper);
        }
    }

    /**
     * 导出
     */
    public void export(AlarmRecordsVo vo, HttpServletResponse response) {
        // 查询数据
        LambdaQueryWrapper<AlarmRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmRecords::getIsDel,0).eq(StringUtils.isNotNull(vo.getAlarmType()),AlarmRecords::getAlarmType,vo.getAlarmType())
                .eq(StringUtils.isNotNull(vo.getFloorId()),AlarmRecords::getFloorId,vo.getFloorId())
                .eq(StringUtils.isNotNull(vo.getAreaId()),AlarmRecords::getAreaId,vo.getAreaId())
                .eq(StringUtils.isNotNull(vo.getAlarmContentId()),AlarmRecords::getAlarmContentId,vo.getAlarmContentId())
                .eq(StringUtils.isNotNull(vo.getDeviceSource()),AlarmRecords::getDeviceSource,vo.getDeviceSource())
                .eq(StringUtils.isNotNull(vo.getAlarmLevel()),AlarmRecords::getAlarmLevel,vo.getAlarmLevel())
                .eq(StringUtils.isNotNull(vo.getHandleStatus()),AlarmRecords::getHandleStatus,vo.getHandleStatus())
                .in(CollUtil.isNotEmpty(vo.getHandleStatusList()),AlarmRecords::getHandleStatus,vo.getHandleStatusList())
                .like(StringUtils.isNotNull(vo.getDeviceName()), AlarmRecords::getDeviceName,vo.getDeviceName())
                .eq(StringUtils.isNotNull(vo.getAlarmCategory()),AlarmRecords::getAlarmCategory,vo.getAlarmCategory())
                .between(StringUtils.isNotNull(vo.getStartTime()),AlarmRecords::getAlarmTime,vo.getStartTime()+" 00:00:00",vo.getEndTime()+" 23:59:59")
                .in(StringUtils.isNotNull(vo.getIds()),AlarmRecords::getId,Arrays.asList(vo.getIds().split(",")))
                .orderByDesc(AlarmRecords::getAlarmTime);
        Page<AlarmRecords> alarmRecordsPage = alarmRecordsMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        List<AlarmRecords> records = alarmRecordsPage.getRecords();
        records.forEach(record -> {
            //区域
            SysBuildArea sysBuildArea = areaMapper.selectById(record.getAreaId());
            record.setAreaName(sysBuildArea.getAreaName());
            //楼层
            SysBuildFloor sysBuildFloor = floorMapper.selectById(record.getFloorId());
            record.setFloorName(sysBuildFloor.getFloorName());
            //建筑
            SysBuild sysBuild = sysBuildMapper.selectById(record.getBuildId());
            record.setBuildName(sysBuild.getBuildName());
            //告警等级
            SysDict alarmLevel = dictService.selectByValueAndType("alarmLevel", String.valueOf(record.getAlarmLevel()));
            record.setAlarmLevelName(alarmLevel.getDictLabel());
            //告警类型
            AlarmCategory alarmCategory = alarmCategoryMapper.selectById(record.getAlarmCategory());
            record.setAlarmCategoryName(alarmCategory.getName());
            //报警来源
            SysDict deviceSource = dictService.selectByValueAndType("deviceSource", String.valueOf(record.getDeviceSource()));
            record.setDeviceSourceName(deviceSource.getDictLabel());
            //报警状态
            SysDict alarmStatus = dictService.selectByValueAndType("alarmStatus", String.valueOf(record.getAlarmStatus()));
            record.setAlarmStatusName(alarmStatus.getDictLabel());
            //处理状态
            SysDict handleStatus = dictService.selectByValueAndType("handleStatus", String.valueOf(record.getHandleStatus()));
            record.setHandleStatusName(handleStatus.getDictLabel());
        });
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.addHeaderAlias("deviceName", "告警设备");
        writer.addHeaderAlias("alarmCategoryName", "告警类型");
        writer.addHeaderAlias("alarmLevelName", "告警等级等级");
        writer.addHeaderAlias("alarmTime", "时间");
        writer.addHeaderAlias("areaName", "区域");
        writer.addHeaderAlias("alarmContentStr", "事件内容");//事件内容
        writer.addHeaderAlias("alarmStatusName", "告警状态");
        writer.addHeaderAlias("handleStatusName", "处理状态");
        writer.setOnlyAlias(true);
        writer.write(records, true);
        OutputStream outputStream = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("alarmRecords.xls", "UTF-8"));
            outputStream = response.getOutputStream();
            writer.flush(outputStream, true);
//            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 弹窗 todo
     *
     * @param
     * @return
     */
    public AlarmRecords getNewEntity() {
        List<String> list = Arrays.asList("3,4".split(","));
        LambdaQueryWrapper<AlarmRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmRecords::getIsDel,0).eq(AlarmRecords::getAlarmType,1).in(AlarmRecords::getAlarmLevel,list)
                .orderByDesc(AlarmRecords::getAlarmTime);
        List<AlarmRecords> list1 = list(queryWrapper);
        if (list1.size()>0){
           AlarmRecords record = list1.get(0);
           //区域
           SysBuildArea sysBuildArea = areaMapper.selectById(record.getAreaId());
           record.setAreaName(sysBuildArea.getAreaName());
           //楼层
           SysBuildFloor sysBuildFloor = floorMapper.selectById(record.getFloorId());
           record.setFloorName(sysBuildFloor.getFloorName());
           //建筑
           SysBuild sysBuild = sysBuildMapper.selectById(record.getBuildId());
           record.setBuildName(sysBuild.getBuildName());
           //告警等级
           SysDict alarmLevel = dictService.selectByValueAndType("alarmLevel", String.valueOf(record.getAlarmLevel()));
           record.setAlarmLevelName(alarmLevel.getDictLabel());
           //告警类型
           AlarmCategory alarmCategory = alarmCategoryMapper.selectById(record.getAlarmCategory());
           record.setAlarmCategoryName(alarmCategory.getName());
           //报警状态
           SysDict alarmStatus = dictService.selectByValueAndType("alarmStatus", String.valueOf(record.getAlarmStatus()));
           record.setAlarmStatusName(alarmStatus.getDictLabel());
           //报警来源
           SysDict deviceSource = dictService.selectByValueAndType("deviceSource", String.valueOf(record.getDeviceSource()));
           record.setDeviceSourceName(deviceSource.getDictLabel());
           //处理状态
           SysDict handleStatus = dictService.selectByValueAndType("handleStatus", String.valueOf(record.getHandleStatus()));
           record.setHandleStatusName(handleStatus.getDictLabel());
            return record;
       }else {
            return new AlarmRecords();
        }

    }



    public List<AlarmCategory> getAlarmCategory(Integer alarmType) {
        LambdaQueryWrapper<AlarmCategory> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(AlarmCategory::getIsDel,0).eq(AlarmCategory::getAlarmType,alarmType);
        List<AlarmCategory> alarmCategories = alarmCategoryMapper.selectList(wrapper);
        return alarmCategories;
    }


    @NotNull
    private static Mission getMission(AlarmRecordsVo bean, AlarmRecords records) {
        Mission mission = new Mission();
        mission.setSource(1);
        mission.setLevels(1);
        if (records.getDeviceSource()==1){//摄像头
            mission.setDeviceType(2);
        }else if (records.getDeviceSource()==4){//电梯
            mission.setDeviceType(4);
        }else{
            mission.setDeviceType(1);
        }
        mission.setDeviceId(records.getDeviceId());
        mission.setSourceId(records.getId());
        mission.setSubstance(records.getAlarmContentStr());
        mission.setNotes(records.getAlarmContentStr());
        mission.setReportingTime(new Date());
        mission.setStates(1);
        mission.setIsReads(0);
        mission.setIs_del(0);
        mission.setAreaId(records.getAreaId());
        return mission;
    }


    public void saveRecordAndExecutePlan(AlarmRecords alarm) {
        CompletableFuture.runAsync(() -> {
            Integer contentId = alarm.getAlarmContentId();
            AlarmContentConfig contentConfig = alarmContentConfigMapper.selectById(contentId);
            // 告警配置未启用
            if (contentConfig.getEnable() == 0) {
                return;
            }
            AlarmPlanConfig planConfig = alarmPlanConfigMapper.selectByContentId(String.valueOf(contentId));
            // 未配置告警预案
            if (Objects.isNull(planConfig)) {
                return;
            }
            // 告警预案未启用
            if (planConfig.getEnable() == 0) {
                return;
            }
            // 未配置联动规则
            if (StrUtil.isBlank(planConfig.getLinkageRule())) {
                return;
            }
            AlarmPlanTimeConfig timeConfig = alarmPlanTimeConfigMapper.selectById(planConfig.getPlanTimeConfigId());
            // 未配置时间模板
            if (Objects.isNull(timeConfig)) {
                return;
            }
            DateTime alarmTime = DateUtil.parseDateTime(alarm.getAlarmTime());
            int week = DateUtil.dayOfWeek(alarmTime);
            String time = null;
            switch (week) {
                case 1:
                    time = timeConfig.getSunday();
                    break;
                case 2:
                    time = timeConfig.getMonday();
                    break;
                case 3:
                    time = timeConfig.getTuesday();
                    break;
                case 4:
                    time = timeConfig.getWednesday();
                    break;
                case 5:
                    time = timeConfig.getThursday();
                    break;
                case 6:
                    time = timeConfig.getFriday();
                    break;
                case 7:
                    time = timeConfig.getSaturday();
                    break;
            }
            if (StrUtil.isBlank(time)) {
                return;
            }
            boolean isIn = false;
            List<AlarmPlanTimeVO> timeList = JSON.parseArray(time, AlarmPlanTimeVO.class);
            for (AlarmPlanTimeVO vo : timeList) {
                DateTime start = DateUtil.parse(DateUtil.formatDate(alarmTime) + " " + vo.getStart() + ":00");
                DateTime end = DateUtil.parse(DateUtil.formatDate(alarmTime) + " " + vo.getEnd() + ":59");
                if (alarmTime.isIn(start, end)) {
                    isIn = true;
                    break;
                }
            }
            if (!isIn) {
                return;
            }

            // 联动规则
            List<String> linkageRule = Arrays.asList(planConfig.getLinkageRule().split(","));
            // 上传至告警中心
            if (linkageRule.contains("1")) {
                alarm.insert();
            }
            // 生成待指派工单
            if (linkageRule.contains("2")) {
                Mission mission = new Mission();
                mission.setSource(1);
                mission.setLevels(alarm.getAlarmLevel());
                if (alarm.getDeviceSource()==1){//摄像头
                    mission.setDeviceType(2);
                }else if (alarm.getDeviceSource()==4){//电梯
                    mission.setDeviceType(4);
                }else{
                    mission.setDeviceType(1);
                }
                mission.setDeviceId(alarm.getDeviceId());
                mission.setSourceId(alarm.getId());
                mission.setSubstance(alarm.getAlarmContentStr());
                mission.setNotes(alarm.getAlarmContentStr());
                mission.setReportingTime(new Date());
                mission.setStates(1);
                mission.setIsReads(0);
                mission.setIs_del(0);
                mission.setAreaId(alarm.getAreaId());
                missionService.saveMission(mission);
            }
            // 通知指定人员
            if (linkageRule.contains("3")) {
                if (Objects.isNull(planConfig.getPushType()) || StrUtil.isBlank(planConfig.getPushUser())) {
                    return;
                }
                List<String> pushConfig = Arrays.asList(planConfig.getPushConfig().split(","));
                List<String> pushUser = Arrays.asList(planConfig.getPushUser().split(","));
                List<Integer> userIds = null;
                List<String> userPhones = null;
                // 按照部门推送
                if (planConfig.getPushType() == 1) {
                    List<SysUser> userList = userService.getBaseMapper().selectList(new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getIsDel, 0)
                            .in(SysUser::getDeptId, pushUser)
                    );
                    if (CollUtil.isNotEmpty(userList)) {
                        userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
                        userPhones = userList.stream().map(SysUser::getPhone).filter(StrUtil::isNotBlank).collect(Collectors.toList());
                    }
                }
                // 指定用户推送
                else if (planConfig.getPushType() == 2) {
                    List<SysUser> userList = userService.getBaseMapper().selectList(new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getIsDel, 0)
                            .in(SysUser::getId, pushUser)
                    );
                    if (CollUtil.isNotEmpty(userList)) {
                        userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
                        userPhones = userList.stream().map(SysUser::getPhone).filter(StrUtil::isNotBlank).collect(Collectors.toList());
                    }
                }
                // 按照运维班组推送
                else if (planConfig.getPushType() == 3) {
                    List<TeamGroupUser> groupUserList = teamGroupUserMapper.selectList(new LambdaQueryWrapper<TeamGroupUser>()
                            .eq(TeamGroupUser::getIsDel, 0)
                            .in(TeamGroupUser::getGroupId, pushUser)
                    );
                    if (CollUtil.isEmpty(groupUserList)) {
                        return;
                    }
                    userIds = groupUserList.stream().map(TeamGroupUser::getUserId).collect(Collectors.toList());
                    List<SysUser> userList = userService.getBaseMapper().selectList(new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getIsDel, 0)
                            .in(SysUser::getId, userIds)
                    );
                    if (CollUtil.isNotEmpty(userList)) {
                        userPhones = userList.stream().map(SysUser::getPhone).filter(StrUtil::isNotBlank).collect(Collectors.toList());
                    }
                } else {
                    return;
                }

                SysBuild build = sysBuildMapper.selectById(alarm.getBuildId());
                SysBuildFloor floor = floorMapper.selectById(alarm.getFloorId());
                SysBuildArea area = areaMapper.selectById(alarm.getAreaId());
                AlarmCategory category = alarmCategoryMapper.selectById(alarm.getAlarmCategory());

                // APP消息推送
                if (pushConfig.contains("1")) {
                    if (CollUtil.isEmpty(userIds)) {
                        return;
                    }
                    AppPushMsgVo vo = new AppPushMsgVo();
                    vo.setRegion(build.getBuildName() + floor.getFloorName() + area.getAreaName());
                    vo.setType(category.getName());
                    vo.setContent(alarm.getAlarmContentStr());
                    vo.setUserIdList(userIds);
                    vo.setAppConfigCode("SYSTEM_ALARM");
                    appPushMsgService.pushMsg(vo);
                }
                // PC弹窗推送
                if (pushConfig.contains("2")) {
                    HashMap<String, Object> paramMap = new HashMap<>();
                    paramMap.put("id", alarm.getId());
                    paramMap.put("areaName", area.getAreaName());
                    paramMap.put("areaId", alarm.getAreaId());
                    paramMap.put("deviceName", alarm.getDeviceName());
                    paramMap.put("warnTypeName", alarm.getAlarmContentStr());
                    paramMap.put("warnLevel", alarm.getAlarmLevel());
                    paramMap.put("alertTime", alarm.getAlarmTime());
                    paramMap.put("warnContent", alarm.getAlarmContentStr());
                    paramMap.put("warnCategory", alarm.getAlarmCategory());
                    HttpUtil.post(mqttUrl + "/mqtt/sendMsg", paramMap);
                }
                // 短信
                if (pushConfig.contains("3")) {
                    String phones = StrUtil.join(",", userPhones);
                    AliSmsUtil.alarmNotice(phones, build.getBuildName() + floor.getFloorName() + area.getAreaName(), category.getName(), alarm.getAlarmContentStr());
                }
                // 电话
                if (pushConfig.contains("4")) {

                }
            }
        });
    }

    // 消防告警上报
    public void fireFighting(String msg) {
        // 去除心跳
        if (msg.length() > 82) {
            if(hexToInt(msg.substring(80,82)) == 1 || hexToInt(msg.substring(80,82)) == 2){
                String deviceCode = hexStringToAscii(msg.substring(64,80)) ;
                // Redis幂等校验，防止重复告警
                String redisKey = "XFALARM:" + deviceCode;
                if (RedisUtils.existsKey(redisKey)) {
                    return;
                }
                RedisUtils.setBeanValue(redisKey, deviceCode, 5);

                AlarmRecords record = new AlarmRecords();
                record.setDeviceSource(5);
                record.setAlarmTime(DateUtil.now());
                record.setAlarmStatus("1");
                record.setHandleStatus("0");
                record.setIsDel(0);
                saveRecordAndExecutePlan(record);
            }
        }
    }

    private static int hexToInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    private static String hexStringToAscii(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public Page<AlarmRecords> getHistoryList(AlarmRecordsVo vo) {
        List<String> list = Arrays.asList("2", "3");
        LambdaQueryWrapper<AlarmRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmRecords::getIsDel,0).eq(StringUtils.isNotNull(vo.getAlarmType()),AlarmRecords::getAlarmType,vo.getAlarmType())
                .eq(StringUtils.isNotNull(vo.getFloorId()),AlarmRecords::getFloorId,vo.getFloorId())
                .eq(StringUtils.isNotNull(vo.getAreaId()),AlarmRecords::getAreaId,vo.getAreaId())
                .eq(StringUtils.isNotNull(vo.getAlarmContentId()),AlarmRecords::getAlarmContentId,vo.getAlarmContentId())
                .eq(StringUtils.isNotNull(vo.getDeviceSource()),AlarmRecords::getDeviceSource,vo.getDeviceSource())
                .eq(StringUtils.isNotNull(vo.getAlarmLevel()),AlarmRecords::getAlarmLevel,vo.getAlarmLevel())
                .eq(StringUtils.isNotNull(vo.getAlarmCategory()),AlarmRecords::getAlarmCategory,vo.getAlarmCategory())
                .eq(StrUtil.isNotBlank(vo.getHandleStatus()),AlarmRecords::getHandleStatus,vo.getHandleStatus())
                .eq(AlarmRecords::getAlarmType,1)
                .in(AlarmRecords::getHandleStatus,list)
                .like(StrUtil.isNotBlank(vo.getDeviceName()), AlarmRecords::getDeviceName,vo.getDeviceName())
                .between(StrUtil.isNotBlank(vo.getStartTime()),AlarmRecords::getAlarmTime,vo.getStartTime()+" 00:00:00",
                        vo.getEndTime()+" 23:59:59")
                .orderByDesc(AlarmRecords::getAlarmTime);
        Page<AlarmRecords> alarmRecordsPage = alarmRecordsMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        alarmRecordsPage.getRecords().forEach(record -> {
            //区域
            SysBuildArea sysBuildArea = areaMapper.selectById(record.getAreaId());
            record.setAreaName(sysBuildArea.getAreaName());
            //楼层
            SysBuildFloor sysBuildFloor = floorMapper.selectById(record.getFloorId());
            record.setFloorName(sysBuildFloor.getFloorName());
            //建筑
            SysBuild sysBuild = sysBuildMapper.selectById(record.getBuildId());
            record.setBuildName(sysBuild.getBuildName());
            //告警等级
            SysDict alarmLevel = dictService.selectByValueAndType("alarmLevel", String.valueOf(record.getAlarmLevel()));
            record.setAlarmLevelName(alarmLevel.getDictLabel());
            //告警类型
            AlarmCategory alarmCategory = alarmCategoryMapper.selectById(record.getAlarmCategory());
            record.setAlarmCategoryName(alarmCategory.getName());
            //报警状态
            SysDict alarmStatus = dictService.selectByValueAndType("alarmStatus", String.valueOf(record.getAlarmStatus()));
            record.setAlarmStatusName(alarmStatus.getDictLabel());
            //报警来源
            SysDict deviceSource = dictService.selectByValueAndType("deviceSource", String.valueOf(record.getDeviceSource()));
            record.setDeviceSourceName(deviceSource.getDictLabel());
            //处理状态
            SysDict handleStatus = dictService.selectByValueAndType("handleStatus", String.valueOf(record.getHandleStatus()));
            record.setHandleStatusName(handleStatus.getDictLabel());
        });
        return alarmRecordsPage;
    }

    public JSONObject getAlarmDetailsByDeviceId(Integer id) {
        AlarmRecords info = this.getById(id);
        if (null == info) {
            return null;
        }
        List<Mission> missions = missionService.getBaseMapper().selectList(Wrappers.<Mission>lambdaQuery()
//                .eq(Mission::getDeviceId, info.getDeviceId())
                        .eq(Mission::getSource, 1)
                        .eq(Mission::getSourceId, id)
                        .like(StringUtils.isNotBlank(info.getAlarmContentStr()), Mission::getNotes, info.getAlarmContentStr())
                        .eq(Mission::getIs_del, G.ISDEL_NO)
        );
        if (CollectionUtils.isEmpty(missions)) {
            return null;
        }
        Mission mission = missions.get(0);
        MissionItemRecordVo vo1 = new MissionItemRecordVo();
        vo1.setMissionId(mission.getId());
        return missionService.inspectionData(vo1);
    }

    public void notice(AlarmRecordsVo vo){

    }

    public List<SysBuildFloorVo> buildWarn() {
        List<SysBuildFloorVo> sysBuildFloors = sysBuildFloorMapper.selectFloorOrder();
        List<AlarmGroup> alarmGroups = alarmGroupMapper.selectList(Wrappers.lambdaQuery(AlarmGroup.class)
                .eq(AlarmGroup::getIsDel, 0)
        );
        for (SysBuildFloorVo sbf : sysBuildFloors) {
            sbf.setSelectGroupByFloorId(alarmGroupMapper.selectGroupByFloorId(sbf.getId()));
        }
        return sysBuildFloors;
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public Long getCount(AlarmRecords entity) {
        LambdaQueryWrapper<AlarmRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmRecords::getIsDel,0);
        return alarmRecordsMapper.selectCount(queryWrapper);
    }
}


