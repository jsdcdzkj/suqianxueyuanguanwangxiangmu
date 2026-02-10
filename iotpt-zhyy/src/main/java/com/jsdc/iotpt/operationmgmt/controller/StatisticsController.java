package com.jsdc.iotpt.operationmgmt.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.WarningInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.model.operate.MissionTjVo;
import com.jsdc.iotpt.model.operate.TeamGroups;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.MissionService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.vo.DataSheetVo;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import com.jsdc.iotpt.vo.ElectricalSafetyReportVo2;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private MissionService missionService ;
    @Autowired
    private TeamGroupsService teamGroupsService;
    @Autowired
    private SysDictService sysDictService;


    @Autowired
    private DeviceCollectService deviceCollectService ;

    @Autowired
    private DataSheetService dataSheetService ;

    /**
     *运维管理统计报表列表 执行班组
     * Author wzn
     * Date 2023/8/24 10:41
     */
    @PostMapping("getListByGroup")
    public ResultInfo getTeamPage(@RequestBody MissionTjVo missionVo){
        return ResultInfo.success(missionService.getListByGroup(missionVo));
    }



    @RequestMapping("/getListByGroupExport")
    public void getListByGroupExport(HttpServletResponse response, MissionTjVo missionVo) throws Exception {
        List<MissionTjVo> datas = missionService.getListByGroup(missionVo) ;
        BigExcelWriter writer = (BigExcelWriter) ExcelUtil.getBigWriter();
        writer.addHeaderAlias("teamGroupsName", "执行班组");
        writer.addHeaderAlias("taskTypeName", "任务类型");
        writer.addHeaderAlias("stateCount", "任务状态");
        writer.addHeaderAlias("count", "任务数量");

        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(datas, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }



    /**
    *数据展示
    * Author wzn
    * Date 2023/8/24 18:30
    */
    @PostMapping("getList")
    public ResultInfo getList(@RequestBody  MissionTjVo vo){
        List<MissionTjVo> missionTjVoList =  missionService.getList(vo) ;
        Page<MissionTjVo> page = new Page<>(vo.getPageIndex(), vo.getPageSize());
        page.setTotal(missionTjVoList.size());
        int startIndex = (int)((vo.getPageIndex() - 1)*vo.getPageSize());
        if(null == missionTjVoList || missionTjVoList.isEmpty() || startIndex > missionTjVoList.size()){
            page.setRecords(null);
        }else {
            int toIndex = (int)(vo.getPageIndex()*vo.getPageSize());
            page.setRecords(missionTjVoList.subList(startIndex,toIndex > missionTjVoList.size() ? missionTjVoList.size() : toIndex));
        }
        return ResultInfo.success(page);
    }


    @RequestMapping("/getListExport")
    public void getListExport(HttpServletResponse response,MissionTjVo missionVo) throws Exception {
        List<MissionTjVo> missionTjVoList =  missionService.getList(missionVo) ;
        BigExcelWriter writer = (BigExcelWriter) ExcelUtil.getBigWriter();
        writer.addHeaderAlias("day", "时间");
        writer.addHeaderAlias("count", "任务数量");
        writer.addHeaderAlias("infoC0", "待指派");
        writer.addHeaderAlias("infoC1", "待处理");
        writer.addHeaderAlias("infoC2", "已处理");
        writer.addHeaderAlias("finishingRate", "完成率(%)");

        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(missionTjVoList, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }



    /**
    *任务类型、状态、数量趋势
    * Author wzn
    * Date 2023/8/24 18:30
    */
    @PostMapping("lineChart")
    public ResultInfo lineChart(@RequestBody  MissionTjVo vo){
        return ResultInfo.success(missionService.lineChart(vo));
    }

    /**
     *任务类型占比
     * Author xj
     * Date 2023/8/24 18:30
     */
    @PostMapping("getMission_type")
    public ResultInfo getMission_type(@RequestBody  MissionTjVo vo){
        if ("2".equals(vo.getTimeType())) {
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(vo.getTimeStr(), "yyyy-MM"));
            vo.setTimeStart(DateUtil.format(DateUtil.beginOfMonth(timeStart),"yyyy-MM-dd"));
            vo.setTimeEnd(DateUtil.format(DateUtil.endOfMonth(timeStart),"yyyy-MM-dd"));
        }else if("3".equals(vo.getTimeType())){
            // 指定日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            try {
                Date date = sdf.parse(vo.getTimeStr());
                vo.setTimeStart(MissionService.getYearFirstDay(date));
                vo.setTimeEnd(MissionService.getYearLastDay(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //querryTimeType  完成时间 /上报时间
        vo.setTimeType(vo.getQuerryTimeType());
        return ResultInfo.success(missionService.getMission_type(vo.getTimeType(),vo.getTimeStart(),vo.getTimeEnd()));
    }

    /**
     *任务状态占比
     * Author xj
     * Date 2023/8/24 18:30
     */
    @PostMapping("getMission_state")
    public ResultInfo getMission_state(@RequestBody  MissionTjVo vo){
        if ("2".equals(vo.getTimeType())) {
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(vo.getTimeStr(), "yyyy-MM"));
            vo.setTimeStart(DateUtil.format(DateUtil.beginOfMonth(timeStart),"yyyy-MM-dd"));
            vo.setTimeEnd(DateUtil.format(DateUtil.endOfMonth(timeStart),"yyyy-MM-dd"));
        }else if("3".equals(vo.getTimeType())){
            // 指定日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            try {
                Date date = sdf.parse(vo.getTimeStr());
                vo.setTimeStart(MissionService.getYearFirstDay(date));
                vo.setTimeEnd(MissionService.getYearLastDay(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
//querryTimeType  完成时间 /上报时间
        vo.setTimeType(vo.getQuerryTimeType());
        return ResultInfo.success(missionService.getMission_state(vo.getTimeType(),vo.getTimeStart(),vo.getTimeEnd()));
    }



    /**
     *班组、任务类型
     * Author xj
     * Date 2023/8/24 18:30
     */
    @PostMapping("getMission_type_team")
    public ResultInfo getMission_type_team(@RequestBody  MissionTjVo vo){
        //参数封装
        if ("2".equals(vo.getTimeType())) {
            Date timeStart = DateUtil.beginOfMonth(DateUtil.parse(vo.getTimeStr(), "yyyy-MM"));
            vo.setTimeStart(DateUtil.format(DateUtil.beginOfMonth(timeStart),"yyyy-MM-dd"));
            vo.setTimeEnd(DateUtil.format(DateUtil.endOfMonth(timeStart),"yyyy-MM-dd"));
        }else if("3".equals(vo.getTimeType())){
            // 指定日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            try {
                Date date = sdf.parse(vo.getTimeStr());
                vo.setTimeStart(MissionService.getYearFirstDay(date));
                vo.setTimeEnd(MissionService.getYearLastDay(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //querryTimeType  完成时间 /上报时间
        vo.setTimeType(vo.getQuerryTimeType());
        JSONObject result = new JSONObject();
        //任务类型
        List<SysDict> sysDicts = sysDictService.selectByType("taskTypes");
        //所有班组
        List<TeamGroups> teamGroups = teamGroupsService.getAllList();
        //坐标横轴
        List<String> xAxisData = new ArrayList<>();
        teamGroups.forEach(x->xAxisData.add(x.getName()));
        result.put("xAxisData",xAxisData);
        Map<String,Integer> dataMap = new HashMap<>();
        List<HashMap> mission_type_team = missionService.getMission_type_team(vo.getTimeType(),vo.getTimeStart(),vo.getTimeEnd());
        mission_type_team.forEach(x->dataMap.put(x.get("TASKTYPE")+"_"+x.get("TEAMGROUPSID"),Integer.parseInt(x.get("VALUE")+"")));
        List<JSONObject> seriesData = new ArrayList<>();
        for (SysDict sysDict : sysDicts) {
            JSONObject item = new JSONObject();
            item.put("name",sysDict.getDictLabel());
            List<Integer> dataArray = new ArrayList<>();
            for (TeamGroups teamGroup : teamGroups) {
                Integer value = dataMap.get(sysDict.getDictValue()+"_"+teamGroup.getId());
                if(value == null){
                    value = 0;
                }
                dataArray.add(value);
            }
            item.put("data",dataArray);
            item.put("type","bar");
            item.put("stack","total");
            item.put("barMaxWidth",30);
            seriesData.add(item);
        }
        result.put("seriesData",seriesData);
        return ResultInfo.success(result);
    }

    /**
    *工作台首页统计
    * Author wzn
    * Date 2023/8/28 11:34
    */
    @PostMapping("statistics")
    public ResultInfo statistics(){
        return ResultInfo.success(missionService.statistics());
    }


    /**
    *近7天派单数统计
    * Author wzn
    * Date 2023/8/28 15:21
    */
    @PostMapping("numberOfDenominations")
    public ResultInfo numberOfDenominations(){
        return ResultInfo.success(missionService.numberOfDenominations());
    }

    /**
     *近7天告警数统计
     * Author wzn
     * Date 2023/8/28 15:21
     */
    @PostMapping("numberGjOfDays")
    public ResultInfo numberGj(){
        return ResultInfo.success(missionService.numberGjOfDays());
    }

    /**
     * 用电量评价
     *
     * @return
     */
    @RequestMapping("/energyEvaluate.do")
    @ApiOperation("用电量评价 ")
    public ResultInfo energyEvaluate() {
        return ResultInfo.success(missionService.energyEvaluate());
    }


    /**
     * 当月告警类型
     * Author wzn
     * Date 2023/8/28 15:21
     */
    @PostMapping("monthAlarm")
    public ResultInfo monthAlarm(){
        return ResultInfo.success(missionService.monthAlarm());
    }

    /**
     * 告警信息统计
     * Date 2023/8/28 15:21
     */
    @PostMapping("alarmStatistics")
    public ResultInfo alarmStatistics(){
        return ResultInfo.success(missionService.alarmStatistics());
    }





    @GetMapping("/export")
    public void export(HttpServletResponse response, ElectricalSafetyReportVo2 vo2)throws Exception{
        //1.模拟一些人物对象数据（工作中从数据库查出来）

        List<ElectricalSafetyReportVo2> list = missionService.electricalSafetyExport2(vo2) ;

        //2.定义基础数据
        List<String> rowHead = CollUtil.newArrayList("区域","设备总数","告警总数","分项告警","轻微次数","一般次数","严重次数");
        //3.通过ExcelUtil.getBigWriter()创建Writer对象，BigExcelWriter用于大数据量的导出，不会引起溢出；
        ExcelWriter writer = ExcelUtil.getBigWriter();
        //4.写入标题
        writer.writeHeadRow(rowHead);
        ServletOutputStream out = null;
        //5.实现核心逻辑
        try {
            //6.定义容器保存人物数据
            List<List<Object>> rows = new LinkedList<>();
            //7.按照班级进行分组
            LinkedHashMap<String, List<ElectricalSafetyReportVo2>> classList = list.stream().collect(Collectors.groupingBy(item -> item.getAreaName(),
                    LinkedHashMap::new, Collectors.toList()));
            //8.定义起始行（方便分组后合并时从哪一行开始）
            //因为标题已经占了一行，所以数据从第二行开始写（excel第一行索引为0）
            //因需要合并到人物分数单元格所以需定义如下起始坐标
            int indexClassName = 1;   //班级名称起始行
            int indexClassScore = 1;
            int indexGroupName = 1;
            int indexGroupScore = 1;
            int indexPersonName = 1;
            int indexPersonScore = 1;
            //9.遍历按班级名分组后的list（用entrySet效率比keySet效率高）
            for (Map.Entry<String, List<ElectricalSafetyReportVo2>> classNameListEntry : classList.entrySet()) {
                //10.获取按照班级名分组后的集合
                List<ElectricalSafetyReportVo2> classValue = classNameListEntry.getValue();
                //11.计算此集合的长度
                int classSize = classValue.size();
                //12.如果只有一行数据不能调用merge方法合并数据，否则会报错
                if (classSize == 1){
                    indexClassName += classSize;
                    indexClassScore += classSize;
                    indexGroupName += classSize;
                    indexGroupScore += classSize;
                    indexPersonName += classSize;
                    indexPersonScore += classSize;
                }else{
                    //13.根据班级名称进行合并单元格
                    //合并行，第一个参数是合并行的开始行号（行号从0开始），第二个参数是合并行的结束行号，第三个参数是合并的列号开始(列号从0开始)，
                    //第四个参数是合并的列号结束，第五个参数是合并后的内容，null不设置，第六个参数指是否支持设置样式，true指的是。
                    writer.merge(indexClassName, indexClassName + classSize - 1, 0, 0, null, true);
                    //14.合并完后起始索引移到下一个合并点
                    indexClassName += classSize;
                    //15.因为班级分数与班级名称相关联，所以不需要对班级分数分组，直接在此基础上对班级分数合并
                    writer.merge(indexClassScore, indexClassScore + classSize - 1, 1, 1, null, true);
                    indexClassScore += classSize;
                    //16.按照小组名进行分组（以下分组与班级名合并类似）
                    LinkedHashMap<String, List<ElectricalSafetyReportVo2>> groupList = classValue.stream().collect(Collectors.groupingBy(item -> item.getWarningNum()+"",
                            LinkedHashMap::new, Collectors.toList()));
                    for (Map.Entry<String, List<ElectricalSafetyReportVo2>> groupListEntry : groupList.entrySet()) {
                        List<ElectricalSafetyReportVo2> groupValue = groupListEntry.getValue();
                        int groupSize = groupValue.size();
                        if (groupSize == 1){
                            indexGroupName += groupSize;
                            indexGroupScore += groupSize;
                            indexPersonName += groupSize;
                            indexPersonScore += groupSize;
                        }else{
                            //合并小组
                            writer.merge(indexGroupName, indexGroupName + groupSize - 1, 2, 2, null, true);
                            indexGroupName += groupSize;

                        }
                    }

                }
                //18.保存数据
                classValue.forEach(
                        sList->{
                            List<Object> rowA = null;
                            rowA = CollUtil.newArrayList(
                                    sList.getAreaName(),
                                    sList.getDeviceNum(),
                                    sList.getWarningNum(),
                                    sList.getSubitemNum(),
                                    sList.getQwNum(),
                                    sList.getYbNum(),
                                    sList.getYzNum()
                            );
                            rows.add(rowA);
                        }
                );
            }
            //19.导出数据
            //logger.info("导出数据：{}",rows.toString());
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(rows, true);
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //获取当前日期作为文件名
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(currentDate);
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            //response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
            response.setHeader("Content-Disposition", "attachment;filename=report_"+date+".xlsx");
            out = response.getOutputStream();
            writer.flush(out, true);
        }finally {
            //关闭输出Servlet流
            IoUtil.close(out);
            //关闭writer，释放内存
            writer.close();
        }
    }

    @PostMapping("/energyConsumption")
    public ResultInfo energyConsumption(){
        return ResultInfo.success(
                deviceCollectService.energyConsumption());
    }


    @PostMapping("/getSubitemEnergy")
    public ResultInfo getSubitemEnergy(@RequestBody DeviceQueryVo bean){
        return ResultInfo.success(
                deviceCollectService.getSubitemEnergy(bean));
    }


    /**
     *能耗预警 折线图
     *
     * @author wzn
     * @date 2024/12/18 09:22
     */
    @PostMapping("/energyForecast")
    public ResultInfo energyForecast(@RequestBody DeviceQueryVo bean){
        return deviceCollectService.energyForecast(bean);
    }

    @PostMapping("/subitemEnergy")
    public ResultInfo subitemEnergy(String id){
        return ResultInfo.success(
                deviceCollectService.subitemEnergy(id));
    }



    @RequestMapping("/getRealTimeWater.do")
    public ResultInfo getRealTimeWater() {
         DataSheetVo bean = new DataSheetVo() ;
        JSONObject jsonObject3 = new JSONObject() ;

        bean.setGroupStr("time(1d)");
        bean.setThisXData(DateUtils.getMonthDays(new Date()));


        //本月用水趋势
        // 用水量
        bean.setChannelId("WATER_L");
        // 用水设备
        bean.setEnergyType(2);
        // 返回值
        bean.setInfluxVal("max(val)-min(val)");

        bean.setTimeType(2);
        JSONObject jsonObject = dataSheetService.getChannelLoad(bean) ;
        jsonObject.get("thisTime") ;

        jsonObject3.put("thisTimeWater", jsonObject.get("thisTime")) ;
        //上个月用水趋势
        // 用水量
        bean.setChannelId("WATER_L");
        // 用水设备
        bean.setEnergyType(2);
        // 返回值
        bean.setTimeType(5);
        bean.setThisXData(DateUtils.getLastMonthDays(new Date()));
        JSONObject jsonObject1 = dataSheetService.getChannelLoad(bean) ;
        jsonObject1.get("lastTime") ;


        jsonObject3.put("lastTimeWater", jsonObject1.get("lastTime")) ;


        //本月用电趋势
        bean.setChannelId("ENERGY_TOTAL");
        bean.setInfluxVal("last(val)-first(val)");
        bean.setEnergyType(1);
        bean.setTimeType(2);
        bean.setThisXData(DateUtils.getMonthDays(new Date()));
        JSONObject jsonObject2 = dataSheetService.getChannelLoad(bean) ;
        jsonObject2.get("thisTime") ;
        jsonObject3.put("thisTimeElectricity", jsonObject2.get("thisTime")) ;

        //上月用电趋势
        bean.setTimeType(5);
        bean.setThisXData(DateUtils.getLastMonthDays(new Date()));
        JSONObject jsonObject4 = dataSheetService.getChannelLoad(bean) ;
        jsonObject4.get("lastTime") ;
        jsonObject3.put("lastTimeElectricity", jsonObject4.get("lastTime")) ;

        List<String>  stringList = new ArrayList<>((Collection) jsonObject.get("thisTime")) ;
        List<String>  stringList2 =new ArrayList<>((Collection) jsonObject2.get("thisTime")) ;
        List<String>  stringList3 =new ArrayList<>((Collection) jsonObject1.get("lastTime")) ;
        List<String>  stringList4 =new ArrayList<>((Collection) jsonObject4.get("lastTime")) ;




        //本月费用趋势
        List<Object> object = new ArrayList<>() ;
        for (int i = 0; i < stringList.size(); i++) {
            double num1 = Double.parseDouble(stringList.get(i));
            double num2 = Double.parseDouble(stringList2.get(i));
            object.add(num1 + num2) ;
        }

        jsonObject3.put("thisMoney", object) ;


        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 获取当前日期的上个月
        YearMonth previousMonth = YearMonth.from(currentDate).minusMonths(1);

        // 获取上个月的天数
        int daysInPreviousMonth = previousMonth.lengthOfMonth();
        //上月费用趋势
        List<Object> objects = new ArrayList<>() ;
        for (int i = 0; i < daysInPreviousMonth; i++) {
            double  num1 = Double.parseDouble(stringList3.get(i));
            double  num2 = Double.parseDouble(stringList4.get(i));
            objects.add(num1 + num2) ;
        }
        jsonObject3.put("lastMoney", objects) ;

        return ResultInfo.success(jsonObject3);
    }


}
