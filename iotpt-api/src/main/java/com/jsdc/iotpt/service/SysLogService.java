package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.ChintCloudDevicePointMapper;
import com.jsdc.iotpt.mapper.SysBuildFloorMapper;
import com.jsdc.iotpt.model.ChintCloudDevicePoint;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysLog;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.AirCMDVo;
import com.jsdc.iotpt.vo.AirConditionCMDVo;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (SysLog)表服务接口
 *
 * @author wangYan
 * @since 2023-05-09
 */
@Service
@Transactional
public class SysLogService extends BaseService<SysLog> {

    Logger logger = LoggerFactory.getLogger(SysLogService.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;

    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<SysLog> getPage(SysLog sysLog, Integer pageIndex, Integer pageSize){
        logger.info("分页查询");
        LambdaQueryWrapper<SysLog> wrapper = getWrapper(sysLog);
        // 分页查询排除这两个字段进行查询,提高查询速度
        wrapper.select(SysLog.class, e -> !Objects.equals(e.getColumn(), "responseBody"));
//        wrapper.select(SysLog.class, e -> !Objects.equals(e.getColumn(), "requestBody") && !Objects.equals(e.getColumn(), "responseBody"));
        Page<SysLog> p = baseMapper.selectPage(new Page<>(pageIndex, pageSize), wrapper);

        Map<Integer, SysUser> userMap = (Map<Integer, SysUser>) RedisUtils.getBeanValue("userDict");

        for (SysLog item : p.getRecords()){
            SysUser user = userMap.get(item.getOperator());
            if(null != user){
                item.setOperatorName(user.getRealName());
            }
            // 日志类型名称
            if (StrUtil.isNotBlank(item.getLogType())){
                item.setLogTypeName(LogEnums.getDescByValue(item.getLogType()));
            }

            try {
                refactorContent(item);
                item.setContent(item.getContent());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                continue;
            }
        }
        return p;
    }

    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;


    // 日志内容重构
    public void refactorContent (SysLog sysLog){
        if(null == sysLog || StrUtil.isBlank(sysLog.getLogType()) ){
//        if(null == sysLog || StrUtil.isBlank(sysLog.getLogType()) || StrUtil.isBlank(sysLog.getModel())){
            return;
        }
        String content = "";
        JSONArray doors = JSON.parseArray(sysLog.getRequestBody());
        if (LogEnums.LOG_ACAIR_CONTROLS.getValue().equals(sysLog.getLogType())) {
            // 空调下发指令
            content = sysLog.getContent();
            Map<String, Object> door = doors.getJSONObject(0).getInnerMap();
            // 模式，0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动
            Integer modal = MapUtils.getInteger(door, "modal");
            // 开关机 0：关机；1：开机
            Integer onOff = MapUtils.getInteger(door, "onOff");
            // 风速，无效；1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档；
            Integer windSpeed = MapUtils.getInteger(door, "windSpeed");
            String innerMachineNum = MapUtils.getString(door, "innerMachineNum");
        }else if (LogEnums.LOG_ACAIR_BATCH_CONTROLS.getValue().equals(sysLog.getLogType())) {
            // 空调下发指令
            content = sysLog.getContent();
            if (CollUtil.isNotEmpty(doors)) {
                String doorsJson = doors.getJSONObject(0).toJSONString();
                AirCMDVo cmdVos = JSONUtil.toBean(doorsJson, AirCMDVo.class);
                if (CollUtil.isNotEmpty(cmdVos.getAirVo())){
                    AirConditionCMDVo obj = cmdVos.getAirVo().get(0);
                     // 模式，0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动
                    Integer modal = obj.getModal();
                    // 开关机 0：关机；1：开机
                    Integer onOff = obj.getOnOff();
                    // 风速，无效；1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档；
                    Integer windSpeed = obj.getWindSpeed();

                    String name = "";
                    SysBuildFloor floor = sysBuildFloorMapper.selectById(cmdVos.getFloorId());
                    if (null != floor){
                        name = floor.getFloorName();
                    }

                    if (StrUtil.isNotBlank(name)){
//                        name = name.substring(0, name.length() - 1);
                        content += "(" + name + ") - "  ;
                         if (onOff == 1){
                            content += "开机成功";
//                            content += " (模式：" + getModalName(modal) + " - 风速：" + getWindSpeed(windSpeed) + ")";
                        }else {
                            content += "关机成功";
                        }
                    }
                }
            }
        }else if (LogEnums.LOG_LIGHT_CONTROLS.getValue().equals(sysLog.getLogType())){
            // 照明控制-照明开关
//            content = sysLog.getModel() + "-" + sysLog.getContent();
            content = sysLog.getContent();
            if (CollUtil.isNotEmpty(doors)) {
                Map<String, Object> door = doors.getJSONObject(0).getInnerMap();
                List<String> pointList = (List<String>) door.get("pointList");
                // 0-关，1-开
                String value = MapUtils.getString(door, "value");
                if(CollUtil.isNotEmpty(pointList)){
                    List<ChintCloudDevicePoint> points = chintCloudDevicePointMapper.selectList(Wrappers.<ChintCloudDevicePoint>lambdaQuery()
                            .in(ChintCloudDevicePoint::getStoragePoint, pointList)
                    );
                    if (CollUtil.isNotEmpty(points)){
                        content += "(" + points.get(0).getPointName() + (points.size()>1?"等":"") + ") - " ;
                        content += value.equals("1")?"开启":"关闭";
                        content += "成功";
                    }
                }
            }
        }else if (LogEnums.LOG_DOOR_CONTROLS.getValue().equals(sysLog.getLogType())){
            if (CollUtil.isNotEmpty(doors)){
                JSONObject b = doors.getJSONObject(0);
                Map<String, Object> door = doors.getJSONObject(0).getInnerMap();
                Integer status = MapUtils.getInteger(door, "status");
                switch (status){
//                    开关门状态（1、常开  2、常关 3、普通)
                    case 1:
                        content += "常开";
                        break;
                    case 2:
                        content += "常关";
                        break;
                    case 3:
                        content += "普通";
                        break;
                    default:
                        content += "未知";
                        break;
                }
            }

        } else if (LogEnums.LOG_CALL_LIFT.getValue().equals(sysLog.getLogType())) {
            String accessEquipment = JSONUtil.toJsonStr(doors.get(0));
            String dstFloor = JSONUtil.toJsonStr(doors.get(1));;
            if (StrUtil.equals("5", accessEquipment)){
                // 小程序/app-智能呼梯-专梯指令下发-11F-呼叫成功
                content = sysLog.getContent() + "-专梯指令下发-" + dstFloor + "F-呼叫成功";
            } else if (StrUtil.equals("4", accessEquipment)){
                // 小程序/app-智能呼梯-通用电梯专梯指令下发-11F-呼叫成功
                content = sysLog.getContent() + "-通用电梯专梯指令下发-" + dstFloor + "F-呼叫成功";
            }
        }
        if (StrUtil.isNotBlank(content)){
            sysLog.setContent(content);
        }
    }

    // 模式，0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动
    public String getModalName(Integer modal){
        if (null == modal){
            return "";
        }
        switch (modal){
            case 1:
                return "制冷";
            case 2:
                return "抽湿";
            case 3:
                return "送风";
            case 4:
                return "制热";
            case 5:
                return "自动";
            case 0:
                return "无效";
            default:
                return "";
        }
    }
    // 开关机 0：关机；1：开机
    public String getOnOffName(Integer onOff){
        if (null == onOff){
            return "";
        }
        switch (onOff){
            case 1:
                return "关机";
            case 0:
                return "开机";
            default:
                return "未知";
        }
    }
    // 风速，无效；1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档；
    public String getWindSpeed(Integer windSpeed){
        if (null == windSpeed){
            return "";
        }
        switch (windSpeed){
            case 1:
                return "自动风速";
            case 2:
                return "低档";
            case 3:
                return "中低档";
            case 4:
                return "中档";
            case 5:
                return "中高档";
            case 6:
                return "高档";
            default:
                return "未知";
        }
    }


    /**
     * 封装查询条件
     * @param sysLog
     * @return
     */
    private LambdaQueryWrapper<SysLog> getWrapper(SysLog sysLog){
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if(null != sysLog){
            if(StringUtils.isNotBlank(sysLog.getOperatorName())){
                List<SysUser> users = sysUserService.list(Wrappers.<SysUser>lambdaQuery()
                        .like(SysUser::getRealName, sysLog.getOperatorName())
                        .eq(SysUser::getIsDel, G.ISDEL_NO));
                if (!CollectionUtils.isEmpty(users)) {
                    wrapper.in(SysLog::getOperator, users.stream().map(SysUser::getId).collect(Collectors.toList()));
                } else {
                    wrapper.eq(SysLog::getOperator, -1);
                }
            }
            if(StringUtils.isNotBlank(sysLog.getContent())){
                wrapper.like(SysLog::getContent, sysLog.getContent());
            }
            if(StringUtils.isNotBlank(sysLog.getLogType())){
                wrapper.eq(SysLog::getLogType, sysLog.getLogType());
            }
            if(StringUtils.isNotBlank(sysLog.getStartTime()) && StringUtils.isNotBlank(sysLog.getEndTime())){
                try {
                    Date startTime = DateUtils.parseDate(sysLog.getStartTime() + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
                    Date endTime = DateUtils.parseDate(sysLog.getEndTime() + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
                    wrapper.between(SysLog::getOperateTime, startTime, endTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        wrapper.orderByDesc(SysLog::getOperateTime);
        wrapper.eq(SysLog::getIsDel, G.ISDEL_NO);
        return wrapper;
    }


    public void saveLog(String content, String logType, String model){
        try {
            SysLog sysLog = new SysLog();
            sysLog.setContent(content);
            sysLog.setContent1(content);
            sysLog.setLogType(logType);
            sysLog.setModel(model);
            sysLog.setOperator(1);
            sysLog.setOperateTime(new Date());
            sysLog.setIsDel(G.ISDEL_NO);
            save(sysLog);
        }catch (Exception e){
            logger.warn("日志记录失败:{}", e.getMessage());
        }
    }

    /**
     * 获取上次登录数据
     */
    public SysLog getLastData(SysLog bean) {
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("logType", "1");
        queryWrapper.eq("model", "智慧应用");
        queryWrapper.eq("operator", sysUserService.getUser().getId());
        queryWrapper.orderByDesc("operateTime");
        List<SysLog> sysLogs = baseMapper.selectList(queryWrapper);
        return null != sysLogs && sysLogs.size() > 0 ? sysLogs.get(0) : null;
    }

    /**
     * 获取上次登录数据
     */
    public Long getCount(SysLog bean) {
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("logType", "1");
        queryWrapper.eq("model", "智慧应用");
        queryWrapper.eq("operator", sysUserService.getUser().getId());
        return baseMapper.selectCount(queryWrapper);
    }

}

