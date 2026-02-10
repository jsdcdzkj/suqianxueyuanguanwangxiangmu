package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.MissionItemRecordMapper;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.DeviceGateway;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.operate.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.operate.DeviceVo;
import com.jsdc.iotpt.vo.operate.MissionRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务项记录
 */
@Service
@Transactional
public class MissionItemRecordService extends BaseService<MissionItemRecord> {

    @Autowired
    private DeviceCollectService collectService;
    @Autowired
    private DeviceGatewayService gatewayService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private MissionService missionService;
    @Autowired
    private CheckTemplateSubService subService;
    @Autowired
    private SysBuildAreaService buildAreaService;
    @Autowired
    private JobPlanAreaService jobPlanAreaService;
    @Autowired
    private MissionItemRecordMapper recordMapper;
    @Autowired
    private SysFileService fileService;
    @Autowired
    private MissionAssignService assignService;
    @Autowired
    private SysBuildAreaService areaService;
    @Autowired
    private SysBuildFloorService floorService;

    /**
     * 根据区域id获取相关的设备id
     *
     * @param areaId
     * @return
     */
    public List<DeviceVo> selectRegionByDevice(Integer areaId, String name) {
        List<DeviceVo> vos = new ArrayList<>();
        if (null != areaId) {
            SysBuildArea area = areaService.getById(areaId);
            List<DeviceCollect> collects = collectService.list(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getIsDel, 0)
                    .eq(DeviceCollect::getAreaId, areaId).like(!StringUtils.isEmpty(name), DeviceCollect::getName, name));
            collects.forEach(a -> {
                DeviceVo vo = new DeviceVo();
                vo.setId(a.getId());
                vo.setPId("dc_" + a.getId());
                vo.setName(a.getName());
                vo.setType(1);
                vo.setDeviceCode(a.getName() + "（" + a.getDeviceCode() + "）");
                vo.setAddress(area.getDetailAddress());
                vos.add(vo);
            });
            List<DeviceGateway> gateways = gatewayService.list(new LambdaQueryWrapper<DeviceGateway>().eq(DeviceGateway::getIsDel, 0)
                    .eq(DeviceGateway::getAreaId, areaId).like(!StringUtils.isEmpty(name), DeviceGateway::getName, name));
            gateways.forEach(c -> {
                DeviceVo vo = new DeviceVo();
                vo.setId(c.getId());
                vo.setPId("dg_" + c.getId());
                vo.setName(c.getName());
                vo.setType(3);
                vo.setDeviceCode(c.getName() + "（" + c.getDeviceCode() + "）");
                vo.setAddress(area.getDetailAddress());
                vos.add(vo);
            });
        }
        return vos;
    }

    /**
     * 根据设备id和设备类型获取数据的值
     *
     * @param deviceId
     * @param type
     * @return
     */
    public String selectByDeviceName(Integer deviceId, Integer type) {
        String deviceName = "";
        if (null != deviceId && null != type) {
            if (1 == type) {
                DeviceCollect collect = collectService.getById(deviceId);
                if (null != collect) {
                    deviceName = collect.getName();
                }
            } else {
                DeviceGateway gateway = gatewayService.getById(deviceId);
                if (null != gateway) {
                    deviceName = gateway.getName();
                }
            }
        }
        return deviceName;
    }


    /**
     * 获取所有区域
     *
     * @return
     */
    public List<SysBuildArea> selectAreaList(String name) {
        return buildAreaService.list(new LambdaQueryWrapper<SysBuildArea>().eq(SysBuildArea::getIsDel, 0).like(!StringUtils.isEmpty(name),SysBuildArea::getAreaName,name));
    }

    /**
     * 向任务项记录表中插入数据
     *
     * @param
     */
    public void missionItemRecordSave(Mission mission, Integer jobId) {
        mission.setCreateTime(new Date());
        if (missionService.save(mission)) {
            //根据检查项id获取当前的设备信息
            List<CheckTemplateSub> list = subService.list(new LambdaQueryWrapper<CheckTemplateSub>()
                    .eq(CheckTemplateSub::getCheckId, mission.getManageId()).eq(CheckTemplateSub::getIsDel, 0));
            List<JobPlanArea> areas = jobPlanAreaService.list(new LambdaQueryWrapper<JobPlanArea>()
                    .eq(JobPlanArea::getPlanId, mission.getSourceId()).isNotNull(JobPlanArea::getAreaId));
            for (CheckTemplateSub sub : list) {
                //根据区域id和设备类型获取相关的设备信息
                List<Integer> ls = areas.stream().filter(x -> null != x.getAreaId()).map(JobPlanArea::getAreaId).collect(Collectors.toList());
                List<DeviceCollect> deviceCollects = collectService
                        .list(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getDeviceType, sub.getDeviceType())
                                .in(DeviceCollect::getAreaId, ls));
                for (DeviceCollect a : deviceCollects) {
                    MissionItemRecord record = new MissionItemRecord();
                    record.setRegion(a.getAreaId());//区域id
                    record.setDeviceId(a.getId());//设备id
                    record.setDeviceName(a.getName());//设备名称
                    record.setDeviceType(1);//设备类型
                    record.setSubContent(sub.getSubContent());
                    record.setCreateTime(new Date());
                    record.setCheckTemplateId(sub.getCheckId());
                    record.setCheckTemplateSubId(a.getId());
                    record.setCreateTime(new Date());
                    record.setIsHandle(0);
                    record.setCreateUser(mission.getCreateUser());
                    record.setMissionId(mission.getId());
                    save(record);
                }
            }
            //获取指派的班组
            MissionAssign bean = new MissionAssign();
            bean.setMissionId(mission.getId());
            bean.setDeadline(mission.getDeadlineTime());
            bean.setCreateUser(mission.getUserId());
            assignService.jobPlanAssigned(bean, jobId);
        }
    }

    /**
     * 获取任务项记录
     *
     * @param mission
     * @return
     */
    public List<MissionRecordVo> missionItemRecordList(Mission mission) {
        List<MissionRecordVo> vos = new ArrayList<>();
        List<MissionItemRecord> list = recordMapper.selectByMissionIdGroup(mission.getId());
        for (MissionItemRecord record : list) {
            MissionRecordVo vo = new MissionRecordVo();
            vo.setId(record.getRegion());
            List<MissionItemRecord> ars = list(new LambdaQueryWrapper<MissionItemRecord>().eq(MissionItemRecord::getMissionId, mission.getId())
                    .eq(MissionItemRecord::getRegion, record.getRegion()));
            ars.forEach(a -> {
                List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, a.getId()+"").eq(SysFile::getBizType, "3"));
                a.setFiles(files);
            });
            if (null != record.getRegion()) {
                SysBuildArea area = buildAreaService.getById(record.getRegion());//区域id
                vo.setRegionName(area.getAreaName());
                vo.setRecords(ars);
            }
            vos.add(vo);
        }
        return vos;
    }


    /**
     * 获取任务项记录
     *
     * @param mission
     * @return
     */
    public Map<Integer, List<MissionRecordVo>> appMissionItemRecordList(Mission mission) {
        Map<Integer, List<MissionRecordVo>> maps = new HashMap<>();
        List<MissionRecordVo> vos = new ArrayList<>();
        List<MissionItemRecord> list = recordMapper.selectByMissionIdGroup(mission.getId());
        for (MissionItemRecord record : list) {
            MissionRecordVo vo = new MissionRecordVo();
            vo.setId(record.getRegion());
            List<MissionItemRecord> ars = list(new LambdaQueryWrapper<MissionItemRecord>().eq(MissionItemRecord::getMissionId, mission.getId())
                    .eq(MissionItemRecord::getRegion, record.getRegion()));
            ars.forEach(a -> {
                List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, a.getId()+"").eq(SysFile::getBizType, "3"));
                a.setFiles(files);
            });
            if (null != record.getRegion()) {
                SysBuildArea area = buildAreaService.getById(record.getRegion());//区域id
                vo.setRegionName(area.getAreaName());
                vo.setRecords(ars);

                SysBuildFloor floor = floorService.getById(area.getFloorId());
                vo.setFloorName(floor.getFloorName());
                vo.setFloorId(floor.getId());


            }
            vos.add(vo);
        }

        maps = vos.stream().collect(Collectors.groupingBy(MissionRecordVo::getFloorId));


        return maps;
    }

    public MissionItemRecord missionItemRecordApp(Integer id) {
        return recordMapper.selectById(id);
    }

    /**
     * 获取处理页面详细信息
     *
     * @param missionid
     * @return
     */
    public JSONObject handleDetailsApp(Integer missionid, Integer region, Integer detailsType) {
        JSONObject object = new JSONObject();
        List<MissionItemRecord> records = recordMapper.selectList(new LambdaQueryWrapper<MissionItemRecord>().eq(MissionItemRecord::getMissionId, missionid)
                .eq(MissionItemRecord::getRegion, region));
        if (!records.isEmpty() && records.size() > 0) {
            MissionItemRecord record = records.get(0);
            SysBuildArea area = buildAreaService.getById(record.getRegion());
            SysBuildFloor floor = floorService.getById(area.getFloorId());
            record.setRegionName(floor.getFloorName() + area.getAreaName());
            record.setDetailAddress(area.getDetailAddress());
            record.setPhones(area.getPhones());
            object.put("bean", record);
            if (null != detailsType) {
                records.forEach(a -> {
                    List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, a.getId()+"").eq(SysFile::getBizType, "5"));
                    a.setFiles(files);
                    if (null != a.getLevels()) {//级别
                        SysDict dict = missionService.returnDicst("warnLevel", a.getLevels());
                        if (StringUtils.isNotNull(dict)) {
                            a.setLevelsName(dict.getDictLabel());
                        }
                    }
                });
            }
        } else {
            object.put("bean", "");
        }
        object.put("subs", records);
        return object;
    }

    /**
     * 获取任务项记录
     *
     * @param mission
     * @return
     */
    public List<MissionRecordVo> appMissionItemRecordList1(Mission mission) {
        LinkedHashMap<Integer, List<MissionRecordVo>> maps = new LinkedHashMap<>();
        List<MissionRecordVo> vos = new ArrayList<>();
        List<MissionItemRecord> list = recordMapper.getRecordByMissionId(mission);
        for (MissionItemRecord record : list) {
            MissionRecordVo vo = new MissionRecordVo();
            vo.setId(record.getMissionId());
            vo.setIsHandle(record.getIsHandle());
            if (null != record.getRegion()) {
                SysBuildArea area = buildAreaService.getById(record.getRegion());//区域id
                vo.setDetailAddress(area.getDetailAddress());
                vo.setRegion(record.getRegion());
                vo.setRegionName(area.getAreaName());
                SysBuildFloor floor = floorService.getById(area.getFloorId());
                vo.setFloorName(floor.getFloorName());
                vo.setFloorId(floor.getId());
                vo.setSort(floor.getSort());
            }
            vos.add(vo);
        }

//        Collections.sort(vos,Comparator.comparing(MissionRecordVo::getSort));
//
//        // 对List进行分组，并保留原始顺序
//        maps = vos.stream()
//                .collect(Collectors.groupingBy(MissionRecordVo::getFloorId, LinkedHashMap<Integer,List<MissionRecordVo>>::new, Collectors.toList()));


        return vos;
    }

    /**
     * 统计占比
     *
     * @param mission
     * @return
     */
    public Integer percentageCount(Integer mission) {
        //总处理条书
        Integer count = recordMapper.selectMissionCount(mission, null);
        //已处理数据
        Integer ycl = recordMapper.selectMissionCount(mission, 1);
        if (0 == count && 0 == ycl) {
            return 0;
        } else {
            BigDecimal de = new BigDecimal(ycl).divide(new BigDecimal(count), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            return Integer.parseInt(new DecimalFormat("#0").format(de));
        }
    }
}
