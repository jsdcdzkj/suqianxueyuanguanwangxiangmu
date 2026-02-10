package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.tcpClient.BootNettyClient;
import com.jsdc.iotpt.common.tcpClient.CommandConfig;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.curtain.Curtain;
import com.jsdc.iotpt.util.uuid.IdUtils;
import com.jsdc.iotpt.vo.AirConditionCMDVo;
import com.jsdc.iotpt.vo.ChintCloudDevicePointOpearteVo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：jxl
 * @Date：2024/6/13 13:41
 * @FileDesc：
 */
@Service
@Transactional
@Slf4j
public class MeetingSceneService extends BaseService<MeetingScene> {
    @Autowired
    private MeetingSceneMapper meetingSceneMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private MeetingSceneDeviceInfoMapper sceneDeviceInfoMapper;
    @Autowired
    private MeetingSceneControlMapper sceneControlMapper;
    @Autowired
    private ChintCloudDevicePointService pointService;
    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;
    @Autowired
    private MultimediaMapper multimediaMapper;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;
    @Autowired
    private CurtainMapper curtainMapper;
    @Autowired
    private MeetingRoomConfigMapper roomConfigMapper;

    @Autowired
    private MeetingSceneControlMapper controlMapper;
    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private ConfigDeviceTypeMapper deviceTypeMapper;
    @Autowired
    private RestTemplate restTemplate;

    //新增、修改
    public void saveOrUpd(MeetingScene bean) {
        if (null != bean.getId()) {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUserService.getUser().getId());
            updateById(bean);
        } else {
            bean.setIsDel(0);
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
            save(bean);
            //新增场景时，添加默认设备
            initSceneDeviceInfo(bean);
        }

    }

    //添加场景时，初始化场景设备方案
    private void initSceneDeviceInfo(MeetingScene bean) {
        //获取会议室下所有设备
        List<MeetingSceneControl> sceneControls = sceneControlMapper.selectList(new LambdaQueryWrapper<MeetingSceneControl>()
                .eq(MeetingSceneControl::getIsDel, 0)
                .eq(MeetingSceneControl::getMeetingId, bean.getMeetingId())
        );

        if (CollUtil.isNotEmpty(sceneControls)) {
            MeetingSceneDeviceInfo deviceInfo;
            List<MeetingSceneDeviceInfo> list = new ArrayList<>();
            for (MeetingSceneControl sceneControl : sceneControls) {
                deviceInfo = new MeetingSceneDeviceInfo();
                deviceInfo.setId(IdUtils.fastSimpleUUID());
                deviceInfo.setDeviceId(sceneControl.getDeviceId());
                deviceInfo.setDeviceType(sceneControl.getDeviceType());
                deviceInfo.setIsDel(0);
                deviceInfo.setCreateUser(sysUserService.getUser().getId());
                deviceInfo.setCreateTime(new Date());
                deviceInfo.setMeetingId(bean.getMeetingId());
                deviceInfo.setSceneId(bean.getId());
                deviceInfo.setIsOpen(0);
                switch (sceneControl.getDeviceType()) {
                    // 1：空调 2：照明 3：多媒体 4：人体感应 5:窗帘
                    case 2:
                        ChintCloudDevicePoint point = chintCloudDevicePointMapper.selectById(sceneControl.getDeviceId());
                        deviceInfo.setDeviceName(point.getPointName());
                        deviceInfo.setLuminance(100);
                        deviceInfo.setColor("黄色");
                        break;
                    case 3:
                        Multimedia multimedia = multimediaMapper.selectOne(new LambdaQueryWrapper<Multimedia>().eq(Multimedia::getMeetingId, sceneControl.getMeetingId()));
                        if (ObjUtil.isNotEmpty(multimedia)) {
                            deviceInfo.setDeviceName(multimedia.getName());
                            deviceInfo.setMediaIp(multimedia.getMediaIp());
                            deviceInfo.setMediaPort(Integer.parseInt(multimedia.getMediaPort()));
                        }
                        break;
                    case 4:
                        DeviceCollect deviceCollect = deviceCollectMapper.selectById(sceneControl.getDeviceId());
                        deviceInfo.setDeviceName(deviceCollect.getName());
                        break;
                    case 5:
                        Curtain curtain = curtainMapper.selectById(sceneControl.getDeviceId());
                        deviceInfo.setDeviceName(curtain.getCurtainName());
                        deviceInfo.setCurtainIp(curtain.getIp());
                        //赋默认值 全降 便于前端页面默认展示
                        deviceInfo.setGeneralControl(3);
                        break;
                    default:
                        break;
                }


                list.add(deviceInfo);
            }
            for (MeetingSceneDeviceInfo sceneDeviceInfo : list) {
                sceneDeviceInfoMapper.insert(sceneDeviceInfo);
            }
        }
    }

    //获取会议下场景
    public List<MeetingScene> getSceneList(Integer meetingId) {
        return meetingSceneMapper.selectList(new LambdaQueryWrapper<MeetingScene>()
                .eq(MeetingScene::getIsDel, 0)
                .eq(MeetingScene::getMeetingId, meetingId)
        );
    }

    public Long isExistJudgeCount(MeetingScene bean) {
        LambdaQueryWrapper<MeetingScene> wrapper = new LambdaQueryWrapper<MeetingScene>()
                .eq(MeetingScene::getIsDel, 0)
                .eq(MeetingScene::getSceneType, bean.getSceneType())
                .eq(MeetingScene::getSceneName, bean.getSceneName())
                .eq(MeetingScene::getMeetingId, bean.getMeetingId())
                .ne(null != bean.getId(), MeetingScene::getId, bean.getId());
        return count(wrapper);
    }

    //删除功能
    public void deleteById(Integer id) {
        MeetingScene meetingScene = getById(id);
        meetingScene.setIsDel(1);
        List<MeetingSceneDeviceInfo> deviceInfos = sceneDeviceInfoMapper.selectList(new LambdaQueryWrapper<MeetingSceneDeviceInfo>().eq(MeetingSceneDeviceInfo::getIsDel, 0).eq(MeetingSceneDeviceInfo::getSceneId, id));
        if (CollUtil.isNotEmpty(deviceInfos)) {
            for (MeetingSceneDeviceInfo deviceInfo : deviceInfos) {
                deviceInfo.setIsDel(1);
                sceneDeviceInfoMapper.updateById(deviceInfo);
            }
        }
        updateById(meetingScene);
    }

    //获取详情
    public MeetingScene getSceneById(Integer id) {
        return getById(id);
    }


//    public void addUserScene(Integer userId,Integer deviceType,Integer deviceId){
//       MeetingSceneControl meetingSceneControl = controlMapper.
//               selectOne(Wrappers.<MeetingSceneControl>lambdaQuery().
//                       eq(MeetingSceneControl::getIsDel,0).
//                       eq(MeetingSceneControl::getDeviceType,deviceType).
//                       eq(MeetingSceneControl::getDeviceId,deviceId));
//
//       if (null==meetingSceneControl){
//           return;
//       }
//
//       List<MeetingSceneControl> controls=controlMapper.selectList(Wrappers.<MeetingSceneControl>lambdaQuery().
//               eq(MeetingSceneControl::getMeetingId,meetingSceneControl.getMeetingId()).
//               eq(MeetingSceneControl::getIsDel,0));
//
//       controls.forEach(x->{
//           MeetingSceneDeviceInfo deviceInfo=sceneDeviceInfoMapper.
//                   selectOne(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
//                           eq(MeetingSceneDeviceInfo::getIsDel,0).
//                           eq(MeetingSceneDeviceInfo::getCreateUser,userId).
//                           eq(MeetingSceneDeviceInfo::getDeviceId,deviceId));
//           if (null==deviceInfo){
//               deviceInfo=new MeetingSceneDeviceInfo();
//               deviceInfo.setDeviceType(1);
//               deviceInfo.setCreateUser(userId);
//               deviceInfo.setDeviceId(deviceId);
//               deviceInfo.setIsInform(false);
//               deviceInfo.setIsDel(0);
//               deviceInfo.setId(IdUtils.fastSimpleUUID());
//           }
//       });
//
//    }

    public void saveUserScene(Object bean) {
        SysUser sysUser = sysUserService.getUser();

        //
        // 空调
        if (bean instanceof AirConditionCMDVo) {
            MeetingSceneControl control = sceneControlMapper.getByDeviceTypeId(1, ((AirConditionCMDVo) bean).getDeviceId());

            if (null != control) {
                MeetingScene meetingScene = meetingSceneMapper.
                        selectOne(Wrappers.<MeetingScene>lambdaQuery().
                                eq(MeetingScene::getIsDel, 0).
                                eq(MeetingScene::getMeetingId, control.getMeetingId()).
                                eq(MeetingScene::getSceneType, 4));

                if (null != meetingScene) {
                    MeetingSceneDeviceInfo deviceInfo = sceneDeviceInfoMapper.
                            selectOne(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                                    eq(MeetingSceneDeviceInfo::getIsDel, 0).
                                    eq(MeetingSceneDeviceInfo::getSceneId, meetingScene.getId()).
                                    eq(MeetingSceneDeviceInfo::getCreateUser, sysUser.getId()).
                                    eq(MeetingSceneDeviceInfo::getDeviceId, ((AirConditionCMDVo) bean).getDeviceId()));

                    if (null == deviceInfo) {
                        deviceInfo = new MeetingSceneDeviceInfo();
                        deviceInfo.setDeviceType(1);
                        deviceInfo.setSceneId(meetingScene.getId());
                        deviceInfo.setCreateUser(sysUser.getId());
                        deviceInfo.setDeviceId(((AirConditionCMDVo) bean).getDeviceId());
                        deviceInfo.setIsInform(false);
                        deviceInfo.setIsDel(0);
                        deviceInfo.setId(IdUtils.fastSimpleUUID());
                    }
                    // 开关
                    deviceInfo.setIsOpen(((AirConditionCMDVo) bean).getOnOff());
                    // 模式
                    deviceInfo.setModelSetting(((AirConditionCMDVo) bean).getModal());
                    // 风俗
                    deviceInfo.setWindSpeedSetting(((AirConditionCMDVo) bean).getWindSpeed());
                    // 温度
                    deviceInfo.setTemperatureSetting(((AirConditionCMDVo) bean).getHighWindSpeed());

                    if (deviceInfo.getId() == null) {
                        sceneDeviceInfoMapper.insert(deviceInfo);
                    } else {
                        sceneDeviceInfoMapper.updateById(deviceInfo);
                    }
                }
            }


        } else if (bean instanceof MeetingSceneDeviceInfo) { // 多媒体
//            MeetingSceneControl control=sceneControlMapper.
//                    selectOne(Wrappers.<MeetingSceneControl>lambdaQuery().
//                            eq(MeetingSceneControl::getIsDel,0).
//                            eq(MeetingSceneControl::getDeviceType,3).
//                            eq(MeetingSceneControl::getDeviceId,((MeetingSceneDeviceInfo) bean).getDeviceId()));
            MeetingSceneControl control = sceneControlMapper.getByDeviceTypeId(3, ((MeetingSceneDeviceInfo) bean).getDeviceId());
            if (null != control) {
                MeetingScene meetingScene = meetingSceneMapper.
                        selectOne(Wrappers.<MeetingScene>lambdaQuery().
                                eq(MeetingScene::getIsDel, 0).
                                eq(MeetingScene::getMeetingId, control.getMeetingId()).
                                eq(MeetingScene::getSceneType, 3));
                if (null != meetingScene) {
                    MeetingSceneDeviceInfo deviceInfo = sceneDeviceInfoMapper.
                            selectOne(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                                    eq(MeetingSceneDeviceInfo::getIsDel, 0).
                                    eq(MeetingSceneDeviceInfo::getCreateUser, sysUser.getId()).
                                    eq(MeetingSceneDeviceInfo::getDeviceId, ((MeetingSceneDeviceInfo) bean).getDeviceId()));
                    if (null == deviceInfo) {
                        deviceInfo = new MeetingSceneDeviceInfo();
                        deviceInfo.setDeviceType(3);
                        deviceInfo.setSceneId(meetingScene.getId());
                        deviceInfo.setCreateUser(sysUser.getId());
                        deviceInfo.setDeviceId(((MeetingSceneDeviceInfo) bean).getDeviceId());
                        deviceInfo.setIsInform(false);
                        deviceInfo.setIsDel(0);
                        deviceInfo.setId(IdUtils.fastSimpleUUID());
                    }

                    // 开关
                    deviceInfo.setIsOpen(((MeetingSceneDeviceInfo) bean).getType() == 1 ? 1 : 0);
                    if (deviceInfo.getId() == null) {
                        sceneDeviceInfoMapper.insert(deviceInfo);
                    } else {
                        sceneDeviceInfoMapper.updateById(deviceInfo);
                    }
                }

            }
        } else if (bean instanceof ChintCloudDevicePointOpearteVo) { //灯
            ((ChintCloudDevicePointOpearteVo) bean).getPointList().forEach(x -> {

//                MeetingSceneControl control=sceneControlMapper.
//                        selectOne(Wrappers.<MeetingSceneControl>lambdaQuery().
//                                eq(MeetingSceneControl::getIsDel,0).
//                                eq(MeetingSceneControl::getDeviceType,2).
//                                eq(MeetingSceneControl::getDeviceId,x));


                MeetingSceneControl control = sceneControlMapper.getByDeviceTypeId(2, Integer.valueOf(x));
                if (null != control) {
                    MeetingScene meetingScene = meetingSceneMapper.
                            selectOne(Wrappers.<MeetingScene>lambdaQuery().
                                    eq(MeetingScene::getIsDel, 0).
                                    eq(MeetingScene::getMeetingId, control.getMeetingId()).
                                    eq(MeetingScene::getSceneType, 2));
                    if (null != meetingScene) {
                        MeetingSceneDeviceInfo deviceInfo = sceneDeviceInfoMapper.
                                selectOne(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                                        eq(MeetingSceneDeviceInfo::getIsDel, 0).
                                        eq(MeetingSceneDeviceInfo::getCreateUser, sysUser.getId()).
                                        eq(MeetingSceneDeviceInfo::getDeviceId, x));
                        if (null == deviceInfo) {
                            deviceInfo = new MeetingSceneDeviceInfo();
                            deviceInfo.setDeviceType(2);
                            deviceInfo.setSceneId(meetingScene.getId());
                            deviceInfo.setCreateUser(sysUser.getId());
                            deviceInfo.setDeviceId(Integer.valueOf(x));
                            deviceInfo.setIsInform(false);
                            deviceInfo.setIsDel(0);
                            deviceInfo.setId(IdUtils.fastSimpleUUID());
                        }
                        // 开关
                        deviceInfo.setIsOpen(Integer.valueOf(((ChintCloudDevicePointOpearteVo) bean).getValue()));

                        if (deviceInfo.getId() == null) {
                            sceneDeviceInfoMapper.insert(deviceInfo);
                        } else {
                            sceneDeviceInfoMapper.updateById(deviceInfo);
                        }

                    }
                }
            });
        }


    }

    public void allScene(MeetingScene bean) {
        log.error("==================会议室场景开始============================");
        List<MeetingSceneDeviceInfo> deviceInfos = null;
        // 个性化模式
        if (4 == bean.getSceneType() && null != bean.getMeetingUser()) {
            // 个人个性化
            deviceInfos = sceneDeviceInfoMapper.selectList(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                    eq(MeetingSceneDeviceInfo::getIsDel, 0).
                    eq(null != bean.getId(), MeetingSceneDeviceInfo::getSceneId, bean.getId()).
                    eq(MeetingSceneDeviceInfo::getCreateUser, bean.getMeetingUser()));

        } else {
            deviceInfos = sceneDeviceInfoMapper.
                    selectList(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                            eq(MeetingSceneDeviceInfo::getIsDel, 0).
                            eq(null != bean.getId(), MeetingSceneDeviceInfo::getSceneId, bean.getId()));
        }
        List<MeetingSceneDeviceInfo> airDevices = deviceInfos.stream().filter(x -> x.getDeviceType() == 1).collect(Collectors.toList());
        log.error("======所有场景=======" + deviceInfos.toString());
        log.error("======空调场景=======" + airDevices.toString());
        airDevices.forEach(x -> {
            AirConditionCMDVo cmdVo = new AirConditionCMDVo();
//                if (cmdVo == null) {
//                    return;
//                }
            //  设备 是否支持 面板控制
            cmdVo.setOnOff(x.getIsOpen());
            cmdVo.setTemp(x.getTemperatureSetting());
            cmdVo.setModal(x.getModelSetting());
            cmdVo.setWindSpeed(x.getWindSpeedSetting());
        });

        List<MeetingSceneDeviceInfo> lightDevices = deviceInfos.stream().filter(x -> x.getDeviceType() == 2).collect(Collectors.toList());
        log.error("======灯光场景=======" + lightDevices.toString());
        lightDevices.forEach(x -> {
            List<String> pointIds = new ArrayList<>();
            ChintCloudDevicePoint point = pointService.getById(x.getDeviceId());
            if (point == null) {
                return;
            }
            pointIds.add(point.getStoragePoint());
            pointService.switchByPoint(pointIds, String.valueOf(x.getIsOpen()));

        });


        List<MeetingSceneDeviceInfo> mediaDevices = deviceInfos.stream().filter(x -> x.getDeviceType() == 3).collect(Collectors.toList());
        log.error("======多媒体设备=======" + mediaDevices.toString());
        mediaDevices.forEach(x -> {
            Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();
            log.error("多媒体设备====1：" + x.getMediaIp());
            if (!channelMap.containsKey(x.getMediaIp())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getMediaIp(), 8889);
                            log.error("多媒体设备====2：" + x.getMediaIp() + "====成功");
                        } catch (Exception e) {
                            log.error("多媒体设备====3：" + x.getMediaIp() + "====失败");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Thread.sleep(10000);
                        // 开启
                        if (null != x.getIsOpen() &&
                                channelMap.containsKey(x.getMediaIp())) {
                            log.error("多媒体设备====4：" + x.getMediaIp() + "====成功");
                            // 任务补偿：没有反馈就连续下发十次开机，或者关机
                            for (int i = 0; i < 10; i++) {
                                if (1 == x.getIsOpen()) {
                                    log.error("多媒体设备====5：" + x.getMediaIp() + "====开机");
                                    channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00C90001FA");
                                    Thread.sleep(1000);
                                    channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00D30001FA");
                                } else {
                                    log.error("多媒体设备====6：" + x.getMediaIp() + "====关机");
                                    channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00D40001FA");
                                    Thread.sleep(1000);
                                    channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00CA0001FA");
                                }
                                Thread.sleep(1000);
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("多媒体设备====7：" + x.getMediaIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();

        });


        List<MeetingSceneDeviceInfo> curtainDevices = deviceInfos.stream().filter(x -> x.getDeviceType() == 5).collect(Collectors.toList());
        log.error("======窗帘=======" + curtainDevices.toString());
        curtainDevices.forEach(x -> {
            Curtain curtain = curtainMapper.selectById(x.getDeviceId());
            if (curtain.getGeneralControl() == 0) {
                return;
            }
            Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();
            log.error("窗帘设备====1：" + x.getCurtainIp());
            if (!channelMap.containsKey(x.getCurtainIp())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getCurtainIp(), 8889);
                            log.error("窗帘设备====2：" + x.getCurtainIp() + "====成功");
                        } catch (Exception e) {
                            log.error("窗帘设备====3：" + x.getCurtainIp() + "====失败");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Thread.sleep(10000);
                        // 开启
                        if (null != x.getGeneralControl() &&
                                channelMap.containsKey(x.getCurtainIp())) {
                            log.error("窗帘设备====4：" + x.getCurtainIp() + "====成功");
                            // 任务补偿：没有反馈就连续下发2次，或者关机
                            if (1 == x.getGeneralControl()) {
                                log.error("窗帘设备====5：" + x.getCurtainIp() + "====升");
                                channelMap.get(x.getCurtainIp()).writeAndFlush("FF06010A" + curtain.getUp() + "0001FA");
                                Thread.sleep(1000);
                                channelMap.get(x.getCurtainIp()).writeAndFlush("FF06010A" + curtain.getUp() + "0001FA");
                            } else if (2 == x.getGeneralControl()) {
                                log.error("窗帘设备====6：" + x.getCurtainIp() + "====降");
                                channelMap.get(x.getCurtainIp()).writeAndFlush("FF06010A" + curtain.getDown() + "0001FA");
                                Thread.sleep(1000);
                                channelMap.get(x.getCurtainIp()).writeAndFlush("FF06010A" + curtain.getDown() + "0001FA");
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("窗帘设备====7：" + x.getCurtainIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();

        });


        roomConfigMapper.update(null, Wrappers.<MeetingRoomConfig>lambdaUpdate().
                set(MeetingRoomConfig::getCurrentMode, bean.getId()).
                eq(MeetingRoomConfig::getId, bean.getMeetingId()));


    }

    //根据设备类型获取设备配置信息
    public List<ConfigDeviceType> getDeviceByType(Integer deviceType, String deviceTypeCode) {
        return deviceTypeMapper.selectList(new LambdaQueryWrapper<ConfigDeviceType>()
                .eq(ConfigDeviceType::getIsDel, 0)
                .eq(ConfigDeviceType::getId, deviceType)
                .eq(ConfigDeviceType::getDeviceTypeCode, deviceTypeCode));
    }

    public List<MeetingScene> getAppSceneList(MeetingScene bean) {

        List<MeetingScene> meetingScenes = meetingSceneMapper.
                selectList(new LambdaQueryWrapper<MeetingScene>()
                        .eq(MeetingScene::getIsDel, 0)
                        .eq(MeetingScene::getMeetingId, bean.getMeetingId()));

        meetingScenes.forEach(x -> {
            List<MeetingSceneDeviceInfo> deviceInfos = sceneDeviceInfoMapper.
                    selectList(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                            eq(MeetingSceneDeviceInfo::getIsDel, 0).
                            eq(MeetingSceneDeviceInfo::getSceneId, x.getId()));
            for (MeetingSceneDeviceInfo deviceInfo : deviceInfos) {
                if (deviceInfo.getDeviceType() == 5) {
                    x.setGeneralControl(deviceInfo.getGeneralControl());
                }
            }
            // 空调数量
            long airCount = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 1).count();
            if (airCount > 0) {
                // 有需要启动的空调
                long isOpen = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 1 && obj.getIsOpen() == 1).count();
                if (isOpen > 0) {
                    x.setAirStatus(0);
                } else {
                    x.setAirStatus(-1);
                }
            } else {
                x.setAirStatus(-1);
            }

            // 多媒体数量
            long mediaCount = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 3).count();
            if (mediaCount > 0) {
                // 有需要启动的空调
                long isOpen = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 3 && obj.getIsOpen() == 1).count();
                if (isOpen > 0) {
                    x.setMediaStatus(0);
                } else {
                    x.setMediaStatus(-1);
                }
            } else {
                x.setMediaStatus(-1);
            }


            // 灯光数量
            long lightCount = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 2).count();
            if (lightCount > 0) {
                // 有需要启动的空调
                long isOpen = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 2 && obj.getIsOpen() == 1).count();
                if (isOpen > 0) {
                    x.setLightStatus(0);
                } else {
                    x.setLightStatus(-1);
                }
            } else {
                x.setLightStatus(-1);
            }


        });

        return meetingScenes;
    }

    public List<MeetingScene> getAppSceneList1(MeetingScene bean) {

        List<MeetingScene> meetingScenes = meetingSceneMapper.
                selectList(new LambdaQueryWrapper<MeetingScene>()
                        .eq(MeetingScene::getIsDel, 0)
                        .eq(MeetingScene::getMeetingId, bean.getMeetingId())
                        .in(MeetingScene::getSceneType, Arrays.asList(1, 3)));

        meetingScenes.forEach(x -> {
            List<MeetingSceneDeviceInfo> deviceInfos = sceneDeviceInfoMapper.
                    selectList(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery().
                            eq(MeetingSceneDeviceInfo::getIsDel, 0).
                            eq(MeetingSceneDeviceInfo::getSceneId, x.getId()));
            for (MeetingSceneDeviceInfo deviceInfo : deviceInfos) {
                if (deviceInfo.getDeviceType() == 5) {
                    x.setGeneralControl(deviceInfo.getGeneralControl());
                }
            }
            // 空调数量
            long airCount = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 1).count();
            if (airCount > 0) {
                // 有需要启动的空调
                long isOpen = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 1 && obj.getIsOpen() == 1).count();
                if (isOpen > 0) {
                    x.setAirStatus(0);
                } else {
                    x.setAirStatus(-1);
                }
            } else {
                x.setAirStatus(-1);
            }

            // 多媒体数量
            long mediaCount = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 3).count();
            if (mediaCount > 0) {
                // 有需要启动的空调
                long isOpen = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 3 && obj.getIsOpen() == 1).count();
                if (isOpen > 0) {
                    x.setMediaStatus(0);
                } else {
                    x.setMediaStatus(-1);
                }
            } else {
                x.setMediaStatus(-1);
            }


            // 灯光数量
            long lightCount = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 2).count();
            if (lightCount > 0) {
                // 有需要启动的空调
                long isOpen = deviceInfos.stream().filter(obj -> obj.getDeviceType() == 2 && obj.getIsOpen() == 1).count();
                if (isOpen > 0) {
                    x.setLightStatus(0);
                } else {
                    x.setLightStatus(-1);
                }
            } else {
                x.setLightStatus(-1);
            }


        });

        return meetingScenes;
    }
}
