package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.ReportRepair;
import com.jsdc.iotpt.service.ReportRepairService;
import com.jsdc.iotpt.service.SysFileService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ReportRepairVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上报报修
 * @author bn
 */
@RestController
@RequestMapping("/app/repair")
@Api(tags = "上报报修")
public class ReportRepairController {

    @Autowired
    private ReportRepairService reportRepairService;

    @Autowired
    private SysFileService sysFileService;



    @PostMapping("getPageList")
    @ApiOperation(value = "报修记录",
            httpMethod = "POST",
            notes = "pageNo(页数)，pageSize(每页条数)")
    public ResultInfo getPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                  ReportRepair bean){

        try {
            return ResultInfo.success(reportRepairService.getPageList(pageNo,pageSize,bean));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取报修记录失败");
        }
    }


    @PostMapping("saveReportRepair")
    @ApiOperation(value = "上报报修",
            httpMethod = "POST")
    public ResultInfo saveReportRepair(@RequestBody ReportRepairVo bean){

        try {
            return reportRepairService.saveReportRepair(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("上报报修失败");
        }
    }


    @PostMapping("uploadFile.do")
    @ApiOperation("文件上传功能")
    public ResultInfo uploadFile(@RequestParam("file") MultipartFile files) {
        try {
            return ResultInfo.success(sysFileService.uploadFiles(new UploadParams("/repair", files)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("文件上传失败");
        }
    }


    /**
     *  关联空间
     * @return
     */
    @PostMapping(value = "getTreeBuild")
    @ApiOperation("关联空间")
    public ResultInfo getTreeBuild(){


        try {
            return ResultInfo.success(reportRepairService.getTreeBuild());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("操作失败");
        }
    }






}
