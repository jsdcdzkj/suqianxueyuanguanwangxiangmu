package com.jsdc.iotpt.smartenergy.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.model.sys.SysLog;
import com.jsdc.iotpt.service.AlarmRecordsService;
import com.jsdc.iotpt.service.DeviceCollectService;
import com.jsdc.iotpt.service.MissionService;
import com.jsdc.iotpt.service.SysLogService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统概览
 */
@RestController
@RequestMapping("/systemOverview")
public class SystemOverviewController {

    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private AlarmRecordsService alarmRecordsService;
    @Autowired
    private MissionService missionService;

    /**
     * 系统概览
     */
    @PostMapping("getCenterData")
    public ResultInfo getSubitemEnergy(@RequestBody DeviceQueryVo bean) {

        try {
            JSONObject jsonObject = new JSONObject();

            DeviceCollectVo deviceCollectVo = new DeviceCollectVo();
            //智慧水表
            deviceCollectVo.setDeviceType(10006);
            //设备在线状态（1正常、2离线、3报警）
            deviceCollectVo.setOnLineStatus("1");
            Long waterOnlineCount = deviceCollectService.getDeviceCount(deviceCollectVo);
            //设备在线状态（1正常、2离线、3报警）
            deviceCollectVo.setOnLineStatus("2");
            Long waterOfflineCount = deviceCollectService.getDeviceCount(deviceCollectVo);

            //漏水检测仪
            deviceCollectVo.setDeviceType(10006);
            //设备在线状态（1正常、2离线、3报警）
            deviceCollectVo.setOnLineStatus("1");
            Long waterDetectorOnlineCount = deviceCollectService.getDeviceCount(deviceCollectVo);
            //设备在线状态（1正常、2离线、3报警）
            deviceCollectVo.setOnLineStatus("2");
            Long waterDetectorOfflineCount = deviceCollectService.getDeviceCount(deviceCollectVo);

            //设备统计-智慧水表：在线数量、离线数量
            jsonObject.put("waterOnlineCount", waterOnlineCount);
            jsonObject.put("waterOfflineCount", waterOfflineCount);
            //设备统计-漏水检测仪：在线数量、离线数量
            jsonObject.put("waterDetectorOnlineCount", waterDetectorOnlineCount);
            jsonObject.put("waterDetectorOfflineCount", waterDetectorOfflineCount);

            //设备统计：运行数量
            jsonObject.put("waterOnlineCounts", waterOnlineCount + waterDetectorOnlineCount);
            //设备统计：离线数量
            jsonObject.put("waterOfflineCounts", waterOfflineCount + waterDetectorOfflineCount);

            SysLog sysLog = sysLogService.getLastData(null);

            Long loginCount = sysLogService.getCount(null);
            //登录次数
            jsonObject.put("loginCount", loginCount);
            //控制台：上次登录IP
            jsonObject.put("logIp", sysLog.getRequestAddr());
            jsonObject.put("logTime", new DateTime(sysLog.getOperateTime()).toString("yyyy-MM-dd HH:mm:ss"));
            jsonObject.put("logLocation", sysLog.getIpGeolocation());
//            //告警数量（条）
//            Long alarmCount = alarmRecordsService.getCount(null);
//            jsonObject.put("alarmCount", alarmCount);
            //工单数量（条）
            Long orderCount = missionService.getCount(null);
            jsonObject.put("orderCount", orderCount);

            return ResultInfo.success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取数据失败");
        }

    }

}
