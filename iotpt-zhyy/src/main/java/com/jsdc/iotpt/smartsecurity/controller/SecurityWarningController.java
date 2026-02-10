package com.jsdc.iotpt.smartsecurity.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.service.MissionService;
import com.jsdc.iotpt.service.WarnSheetService;
import com.jsdc.iotpt.service.WarningConfigService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarnSheetVo;
import com.jsdc.iotpt.vo.WarningConfigVo;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author lb
 * @Date 2023/8/1 10:58
 * @Description 智慧安防 告警中心
 **/

@RestController
@RequestMapping("/securityWarning")
public class SecurityWarningController {


    @Autowired
    private WarnSheetService warnSheetService;


    @Autowired
    private MissionService missionService ;


    @Autowired
    private WarningConfigService warningConfigService;

    /**
     * 告警重点设备排行
     * 将大楼内所有设备的报警数量值倒序排列，取前5个设备用横柱图按顺序展示（设备+告警数值）
     *
     * @return
     */
    @RequestMapping("/getCollectWarningTop.do")
    @ApiOperation("告警信息分页查询 ")
    public ResultInfo getCollectWarningTop() {
        return ResultInfo.success(warnSheetService.getCollectWarningTop());
    }

    /**
     * 实时告警列表
     * 底部实时告警列表区域，展示所有来源的设备告警信息，包括告警对象、告警位置、告警等级、告警来源、告警时间、告警内容等信息。告警消息列表动态向上滚动显示。
     * 弹窗提示：新的线上告警产生，可在右下角弹窗提示(弹窗内容：告警对象、告警位置、告警时间、告警等级)，同时可发出声光告警提示。
     *
     * @return
     */
    @RequestMapping("/getRealTimeWarning.do")
    @ApiOperation("告警信息分页查询 ")
    public ResultInfo getRealTimeWarning() {
        return ResultInfo.success(warnSheetService.getRealTimeWarning(new WarnSheetVo()));
    }

    /**
     * 重点告警区域
     * 将大楼内所有区域内设备的报警数量值倒序排列，取前5个区域用柱形图按顺序展示（区域+告警数值）
     *
     * @return
     */
    @RequestMapping("/getKeyAreaWarning.do")
    @ApiOperation("告警信息分页查询 ")
    public ResultInfo getKeyAreaWarning() {
        return ResultInfo.success(warnSheetService.getKeyAreaWarning());
    }




    /**
     * 告警等级统计
     * Author wzn
     * Date 2023/7/21 9:03
     */
    @RequestMapping("/warnList")
    @ApiOperation("告警等级统计 ")
    public ResultInfo warnList() {
        return ResultInfo.success(warnSheetService.warnList());
    }








}
