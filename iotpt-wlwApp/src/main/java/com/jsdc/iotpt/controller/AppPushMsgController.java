package com.jsdc.iotpt.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.common.NoticePushMsgVo;
import com.jsdc.iotpt.service.AppPushMsgService;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APP消息推送
 *
 * @author wr
 * @date 2024/11/10
 */
@RestController
@RequestMapping("/app/message/push")
@Api(tags = "APP消息推送")
public class AppPushMsgController {

    @Autowired
    private AppPushMsgService appPushMsgService;

    /**
     * @Description: 用车消息推送
     */
    @ApiOperation("用车消息推送")
    @RequestMapping("/dcycPush")
    public ResultInfo dcycPush(@RequestBody AppPushMsgVo data) {
        appPushMsgService.dcycPush(data);
        return ResultInfo.success();
    }

    /**
     * @Description: 固定资产消息推送
     */
    @ApiOperation("固定资产消息推送")
    @PostMapping("/rfidPush")
    public ResultInfo rfidPush(@RequestBody String json) {
//        appPushMsgService.dcycPush(data);
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(json);
        NoticePushMsgVo data = jsonObject.get("data", NoticePushMsgVo.class);
        if (null == data) {
            return ResultInfo.error("参数错误");
        }

        AppPushMsgVo appPushMsgVo = new AppPushMsgVo();
        appPushMsgVo.setTitle(data.getTitle());
//        appPushMsgVo.setContent(data.getContent());
        appPushMsgVo.setName(data.getName());
        appPushMsgVo.setSending_phone(data.getSending_phone());
        appPushMsgVo.setAppConfigCode(data.getMenu_code());
        appPushMsgVo.setReasons(data.getResons());
        appPushMsgVo.setType(data.getType());
        appPushMsgService.dcycRfid(appPushMsgVo);
        return ResultInfo.success();
    }

    /**
     * itss响应推送
     */
    @ApiOperation("itss响应推送")
    @RequestMapping("/itssResponsePush")
    public ResultInfo itssResponsePush(@RequestBody JSONObject data) {
        appPushMsgService.itssPush(data, "ITSS_REPAIRS_NOTICE");
        return ResultInfo.success();
    }

    /**
     * itss响应推送
     */
    @ApiOperation("itss超时推送")
    @RequestMapping("/itssTimeoutPush")
    public ResultInfo itssTimeoutPush(@RequestBody JSONObject data) {
        appPushMsgService.itssPush(data, "ITSS_REPAIRS_TIMEOUT");
        return ResultInfo.success();
    }

    /**
     * 工时系统 任务推送
     */
    @ApiOperation("工时系统任务推送")
    @RequestMapping("/taskPush")
    public ResultInfo taskPush(@RequestBody AppPushMsgVo data) {
        appPushMsgService.taskPush(data);
        return ResultInfo.success();
    }


}
