package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.HttpUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ConfigLink;
import com.jsdc.iotpt.service.ConfigDataTransferService;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigDataTransferVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/configDataTransfer")
@Api(tags = "数据转换模板管理")
public class ConfigDataTransferController {

    @Autowired
    ConfigDataTransferService configDataTransferService;

    @Value("${linkmqtt.ip}")
    private String mqtt_ip;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @PostMapping("/getPageList")
    @ApiOperation("查询数据转换模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "模板名称", dataType = "String"),
            @ApiImplicitParam(name = "modelCode", value = "模板编码", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "页数", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "条数", dataType = "Integer")
    })
    public ResultInfo getPageList(ConfigDataTransferVo bean) {
        return ResultInfo.success(configDataTransferService.getPageList(bean));
    }

    /**
     * 数据转换模板列表 todo
     *
     * @param vo
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("数据转换模板列表")
    public ResultInfo getList(ConfigDataTransferVo vo) {
        return ResultInfo.success(configDataTransferService.getList(vo));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑数据转换模板")
    @LogInfo(LogEnums.LOG_CONFIG_TRANSFER)
    public ResultInfo saveOrUpdateConfigDataTransfer(@RequestBody ConfigDataTransferVo bean) {
        return configDataTransferService.saveOrUpdateConfigDataTransfer(bean);
    }

    @RequestMapping("/getEntity")
    @ApiOperation("数据转换模板详情")
    public ResultInfo getEntity(ConfigDataTransferVo bean) {
        return configDataTransferService.getEntityById(bean.getId());
    }

    /**
     * 删除 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/del")
    @ApiOperation("删除数据转换模板")
    @LogInfo(LogEnums.LOG_CONFIG_TRANSFER)
    public ResultInfo delConfigDataTransfer(ConfigDataTransferVo bean) {
        return configDataTransferService.delConfigDataTransfer(bean);
    }

    /**
     * 获取mqtt数据
     */
    @PostMapping("getDatas")
    @ApiOperation("获取mqtt数据")
    public String getDatas(@RequestBody Map<String, Object> params) {
        Integer linkId = (Integer) params.get("linkId");
        String type = (String) params.get("type");
        String topic = (String) params.get("topic");
        Integer online = (Integer) params.get("online");
//        请求参数应该是 name1=value1&name2=value2 的形式。
        String param = "linkId=" + linkId + "&type=" + type + "&topic=" + topic + "&online=" + online;
        return HttpUtils.sendPost(mqtt_ip + "/sysConfig/online.do", param);
    }

    /**
     * 数据解析
     */
    @PostMapping("parseTest")
    @ApiOperation("数据解析")
    public ResultInfo parseTest(@RequestBody ConfigDataTransferVo bean) {
        System.out.println("==============");
        System.out.println(bean.getJsonData());



        return configDataTransferService.parseTest(bean);
    }
}
