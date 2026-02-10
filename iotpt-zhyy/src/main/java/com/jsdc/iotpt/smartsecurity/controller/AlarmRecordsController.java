package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.AlarmRecordsService;
import com.jsdc.iotpt.service.AlarmStatisticsService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.DcDifyUtils;
import com.jsdc.iotpt.vo.AlarmRecordsVo;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import com.jsdc.iotpt.vo.FireAlarmInfoVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/alarmRecords")
@Api(tags = "告警事件")
public class AlarmRecordsController {

    @Autowired
    AlarmRecordsService alarmRecordsService;
    @Autowired
    AlarmStatisticsService alarmStatisticsService;

    @Autowired
    SysUserService sysUserService;
    @Autowired
    DcDifyUtils dcDifyUtils;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("分页查询")
    public ResultInfo getPageList(@RequestBody AlarmRecordsVo bean) {
        return ResultInfo.success(alarmRecordsService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("XXXX")
    public ResultInfo saveOrUpdateAlarmRecords(AlarmRecordsVo bean) {
        return alarmRecordsService.saveOrUpdateAlarmRecords(bean);
    }

    /**
     * 查看详情
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("查看详情")
    public ResultInfo getEntity(AlarmRecordsVo bean) {
        return ResultInfo.success(alarmRecordsService.getEntityById(bean.getId()));
    }



    /**
     * 角标
     * @return
     */
    @RequestMapping("/getsubscript")
    @ApiOperation("获取角标")
    public ResultInfo getsubscript(@RequestBody AlarmRecordsVo bean){
        return ResultInfo.success(alarmRecordsService.getsubscript(bean));
    }

    /**
     * 统计数量根据程度
     * @return
     */

    @RequestMapping("/groupAlarmLevel")
    @ApiOperation("统计数量根据程度")
    public ResultInfo groupAlarmLevel(@RequestBody AlarmRecordsVo bean){
        return ResultInfo.success(alarmRecordsService.groupAlarmLevel(bean));
    }


    /**
     * 折线图统计
     * @return
     */

    @RequestMapping("/line")
    @ApiOperation("折线图统计")
    public ResultInfo line(@RequestBody AlarmRecordsVo bean){
        return ResultInfo.success(alarmRecordsService.line(bean));
    }

    /**
     * 批量处理
     * @return
     */

    @RequestMapping("/batchProcessing")
    @ApiOperation("批量处理")
    public ResultInfo batchProcessing(@RequestBody AlarmRecordsVo bean){
        alarmRecordsService.batchProcessing(bean);
        return ResultInfo.success();
    }



    /**
     * 导出
     */
    @RequestMapping(value = "/toExportTemplate", method = RequestMethod.GET)
    @ApiOperation("导出")
    public void toExportTemplate(AlarmRecordsVo bean, HttpServletResponse response) {
        alarmRecordsService.export(bean, response);
    }



    /**
     * 弹窗（最新一条重要告警）
     * @param
     * @return
     */
    @RequestMapping("/getNewEntity")
    @ApiOperation("弹窗")
    public ResultInfo getNewEntity() {
        return ResultInfo.success(alarmRecordsService.getNewEntity());
    }

    /**
     * 跑马灯
     * @param
     * @return
     */
    @RequestMapping("/lamp")
    @ApiOperation("跑马灯")
    public ResultInfo lamp(@RequestBody AlarmStatisticsVo arVo) {
        try {

            String defaultUser = "11";
            try {
                SysUser u = sysUserService.getUser();
                defaultUser = u.getRealName();
            } catch (Exception e) {
                defaultUser = "11";
            }
            //组装问题描述
            List<AlarmStatisticsVo> vos = alarmStatisticsService.pieChart(arVo);

            StringBuilder sb = new StringBuilder();
            sb.append("你是一个智慧园区系统的运维人员，平台现在接收到了众多告警信息，告警类型分为：人员告警、厨房告警、车辆告警、设备运维告警、消防告警、电气告警、门禁告警。告警等级分为：警告、次要、重要、紧急。其中不同类型的告警都接收到了最新的告警信息，其中");
            boolean flag = false;
            for (AlarmStatisticsVo vo : vos) {
                if (flag) {
                    sb.append("、");
                }
                sb.append(vo.getName()).append(vo.getValue()).append("个");

                flag = true;
            }

            sb.append("。请从不同维度简要分析一下告警数据，生成告警报告").append(".生成报时间为:").append(DateUtils.dateToStr(new Date()));


            //调用公司AI模型
            String result = dcDifyUtils.sendRequest(sb.toString(), defaultUser);

            return ResultInfo.success(result);
        } catch (Exception e) {
            return ResultInfo.error("请联系管理员");
        }

    }

    /**
     * 跑马灯预警
     * @param
     * @return
     */
    @RequestMapping("/lampYJ")
    @ApiOperation("跑马灯")
    public ResultInfo lampYJ(@RequestBody AlarmStatisticsVo arVo) {
        try {

            String defaultUser = "11";
            try {
                SysUser u = sysUserService.getUser();
                defaultUser = u.getRealName();
            } catch (Exception e) {
                defaultUser = "11";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("你是一个智慧园区系统的运维人员，平台现在接收到了众多预警信息，预警类型分为：能耗预警、余额预警。预警等级分为：警告、次要、重要、紧急。其中不同类型的预警都接收到了最新的预警信息，其中");

            // 能耗预警统计
            AlarmRecordsVo voNH = new AlarmRecordsVo();
            voNH.setAlarmCategory(7);
            voNH.setAlarmType(2);
            Map<String, Integer> nhStats = countAlarmLevels(voNH);
            sb.append(String.format("能耗预警共%d个(警告是%d个,次要是%d个,重要是%d个,紧急是%d个)、",
                nhStats.get("total"), nhStats.get("warning"), nhStats.get("minor"), 
                nhStats.get("major"), nhStats.get("critical")));

            // 余额预警统计
            AlarmRecordsVo voYe = new AlarmRecordsVo();
            voYe.setAlarmCategory(8);
            voYe.setAlarmType(2);
            Map<String, Integer> yeStats = countAlarmLevels(voYe);
            sb.append(String.format("余额预警共%d个(警告是%d个,次要是%d个,重要是%d个,紧急是%d个)、",
                yeStats.get("total"), yeStats.get("warning"), yeStats.get("minor"), 
                yeStats.get("major"), yeStats.get("critical")));

            sb.append("。请从不同维度简要分析一下告警数据，生成告警报告").append(".生成报时间为:").append(DateUtils.dateToStr(new Date()));

//            System.out.println(sb.toString());
            //调用公司AI模型
            String result = dcDifyUtils.sendRequest(sb.toString(), defaultUser);

            return ResultInfo.success(result);
        } catch (Exception e) {
            return ResultInfo.error("请联系管理员");
        }

    }


    /**
     * 获取报警类型
     * @param
     * @return
     */
    @RequestMapping("/getAlarmCategory")
    @ApiOperation("获取报警类型")
    public ResultInfo getAlarmCategory(Integer alarmType) {
        return ResultInfo.success(alarmRecordsService.getAlarmCategory(alarmType));
    }


    /**
     * 获取历史告警列表
     * @param
     * @return
     */
    @PostMapping("/getHistoryList")
    @ApiOperation("获取历史告警列表")
    public ResultInfo getHistoryList(@RequestBody AlarmRecordsVo bean) {
        return ResultInfo.success(alarmRecordsService.getHistoryList(bean));
    }

    /**
     *  设备关联的工单 详情
     */
    @PostMapping("/getDetailsByDeviceId")
    @ApiOperation("设备关联的工单 详情")
    public ResultInfo getDetailsByDeviceId(Integer id) {
        return ResultInfo.success(alarmRecordsService.getAlarmDetailsByDeviceId(id));
    }



    /**
     * 通知
     * @param
     * @return
     */
    @RequestMapping("/notice")
    @ApiOperation("通知")
    public ResultInfo notice(AlarmRecordsVo vo) {
        alarmRecordsService.notice(vo);
        return ResultInfo.success();
    }

    /**
     * 统计各告警等级数量
     * @param vo 告警查询条件
     * @return 包含各等级数量的Map
     */
    private Map<String, Integer> countAlarmLevels(AlarmRecordsVo vo) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("warning", 0);
        stats.put("minor", 0);
        stats.put("major", 0);
        stats.put("critical", 0);
        
        List<AlarmRecordsVo> alarms = alarmRecordsService.groupAlarmLevel(vo);
        for (AlarmRecordsVo alarm : alarms) {
            if("警告".equals(alarm.getAlarmLevelName())) {
                stats.put("warning", NumberUtils.toInt(alarm.getNum()));
            } else if("次要".equals(alarm.getAlarmLevelName())) {
                stats.put("minor", NumberUtils.toInt(alarm.getNum()));
            } else if("重要".equals(alarm.getAlarmLevelName())) {
                stats.put("major", NumberUtils.toInt(alarm.getNum()));
            } else if("紧急".equals(alarm.getAlarmLevelName())) {
                stats.put("critical", NumberUtils.toInt(alarm.getNum()));
            }
        }
        
        stats.put("total", stats.get("warning") + stats.get("minor") + 
                          stats.get("major") + stats.get("critical"));
        return stats;
    }
}
