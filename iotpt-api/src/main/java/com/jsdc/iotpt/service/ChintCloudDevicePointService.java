package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.dto.chintcloud.DevicePointDto;
import com.jsdc.iotpt.dto.chintcloud.GatewayDto;
import com.jsdc.iotpt.dto.chintcloud.PointRealtimeDataDto;
import com.jsdc.iotpt.mapper.ChintCloudDevicePointMapper;
import com.jsdc.iotpt.model.ChintCloudDevicePoint;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ChintCloudDevicePointVo;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ChintCloudDevicePointService extends BaseService<ChintCloudDevicePoint> {

    @Autowired
    private ChintCloudDevicePointMapper chintCloudDevicePointMapper;

    @Autowired
    private LightingService lightingService;

    @Autowired
    private SysBuildService sysBuildService;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private SysBuildAreaService sysBuildAreaService;

    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;

    public Page<ChintCloudDevicePoint> pageList(ChintCloudDevicePointVo point) {
        Page<ChintCloudDevicePoint> page = new Page<>(point.getPageIndex(), point.getPageSize());
        Page<ChintCloudDevicePoint> result = page(page, new LambdaUpdateWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getIsMaster, 0)
                .eq(StringUtils.isNotNull(point.getBuildId()), ChintCloudDevicePoint::getBuildId, point.getBuildId())
                .eq(StringUtils.isNotNull(point.getFloorId()), ChintCloudDevicePoint::getFloorId, point.getFloorId())
                .eq(StringUtils.isNotNull(point.getAreaId()), ChintCloudDevicePoint::getAreaId, point.getAreaId())
                .eq(StringUtils.isNotNull(point.getType()), ChintCloudDevicePoint::getType, point.getType())
                .like(StringUtils.isNotBlank(point.getPointName()), ChintCloudDevicePoint::getPointName, point.getPointName())
        );
        if (result.getTotal() > 0) {
            List<ChintCloudDevicePoint> list = result.getRecords();

            // 建筑名称映射
            List<SysBuild> buildList = sysBuildService.list(new LambdaQueryWrapper<SysBuild>().eq(SysBuild::getIsDel, 0));
            Map<Integer, String> buildMap = buildList.stream().collect(Collectors.toMap(SysBuild::getId, SysBuild::getBuildName, (a, b) -> b));
            // 楼层名称映射
            List<SysBuildFloor> floorList = sysBuildFloorService.list(new LambdaQueryWrapper<SysBuildFloor>().eq(SysBuildFloor::getIsDel, 0));
            Map<Integer, String> floorMap = floorList.stream().collect(Collectors.toMap(SysBuildFloor::getId, SysBuildFloor::getFloorName, (a, b) -> b));
            // 区域名称映射
            List<SysBuildArea> areaList = sysBuildAreaService.list(new LambdaQueryWrapper<SysBuildArea>().eq(SysBuildArea::getIsDel, 0));
            Map<Integer, String> areaMap = areaList.stream().collect(Collectors.toMap(SysBuildArea::getId, SysBuildArea::getAreaName, (a, b) -> b));

            List<String> pointList = list.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
            List<PointRealtimeDataDto> realtimeData = lightingService.getPointRealtimeData(pointList);
            Map<String, String> collect = realtimeData.stream().collect(Collectors.toMap(PointRealtimeDataDto::getPointId, PointRealtimeDataDto::getValue));

            List<ConfigDeviceType> typeList = configDeviceTypeService.list(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getIsDel, 0));
            Map<Integer, ConfigDeviceType> deviceTypeMap = typeList.stream().collect(Collectors.toMap(ConfigDeviceType::getId, Function.identity(), (a, b) -> b));

            for (ChintCloudDevicePoint record : list) {
                record.setOpen(Objects.equals("1", collect.get(record.getStoragePoint())));
                record.setBuildName(buildMap.get(record.getBuildId()));
                record.setFloorName(floorMap.get(record.getFloorId()));
                record.setAreaName(areaMap.get(record.getAreaId()));

                if (StringUtils.isNotBlank(record.getType())) {
                    ConfigDeviceType type = deviceTypeMap.get(Integer.valueOf(record.getType()));
                    record.setTypeName(type.getDeviceTypeName());
                    record.setFileName(type.getFilename());
                }
            }
        }
        return result;
    }

    public List<ChintCloudDevicePoint> list(ChintCloudDevicePoint point) {
        List<ChintCloudDevicePoint> list = list(new LambdaUpdateWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getIsMaster, 0)
                .eq(StringUtils.isNotNull(point.getBuildId()), ChintCloudDevicePoint::getBuildId, point.getBuildId())
                .eq(StringUtils.isNotNull(point.getFloorId()), ChintCloudDevicePoint::getFloorId, point.getFloorId())
                .eq(StringUtils.isNotNull(point.getAreaId()), ChintCloudDevicePoint::getAreaId, point.getAreaId())
                .eq(StringUtils.isNotNull(point.getType()), ChintCloudDevicePoint::getType, point.getType())
                .like(StringUtils.isNotBlank(point.getPointName()), ChintCloudDevicePoint::getPointName, point.getPointName()));
        if (StringUtils.isNotEmpty(list)) {
            // 建筑名称映射
            List<SysBuild> buildList = sysBuildService.list(new LambdaQueryWrapper<SysBuild>().eq(SysBuild::getIsDel, 0));
            Map<Integer, String> buildMap = buildList.stream().collect(Collectors.toMap(SysBuild::getId, SysBuild::getBuildName, (a, b) -> b));
            // 楼层名称映射
            List<SysBuildFloor> floorList = sysBuildFloorService.list(new LambdaQueryWrapper<SysBuildFloor>().eq(SysBuildFloor::getIsDel, 0));
            Map<Integer, String> floorMap = floorList.stream().collect(Collectors.toMap(SysBuildFloor::getId, SysBuildFloor::getFloorName, (a, b) -> b));
            // 区域名称映射
            List<SysBuildArea> areaList = sysBuildAreaService.list(new LambdaQueryWrapper<SysBuildArea>().eq(SysBuildArea::getIsDel, 0));
            Map<Integer, String> areaMap = areaList.stream().collect(Collectors.toMap(SysBuildArea::getId, SysBuildArea::getAreaName, (a, b) -> b));

            List<String> pointList = list.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
            List<PointRealtimeDataDto> realtimeData = lightingService.getPointRealtimeData(pointList);
            Map<String, String> collect = realtimeData.stream().collect(Collectors.toMap(PointRealtimeDataDto::getPointId, PointRealtimeDataDto::getValue));

            List<ConfigDeviceType> typeList = configDeviceTypeService.list(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getIsDel, 0));
            Map<Integer, ConfigDeviceType> deviceTypeMap = typeList.stream().collect(Collectors.toMap(ConfigDeviceType::getId, Function.identity(), (a, b) -> b));

            ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "CHINTRADAR").eq(ConfigDeviceType::getIsDel, 0));
            ChintCloudDevicePoint one = getOne(new LambdaQueryWrapper<ChintCloudDevicePoint>().eq(ChintCloudDevicePoint::getIsDel, 0).eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId())).eq(ChintCloudDevicePoint::getIsMaster, 1));
            PointRealtimeDataDto radarData = lightingService.getPointRealtimeData(one.getStoragePoint());
            boolean radar = Objects.equals("1", radarData.getValue());

            for (ChintCloudDevicePoint record : list) {
                record.setOpen(Objects.equals("1", collect.get(record.getStoragePoint())));
                record.setBuildName(buildMap.get(record.getBuildId()));
                record.setFloorName(floorMap.get(record.getFloorId()));
                record.setAreaName(areaMap.get(record.getAreaId()));

                if (StringUtils.isNotBlank(record.getType())) {
                    ConfigDeviceType type = deviceTypeMap.get(Integer.valueOf(record.getType()));
                    record.setTypeName(type.getDeviceTypeName());
                    record.setFileName(type.getFilename());
                }
                record.setSceneLinkage(radar ? 1 : 2);
                record.setRadar(radar ? 1 : 0);
            }
        }
        if (StringUtils.isNotNull(point.getOpen())) {
            return list.stream().filter(a -> a.getOpen() == point.getOpen()).collect(Collectors.toList());
        }
        return list;
    }

    public void edit(ChintCloudDevicePoint point) {
        if (StringUtils.isNull(point.getId())) {
            return;
        }
        ChintCloudDevicePoint record = new ChintCloudDevicePoint();
        record.setId(point.getId());
        if (StringUtils.isNotNull(point.getBuildId())) {
            record.setBuildId(point.getBuildId());
        }
        if (StringUtils.isNotNull(point.getFloorId())) {
            record.setFloorId(point.getFloorId());
        }
        if (StringUtils.isNotNull(point.getAreaId())) {
            record.setAreaId(point.getAreaId());
        }
        if (StringUtils.isNotBlank(point.getPointName())) {
            record.setPointName(point.getPointName());
        }
        if (StringUtils.isNotNull(point.getType())) {
            record.setType(point.getType());
        }
        if (StringUtils.isNotNull(point.getIsMaster())) {
            record.setIsMaster(point.getIsMaster());
        }
        record.setRemark(point.getRemark());
        record.updateById();
    }

    public void switchByPoint(List<String> pointIds, String value) {
        lightingService.setPointRealtimeData(pointIds, value);
    }

    public void switchBySpace(Integer buildId, Integer floorId, Integer areaId, String value) {
        if (buildId == null && floorId == null && areaId == null) {
            throw new RuntimeException("楼宇、楼层、区域至少一项不为空");
        }
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "LIGHT").eq(ConfigDeviceType::getIsDel, 0));
        List<ChintCloudDevicePoint> list = list(new LambdaUpdateWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(StringUtils.isNotNull(buildId), ChintCloudDevicePoint::getBuildId, buildId)
                .eq(StringUtils.isNotNull(floorId), ChintCloudDevicePoint::getFloorId, floorId)
                .eq(StringUtils.isNotNull(areaId), ChintCloudDevicePoint::getAreaId, areaId)
                .eq(ChintCloudDevicePoint::getIsMaster, 0)
                .eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId()))
        );
        List<String> pointIds = list.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
        boolean flag = lightingService.setPointRealtimeData(pointIds, value);
        if (!flag) {
            throw new RuntimeException("操作失败");
        }
    }

    public void refresh() {
        List<GatewayDto> gatewayList = lightingService.gatewayList();
        for (GatewayDto dto : gatewayList) {
            List<DevicePointDto> dtoList = lightingService.getDevicePointList(dto.getId());
            for (DevicePointDto pointDto : dtoList) {
                ChintCloudDevicePoint point = chintCloudDevicePointMapper.selectOne(new LambdaUpdateWrapper<ChintCloudDevicePoint>().eq(ChintCloudDevicePoint::getChintCloudId, pointDto.getId()));
                if (point == null) {
                    point = new ChintCloudDevicePoint();
                    BeanUtils.copyProperties(pointDto, point);
                    point.setId(null);
                    point.setChintCloudId(pointDto.getId());
                    point.setIsDel(0);
                    point.insert();
                } else {
                    String pointName = point.getPointName();
                    String type = point.getType();
                    String remark = point.getRemark();
                    BeanUtils.copyProperties(pointDto, point);
                    point.setPointName(pointName);
                    point.setType(type);
                    point.setRemark(remark);
                    point.setChintCloudId(pointDto.getId());
                    point.setIsDel(0);
                    point.updateById();
                }
            }
        }
    }

    public void del(Integer id) {
        ChintCloudDevicePoint point = new ChintCloudDevicePoint();
        point.setId(id);
        point.setIsDel(1);
        point.updateById();
    }

    public Map<String, String> statistics() {
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "LIGHT").eq(ConfigDeviceType::getIsDel, 0));
        List<GatewayDto> gatewayList = lightingService.gatewayList();
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isEmpty(gatewayList)) {
            result.put("gatewayCount", "0");
            result.put("gatewayOnline", "0");
        } else {
            result.put("gatewayCount", String.valueOf(gatewayList.size()));
            result.put("gatewayOnline", String.valueOf(gatewayList.size()));
        }
        result.put("gatewayOffline", "0");
        result.put("gatewayOfflineRate", "0.0%");
        Long count = chintCloudDevicePointMapper.selectCount(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId()))
                .eq(ChintCloudDevicePoint::getIsMaster, 0));
        result.put("deviceCount", String.valueOf(count));
        ChintCloudDevicePoint point = chintCloudDevicePointMapper.selectOne(new LambdaQueryWrapper<ChintCloudDevicePoint>().eq(ChintCloudDevicePoint::getIsDel, 0).eq(ChintCloudDevicePoint::getType, "200104").eq(ChintCloudDevicePoint::getIsMaster, "1"));
        if (StringUtils.isNotNull(point)) {
            PointRealtimeDataDto data = lightingService.getPointRealtimeData(point.getStoragePoint());
            result.put("radarStatus", data.getValue());
        }


        return result;
    }

    public void radarSwitch(String value) {
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "CHINTRADAR").eq(ConfigDeviceType::getIsDel, 0));
        List<ChintCloudDevicePoint> list = list(new LambdaQueryWrapper<ChintCloudDevicePoint>().eq(ChintCloudDevicePoint::getIsDel, 0).eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId())).eq(ChintCloudDevicePoint::getIsMaster, 1));
        List<String> pointIds = list.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
        boolean flag = lightingService.setPointRealtimeData(pointIds, value);
        if (!flag) {
            throw new RuntimeException("操作失败");
        }
    }

    public void masterSwitch(String value) {
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "LIGHT").eq(ConfigDeviceType::getIsDel, 0));
        List<ChintCloudDevicePoint> list = list(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId()))
                .eq(ChintCloudDevicePoint::getIsMaster, 0));
        List<String> pointIds = list.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
        boolean flag = lightingService.setPointRealtimeData(pointIds, value);
        if (!flag) {
            throw new RuntimeException("操作失败");
        }
    }

    public void switchByBuild(Integer floorId, Integer areaId, String value) {
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "LIGHT").eq(ConfigDeviceType::getIsDel, 0));
        List<ChintCloudDevicePoint> chintCloudDevicePoints = chintCloudDevicePointMapper.selectList(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId()))
                .eq(ChintCloudDevicePoint::getIsMaster, 0)
                .eq(StringUtils.isNotNull(floorId), ChintCloudDevicePoint::getFloorId, floorId)
                .eq(StringUtils.isNotNull(areaId), ChintCloudDevicePoint::getAreaId, areaId));
        List<String> collect = chintCloudDevicePoints.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
        boolean flag = lightingService.setPointRealtimeData(collect, value);
        if (!flag) {
            throw new RuntimeException("操作失败");
        }
    }

    public Map<String, Object> wlwAppStatistics() {
        Map<String, Object> result = new HashMap<>();
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "LIGHT").eq(ConfigDeviceType::getIsDel, 0));
        ChintCloudDevicePoint device = new ChintCloudDevicePoint();
        device.setType(String.valueOf(deviceType.getId()));
        List<ChintCloudDevicePoint> list = list(device);

        result.put("fileName", deviceType.getFilename());
        result.put("total", list.size());
        result.put("online", list.size());
        long openCount = list.stream().filter(ChintCloudDevicePoint::getOpen).count();
        result.put("open", openCount);
        result.put("close", list.size() - openCount);
        return result;
    }

    public List<SysBuildFloor> groupByFloor(String type) {
        ConfigDeviceType deviceType = configDeviceTypeService.getOne(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getDeviceTypeCode, "LIGHT").eq(ConfigDeviceType::getIsDel, 0));

        List<ChintCloudDevicePoint> pointList = list(new LambdaQueryWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(ChintCloudDevicePoint::getType, String.valueOf(deviceType.getId()))
                .eq(ChintCloudDevicePoint::getIsMaster, 0)
                .isNotNull(ChintCloudDevicePoint::getFloorId));
        if (!Objects.equals("0", type)) {
            List<String> storagePointList = pointList.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
            List<PointRealtimeDataDto> pointRealtimeData = lightingService.getPointRealtimeData(storagePointList);
            Map<String, String> realtimeDataMap = pointRealtimeData.stream().collect(Collectors.toMap(PointRealtimeDataDto::getPointId, PointRealtimeDataDto::getValue));
            pointList.forEach(point -> {
                point.setOpen(Objects.equals("1", realtimeDataMap.get(point.getStoragePoint())));
            });
        }

        Set<Integer> hasPointFloorIds = pointList.stream().map(ChintCloudDevicePoint::getFloorId).collect(Collectors.toSet());

        List<SysBuildFloor> list = sysBuildFloorService.list(new LambdaQueryWrapper<SysBuildFloor>()
                .eq(SysBuildFloor::getIsDel, 0)
                .in(SysBuildFloor::getId, hasPointFloorIds)
                .orderByAsc(SysBuildFloor::getSort));

        Map<Integer, List<ChintCloudDevicePoint>> collect = pointList.stream().collect(Collectors.groupingBy(ChintCloudDevicePoint::getFloorId));
        for (SysBuildFloor floor : list) {
            List<ChintCloudDevicePoint> points = collect.get(floor.getId());
            if (StringUtils.isEmpty(points)) {
                floor.setDeviceCount(0L);
            } else if (Objects.equals("1", type)) {
                floor.setDeviceCount(points.stream().filter(ChintCloudDevicePoint::getOpen).count());
            } else if (Objects.equals("2", type)) {
                floor.setDeviceCount(points.stream().filter(e -> !e.getOpen()).count());
            } else {
                floor.setDeviceCount((long) points.size());
            }
        }
        return list;
    }

    public ChintCloudDevicePoint detail(Integer id) {
        ChintCloudDevicePoint point = getById(id);
        PointRealtimeDataDto pointRealtimeData = lightingService.getPointRealtimeData(point.getStoragePoint());
        point.setOpen(Objects.equals("1", pointRealtimeData.getValue()));
        return point;
    }

    /**
     * 能耗预测
     * 照明列表
     */
    public List<ChintCloudDevicePoint> getLightingList(DeviceQueryVo vo) {
        List<ChintCloudDevicePoint> list = list(new LambdaUpdateWrapper<ChintCloudDevicePoint>()
                .eq(ChintCloudDevicePoint::getIsDel, 0)
                .eq(StringUtils.isNotNull(vo.getBuildIds()) && vo.getBuildIds().size() > 0, ChintCloudDevicePoint::getBuildId, vo.getBuildId())
                .eq(StringUtils.isNotNull(vo.getFloorIds()) && vo.getFloorIds().size() > 0, ChintCloudDevicePoint::getFloorId, vo.getFloorId())
                .eq(StringUtils.isNotNull(vo.getAreaIds()) && vo.getAreaIds().size() > 0, ChintCloudDevicePoint::getAreaId, vo.getAreaId()));
        if (StringUtils.isNotEmpty(list)) {
            // 区域名称映射
            List<SysBuildArea> areaList = sysBuildAreaService.list(new LambdaQueryWrapper<SysBuildArea>().eq(SysBuildArea::getIsDel, 0));
            Map<Integer, String> areaMap = areaList.stream().collect(Collectors.toMap(SysBuildArea::getId, SysBuildArea::getAreaName, (a, b) -> b));

            List<String> pointList = list.stream().map(ChintCloudDevicePoint::getStoragePoint).collect(Collectors.toList());
            List<PointRealtimeDataDto> realtimeData = lightingService.getPointRealtimeData(pointList);
            Map<String, String> collect = realtimeData.stream().collect(Collectors.toMap(PointRealtimeDataDto::getPointId, PointRealtimeDataDto::getValue));

            List<ConfigDeviceType> typeList = configDeviceTypeService.list(new LambdaQueryWrapper<ConfigDeviceType>().eq(ConfigDeviceType::getIsDel, 0));
            Map<Integer, ConfigDeviceType> deviceTypeMap = typeList.stream().collect(Collectors.toMap(ConfigDeviceType::getId, Function.identity(), (a, b) -> b));

            for (ChintCloudDevicePoint record : list) {
                //开启状态
                record.setOpen(Objects.equals("1", collect.get(record.getStoragePoint())));
                record.setAreaName(areaMap.get(record.getAreaId()));

                if (StringUtils.isNotBlank(record.getType())) {
                    ConfigDeviceType type = deviceTypeMap.get(Integer.valueOf(record.getType()));
                    record.setTypeName(type.getDeviceTypeName());
                }
            }
        }

        return list;
    }
}
