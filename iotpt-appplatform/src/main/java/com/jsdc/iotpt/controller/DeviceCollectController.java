package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.service.DeviceCollectService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 * todo
 * controller控制器
 */
@RestController
@RequestMapping("/deviceCollect")
@Api(tags = "采集终端管理")
public class DeviceCollectController {

    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private DataSheetService dataSheetService;
    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("采集终端-分页查询")
    public ResultInfo getPageList(DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getPageList(bean));
    }

    @PostMapping("/getList")
    @ApiOperation("采集终端列表")
    public ResultInfo getList(DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getList(bean));
    }

    /**
     * 根据id查看
     */
    @PostMapping("/getById")
    @ApiOperation("添加/编辑采集终端")
    public ResultInfo getById(DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getDeviceCollect(bean.getId()));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑采集终端")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public ResultInfo saveOrUpdateDeviceCollect(@RequestBody DeviceCollectVo bean) {
        return deviceCollectService.saveOrUpdateDeviceCollect(bean);
    }

    /**
     *  关联设备
     * @param bean
     * @return
     */
    @PostMapping("/bindDevice")
    @ApiOperation("关联设备")
    public ResultInfo bindDevice(DeviceCollectVo bean){
        return deviceCollectService.bindDevice(bean);
    }

    /**
     *  解除关联设备
     * @param bean
     * @return
     */
    @PostMapping("/delBindDevice")
    @ApiOperation("解除关联")
    public ResultInfo delBindDevice(DeviceCollectVo bean){
        return deviceCollectService.delBindDevice(bean);
    }

    /**
     * 删除 todo
     */
    @RequestMapping("/delete")
    @ApiOperation("删除采集终端")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public ResultInfo deleteDeviceCollect(DeviceCollect bean) {
        deviceCollectService.deleteDeviceCollect(bean.getId());
        return ResultInfo.success("删除成功", new LogVo("删除"));
    }

    /**
     * 批量编辑
     */
    @RequestMapping("/batchUpdate")
    @ApiOperation("批量编辑采集终端")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public ResultInfo batchUpdateDeviceCollect(@RequestBody DeviceCollectVo bean) {
        deviceCollectService.batchEditDeviceCollect(bean);
        return ResultInfo.success("操作成功", new LogVo("批量编辑"));
    }

    /**
     * 导出
     */
    @RequestMapping(value = "/toExportTemplate", method = RequestMethod.GET)
    @ApiOperation("导出采集终端")
    public void toExportTemplate(DeviceCollectVo vo, HttpServletResponse response) {
        deviceCollectService.exportDeviceCollect(vo, response);
    }

    /**
     * @Description: 设备监控
     * @param: [areaId, name, type]
     * @return: com.jsdc.iotpt.vo.ResultInfo
     * @author: 苹果
     * @date: 2023/7/26
     * @time: 9:27
     */
    @PostMapping("/deviceMonitoring")
    @ApiOperation("设备监控")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaId",value = "区域",dataType = "Integer"),
            @ApiImplicitParam(name = "floorId",value = "楼层",dataType = "Integer"),
            @ApiImplicitParam(name = "buildId",value = "建筑",dataType = "Integer"),
            @ApiImplicitParam(name = "name",value = "设备名称",dataType = "String"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "Integer"),
            @ApiImplicitParam(name = "onLineStatus",value = "设备状态",dataType = "Integer"),
            @ApiImplicitParam(name = "pageIndex",value = "当前页数",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "显示条数",dataType = "Integer")
    })
    public ResultInfo deviceMonitoring(Integer onLineStatus, Integer areaId, Integer floorId, Integer buildId, String name,
                                       Integer deviceType, @RequestParam(name = "page") Integer pageIndex, @RequestParam(name = "limit") Integer pageSize,String id) {
        Page<DeviceCollectVo> deviceCollectVoPage = deviceCollectService.deviceMonitoring(onLineStatus, areaId, floorId, buildId, name,
                deviceType, pageIndex, pageSize, null, null, null, null, null,id,null,null);
        return ResultInfo.success(deviceCollectVoPage);
    }

    /**
     * @Description: 设备监控--信号上报
     * @param: [type, id]
     * @return: com.jsdc.iotpt.vo.ResultInfo
     * @author: 苹果
     * @date: 2023/7/26
     * @time: 9:55
     */
    @PostMapping("/getInformationReporting")
    @ApiOperation("设备监控--信号上报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "typeId",value = "设备类型id",dataType = "Integer")
    })
    public ResultInfo getInformationReporting(Integer typeId,Integer id) throws ParseException {
        return ResultInfo.success(dataSheetService.getInformationReporting(typeId, id));
    }

    @PostMapping("/getReportingList")
    @ApiOperation("设备监控--信号上报详情趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "String"),
            @ApiImplicitParam(name = "start",value = "开始时间",dataType = "String"),
            @ApiImplicitParam(name = "end",value = "结束时间",dataType = "String"),
            @ApiImplicitParam(name = "index",value = "当前页",dataType = "Integer"),
            @ApiImplicitParam(name = "size",value = "条数",dataType = "Integer")
    })
    public ResultInfo getReportingList(Integer id,String type,String start,String end,
                                       Integer index,Integer size) throws ParseException {
        if (StringUtils.isNull(index)||StringUtils.isNull(size)){
            return  ResultInfo.error("参数验证失败");
        }
        return ResultInfo.success(dataSheetService.getReportingList(id, type,start,end,index,size));
    }


    /**
     * 导出数据模板
     *
     * @return
     */
    @GetMapping("/downloadTemplate")
    @ApiOperation("下载数据模板")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        deviceCollectService.downloadTemplate(response);

    }

    /**
     * 上传数据模板
     *
     * @param files
     * @return
     */
    @PostMapping("/importUploadData")
    @ApiOperation("上传数据模板")
//    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "数据模板文件", dataType = "MultipartFile")
    })
    public ResultInfo importUploadData(@RequestParam(value = "file", required = false) List<MultipartFile> files) throws Exception {
        try {
            return deviceCollectService.importUploadData(files);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error(e.getMessage());
        }
    }


}
