package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.Stranger;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.PalletVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zln
 */
@RestController
@RequestMapping("/app/pallet")
@Api(tags = "物联监控")
public class PalletController {

    @Autowired
    private SysBuildFloorService floorService;
    @Autowired
    private SysBuildAreaService areaService;
    @Autowired
    private DeviceCollectService collectService;
    @Autowired
    private ConfigSignalTypeService typeService;
    @Autowired
    private ConfigDeviceSignalMapService mapService;


    @RequestMapping(value = "/selectFloorAll", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有楼层")
    public ResultInfo selectFloorAll() {
        try {
            return ResultInfo.success(floorService.list(new LambdaQueryWrapper<SysBuildFloor>()
                    .eq(SysBuildFloor::getIsDel, 0).orderByAsc(SysBuildFloor::getSort)));
        } catch (Exception e) {
            throw new CustomException("获取所有楼层失败");
        }
    }


    @RequestMapping(value = "/selectByFloorId", method = RequestMethod.POST)
    @ApiOperation(value = "根据楼层获取区域", notes = "传递参数：floorId：楼层id(必填)")
    public ResultInfo selectByFloorId(Integer floorId) {
        try {
            if (null != floorId) {
                List<SysBuildArea> list = areaService.list(new LambdaQueryWrapper<SysBuildArea>().eq(SysBuildArea::getIsDel, 0).eq(SysBuildArea::getFloorId, floorId));
                return ResultInfo.success(list);
            }
            throw new CustomException("暂无区域");
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }


    @RequestMapping(value = "/selectByDeviceId", method = RequestMethod.POST)
    @ApiOperation(value = "根据区域获取设备和信号", notes = "传递参数：areaId：区域id(必填)")
    public ResultInfo selectByDeviceId(Integer areaId) {
        try {
            List<PalletVo> vos = new ArrayList<>();
            if (null != areaId) {
                List<DeviceCollect> list = collectService.list(new LambdaQueryWrapper<DeviceCollect>().eq(DeviceCollect::getIsDel, 0)
                        .eq(DeviceCollect::getAreaId, areaId));
                list.forEach(a -> {
                    PalletVo vo = new PalletVo();
                    List<ConfigDeviceSignalMap> signals = mapService.list(new LambdaQueryWrapper<ConfigDeviceSignalMap>().eq(ConfigDeviceSignalMap::getIsDel, 0)
                            .eq(ConfigDeviceSignalMap::getDeviceTypeId, a.getId()));
                    vo.setDevice_id(a.getId());
                    vo.setDevice_name(a.getName());
                    signals.forEach(b -> {
                        List<ConfigSignalType> configs = typeService.list(new LambdaQueryWrapper<ConfigSignalType>().eq(ConfigSignalType::getIsDel, 0)
                                .eq(ConfigSignalType::getId, b.getSignalTypeId()));
                        vo.setList(configs);
                    });
                    vos.add(vo);
                });
                return ResultInfo.success(vos);
            }
            throw new CustomException("暂无区域");
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }
}
