package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.vo.ConfigDeviceSignalMapVo;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/deviceMonitor")
@Api(tags = "采集终端管理")
public class DeviceMonitorController {
    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private DataSheetService dataSheetService;

    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;

    @Autowired
    private SysOrgManageService sysOrgManageService;

    /**
     * @Description: 设备监控
     * @param: [areaId, name, type]
     * @return: com.jsdc.iotpt.vo.ResultInfo
     * @author: yc
     * @date: 2023/7/26
     * @time: 9:27
     */
    @PostMapping("/pageListDeviceMonitor")
    @ApiOperation("设备监控")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaId",value = "区域",dataType = "Integer"),
            @ApiImplicitParam(name = "floorId",value = "楼层",dataType = "Integer"),
            @ApiImplicitParam(name = "buildId",value = "建筑",dataType = "Integer"),
            @ApiImplicitParam(name = "name",value = "设备名称",dataType = "String"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "Integer"),
            @ApiImplicitParam(name = "onLineStatus",value = "设备状态",dataType = "Integer"),
            @ApiImplicitParam(name = "page",value = "当前页数",dataType = "Integer" ,required = true),
            @ApiImplicitParam(name = "limit",value = "显示条数",dataType = "Integer",required = true)
    })
    public ResultInfo deviceMonitoring(Integer onLineStatus,Integer areaId,Integer floorId,Integer buildId, String name,
                                       Integer type,@RequestParam(name = "page") Integer pageIndex,@RequestParam(name = "limit")Integer pageSize,String areaName,
                                       Integer deviceType, String areaIds ,String buildIds ,String floorIds,String id){
        return ResultInfo.success(deviceCollectService.deviceMonitoring(onLineStatus,areaId,floorId,buildId, name,
                type, pageIndex, pageSize,areaName,deviceType,areaIds,buildIds,floorIds,id,null,null));
    }

    @PostMapping("/areaTreeList")
    public ResultInfo areaTreeList() {
        List<Map<String, Object>> maps = sysOrgManageService.areaTreeList();
        return ResultInfo.success(maps);
    }

    /**
     * 根据id查看
     */
    @PostMapping("/getById")
    public ResultInfo getById(DeviceCollectVo bean) {
        DeviceCollectVo deviceCollect = deviceCollectService.getDeviceCollect(bean.getId());
        return ResultInfo.success(deviceCollect);
    }

    @PostMapping("/getEntityByTIdGroup")
    public ResultInfo getEntityByTIdGroup(Integer typeId, Integer id) throws ParseException {
        List<ConfigDeviceSignalMapVo> entityByTIdGroup = dataSheetService.getEntityByTIdGroup(typeId, id);
        return ResultInfo.success(entityByTIdGroup);
    }

    @PostMapping("/getInformationReporting")
    @ApiOperation("设备监控--信号上报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "typeId",value = "设备类型id",dataType = "Integer")
    })
    public ResultInfo getInformationReporting(Integer typeId, Integer id) throws ParseException {
        return ResultInfo.success(dataSheetService.getInformationReporting(typeId, id));
    }


}
