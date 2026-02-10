package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ReportManageVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReportManageService extends BaseService<ReportManage> {

    @Autowired
    private ReportManageMapper reportManageMapper;
    @Autowired
    private ReportTemplateService reportTemplateService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ReportBusinessMapper reportBusinessMapper;

    @Autowired
    private SysBuildAreaMapper areaMapper;

    @Autowired
    private DeviceCollectMapper collectMapper;

    @Autowired
    private ConfigDeviceTypeMapper typeMapper;

    @Autowired
    private MissionMapper missionMapper;
    @Autowired
    private MissionService missionService;
    @Autowired
    private DataSheetService dataSheetService;
    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private SysDictService sysDictService;


    /**
     * 报告管理分页查询
     * @param bean
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<ReportManage> getPage(ReportManage bean, Integer pageIndex, Integer pageSize){
        LambdaQueryWrapper<ReportManage> wrapper = getWrapper(bean);
        wrapper.orderByDesc(ReportManage::getCreateTime);
        Page<ReportManage> p = baseMapper.selectPage(new Page<>(pageIndex, pageSize), wrapper);

        p.getRecords().forEach(item -> {
          SysUser sysUser = sysUserMapper.selectById(item.getCreateUser());
          if (null != sysUser){
              item.setCreateName(sysUser.getRealName());
          }

          if (null != item.getChooseTemple()){
              ReportTemplate reportTemplate = reportTemplateService.selectByTemplate(item.getChooseTemple());
              if (null != reportTemplate){
                  item.setChooseTempleName(reportTemplate.getTempTypeName());
              }
          }
        });

        return p;
    }


    private LambdaQueryWrapper<ReportManage> getWrapper(ReportManage bean) {
        LambdaQueryWrapper<ReportManage> wrapper = new LambdaQueryWrapper<>();
        if (null != bean) {
            if (StringUtils.isNotBlank(bean.getReportName())){
                wrapper.like(ReportManage::getReportName,bean.getReportName());
            }
            if (StringUtils.isNotBlank(bean.getCreateName())){
                List<SysUser> sysUsers =  sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel,0).like(SysUser::getRealName,bean.getCreateName()));
                if (CollectionUtils.isNotEmpty(sysUsers)){
                    wrapper.in(ReportManage::getCreateUser,sysUsers.stream().map(SysUser::getId).collect(Collectors.toList()).toArray());
                }else {
                    wrapper.eq(ReportManage::getIsDel,-1);
                }
            }
        }
        wrapper.eq(ReportManage::getIsDel, G.ISDEL_NO);
        return wrapper;

    }


    /**
     * 根据ID删除报告
     * @param id
     * @return
     */
    public ResultInfo deleteReport(Integer id){
        reportManageMapper.update(null,Wrappers.<ReportManage>lambdaUpdate().set(ReportManage::getIsDel,G.ISDEL_YES).eq(ReportManage::getId,id));
        return ResultInfo.success();
    }

    /**
     * 查看 分享  下载
     * @param id
     * @return
     */
    public ReportManage selectOneInfo(Integer id){
        return reportManageMapper.selectById(id);
    }


    /**
     * 新增
     * @param bean
     * @return
     */
    public Integer addReport(ReportManage bean){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date reportStarTime = null ;
        Date reportEndTime = null ;
        try {
            reportStarTime = s.parse(bean.getStartTime());
            reportEndTime = s.parse(bean.getEndTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        bean.setReportStarTime(reportStarTime);
        bean.setReportEndTime(reportEndTime);
        bean.setIsDel(G.ISDEL_NO);
        bean.setCreateTime(new Date());
        bean.setCreateUser(sysUserService.getUser().getId());
        Integer value = reportManageMapper.insert(bean);
        if (CollectionUtils.isNotEmpty(bean.getAreaIds())){
            List<Integer> ids = bean.getAreaIds();
            for (Integer id : ids){
                ReportBusiness r = new ReportBusiness();
                r.setIsDel(G.ISDEL_NO);
                r.setReportId(bean.getId());
                r.setAreaId(id);
                reportBusinessMapper.insert(r);
            }
        }
        return value;
    }




    /**
     * 项目概况-基本信息
     *
     * @return
     */
    public JSONObject baseInfo(ReportManageVo bean) {
        JSONObject jsonObject = new JSONObject();
        // 区域信息
        List<SysBuildArea> areas = areaMapper.selectBatchIds(bean.getAreaIds());

        // 设备
        List<DeviceCollect> deviceCollects = collectMapper.getDeviceByAreas(bean);

        // 设备总数
        jsonObject.put("deviceCount", deviceCollects.size());
        if (bean.getSplType()==1){
            jsonObject.put("area",areas.isEmpty()?null:areas.get(0));
            return jsonObject;
        }


        // 商户数
        jsonObject.put("areaCount", areas.size());
        // 设备总数
        jsonObject.put("deviceCount", deviceCollects.size());

        // 设备在线状态
        Map<String, Integer> collectOnline = new HashMap<>();

        for (DeviceCollect item : deviceCollects) {
            if (!collectOnline.containsKey(item.getOnLineStatus())) {
                collectOnline.put(item.getOnLineStatus(), 0);
            }

            // 设备在线
            int currentCount = collectOnline.get(item.getOnLineStatus());
            collectOnline.put(item.getOnLineStatus(), currentCount + 1);

        }
        // 离线
        if (collectOnline.containsKey("2")) {
            jsonObject.put("deviceCountOff",collectOnline.get("2"));
        } else {
            jsonObject.put("deviceCountOff",0);
        }
        // 异常
        if (collectOnline.containsKey("3")) {
            jsonObject.put("deviceCountError",collectOnline.get("3"));
        } else {
            jsonObject.put("deviceCountError",0);
        }
        // 正常
        if (collectOnline.containsKey("1")) {
            jsonObject.put("deviceCountOn",collectOnline.get("1"));
        } else {
            jsonObject.put("deviceCountOn",0);
        }

        List<String> areaName=areas.stream().map(x->x.getAreaName()).collect(Collectors.toList());

        jsonObject.put("areaNames",StringUtils.join(areaName,","));

        return jsonObject;
    }


    /**
     *  项目概况-终端情况
     * @param bean
     * @return
     */
    public JSONObject cmdInfo(ReportManageVo bean){
        JSONObject jsonObject = new JSONObject();

        // 设备
        List<DeviceCollect> deviceCollects = collectMapper.getDeviceByAreas(bean);

        // 设备类型 在线统计
        Map<Integer,Map<String,Integer>> collectType=new HashMap<>();

        for (DeviceCollect item : deviceCollects) {
            if (!collectType.containsKey(item.getDeviceType())){
                Map<String,Integer> itemOnline=new HashMap<>();
                itemOnline.put("0",0);
                itemOnline.put("1",0);
                itemOnline.put("2",0);
                itemOnline.put("3",0);
                collectType.put(item.getDeviceType(),itemOnline);
            }
            // 设备类型-在线
            int itemCurrentCount=collectType.get(item.getDeviceType()).get(item.getOnLineStatus());
            collectType.get(item.getDeviceType()).put(item.getOnLineStatus(),itemCurrentCount+1);

            int itemCount=collectType.get(item.getDeviceType()).get("0");
            collectType.get(item.getDeviceType()).put("0",itemCount+1);
        }

        Map<String,Map<String,Integer>> collectResult=new HashMap<>();

        for (Map.Entry<Integer,Map<String,Integer>> item:collectType.entrySet()) {
            ConfigDeviceType configDeviceType=typeMapper.selectById(item.getKey());

            collectResult.put(configDeviceType.getDeviceTypeName(),item.getValue());
        }

        jsonObject.put("result",collectResult);



        return jsonObject;
    }

    public static void main(String[] args) {
        Date date = new Date();

        Date currentDate=new Date();

        System.out.println(currentDate.getTime()-date.getTime());
    }


    /**
     *  巡检运维情况-工单情况
     * @param bean
     * @return
     */
    public JSONObject missionInfo(ReportManageVo bean){
        JSONObject jsonObject=new JSONObject();

        List<MissionVo> allMissionVos=missionMapper.getMissionViews(bean);

        // 总计
        jsonObject.put("allMissionTotal",allMissionVos.size());

        List<MissionVo> unMissionVos=allMissionVos.stream().filter(x->(x.getStates()!=3)).collect(Collectors.toList());
        // 未处理
        jsonObject.put("unMissionTotal",unMissionVos.size());


        List<MissionVo> yMissionVos=allMissionVos.stream().filter(x->(x.getStates()==3)).collect(Collectors.toList());

        // 未处理
        jsonObject.put("missionTotal",yMissionVos.size());
        // 响应时间
        long responseSum=0l;
        // 完成时间
        long finishSum=0l;

        for (MissionVo x:yMissionVos) {
            if (x.getReportingTime()!=null&&x.getACreateTime()!=null&&x.getHandleDate()!=null){
                long reportTime=x.getReportingTime().getTime();
                long createTime=x.getACreateTime().getTime();
                long handleTime=x.getHandleDate().getTime();

                responseSum=responseSum+createTime-reportTime;
                finishSum=finishSum+handleTime-reportTime;
            }
        }

        // 响应时间
        if (yMissionVos.isEmpty()){
            jsonObject.put("responseMin",0);
            jsonObject.put("finishMin",0);

        }else {
            jsonObject.put("responseMin",responseSum/1000/60/yMissionVos.size());
            jsonObject.put("finishMin",finishSum/1000/60/yMissionVos.size());
        }


        // 完成时间



        return jsonObject;
    }

    public SysDict returnDicst(String dictType, Integer dictValue) {
        if (null == dictValue) {
            return null;
        }
        SysDict dict = new SysDict();
        List<SysDict> dicts = sysDictService.list(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, dictType)
                .eq(SysDict::getDictValue, String.valueOf(dictValue)).eq(SysDict::getIsDel, 0));
        if (!dicts.isEmpty() && dicts.size() > 0) {
            return dicts.get(0);
        }
        return dict;
    }

    public JSONObject taskInfo(ReportManageVo bean){
        JSONObject jsonObject=new JSONObject();

        List<MissionVo> allMissionVos=missionMapper.getMissionViews(bean);

        allMissionVos.forEach(x->{
            if (null != x.getStates()) {
                x.setStatesName(missionService.retunStatusName(1, x.getStates()));//任务状态
            }
            if (null != x.getAssignTaskType()) {//任务类型
                if (4 == x.getSource()) {
                    SysDict dict = returnDicst("planType", x.getAssignTaskType());
                    if (StringUtils.isNotNull(dict)) {
                        x.setTaskTypeName(dict.getDictLabel());
                    }
                } else {
                    SysDict dict = returnDicst("taskTypes", x.getAssignTaskType());
                    if (StringUtils.isNotNull(dict)) {
                        x.setTaskTypeName(dict.getDictLabel());
                    }
                }
            }

            if (null != x.getUrgency()) {//紧急程度
                SysDict dict = returnDicst("missionUrgency", x.getUrgency());
                if (StringUtils.isNotNull(dict)) {
                    x.setUrgencyName(dict.getDictLabel());
                }
            }
        });


        List<MissionVo> allTaskMissions=allMissionVos.stream().filter(x->(x.getSource()==4)).collect(Collectors.toList());

        // 所有巡检任务
        jsonObject.put("allTaskTotal",allTaskMissions.size());

        // 未完成的巡检任务
        List<MissionVo> unTaskMissions=allTaskMissions.stream().filter(x->(x.getStates()!=3)).collect(Collectors.toList());

        jsonObject.put("unTaskTotal",unTaskMissions.size());


        // 已完成的巡检任务id
        List<Integer> taskMissionIds=allTaskMissions.stream().filter(x->(x.getStates()==3)).map(y->y.getId()).collect(Collectors.toList());

        if (!taskMissionIds.isEmpty()){

            List<MissionVo> allErrors=missionMapper.getErrorMissions(taskMissionIds);
            jsonObject.put("errorTaskTotal",allErrors.size());

        }else {
            jsonObject.put("errorTaskTotal",0);
        }

        jsonObject.put("allTask",allTaskMissions);

        return jsonObject;
    }

    public JSONObject taskErrorInfo(ReportManageVo bean){
        JSONObject jsonObject=new JSONObject();

        List<MissionVo> allMissionVos=missionMapper.getMissionViews(bean);



        List<MissionVo> allTaskMissions=allMissionVos.stream().filter(x->(x.getSource()==4)).collect(Collectors.toList());

        // 已完成的巡检任务id
        List<Integer> taskMissionIds=allTaskMissions.stream().filter(x->(x.getStates()==3)).map(y->y.getId()).collect(Collectors.toList());

        if (!taskMissionIds.isEmpty()){

            List<MissionVo> allErrors=missionMapper.getErrorMissions(taskMissionIds);
            allErrors.forEach(x->{
                if (null != x.getStates()) {
                    x.setStatesName(missionService.retunStatusName(1, x.getStates()));//任务状态
                }

                SysUser sysUser=sysUserMapper.selectById(x.getCreateUser());
                x.setCreateUserName(sysUser.getRealName());
            });

            List<MissionVo> unErrors=allErrors.stream().filter(x->(x.getStates()!=3)).collect(Collectors.toList());

            jsonObject.put("errorTaskTotal",allErrors.size());
            jsonObject.put("unErrorTaskTotal",unErrors.size());

            jsonObject.put("yErrorTaskTotal",allErrors.size()-unErrors.size());

            jsonObject.put("allErrorTask",allErrors);

        }else {
            jsonObject.put("errorTaskTotal",0);
            jsonObject.put("unErrorTaskTotal",0);
            jsonObject.put("yErrorTaskTotal",0);
            jsonObject.put("allErrorTask",null);
        }

        return jsonObject;
    }

    public Map<String,Double> getInfluxExecute(List<DeviceCollect> collects,String influxVal,String startTime,String endTime,String groupStr,String channelId){


        Map<String, Double> result = new LinkedHashMap<>();

        List<String> ids=collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());

        String sql = dataSheetService.getInfluxSql(influxVal, startTime, endTime, groupStr, channelId, ids);
        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);

        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);


        for (Map<String, Object> map : maps) {
            if (result.containsKey(map.get("time"))) {
                result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
            } else {
                result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
            }
        }

        return result;


    }


    /**
     * 用电能统计
     * @param bean
     * @return
     */
    public JSONObject energyInfo(ReportManageVo bean) {
        JSONObject jsonObject = new JSONObject();
        // 设备
        List<DeviceCollect> deviceCollects = collectMapper.getDeviceByAreas(bean);



        if (bean.getTimeType()==1){
            LinkedHashMap<String,String> thisXData= DateUtils.getDatesBetween(bean.getStartTime(),bean.getEndTime());

            Map<String, Double> map = getInfluxExecute(deviceCollects,"MAX(val)-MIN(val)", bean.getStartTime(), bean.getEndTime(),"time(1d)","ENERGY_TOTAL");

            for (Map.Entry<String, Double> item : map.entrySet()) {
                thisXData.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }

            jsonObject.put("thisTime", thisXData.keySet());
            jsonObject.put("thisData", thisXData.values());
        }else if (bean.getTimeType()==2){
            try {
                Date date=new SimpleDateFormat("yyyy-MM-dd").parse(bean.getStartTime());
                LinkedHashMap<String,String> thisXData= DateUtils.getMonthDays(date);
                Map<String, Double> map = getInfluxExecute(deviceCollects,"MAX(val)-MIN(val)",
                        DateUtils.getStartTimeOfCurrentMonth(date), DateUtils.getEndTimeOfCurrentMonth(date),"time(1d)","ENERGY_TOTAL");

                for (Map.Entry<String, Double> item : map.entrySet()) {
                    thisXData.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }

                jsonObject.put("thisTime", thisXData.keySet());
                jsonObject.put("thisData", thisXData.values());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(bean.getStartTime().substring(0,4));
            List<String> thisResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute(deviceCollects,"MAX(val)-MIN(val)",
                        thisMonths.get("startTimes").get(i), thisMonths.get("endTimes").get(i),"time(1d)","ENERGY_TOTAL");
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));
            }
            jsonObject.put("thisTime", xData);
            jsonObject.put("thisData", thisResult);

        }

        // 转标煤
        List<String> values=new ArrayList<String>((Collection<String>) jsonObject.get("thisData"));
        Double thisSum=values.stream().mapToDouble(i->Double.parseDouble(i)).sum();
        // 总电量
        jsonObject.put("thisSum",String.format("%.2f",thisSum));
        // 碳排放：用电量(度)*0.785/1000
        jsonObject.put("COSum",String.format("%.2f",thisSum*0.785/1000));
        //总标煤（tce）=总用电量（万度）*1.229
        jsonObject.put("TceSum",String.format("%.2f",thisSum*0.785/10000));



        return jsonObject;

    }



    public JSONObject getDeviceCollectTop(ReportManageVo bean){

        JSONObject jsonObject=new JSONObject();


        // 设备
        List<DeviceCollect> deviceCollects = collectMapper.getDeviceByAreas(bean);
        List<JSONObject> result = new ArrayList<>();


        int excellentCount=0;

        int goodCount=0;


        for(DeviceCollect x:deviceCollects){
//            result.add(JSONObject.parseObject("{\"name\":\"" + x.getName() + "\",\"value\":\"" + 0d + "\"}"));

            String sql = dataSheetService.getDeviceCollectTopSql(String.valueOf(x.getId()), "P_TOTAL", bean.getStartTime(), bean.getEndTime(), "MAX(val)");

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> item : maps) {
                sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
            }

            if (sum!=0d){

                sql = dataSheetService.getDeviceCollectTopSql(String.valueOf(x.getId()), "Dev_Type", bean.getStartTime(), bean.getEndTime(), "MAX(val)");
                query = influxdbService.query(sql);
                maps = influxdbService.queryResultProcess(query);
                Double typeSum=0d;
                for (Map<String, Object> item : maps) {
                    typeSum = typeSum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }

                if (typeSum!=0d){
                    sql = dataSheetService.getDeviceCollectTopSql(String.valueOf(x.getId()), "IPC", bean.getStartTime(), bean.getEndTime(), "MAX(val)");
                    query = influxdbService.query(sql);
                    maps = influxdbService.queryResultProcess(query);
                    Double ipcSum=0d;
                    for (Map<String, Object> item : maps) {
                        ipcSum = ipcSum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                    }
                    if (ipcSum==0d){
                        sum=0d;
                    }else {
                        sum=sum/ipcSum;
                    }

                }else{
                    sum=0d;
                }

            }

            if (sum>=85){
                excellentCount++;
            }else if (sum>=70&&sum<85){
                goodCount++;
            }

            result.add(JSONObject.parseObject("{\"name\":\"" + x.getName() + "\",\"value\":\"" + (sum==0d?0d:String.format("%.2f",sum) )+ "\",\"areaName\":\"" + x.getAreaName()+ "\",\"floorName\":\"" + x.getFloorName()+ "\"}"));

        };



        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));

        Collections.reverse(result);

        jsonObject.put("data",result);
        jsonObject.put("excellentCount",excellentCount);
        jsonObject.put("goodCount",goodCount);


        return jsonObject;
    }


    public JSONObject getAreaLoad(ReportManageVo bean){
        JSONObject jsonObject=new JSONObject();



        LocalDate start = LocalDate.parse(bean.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(bean.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<JSONObject> result = new ArrayList<>();

        String maxTime=bean.getStartTime();
        double maxValue=0d;

        while (!start.isAfter(end)) {
            String timeStr=start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String sql = dataSheetService.getAreaLoadSql(StringUtils.join(bean.getAreaIds(),","), "P_TOTAL",timeStr+" 00:00:00" , timeStr+" 23:59:59", "MAX(val)");

            // 时序数据库解析
            String timeTemp=null;
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> item : maps) {
                sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                timeTemp=(String) item.get("time");
            }

            if (sum!=0d){

                sql = dataSheetService.getAreaLoadSql(StringUtils.join(bean.getAreaIds(),","), "Dev_Type", bean.getStartTime(), bean.getEndTime(), "MAX(val)");
                query = influxdbService.query(sql);
                maps = influxdbService.queryResultProcess(query);
                Double typeSum=0d;
                for (Map<String, Object> item : maps) {
                    typeSum = typeSum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                }

                if (typeSum!=0d){
                    sql = dataSheetService.getAreaLoadSql(StringUtils.join(bean.getAreaIds(),","), "IPC", bean.getStartTime(), bean.getEndTime(), "MAX(val)");
                    query = influxdbService.query(sql);
                    maps = influxdbService.queryResultProcess(query);
                    Double ipcSum=0d;
                    for (Map<String, Object> item : maps) {
                        ipcSum = ipcSum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                    }
                    if (ipcSum==0d){
                        sum=0d;
                    }else {
                        sum=sum/ipcSum;
                    }

                }else{
                    sum=0d;
                }

            }


            if (sum>maxValue){
                maxTime=timeTemp;
                maxValue=sum;
            }

            result.add(JSONObject.parseObject("{\"name\":\"" + timeStr + "\",\"value\":\"" + (sum==0d?0d:String.format("%.2f",sum) )+ "\"}"));

            start = start.plusDays(1);
        }

        jsonObject.put("data",result);
        jsonObject.put("maxTime",maxTime);
        jsonObject.put("maxValue",maxValue);


        return jsonObject;

    }
}
