package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
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
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.new_alarm.AlarmContentConfig;
import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.model.sys.*;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.*;
import com.jsdc.iotpt.vo.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Sheet;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @projectName: IOT
 * @className: DeviceCollectService
 * @author: wp
 * @description:
 * @date: 2023/5/9 9:49
 */
@Service
@Transactional
public class DeviceCollectService extends BaseService<DeviceCollect> {


    @Autowired
    private ConfigSupplierMapper configSupplierMapper;
    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;
    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private SysBuildMapper sysBuildMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ConfigDeviceSubitemMapper configDeviceSubitemMapper;
    @Autowired
    private DeviceGatewayMapper deviceGatewayMapper;
    @Autowired
    private ConfigModelMapper configModelMapper;
    @Autowired
    private ConfigDeviceTypeMapper configDeviceTypeMapper;

    @Autowired
    DeviceCollectMapper deviceCollectMapper;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    MemoryCacheService memoryCacheService;

    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ConfigDeviceSignalMapMapper deviceSignalMapMapper;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private DeviceCollectService deviceCollectService;

    @Autowired
    private ConfigDeviceSignalMapMapper configDeviceSignalMapMapper;

    @Autowired
    private ConfigTopicMapper configTopicMapper;

    @Autowired
    private PeakGuPingTimeService peakGuPingTimeService;

    @Autowired
    private DeviceControlService controlService;

    @Autowired
    private DataSheetService dataSheetService ;

    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;


    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private SysFileService sysFileService;
    @Value("${jsdc.nginxPath}")
    private String nginxPath;

    @Value("${jsdc.uploadPath}")
    private String FilePath;

    @Autowired
    private WaterPriceService waterPriceService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;

    @Autowired
    private ConfigTopicService configTopicService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysBuildService sysBuildService;



    @Autowired
    private DeviceEnergyUseMapper deviceEnergyUseMapper;
    @Autowired
    private SubmiteValueMapper submiteValueMapper;


    @Autowired
    private AlarmContentConfigMapper alarmContentConfigMapper;
    @Autowired
    private AlarmRecordsService alarmRecordsService;
    /**
     * 获取设备信息
     *
     * @param deviceType
     * @param floor
     * @param deviceId
     */
    public List<DeviceCollect> getDeviceCollects(String deviceType, Integer floor, Integer deviceId) {
        LambdaQueryWrapper<DeviceCollect> queryWrapper = new LambdaQueryWrapper();
        if (null != deviceId) {
            queryWrapper.eq(DeviceCollect::getId, deviceId);
        }

        if (StringUtils.isNotEmpty(deviceType)) {
            queryWrapper.eq(DeviceCollect::getDeviceType, deviceType);
        }

        if (StringUtils.isNotNull(floor) && floor.intValue() >= 0) {
            queryWrapper.eq(DeviceCollect::getFloorId, floor);
        }

        queryWrapper.eq(DeviceCollect::getIsDel, G.ISDEL_NO);
        return deviceCollectMapper.selectList(queryWrapper);
    }

    /**
     * 分页查询
     */
    public Page<DeviceCollectVo> getPageList(DeviceCollectVo vo) {
//        LambdaQueryWrapper<DeviceCollect> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(DeviceCollect::getIsDel, G.ISDEL_NO);
//        return deviceCollectMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

        Page<DeviceCollectVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<DeviceCollectVo> pageList = deviceCollectMapper.getEntityList(page, vo);

        for (DeviceCollectVo bean : pageList.getRecords()) {
            //用区域查楼层 物理位置
            if (StringUtils.isNotNull(bean.getAreaId())) {
                SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(bean.getAreaId());
                if (StringUtils.isNotNull(sysBuildArea)) {
                    //查楼宇
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        bean.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea.getAreaName());
                    }
                }
            }

            //用区域查楼层 逻辑位置
            if (StringUtils.isNotNull(bean.getLogicalAreaId()) && bean.getLogicalAreaId() != 0) {
                SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(bean.getLogicalAreaId());
                if (StringUtils.isNotNull(sysBuildArea2)) {
                    //查楼层
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        //查楼宇
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        bean.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                    }
                }
            } else if (StringUtils.isNotNull(bean.getLogicalFloorId()) && bean.getLogicalFloorId() != 0) {
                //查楼层
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(bean.getLogicalFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    bean.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
                }
            } else if (StringUtils.isNotNull(bean.getLogicalBuildId()) && bean.getLogicalBuildId() != 0) {
                //查楼宇
                SysBuild sysBuild = sysBuildMapper.selectById(bean.getLogicalBuildId());
                bean.setLogicalAreaNames(sysBuild.getBuildName());
            }
        }
        return pageList;
    }

    /**
     * 列表查询
     */
    public List<DeviceCollectVo> getList(DeviceCollectVo vo) {
//        LambdaQueryWrapper<DeviceCollect> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(DeviceCollect::getIsDel, G.ISDEL_NO);
//        return deviceCollectMapper.selectList(queryWrapper);

        List<DeviceCollectVo> list = deviceCollectMapper.getList(vo);
        for (DeviceCollectVo bean : list) {
            //用区域查楼层 物理位置
            if (StringUtils.isNotNull(bean.getAreaId())) {
                SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(bean.getAreaId());
                if (StringUtils.isNotNull(sysBuildArea)) {
                    //查楼宇
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        bean.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea.getAreaName());
                    }
                }
            }

            //用区域查楼层 逻辑位置
            if (StringUtils.isNotNull(bean.getLogicalAreaId()) && bean.getLogicalAreaId() != 0) {
                SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(bean.getLogicalAreaId());
                if (StringUtils.isNotNull(sysBuildArea2)) {
                    //查楼层
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        //查楼宇
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        bean.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                        bean.setLogicalAreaName(sysBuildArea2.getAreaName());
                    }
                }
            } else if (StringUtils.isNotNull(bean.getLogicalFloorId()) && bean.getLogicalFloorId() != 0) {
                //查楼层
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(bean.getLogicalFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    bean.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
                }
            } else if (StringUtils.isNotNull(bean.getLogicalBuildId()) && bean.getLogicalBuildId() != 0) {
                //查楼宇
                SysBuild sysBuild = sysBuildMapper.selectById(bean.getLogicalBuildId());
                bean.setLogicalAreaNames(sysBuild.getBuildName());
            }
        }
        return list;
    }

    /**
     * 列表查询
     */
    public List<DeviceCollect> getListNew(DeviceCollectVo vo) {
        List<DeviceCollect> collectList = new ArrayList<>();

        List<DeviceCollect> list = new ArrayList<>();
        List<DeviceCollect> list2 = new ArrayList<>();
        List<DeviceCollect> list3 = new ArrayList<>();

        if (StringUtils.isNotNull(vo.getLogicalBuildIds()) && vo.getLogicalBuildIds().size() > 0) {
            //楼宇
            LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
            wrapper.eq(StringUtils.isNotNull(vo.getLogicalBuildId()), DeviceCollect::getLogicalBuildId, vo.getLogicalBuildId())
                    .in(StringUtils.isNotNull(vo.getLogicalBuildIds()) && vo.getLogicalBuildIds().size() > 0, DeviceCollect::getLogicalBuildId, vo.getLogicalBuildIds())
                    .eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                    .in(StringUtils.isNotNull(vo.getDeviceTypes()) && vo.getDeviceTypes().size() > 0, DeviceCollect::getDeviceType, vo.getDeviceTypes())
                    .eq(DeviceCollect::getLogicalFloorId, 0)
                    .eq(DeviceCollect::getLogicalAreaId, 0)
//                .eq(DeviceCollect::getIsTotalDevice, 1)
                    .eq(DeviceCollect::getIsDel, 0);
            list = deviceCollectMapper.selectList(wrapper);
        }

        if (StringUtils.isNotNull(vo.getLogicalFloorIds()) && vo.getLogicalFloorIds().size() > 0) {
            //楼层
            LambdaQueryWrapper<DeviceCollect> wrapper2 = new LambdaQueryWrapper();
            wrapper2.eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                    .in(StringUtils.isNotNull(vo.getDeviceTypes()) && vo.getDeviceTypes().size() > 0, DeviceCollect::getDeviceType, vo.getDeviceTypes())
                    .eq(StringUtils.isNotNull(vo.getLogicalFloorId()), DeviceCollect::getLogicalFloorId, vo.getLogicalFloorId())
                    .in(StringUtils.isNotNull(vo.getLogicalFloorIds()) && vo.getLogicalFloorIds().size() > 0, DeviceCollect::getLogicalFloorId, vo.getLogicalFloorIds())
                    .eq(DeviceCollect::getLogicalAreaId, 0)
//                .eq(DeviceCollect::getIsTotalDevice, 1)
                    .eq(DeviceCollect::getIsDel, 0);
            list2 = deviceCollectMapper.selectList(wrapper2);
        }

        if (StringUtils.isNotNull(vo.getLogicalAreaIdList()) && vo.getLogicalAreaIdList().size() > 0) {
            //区域
            LambdaQueryWrapper<DeviceCollect> wrapper3 = new LambdaQueryWrapper();
            wrapper3.eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                    .in(StringUtils.isNotNull(vo.getDeviceTypes()) && vo.getDeviceTypes().size() > 0, DeviceCollect::getDeviceType, vo.getDeviceTypes())
                    .eq(StringUtils.isNotNull(vo.getLogicalAreaId()), DeviceCollect::getLogicalAreaId, vo.getLogicalAreaId())
                    .in(StringUtils.isNotNull(vo.getLogicalAreaIdList()) && vo.getLogicalAreaIdList().size() > 0, DeviceCollect::getLogicalAreaId, vo.getLogicalAreaIdList())
//                .eq(DeviceCollect::getIsTotalDevice, 1)
                    .eq(DeviceCollect::getIsDel, 0);
            list3 = deviceCollectMapper.selectList(wrapper3);
        }

        collectList = Stream.of(list, list2, list3)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

//        Set<DeviceCollect> set1 = new HashSet<>(list);
//        Set<DeviceCollect> set2 = new HashSet<>(list2);
//        Set<DeviceCollect> set3 = new HashSet<>(list3);
//        List<DeviceCollect> collectList2 = new ArrayList<>();
//
//        collectList2.addAll(set1);
//        collectList2.addAll(set2);
//        collectList2.addAll(set3);

        return collectList;
    }

    /**
     * 新增/编辑
     */
    public ResultInfo saveOrUpdateDeviceCollect(DeviceCollect bean) {
        String msg = "";
        if (StringUtils.isNotNull(bean.getId())) {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUserService.getUser().getId());

//            if (StringUtils.isNotNull(bean.getIsTotalDevice()) && bean.getIsTotalDevice() == 1) {
//                LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
//                wrapper.eq(StringUtils.isNotNull(bean.getLogicalAreaId()) && bean.getLogicalAreaId() != 0, DeviceCollect::getLogicalAreaId, bean.getLogicalAreaId())
//                        .eq(StringUtils.isNotNull(bean.getLogicalFloorId()) && bean.getLogicalFloorId() != 0, DeviceCollect::getLogicalFloorId, bean.getLogicalFloorId())
//                        .eq(StringUtils.isNotNull(bean.getLogicalBuildId()) && bean.getLogicalBuildId() != 0, DeviceCollect::getLogicalBuildId, bean.getLogicalBuildId())
//                        .eq(DeviceCollect::getIsDel, 0)
//                        .notIn(DeviceCollect::getId, bean.getId());
//                List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);
//                if (list.size() > 0) {
//                    return ResultInfo.error("该逻辑位置已存在总设备");
//                }
//            }

            if (StringUtils.isNotEmpty(bean.getDeviceCode()) && StringUtils.isNotNull(bean.getGatewayId())) {
                LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
                wrapper.eq(DeviceCollect::getDeviceCode, bean.getDeviceCode())
                        .eq(DeviceCollect::getGatewayId, bean.getGatewayId())
                        .eq(DeviceCollect::getIsDel, 0)
                        .notIn(DeviceCollect::getId, bean.getId());
                List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);
                if (list.size() > 0) {
                    return ResultInfo.error("该网关下已存在该设备编码");
                }
            }

            msg = "修改";
        } else {
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
            bean.setIsDel(0);
//            if (StringUtils.isNotNull(bean.getIsTotalDevice()) && bean.getIsTotalDevice() == 1) {
//                LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
//                wrapper.eq(StringUtils.isNotNull(bean.getLogicalAreaId()) && bean.getLogicalAreaId() != 0, DeviceCollect::getLogicalAreaId, bean.getLogicalAreaId())
//                        .eq(StringUtils.isNotNull(bean.getLogicalFloorId()) && bean.getLogicalFloorId() != 0, DeviceCollect::getLogicalFloorId, bean.getLogicalFloorId())
//                        .eq(StringUtils.isNotNull(bean.getLogicalBuildId()) && bean.getLogicalBuildId() != 0, DeviceCollect::getLogicalBuildId, bean.getLogicalBuildId())
//                        .eq(DeviceCollect::getIsDel, 0);
//                List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);
//                if (list.size() > 0) {
//                    return ResultInfo.error("该逻辑位置已存在总设备");
//                }
//            }

            if (StringUtils.isNotEmpty(bean.getDeviceCode()) && StringUtils.isNotNull(bean.getGatewayId())) {
                LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
                wrapper.eq(DeviceCollect::getDeviceCode, bean.getDeviceCode())
                        .eq(DeviceCollect::getGatewayId, bean.getGatewayId())
                        .eq(DeviceCollect::getIsDel, 0);
                List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);
                if (list.size() > 0) {
                    return ResultInfo.error("该网关下已存在该设备编码");
                }
            }
            msg = "新增";
            //设备在线状态（1正常、2离线、3异常）
            bean.setOnLineStatus("1");
        }
        if (Objects.nonNull(bean.getBindSwitch())) {
            if (0 == bean.getBindSwitch()) {
                bean.setSwitchIds("");
            } else {
                if (CollUtil.isNotEmpty(bean.getSwitchIdList())) {
                    bean.setSwitchIds(String.join(",", bean.getSwitchIdList()));
                } else {
                    return ResultInfo.error("请选择关联的空开设备");
                }
            }
        }
        saveOrUpdate(bean);
        //
        controlService.editByDevice(bean);
        //当采集设备修改时更新redis中的信息
        memoryCacheService.changeDeviceCollectVo();
        return ResultInfo.success("操作成功", new LogVo(msg));
    }

    /**
     * 查看
     */
    public DeviceCollectVo getDeviceCollect(Integer id) {
        DeviceCollect bean = deviceCollectMapper.selectById(id);
        DeviceCollectVo vo = new DeviceCollectVo();
        BeanUtils.copyProperties(bean, vo);
        // 绑定空开设备
        if (StringUtils.isNotBlank(vo.getSwitchIds())) {
            List<String> list = Arrays.asList(vo.getSwitchIds().split(","));
            vo.setSwitchIdList(list);
        }
        // 供应商
        if (StringUtils.isNotNull(vo.getSupplierId())) {
            ConfigSupplier configSupplier = configSupplierMapper.selectById(vo.getSupplierId());
            if (StringUtils.isNotNull(configSupplier)) {
                vo.setSupplierName(configSupplier.getSupplierName());
            }
        }
//        // 楼层
//        if (StringUtils.isNotNull(vo.getFloorId())) {
//            SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(vo.getFloorId());
//            if (StringUtils.isNotNull(sysBuildFloor)) {
//                vo.setFloorName(sysBuildFloor.getFloorName());
//            }
//        }
//        // 区域
//        if (StringUtils.isNotNull(vo.getAreaId())) {
//            SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(vo.getAreaId());
//            if (StringUtils.isNotNull(sysBuildArea)) {
//                vo.setAreaName(sysBuildArea.getAreaName());
//            }
//        }
//        // 楼宇
//        if (StringUtils.isNotNull(vo.getBuildId())) {
//            SysBuild sysBuild = sysBuildMapper.selectById(vo.getBuildId());
//            if (StringUtils.isNotNull(sysBuild)) {
//                vo.setBuildName(sysBuild.getBuildName());
//            }
//        }
        // 管理员
        if (StringUtils.isNotNull(vo.getUserId())) {
            SysUser sysUser = sysUserMapper.selectById(vo.getUserId());
            if (StringUtils.isNotNull(sysUser)) {
                vo.setUserName(sysUser.getRealName());
            }
        }
        // 维保公司
        if (StringUtils.isNotNull(vo.getCompanyId())) {
            HashMap<String, SysDict> companyMap = RedisDictDataUtil.getDictByType("supplierType");
            vo.setCompanyName(companyMap.get(vo.getCompanyId()).getDictLabel());
        }
        // 分项
        if (StringUtils.isNotNull(vo.getSubitem())) {
            ConfigDeviceSubitem configDeviceSubitem = configDeviceSubitemMapper.selectById(vo.getSubitem());
            if (StringUtils.isNotNull(configDeviceSubitem)) {
                vo.setSubitemName(configDeviceSubitem.getSubitemName());
            }
        }
        // 网关设备
        if (StringUtils.isNotNull(vo.getGatewayId())) {
            DeviceGateway deviceGateway = deviceGatewayMapper.selectById(vo.getGatewayId());
            if (StringUtils.isNotNull(deviceGateway)) {
                vo.setGatewayName(deviceGateway.getName());
            }
        }
        // 状态
        if (StringUtils.isNotNull(vo.getStatus())) {
            HashMap<String, SysDict> statusMap = RedisDictDataUtil.getDictByType("deviceStatus");
            vo.setStatusName(statusMap.get(vo.getStatus()).getDictLabel());
        }
        // 设备型号
        if (StringUtils.isNotNull(vo.getModelNum())) {
            ConfigModel configModel = configModelMapper.selectById(vo.getModelNum());
            if (StringUtils.isNotNull(configModel)) {
                vo.setModelName(configModel.getModelName());
            }
        }
        // 设备类型
        if (StringUtils.isNotNull(vo.getDeviceType())) {
            ConfigDeviceType configDeviceType = configDeviceTypeMapper.selectById(vo.getDeviceType());
            if (StringUtils.isNotNull(configDeviceType)) {
                vo.setDeviceTypeName(configDeviceType.getDeviceTypeName());
            }
        }

        if (StringUtils.isNotNull(bean.getAreaId())) {
            //用区域查楼层 物理位置
            SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(bean.getAreaId());
            if (StringUtils.isNotNull(sysBuildArea)) {
                //查楼宇
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(sysBuildFloor.getDictBuilding().toString());
                    arrayList.add(sysBuildArea.getFloorId().toString());
                    arrayList.add(bean.getAreaId().toString());
                    vo.setAreaIds(arrayList);

                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    vo.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea.getAreaName());
                }
            }
        }

        //用区域查楼层 逻辑位置
        if (StringUtils.isNotNull(bean.getLogicalAreaId()) && bean.getLogicalAreaId() != 0) {
            SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(bean.getLogicalAreaId());
            if (StringUtils.isNotNull(sysBuildArea2)) {
                //查楼层
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(sysBuildFloor.getDictBuilding().toString());
                    arrayList.add(sysBuildArea2.getFloorId().toString());
                    arrayList.add(bean.getLogicalAreaId().toString());
                    vo.setLogicalAreaIds(arrayList);

                    //查楼宇
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    vo.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                }
            }
        } else if (StringUtils.isNotNull(bean.getLogicalFloorId()) && bean.getLogicalFloorId() != 0) {
            //查楼层
            SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(bean.getLogicalFloorId());
            if (StringUtils.isNotNull(sysBuildFloor)) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(sysBuildFloor.getDictBuilding().toString());
                arrayList.add(bean.getLogicalFloorId().toString());
                vo.setLogicalAreaIds(arrayList);

                SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                vo.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
            }
        } else if (StringUtils.isNotNull(bean.getLogicalBuildId()) && bean.getLogicalBuildId() != 0) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(bean.getLogicalBuildId().toString());
            vo.setLogicalAreaIds(arrayList);

            //查楼宇
            SysBuild sysBuild = sysBuildMapper.selectById(bean.getLogicalBuildId());
            vo.setLogicalAreaNames(sysBuild.getBuildName());
        }
        return vo;
    }

    /**
     * 删除
     */
    public void deleteDeviceCollect(Integer id) {
        DeviceCollect obj = deviceCollectMapper.selectById(id);
        obj.setIsDel(G.ISDEL_YES);
        deviceCollectMapper.updateById(obj);
    }

    /**
     * 批量编辑
     */
    public void batchEditDeviceCollect(DeviceCollectVo bean) {
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getVoList()) && bean.getVoList().size() > 0) {
                List<Integer> idList = bean.getVoList().stream().map(DeviceCollectVo::getId).collect(Collectors.toList());

                int result = deviceCollectMapper.update(null, Wrappers.<DeviceCollect>lambdaUpdate()
                        .set(StringUtils.isNotNull(bean.getDeviceType()), DeviceCollect::getDeviceType, bean.getDeviceType())
                        .set(StringUtils.isNotEmpty(bean.getStatus()), DeviceCollect::getStatus, bean.getStatus())
                        .set(StringUtils.isNotEmpty(bean.getPlace()), DeviceCollect::getPlace, bean.getPlace())
                        .set(StringUtils.isNotEmpty(bean.getOnLineStatus()), DeviceCollect::getOnLineStatus, bean.getOnLineStatus())
                        .set(StringUtils.isNotEmpty(bean.getLoad()), DeviceCollect::getLoad, bean.getLoad())
                        .set(StringUtils.isNotNull(bean.getLogicalAreaId()), DeviceCollect::getLogicalAreaId, bean.getLogicalAreaId())
                        .set(StringUtils.isNotNull(bean.getLogicalFloorId()), DeviceCollect::getLogicalFloorId, bean.getLogicalFloorId())
                        .set(StringUtils.isNotNull(bean.getLogicalBuildId()), DeviceCollect::getLogicalBuildId, bean.getLogicalBuildId())
                        .set(StringUtils.isNotNull(bean.getAreaId()), DeviceCollect::getAreaId, bean.getAreaId())
                        .set(StringUtils.isNotNull(bean.getFloorId()), DeviceCollect::getFloorId, bean.getAreaId())
                        .set(StringUtils.isNotNull(bean.getBuildId()), DeviceCollect::getBuildId, bean.getBuildId())
                        .set(StringUtils.isNotNull(bean.getSubitem()), DeviceCollect::getSubitem, bean.getSubitem())
                        .set(StringUtils.isNotNull(bean.getInstallationDate()), DeviceCollect::getInstallationDate, bean.getInstallationDate())
                        .set(StringUtils.isNotNull(bean.getUseTime()), DeviceCollect::getUseTime, bean.getUseTime())
                        .set(StringUtils.isNotNull(bean.getUserId()), DeviceCollect::getUserId, bean.getUserId())
                        .set(StringUtils.isNotEmpty(bean.getPhone()), DeviceCollect::getPhone, bean.getPhone())
                        .set(StringUtils.isNotNull(bean.getSupplierId()), DeviceCollect::getSupplierId, bean.getSupplierId())
                        .set(StringUtils.isNotNull(bean.getModelNum()), DeviceCollect::getModelNum, bean.getModelNum())
                        .set(StringUtils.isNotEmpty(bean.getCompanyId()), DeviceCollect::getCompanyId, bean.getCompanyId())
                        .set(StringUtils.isNotNull(bean.getInspectionDate()), DeviceCollect::getInspectionDate, bean.getInspectionDate())
                        .set(StringUtils.isNotNull(bean.getProcureDate()), DeviceCollect::getProcureDate, bean.getProcureDate())
                        .set(StringUtils.isNotNull(bean.getExpirationDate()), DeviceCollect::getExpirationDate, bean.getExpirationDate())
                        .in(DeviceCollect::getId, idList));
//                System.out.println("批量编辑----------");
//                System.out.println(result);
            }
        }


    }

    /**
     * 导出
     */
    public void exportDeviceCollect(DeviceCollectVo vo, HttpServletResponse response) {
        // 查询数据
        List<DeviceCollectVo> deviceCollects = getList(vo);
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.addHeaderAlias("name", "设备名称");
        writer.addHeaderAlias("modelName", "设备型号");
        writer.addHeaderAlias("supplierName", "供应商");
        writer.addHeaderAlias("deviceCode", "设备编码");
        writer.addHeaderAlias("deviceTypeName", "设备类型");
        writer.addHeaderAlias("statusName", "设备状态");
        writer.addHeaderAlias("areaNames", "物理位置");
        writer.addHeaderAlias("logicalAreaNames", "逻辑位置");
        writer.addHeaderAlias("isTotalDeviceName", "是否总设备");
        writer.addHeaderAlias("place", "安装位置");
        writer.addHeaderAlias("installationDateStr", "安装日期");
        writer.addHeaderAlias("useTimeStr", "使用日期");
        writer.addHeaderAlias("userName", "设备管理员");
        writer.addHeaderAlias("phone", "管理员电话");
        writer.addHeaderAlias("companyName", "维保公司");
        writer.addHeaderAlias("inspectionDateStr", "年检日期");
        writer.addHeaderAlias("procureDateStr", "采购日期");
        writer.addHeaderAlias("expirationDateStr", "过保日期");
        writer.addHeaderAlias("subitemName", "所属分项");
        writer.addHeaderAlias("deviceDesc", "设备描述");
        writer.addHeaderAlias("gatewayName", "网关设备");
        writer.setOnlyAlias(true);
        writer.write(deviceCollects, true);
        OutputStream outputStream = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("123.xls", "UTF-8"));
            outputStream = response.getOutputStream();
            writer.flush(outputStream, true);
//            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * done List
     * 对字典值进行赋值
     */
    public List<DeviceCollect> afterList(List<DeviceCollect> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        // 减少数据库交互,把相关的id取出来
        List<Integer> suppliers = list.stream().map(DeviceCollect::getSupplierId).collect(Collectors.toList());
        // 供应商
        Map<Integer, ConfigSupplier> supplierMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(suppliers)) {
            supplierMap = configSupplierMapper.selectList(Wrappers.<ConfigSupplier>lambdaQuery().in(ConfigSupplier::getId, suppliers)).stream().collect(Collectors.toMap(ConfigSupplier::getId, configSupplier -> configSupplier));
        }
        List<Integer> floors = list.stream().map(DeviceCollect::getFloorId).collect(Collectors.toList());
        // 楼层
        Map<Integer, SysBuildFloor> floorMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(floors)) {
            floorMap = sysBuildFloorMapper.selectList(Wrappers.<SysBuildFloor>lambdaQuery().in(SysBuildFloor::getId, floors)).stream().collect(Collectors.toMap(SysBuildFloor::getId, sysBuildFloor -> sysBuildFloor));
        }
        List<Integer> areas = list.stream().map(DeviceCollect::getAreaId).collect(Collectors.toList());
        // 区域
        Map<Integer, SysBuildArea> areaMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(areas)) {
            areaMap = sysBuildAreaMapper.selectList(Wrappers.<SysBuildArea>lambdaQuery().in(SysBuildArea::getId, areas)).stream().collect(Collectors.toMap(SysBuildArea::getId, sysBuildArea -> sysBuildArea));
        }
        List<Integer> builds = list.stream().map(DeviceCollect::getBuildId).collect(Collectors.toList());
        // 楼宇
        Map<Integer, SysBuild> buildMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(builds)) {
            buildMap = sysBuildMapper.selectList(Wrappers.<SysBuild>lambdaQuery().in(SysBuild::getId, builds)).stream().collect(Collectors.toMap(SysBuild::getId, sysBuild -> sysBuild));
        }
        List<Integer> users = list.stream().map(DeviceCollect::getUserId).collect(Collectors.toList());
        // 管理员
        Map<Integer, SysUser> userMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(users)) {
            userMap = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().in(SysUser::getId, users)).stream().collect(Collectors.toMap(SysUser::getId, sysUser -> sysUser));
        }
//        List<Integer> companies = list.stream().map(DeviceCollect::getCompanyId).collect(Collectors.toList());
//        // 维保公司
//        Map<Integer, ConfigSupplier> companyMap = new HashMap<>();
//        if (!CollectionUtils.isEmpty(companies)) {
//            companyMap = configSupplierMapper.selectList(Wrappers.<ConfigSupplier>lambdaQuery().in(ConfigSupplier::getId, companies)).stream().collect(Collectors.toMap(ConfigSupplier::getId, configSupplier -> configSupplier));
//        }
        List<Integer> subitems = list.stream().map(DeviceCollect::getSubitem).collect(Collectors.toList());
        // 分项
        Map<Integer, ConfigDeviceSubitem> subitemMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(subitems)) {
            subitemMap = configDeviceSubitemMapper.selectList(Wrappers.<ConfigDeviceSubitem>lambdaQuery().in(ConfigDeviceSubitem::getId, subitems)).stream().collect(Collectors.toMap(ConfigDeviceSubitem::getId, configDeviceSubitem -> configDeviceSubitem));
        }

        List<Integer> gateways = list.stream().map(DeviceCollect::getGatewayId).collect(Collectors.toList());
        // 网关设备
        Map<Integer, DeviceGateway> gatewayMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(gateways)) {
            gatewayMap = deviceGatewayMapper.selectList(Wrappers.<DeviceGateway>lambdaQuery().in(DeviceGateway::getId, gateways)).stream().collect(Collectors.toMap(DeviceGateway::getId, deviceGateway -> deviceGateway));
        }
        //设备状态 1未使用;  2使用中;  3损坏;  4维修中
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("1", "未使用");
        statusMap.put("2", "使用中");
        statusMap.put("3", "损坏");
        statusMap.put("4", "维修中");

//        for (DeviceCollect deviceCollect : list) {
//            // 供应商
//            if (CommonUtils.isMapNotEmpty(supplierMap, deviceCollect.getSupplierId())) {
//                deviceCollect.setSupplierName(supplierMap.get(deviceCollect.getSupplierId()).getSupplierName());
//            }
//            // 楼层
//            if (CommonUtils.isMapNotEmpty(floorMap, deviceCollect.getFloorId())) {
//                deviceCollect.setFloorName(floorMap.get(deviceCollect.getFloorId()).getFloorName());
//            }
//            // 区域
//            if (CommonUtils.isMapNotEmpty(areaMap, deviceCollect.getAreaId())) {
//                deviceCollect.setAreaName(areaMap.get(deviceCollect.getAreaId()).getAreaName());
//            }
//            // 楼宇
//            if (CommonUtils.isMapNotEmpty(buildMap, deviceCollect.getBuildId())) {
//                deviceCollect.setBuildName(buildMap.get(deviceCollect.getBuildId()).getBuildName());
//            }
//            // 管理员
//            if (CommonUtils.isMapNotEmpty(userMap, deviceCollect.getUserId())) {
//                deviceCollect.setUserName(userMap.get(deviceCollect.getUserId()).getRealName());
//            }
//            // 维保公司
//            if (CommonUtils.isMapNotEmpty(companyMap, deviceCollect.getCompanyId())) {
//                deviceCollect.setCompanyName(companyMap.get(deviceCollect.getCompanyId()).getSupplierName());
//            }
//            // 分项
//            if (CommonUtils.isMapNotEmpty(subitemMap, deviceCollect.getSubitem())) {
//                deviceCollect.setSubitemName(subitemMap.get(deviceCollect.getSubitem()).getSubitemName());
//            }
//            // 网关设备
//            if (CommonUtils.isMapNotEmpty(gatewayMap, deviceCollect.getGatewayId())) {
//                deviceCollect.setGatewayName(gatewayMap.get(deviceCollect.getGatewayId()).getName());
//            }
////            // 状态
////            if (CommonUtils.isMapNotEmpty(statusMap, deviceCollect.getStatus())) {
////                deviceCollect.setStatusName(statusMap.get(deviceCollect.getStatus()));
////            }
//        }

        return list;
    }

    public ResultInfo bindDevice(DeviceCollectVo bean) {

        String[] ids = bean.getCheckIds().split(",");
        for (String id : ids) {
            if (Strings.isNotEmpty(id)) {
                LambdaUpdateWrapper<DeviceCollect> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
                lambdaQueryWrapper.set(DeviceCollect::getGatewayId, bean.getGatewayId());
                lambdaQueryWrapper.eq(DeviceCollect::getId, id);
                update(lambdaQueryWrapper);
            }
        }

        return ResultInfo.success();
    }

    public ResultInfo delBindDevice(DeviceCollectVo bean) {

        LambdaUpdateWrapper<DeviceCollect> lambdaUpdateWrapper = new LambdaUpdateWrapper();


        lambdaUpdateWrapper.set(DeviceCollect::getGatewayId, "");
        lambdaUpdateWrapper.eq(DeviceCollect::getId, bean.getId());

        update(lambdaUpdateWrapper);

        return ResultInfo.success();
    }

    /**
     * 分类实时状态统计
     *
     * @param vo
     * @return
     */
    public List<DeviceCollectVo> getDeviceStatus(DeviceCollectVo vo) {
        // 获取设备状态列表
        List<DeviceCollectVo> list = deviceCollectMapper.getDeviceStatus(vo);
        // 获取设备类型映射表
        Map<Integer, String> deviceDescMap = configDeviceTypeService.getConfigDeviceTypeMapDesc(new ConfigDeviceType());
        Map<Integer, String> deviceCodeMap = configDeviceTypeService.getConfigDeviceTypeMapCode(new ConfigDeviceType());

        for (DeviceCollectVo deviceCollectVo : list) {
            deviceCollectVo.setYs(deviceDescMap.get(deviceCollectVo.getDeviceType()));
            deviceCollectVo.setDeviceTypeCode(deviceCodeMap.get(deviceCollectVo.getDeviceType()));
        }

        return list;
    }

    /**
     * 根据楼宇查询总设备
     * 查询条件：
     * 设备类型为电表/水表
     * 楼宇不为空，楼层和区域为空
     * 设备为总设备
     */
    public List<DeviceCollect> getListByBuildId(DeviceCollectVo vo) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotNull(vo.getLogicalBuildId()), DeviceCollect::getLogicalBuildId, vo.getLogicalBuildId())
                .in(StringUtils.isNotNull(vo.getLogicalBuildIds()), DeviceCollect::getLogicalBuildId, vo.getLogicalBuildIds())
                .eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                .in(StringUtils.isNotNull(vo.getDeviceTypes()), DeviceCollect::getDeviceType, vo.getDeviceTypes())
                .eq(DeviceCollect::getLogicalFloorId, 0)
                .eq(DeviceCollect::getLogicalAreaId, 0)
                .eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0);
        List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);

//        if (list.size() > 0) {
//            return list;
//        }
        return list;
    }


    public List<DeviceCollect> getListByBuildId2(DeviceCollectVo vo) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotNull(vo.getLogicalBuildId()), DeviceCollect::getLogicalBuildId, vo.getLogicalBuildId())
                .in(StringUtils.isNotNull(vo.getLogicalBuildIds()), DeviceCollect::getLogicalBuildId, vo.getLogicalBuildIds())
                .eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                .in(StringUtils.isNotNull(vo.getDeviceTypes()), DeviceCollect::getDeviceType, vo.getDeviceTypes())
                .eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0);
        if(null != vo.getLogicalFloorId()){
            wrapper.eq(DeviceCollect::getLogicalFloorId, vo.getLogicalFloorId());
        }
        List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);
        return list;
    }

    public List<DeviceCollect> getListByFloorId(DeviceCollectVo vo) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                .in(StringUtils.isNotNull(vo.getDeviceTypes()), DeviceCollect::getDeviceType, vo.getDeviceTypes())
                .eq(StringUtils.isNotNull(vo.getLogicalFloorId()), DeviceCollect::getLogicalFloorId, vo.getLogicalFloorId())
                .in(StringUtils.isNotNull(vo.getLogicalFloorIds()), DeviceCollect::getLogicalFloorId, vo.getLogicalFloorIds())
                .eq(DeviceCollect::getLogicalAreaId, 0)
                .eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0);
        List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);

//        if (list.size() > 0) {
//            return list;
//        }
        return list;
    }

    public List<DeviceCollect> getListByAreaId(DeviceCollectVo vo) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                .in(StringUtils.isNotNull(vo.getDeviceTypes()), DeviceCollect::getDeviceType, vo.getDeviceTypes())
                .eq(StringUtils.isNotNull(vo.getLogicalAreaId()), DeviceCollect::getLogicalAreaId, vo.getLogicalAreaId())
                .in(StringUtils.isNotNull(vo.getLogicalAreaIdList()), DeviceCollect::getLogicalAreaId, vo.getLogicalAreaIdList())
                .eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0);
        List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);

//        if (list.size() > 0) {
//            return list;
//        }
        return list;
    }

    /**
     * 区域子设备
     * @param vo
     * @return
     */
    public List<DeviceCollect> getListByAreaId2(DeviceCollectVo vo) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                .in(StringUtils.isNotNull(vo.getDeviceTypes()), DeviceCollect::getDeviceType, vo.getDeviceTypes())
                .eq(StringUtils.isNotNull(vo.getLogicalAreaId()), DeviceCollect::getLogicalAreaId, vo.getLogicalAreaId())
                .in(StringUtils.isNotNull(vo.getLogicalAreaIdList()), DeviceCollect::getLogicalAreaId, vo.getLogicalAreaIdList())
                .eq(DeviceCollect::getIsTotalDevice, 0)
                .eq(DeviceCollect::getIsDel, 0);
        List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);

//        if (list.size() > 0) {
//            return list;
//        }
        return list;
    }

    public List<DeviceCollect> getListBySubItemId(DeviceCollectVo vo) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotNull(vo.getDeviceType()), DeviceCollect::getDeviceType, vo.getDeviceType())
                .in(StringUtils.isNotNull(vo.getDeviceTypes()), DeviceCollect::getDeviceType, vo.getDeviceTypes())
                .eq(DeviceCollect::getSubitem, vo.getSubitem())
                .eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0);
        List<DeviceCollect> list = deviceCollectMapper.selectList(wrapper);

//        if (list.size() > 0) {
//            return list;
//        }
        return list;
    }

    /**
     * 用电量、用水量
     * 参数
     */
    public String setEnergyParamData(EnergyReportVo vo) {
        //时间类型 1 年度 2 月度 3 日
        if (vo.getTimeType().equals("1")) {
            // 年度
            vo.setStartTime(new DateTime().toString("yyyy-01-01 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(1).toString("yyyy-01-01 00:00:00"));
        }
        if (vo.getTimeType().equals("2")) {
            // 月度
            vo.setStartTime(new DateTime().toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime().plusMonths(1).toString("yyyy-MM-01 00:00:00"));
        }
        if (vo.getTimeType().equals("3")) {
            // 天
            vo.setStartTime(new DateTime().toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime(new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00"));
        }
        //时间类型 4 去年同月 5 去年同日
        if (vo.getTimeType().equals("4")) {
            // 月度
            vo.setStartTime(new DateTime().plusYears(-1).toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(-1).plusMonths(1).toString("yyyy-MM-01 00:00:00"));
        }
        if (vo.getTimeType().equals("5")) {
            // 天
            vo.setStartTime(new DateTime().plusYears(-1).toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(-1).plusDays(1).toString("yyyy-MM-dd 00:00:00"));
        }//上个月
        if (vo.getTimeType().equals("6")) {
            // 月度
            vo.setStartTime(new DateTime().plusMonths(-1).toString("yyyy-MM-01 00:00:00"));



            // 定义输出格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();

            // 创建一个LocalDate对象用于计算
            LocalDate today = now.toLocalDate();
            int dayOfMonth = today.getDayOfMonth();

            // 获取上个月的同一天
            // 获取上个月
            LocalDate firstDayOfLastMonth = today.withDayOfMonth(1).minusMonths(1);
            // 尝试获取上个月的同一天
            LocalDate lastMonthSameDay = null;
            try {
                lastMonthSameDay = today.minusMonths(1);
            } catch (DateTimeException e) {
                // 如果抛出异常，说明这个月没有这一天，我们得到的是上个月的第一天
                // 因此我们需要调整到上个月的最后一天
                lastMonthSameDay = firstDayOfLastMonth.with(TemporalAdjusters.lastDayOfMonth());
            }

            // 格式化输出
            String formattedDate = lastMonthSameDay.format(formatter);
            vo.setEndTime(formattedDate+" 00:00:00");
        }

        if (vo.getTimeType().equals("7")) {
            //某一年
            Date date = DateUtil.parseDate(vo.getYear()+"-01-01") ;
            Long time= date.getTime() ;
            vo.setStartTime(new DateTime(time).toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime(time).plusYears(1).toString("yyyy-MM-01 00:00:00"));
        }
        return this.queryEnergyList(vo);
    }


    public String setLastValParamData(EnergyReportVo vo) {
        //时间类型 1 年度 2 月度 3 日
        if (vo.getTimeType().equals("1")) {
            // 年度
            vo.setStartTime(new DateTime().toString("yyyy-01-01 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(1).toString("yyyy-01-01 00:00:00"));
        }
        if (vo.getTimeType().equals("2")) {
            // 月度
            vo.setStartTime(new DateTime().toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime().plusMonths(1).toString("yyyy-MM-01 00:00:00"));
        }
        if (vo.getTimeType().equals("3")) {
            // 天
            vo.setStartTime(new DateTime().toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime(new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00"));
        }
        //时间类型 4 去年同月 5 去年同日
        if (vo.getTimeType().equals("4")) {
            // 月度
            vo.setStartTime(new DateTime().plusYears(-1).toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(-1).plusMonths(1).toString("yyyy-MM-01 00:00:00"));
        }
        if (vo.getTimeType().equals("5")) {
            // 天
            vo.setStartTime(new DateTime().plusYears(-1).toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(-1).plusDays(1).toString("yyyy-MM-dd 00:00:00"));
        }//上个月
        if (vo.getTimeType().equals("6")) {
            // 月度
            vo.setStartTime(new DateTime().plusMonths(-1).toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime().plusMonths(0).toString("yyyy-MM-01 00:00:00"));
        }
        return this.queryLastValList(vo);
    }

    /**
     * 用电量、用水量
     * sql语句
     */
    public String queryEnergyList(EnergyReportVo vo) {
        String sql = "select last(val)-first(val) from datasheet where 1=1";
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sql += " and time>='" + vo.getStartTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sql += " and time<'" + vo.getEndTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getChannelId())) {
            sql += " and channelId='" + vo.getChannelId() + "'";
        }
        if (StringUtils.isNotNull(vo.getChannelIds()) && vo.getChannelIds().size() > 0) {
            String channelIds = "~/";
            for (int i = 0; i < vo.getChannelIds().size(); i++) {
                channelIds = channelIds + "^" + vo.getChannelIds().get(i) + "$|";
            }
            channelIds = channelIds.substring(0, channelIds.lastIndexOf("|"));
            channelIds += "/";
            sql += " and channelId=" + channelIds;
        }
        if (StringUtils.isNotNull(vo.getDeviceCollectId())) {
            sql += " and deviceCollectId='" + vo.getDeviceCollectId() + "'";
        }
        if (StringUtils.isNotNull(vo.getDeviceIdList()) && vo.getDeviceIdList().size() > 0) {
            String deviceIds = "~/";
            for (int i = 0; i < vo.getDeviceIdList().size(); i++) {
                deviceIds = deviceIds + "^" + vo.getDeviceIdList().get(i) + "$|";
            }
            deviceIds = deviceIds.substring(0, deviceIds.lastIndexOf("|"));
            deviceIds += "/";
            sql += " and deviceCollectId=" + deviceIds;
        }
        sql += " group by deviceCollectId";
        QueryResult query = influxdbService.query(sql);
//        System.out.println("用电量、用水量");
        return this.anyResult(query);
    }

    public String queryLastValList(EnergyReportVo vo) {
        String sql = "select last(val) from datasheet where 1=1";
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sql += " and time>='" + vo.getStartTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sql += " and time<'" + vo.getEndTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getChannelId())) {
            sql += " and channelId='" + vo.getChannelId() + "'";
        }
        if (StringUtils.isNotNull(vo.getChannelIds()) && vo.getChannelIds().size() > 0) {
            String channelIds = "~/";
            for (int i = 0; i < vo.getChannelIds().size(); i++) {
                channelIds = channelIds + "^" + vo.getChannelIds().get(i) + "$|";
            }
            channelIds = channelIds.substring(0, channelIds.lastIndexOf("|"));
            channelIds += "/";
            sql += " and channelId=" + channelIds;
        }
        if (StringUtils.isNotNull(vo.getDeviceCollectId())) {
            sql += " and deviceCollectId='" + vo.getDeviceCollectId() + "'";
        }
        if (StringUtils.isNotNull(vo.getDeviceIdList()) && vo.getDeviceIdList().size() > 0) {
            String deviceIds = "~/";
            for (int i = 0; i < vo.getDeviceIdList().size(); i++) {
                deviceIds = deviceIds + "^" + vo.getDeviceIdList().get(i) + "$|";
            }
            deviceIds = deviceIds.substring(0, deviceIds.lastIndexOf("|"));
            deviceIds += "/";
            sql += " and deviceCollectId=" + deviceIds;
        }
        sql += " group by deviceCollectId";
        QueryResult query = influxdbService.query(sql);
//        System.out.println("用电量、用水量");
        return this.anyResult(query);
    }

    /**
     * 室内温度平均值
     */
    public String averageRoomTemperature(EnergyReportVo vo) {
        List<SysDict> inTempDevice = sysDictService.selectByType("inTempDevice");
        if (inTempDevice.size() > 0){
            SysDict sysDict = inTempDevice.get(0);
            String[] ids = sysDict.getDictValue().split(",");
            if (ids.length > 0) {
                vo.setDeviceIds(ids);
                return this.queryHumitureList(vo);
            }
        }

        return "0";
    }


    /**
     * 温湿度
     * 参数
     */
    public String setHumitureParamData(EnergyReportVo vo) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.isNotEmpty(vo.getConfigSign()), SysConfig::getConfigSign, vo.getConfigSign())
                .eq(SysConfig::getIsDel, 0)
                .orderByDesc(SysConfig::getCreateTime);
        List<SysConfig> list = sysConfigMapper.selectList(wrapper);
        if (list.size() > 0) {
            if (StringUtils.isNotNull(list.get(0).getDeviceId())) {
                String[] ids = list.get(0).getDeviceId().split(",");
                if (ids.length > 0) {
                    vo.setDeviceIds(ids);
                    return this.queryHumitureList(vo);
                }
            }
        }
        return "0";
    }

    /**
     * 温湿度
     */
    public String queryHumitureList(EnergyReportVo vo) {
        String sql = "select mean(val) from " +
                "(select last(val) as val from datasheet" +
                " where 1=1";
        if (StringUtils.isNotNull(vo.getDeviceIds()) && vo.getDeviceIds().length > 0) {
            String deviceIds = "~/";
            for (int i = 0; i < vo.getDeviceIds().length; i++) {
                deviceIds = deviceIds + "^" + vo.getDeviceIds()[i] + "$|";
            }
            deviceIds = deviceIds.substring(0, deviceIds.lastIndexOf("|"));
            deviceIds += "/";
            sql += " and deviceCollectId=" + deviceIds;
        }
        if (StringUtils.isNotNull(vo.getDeviceIdList()) && vo.getDeviceIdList().size() > 0) {
            String deviceIds = "~/";
            for (int i = 0; i < vo.getDeviceIdList().size(); i++) {
                deviceIds = deviceIds + "^" + vo.getDeviceIdList().get(i) + "$|";
            }
            deviceIds = deviceIds.substring(0, deviceIds.lastIndexOf("|"));
            deviceIds += "/";
            sql += " and deviceCollectId=" + deviceIds;
        }
        if (StringUtils.isNotNull(vo.getChannelId())) {
            sql += " and channelId='" + vo.getChannelId() + "'";
        }
        if (StringUtils.isNotNull(vo.getDeviceCollectId())) {
            sql += " and deviceCollectId='" + vo.getDeviceCollectId() + "'";
        }
        sql += " group by deviceCollectId)";
        QueryResult query = influxdbService.query(sql);
//        System.out.println("温湿度");
        return this.anyResult(query);
    }

    /**
     * 查询结果解析
     */
    public String anyResult(QueryResult queryResult) {
        double num = 0;
        List<QueryResult.Result> resultList = queryResult.getResults();
        for (QueryResult.Result query : resultList) {
            List<QueryResult.Series> seriesList = query.getSeries();
            if (StringUtils.isNotNull(seriesList)) {
                for (QueryResult.Series series : seriesList) {
                    Map<String, String> tags = series.getTags();
                    List<String> columns = series.getColumns();
                    String[] keys = columns.toArray(new String[columns.size()]);
                    List<List<Object>> values = series.getValues();
                    if (values != null && values.size() != 0) {
//                        for(List<Object> value : values){
//                            Map<String, Object> map = new HashMap(keys.length);
//                            for (int i = 0; i < keys.length; i++) {
//                                map.put(keys[i], value.get(i));
//                            }
//                        }
                        String val = values.get(0).get(1).toString();
                        Double d = Double.parseDouble(val);
                        num = num + d;
                    }
//                    System.out.println("测试");
                }
            } else {
                return "0";
            }
        }
        DecimalFormat df = new DecimalFormat("0");
        String s = df.format(num);
        return s;
    }

    /**
     * 能耗监测
     *
     * @return
     */
    public ResultInfo getDeviceInfo(DeviceQueryVo queryVo) {
        Page<DeviceCollectVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.getDeviceInfo(page, queryVo);
        List<DeviceCollectVo> records = pageInfo.getRecords();
        String channelType = "";
        if ("1".equals(queryVo.getEnergyType())) {
            channelType = "ENERGY_TOTAL";//电量
        } else if ("2".equals(queryVo.getEnergyType())) {
            channelType = "WATER_L";//水量
        }
        //查询实时用电量
        for (DeviceCollectVo record : records) {
            String val = this.getElectricitybyId(record.getId() + "", channelType);
            Double toDay1 =0.0 ;
            if ("1".equals(queryVo.getEnergyType())) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(new Date(),
                        JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                        JsdcDateUtil.FULL_SPLIT_PATTERN), record.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                 toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));

            record.setElectricVal(new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString()) ;
            }else {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                        JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                        JsdcDateUtil.FULL_SPLIT_PATTERN), record.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));

                record.setElectricVal(new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString()) ;
            }


//            record.setElectricVal(val == null ? "0.00" : new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).toString());

            LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
            wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, record.getDeviceType()+"");
            SysFile file = sysFileService.getOne(wrapper);
            if (StringUtils.isNotNull(file)) {
                record.setFilePath(nginxPath + file.getFileUrl());
            }
        }
        return ResultInfo.success(pageInfo);
    }



    /**
     * 能耗监测
     *
     * @return
     */
    public ResultInfo getDeviceInfo2(DeviceQueryVo queryVo) {

        Page<DeviceCollectVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.getDeviceInfo(page, queryVo);


        List<DeviceCollectVo> records = pageInfo.getRecords();

        String startDate = "" ;
        String endDate = "" ;

        if (queryVo.getTimeType().equals("3")) {
            // 年度
            startDate = new DateTime().toString("yyyy-01-01 00:00:00") ;
            endDate = new DateTime().plusYears(1).toString("yyyy-01-01 00:00:00") ;

        }
        if (queryVo.getTimeType().equals("2")) {
            // 月度
            startDate = new DateTime().toString("yyyy-MM-01 00:00:00") ;
            endDate = new DateTime().plusMonths(1).toString("yyyy-MM-01 00:00:00") ;

        }
        if (queryVo.getTimeType().equals("1")) {
            // 天
            startDate = new DateTime().toString("yyyy-MM-dd 00:00:00") ;
            endDate = new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00") ;
        }

        if (queryVo.getTimeType().equals("4")) {
            // 周

            // 获取当前日期
            LocalDate today = LocalDate.now();

            // 计算本周的第一天（周一）
            LocalDate firstDayOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

            // 计算本周的最后一天（周日）
            LocalDate lastDayOfWeek = today.with(TemporalAdjusters.next(java.time.DayOfWeek.SUNDAY));
// 计算本周最后一天的下一天
            LocalDate dayAfterLastDayOfWeek = lastDayOfWeek.plusDays(1);

            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 输出结果


            startDate = firstDayOfWeek.format(formatter)+" 00:00:00" ;
            endDate =dayAfterLastDayOfWeek.format(formatter)+" 00:00:00" ;

        }

        String channelType = "";
        if ("1".equals(queryVo.getEnergyType())) {
            channelType = "ENERGY_TOTAL";//电量
        } else if ("2".equals(queryVo.getEnergyType())) {
            channelType = "WATER_L";//水量
        }
        //查询实时用电量
        TimeInterval timer = DateUtil.timer();
        for (DeviceCollectVo record : records) {
            String val = this.getElectricitybyId(record.getId() + "", channelType);
            Double toDay1 =0.0 ;
            if ("1".equals(queryVo.getEnergyType())) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluate2(endDate, startDate, record.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));

                record.setElectricVal(new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString()) ;
            }else {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(endDate, startDate, record.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));

                record.setElectricVal(new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString()) ;
            }


//            record.setElectricVal(val == null ? "0.00" : new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).toString());

            LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
            wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, record.getDeviceType()+"");
            SysFile file = sysFileService.getOne(wrapper);
            if (StringUtils.isNotNull(file)) {
                record.setFilePath(nginxPath + file.getFileUrl());
            }
        }
        System.out.println(timer.interval()) ;
        return ResultInfo.success(pageInfo);
    }
    /**
     * 根据设备查询所有信号的值
     *
     * @param deviceId
     * @return
     */
    public ResultInfo getDeviceDataByChannelId(String deviceId) {
        DeviceCollect deviceCollect = this.getById(deviceId);
        //查询该设备的所有信号
        List<ConfigDeviceSignalMapVo> signTypes = deviceSignalMapMapper.getEntityByTId(deviceCollect.getDeviceType());
        for (ConfigDeviceSignalMapVo signType : signTypes) {
            signType.setSignValue(this.getElectricitybyId(deviceId, signType.getSignalTypeCode()));
        }
        return ResultInfo.success(signTypes);
    }

    private String getElectricitybyId(String deviceId, String channelId) {
        String sql = "select last(val) as val  from datasheet WHERE channelId = '" + channelId + "' and deviceCollectId='" + deviceId + "'";
        QueryResult query = influxdbService.query(sql);
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        if (maps != null && maps.size() > 0) {
            return maps.get(0).get("val") + "";
        }
        return null;
    }



    public Page<DeviceCollectVo> deviceMonitoring(Integer onLineStatus, Integer areaId, Integer floorId,
                                                  Integer buildId, String name,
                                                  Integer type, Integer index, Integer size, String areaName, Integer deviceType,
                                                  String areaIds, String buildIds, String floorIds, String id,List<Integer> deviceTypes,Integer isAir) {
        Page<DeviceCollectVo> page = new Page<>(index, size);
        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.deviceMonitoring(page, onLineStatus, areaId, floorId,
                buildId, name, type, areaName, deviceType, areaIds, buildIds, floorIds, id,deviceTypes);
        pageInfo.getRecords().forEach(a -> {
            LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
            wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, String.valueOf(a.getDeviceType()));
            SysFile file = sysFileService.getOne(wrapper);
            if (StringUtils.isNotNull(file)) {
                a.setFilePath(nginxPath + file.getFileUrl());
            }
            if (null != isAir){

            }
        });
        pageInfo.getRecords().forEach(x -> {
                x.setCdsm(dataSheetService.getInformationReporting(x.getDeviceType(), x.getId()));
        });
        return pageInfo;
    }


    public Page<DeviceCollectVo> deviceMonitoring2(Integer onLineStatus, Integer areaId, Integer floorId,
                                                  Integer buildId, String name,
                                                  Integer type, Integer index, Integer size, String areaName, Integer deviceType,
                                                  String areaIds, String buildIds, String floorIds, String id,List<String> deviceTypes,Integer isAir) {
        Page<DeviceCollectVo> page = new Page<>(index, size);
        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.deviceMonitoring2(page, onLineStatus, areaId, floorId,
                buildId, name, type, areaName, deviceType, areaIds, buildIds, floorIds, id,deviceTypes);
        pageInfo.getRecords().forEach(a -> {
            LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
            wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, String.valueOf(a.getDeviceType()));
            SysFile file = sysFileService.getOne(wrapper);
            if (StringUtils.isNotNull(file)) {
                a.setFilePath(nginxPath + file.getFileUrl());
            }
            if (null != isAir){


            }
        });
        pageInfo.getRecords().forEach(x -> {
                x.setCdsm(dataSheetService.getInformationReporting(x.getDeviceType(), x.getId()));
        });
        return pageInfo;
    }

    /**
     * 明细分析
     *
     * @return
     * @paramn queryVo
     */
    public ResultInfo deviceDetailsAnalysis(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
        Page<DeviceCollectVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.getDeviceInfo(page, queryVo);

        List<DeviceCollectVo> list = pageInfo.getRecords();
        if (list.size() == 0) {
            list = new ArrayList<>();
            DeviceCollectVo deviceCollectVo = new DeviceCollectVo();
            deviceCollectVo.setId(-1);
            list.add(deviceCollectVo);
        }
        queryVo.setDeviceCollectVos(list);

        List<JSONObject> data = new ArrayList<>();//数据
        //列的封装
        List<String> columns = new ArrayList<>();
        if ("1".equals(queryVo.getTimeType())) {//按日
            Date timeStart = DateUtil.parseDate(queryVo.getTimeStart());
            Date timeEnd = DateUtil.parseDate(queryVo.getTimeEnd());
            long days = DateUtil.between(timeStart, timeEnd, DateUnit.DAY);
            if (days >= 31) {
                return ResultInfo.error("日期区间不得大于31天");
            }
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, timeEnd));
            queryVo.setTimeEnd(DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(queryVo.getTimeEnd()), 1)));
        } else if ("2".equals(queryVo.getTimeType())) {//按月
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"));
            Date endStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"), 1));
            queryVo.setTimeStart(DateUtil.format(timeStart, "yyyy-MM-dd"));
            queryVo.setTimeEnd(DateUtil.format(endStart, "yyyy-MM-dd"));
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"))));
        } else if ("3".equals(queryVo.getTimeType())) {
            for (int i = 1; i <= 12; i++) {
                columns.add(i + "月");
            }
        }
        if ("1".equals(queryVo.getEnergyType())) {
            queryVo.setChannelId("ENERGY_TOTAL");
        } else if ("2".equals(queryVo.getEnergyType())) {
            //todo 水信号
            queryVo.setChannelId("WATER_L");
        }
        Map<String, Object> map = new HashMap<>();
        Map<Integer, Object> map2 = new HashMap<>();
        //判断统计维度--》日、月都为按日统计
        if ("1".equals(queryVo.getTimeType()) || "2".equals(queryVo.getTimeType())) {
            //判断能源类型决定信号类型
            JSONObject deviceInfo = this.getDataByDay(queryVo);
            for (DeviceCollectVo record : pageInfo.getRecords()) {
                JSONObject json = new JSONObject();
                json.put("deviceName", record.getName());
                json.put("location", record.getLogicalLocation());
                //查询该设备的电量
                //查询该设备的电量
                Object o = deviceInfo.get(record.getId() + "");
                if (o == null) {
                    data.add(json);
                    continue;
                }
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;
                for (Map<String, Object> obj : dataList) {
                    String key = obj.get("time") + "";
                    String value = obj.get("val") + "";
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                    } else {
                        value = "0.00";
                    }
                    json.put(key.substring(0, 10), value);
                }
                data.add(json);
            }

//            for (DeviceCollectVo record : list) {
//                Object o = deviceInfo.get(record.getId() + "");
//                if (o == null) {
//                    continue;
//                }
//                List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;
//
//                String val;
//                for (Map<String, Object> obj : dataList) {
//                    String key = obj.get("time") + "";
//                    String value = obj.get("val") + "";
//                    if (value != null && !"".equals(value) && !"null".equals(value)) {
//                        value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
//                    } else {
//                        value = "0.00";
//                    }
//
//                    if (map.get(key.substring(0, 10)) != null) {
//                        //累加value
//                        val = new BigDecimal(map.get(key.substring(0, 10)).toString()).add(new BigDecimal(value)).setScale(2, RoundingMode.HALF_UP).toString();
//                        map.put(key.substring(0, 10), val);
//                    } else {
//                        map.put(key.substring(0, 10), value);
//                    }
//                }
//            }

        } else {//按照年统计
            JSONObject dataByMonth = getDataByMonth(queryVo);
            for (DeviceCollectVo record : pageInfo.getRecords()) {
                JSONObject json = new JSONObject();
                json.put("deviceName", record.getName());
                json.put("location", record.getLogicalLocation());

                for (int i = 1; i <= 12; i++) {
                    JSONObject obj_month = (JSONObject) dataByMonth.get(i + "");
                    if (obj_month != null && obj_month.size() != 0 && obj_month.get(record.getId()) != null) {
                        HashMap obj = (HashMap) obj_month.get(record.getId());
                        String value = obj.get("val") + "";
                        if (value != null && !"".equals(value) && !"null".equals(value)) {
                            value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                        }
                        json.put(obj.get("time") + "月", value);
                    } else {
                        json.put(i + "月", 0.00);
                    }
                }
                data.add(json);
            }

//            for (DeviceCollectVo record : list) {
//                String val;
//                for (int i = 1; i <= 12; i++) {
//                    JSONObject obj_month = (JSONObject) dataByMonth.get(i + "");
//                    if (obj_month != null && obj_month.size() != 0 && obj_month.get(record.getId()) != null) {
//                        HashMap obj = (HashMap) obj_month.get(record.getId());
//                        String value = obj.get("val") + "";
//                        if (value != null && !"".equals(value) && !"null".equals(value)) {
//                            value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
//                        }
//                        if (map2.get(Integer.parseInt(obj.get("time").toString())) != null) {
//                            //累加value
//                            val = new BigDecimal(map2.get(Integer.parseInt(obj.get("time").toString())).toString()).add(new BigDecimal(value)).setScale(2, RoundingMode.HALF_UP).toString();
//                            map2.put(Integer.parseInt(obj.get("time").toString()), val);
//                        } else {
//                            map2.put(Integer.parseInt(obj.get("time").toString()), value);
//                        }
//                    } else {
//                        if (map2.get(i) != null) {
//                            //累加value
//                            val = new BigDecimal(map2.get(i).toString()).add(new BigDecimal("0.00")).setScale(2, RoundingMode.HALF_UP).toString();
//                            map2.put(i, val);
//                        } else {
//                            map2.put(i, "0.00");
//                        }
//                    }
//                }
//            }

        }
        result.put("data", data);
        result.put("columns", columns);
        result.put("total", pageInfo.getTotal());

//        //判断统计维度--》日、月都为按日统计
//        if ("1".equals(queryVo.getTimeType()) || "2".equals(queryVo.getTimeType())) {
//            //根据时间进行升序排序
//            Map<String, String> sortedMap = new LinkedHashMap<>();
//            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue().toString()));
//
//            result.put("map", sortedMap);
//        } else {
//            // 使用Map的entrySet和Stream进行排序
//            Map<Integer, String> sortedMap = new HashMap<>();
//            // 使用Map的entrySet和Stream进行排序
//            map2.entrySet().stream()
//                    .sorted(Map.Entry.comparingByKey())
//                    .forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue().toString()));
//            result.put("map", sortedMap);
//        }

        return ResultInfo.success(result);
    }

    public ResultInfo deviceDetailsAnalysis2(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
//        Page<DeviceCollectVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
//        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.getDeviceInfo(page, queryVo);

        List<DeviceCollectVo> list = deviceCollectMapper.getDeviceInfoList(null, queryVo);
        if (list.size() == 0) {
            DeviceCollectVo deviceCollectVo = new DeviceCollectVo();
            deviceCollectVo.setId(-1);
            list.add(deviceCollectVo);
        }
        queryVo.setDeviceCollectVos(list);

        List<JSONObject> data = new ArrayList<>();//数据
        //列的封装
        List<String> columns = new ArrayList<>();
        if ("1".equals(queryVo.getTimeType())) {//按日
            Date timeStart = DateUtil.parseDate(queryVo.getTimeStart());
            Date timeEnd = DateUtil.parseDate(queryVo.getTimeEnd());
            long days = DateUtil.between(timeStart, timeEnd, DateUnit.DAY);
            if (days >= 31) {
                return ResultInfo.error("日期区间不得大于31天");
            }
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, timeEnd));
            queryVo.setTimeEnd(DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(queryVo.getTimeEnd()), 1)));
        } else if ("2".equals(queryVo.getTimeType())) {//按月
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"));
            Date endStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"), 1));
            queryVo.setTimeStart(DateUtil.format(timeStart, "yyyy-MM-dd"));
            queryVo.setTimeEnd(DateUtil.format(endStart, "yyyy-MM-dd"));
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"))));
        } else if ("3".equals(queryVo.getTimeType())) {
            for (int i = 1; i <= 12; i++) {
                columns.add(i + "月");
            }
        }
        if ("1".equals(queryVo.getEnergyType())) {
            queryVo.setChannelId("ENERGY_TOTAL");
        } else if ("2".equals(queryVo.getEnergyType())) {
            //todo 水信号
            queryVo.setChannelId("WATER_L");
        }
        Map<String, Object> map = new HashMap<>();
        Map<Integer, Object> map2 = new HashMap<>();
        //判断统计维度--》日、月都为按日统计
        if ("1".equals(queryVo.getTimeType()) || "2".equals(queryVo.getTimeType())) {
            //判断能源类型决定信号类型
            JSONObject deviceInfo = this.getDataByDay(queryVo);
//            for (DeviceCollectVo record : pageInfo.getRecords()) {
//                JSONObject json = new JSONObject();
//                json.put("deviceName", record.getName());
//                json.put("location", record.getLogicalLocation());
//                //查询该设备的电量
//                //查询该设备的电量
//                Object o = deviceInfo.get(record.getId() + "");
//                if (o == null) {
//                    data.add(json);
//                    continue;
//                }
//                List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;
//                for (Map<String, Object> obj : dataList) {
//                    String key = obj.get("time") + "";
//                    String value = obj.get("val") + "";
//                    if (value != null && !"".equals(value) && !"null".equals(value)) {
//                        value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
//                    } else {
//                        value = "0.00";
//                    }
//                    json.put(key.substring(0, 10), value);
//                }
//                data.add(json);
//            }

            for (DeviceCollectVo record : list) {
                Object o = deviceInfo.get(record.getId() + "");
                if (o == null) {
                    continue;
                }
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;

                String val;
                for (Map<String, Object> obj : dataList) {
                    String key = obj.get("time") + "";
                    String value = obj.get("val") + "";
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                    } else {
                        value = "0.00";
                    }

                    if (map.get(key.substring(0, 10)) != null) {
                        //累加value
                        val = new BigDecimal(map.get(key.substring(0, 10)).toString()).add(new BigDecimal(value)).setScale(2, RoundingMode.HALF_UP).toString();
                        map.put(key.substring(0, 10), val);
                    } else {
                        map.put(key.substring(0, 10), value);
                    }
                }
            }

        } else {//按照年统计
            JSONObject dataByMonth = getDataByMonth(queryVo);
//            for (DeviceCollectVo record : pageInfo.getRecords()) {
//                JSONObject json = new JSONObject();
//                json.put("deviceName", record.getName());
//                json.put("location", record.getLogicalLocation());
//
//                for (int i = 1; i <= 12; i++) {
//                    JSONObject obj_month = (JSONObject) dataByMonth.get(i + "");
//                    if (obj_month != null && obj_month.size() != 0 && obj_month.get(record.getId()) != null) {
//                        HashMap obj = (HashMap) obj_month.get(record.getId());
//                        String value = obj.get("val") + "";
//                        if (value != null && !"".equals(value) && !"null".equals(value)) {
//                            value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
//                        }
//                        json.put(obj.get("time") + "月", value);
//                    } else {
//                        json.put(i + "月", 0.00);
//                    }
//                }
//                data.add(json);
//            }

            for (DeviceCollectVo record : list) {
                String val;
                for (int i = 1; i <= 12; i++) {
                    JSONObject obj_month = (JSONObject) dataByMonth.get(i + "");
                    if (obj_month != null && obj_month.size() != 0 && obj_month.get(record.getId()) != null) {
                        HashMap obj = (HashMap) obj_month.get(record.getId());
                        String value = obj.get("val") + "";
                        if (value != null && !"".equals(value) && !"null".equals(value)) {
                            value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                        }
                        if (map2.get(Integer.parseInt(obj.get("time").toString())) != null) {
                            //累加value
                            val = new BigDecimal(map2.get(Integer.parseInt(obj.get("time").toString())).toString()).add(new BigDecimal(value)).setScale(2, RoundingMode.HALF_UP).toString();
                            map2.put(Integer.parseInt(obj.get("time").toString()), val);
                        } else {
                            map2.put(Integer.parseInt(obj.get("time").toString()), value);
                        }
                    } else {
                        if (map2.get(i) != null) {
                            //累加value
                            val = new BigDecimal(map2.get(i).toString()).add(new BigDecimal("0.00")).setScale(2, RoundingMode.HALF_UP).toString();
                            map2.put(i, val);
                        } else {
                            map2.put(i, "0.00");
                        }
                    }
                }
            }
        }
        result.put("data", data);
        result.put("columns", columns);
//        result.put("total", pageInfo.getTotal());
        //判断统计维度--》日、月都为按日统计
        if ("1".equals(queryVo.getTimeType()) || "2".equals(queryVo.getTimeType())) {
            //根据时间进行升序排序
            Map<String, String> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue().toString()));

            result.put("map", sortedMap);
        } else {
            // 使用Map的entrySet和Stream进行排序
            Map<Integer, String> sortedMap = new HashMap<>();
            map2.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue().toString()));
//            // 使用Map的entrySet和Stream进行排序
//            map2.entrySet().stream()
//                    .sorted(Map.Entry.comparingByKey())
//                    .forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue().toString()));
            result.put("map", sortedMap);
        }

        return ResultInfo.success(result);
    }

    private JSONObject getDataByHours(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
        String sql = "select SPREAD(val) as val from datasheet where channelId='" + queryVo.getChannelId() + "' and time>'" + queryVo.getTimeStart() + "' and time <'" + queryVo.getTimeEnd() + "' group by time(1h),deviceCollectId";
        QueryResult queryResult = influxdbService.query(sql);
        List<QueryResult.Result> resultList = queryResult.getResults();
        for (QueryResult.Result query : resultList) {
            List<QueryResult.Series> seriesList = query.getSeries();
            if (seriesList == null || seriesList.size() == 0) continue;
            for (QueryResult.Series series : seriesList) {
                Map<String, String> tags = series.getTags();
                List<String> columns = series.getColumns();
                String[] keys = columns.toArray(new String[columns.size()]);
                List<List<Object>> values = series.getValues();
                List<Map<String, Object>> mapList = new ArrayList<>();
                if (values != null && values.size() != 0) {
                    for (List<Object> value : values) {
                        Map<String, Object> map = new HashMap(keys.length);
                        for (int i = 0; i < keys.length; i++) {
                            map.put(keys[i], value.get(i));
                        }
                        mapList.add(map);
                    }
                }
                result.put(tags.get("deviceCollectId"), mapList);
            }
        }
        return result;
    }

    private JSONObject getDataByDay(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
        String sql = "select SPREAD(val) as val from datasheet where channelId='" + queryVo.getChannelId() + "' and time>'" + queryVo.getTimeStart() + "' and time <'" + queryVo.getTimeEnd() + "' ";
        if (queryVo.getDeviceCollectVos().size() == 1) {
            sql += " and deviceCollectId=~/^" + queryVo.getDeviceCollectVos().get(0).getId() + "$/";
        } else {
            for (int i = 0; i < queryVo.getDeviceCollectVos().size(); i++) {
                if (i == 0) {//~/^admin$|^username$/
                    sql += " and deviceCollectId=~/^" + queryVo.getDeviceCollectVos().get(0).getId() + "$";
                } else if (i > 0) {
                    sql += "|^" + queryVo.getDeviceCollectVos().get(i).getId() + "$";
                    if (i == queryVo.getDeviceCollectVos().size()-1) {
                        sql += "/ ";
                    }
                }
            }
        }
        sql += " group by time(1d),deviceCollectId";

        QueryResult queryResult = influxdbService.query(sql);
        List<QueryResult.Result> resultList = queryResult.getResults();
        for (QueryResult.Result query : resultList) {
            List<QueryResult.Series> seriesList = query.getSeries();
            if (seriesList == null || seriesList.size() == 0) continue;
            for (QueryResult.Series series : seriesList) {
                Map<String, String> tags = series.getTags();
                List<String> columns = series.getColumns();
                String[] keys = columns.toArray(new String[columns.size()]);
                List<List<Object>> values = series.getValues();
                List<Map<String, Object>> mapList = new ArrayList<>();
                if (values != null && values.size() != 0) {
                    for (List<Object> value : values) {
                        Map<String, Object> map = new HashMap(keys.length);
                        for (int i = 0; i < keys.length; i++) {
                            map.put(keys[i], value.get(i));
                        }
                        mapList.add(map);
                    }
                }
                result.put(tags.get("deviceCollectId"), mapList);
            }
        }
        return result;
    }

    /**
     * 电能质量 - 谐波分析
     *
     * @param queryVo
     * @return
     */
    public ResultInfo harmonics(DeviceQueryVo queryVo) {
        // 设备类型为智能电表
        queryVo.setDeviceType("10005");

        List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceHarmonics(queryVo);
        JSONObject result = new JSONObject();
        List<JSONObject> data = new ArrayList<>();//数据
        List<String> columns = new ArrayList<>();//列的封装
        BigDecimal max = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal min = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        String value = "0.00";
        queryVo.setChannelId("HARMONICS");
//        queryVo.setChannelId("Energy_ALL");
        if ("1".equals(queryVo.getTimeType())) {//按日
            columns.addAll(JsdcDateUtil.getDatesInRange(DateUtil.parseDate(queryVo.getTimeStart()), DateUtil.parseDate(queryVo.getTimeEnd())));
            queryVo.setTimeEnd(DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(queryVo.getTimeEnd()), 1)));
        } else if ("2".equals(queryVo.getTimeType())) {//按月
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"));
            Date endStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"), 1));
            queryVo.setTimeStart(DateUtil.format(timeStart, "yyyy-MM-dd"));
            queryVo.setTimeEnd(DateUtil.format(endStart, "yyyy-MM-dd"));
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"))));
        } else if ("3".equals(queryVo.getTimeType())) {
            for (int i = 1; i <= 12; i++) {
                columns.add(i + "月");
            }
        }


        //判断统计维度--》日、月都为按日统计
        if ("1".equals(queryVo.getTimeType()) || "2".equals(queryVo.getTimeType())) {
            //判断能源类型决定信号类型
            JSONObject deviceInfo = this.getDataByDay(queryVo);
            for (DeviceCollect record : deviceCollects) {
                JSONObject json = new JSONObject();
                json.put("deviceName", record.getName());
                //查询该设备的电量
                Object o = deviceInfo.get(record.getId() + "");
                if (o == null) {
                    json.put("max", max.toPlainString());
                    json.put("min", min.toPlainString());
                    json.put("value", "0.00");
                    data.add(json);
                    continue;
                }
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;
                for (Map<String, Object> obj : dataList) {
                    String key = obj.get("time") + "";
                    value = obj.get("val") + "";
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                        BigDecimal v = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
                        value = v.toPlainString();
                        max = max.max(v);
                        min = min.min(v);
                    } else {
                        value = "0.00";
                    }
                    json.put(key.substring(0, 10), value);
                    json.put("value", value);
                }

                json.put("value", value);
                json.put("max", max.toPlainString());
                json.put("min", min.toPlainString());
                data.add(json);
            }
        } else {//按照年统计
            JSONObject dataByMonth = getDataByMonth(queryVo);
            for (DeviceCollect record : deviceCollects) {
                JSONObject json = new JSONObject();
                json.put("deviceName", record.getName());
                for (int i = 1; i <= 12; i++) {
                    JSONObject obj_month = (JSONObject) dataByMonth.get(i + "");
                    if (obj_month != null && obj_month.size() != 0 && obj_month.get(record.getId()) != null) {
                        HashMap obj = (HashMap) obj_month.get(record.getId());
                        value = obj.get("val") + "";
                        if (value != null && !"".equals(value) && !"null".equals(value)) {
                            BigDecimal v = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
                            value = v.toPlainString();
                            max = max.max(v);
                            min = min.min(v);
                        }
                        json.put(obj.get("time") + "月", value);
                    } else {
                        json.put(i + "月", value);
                    }
                }
                json.put("value", value);
                json.put("max", max.toPlainString());
                json.put("min", min.toPlainString());
                data.add(json);
            }
        }

        result.put("max", max);
        result.put("min", min);
        result.put("data", data);
        result.put("columns", columns);
        result.put("total", deviceCollects.size());
        return ResultInfo.success(result);
    }

    /**
     * 智慧能源-谐波分析 sql语句
     */
    public String harmonicAnalysisSql(String startTime, String endTime, String channelId, String deviceCollectId) {
        String sql = "SELECT last(val) as val FROM datasheet WHERE channelId = '" + channelId + "' AND deviceCollectId= '" + deviceCollectId + "'" +
                " AND time >= '" + startTime + "' AND time < '" + endTime + "'";
        sql += "  GROUP BY time(10m)";
        return sql;
    }

    private JSONObject getDataByMonth(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
        for (int i = 1; i <= 12; i++) {
            String[] timeArr = JsdcDateUtil.getMonthByYear(queryVo.getTimeStr()).get(i + "").split(",");
            queryVo.setTimeStart(timeArr[0]);
            queryVo.setTimeEnd(timeArr[1]);
            String sql = "select SPREAD(val) as val from datasheet where channelId='" + queryVo.getChannelId() + "' and time>'" + queryVo.getTimeStart() + "' and time <'" + queryVo.getTimeEnd() + "' ";
            if (queryVo.getDeviceCollectVos().size() == 1) {
                sql += " and deviceCollectId=~/^" + queryVo.getDeviceCollectVos().get(0).getId() + "$/";
            } else {
                for (int j = 0; j < queryVo.getDeviceCollectVos().size(); j++) {
                    if (j == 0) {//~/^admin$|^username$/
                        sql += " and deviceCollectId=~/^" + queryVo.getDeviceCollectVos().get(0).getId() + "$";
                    } else if (j > 0) {
                        sql += "|^" + queryVo.getDeviceCollectVos().get(j).getId() + "$";
                        if (j == queryVo.getDeviceCollectVos().size()-1) {
                            sql += "/ ";
                        }
                    }
                }
            }
            sql += " group by deviceCollectId";

            QueryResult queryResult = influxdbService.query(sql);
            List<QueryResult.Result> resultList = queryResult.getResults();
            JSONObject json = new JSONObject();
            for (QueryResult.Result query : resultList) {
                List<QueryResult.Series> seriesList = query.getSeries();
                if (seriesList == null || seriesList.size() == 0) continue;
                for (QueryResult.Series series : seriesList) {
                    Map<String, String> tags = series.getTags();
                    List<String> columns = series.getColumns();
                    String[] keys = columns.toArray(new String[columns.size()]);
                    List<List<Object>> values = series.getValues();
//                    List<Map<String, Object>> mapList = new ArrayList<>();
                    Map<String, Object> map = new HashMap(keys.length);
                    if (values != null && values.size() != 0) {
                        for (List<Object> value : values) {
                            for (int j = 0; j < keys.length; j++) {
                                if ("time".equals(keys[j])) {
                                    map.put(keys[j], i + "");
                                } else {
                                    map.put(keys[j], value.get(j));
                                }

                            }
//                            mapList.add(map);
                        }
                    }
                    json.put(tags.get("deviceCollectId"), map);
                }
            }
            result.put(i + "", json);
        }
        return result;
    }

    /**
     * 季度用水用电统计
     *
     * @return
     */
    public JSONObject quarterSumSql() {
        Integer year = Calendar.getInstance().get(Calendar.YEAR);
        JSONObject object = new JSONObject();
        List<String> electricitylist = new ArrayList<>();
        List<String> waterlist = new ArrayList<>();
        DataSheetVo bean = new DataSheetVo();
        bean.setEnergyType(1);
        List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
        for (int i = 0; i < 4; i++) {
            if (collects.isEmpty()) {//无采集设备
                electricitylist.add("0");
                waterlist.add("0");
                continue;
            }
            BigDecimal electricity = new BigDecimal(0);//用电
            BigDecimal water = new BigDecimal(0);//用水
            // 总设备
            List<String> deviceIds = collects.stream().filter(x -> x.getIsTotalDevice() == 1).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());
            if (deviceIds.isEmpty()) {
                deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }
            for (String collect : deviceIds) {
                //根据月份获取每月的最后一天的日期
                int lastDay = LocalDate.of(year, i * 3 + 3, 1).withDayOfMonth(LocalDate.of(year, i * 3 + 3, 1).lengthOfMonth()).getDayOfMonth();
                //用电
                EnergyReportVo vo = new EnergyReportVo();
                vo.setChannelId(G.CHANNELID_ELECTRICITY);
                vo.setStartTime(String.format("%s-%02d-01 00:00:00", year, i * 3 + 1));
                vo.setEndTime(String.format("%s-%02d-" + lastDay + " 23:59:59", year, i * 3 + 3));
                vo.setDeviceCollectId(Integer.valueOf(collect));
                electricity = electricity.add(new BigDecimal(this.quarterSumSqlList(vo)));
                //用水
                EnergyReportVo svo = new EnergyReportVo();
                svo.setChannelId(G.CHANNELID_WATER);
                svo.setStartTime(String.format("%s-%02d-01 00:00:00", year, i * 3 + 1));
                svo.setEndTime(String.format("%s-%02d-" + lastDay + " 23:59:59", year, i * 3 + 3));
                vo.setDeviceCollectId(Integer.valueOf(collect));
                water = water.add(new BigDecimal(this.quarterSumSqlList(svo)));
            }
            waterlist.add(water.setScale(0, RoundingMode.DOWN).toString());
            electricitylist.add(electricity.setScale(0, RoundingMode.DOWN).toString());
        }
        object.put("electricity", electricitylist);
        object.put("water", waterlist);
        return object;
    }


    public ResultInfo floorWhiteFuel(DeviceQueryVo queryVo) {
        SysBuildFloor sysBuildFloor = new SysBuildFloor();
        sysBuildFloor.setDictBuilding(queryVo.getBuildId());
        List<SysBuildFloor> sysBuildFloorList = sysBuildFloorService.getListByBuild(sysBuildFloor);
        List<Map<String, String>> mapList = new ArrayList<>();
        List<Map<String, String>> mapList2 = new ArrayList<>();
        Map<String, String> map = null;
        for (SysBuildFloor s : sysBuildFloorList) {
            map = new HashMap<>();
            map.put("id", s.getFloorName());
            if ("1".equals(queryVo.getTimeType())) {//按日
                queryVo.setTimeStart(DateUtil.format(new Date(), "yyyy-MM-dd"));
                String tomorrowStr = DateUtil.formatDate(DateUtil.tomorrow());
                queryVo.setTimeEnd(tomorrowStr);
            } else if ("2".equals(queryVo.getTimeType())) {//按月
                queryVo.setTimeStart(DateUtil.beginOfMonth(new Date()).toString());
                queryVo.setTimeEnd(DateUtil.endOfMonth(new Date()).toString());
            } else if ("3".equals(queryVo.getTimeType())) {//按年
                queryVo.setTimeStart(DateUtil.beginOfYear(new Date()).toString());
                queryVo.setTimeEnd(DateUtil.endOfMonth(new Date()).toString());
            } else if ("4".equals(queryVo.getTimeType())) {//按周
                queryVo.setTimeStart(DateUtil.beginOfWeek(new Date()).toString());
                queryVo.setTimeEnd(DateUtil.endOfWeek(new Date()).toString());
            }
            queryVo.setFloorId(s.getId());
            //查复核条件的总设备
            QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("logicalFloorId", s.getId());
            queryWrapper.eq("logicalAreaId", 0);
            queryWrapper.eq("isTotalDevice", 1);
            queryWrapper.eq("isDel", 0);
            queryWrapper.eq("status", 2);
            List<DeviceCollect> deviceCollects = deviceCollectService.list(queryWrapper);
            queryVo.setDeviceCollects(deviceCollects);

            //查该楼层的电
            queryVo.setChannelId("ENERGY_TOTAL");
            String deviceInfo = this.getDataByDay2(queryVo);
            if (StringUtils.isEmpty(deviceInfo)) {
                map.put("dataE", "0.00");
            } else {
                map.put("dataE", deviceInfo);
            }
            //查该楼层的水
            queryVo.setChannelId("WATER_L");
            String deviceInfo2 = this.getDataByDay2(queryVo);
            if (StringUtils.isEmpty(deviceInfo2)) {
                map.put("dataW", "0.00");
            } else {
                map.put("dataW", deviceInfo2);
            }

            mapList.add(map);
        }
        mapList2.addAll(mapList);
        //排序 倒序
        Comparator<Map<String, String>> comparator = (map1, map2) -> {
            // 假设属性值为Integer类型
            BigDecimal value1 = new BigDecimal(map1.get("dataE")) ;
            BigDecimal value2 = new BigDecimal(map2.get("dataE")) ;
            return value2.compareTo(value1);
        };
        Collections.sort(mapList, comparator);
        String maxE = mapList.get(0).get("dataE") ;
        Comparator<Map<String, String>> comparator2 = (map1, map2) -> {
            // 假设属性值为Integer类型
            BigDecimal value1 = new BigDecimal(map1.get("dataW")) ;
            BigDecimal value2 = new BigDecimal(map2.get("dataW")) ;
            return value2.compareTo(value1);
        };
        Collections.sort(mapList, comparator2);
        String maxW = mapList.get(0).get("dataW") ;
        Map<String,String> map1 = new HashMap<>() ;
//        map1.put("maxE",maxE) ;
//        map1.put("maxW",maxW) ;
        //mapList.add(map1) ;
//        mapList2.add(map1) ;

        System.out.println("1111"+ mapList2);
        return ResultInfo.success(sortByElectricityAndWater(mapList2));
    }

    /**
     * 分项用水占比
     * timeType=2 按月
     */
    public ResultInfo floorWhiteFuel2(DeviceQueryVo queryVo) {
        SysBuildFloor sysBuildFloor = new SysBuildFloor();
        sysBuildFloor.setDictBuilding(queryVo.getBuildId());
        List<SysBuildFloor> sysBuildFloorList = sysBuildFloorService.getListByBuild(sysBuildFloor);
        List<Map<String, String>> mapList = new ArrayList<>();
        List<Map<String, String>> mapList2 = new ArrayList<>();
        Map<String, String> map = null;
        for (SysBuildFloor s : sysBuildFloorList) {
            map = new HashMap<>();
            map.put("id", s.getFloorName());
            if ("1".equals(queryVo.getTimeType())) {//按日
                queryVo.setTimeStart(DateUtil.format(new Date(), "yyyy-MM-dd"));
                String tomorrowStr = DateUtil.formatDate(DateUtil.tomorrow());
                queryVo.setTimeEnd(tomorrowStr);
            } else if ("2".equals(queryVo.getTimeType())) {//按月
                queryVo.setTimeStart(DateUtil.beginOfMonth(new Date()).toString());
                queryVo.setTimeEnd(DateUtil.endOfMonth(new Date()).toString());
            } else if ("3".equals(queryVo.getTimeType())) {//按年
                queryVo.setTimeStart(DateUtil.beginOfYear(new Date()).toString());
                queryVo.setTimeEnd(DateUtil.endOfMonth(new Date()).toString());
            } else if ("4".equals(queryVo.getTimeType())) {//按周
                queryVo.setTimeStart(DateUtil.beginOfWeek(new Date()).toString());
                queryVo.setTimeEnd(DateUtil.endOfWeek(new Date()).toString());
            }
            queryVo.setFloorId(s.getId());
            //查复核条件的总设备
            QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("logicalFloorId", s.getId());
            queryWrapper.eq("logicalAreaId", 0);
            queryWrapper.eq("isTotalDevice", 1);
            queryWrapper.eq("isDel", 0);
            queryWrapper.eq("status", 2);
            List<DeviceCollect> deviceCollects = deviceCollectService.list(queryWrapper);
            queryVo.setDeviceCollects(deviceCollects);

//            //查该楼层的电
//            queryVo.setChannelId("ENERGY_TOTAL");
//            String deviceInfo = this.getDataByDay2(queryVo);
//            if (StringUtils.isEmpty(deviceInfo)) {
//                map.put("dataE", "0.00");
//            } else {
//                map.put("dataE", deviceInfo);
//            }
            //查该楼层的水
            queryVo.setChannelId("WATER_L");
            String deviceInfo2 = this.getDataByDay2(queryVo);
            if (StringUtils.isEmpty(deviceInfo2)) {
                map.put("dataW", "0.00");
            } else {
                map.put("dataW", deviceInfo2);
            }

            mapList.add(map);
        }
        mapList2.addAll(mapList);
        //排序 倒序
//        Comparator<Map<String, String>> comparator = (map1, map2) -> {
//            // 假设属性值为Integer类型
//            BigDecimal value1 = new BigDecimal(map1.get("dataE")) ;
//            BigDecimal value2 = new BigDecimal(map2.get("dataE")) ;
//            return value2.compareTo(value1);
//        };
//        Collections.sort(mapList, comparator);
//        String maxE = mapList.get(0).get("dataE") ;
        Comparator<Map<String, String>> comparator2 = (map1, map2) -> {
            // 假设属性值为Integer类型
            BigDecimal value1 = new BigDecimal(map1.get("dataW")) ;
            BigDecimal value2 = new BigDecimal(map2.get("dataW")) ;
            return value2.compareTo(value1);
        };
        Collections.sort(mapList, comparator2);
        String maxW = mapList.get(0).get("dataW") ;
        Map<String,String> map1 = new HashMap<>() ;
//        map1.put("maxE",maxE) ;
        map1.put("maxW",maxW) ;
        //mapList.add(map1) ;
        mapList2.add(map1) ;

        System.out.println("1111"+ mapList2);
        return ResultInfo.success(sortByWater(mapList2));
    }

    /**
     * 先按电 在按水
     *
     */
    private static List<Map<String, String>> sortByElectricityAndWater(List<Map<String, String>> mapList) {
        System.out.println("11112"+mapList.toString());
        Collections.sort(mapList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                System.out.println(map1.toString());
                BigDecimal d1=new BigDecimal(map1.get("dataE")== null?"0":map1.get("dataE"));
                BigDecimal d2=new BigDecimal(map2.get("dataE")== null?"0":map2.get("dataE"));

                // 根据需要的排序规则进行比较
                if(d2.compareTo(d1) == 0){//先按电 在按水
                    BigDecimal dw1=new BigDecimal(map1.get("dataW")== null?"0":map1.get("dataW"));
                    BigDecimal dw2=new BigDecimal(map2.get("dataW")== null?"0":map2.get("dataW"));
                    return dw2.compareTo(dw1);
                }else{
                    return d2.compareTo(d1);
                }
            }
        });
        System.out.println("22222"+ mapList);
        return mapList;
    }

    /**
     * 按水
     */
    private static List<Map<String, String>> sortByWater(List<Map<String, String>> mapList) {
//        System.out.println("11112"+mapList.toString());
        Collections.sort(mapList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
//                System.out.println(map1.toString());
//                BigDecimal d1=new BigDecimal(map1.get("dataE")== null?"0":map1.get("dataE"));
//                BigDecimal d2=new BigDecimal(map2.get("dataE")== null?"0":map2.get("dataE"));

                // 根据需要的排序规则进行比较
//                if(d2.compareTo(d1) == 0){//先按电 在按水
                    BigDecimal dw1=new BigDecimal(map1.get("dataW")== null?"0":map1.get("dataW"));
                    BigDecimal dw2=new BigDecimal(map2.get("dataW")== null?"0":map2.get("dataW"));
                    return dw2.compareTo(dw1);
//                }else{
//                    return d2.compareTo(d1);
//                }
            }
        });
//        System.out.println("22222"+ mapList);
        return mapList;
    }

    private static List<Map<String, String>> sortByKeys(List<Map<String, String>> mapList, String... keys) {
        Comparator<Map<String, String>> comparator = Comparator.comparing(map -> {
            StringBuilder keyConcatenation = new StringBuilder();
            for (String key : keys) {
                keyConcatenation.append(map.get(key));
            }
            return keyConcatenation.toString();
        });

        Collections.sort(mapList, comparator);
        return mapList;
    }

    private String getDataByDay2(DeviceQueryVo queryVo) {
        if (!CollectionUtils.isEmpty(queryVo.getDeviceCollects())) {
            String sql = "";
            if(StringUtils.isNotEmpty(queryVo.getGroupStr())){
                sql = "select " + queryVo.getGroupStr() + " as val from datasheet where floorId='" + queryVo.getFloorId() + "' and channelId='" + queryVo.getChannelId() + "' and time>'" + queryVo.getTimeStart() + "' and time <'" + queryVo.getTimeEnd() + "' ";
            }else{
                sql = "select last(val)-first(val) as val from datasheet where floorId='" + queryVo.getFloorId() + "' and channelId='" + queryVo.getChannelId() + "' and time>'" + queryVo.getTimeStart() + "' and time <'" + queryVo.getTimeEnd() + "' ";
            }
            if (queryVo.getDeviceCollects().size() == 1) {
                sql += " and deviceCollectId=~/^" + queryVo.getDeviceCollects().get(0).getId() + "$";
            } else {
                for (int i = 0; i < queryVo.getDeviceCollects().size(); i++) {
                    if (i == 0) {//~/^admin$|^username$/
                        sql += " and deviceCollectId=~/^" + queryVo.getDeviceCollects().get(0).getId() + "$";
                    } else if (i > 0) {
                        sql += "|^" + queryVo.getDeviceCollects().get(i).getId() + "$";
                    }
                }
            }
            sql += "/ GROUP by deviceCollectId ";
            sql = "select sum(val) from (" + sql + ")";
            System.out.println(sql);
            QueryResult queryResult = influxdbService.query(sql);
            List<QueryResult.Result> resultList = queryResult.getResults();
            for (QueryResult.Result query : resultList) {
                List<QueryResult.Series> seriesList = query.getSeries();
                if (seriesList == null || seriesList.size() == 0) continue;
                for (QueryResult.Series series : seriesList) {
                    List<List<Object>> values = series.getValues();
                    if (values != null && values.size() != 0) {
                        BigDecimal b = new BigDecimal(values.get(0).get(1) + "");
                        b = b.setScale(2, RoundingMode.HALF_UP);
                        return b + "";
                    }
                }
            }
        }

        return "";
    }


    public String quarterSumSqlList(EnergyReportVo vo) {
        String sql = "select spread(val) from datasheet where 1=1 ";
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sql += " and time>='" + vo.getStartTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sql += " and time<'" + vo.getEndTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getChannelId())) {
            sql += " and channelId='" + vo.getChannelId() + "'";
        }
        if (StringUtils.isNotNull(vo.getChannelIds()) && vo.getChannelIds().size() > 0) {
            String channelIds = "~/";
            for (int i = 0; i < vo.getChannelIds().size(); i++) {
                channelIds = channelIds + "^" + vo.getChannelIds().get(i) + "$|";
            }
            channelIds = channelIds.substring(0, channelIds.lastIndexOf("|"));
            channelIds += "/";
            sql += " and channelId=" + channelIds;
        }
        if (StringUtils.isNotNull(vo.getDeviceCollectId())) {
            sql += " and deviceCollectId='" + vo.getDeviceCollectId() + "'";
        }
        if (StringUtils.isNotNull(vo.getDeviceIdList()) && vo.getDeviceIdList().size() > 0) {
            String deviceIds = "~/";
            for (int i = 0; i < vo.getDeviceIdList().size(); i++) {
                deviceIds = deviceIds + "^" + vo.getDeviceIdList().get(i) + "$|";
            }
            deviceIds = deviceIds.substring(0, deviceIds.lastIndexOf("|"));
            deviceIds += "/";
            sql += " and deviceCollectId=" + deviceIds;
        }
        sql += " group by deviceCollectId";
        QueryResult query = influxdbService.query(sql);
        return this.anyResult(query);
    }

    public String remoteControl(DeviceCollectVo vo) {
        String jsonString = "";
        Integer id = vo.getId();

        // 查询所有发布主题
        LambdaQueryWrapper<ConfigTopic> topicConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        topicConfigLambdaQueryWrapper.eq(ConfigTopic::getIsDel, 0);
        topicConfigLambdaQueryWrapper.eq(ConfigTopic::getTopicType, 2);
        List<ConfigTopic> configTopicList = this.configTopicMapper.selectList(topicConfigLambdaQueryWrapper);
        List<HashMap<String, Object>> mapList = vo.getMapList();
        for (HashMap<String, Object> stringObjectHashMap : mapList) {
            if (stringObjectHashMap.containsKey("TEMP_LOWER")) {
                String temp_lower = (String) stringObjectHashMap.get("TEMP_LOWER");
                stringObjectHashMap.put("TEMP_LOWER", Double.parseDouble(temp_lower));
            }
            if (stringObjectHashMap.containsKey("TEMP_UPPER")) {
                String temp_lower = (String) stringObjectHashMap.get("TEMP_UPPER");
                stringObjectHashMap.put("TEMP_UPPER", Double.parseDouble(temp_lower));
            }
        }

        List<DeviceCollect> deviceTypeCodeList = this.deviceCollectMapper.getDeviceTypeCode(id);
        if (!deviceTypeCodeList.isEmpty()) {
            DeviceCollect deviceCollect = deviceTypeCodeList.get(0);
            String deviceCode = deviceCollect.getDeviceCode();

            RemoteControl remoteControl = new RemoteControl();
            RemoteControl.Commend commend = remoteControl.getCommend();

            commend.setChannelName(deviceCode);
            commend.setDevName(deviceCode);

            remoteControl.getCommend().values.addAll(vo.getMapList());
            remoteControl.setTopicList(configTopicList);

            jsonString = JSON.toJSONString(remoteControl);
        }
        return jsonString;
    }

    public List<ConfigSignalType> getSignalByDevice(Integer id) {
        DeviceCollect deviceCollect = this.deviceCollectService.getById(id);
        // 根据设备的设备类型获取signal
        Integer deviceType = deviceCollect.getDeviceType();
        List<ConfigSignalType> configSignalTypeListDB = this.configDeviceSignalMapMapper.getSignalListByDeviceCode(deviceType);
        return configSignalTypeListDB;
    }

    /**
     * 能耗报表
     *
     * @param queryVo
     * @return
     */
    public ResultInfo analysis(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
        Page<DeviceCollectVo> pageInfo = new Page<DeviceCollectVo>();
        if(queryVo.getPageNo() == null || queryVo.getPageSize() == null){
            List<DeviceCollectVo> list = deviceCollectMapper.getDeviceReportList(queryVo);
            pageInfo.setRecords(list);
        }else{
            Page<DeviceCollectVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
            pageInfo = deviceCollectMapper.getDeviceReportPage(page, queryVo);
        }
        List<JSONObject> data = new ArrayList<>();//数据
        //列的封装
        List<String> columns = new ArrayList<>();
        if ("1".equals(queryVo.getTimeType())) {//按日
            Date timeStart = DateUtil.parseDate(queryVo.getTimeStart());
            Date timeEnd = DateUtil.parseDate(queryVo.getTimeEnd());
            long days = DateUtil.between(timeStart, timeEnd, DateUnit.DAY);
            if (days >= 31) {
                return ResultInfo.error("日期区间不得大于31天");
            }
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, timeEnd));
            queryVo.setTimeEnd(DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(queryVo.getTimeEnd()), 1)));
        } else if ("2".equals(queryVo.getTimeType())) {//按月
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"));
            Date endStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"), 1));
            queryVo.setTimeStart(DateUtil.format(timeStart, "yyyy-MM-dd"));
            queryVo.setTimeEnd(DateUtil.format(endStart, "yyyy-MM-dd"));
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"))));
        } else if ("3".equals(queryVo.getTimeType())) {
            for (int i = 1; i <= 12; i++) {
                columns.add(i + "月");
            }
        }
        if ("1".equals(queryVo.getEnergyType())) {
            queryVo.setChannelId("ENERGY_TOTAL");
        } else if ("2".equals(queryVo.getEnergyType())) {
            //todo 水信号
            queryVo.setChannelId("WATER_L");
        }
        //判断统计维度--》日、月都为按日统计
        if ("1".equals(queryVo.getTimeType()) || "2".equals(queryVo.getTimeType())) {
            //判断能源类型决定信号类型
            JSONObject deviceInfo = this.getDataByDay(queryVo);
            for (DeviceCollect record : pageInfo.getRecords()) {
                JSONObject json = new JSONObject();
                json.put("deviceName", record.getName());
                json.put("deviceCode", record.getDeviceCode());
                //用区域查楼层 逻辑位置
                if (StringUtils.isNotNull(record.getLogicalAreaId()) && record.getLogicalAreaId() != 0) {
                    SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(record.getLogicalAreaId());
                    if (StringUtils.isNotNull(sysBuildArea2)) {
                        //查楼层
                        SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                        if (StringUtils.isNotNull(sysBuildFloor)) {
                            //查楼宇
                            SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                            json.put("areaName", sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                        }
                    }
                } else if (StringUtils.isNotNull(record.getLogicalFloorId()) && record.getLogicalFloorId() != 0) {
                    //查楼层
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(record.getLogicalFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        json.put("areaName", sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
                    }
                } else if (StringUtils.isNotNull(record.getLogicalBuildId()) && record.getLogicalBuildId() != 0) {
                    //查楼宇
                    SysBuild sysBuild = sysBuildMapper.selectById(record.getLogicalBuildId());
                    json.put("areaName", sysBuild.getBuildName());
                }
//                json.put("areaName", record.getAreaName());
                json.put("address", record.getAddress());
                //查询该设备的电量
                Object o = deviceInfo.get(record.getId() + "");
                if (o == null) {
                    data.add(json);
                    continue;
                }
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;
                for (Map<String, Object> obj : dataList) {
                    String key = obj.get("time") + "";
                    String value = obj.get("val") + "";
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                    } else {
                        value = "0.00";
                    }
                    json.put(key.substring(0, 10), value);
                }
                data.add(json);
            }
        } else {//按照年统计
            JSONObject dataByMonth = getDataByMonth(queryVo);
            for (DeviceCollect record : pageInfo.getRecords()) {
                JSONObject json = new JSONObject();
                json.put("deviceName", record.getName());
                json.put("deviceCode", record.getDeviceCode());
                //用区域查楼层 逻辑位置
                if (StringUtils.isNotNull(record.getLogicalAreaId()) && record.getLogicalAreaId() != 0) {
                    SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(record.getLogicalAreaId());
                    if (StringUtils.isNotNull(sysBuildArea2)) {
                        //查楼层
                        SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                        if (StringUtils.isNotNull(sysBuildFloor)) {
                            //查楼宇
                            SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                            json.put("areaName", sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                        }
                    }
                } else if (StringUtils.isNotNull(record.getLogicalFloorId()) && record.getLogicalFloorId() != 0) {
                    //查楼层
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(record.getLogicalFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        json.put("areaName", sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
                    }
                } else if (StringUtils.isNotNull(record.getLogicalBuildId()) && record.getLogicalBuildId() != 0) {
                    //查楼宇
                    SysBuild sysBuild = sysBuildMapper.selectById(record.getLogicalBuildId());
                    json.put("areaName", sysBuild.getBuildName());
                }
//                json.put("areaName", record.getAreaName());
                json.put("address", record.getAddress());
                for (int i = 1; i <= 12; i++) {
                    JSONObject obj_month = (JSONObject) dataByMonth.get(i + "");
                    if (obj_month != null && obj_month.size() != 0 && obj_month.get(record.getId()) != null) {
                        HashMap obj = (HashMap) obj_month.get(record.getId());
                        String value = obj.get("val") + "";
                        if (value != null && !"".equals(value) && !"null".equals(value)) {
                            value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                        }
                        json.put(obj.get("time") + "月", value);
                    } else {
                        json.put(i + "月", 0.00);
                    }
                }
                data.add(json);
            }
        }
        result.put("data", data);
        result.put("columns", columns);
        result.put("total", pageInfo.getTotal());
        return ResultInfo.success(result);
    }

    /**
     * 复费率
     *
     * @param queryVo
     * @return
     */
    public ResultInfo multiRate(DeviceQueryVo queryVo) {
        JSONObject result = new JSONObject();
        Page<DeviceCollectVo> pageInfo = new Page<DeviceCollectVo>();
        if(queryVo.getPageNo() == null || queryVo.getPageSize() == null){
            List<DeviceCollectVo> list = deviceCollectMapper.getDeviceReportList(queryVo);
            pageInfo.setRecords(list);
        }else{
            Page<DeviceCollectVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
            pageInfo = deviceCollectMapper.getDeviceReportPage(page, queryVo);
        }
        List<JSONObject> data = new ArrayList<>();//数据
        String jsonTime = "";

        //列的封装
        List<String> columns = new ArrayList<>();
        if ("1".equals(queryVo.getTimeType())) {//按日
            jsonTime = queryVo.getTimeStart() + "~" + queryVo.getTimeEnd();

            Date timeStart = DateUtil.parseDate(queryVo.getTimeStart());
            Date timeEnd = DateUtil.parseDate(queryVo.getTimeEnd());
            long days = DateUtil.between(timeStart, timeEnd, DateUnit.DAY);
            if (days >= 31) {
                return ResultInfo.error("日期区间不得大于31天");
            }
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, timeEnd));
            queryVo.setTimeEnd(DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(queryVo.getTimeEnd()), 1)));
        } else if ("2".equals(queryVo.getTimeType())) {//按月
            jsonTime = queryVo.getTimeStr();

            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"));
            Date endStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"), 1));
            queryVo.setTimeStart(DateUtil.format(timeStart, "yyyy-MM-dd"));
            queryVo.setTimeEnd(DateUtil.format(endStart, "yyyy-MM-dd"));
            columns.addAll(JsdcDateUtil.getDatesInRange(timeStart, DateUtil.endOfMonth(DateUtil.parse(queryVo.getTimeStr(), "yyyy-MM"))));
        } else if ("3".equals(queryVo.getTimeType())) {
            jsonTime = queryVo.getTimeStr();

            for (int i = 1; i <= 12; i++) {
                columns.add(i + "月");
            }
        }
        if ("1".equals(queryVo.getEnergyType())) {
            queryVo.setChannelId("ENERGY_TOTAL");
        } else if ("2".equals(queryVo.getEnergyType())) {
            //todo 水信号
            queryVo.setChannelId("WATER_L");
        }
        //峰谷平
        List<PeakGuPingTime> timeList = peakGuPingTimeService.list();
        //判断能源类型决定信号类型
        JSONObject deviceInfo = this.getDataByHours(queryVo);
        for (DeviceCollect record : pageInfo.getRecords()) {
            JSONObject json = new JSONObject();
            json.put("deviceName", record.getName());
            json.put("deviceCode", record.getDeviceCode());
            //用区域查楼层 逻辑位置
            if (StringUtils.isNotNull(record.getLogicalAreaId()) && record.getLogicalAreaId() != 0) {
                SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(record.getLogicalAreaId());
                if (StringUtils.isNotNull(sysBuildArea2)) {
                    //查楼层
                    SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                    if (StringUtils.isNotNull(sysBuildFloor)) {
                        //查楼宇
                        SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                        json.put("areaName", sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                    }
                }
            } else if (StringUtils.isNotNull(record.getLogicalFloorId()) && record.getLogicalFloorId() != 0) {
                //查楼层
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(record.getLogicalFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    json.put("areaName", sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
                }
            } else if (StringUtils.isNotNull(record.getLogicalBuildId()) && record.getLogicalBuildId() != 0) {
                //查楼宇
                SysBuild sysBuild = sysBuildMapper.selectById(record.getLogicalBuildId());
                json.put("areaName", sysBuild.getBuildName());
            }
//            json.put("areaName", record.getAreaName());
            json.put("address", record.getAddress());
            json.put("time", jsonTime);
            json.put("sum","0.00");
            json.put("peak","0.00");
            json.put("gu","0.00");
            json.put("ping","0.00");
            //查询该设备的电量
            Object o = deviceInfo.get(record.getId() + "");
            if (o == null) {
                data.add(json);
                continue;
            }
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) o;
            String sum = "0.00";
            String peak = "0.00";
            String gu = "0.00";
            String ping = "0.00";

            for (Map<String, Object> obj : dataList) {
                String key = obj.get("time") + "";
                String value = obj.get("val") + "";
                if (value != null && !"".equals(value) && !"null".equals(value)) {
                    value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
                } else {
                    value = "0.00";
                }
                //峰谷平计算
                for (int i = 0; i < timeList.size(); i++) {
                    PeakGuPingTime time = timeList.get(i);
                    Integer localTime = Integer.parseInt(key.substring(11, 13)); // 数据时间
                    Integer startTime = Integer.parseInt(time.getStartTime().equals("24") ? "00" : time.getStartTime()); // 起始时间
                    Integer endTime = Integer.parseInt(time.getEndTime().equals("24") ? "00" : time.getEndTime()); // 结束时间
                    if (localTime >= startTime && localTime < endTime) {
                        //0 高峰 1低估
                        if (time.getType().equals("0")) {
                            peak = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                                    .add(new BigDecimal(peak).setScale(2, RoundingMode.HALF_UP)).toString();
                        } else if (time.getType().equals("1")) {
                            gu = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                                    .add(new BigDecimal(gu).setScale(2, RoundingMode.HALF_UP)).toString();
                        }
                    } else {
                        ping = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                                .add(new BigDecimal(gu).setScale(2, RoundingMode.HALF_UP)).toString();
                    }
                }
                sum = new BigDecimal(peak).setScale(2, RoundingMode.HALF_UP)
                        .add(new BigDecimal(gu).setScale(2, RoundingMode.HALF_UP))
                        .add(new BigDecimal(ping).setScale(2, RoundingMode.HALF_UP)).toString();
            }
            json.put("sum",sum);
            json.put("peak",peak);
            json.put("gu",gu);
            json.put("ping",ping);
            data.add(json);
        }
        result.put("data", data);
        result.put("columns", columns);
        result.put("total", pageInfo.getTotal());
        return ResultInfo.success(result);
    }


    public JSONObject energyConsumption(){

        JSONObject result=new JSONObject();
        DataSheetVo bean = new DataSheetVo();


        EnergyReportVo vo = new EnergyReportVo() ;
        List<Integer> collectIds = new ArrayList<>();

        // 电
        ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
        configDeviceType2.setDeviceTypeCode("E_METER");
        List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);

        DeviceCollectVo collectVo = new DeviceCollectVo();
        collectVo.setDeviceType(deviceTypeList2.get(0).getId());
        collectVo.setLogicalFloorId(0);//大楼总电表
        List<DeviceCollect> collectList = deviceCollectService.getListByFloorId(collectVo);
        collectIds = collectList.stream().map(DeviceCollect::getId).collect(Collectors.toList());
        vo.setChannelId("ENERGY_TOTAL");
        vo.setDeviceIdList(collectIds);
        //时间类型 1 年度 2 月度 3 日
        vo.setTimeType("2");
        Double thisTimeElectricity = Double.valueOf(deviceCollectService.setEnergyParamData(vo));


        result.put("thisTimeElectricity",String.format("%.2f",thisTimeElectricity));
        // 上月用电
        vo.setTimeType("6");
        Double lastTimeElectricity = Double.valueOf(deviceCollectService.setEnergyParamData(vo));
        result.put("lastTimeElectricity",String.format("%.2f",lastTimeElectricity));

        Double radioElectricity=(0==lastTimeElectricity)?thisTimeElectricity:((thisTimeElectricity - lastTimeElectricity) / lastTimeElectricity) * 100;

        if(radioElectricity > 0.0){
            result.put("radioElectricity",new BigDecimal(radioElectricity).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioElectricity == 0.0){
            result.put("radioElectricity",0);
        }else {
            Double radioElectricity2 = radioElectricity*-1 ;
            result.put("radioElectricity",new BigDecimal(radioElectricity2).setScale(2, ROUND_HALF_UP).toString());
        }


        // 用水
        List<Integer> collectIdsWater = new ArrayList<>();

        // 电
        ConfigDeviceType configDeviceTypeWater = new ConfigDeviceType();
        configDeviceTypeWater.setDeviceTypeCode("W_METER");
        List<ConfigDeviceType> deviceTypeListWater = configDeviceTypeService.getList(configDeviceTypeWater);

        DeviceCollectVo collectVoWater = new DeviceCollectVo();
        collectVoWater.setDeviceType(deviceTypeListWater.get(0).getId());
        collectVoWater.setLogicalFloorId(0);//大楼总电表
        List<DeviceCollect> collectListWater = deviceCollectService.getListByFloorId(collectVoWater);
        collectIdsWater = collectListWater.stream().map(DeviceCollect::getId).collect(Collectors.toList());
        vo.setChannelId("WATER_L");
        vo.setDeviceIdList(collectIdsWater);
        //时间类型 1 年度 2 月度 3 日
        vo.setTimeType("2");
        Double thisTimeWater = Double.valueOf(deviceCollectService.setEnergyParamData(vo));


        result.put("thisTimeWater",String.format("%.2f",thisTimeWater));
        // 上月用电
        vo.setTimeType("6");
        Double lastTimeWater = Double.valueOf(deviceCollectService.setEnergyParamData(vo));
        result.put("lastTimeWater",String.format("%.2f",lastTimeWater));


        Double radioWater=(0==lastTimeWater)?thisTimeWater:((thisTimeWater - lastTimeWater) / lastTimeWater) * 100;



        if(radioWater > 0.0){
            result.put("radioWater",new BigDecimal(radioWater).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioWater == 0.0){
            result.put("radioWater",0);
        }else {
            Double radioWater2 = radioWater*-1 ;
            result.put("radioWater",new BigDecimal(radioWater2).setScale(2, ROUND_HALF_UP).toString());
        }





//        bean.setChannelId("WATER_L");
        bean.setInfluxVal("MAX(val)-MIN(val)");
//        bean.setEnergyType(2);
        bean.setTimeType(2);


        //总碳排放量（吨）=用电量(度)*0.785/1000

        Double thisTimeCarbon = thisTimeElectricity*0.000785 ;
        Double lastTimeCarbon = lastTimeElectricity*0.000785 ;
        // 本月碳排放
        result.put("thisTimeCarbon",String.format("%.2f",thisTimeElectricity*0.000785));
        // 上月碳排放
        result.put("lastTimeCarbon",String.format("%.2f",lastTimeElectricity*0.000785));


        Double radioCarbon=(0==lastTimeCarbon)?thisTimeCarbon:((thisTimeCarbon-lastTimeCarbon) / lastTimeCarbon) * 100;

        if(radioElectricity > 0.0){
            result.put("radioCarbon",new BigDecimal(radioCarbon).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioElectricity == 0.0){
            result.put("radioCarbon",0);
        }else {
            Double radioCarbon2 = radioElectricity*-1 ;
            result.put("radioCarbon",new BigDecimal(radioCarbon2).setScale(2, ROUND_HALF_UP).toString());
        }





        return result ;

    }


    public JSONObject subitemEnergy(String id){
        JSONObject jsonObject = new JSONObject() ;
        DeviceCollect deviceCollect = deviceCollectMapper.selectById(id) ;
        ConfigDeviceSubitem configDeviceSubitem = configDeviceSubitemMapper.selectById(deviceCollect.getSubitem());
        if("1".equals(configDeviceSubitem.getEnergy_type())){
            //用电统计
            Double toDay1 =0.0 ;
            Double lastDay1 =0.0 ;
            Double averDay =0.0 ;
            Double toMonth1 =0.0 ;
            Double lastMonth1 =0.0 ;
            Double averMonth1 =0.0 ;
            Double toYear1 =0.0 ;
            Double lastYear1 =0.0 ;
            Double averYear =0.0 ;
            Double total1 =0.0 ;
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));

            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                            new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1, new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));

            averDay =(toDay1+lastDay1)/2 ;

            jsonObject.put("toDay1",new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("lastDay1",new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("averDay",new BigDecimal(averDay).setScale(2, ROUND_HALF_UP).toString());

            Double radioDay=(0==lastDay1)?toDay1:((toDay1 - lastDay1) / lastDay1) * 100;
           if(radioDay == 0.0){
                jsonObject.put("radioDay",0);
            }else {
                jsonObject.put("radioDay",new BigDecimal(radioDay).setScale(2, ROUND_HALF_UP).toString());
            }


            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));

            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));

            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            averMonth1 =(toMonth1+lastMonth1)/2 ;


            jsonObject.put("toMonth1",new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("lastMonth1",new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("averMonth1",new BigDecimal(averMonth1).setScale(2, ROUND_HALF_UP).toString());

            Double radioMonth=(0==lastMonth1)?toMonth1:((toMonth1 - lastMonth1) / lastMonth1) * 100;
            if(radioMonth == 0.0){
                jsonObject.put("radioMonth",0);
            }else {
                jsonObject.put("radioMonth",new BigDecimal(radioMonth).setScale(2, ROUND_HALF_UP).toString());
            }


            QueryResult toYear = influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyEvaluate2(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));

            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            toYear1 += Double.parseDouble(this.spreadResultProcess(toYearList));

            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            lastYear1 += Double.parseDouble(this.spreadResultProcess(lastYearList));

            averYear =(toYear1+lastYear1)/2 ;

            jsonObject.put("toYear1",new BigDecimal(toYear1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("lastYear1",new BigDecimal(lastYear1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("averYear",new BigDecimal(averYear).setScale(2, ROUND_HALF_UP).toString());


            Double radioYear=(0==lastYear1)?toYear1:((toYear1 - lastYear1) / lastYear1) * 100;
            if(radioYear > 0.0){
                jsonObject.put("radioYear", new BigDecimal(radioYear).setScale(2, ROUND_HALF_UP).toString());
            }else if(radioYear == 0.0){
                jsonObject.put("radioYear",0);
            }else {
                jsonObject.put("radioYear",new BigDecimal(radioYear*-1).setScale(2,RoundingMode.HALF_UP));
            }


            QueryResult total = influxdbService.query(this.getEnergyEvaluateTotal( deviceCollect.getId() + ""));
            List<Map<String, Object>> totalList = influxdbService.queryResultProcess(total);
            total1 += Double.parseDouble(this.spreadResultProcess2(totalList));
            jsonObject.put("total1",new BigDecimal(total1).setScale(2, ROUND_HALF_UP).toString());


            //当量值：总标煤（tce）=总用电量（万度）*1.229
            double tce = total1 / 10000 * 1.229;
            jsonObject.put("tce",new BigDecimal(tce).setScale(2, ROUND_HALF_UP).toString()) ;
            String co2 = new BigDecimal(total1 * 0.785 / 1000).setScale(2, ROUND_HALF_UP).toString();
            jsonObject.put("co2",co2) ;

        }else if("2".equals(configDeviceSubitem.getEnergy_type())){
            //用水统计

            Double toDay1 =0.0 ;
            Double lastDay1 =0.0 ;
            Double averDay =0.0 ;
            Double toMonth1 =0.0 ;
            Double lastMonth1 =0.0 ;
            Double averMonth1 =0.0 ;
            Double toYear1 =0.0 ;
            Double lastYear1 =0.0 ;
            Double averYear =0.0 ;
            Double total1 =0.0 ;
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));

            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                            new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1, new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));

            averDay =(toDay1+lastDay1)/2 ;

            jsonObject.put("toDay1",new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("lastDay1",new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("averDay",new BigDecimal(averDay).setScale(2, ROUND_HALF_UP).toString());

            Double radioDay=(0==lastDay1)?toDay1:((toDay1 - lastDay1) / lastDay1) * 100;
            if(radioDay > 0.0){
                jsonObject.put("radioDay",new BigDecimal(radioDay).setScale(2, ROUND_HALF_UP).toString());
            }else if(radioDay == 0.0){
                jsonObject.put("radioDay",0);
            }else {
                jsonObject.put("radioDay",radioDay*-1);
            }


            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));

            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));

            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            averMonth1 =(toMonth1+lastMonth1)/2 ;


            jsonObject.put("toMonth1",new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("lastMonth1",new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("averMonth1",new BigDecimal(averMonth1).setScale(2, ROUND_HALF_UP).toString());

            Double radioMonth=(0==lastMonth1)?toMonth1:((toMonth1 - lastMonth1) / lastMonth1) * 100;
           if(radioMonth == 0.0){
                jsonObject.put("radioMonth",0);
            }else {
                jsonObject.put("radioMonth",new BigDecimal(radioMonth).setScale(2, ROUND_HALF_UP).toString());
            }


            QueryResult toYear = influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));

            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            toYear1 += Double.parseDouble(this.spreadResultProcess(toYearList));

            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            lastYear1 += Double.parseDouble(this.spreadResultProcess(lastYearList));

            averYear =(toYear1+lastYear1)/2 ;

            jsonObject.put("toYear1",new BigDecimal(toYear1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("lastYear1",new BigDecimal(lastYear1).setScale(2, ROUND_HALF_UP).toString());
            jsonObject.put("averYear",new BigDecimal(averYear).setScale(2, ROUND_HALF_UP).toString());


            Double radioYear=(0==lastYear1)?toYear1:((toYear1 - lastYear1) / lastYear1) * 100;
           if(radioYear == 0.0){
                jsonObject.put("radioYear",0);
            }else {
                jsonObject.put("radioYear",new BigDecimal(radioYear).setScale(2, ROUND_HALF_UP).toString());
            }


            QueryResult total = influxdbService.query(this.getWaterTotal( deviceCollect.getId() + ""));
            List<Map<String, Object>> totalList = influxdbService.queryResultProcess(total);
            total1 += Double.parseDouble(this.spreadResultProcess2(totalList));
            jsonObject.put("total1",new BigDecimal(total1).setScale(2, ROUND_HALF_UP).toString());



        }

        return  jsonObject ;
    }

    public JSONObject getSubitemEnergy(DeviceQueryVo bean) {

        if(StringUtils.isEmpty(bean.getCompareStart()) || StringUtils.isEmpty(bean.getCompareEnd())){
            throw new RuntimeException("请选择对比日期！");
        }

//对比时间
        String compareStart = bean.getCompareStart()+" 00:00:00" ;
        String compareEnd = bean.getCompareEnd()+" 00:00:00" ;

        bean.setTimeStart(compareStart);
        bean.setTimeEnd(compareEnd);


        LinkedHashMap<String,String> thisXData = new LinkedHashMap<>() ;
        // private String timeType;//1:日 2：月 3：年 4 周
        //选中类型的时间
        String startDate = "" ;
        String endDate = "" ;
        List<String> strings = null ;
        if (bean.getTimeType().equals("3")) {
            // 年度
            startDate = new DateTime().toString("yyyy-01-01 00:00:00") ;
            endDate = new DateTime().plusYears(1).toString("yyyy-01-01 00:00:00") ;
            strings =DateUtils.getMonList(startDate,endDate);
            for(String s:strings){
                thisXData.put(s,"0.00") ;
            }
        }
        if (bean.getTimeType().equals("2")) {
            // 月度
            startDate = new DateTime().toString("yyyy-MM-01 00:00:00") ;
            endDate = new DateTime().plusMonths(1).toString("yyyy-MM-01 00:00:00") ;

        }
        if (bean.getTimeType().equals("1")) {
            // 天
            startDate = new DateTime().toString("yyyy-MM-dd 00:00:00") ;
            endDate = new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00") ;
            thisXData=DateUtils.getTimeHours(startDate,endDate);

        }

        if (bean.getTimeType().equals("4")) {
            // 周

            // 获取当前日期
            LocalDate today = LocalDate.now();

            // 计算本周的第一天（周一）
            LocalDate firstDayOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

            // 计算本周的最后一天（周日）
            LocalDate lastDayOfWeek = today.with(TemporalAdjusters.next(java.time.DayOfWeek.SUNDAY));
// 计算本周最后一天的下一天
            LocalDate dayAfterLastDayOfWeek = lastDayOfWeek.plusDays(1);

            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 输出结果


            startDate = firstDayOfWeek.format(formatter)+" 00:00:00" ;
            endDate =dayAfterLastDayOfWeek.format(formatter)+" 00:00:00" ;

        }


        JSONObject jsonObject=new JSONObject();




        Double toDay1 = 0.00;


        Double time1 = 0.00;
        // 获取设备
//        List<DeviceCollectVo> deviceCollects=deviceCollectMapper.getSubitemEnergy(bean);
        // 随机取个楼宇的id
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));

        // 获取采集设备
        DataSheetVo bean2 = new DataSheetVo() ;
        if(null != bean.getBuildIds()){
            bean2.setBuildIds(bean.getBuildIds());
        }

        if(null != bean.getSubitemId()){
            bean2.setSubitem(bean.getSubitemId());
        }

        if(null != bean.getFloorIds()){
            bean2.setFloorIds(bean.getFloorIds());
        }

        if(null != bean.getAreaIds()){
            bean2.setAreaIds(bean.getAreaIds());
        }

        //不选默认查楼宇
        if(null == bean.getBuildIds() && null == bean.getFloorIds() && null == bean.getAreaIds()){
            List<Integer> integers = new ArrayList<>() ;
            integers.add(121);
            bean.setBuildIds(integers) ;
        }

        //选中楼宇  统计楼宇的电表   不选则统计楼层电表总和
        List<DeviceCollect> deviceCollects =new ArrayList<>() ;
        List<String> ids = new ArrayList<>();
        List<String> ids2 = new ArrayList<>();
        List<String> ids3 = new ArrayList<>();
        if(null != bean.getBuildIds() && null == bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            //楼宇总电表
            List<String> collectIds = new ArrayList<>();

            // 电
            ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
            configDeviceType2.setDeviceTypeCode("E_METER");
            List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalFloorId(0);//大楼总电表
            deviceCollects = deviceCollectService.getListByFloorId(collectVo);
            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
        }else {
            //统计楼层电表 或者区域总空开设备
            bean2.setIsTotalDevice(1);
            deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            //没有总设备的  统计所有不是总设备的（包括了虚拟设备 ）
            if(CollectionUtils.isEmpty(deviceCollects)){
                bean2.setIsTotalDevice(0);
                deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            } else {
                //有总设备-选区域时-加上区域里面的虚拟设备 格力
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200124);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids2 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


                //有总设备-选区域时-加上区域里面的虚拟设备 大金
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200166);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids3 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


            }



            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(ids2)){
                //并集
                ids = Stream.concat(ids.stream(), ids2.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }

            if(!CollectionUtils.isEmpty(ids3)){
                //并集
                ids = Stream.concat(ids.stream(), ids3.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
        }


        if(CollectionUtils.isEmpty(deviceCollects)){
            jsonObject.put("xData",thisXData.keySet());


            ArrayList<String> arrayList = new ArrayList<>() ;
            for(int i = 0 ;i<25;i++){
                arrayList.add("0.00") ;
            }
            jsonObject.put("yData",arrayList);

            jsonObject.put("yData2",arrayList);
            jsonObject.put("toDay", 0.0);
            jsonObject.put("time", 0.0);
            jsonObject.put("radioElectricity",0);
            return jsonObject ;
        }
//        List<String> ids = deviceCollectsollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());



        String channelId="";
        String influxVal="";
        switch (bean.getEnergyType()){
            case "1":
                channelId="ENERGY_TOTAL";
                influxVal="last(val)-first(val)";
                break;
            case "2":
                channelId="WATER_L";
                influxVal="spread(val)";
                break;
            default:
                channelId="GAS_METERING";
                influxVal="spread(val)";
                break;

        }
        String sql = "" ;
        Map<String, Double> result = new LinkedHashMap<>();
        if (bean.getTimeType().equals("1")) {
            sql = getInfluxSql(influxVal, startDate, endDate, "time(1h)", channelId, ids);
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            for (Map<String, Object> map : maps) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }

        }else if(bean.getTimeType().equals("2") || bean.getTimeType().equals("4")){
            sql = getInfluxSql(influxVal, startDate, endDate, "time(1d)", channelId, ids);

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            for (Map<String, Object> map : maps) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }
        }else if(bean.getTimeType().equals("3")){
            for (int i = 0; i < strings.size(); i++) {

                String nextDay = DateUtils.nextDay(strings.get(i)) ;
                sql = getInfluxSql(influxVal, strings.get(i)+"-01", nextDay, null, channelId, ids);

                // 时序数据库解析
                QueryResult query = influxdbService.query(sql);

                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0.00 ;
                if(!CollectionUtils.isEmpty(maps)){
                    for (Map<String, Object> map : maps) {
                        sum += (Double)map.get("totalPower") ;
                    }
                }
                result.put(strings.get(i), sum);



            }


        }









        if (bean.getTimeType().equals("1")) {
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 19).replaceAll("T"," "), String.format("%.2f", item.getValue()));
            }
        }else if(bean.getTimeType().equals("2")){
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
        }else if(bean.getTimeType().equals("3")){
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey(), String.format("%.2f", item.getValue()));
            }
        }else if(bean.getTimeType().equals("4")){
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
        }










        JSONObject jsonObject1 = getSubitemEnergy2(bean) ;
        jsonObject.put("xData",thisXData.keySet());
        //现在数据
        jsonObject.put("yData",thisXData.values());
        //对比数据
        jsonObject.put("yData2",jsonObject1.get("yData"));

        jsonObject.put("xData2",jsonObject1.get("xData"));


        if("1".equals(bean.getEnergyType())){





            for (String deviceCollect : ids) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluate2(endDate, startDate, deviceCollect+ ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);

                toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));


                QueryResult time = influxdbService.query(this.getEnergyEvaluate2(compareEnd,compareStart, deviceCollect + ""));
                List<Map<String, Object>> timeList = influxdbService.queryResultProcess(time);
                time1 += Double.parseDouble(this.spreadResultProcess(timeList));

            }
        }else {
            for (DeviceCollect deviceCollect : deviceCollects) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(endDate, startDate, deviceCollect.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);

                toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));


                QueryResult time = influxdbService.query(this.getEnergyEvaluateWater(compareEnd,compareStart, deviceCollect.getId() + ""));
                List<Map<String, Object>> timeList = influxdbService.queryResultProcess(time);
                time1 += Double.parseDouble(this.spreadResultProcess(timeList));

            }
        }


        jsonObject.put("toDay", new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
        jsonObject.put("time", new BigDecimal(time1).setScale(2, ROUND_HALF_UP).toString());


        Double radio=(0==time1)?toDay1:((toDay1 - time1) / time1) * 100;


        if(radio > 0.0){
            jsonObject.put("radioElectricity",new BigDecimal(radio).setScale(2, ROUND_HALF_UP).toString());
        }else if(radio == 0.0){
            jsonObject.put("radioElectricity",0);
        }else {
            jsonObject.put("radioElectricity",new BigDecimal(radio).setScale(2, ROUND_HALF_UP));
        }
        return jsonObject;

    }

//    public JSONObject getSubitemEnergy3(DeviceQueryVo bean) {
//        String year  = DateUtil.format(new Date(), "yyyy") ;
//        SysUser loginUser = sysUserService.getUser();
//        Integer orgId = loginUser.getUnitId();
//        if (orgId == null) {
//            return null;
//        }
////
////        TenantContract contract = tenantContractService.getByMerchantId(orgId+"");
////        if (contract == null) {
////            return null;
////        }
//
//        JSONObject jsonObject = new JSONObject() ;
//        List<String> xData = new ArrayList<>();
//        List<String> yData = new ArrayList<>();
//        // 区间用电量
//        String timeLastSql="" ;
//        String timeFirstSql="" ;
//        List<HireRoomOfDevice> deviceList = null ;
//        String roomIds = contract.getHireRoomIds();
//        List<String> list = Arrays.asList(roomIds.split(","));
//        if("1".equals(bean.getEnergyType())){
//             timeLastSql = "SELECT LAST(val) as val FROM datasheet WHERE channelId = 'ENERGY_TOTAL' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//             timeFirstSql = "SELECT FIRST(val) as val FROM datasheet WHERE channelId = 'ENERGY_TOTAL' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//
//             deviceList = hireRoomOfDeviceMapper.selectList(new LambdaQueryWrapper<HireRoomOfDevice>().in(HireRoomOfDevice::getRoom, list).eq(HireRoomOfDevice::getDeviceType, "1"));
//        }else {
//            timeFirstSql = "SELECT FIRST(val) as val FROM datasheet WHERE channelId = 'WATER_L' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//            timeLastSql = "SELECT LAST(val) as val FROM datasheet WHERE channelId = 'WATER_L' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//
//            deviceList = hireRoomOfDeviceMapper.selectList(new LambdaQueryWrapper<HireRoomOfDevice>().in(HireRoomOfDevice::getRoom, list).eq(HireRoomOfDevice::getDeviceType, "3"));
//
//        }
//
//
//        // private String timeType;//1:日 2：月 3：年 4 周
//        //选中类型的时间
//        String startDate = "" ;
//        String endDate = "" ;
//        List<String> strings = null ;
//        if (bean.getTimeType().equals("3")) {
//            // 年度
//            startDate = new DateTime().toString("yyyy-01-01 00:00:00") ;
//            endDate = new DateTime().plusYears(1).toString("yyyy-01-01 00:00:00") ;
//
//
//            String contractStartDate = contract.getLeaseStartTime() + " 00:00:00";
//            String contractEndDate = contract.getLeaseEndTime() + " 23:59:59";
//
//            cn.hutool.core.date.DateTime contractStartDateTime = DateUtil.parseDateTime(contractStartDate);
//            cn.hutool.core.date.DateTime contractEndDateTime = DateUtil.parseDateTime(contractEndDate);
//
//            // 统计开始时间 早于 合同开始时间
//            if (DateUtil.parseDateTime(startDate).compareTo(contractStartDateTime) < 0) {
//                startDate = contractStartDate;
//            }
//            // 统计结束时间 晚于 合同结束时间
//            if (DateUtil.parseDateTime(endDate).compareTo(contractEndDateTime) > 0) {
//                endDate = contractEndDate;
//            }
//
//            for (int i = 1; i <= 12; i++) {
//                if (i < 10) {
//                    xData.add(year + "-0" + i);
//                } else {
//                    xData.add(year + "-" + i);
//                }
//            }
//            for (String x : xData) {
//                String start = x + "-01 00:00:00";
//                cn.hutool.core.date.DateTime endMonth = DateUtil.endOfMonth(DateUtil.parseDate(start));
//                String end = DateUtil.formatDate(endMonth) + " 23:59:59";
//                BigDecimal yTotal = BigDecimal.ZERO;
//                cn.hutool.core.date.DateTime startTime = DateUtil.parseDateTime(start);
//                cn.hutool.core.date.DateTime endTime = DateUtil.parseDateTime(end);
//                // 统计结束时间 早于 合同开始时间
//                if (endTime.compareTo(contractStartDateTime) < 0) {
//                    yData.add(yTotal.toString());
//                    continue;
//                }
//                // 统计开始时间 晚于 合同结束时间
//                else if (startTime.compareTo(contractEndDateTime) > 0) {
//                    yData.add(yTotal.toString());
//                    continue;
//                }
//                // 合同开始时间 晚于 统计开始时间 && 合同开始时间 早于 统计结束时间
//                if (contractStartDateTime.compareTo(startTime) > 0 && contractStartDateTime.compareTo(endTime) < 0) {
//                    start = contractStartDate;
//                }
//                // 合同结束时间 晚于 统计开始时间 && 合同结束时间 早于 统计结束时间
//                if (contractEndDateTime.compareTo(startTime) > 0 && contractEndDateTime.compareTo(endTime) < 0) {
//                    end = contractEndDate;
//                }
//
//
//                for (HireRoomOfDevice device : deviceList) {
//                    String last = queryInfluxdbVal(String.format(timeLastSql, device.getDevice(), start, end));
//                    String first = queryInfluxdbVal(String.format(timeFirstSql, device.getDevice(), start, end));
//                    BigDecimal scale = new BigDecimal(last).subtract(new BigDecimal(first)).setScale(2, RoundingMode.HALF_UP);
//                    yTotal = yTotal.add(scale);
//                }
//                yData.add(yTotal.toString());
//            }
//        }
//        jsonObject.put("xData",xData);
//        //现在数据
//        jsonObject.put("yData",yData);
//
//
//        // 获取当前年月，格式为 "yyyy-MM"
//        String currentMonth = YearMonth.now().toString(); // 例如：2025-11
//        // 查找索引
//        int index = xData.indexOf(currentMonth);
//
//            String yValue = yData.get(index);
//            System.out.println("本月 (" + currentMonth + ") 对应的值是: " + yValue);
//
//        jsonObject.put("toDay",new BigDecimal(yValue).setScale(2, ROUND_HALF_UP).toString());
//
//
//
//
//
//        return jsonObject;
//
//    }


    //组装用电量评价sql
    public String getEnergyEvaluate(String time1, String time2, String id) {

        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND time" +
                " <'" + time1 + "' " +
                "AND " +
                "time >'" + time2 + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }


    public String getEnergyEvaluate2(String time1, String time2, String id) {

        String sql = "select  last(val)-first(val) as spread from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND time" +
                " <'" + time1 + "' " +
                "AND " +
                "time >'" + time2 + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }


    //组装用电量评价sql
    public String getEnergyEvaluateTotal(String id) {

        String sql = "select  last(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY  + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }

    //组装用电量评价sql
    public String getWaterTotal(String id) {

        String sql = "select  last(val)  from datasheet WHERE channelId='" + G.CHANNELID_WATER  + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }

    //组装用电量评价sql
    public String getEnergyEvaluateWater(String time1, String time2, String id) {

        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_WATER + "'  AND time" +
                " <'" + time1 + "' " +
                "AND " +
                "time >'" + time2 + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }


    public JSONObject getSubitemEnergy2(DeviceQueryVo bean) {



        //选中类型的时间
        String startDate = "" ;
        String endDate = "" ;
        LinkedHashMap<String,String> thisXData = new LinkedHashMap<>() ;
        List<String> strings = null ;
        if (bean.getTimeType().equals("3")) {
            // 年度
            strings =DateUtils.getMonList(bean.getTimeStart(),bean.getTimeEnd());
            for(String s:strings){
                thisXData.put(s,"0.00") ;
            }
        }
        if (bean.getTimeType().equals("2")) {
            // 月度

            strings =DateUtils.getDayList2(bean.getTimeStart(),bean.getTimeEnd());
//            for(String s:strings){
//                thisXData.put(s,"0.00") ;
//            }
        }
        if (bean.getTimeType().equals("1")) {
            // 天

            thisXData=DateUtils.getTimeHours(bean.getTimeStart(),bean.getTimeEnd());
        }

        if (bean.getTimeType().equals("4")) {
            // 周


        }

//
//        bean.setTimeStart(startDate);
//        bean.setTimeEnd(endDate);

        // 随机取个楼宇的id
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));

        // 获取采集设备
        DataSheetVo bean2 = new DataSheetVo() ;
        if(null != bean.getBuildIds()){
            bean2.setBuildIds(bean.getBuildIds());
        }
        if(null != bean.getSubitemId()){
            bean2.setSubitem(bean.getSubitemId());
        }
        if(null != bean.getFloorIds()){
            bean2.setFloorIds(bean.getFloorIds());
        }

        if(null != bean.getAreaIds()){
            bean2.setAreaIds(bean.getAreaIds());
        }

        //选中楼宇  统计楼宇的电表   不选则统计楼层电表总和
        if(null == bean.getBuildIds() && null == bean.getFloorIds() && null == bean.getAreaIds()){
            List<Integer> integers = new ArrayList<>() ;
            integers.add(121);
            bean.setBuildIds(integers) ;
        }


        List<DeviceCollect> deviceCollects =new ArrayList<>() ;
        List<String> ids = new ArrayList<>();
        if(null != bean.getBuildIds() && null == bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            //楼宇总电表
            List<String> collectIds = new ArrayList<>();

            // 电
            ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
            configDeviceType2.setDeviceTypeCode("E_METER");
            List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalFloorId(0);//大楼总电表
            deviceCollects = deviceCollectService.getListByFloorId(collectVo);
            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
        }else {
            //统计楼层电表 或者区域总空开设备
            bean2.setIsTotalDevice(1);
            deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            //没有总设备的  统计所有不是总设备的
            if(CollectionUtils.isEmpty(deviceCollects)){
                bean2.setIsTotalDevice(0);
                deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            }

            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
        }

        String channelId="";
        String influxVal="";
        switch (bean.getEnergyType()){
            case "1":
                channelId="ENERGY_TOTAL";
                influxVal="last(val)-first(val)";
                break;
            case "2":
                channelId="WATER_L";
                influxVal="spread(val)";
                break;
            default:
                channelId="GAS_METERING";
                influxVal="spread(val)";
                break;

        }

        String sql = "" ;
        Map<String, Double> result = new LinkedHashMap<>();
        if (bean.getTimeType().equals("1")) {
            sql = getInfluxSql(influxVal, bean.getTimeStart(), bean.getTimeEnd(), "time(1h)", channelId, ids);
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            for (Map<String, Object> map : maps) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }

        }else if(bean.getTimeType().equals("2") || bean.getTimeType().equals("4")){
            sql = getInfluxSql(influxVal, bean.getTimeStart(), bean.getTimeEnd(), "time(1d)", channelId, ids);

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            for (Map<String, Object> map : maps) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }
        }else if(bean.getTimeType().equals("3")){
            for (int i = 0; i < strings.size(); i++) {

                String nextDay = DateUtils.nextDay(strings.get(i)) ;
                sql = getInfluxSql(influxVal, strings.get(i)+"-01", nextDay, null, channelId, ids);

                // 时序数据库解析
                QueryResult query = influxdbService.query(sql);

                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0.00 ;
                if(!CollectionUtils.isEmpty(maps)){
                    for (Map<String, Object> map : maps) {
                        sum += (Double)map.get("totalPower") ;
                    }
                }
                result.put(strings.get(i), sum);



            }


        }

//        =DateUtils.getTimeHours(bean.getTimeStart(),bean.getTimeEnd());

        if (bean.getTimeType().equals("1")) {
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 19).replaceAll("T"," "), String.format("%.2f", item.getValue()));
            }
        }else if(bean.getTimeType().equals("2")|| bean.getTimeType().equals("4")){
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
        }else if(bean.getTimeType().equals("3")){
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey(), String.format("%.2f", item.getValue()));
            }
        }



        JSONObject jsonObject=new JSONObject();


        jsonObject.put("xData",thisXData.keySet());


        jsonObject.put("yData",thisXData.values());
        return jsonObject;

    }

    public String getInfluxSql(String val, String startTime, String endTime, String groupStr, String channelId, List<String> ids) {
        String idstr = influxdbInToString(ids);
        StringBuilder sql = new StringBuilder();
        sql.append("select " + val + " as totalPower from datasheet ");
        sql.append("WHERE deviceCollectId=~/" + idstr + "/ ");
        sql.append("AND time>'" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");


        if (null != groupStr) {
            sql.append(" GROUP BY " + groupStr + ",deviceCollectId ");
        } else {
            sql.append(" GROUP BY deviceCollectId ");
        }

        if (val.indexOf("DIFFERENCE") >= 0) {
            sql.append("fill(previous) ");
        } else {
            sql.append("fill(0) ");
        }

        sql.append("ORDER BY time ");


        return sql.toString();
    }


    //组装多选sql条件
    public String influxdbInToString(List<String> ids) {

        ids = ids.stream().map(x -> "^" + x + "$").collect(Collectors.toList());


        String list = StringUtils.join(ids, "|");
        return list;
    }

    public String spreadResultProcess(List<Map<String, Object>> list) {
        String spread = "0.00";
        if (list != null && list.size() != 0) {
            Map<String, Object> map = list.get(0);
            double num = (Double) map.get("spread");
            spread = new BigDecimal(num).setScale(2, ROUND_HALF_UP).toString();
        }

        return spread;
    }



    public String spreadResultProcess2(List<Map<String, Object>> list) {
        String spread = "0.00";
        if (list != null && list.size() != 0) {
            Map<String, Object> map = list.get(0);
            double num = (Double) map.get("last");
            spread = new BigDecimal(num).setScale(2, ROUND_HALF_UP).toString();
        }

        return spread;
    }


    public Map deviceByType(String type){
        Map<String,String> map=new HashMap<>();
        LambdaQueryWrapper<DeviceCollect> wrapper= new LambdaQueryWrapper<DeviceCollect>();
        wrapper .eq(DeviceCollect::getIsDel , 0).eq(DeviceCollect::getDeviceType, type);
        Long all =  deviceCollectMapper.selectCount(wrapper);
        wrapper.clear();
        wrapper.eq(DeviceCollect::getIsDel , 0).eq(DeviceCollect::getDeviceType, type).eq(DeviceCollect::getOnLineStatus,1);
        Long online = deviceCollectMapper.selectCount(wrapper);
        wrapper.clear();
        wrapper.eq(DeviceCollect::getIsDel , 0).eq(DeviceCollect::getDeviceType, type).eq(DeviceCollect::getOnLineStatus,2);
        Long out = deviceCollectMapper.selectCount(wrapper);
        int warn = deviceCollectMapper.deviceByType(type).size();
        map.put("all",all+"");
        map.put("online",online+"");
        map.put("out",out+"");
        map.put("warn",warn+"");
        return map;
    }

    public Long getDeviceCount(DeviceCollectVo vo) {
        //设备在线状态（1正常、2离线、3异常）
        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getOnLineStatus())) {
                queryWrapper.eq("onLineStatus", vo.getOnLineStatus());
            }
            if (StringUtils.isNotNull(vo.getDeviceType())) {
                queryWrapper.eq("deviceType", vo.getDeviceType());
            }
        }
        return deviceCollectMapper.selectCount(queryWrapper);
    }





    /**
     * 照明
     * @author wzn
     * @date 2024/6/29 11:01
     */
    public Long getChintCloudDevicePointCount(DeviceCollectVo vo) {
        //设备在线状态（1正常、2离线、3异常）
        QueryWrapper<ChintCloudDevicePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("isMaster", 0);

        return chintCloudDevicePointMapper.selectCount(queryWrapper);
    }


    /**
    *驾驶舱-实时负荷
    * Author wzn
    * Date 2024/4/25 13:45
    */
    public JSONObject getLoad(){
        JSONObject jsonObject = new JSONObject() ;
        List<String> xData = getTime() ;

       Collections.reverse(xData);
//        Collections.sort(xData, (s1, s2) -> s2.compareTo(s1));
        jsonObject.put("xData",xData) ;

        String nowDate = DateUtil.format(new Date(),"yyyy-MM-dd") ;
        List<String> yData = new ArrayList<>() ;
        List<String> yData2 = new ArrayList<>() ;

        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("isDel","0") ;
        queryWrapper.eq("deviceType","10005") ;
        queryWrapper.eq("isTotalDevice","1") ;
        queryWrapper.eq("logicalFloorId","0") ;
        List<DeviceCollect> deviceCollectList = deviceCollectMapper.selectList(queryWrapper) ;


        if(deviceCollectList.size() == 1){
            for(String s:xData){
                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
                String sql = dataSheetService.getLoadSql(nowDate+" "+":00",nowDate+" "+nowDate.substring(0, 2)+":59:59",deviceCollectList.get(0).getId()+"");
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0d;
                for (Map<String, Object> item : maps) {
                    sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }
                yData.add(sum+"") ;
                yData2.add("0.0") ;
            }
        }else if(deviceCollectList.size() == 2){
            for(String s:xData){
                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
                String sql = dataSheetService.getLoadSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0,2)+":59:59",deviceCollectList.get(0).getId()+"");
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0d;
                for (Map<String, Object> item : maps) {
                    sum =  (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                    yData.add(sum+"") ;
                }

            }


            for(String s:xData){
                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
                String sql = dataSheetService.getLoadSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0,2)+":59:59",deviceCollectList.get(1).getId()+"");
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0d;
                for (Map<String, Object> item : maps) {
                    sum =  (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                    yData2.add(sum+"") ;
                }

            }
        }
        jsonObject.put("yData",yData) ;
        jsonObject.put("yData2",yData2) ;
        return  jsonObject ;
    }

    public List<String> getTime(){
        LocalDateTime now = LocalDateTime.now();
        List<String> formattedHours = new ArrayList<>();

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");

        for (int i = 0; i <= now.getHour(); i++) {
            LocalDateTime hourStart = now.minusHours(i).withMinute(0).withSecond(0).withNano(0);
            String formattedHour = hourStart.format(formatter);
            formattedHours.add(formattedHour);
        }

        // 打印结果
        formattedHours.forEach(System.out::println);
        return formattedHours ;
    }

    public List<String> getTime2(){
        LocalDateTime now = LocalDateTime.now();
        List<String> formattedHours = new ArrayList<>();

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");

        for (int i = 0; i <= now.getHour(); i++) {
            LocalDateTime hourStart = now.minusHours(i).withMinute(0).withSecond(0).withNano(0);
            String formattedHour = hourStart.format(formatter);
            formattedHours.add(formattedHour);
        }

        // 打印结果
        formattedHours.forEach(System.out::println);
        return formattedHours ;
    }


    /**
     * 驾驶舱
     * 空气质量
     * 趋势图
     */
    public JSONObject aqdReport(EnergyReportVo vo){
        JSONObject jsonObject = new JSONObject() ;
        List<String> xData = getTime() ;
        Collections.reverse(xData);
        jsonObject.put("xData",xData) ;

        String nowDate = DateUtil.format(new Date(),"yyyy-MM-dd") ;
        List<String> yData = new ArrayList<>() ;
        List<String> yDataOut = new ArrayList<>() ;

        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        configDeviceType.setDeviceTypeCode(vo.getDeviceTypeCode());
        List<ConfigDeviceType> deviceTypeList = configDeviceTypeService.getList(configDeviceType);
        int deviceType = 0;
        if (deviceTypeList.size() > 0) {
            deviceType = deviceTypeList.get(0).getId();
        }

        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("isDel","0") ;
//        queryWrapper.eq("deviceType", deviceType);
        queryWrapper.eq("deviceCode", "11FBKCBGQPM25");
//        queryWrapper.eq("isTotalDevice","1") ;
//        queryWrapper.eq("logicalFloorId","0") ;
        List<DeviceCollect> deviceCollectList = deviceCollectMapper.selectList(queryWrapper) ;
        List<Integer> deviceCollectIds = deviceCollectList.stream().map(p -> p.getId()).collect(Collectors.toList());

        //室外
        QueryWrapper<DeviceCollect> queryWrapper2 = new QueryWrapper<>() ;
        queryWrapper2.eq("isDel","0") ;
        queryWrapper2.eq("deviceCode", "17FSWPM25");
        DeviceCollect deviceCollect = deviceCollectMapper.selectOne(queryWrapper2) ;
        List<Integer> deviceCollectIds2= new ArrayList<>();
        deviceCollectIds2.add(deviceCollect.getId());

//        if(deviceCollectList.size() == 1){
            for(String s:xData){
                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
                String sql = dataSheetService.getAqdSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0, 2)+":59:59",deviceCollectIds,vo.getChannelId());
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0d;
                for (Map<String, Object> item : maps) {
                    sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }
                yData.add(sum+"") ;

                //室外
                String sql2 = dataSheetService.getAqdSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0, 2)+":59:59",deviceCollectIds2,vo.getChannelId());
                QueryResult query2 = influxdbService.query(sql2);
                //解析列表类查询
                List<Map<String, Object>> maps2 = influxdbService.queryResultProcess(query2);
                Double sum2 = 0d;
                for (Map<String, Object> item : maps2) {
                    sum2 = sum2 + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }
                yDataOut.add(sum2+"") ;
            }
//        }
        jsonObject.put("yData",yData) ;
        jsonObject.put("yDataOut",yDataOut) ;
        return  jsonObject ;
    }


    public JSONObject electricityTotal(){
        JSONObject map = new JSONObject() ;
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        Double toDay1 = 0.00;
        Double lastDay1 = 0.00;

        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;

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

            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));

            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);

            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));

            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));

        }

        map.put("toDay", new BigDecimal(toDay1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastDay", new BigDecimal(lastDay1).setScale(2, RoundingMode.HALF_UP).toString());

        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, RoundingMode.HALF_UP).toString());



        Double radioDay=(0==lastDay1)?toDay1:((toDay1 - lastDay1) / lastDay1) * 100;
        if(radioDay > 0.0){
            map.put("radioDay",new BigDecimal(radioDay).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioDay == 0.0){
            map.put("radioDay",0);
        }else {
            map.put("radioDay",new BigDecimal(radioDay*-1).setScale(2, ROUND_HALF_UP).toString());
        }

        Double radioMon=(0==lastMonth1)?toDay1:((toMonth1 - lastMonth1) / lastMonth1) * 100;
        if(radioMon > 0.0){
            map.put("radioMon",new BigDecimal(radioMon).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioDay == 0.0){
            map.put("radioMon",0);
        }else {
            map.put("radioMon",new BigDecimal(radioMon*-1).setScale(2, ROUND_HALF_UP).toString());
        }


        //总碳排放量（吨）=用电量(度)*0.785/1000
        double toc = toMonth1 * 0.785 / 1000;
        map.put("toc",new BigDecimal(toc).setScale(2, ROUND_HALF_UP).toString()) ;

        //当量值：总标煤（tce）=总用电量（万度）*1.229
        double tce = toMonth1 / 10000 * 1.229;
        map.put("tce",new BigDecimal(tce).setScale(2, ROUND_HALF_UP).toString()) ;


        return map;
    }

    /**
     * 实时水压监测
     */
    public JSONObject getLoad2(EnergyReportVo vo){
        JSONObject jsonObject = new JSONObject() ;
        List<String> xData = getTime() ;
        Collections.reverse(xData);
        jsonObject.put("xData",xData) ;

        String nowDate = DateUtil.format(new Date(),"yyyy-MM-dd") ;
        List<String> yData = new ArrayList<>() ;
        List<String> yData2 = new ArrayList<>() ;

        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        configDeviceType.setDeviceTypeCode(vo.getDeviceTypeCode());
        List<ConfigDeviceType> deviceTypeList = configDeviceTypeService.getList(configDeviceType);
        int deviceType = 0;
        if (deviceTypeList.size() > 0) {
            deviceType = deviceTypeList.get(0).getId();
        }

        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("isDel","0") ;
        queryWrapper.eq("deviceType",deviceType) ;
        queryWrapper.eq("isTotalDevice","1") ;
//        queryWrapper.eq("logicalFloorId","0") ;
        List<DeviceCollect> deviceCollectList = deviceCollectMapper.selectList(queryWrapper) ;

        List<Integer> deviceCollectIds = deviceCollectList.stream().map(p -> p.getId()).collect(Collectors.toList());

        if(deviceCollectList.size() == 1){
            for(String s:xData){
                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
                String sql = dataSheetService.getAqdSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0, 2)+":59:59",deviceCollectIds,"WATER_P");
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0d;
                for (Map<String, Object> item : maps) {
                    sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }
                yData.add(sum+"") ;
            }
        }else if(deviceCollectList.size() > 1){
            for(String s:xData){
                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
                String sql = dataSheetService.getAqdSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0, 2)+":59:59",deviceCollectIds,"WATER_P");
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                Double sum = 0d;
                for (Map<String, Object> item : maps) {
                    sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }
                yData.add(sum+"") ;
            }


//            for(String s:xData){
//                //08:00  2023-09-01 00:00:00  2023-09-01 00:59:59
//                String sql = dataSheetService.getAqdSql(nowDate+" "+s+":00",nowDate+" "+s.substring(0, 2)+":59:59",deviceCollectIds,"WATER_P");
//                QueryResult query = influxdbService.query(sql);
//                //解析列表类查询
//                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
//                Double sum = 0d;
//                for (Map<String, Object> item : maps) {
//                    sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
//                }
//                yData2.add(sum+"") ;
//            }
        }
        jsonObject.put("yData",yData) ;
        jsonObject.put("yData2",yData2) ;
        return  jsonObject ;
    }

    /**
     * 用水量统计
     * 当日累计用水 、同比
     * 当月累计用水、同比
     */
    public JSONObject waterTotal(){
        JSONObject map = new JSONObject() ;
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        Double toDay1 = 0.00;
        Double lastDay1 = 0.00;

        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;

        ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
        configDeviceType2.setDeviceTypeCode("W_METER");
        List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);

        wrapper.eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0)
//                .isNotNull(DeviceCollect::getLogicalBuildId)
//                .eq(DeviceCollect::getLogicalFloorId, 0)
                .eq(DeviceCollect::getLogicalAreaId, 0)
                .eq(DeviceCollect::getDeviceType, deviceTypeList2.get(0).getId());
//        wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collectList = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collectList) {
            //今天
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN),
                    deviceCollect.getId() + ""));
            //去年今天
            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluateWater(new DateTime().plusYears(-1).plusDays(1).toString("yyyy-MM-dd"),
                            new DateTime().plusYears(-1).toString("yyyy-MM-dd"),
                            deviceCollect.getId() + ""));

            //本月
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            new DateTime().toString("yyyy-MM-01"),
                            deviceCollect.getId() + ""));

            //去年同月
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(new DateTime().plusYears(-1).plusMonths(1).toString("yyyy-MM-01"),
                            new DateTime().plusYears(-1).toString("yyyy-MM-01"),
                             deviceCollect.getId() + ""));

            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);

            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));

            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));

        }

        map.put("toDay", new BigDecimal(toDay1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastDay", new BigDecimal(lastDay1).setScale(2, RoundingMode.HALF_UP).toString());

        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, RoundingMode.HALF_UP).toString());



        Double radioDay=(0==lastDay1)?toDay1:((toDay1 - lastDay1) / lastDay1) * 100;
        if(radioDay > 0.0){
            map.put("radioDay",new BigDecimal(radioDay).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioDay == 0.0){
            map.put("radioDay",0);
        }else {
            map.put("radioDay",new BigDecimal(radioDay*-1).setScale(2, ROUND_HALF_UP).toString());
        }

        Double radioMon=(0==lastMonth1)?toMonth1:((toMonth1 - lastMonth1) / lastMonth1) * 100;
        if(radioMon > 0.0){
            map.put("radioMon",new BigDecimal(radioMon).setScale(2, ROUND_HALF_UP).toString());
        }else if(radioDay == 0.0){
            map.put("radioMon",0);
        }else {
            map.put("radioMon",new BigDecimal(radioMon*-1).setScale(2, ROUND_HALF_UP).toString());
        }
        return map;
    }

    /**
     * 分项用水占比
     * 去年同月用水量、今年当月用水量
     * 大楼总用水量 今年
     */
    public EnergyReportVo waterTotalReport(){
        JSONObject map = new JSONObject() ;
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();

        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;

        Double toYear = 0.00;

        ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
        configDeviceType2.setDeviceTypeCode("W_METER");
        List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);

        wrapper.eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0)
//                .isNotNull(DeviceCollect::getLogicalBuildId)
//                .eq(DeviceCollect::getLogicalFloorId, 0)
                .eq(DeviceCollect::getLogicalAreaId, 0)
                .eq(DeviceCollect::getDeviceType, deviceTypeList2.get(0).getId());
//        wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collectList = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collectList) {
            //今年同月
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            new DateTime().toString("yyyy-MM-01")
                            , deviceCollect.getId() + ""));
            //去年同月
            QueryResult lastYearMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(new DateTime().plusYears(-1).plusMonths(1).toString("yyyy-MM-01"),
                            new DateTime().plusYears(-1).toString("yyyy-MM-01")
                            , deviceCollect.getId() + ""));

            //今年
            QueryResult thisYear =
                    influxdbService.query(this.getEnergyEvaluateWater(new DateTime().toString("yyyy-MM-dd HH:mm:ss"),
                            new DateTime().toString("yyyy-01-01"), deviceCollect.getId() + ""));

            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastYearMonth);
            List<Map<String, Object>> thisYearList = influxdbService.queryResultProcess(thisYear);

            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            toYear += Double.parseDouble(this.spreadResultProcess(thisYearList));

        }

//        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
//        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, RoundingMode.HALF_UP).toString());

        EnergyReportVo dataVo = new EnergyReportVo();
        dataVo.setWaterMonth(new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
        dataVo.setLastYearWaterMonth(new BigDecimal(lastMonth1).setScale(2, RoundingMode.HALF_UP).toString());

        //大楼总用水量
        dataVo.setWaterYear(new BigDecimal(toYear).setScale(2, RoundingMode.HALF_UP).toString());
        return dataVo;
    }

    /**
     * 用水日月年统计
     */
    public EnergyReportVo waterTotalTimeReport(){
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();

        Double toDay1 = 0.00;

        Double toMonth1 = 0.00;

        Double toYear = 0.00;

        ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
        configDeviceType2.setDeviceTypeCode("W_METER");
        List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);

        wrapper.eq(DeviceCollect::getIsTotalDevice, 1)
                .eq(DeviceCollect::getIsDel, 0)
//                .isNotNull(DeviceCollect::getLogicalBuildId)
//                .eq(DeviceCollect::getLogicalFloorId, 0)
                .eq(DeviceCollect::getLogicalAreaId, 0)
                .eq(DeviceCollect::getDeviceType, deviceTypeList2.get(0).getId());
//        wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collectList = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collectList) {
            //今天
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN),
                    deviceCollect.getId() + ""));

            //本月
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluateWater(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            new DateTime().toString("yyyy-MM-01"),
                            deviceCollect.getId() + ""));

            //今年
            QueryResult thisYear =
                    influxdbService.query(this.getEnergyEvaluateWater(new DateTime().toString("yyyy-MM-dd HH:mm:ss"),
                            new DateTime().toString("yyyy-01-01"), deviceCollect.getId() + ""));

            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> thisYearList = influxdbService.queryResultProcess(thisYear);
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);

            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            toYear += Double.parseDouble(this.spreadResultProcess(thisYearList));

        }

//        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
//        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, RoundingMode.HALF_UP).toString());

        EnergyReportVo dataVo = new EnergyReportVo();
        dataVo.setWaterDay(new BigDecimal(toDay1).setScale(2, RoundingMode.HALF_UP).toString());
        dataVo.setWaterMonth(new BigDecimal(toMonth1).setScale(2, RoundingMode.HALF_UP).toString());
        //大楼总用水量
        dataVo.setWaterYear(new BigDecimal(toYear).setScale(2, RoundingMode.HALF_UP).toString());
        return dataVo;
    }

    /**
     * 下载模板
     *
     * @param response
     */
    public ResultInfo downloadTemplate(HttpServletResponse response) {
        //导出excel模板
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("设备名称", StringUtils.EMPTY);
            row.put("设备类型", StringUtils.EMPTY);
            row.put("设备编码", StringUtils.EMPTY);
            row.put("设备状态", StringUtils.EMPTY);
            row.put("所属分项", StringUtils.EMPTY);
            row.put("所属网关", StringUtils.EMPTY);
            row.put("物理位置", StringUtils.EMPTY);
            row.put("逻辑位置",StringUtils.EMPTY);
            row.put("安装位置",StringUtils.EMPTY);
            row.put("是否总设备", StringUtils.EMPTY);
            row.put("安装日期", StringUtils.EMPTY);
            row.put("使用日期", StringUtils.EMPTY);
            row.put("设备管理员", StringUtils.EMPTY);
            row.put("管理员电话", StringUtils.EMPTY);
            row.put("供应商名称", StringUtils.EMPTY);
            row.put("设备型号", StringUtils.EMPTY);
            row.put("维保公司", StringUtils.EMPTY);
            row.put("年检日期", StringUtils.EMPTY);
            row.put("采购日期", StringUtils.EMPTY);
            row.put("过保日期", StringUtils.EMPTY);
            row.put("负荷", StringUtils.EMPTY);
            row.put("设备描述", StringUtils.EMPTY);
            list.add(row);
        }

        ExcelWriter writer = ExcelUtil.getWriter();
        StyleSet styleSet = writer.getStyleSet();
        Sheet sheet = writer.getSheet();
        //设置下拉数据 从第几行开始
        int firstRow = 1;

        // 设置只导出有别名的字段
        writer.setOnlyAlias(true);
        // 设置默认行高
        writer.setDefaultRowHeight(20);
        // 设置冻结行
        writer.setFreezePane(1);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);

        // 需要必填的字段名称
        List<String> requiredFields = Arrays.asList("设备名称", "设备类型", "设备编码", "设备状态", "所属分项", "所属网关", "物理位置", "逻辑位置", "是否总设备", "供应商名称", "设备型号", "负荷");
        // 需要下拉的字段名称
        List<String> dictFields = Arrays.asList("设备类型", "设备状态", "所属分项", "所属网关", "物理位置", "逻辑位置", "是否总设备", "设备管理员", "供应商名称", "设备型号", "维保公司");
        // 需要组装的 List<List<?>> rows
        List<List<?>> rows = new ArrayList<>();
        // 需要下拉的列
        List<Integer> firstCols = new ArrayList<>();
        // 得到map
        Map<String, Object> demo = list.get(0);
        // 获取 Map 的键集合
        Object[] keys = demo.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];

            ExportDataUtils.writeCell(writer, i, key, requiredFields.contains(key));

            if (dictFields.contains(key)) {
                List<String> colList = getColList(key);
                rows.add(colList);
                firstCols.add(i);
            }
        }

        // firstCols  转换为数组
        Integer[] firstCol = firstCols.toArray(new Integer[0]);

        ExportDataUtils.writeData(rows, writer, firstCol);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("采集终端模板.xls", "UTF-8"));
            writer.flush(outputStream, true);
            outputStream.flush();
            outputStream.close();
            return ResultInfo.success(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> getColList(String type) {
        List<String> list = new ArrayList<>();
        List<SysBuildArea> areas = sysBuildAreaMapper.selectList(Wrappers.<SysBuildArea>lambdaQuery().eq(SysBuildArea::getIsDel, G.ISDEL_NO));

        switch (type) {
            case "设备类型":
                List<ConfigDeviceType> configDeviceTypes = configDeviceTypeMapper.selectList(Wrappers.<ConfigDeviceType>lambdaQuery().eq(ConfigDeviceType::getIsDel, G.ISDEL_NO));
                list = configDeviceTypes.stream().distinct().map(ConfigDeviceType::getDeviceTypeName).collect(Collectors.toList());
                break;
            case "设备状态":
                List<SysDict> dicts = sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getIsDel, G.ISDEL_NO).eq(SysDict::getDictType, "deviceStatus"));
                list = dicts.stream().distinct().map(SysDict::getDictLabel).collect(Collectors.toList());
                break;
            case "所属分项":
                List<ConfigDeviceSubitem> deviceSubitems = configDeviceSubitemMapper.selectList(Wrappers.<ConfigDeviceSubitem>lambdaQuery().eq(ConfigDeviceSubitem::getIsDel, G.ISDEL_NO));
                list = deviceSubitems.stream().distinct().map(ConfigDeviceSubitem::getSubitemName).collect(Collectors.toList());
                break;
            case "所属网关":
                List<DeviceGateway> gateways = deviceGatewayMapper.selectList(Wrappers.<DeviceGateway>lambdaQuery().eq(DeviceGateway::getIsDel, G.ISDEL_NO));
                list = gateways.stream().distinct().map(DeviceGateway::getName).collect(Collectors.toList());
                break;
            case "物理位置":
                list = areas.stream().distinct().map(SysBuildArea::getAreaName).collect(Collectors.toList());
                break;
            case "逻辑位置":
                list = areas.stream().distinct().map(SysBuildArea::getAreaName).collect(Collectors.toList());
                break;
            case "是否总设备":
                list.add("是");
                list.add("否");
                break;
            case "设备管理员":
                List<SysUser> users = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, G.ISDEL_NO));
                list = users.stream().distinct().map(SysUser::getRealName).collect(Collectors.toList());
                break;
            case "供应商名称":
                List<ConfigSupplier> suppliers = configSupplierMapper.selectList(Wrappers.<ConfigSupplier>lambdaQuery().eq(ConfigSupplier::getIsDel, G.ISDEL_NO));
                list = suppliers.stream().distinct().map(ConfigSupplier::getSupplierName).collect(Collectors.toList());
                break;
            case "设备型号":
                List<ConfigModel> models = configModelMapper.selectList(Wrappers.<ConfigModel>lambdaQuery().eq(ConfigModel::getIsDel, G.ISDEL_NO));
                list = models.stream().distinct().map(ConfigModel::getModelName).collect(Collectors.toList());
                break;
            case "维保公司":
                List<SysDict> dicts1 = sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getIsDel, G.ISDEL_NO).eq(SysDict::getDictType, "supplierType"));
                list = dicts1.stream().distinct().map(SysDict::getDictLabel).collect(Collectors.toList());
                break;
        }
        return list;
    }


    /**
     * 创建处理数据
     *
     * @param files
     * @return
     */
    public ResultInfo importUploadData(List<MultipartFile> files) throws Exception {
        boolean falg = false;
        String url = null;
        for (MultipartFile file : files) {
            //判断文件是否大于200M
            falg = FileUtils.checkFileSize(file.getSize(), 200, "M");
            if (!falg) {
                return ResultInfo.error("文件过大，最大200M");
            }
            url = UploadUtils.uploadUUIDFile(file, FilePath);
        }

        List<DeviceCollect> list = new ArrayList<>();
        if (StringUtils.isEmpty(url)) {
            return ResultInfo.error("上传失败");
        } else {
            File file = new File(url);
            if (!file.exists()) {
                throw new Exception("文件不存在!");
            }
            ExcelReader reader = ExcelUtil.getReader(file);
            List<Map<String, Object>> readAll = reader.readAll();
            if (CollectionUtils.isEmpty(readAll)) {
                throw new RuntimeException("导入的数据为空");
            }
            // 得到字典匹配的集合
            Map<String, List<?>> dictsMap = getDictsMap();

            // 需要必填的字段名称
            List<String> requiredFields = Arrays.asList("设备名称", "设备类型", "设备编码", "设备状态", "所属分项", "所属网关", "物理位置", "逻辑位置", "是否总设备", "供应商名称", "设备型号", "负荷");
            // 需要下拉的字段名称
            List<String> dictFields = Arrays.asList("设备类型", "设备状态", "所属分项", "所属网关", "物理位置", "逻辑位置", "是否总设备", "设备管理员", "供应商名称", "设备型号", "维保公司");

            // 设置errorList，加入校验失败的行号和原因
            List<Map<String, Object>> errorList = new ArrayList<>();
            for (Map<String, Object> map : readAll) {
                // map的所有value 去除前后空格
                map.forEach((k, v) -> map.put(k, null == v ? "" : v.toString().trim()));
                // 行号
                int rowNum = readAll.indexOf(map) + 2;

                DeviceCollect deviceCollect = new DeviceCollect();
                // 获取 Map 的键集合
                for (String key : map.keySet()) {
                    if (StringUtils.isBlank(key)) {
                        continue;
                    }
                    String value = "";
                    // 判断是否验证必填字段
                    if (requiredFields.contains(key)) {
                        if (StringUtils.isBlank(MapUtils.getString(map, key))) {
                            throw new RuntimeException("第" + rowNum + "行," + key + "不能为空");
                        }
                    }
                    value = MapUtils.getString(map, key);
                    // 验证类型字段是否可以在数据库中找得到
                    if (StringUtils.isNotBlank(value) && dictFields.contains(key)) {
                        try {
                            value = getImportVerifySelect(key, value, dictsMap.get(key));
                        } catch (RuntimeException e) {
                            throw new RuntimeException("第" + rowNum + "行," + key + "不存在");
                        }
                    }
                    getImportDeviceCollectData(key, value, deviceCollect);
                }
                // 逻辑位置和物理位置相同
                deviceCollect.setLogicalAreaId(deviceCollect.getAreaId());
                list.add(deviceCollect);
            }
            for (DeviceCollect vo : list) {
                String msg = "";
                if (StringUtils.isNotNull(vo.getId())) {
                    vo.setUpdateTime(new Date());
                    vo.setUpdateUser(sysUserService.getUser().getId());
                    if (StringUtils.isNotEmpty(vo.getDeviceCode()) && StringUtils.isNotNull(vo.getGatewayId())) {
                        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
                        wrapper.eq(DeviceCollect::getDeviceCode, vo.getDeviceCode())
                                .eq(DeviceCollect::getGatewayId, vo.getGatewayId())
                                .eq(DeviceCollect::getIsDel, 0)
                                .notIn(DeviceCollect::getId, vo.getId());
                        List<DeviceCollect> list1 = deviceCollectMapper.selectList(wrapper);
                        if (list1.size() > 0) {
                            return ResultInfo.error("该网关下已存在该设备编码");
                        }
                    }

                    msg = "修改";
                } else {
                    vo.setCreateTime(new Date());
                    vo.setCreateUser(sysUserService.getUser().getId());
                    vo.setIsDel(0);
                    if (StringUtils.isNotEmpty(vo.getDeviceCode()) && StringUtils.isNotNull(vo.getGatewayId())) {
                        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper();
                        wrapper.eq(DeviceCollect::getDeviceCode, vo.getDeviceCode())
                                .eq(DeviceCollect::getGatewayId, vo.getGatewayId())
                                .eq(DeviceCollect::getIsDel, 0);
                        List<DeviceCollect> list2 = deviceCollectMapper.selectList(wrapper);
                        if (list2.size() > 0) {
                            return ResultInfo.error("该网关下已存在该设备编码");
                        }
                    }
                    msg = "新增";
                    //设备在线状态（1正常、2离线、3异常）
                    vo.setOnLineStatus("1");
                }
                saveOrUpdate(vo);
                controlService.editByDevice(vo);
            }
            //当采集设备修改时更新redis中的信息
            memoryCacheService.changeDeviceCollectVo();
        }
        return ResultInfo.success(list);
    }

    private Map<String, List<?>> getDictsMap() {
        Map<String, List<?>> map = new HashMap<>();
        map.put("设备类型", configDeviceTypeMapper.selectList(Wrappers.<ConfigDeviceType>lambdaQuery().eq(ConfigDeviceType::getIsDel, G.ISDEL_NO)));
        map.put("设备状态", sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getIsDel, G.ISDEL_NO).eq(SysDict::getDictType, "deviceStatus")));
        map.put("所属分项", configDeviceSubitemMapper.selectList(Wrappers.<ConfigDeviceSubitem>lambdaQuery().eq(ConfigDeviceSubitem::getIsDel, G.ISDEL_NO)));
        map.put("所属网关", deviceGatewayMapper.selectList(Wrappers.<DeviceGateway>lambdaQuery().eq(DeviceGateway::getIsDel, G.ISDEL_NO)));
        map.put("物理位置", sysBuildAreaMapper.selectList(Wrappers.<SysBuildArea>lambdaQuery().eq(SysBuildArea::getIsDel, G.ISDEL_NO)));
        map.put("逻辑位置", sysBuildAreaMapper.selectList(Wrappers.<SysBuildArea>lambdaQuery().eq(SysBuildArea::getIsDel, G.ISDEL_NO)));
        map.put("是否总设备", Arrays.asList("否", "是"));
        map.put("设备管理员", sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, G.ISDEL_NO)));
        map.put("供应商名称", configSupplierMapper.selectList(Wrappers.<ConfigSupplier>lambdaQuery().eq(ConfigSupplier::getIsDel, G.ISDEL_NO)));
        map.put("设备型号", configModelMapper.selectList(Wrappers.<ConfigModel>lambdaQuery().eq(ConfigModel::getIsDel, G.ISDEL_NO)));
        map.put("维保公司", sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getIsDel, G.ISDEL_NO).eq(SysDict::getDictType, "supplierType")));
        return map;
    }

    /**
     * 验证数据库是否匹配
     *
     * @param type
     * @return
     * @throws RuntimeException
     */
    private String getImportVerifySelect(String type, String value, List<?> list) throws RuntimeException {
        String successMsg = "";
        // 传输协议 configProtocolId
        switch (type) {
            case "设备类型":
                successMsg = ExportDataUtils.getListValue(list, "deviceTypeName", value, "id");
                break;
            case "设备状态":
                successMsg = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
            case "所属分项":
                successMsg = ExportDataUtils.getListValue(list, "subitemName", value, "id");
                break;
            case "所属网关":
                successMsg = ExportDataUtils.getListValue(list, "name", value, "id");
                break;
            case "物理位置":
                successMsg = ExportDataUtils.getListValue(list, "areaName", value, "id");
                break;
            case "逻辑位置":
                successMsg = ExportDataUtils.getListValue(list, "areaName", value, "id");
                break;
            case "是否总设备":
                //是否总设备 0否 1是
                if (StringUtils.equals("否", value)) {
                    successMsg = "0";
                } else {
                    successMsg = "1";
                }
                break;
            case "设备管理员":
                successMsg = ExportDataUtils.getListValue(list, "realName", value, "id");
                break;
            case "供应商名称":
                successMsg = ExportDataUtils.getListValue(list, "supplierName", value, "id");
                break;
            case "设备型号":
                successMsg = ExportDataUtils.getListValue(list, "modelName", value, "id");
                break;
            case "维保公司":
                successMsg = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
        }
        return successMsg;
    }

    private DeviceCollect getImportDeviceCollectData(String type, String value, DeviceCollect deviceCollect) {
        if (StringUtils.isBlank(type)) {
            System.out.println("type为空");
            return deviceCollect;
        }
        switch (type) {
            case "设备名称":
                deviceCollect.setName(value);
                break;
            //设备类型
            case "设备类型":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setDeviceType(Integer.parseInt(value));
                }
                break;
            //设备编码
            case "设备编码":
                deviceCollect.setDeviceCode(value);
                break;
            //设备状态
            case "设备状态":
                deviceCollect.setStatus(value);
                break;
            //所属分项
            case "所属分项":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setSubitem(Integer.parseInt(value));
                }
                break;
            //所属网关
            case "所属网关":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setGatewayId(Integer.parseInt(value));
                }
                break;
            //物理位置
            case "物理位置":
                if (StringUtils.isNotBlank(value)) {
                    Integer areaId = Integer.parseInt(value);
                    deviceCollect.setAreaId(areaId);
                    Integer floorId;
                    Integer buildId;
                    //用区域查楼层 物理位置
                    SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(areaId);
                    if (StringUtils.isNotNull(sysBuildArea)) {
                        //查楼宇
                        SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                        if (StringUtils.isNotNull(sysBuildFloor)) {
                            deviceCollect.setFloorId(sysBuildFloor.getId());
                            SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                            deviceCollect.setBuildId(sysBuild.getId());
                        }
                    }
                }
                break;
            //逻辑位置
            case "逻辑位置":
                if (StringUtils.isNotBlank(value)) {
                    Integer areaId = Integer.parseInt(value);
                    deviceCollect.setLogicalAreaId(areaId);
                    Integer floorId;
                    Integer buildId;
                    //用区域查楼层 物理位置
                    SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(areaId);
                    if (StringUtils.isNotNull(sysBuildArea)) {
                        //查楼宇
                        SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                        if (StringUtils.isNotNull(sysBuildFloor)) {
                            deviceCollect.setLogicalFloorId(sysBuildFloor.getId());
                            SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                            deviceCollect.setLogicalBuildId(sysBuild.getId());
                        }
                    }
                }
                break;
            //安装位置
            case "安装位置":
                deviceCollect.setPlace(value);
                break;
            case "是否总设备":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setIsTotalDevice(Integer.parseInt(value));
                }
                break;
            //安装日期
            case "安装日期":
                if (StringUtils.isNotBlank(value)) {
                    try {
                        deviceCollect.setInstallationDate(DateUtil.parse(value));
                    } catch (Exception e) {
                        deviceCollect.setInstallationDate(new Date());
                    }
                }
                break;
            //使用日期
            case "使用日期":
                if (StringUtils.isNotBlank(value)) {
                    try {
                        deviceCollect.setUseTime(DateUtil.parse(value));
                    } catch (Exception e) {
                        deviceCollect.setUseTime(new Date());
                    }
                }
                break;
            //设备管理员
            case "设备管理员":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setUserId(Integer.parseInt(value));
                }
                break;
            //管理员电话
            case "管理员电话":
                deviceCollect.setPhone(value);
                break;
            //供应商名称
            case "供应商名称":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setSupplierId(Integer.parseInt(value));
                }
                break;
            //设备型号
            case "设备型号":
                if (StringUtils.isNotBlank(value)) {
                    deviceCollect.setModelNum(Integer.parseInt(value));
                }
                break;
            //维保公司
            case "维保公司":
                deviceCollect.setCompanyId(value);
                break;
            //年检日期
            case "年检日期":
                if (StringUtils.isNotBlank(value)) {
                    try {
                        deviceCollect.setInspectionDate(DateUtil.parse(value));
                    } catch (Exception e) {
                        deviceCollect.setInspectionDate(new Date());
                    }
                }
                break;
            //采购日期
            case "采购日期":
                if (StringUtils.isNotBlank(value)) {
                    try {
                        deviceCollect.setProcureDate(DateUtil.parse(value));
                    } catch (Exception e) {
                        deviceCollect.setProcureDate(new Date());
                    }
                }
                break;
            //过保日期
            case "过保日期":
                if (StringUtils.isNotBlank(value)) {
                    try {
                        deviceCollect.setExpirationDate(DateUtil.parse(value));
                    } catch (Exception e) {
                        deviceCollect.setExpirationDate(new Date());
                    }
                }
                break;
            //负荷
            case "负荷":
                deviceCollect.setLoad(value);
                break;
            //设备描述
            case "设备描述":
                deviceCollect.setDeviceDesc(value);
                break;
        }
        return deviceCollect;
    }


    public JSONObject energyConsumptionApp(){

        JSONObject result=new JSONObject();
        DataSheetVo bean = new DataSheetVo();
        // 用电
        bean.setChannelId("ENERGY_TOTAL");
        bean.setGroupStr("time(1d)");
        bean.setInfluxVal("MAX(val)-MIN(val)");
        bean.setEnergyType(1);
        bean.setTimeType(3);


        JSONObject jsonObject=dataSheetService.getChannelMOM(bean);
        // 本月用电
        List<String> thisValues=new ArrayList<String>((Collection<String>) jsonObject.get("thisTime"));
        Double thisTimeElectricity=thisValues.stream().mapToDouble(i->Double.parseDouble(i)).sum();
        result.put("thisTimeElectricity",String.format("%.2f",thisTimeElectricity));

        //总碳排放量（吨）=用电量(度)*0.785/1000

        Double thisTimeCarbon = thisTimeElectricity*0.000785 ;
        // 本月碳排放
        result.put("thisTimeCarbon",String.format("%.2f",thisTimeElectricity*0.000785));

        result.put("tec",String.format("%.2f",thisTimeElectricity/10000*1.229));

        return result ;

    }

    /**
     *
     * @param vo
     * @return
     */
    public List<DeviceCollect> getListDeviceCollect(DeviceCollect vo) {
        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
        if(null != vo.getIsDel()){
            queryWrapper.eq("isDel", vo.getIsDel());
        }

        if(null != vo.getDeviceType()){
            queryWrapper.eq("deviceType", vo.getDeviceType());
        }

        return deviceCollectMapper.selectList(queryWrapper);
    }

    public  Page<Map<String, Object>> airLog(SysLogVo vo){
        if (StringUtils.isNotEmpty(vo.getStartTime())){
            vo.setStartTime(vo.getStartTime()+" 00:00:00");
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())){
            vo.setEndTime(vo.getEndTime()+" 23:59:59");
        }
        Page<Map<String, Object>> page=new Page();
        page.setSize(vo.getPageSize());
        List<Map<String, Object>> list = dataSheetService.airLog(vo);
        vo.setPageSize(0);
        List<Map<String, Object>> list1 = dataSheetService.airLog(vo);
        for (Map<String, Object> map : list) {
            map.put("time",map.get("time").toString().replace('T',' '));
            map.put("time",map.get("time").toString().replace('Z',' '));
        }
        page.setRecords(list);
        page.setTotal(list1.size());
        page.setCurrent(vo.getPageNo());
        return page;
    }




    public  Page<Map<String, Object>> air17Log(SysLogVo vo){
        if (StringUtils.isNotEmpty(vo.getStartTime())){
            vo.setStartTime(vo.getStartTime()+" 00:00:00");
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())){
            vo.setEndTime(vo.getEndTime()+" 23:59:59");
        }
        Page<Map<String, Object>> page=new Page();
        page.setSize(vo.getPageSize());
        vo.setLogType(LogEnums.LOG_ACAIR_17_CONTROLS.getValue());
        List<Map<String, Object>> list = dataSheetService.airLog(vo);
        vo.setPageSize(0);
        List<Map<String, Object>> list1 = dataSheetService.airLog(vo);
        for (Map<String, Object> map : list) {
            map.put("time",map.get("time").toString().replace('T',' '));
            map.put("time",map.get("time").toString().replace('Z',' '));
        }
        page.setRecords(list);
        page.setTotal(list1.size());
        page.setCurrent(vo.getPageNo());
        return page;
    }

    public ResultInfo getEnetgyAll(String id){
        return ResultInfo.success(dataSheetService.getEnetgyAll(id));
    }




    public List<Map<String, Object>> highValueElectricalEquipment() {

        DeviceCollectVo vo = new DeviceCollectVo() ;
        vo.setDeviceType(200084);
        vo.setLogicalAreaId(0);
        vo.setLogicalFloorId(0);
        vo.setLogicalBuildId(0);
        List<DeviceCollectVo> collectVoList = deviceCollectService.getList(vo) ;


        DeviceCollectVo vo2 = new DeviceCollectVo() ;
        vo2.setDeviceType(10005);
        vo2.setLogicalAreaId(0);
        vo2.setLogicalFloorId(0);
        vo2.setLogicalBuildId(0);
        vo2.setSubitem(62);
        List<DeviceCollectVo> collectVoList2 = deviceCollectService.getList(vo2) ;

        // 合并 list2 到 list1 中
        collectVoList.addAll(collectVoList2);


        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 计算7天前的日期
        LocalDate sevenDaysAgo = today.minusDays(6);

        // 创建日期格式器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 格式化日期
        String formattedSevenDaysAgo = sevenDaysAgo.format(formatter);
        String formattedToday = today.format(formatter);



        List<String> deviceIds = collectVoList.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String sql = "select deviceCollectId,val from (select spread(val) as val  from datasheet where channelId='ENERGY_TOTAL'and time >'"+DateUtil.format(new Date(), "yyyy-MM-dd")+" 00:00:00"+"' and time<'"+DateUtil.format(new Date(), "yyyy-MM-dd")+ " 23:59:59"+"'  and deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ group by deviceCollectId)";
        List<Map<String, Object>> mapList = new ArrayList<>();
            QueryResult queryResult = influxdbService.query(sql);
            List<QueryResult.Result> resultList = queryResult.getResults();
            JSONObject json = new JSONObject();
            for (QueryResult.Result query : resultList) {
                List<QueryResult.Series> seriesList = query.getSeries();
                if (seriesList == null || seriesList.size() == 0) continue;
                for (QueryResult.Series series : seriesList) {
                    Map<String, String> tags = series.getTags();
                    List<String> columns = series.getColumns();
                    String[] keys = columns.toArray(new String[columns.size()]);
                    List<List<Object>> values = series.getValues();


                    if (values != null && values.size() != 0) {
                        for (List<Object> value : values) {
                            Map<String, Object> map = new HashMap(keys.length);
                            for (int j = 0; j < keys.length; j++) {
                                if ("deviceCollectId".equals(keys[j])) {

                                    map.put("deviceCollectId",value.get(j)) ;
                                    //查设备名称
                                    Integer id = Integer.parseInt(value.get(j)+"") ;
                                    DeviceCollect deviceCollect = deviceCollectMapper.selectById(id) ;
                                    if(null != deviceCollect){
                                        map.put("deviceCollectName",deviceCollect.getName()) ;
                                    }

                                }else if ("val".equals(keys[j])) {
                                    map.put("val",String.format("%.2f", ((Number) value.get(j)).doubleValue())) ;
                                }

                            }
                            mapList.add(map) ;
                        }
                    }
                }
            }
            //排序取前5
        // 按照"value"键的值降序排序，并取前5个
        // 按照"val"键的值降序排序
        // 排序并取前5个
        List<Map<String, Object>> top5 = mapList.stream()
                .sorted((mapA, mapB) -> ((Comparable) Double.parseDouble(mapB.get("val")+"")).compareTo(Double.parseDouble(mapA.get("val")+"")))
                .limit(10)
                .collect(Collectors.toList());

        // 打印结果

            return  top5 ;
    }




    public List<DeviceCollect> energyUseAnomaly(DeviceCollect vo) {
        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
        if(null != vo.getIsDel()){
            queryWrapper.eq("isDel", vo.getIsDel());
        }

        if(null != vo.getDeviceType()){
            queryWrapper.eq("deviceType", vo.getDeviceType());
        }

        return deviceCollectMapper.selectList(queryWrapper);
    }

    public List<DeviceCollect> electricityMeterData(DeviceCollect vo) {
        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        //设备类型 = 智能电表
        queryWrapper.eq("deviceType", 10005);
        //设备分项 != 暖通空调用电
        queryWrapper.ne("subitem", 62);
        // 设备状态 1未使用;  2使用中;  3损坏;  4维修中
        queryWrapper.eq("status", "2");
        return deviceCollectMapper.selectList(queryWrapper);
    }

    /**
     * 1、所有的4P空开
     */
    public List<DeviceCollect> airSwitchData(DeviceCollect vo) {
        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        //设备类型 = 智能空开(4P)
        queryWrapper.eq("deviceType", 200084);
        // 设备状态 1未使用;  2使用中;  3损坏;  4维修中
        queryWrapper.eq("status", "2");
        return deviceCollectMapper.selectList(queryWrapper);
    }




    /**
     *
     *能耗预测
     * @author wzn
     * @date 2024/12/18 08:57
     */
    public ResultInfo energyForecast(DeviceQueryVo bean) {




        LinkedHashMap<String,String> thisXData = new LinkedHashMap<>() ;
        LinkedHashMap<String,String> yesterdayXData = new LinkedHashMap<>() ;
        // private String timeType;//1:日 2：月 3：年 4 周
        //选中类型的时间
        String startDate = "" ;
        String endDate = "" ;
        String yesStartDate = "" ;
        String yesEndDate = "" ;


        List<String> strings = null ;

        if (bean.getTimeType().equals("2")) {
            // 月度
            // 定义输入日期格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

            // 解析输入字符串为YearMonth对象selectData
            YearMonth yearMonth = YearMonth.parse(bean.getSelectData(), inputFormatter);

            // 获取该月的第一天
            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            // 定义输出日期格式
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
             startDate = firstDayOfMonth.format(outputFormatter);

            // 获取该月的最后一天
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
             endDate = lastDayOfMonth.format(outputFormatter);



// 获取当前月份
            YearMonth currentYearMonth = YearMonth.now();

            // 获取上个月份
            YearMonth lastYearMonth = currentYearMonth.minusMonths(1);

            // 获取上个月的第一天和最后一天
            LocalDate firstDayOfLastMonth = lastYearMonth.atDay(1);
            LocalDate lastDayOfLastMonth = lastYearMonth.atEndOfMonth();

            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 格式化日期
             yesStartDate = firstDayOfLastMonth.format(formatter);
             yesEndDate = lastDayOfLastMonth.format(formatter);


        }
        if (bean.getTimeType().equals("1")) {
            // 天
            startDate =bean.getSelectData() +" 00:00:00" ;
            endDate  =bean.getSelectData() +" 23:59:59" ;
            thisXData=DateUtils.getTimeHours(startDate,endDate);


            // 获取昨天的日期
            LocalDate yesterday = LocalDate.now().minusDays(7);

            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 格式化昨天的日期
            String formattedYesterday = yesterday.format(formatter);

            yesStartDate = formattedYesterday+" 00:00:00" ;
            yesEndDate = formattedYesterday+" 23:59:59" ;
            yesterdayXData=DateUtils.getTimeHours(yesStartDate,yesEndDate);

        }

        JSONObject jsonObject=new JSONObject();


        // 获取设备
//        List<DeviceCollectVo> deviceCollects=deviceCollectMapper.getSubitemEnergy(bean);
        // 随机取个楼宇的id
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));

        // 获取采集设备
        DataSheetVo bean2 = new DataSheetVo() ;
        if(null != bean.getBuildIds()){
            bean2.setBuildIds(bean.getBuildIds());
        }

        if(null != bean.getSubitemId()){
            bean2.setSubitem(bean.getSubitemId());
        }

        if(null != bean.getFloorIds()){
            bean2.setFloorIds(bean.getFloorIds());
        }

        if(null != bean.getAreaIds()){
            bean2.setAreaIds(bean.getAreaIds());
        }

        //不选默认查楼宇
        if(null == bean.getBuildIds() && null == bean.getFloorIds() && null == bean.getAreaIds()){
            List<Integer> integers = new ArrayList<>() ;
            integers.add(121);
            bean.setBuildIds(integers) ;
        }

        //选中楼宇  统计楼宇的电表   不选则统计楼层电表总和
        List<DeviceCollect> deviceCollects =new ArrayList<>() ;
        List<String> ids = new ArrayList<>();
        List<String> ids2 = new ArrayList<>();
        List<String> ids3 = new ArrayList<>();
        if(null != bean.getBuildIds() && null == bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            //楼宇总电表
            List<String> collectIds = new ArrayList<>();

            // 电
            ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
            configDeviceType2.setDeviceTypeCode("E_METER");
            List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalFloorId(0);//大楼总电表
            deviceCollects = deviceCollectService.getListByFloorId(collectVo);
            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
        }else {
            //统计楼层电表 或者区域总空开设备
            bean2.setIsTotalDevice(1);
            deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            //没有总设备的  统计所有不是总设备的（包括了虚拟设备 ）
            if(CollectionUtils.isEmpty(deviceCollects)){
                bean2.setIsTotalDevice(0);
                deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            } else {
                //有总设备-选区域时-加上区域里面的虚拟设备 格力
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200124);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids2 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


                //有总设备-选区域时-加上区域里面的虚拟设备 大金
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200166);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids3 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


            }



            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(ids2)){
                //并集
                ids = Stream.concat(ids.stream(), ids2.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }

            if(!CollectionUtils.isEmpty(ids3)){
                //并集
                ids = Stream.concat(ids.stream(), ids3.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
        }


        if(CollectionUtils.isEmpty(deviceCollects)){
           return ResultInfo.error("该区域无设备！") ;

        }



        String channelId="";
        String influxVal="";
        switch (bean.getEnergyType()){
            case "1":
                channelId="ENERGY_TOTAL";
                influxVal="last(val)-first(val)";
                break;
            case "2":
                channelId="WATER_L";
                influxVal="last(val)-first(val)";
                break;
            default:
                channelId="GAS_METERING";
                influxVal="spread(val)";
                break;

        }
        String sql = "" ;
        Map<String, Double> result = new LinkedHashMap<>();
        Map<String, Double> result2 = new LinkedHashMap<>();
        Map<String, Double> yesResult = new LinkedHashMap<>();

        if (bean.getTimeType().equals("1")) {


            sql = getInfluxSql(influxVal, startDate, endDate, "time(1h)", channelId, ids);
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            for (Map<String, Object> map : maps) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }

            sql = getInfluxSql(influxVal, yesStartDate, yesEndDate, "time(1h)", channelId, ids);
            // 时序数据库解析
            QueryResult query3 = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);
            for (Map<String, Object> map : maps3) {
                if (yesResult.containsKey(map.get("time"))) {
                    yesResult.put((String) map.get("time"), yesResult.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    yesResult.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }



            List<String> list = Arrays.stream(bean.getOldYears().split(","))
                    .collect(Collectors.toList());
            for(int i= 0 ;i<list.size();i++){
                List<String> doubleList = new ArrayList<>() ;
                Map<String, Double> timeMap = new LinkedHashMap<>();
                startDate =list.get(i)+ startDate.substring(4, startDate.length()) ;
                endDate = list.get(i)+ endDate.substring(4, endDate.length()) ;

                sql = getInfluxSql(influxVal, startDate, endDate, "time(1h)", channelId, ids);

                QueryResult query2 = influxdbService.query(sql);
                List<Map<String, Object>> maps2 = influxdbService.queryResultProcess(query2);


                if(!CollectionUtils.isEmpty(maps2)){
                    for (int j = 0; j < maps2.size();j++) {

                        if (timeMap.containsKey(maps2.get(j).get("time"))) {
                            timeMap.put((String) maps2.get(j).get("time"), timeMap.get(maps2.get(j).get("time")) + (maps2.get(j).get("totalPower") == null ? 0d : (Double) maps2.get(j).get("totalPower")));
                        } else {
                            timeMap.put((String) maps2.get(j).get("time"), maps2.get(j).get("totalPower") == null ? 0d : (Double) maps2.get(j).get("totalPower"));
                        }
                    }
                    jsonObject.put(list.get(i),timeMap.values().stream().collect(Collectors.toList()));
                }else {
                    List<Double> doubles = new ArrayList<>() ;
                    for(int h = 0 ; h<thisXData.keySet().size();h++){
                        doubles.add(0.00) ;

                    }
                    jsonObject.put(list.get(i),doubles);
                }



            }



        }else if(bean.getTimeType().equals("2") || bean.getTimeType().equals("4")){
            sql = getInfluxSql(influxVal, startDate, endDate, "time(1d)", channelId, ids);

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            for (Map<String, Object> map : maps) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }

            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }


            sql = getInfluxSql(influxVal, yesStartDate, yesEndDate, "time(1d)", channelId, ids);
            // 时序数据库解析
            QueryResult query3 = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);
            for (Map<String, Object> map : maps3) {
                yesterdayXData.put((String) map.get("time"),String.valueOf(map.get("totalPower"))) ;
            }

            List<String> list = Arrays.stream(bean.getOldYears().split(","))
                    .collect(Collectors.toList());
            for(int i= 0 ;i<list.size();i++){
                List<String> doubleList = new ArrayList<>() ;
                Map<String, Double> timeMap = new LinkedHashMap<>();

                startDate =list.get(i)+ startDate.substring(4, startDate.length()) ;
                endDate = list.get(i)+ endDate.substring(4, endDate.length()) ;

                sql = getInfluxSql(influxVal, startDate, endDate, "time(1d)", channelId, ids);

                QueryResult query2 = influxdbService.query(sql);
                List<Map<String, Object>> maps2 = influxdbService.queryResultProcess(query2);

                if(!CollectionUtils.isEmpty(maps2)){
                    for (int j = 0; j < maps2.size();j++) {


                        if (timeMap.containsKey(maps2.get(j).get("time"))) {
                                timeMap.put((String) maps2.get(j).get("time"), timeMap.get(maps2.get(j).get("time")) + (maps2.get(j).get("totalPower") == null ? 0d : (Double) maps2.get(j).get("totalPower")));

                        } else {
                            timeMap.put((String) maps2.get(j).get("time"), maps2.get(j).get("totalPower") == null ? 0d : (Double) maps2.get(j).get("totalPower"));
                        }
//                    Double val2 = (Double) maps2.get(j).get("totalPower");
//                    doubleList.add(String.format("%.2f", val2)) ;
                    }
                    jsonObject.put(list.get(i),timeMap.values().stream().collect(Collectors.toList()));
                }else {
                    List<Double> doubles = new ArrayList<>() ;
                    for(int h = 0 ; h<thisXData.keySet().size();h++){
                        doubles.add(0.00) ;

                    }
                    jsonObject.put(list.get(i),doubles);
                }


            }
        }


        if (bean.getTimeType().equals("1")) {
            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 19).replaceAll("T"," "), String.format("%.2f", item.getValue()));
            }

            for (Map.Entry<String, Double> item : yesResult.entrySet()) {
                yesterdayXData.put(item.getKey().substring(0, 19).replaceAll("T"," "), String.format("%.2f", item.getValue()));
            }
        }



        //---------预测率计算
        Integer size = 0 ;
        List<Double> forecast = new ArrayList<>() ;

        //预测原始数据
        List<String> topTenValues = yesterdayXData.values().stream()
                .collect(Collectors.toList());
        String key = "" ;

        if(!CollectionUtils.isEmpty(bean.getFloorIds())){
            key += bean.getFloorIds().get(0) ;
        }
        if(!CollectionUtils.isEmpty(bean.getAreaIds())){
            key += bean.getAreaIds().get(0) ;
        }
        key+= bean.getEnergyType()+bean.getTimeType() ;

        if(null != bean.getSubitemId()){
            key+=bean.getSubitemId() ;
        }
        if (bean.getTimeType().equals("1")) {
            size = Integer.valueOf(DateUtil.format(new Date(), "HH")) ;

            startDate = new DateTime().toString("yyyy-MM-dd 00:00:00") ;



            //浮动之后的预测数据
//            redisUtils.deleteKey(key);
            forecast  = (List<Double>) redisUtils.getBeanValue(key);
            if(CollectionUtils.isEmpty(forecast)){
                // 创建一个 Random 实例用于生成随机数
                Random random = new Random();

                // 将字符串列表转换为 Double 列表，并应用 -5% 到 +5% 的随机波动
                List<Double> fluctuatedNumbers = new ArrayList<>();
                for (String numberStr : topTenValues) {
                    double number = Double.parseDouble(numberStr);
                    double fluctuation = 1 + (random.nextDouble() * 0.1 - 0.05); // 生成 [-0.05, 0.05] 的随机值，然后加1变成 [0.95, 1.05]
                    double fluctuatedNumber = number * fluctuation;
                    fluctuatedNumbers.add(Double.valueOf(String.format("%.2f", fluctuatedNumber)));
                }
                redisUtils.setBeanValue(key, (Object) fluctuatedNumbers,1440) ;
                forecast  = (List<Double>) redisUtils.getBeanValue(key);
            }else {
                forecast = (List<Double>) redisUtils.getBeanValue(key);
            }



        }else if(bean.getTimeType().equals("2")){
            size = Integer.valueOf(DateUtil.format(new Date(), "dd")) ;

            // 获取当前时间的LocalDateTime对象
            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            // 获取当前月份的第一天，时间为00:00:00
            LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 定义日期时间格式
            // 格式化当前月份的第一天为字符串
            startDate = firstDayOfMonth.format(formatter);






            //浮动之后的预测数据
//            redisUtils.deleteKey(key);
            forecast  = (List<Double>) redisUtils.getBeanValue(key);
            if(CollectionUtils.isEmpty(forecast)){
                // 创建一个 Random 实例用于生成随机数
                Random random = new Random();

                // 将字符串列表转换为 Double 列表，并应用 -5% 到 +5% 的随机波动
                List<Double> fluctuatedNumbers = new ArrayList<>();
                for (String numberStr : topTenValues) {
                    double number = Double.parseDouble(numberStr);
                    double fluctuation = 1 + (random.nextDouble() * 0.1 - 0.05); // 生成 [-0.05, 0.05] 的随机值，然后加1变成 [0.95, 1.05]
                    double fluctuatedNumber = number * fluctuation;
                    fluctuatedNumbers.add(Double.valueOf(String.format("%.2f", fluctuatedNumber)));
                }
                redisUtils.setBeanValue(key, (Object) fluctuatedNumbers,1440) ;
                forecast  = (List<Double>) redisUtils.getBeanValue(key);
            }else {
                forecast = (List<Double>) redisUtils.getBeanValue(key);
            }
        }




        //预测数据截取
        List<Double> topTenValueslimit =forecast.stream()
                .limit(size)
                .collect(Collectors.toList());


        // 将字符串列表转换为 Double 流，尝试解析每个字符串并求和
        OptionalDouble sum = topTenValueslimit.stream()
                .mapToDouble(Double::doubleValue)  // 转换为原始 double 类型以便使用 sum() 方法
                .reduce(Double::sum);  // 对流中的所有元素进行求和

        // 如果成功计算了总和，则输出结果；否则处理异常情况
        Double historicalData = sum.getAsDouble() ;





        //昨天这时候的总用电/用水数据
        String idstr = influxdbInToString(ids);

        Date nowDate = new Date();

        sql = "select sum(totalPower) as totalPower from (select last(val)-first(val) as totalPower from datasheet WHERE deviceCollectId=~/"+idstr+"/ AND time>='"+startDate+"' AND time<='"+DateUtil.format(nowDate, "yyyy-MM-dd HH:mm:ss")+"' AND channelId='"+channelId+"'  GROUP BY deviceCollectId fill(0) ) ";


        QueryResult query2 = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps2 = influxdbService.queryResultProcess(query2);
        Double nowData = 0.00;
        for (int i = 0; i < maps2.size(); i++) {
            nowData = (Double) maps2.get(i).get("totalPower");
        }

        double successRate = 0.00 ;
        // 计算公式: 1 - |预测值 / 实际值|
        if(0.00 !=nowData){
            successRate =   calculateSuccessRate(historicalData, nowData);
        }

        if(successRate <0){
            successRate = 0.00 ;
        }


        jsonObject.put("xData",thisXData.keySet());
        //现在数据
        jsonObject.put("yData",thisXData.values());


        jsonObject.put("forecast",forecast);
        jsonObject.put("rate",String.format("%.2f", successRate*100));



        return ResultInfo.success(jsonObject);

    }


    public static double calculateSuccessRate(double predictedValue, double actualValue) {
        return 1 - Math.abs((predictedValue - actualValue) / actualValue);
    }

    /**
     * 能耗预测
     * 空调列表
     */
    public Page<DeviceCollectVo> getAirConditioningList(Integer onLineStatus, Integer areaId, Integer floorId,
                                                  Integer buildId, String name,
                                                  Integer type, Integer index, Integer size, String areaName, Integer deviceType,
                                                  String areaIds, String buildIds, String floorIds, String id,List<Integer> deviceTypes,Integer isAir) {
        Page<DeviceCollectVo> page = new Page<>(index, size);
        Page<DeviceCollectVo> pageInfo = deviceCollectMapper.deviceMonitoring(page, onLineStatus, areaId, floorId,
                buildId, name, type, areaName, deviceType, areaIds, buildIds, floorIds, id,deviceTypes);
        return pageInfo;
    }


    public Object aiEnergy2(DeviceQueryVo bean) {
        //趋势数据
        JSONObject jsonObject = new JSONObject() ;
        if("1".equals(bean.getType())){
            List<Map<String,Double>> doubleList = new ArrayList<>() ;
            List<String> dateList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 获取当前日期
            LocalDate currentDate = LocalDate.now();

            for (int i = 0; i < 7; i++) {
                // 格式化日期并添加到列表
                String formattedDate = currentDate.minusDays(i).format(formatter);
                dateList.add(formattedDate);
            }

            // 打印结果检查
            for (String date : dateList) {
                bean.setTimeStart(date+" 00:00:00");
                bean.setTimeEnd(date+" 23:59:59");
                Double value = aiEnergy(bean) ;
                Map<String,Double> map  = new HashMap<>() ;
                map.put(date,value) ;
                doubleList.add(map) ;
            }
            return doubleList;
        }else {
            Double value = aiEnergy(bean) ;
            return value;
        }

    }


    public void dataEnergy() {
        DeviceQueryVo bean = new DeviceQueryVo();
        bean.setEnergyType("1") ;
        List<Map<String, Double>> doubleList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        for (int i = 310; i > 0; i--) {
            // 格式化日期并添加到列表
            String formattedDate = currentDate.minusDays(i).format(formatter);
            dateList.add(formattedDate);
        }

//        for (int i =0; i <2 ; i++) {
//            // 格式化日期并添加到列表
//            String formattedDate = currentDate.minusDays(i).format(formatter);
//            dateList.add(formattedDate);
//        }

        // 打印结果检查
        for (String date : dateList) {
            bean.setTimeStart(date + " 00:00:00");
            bean.setTimeEnd(date + " 23:59:59");

            //插楼宇数据
            Double value = insertData(bean);
            DataEnergy dataEnergy = new DataEnergy() ;
            dataEnergy.setType("1");
            dataEnergy.setBuildId("121");
            dataEnergy.setValue(value+"");
            dataEnergy.setDateStr(date);
            dataEnergy.insert() ;

            //插楼层数据
            List<FloorAiInfo> floorAiInfoList = sysBuildService.aiBulidTreeList() ;
            for(FloorAiInfo floorAiInfo : floorAiInfoList){
                DeviceQueryVo bean2 = new DeviceQueryVo();
                bean2.setEnergyType("1") ;
                bean2.setTimeStart(date + " 00:00:00");
                bean2.setTimeEnd(date + " 23:59:59");
                bean2.setFloorId(Integer.valueOf(floorAiInfo.getId()));
                Double valueFloor = insertData(bean2);

                DataEnergy dataEnergy2 = new DataEnergy() ;
                dataEnergy2.setType("2");
                dataEnergy2.setFloorId(floorAiInfo.getId());
                dataEnergy2.setBuildId("121");
                dataEnergy2.setValue(valueFloor+"");
                dataEnergy2.setDateStr(date);
                dataEnergy2.insert() ;
                System.out.println(date+floorAiInfo.getName()+"楼层数据:"+valueFloor);




                //区域是否有总空开设备 有 只查总设备所在区域的电量  没有总设备 所有子设备相加
                // 获取采集设备
                DataSheetVo bean3= new DataSheetVo() ;
                ArrayList<Integer> arrayList = new ArrayList<>() ;
                arrayList.add(Integer.valueOf(floorAiInfo.getId())) ;
                bean3.setFloorIds(arrayList);
                bean3.setEnergyType(1);


                bean3.setIsTotalDevice(1);
                List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean3);

                //没有总开  所有区域累加
                if(CollectionUtils.isEmpty(deviceCollects)){
                    List<FloorAiInfo> areaList = sysBuildService.aiAreareeList(floorAiInfo.getId()) ;
                    //查所有分项
                    List<FloorAiInfo> subList =  sysBuildService.aiSubitemIdList2("1") ;
                    for(FloorAiInfo area : areaList){


                        DeviceQueryVo bean4 = new DeviceQueryVo();
                        bean4.setTimeStart(date + " 00:00:00");
                        bean4.setTimeEnd(date + " 23:59:59");
                        bean4.setEnergyType("1") ;
                        bean4.setAreaId(Integer.valueOf(area.getId()));
                        Double valueArea = insertData(bean4);

                        DataEnergy dataEnergy3 = new DataEnergy() ;
                        dataEnergy3.setType("3");
                        dataEnergy3.setAreaId(area.getId());

                        //查所在楼层
                        SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(area.getId()) ;
                        if(null != sysBuildArea){
                            dataEnergy3.setFloorId(sysBuildArea.getFloorId()+"");
                            dataEnergy3.setBuildId("121");

                        }
                        dataEnergy3.setAreaId(area.getId());
                        dataEnergy3.setValue(valueArea+"");
                        dataEnergy3.setDateStr(date);
                        dataEnergy3.insert() ;
                        System.out.println(date+floorAiInfo.getName()+area.getName()+"区域数据:"+valueArea);

                        //插分项数据
                        for(FloorAiInfo sub : subList){

                            DeviceQueryVo bean5= new DeviceQueryVo();
                            bean5.setTimeStart(date + " 00:00:00");
                            bean5.setTimeEnd(date + " 23:59:59");
                            bean5.setEnergyType("1") ;
                            bean5.setAreaId(Integer.valueOf(area.getId()));
                            bean5.setSubitemId(Integer.valueOf(sub.getId()));
                            Double valueSub= insertData(bean5);


                            DataEnergy dataEnergy4 = new DataEnergy() ;
                            dataEnergy4.setType("4");
                            dataEnergy4.setSubitemId(sub.getId());
                            dataEnergy4.setAreaId(area.getId());
                            dataEnergy4.setFloorId(dataEnergy2.getFloorId());
                            dataEnergy4.setBuildId("121");

                            dataEnergy4.setValue(valueSub+"");
                            dataEnergy4.setDateStr(date);
                            dataEnergy4.insert() ;
                            System.out.println(date+floorAiInfo.getName()+area.getName()+sub.getName()+"分项数据:"+valueSub);
                        }
                    }
                }else {
                    //有总开  只插总开所在区域的电量

                    SysBuildArea area = sysBuildAreaMapper.selectById(deviceCollects.get(0).getAreaId()) ;

                    DeviceQueryVo bean4 = new DeviceQueryVo();
                    bean4.setTimeStart(date + " 00:00:00");
                    bean4.setTimeEnd(date + " 23:59:59");
                    bean4.setEnergyType("1") ;
                    bean4.setAreaId(Integer.valueOf(area.getId()));
                    Double valueArea = insertData(bean4);

                    DataEnergy dataEnergy3 = new DataEnergy() ;
                    dataEnergy3.setType("3");
                    dataEnergy3.setAreaId(deviceCollects.get(0).getAreaId()+"");

                    //查所在楼层
                    SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(deviceCollects.get(0).getAreaId()) ;
                    if(null != sysBuildArea){
                        dataEnergy3.setFloorId(sysBuildArea.getFloorId()+"");
                        dataEnergy3.setBuildId("121");

                    }
                    dataEnergy3.setAreaId(deviceCollects.get(0).getAreaId()+"");
                    dataEnergy3.setValue(valueArea+"");
                    dataEnergy3.setDateStr(date);
                    dataEnergy3.insert() ;
                    System.out.println(date+floorAiInfo.getName()+area.getAreaName()+"区域数据:"+valueArea);

                    //插分项数据
                    //查所有分项
                    List<FloorAiInfo> subList =  sysBuildService.aiSubitemIdList2("1") ;
                    for(FloorAiInfo sub : subList){

                        DeviceQueryVo bean5= new DeviceQueryVo();
                        bean5.setTimeStart(date + " 00:00:00");
                        bean5.setTimeEnd(date + " 23:59:59");
                        bean5.setEnergyType("1") ;
                        bean5.setAreaId(Integer.valueOf(area.getId()));
                        bean5.setSubitemId(Integer.valueOf(sub.getId()));
                        Double valueSub= insertData(bean5);


                        DataEnergy dataEnergy4 = new DataEnergy() ;
                        dataEnergy4.setType("4");
                        dataEnergy4.setSubitemId(sub.getId());
                        dataEnergy4.setAreaId(area.getId()+"");
                        dataEnergy4.setFloorId(dataEnergy2.getFloorId());
                        dataEnergy4.setBuildId("121");

                        dataEnergy4.setValue(valueSub+"");
                        dataEnergy4.setDateStr(date);
                        dataEnergy4.insert() ;
                        System.out.println(date+floorAiInfo.getName()+area.getAreaName()+sub.getName()+"分项数据:"+valueSub);
                    }


                }
            }
        }
    }

    public Double insertData(DeviceQueryVo bean) {
        String startDate = "" ;
        String endDate = "" ;
        startDate =bean.getTimeStart();
        endDate = bean.getTimeEnd() ;




        // 获取设备
//        List<DeviceCollectVo> deviceCollects=deviceCollectMapper.getSubitemEnergy(bean);
        // 随机取个楼宇的id
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));

        // 获取采集设备
        DataSheetVo bean2 = new DataSheetVo() ;
        if(null != bean.getBuildIds()){
            bean2.setBuildIds(bean.getBuildIds());
        }

        if(null != bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            bean2.setSubitem(bean.getSubitemId());
        }

        if("1".equals(bean.getEnergyType())){
            if(null != bean.getFloorId()){
                ArrayList<Integer> arrayList = new ArrayList<>() ;
                arrayList.add( bean.getFloorId()) ;
                bean2.setFloorIds(arrayList);
            }
        }else {
            if(null != bean.getFloorId()){
                ArrayList<Integer> arrayList = new ArrayList<>() ;
                arrayList.add( bean.getFloorId()) ;
                bean2.setFloorIds(arrayList);
            }
        }



        if(null != bean.getAreaId()){
            List<Integer> areaIds = new ArrayList<>() ;
            areaIds.add(bean.getAreaId()) ;
            bean2.setAreaIds(areaIds);
            bean2.setFloorIds(null);
        }

        //不选默认查楼宇
        if(null == bean.getBuildIds() && null == bean.getFloorId() && null == bean.getAreaId()){
            List<Integer> integers = new ArrayList<>() ;
            integers.add(121);
            bean.setBuildIds(integers) ;
        }

        //选中楼宇  统计楼宇的电表   不选则统计楼层电表总和
        List<DeviceCollect> deviceCollects =new ArrayList<>() ;
        List<String> ids = new ArrayList<>();
        List<String> ids2 = new ArrayList<>();
        List<String> ids3 = new ArrayList<>();
        if(null != bean.getBuildIds() && null == bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            //楼宇总电表
            List<String> collectIds = new ArrayList<>();

            // 电
            ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
            configDeviceType2.setDeviceTypeCode("E_METER");
            List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalFloorId(0);//大楼总电表
            deviceCollects = deviceCollectService.getListByFloorId(collectVo);
            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
        }else {
            //统计楼层电表 或者区域总空开设备
            bean2.setIsTotalDevice(1);
            deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            //没有总设备的  统计所有不是总设备的（包括了虚拟设备 ）
            if(CollectionUtils.isEmpty(deviceCollects)){
                bean2.setIsTotalDevice(0);
                deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            } else {
                //有总设备-选区域时-加上区域里面的虚拟设备 格力
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200124);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids2 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


                //有总设备-选区域时-加上区域里面的虚拟设备 大金
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200166);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids3 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


            }



            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(ids2)){
                //并集
                ids = Stream.concat(ids.stream(), ids2.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }

            if(!CollectionUtils.isEmpty(ids3)){
                //并集
                ids = Stream.concat(ids.stream(), ids3.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
        }

        Double value = 0.00;
        if("1".equals(bean.getEnergyType())){
            for (String deviceCollect : ids) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluate2(endDate, startDate, deviceCollect+ ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                value += Double.parseDouble(this.spreadResultProcess(toDayList));
            }
        }else {
            for (DeviceCollect deviceCollect : deviceCollects) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(endDate, startDate, deviceCollect.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                value += Double.parseDouble(this.spreadResultProcess(toDayList));
            }
        }

        JSONObject jsonObject = new JSONObject() ;
        double number = value; // 将"你的数值"替换为你实际需要处理的double值
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedNumber = bd.doubleValue();
        return roundedNumber;

    }


    /**
     *
     *ai模块获取电量
     * @author wzn
     * @date 2025/03/08 09:18
     */
    public Double aiEnergy(DeviceQueryVo bean) {
        String startDate = "" ;
        String endDate = "" ;
        startDate =bean.getTimeStart();
        endDate = bean.getTimeEnd() ;




        // 获取设备
//        List<DeviceCollectVo> deviceCollects=deviceCollectMapper.getSubitemEnergy(bean);
        // 随机取个楼宇的id
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));

        // 获取采集设备
        DataSheetVo bean2 = new DataSheetVo() ;
        if(null != bean.getBuildIds()){
            bean2.setBuildIds(bean.getBuildIds());
        }

        if(null != bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            bean2.setSubitem(bean.getSubitemId());
        }

        if("1".equals(bean.getEnergyType())){
            if(null != bean.getFloorId()){
                //查楼层所有区域
                QueryWrapper<SysBuildArea> queryWrapper = new QueryWrapper<>() ;
                queryWrapper.eq("isDel", "0") ;
                queryWrapper.eq("floorId", bean.getFloorId()) ;
                List<SysBuildArea> sysBuildAreas =  sysBuildAreaMapper.selectList(queryWrapper);
                List<Integer> areaIds = new ArrayList<>() ;
                if(!CollectionUtils.isEmpty(sysBuildAreas)){
                    for(SysBuildArea s:sysBuildAreas){
                        areaIds.add(s.getId()) ;
                    }
                    bean2.setAreaIds(areaIds);
                    bean.setAreaIds(areaIds);
                }else {
                    bean2.setFloorIds(null);
                }
            }
        }else {
            if(null != bean.getFloorId()){
                ArrayList<Integer> arrayList = new ArrayList<>() ;
                arrayList.add( bean.getFloorId()) ;
                bean2.setFloorIds(arrayList);
            }
        }



        if(null != bean.getAreaId()){
            List<Integer> areaIds = new ArrayList<>() ;
            areaIds.add(bean.getAreaId()) ;
            bean2.setAreaIds(areaIds);
            bean2.setFloorIds(null);
        }

        //不选默认查楼宇
        if(null == bean.getBuildIds() && null == bean.getFloorId() && null == bean.getAreaId()){
            List<Integer> integers = new ArrayList<>() ;
            integers.add(121);
            bean.setBuildIds(integers) ;
        }

        //选中楼宇  统计楼宇的电表   不选则统计楼层电表总和
        List<DeviceCollect> deviceCollects =new ArrayList<>() ;
        List<String> ids = new ArrayList<>();
        List<String> ids2 = new ArrayList<>();
        List<String> ids3 = new ArrayList<>();
        if(null != bean.getBuildIds() && null == bean.getSubitemId() && "1".equals(bean.getEnergyType())){
            //楼宇总电表
            List<String> collectIds = new ArrayList<>();

            // 电
            ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
            configDeviceType2.setDeviceTypeCode("E_METER");
            List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalFloorId(0);//大楼总电表
            deviceCollects = deviceCollectService.getListByFloorId(collectVo);
            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
        }else {
            //统计楼层电表 或者区域总空开设备
            bean2.setIsTotalDevice(1);
            deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            //没有总设备的  统计所有不是总设备的（包括了虚拟设备 ）
            if(CollectionUtils.isEmpty(deviceCollects)){
                bean2.setIsTotalDevice(0);
                deviceCollects = deviceCollectMapper.getDeviceByArea(bean2);
            } else {
                //有总设备-选区域时-加上区域里面的虚拟设备 格力
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200124);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids2 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


                //有总设备-选区域时-加上区域里面的虚拟设备 大金
                if(null != bean.getAreaIds()){
                    bean2.setDeviceType(200166);
                    bean2.setIsTotalDevice(0);
                    List<DeviceCollect> deviceCollects2 = deviceCollectMapper.getDeviceByArea(bean2);
                    ids3 = deviceCollects2.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
                }


            }



            ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(ids2)){
                //并集
                ids = Stream.concat(ids.stream(), ids2.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }

            if(!CollectionUtils.isEmpty(ids3)){
                //并集
                ids = Stream.concat(ids.stream(), ids3.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
        }

        Double value = 0.00;
        if("1".equals(bean.getEnergyType())){
            for (String deviceCollect : ids) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluate2(endDate, startDate, deviceCollect+ ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                value += Double.parseDouble(this.spreadResultProcess(toDayList));
            }
        }else {
            for (DeviceCollect deviceCollect : deviceCollects) {
                QueryResult toDay = influxdbService.query(this.getEnergyEvaluateWater(endDate, startDate, deviceCollect.getId() + ""));
                List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
                value += Double.parseDouble(this.spreadResultProcess(toDayList));
            }
        }

        JSONObject jsonObject = new JSONObject() ;
        double number = value; // 将"你的数值"替换为你实际需要处理的double值
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedNumber = bd.doubleValue();
        return roundedNumber;

    }

    /**
     * 负载超过80%发送告警消息
     */
    public void warnLoadRate() {
        //默认查询今日

        //最新负载率
        String stap_total = dataSheetService.getLastValSql(new DateTime().toString("yyyy-MM-dd"), new DateTime().toString("yyyy-MM-dd HH:mm:ss"), "STAP_Total", "200363");
        //最新负载率 =  总视在功率 / 额定容量
        String loadRate = new BigDecimal(stap_total).divide(new BigDecimal(1250000)).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString();

        BigDecimal a = new BigDecimal(loadRate);
        BigDecimal b = new BigDecimal("80.00");

        int result = a.compareTo(b);
        if (result > 0) {
            //负载超过80%发送告警消息
            sendMesg(55,deviceCollectMapper.selectById(200363),"变压器长时间处于80%以上") ;


//            System.out.println("a 大于 b");
//        } else if (result < 0) {
//            System.out.println("a 小于 b");
//        } else {
//            System.out.println("a 等于 b");
        }
    }



    /**
     *
     *设备用电异常
     * @author wzn
     * @date 2025/05/22 08:58
     */
    public void powerEquipment(){
        //---------------设备用电异常

        DeviceCollectVo vo = new DeviceCollectVo() ;
        List<Integer> deviceTypes = new ArrayList<>() ;
        deviceTypes.add(10005) ;
        deviceTypes.add(200084) ;
        vo.setDeviceTypes(deviceTypes);
        List<DeviceCollectVo> list = deviceCollectMapper.getList(vo);

        for(DeviceCollectVo deviceCollectVo:list){

            // 获取当前日期
            LocalDate today = LocalDate.now();

            // 获取昨天的日期
            LocalDate yesterday = today.minusDays(1);
            //昨日用电
            List<Map<String, Object>> maps =getVal(deviceCollectVo.getDeviceCode(),yesterday+" 00:00:00",yesterday+" 23:59:59") ;
            Double yesVal = 0.00 ;
            if(!CollectionUtils.isEmpty(maps)){
                yesVal = (Double) maps.get(0).get("val");
            }


            //今日用电
            List<Map<String, Object>> maps2 =getVal(deviceCollectVo.getDeviceCode(),today+" 00:00:00",DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")) ;
            Double nowVal = 0.00 ;
            if(!CollectionUtils.isEmpty(maps2)){
                nowVal = (Double) maps2.get(0).get("val");
            }

            if(yesVal >0){
                // 判断是否超过 20%
                if (isExceedThreshold(nowVal, yesVal, 20.0)) {
                    //todo  调用推送告警信息接口
                    sendMesg(52,deviceCollectVo,"设备用电异常") ;
                }
            }
        }




    }

    /**
     *
     *月度用能超额10%
     * @author wzn
     * @date 2025/05/22 11:51
     */
    public void monthlyExcess() {
        EnergyUseAnomalyVo energyUseAnomalyVo = new EnergyUseAnomalyVo();
        QueryWrapper<DeviceEnergyUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("searchDate", DateUtil.format(new Date(), "yyyy-MM"));
        DeviceEnergyUse deviceEnergyUse = deviceEnergyUseMapper.selectOne(queryWrapper);
        energyUseAnomalyVo.setControlValue(deviceEnergyUse.getControlValue()) ;

        if(com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(deviceEnergyUse.getControlValue())) {
            //定额
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(deviceEnergyUse.getRatedValue())) {

                Double ratedValue = Double.parseDouble(deviceEnergyUse.getRatedValue());
                //实际用电
                Double realVal = getVal2(DateUtil.format(new Date(), "yyyy-MM"));

//                String result = calculateRatio(realVal, ratedValue);

                double result = 0.00;
                if (ratedValue != 0 && ratedValue != null) {
                    result = (realVal / ratedValue) * 100;
                } else {
                    result = realVal;
                }

                BigDecimal bd = new BigDecimal(Double.toString(result));
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                if (bd.compareTo(new BigDecimal("110")) ==1) {
                    //todo  调用推送告警信息接口
                    sendMesg(53,deviceCollectMapper.selectById(200363),"月度用能超规划值") ;
                }
            }
        }
    }


    /**
     *
     *分项用能超额定值预警
     * @author wzn
     * @date 2025/05/22 15:14
     */
    public void submit(){
        QueryWrapper<ConfigDeviceSubitem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("energy_type","1");
        List<ConfigDeviceSubitem> configDeviceSubitemList = configDeviceSubitemMapper.selectList(queryWrapper);

        Calendar calendar = Calendar.getInstance();

        // 获取本月第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        // 获取本月最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        for(ConfigDeviceSubitem c:configDeviceSubitemList){
            DataSheetVo bean = new DataSheetVo();
            bean.setEnergyType(1);
            bean.setSubitem(c.getId());
            bean.setIsTotalDevice(1);//查询是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
            if (collects.isEmpty()) {// 无采集设备

                bean.setIsTotalDevice(0);//查询不是总设备的数据
                collects= deviceCollectMapper.getDeviceByArea(bean);
            }
            List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String sql3 = " select sum(totalPower) as totalPower from (select last(val)-first(val) as totalPower from datasheet WHERE subitem='"+c.getId()+"'  AND channelId='ENERGY_TOTAL'  and time <='" + lastDayOfMonth + " 23:59:59' and time>='" + firstDayOfMonth +" 00:00:00"+ "' and deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ group by deviceCollectId) ";

            // 时序数据库解析
            QueryResult query3 = influxdbService.query(sql3);
            //解析列表类查询
            List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);
            Double nowVal = 0.00 ;
            //本月实际分项用电
            if(!CollectionUtils.isEmpty(maps3)){
                nowVal = (Double) maps3.get(0).get("totalPower");
            }

            //查询该月定额值
            QueryWrapper<SubmiteValue> queryWrapper1 = new QueryWrapper<>() ;
            queryWrapper1.eq("subitemId", c.getId()) ;
            queryWrapper1.eq("searchDate", DateUtil.format(new Date(), "yyyy-MM")) ;
            SubmiteValue submiteValue = submiteValueMapper.selectOne(queryWrapper1) ;



            if(StringUtils.isNotEmpty(submiteValue.getRatedValue()) && !"0".equals(submiteValue.getRatedValue())){
                // 判断是否超过 10%
                if (isExceedThreshold(nowVal, Double.valueOf(submiteValue.getRatedValue()), 10.0)) {
                    //todo  调用推送告警信息接口
                    sendMesg(54,deviceCollectMapper.selectById(200363),c.getSubitemName()+"超过定额值") ;
                }
            }


        }
    }



    /**
     *
     *空载电流异常  0-5点
     * @author wzn
     * @date 2025/05/23 08:51
     */
    public void noLoadCurrent(){

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 上一个小时
        LocalDateTime previousHour = now.minusHours(1);

        // 获取当天的零点时间
        LocalDateTime todayMidnight = now.with(LocalTime.MIDNIGHT);

        // 判断上个小时是否在当天0点之后
        if (previousHour.isAfter(todayMidnight)) {
           //上一个小时是在今天0点之后


            // 定义格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

            // 获取当前时间
            String currentHour = now.format(formatter);

            // 获取上一个小时
            String lastHour = previousHour.format(formatter);

            //当前小时A相平均电流
            String sql = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_A'  and time <='" + currentHour + " :59:59' and time>='" + currentHour + " :00:00'";

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

            //当前小时A相平均电流
            Double nowAVal = 0.00 ;
            if(!CollectionUtils.isEmpty(maps)){
                nowAVal = (Double) maps.get(0).get("totalPower");
            }


            //上个小时A相平均电流
            String lastSql = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_A'  and time <='" + lastHour + " :59:59' and time>='" + lastHour + " :00:00'";

            // 时序数据库解析
            QueryResult lastQuery = influxdbService.query(lastSql);
            //解析列表类查询
            List<Map<String, Object>> lastMaps = influxdbService.queryResultProcess(lastQuery);

            //上个小时A相平均电流
            Double lastAVal = 0.00 ;
            if(!CollectionUtils.isEmpty(lastMaps)){
                lastAVal = (Double) lastMaps.get(0).get("totalPower");
            }

            if (isExceedThreshold(nowAVal, Double.valueOf(lastAVal), 20.0)) {
                //todo  调用推送告警信息接口
                sendMesg(56,deviceCollectMapper.selectById(200363),"A相空载电流异常") ;
            }




            //当前小时B相平均电流
            String sqlB = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_B'  and time <='" + currentHour + " :59:59' and time>='" + currentHour + " :00:00'";

            // 时序数据库解析
            QueryResult queryB = influxdbService.query(sqlB);
            //解析列表类查询
            List<Map<String, Object>> mapsB = influxdbService.queryResultProcess(queryB);

            //当前小时A相平均电流
            Double nowBVal = 0.00 ;
            if(!CollectionUtils.isEmpty(mapsB)){
                nowBVal = (Double) mapsB.get(0).get("totalPower");
            }


            //上个小时B相平均电流
            String lastSqlB = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_B'  and time <='" + lastHour + " :59:59' and time>='" + lastHour + " :00:00'";

            // 时序数据库解析
            QueryResult lastQueryB = influxdbService.query(lastSqlB);
            //解析列表类查询
            List<Map<String, Object>> lastMapsB = influxdbService.queryResultProcess(lastQueryB);

            //上个小时B相平均电流
            Double lastBVal = 0.00 ;
            if(!CollectionUtils.isEmpty(lastMapsB)){
                lastBVal = (Double) lastMapsB.get(0).get("val");
            }

            if (isExceedThreshold(nowBVal, Double.valueOf(lastBVal), 20.0)) {
                //todo  调用推送告警信息接口
                sendMesg(56,deviceCollectMapper.selectById(200363),"B相空载电流异常") ;

            }





            //当前小时C相平均电流
            String sqlC = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_C'  and time <='" + currentHour + " :59:59' and time>='" + currentHour + " :00:00'";

            // 时序数据库解析
            QueryResult queryC = influxdbService.query(sqlC);
            //解析列表类查询
            List<Map<String, Object>> mapsC = influxdbService.queryResultProcess(queryC);

            //当前小时A相平均电流
            Double nowCVal = 0.00 ;
            if(!CollectionUtils.isEmpty(mapsC)){
                nowCVal = (Double) mapsC.get(0).get("totalPower");
            }


            //上个小时B相平均电流
            String lastSqlC = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_C'  and time <='" + lastHour + " :59:59' and time>='" + lastHour + " :00:00'";

            // 时序数据库解析
            QueryResult lastQueryC = influxdbService.query(lastSqlC);
            //解析列表类查询
            List<Map<String, Object>> lastMapsC = influxdbService.queryResultProcess(lastQueryC);

            //上个小时B相平均电流
            Double lastCVal = 0.00 ;
            if(!CollectionUtils.isEmpty(lastMapsC)){
                lastCVal = (Double) lastMapsC.get(0).get("totalPower");
            }

            if (isExceedThreshold(nowCVal, Double.valueOf(lastCVal), 20.0)) {
                //todo  调用推送告警信息接口
                sendMesg(56,deviceCollectMapper.selectById(200363),"C相空载电流异常") ;

            }

        }





    }

    public  void sendMesg(Integer configId,DeviceCollect deviceCollect ,String contentStr) {
        AlarmContentConfig config = alarmContentConfigMapper.selectById(configId);
        AlarmRecords record = new AlarmRecords();
        record.setDeviceSource(5);
        record.setDeviceId(deviceCollect.getId());
        record.setDeviceName(deviceCollect.getName());
        record.setGatewayId(deviceCollect.getGatewayId());
        record.setDeviceType(deviceCollect.getDeviceType());
        record.setBuildId(deviceCollect.getBuildId());
        record.setFloorId(deviceCollect.getFloorId());
        record.setAreaId(deviceCollect.getAreaId());
        record.setAlarmTime(DateUtil.now());
        record.setAlarmCategory(config.getAlarmCategory());
        record.setAlarmContentId(config.getId());
        record.setAlarmLevel(config.getAlarmLevel());
        record.setAlarmContentStr(contentStr);
        record.setAlarmType(config.getAlarmType());
        record.setAlarmStatus("1");
        record.setHandleStatus("0");
        record.setIsDel(0);
        alarmRecordsService.saveRecordAndExecutePlan(record);
    }

    /**
     * 判断今日值是否比昨日值高出指定百分比
     * @param today 当前数值
     * @param yesterday 上一日数值
     * @param threshold 百分比阈值（如20.0表示20%）
     * @return 是否超过阈值
     */
    public static boolean isExceedThreshold(Double today, Double yesterday, double threshold) {
        if (today == null || yesterday == null || yesterday == 0.0) {
            return false; // 防止空值或除以零
        }

        double increaseRate = (today - yesterday) / yesterday * 100;

        return increaseRate >= threshold;
    }


    public List<Map<String, Object>> getVal(String deviceCode,String startTime,String endTime){
            String sql1 = " select last(val)-first(val) from datasheet where deviceCollectId='"+deviceCode+"' and channelId='ENERGY_TOTAL'   and time >'"+startTime+"' and time <'"+endTime+"'  \n" ;
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql1);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            return maps;
    }



    public Double getVal2(String time) {
        double val = 0.00;
        // 给定的月份字符串
        String monthString = time;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 解析给定的月份字符串
        YearMonth yearMonth = YearMonth.parse(monthString, inputFormatter);

        // 获取该月的第一天
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDateTime firstDayDateTime = firstDay.atStartOfDay();

        // 获取该月的最后一天
        LocalDate lastDay = yearMonth.atEndOfMonth();
        LocalDateTime lastDayDateTime = lastDay.atTime(23, 59, 59);


        String sql = "select last(val)-first(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='ENERGY_TOTAL'  and time <='" + lastDayDateTime.format(outputFormatter) + "' and time>='" + firstDayDateTime.format(outputFormatter) + "'";


        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(maps)) {
            val = (Double) maps.get(0).get("totalPower");
        }
        return val ;
    }









//    public List<JSONObject> energyZuhu(String type,String starTime,String endTime){
//
//        SysUser loginUser = sysUserService.getUser();
//        Integer orgId = loginUser.getUnitId();
//        if (orgId == null) {
//            return null ;
//        }
////        TenantContract contract = tenantContractService.getByMerchantId(orgId+"");
////        if (contract == null) {
////            return null;
////        }
////        String roomIds = contract.getHireRoomIds();
////        if (org.apache.commons.lang3.StringUtils.isBlank(roomIds)) {
////            return null;
////        }
//
//
//        String timeLastSql = "" ;
//        String timeFirstSql = "" ;
//        List<HireRoomOfDevice> deviceList= null ;
//        List<String> list2 = Arrays.asList(roomIds.split(","));
//
//        if("1".equals(type)){
//            timeLastSql = "SELECT LAST(val) as val FROM datasheet WHERE channelId = 'ENERGY_TOTAL' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//            timeFirstSql = "SELECT FIRST(val) as val FROM datasheet WHERE channelId = 'ENERGY_TOTAL' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//             deviceList = hireRoomOfDeviceMapper.selectList(new LambdaQueryWrapper<HireRoomOfDevice>().in(HireRoomOfDevice::getRoom, list2).eq(HireRoomOfDevice::getDeviceType, "1"));
//
//        }else {
//            timeFirstSql = "SELECT FIRST(val) as val FROM datasheet WHERE channelId = 'WATER_L' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//            timeLastSql = "SELECT LAST(val) as val FROM datasheet WHERE channelId = 'WATER_L' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//            deviceList = hireRoomOfDeviceMapper.selectList(new LambdaQueryWrapper<HireRoomOfDevice>().in(HireRoomOfDevice::getRoom, list2).eq(HireRoomOfDevice::getDeviceType, "3"));
//
//
//        }
//
//        // 定义格式化器
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
//
//        // 解析为 YearMonth
//        YearMonth startMonth = YearMonth.parse(starTime, yearMonthFormatter);
//        YearMonth endMonth = YearMonth.parse(endTime, yearMonthFormatter);
//
//
//        // 从起始月开始循环，直到结束月（包含）
//        YearMonth current = startMonth;
//
//        List<JSONObject> list = new ArrayList<>();
//
//        // 循环直到当前月份超过结束月份
//        while (!current.isAfter(endMonth)) {
//            // 获取当月第一天和最后一天
//            LocalDate monthStart = current.atDay(1);
//            LocalDate monthEnd = current.atEndOfMonth();
//
//            System.out.println("开始日期: " + monthStart.format(formatter));
//            System.out.println("结束日期: " + monthEnd.format(formatter));
//
//            starTime = monthStart.format(formatter) ;
//            endTime = monthEnd.format(formatter);
//
//            JSONObject result=new JSONObject();
//
//            result.put("month", current.toString()) ;
//            EnergyReportVo vo = new EnergyReportVo() ;
//
//
//            starTime = starTime + " 00:00:00";
//            endTime = endTime + " 23:59:59";
//
//
//            String contractStartDate = contract.getLeaseStartTime() + " 00:00:00";
//            String contractEndDate = contract.getLeaseEndTime() + " 23:59:59";
//
//            cn.hutool.core.date.DateTime contractStartDateTime = DateUtil.parseDateTime(contractStartDate);
//            cn.hutool.core.date.DateTime contractEndDateTime = DateUtil.parseDateTime(contractEndDate);
//
//            // 统计开始时间 早于 合同开始时间
//            if (DateUtil.parseDateTime(starTime).compareTo(contractStartDateTime) < 0) {
//                starTime = contractStartDate;
//            }
//            // 统计结束时间 晚于 合同结束时间
//            if (DateUtil.parseDateTime(endTime).compareTo(contractEndDateTime) > 0) {
//                endTime = contractEndDate;
//            }
//
//            if(StringUtils.isNotEmpty(type)){
//
//                BigDecimal yTotal = BigDecimal.ZERO;
//                // 合同开始时间 早于 统计开始时间
//                if (contractStartDateTime.compareTo(DateUtil.parseDateTime(starTime)) < 1) {
//                    for (HireRoomOfDevice device : deviceList) {
//                        String last = queryInfluxdbVal(String.format(timeLastSql, device.getDevice(), starTime, endTime));
//                        String first = queryInfluxdbVal(String.format(timeFirstSql, device.getDevice(), starTime, endTime));
//                        BigDecimal scale = new BigDecimal(last).subtract(new BigDecimal(first)).setScale(2, RoundingMode.HALF_UP);
//                        yTotal = yTotal.add(scale);
//                    }
//                }
//if("1".equals(type)){
//    result.put("thisTimeElectricity",String.format("%.2f",yTotal));
//
//}else {
//    result.put("thisTimeWater",String.format("%.2f",yTotal));
//
//}
//            }else {
//
//                BigDecimal yTotal = BigDecimal.ZERO;
//                BigDecimal yTotal2 = BigDecimal.ZERO;
//                timeLastSql = "SELECT LAST(val) as val FROM datasheet WHERE channelId = 'ENERGY_TOTAL' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//                timeFirstSql = "SELECT FIRST(val) as val FROM datasheet WHERE channelId = 'ENERGY_TOTAL' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//                // 合同开始时间 早于 统计开始时间
//                deviceList = hireRoomOfDeviceMapper.selectList(new LambdaQueryWrapper<HireRoomOfDevice>().in(HireRoomOfDevice::getRoom, list2).eq(HireRoomOfDevice::getDeviceType, "1"));
//
//                if (contractStartDateTime.compareTo(DateUtil.parseDateTime(starTime)) < 1) {
//                    for (HireRoomOfDevice device : deviceList) {
//                        String last = queryInfluxdbVal(String.format(timeLastSql, device.getDevice(), starTime, endTime));
//                        String first = queryInfluxdbVal(String.format(timeFirstSql, device.getDevice(), starTime, endTime));
//                        BigDecimal scale = new BigDecimal(last).subtract(new BigDecimal(first)).setScale(2, RoundingMode.HALF_UP);
//                        yTotal = yTotal.add(scale);
//                    }
//                }
//                result.put("thisTimeElectricity",String.format("%.2f",yTotal));
//
//
//                timeFirstSql = "SELECT FIRST(val) as val FROM datasheet WHERE channelId = 'WATER_L' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//                timeLastSql = "SELECT LAST(val) as val FROM datasheet WHERE channelId = 'WATER_L' AND deviceCollectId = '%s' AND time >= '%s' AND time <= '%s'";
//
//// 合同开始时间 早于 统计开始时间
//                deviceList = hireRoomOfDeviceMapper.selectList(new LambdaQueryWrapper<HireRoomOfDevice>().in(HireRoomOfDevice::getRoom, list2).eq(HireRoomOfDevice::getDeviceType, "3"));
//
//                if (contractStartDateTime.compareTo(DateUtil.parseDateTime(starTime)) < 1) {
//                    for (HireRoomOfDevice device : deviceList) {
//                        String last = queryInfluxdbVal(String.format(timeLastSql, device.getDevice(), starTime, endTime));
//                        String first = queryInfluxdbVal(String.format(timeFirstSql, device.getDevice(), starTime, endTime));
//                        BigDecimal scale = new BigDecimal(last).subtract(new BigDecimal(first)).setScale(2, RoundingMode.HALF_UP);
//                        yTotal2 = yTotal2.add(scale);
//                    }
//                }
//                result.put("thisTimeWater",String.format("%.2f",yTotal2));
//            }
//
//
//            // 进入下一个月
//            current = current.plusMonths(1);
//            list.add(result) ;
//        }
//
//
//        return list ;
//
//    }


//    public List<Integer> getDevice(String deviceType){
//        SysUser loginUser = sysUserService.getUser();
//        Integer orgId = loginUser.getUnitId();
//        if (orgId == null) {
//            throw new CustomException("未绑定租赁单位");
//        }
//
//        //查合同
//        TenantContractVo tenantContractVo =  tenantContractService.getByMerchantId(orgId+"") ;
//
//        //房间
//        List<String> list = Arrays.stream(tenantContractVo.getHireRoomIds().split(","))
//                .collect(Collectors.toList());
//
//        QueryWrapper<HireRoomOfDevice>
//
//
//
//    }

    public String queryInfluxdbVal(String sql) {
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        if (CollUtil.isEmpty(maps)) {
            return "0";
        }
        Map<String, Object> map = maps.get(0);
        return map.get("val").toString();
    }
}
