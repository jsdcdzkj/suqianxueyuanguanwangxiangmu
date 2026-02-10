package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.tcpClient.BootNettyClient;
import com.jsdc.iotpt.common.tcpClient.CommandConfig;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.curtain.Curtain;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.HttpUtils;
import com.jsdc.iotpt.vo.DeviceControlVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ClassName: DeviceControlService
 * Description:
 * date: 2023/12/20 13:50
 *
 * @author bn
 */
@Service
@Transactional
@Slf4j
public class DeviceControlService extends BaseService<DeviceControl> {

    @Autowired
    private DeviceControlMapper mapper;

    @Autowired
    private DeviceCollectMapper collectMapper;
    @Autowired
    private DeviceGatewayMapper gatewayMapper;

    @Autowired
    private ConfigLinkMapper configLinkMapper;

    @Autowired
    private ConfigSignalTypeMapper signalTypeMapper;

    @Autowired
    private ConfigSignalTypeItemMapper itemMapper;

    @Autowired
    private InfluxdbService influxdbService;

    @Value("${linkmqtt.ip}")
    private String mqttLine;

    @Autowired
    private MeetingRoomConfigMapper configMapper;

    @Autowired
    private MeetingSceneControlService sceneControlService;
    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;

    @Autowired
    private LightingService lightingService;
    @Autowired
    private MultimediaMapper multimediaMapper;
    @Autowired
    private CurtainMapper curtainMapper;

    @Autowired
    private ChintCloudDevicePointService pointService;



    @Autowired
    private ConfigDeviceTypeMapper deviceTypeMapper;


    /**
     *  添加设备类型时组装数据
     * @param deviceTypeId
     */
    public void allByDeviceType(Integer deviceTypeId){
        // 1.根据当前类型查找关联信号模型数据类型为布尔型的数据
        List<ConfigSignalType> configSignalTypes=mapper.getSignalByDeviceTypeId(deviceTypeId);
        //2.根据类型查找所关联的采集设备
        List<DeviceCollect> collects=collectMapper.selectList(Wrappers.<DeviceCollect>lambdaQuery().
                eq(DeviceCollect::getDeviceType,deviceTypeId).eq(DeviceCollect::getIsDel,0));

        configSignalTypes.forEach(x->{
            collects.forEach(y->{
                DeviceControl deviceControl=new DeviceControl();
                deviceControl.setDeviceState("-1");
                deviceControl.setCollectId(y.getId());
                deviceControl.setIsDel(0);
                deviceControl.setSignalId(x.getId());
                deviceControl.setTypeId(deviceTypeId);
                mapper.insert(deviceControl);
            });
        });

    }


    public void editByDeviceTypeAndSignal(Integer deviceTypeId,List<String> ids,List<Integer> oldIds){
        //
        if (!oldIds.isEmpty()){
            mapper.update(null,Wrappers.<DeviceControl>lambdaUpdate().
                    set(DeviceControl::getIsDel,1).
                    eq(DeviceControl::getTypeId,deviceTypeId));
        }

        if (ids.isEmpty()){
            return;
        }


        List<ConfigSignalType> signalTypes=signalTypeMapper.selectList(Wrappers.<ConfigSignalType>lambdaQuery().
                eq(ConfigSignalType::getIsDel,0).
                eq(ConfigSignalType::getDataType,"4").
                in(ConfigSignalType::getId,ids));

        //2.根据类型查找所关联的采集设备
        List<DeviceCollect> collects=collectMapper.selectList(Wrappers.<DeviceCollect>lambdaQuery().
                eq(DeviceCollect::getDeviceType,deviceTypeId).eq(DeviceCollect::getIsDel,0));


        signalTypes.forEach(x->{
            collects.forEach(y->{
                DeviceControl deviceControl=new DeviceControl();
                deviceControl.setDeviceState("-1");
                deviceControl.setCollectId(y.getId());
                deviceControl.setIsDel(0);
                deviceControl.setSignalId(x.getId());
                deviceControl.setTypeId(deviceTypeId);
                mapper.insert(deviceControl);
            });
        });


    }


    public void editByDevice(DeviceCollect bean){

        mapper.update(null,Wrappers.<DeviceControl>lambdaUpdate().
                set(DeviceControl::getIsDel,1).
                eq(DeviceControl::getCollectId,bean.getId()));

//        DeviceCollect deviceCollect=collectMapper.selectById(deviceId);

        List<ConfigSignalType> configSignalTypes=mapper.getSignalByDeviceTypeId(bean.getDeviceType());

        configSignalTypes.forEach(x->{
            DeviceControl deviceControl=new DeviceControl();
            deviceControl.setDeviceState("-1");
            deviceControl.setCollectId(bean.getId());
            deviceControl.setIsDel(0);
            deviceControl.setSignalId(x.getId());
            deviceControl.setTypeId(bean.getDeviceType());
            mapper.insert(deviceControl);
        });
    }


    public List<DeviceControlVo> getList(DeviceControl control) {
        long startTime=System.currentTimeMillis();
        // 1.根据采集设备所属网关获取网关下采集设备是否运行正常
        DeviceCollect deviceCollect= collectMapper.selectById(control.getCollectId());

        // 网关设备
        DeviceGateway deviceGateway=gatewayMapper.selectById(deviceCollect.getGatewayId());

        ConfigLink configLink= configLinkMapper.selectById(deviceGateway.getConfigLinkId());

        String param = "clientId='"+configLink.getClientId()+"'&topic=/linkqi/sub&type=calldata&sn=" + deviceGateway.getDeviceCode();

        HttpUtils.sendPost(mqttLine + "/mqtt/getDeviceState", param);



        // 1. 获取设备信号
        List<DeviceControlVo> controlVos=mapper.getList(control);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!controlVos.isEmpty()){
            String sql="select LAST(val) AS val from datasheet where channelId='NowState' " +
                    "and deviceGatewayId='"+deviceCollect.getGatewayId()+"' "+"and deviceCollectId='"+deviceCollect.getId()+"'";
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

            controlVos.forEach(x->{
                // 信号子项
                ConfigSignalTypeItem item=itemMapper.selectOne(Wrappers.<ConfigSignalTypeItem>lambdaQuery().
                        eq(ConfigSignalTypeItem::getTypeId,x.getSignalId()).
                        eq(ConfigSignalTypeItem::getIsDel,"0"));

                x.setItem(item);
                if (!maps.isEmpty()){
                    String time=String.valueOf(maps.get(0).get("time"));
                    if (Instant.parse(time).toEpochMilli()-startTime-28800000>0){
                        x.setIsFlag(0);
                        x.setDeviceState(String.valueOf(maps.get(0).get("val")));
                        if ((Double)maps.get(0).get("val")>0d){
                            x.setSwitchValue(1);
                            x.setDeviceStateName(item.getItemVal());
                        }else {
                            x.setSwitchValue(0);
                            x.setDeviceStateName(item.getItemKey());
                        }
                    }else {
                        x.setIsFlag(-1);
                        x.setDeviceState("-1");
                        x.setDeviceStateName("获取失败");
                    }
                }
            });

        }
        return controlVos;
    }

    public String isControl(DeviceControlVo bean) {
        long startTime=System.currentTimeMillis();

        //
        DeviceControl deviceControl=mapper.selectById(bean.getId());
        // 设备
        DeviceCollect deviceCollect=collectMapper.selectById(deviceControl.getCollectId());
        // 网关
        DeviceGateway deviceGateway=gatewayMapper.selectById(deviceCollect.getGatewayId());
        // 连接
        ConfigLink configLink=configLinkMapper.selectById(deviceGateway.getConfigLinkId());
        // 信号
        ConfigSignalType configSignalType=signalTypeMapper.selectById(bean.getSignalId());


        String param = "clientId='"+configLink.getClientId()+"'&topic=/linkqi/sub&gateWayId='"+
                deviceGateway.getDeviceCode()+"'&PublicAddr='random_"+
                deviceCollect.getDeviceCode()+"'&tagid='"+configSignalType.getSignalTypeCode()+"'&type=write&value=" + (bean.getSwitchValue()==0?1:0);

        HttpUtils.sendPost(mqttLine + "/mqtt/writeControl", param);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String sql="select * from controlSheet where channelId='"+configSignalType.getSignalTypeCode()+"' " +
                "and deviceGatewayId='"+deviceCollect.getGatewayId()+"' "+"and deviceCollectId='"+deviceCollect.getId()+"' ORDER BY time desc LIMIT 1";

        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

        if (!maps.isEmpty()){
            String time=String.valueOf(maps.get(0).get("time"));
            if (Instant.parse(time).toEpochMilli()-startTime-28800000>0){
                String controlResult=String.valueOf(maps.get(0).get("controlResult"));

                return controlResult;
            }else {
                return "fail";
            }
        }

        return "fail";
    }


    public ResultInfo closeMeetDevice(String meetId) {

        long currentTime=System.currentTimeMillis();
        JSONObject resultMap=new JSONObject();

        MeetingRoomConfig meetingRoomConfig=configMapper.selectById(meetId);

        //照明设备
        List<ChintCloudDevicePoint> devicePoints = chintCloudDevicePointMapper.selectList(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getAreaId, meetingRoomConfig.getAreaId())
        );

        List<String> points=new ArrayList<>();


        for (ChintCloudDevicePoint x:devicePoints) {
            points.add(x.getStoragePoint());
        }
        log.error("========我有日志========="+currentTime+"展厅控制灯光关闭====1：");
        pointService.switchByPoint(points, String.valueOf(0));
        log.error("========我有日志========="+currentTime+"展厅控制灯光关闭====2：");

        //多媒体
        List<Multimedia> multimedia = multimediaMapper.selectList(new LambdaQueryWrapper<Multimedia>()
                .eq(Multimedia::getIsDel,0)
                .eq(Multimedia::getMeetingId, meetingRoomConfig.getId())
        );

        multimedia.forEach(x->{
            Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();
            log.error("========我有日志========="+currentTime+"展厅控制多媒体设备关闭====1：" + x.getMediaIp());
            if (!channelMap.containsKey(x.getMediaIp())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getMediaIp(), 8889);
                            log.error("========我有日志========="+currentTime+"多媒体设备关闭====2：" + x.getMediaIp() + "====成功");
                        } catch (Exception e) {
                            log.error("========我有日志========="+currentTime+"多媒体设备关闭====3：" + x.getMediaIp() + "====失败");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Thread.sleep(3000);
                        // 开启
                        log.error("========我有日志========="+currentTime+"多媒体设备关闭====4：" + x.getMediaIp() + "====成功");
                        // 任务补偿：没有反馈就连续下发十次开机，或者关机
                        for (int i = 0; i < 10; i++) {
                            log.error("========我有日志========="+currentTime+"多媒体设备关闭====5：" + x.getMediaIp() + "====开机");
                            channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00D40001FA");
                            Thread.sleep(1000);
                            channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00CA0001FA");
                        }
                    } catch (InterruptedException e) {
                        log.error("========我有日志========="+currentTime+"多媒体设备关闭====7：" + x.getMediaIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();
        });

        //窗帘
        List<Curtain> curtains = curtainMapper.selectList(new LambdaQueryWrapper<Curtain>().eq(Curtain::getIsDel, 0).eq(Curtain::getAreaId, meetingRoomConfig.getAreaId()));

        curtains.forEach(x->{
            if (x.getGeneralControl() == 0) {
                return;
            }
            Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();
            log.error("========我有日志========="+currentTime+"展厅窗帘设备上升====1：" + x.getIp());
            if (!channelMap.containsKey(x.getIp())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getIp(), 8889);
                            log.error("========我有日志========="+currentTime+"窗帘设备上升====2：" + x.getIp() + "====成功");
                        } catch (Exception e) {
                            log.error("========我有日志========="+currentTime+"窗帘设备上升====3：" + x.getIp() + "====失败");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        log.error("========我有日志========="+currentTime+"窗帘设备上升====6：" + x.getIp() + "====");
                        channelMap.get(x.getIp()).writeAndFlush("FF06010A" + x.getUp() + "0001FA");
                        Thread.sleep(1000);
                        channelMap.get(x.getIp()).writeAndFlush("FF06010A" + x.getUp() + "0001FA");
                    } catch (InterruptedException e) {
                        log.error("========我有日志========="+currentTime+"窗帘设备上升====7：" + x.getIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();
        });


        return ResultInfo.success(resultMap);

    }

    public ResultInfo getDeviceList(String meetId) {

        long currentTime=System.currentTimeMillis();

        JSONObject resultMap=new JSONObject();

        MeetingRoomConfig meetingRoomConfig=configMapper.selectById(meetId);

        //照明设备
        List<ChintCloudDevicePoint> devicePoints = chintCloudDevicePointMapper.selectList(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getAreaId, meetingRoomConfig.getAreaId())
        );

        List<String> points=new ArrayList<>();


        for (ChintCloudDevicePoint x:devicePoints) {
            points.add(x.getStoragePoint());
        }
        log.error("========我有日志========="+currentTime+"展厅控制灯光====1：");
        pointService.switchByPoint(points, String.valueOf(1));
        log.error("========我有日志========="+currentTime+"展厅控制灯光====2：");


        //多媒体
        List<Multimedia> multimedia = multimediaMapper.selectList(new LambdaQueryWrapper<Multimedia>()
                .eq(Multimedia::getIsDel,0)
                .eq(Multimedia::getMeetingId, meetingRoomConfig.getId())
        );

        multimedia.forEach(x->{
            Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();
            log.error("========我有日志========="+currentTime+"展厅控制多媒体设备====1：" + x.getMediaIp());
            if (!channelMap.containsKey(x.getMediaIp())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getMediaIp(), 8889);
                            log.error("========我有日志========="+currentTime+"多媒体设备====2：" + x.getMediaIp() + "====成功");
                        } catch (Exception e) {
                            log.error("========我有日志========="+currentTime+"多媒体设备====3：" + x.getMediaIp() + "====失败");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Thread.sleep(3000);
                        // 开启
                        log.error("========我有日志========="+currentTime+"多媒体设备====4：" + x.getMediaIp() + "====成功");
                        // 任务补偿：没有反馈就连续下发十次开机，或者关机
                        for (int i = 0; i < 10; i++) {
                            log.error("========我有日志========="+currentTime+"多媒体设备====5：" + x.getMediaIp() + "====开机");
                            channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00C90001FA");
                            Thread.sleep(1000);
                            channelMap.get(x.getMediaIp()).writeAndFlush("FF06010A00D30001FA");
                        }
                    } catch (InterruptedException e) {
                        log.error("========我有日志========="+currentTime+"多媒体设备====7：" + x.getMediaIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();
        });

        //窗帘
        List<Curtain> curtains = curtainMapper.selectList(new LambdaQueryWrapper<Curtain>().eq(Curtain::getIsDel, 0).eq(Curtain::getAreaId, meetingRoomConfig.getAreaId()));

        curtains.forEach(x->{
            if (x.getGeneralControl() == 0) {
                return;
            }
            Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();
            log.error("========我有日志========="+currentTime+"展厅窗帘设备====1：" + x.getIp());
            if (!channelMap.containsKey(x.getIp())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getIp(), 8889);
                            log.error("========我有日志========="+currentTime+"窗帘设备====2：" + x.getIp() + "====成功");
                        } catch (Exception e) {
                            log.error("========我有日志========="+currentTime+"窗帘设备====3：" + x.getIp() + "====失败");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        log.error("========我有日志========="+currentTime+"窗帘设备====6：" + x.getIp() + "====降");
                        channelMap.get(x.getIp()).writeAndFlush("FF06010A" + x.getDown() + "0001FA");
                        Thread.sleep(1000);
                        channelMap.get(x.getIp()).writeAndFlush("FF06010A" + x.getDown() + "0001FA");
                    } catch (InterruptedException e) {
                        log.error("========我有日志========="+currentTime+"窗帘设备====7：" + x.getIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();
        });



        return ResultInfo.success(resultMap);

    }
    //根据设备类型获取设备配置信息
    public List<ConfigDeviceType> getDeviceByType(Integer deviceType, String deviceTypeCode) {
        return deviceTypeMapper.selectList(new LambdaQueryWrapper<ConfigDeviceType>()
                .eq(ConfigDeviceType::getIsDel, 0)
                .eq(ConfigDeviceType::getId, deviceType)
                .eq(ConfigDeviceType::getDeviceTypeCode, deviceTypeCode));
    }

    public ResultInfo largeControl(MeetingSceneDeviceInfo deviceInfo) {

        Multimedia multimedia=multimediaMapper.selectById(deviceInfo.getDeviceId());

        Map<String, Channel> channelMap= CommandConfig.getInstance().getChannels();
        if (!channelMap.containsKey(multimedia.getMediaIp())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BootNettyClient client = new BootNettyClient();
                    try {
                        client.connect(multimedia.getMediaIp(), 8889);
                    } catch (Exception e) {
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
                    if (null != deviceInfo.getType()&&
                            channelMap.containsKey(multimedia.getMediaIp())) {
                        // 任务补偿：没有反馈就连续下发十次开机，或者关机
                        for (int i = 0; i < 10; i++) {
                            if (1 == deviceInfo.getType()) {
                                channelMap.get(multimedia.getMediaIp()).writeAndFlush("FF06010A00C90001FA");
                                channelMap.get(multimedia.getMediaIp()).writeAndFlush("FF06010A00D30001FA");
                            } else {
                                channelMap.get(multimedia.getMediaIp()).writeAndFlush("FF06010A00D40001FA");
                                channelMap.get(multimedia.getMediaIp()).writeAndFlush("FF06010A00CA0001FA");
                            }
                            Thread.sleep(1000);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        return ResultInfo.success();

    }

    public ResultInfo getMeetVideo(String meetId) {
        MeetingRoomConfig meetingRoomConfig=configMapper.selectById(meetId);
        return ResultInfo.success();
    }


}
