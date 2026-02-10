package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.service.SysBuildService;
import com.jsdc.iotpt.service.SysFileService;
import com.jsdc.iotpt.service.SysLogService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/sys_build")
public class SysBuildController {
    @Autowired
    private SysBuildService sysBuildService;
    @Autowired
    private SysFileService sysFileService;

    /**
     * 楼宇新增
     * Author wzn
     * Date 2023/5/9 9:14
     */
    @PostMapping("/addBuild")
    @LogInfo(value = LogEnums.LOG_BUILD_ADD, model = Constants.MODEL_YYZT)
    public ResultInfo addBuild(@RequestBody SysBuild sysBuild) {
        sysBuildService.addSysBuild(sysBuild);
        return ResultInfo.success();
    }


    /**
     * 楼宇修改
     * Author wzn
     * Date 2023/5/9 9:15
     */
    @PostMapping("/updateBuild")
    @LogInfo(value = LogEnums.LOG_BUILD_UPDATE, model = Constants.MODEL_YYZT)
    public ResultInfo updateBuild(@RequestBody SysBuild sysBuild) {
        sysBuildService.updateSysBuild(sysBuild);
        return ResultInfo.success();
    }

    /**
     * 楼宇删除
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/delBuild")
    @LogInfo(value = LogEnums.LOG_BUILD_DELETE, model = Constants.MODEL_YYZT)
    public ResultInfo delBuild(Integer id) {

        //查是否有楼层
        if(sysBuildService.exist(id+"")){
            throw new CustomException("请先删除楼层!") ;
        }
        SysBuild sysBuild1 = new SysBuild();
        sysBuild1.setId(id);
        sysBuild1.setIsDel(1);
        sysBuildService.updateById(sysBuild1);
        return ResultInfo.success();
    }

    /**
     * 楼宇列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/selectBuildList")
    public ResultInfo selectBuildList(@RequestBody SysBuild sysBuild) {
        Page<SysBuild> sysBuildPage = sysBuildService.selectBuildList(sysBuild);
        if (!CollectionUtils.isEmpty(sysBuildPage.getRecords())) {
            for (SysBuild s : sysBuildPage.getRecords()) {
                if (s.getDictBuilding() == 0) {
                    s.setParentName("");
                    s.setDictBuilding(null);
                } else {
                    SysBuild sysBuild1 = sysBuildService.getById(s.getDictBuilding());
                    if (null != sysBuild1) {
                        s.setParentName(sysBuild1.getBuildName());
                    }
                }
            }
        }
        return ResultInfo.success(sysBuildPage);
    }

    @PostMapping("/allBuildList")
    public ResultInfo allBuildList() {
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<SysBuild> sysBuildList = sysBuildService.list(queryWrapper);
        return ResultInfo.success(sysBuildList);
    }

    @PostMapping("/getList")
    public ResultInfo getList() {
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<SysBuild> sysBuildList = sysBuildService.list(queryWrapper);
        return ResultInfo.success(sysBuildList);
    }

    /**
     * 楼宇详情
     * Author wzn
     * Date 2023/5/9 9:18
     */
    @PostMapping("/info")
    public ResultInfo info(String id) {
        JSONObject jsonObject = new JSONObject();
        SysBuild sysBuild = sysBuildService.info(id);
        List<SysFile> fileList = sysFileService.list(new QueryWrapper<SysFile>().eq("bizType","1").eq("bizId",String.valueOf(sysBuild.getId())));
        jsonObject.put("sysBuild",sysBuild);
        jsonObject.put("fileList",fileList);
        return ResultInfo.success(jsonObject);
    }

    /**
    *楼宇,楼层树形图接口
    * Author wzn
    * Date 2023/6/29 11:35
    */
    @PostMapping("/bulidTreeList")
    public ResultInfo bulidTreeList() {
        return ResultInfo.success( sysBuildService.bulidTreeList());
    }


}
