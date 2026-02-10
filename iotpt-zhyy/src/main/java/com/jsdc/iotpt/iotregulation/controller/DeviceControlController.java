package com.jsdc.iotpt.iotregulation.controller;

import com.jsdc.iotpt.model.DeviceControl;
import com.jsdc.iotpt.service.DeviceControlService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.DeviceControlVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: DeviceControlController
 * Description:
 * date: 2023/12/27 9:51
 *
 * @author bn
 */
@RestController
@RequestMapping("/deviceControl")
@Api(tags = "远程控制")
public class DeviceControlController {


    @Autowired
    private DeviceControlService controlService;


    @PostMapping("/getList")
    public ResultInfo getList(DeviceControl control){

        try {
            return ResultInfo.success(controlService.getList(control));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    @PostMapping("/isControl")
    public ResultInfo isControl(DeviceControlVo bean){

        try {
            return ResultInfo.success(controlService.isControl(bean));
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }



}
