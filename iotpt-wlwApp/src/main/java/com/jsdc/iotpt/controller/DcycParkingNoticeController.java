package com.jsdc.iotpt.controller;


import com.jsdc.iotpt.service.DcycParkingNoticeService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/dcyc/parking/notice")
@Api(tags = "APP消息配置")
public class DcycParkingNoticeController {

    @Autowired
    private DcycParkingNoticeService dcycParkingNoticeService;


    @GetMapping("byId")
    public ResultInfo getById(@RequestParam Integer id) {
        return ResultInfo.success(dcycParkingNoticeService.getById(id));
    }

}
