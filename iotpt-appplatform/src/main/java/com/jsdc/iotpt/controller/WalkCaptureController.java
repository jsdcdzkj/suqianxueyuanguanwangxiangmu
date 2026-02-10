package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WalkCaptureVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("walkCapture")
@Api(tags = "人脸抓拍库")
public class WalkCaptureController {

    @Autowired
    private WalkCaptureService captureService;
    @Autowired
    private WalkFocalPersonEarlyWarnService focalPersonEarlyWarnService;


    @RequestMapping("snapRecord.do")
    @ApiOperation("抓拍记录")
    public ResultInfo snapRecord(@RequestParam(defaultValue = "1") Integer pageIndex, @RequestParam(defaultValue = "10") Integer pageSize, WalkCaptureVo bean) {
        return ResultInfo.success(focalPersonEarlyWarnService.snapRecord(pageIndex, pageSize, bean));
    }


    @RequestMapping("captureIn.do")
    @ApiOperation("抓拍记录入库")
    public ResultInfo captureIn(MultipartFile image, String equipmentCode, WalkActionLocus actionLocus) {
        Integer count = captureService.captureIn(image, equipmentCode, actionLocus);
        if (count == -1) {
            return ResultInfo.error("抓拍记录失败！！");
        } else {
            return ResultInfo.success("抓拍记录成功");
        }
    }


    @RequestMapping("saveCapture.do")
    @ApiOperation("保存抓拍记录")
    public ResultInfo saveCapture(WalkPerson person, String base64) {
        return captureService.saveCapture(person, base64);
    }


    @RequestMapping("selectCaptureById.do")
    @ApiOperation("抓拍准备数据")
    public String selectCaptureById(Integer id) {
        return captureService.captureData(id);
    }


    @RequestMapping(value = "deleteById.do", method = RequestMethod.POST)
    @ApiOperation("删除数据")
    public ResultInfo updOrDel(WalkFocalPersonEarlyWarn focalPersonEarlyWarn) {
        return ResultInfo.success(focalPersonEarlyWarnService.updateById(focalPersonEarlyWarn));
    }
}
