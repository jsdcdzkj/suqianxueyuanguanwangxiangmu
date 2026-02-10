package com.jsdc.iotpt.smartsecurity.controller;


import cn.hutool.core.util.IdUtil;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.model.new_alarm.AlarmCategory;
import com.jsdc.iotpt.service.AlarmCategoryService;
import com.jsdc.iotpt.vo.AlarmCategoryVO;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/alarm/category")
@Api(tags = "告警内容-告警分类")
public class AlarmCategoryController {


    @Autowired
    private AlarmCategoryService alarmCategoryService;

    @Autowired
    private MinioTemplate minioTemplate;


    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo getPage(@RequestBody AlarmCategoryVO vo) {
        return ResultInfo.success(alarmCategoryService.getPage(vo));
    }


    @GetMapping("/list")
    @ApiOperation("告警类型列表查询")
    public ResultInfo getList() {
        return ResultInfo.success(alarmCategoryService.getList(new AlarmStatisticsVo()));
    }


    @PostMapping("/save")
    @ApiOperation("新增或编辑或删除")
    public ResultInfo save(@RequestBody AlarmCategory entity) {
        alarmCategoryService.save(entity);
        return ResultInfo.success();
    }

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public ResultInfo upload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if (file.getSize() > 512000) {
            return ResultInfo.error("文件过大，请压缩后再上传");
        }
        if (".jpg".equals(fileSuffix) || ".png".equals(fileSuffix)) {
            String newFilename = IdUtil.fastSimpleUUID() + fileSuffix;
            minioTemplate.putObject(G.MINIO_BUCKET, newFilename, file.getInputStream());
            return ResultInfo.success(newFilename);
        }else {
            return ResultInfo.error("文件格式错误");
        }
    }

}
