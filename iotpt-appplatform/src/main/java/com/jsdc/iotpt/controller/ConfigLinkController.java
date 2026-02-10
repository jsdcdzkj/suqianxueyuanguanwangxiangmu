package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.HttpUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ConfigLink;
import com.jsdc.iotpt.service.ConfigLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigLinkVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @authon thr
 * @describe 连接管理
 */
@RestController
@RequestMapping("/configLink")
@Api(tags = "连接管理")
public class ConfigLinkController {

    @Autowired
    ConfigLinkService configLinkService;

    @Value("${linkmqtt.ip}")
    private String mqtt_ip;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("连接管理-分页查询")
    public ResultInfo getPageList(ConfigLinkVo bean) {
        return ResultInfo.success(configLinkService.getPageList(bean));
    }

    /**
     * 连接管理列表 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("连接管理列表")
    public ResultInfo getList(ConfigLinkVo bean) {
        return ResultInfo.success(configLinkService.getList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑连接")
    @LogInfo(LogEnums.LOG_CONFIG_LINK)
    public ResultInfo saveOrUpdateConfigLink(@RequestBody ConfigLinkVo bean) {
        return configLinkService.saveOrUpdateConfigLink(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("连接详情")
    public ResultInfo getEntity(ConfigLinkVo bean) {
        return configLinkService.getEntityById(bean.getId());
    }


    /**
     * 建立mqtt连接
     */
    @PostMapping("addMqttConnect")
    public ResultInfo addMqttConnect(ConfigLink configLink) {
//        请求参数应该是 name1=value1&name2=value2 的形式。
        String param = "id=" + configLink.getId();
        String result = HttpUtils.sendPost(mqtt_ip + "/mqtt/addMqttConnect", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
        if (resultInfo.getCode() == 0) {
            //连接状态 1断开 2连接
            configLink.setConnectionStatus(2);
            configLinkService.updateById(configLink);
        }
        return resultInfo;
    }

    /**
     * 移除mqtt连接
     */
    @PostMapping("removeMqttConnect")
    public ResultInfo removeMqttConnect(ConfigLink configLink) {
//        请求参数应该是 name1=value1&name2=value2 的形式。
        String param = "id=" + configLink.getId();
        String result = HttpUtils.sendPost(mqtt_ip + "/mqtt/removeMqttConnect", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
        if (resultInfo.getCode() == 0) {
            //连接状态 1断开 2连接
            configLink.setConnectionStatus(1);
            configLinkService.updateById(configLink);
        }
        return resultInfo;
    }

    /**
     * 测试mqtt连接
     */
    @PostMapping("testMqttConnect")
    public ResultInfo testMqttConnect(@RequestBody ConfigLinkVo configLink) {
        String param = "serviceAddress=" + configLink.getServiceAddress()
                + "&clientId=" + configLink.getClientId()
                + "&username=" + configLink.getUsername()
                + "&password=" + configLink.getPassword();

        String result = HttpUtils.sendPost(mqtt_ip + "/mqtt/testMqttConnect", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
        return resultInfo;
    }
}
