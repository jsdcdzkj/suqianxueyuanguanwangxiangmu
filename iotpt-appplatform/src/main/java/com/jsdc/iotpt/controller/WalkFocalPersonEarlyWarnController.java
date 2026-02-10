package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.WalkActionLocus;
import com.jsdc.iotpt.model.WalkFocalPersonEarlyWarn;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.service.WalkActionLocusService;
import com.jsdc.iotpt.service.WalkEquipmentService;
import com.jsdc.iotpt.service.WalkFocalPersonEarlyWarnService;
import com.jsdc.iotpt.vo.ResultInfo;

import com.jsdc.iotpt.vo.WalkActionLocusVo;
import com.jsdc.iotpt.vo.WalkFocalPersonEarlyWarnVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 *  预警
 */
@Controller
@RequestMapping("warn")
public class WalkFocalPersonEarlyWarnController {

    @Autowired
    private WalkFocalPersonEarlyWarnService focalPersonEarlyWarnService;

    @Autowired
    private WalkActionLocusService actionLocusService;


    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WalkEquipmentService equipmentService;




    /**
     * 查询
     * @param focalPersonEarlyWarn
     * @return
     */
    @RequestMapping(value = "find.do",method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo find(WalkFocalPersonEarlyWarnVo focalPersonEarlyWarn){
        Page<WalkFocalPersonEarlyWarn> warnList=focalPersonEarlyWarnService.findAll(focalPersonEarlyWarn);
        return ResultInfo.success(warnList);
    }


    /**
     *  查询滞留人员
     */
    @RequestMapping(value = "retentionPeople.do",method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo retentionPeople(String realName,Integer isFlag, String startTime,Integer deptId,
                                      @RequestParam(defaultValue = "1",required = false) Integer pageIndex,
                                      @RequestParam(defaultValue = "10",required = false) Integer pageSize){

        if (Strings.isEmpty(startTime)){
            startTime= DateUtils.toDay("yyyy-MM-dd");
        }

        SysUser sysUser=sysUserService.getUser();
        if (deptId==null){
            deptId=sysUser.getDeptId();
        }

        List<WalkActionLocusVo> actionLocusVoList=actionLocusService.retentionPeople(realName,isFlag,startTime,deptId);



        return ResultInfo.success(actionLocusVoList);
    }



    @RequestMapping(value = "retentionPeopleCount.do")
    @ResponseBody
    public ResultInfo retentionPeopleCount(){
        SysUser sysUser=sysUserService.getUser();

        Map<String, List<WalkActionLocusVo>> map= actionLocusService.retentionPeopleInfo(DateUtils.toDay("yyyy-MM-dd"),
                sysUser.getDeptId());

        return ResultInfo.success(map.size());
    }

    @RequestMapping(value = "retentionEnd.do")
    @ResponseBody
    public ResultInfo retentionEnd(){

        SysUser sysUser=sysUserService.getUser();

        List<WalkActionLocusVo> locusVos= actionLocusService.retentionEnd(DateUtils.toDay("yyyy-MM-dd"),
                sysUser.getDeptId());

//        PageInfo pageInfo = PageInfoUtils.list2PageInfo(locusVos, pageIndex, pageSize);

        return ResultInfo.success(locusVos);

    }





    /**
     *  处置滞留人员
     * @return
     */
    @RequestMapping(value = "retentionUpdate.do",method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo retentionUpdate(WalkActionLocus actionLocus){

        List<WalkActionLocus> actionLocusData=actionLocusService.selectToDayById(actionLocus.getId());
        actionLocusData.forEach(x->{
            x.setIsFlag(actionLocus.getIsFlag());
            actionLocusService.updateById(x);
        });

        return ResultInfo.success();
    }





}
