package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.DeviceCollectDao;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface DeviceCollectMapper extends BaseMapper<DeviceCollect> {

    @SelectProvider(type = DeviceCollectDao.class, method = "getEntityList")
    Page<DeviceCollectVo> getEntityList(Page page, DeviceCollectVo vo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getList")
    List<DeviceCollectVo> getList(DeviceCollectVo vo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getFileOut")
    List<DeviceCollectVo> getFileOut(Integer id);
    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceStatus")
    List<DeviceCollectVo> getDeviceStatus(DeviceCollectVo vo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceInfo")
    Page<DeviceCollectVo> getDeviceInfo(Page page, DeviceQueryVo queryVo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceInfo")
    List<DeviceCollectVo> getDeviceInfoList(Page page, DeviceQueryVo queryVo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getSubitemEnergy")
    List<DeviceCollectVo> getSubitemEnergy(DeviceQueryVo queryVo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceList")
    List<DeviceCollect> getDeviceList(DeviceQueryVo queryVo);
    //数据统计sql
    @SelectProvider(type = DeviceCollectDao.class, method = "coulometry")
    List<DeviceCollectVo> coulometry(Integer buildId,Integer floorId,Integer areaId,Integer deviceType,
                                     Integer energyType);

    //数据统计sql
    @SelectProvider(type = DeviceCollectDao.class, method = "coulometry")
    List<DeviceCollectVo> coulometry(String name);
    //设备监控
//    @SelectProvider(type = DeviceCollectDao.class, method = "deviceMonitoring")
//    Page<DeviceCollectVo> deviceMonitoring(Page page,Integer onLineStatus,Integer areaId,Integer floorId,
//                                           Integer buildId,
//                                           String name,
//                                           Integer type,String areaName,Integer deviceType,String areaIds ,String buildIds ,String floorIds,String id) ;
    //设备监控
    @SelectProvider(type = DeviceCollectDao.class, method = "deviceMonitoring")
    Page<DeviceCollectVo> deviceMonitoring(Page page,Integer onLineStatus,Integer areaId,Integer floorId,
                                           Integer buildId,
                                           String name,
                                           Integer type,String areaName,Integer deviceType,String areaIds ,String buildIds ,String floorIds,String id,List<Integer> deviceTypes) ;
    @SelectProvider(type = DeviceCollectDao.class, method = "deviceMonitoring2")
    Page<DeviceCollectVo> deviceMonitoring2(Page page,Integer onLineStatus,Integer areaId,Integer floorId,
                                           Integer buildId,
                                           String name,
                                           Integer type,String areaName,Integer deviceType,String areaIds ,String buildIds ,String floorIds,String id,List<String> deviceTypes) ;

    // 获取设备类型下所有用电设备，用水设备，用气设备
    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceByType")
    List<DeviceCollectVo> getDeviceByType(DataSheetVo bean);

    // 获取设备类型下所有用电设备，用水设备，用气设备
    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceByFloor")
    List<DeviceCollectVo> getDeviceByFloor(DataSheetVo bean);

    // 获取区域下所有用电设备，用水设备，用气设备
    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceByArea")
    List<DeviceCollect> getDeviceByArea(DataSheetVo bean);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceHarmonics")
    List<DeviceCollect> getDeviceHarmonics(DeviceQueryVo queryVo);

    // 获取设备所属的类型
    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceTypeCode")
    List<DeviceCollect> getDeviceTypeCode(Integer deviceId);

    @SelectProvider(type = DeviceCollectDao.class, method = "getNotTotalDeviceList")
    List<DeviceCollect> getNotTotalDeviceList(Integer deviceId);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceReport")
    Page<DeviceCollectVo> getDeviceReportPage(Page page, DeviceQueryVo queryVo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceReport")
    List<DeviceCollectVo> getDeviceReportList(DeviceQueryVo queryVo);

    @SelectProvider(type = DeviceCollectDao.class, method = "getDeviceByAreas")
    List<DeviceCollect> getDeviceByAreas(ReportManageVo bean);

    @SelectProvider(type = DeviceCollectDao.class, method = "deviceByType")
    List<String > deviceByType(String type);

    @SelectProvider(type = DeviceCollectDao.class, method = "selectTenantRoomDevice")
    List<TenantRoomDeviceVo> selectTenantRoomDevice(Integer deviceType, Integer id, List<Integer> excludeIds);

    @SelectProvider(type = DeviceCollectDao.class, method = "selectNewTenantRoomDevice")
    Page<TenantRoomDeviceVo> selectNewTenantRoomDevice(Page page, List<Integer> ids);
}
