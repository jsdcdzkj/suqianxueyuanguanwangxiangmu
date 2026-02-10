package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSON;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.HttpUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.service.ConfigTopicService;
import com.jsdc.iotpt.vo.ConfigTopicVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @authon thr
 * @describe 主题管理
 */
@RestController
@RequestMapping("configTopic")
@Api(tags = "主题管理")
public class ConfigTopicController {

    @Autowired
    private ConfigTopicService configTopicService;

    @Value("${linkmqtt.ip}")
    private String mqtt_ip;


    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("主题管理-分页查询")
    public ResultInfo getPageList(ConfigTopicVo bean) {
        return ResultInfo.success(configTopicService.getPageList(bean));
    }

    /**
     * 主题管理列表 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("主题管理列表")
    public ResultInfo getList(ConfigTopicVo bean) {
        return ResultInfo.success(configTopicService.getList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑主题")
    @LogInfo(LogEnums.LOG_CONFIG_TOPIC)
    public ResultInfo saveOrUpdateConfigProtocol(ConfigTopicVo bean) {
        return configTopicService.saveOrUpdateConfigTopic(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("主题详情")
    public ResultInfo getEntity(ConfigTopicVo bean) {
        return configTopicService.getEntityById(bean.getId());
    }


    /**
     * 动态订阅主题
     */
    @PostMapping("addTopic")
    public ResultInfo addTopic(ConfigTopicVo bean) {
        String param = "linkId=" + bean.getLinkId() + "&topicId=" + bean.getId();
        String result = HttpUtils.sendPost( mqtt_ip+ "/mqtt/addTopic", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
        if (resultInfo.getCode() == 0) {
            //主题状态 0：禁用 1:启用
            bean.setTopicStatus(1);
            configTopicService.updateById(bean);
        }
        return resultInfo;
    }

    /**
     * 动态移除主题
     */
    @PostMapping("removeTopic")
    public ResultInfo removeTopic(ConfigTopicVo bean) {
        String param = "linkId=" + bean.getLinkId() + "&topicId=" + bean.getId();
        String result = HttpUtils.sendPost(mqtt_ip + "/mqtt/removeTopic", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
        if (resultInfo.getCode() == 0) {
            //主题状态 0：禁用 1:启用
            bean.setTopicStatus(0);
            configTopicService.updateById(bean);
        }
        return resultInfo;
    }

    /**
     * 批量停用
     */
    @PostMapping("runStops")
    public ResultInfo runStops(ConfigTopicVo bean) {
        String param = "idList=" + bean.getIdList() + "&linkIdList=" + bean.getLinkIdList();
        String result = HttpUtils.sendPost(mqtt_ip + "/mqtt/batchRemoveTopic", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
//        if (resultInfo.getCode() == 0) {
//            //主题状态 0：禁用 1:启用
//            configTopicService.runStops(bean);
//            resultInfo.setMsg("停用成功");
//        }
        return resultInfo;
    }

    /**
     * 批量启用
     */
    @PostMapping("runStarts")
    public ResultInfo runStarts(ConfigTopicVo bean) {
        String param = "idList=" + bean.getIdList() + "&linkIdList=" + bean.getLinkIdList();
        String result = HttpUtils.sendPost(mqtt_ip+ "/mqtt/batchAddTopic", param);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
//        if (resultInfo.getCode() == 0) {
//            //主题状态 0：禁用 1:启用
//            configTopicService.runStarts(bean);
//            resultInfo.setMsg("启用成功");
//        }
        return resultInfo;
    }

}
