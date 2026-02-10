package com.jsdc.iotpt.controller;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.service.DeviceGatewayService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceGatewayVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/deviceGateway")
@Api(tags = "网关设备")
public class DeviceGatewayController {

    @Autowired
    DeviceGatewayService deviceGatewayService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @PostMapping("/getPageList")
    @ApiOperation("查询网关设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "设备名称", dataType = "String"),
            @ApiImplicitParam(name = "supplierId", value = "供应商", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "页数", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "条数", dataType = "Integer")
    })
    public ResultInfo getPageList(DeviceGatewayVo bean) {
        return ResultInfo.success(deviceGatewayService.getPageList(bean));
    }

    @PostMapping("/getList")
    @ApiOperation("网关设备列表")
    public ResultInfo getList(DeviceGatewayVo bean) {
        return ResultInfo.success(deviceGatewayService.getList(bean));
    }


    /**
     * 添加 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/add")
    @LogInfo(LogEnums.LOG_DEVICE_GATEWAY)
    @ApiOperation("添加网关设备")
    public ResultInfo add(@RequestBody DeviceGatewayVo bean) {
        if (StringUtils.isNull(bean.getSubscribeTopicId()) && StringUtils.isNull(bean.getAlarmTopicId()) && StringUtils.isNull(bean.getHeartTopicId()) && StringUtils.isNull(bean.getPublishTopicId())) {
            return ResultInfo.error("至少选择一个订阅主题");
        }
        return deviceGatewayService.add(bean);
    }

    /**
     * 编辑 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/edit")
    @LogInfo(LogEnums.LOG_DEVICE_GATEWAY)
    @ApiOperation("编辑网关设备")
    public ResultInfo edit(@RequestBody DeviceGatewayVo bean) {
        if (StringUtils.isNull(bean.getSubscribeTopicId()) && StringUtils.isNull(bean.getAlarmTopicId()) && StringUtils.isNull(bean.getHeartTopicId()) && StringUtils.isNull(bean.getPublishTopicId())) {
            return ResultInfo.error("至少选择一个订阅主题");
        }
        return deviceGatewayService.edit(bean);
    }

    @PostMapping("/getEntity")
    @ApiOperation("查看网关设备")
    @ApiImplicitParam(name = "id", value = "设备id", dataType = "Integer")
    public ResultInfo getEntity(Integer id) {
        return ResultInfo.success(deviceGatewayService.getEntityById(id));
    }

    @PostMapping("/delDeviceGateway")
    @LogInfo(value = LogEnums.LOG_DEVICE_GATEWAY, model = Constants.MODEL_YYZT)
    @ApiOperation("删除网关设备")
    @ApiImplicitParam(name = "id", value = "设备id", dataType = "Integer")
    public ResultInfo delDeviceGateway(Integer id) {
        return deviceGatewayService.delDeviceGateway(id);
    }


    /**
     * 上传数据模板
     *
     * @param files
     * @return
     */
    @PostMapping("/uploadFile")
    @ApiOperation("上传数据模板")
    @LogInfo(value = LogEnums.LOG_DEVICE_GATEWAY, model = Constants.MODEL_YYZT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "数据模板文件", dataType = "MultipartFile"),
            @ApiImplicitParam(name = "id", value = "网关设备id", dataType = "Integer")
    })
    public ResultInfo uploadFile(@RequestParam(value = "file", required = false) List<MultipartFile> files,
                                 Integer id) throws Exception {
        return deviceGatewayService.uploadFile(files, id);
    }


    /**
     * 导出数据模板
     *
     * @param id
     * @return
     */
    @PostMapping("/exportFile")
    @ApiOperation("导出数据模板")
    @LogInfo(value = LogEnums.LOG_DEVICE_GATEWAY, model = Constants.MODEL_YYZT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "网关设备id", dataType = "Integer")
    })
    public void exportFile(Integer id, HttpServletResponse response) throws Exception {
        deviceGatewayService.exportFile(id, response);

    }

    /**
     * 导出数据模板
     *
     * @return
     */
    @PostMapping("/downloadTemplate")
    @ApiOperation("下载数据模板")
    @LogInfo(value = LogEnums.LOG_DEVICE_GATEWAY, model = Constants.MODEL_YYZT)
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        deviceGatewayService.downloadTemplate(response);

    }

    /**
     * 上传数据模板
     *
     * @param files
     * @return
     */
    @PostMapping("/importUploadData")
    @ApiOperation("上传数据模板")
    @LogInfo(value = LogEnums.LOG_DEVICE_GATEWAY, model = Constants.MODEL_YYZT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "数据模板文件", dataType = "MultipartFile")
    })
    public ResultInfo importUploadData(@RequestParam(value = "file", required = false) List<MultipartFile> files) throws Exception {
        try {
            return deviceGatewayService.importUploadData(files);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
    }
}
