package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.curtain.Curtain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author：jxl
 * @Date：2024/6/13 13:44
 * @FileDesc：
 */
@Service
@Transactional
public class MeetingSceneControlService extends BaseService<MeetingSceneControl> {

    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;
    @Autowired
    private MultimediaMapper multimediaMapper;
    @Autowired
    private CurtainMapper curtainMapper;

    public Map getDeviceList(Integer areaId, Integer meetingId) {
        Map resultMap = new HashMap<>();
        //照明设备
        List<ChintCloudDevicePoint> devicePoints = chintCloudDevicePointMapper.selectList(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getAreaId, areaId)
        );
        List<DeviceCollect> deviceCollects = deviceCollectMapper.selectList(new LambdaQueryWrapper<DeviceCollect>()
                .eq(DeviceCollect::getIsDel, 0)
                .eq(DeviceCollect::getAreaId, areaId)
                //人体感应设备
                .eq(DeviceCollect::getDeviceType, 10013));
        //照明
        resultMap.put("devicePoints", devicePoints);
        //人体感应设备
        resultMap.put("deviceCollects", deviceCollects);
        //多媒体 获取未被引用的设备
        List<Multimedia> multimedia = multimediaMapper.selectList(new LambdaQueryWrapper<Multimedia>()
                .eq(null != meetingId, Multimedia::getIsReference, 1)
                .eq(null != meetingId, Multimedia::getMeetingId, meetingId)
                .or().eq(Multimedia::getIsReference, 0)
        );
        if (CollUtil.isEmpty(multimedia)) {
            multimedia = multimediaMapper.selectList(new LambdaQueryWrapper<Multimedia>()
                    .eq(Multimedia::getIsReference, 0)
            );
        }
        resultMap.put("multimedia", multimedia);
        //窗帘
        List<Curtain> curtains = curtainMapper.selectList(new LambdaQueryWrapper<Curtain>().eq(Curtain::getIsDel, 0).eq(Curtain::getAreaId, areaId));
        resultMap.put("curtains", curtains);
        return resultMap;
    }


    public List<MeetingSceneControl> getDeviceListByRoomId(Integer roomId) {
        List<MeetingSceneControl> list = list(Wrappers.<MeetingSceneControl>lambdaQuery()
                .eq(MeetingSceneControl::getMeetingId, roomId)
                .eq(MeetingSceneControl::getIsDel, 0)
                .ne(MeetingSceneControl::getDeviceType, 4)
        );
        // 使用 Stream API 去重，根据 deviceType 字段
        List<MeetingSceneControl> distinctList = new ArrayList<>(list.stream()
                .collect(Collectors.toMap(
                        MeetingSceneControl::getDeviceType,  // keyMapper，根据 deviceType 分组
                        Function.identity(),  // valueMapper，保留第一个出现的元素
                        (existing, replacement) -> existing  // mergeFunction，保留第一个元素
                ))
                .values());

        return distinctList;
    }
}
