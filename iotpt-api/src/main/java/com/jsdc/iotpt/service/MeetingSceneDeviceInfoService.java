package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.curtain.Curtain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author：jxl
 * @Date：2024/6/13 13:48
 * @FileDesc：
 */
@Service
@Transactional
public class MeetingSceneDeviceInfoService extends BaseService<MeetingSceneDeviceInfo> {

    @Autowired
    private MeetingSceneDeviceInfoMapper sceneDeviceInfoMapper;

    @Autowired
    private MeetingSceneControlMapper meetingSceneControlMapper;

    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;
    @Autowired
    private MeetingSceneMapper meetingSceneMapper;
    @Autowired
    private MultimediaMapper multimediaMapper;

    @Autowired
    private DeviceCollectMapper deviceCollectMapper;

    @Autowired
    private CurtainMapper curtainMapper;

    /**
     * 场景设置 - 保存方案 测试提交
     */
    public void saveOrUpd(List<MeetingSceneDeviceInfo> bean) {
        if (CollectionUtils.isEmpty(bean)) {
            throw new RuntimeException("参数不能为空");
        }
        List<MeetingSceneDeviceInfo> saveList = new ArrayList<>();
        for (MeetingSceneDeviceInfo sceneDeviceInfo : bean) {
            if (null == sceneDeviceInfo.getSceneId()) {
                throw new RuntimeException("场景不能为空");
            }
            if (null == sceneDeviceInfo.getDeviceId()) {
                throw new RuntimeException("设备不能为空");
            }
            if (null == sceneDeviceInfo.getDeviceType()) {
                throw new RuntimeException("设备类型不能为空");
            }
            if (null == sceneDeviceInfo.getMeetingId()) {
                throw new RuntimeException("会议不能为空");
            }

            List<MeetingSceneDeviceInfo> deviceInfos = sceneDeviceInfoMapper.selectList(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery()
                    .eq(MeetingSceneDeviceInfo::getSceneId, sceneDeviceInfo.getSceneId())
                    .eq(MeetingSceneDeviceInfo::getIsDel, 0)
                    .eq(MeetingSceneDeviceInfo::getDeviceType, sceneDeviceInfo.getDeviceType())
                    .eq(MeetingSceneDeviceInfo::getDeviceId, sceneDeviceInfo.getDeviceId())
                    .eq(MeetingSceneDeviceInfo::getMeetingId, sceneDeviceInfo.getMeetingId())
            );
            if (CollectionUtils.isEmpty(deviceInfos)) {
                sceneDeviceInfo.setInformUserId(sceneDeviceInfo.getInformUserId());
                // 新增
                saveList.add(sceneDeviceInfo);
            } else {
                // 更新
                sceneDeviceInfo.setId(deviceInfos.get(0).getId());
                sceneDeviceInfo.setInformUserId(sceneDeviceInfo.getInformUserId());
                //如果修改的窗帘,则需要同时修改所有
                if (sceneDeviceInfo.getDeviceType() == 5) {
                    sceneDeviceInfoMapper.update(null, Wrappers.<MeetingSceneDeviceInfo>lambdaUpdate()
                            .eq(MeetingSceneDeviceInfo::getMeetingId, sceneDeviceInfo.getMeetingId())
                            .eq(MeetingSceneDeviceInfo::getDeviceType, 5)
                            .eq(MeetingSceneDeviceInfo::getSceneId, sceneDeviceInfo.getSceneId())
                            .set(MeetingSceneDeviceInfo::getGeneralControl, sceneDeviceInfo.getGeneralControl()));
                }
                sceneDeviceInfoMapper.updateById(sceneDeviceInfo);
            }
        }

//        for (MeetingSceneDeviceInfo sceneDeviceInfo : saveList) {
//            sceneDeviceInfo.setIsDel(G.ISDEL_NO);
//            sceneDeviceInfo.setCreateTime(new Date());
//            sceneDeviceInfo.setId(IdUtils.fastSimpleUUID());
//            sceneDeviceInfoMapper.insert(sceneDeviceInfo);
//        }
    }

    public Map<String, List<MeetingSceneDeviceInfo>> getSceneDeviceInfoBySceneId1(Integer meetingId, Integer sceneId) {
        if (null == meetingId) {
            throw new RuntimeException("会议id不能为空");
        }
        List<MeetingSceneDeviceInfo> meetingSceneDeviceInfos = sceneDeviceInfoMapper.selectList(new LambdaQueryWrapper<MeetingSceneDeviceInfo>()
                .eq(MeetingSceneDeviceInfo::getIsDel, 0)
                .eq(MeetingSceneDeviceInfo::getMeetingId, meetingId)
                .eq(null != sceneId, MeetingSceneDeviceInfo::getSceneId, sceneId)
        );

        for (MeetingSceneDeviceInfo sceneDeviceInfo : meetingSceneDeviceInfos) {
            switch (sceneDeviceInfo.getDeviceType()) {
                // 1：空调 2：照明 3：多媒体 4：人体感应 5：窗帘
                case 2:
                    ChintCloudDevicePoint point = chintCloudDevicePointMapper.selectById(sceneDeviceInfo.getDeviceId());
                    sceneDeviceInfo.setDeviceName(point.getPointName());
                    sceneDeviceInfo.setLuminance(100);
                    sceneDeviceInfo.setColor("黄色");
                    break;
                case 3:
                    Multimedia multimedia = multimediaMapper.selectById(sceneDeviceInfo.getDeviceId());
                    sceneDeviceInfo.setDeviceName(multimedia.getName());
                    sceneDeviceInfo.setMediaIp(multimedia.getMediaIp());
                    break;
                case 4:
                    DeviceCollect deviceCollect = deviceCollectMapper.selectById(sceneDeviceInfo.getDeviceId());
                    sceneDeviceInfo.setDeviceName(deviceCollect.getName());
                    break;
                case 5:
                    Curtain curtain = curtainMapper.selectById(sceneDeviceInfo.getDeviceId());
                    sceneDeviceInfo.setDeviceName(curtain.getCurtainName());
                    sceneDeviceInfo.setCurtainIp(curtain.getIp());
                    break;
                default:
                    break;
            }
        }
        Map<Integer, List<MeetingSceneDeviceInfo>> map = meetingSceneDeviceInfos.stream().collect(Collectors.groupingBy(MeetingSceneDeviceInfo::getDeviceType));
        Map<String, List<MeetingSceneDeviceInfo>> resultMap = new HashMap<>();
        map.forEach((integer, MeetingSceneDeviceInfoList) -> {
            if (integer == 1) {
                resultMap.put("airConditioner", MeetingSceneDeviceInfoList);
            }
            if (integer == 2) {
                resultMap.put("lighting", MeetingSceneDeviceInfoList);
            }
            if (integer == 3) {
                resultMap.put("multimedia", MeetingSceneDeviceInfoList);
            }
            if (integer == 4) {
                resultMap.put("induction", MeetingSceneDeviceInfoList);
            }
            if (integer == 5) {
                resultMap.put("curtain", MeetingSceneDeviceInfoList.stream().filter(x -> x.getDeviceName().contains("总控")).collect(Collectors.toList()));
            }
        });
        if (null != sceneId) {
            MeetingScene meetingScene = meetingSceneMapper.selectById(sceneId);
            if (meetingScene.getSceneType() == 4) {
                return new HashMap<>();
            }
        }
        return resultMap;
    }

    /**
     * 根据场景id 查询设备信息
     *
     * @param sceneId
     * @return
     */
    public Map<String, List<MeetingSceneControl>> getSceneDeviceInfoBySceneId(Integer meetingId, Integer sceneId) {
        if (null == meetingId) {
            throw new RuntimeException("会议id不能为空");
        }
        // 查询关联表
        List<MeetingSceneControl> sceneControls = meetingSceneControlMapper.selectList(Wrappers.<MeetingSceneControl>lambdaQuery().eq(MeetingSceneControl::getMeetingId, meetingId).eq(MeetingSceneControl::getIsDel, 0));
        if (CollectionUtils.isEmpty(sceneControls)) {
            throw new RuntimeException("会议下没有设备");
        }

        for (MeetingSceneControl sceneControl : sceneControls) {
            if (null == sceneControl.getDeviceType()) {
                continue;
            }

            //查询已选设备表
            if (null != sceneId) {
                List<MeetingSceneDeviceInfo> deviceInfos = sceneDeviceInfoMapper.selectList(Wrappers.<MeetingSceneDeviceInfo>lambdaQuery()
                        .eq(MeetingSceneDeviceInfo::getMeetingId, meetingId)
                        .eq(MeetingSceneDeviceInfo::getSceneId, sceneId)
                        .eq(MeetingSceneDeviceInfo::getIsDel, 0)
                        .eq(MeetingSceneDeviceInfo::getDeviceId, sceneControl.getDeviceId())
                );
                if (!CollectionUtils.isEmpty(deviceInfos)) {
                    sceneControl.setDevice(deviceInfos.get(0));
                    continue;
                }
            }

            MeetingSceneDeviceInfo sceneDeviceInfo = MeetingSceneDeviceInfo.builder()
                    .deviceType(sceneControl.getDeviceType())
                    .meetingId(sceneControl.getMeetingId())
                    .deviceId(sceneControl.getDeviceId())
                    .isOpen(0)
                    .build();
            switch (sceneControl.getDeviceType()) {
                // 1：空调 2：照明 3：多媒体 4：人体感应 5：窗帘
                case 2:
                    ChintCloudDevicePoint point = chintCloudDevicePointMapper.selectById(sceneControl.getDeviceId());
                    sceneDeviceInfo.setDeviceName(point.getPointName());
                    sceneDeviceInfo.setLuminance(100);
                    sceneDeviceInfo.setColor("黄色");
                    break;
                case 3:
                    Multimedia multimedia = multimediaMapper.selectById(sceneControl.getDeviceId());
                    sceneDeviceInfo.setDeviceName(multimedia.getName());
                    sceneDeviceInfo.setMediaIp(multimedia.getMediaIp());
                    break;
                case 4:
                    DeviceCollect deviceCollect = deviceCollectMapper.selectById(sceneControl.getDeviceId());
                    sceneDeviceInfo.setDeviceName(deviceCollect.getName());
                    sceneDeviceInfo.setInductionRadar("多功能厅一号雷达");
                    break;
                case 5:
                    Curtain curtain = curtainMapper.selectById(sceneDeviceInfo.getDeviceId());
                    sceneDeviceInfo.setDeviceName(curtain.getCurtainName());
                    sceneDeviceInfo.setCurtainIp(curtain.getIp());
                    break;
                default:
                    break;
            }
            sceneControl.setDevice(sceneDeviceInfo);
        }
        Map<Integer, List<MeetingSceneControl>> map = sceneControls.stream().collect(Collectors.groupingBy(MeetingSceneControl::getDeviceType));
        Map<String, List<MeetingSceneControl>> resultMap = new HashMap<>();
        map.forEach((integer, meetingSceneControls) -> {
            if (integer == 1) {
                resultMap.put("airConditioner", meetingSceneControls);
            }
            if (integer == 2) {
                resultMap.put("lighting", meetingSceneControls);
            }
            if (integer == 3) {
                resultMap.put("multimedia", meetingSceneControls);
            }
            if (integer == 4) {
                resultMap.put("induction", meetingSceneControls);
            }
            if (integer == 5) {
                // 自定义Comparator来按照名字中的数字进行排序
                meetingSceneControls.sort(new Comparator<MeetingSceneControl>() {
                    @Override
                    public int compare(MeetingSceneControl c1, MeetingSceneControl c2) {
                        // 提取名字中的数字
                        int num1 = extractNumber(c1.getDevice().getDeviceName());
                        int num2 = extractNumber(c2.getDevice().getDeviceName());

                        // 按照数字进行比较
                        return Integer.compare(num1, num2);
                    }

                    // 辅助方法，用于从字符串中提取数字
                    private int extractNumber(String s) {
                        // 使用正则表达式提取数字
                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(s);
                        if (m.find()) {
                            return Integer.parseInt(m.group());
                        } else {
                            // 如果没有找到数字，返回一个默认值，比如0
                            return 0;
                        }
                    }
                });
                resultMap.put("curtain", meetingSceneControls);
            }
        });
        return resultMap;
    }


}
