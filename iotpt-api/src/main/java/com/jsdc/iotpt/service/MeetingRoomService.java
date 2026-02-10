package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.curtain.Curtain;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.util.QRCodeUtils;
import com.jsdc.iotpt.util.QrCoder;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.util.uuid.IdUtils;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import com.jsdc.iotpt.vo.MeetingReportVo;
import com.jsdc.iotpt.vo.MeetingRoomReservationVo;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会议室管理配置表(MeetingRoomConfig)表服务实现类
 *
 * @author zhangdequan
 * @since 2023-05-09
 */
@Service
@Slf4j
@Transactional
public class MeetingRoomService extends BaseService<MeetingRoomConfig> {

    Logger logger = LoggerFactory.getLogger(MeetingRoomService.class);

    private static String gateOpenUrl;

    @Value("${wx.cn}")
    public void setGateOpenUrl(String gateOpenUrl) {
        this.gateOpenUrl = gateOpenUrl;
    }

    @Autowired
    private MeetingRoomReservationMapper meetingRoomReservationMapper;

    @Autowired
    private MeetingRoomReservationUserMapper meetingRoomReservationUserMapper;

    @Autowired
    private MeetingRoomConfigMapper meetingRoomConfigMapper;

    @Autowired
    private MeetingSceneControlMapper sceneControlMapper;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;
    @Autowired
    private CurtainMapper curtainMapper;
    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MeetingSceneControlMapper meetingSceneControlMapper;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SysOrgDeptService sysOrgDeptService;
    @Autowired
    private MeetingRoomReservationServiceOptionsRelationService meetingRoomReservationServiceOptionsRelationService;
    @Autowired
    private MeetingRoomReservationServiceOptionsRelationMapper meetingRoomReservationServiceOptionsRelationMapper;

    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;
    @Autowired
    private SysBuildService sysBuildService;
    @Autowired
    private MeetingSceneService meetingSceneService;
    @Autowired
    private MultimediaMapper multimediaMapper;
    @Autowired
    private MeetingSceneControlService meetingSceneControlService;
    @Autowired
    private MeetingSceneMapper sceneMapper;
    @Autowired
    private SysFileMapper fileMapper;
    @Autowired
    private MeetingSceneDeviceInfoMapper sceneDeviceInfoMapper;
    @Autowired
    private AppPushMsgService appPushMsgService;

    /**
     * 新增会议室
     */
    public void addOrUpdateMeetingRoom(MeetingRoomConfig bean) {
        // 存在标识
        bean.setIsDel(G.ISDEL_NO);
        if (null == bean.getId() || null == getById(bean.getId())) {
            //新增
            bean.setRoomStatus(1);
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
            this.save(bean);
            saveSceneControl(bean, bean.getId());
        } else {
            //修改
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUserService.getUser().getId());
            this.updateById(bean);
            //删除原来的设备
            List<MeetingSceneControl> meetingSceneControls = sceneControlMapper.selectList(new LambdaQueryWrapper<MeetingSceneControl>().eq(MeetingSceneControl::getMeetingId, bean.getId()));
            if (CollUtil.isNotEmpty(meetingSceneControls)) {
                for (MeetingSceneControl meetingSceneControl : meetingSceneControls) {
                    meetingSceneControl.setIsDel(1);
                    sceneControlMapper.updateById(meetingSceneControl);
                }
            }
            //如果存在原来的多媒体设备，需要释放引用
            Multimedia multimedia = multimediaMapper.selectOne(new LambdaQueryWrapper<Multimedia>().eq(Multimedia::getMeetingId, bean.getId()));
            if (ObjUtil.isNotEmpty(multimedia)) {
                multimedia.setIsReference(0);
                multimedia.setMeetingId(0);
                multimediaMapper.updateById(multimedia);
            }
            //添加会议设备
            saveSceneControl(bean, bean.getId());

            //重新绑定场景和设备信息
            saveSceneControlInfo(bean);
        }
        SysFile file = bean.getFile();
        //先删除原数据
        fileMapper.update(null, Wrappers.<SysFile>lambdaUpdate()
                .eq(SysFile::getIsDel, 0).eq(SysFile::getBizId, bean.getId())
                .eq(SysFile::getBizType, "17")
                .set(SysFile::getIsDel, 1).set(SysFile::getUpdateTime, new Date())
                .set(SysFile::getUpdateUser, sysUserService.getUser().getId()));
        if (ObjUtil.isNotEmpty(file)) {
            file.setIsDel(0);
            file.setBizId(bean.getId().toString());
            file.setBizType("17");
            file.setCreateUser(sysUserService.getUser().getId());
            file.setCreateTime(new Date());
            fileMapper.insert(file);
        }
    }

    private void saveSceneControlInfo(MeetingRoomConfig bean) {
        Integer meetingId = bean.getId();
        List<Integer> airConditionerIds = bean.getAirConditioner();
        List<Integer> lightingIds = bean.getLighting();
        Integer multimediaId = bean.getMultimedia();
        List<Integer> inductionIds = bean.getInduction();
        //窗帘
        List<Integer> curtains = bean.getCurtain();

        List<MeetingSceneDeviceInfo> saveList = new ArrayList<>();
        List<MeetingSceneDeviceInfo> deleteList = new ArrayList<>();
        //获取会议室下所有场景
        List<MeetingScene> sceneList = sceneMapper.selectList(new LambdaQueryWrapper<MeetingScene>().eq(MeetingScene::getIsDel, 0).eq(MeetingScene::getMeetingId, meetingId));
        if (CollUtil.isEmpty(sceneList)) {
            return;
        }
        if (CollUtil.isEmpty(airConditionerIds)) {
            //删除会议室下场景对应的空调设备
            deleteSceneDeviceInfo(meetingId, 1);
        } else {
            for (Integer airConditionerId : airConditionerIds) {
                //获取会议下的空调
                List<MeetingSceneDeviceInfo> airConditioners = getSceneDeviceInfoList(meetingId, 1, airConditionerId, sceneList);
                if (CollUtil.isNotEmpty(airConditioners)) {
                    for (MeetingSceneDeviceInfo airConditioner : airConditioners) {
                        airConditioner.setCreateUser(sysUserService.getUser().getId());
                        airConditioner.setCreateTime(new Date());
                        airConditioner.setDeviceType(1);
                        airConditioner.setDeviceId(airConditionerId);
                        saveList.add(airConditioner);
                    }
                } else {
                    MeetingSceneDeviceInfo air;
                    for (MeetingScene meetingScene : sceneList) {
                        air = new MeetingSceneDeviceInfo();
                        air.setModelSetting(1);
                        air.setWindSpeedSetting(2);
                        air.setDeviceId(airConditionerId);
                        air.setTemperatureSetting(25);
                        air.setMeetingId(meetingId);
                        air.setDeviceType(1);
                        air.setIsOpen(0);
                        air.setSceneId(meetingScene.getId());
                        saveList.add(air);
                    }
                }
            }
        }
        if (CollUtil.isEmpty(lightingIds)) {
            //删除会议室下场景对应的照明设备
            deleteSceneDeviceInfo(meetingId, 2);
        } else {
            for (Integer lightingId : lightingIds) {
                //照明
                List<MeetingSceneDeviceInfo> lightings = getSceneDeviceInfoList(meetingId, 2, lightingId, sceneList);
                if (CollUtil.isNotEmpty(lightings)) {
                    for (MeetingSceneDeviceInfo lighting : lightings) {
                        lighting.setCreateUser(sysUserService.getUser().getId());
                        lighting.setCreateTime(new Date());
                        lighting.setDeviceId(lightingId);
                        saveList.add(lighting);
                    }
                } else {
                    MeetingSceneDeviceInfo light;
                    ChintCloudDevicePoint point = chintCloudDevicePointMapper.selectById(lightingId);
                    for (MeetingScene meetingScene : sceneList) {
                        light = new MeetingSceneDeviceInfo();
                        light.setMeetingId(meetingId);
                        light.setSceneId(meetingScene.getId());
                        light.setDeviceName(point.getPointName());
                        light.setDeviceType(2);
                        light.setDeviceId(lightingId);
                        light.setLuminance(100);
                        light.setColor("黄色");
                        saveList.add(light);
                    }
                }
            }
        }
        if (null == multimediaId) {
            //删除会议室下场景对应的多媒体设备
            deleteSceneDeviceInfo(meetingId, 3);
        } else {
            //多媒体
            List<MeetingSceneDeviceInfo> multimedias = getSceneDeviceInfoList(meetingId, 3, multimediaId, sceneList);
            if (CollUtil.isNotEmpty(multimedias)) {
                for (MeetingSceneDeviceInfo multimedia : multimedias) {
                    multimedia.setCreateUser(sysUserService.getUser().getId());
                    multimedia.setCreateTime(new Date());
                    multimedia.setDeviceId(multimediaId);
                    saveList.add(multimedia);
                }
            } else {
                MeetingSceneDeviceInfo multimedia1;
                Multimedia multimediaInfo = multimediaMapper.selectOne(new LambdaQueryWrapper<Multimedia>().eq(Multimedia::getMeetingId, multimediaId));
                if (ObjUtil.isNotEmpty(multimediaInfo)) {
                    for (MeetingScene meetingScene : sceneList) {
                        multimedia1 = new MeetingSceneDeviceInfo();
                        multimedia1.setMeetingId(meetingId);
                        multimedia1.setSceneId(meetingScene.getId());
                        multimedia1.setDeviceName(multimediaInfo.getName());
                        multimedia1.setDeviceId(multimediaId);
                        multimedia1.setDeviceType(3);
                        multimedia1.setIsOpen(0);
                        multimedia1.setMediaIp(multimediaInfo.getMediaIp());
                        saveList.add(multimedia1);
                    }
                }
            }
        }
        if (CollUtil.isEmpty(inductionIds)) {
            //删除会议室下场景对应的人体感应设备
            deleteSceneDeviceInfo(meetingId, 4);
        } else {
            for (Integer inductionId : inductionIds) {
                //人体感应
                List<MeetingSceneDeviceInfo> inductions = getSceneDeviceInfoList(meetingId, 4, inductionId, sceneList);
                if (CollUtil.isNotEmpty(inductions)) {
                    for (MeetingSceneDeviceInfo induction : inductions) {
                        induction.setCreateUser(sysUserService.getUser().getId());
                        induction.setCreateTime(new Date());
                        induction.setDeviceId(inductionId);
                        induction.setIsOpen(0);
                        saveList.add(induction);
                    }
                } else {
                    MeetingSceneDeviceInfo induction1;
                    DeviceCollect deviceCollect = deviceCollectMapper.selectById(inductionId);
                    for (MeetingScene meetingScene : sceneList) {
                        induction1 = new MeetingSceneDeviceInfo();
                        induction1.setDeviceId(inductionId);
                        induction1.setMeetingId(meetingId);
                        induction1.setIsOpen(0);
                        induction1.setSceneId(meetingScene.getId());
                        induction1.setDeviceType(4);
                        induction1.setDeviceName(deviceCollect.getName());
                        saveList.add(induction1);
                    }
                }
            }
        }
        if (CollUtil.isEmpty(curtains)) {
            //删除会议室下场景对应的窗帘设备
            deleteSceneDeviceInfo(meetingId, 5);
        } else {
            for (Integer curtainId : curtains) {
                //窗帘设备
                List<MeetingSceneDeviceInfo> curtainList = getSceneDeviceInfoList(meetingId, 5, curtainId, sceneList);
                if (CollUtil.isNotEmpty(curtainList)) {
                    for (MeetingSceneDeviceInfo curtain : curtainList) {
                        curtain.setCreateUser(sysUserService.getUser().getId());
                        curtain.setCreateTime(new Date());
                        curtain.setDeviceId(curtainId);
                        curtain.setIsOpen(0);
                        saveList.add(curtain);
                    }
                } else {
                    MeetingSceneDeviceInfo curtain;
                    Curtain curtain1 = curtainMapper.selectById(curtainId);
                    for (MeetingScene meetingScene : sceneList) {
                        curtain = new MeetingSceneDeviceInfo();
                        curtain.setDeviceId(curtainId);
                        curtain.setMeetingId(meetingId);
                        curtain.setSceneId(meetingScene.getId());
                        curtain.setDeviceType(5);
                        curtain.setIsOpen(0);
                        if (curtain1.getGeneralControl() == 1) {
                            curtain.setIsGeneralControl(1);
                        } else {
                            curtain.setIsGeneralControl(0);
                        }
                        curtain.setDeviceName(curtain1.getCurtainName());
                        curtain.setCurtainIp(curtain1.getIp());
                        curtain.setGeneralControl(3);
                        saveList.add(curtain);
                    }
                }
            }
        }
        //删除原有绑定场景设备方案
        List<MeetingSceneDeviceInfo> list = sceneDeviceInfoMapper.selectList(new LambdaQueryWrapper<MeetingSceneDeviceInfo>()
                .eq(MeetingSceneDeviceInfo::getIsDel, 0)
                .eq(MeetingSceneDeviceInfo::getMeetingId, meetingId));
        if (CollUtil.isNotEmpty(list)) {
            for (MeetingSceneDeviceInfo sceneDeviceInfo : list) {
                sceneDeviceInfo.setIsDel(1);
                sceneDeviceInfo.setUpdateTime(new Date());
                sceneDeviceInfo.setUpdateUser(sysUserService.getUser().getId());
                sceneDeviceInfoMapper.updateById(sceneDeviceInfo);
            }
        }

        //将数据初始化
        for (MeetingSceneDeviceInfo sceneDeviceInfo : saveList) {
            sceneDeviceInfo.setId(IdUtils.fastSimpleUUID());
            sceneDeviceInfo.setIsDel(0);
            sceneDeviceInfo.setCreateTime(new Date());
            sceneDeviceInfo.setCreateUser(sysUserService.getUser().getId());
            sceneDeviceInfoMapper.insert(sceneDeviceInfo);
        }

    }

    private void deleteSceneDeviceInfo(Integer meetingId, Integer deviceType) {
        List<MeetingSceneDeviceInfo> list = sceneDeviceInfoMapper.selectList(new LambdaQueryWrapper<MeetingSceneDeviceInfo>().eq(MeetingSceneDeviceInfo::getIsDel, 0).eq(MeetingSceneDeviceInfo::getMeetingId, meetingId).eq(MeetingSceneDeviceInfo::getDeviceType, deviceType));
        if (CollUtil.isNotEmpty(list)) {
            for (MeetingSceneDeviceInfo sceneDeviceInfo : list) {
                sceneDeviceInfo.setIsDel(1);
                sceneDeviceInfo.setUpdateTime(new Date());
                sceneDeviceInfo.setUpdateUser(sysUserService.getUser().getId());
                sceneDeviceInfoMapper.updateById(sceneDeviceInfo);
            }
        }
    }


    private List<MeetingSceneDeviceInfo> getSceneDeviceInfoList(Integer meetingId, Integer deviceType, Integer deviceId, List<MeetingScene> sceneList) {
        List<MeetingSceneDeviceInfo> list = new ArrayList<>();
        if (CollUtil.isEmpty(sceneList)) {
            return list;
        }

        for (MeetingScene scene : sceneList) {
            List<MeetingSceneDeviceInfo> deviceInfos = sceneDeviceInfoMapper.selectList(new LambdaQueryWrapper<MeetingSceneDeviceInfo>()
                    .eq(MeetingSceneDeviceInfo::getIsDel, 0)
                    .eq(MeetingSceneDeviceInfo::getMeetingId, meetingId)
                    .eq(MeetingSceneDeviceInfo::getDeviceType, deviceType)
                    .eq(MeetingSceneDeviceInfo::getDeviceId, deviceId)
                    .eq(MeetingSceneDeviceInfo::getSceneId, scene.getId())
            );
            if (CollUtil.isNotEmpty(deviceInfos)) {
                list.add(deviceInfos.get(0));
            }
        }
        return list;
    }

    //添加会议室设备
    private void saveSceneControl(MeetingRoomConfig bean, Integer meetingId) {
        //空调
        List<Integer> airConditionerIds = bean.getAirConditioner();
        //照明
        List<Integer> lightingIds = bean.getLighting();
        //多媒体
        List<Integer> multimediaIds = null;
        if (null != bean.getMultimedia()) {
            multimediaIds = Arrays.asList(bean.getMultimedia());
        }
        //感应设备
        List<Integer> inductionIds = bean.getInduction();
        List<MeetingSceneControl> saveList = new ArrayList<>();
        //空调
        if (CollUtil.isNotEmpty(airConditionerIds)) {
            saveList = initSceneController(bean, saveList, airConditionerIds, meetingId, 1);
        }
        //照明
        if (CollUtil.isNotEmpty(lightingIds)) {
            saveList = initSceneController(bean, saveList, lightingIds, meetingId, 2);
        }
        //多媒体
        if (CollUtil.isNotEmpty(multimediaIds)) {
            saveList = initSceneController(bean, saveList, multimediaIds, meetingId, 3);
        }
        //感应设备
        if (CollUtil.isNotEmpty(inductionIds)) {
            saveList = initSceneController(bean, saveList, inductionIds, meetingId, 4);
        }
        List<Integer> curtains = bean.getCurtain();
        //窗帘设备
        if (CollUtil.isNotEmpty(curtains)) {
            saveList = initSceneController(bean, saveList, curtains, meetingId, 5);
        }
        saveList.forEach(x -> sceneControlMapper.insert(x));
    }

    //新增方法封装
    private List<MeetingSceneControl> initSceneController(MeetingRoomConfig bean, List<MeetingSceneControl> saveList, List<Integer> ids, Integer meetingId, Integer deviceType) {
        //如果是多媒体设备，更新设备装备已被引用。在获取设备列表时，就不会展示此设备
        if (3 == deviceType) {
            Multimedia multimedia = multimediaMapper.selectById(ids.get(0));
            multimedia.setIsReference(1);
            multimedia.setMeetingId(bean.getId());
            multimediaMapper.updateById(multimedia);
        }
        MeetingSceneControl sceneControl;
        for (Integer id : ids) {
            sceneControl = new MeetingSceneControl();
            sceneControl.setMeetingId(meetingId);
            sceneControl.setDeviceId(id);
            sceneControl.setIsDel(0);
            sceneControl.setDeviceType(deviceType);
            sceneControl.setCreateTime(new Date());
            sceneControl.setCreateUser(sysUserService.getUser().getId());
            saveList.add(sceneControl);
        }
        return saveList;
    }


    /**
     * 删除会议室
     */
    public void delMeetingRoom(Integer id) {
        // 删除会议室关联的预约
        meetingRoomReservationMapper.update(null, Wrappers.<MeetingRoomReservation>lambdaUpdate()
                .set(MeetingRoomReservation::getIsDel, G.ISDEL_YES)
                .eq(MeetingRoomReservation::getRoomId, id)
        );
        // 删除会议室
        this.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate()
                .set(MeetingRoomConfig::getIsDel, G.ISDEL_YES)
                .eq(MeetingRoomConfig::getId, id)
        );
        //如果会议室存在设备，需要逻辑删除对应的设备
        List<MeetingSceneControl> meetingSceneControls = sceneControlMapper.selectList(new LambdaQueryWrapper<MeetingSceneControl>().eq(MeetingSceneControl::getMeetingId, id).eq(MeetingSceneControl::getIsDel, 0));
        if (CollUtil.isNotEmpty(meetingSceneControls)) {
            for (MeetingSceneControl meetingSceneControl : meetingSceneControls) {
                meetingSceneControl.setIsDel(1);
                sceneControlMapper.updateById(meetingSceneControl);
            }
        }
        //删除会议之后，如果存在引用多媒体设备。需要进行解绑
        Multimedia multimedia = multimediaMapper.selectOne(new LambdaQueryWrapper<Multimedia>().eq(Multimedia::getMeetingId, id));
        if (ObjUtil.isNotEmpty(multimedia)) {
            multimedia.setIsReference(0);
            multimedia.setMeetingId(0);
            multimediaMapper.updateById(multimedia);
        }
    }

    private void handleRoomFile(MeetingRoomConfig bean) {
        if (ObjUtil.isNotEmpty(bean)) {
            List<SysFile> sysFiles = fileMapper.selectList(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizType, "17").eq(SysFile::getIsDel, 0)
                    .eq(SysFile::getBizId, bean.getId()));
            if (CollUtil.isNotEmpty(sysFiles)) {
                bean.setFile(sysFiles.get(0));
            }
        }
    }

    /**
     * 查询会议室列表分页
     */
    public Page<MeetingRoomConfig> getMeetingRoomPage(MeetingRoomConfig bean, Integer pageIndex, Integer pageSize) {
        Page<MeetingRoomConfig> page = this.page(new Page<>(pageIndex, pageSize), Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, G.ISDEL_NO)
                .like(StringUtils.isNotBlank(bean.getRoomName()), MeetingRoomConfig::getRoomName, bean.getRoomName())
                // 楼层
                .eq(null != bean.getAreaId(), MeetingRoomConfig::getAreaId, bean.getAreaId())
                .eq(null != bean.getFloorId(), MeetingRoomConfig::getFloorId, bean.getFloorId())
                // 类型
                .eq(null != bean.getRoomType(), MeetingRoomConfig::getRoomType, bean.getRoomType())
                // 状态
                .eq(null != bean.getRoomStatus(), MeetingRoomConfig::getRoomStatus, bean.getRoomStatus())
                .orderByDesc(MeetingRoomConfig::getCreateTime)
        );
        for (MeetingRoomConfig meetingRoomConfig : page.getRecords()) {
            if (StringUtils.isNotEmpty(meetingRoomConfig.getCurrentMode()) && !Objects.equals("null", meetingRoomConfig.getCurrentMode())) {
                meetingRoomConfig.setCurrentModeStr(
                        meetingSceneService.getById(Integer.valueOf(meetingRoomConfig.getCurrentMode()))
                                .getSceneName()
                );
            }
            Long count = meetingSceneControlMapper.selectCount(new LambdaQueryWrapper<MeetingSceneControl>()
                    .eq(MeetingSceneControl::getIsDel, 0)
                    .eq(MeetingSceneControl::getDeviceType, 5)
                    .eq(MeetingSceneControl::getMeetingId, meetingRoomConfig.getId())
            );
            meetingRoomConfig.setCurtainCount(count);
            meetingRoomConfig.setRoomDeviceType(meetingSceneControlService.getDeviceListByRoomId(meetingRoomConfig.getId()));
            //返回文件处理
            handleRoomFile(meetingRoomConfig);
        }
        return page;
    }

    /**
     * 根据id查询会议室
     */
    public MeetingRoomConfig getMeetingRoomById(Integer id) {
        MeetingRoomConfig meetingRoomConfig = this.getById(id);
        // 查询会议室预约人
        SysUser createUser = sysUserService.getUser(SysUser.builder().id(meetingRoomConfig.getCreateUser()).build());
        meetingRoomConfig.setCreateUserName(createUser.getRealName());
        //照明
        meetingRoomConfig.setLighting(getDeviceIds(id, 2));
        //空调
        meetingRoomConfig.setAirConditioner(getDeviceIds(id, 1));
        //多媒体
        if (CollUtil.isNotEmpty(getDeviceIds(id, 3))) {
            meetingRoomConfig.setMultimedia(getDeviceIds(id, 3).get(0));
        }
        //人体感应设备
        meetingRoomConfig.setInduction(getDeviceIds(id, 4));
        meetingRoomConfig.setCurtain(getDeviceIds(id, 5));
        //返回文件处理
        handleRoomFile(meetingRoomConfig);
        return meetingRoomConfig;
    }

    private List<Integer> getDeviceIds(Integer id, Integer type) {
        //获取对应设备id
        return sceneControlMapper.selectList(new LambdaQueryWrapper<MeetingSceneControl>()
                .eq(MeetingSceneControl::getMeetingId, id)
                .eq(MeetingSceneControl::getDeviceType, type)
                .eq(MeetingSceneControl::getIsDel, 0)).stream().map(MeetingSceneControl::getDeviceId).collect(Collectors.toList());
    }

    //获取人体感应设备列表
    private List<DeviceCollect> getInductionList(List<Integer> ids) {
        List<DeviceCollect> deviceCollects = new ArrayList<>();
        for (Integer id : ids) {
            deviceCollects.add(deviceCollectMapper.selectById(id));
        }
        return deviceCollects;
    }

    //获取照明设备列表
    private List<ChintCloudDevicePoint> getLightingList(List<Integer> ids) {
        List<ChintCloudDevicePoint> cloudDevicePoints = new ArrayList<>();
        for (Integer id : ids) {
            cloudDevicePoints.add(chintCloudDevicePointMapper.selectById(id));
        }
        return cloudDevicePoints;
    }

    /**
     * 今日会议预约一览
     *
     * @param pageIndex
     * @param pageSize
     * @param reservation
     * @return
     */
    public Page<MeetingRoomReservation> getTodayReservation(int pageIndex, int pageSize, MeetingRoomReservation reservation) {
        Date currtentDate = new Date();
        return meetingRoomReservationMapper.selectPage(new Page<>(pageIndex, pageSize),
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getRoomId, reservation.getRoomId())
                        .le(MeetingRoomReservation::getStartTime, DateUtils.getStartTimeOfCurrentDay(currtentDate))
                        .ge(MeetingRoomReservation::getEndTime, DateUtils.getEndTimeOfCurrentDay(currtentDate))
                        .orderByDesc(MeetingRoomReservation::getStartTime)
        );
    }

    /**
     * 今日会议预约一览  屏幕用
     *
     * @param pageIndex
     * @param pageSize
     * @param roomId
     * @return
     */
    public Page<MeetingRoomReservation> getTodayReservation(int pageIndex, int pageSize, int roomId) {

        updateMeetingRoomOrderStatus();

        List<SysUser> users = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, G.ISDEL_NO));
        Map<Integer, SysUser> map = new HashMap<>();
        for (SysUser u : users) {
            map.put(u.getId(), u);
        }

        Date currtentDate = new Date();
        Page<MeetingRoomReservation> page = meetingRoomReservationMapper.selectPage(new Page<>(pageIndex, pageSize),
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getIsDel, 0)
                        .eq(MeetingRoomReservation::getRoomId, roomId)
                        .ge(MeetingRoomReservation::getStartTime, DateUtils.getStartTimeOfCurrentDay(currtentDate))
                        .le(MeetingRoomReservation::getEndTime, DateUtils.getEndTimeOfCurrentDay(currtentDate))
                        .orderByAsc(MeetingRoomReservation::getStartTime)
        );

        //数据赋值
        for (MeetingRoomReservation mrr : page.getRecords()) {
            if (null == mrr.getReservationPerson()) {
                mrr.setReservationPersonName("");
            }
            SysUser su = map.get(mrr.getReservationPerson());
            if (null == su) {
                mrr.setReservationPersonName("");
            } else {
                mrr.setReservationPersonName(StringUtils.trimToEmpty(su.getRealName()));
            }
        }
        List<MeetingRoomReservation> list = page.getRecords();
        Map<Integer, Integer> customOrder = new HashMap<>();
        customOrder.put(2, 1);
        customOrder.put(1, 2);
        customOrder.put(3, 3);

        // 排序
        list.sort(Comparator.comparing((MeetingRoomReservation e) -> customOrder.getOrDefault(e.getRoomStatus(), Integer.MAX_VALUE))
                .thenComparing(MeetingRoomReservation::getStartTime));


        return page;
    }

    /**
     * 根据会议室id 获取最近的一个会议 获取参会人员信息 签到  未签到
     *
     * @param roomId
     * @return
     */
    public Map<String, Object> getCurrenReservation(Integer roomId) {
        Date currtentDate = new Date();
        Map<String, Object> reservationMap = new HashMap<>();
        reservationMap.put("qrCode", "");

        List<SysUser> users = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, G.ISDEL_NO));
        Map<Integer, SysUser> map = new HashMap<>();
        for (SysUser u : users) {
            map.put(u.getId(), u);
        }

        //预约信息
        //MeetingRoomReservation reservation = meetingRoomReservationMapper.selectById(meetingId);
        //获取当天的所有预约
        List<MeetingRoomReservation> reservations = meetingRoomReservationMapper.selectList(
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getIsDel, 0)
                        .eq(MeetingRoomReservation::getRoomId, roomId)
                        // .ge(MeetingRoomReservation::getEndTime, DateUtils.format(currtentDate))
                        .ge(MeetingRoomReservation::getStartTime, DateUtils.getStartTimeOfCurrentDay(currtentDate))
                        .le(MeetingRoomReservation::getEndTime, DateUtils.getEndTimeOfCurrentDay(currtentDate))
                        .orderByAsc(MeetingRoomReservation::getStartTime)
        );
        //获取当天的所有预约
        if (null == reservations || reservations.isEmpty()) {
            //如果当天没有预约
            reservationMap.put("state", 0);
            reservationMap.put("shouldCheckedIn", 0);
            reservationMap.put("checkedIn", 0);
            reservationMap.put("notCheckedIn", 0);
            reservationMap.put("currentReservation", null);
            return reservationMap;
        } else {
            //有预约要取最近一个开始或者正在进行的
            MeetingRoomReservation reservation = reservation = reservations.get(0);

            boolean falg = false;
            for (MeetingRoomReservation mRoom : reservations) {
                if (DateUtils.parseDate(mRoom.getEndTime()).after(currtentDate)) {
                    reservation = mRoom;
                    falg = true;
                    break;
                }
            }

            if (!falg) {
                //会议都结束展示最后一个
                reservation = reservations.get(reservations.size() - 1);
            }
            // MeetingRoomReservation reservation = reservations.get(0);
            SysUser su = map.get(reservation.getReservationPerson());
            reservation.setReservationPersonName(StringUtils.trimToEmpty(su.getRealName()));
            // 翻译 场景
            reservation.setSceneTypeStr(
                    reservation.getSceneType() == null ? "" :
                            meetingSceneService.getById(Integer.valueOf(reservation.getSceneType())).getSceneName()
//                            sysDictService.selectByValueAndType("sceneType", reservation.getSceneType()).getDictLabel()
            );
            reservationMap.put("currentReservation", reservation);
            List<MeetingRoomReservationUser> reservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery().eq(MeetingRoomReservationUser::getReservationId, reservation.getId()));
            //已签到
            Integer checkedIn = 0;
            //未签到
            Integer notCheckedIn = 0;
            //应签到
            reservationMap.put("shouldCheckedIn", reservationUsers.size());
            reservationMap.put("state", 1);
            for (MeetingRoomReservationUser u : reservationUsers) {
                if (u.getStatus() == 0) {
                    checkedIn = checkedIn + 1;
                } else {
                    notCheckedIn = notCheckedIn + 1;
                }
            }

            reservationMap.put("checkedIn", checkedIn);
            reservationMap.put("notCheckedIn", notCheckedIn);

            String url = gateOpenUrl + "/meeting/sign.do?id=" + reservation.getId();
            reservationMap.put("qrCode", QRCodeUtils.generateQRCodeAsBase64(url));
            return reservationMap;
        }
    }

    /**
     * 获取今天8点到17点会议室状态
     *
     * @param roomId
     * @return
     */
    public JSONArray getTodayListStatus(Integer roomId) {
        JSONArray array = new JSONArray();
        Date currtentDate = new Date();
        List<MeetingRoomReservation> mrrList = meetingRoomReservationMapper.selectList(
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getIsDel, 0)
                        .eq(MeetingRoomReservation::getRoomId, roomId)
                        .ge(MeetingRoomReservation::getStartTime, DateUtils.getStartTimeOfCurrentDay(currtentDate))
                        .le(MeetingRoomReservation::getEndTime, DateUtils.getEndTimeOfCurrentDay(currtentDate))
                        .orderByAsc(MeetingRoomReservation::getStartTime)
        );

        for (int i = 0; i <= 23; i++) {
            Calendar s = Calendar.getInstance();
            s.set(Calendar.HOUR_OF_DAY, i);
            s.set(Calendar.MINUTE, 0);
            s.set(Calendar.SECOND, 0);
            s.set(Calendar.MILLISECOND, 0);

            Calendar e = Calendar.getInstance();
            e.set(Calendar.HOUR_OF_DAY, i);
            e.set(Calendar.SECOND, 59);
            e.set(Calendar.MINUTE, 59);
            e.set(Calendar.MILLISECOND, 999);

            JSONObject json = new JSONObject();
            json.put("type", "2");
            json.put("name", i + ":00");
            json.put("imgurl", "../../static/icon_bg_2.png");

            if (i < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                json.put("type", "1");
                json.put("name", i + ":00");
                json.put("imgurl", "../../static/icon_bg_1.png");
            }

            for (MeetingRoomReservation mrr : mrrList) {
                Date s2 = DateUtils.parseDate(mrr.getStartTime());
                Date e2 = DateUtils.parseDate(mrr.getEndTime());

                // 翻译 场景
                mrr.setSceneTypeStr(
                        mrr.getSceneType() == null ? "" :
                                meetingSceneService.getById(Integer.valueOf(mrr.getSceneType())).getSceneName()
//                                sysDictService.selectByValueAndType("sceneType", mrr.getSceneType()).getDictLabel()
                );
                if (DateUtils.doTimeRangesOverlap(s2, e2, s.getTime(), e.getTime())) {
                    if (2 == mrr.getRoomStatus()) {
                        //stateVal = "进行中" typeVal="3"
                        json.put("type", "3");
                        json.put("name", i + ":00");
                        json.put("imgurl", "../../static/icon_bg_3.png");
                    } else if (3 == mrr.getRoomStatus()) {
                        //stateVal = "已结束" typeVal="1"
                        json.put("type", "1");
                        json.put("name", i + ":00");
                        json.put("imgurl", "../../static/icon_bg_1.png");
                    } else if (1 == mrr.getRoomStatus()) {
                        //stateVal = "已预约" typeVal="4"
                        json.put("type", "4");
                        json.put("name", i + ":00");
                        json.put("imgurl", "../../static/icon_bg_4.png");
                    }
                }

            }
            array.add(json);
        }
        //LocalDateTime start1 = LocalDateTime.of(2023, 1, 1, 8, 0,0);
        return array;
    }


    /**
     * 会议室总量、小会议室、大会议室、多功能会议室
     */
    public Long getMeetingRoomCount(MeetingRoomConfig bean) {
        Long num = meetingRoomConfigMapper.selectCount(Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, G.ISDEL_NO)
                .eq(StringUtils.isNotNull(bean.getRoomType()), MeetingRoomConfig::getRoomType, bean.getRoomType())
                .eq(StringUtils.isNotNull(bean.getRoomStatus()), MeetingRoomConfig::getRoomStatus, bean.getRoomStatus())
        );
        return num;
    }

    /**
     * 会议室部门使用情况
     */
    public ResultInfo floorReport(MeetingReportVo vo) {
        List<MeetingReportVo> list = meetingRoomConfigMapper.deptReport(vo);
        List<String> nameList = new ArrayList<>();
        List<Integer> valueList = new ArrayList<>();
        List<Integer> valueList2 = new ArrayList<>();
        List<Integer> valueList3 = new ArrayList<>();
        for (MeetingReportVo vo1 : list) {
            nameList.add(vo1.getName());
            valueList.add(vo1.getValue1());
            valueList2.add(vo1.getValue2());
            valueList3.add(vo1.getValue3());
        }
        JSONObject object = new JSONObject();
        object.put("nameList", nameList);
        object.put("valueList", valueList);
        object.put("valueList2", valueList2);
        object.put("valueList3", valueList3);
        return ResultInfo.success(object);
    }

    /**
     * 会议室使用频率
     */
    public List<MeetingReportVo> useCountReport(MeetingReportVo vo) {
        return meetingRoomReservationMapper.meetingRoomFrequency(vo);
    }

    /**
     * 会议室使用趋势分析
     */
    public List<MeetingReportVo> useTrendsReport(MeetingReportVo vo) {
        return meetingRoomReservationMapper.useTrendsReport(vo);
    }

    /**
     * 会议室使用趋势分析
     */
    public ResultInfo getUseTrendsReport(MeetingReportVo vo) {
        //总会议室数量
        Long num = getMeetingRoomCount(new MeetingRoomConfig());

        List<String> nameList = new ArrayList<>();
        List<Integer> valueList = new ArrayList<>();
        List<Integer> valueList2 = new ArrayList<>();
        List<Integer> valueList3 = new ArrayList<>();

        String date = vo.getStartTime();
        int day = Integer.parseInt(new DateTime(vo.getEndTime()).toString("dd"));
        for (int i = 0; i < day; i++) {
            if (i < 9) {
                vo.setStartTime(new DateTime(date).toString("yyyy-MM-0" + (i + 1) + " 00:00:00"));
                vo.setEndTime(new DateTime(date).toString("yyyy-MM-0" + (i + 1) + " 23:59:59"));

                nameList.add(new DateTime(date).toString("MM-0" + (i + 1)));
            } else {
                vo.setStartTime(new DateTime(date).toString("yyyy-MM-" + (i + 1) + " 00:00:00"));
                vo.setEndTime(new DateTime(date).toString("yyyy-MM-" + (i + 1) + " 23:59:59"));

                nameList.add(new DateTime(date).toString("MM-" + (i + 1)));
            }
            System.out.println("start=========" + vo.getStartTime());
            System.out.println("end===========" + vo.getEndTime());

            List<MeetingReportVo> list = meetingRoomReservationMapper.useTrendsReport(vo);

            Integer value = Integer.parseInt(String.valueOf(num)) - list.size();

            Integer count = 0;
            for (MeetingReportVo vo2 : list) {
                count = count + vo2.getValue();
            }

            valueList.add(list.size());//使用
            valueList2.add(value);//空闲
            valueList3.add(count);//会议数量
        }

        JSONObject object = new JSONObject();
        object.put("nameList", nameList);
        object.put("valueList", valueList);
        object.put("valueList2", valueList2);
        object.put("valueList3", valueList3);
        return ResultInfo.success(object);
    }

    /**
     * 新增/编辑会议室预约
     */
    public void addOrUpdateMeetingRoomOrder(MeetingRoomReservation bean) {
        if (StringUtils.isBlank(bean.getUserIds())) {
            throw new CustomException("请选择预约人员");
        }
        if (null == bean.getRoomId()) {
            throw new CustomException("请选择会议室");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime appointmentDateTime = LocalDateTime.parse(bean.getStartTime(), formatter);
        // 结束时间
        LocalDateTime appointmentEndTime = LocalDateTime.parse(bean.getEndTime(), formatter);
        if (appointmentDateTime.isAfter(appointmentEndTime)) {
            throw new CustomException("开始时间不能大于结束时间！");
        }
        // 获取系统当前时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 判断预约时间是否已经过去
        if (currentDateTime.isAfter(appointmentDateTime)) {
            throw new CustomException("无法预约，预约时间已经过去！");
        }
        // 处理预约的开始时间和结束时间 如:2023-10-26 8:30 转为 2023-10-26 08:30
        if (StringUtils.isNotBlank(bean.getStartTime()) && bean.getStartTime().length() == 15) {
            bean.setStartTime(bean.getStartTime().substring(0, 10) + " 0" + bean.getStartTime().substring(11));
        }
        if (StringUtils.isNotBlank(bean.getEndTime()) && bean.getEndTime().length() == 15) {
            bean.setEndTime(bean.getEndTime().substring(0, 10) + " 0" + bean.getEndTime().substring(11));
        }

        // 查询会议室预约时间是否冲突
        Long reCount = meetingRoomReservationMapper.selectCount(Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
                // 如果id存在是编辑，编辑查询时应排除掉自己
                .ne(!StringUtils.isBlank(bean.getId()), MeetingRoomReservation::getId, bean.getId())
                .eq(MeetingRoomReservation::getRoomId, bean.getRoomId())
                .le(MeetingRoomReservation::getStartTime, bean.getEndTime())
                .ge(MeetingRoomReservation::getEndTime, bean.getStartTime())
        );
        if (reCount > 0) {
            throw new CustomException("会议室预约时间冲突");
        }
        // 当前登陆人
        SysUser sysUser = sysUserService.getUser();

        // 0902 计划任务 15楼的会议室只能总经办和行政部才能预约 配置为字典
        if (sysDictService.compareDictValueIsContains("15FMeetingRoom",String.valueOf(bean.getRoomId()))){
            if (!sysDictService.compareDictValueIsContains("15FMeetingRoom_Dept",String.valueOf(sysUser.getDeptId()))){
                throw new CustomException("无权限预约此会议室");
            }
        }

        // 存在标识
        bean.setIsDel(G.ISDEL_NO);
        // 预约人
        bean.setReservationPerson(sysUser.getId());
        // 会议模式
        if (StringUtils.isBlank(bean.getSceneType()) || Objects.equals("null", bean.getSceneType())) {
            bean.setSceneType("");
        }
        // 会议提前准备时间
        if (StringUtils.isBlank(bean.getAdvanceStartTime()) || Objects.equals("null", bean.getAdvanceStartTime())) {
            bean.setAdvanceStartTime("0");
        } else {
            // 判断提前准备时间是否已经过去
            if (currentDateTime.plusMinutes(Integer.parseInt(bean.getAdvanceStartTime())).isAfter(appointmentDateTime)) {
                System.out.println("无法预约，场景提前准备时间不足！");
                throw new CustomException("无法预约，场景提前准备时间不足！");
            }
        }
        if (StringUtils.isBlank(bean.getId()) || null == meetingRoomReservationMapper.selectById(bean.getId())) {
            //新增
            bean.setId(IdUtil.fastSimpleUUID());
            bean.setRoomStatus(1);
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUser.getId());
            meetingRoomReservationMapper.insert(bean);
        } else {
            MeetingRoomReservation m = meetingRoomReservationMapper.selectById(bean.getId());
            if (m.getRoomStatus() != 1) {
                throw new CustomException("当前时间已无法编辑会议！");
            }
            if (!Objects.equals(m.getReservationPerson(), sysUser.getId())) {
                throw new CustomException("当前会议不是您创建的，无法编辑！");
            }
            //修改
            bean.setUpdateTime(new Date());
            meetingRoomReservationMapper.updateById(bean);
        }

        // 修改关联人员 String 转 List
        String[] userIds = bean.getUserIds().split(",");
        // 去重
        Set<Integer> userIdList = new HashSet<>();
        for (String userId : userIds) {
            userIdList.add(Integer.parseInt(userId));
        }
        // 删除关联人员
        meetingRoomReservationUserMapper.delete(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                .eq(MeetingRoomReservationUser::getReservationId, bean.getId())
        );

        // 新增关联人员
        for (Integer userId : userIdList) {
            //因为会事务冲突 故注释此行代码 采用下面的方式 ById
//            SysUser user = sysUserService.getUser(SysUser.builder().id(userId).build());
            SysUser user = sysUserService.getBaseMapper().selectById(userId);
            MeetingRoomReservationUser meetingRoomReservationUser = MeetingRoomReservationUser.builder()
                    .id(IdUtil.fastSimpleUUID())
                    .reservationId(bean.getId())
                    .userId(userId)
                    .userName(user.getRealName())
                    .deptId(user.getDeptId())
                    .status(1)
                    .build();
            meetingRoomReservationUserMapper.insert(meetingRoomReservationUser);
        }
        try {
            List<Integer> list = new ArrayList<>(userIdList);
            // APP推送 参会人员参会通知
            AppPushMsgVo appPushMsgVo = new AppPushMsgVo();
            appPushMsgVo.setTime(bean.getStartTime());
            appPushMsgVo.setName(meetingRoomConfigMapper.selectById(bean.getRoomId()).getRoomName());
            appPushMsgVo.setProjectName(bean.getMeetingTheme());
            appPushMsgVo.setUserIdList(list);
            appPushMsgVo.setAppConfigCode("SYSTEM_ATTEND_MEETING");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", bean.getId());
            appPushMsgVo.setCustomParameters(jsonObject.toJSONString());
            appPushMsgService.pushMsg(appPushMsgVo);
        } catch (Exception e) {
            log.error("APP推送 参会人员参会通知失败", e);
        }


        // 删除关联服务
        meetingRoomReservationServiceOptionsRelationService.getBaseMapper().delete(Wrappers.<MeetingServiceRelation>lambdaQuery()
                .eq(MeetingServiceRelation::getMeetingRoomReservationId, bean.getId())
        );

        // 插入中间表
        if (null != bean.getServiceOptionIdList() && !Objects.equals("", bean.getServiceOptionIdList())) {
            ArrayList<MeetingServiceRelation> l = new ArrayList<>();
            List<Integer> list = Arrays.stream(bean.getServiceOptionIdList().split(",")).map(Integer::parseInt).collect(Collectors.toList());
            for (Integer i : list) {
                MeetingServiceRelation m = new MeetingServiceRelation();
                m.setMeetingRoomReservationId(bean.getId());
                m.setServiceOptionsId(i);
                meetingRoomReservationServiceOptionsRelationService.save(m);
                l.add(m);
            }
//            MeetingRoomConfig meetingRoomConfig = meetingRoomConfigMapper.selectById(bean.getRoomId());
            //发短信 公安需要发短信
//            try {
//                l.forEach(x->{
//                    ServiceOptions serviceOptions = serviceOptionsService.getBaseMapper().selectById(x.getServiceOptionsId());
//                    SysUser user = sysUserService.getById(serviceOptions.getServicePerson());
////                    String cont = user.getRealName()+"您好，"+sysUser.getRealName()+"预约了"+serviceOptions.getServiceName()+"会议服务，会议于："+bean.getStartTime()+"开始，会议地点："+meetingRoomConfig.getRoomName()+"，请及时提供服务。";
//                    String cont = user.getRealName()+"您好，请及时提供服务。";
//                    AliSmsUtil.MeetingServiceWarn("888888",user.getPhone());
//                });
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
        // 会议触发场景
//        if (!StringUtils.isBlank(bean.getSceneType()) && !Objects.equals("null", bean.getSceneType())) {
//            MeetingScene ms = new MeetingScene();
//            ms.setId(Integer.valueOf(bean.getSceneType()));
//            ms.setMeetingId(bean.getRoomId());
//            meetingSceneService.allScene(ms);
//        }
    }

    /**
     * 会议室预约列表分页查询
     *
     * @param bean
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<MeetingRoomReservation> getMeetingRoomOrderPage(MeetingRoomReservation bean, Integer pageIndex, Integer pageSize) {
        updateMeetingRoomOrderStatus();
        List<Integer> userIds = new ArrayList<>();
        if (StringUtils.isNotBlank(bean.getReservationPersonName())) {
            List<SysUser> users = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery()
                    .like(SysUser::getRealName, bean.getReservationPersonName()).eq(SysUser::getIsDel, G.ISDEL_NO)
            );
            if (!CollectionUtils.isEmpty(users)) {
                userIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
            }
        }
        if (StringUtils.isNotBlank(bean.getEndTime())) {
            // 截取日期 拼接时间
            bean.setEndTime(bean.getEndTime().substring(0, 10) + " 23:59:59");
        }
        Page<MeetingRoomReservation> page = meetingRoomReservationMapper.selectPage(new Page<>(pageIndex, pageSize), Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
                .eq(null != bean.getRoomId(), MeetingRoomReservation::getRoomId, bean.getRoomId())
                .in(StringUtils.isNotBlank(bean.getReservationPersonName()), MeetingRoomReservation::getReservationPerson, CollectionUtils.isEmpty(userIds) ? Collections.singletonList(-1) : userIds)
                .ge(StringUtils.isNotBlank(bean.getStartTime()) && StringUtils.isNotBlank(bean.getEndTime()), MeetingRoomReservation::getStartTime, bean.getStartTime())
                .le(StringUtils.isNotBlank(bean.getStartTime()) && StringUtils.isNotBlank(bean.getEndTime()), MeetingRoomReservation::getStartTime, bean.getEndTime())
                .orderByAsc(MeetingRoomReservation::getRoomStatus).orderByDesc(MeetingRoomReservation::getStartTime)
        );
        for (MeetingRoomReservation meetingRoomReservation : page.getRecords()) {
            // 会议预约人
            SysUser reservationPerson = sysUserService.getUser(SysUser.builder().id(meetingRoomReservation.getReservationPerson()).build());
            meetingRoomReservation.setReservationPersonName(reservationPerson.getRealName());
            // 查询会议室预约人
            List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                    .eq(MeetingRoomReservationUser::getReservationId, meetingRoomReservation.getId())
            );
            if (CollectionUtils.isEmpty(meetingRoomReservationUsers)) {
                meetingRoomReservation.setUserList(Collections.emptyList());
                continue;
            }
            List<SysUser> userList = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery()
                    .in(SysUser::getId, meetingRoomReservationUsers.stream().map(MeetingRoomReservationUser::getUserId).toArray())
            );
            meetingRoomReservation.setUserList(userList);

            // 查询会议室名称
            MeetingRoomConfig room = meetingRoomConfigMapper.selectById(meetingRoomReservation.getRoomId());
            String roomName = room == null ? "" : room.getRoomName();
            meetingRoomReservation.setRoomName(roomName);
        }
        return page;
    }

    @Autowired
    private MeetingRoomReservationServiceOptionsRelationMapper serviceOptionsRelationMapper;

    /**
     * 会议室预约列表分页查询
     *
     * @param id
     * @return
     */
    public MeetingRoomReservation getMeetingRoomOrderById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new CustomException("id不能为空");
        }
        MeetingRoomReservation meetingRoomReservation = meetingRoomReservationMapper.selectById(id);
        if (null == meetingRoomReservation) {
            throw new CustomException("未查询到预约信息");
        }
        // 会议预约人
        SysUser reservationPerson = sysUserService.getUser(SysUser.builder().id(meetingRoomReservation.getReservationPerson()).build());
        meetingRoomReservation.setReservationPersonName(reservationPerson.getRealName());
        // 查询会议室预约人
        List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                .eq(MeetingRoomReservationUser::getReservationId, meetingRoomReservation.getId())
        );
        if (CollectionUtils.isEmpty(meetingRoomReservationUsers)) {
            meetingRoomReservation.setUserList(Collections.emptyList());
        }
        List<SysUser> userList = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getId, meetingRoomReservationUsers.stream().map(MeetingRoomReservationUser::getUserId).toArray())
        );
        if (!CollectionUtils.isEmpty(userList)) {
            // 得到部门信息 转换为 Map<Integer, String> KEY:部门ID, VALUE:部门名称
            Map<Integer, String> deptMap = sysOrgDeptService.getBaseMapper().selectList(Wrappers.<SysOrgDept>lambdaQuery()
                    .in(SysOrgDept::getId, userList.stream().map(SysUser::getDeptId).toArray())
            ).stream().collect(Collectors.toMap(SysOrgDept::getId, SysOrgDept::getDeptName));
            for (SysUser user : userList) {
                user.setDeptName(deptMap.get(user.getDeptId()));
            }
        }
        meetingRoomReservation.setUserList(userList);

        // 查询会议室名称
        MeetingRoomConfig room = meetingRoomConfigMapper.selectById(meetingRoomReservation.getRoomId());
        //返回文件处理
        if (ObjUtil.isNotEmpty(room)) {
            handleRoomFile(room);
        }
        String roomName = room == null ? "" : room.getRoomName();
        meetingRoomReservation.setRoomName(roomName);

//        if(null != meetingRoomReservation.getSignInStartTime() && null != meetingRoomReservation.getSignInEndTime()){
//            meetingRoomReservation.setSignInStartTime(formatDate(meetingRoomReservation.getSignInStartTime()));
//            meetingRoomReservation.setSignInEndTime(formatDate(meetingRoomReservation.getSignInEndTime()));
//        }

        // 查询服务名称
        List<MeetingServiceRelation> serviceRelations = serviceOptionsRelationMapper.selectList(Wrappers.<MeetingServiceRelation>lambdaQuery()
                .eq(MeetingServiceRelation::getMeetingRoomReservationId, id)
        );
        meetingRoomReservation.setSceneTypeStr(
                StringUtils.isBlank(meetingRoomReservation.getSceneType()) ? "" :
                        meetingSceneService.getById(meetingRoomReservation.getSceneType()).getSceneName()
        );
        return meetingRoomReservation;
    }

    public String formatDate(String dbDateString) {

        if (StringUtils.contains(dbDateString, "GMT")) {
            // 2023-10-25 Tue Oct 10 2023 18:30:00
            dbDateString = dbDateString.substring(0, dbDateString.indexOf("GMT")).trim();
            // 截取字符 2023-10-25
            String pie = dbDateString.substring(0, dbDateString.indexOf(" ")).trim();
            dbDateString = dbDateString.substring(dbDateString.indexOf(" ")).trim();

            SimpleDateFormat dbDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ENGLISH);

            try {
                Date dbDate = dbDateFormat.parse(dbDateString);

                SimpleDateFormat targetDateFormat = new SimpleDateFormat("HH:mm");
                String targetDateString = targetDateFormat.format(dbDate);

                return pie + " " + targetDateString;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return "";
            }
        }
        return dbDateString;
    }

    // 调整预约的状态
    public void updateMeetingRoomOrderStatus() {
        // 取得当前日期
        String today = new DateTime().toString("yyyy-MM-dd HH:mm:00");
        // 查询当前时间之前的预约, 全部调整为已结束
        List<MeetingRoomReservation> a = meetingRoomReservationMapper.selectList(Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
                .le(MeetingRoomReservation::getEndTime, today)
                .ne(MeetingRoomReservation::getRoomStatus, 3)
        );
        if (!CollectionUtils.isEmpty(a)) {
            meetingRoomReservationMapper.update(null, Wrappers.<MeetingRoomReservation>lambdaUpdate()
                    .set(MeetingRoomReservation::getRoomStatus, 3)
                    .in(MeetingRoomReservation::getId, a.stream().map(MeetingRoomReservation::getId).toArray())
            );
        }

        // 如果当前时间在预约开始时间和结束时间中间, 则调整为进行中
        List<MeetingRoomReservation> b = meetingRoomReservationMapper.selectList(Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
                        .le(MeetingRoomReservation::getStartTime, today)
                        .ge(MeetingRoomReservation::getEndTime, today)
//                .eq(MeetingRoomReservation::getRoomStatus, 1)
        );
        List<Integer> runningRoom = new ArrayList<>();
        if (!CollectionUtils.isEmpty(b)) {
            meetingRoomReservationMapper.update(null, Wrappers.<MeetingRoomReservation>lambdaUpdate()
                    .set(MeetingRoomReservation::getRoomStatus, 2)
                    .in(MeetingRoomReservation::getId, b.stream().map(MeetingRoomReservation::getId).toArray())
            );
            runningRoom = b.stream().map(MeetingRoomReservation::getRoomId).collect(Collectors.toList());
        }
        // 得到所有预约的roomId
        List<Integer> allRoom = meetingRoomReservationMapper.selectList(Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
                .eq(MeetingRoomReservation::getRoomStatus, 1)
        ).stream().map(MeetingRoomReservation::getRoomId).distinct().collect(Collectors.toList());

        // 更新会议室状态, 如果runningRoom 包含的roomId 则调整为2, 如果 allRoom 包含的roomId 并且不在runningRomm 则调整为3,否则调整为1
        List<MeetingRoomConfig> q = meetingRoomConfigMapper.selectList(Wrappers.<MeetingRoomConfig>lambdaQuery()
                .select(MeetingRoomConfig::getId).eq(MeetingRoomConfig::getIsDel, G.ISDEL_NO)
        );
        if (!CollectionUtils.isEmpty(q)) {
            // 取得 allRoom 和 runningRoom 的差集
            List<Integer> finalRunningRoom = runningRoom;
            List<Integer> difference = allRoom.stream().filter(item -> !finalRunningRoom.contains(item)).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(difference)) {
                // 已预约
                meetingRoomConfigMapper.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate()
                        .set(MeetingRoomConfig::getRoomStatus, 3)
                        .in(MeetingRoomConfig::getId, difference)
                );
            }
            if (!CollectionUtils.isEmpty(runningRoom)) {
                // 进行中
                meetingRoomConfigMapper.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate()
                        .set(MeetingRoomConfig::getRoomStatus, 2)
                        .in(MeetingRoomConfig::getId, runningRoom)
                );
            }
            // 空闲
            meetingRoomConfigMapper.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate()
                    .set(MeetingRoomConfig::getRoomStatus, 1)
                    .notIn(!CollectionUtils.isEmpty(runningRoom), MeetingRoomConfig::getId, runningRoom)
                    .notIn(!CollectionUtils.isEmpty(difference), MeetingRoomConfig::getId, difference)
            );


        }

//        updateMeetingRoomStatus();
    }

    // 调整会议室状态
    public void updateMeetingRoomStatus() {
        // 调整所有会议室设置为空闲
        this.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate().set(MeetingRoomConfig::getRoomStatus, 1).eq(MeetingRoomConfig::getIsDel, G.ISDEL_NO));
        meetingRoomReservationMapper.selectList(Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
        ).forEach(x -> {
            if (x.getMeetingTheme().equals("系统设计")) {
                System.out.println(111);
            }
            if (x.getRoomStatus() == 2) {
                // 如果有预约正在进行中, 则调整为会议中
                meetingRoomConfigMapper.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate()
                        .set(MeetingRoomConfig::getRoomStatus, 2)
                        .eq(MeetingRoomConfig::getId, x.getRoomId())
                );
            } else if (x.getRoomStatus() == 1) {
                // 已预约
                meetingRoomConfigMapper.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate()
                        .set(MeetingRoomConfig::getRoomStatus, 3)
                        .eq(MeetingRoomConfig::getId, x.getRoomId())
                );
            }
        });

    }

    /**
     * 查询当天的预约情况,并从早上8点开始,每半小时记录一次事件是否被占用
     *
     * @param bean
     * @return
     */
    public List<Map<String, Object>> getMeetingRoomOrderList(MeetingRoomReservation bean) {
        String yyDay = bean.getReservationDay();
        if (StringUtils.isBlank(yyDay)) {
            throw new CustomException("预约日期不能为空");
        }
        if (null == bean.getRoomId()) {
            throw new CustomException("会议室id不能为空");
        }
        // 查询预约天的所有预约
        List<MeetingRoomReservation> mrrList = meetingRoomReservationMapper.selectList(
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getIsDel, 0)
                        .eq(MeetingRoomReservation::getRoomId, bean.getRoomId())
                        .ge(MeetingRoomReservation::getStartTime, yyDay + " 00:00:00")
                        .le(MeetingRoomReservation::getEndTime, yyDay + " 23:59:59")
                        .orderByAsc(MeetingRoomReservation::getStartTime)
        );
        // 拆分 yyDay 年月日
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 8; i < 23; i++) {
            Date s = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":00:00");
            Date e = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":29:59");
            int isUse1 = 0;

            Date q = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":30:00");
            Date w = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":59:00");
            int isUse2 = 0;
            for (MeetingRoomReservation mrr : mrrList) {
                Date s2 = DateUtils.parseDate(mrr.getStartTime());
                Date e2 = DateUtils.parseDate(mrr.getEndTime());

                // 如果当前时间,在开始时间和结束时间之间, 则isUse = 2
                Date currtentDate = new Date();
                if (currtentDate.after(s2) && currtentDate.before(e2)) {
                    if (currtentDate.after(s) && currtentDate.before(e)) {
                        isUse1 = 2;
                    } else if (currtentDate.after(q) && currtentDate.before(w)) {
                        isUse2 = 2;
                    }
//                    isUse1 = 2;
                    continue;
                }
                if (DateUtils.doTimeRangesOverlap(s2, e2, s, e)) {
                    isUse1 = 1;
                }
                if (DateUtils.doTimeRangesOverlap(s2, e2, q, w)) {
                    isUse2 = 1;
                }
            }

            // 判断 s 的时间是否在当前时间之前, 如果是, 则不可预约
            if (s.before(new Date())) {
                isUse1 = 3;
            }

            if (i != 8) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", i);
                map.put("checked", false);
                map.put("status", isUse1);
                map.put("time", (i < 10 ? "0" + i : i + "") + ":00");
                list.add(map);
            }

            if (i == 22) {
                continue;
            }
            if (q.before(new Date())) {
                isUse2 = 3;
            }

            Map<String, Object> map2 = new HashMap<>();
            map2.put("id", i);
            map2.put("checked", false);
            map2.put("status", isUse2);
            map2.put("time", (i < 10 ? "0" + i : i + "") + ":30");
            list.add(map2);
        }
        return list;
    }

    /**
     * 修改会议室验证密码
     *
     * @return
     */
    public String getChangeMeettingPwd() {
        String defaultPwd = "1234567890";
        List<SysDict> sysDicts = sysDictService.selectByType("meetingPwd");
        if (null == sysDicts) {
            return defaultPwd;
        } else {
            SysDict sd = sysDicts.get(0);
            String dictValue = sd.getDictValue();
            if ("".equals(StringUtils.trimToEmpty(dictValue))) {
                return defaultPwd;
            } else {
                return dictValue;
            }
        }
    }

    /**
     * 我要签到
     *
     * @param bean
     * @return
     */
    public ResultInfo sign(MeetingRoomReservation bean) {
        SysUser user = sysUserService.getUser();
        if (null == user) {
            throw new CustomException("扫描失败，请重新登陆小程序！");
        }
        //会议对象
        MeetingRoomReservation meetingRoomReservation = meetingRoomReservationMapper.selectById(bean.getId());
        if (meetingRoomReservation.getIsDel() == 1) {
            throw new CustomException("会议已取消，无法签到！");
        }

        Date date = new Date();
        if (date.before(meetingRoomReservation.getSignInStartTime()) || date.after(meetingRoomReservation.getSignInEndTime())) {
            throw new CustomException("当前不在签到时间段！");
        }

        MeetingRoomReservationUser meetingRoomReservationUser = meetingRoomReservationUserMapper.selectOne(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                .eq(MeetingRoomReservationUser::getReservationId, bean.getId())
                .eq(MeetingRoomReservationUser::getUserId, user.getId())
        );

        if (null == meetingRoomReservationUser) {
            log.info("此人：" + user.getRealName() + " 不是 " + meetingRoomReservation.getMeetingTheme() + " de 参会人员");
            MeetingRoomReservationUser mru = new MeetingRoomReservationUser();
            //新增
            mru.setId(IdUtil.fastSimpleUUID());
            mru.setUserId(user.getId());
            mru.setUserName(user.getRealName());
            mru.setReservationId(meetingRoomReservation.getId());
            mru.setDeptId(user.getDeptId());
            mru.setStatus(0);
            mru.setUserType(1);
            meetingRoomReservationUserMapper.insert(mru);
//            throw new RuntimeException("您不是参会成员！");
        } else {
            meetingRoomReservationUser.setStatus(0);
            meetingRoomReservationUserMapper.updateById(meetingRoomReservationUser);
        }
        return ResultInfo.success();
    }

    /**
     * 我的签到列表
     *
     * @param bean
     * @return
     */
    public String getMySignList(MeetingRoomReservation bean) {
        return "";
    }

    /**
     * 我的预约1 我所预约的会议
     *
     * @return
     */
    public Page<MeetingRoomReservation> getMyBookingList(Integer pageIndex, Integer pageSize) {
        SysUser user = sysUserService.getUser();
        if (null == user) {
            throw new CustomException("查询失败，请刷新重试");
        }

        Page<MeetingRoomReservation> mrr = meetingRoomReservationMapper.selectPage(new Page<>(pageIndex, pageSize), Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, G.ISDEL_NO)
                .eq(MeetingRoomReservation::getReservationPerson, user.getId())
                .orderByDesc(MeetingRoomReservation::getCreateTime)
        );


        for (MeetingRoomReservation vo : mrr.getRecords()) {
            MeetingRoomReservationVo vo1 = new MeetingRoomReservationVo();
            BeanUtils.copyProperties(vo, vo1);
            List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                    .eq(MeetingRoomReservationUser::getReservationId, vo.getId())
            );
            List<String> shouldCheckedInList = new ArrayList<>();
            List<String> checkedInList = new ArrayList<>();
            List<String> notCheckedInList = new ArrayList<>();
            for (MeetingRoomReservationUser u : meetingRoomReservationUsers) {
                shouldCheckedInList.add(u.getUserName());
                //0已签到 , 1未签到
                if (0 == u.getStatus()) {
                    checkedInList.add(u.getUserName());
                } else {
                    notCheckedInList.add(u.getUserName());
                }
            }
            vo.setShouldCheckedInList(shouldCheckedInList);
            vo.setCheckedInList(checkedInList);
            vo.setNotCheckedInList(notCheckedInList);
        }
        return mrr;
    }


    /**
     * 获取我参与的会议
     *
     * @return
     */
    public Page<MeetingRoomReservationVo> getMyAttendedMeetingsList(Integer pageIndex, Integer pageSize, MeetingRoomReservationVo bean) {
        SysUser user = sysUserService.getUser();
        if (null == user) {
            throw new CustomException("查询失败，请刷新重试");
        }
        Page<MeetingRoomReservationVo> mrr = meetingRoomReservationMapper.getMyAttendedMeetingsList(new Page<>(pageIndex, pageSize), user.getId(), bean);

        for (MeetingRoomReservationVo vo : mrr.getRecords()) {
            List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                    .eq(MeetingRoomReservationUser::getReservationId, vo.getId())
            );
            List<String> shouldCheckedInList = new ArrayList<>();
            List<String> checkedInList = new ArrayList<>();
            List<String> notCheckedInList = new ArrayList<>();
            for (MeetingRoomReservationUser u : meetingRoomReservationUsers) {
                shouldCheckedInList.add(u.getUserName());
                //0已签到 , 1未签到
                if (0 == u.getStatus()) {
                    checkedInList.add(u.getUserName());
                } else {
                    notCheckedInList.add(u.getUserName());
                }
            }
            vo.setShouldCheckedInList(shouldCheckedInList);
            vo.setCheckedInList(checkedInList);
            vo.setNotCheckedInList(notCheckedInList);
        }

        return mrr;
    }


    public List<SysUser> getUnitUser() {
        SysUser user = sysUserService.getUser();
        if (null == user) {
            throw new CustomException("查询失败，请刷新重试！");
        }
        List<SysUser> sysUsers = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUnitId, user.getUnitId()).eq(SysUser::getIsDel, 0));
        List<Integer> collect = sysUsers.stream().map(SysUser::getDeptId).collect(Collectors.toList());
        Set<Integer> set = new HashSet<>(collect);
        List<SysOrgDept> sysOrgDepts = sysOrgDeptService.getBaseMapper().selectList(Wrappers.<SysOrgDept>lambdaQuery().in(SysOrgDept::getId, set).eq(SysOrgDept::getIsDel, 0));
        sysUsers.forEach(x -> {
            x.setDeptName(sysOrgDepts.stream().filter(s -> s.getId().equals(x.getDeptId())).findFirst().get().getDeptName());
        });
        return sysUsers;

    }

    /**
     * 查询当天的预约情况,并从早上8点开始,每半小时记录一次事件是否被占用 1小时一次
     *
     * @param bean
     * @return
     */
    public List<Map<String, Object>> getMeetingRoomOrderListOne(MeetingRoomReservation bean) {
        String yyDay = bean.getReservationDay();
        if (StringUtils.isBlank(yyDay)) {
            throw new CustomException("预约日期不能为空");
        }
        if (null == bean.getRoomId()) {
            throw new CustomException("会议室id不能为空");
        }
        // 查询预约天的所有预约
        List<MeetingRoomReservation> mrrList = meetingRoomReservationMapper.selectList(
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getRoomId, bean.getRoomId())
                        .ge(MeetingRoomReservation::getStartTime, yyDay + " 00:00:00")
                        .le(MeetingRoomReservation::getEndTime, yyDay + " 23:59:59")
                        .orderByAsc(MeetingRoomReservation::getStartTime)
        );
        // 获取当前时间
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 获取当前小时和分钟
        int currentHour = currentDateTime.getHour();
        int currentMinute = currentDateTime.getMinute();

        // 设置为整点或半点  16.06
        if (currentMinute < 30) {
            currentDateTime = currentDateTime.withMinute(0);
        } else {
            currentDateTime = currentDateTime.withMinute(30);
        }
        currentDateTime = currentDateTime.minus(1, ChronoUnit.MINUTES);

        // 转换为 Date 对象
        Date currentDate = java.sql.Timestamp.valueOf(currentDateTime);


        // 拆分 yyDay 年月日
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 8; i < 18; i++) {
            Date s = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":00:00");
            Date e = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":29:59");
            int isUse1 = 0;

            Date q = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":30:00");
            Date w = DateUtils.parseDate(yyDay + (i < 11 ? " 0" : " ") + i + ":59:00");
            int isUse2 = 0;
            for (MeetingRoomReservation mrr : mrrList) {
                Date s2 = DateUtils.parseDate(mrr.getStartTime());
                Date e2 = DateUtils.parseDate(mrr.getEndTime());

                // 如果当前时间,在开始时间和结束时间之间, 则isUse = 2
                Date currtentDate = new Date();
                if (currtentDate.after(s2) && currtentDate.before(e2)) {
                    if (currtentDate.after(s) && currtentDate.before(e)) {
                        isUse1 = 2;
                    } else if (currtentDate.after(q) && currtentDate.before(w)) {
                        isUse2 = 2;
                    }
//                    isUse1 = 2;
                    continue;
                }
                if (DateUtils.doTimeRangesOverlap(s2, e2, s, e)) {
                    isUse1 = 1;
                }
                if (DateUtils.doTimeRangesOverlap(s2, e2, q, w)) {
                    isUse2 = 1;
                }
            }
            // 16.04  16.30
            // 判断 s 的时间是否在当前时间之前, 如果是, 则不可预约
            if (s.before(currentDate)) {
                isUse1 = 3;
            }

//            if (i != 8){
            Map<String, Object> map = new HashMap<>();
            map.put("id", i);
            map.put("checked", false);
            map.put("status", isUse1);
//                map.put("time", i + ":00");
            map.put("timeStar", i + ":00");
            map.put("timeEnd", i + ":30");
            list.add(map);
//            }

            if (i == 22) {
                continue;
            }
            if (q.before(currentDate)) {
                isUse2 = 3;
            }

            Map<String, Object> map2 = new HashMap<>();
            map2.put("id", i);
            map2.put("checked", false);
            map2.put("status", isUse2);
//            map2.put("time", i + ":30");
            map2.put("timeStar", i + ":30");
            map2.put("timeEnd", (i + 1) + ":00");
            list.add(map2);
        }
        return list;
    }

    /**
     * 查询会议室列表分页
     */
    public Page<MeetingRoomConfig> getAppMeetingRoomPage(MeetingRoomConfig bean, Integer pageIndex, Integer pageSize) {
        Page<MeetingRoomConfig> page = this.page(new Page<>(pageIndex, pageSize), Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, G.ISDEL_NO)
                .like(StringUtils.isNotBlank(bean.getRoomName()), MeetingRoomConfig::getRoomName, bean.getRoomName())
                // 楼层
                .eq(null != bean.getFloorId(), MeetingRoomConfig::getFloorId, bean.getFloorId())
                .eq(null != bean.getAreaId(), MeetingRoomConfig::getAreaId, bean.getAreaId())
                // 类型
                .eq(null != bean.getRoomType(), MeetingRoomConfig::getRoomType, bean.getRoomType())
                // 状态
                .eq(null != bean.getRoomStatus(), MeetingRoomConfig::getRoomStatus, bean.getRoomStatus())
                // 参会人数
                .ge(null != bean.getRoomCapacity(), MeetingRoomConfig::getRoomCapacity, bean.getRoomCapacity())
                .orderByDesc(MeetingRoomConfig::getCreateTime)
        );
        for (MeetingRoomConfig meetingRoomConfig : page.getRecords()) {
            // 查询会议室预约人
//            meetingRoomConfig.setReservationUserList(meetingRoomReservationUserMapper.selectReservationUserList(meetingRoomConfig.getId()));
            MeetingRoomReservation meetingRoomReservation = new MeetingRoomReservation();
            meetingRoomReservation.setRoomId(meetingRoomConfig.getId());
            meetingRoomReservation.setReservationDay(bean.getYyDate());
            List<Map<String, Object>> meetingRoomOrderListOne = getMeetingRoomOrderListOne(meetingRoomReservation);
            meetingRoomConfig.setReservationCondition(meetingRoomOrderListOne);
            Long count = meetingSceneControlMapper.selectCount(new LambdaQueryWrapper<MeetingSceneControl>()
                    .eq(MeetingSceneControl::getIsDel, 0)
                    .eq(MeetingSceneControl::getDeviceType, 5)
                    .eq(MeetingSceneControl::getMeetingId, meetingRoomConfig.getId())
            );
            meetingRoomConfig.setCurtainCount(count);
        }
        return page;
    }

    public MeetingRoomReservation getMyAttendedMeetingDetails(String id) {
        if (null == id) {
            throw new CustomException("请求失败，请刷新重试");
        }

        MeetingRoomReservation meetingRoomReservation = meetingRoomReservationMapper.selectById(id);
        // 参会人员
        List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                .eq(MeetingRoomReservationUser::getReservationId, meetingRoomReservation.getId())
        );
        // 会议服务
        List<MeetingServiceRelation> services = meetingRoomReservationServiceOptionsRelationService.list(Wrappers.<MeetingServiceRelation>lambdaQuery()
                .eq(MeetingServiceRelation::getMeetingRoomReservationId, meetingRoomReservation.getId())
        );
        // 会议室名称
        MeetingRoomConfig meetingRoomConfig = meetingRoomConfigMapper.selectById(meetingRoomReservation.getRoomId());
        meetingRoomReservation.setRoomName(meetingRoomConfig.getRoomName());
        List<String> shouldCheckedInList = new ArrayList<>();
        List<String> checkedInList = new ArrayList<>();
        List<String> notCheckedInList = new ArrayList<>();
        List<Integer> userIdsList = new ArrayList<>();
        List<String> serviceTypes = new ArrayList<>();
        List<Integer> serviceTypeIds = new ArrayList<>();

        for (MeetingRoomReservationUser u : meetingRoomReservationUsers) {
            shouldCheckedInList.add(u.getUserName());
            userIdsList.add(u.getUserId());
            //0已签到 , 1未签到
            if (0 == u.getStatus()) {
                checkedInList.add(u.getUserName());
            } else {
                notCheckedInList.add(u.getUserName());
            }
        }
        for (MeetingServiceRelation s : services) {
            serviceTypeIds.add(s.getServiceOptionsId());
        }
        meetingRoomReservation.setServiceTypes(serviceTypes);
        meetingRoomReservation.setServiceTypeIds(serviceTypeIds);
        meetingRoomReservation.setShouldCheckedInList(shouldCheckedInList);
        meetingRoomReservation.setCheckedInList(checkedInList);
        meetingRoomReservation.setNotCheckedInList(notCheckedInList);
        meetingRoomReservation.setUserIdsList(userIdsList);
        SysUser user = sysUserService.getUser();
        String qrCode = "请刷新重新生成二维码";
        try {
            List<String> titles = new ArrayList<>();
            titles.add("会议名：" + meetingRoomReservation.getMeetingTheme());
            titles.add("会议室：" + meetingRoomReservation.getRoomName()
//                    +"("+sysBuildService.getById(meetingRoomConfig.getBuildId()).getBuildName()
//                    +sysBuildFloorMapper.selectById(meetingRoomConfig.getFloorId()).getFloorName()+")"
            );
            List<String> footers = new ArrayList<>();
            footers.add("开始时间：" + meetingRoomReservation.getStartTime() + "  结束时间：" + meetingRoomReservation.getEndTime());
//            footers.add("结束时间："+meetingRoomReservation.getEndTime());
            BufferedImage qrCode1 = QrCoder.createQrCode("", titles, null, null, footers, String.valueOf(id));
            qrCode = QrCoder.BufferedImageToBase64(qrCode1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        meetingRoomReservation.setQrCode(qrCode);
        meetingRoomReservation.setRoomCapacity(meetingRoomConfig.getRoomCapacity());
        // 前端需要 会议室+位置
//        meetingRoomReservation.setRoomName(meetingRoomReservation.getRoomName()
//                +"("+sysBuildService.getById(meetingRoomConfig.getBuildId()).getBuildName()
//                +sysBuildFloorMapper.selectById(meetingRoomConfig.getFloorId()).getFloorName()+")"
//        );
        meetingRoomReservation.setReservationPersonName(sysUserService.getById(meetingRoomReservation.getReservationPerson()).getRealName());
        meetingRoomReservation.setSceneTypeStr(
                StringUtils.isBlank(meetingRoomReservation.getSceneType()) ? "" :
                        meetingSceneService.getById(meetingRoomReservation.getSceneType()).getSceneName()
        );
        return meetingRoomReservation;
    }

    public List<SysBuildFloor> getMeetingRoomFloorList() {
        List<MeetingRoomConfig> list = list(new LambdaQueryWrapper<MeetingRoomConfig>().eq(MeetingRoomConfig::getIsDel, 0));
        if (StringUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Integer> floorSet = list.stream().map(MeetingRoomConfig::getFloorId).collect(Collectors.toSet());
        return sysBuildFloorMapper.selectList(new LambdaQueryWrapper<SysBuildFloor>()
                .eq(SysBuildFloor::getIsDel, 0)
                .in(SysBuildFloor::getId, floorSet)
                .orderByAsc(SysBuildFloor::getSort));
    }

    public Page<MeetingRoomReservationVo> getMeetingPage(Integer pageIndex, Integer pageSize, MeetingRoomReservationVo bean) {
        SysUser user = sysUserService.getUser();
        Page<MeetingRoomReservationVo> mrr = meetingRoomReservationMapper.getMyAttendedMeetingsList(new Page<>(pageIndex, pageSize), user.getId(), bean);
        for (MeetingRoomReservationVo vo : mrr.getRecords()) {
            List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                    .eq(MeetingRoomReservationUser::getReservationId, vo.getId())
            );
            List<String> shouldCheckedInList = new ArrayList<>();
            List<String> checkedInList = new ArrayList<>();
            List<String> notCheckedInList = new ArrayList<>();
            for (MeetingRoomReservationUser u : meetingRoomReservationUsers) {
                shouldCheckedInList.add(u.getUserName());
                //0已签到 , 1未签到
                if (0 == u.getStatus()) {
                    checkedInList.add(u.getUserName());
                } else {
                    notCheckedInList.add(u.getUserName());
                }
            }
            vo.setShouldCheckedInList(shouldCheckedInList);
            vo.setCheckedInList(checkedInList);
            vo.setNotCheckedInList(notCheckedInList);
            vo.setReservationPersonName(sysUserService.getById(vo.getReservationPerson()).getRealName());
            // 会议室名称
            MeetingRoomConfig meetingRoomConfig = meetingRoomConfigMapper.selectById(vo.getRoomId());
            //返回文件处理
            handleRoomFile(meetingRoomConfig);
            vo.setMeetingRoomConfig(meetingRoomConfig);
            // 前端需要 会议室+位置
            vo.setRoomName(meetingRoomConfig.getRoomName()
//                    +"("+sysBuildService.getById(meetingRoomConfig.getBuildId()).getBuildName()
//                    +sysBuildFloorMapper.selectById(meetingRoomConfig.getFloorId()).getFloorName()+")"
            );
        }
        return mrr;
    }

    public String cancelMeeting(MeetingRoomReservationVo bean) {
        MeetingRoomReservation meetingRoomReservation = meetingRoomReservationMapper.selectById(bean.getId());
        if (meetingRoomReservation.getRoomStatus() == 2) {
            throw new CustomException("会议进行中不可取消!");
//            return "会议进行中不可取消!";
        }
        if (meetingRoomReservation.getRoomStatus() == 3) {
            throw new CustomException("会议已结束不可取消!");
//            return "会议已结束不可取消!";
        }
        // 项目经理说直接删除，不用有取消状态
        meetingRoomReservation.setIsDel(1);
        meetingRoomReservationMapper.updateById(meetingRoomReservation);

        MeetingRoomConfig meetingRoomConfig = meetingRoomConfigMapper.selectById(meetingRoomReservation.getRoomId());
        if (meetingRoomConfig.getRoomStatus() != 2) {
            //如果有离开模式 取一个离开模式
            List<MeetingScene> list = meetingSceneService.list(Wrappers.<MeetingScene>lambdaQuery()
                    .eq(MeetingScene::getMeetingId, meetingRoomReservation.getRoomId())
                    .eq(MeetingScene::getIsDel, 0)
                    // 离开模式
                    .eq(MeetingScene::getSceneType, 2)
            );
            if (CollUtil.isNotEmpty(list)) {
                meetingSceneService.allScene(list.get(0));
            }

        }
        try {
            // 查询关联人员id
            List<Integer> ids = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                    .eq(MeetingRoomReservationUser::getReservationId, bean.getId())
            ).stream().map(MeetingRoomReservationUser::getUserId).collect(Collectors.toList());
            // APP推送 会议取消通知
            AppPushMsgVo appPushMsgVo = new AppPushMsgVo();
            appPushMsgVo.setUserIdList(ids);
            appPushMsgVo.setAppConfigCode("SYSTEM_CANCEL_MEETING");
            appPushMsgVo.setTime(meetingRoomReservation.getStartTime());
            appPushMsgVo.setName(meetingRoomConfigMapper.selectById(meetingRoomReservation.getRoomId()).getRoomName());
            appPushMsgVo.setTitle(meetingRoomReservation.getMeetingTheme());
            appPushMsgService.pushMsg(appPushMsgVo);
        } catch (Exception e) {
            log.error("APP推送 会议取消通知失败", e);
        }
        return "会议取消成功!";
    }

    public List<MeetingRoomConfig> getDayList(MeetingRoomConfig bean) {
        String yyDate = bean.getYyDate();
        if (StringUtils.isBlank(yyDate)) {
            throw new CustomException("预约日期不能为空");
        }
        // 查询所有会议室
        List<MeetingRoomConfig> list = meetingRoomConfigMapper.selectList(new LambdaQueryWrapper<MeetingRoomConfig>()
                .eq(MeetingRoomConfig::getIsDel, 0));
        // 建筑名称映射
        List<SysBuild> buildList = sysBuildService.list(new LambdaQueryWrapper<SysBuild>().eq(SysBuild::getIsDel, 0));
        Map<Integer, String> buildMap = buildList.stream().collect(Collectors.toMap(SysBuild::getId, SysBuild::getBuildName, (a, b) -> b));
        // 楼层名称映射
        List<SysBuildFloor> floorList = sysBuildFloorMapper.selectList(new LambdaQueryWrapper<SysBuildFloor>().eq(SysBuildFloor::getIsDel, 0));
        Map<Integer, String> floorMap = floorList.stream().collect(Collectors.toMap(SysBuildFloor::getId, SysBuildFloor::getFloorName, (a, b) -> b));

        for (MeetingRoomConfig roomConfig : list) {
            // 查询预约天的所有预约
            List<MeetingRoomReservation> mrrList = meetingRoomReservationMapper.selectList(
                    Wrappers.<MeetingRoomReservation>lambdaQuery()
                            .eq(MeetingRoomReservation::getIsDel, 0)
                            .eq(MeetingRoomReservation::getRoomId, roomConfig.getId())
                            .ge(MeetingRoomReservation::getStartTime, yyDate + " 00:00:00")
                            .le(MeetingRoomReservation::getEndTime, yyDate + " 23:59:59")
                            .orderByAsc(MeetingRoomReservation::getStartTime)
            );
            roomConfig.setMeetingRoomReservations(mrrList);
            roomConfig.setBuildName(buildMap.get(roomConfig.getBuildId()));
            roomConfig.setFloorName(floorMap.get(roomConfig.getFloorId()));

            //返回文件处理
            handleRoomFile(roomConfig);
        }
        return list;
    }


    public String optionalRange(MeetingRoomConfig bean) {
        String yyDate = bean.getYyDate();
        if (StringUtils.isBlank(yyDate)) {
            throw new CustomException("预约日期不能为空");
        }

        // 查询预约天的所有预约
        List<MeetingRoomReservation> mrrList = meetingRoomReservationMapper.selectList(
                Wrappers.<MeetingRoomReservation>lambdaQuery()
                        .eq(MeetingRoomReservation::getIsDel, 0)
                        .eq(MeetingRoomReservation::getRoomId, bean.getId())
                        .ge(MeetingRoomReservation::getStartTime, yyDate + " 00:00:00")
                        .le(MeetingRoomReservation::getEndTime, yyDate + " 23:59:59")
                        .orderByAsc(MeetingRoomReservation::getStartTime)
        );
        // 当前时间
        String format = "00:00";
        Date date = new Date();
        // 判断预约时间与今日是不是同一天
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 如果是同一天
        if (yyDate.equals(fmt.format(date))) {
            date.setTime(date.getTime() + 60000);
            format = sdf.format(date);
        }
        // 可选范围
        for (MeetingRoomReservation m : mrrList) {
            // 给 start 加一分钟

            Date start;
            Date end;
            try {
                start = sdff.parse(m.getStartTime());
                end = sdff.parse(m.getEndTime());
                // 如果会议的开始时间是按在当前时间之前
                // 可选范围的开始时间是会议的结束时间
                if (start.before(date)) {
                    end.setTime(end.getTime() + 60000);
                    format = sdf.format(end);
                    // 如果会议结束时间在当前时间之前
                    // 可选范围的开始时间是当前时间
                    if (end.before(date)) {
                        format = sdf.format(date);
                    }
                    continue;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // 可选范围 如果包含会议开始时间
            if (format.contains(sdf.format(start))) {

                format = format.replace(sdf.format(start), "");

                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(end);
                calendar3.add(Calendar.MINUTE, 1);
                end = calendar3.getTime();
                format = format + sdf.format(end);

            } else {

                Calendar calendar4 = Calendar.getInstance();
                calendar4.setTime(start);
                calendar4.add(Calendar.MINUTE, -1);
                start = calendar4.getTime();

                calendar4.setTime(end);
                calendar4.add(Calendar.MINUTE, 1);
                end = calendar4.getTime();

                format = format + "~" + sdf.format(start) + "," + sdf.format(end);
            }

        }
        if (format.contains("23:59")) {
            format = format.replace(",23:59", "");
        } else {
            format = format + "~23:59";
        }
        // 过滤指定时间段
        format = filterShortTimeRanges(format, 10);
        return format;
    }

    /**
     * 过滤指定时间段
     *
     * @param time               时间段字符串
     * @param minDurationMinutes 最小时间差，单位分钟
     * @return 过滤后的时间段字符串
     */
    public static String filterShortTimeRanges(String time, int minDurationMinutes) {
        // 拆分时间段
        String[] timeRanges = time.split(",");
        List<String> validRanges = new ArrayList<>();

        for (String range : timeRanges) {
            String[] times = range.split("~");
            if (times.length == 2) {
                // 解析时间
                LocalTime startTime = LocalTime.parse(times[0]);
                LocalTime endTime = LocalTime.parse(times[1]);

                // 计算时间差
                long durationMinutes = Duration.between(startTime, endTime).toMinutes();

                // 过滤符合条件的时间段
                if (durationMinutes >= minDurationMinutes) {
                    validRanges.add(range);
                }
            }
        }
        // 拼接过滤后的时间段
        return String.join(",", validRanges);
    }

    public Page<MeetingRoomReservationVo> getMeetingHomePage(Integer pageIndex, Integer pageSize, MeetingRoomReservationVo bean) {
        SysUser user = sysUserService.getUser();
        Page<MeetingRoomReservationVo> mrr = meetingRoomReservationMapper.getMeetingHomePage(new Page<>(pageIndex, pageSize), user.getId(), bean);
        for (MeetingRoomReservationVo vo : mrr.getRecords()) {
            List<MeetingRoomReservationUser> meetingRoomReservationUsers = meetingRoomReservationUserMapper.selectList(Wrappers.<MeetingRoomReservationUser>lambdaQuery()
                    .eq(MeetingRoomReservationUser::getReservationId, vo.getId())
            );
            List<String> shouldCheckedInList = new ArrayList<>();
            List<String> checkedInList = new ArrayList<>();
            List<String> notCheckedInList = new ArrayList<>();
            for (MeetingRoomReservationUser u : meetingRoomReservationUsers) {
                shouldCheckedInList.add(u.getUserName());
                //0已签到 , 1未签到
                if (0 == u.getStatus()) {
                    checkedInList.add(u.getUserName());
                } else {
                    notCheckedInList.add(u.getUserName());
                }
            }
            vo.setShouldCheckedInList(shouldCheckedInList);
            vo.setCheckedInList(checkedInList);
            vo.setNotCheckedInList(notCheckedInList);
            vo.setReservationPersonName(sysUserService.getById(vo.getReservationPerson()).getRealName());
            // 会议室名称
            MeetingRoomConfig meetingRoomConfig = meetingRoomConfigMapper.selectById(vo.getRoomId());
            // 前端需要 会议室+位置
            vo.setRoomName(meetingRoomConfig.getRoomName()
//                    +"("+sysBuildService.getById(meetingRoomConfig.getBuildId()).getBuildName()
//                    +sysBuildFloorMapper.selectById(meetingRoomConfig.getFloorId()).getFloorName()+")"
            );
        }
        return mrr;
    }

    public String meetingSceneSwitch(MeetingRoomReservationVo bean) {
        if (StringUtils.isBlank(bean.getSceneType()) || Objects.equals("null", bean.getSceneType())) {
            throw new CustomException("场景切换失败，请刷新重试！");
        }
        // 触发场景
        MeetingScene ms = meetingSceneService.getById(Integer.valueOf(bean.getSceneType()));
        if (null == bean.getId() || Objects.equals("", bean.getId())) { // 会议室直接修改场景
            if (Objects.equals("null", bean.getId())) {
                throw new CustomException("场景切换失败，请刷新重试！");
            }
            meetingSceneService.allScene(ms);
        } else { // 通过会议修改会议室场景
            MeetingRoomReservation meeting = meetingRoomReservationMapper.selectById(bean.getId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime meetStartTime = LocalDateTime.parse(meeting.getStartTime(), formatter);
            // 获取系统当前时间
            LocalDateTime currentDateTime = LocalDateTime.now();
            // 判断会议是否已经开始
            if (currentDateTime.isAfter(meetStartTime)) {
                ms.setMeetingUser(meeting.getCreateUser());
                meetingSceneService.allScene(ms);
            }
            // 更新会议场景
            meetingRoomReservationMapper.update(null, Wrappers.<MeetingRoomReservation>lambdaUpdate()
                    .set(MeetingRoomReservation::getSceneType, bean.getSceneType())
                    .eq(MeetingRoomReservation::getId, bean.getId()));
        }
        return "场景切换成功！";
    }

    public List<MeetingRoomConfig> meetingFloor() {
        return meetingRoomReservationMapper.meetingFloor();
    }

    public HashMap<String, Long> roomStatus() {
        HashMap<String, Long> map = new HashMap<>();
        Long z = meetingRoomConfigMapper.selectCount(Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, 0)
        );
        map.put("zong", z);
        Long l = meetingRoomConfigMapper.selectCount(Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, 0).eq(MeetingRoomConfig::getRoomStatus, 2)
        );
        map.put("mang", l);
        Long ne = meetingRoomConfigMapper.selectCount(Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, 0).ne(MeetingRoomConfig::getRoomStatus, 2)
        );
        map.put("xian", ne);
        return map;
    }

    public String earlyEnd(MeetingRoomReservation meet) {
        MeetingRoomReservation meetingRoomReservation = meetingRoomReservationMapper.selectById(meet.getId());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = sdf.format(date);
        meetingRoomReservation.setEndTime(format);
        meetingRoomReservation.setRoomStatus(3);
        MeetingRoomConfig meetingRoomConfig = meetingRoomConfigMapper.selectById(meetingRoomReservation.getRoomId());
        meetingRoomConfig.setRoomStatus(1);
        meetingRoomConfigMapper.updateById(meetingRoomConfig);
        meetingRoomReservationMapper.updateById(meetingRoomReservation);
        //如果有离开模式 取一个离开模式
        List<MeetingScene> list = meetingSceneService.list(Wrappers.<MeetingScene>lambdaQuery()
                .eq(MeetingScene::getMeetingId, meetingRoomReservation.getRoomId())
                .eq(MeetingScene::getIsDel, 0)
                // 离开模式
                .eq(MeetingScene::getSceneType, 2)
        );
        if (CollUtil.isNotEmpty(list)) {
            meetingSceneService.allScene(list.get(0));
        }
        return "会议取消成功";
    }

    public JSONArray getMeetingRoom() {

        JSONArray result = new JSONArray();
        List<MeetingRoomConfig> roomConfigs = meetingRoomConfigMapper.
                selectList(Wrappers.<MeetingRoomConfig>lambdaQuery().
                        eq(MeetingRoomConfig::getIsDel, 0));

        for (MeetingRoomConfig item : roomConfigs) {
            List<Curtain> curtains = curtainMapper.selectByMeetingId(item.getId());
            curtains.forEach(x -> {
                if (x.getGeneralControl() == 1) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("roomName", item.getRoomName());
                    jsonObject.put("roomId", item.getId());
                    jsonObject.put("curtain_id", x.getId());
                    result.add(jsonObject);
                }
            });
        }
        return result;
    }

    /**
     * 查询会议室列表分页
     */
    public List<MeetingRoomConfig> getList(MeetingRoomConfig bean) {
        List<MeetingRoomConfig> list = this.list( Wrappers.<MeetingRoomConfig>lambdaQuery()
                .eq(MeetingRoomConfig::getIsDel, G.ISDEL_NO)
                .like(StringUtils.isNotBlank(bean.getRoomName()), MeetingRoomConfig::getRoomName, bean.getRoomName())
                // 楼层
                .eq(null != bean.getAreaId(), MeetingRoomConfig::getAreaId, bean.getAreaId())
                .eq(null != bean.getFloorId(), MeetingRoomConfig::getFloorId, bean.getFloorId())
                // 类型
                .eq(null != bean.getRoomType(), MeetingRoomConfig::getRoomType, bean.getRoomType())
                // 状态
                .eq(null != bean.getRoomStatus(), MeetingRoomConfig::getRoomStatus, bean.getRoomStatus())
                .orderByDesc(MeetingRoomConfig::getCreateTime)
        );
        for (MeetingRoomConfig meetingRoomConfig : list) {
            if (StringUtils.isNotEmpty(meetingRoomConfig.getCurrentMode()) && !Objects.equals("null", meetingRoomConfig.getCurrentMode())) {
                meetingRoomConfig.setCurrentModeStr(
                        meetingSceneService.getById(Integer.valueOf(meetingRoomConfig.getCurrentMode()))
                                .getSceneName()
                );
            }
            Long count = meetingSceneControlMapper.selectCount(new LambdaQueryWrapper<MeetingSceneControl>()
                    .eq(MeetingSceneControl::getIsDel, 0)
                    .eq(MeetingSceneControl::getDeviceType, 5)
                    .eq(MeetingSceneControl::getMeetingId, meetingRoomConfig.getId())
            );
            meetingRoomConfig.setCurtainCount(count);
            meetingRoomConfig.setRoomDeviceType(meetingSceneControlService.getDeviceListByRoomId(meetingRoomConfig.getId()));
            //返回文件处理
            handleRoomFile(meetingRoomConfig);
        }
        return list;
    }

}
