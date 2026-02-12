package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.enums.CommonEnum;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.operate.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.JsdcDateUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import com.jsdc.iotpt.vo.operate.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.influxdb.dto.QueryResult;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务表功能
 */
@Service
@Transactional
public class MissionService extends BaseService<Mission> {

    @Autowired
    private MissionAssignMapper missionAssignMapper;

    @Autowired
    private DeviceCollectService collectService;
    @Autowired
    private DeviceGatewayService gatewayService;
    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;
    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private SysBuildMapper sysBuildMapper;
    @Autowired
    private MissionMapper missionMapper;
    @Autowired
    private MissionCareMapper missionCareMapper;
    @Autowired
    private MissionAssignService assignService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private MissionItemRecordService itemRecordService;
    @Autowired
    private JobPlanService jobPlanService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private TeamGroupsMapper teamGroupsMapper;
    @Autowired
    private SysFileMapper sysFileMapper;
    @Autowired
    private SysFileService fileService;
    @Autowired
    private SysUserMapper userMapper;
//    @Autowired
//    private AppPushMsgService appPushMsgService;
    @Autowired
    private MissionConsumableMapper consumableMapper;
    @Autowired
    private TeamGroupUserService groupUserService;
    @Autowired
    private MissionItemRecordService recordService;
    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;

    @Autowired
    private SysBuildAreaService sysBuildAreaService;

    @Autowired
    private DeviceGatewayMapper deviceGatewayMapper;
    @Autowired
    private TeamGroupsService teamGroupsService;
    @Autowired
    private MissionAssignUserService assignUserService;
    @Autowired
    private MissionCareService missionCareService;
    @Autowired
    private SysOrgDeptService orgDeptService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    SqlSessionTemplate sqlSessionTemplate;
    @Value("${jsdc.uploadPath}")
    private String uploadPath;

    /**
     * PC端专用
     * 我的任务—根据不同的状态切换页面数据
     * 1、待指派
     * 2、待处理
     * 3、已处理
     * 4、开启
     * 5、撤销
     *
     * @param vo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<MissionVo> pageListMission(MissionVo vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        Page<MissionVo> missionPage = missionMapper.pageListMission(page, vo);
        List<MissionVo> records = missionPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            records.stream().sorted(Comparator.comparing(Mission::getReportingTime).reversed());
        }
        records.forEach(a -> {
            parameterMiss(a);
            if (null != a.getStates() && (a.getStates() == 2 || a.getStates() == 4 || a.getStates() == 3)) {//是待处理和开启状态的显示处理人
                List<MissionAssignUser> assignUser = assignUserService.list(new LambdaQueryWrapper<MissionAssignUser>().eq(MissionAssignUser::getMissionId, a.getId()));
                String assinUserString = "";
                if (!assignUser.isEmpty() && assignUser.size() > 0) {
                    for (MissionAssignUser b : assignUser) {
                        SysUser user = userService.getById(b.getUserId());
                        if (null != user && StringUtils.isNotEmpty(user.getRealName())) {
                            assinUserString += user.getRealName() + "、";
                        }
                    }
                }
                if (assinUserString.length() > 0) {
                    String str = assinUserString.substring(0, assinUserString.length() - 1);
                    a.setAssionUserName(str);
                }
            }
        });

        return missionPage;
    }

    public Page<MissionVo> pageListMission1(MissionVo vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        Page<MissionVo> missionPage = missionMapper.pageListMission1(page, vo);
        List<MissionVo> records = missionPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            records.stream().sorted(Comparator.comparing(Mission::getReportingTime).reversed());
        }
        records.forEach(a -> {
            parameterMiss(a);
            if (null != a.getStates() && (a.getStates() == 2 || a.getStates() == 4 || a.getStates() == 3)) {//是待处理和开启状态的显示处理人
                List<MissionAssignUser> assignUser = assignUserService.list(new LambdaQueryWrapper<MissionAssignUser>().eq(MissionAssignUser::getMissionId, a.getId()));
                String assinUserString = "";
                if (!assignUser.isEmpty() && assignUser.size() > 0) {
                    for (MissionAssignUser b : assignUser) {
                        SysUser user = userService.getById(b.getUserId());
                        if (null != user && StringUtils.isNotEmpty(user.getRealName())) {
                            assinUserString += user.getRealName() + "、";
                        }
                    }
                }
                if (assinUserString.length() > 0) {
                    String str = assinUserString.substring(0, assinUserString.length() - 1);
                    a.setAssionUserName(str);
                }
            }
        });

        return missionPage;
    }

    //报事人处理工单  同意、拒绝
    public ResultInfo handleMission(MissionVo bean) {
        try {
            Mission mission = getById(bean.getId());
            //处理状态 1：同意 2：拒绝
            if (1 == bean.getHandleStatus()) {
                mission.setHandleStatus(1);
                //报修人同意耗材消费 消息推送
                AppPushMsgVo msgVo = new AppPushMsgVo();
                msgVo.setAppConfigCode("SYSTEM_REPAIRS_NOTICE_C");
                msgVo.setName(userMapper.selectById(mission.getCreateUser()).getRealName());
                if (null != mission.getHandleId()) {
                    msgVo.setUserIdList(Collections.singletonList(mission.getHandleId()));
                }
//                appPushMsgService.pushMsg(msgVo);
            } else {
                mission.setStates(7);
                mission.setHandleStatus(2);
                //工单关闭 消息推送
                AppPushMsgVo msgVo = new AppPushMsgVo();
                msgVo.setAppConfigCode("SYSTEM_REPAIRS_NOTICE_B");
                msgVo.setName(userMapper.selectById(mission.getCreateUser()).getRealName());
                if (null != mission.getHandleId()) {
                    msgVo.setUserIdList(Collections.singletonList(mission.getHandleId()));
                }
//                appPushMsgService.pushMsg(msgVo);
            }
            mission.setUpdateUser(userService.getUser().getId());
            mission.setUpdateTime(new Date());
            updateById(mission);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("操作失败");
        }

    }


    /**
     * APP端我的关注
     *
     * @param vo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<MissionVo> pageCareMissionApp(MissionVo vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        Page<MissionVo> missionPage = missionMapper.pageCareMission(page, vo);
        missionPage.getRecords().forEach(a -> {
            a = parameterMiss(a);
            if (null != a.getSource() && 4 == a.getSource()) {
                a.setProportion(itemRecordService.percentageCount(a.getId()));
            }
        });
        return missionPage;
    }

    public void missionRejection(Mission bean) {
        missionMapper.update(null, Wrappers.<Mission>lambdaUpdate().eq(Mission::getIs_del, 0).eq(Mission::getId, bean.getId())
                .set(Mission::getReason, bean.getReason())
                .set(Mission::getUpdateTime, new Date())
                .set(Mission::getStates, 8)
                .set(Mission::getRejectUser, userService.getUser().getId())
                .set(Mission::getUpdateUser, userService.getUser().getId())
        );
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setAppConfigCode("GONGDN_BOHUI");
        msgVo.setUserIdList(Collections.singletonList(getById(bean.getId()).getCreateUser()));
//        appPushMsgService.pushMsg(msgVo);
    }

    /**
     * 待指派统计
     *
     * @return
     */
    public JSONObject assignedCount(AssignedCountDTO dto) {
        JSONObject object = new JSONObject();
        //待指派
        MissionVo vo1 = new MissionVo();
        vo1.setStates(1);
        vo1.setIsHandle(0);
        long dzpCount = missionMapper.dclCount(vo1);
        //待处理
        MissionVo vo2 = new MissionVo();
        vo2.setStates(2);
        vo2.setIsHandle(2);
        vo2.setUserId(dto.getUserId());
        long dclCount = missionMapper.dclCount(vo2);

        //已处理
        MissionVo vo3 = new MissionVo();
        vo3.setStates(3);
        vo3.setIsHandle(1);
        vo3.setUserId(dto.getUserId());
        vo3.setIsAdmin(dto.getIsAdmin());
        vo3.setHome_tab_type(dto.getHome_tab_type());
        long yclCount = missionMapper.dclCount(vo3);

        //已挂起
        MissionVo vo4 = new MissionVo();
        vo4.setStates(2);
        vo4.setIsHandle(2);
        vo4.setIsPending(1);
//        vo4.setUserId(dto.getUserId());
        long ygqCount = missionMapper.dclCount(vo4);


        long weekgqCount = missionMapper.selectCount(Wrappers.<Mission>lambdaQuery().
                eq(Mission::getIs_del, 0).
                eq(Mission::getStates, 2).
                eq(Mission::getIsPending, 1).
                apply(" pendingTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ").
                apply(" pendingTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

        long monthgqCount = missionMapper.selectCount(Wrappers.<Mission>lambdaQuery().
                eq(Mission::getIs_del, 0).
                eq(Mission::getStates, 2).
                eq(Mission::getIsPending, 1).
                apply(" pendingTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ").
                apply(" pendingTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));


        long lsgqCount = missionMapper.selectCount(Wrappers.<Mission>lambdaQuery().
                eq(Mission::getIs_del, 0).
                eq(Mission::getStates, 2).
                eq(Mission::getIsPending, 1));


        //历史已指派
        Long historyCount = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .in(Mission::getStates, 2, 3, 4)
        );
        //本周已指派
        Long weekCount = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .in(Mission::getStates, 2, 3, 4)
                .between(Mission::getReportingTime, DateUtils.strToDate(DateUtils.getStartTimeOfCurrentWeek(new Date())), DateUtils.strToDate(DateUtils.getEndTimeOfCurrentWeek(new Date())))
        );
        //本月已指派
        Long monthCount = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .ne(Mission::getSource, 4)
                .in(Mission::getStates, 2, 3, 4)
                .between(Mission::getReportingTime, DateUtils.strToDate(DateUtils.getStartTimeOfCurrentMonth(new Date())), DateUtils.strToDate(DateUtils.getEndTimeOfCurrentMonth(new Date())))
        );
        //我的关注
        if (null != dto.getPublicity()) {
//            long count = missionCareService.count(new LambdaQueryWrapper<MissionCare>().eq(MissionCare::getCreateUser, dto.getUserId()).eq(MissionCare::getCareType, 2));
            Long count = missionCareMapper.countCare(dto.getUserId());
            object.put("careCount", count);
        }
        object.put("dzpCount", dzpCount);
        object.put("dclCount", dclCount);
        object.put("yclCount", yclCount);
        object.put("ygqCount", ygqCount);
        object.put("weekCount", weekCount);
        object.put("monthCount", monthCount);
        object.put("historyCount", historyCount);
        object.put("weekgqCount", weekgqCount);
        object.put("monthgqCount", monthgqCount);
        object.put("lsgqCount", lsgqCount);
        return object;
    }

    /**
     * 根据不同的状态统计数据
     * 1、待指派
     * 2、待处理
     * 3、已处理
     *
     * @return
     */
    public JSONObject statusCount(Integer userId, Integer isAdmin, Integer publicity, Integer home_tab_type) {
        JSONObject object = new JSONObject();

        //待指派
        MissionVo vo1 = new MissionVo();
        vo1.setStates(1);
        vo1.setIsHandle(0);
        long count1 = missionMapper.dclCount(vo1);


        //待处理
        MissionVo vo2 = new MissionVo();
        vo2.setStates(2);
        vo2.setIsHandle(2);
        vo2.setUserId(userId);
        long count2 = missionMapper.dclCount(vo2);

//        MissionVo vo3 = new MissionVo();
//        vo3.setStates(3);
//        vo3.setIsHandle(1);
//        vo3.setUserId(userId);
//        vo3.setIsAdmin(isAdmin);
//        vo3.setHome_tab_type(home_tab_type);
//        long count3 = missionMapper.dclCount(vo3);

        //已处理
        long count3 = missionMapper.countHandle().size();

        //我上报
//        MissionVo vo4 = new MissionVo();
//        vo4.setStates(0);
//        vo4.setIsHandle(1);
//        vo4.setUserId(userId);
//        long count4 = missionMapper.dclCount(vo4);
        long count4 = missionMapper.countAllTask().size();


        //我的挂起
        MissionVo vo5 = new MissionVo();
        vo5.setStates(2);
        vo5.setIsHandle(2);
        vo5.setUserId(userId);
        vo5.setIsPending(1);
        long count5 = missionMapper.dclCount(vo5);

        //我的关注
        if (null != publicity) {
            long count = missionCareService.count(new LambdaQueryWrapper<MissionCare>().eq(MissionCare::getCreateUser, userId).eq(MissionCare::getCareType, 1));
            object.put("wdgzCount", count);
        }
        //本周已处理
        Long weekCount = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .in(Mission::getStates, 3)
                .eq(null != userId, Mission::getHandleId, userId)
                .between(Mission::getHandleDate, DateUtils.strToDate(DateUtils.getStartTimeOfCurrentWeek(new Date())), DateUtils.strToDate(DateUtils.getEndTimeOfCurrentWeek(new Date())))
        );
        //本月已处理
        Long monthCount = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .in(Mission::getStates, 3)
                .eq(null != userId, Mission::getHandleId, userId)
                .between(Mission::getHandleDate, DateUtils.strToDate(DateUtils.getStartTimeOfCurrentMonth(new Date())), DateUtils.strToDate(DateUtils.getEndTimeOfCurrentMonth(new Date())))
        );
        //历史已处理
        Long historyCount = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .in(Mission::getStates, 3)
                .eq(null != userId, Mission::getHandleId, userId)
        );


        object.put("dzpCount", count1);
        object.put("dclCount", count2);
        object.put("yclCount", count3);
        object.put("wsbCount", count4);
        object.put("weekCount", weekCount);
        object.put("monthCount", monthCount);
        object.put("historyCount", historyCount);
        object.put("wdgq", count5);
        return object;
    }


    /**
     * 1、新增
     * 2、编辑
     * 根据当前传入的id，实现新增或修改
     *
     * @param mission
     */
    public boolean saveMission(Mission mission) {
        SysUser user = new SysUser();
        if (null == mission.getUserId()) {
            user = userService.getUser();
        } else {
            user.setId(mission.getUserId());
        }
        if (null != mission.getId()) {
            mission.setReportingTime(new Date());
            mission.setUpdateTime(new Date());
            mission.setUserId(user.getId());
            mission.setIsReads(0);
            updateById(mission);
        } else {
            mission.setUserId(user.getId());
            mission.setReportingTime(new Date());
            mission.setCreateTime(new Date());
            mission.setCreateUser(user.getId());
            mission.setSubstance(mission.getNotes());
            mission.setNotes(mission.getNotes());
            //订单号规则
            mission.setOrderID("GD" + mission.getSourceId() + System.currentTimeMillis());
            mission.setIs_del(0);
            mission.setIsReads(0);
            mission.setPublicity(0);
            save(mission);
        }
        //向图片表中插入数据
        if (!CollectionUtils.isEmpty(mission.getFileList())) {
            List<String> ls = mission.getFileList().stream().filter(x -> StringUtils.isNotEmpty(x.getBizId())).map(SysFile::getBizId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ls)) {
                fileService.remove(new LambdaQueryWrapper<SysFile>().in(SysFile::getBizId, ls).eq(SysFile::getBizType, 4));
            }
            for (SysFile file : mission.getFileList()) {
                try {
                    file.setBizId(mission.getId().toString());
                    file.setBizType("4");
                    file.setIsDel(0);
                    file.insert();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //通知审核上报工单(指派到维修组)
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setAppConfigCode("TEAMWORK_REPAIRS_ASSIGN");
        List<SysUser> sysUsers = userMapper.getUsersByRole("app_rw_zp");
        msgVo.setUserIdList(sysUsers.stream().map(SysUser::getId).collect(Collectors.toList()));
//        appPushMsgService.pushMsg(msgVo);
        return true;
    }


    /**
     * 详情
     *
     * @param bean
     * @return
     */
    public JSONObject detailsMission(Mission bean) {
        JSONObject object = new JSONObject();
        Mission mission = getById(bean);
        if (StringUtils.isNotNull(mission.getAreaId())) {
            SysBuildArea sysBuildArea = sysBuildAreaService.getById(mission.getAreaId());
            if (StringUtils.isNotNull(sysBuildArea)) {
                mission.setAreaName(sysBuildArea.getAreaName());
            }
        }

        if ("1".equals(mission.getDeviceType() + "")) {
            if (null != mission.getDeviceId()) {
                DeviceCollect collect = deviceCollectMapper.selectById(mission.getDeviceId());
                if (null != collect) {
                    SysBuildArea area = sysBuildAreaService.getById(collect.getAreaId());
                    collect.setAreaName(area.getAreaName());
                    collect.setAddress(area.getDetailAddress());
                    object.put("device", collect);
                }
            }
            mission.setPId("dc_" + mission.getDeviceId());
        } else if ("2".equals(mission.getDeviceType() + "")) {
            if (null != mission.getDeviceId()) {

            }
            mission.setPId("dv_" + mission.getDeviceId());
        } else {
            if (null != mission.getDeviceId()) {
                DeviceGateway deviceGateway = deviceGatewayMapper.selectById(mission.getDeviceId());
                SysBuildArea area = sysBuildAreaService.getById(deviceGateway.getAreaId());
                deviceGateway.setAreaName(area.getAreaName());
                deviceGateway.setAddress(area.getDetailAddress());
                object.put("device", deviceGateway);
            }
            mission.setPId("dg_" + mission.getDeviceId());
        }
        List<MissionAssign> missionAssigns = missionAssignMapper.selectList(new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, mission.getId()));
        if (CollUtil.isNotEmpty(missionAssigns)) {
            MissionAssign assign = missionAssigns.get(0);
            //任务班组
            List<TeamGroups> teamGroups = teamGroupsMapper.selectList(new LambdaQueryWrapper<TeamGroups>().eq(TeamGroups::getIsDel, 0)
                    .eq(TeamGroups::getId, assign.getTeamGroupsId()));
            if (CollUtil.isNotEmpty(teamGroups)) {
                assign.setTeamGroupsName(teamGroups.get(0).getName());
            }
            //紧急程度
            List<SysDict> urgencyList = sysDictService.getBaseMapper().selectList(Wrappers.<SysDict>lambdaQuery()
                    .eq(SysDict::getDictType, "missionUrgency").eq(SysDict::getDictValue, String.valueOf(assign.getUrgency())));
            if (CollUtil.isNotEmpty(urgencyList)) {
                assign.setUrgencyLabel(urgencyList.get(0).getDictLabel());
            }
//            //任务类型
            List<SysDict> taskTypes = sysDictService.getBaseMapper().selectList(new LambdaQueryWrapper<SysDict>()
                    .eq(SysDict::getIsDel, 0)
                    .eq(SysDict::getDictType, "taskTypes")
                    .eq(SysDict::getDictValue, assign.getTaskType())
            );
            if (CollUtil.isNotEmpty(taskTypes)) {
                assign.setTaskTypeName(taskTypes.get(0).getDictLabel());
            }

            if (null != mission.getRejectUser()) {
                SysUser user = userService.getById(mission.getRejectUser());
                if (ObjUtil.isNotEmpty(user)) {
                    mission.setRejectUsername(user.getRealName());
                }
            }
            mission.setMissionAssign(assign);
            mission.setConsumables(consumableMapper.selectList(new LambdaQueryWrapper<MissionConsumable>()
                    .eq(MissionConsumable::getIsDel, 0)
                    .eq(MissionConsumable::getTemporary, 0)
                    .eq(MissionConsumable::getMissionId, mission.getId())
            ));
        }
        object.put("mission", mission);
        List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getBizId, String.valueOf(bean.getId()))
                .eq(SysFile::getIsDel, 0)
                .eq(SysFile::getBizType, "4"));
        object.put("files", files);
        return object;
    }


    /**
     * 处理信息（根据1、2、3进行简单处理异常）
     * 1、告警上报；（需要给告警表的状态更改为已处理）
     * 2、人员上报；
     * 3、巡检异常上报；（需要给巡检异常上报的状态更改为已处理）
     * 4、巡检计划生成（需要生成巡检异常信息）
     *
     * @param mission
     * @return
     */
    public boolean waitHandle(MissionVo mission) {
        Mission bean = getById(mission.getId());
        SysUser user = userService.getUser();
        if (StringUtils.isNotEmpty(mission.getType())) {
            if ("1".equals(mission.getType())) {//巡检计划生成（需要生成巡检异常信息）
                List<MissionItemRecord> recordList = mission.getRecordList();
                if (!recordList.isEmpty() && recordList.size() > 0) {
                    for (MissionItemRecord a : recordList) {//向任务项记录表中插入数据
                        a.setCreateTime(new Date());
                        a.setCreateUser(user.getId());
                        a.setIsHandle(1);//已处理
                        if (itemRecordService.updateById(a)) {
                            //向巡检异常表进行插入异常的数据
                            if (2 == a.getExecution()) {
                                Mission m = new Mission();
                                m.setSource(3);
                                m.setUserId(user.getId());
                                m.setSourceId(a.getId());
                                m.setDeviceId(a.getDeviceId());
                                m.setDeviceType(a.getDeviceType());
                                mission.setIsReads(0);
                                m.setLevels(a.getLevels());
                                m.setNotes(a.getDescription());
                                m.setReportingTime(new Date());
                                List<MissionAssign> list = assignService.list(new LambdaQueryWrapper<MissionAssign>()
                                        .eq(MissionAssign::getMissionId, bean.getId()).eq(MissionAssign::getState, 1));
                                if (!list.isEmpty() && list.size() > 0) {
                                    m.setTitle(list.get(0).getJobPlanName());
                                }
                                m.setStates(1);
                                m.setIs_del(0);
                                m.setCreateUser(bean.getCreateUser());
                                m.setCreateTime(new Date());
                                m.setPublicity(0);
                                save(m);
                                //向图片表中插入数据
                                if (!CollectionUtils.isEmpty(a.getFiles())) {
                                    for (SysFile file : a.getFiles()) {
                                        try {
                                            file.setBizId(m.getId().toString());
                                            file.setBizType("4");
                                            file.insert();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                            //删除原来文件
//                            fileService.getBaseMapper().update(null, Wrappers.<SysFile>lambdaUpdate().
//                                    set(SysFile::getIsDel, 1).
//                                    eq(SysFile::getBizType, "5").
//                                    eq(SysFile::getBizId, bean.getId()));
                            List<SysFile> sysFiles = sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>()
                                    .eq(SysFile::getBizType, "5")
                                    .eq(SysFile::getIsDel, 0)
                                    .eq(SysFile::getBizId, bean.getId()));
                            if (CollUtil.isNotEmpty(sysFiles)) {
                                for (SysFile sysFile : sysFiles) {
                                    sysFile.setIsDel(1);
                                    sysFile.setUpdateUser(userService.getUser().getId());
                                    sysFile.setUpdateTime(new Date());
                                    sysFileMapper.updateById(sysFile);
                                }
                            }
                            //向图片表中插入数据
                            if (!CollectionUtils.isEmpty(a.getFiles())) {
                                for (SysFile file : a.getFiles()) {
                                    try {
                                        file.setBizId(a.getId().toString());
                                        file.setBizType("5");
                                        file.setIsDel(0);
                                        file.insert();
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                    bean.setHandleId(user.getId());
                    bean.setStates(3);//已处理
                    bean.setUpdateTime(new Date());
                    bean.setUpdateUser(user.getId());
                    //协助处理人
                    bean.setAssistHandleIds(mission.getAssistHandleIds());
                    updateById(bean);
                    return true;
                }
            } else {//其他异常处理
                bean.setCrunch(mission.getCrunch());
                bean.setHandleId(mission.getHandleId());
                bean.setHandleDate(mission.getHandleDate());
                bean.setStates(3);
                bean.setIsPending(0);
                //协助处理人
                bean.setAssistHandleIds(mission.getAssistHandleIds());
                if (updateById(bean)) {
                    if (null != bean.getStates()) {
                        if (1 == bean.getSource()) {

                        }
                    }
                }
                List<SysFile> sysFiles = sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>()
                        .eq(SysFile::getBizType, "5")
                        .eq(SysFile::getIsDel, 0)
                        .eq(SysFile::getBizId, bean.getId()));
                if (CollUtil.isNotEmpty(sysFiles)) {
                    for (SysFile sysFile : sysFiles) {
                        sysFile.setIsDel(1);
                        sysFile.setUpdateUser(userService.getUser().getId());
                        sysFile.setUpdateTime(new Date());
                        sysFileMapper.updateById(sysFile);
                    }
                }
                if (!CollectionUtils.isEmpty(mission.getFileList())) {
                    for (SysFile file : mission.getFileList()) {
                        try {
                            file.setBizId(bean.getId().toString());
                            file.setBizType("5");
                            file.setIsDel(0);
                            file.insert();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                if (5 == bean.getSource()) {
                    AppPushMsgVo msgVo = new AppPushMsgVo();
                    msgVo.setAppConfigCode("SYSTEM_REPAIRS_RESULT");
                    String[] prepares = {String.valueOf(bean.getUserId())};
                    msgVo.setUserIdList(Arrays.stream(prepares).map(Integer::parseInt).collect(Collectors.toList()));
//                    appPushMsgService.pushMsg(msgVo);
                }
                return true;
            }
        }
        return false;
    }

    public ResultInfo consumableTemporary(Integer id) {
        return ResultInfo.success(consumableMapper.selectList(new LambdaQueryWrapper<MissionConsumable>()
                .eq(MissionConsumable::getIsDel, 0)
                .eq(MissionConsumable::getTemporary, 1)
                .eq(MissionConsumable::getMissionId, id)
        ));
    }

    public ResultInfo addConsumables(MissionVo bean) {
        try {
            Mission mission = getById(bean.getId());
            //8：报修人确认中
            mission.setStates(2);
            mission.setUpdateUser(userService.getUser().getId());
            mission.setUpdateTime(new Date());
            mission.setHandleId(userService.getUser().getId());
            mission.setHandleStatus(bean.getHandleStatus());
            updateById(mission);
            List<MissionConsumable> consumables = bean.getConsumables();
            //耗材信息
            if (CollUtil.isNotEmpty(consumables)) {
                consumableMapper.update(null, Wrappers.<MissionConsumable>lambdaUpdate()
                        .eq(MissionConsumable::getIsDel, 0)
                        .eq(MissionConsumable::getMissionId, bean.getId())
                        .set(MissionConsumable::getIsDel, 1)
                        .set(MissionConsumable::getUpdateTime, new Date())
                        .set(MissionConsumable::getUpdateUser, userService.getUser().getId())
                );
                for (MissionConsumable consumable : consumables) {
                    consumable.setIsDel(0);
                    consumable.setMissionId(mission.getId());
                    consumable.setCreateUser(userService.getUser().getId());
                    consumable.setCreateTime(new Date());
                    consumableMapper.insert(consumable);
                }
                //只有提交才发送消息 发送消息推送给报修人
                if (consumables.get(0).getTemporary() == 0) {
                    AppPushMsgVo msgVo = new AppPushMsgVo();
                    msgVo.setAppConfigCode("SYSTEM_REPAIRS_NOTICE_A");
                    msgVo.setTime(DateUtils.dateToStr(mission.getCreateTime()));
                    msgVo.setUserIdList(Collections.singletonList(mission.getCreateUser()));
//                    appPushMsgService.pushMsg(msgVo);
                }

            }
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("操作失败");
        }
    }


    /**
     * 处理信息（根据1、2、3进行简单处理异常）
     * 1、告警上报；（需要给告警表的状态更改为已处理）
     * 2、人员上报；
     * 3、巡检异常上报；（需要给巡检异常上报的状态更改为已处理）
     * 4、巡检计划生成（需要生成巡检异常信息）
     *
     * @param mission
     * @return
     */
    public ResultInfo storeHandle(MissionVo mission) {
        Mission bean = getById(mission.getId());
        bean.setCrunch(mission.getCrunch());
        if (StrUtil.isNotEmpty(mission.getStartTime())) {
            bean.setHandleDate(DateUtils.strToDate(mission.getStartTime()));
        }
        List<SysFile> sysFiles = sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getBizType, "5")
                .eq(SysFile::getIsDel, 0)
                .eq(SysFile::getBizId, bean.getId()));
        if (CollUtil.isNotEmpty(sysFiles)) {
            for (SysFile sysFile : sysFiles) {
                sysFile.setIsDel(1);
                sysFile.setUpdateUser(userService.getUser().getId());
                sysFile.setUpdateTime(new Date());
                sysFileMapper.updateById(sysFile);
            }
        }
//        fileService.getBaseMapper().update(null, Wrappers.<SysFile>lambdaUpdate().
//                set(SysFile::getIsDel, 1).
//                eq(SysFile::getBizType, "5").
//                eq(SysFile::getBizId, bean.getId()));
        if (!CollectionUtils.isEmpty(mission.getFileList())) {
            for (SysFile file : mission.getFileList()) {
                try {
                    file.setBizId(bean.getId().toString());
                    file.setBizType("5");
                    file.setIsDel(0);
                    file.insert();
                } catch (Exception e) {
                    return ResultInfo.error("暂存失败！");
                }
            }
        }
        bean.setAssistHandleIds(mission.getAssistHandleIds());
        updateById(bean);
        return ResultInfo.success("暂存成功！");
    }

    /**
     * 根据id获取班组下的成员
     *
     * @param groupId
     * @return
     */
    public List<TeamGroupUser> selectByGroupUser(Integer groupId) {
        List<TeamGroupUser> list = groupUserService.list(new LambdaQueryWrapper<TeamGroupUser>().eq(TeamGroupUser::getGroupId, groupId)
                .eq(TeamGroupUser::getIsDel, 0));
        list.forEach(a -> {
            SysUser user = userService.getById(a.getUserId());
            a.setRealName(user.getRealName());
        });
        return list;
    }

    /**
     * 处理信息详情
     * 根据指派的班组查询巡检计划项
     *
     * @param bean
     * @return
     */
//    public JSONObject inspectionData(MissionItemRecordVo bean) {
//        JSONObject object = new JSONObject();
//        Mission mission = getById(bean.getMissionId());//上报信息
//        MissionVo vo = new MissionVo();
//        BeanUtils.copyProperties(mission, vo);
//        vo.setSourceName(retunStatusName(3, vo.getSource()));
//        SysDict dict = returnDicst("warnLevel", vo.getLevels());
//        if (StringUtils.isNotNull(dict)) {
//            vo.setLevelsName(dict.getDictLabel());
//        }
//        if (null != bean.getCreateUser()) {
//            long count = careService.count(new LambdaQueryWrapper<MissionCare>().eq(MissionCare::getMissionId, mission.getId()).eq(MissionCare::getCreateUser, bean.getCreateUser()));
//            if (count > 0) {
//                vo.setPublicity(1);
//            } else {
//                vo.setPublicity(0);
//            }
//        }
//        if (null != vo.getUserId()) {
//            SysUser sysUser = userService.getById(vo.getUserId());
//            if (null != sysUser) {
//                vo.setUserName(sysUser.getRealName());
//            }
//        }
//        if (null != vo.getHandleId()) {
//            SysUser sysUser = userService.getById(vo.getHandleId());
//            if (null != sysUser) {
//                vo.setHandleName(sysUser.getRealName());
//            }
//        }
//        if (null != vo.getDeviceId() && null != vo.getDeviceType()) {
//            vo.setDeviceName(recordService.selectByDeviceName(vo.getDeviceId(), vo.getDeviceType()));
//        }
//        if (null != vo.getAreaId()) {
//            SysBuildArea area = sysBuildAreaService.getById(vo.getAreaId());
//            if (StringUtils.isNotNull(area)) {
//                vo.setAreaName(area.getAreaName());
//                vo.setAddress(area.getDetailAddress());
//            }
//        }
//        List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId()).eq(SysFile::getBizType, "4"));
//        vo.setFileList(files);
//        List<SysFile> handles = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId()).eq(SysFile::getBizType, "5"));
//        object.put("bean", vo);
//        object.put("handles", handles);
//        if (null != mission.getSource() && 4 != mission.getSource()) {
//            //指派信息，目前只会存在一个启用状态的指派的班组数据
//            List<MissionAssignVo> vos = assignService.selectAssignsList(bean.getMissionId(), 1);
//            if (!vos.isEmpty() && vos.size() > 0) {
//                List<TeamGroupUser> groupUsers = selectByGroupUser(vos.get(0).getTeamGroupsId());
//                object.put("groupUsers", groupUsers);
//            }
//            List<MissionAssignUser> assignUser = assignUserService.list(new LambdaQueryWrapper<MissionAssignUser>().eq(MissionAssignUser::getMissionId, mission.getId()));
//            String assinUserString = "";
//            if (!assignUser.isEmpty() && assignUser.size() > 0) {
//                for (MissionAssignUser a : assignUser) {
//                    SysUser user = userService.getById(a.getUserId());
//                    assinUserString += user.getRealName() + "、";
//                }
//            }
//            object.put("assignUser", assinUserString);
//            object.put("assigns", vos);
//        }
//        if (null != mission.getSource() && 4 == mission.getSource()) { //巡检计划内容
//            JobPlan plan = jobPlanService.getById(mission.getSourceId());//用来获取任务名称、周期、任务类型字段
//            object.put("plan", plan);
//            mission.setIsHandle(bean.getIsHandle());
//            object.put("subs", recordService.missionItemRecordList(mission));
//        }
//        return object;
//    }

    /**
     * 运维管理统计报表列表
     * Author wzn
     * Date 2023/8/24 10:41
     */
    public List<MissionTjVo> getListByGroup(MissionTjVo missionVo) {
        //查询条件
        //0完成时间 1上报时间
        if ("1".equals(missionVo.getQuerryTimeType())) {
            missionVo.setQuerryTimeString("ms.createTime");
        } else if ("0".equals(missionVo.getQuerryTimeType())) {
            missionVo.setQuerryTimeString("ms.handleDate");
        }

        if ("2".equals(missionVo.getTimeType())) {
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(missionVo.getTimeStr(), "yyyy-MM"));
            missionVo.setTimeStart(DateUtil.format(DateUtil.beginOfMonth(timeStart), "yyyy-MM-dd"));
            missionVo.setTimeEnd(DateUtil.format(DateUtil.endOfMonth(timeStart), "yyyy-MM-dd"));
        } else if ("3".equals(missionVo.getTimeType())) {
            // 指定日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            try {
                Date date = sdf.parse(missionVo.getTimeStr());
                missionVo.setTimeStart(getYearFirstDay(date));
                missionVo.setTimeEnd(getYearLastDay(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        missionVo.setFormate("yyyy-MM-dd");

        List<MissionTjVo> missionVoList = missionMapper.getListByGroup(missionVo);
        //任务状态统计1、处理中 2、已完成
        String taskStatus = "";
        Integer total = 0;
        for (int i = 0; i < missionVoList.size(); i++) {
            MissionTjVo m = missionVoList.get(i);
            m.setId(i + 1 + "");
            if ("1".equals(missionVo.getQuerryTimeType())) {
                m.setQuerryTimeString("ms.createTime");
            } else if ("0".equals(missionVo.getQuerryTimeType())) {
                m.setQuerryTimeString("ms.handleDate");
            }
            m.setFormate("yyyy-MM-dd");
            m.setTimeStart(missionVo.getTimeStart());
            m.setTimeEnd(missionVo.getTimeEnd());
            total += Integer.valueOf(m.getCount());
            m.setState("1");
            m.setInfoC0(missionMapper.stateCount(m).getCount());
            m.setState("2");
            m.setInfoC1(missionMapper.stateCount(m).getCount());
            m.setState("3");
            m.setInfoC2(missionMapper.stateCount(m).getCount());

            m.setTotal(total + "");
            //执行班组翻译
            TeamGroups teamGroups = teamGroupsMapper.selectById(m.getTeamGroupsId());
            if (null != teamGroups) {
                m.setTeamGroupsName(teamGroups.getName());
            }
            //任务类型
//            //1、维修；2、抢修
//            if ("1".equals(m.getTaskType())) {
//                m.setTaskTypeName("维修");
//            } else if ("2".equals(m.getTaskType())) {
//                m.setTaskTypeName("抢修");
//            }


            //任务类型
            List<SysDict> taskType = sysDictService.getBaseMapper().selectList(Wrappers.<SysDict>lambdaQuery()
                    .eq(SysDict::getDictType, "taskTypes"));
            Map<String, String> map = new HashMap<>();
            if (CollectionUtil.isNotEmpty(taskType)) {
                for (SysDict s : taskType) {
                    map.put(s.getDictValue(), s.getDictLabel());
                }
            }
            m.setTaskTypeName(map.get(m.getTaskType()));

        }
        return missionVoList;
    }

    /**
     * 转换返回值
     * 公共方法
     *
     * @param states
     * @return
     */
    public String retunStatusName(Integer type, Integer states) {
        String msg = "";
        if (null != type && null != states) {
            if (1 == type) {//任务状态
                if (1 == states) {
                    msg = "待指派";
                } else if (2 == states) {
                    msg = "待处理";
                } else if (3 == states) {
                    msg = "已处理";
                } else if (4 == states) {
                    msg = "开启";
                } else {
                    msg = "暂存";
                }
            } else if (2 == type) {//处理状态
                if (1 == states) {
                    msg = "处理中";
                } else if (2 == states) {
                    msg = "已完成";
                }
            } else if (3 == type) {//来源状态
                if (1 == states) {
                    //告警上报
                    msg = "系统工单";
                } else if (2 == states) {
                    msg = "人员上报";
                } else if (3 == states) {
                    //巡检异常上报
                    msg = "巡检工单";
                } else if (4 == states) {
                    msg = "巡检计划生成";
                } else if (5 == states) {
                    msg = "服务工单";
                } else if (6 == states) {
                    msg = "投诉工单";
                }
            } else {

            }
        }
        return msg;
    }

    /**
     * 返回字典值
     *
     * @param dictType
     * @param dictValue
     * @return
     */
    public SysDict returnDicst(String dictType, Integer dictValue) {
        SysDict dict = new SysDict();
        List<SysDict> dicts = sysDictService.list(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, dictType).eq(SysDict::getIsDel, 0)
                .eq(SysDict::getDictValue, dictValue.toString()));
        if (!dicts.isEmpty() && dicts.size() > 0) {
            return dicts.get(0);
        }
        return dict;
    }

    /**
     * 电气安全分析 派单各个状态数量统计
     *
     * @return
     */
    public ElectricalSafetyReportVo stateCountReport(ElectricalSafetyReportVo vo) {
        return missionMapper.stateCountReport(vo);
    }

    /**
     * 电气安全分析 工单处理情况排名 列表
     *
     * @return
     */
    public List<ElectricalSafetyReportVo> getListTop(ElectricalSafetyReportVo vo) {
        return missionMapper.getListTop(vo);
    }



    /**
     * 电气安全报表
     * 导出
     */
//    public void electricalSafetyExport(ElectricalSafetyReportVo vo, HttpServletResponse response) {
//        // 查询数据
//        if (StringUtils.isNotEmpty(vo.getStartTime())) {
//            vo.setStartTime(new DateTime(vo.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"));
//        }
//        if (StringUtils.isNotEmpty(vo.getEndTime())) {
//            vo.setEndTime(new DateTime(vo.getEndTime()).plusDays(1).toString("yyyy-MM-dd HH:mm:ss"));
//        }
//        List<ElectricalSafetyReportVo> deviceCollects = missionMapper.getListByDeviceIdReport2(null, vo);
//        ExcelWriter writer = ExcelUtil.getWriter();
//
//        writer.addHeaderAlias("deviceId", "设备编号");
//        writer.addHeaderAlias("location", "所属商户");
//        writer.addHeaderAlias("place", "安装地址");
//        writer.addHeaderAlias("count", "告警合计(次)");
//        writer.setOnlyAlias(true);
//        writer.write(deviceCollects, true);
//        OutputStream outputStream = null;
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/vnd.ms-excel");
//        try {
//            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("123.xls", "UTF-8"));
//            outputStream = response.getOutputStream();
//            writer.flush(outputStream, true);
////            outputStream.flush();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 统计报表分页查询
     * Author wzn
     * Date 2023/8/24 14:19
     */
    public List<MissionTjVo> getList(MissionTjVo vo) {
        //查询条件
        //0完成时间 1上报时间
        if ("1".equals(vo.getQuerryTimeType())) {
            vo.setQuerryTimeString("ms.reportingTime");
        } else if ("0".equals(vo.getQuerryTimeType())) {
            vo.setQuerryTimeString("ms.handleDate");
        }
        List<MissionTjVo> missionTjVoList = new ArrayList<>();
        List<String> data = new ArrayList<>();

        if ("2".equals(vo.getTimeType())) {//按月
            //横坐标
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(vo.getTimeStr(), "yyyy-MM"));
            vo.setTimeStart(DateUtil.format(DateUtil.beginOfMonth(timeStart), "yyyy-MM-dd"));
            vo.setTimeEnd(DateUtil.format(DateUtil.endOfMonth(timeStart), "yyyy-MM-dd"));
            data.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(timeStart)));
            vo.setFormate("YYYY-MM-DD");
            MissionTjVo missionTjVo = null;
            for (String s : data) {
                vo.setTimeStart(s);
                missionTjVo = missionMapper.rwqs(vo);
                if (null == missionTjVo) {
                    {
                        missionTjVo = new MissionTjVo();
                        missionTjVo.setDay(s);
                        missionTjVo.setCount("0");
                        missionTjVo.setInfoC0("0");
                        missionTjVo.setInfoC1("0");
                        missionTjVo.setInfoC2("0");
                        missionTjVo.setFinishingRate("0");
                    }

                }
                missionTjVoList.add(missionTjVo);
            }

        } else if ("3".equals(vo.getTimeType())) {
            //横坐标

            for (int i = 1; i <= 12; i++) {
                if (i < 10) {
                    data.add(vo.getTimeStr() + "-0" + i);
                } else {
                    data.add(vo.getTimeStr() + "-" + i);
                }
            }

            vo.setFormate("YYYY-MM");
            MissionTjVo missionTjVo = null;
            for (String s : data) {
                vo.setTimeStart(s);
                missionTjVo = missionMapper.rwqs(vo);
                if (null == missionTjVo) {
                    {
                        missionTjVo = new MissionTjVo();
                        missionTjVo.setDay(s);
                        missionTjVo.setCount("0");
                        missionTjVo.setInfoC0("0");
                        missionTjVo.setInfoC1("0");
                        missionTjVo.setInfoC2("0");
                        missionTjVo.setFinishingRate("0");
                    }

                }
                missionTjVoList.add(missionTjVo);
            }

        }


        return missionTjVoList;

    }


    public JSONObject lineChart(MissionTjVo vo) {
        JSONObject object = new JSONObject();
        List<String> data = new ArrayList<>();
        List<Map<Object, Object>> series = new ArrayList<>();
        Map<Object, Object> map = null;
        Map<Object, Object> map2 = null;
        Map<Object, Object> map3 = null;
        Map<Object, Object> map4 = null;
        Map<Object, Object> map5 = null;
        //0完成时间 1上报时间
        if ("1".equals(vo.getQuerryTimeType())) {
            vo.setQuerryTimeString("ms.reportingTime");
        } else if ("0".equals(vo.getQuerryTimeType())) {
            vo.setQuerryTimeString("ms.handleDate");
        }
        if ("2".equals(vo.getTimeType())) {//按月
            //横坐标
            map = new HashMap<>();
            map2 = new HashMap<>();
            map3 = new HashMap<>();
            map4 = new HashMap<>();
            map5 = new HashMap<>();
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(vo.getTimeStr(), "yyyy-MM"));
            vo.setTimeStart(DateUtil.format(DateUtil.beginOfMonth(timeStart), "yyyy-MM-dd"));
            vo.setTimeEnd(DateUtil.format(DateUtil.endOfMonth(timeStart), "yyyy-MM-dd"));
            data.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(timeStart)));
            map.put("type", "category");
            map.put("data", data);

            // 待指派数据查询   1、待指派；2、待处理；3、已处理  0、暂存
            vo.setState("1");
            vo.setFormate("YYYY-MM-DD");
            List<MissionTjVo> missionTjVoList = missionMapper.getCount(vo);
            Map<String, String> dzp = new HashMap<>();
            for (MissionTjVo m : missionTjVoList) {
                dzp.put(m.getDay(), m.getCount());
            }
            map2.put("name", "待指派");
            map2.put("type", "bar");
            List<String> dataList = new ArrayList<>();
            for (String s : data) {
                if (null == dzp.get(s)) {
                    dataList.add("0");
                } else {
                    dataList.add(dzp.get(s));
                }
            }
            map2.put("data", dataList);
            series.add(map2);
            //待处理
            vo.setState("2");
            vo.setFormate("YYYY-MM-DD");
            List<MissionTjVo> missionTjVoList2 = missionMapper.getCount(vo);
            Map<String, String> dzp2 = new HashMap<>();
            for (MissionTjVo m : missionTjVoList2) {
                dzp2.put(m.getDay(), m.getCount());
            }
            map3.put("name", "待处理");
            map3.put("type", "bar");
            List<String> dataList2 = new ArrayList<>();
            for (String s : data) {
                if (null == dzp2.get(s)) {
                    dataList2.add("0");
                } else {
                    dataList2.add(dzp2.get(s));
                }
            }
            map3.put("data", dataList2);
            series.add(map3);
            //已处理
            vo.setState("3");
            vo.setFormate("YYYY-MM-DD");
            List<MissionTjVo> missionTjVoList3 = missionMapper.getCount(vo);
            Map<String, String> dzp3 = new HashMap<>();
            for (MissionTjVo m : missionTjVoList3) {
                dzp3.put(m.getDay(), m.getCount());
            }
            map4.put("name", "已处理");
            map4.put("type", "bar");
            List<String> dataList3 = new ArrayList<>();
            for (String s : data) {
                if (null == dzp3.get(s)) {
                    dataList3.add("0");
                } else {
                    dataList3.add(dzp3.get(s));
                }
            }
            map4.put("data", dataList3);
            series.add(map4);
            //完成率
            vo.setFormate("YYYY-MM-DD");
            List<MissionTjVo> missionTjVoList4 = missionMapper.finishingRate(vo);
            Map<String, String> dzp5 = new HashMap<>();
            for (MissionTjVo m : missionTjVoList4) {
                dzp5.put(m.getDay(), m.getFinishingRate());
            }
            List<String> dataList4 = new ArrayList<>();
            for (String s : data) {
                if (null == dzp5.get(s)) {
                    dataList4.add("0");
                } else {
                    dataList4.add(dzp5.get(s));
                }
            }
            map5.put("name", "完成率");
            map5.put("type", "line");
            map5.put("yAxisIndex", 1);
            map5.put("data", dataList4);
            series.add(map5);

            object.put("xAxis", map);
            object.put("series", series);
        } else if ("3".equals(vo.getTimeType())) {
            //横坐标
            map = new HashMap<>();
            map2 = new HashMap<>();
            map3 = new HashMap<>();
            map4 = new HashMap<>();
            map5 = new HashMap<>();
            for (int i = 1; i <= 12; i++) {
                if (i < 10) {
                    data.add(vo.getTimeStr() + "-0" + i);
                } else {
                    data.add(vo.getTimeStr() + "-" + i);
                }
            }
            map.put("type", "category");
            map.put("data", data);
            vo.setTimeStart(data.get(0));
            vo.setTimeEnd(data.get(data.size() - 1));
            // 待指派数据查询   1、待指派；2、待处理；3、已处理  0、暂存
            vo.setState("1");
            vo.setFormate("YYYY-MM");
            List<MissionTjVo> missionTjVoList = missionMapper.getCount(vo);
            Map<String, String> dzp = new HashMap<>();
            for (MissionTjVo m : missionTjVoList) {
                dzp.put(m.getDay(), m.getCount());
            }
            map2.put("name", "待指派");
            map2.put("type", "bar");
            List<String> dataList = new ArrayList<>();
            for (String s : data) {
                if (null == dzp.get(s)) {
                    dataList.add("0");
                } else {
                    dataList.add(dzp.get(s));
                }
            }
            map2.put("data", dataList);
            series.add(map2);
            //待处理
            vo.setState("2");
            vo.setFormate("YYYY-MM");
            List<MissionTjVo> missionTjVoList2 = missionMapper.getCount(vo);
            Map<String, String> dzp2 = new HashMap<>();
            for (MissionTjVo m : missionTjVoList2) {
                dzp2.put(m.getDay(), m.getCount());
            }
            map3.put("name", "待处理");
            map3.put("type", "bar");
            List<String> dataList2 = new ArrayList<>();
            for (String s : data) {
                if (null == dzp2.get(s)) {
                    dataList2.add("0");
                } else {
                    dataList2.add(dzp2.get(s));
                }
            }
            map3.put("data", dataList2);
            series.add(map3);
            //已处理
            vo.setState("3");
            vo.setFormate("YYYY-MM");
            List<MissionTjVo> missionTjVoList3 = missionMapper.getCount(vo);
            Map<String, String> dzp3 = new HashMap<>();
            for (MissionTjVo m : missionTjVoList3) {
                dzp3.put(m.getDay(), m.getCount());
            }
            map4.put("name", "已处理");
            map4.put("type", "bar");
            List<String> dataList3 = new ArrayList<>();
            for (String s : data) {
                if (null == dzp3.get(s)) {
                    dataList3.add("0");
                } else {
                    dataList3.add(dzp3.get(s));
                }
            }
            map4.put("data", dataList3);
            series.add(map4);
            //完成率
            vo.setFormate("YYYY-MM");
            List<MissionTjVo> missionTjVoList4 = missionMapper.finishingRate(vo);
            Map<String, String> dzp5 = new HashMap<>();
            for (MissionTjVo m : missionTjVoList4) {
                dzp5.put(m.getDay(), m.getFinishingRate());
            }
            List<String> dataList4 = new ArrayList<>();
            for (String s : data) {
                if (null == dzp5.get(s)) {
                    dataList4.add("0");
                } else {
                    dataList4.add(dzp5.get(s));
                }
            }
            map5.put("name", "完成率");
            map5.put("type", "line");
            map5.put("yAxisIndex", 1);
            map5.put("data", dataList4);
            series.add(map5);

            object.put("xAxis", map);
            object.put("series", series);
        }
        return object;
    }

    public List<HashMap> getMission_type(String timeType, String timeStart, String timeEnd) {


        List<SysDict> sysDicts = sysDictService.getBaseMapper().selectList(Wrappers.<SysDict>lambdaQuery()
                .eq(SysDict::getDictType, "taskTypes"));
        Map<String, String> dictMap = new HashMap<>();
        sysDicts.forEach(x -> dictMap.put(x.getDictValue(), x.getDictLabel()));
        List<HashMap> mission_type_data = missionMapper.getMission_type(timeType, timeStart, timeEnd);
        mission_type_data.forEach(x -> x.put("name", dictMap.get(x.get("name") + "")));
        return mission_type_data;
    }

    public List<HashMap> getMission_state(String timeType, String timeStart, String timeEnd) {
        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("1", "待指派");
        stateMap.put("2", "待处理");
        stateMap.put("3", "已处理");
        stateMap.put("0", "暂存");
        stateMap.put("4", "开启");
        List<HashMap> mission_state = missionMapper.getMission_state(timeType, timeStart, timeEnd);
        mission_state.forEach(x -> {
            Map<String, String> map = new HashMap<>();
            x.put("name", stateMap.get(x.get("status") + ""));
            x.put("endRatio", "1");
            x.put("startRatio", "0");
            if (stateMap.get(x.get("status") + "").equals("待指派")) {
                map.put("color", "#ebbf46");
                x.put("itemstyle", map);
            } else if (stateMap.get(x.get("status") + "").equals("待处理")) {
                map.put("color", "#ebbf46");
                x.put("itemstyle", map);
            } else {
                map.put("color", "#ebbf46");
                x.put("itemstyle", map);
            }
        });
        return mission_state;
    }

    /**
     * 获取指定日期所在年的第一天
     *
     * @param date 指定日期
     * @return 日期字符串
     */
    public static String getYearFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取指定日期所在年的最后一天
     *
     * @param date 指定日期
     * @return 日期字符串
     */
    public static String getYearLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DATE, 31);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    public List<HashMap> getMission_type_team(String timeType, String timeStart, String timeEnd) {
        return missionMapper.getMission_type_team(timeType, timeStart, timeEnd);
    }


    /**
     * 工作台 统计接口
     * Author wzn
     * Date 2023/8/28 10:25
     */
    public Map<String, Object> statistics() {
//        Map<String, Long> map = new HashMap<>();
//
//        //未派单数
//        QueryWrapper<Mission> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("states", "1");
//        Long undispatchedOrderCpount = missionMapper.selectCount(queryWrapper);
//        map.put("undispatchedOrderCpount", undispatchedOrderCpount);
//
//        //进行中的工单
//        QueryWrapper<Mission> queryWrapper2 = new QueryWrapper<>();
//        queryWrapper2.ne("states", "3");
//        Long ingCount = missionMapper.selectCount(queryWrapper2);
//        map.put("ingCount", ingCount);
//
//        //未完成的巡检
//        QueryWrapper<Mission> queryWrapper3 = new QueryWrapper<>();
//        queryWrapper3.eq("states", "2");
//        queryWrapper3.and(tmp -> tmp.eq("source", "3").or().eq("source", "4"));
//
//        Long undoneCount = missionMapper.selectCount(queryWrapper3);
//        map.put("undoneCount", undoneCount);
//
//        return map;

        Map<String, Object> map = new HashMap<>();

        //未派单数
        SysUser sysUser = userService.getUser();
        String count = missionMapper.undispatchedOrderCpount("1", sysUser.getId() + "");
        map.put("undispatchedOrderCpount", count);

        //进行中的工单
//
        String count2 = missionMapper.undispatchedOrderCpount("2", sysUser.getId() + "");
        map.put("ingCount", count2);

        //未开启的巡检

        String count3 = missionMapper.undispatchedOrderCpount("4", sysUser.getId() + "");
        map.put("undoneCount", count3);


        // 逾期
        String ccc = missionMapper.overdueMissionCount(sysUser.getId() + "");
        map.put("overdueCount", ccc);

        // 30天
        String bbb = missionMapper.missionTimeCount(30, sysUser.getId() + "");
        map.put("missionMonth", bbb);


        // 7天
        String aaa = missionMapper.missionTimeCount(7, sysUser.getId() + "");
        map.put("missionWeek", aaa);

        // 任务总数

        String count4 = missionMapper.missionCount(sysUser.getDeptId() + "");
        map.put("missionCount", count4);

        // 今日
        String ddd = missionMapper.missionTodayCount(sysUser.getDeptId() + "");

        map.put("todayCount", ddd);


        return map;

    }


    /**
     * 近7天派单数
     * Author wzn
     * Date 2023/8/28 14:36
     */
    public JSONObject numberOfDenominations() {
        JSONObject jsonObject = new JSONObject();
        List<String> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("type", "category");
        //近7天日期数据封装
        Date newDate1 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -6);
        data.add(DateUtil.format(newDate1, "yyyy-MM-dd"));
        Date newDate2 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -5);
        data.add(DateUtil.format(newDate2, "yyyy-MM-dd"));
        Date newDate3 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -4);
        data.add(DateUtil.format(newDate3, "yyyy-MM-dd"));
        Date newDate4 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -3);
        data.add(DateUtil.format(newDate4, "yyyy-MM-dd"));
        Date newDate5 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -2);
        data.add(DateUtil.format(newDate5, "yyyy-MM-dd"));
        Date newDate6 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -1);
        data.add(DateUtil.format(newDate6, "yyyy-MM-dd"));
        data.add(DateUtil.format(new Date(), "yyyy-MM-dd"));
        map.put("data", data);
        jsonObject.put("xAxis", map);
        //柱形图数据封装
        List<Map<Object, Object>> mapList = new ArrayList<>();
        Map<Object, Object> objectMap = new HashMap<>();
        objectMap.put("name", "派单数");
        objectMap.put("type", "bar");
        List<String> stringList = new ArrayList<>();
        for (String s : data) {

            //派单数
            MissionTjVo missionTjVo = missionMapper.getSendOrdersCount(s);
            stringList.add(missionTjVo.getCount());
        }
        objectMap.put("data", stringList);
        mapList.add(objectMap);
        //完成数
        Map<Object, Object> objectMap2 = new HashMap<>();
        List<String> stringList2 = new ArrayList<>();

        objectMap2.put("name", "完成数");
        objectMap2.put("type", "bar");
        for (String s : data) {
            //派单数
            MissionTjVo missionTjVo = missionMapper.getCompleteCount(s);
            stringList2.add(missionTjVo.getCount());
        }
        objectMap2.put("data", stringList2);
        mapList.add(objectMap2);
        jsonObject.put("series", mapList);
        return jsonObject;
    }

    /**
     * 近7天告警数
     * Date 2023/8/28 14:36
     */
    public JSONObject numberGjOfDays() {
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = missionMapper.numberGjOfDays();
        } catch (Exception e) {
            // 初始化数据信息
            // 得到近7天的日期
            for (int i = 6; i >= 0; i--) {
                Map<String, Object> map = new HashMap<>();
                Date newDate1 = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -i);
                map.put("CREATETIME", DateUtil.format(newDate1, "yyyy-MM-dd"));
                map.put("COUNT", 0);
                list.add(map);
            }
        }

        // 组装数据
        // xAxis: {type: "category", boundaryGap: true, data: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月",], splitNumber: 1,},
        JSONObject xAxis = new JSONObject();
        xAxis.put("type", "category");
        xAxis.put("boundaryGap", true);
        xAxis.put("splitNumber", 1);
        List<String> data = list.stream().map(x -> MapUtils.getString(x, "CREATETIME")).collect(Collectors.toList());
        xAxis.put("data", data);
        jsonObject.put("xAxis", xAxis);

        // series: [{name: "2020年", data: [15040, 14278, 14136, 14008, 14086, 14305, 14965, 15865, 15399, 14664, 14559, 14801,], type: "line",},],
        List<Map<String, Object>> series = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "告警数");
        map.put("type", "line");
        List<String> data2 = list.stream().map(x -> MapUtils.getString(x, "COUNT")).collect(Collectors.toList());
        map.put("data", data2);
        series.add(map);
        jsonObject.put("series", series);

        return jsonObject;
    }


    //用电量评价

    public List<Map<String, Object>> energyEvaluate() {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        Double toDay1 = 0.00;
        Double lastDay1 = 0.00;
        Double toWeek1 = 0.00;
        Double lastWeek1 = 0.00;
        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;
        Double toYear1 = 0.00;
        Double lastYear1 = 0.00;
        wrapper.eq(DeviceCollect::getIsTotalDevice, 1).eq(DeviceCollect::getIsDel, 0).isNotNull(DeviceCollect::getLogicalBuildId).eq(DeviceCollect::getLogicalFloorId, 0).eq(DeviceCollect::getLogicalAreaId, 0).eq(DeviceCollect::getDeviceType, 10005);
//        wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collect = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collect) {
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1, new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    JsdcDateUtil.getFirstWeekDay(new Date())),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toYear = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toWeekList = influxdbService.queryResultProcess(toWeek);
            List<Map<String, Object>> lastWeekList = influxdbService.queryResultProcess(lastWeek);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));
            toWeek1 += Double.parseDouble(this.spreadResultProcess(toWeekList));
            lastWeek1 += Double.parseDouble(this.spreadResultProcess(lastWeekList));
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            toYear1 += Double.parseDouble(this.spreadResultProcess(toYearList));
            lastYear1 += Double.parseDouble(this.spreadResultProcess(lastYearList));
        }

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("toDay", new BigDecimal(toDay1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastDay", new BigDecimal(lastDay1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("toWeek", new BigDecimal(toWeek1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastWeek", new BigDecimal(lastWeek1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("toYear", new BigDecimal(toYear1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastYear", new BigDecimal(lastYear1).setScale(2, RoundingMode.HALF_UP).toString());
        list.add(map);
        return list;
    }


    //组装用电量评价sql
    public String getEnergyEvaluate(String time1, String time2, String id) {

        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND time" +
                " <'" + time1 + "' " +
                "AND " +
                "time >'" + time2 + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }


    public String spreadResultProcess(List<Map<String, Object>> list) {
        String spread = "0.00";
        if (list != null && list.size() != 0) {
            Map<String, Object> map = list.get(0);
            double num = (Double) map.get("spread");
            spread = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP).toString();
        }

        return spread;
    }

    /**
     * 当月告警类型
     * Date 2023/8/28 15:21
     */
    public JSONObject monthAlarm() {
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = missionMapper.monthAlarm();
        } catch (Exception e) {
            List<SysDict> dicts = sysDictService.getBaseMapper().selectList(Wrappers.<SysDict>lambdaQuery()
                    .eq(SysDict::getDictType, "warnType")
                    .eq(SysDict::getIsDel, G.ISDEL_NO));
            for (SysDict temp : dicts) {
                Map<String, Object> data = new HashMap<>();
                data.put("NAME", temp.getDictLabel());
                data.put("COUNT", 0);
                list.add(data);
            }
        }
        // 组装数据
        JSONObject legend = new JSONObject();
        legend.put("top", "bottom");
        legend.put("type", "scroll");
        List<String> data = list.stream().map(x -> MapUtils.getString(x, "NAME")).collect(Collectors.toList());
        legend.put("data", data);
        jsonObject.put("legend", legend);

        JSONObject series = new JSONObject();

        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (Map<String, Object> temp : list) {
            Map<String, Object> seriesDataTemp = new HashMap<>();
            seriesDataTemp.put("name", MapUtils.getString(temp, "NAME"));
            seriesDataTemp.put("value", MapUtils.getString(temp, "COUNT"));
            seriesData.add(seriesDataTemp);
        }
        series.put("data", seriesData);
        jsonObject.put("series", series);
        return jsonObject;
    }

    /**
     * 告警信息统计
     * Date 2023/8/28 15:21
     */
    public JSONObject alarmStatistics() {
        JSONObject jsonObject = new JSONObject();
        // 今日告警,本周告警,本月告警
        Map<String, Object> alarmNum = missionMapper.alarmStatisticsNum();
        if (CollectionUtils.isEmpty(alarmNum)) {
            alarmNum.put("JRGJ", 0);
            alarmNum.put("BZGJ", 0);
            alarmNum.put("BYGJ", 0);
        }
        jsonObject.put("alarm", alarmNum);

        // 监测设备(块)
        Map<String, Object> b = missionMapper.equipmentStatisticsNum();
        if (CollectionUtils.isEmpty(b)) {
            b.put("ZNUM", 0);
            b.put("ZCNUM", 0);
            b.put("LXNUM", 0);
            b.put("YCNUM", 0);
            b.put("SBNUM", 0);
        }
        jsonObject.put("equipment", b);

        // 商户统计
        Map<String, Object> c = new HashMap<>();
        // 总商户数量- 区域数据
        long num = sysBuildAreaService.getBaseMapper().selectCount(Wrappers.<SysBuildArea>lambdaQuery().eq(SysBuildArea::getIsDel, G.ISDEL_NO));
        c.put("merZnum", num);
        c.put("merJRnum", missionMapper.merAlarmNum(0));
        c.put("merBYnum", missionMapper.merAlarmNum(30));
        jsonObject.put("merchant", c);

        // 网关设备
        Map<String, Object> d = new HashMap<>();
        Long wgNum = deviceGatewayMapper.selectCount(Wrappers.<DeviceGateway>lambdaQuery().eq(DeviceGateway::getIsDel, G.ISDEL_NO));
        d.put("wgNum", wgNum);
        jsonObject.put("wgsb", d);

        return jsonObject;
    }


    /**
     * app端调用接口
     * 根据状态进行查询不同的数据(待处理\已处理\我的关注 （publicity不为空，代表已关注）\指派)
     *
     * @param vo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<MissionVo> missionPageList(MissionVo vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);

        Page<MissionVo> missionPage = missionMapper.missionPageList(page, vo);
        missionPage.getRecords().forEach(a -> {
            if (null != a.getSource()) {
                a.setSourceName(retunStatusName(3, a.getSource()));//来源
            }
        });
        return missionPage;
    }

    public Page<MissionVo> appRepairPageList(MissionVo bean, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        SysUser user = userService.getUser();
        Page<MissionVo> missionPage = missionMapper.appRepairPageList(user, page, bean);
        List<MissionVo> records = missionPage.getRecords();
        records.forEach(a -> {
            if (null != a.getSource()) {
                a.setSourceName(retunStatusName(3, a.getSource()));//来源
            }
        });
//        records.sort(Comparator.comparing(Mission::getReportingTime).reversed());
        return missionPage;
    }

    public List<MissionVo> missionExcel(MissionVo bean, Integer pageIndex, Integer pageSize) {
        Page<MissionVo> page = this.pageListMission(bean, pageIndex, pageSize);
        List<MissionVo> records = page.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (MissionVo record : records) {
                //任务类型
                List<MissionAssign> missionAssigns = missionAssignMapper.selectList(new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, record.getId()));
                List<SysDict> taskTypes = null;
                if (CollUtil.isNotEmpty(missionAssigns)) {
                    taskTypes = sysDictService.getBaseMapper().selectList(new LambdaQueryWrapper<SysDict>()
                            .eq(SysDict::getIsDel, 0)
                            .eq(SysDict::getDictType, "taskTypes")
                            .eq(SysDict::getDictValue, missionAssigns.get(0).getTaskType()+"")
                    );
                    if (CollUtil.isNotEmpty(taskTypes)) {
                        record.setTaskTypeName(taskTypes.get(0).getDictLabel());
                    }
                }

                record.setReportUser(userMapper.selectById(record.getUserId()).getRealName());
                //状态
                switch (record.getStates()) {
                    case 0:
                        record.setStatesName("暂存");
                        break;
                    case 1:
                        record.setStatesName("待指派");
                        break;
                    case 2:
                        record.setStatesName("待处理");
                        break;
                    case 3:
                        record.setStatesName("已处理");
                        break;
                    case 4:
                        record.setStatesName("开启");
                        break;
                    case 5:
                        record.setStatesName("撤销");
                        break;
                    case 6:
                        record.setStatesName("忽略");
                        break;
                    default:
                        record.setStatesName("未知");
                }
            }
            return records;
        }
        return new ArrayList<>();
    }

    public void deleteMission(Integer id) {
        missionMapper.update(null, Wrappers.<Mission>lambdaUpdate()
                .eq(Mission::getIs_del, 0)
                .eq(Mission::getId, id)
                .set(Mission::getIs_del, 1)
        );
        //删除对应关注列表数据
        missionCareMapper.delete(Wrappers.<MissionCare>lambdaUpdate().eq(MissionCare::getMissionId, id));
    }


    public Page<MissionVo> appMissionPageList(MissionVo bean, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        String startTime = bean.getStartTime();
        String endTime = bean.getEndTime();
        Integer searchType = bean.getSearchType();
        if (StrUtil.isEmpty(startTime) && StrUtil.isEmpty(endTime)) {
            if (1 == searchType) {
                //本周数据
                bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(new Date()));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentWeek(new Date()));
            }
            if (2 == searchType) {
                //本月数据
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(new Date()));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(new Date()));
            }
        }
        Page<MissionVo> missionPage = missionMapper.appMissionPageList(page, bean);
        missionPage.getRecords().forEach(a -> {
            if (null != a.getSource()) {
                a.setSourceName(retunStatusName(3, a.getSource()));//来源
            }
        });
        return missionPage;
    }

    public boolean saveAppMission(Mission mission) {
        if (null != mission.getId()) {
            mission.setReportingTime(new Date());
            mission.setUpdateTime(new Date());
            updateById(mission);
        } else {
            mission.setReportingTime(new Date());
            mission.setCreateUser(mission.getUserId());
            mission.setCreateTime(new Date());
            mission.setIs_del(0);
            //订单号规则
            mission.setOrderID("GD" + mission.getSource() + System.currentTimeMillis());
            save(mission);
        }
        //向图片表中插入数据
        if (!CollectionUtils.isEmpty(mission.getFileList())) {
            List<String> ls = mission.getFileList().stream().filter(x -> StringUtils.isNotEmpty(x.getBizId())).map(SysFile::getBizId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ls)) {
                fileService.remove(new LambdaQueryWrapper<SysFile>().in(SysFile::getBizId, ls).eq(SysFile::getBizType, 4));
            }
            for (SysFile file : mission.getFileList()) {
                try {
                    file.setBizId(mission.getId().toString());
                    file.setBizType("4");
                    file.insert();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //通知审核上报工单(指派到维修组)
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setAppConfigCode("TEAMWORK_REPAIRS_ASSIGN");
        List<SysUser> sysUsers = userMapper.getUsersByRole("app_rw_zp");
        msgVo.setUserIdList(sysUsers.stream().map(SysUser::getId).collect(Collectors.toList()));
//        appPushMsgService.pushMsg(msgVo);
        return true;
    }


    /**
     * app单项处理功能
     *
     * @param bean
     * @return
     */
    public boolean waitHandleApp(MissionItemRecordVo bean) {
        long count = itemRecordService.count(new LambdaQueryWrapper<MissionItemRecord>()
                .eq(MissionItemRecord::getMissionId, bean.getMissionId()).eq(MissionItemRecord::getIsHandle, 0));
        bean.setCreateTime(new Date());
        bean.setCreateUser(userService.getUser().getId());
        if (!CollectionUtils.isEmpty(bean.getFiles())) {
            for (SysFile file : bean.getFiles()) {
                try {
                    file.setBizId(bean.getId().toString());
                    file.setBizType("3");
                    file.insert();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Mission mis = getById(bean.getMissionId());
        if (itemRecordService.updateById(bean)) {
            Mission mission = new Mission();
            mission.setSource(3);
            mission.setSourceId(bean.getId());
            mission.setDeviceId(bean.getDeviceId());
            mission.setDeviceType(bean.getDeviceType());
            mission.setLevels(mis.getLevels());
            mission.setSubstance(bean.getSubContent());
            mission.setReportingTime(new Date());
            mission.setStates(1);
            save(mission);
        }
        if (count == 1) {//最后一个处理信息，将原有的巡检任务计划中的处理状态改成已处理
            mis.setStates(3);
            updateById(mis);
        }
        return false;
    }


    public JSONObject inspectionDataApp(Integer missionId, Integer id) {
        JSONObject object = new JSONObject();
        Mission mission = getById(missionId);//上报信息
        object.put("mission", mission);
        object.put("subs", recordService.missionItemRecordList(mission));
        return object;
    }


    public JSONObject gdtjs() {
        JSONObject object = new JSONObject();
        List<String> stringList = missionMapper.gdtjs();
        object.put("count0", stringList.get(0));
        object.put("count1", stringList.get(1));
        object.put("count2", stringList.get(2));
        object.put("count3", stringList.get(3));
        object.put("count4", stringList.get(4));
        object.put("count5", stringList.get(5));
        object.put("count6", stringList.get(6));
        object.put("count7", stringList.get(7));
        object.put("count8", stringList.get(8));
        object.put("count9", stringList.get(9));
        return object;
    }





    /**
     * 监管设备数 当月巡保设备 当月异常设备
     *
     * @return
     */
    public Map<String, Object> getRightCount() {
        Map<String, Object> map = new HashMap<>();
        // 监管设备数
        long count = 0;
        try {
            count = deviceCollectMapper.selectCount(Wrappers.<DeviceCollect>lambdaQuery()
                    .eq(DeviceCollect::getIsDel, G.ISDEL_NO)
                    .eq(DeviceCollect::getIsTotalDevice, 1));
        } catch (Exception e) {

        }
        map.put("taskNum", count);
        // 当月巡保设备 itemRecordService
        long count2 = 0;
        try {
            count2 = itemRecordService.getBaseMapper().selectCount(Wrappers.<MissionItemRecord>lambdaQuery()
                    .ge(MissionItemRecord::getCreateTime, DateUtil.beginOfMonth(new Date()))
                    .le(MissionItemRecord::getCreateTime, DateUtil.endOfMonth(new Date()))
                    .groupBy(MissionItemRecord::getDeviceId)
            );
        } catch (Exception ignored) {

        }
        map.put("taskNum2", count2);
        // 当月异常设备
        long count3 = 0;
        try {
            count3 = itemRecordService.getBaseMapper().selectCount(Wrappers.<MissionItemRecord>lambdaQuery()
                    .ge(MissionItemRecord::getCreateTime, DateUtil.beginOfMonth(new Date()))
                    .le(MissionItemRecord::getCreateTime, DateUtil.endOfMonth(new Date()))
                    .eq(MissionItemRecord::getExecution, 2)
                    .groupBy(MissionItemRecord::getDeviceId)
            );
        } catch (Exception e) {

        }
        map.put("taskNum3", count3);
        return map;
    }

    /**
     * 班组巡保任务数
     *
     * @return
     */
    public List<Map<String, Object>> getTeamTaskCount() {
        // 巡检组 人数：3人 本周任务数：16
        List<Map<String, Object>> list = new ArrayList<>();
        teamGroupsService.getTeamGroupPage(new TeamGroups(), 1, 1000).getRecords().forEach(x -> {
            Map<String, Object> map = new HashMap<>();
            map.put("teamName", x.getName());
            map.put("teamNum", x.getPeopleNum());
            map.put("taskNum", 5);
            list.add(map);
        });
        // 如果list 超过3个,则取前三个
        if (list.size() > 3) {
            return list.subList(0, 3);
        }
        return list;
    }

    public List<Map> responseSpeed(String startTime, String endTime) {
        return missionMapper.responseSpeed(startTime, endTime);
    }

    public List<Map> completionStatus(MissionVo bean) {
        return missionMapper.completionStatus(bean);
    }

    public List<MissionVo> abnormalEquipment() {
        // 区域
        Map<Integer, SysBuildArea> areaMap = new HashMap<>();
        areaMap = sysBuildAreaMapper.selectList(Wrappers.<SysBuildArea>lambdaQuery().in(SysBuildArea::getIsDel, G.ISDEL_NO)).stream().collect(Collectors.toMap(SysBuildArea::getId, sysBuildArea -> sysBuildArea));

        List<MissionVo> list = missionMapper.abnormalEquipment();
        for (MissionVo vo : list) {

            String deviceName = "";
            Integer deviceId = vo.getDeviceId();
            Integer type = vo.getDeviceType();
            if (null != deviceId && null != type) {
                //1、采集设备 2、视频设备 3、网关设备 4电梯
                if (1 == type) {
                    DeviceCollect collect = collectService.getById(deviceId);
                    if (null != collect) {
                        deviceName = collect.getName();
                    }
                } else if (2 == type) {

                } else if (3 == type) {

                }
            }
            vo.setDeviceName(deviceName);
            //位置
            if (areaMap.get(vo.getAreaId()) != null) {
                vo.setAreaName(areaMap.get(vo.getAreaId()).getAreaName());
                vo.setDevicePlace(areaMap.get(vo.getAreaId()).getAreaName());
            }
            //设备类型 1、采集设备 2、视频设备 3、网关设备 4电梯
            if (null == vo.getDeviceType()) {
                vo.setDeviceTypeName("");
            } else if (1 == vo.getDeviceType()) {
                vo.setDeviceTypeName("采集设备");
            } else if (2 == vo.getDeviceType()) {
                vo.setDeviceTypeName("视频设备");
            } else if (3 == vo.getDeviceType()) {
                vo.setDeviceTypeName("网关设备");
            } else if (4 == vo.getDeviceType()) {
                vo.setDeviceTypeName("电梯");
            } else {
                vo.setDeviceTypeName("aep设备");
            }


        }
        return list;
    }

    public JSONObject missionTypeCount(String startTime, String endTime) {
        JSONObject object = new JSONObject();
        List<Map<String, String>> mapList = new ArrayList<>();
        List<String> stringList = missionMapper.missionTypeCount(startTime, endTime);
        Map<String, String> map = new HashMap<>();
        map.put("overWeightNum", stringList.get(0));
        map.put("stock", "巡检");


        Map<String, String> map2 = new HashMap<>();
        map2.put("overWeightNum", stringList.get(1));
        map2.put("stock", "保养");


        Map<String, String> map3 = new HashMap<>();
        map3.put("overWeightNum", stringList.get(2));
        map3.put("stock", "抢修");


        Map<String, String> map4 = new HashMap<>();
        map4.put("overWeightNum", stringList.get(3));
        map4.put("stock", "维修");


        mapList.add(map);
        mapList.add(map2);
        mapList.add(map3);
        mapList.add(map4);
        object.put("mapList", mapList);

        //1、告警上报；2、人员上报；3、巡检异常上报；4、巡检计划生成
        MissionVo missionVo = missionMapper.missionTypeCount2(startTime, endTime);
        List<Map<String, String>> mapList2 = new ArrayList<>();
        Map<String, String> map5 = new HashMap<>();
        map5.put("underweightNum", missionVo == null || missionVo.getCount0() == null ? "0" : missionVo.getCount0());
        map5.put("stock", "告警上报");


        Map<String, String> map6 = new HashMap<>();
        map6.put("underweightNum", missionVo == null || missionVo.getCount1() == null ? "0" : missionVo.getCount1());
        map6.put("stock", "人员上报");


        Map<String, String> map7 = new HashMap<>();
        map7.put("underweightNum", missionVo == null || missionVo.getCount2() == null ? "0" : missionVo.getCount2());
        map7.put("stock", "巡检异常上报");


        Map<String, String> map8 = new HashMap<>();
        map8.put("underweightNum", missionVo == null || missionVo.getCount3() == null ? "0" : missionVo.getCount3());
        map8.put("stock", "巡检计划生成");


        mapList2.add(map5);
        mapList2.add(map6);
        mapList2.add(map7);
        mapList2.add(map8);

        object.put("mapList2", mapList2);
        return object;
    }


    public List<MissionVo> listData() {
        List<MissionVo> missionVos = missionMapper.listData();
        List<SysDict> taskType = sysDictService.selectByType("taskTypes");
        Map<String, String> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(taskType)) {
            for (SysDict s : taskType) {
                map.put(s.getDictValue(), s.getDictLabel());
            }
        }

        List<SysDict> planType = sysDictService.selectByType("planType");
        Map<String, String> map2 = new HashMap<>();
        if (CollectionUtil.isNotEmpty(planType)) {
            for (SysDict s : planType) {
                map2.put(s.getDictValue(), s.getDictLabel());
            }
        }
        if (CollectionUtil.isNotEmpty(missionVos)) {
            for (MissionVo m : missionVos) {
                //=4 planType  1、告警上报；2、人员上报；3、巡检异常上报；4、巡检计划生成
                if ("4".equals(m.getSource() + "")) {
                    m.setSourceName("巡检计划生成");
                    m.setTaskTypeName(String.valueOf(map2.get(m.getTaskType())));
                } else if ("1".equals(m.getSource() + "")) {
                    m.setSourceName("告警上报");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                } else if ("2".equals(m.getSource() + "")) {
                    m.setSourceName("人员上报");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                } else if ("3".equals(m.getSource() + "")) {
                    m.setSourceName("巡检异常上报");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                }

                //1、待指派；2、待处理；3、已处理  0、暂存 4、开启 5、撤销,6：忽略
                if (null != m.getStates()) {
                    switch (m.getStates()) {
                        case 1:
                            //执行代码
                            m.setStatesName("待指派");
                            break;
                        case 2:
                            m.setStatesName("待处理");
                            break;
                        case 3:
                            //执行相同的代码
                            m.setStatesName("已处理");
                            break;
                        case 0:
                            //执行相同的代码
                            m.setStatesName("暂存");
                            break;
                        case 4:
                            //执行相同的代码
                            m.setStatesName("开启");
                            break;
                        case 5:
                            //执行相同的代码
                            m.setStatesName("撤销");
                            break;
                        case 6:
                            //执行相同的代码
                            m.setStatesName("忽略");
                            break;
                    }
                }

            }
        }
        return missionVos;
    }

    public List<MissionVo> listData2() {
        List<MissionVo> missionVos = missionMapper.listData2();
        List<SysDict> taskType = sysDictService.selectByType("taskTypes");
        Map<String, String> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(taskType)) {
            for (SysDict s : taskType) {
                map.put(s.getDictValue(), s.getDictLabel());
            }
        }

        List<SysDict> planType = sysDictService.selectByType("planType");
        Map<String, String> map2 = new HashMap<>();
        if (CollectionUtil.isNotEmpty(planType)) {
            for (SysDict s : planType) {
                map2.put(s.getDictValue(), s.getDictLabel());
            }
        }
        if (CollectionUtil.isNotEmpty(missionVos)) {
            for (MissionVo m : missionVos) {
                //来源1、(系统工单)告警上报；2、人员上报；3、（巡检工单）巡检异常上报；4、巡检计划生成  5、（服务工单）报事报修  6、（投诉工单）投诉申请
                if ("4".equals(m.getSource() + "")) {
                    m.setSourceName("巡检计划生成");
                    m.setTaskTypeName(String.valueOf(map2.get(m.getTaskType())));
                } else if ("1".equals(m.getSource() + "")) {
                    m.setSourceName("告警上报");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                } else if ("2".equals(m.getSource() + "")) {
                    m.setSourceName("人员上报");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                } else if ("3".equals(m.getSource() + "")) {
                    m.setSourceName("巡检异常上报");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                } else if ("5".equals(m.getSource() + "")) {
                    m.setSourceName("报事报修");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                } else if ("6".equals(m.getSource() + "")) {
                    m.setSourceName("投诉申请");
                    m.setTaskTypeName(String.valueOf(map.get(m.getTaskType())));
                }

                //1、待指派；2、待处理；3、已处理  0、暂存 4、开启 5、撤销,6：忽略
                if (null != m.getStates()) {
                    switch (m.getStates()) {
                        case 1:
                            //执行代码
                            m.setStatesName("待指派");
                            break;
                        case 2:
                            m.setStatesName("待处理");
                            break;
                        case 3:
                            //执行相同的代码
                            m.setStatesName("已处理");
                            break;
                        case 0:
                            //执行相同的代码
                            m.setStatesName("暂存");
                            break;
                        case 4:
                            //执行相同的代码
                            m.setStatesName("开启");
                            break;
                        case 5:
                            //执行相同的代码
                            m.setStatesName("撤销");
                            break;
                        case 6:
                            //执行相同的代码
                            m.setStatesName("忽略");
                            break;
                    }
                }

            }
        }
        return missionVos;
    }

    /**
     * 任务类型最大用时统计
     * Author wzn
     * Date 2023/11/6 9:52
     */
    public List<String> timeData(String startTime, String endTime) {
        return missionMapper.timeData(startTime, endTime);
    }


    /**
     * 任务类型最小用时统计
     * Author wzn
     * Date 2023/11/6 9:52
     */
    public List<String> timeData2(String startTime, String endTime) {
        return missionMapper.timeData2(startTime, endTime);
    }

    /**
     * 任务类型平均用时统计
     * Author wzn
     * Date 2023/11/6 9:52
     */
    public List<String> timeData3(String startTime, String endTime) {
        return missionMapper.timeData3(startTime, endTime);
    }

    public JSONObject inspectionAppData(MissionItemRecordVo bean) {
        JSONObject object = new JSONObject();
        Mission mission = getById(bean.getMissionId());//上报信息
        MissionVo vo = new MissionVo();
        BeanUtils.copyProperties(mission, vo);
        vo.setSourceName(retunStatusName(3, vo.getSource()));
        SysDict dict = returnDicst("warnLevel", vo.getLevels());
        if (StringUtils.isNotNull(dict)) {
            vo.setLevelsName(dict.getDictLabel());
        }
        if (null != vo.getUserId()) {
            SysUser sysUser = userService.getById(vo.getUserId());
            if (null != sysUser) {
                vo.setUserName(sysUser.getRealName());
            }
        }
        if (null != vo.getHandleId()) {
            SysUser sysUser = userService.getById(vo.getHandleId());
            if (null != sysUser) {
                vo.setHandleName(sysUser.getRealName());
            }
        }
        if (null != vo.getDeviceId() && null != vo.getDeviceType()) {
            vo.setDeviceName(recordService.selectByDeviceName(vo.getDeviceId(), vo.getDeviceType()));
        }
        if (null != vo.getAreaId()) {
            SysBuildArea area = sysBuildAreaService.getById(vo.getAreaId());
            vo.setAreaName(area.getAreaName());
        }
        List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId() + "").eq(SysFile::getBizType, "4"));
        vo.setFileList(files);
        //耗材信息
        vo.setConsumables(consumableMapper.selectList(new LambdaQueryWrapper<MissionConsumable>()
                .eq(MissionConsumable::getIsDel, 0)
                .eq(MissionConsumable::getTemporary, 0)
                .eq(MissionConsumable::getMissionId, mission.getId())
        ));
        object.put("bean", vo);
        if (null != mission.getSource() && 4 != mission.getSource()) {
            //指派信息，目前只会存在一个启用状态的指派的班组数据
            List<MissionAssignVo> vos = assignService.selectAssignsList(bean.getMissionId(), 1);
            if (!vos.isEmpty() && vos.size() > 0) {
                List<TeamGroupUser> groupUsers = selectByGroupUser(vos.get(0).getTeamGroupsId());
                object.put("groupUsers", groupUsers);
            }
            object.put("assigns", vos);
        }
        if (null != mission.getSource() && 4 == mission.getSource()) { //巡检计划内容
            JobPlan plan = jobPlanService.getById(mission.getSourceId());//用来获取任务名称、周期、任务类型字段
            object.put("plan", plan);
            object.put("subs", recordService.missionItemRecordList(mission));
        }
        return object;
    }

    public List<Map> trendsMonth() {
        return missionMapper.trendsMonth();
    }

    public List<Map> trendsYear() {
        return missionMapper.trendsYear();
    }

    public Page<MissionVo> pageReportList(MissionVo vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        Page<MissionVo> missionPage = missionMapper.pageReportList(page, vo);
        missionPage.getRecords().forEach(a -> {
            parameterMiss(a);
        });
        return missionPage;
    }

    /**
     * 公共返回参数
     *
     * @param a
     */
    public MissionVo parameterMiss(MissionVo a) {
        if (null != a.getUserId()) {
            SysUser user = userService.getById(a.getUserId());
            if (StringUtils.isNotNull(user)) {
                a.setUserName(user.getRealName());
            }
        }
        if (null != a.getStates()) {
            a.setStatesName(retunStatusName(1, a.getStates()));//任务状态
        }
        if (null != a.getRejectUser()) {
            SysUser user = userService.getById(a.getRejectUser());
            if (ObjUtil.isNotEmpty(user)) {
                a.setRejectUsername(user.getRealName());
            }
        }
        if (null != a.getSource()) {
            a.setSourceName(retunStatusName(3, a.getSource()));//来源
        }
        if (null != a.getUrgency()) {//紧急程度
            SysDict dict = returnDicst("missionUrgency", a.getUrgency());
            if (StringUtils.isNotNull(dict)) {
                a.setUrgencyName(dict.getDictLabel());
                a.setUrgencyColour(dict.getColour());
            }
        }
        if (null != a.getLevels()) {//级别
            SysDict dict = returnDicst("warnLevel", a.getLevels());
            if (StringUtils.isNotNull(dict)) {
                a.setLevelsName(dict.getDictLabel());
                a.setLevelsColour(dict.getColour());
            }
        }
        if (null != a.getAssignTaskType()) {//任务类型
            if (4 == a.getSource()) {
                SysDict dict = returnDicst("planType", a.getAssignTaskType());
                if (StringUtils.isNotNull(dict)) {
                    a.setTaskTypeName(dict.getDictLabel());
                }
            } else {
                SysDict dict = returnDicst("taskTypes", a.getAssignTaskType());
                if (StringUtils.isNotNull(dict)) {
                    a.setTaskTypeName(dict.getDictLabel());
                }
            }
        }
        if (null != a.getTeamGroupsId()) {
            TeamGroups groups = teamGroupsMapper.selectById(a.getTeamGroupsId());
            if (StringUtils.isNotNull(groups)) {
                a.setTeamGroupsName(groups.getName());
            }

        }
        if (null != a.getAreaId()) {
            SysBuildArea area = sysBuildAreaService.getById(a.getAreaId());
            if (StringUtils.isNotNull(area)) {
                a.setAreaName(area.getAreaName());
                a.setAddress(area.getDetailAddress());
            }
        }
        return a;
    }


    /**
     * 根据任务ID查询相关设备的历史任务
     *
     * @param bean
     * @return
     */
    public List<Mission> getHistoryMission(Mission bean) {
        List<Mission> list = new ArrayList<>();
        Mission mission = missionMapper.selectById(bean.getId());
        if (null != mission) {
            if (null != mission.getDeviceId() && null != mission.getDeviceType()) {
                list = missionMapper.selectList(Wrappers.<Mission>lambdaQuery()
                        .eq(Mission::getIs_del, 0)
                        .eq(Mission::getStates, 3)
                        .eq(Mission::getDeviceId, mission.getDeviceId())
                        .eq(Mission::getDeviceType, mission.getDeviceType())
                        .ne(Mission::getId, mission.getId())
                        .orderByDesc(Mission::getHandleDate)
                );

            }
        }
        return list;
    }

    /**
     * app巡检计划单项处理功能
     *
     * @param bean
     * @return
     */
    public long waitHandleApp(MissionVo bean) {
        List<MissionItemRecord> recordList = bean.getRecordList();
        if (!recordList.isEmpty() && recordList.size() > 0) {
            for (MissionItemRecord a : recordList) {//向任务项记录表中插入数据
                a.setCreateTime(new Date());
                a.setCreateUser(bean.getCreateUser());
                a.setIsHandle(1);
                if (itemRecordService.updateById(a)) {
                    Mission mission = new Mission();
                    //向巡检异常表进行插入异常的数据
                    if (2 == a.getExecution()) {
                        mission.setSource(3);
                        mission.setUserId(bean.getCreateUser());
                        mission.setSourceId(a.getId());
                        mission.setIsReads(0);
                        mission.setDeviceId(a.getDeviceId());
                        mission.setDeviceType(a.getTypes());
                        mission.setAreaId(a.getRegion());
                        mission.setLevels(a.getLevels());
                        mission.setNotes(a.getDescription());
                        mission.setReportingTime(new Date());
                        List<MissionAssign> list = assignService.list(new LambdaQueryWrapper<MissionAssign>()
                                .eq(MissionAssign::getMissionId, bean.getId()).eq(MissionAssign::getState, 1));
                        if (!list.isEmpty() && list.size() > 0) {
                            mission.setTitle(list.get(0).getJobPlanName());
                        }
                        mission.setStates(1);
                        mission.setIs_del(0);
                        mission.setCreateUser(bean.getCreateUser());
                        mission.setCreateTime(new Date());
                        mission.setPublicity(0);
                        save(mission);
                        //向图片表中插入数据-上报的图片
                        if (!CollectionUtils.isEmpty(a.getFiles())) {
                            for (SysFile file : a.getFiles()) {
                                try {
                                    file.setBizId(mission.getId().toString());
                                    file.setBizType("4");
                                    file.insert();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    //向图片表中插入数据
                    if (!CollectionUtils.isEmpty(a.getFiles())) {
                        for (SysFile file : a.getFiles()) {
                            try {
                                file.setBizId(a.getId().toString());
                                file.setBizType("5");
                                file.setIsDel(0);
                                file.insert();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
            long count = itemRecordService.count(new LambdaQueryWrapper<MissionItemRecord>()
                    .eq(MissionItemRecord::getMissionId, bean.getId()).eq(MissionItemRecord::getIsHandle, 0));
            if (count == 0) {//最后一个处理信息，将原有的巡检任务计划中的处理状态改成已处理
                Mission mission = getById(bean.getId());
                mission.setStates(3);
                mission.setUpdateTime(new Date());
                mission.setHandleId(bean.getCreateUser());
                mission.setUpdateUser(bean.getCreateUser());
                updateById(mission);
            }
            return count;
        }
        return -1;
    }

    public JSONObject inspectionAppData1(MissionItemRecordVo bean) {
        JSONObject object = new JSONObject();
        Mission mission = getById(bean.getMissionId());//上报信息
        mission.setConsumables(consumableMapper.selectList(new LambdaQueryWrapper<MissionConsumable>()
                .eq(MissionConsumable::getIsDel, 0)
                .eq(MissionConsumable::getTemporary, 0)
                .eq(MissionConsumable::getMissionId, mission.getId())
        ));
        object.put("bean", mission);
        if (null != mission.getCreateUser()) {
            long count = missionCareService.count(new LambdaQueryWrapper<MissionCare>().eq(MissionCare::getMissionId, mission.getId()).eq(MissionCare::getCreateUser, bean.getCreateUser()));
            if (count > 0) {
                mission.setPublicity(1);
            } else {
                mission.setPublicity(0);
            }
        }
        if (null != mission.getSource() && 4 == mission.getSource()) { //巡检计划内容
            JobPlan plan = jobPlanService.getById(mission.getSourceId());//用来获取任务名称、周期、任务类型字段
            SysDict dict = returnDicst("taskTypes", plan.getPlanType());
            if (StringUtils.isNotNull(dict)) {
                plan.setTypeName(dict.getDictLabel());
            }
            object.put("plan", plan);
            object.put("subs", recordService.appMissionItemRecordList1(mission));
        }
        return object;
    }

    /**
     * APP端待处理、已处理、我的关注
     *
     * @param vo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<MissionVo> pageListMissionApp(MissionVo vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        String startTime = vo.getStartTime();
        String endTime = vo.getEndTime();
        Integer searchType = vo.getSearchType();
        if (StrUtil.isEmpty(startTime) && StrUtil.isEmpty(endTime)) {
            if (1 == searchType) {
                //本周数据
                vo.setStartTime1(DateUtils.getStartTimeOfCurrentWeek(new Date()));
                vo.setEndTime1(DateUtils.getEndTimeOfCurrentWeek(new Date()));
            }
            if (2 == searchType) {
                //本月数据
                vo.setStartTime1(DateUtils.getStartTimeOfCurrentMonth(new Date()));
                vo.setEndTime1(DateUtils.getEndTimeOfCurrentMonth(new Date()));
            }
        }
        Page<MissionVo> missionPage = missionMapper.pageListMissionApp(page, vo);
        missionPage.getRecords().forEach(a -> {
            a = parameterMiss(a);
            if (null != a.getSource() && 4 == a.getSource()) {
                a.setProportion(itemRecordService.percentageCount(a.getId()));
            }
        });
        return missionPage;
    }


//    @Autowired
//    private ResourceLoader resourceLoader;

    /**
     * 处理信息详情
     * 根据指派的班组查询巡检计划项
     *
     * @param bean
     * @return
     */
    public JSONObject exportExcel(MissionItemRecordVo bean, HttpServletResponse response) {
        JSONObject object = new JSONObject();
        Mission mission = getById(bean.getMissionId());//上报信息
        List<MissionRecordVo> recordVos = recordService.missionItemRecordList(mission);
        String path = System.getProperty("user.dir");
        String fileName = path.substring(0, path.indexOf("bin")) + "webapps\\ROOT\\任务明细.xls";

//        String fileName = "E:\\园区物联网管控平台2\\iotpt-zhyy\\src\\main\\webapp\\任务明细.xls";
        try {
            addExcel(fileName, "任务明细", recordVos, mission, response);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return object;
    }


    public JSONObject exportAssignExcel(MissionItemRecordVo bean, HttpServletResponse response) {


        JSONObject object = new JSONObject();
        Mission mission = getById(bean.getMissionId());//上报信息
        MissionVo vo = new MissionVo();
        BeanUtils.copyProperties(mission, vo);

        if (null != vo.getAreaId()) {
            SysBuildArea area = sysBuildAreaService.getById(vo.getAreaId());
            vo.setAreaName(area.getAreaName());
        }
        //获取上报时传入的设备异常的图片
        List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId() + "").eq(SysFile::getBizType, CommonEnum.MISSION_SB_FILE));
//        files.add(new SysFile());
//        files.add(new SysFile());
//        files.add(new SysFile());
//        files.add(new SysFile());
//        files.add(new SysFile());
//        files.add(new SysFile());
        vo.setFileList(files);

        String path = System.getProperty("user.dir");
        String fileName = path.substring(0, path.indexOf("bin")) + "webapps\\ROOT\\任务明细.xls";
//        String fileName="E:\\项目\\svn\\园区物联网管控平台\\iotpt-zhyy\\target\\iotpt-zhyy-0.0.1\\任务明细.xls";
        try {
            addExcel(fileName, "任务明细", vo, response);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return object;
    }

    private void generatePicture(HSSFWorkbook workbook, HSSFPatriarch patriarch, List<SysFile> sysFiles, int hang) throws IOException {
        //设置图片位于表格的单元格下标
        int lie = 1;
        BufferedImage bufferedImage = null;
        if (CollectionUtils.isEmpty(sysFiles)) {
            return;
        }
        for (int i = 0; i < sysFiles.size(); i++) {
            // 计算边距
            int mar = 10 + 10 + (sysFiles.size() - 1) * 10;
            // 大致平均值,每个图片宽度(1023为每个单元格总比，）
            int ave = (1023 - mar) / sysFiles.size();
            SysFile sysFile = sysFiles.get(i);
            String path = uploadPath + sysFile.getFileUrl();
//            String path="C:\\Users\\Administrator\\Desktop\\11.png";
            File file = new File(path);
            try {
                bufferedImage = ImageIO.read(file);
                if (ObjectUtils.isEmpty(bufferedImage)) {
                    continue;
                }
            } catch (IOException e) {
                continue;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            } catch (IOException e) {
                continue;
            }
            /* dx1:图片左边界距离单元格左边框像素值,
             * dy1:图片上边界距离单元格上边框像素值,
             * dx2:图片右边界距离单元格右边框像素值（负数）,
             * dy2:图片下边界距离单元格下边框像素值（负数）,
             * col1:列下标（0开始），
             * row1:行下标（0开始），
             * col2:列下标（1开始），
             * row2:行下标（1开始）。*/
            HSSFClientAnchor anchor = new HSSFClientAnchor(10 * (i + 1) + ave * i, 10, (10 + ave) * (i + 1), 245, (short) lie, hang, (short) lie, hang);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            patriarch.createPicture(anchor, workbook.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));

            byteArrayOutputStream.close();
        }
    }

    private void generatePicture(int lie, HSSFWorkbook workbook, HSSFPatriarch patriarch, SysFile sysFile, int hang) throws IOException {
        //设置图片位于表格的单元格下标
        BufferedImage bufferedImage = null;


        // 计算边距
        int mar = 20;
        // 大致平均值,每个图片宽度(1023为每个单元格总比，）
        int ave = (1023 - mar);
        String path = uploadPath + sysFile.getFileUrl();
//            String path="C:\\Users\\Administrator\\Desktop\\11.png";
        File file = new File(path);
        try {
            bufferedImage = ImageIO.read(file);

        } catch (IOException e) {

        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        } catch (IOException e) {

        }
        /* dx1:图片左边界距离单元格左边框像素值,
         * dy1:图片上边界距离单元格上边框像素值,
         * dx2:图片右边界距离单元格右边框像素值（负数）,
         * dy2:图片下边界距离单元格下边框像素值（负数）,
         * col1:列下标（0开始），
         * row1:行下标（0开始），
         * col2:列下标（1开始），
         * row2:行下标（1开始）。*/
        HSSFClientAnchor anchor = new HSSFClientAnchor(10, 10, (10 + ave), 245, (short) lie, hang, (short) lie, hang);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        patriarch.createPicture(anchor, workbook.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));

        byteArrayOutputStream.close();

    }


    public void addExcel(String absolutePath, String sheetName, MissionVo vo, HttpServletResponse response) throws IOException {

        FileInputStream fs = new FileInputStream(absolutePath);
        POIFSFileSystem ps = new POIFSFileSystem(fs); //使用POI提供的方法得到excel的信息
        HSSFWorkbook wb = new HSSFWorkbook(ps);
        HSSFSheet sheet = wb.getSheet(sheetName);

        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        sheet.getRow(0).getCell(0).setCellValue(vo.getTitle() + "明细");
        HSSFRow row;

        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);//上
        style.setBorderBottom(BorderStyle.THIN); //下
        style.setBorderLeft(BorderStyle.THIN);//左
        style.setBorderRight(BorderStyle.THIN);//右
        style.setAlignment(HorizontalAlignment.CENTER); // 居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        style.setWrapText(true);


        int lastRowNum = 2;//从第3行开始
        row = sheet.createRow(lastRowNum);
        row.setHeight((short) (200 * 20));

        HSSFCell cell0 = row.createCell(0);
        cell0.setCellStyle(style);
        cell0.setCellValue(vo.getAreaName());

        HSSFCell cell1 = row.createCell(1);
        cell1.setCellValue("");
        cell1.setCellStyle(style);

        if (vo.getFileList().size() < 1) {
            generatePicture(wb, patriarch, vo.getFileList(), lastRowNum);
        }

        HSSFCell cell2 = row.createCell(2);
        cell2.setCellValue(vo.getSubstance());
        cell2.setCellStyle(style);

        HSSFCell cell3 = row.createCell(3);
        cell3.setCellValue("");
        cell3.setCellStyle(style);


        HSSFCell cell4 = row.createCell(4);
        cell4.setCellValue(vo.getNotes());
        cell4.setCellStyle(style);

        if (vo.getFileList().size() > 1) {
            for (int i = 0; i < vo.getFileList().size(); i++) {
                if (i % 5 == 0) {
                    lastRowNum++;
                    row = sheet.createRow(lastRowNum);
                    row.setHeight((short) (200 * 20));
                }
                HSSFCell c = row.createCell(i % 5);
                c.setCellStyle(style);
                c.setCellValue("");
                generatePicture(i % 5, wb, patriarch, vo.getFileList().get(i), lastRowNum);
            }
        }


        OutputStream outputStream = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=export.xls");
        try {
            outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addExcel(String absolutePath, String sheetName, List<MissionRecordVo> dataList, Mission mission, HttpServletResponse response) throws IOException {

        FileInputStream fs = new FileInputStream(absolutePath);
        POIFSFileSystem ps = new POIFSFileSystem(fs); //使用POI提供的方法得到excel的信息
        HSSFWorkbook wb = new HSSFWorkbook(ps);
        HSSFSheet sheet = wb.getSheet(sheetName);

        sheet.getRow(0).getCell(0).setCellValue(mission.getTitle() + "明细");
        HSSFRow row;

        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);//上
        style.setBorderBottom(BorderStyle.THIN); //下
        style.setBorderLeft(BorderStyle.THIN);//左
        style.setBorderRight(BorderStyle.THIN);//右
        style.setAlignment(HorizontalAlignment.CENTER); // 居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中


        int lastRowNum = 2;//从第3行开始
        for (int i = 0; i < dataList.size(); i++) {
            MissionRecordVo vo = dataList.get(i);
            row = sheet.createRow(lastRowNum);
            row.setHeight((short) (40 * 20));

            HSSFCell cell0 = row.createCell(0);
            cell0.setCellStyle(style);
            cell0.setCellValue(vo.getRegionName());


            for (int j = 0; j < vo.getRecords().size(); j++) {
                if (j != 0) {
                    lastRowNum += 1;
                    row = sheet.createRow(lastRowNum);
                    row.setHeight((short) (40 * 20));
                }
                MissionItemRecord record = vo.getRecords().get(j);

                HSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(record.getDeviceName());
                cell1.setCellStyle(style);


                HSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(record.getSubContent());
                cell2.setCellStyle(style);


                HSSFCell cell3 = row.createCell(3);
                cell3.setCellValue("");
                cell3.setCellStyle(style);


                HSSFCell cell4 = row.createCell(4);
                cell4.setCellValue("");
                cell4.setCellStyle(style);
            }

            if (vo.getRecords().size() > 1) {
                CellRangeAddress region = new CellRangeAddress(lastRowNum - vo.getRecords().size() + 1, lastRowNum, 0, 0);
                sheet.addMergedRegion(region);
                rangeAddress(region, sheet);
            }

            lastRowNum += 1;
        }


        OutputStream outputStream = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=export.xls");
        try {
            outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 加边框
     *
     * @param region
     * @param sheet
     */
    public void rangeAddress(CellRangeAddress region, HSSFSheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet); // 左边框
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet); // 有边框
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet); // 上边框
    }

    public ResultInfo uploadAssignFile(MultipartFile files, String missionId, String bizType) {
        // 文件上传
        fileService.uploadFiles(new UploadParams("/missionFile", files, bizType, missionId));
        SysUser user = userService.getUser();
        missionMapper.update(null, Wrappers.<Mission>lambdaUpdate().set(Mission::getStates, "3")
                .set(Mission::getIsReads, 1).eq(Mission::getId, missionId));

        return ResultInfo.success();
    }

    public Map<String, String> getTimeWarnTJ() {
        Map<String, String> map = new HashMap<>();
        map.put("XY", StringUtils.isNotEmpty(missionMapper.avgXiangYingTime()) ? missionMapper.avgXiangYingTime() : "0");
        map.put("HFL", missionMapper.isNotWarn() != 0 ? missionMapper.warnHuiFu() : "0");
        map.put("HF", StringUtils.isNotEmpty(missionMapper.avgHuiFuTime()) ? missionMapper.avgHuiFuTime() : "0");
        return map;
    }

    public List<ElectricalSafetyReportVo2> electricalSafetyExport2(ElectricalSafetyReportVo2 vo2) {
        return missionMapper.getListByDeviceIdReport2(vo2);
    }

    //待办事项-小程序端
    public Page<WorkOrderVo> pageWorkOrderList(WorkOrderVo vo) {
        Page page = new Page(vo.getPageIndex(), vo.getPageSize());
        Integer id = userService.getUser().getId();
        List<String> roleFlags = roleService.selectByRoleFlag(id);
        // //状态类型（默认0）0、全部 1、进行中 2、我发起的  3、指派给我的  4、已完成的
        vo.setUserId(id);
        Page<WorkOrderVo> list = missionMapper.pageWorkOrderList(page, vo, roleFlags);
        list.getRecords().forEach(a -> {
            a.setSourceName(retunStatusName(3, a.getSource()));
            if (null != a.getHandleId()) {
                SysUser user = userService.getById(a.getHandleId());
                if (null != user) {
                    a.setUsername(user.getRealName());
                }
            }
            a.setStatesName(retunStatusName(1, a.getStates()));
        });
        return list;
    }


    //统计进行中数据
    public List<StateVo> workSourceCount(Integer large, Integer small) {
        SysUser user = userService.getUser();
        Integer id = user.getId();
        List<String> roleFlags = roleService.selectByRoleFlag(user.getId());

        List<Integer> list = missionMapper.workSourceCount(large, small, id, roleFlags);
        List<StateVo> vos = new ArrayList<>();
        StateVo vo5 = new StateVo();
        vo5.setId(0);
        vo5.setName("全部");
        //普通用户
        if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
            Page page = new Page(1, 10);
            WorkOrderVo vo = new WorkOrderVo();
            vo.setLarge(0);
            vo.setStateType(0);
            vo.setSortType(1);
            vo.setUserId(id);
            vo5.setNum(Math.toIntExact(missionMapper.pageWorkOrderList(page, vo, roleFlags).getTotal()));
        } else {
            //管理员
            Page page = new Page(1, 10);
            WorkOrderVo vo = new WorkOrderVo();
            vo.setLarge(0);
            vo.setStateType(0);
            vo.setSortType(1);
            vo5.setNum(Math.toIntExact(missionMapper.pageWorkOrderList(page, vo, roleFlags).getTotal()));
        }
        vos.add(vo5);

        StateVo vo1 = new StateVo();
        vo1.setId(1);
        vo1.setName("待指派");
        vo1.setNum(list.get(0));
        vos.add(vo1);


        StateVo vo6 = new StateVo();
        vo6.setId(5);
        vo6.setName("待处理");
        vo6.setNum(list.get(4));
        vos.add(vo6);

        StateVo vo2 = new StateVo();
        vo2.setId(2);
        vo2.setName("我发起的");
        vo2.setNum(list.get(1));
        vos.add(vo2);

        StateVo vo3 = new StateVo();
        vo3.setId(3);
        vo3.setName("指派给我的");
        vo3.setNum(list.get(2));
        vos.add(vo3);

        StateVo vo4 = new StateVo();
        vo4.setId(4);
        vo4.setName("已完成");
        vo4.setNum(list.get(3));
        vos.add(vo4);
        return vos;
    }


    //工单的详情
    public Mission wordOrderDetails(Integer id) {
        Mission mission = getById(id);
        if (null != mission.getQuestionType()) {//问题类型
            SysDict dict = sysDictService.getOne(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, "missQuestion").eq(SysDict::getDictValue, String.valueOf(mission.getQuestionType())));
            mission.setQuestionName(dict.getDictLabel());
        }
        if (null != mission.getSource()) {
            mission.setSourceName(retunStatusName(3, mission.getSource()));
        }
        if (null != mission.getLevels()) {
            SysDict dict = sysDictService.getOne(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, "warnLevel").eq(SysDict::getDictValue, String.valueOf(mission.getLevels())));
            mission.setLevelsName(dict.getDictLabel());
        }
        SysUser user = userService.getById(mission.getUserId());
        if (null != user) {
            mission.setUserName(user.getRealName());
        }
        SysOrgDept dept = orgDeptService.getById(user.getDeptId());
        if (null != dept) {
            mission.setDeptName(dept.getDeptName());
        }
        List<MissionAssign> list = assignService.list(new LambdaQueryWrapper<MissionAssign>().eq(MissionAssign::getMissionId, mission.getId()));
        if (!list.isEmpty() && list.size() > 0) {
            mission.setHandleDate(list.get(0).getDeadline());
        }
        if (null != mission.getSource() && 4 == mission.getSource()) { //巡检计划内容
            JobPlan plan = jobPlanService.getById(mission.getSourceId());//用来获取任务名称、周期、任务类型字段
            if (null != plan) {
                mission.setPlanName(plan.getPlanName());
            }
        }
        if (null != mission.getStates()) {
            mission.setStatesName(retunStatusName(1, mission.getStates()));
        }
        List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId() + "")
                .eq(SysFile::getBizType, "4"));
        mission.setFileList(files);
        return mission;
    }


    public List<Map<String, Object>> missionType() {
        return missionMapper.missionType();
    }


    public List<Map<String, Object>> missionSource() {
        return missionMapper.missionSource();
    }


    public Map<String, Object> missionTrend() {
        List<Map<String, Object>> list = missionMapper.missionTrend();
        List<Map<String, Object>> list1 = missionMapper.missionTrendLast();
        Map<String, Object> y = new HashMap<>();
        List<String> thisYear = new ArrayList<>();
        List<String> lsatYear = new ArrayList<>();
        for (Map<String, Object> map : list) {
            thisYear.add(map.get("POINT") + "");
        }
        for (Map<String, Object> map : list1) {
            lsatYear.add(map.get("POINT") + "");
        }
        y.put("this", thisYear);
        y.put("last", lsatYear);
        return y;
    }

    public List<Map<String, Object>> missionTrendLast() {
        return missionMapper.missionTrendLast();
    }

    public Map<String, Object> deviceState() {
        Map<String, Object> map = new HashMap<>();
        List<ConfigDeviceType> list =
                configDeviceTypeService.list(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getIsDel
                        , 0).orderBy(true, true, ConfigDeviceType::getId));
        for (ConfigDeviceType configDeviceType : list) {
            Map<String, String> num = new HashMap<>();
            Long run =
                    deviceCollectMapper.selectCount(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getDeviceType,
                            configDeviceType.getId()).eq(DeviceCollect::getStatus, 2).eq(DeviceCollect::getIsDel, 0));


            Long fault =
                    deviceCollectMapper.selectCount(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getDeviceType,
                            configDeviceType.getId()).inSql(DeviceCollect::getStatus, "3,4").eq(DeviceCollect::getIsDel, 0));
            Long out =
                    deviceCollectMapper.selectCount(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getDeviceType,
                            configDeviceType.getId()).eq(DeviceCollect::getStatus, 1).eq(DeviceCollect::getIsDel, 0));
            num.put("run", run + "");
            num.put("fault", fault + "");
            num.put("out", out + "");
            map.put(configDeviceType.getDeviceTypeName(), num);
        }
        Long run =
                deviceCollectMapper.selectCount(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getStatus, 2).eq(DeviceCollect::getIsDel, 0));


        Long fault =
                deviceCollectMapper.selectCount(new LambdaQueryWrapper<DeviceCollect>().inSql(DeviceCollect::getStatus, "3,4").eq(DeviceCollect::getIsDel, 0));
        Long out =
                deviceCollectMapper.selectCount(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getStatus, 1).eq(DeviceCollect::getIsDel, 0));
        map.put("runAll", run);
        map.put("faultAll", fault);
        map.put("outAll", out);
        return map;
    }

    public JSONObject inspectionData(MissionItemRecordVo bean) {
        JSONObject object = new JSONObject();
        Mission mission = getById(bean.getMissionId());//上报信息
        MissionVo vo = new MissionVo();
        BeanUtils.copyProperties(mission, vo);
        vo.setSourceName(retunStatusName(3, vo.getSource()));
        String dictType = "warnLevel";
        if (5 == vo.getSource() || 6 == vo.getSource()) {
            dictType = "appEmergent";
        }
        SysDict dict = returnDicst(dictType, vo.getLevels());
        if (StringUtils.isNotNull(dict)) {
            vo.setLevelsName(dict.getDictLabel());
        }
        if (null != bean.getCreateUser()) {
            long count = missionCareService.count(new LambdaQueryWrapper<MissionCare>().eq(MissionCare::getMissionId, mission.getId()).eq(MissionCare::getCreateUser, bean.getCreateUser()));
            if (count > 0) {
                vo.setPublicity(1);
            } else {
                vo.setPublicity(0);
            }
        }
        if (null != vo.getRejectUser()) {
            SysUser user = userService.getById(vo.getRejectUser());
            if (ObjUtil.isNotEmpty(user)) {
                vo.setRejectUsername(user.getRealName());
            }
        }
        if (null != vo.getUserId()) {
            SysUser sysUser = userService.getById(vo.getUserId());
            if (null != sysUser) {
                vo.setUserName(sysUser.getRealName());
            }
        }
        if (null != vo.getAssignerId()) {
            SysUser sysUser = userService.getById(vo.getAssignerId());
            if (null != sysUser) {
                vo.setAssionUserName(sysUser.getRealName());
            }
        }
        if (null != vo.getHandleId()) {
            SysUser sysUser = userService.getById(vo.getHandleId());
            if (null != sysUser) {
                vo.setHandleName(sysUser.getRealName());
            }
        }
        if (null != vo.getDeviceId() && null != vo.getDeviceType()) {
            vo.setDeviceName(recordService.selectByDeviceName(vo.getDeviceId(), vo.getDeviceType()));
        }
        if (null != vo.getAreaId()) {
            SysBuildArea area = sysBuildAreaService.getById(vo.getAreaId());
            if (StringUtils.isNotNull(area)) {
                vo.setAreaName(area.getAreaName());
                vo.setAddress(area.getDetailAddress());
            }
        }
        List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId() + "").eq(SysFile::getBizType, "4"));
        vo.setFileList(files);
        List<SysFile> handles = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, mission.getId() + "").eq(SysFile::getBizType, "5").eq(SysFile::getIsDel, 0));
        List<MissionConsumable> consumables = consumableMapper.selectList(new LambdaQueryWrapper<MissionConsumable>()
                .eq(MissionConsumable::getIsDel, 0)
                .eq(MissionConsumable::getTemporary, 0)
                .eq(MissionConsumable::getMissionId, mission.getId())
        );
        for (MissionConsumable consumable : consumables) {
            consumable.setUsername(userService.getById(consumable.getCreateUser()).getRealName());
        }
        vo.setConsumables(consumables);
        Map<String, List<String>> resultMap = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        String assistHandleIds = mission.getAssistHandleIds();
        if (null != assistHandleIds && StrUtil.isNotEmpty(assistHandleIds)) {
            for (String groupIdAnduserId : assistHandleIds.split(",")) {
//                String groupId = Arrays.asList(groupIdAnduserId.split(":")).get(0);
//                TeamGroups teamGroups = teamGroupsMapper.selectById(groupId);
                String userId = Arrays.asList(groupIdAnduserId.split(":")).get(1);
                SysUser sysUser = userMapper.selectById(userId);
                if (ObjUtil.isNotEmpty(sysUser)) {
                    nameList.add(sysUser.getRealName());
//                    resultMap.put(teamGroups.getName(), nameList);
                }

            }
        }

        if (CollUtil.isNotEmpty(nameList)) {
            vo.setAssistHandleNames(CollUtil.join(nameList.stream().distinct().collect(Collectors.toList()), ","));
        }
        object.put("bean", vo);
        object.put("handles", handles);
        if (null != mission.getSource() && 4 != mission.getSource()) {
            //指派信息，目前只会存在一个启用状态的指派的班组数据
            List<MissionAssignVo> vos = assignService.selectAssignsList(bean.getMissionId(), 1);
            if (!vos.isEmpty() && vos.size() > 0) {
                List<TeamGroupUser> groupUsers = selectByGroupUser(vos.get(0).getTeamGroupsId());
                object.put("groupUsers", groupUsers);
            }
            List<MissionAssignUser> assignUser = assignUserService.list(new LambdaQueryWrapper<MissionAssignUser>().eq(MissionAssignUser::getMissionId, mission.getId()));
            String assinUserString = "";
            if (!assignUser.isEmpty() && assignUser.size() > 0) {
                for (MissionAssignUser a : assignUser) {
                    SysUser user = userService.getById(a.getUserId());
                    assinUserString += user.getRealName() + "、";
                }
            }
            object.put("assignUser", assinUserString);
            object.put("assigns", vos);
        }
        if (null != mission.getSource() && 4 == mission.getSource()) { //巡检计划内容
            JobPlan plan = jobPlanService.getById(mission.getSourceId());//用来获取任务名称、周期、任务类型字段
            object.put("plan", plan);
            mission.setIsHandle(bean.getIsHandle());
            object.put("subs", recordService.missionItemRecordList(mission));
        }
        return object;
    }

    public String tasksInformation() {
        HashMap<String, Object> map = new HashMap<>();
        long count = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del, 0).ne(Mission::getStates, 0));
        map.put("zongshu", count);
        long count1 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del, 0).eq(Mission::getReportingTime, new Date()));
        map.put("today", count1);
        // 带指派
        long count2 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del, 0).eq(Mission::getStates, 1));
        map.put("daizhipai", count2);
        // 未开启
//        long count2 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del,0).eq(Mission::getStates,1));
//        map.put("daizhipai",count2);
//        // 进行中
//        long count2 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del,0).eq(Mission::getStates,1));
//        map.put("daizhipai",count2);
//        // 已逾期
//        long count2 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del,0).eq(Mission::getStates,1));
//        map.put("daizhipai",count2);
//        // 本周已完成
//        long count2 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del,0).eq(Mission::getStates,1));
//        map.put("daizhipai",count2);
//        // 30天已完成
//        long count2 = count(Wrappers.<Mission>lambdaQuery().eq(Mission::getIs_del,0).eq(Mission::getStates,1));
//        map.put("daizhipai",count2);
        return null;
    }

    public JSONObject totalTasksJSC(String startTime, String endTime, String areaId) {
        JSONObject object = new JSONObject();

        //未派单数
        String count = missionMapper.undispatchedOrderCpount("1", "");

        //进行中的工单
        String count2 = missionMapper.undispatchedOrderCpount("2", "");

        //未开启的巡检
        String count3 = missionMapper.undispatchedOrderCpount("4", "");


        // 逾期
        String ccc = missionMapper.overdueMissionCount("");

        // 30天
        String bbb = missionMapper.missionTimeCount(30, "");


        // 7天
        String aaa = missionMapper.missionTimeCount(7, "");

        // 任务总数
        String count4 = missionMapper.missionCount("");

        // 今日
        String ddd = missionMapper.missionTodayCount("");

        object.put("count0", count4);
        object.put("count1", ddd);
        object.put("count2", count);
        object.put("count3", count3);
        object.put("count4", count2);
        object.put("count5", ccc);
        object.put("count6", aaa);
        object.put("count7", bbb);
        return object;
    }


    public JSONObject missionCount2(String timeType) {
        String startTime = "";
        String endTime = "";
        JSONObject object = new JSONObject();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        List<String> nowYear = new ArrayList<>();
        List<String> lastYear = new ArrayList<>();
        List<String> nowMon = new ArrayList<>();
        List<String> lastMon = new ArrayList<>();

        //时间类型 1 年度 2 月度 3 日
        if (timeType.equals("1")) {
            // 今年年度
            List<String> monthsOfYearDates = getMonthsOfYearDates();
            List<String> lastYearDates = getLastYearMonthsDates();
//            object.put("timeData", monthsOfYearDates) ;

            for (String s : monthsOfYearDates) {
                startTime = s + " 00:00:00";

                String givenDateStr = s;
                LocalDate givenDate = LocalDate.parse(givenDateStr);
                LocalDate lastDayOfMonth = givenDate.with(TemporalAdjusters.lastDayOfMonth());
                String formattedLastDay = lastDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);
                endTime = formattedLastDay + " 23:59:59";

                String count = missionMapper.missionCount2(startTime, endTime);
                nowYear.add(count);
            }


            for (String s : lastYearDates) {
                startTime = s + " 00:00:00";

                String givenDateStr = s;
                LocalDate givenDate = LocalDate.parse(givenDateStr);
                LocalDate lastDayOfMonth = givenDate.with(TemporalAdjusters.lastDayOfMonth());
                String formattedLastDay = lastDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);
                endTime = formattedLastDay + " 23:59:59";

                String count = missionMapper.missionCount2(startTime, endTime);
                lastYear.add(count);
            }

            object.put("nowYear", nowYear);
            object.put("lastYear", lastYear);


        }

        if (timeType.equals("2")) {
            // 本月度
            List<String> currentMonthDates = getCurrentMonthDates();


            List<String> jq = new ArrayList<>();
            for (String s : currentMonthDates) {
                startTime = s + " 00:00:00";
                endTime = s + " 23:59:59";

                String count = missionMapper.missionCount2(startTime, endTime);
                nowMon.add(count);
                jq.add(s.substring(5, 10));
            }
            object.put("timeData", jq);
            //
            List<String> lastYearSameMonthDates = getLastYearSameMonthDates();

            for (String s : lastYearSameMonthDates) {
                startTime = s + " 00:00:00";
                endTime = s + " 23:59:59";

                String count = missionMapper.missionCount2(startTime, endTime);
                lastMon.add(count);
            }

            object.put("nowMon", nowMon);
            object.put("lastMon", lastMon);

        }


        return object;
    }


    private static List<String> getMonthsOfYearDates() {
        List<String> dates = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();

        for (int month = 1; month <= 12; month++) {
            LocalDate date = YearMonth.of(currentYear, month).atDay(1);
            dates.add(date.toString());
        }

        return dates;
    }


    private static List<String> getLastYearMonthsDates() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now(); // 获取当前日期
        LocalDate firstDayOfLastYear = today.minusYears(1).with(TemporalAdjusters.firstDayOfYear()); // 获取去年的第一天

        for (int month = 1; month <= 12; month++) {
            LocalDate firstDayOfMonth = firstDayOfLastYear.withMonth(month).withDayOfMonth(1);
            dates.add(firstDayOfMonth.toString());
        }

        return dates;
    }


    private static List<String> getCurrentMonthDates() {
        List<String> dates = new ArrayList<>();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // 获取本月的第一天
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()); // 获取本月的最后一天

        while (!startDate.isAfter(endDate)) {
            dates.add(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            startDate = startDate.plusDays(1); // 增加一天
        }

        return dates;
    }


    private static List<String> getLastYearSameMonthDates() {
        List<String> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now(); // 获取当前日期
        LocalDate startDateOfLastYearSameMonth = currentDate.minusYears(1).withDayOfMonth(1); // 获取去年同月的第一天
        LocalDate endDateOfLastYearSameMonth = startDateOfLastYearSameMonth.with(TemporalAdjusters.lastDayOfMonth()); // 获取去年同月的最后一天

        while (!startDateOfLastYearSameMonth.isAfter(endDateOfLastYearSameMonth)) {
            dates.add(startDateOfLastYearSameMonth.format(DateTimeFormatter.ISO_LOCAL_DATE));
            startDateOfLastYearSameMonth = startDateOfLastYearSameMonth.plusDays(1); // 增加一天
        }

        return dates;
    }

    public ResultInfo isPending(MissionVo mission) {

        missionMapper.updateById(mission);
        return ResultInfo.success();
    }


    public Page<MissionVo> pendingList(MissionVo bean) {
        Page page = new Page(bean.getPageIndex(), bean.getPageSize());
        String startTime = bean.getStartTime();
        String endTime = bean.getEndTime();
        Integer pendType = bean.getPendType();
        if (StrUtil.isEmpty(startTime) && StrUtil.isEmpty(endTime)) {
            if (1 == pendType) {
                //本周数据
                bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(new Date()));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentWeek(new Date()));
            }
            if (2 == pendType) {
                //本月数据
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(new Date()));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(new Date()));
            }
        }
        Page<MissionVo> missionPage = missionMapper.pendingList(page, bean);
        missionPage.getRecords().forEach(a -> {
            if (null != a.getSource()) {
                a.setSourceName(retunStatusName(3, a.getSource()));//来源
            }
        });
        return missionPage;
    }


    public Page<MissionVo> myPendingList(MissionVo bean) {
        Page page = new Page(bean.getPageIndex(), bean.getPageSize());
        Page<MissionVo> missionPage = missionMapper.myPendingList(page, bean);
        missionPage.getRecords().forEach(a -> {
            a = parameterMiss(a);
            if (null != a.getSource() && 4 == a.getSource()) {
                a.setProportion(itemRecordService.percentageCount(a.getId()));
            }
        });
        return missionPage;
    }

    public Long getCount(MissionVo bean) {
        Long count = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
        );
        return count;
    }
}
