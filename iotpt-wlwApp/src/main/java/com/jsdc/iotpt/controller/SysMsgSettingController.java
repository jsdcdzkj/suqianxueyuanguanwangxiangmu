package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.sys.SysMessage;
import com.jsdc.iotpt.model.sys.SysMsgSetting;
import com.jsdc.iotpt.service.SysMsgSettingService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: SysMsgSettingController
 * Description:
 * date: 2024/11/26 9:25
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/msgSet")
@Api(tags = "系统消息")
public class SysMsgSettingController {

    @Autowired
    public SysMsgSettingService msgSettingService;


    @GetMapping("getSysMsgSet.do")
    @ApiOperation(value = "消息配置")
    public ResultInfo getSysMsgSet() {

        try {
            return ResultInfo.success(msgSettingService.getSysMsgSet());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取消息设置失败");
        }
    }


    @PostMapping("editSysMsgSet.do")
    @ApiOperation(value = "消息设置")
    public ResultInfo editSysMsgSet(@RequestBody SysMsgSetting bean) {


        try {
            return msgSettingService.editSysMsgSet(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取消息设置失败");
        }
    }



}
