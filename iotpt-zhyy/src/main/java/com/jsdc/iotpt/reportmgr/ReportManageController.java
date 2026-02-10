package com.jsdc.iotpt.reportmgr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.ReportManage;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/reportManage")
@Api(tags = "报告接口")
public class ReportManageController {

    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private SysOrgManageService sysOrgManageService;


    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private WarnSheetService warnSheetService;
    @Autowired
    private SmartEnergyReportService smartEnergyReportService;


    @PostMapping("baseInfo")
    @ApiOperation(value = "项目概括-基本信息",notes = "areaIds 必填；splType 必填")
    public ResultInfo baseInfo(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.baseInfo(bean));
        } catch (Exception e) {
            throw new CustomException("获取项目概括-基本信息失败");
        }
    }
    @PostMapping("cmdInfo")
    @ApiOperation(value = "项目概括-终端信息",notes = "areaIds 必填；")
    public ResultInfo cmdInfo(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.cmdInfo(bean));
        } catch (Exception e) {
            throw new CustomException("获取项目概括-终端信息失败");
        }
    }

    @PostMapping("missionInfo")
    @ApiOperation(value = "巡检运维情况-工单情况",notes = "areaIds必填")
    public ResultInfo missionInfo(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.missionInfo(bean));
        } catch (Exception e) {
            throw new CustomException("获取巡检运维情况-工单情况失败");
        }
    }

    @PostMapping("taskInfo")
    @ApiOperation(value = "巡检运维情况-巡检记录",notes = "areaIds必填")
    public ResultInfo taskInfo(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.taskInfo(bean));
        } catch (Exception e) {
            throw new CustomException("获取巡检运维情况-巡检记录失败");
        }
    }

    @PostMapping("taskErrorInfo")
    @ApiOperation(value = "巡检运维情况-巡检异常情况",notes = "areaIds必填")
    public ResultInfo taskErrorInfo(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.taskErrorInfo(bean));
        } catch (Exception e) {
            throw new CustomException("获取巡检运维情况-巡检异常情况失败");
        }
    }

    @PostMapping("getAreaLoad")
    @ApiOperation(value = "用能统计-负载率分析",notes = "areaIds必填；timeType 必填")
    public ResultInfo getAreaLoad(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.getAreaLoad(bean));
        } catch (Exception e) {
            throw new CustomException("用能统计-负载率分析失败");
        }
    }

    /**
     *
     * @param bean
     * @return
     */
    @PostMapping("energyInfo")
    @ApiOperation(value = "用能统计-电能统计",notes = "timeType 必填；日（yyyy-MM-dd）月（yyyy-MM）年 （yyyy）")
    public ResultInfo energyInfo(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.energyInfo(bean));
        } catch (Exception e) {
            throw new CustomException("用能统计-电能统计失败");
        }
    }


    /**
     *
     * @param bean
     * @return
     */
    @PostMapping("getDeviceCollectTop")
    @ApiOperation(value = "用能统计-负载排行",notes = "areaIds必填，timeType 必填；日（yyyy-MM-dd）月（yyyy-MM）年 （yyyy）")
    public ResultInfo getDeviceCollectTop(@RequestBody ReportManageVo bean){
        try {
            return ResultInfo.success(reportManageService.getDeviceCollectTop(bean));
        } catch (Exception e) {
            throw new CustomException("用能统计-负载排行失败");
        }
    }

    @PostMapping("getPage")
    @ApiOperation(value = "报告管理分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo getPage(ReportManage bean, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {

        try {
            return ResultInfo.success(reportManageService.getPage(bean, pageNo, pageSize));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }



    @PostMapping("deleteReport")
    @ApiOperation("删除报告")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "报告ID", dataType = "int")
    })
    public ResultInfo deleteReport(Integer id) {
        try {
            return reportManageService.deleteReport(id);
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }


    /**
     * 下载
     * @param id
     * @param response
     */
    @RequestMapping("/downOne")
    @ApiOperation("下载（流）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "报告ID", dataType = "int")
    })
    public void downOne(@RequestParam Integer id,final HttpServletResponse response){
        try {
            ReportManage r =reportManageService.selectOneInfo(id);
            @Cleanup InputStream inputStream = minioTemplate.getObject(G.MINIO_BUCKET, r.getMinioFile());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            byte[] data = os.toByteArray();
            response.resetBuffer();
            response.resetBuffer();
            response.setHeader("Content-Disposition", "attachment");
            response.setHeader("Content-Disposition", "attachment;filename=" + r.getMinioFile());
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            IOUtils.write(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("操作失败");
        }
    }


    /**
     * 预览
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping("/previewCheck")
    @ApiOperation("预览（流）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "报告ID", dataType = "int")
    })
    public void previewCheck(@RequestParam Integer id, HttpServletResponse response) throws
            IOException {
        try {
            ReportManage r =reportManageService.selectOneInfo(id);
            @Cleanup InputStream is = minioTemplate.getObject(G.MINIO_BUCKET, r.getMinioFile());
            // 清空response
            response.reset();
            //2、设置文件下载方式
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/pdf");
            OutputStream outputStream = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((count = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("预览失败");
        }
    }

    /**
     * 查看,预览下载
     * @param id
     * @return
     */
    @RequestMapping("/getOneInfo")
    @ApiOperation("查看,预览,下载(路径)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "报告ID", dataType = "int")
    })
    public ResultInfo getOneInfo(Integer id){
        try {
            ReportManage r =reportManageService.selectOneInfo(id);
            String lookUrl = "/minio/previewCheck?fileName="+r.getMinioFile();
            String downUrl = "/minio/downMinio?fileName="+r.getMinioFile();
            Map<String,String> map = new HashMap<>();
            map.put("lookUrl",lookUrl);
            map.put("downUrl",downUrl);
            return ResultInfo.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("操作失败");
        }
    }


    /**
     * 新增
     * @param
     * @param file
     * @return
     */
    @RequestMapping("/addReport")
    @ApiOperation(value="新增报告",notes = "body: reportName(报告名称)， starTime（开始时间 yyyy-MM-dd string）, endTime（结束时间 yyyy-MM-dd string）， reportType（选择类型（0 年 1月 2日）， chooseTemple（选择模板ID）,areaIds(选择商户Id集合)）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="body",value = "body", dataType = "string"),
            @ApiImplicitParam(name="file",value="以MultipartFile格式",dataType = "multipartFile")
    })
    public ResultInfo addReport( ReportManage bean , @RequestPart("file")MultipartFile file){

        try {
            String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);
            String suffixName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = ymd + suffixName;
            minioTemplate.putObject(G.MINIO_BUCKET, filename, file.getInputStream());

            bean.setMinioFile(filename);

            Integer value = reportManageService.addReport(bean);
            if (1 == value){
                return ResultInfo.success();
            }
            throw new CustomException("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("上传文件错误!");
        }
    }

    /**
     * 多维分析树状图专用
     * 区域三级树形图
     */
    @PostMapping("/areaTreeList")
    @ApiOperation("多维分析树状图专用")
    public ResultInfo getAreaTree(){
        try {
            return ResultInfo.success(sysOrgManageService.areaTreeList3(null));
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }










    /**
     *
     *设备告警类型占比
     * @return
     */
    @RequestMapping(value = "/getWarningType.do",method = RequestMethod.POST)
    @ApiOperation(value="告警类型统计 ",notes = "vo:  startTime（开始时间 yyyy-MM-dd string）, endTime（结束时间 yyyy-MM-dd string）,areaIds(选择商户Id集合)）")
    public ResultInfo getWarningType(@RequestBody WarningConfigVo vo) {
        try {
            vo.setStartTime(vo.getStartTime()+" 00:00:00");
            vo.setEndTime(vo.getEndTime()+" 23:59:59");
            return ResultInfo.success(warnSheetService.getWarningType1(vo));
        } catch (Exception e) {
            throw new CustomException("告警类型统计失败");
        }
    }


    @RequestMapping("/analysis")
    @ApiOperation("能耗报表")
    public ResultInfo analysis(@RequestBody DeviceQueryVo queryVo) {
        try {
            return deviceCollectService.analysis(queryVo);
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }



    /**
     * 电能质量-三项不平衡统计
     */
    @RequestMapping(value = "/tripartiteImbalanceReport",method = RequestMethod.POST)
    @ApiOperation(value="三项不平衡统计",notes = "timeType: 5 , startTime：yyyy-MM-dd string ,endTime: yyyy-MM-dd string  ,deviceType: 1 电压 2 电流  , deviceIds: [] ")
    public ResultInfo tripartiteImbalanceReport(@RequestBody SmartEnergyReportVo vo) {
        try {
            return ResultInfo.success(smartEnergyReportService.tripartiteImbalanceReport(vo));
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }


    /**
     * 采集终端列表
     */
    @RequestMapping(value= "/getDeviceCollectList",method = RequestMethod.POST)
    @ApiOperation(value="采集终端列表",notes = "areaIds:[]")
    public ResultInfo getDeviceCollectList(@RequestBody DeviceCollectVo bean) {
        try {
            return ResultInfo.success(deviceCollectService.getList(bean));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


}
