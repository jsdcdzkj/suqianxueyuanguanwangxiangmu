package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.SysConfig;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: SysConfigController
 * Description:
 * date: 2023/5/26 15:56
 *
 * @author bn
 */
@RequestMapping("sysConfig")
@Controller
public class SysConfigController {


    @RequestMapping(value = "online.do",method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo topicOnline(String linkId,String type,String topic,Integer online){

        SysConfig.getInstance().getTopicOnline().put("C"+linkId+"T"+type+topic,online==0);

        return ResultInfo.success();
    }

}
