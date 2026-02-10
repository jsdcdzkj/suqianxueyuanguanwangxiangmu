package com.jsdc.iotpt.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.DataSheet;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.sys.*;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DataSheetVo;
import com.jsdc.iotpt.vo.PowerConsumptionVo;
import com.jsdc.iotpt.vo.TemAndHumVo;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnergyReportService {

    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private SysBuildMapper sysBuildMapper;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;
    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;


    /**
     * 能源看板月度能耗
     * 展示数据均采用四舍五入的原则
     * @return
     */
    public PowerConsumptionVo monthlyEnergy(){
        PowerConsumptionVo powerConsumptionVo = new PowerConsumptionVo();

        Integer year = new Date().getYear()+1900;
        Integer month = new Date().getMonth()+1;

        Date date = DateUtil.parseDateTime(year+"-"+month+"-01 00:00:00");
        String s = TimeUtil.toInfluxDBTimeFormat(date.getTime());
        //N是信号 暂定N
        String sql = "select spread(val) from datasheet where buildId != null AND areaId = null AND floorId = null AND channelId = 'N' AND time >= '"+s+ "' AND time <= now()";
        QueryResult query = influxdbService.query(sql);
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

        //转换成实体对象
        List<PowerConsumptionVo> data = JSONObject.parseArray(JSONObject.toJSONString(maps),PowerConsumptionVo.class);
        if (CollectionUtils.isNotEmpty(data) && null != data.get(0).getSpread() ){
            double temp_kWh = data.get(0).getSpread();
            //月用电取整（四舍五入）
            Integer month_kWh = (int)Math.round(temp_kWh) ;
            powerConsumptionVo.setMonth_kWh(month_kWh);

            //总碳排放量（吨）=用电量(度)*0.785/1000
            double temp_t = month_kWh*0.785/1000;
            Integer month_t = (int)Math.round(temp_t) ;
            powerConsumptionVo.setMonth_t(month_t);

            //当量值：总标煤（tce）=总用电量（万度）*1.229
            double temp_tce = month_kWh / 10000*1.229;
            Integer month_tce = (int)Math.round(temp_tce) ;
            powerConsumptionVo.setMonth_tce(month_tce);
        }else {
            powerConsumptionVo.setMonth_kWh(0);
            powerConsumptionVo.setMonth_tce(0);
            powerConsumptionVo.setMonth_t(0);
        }

        return powerConsumptionVo;
    }

    /**
     * 查询各个楼层的用电量以及碳排放
     * @return
     */
    public PowerConsumptionVo monthFloorEnergy(PowerConsumptionVo bean){
        PowerConsumptionVo powerConsumptionVo = new PowerConsumptionVo();
        //1.根据能源类型，是否总设备，楼宇ID,楼层ID 查询出采集设备的ID集合（楼宇或者楼层可能存在多个总设备）
        DataSheetVo bean1  = new DataSheetVo();
        SysBuild sysBuild=sysBuildMapper.selectList(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel,0).orderByDesc(SysBuild::getCreateTime)).get(0);

        bean1.setBuildId(sysBuild.getId());

        // 能源类型为用电的设备
        bean1.setEnergyType(1);

        List<String> ids = new ArrayList<>();
        for (int i = 0 ; i < bean.getFloorList().size() ; i++){
            bean1.setFloorId(bean.getFloorList().get(i));
            // 获取采集设备
            List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean1);
            //如果标记是总设备并且楼层ID不为空 区域ID为空就获取此数据的ID
            List<String> tempIds = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1)&&
                    (0 != x.getLogicalFloorId())&&(0 == x.getLogicalAreaId())).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());
            if (tempIds.isEmpty()) {
                tempIds = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 0)).map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }

            ids.addAll(tempIds);
        }





        //2.组装时间
        //获取当前时间
        Date date = new Date();
        Integer year = date.getYear()+1900;
        Integer month = date.getMonth()+1;
        String tempMonth = month+"";
        if (month < 10){
            tempMonth = 0+tempMonth;
        }
        Integer day = date.getDay();

        Integer sky = date.getDate();
        String tempSky = sky+"";
        if (sky < 10){
            tempSky = 0+tempSky;
        }
        Integer week = sky - day + 1 ;
        String tempWeek =week+"";
        if (week < 10){
            tempWeek = 0+tempWeek;
        }

        Map<String,Double> resultMap = new HashMap<>();
        if (0 == bean.getType()){
            for (int i = 0 ; i < 24 ; i++){
                String tempValue = i + ":00";
                if (i < 10){
                    tempValue = "0"+i+":00";
                }
                resultMap.put(tempValue,Double.parseDouble("0"));
            }
        }else if (2 == bean.getType()){
            LocalDate localDate = LocalDate.now();
            for (int i = 1 ; i<= localDate.lengthOfMonth() ; i++){
                String tempDay = i+"";
                if (i < 10){
                   tempDay = 0+tempDay;
                }
                resultMap.put(year+"-"+tempMonth+"-"+tempDay,Double.parseDouble("0"));
            }
        }


        //如果是年
        if (3 == bean.getType()){
            return yearFloorEnergy(year,month,bean,ids);
        }else {
            //3.如果是其他组装sql语句
            for (String idstr : ids){
                StringBuffer sql = new StringBuffer();
                sql.append("select spread(val) from datasheet ");
                sql.append("WHERE deviceCollectId= '"+idstr+"'");
                //信号
                sql.append(" AND channelId = 'ENERGY_TOTAL' ");

                if (null == bean.getType()){
                    return powerConsumptionVo;
                }else if (0 == bean.getType()){
                    //按日查询（今天）
                    String value = year+"-"+tempMonth+"-"+tempSky+" 00:00:00";
                    String value1 = year+"-"+tempMonth+"-"+tempSky+" 23:59:59";
                    sql.append(" AND time >= '"+value+"'");
                    sql.append(" AND time < '"+value1+"'");
                    sql.append(" group by time(1h)");
                }else if (1 == bean.getType()){
                    //按周

                    String value = year+"-"+tempMonth+"-"+tempWeek+" 00:00:00";
                    sql.append(" AND time >= '"+value+"'");
                    String value1 = year+"-"+tempMonth+"-"+tempSky+" 23:59:59";
                    sql.append(" AND time < '"+value1+"'");
                    sql.append(" group by time(1d)");
                }else if (2 == bean.getType()){
                    //按月
                    String value = year+"-"+tempMonth+"-01 00:00:00";
                    sql.append(" AND time >= '"+value+"'");
                    String value1 = year+"-"+tempMonth+"-"+tempSky+" 23:59:59";
                    sql.append(" AND time < '"+value1+"'");
                    sql.append(" group by time(1d)");
                }


                QueryResult query = influxdbService.query(sql.toString());
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

                if (CollectionUtils.isNotEmpty(maps)){
                    for (Map<String, Object> x : maps) {
                        double sum =  (null == x.get("spread") ? 0d : (Double) x.get("spread"));
                        String tempValue  = (String)x.get("time");
                        String value = "";
                        if (0 == bean.getType()){
                            value = tempValue.substring(11,16);
                        }else {
                            value = tempValue.substring(0,10);
                        }


                        if (resultMap.containsKey(value)){
                            resultMap.put(value,resultMap.get(value) + sum);
                        }else {
                            resultMap.put(value,sum);
                        }


                    }

                }else {
                    if (1 == bean.getType()){
                        for (int i = Integer.valueOf(tempWeek) ; i <= Integer.valueOf(tempSky); i++){
                            if (!resultMap.containsKey(year+"-"+tempMonth+"-"+tempSky)){
                                resultMap.put(year+"-"+tempMonth+"-"+tempSky,Double.valueOf(0));
                            }

                        }
                    }
                }

            }


            List<Double> yValue1 = new ArrayList<>();
            List<Double> yValue2 = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(resultMap)){
                List<String> xList = resultMap.keySet().stream().sorted().collect(Collectors.toList());

//                if (0 == bean.getType()){
//                    Integer num = date.getHours()+1;
//                    xList = xList.subList(0,num);
//                }
                powerConsumptionVo.setXValue(xList);
                for (String x : xList){

                    double temp_kWh = resultMap.get(x);
                    BigDecimal bd = new BigDecimal(temp_kWh);
                    double month_kWh = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                    //月用电取整（四舍五入）
//                    Integer month_kWh = (int)Math.round(temp_kWh) ;
                    yValue1.add(month_kWh);

                    //总碳排放量（吨）=用电量(度)*0.785/1000
                    double temp_t = month_kWh*0.785/1000;

                    BigDecimal bd1 = new BigDecimal(temp_t);
                    double month_t = bd1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//                    Integer month_t = (int)Math.round(temp_t) ;
                    yValue2.add(month_t);
                }
            }
            powerConsumptionVo.setYValue1(yValue1);
            powerConsumptionVo.setYValue2(yValue2);
            return powerConsumptionVo;
        }
    }

    public PowerConsumptionVo yearFloorEnergy(Integer year ,Integer month,PowerConsumptionVo bean,List<String> ids){

        Map<String,Double> resultMap = new HashMap<>();

        if (3 == bean.getType()){
            for (int i = 1 ; i<= 12 ; i++){
                String tempMon = i+"";
                if (i < 10){
                    tempMon = 0+tempMon;
                }
                resultMap.put(year+"-"+tempMon,Double.parseDouble("0"));
            }

        }

        for (String idstr : ids){
            for (int i= 0 ; i < month ; i++){
                StringBuffer sql = new StringBuffer();
                sql.append("select spread(val) from datasheet ");
                sql.append("WHERE deviceCollectId= '"+idstr+"'");
                //信号
                sql.append(" AND channelId = 'ENERGY_TOTAL' ");
                //按年
                Integer temp = i + 1;
                Integer temp1 = i +2 ;
                String val1 = temp+"";
                if (temp < 10){
                    val1 = 0 + val1;
                }
                if (null == bean.getType()){
                  return new PowerConsumptionVo();
                }else if(3 == bean.getType()){
                    String val2 = temp1+"";
                    if (temp1 < 10){
                        val2 = 0 + val2;
                    }
                    Integer tempYear = year+1;
                    String value1 = year+"-"+val1+"-01 00:00:00";
                    String value2 = "";
                    if (temp == 12){
                        value2 = tempYear+"-01-01 00:00:00";
                    }else {
                        value2 = year+"-"+val2+"-01 00:00:00";
                    }

                    sql.append(" AND time >= '"+value1+"'");
                    sql.append(" AND time <= '"+value2+"'");
                }

                QueryResult query = influxdbService.query(sql.toString());
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

                if (CollectionUtils.isNotEmpty(maps)){
                    for (Map<String, Object> x : maps) {
                        double sum =  (null == x.get("spread") ? 0d : (Double) x.get("spread"));
                        String tempValue  = (String)x.get("time");
                        String value = tempValue.substring(0,7);
                        if (resultMap.containsKey(value)){
                            resultMap.put(value,resultMap.get(value) + sum);
                        }else {
                            resultMap.put(value,sum);
                        }
                    }
                }
            }
        }
        PowerConsumptionVo powerConsumptionVo = new PowerConsumptionVo();
        List<Double> yValue1 = new ArrayList<>();
        List<Double> yValue2 = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultMap)){
            List<String> xList = resultMap.keySet().stream().sorted().collect(Collectors.toList());

            powerConsumptionVo.setXValue(xList);
            for (String x : xList){

                double temp_kWh = resultMap.get(x);
                //月用电取整（四舍五入）
                BigDecimal bd = new BigDecimal(temp_kWh);
                double month_kWh = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//                Integer month_kWh = (int)Math.round(temp_kWh) ;
                yValue1.add(month_kWh);

                //总碳排放量（吨）=用电量(度)*0.785/1000
                double temp_t = month_kWh*0.785/1000;
                BigDecimal bd1 = new BigDecimal(temp_t);
                double month_t = bd1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//                Integer month_t = (int)Math.round(temp_t) ;
                yValue2.add(month_t);
            }
        }
        powerConsumptionVo.setYValue1(yValue1);
        powerConsumptionVo.setYValue2(yValue2);
        return powerConsumptionVo;
    }

    /**
     * 获取配电间温度湿度
     * @return
     */
    public TemAndHumVo getTemHum(){

        //1.查询所有区域类型是配电间的区域ID(orcl数据库查询)
        //todo 改造 从字典获取配电间value
        List<SysDict> dictList = sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery()
                .eq(SysDict::getDictType,"area_type")
                .eq(SysDict::getDictLabel,"配电间")
        );
        SysDict sysDict = dictList.get(0);
        String s = sysDict.getDictValue();


        List<SysBuildArea> sysAreaDepts = sysBuildAreaMapper.selectList(Wrappers.<SysBuildArea>lambdaQuery()
                .eq(SysBuildArea::getIsDel, G.ISDEL_NO)
                .eq(SysBuildArea::getAreaType,s)
        );
        List<Integer> areaIdList = sysAreaDepts.stream().map(SysBuildArea::getId).collect(Collectors.toList());




        //2.查询sql;
        List<TemAndHumVo> temList = new ArrayList<>();
        List<TemAndHumVo> humList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(areaIdList)){
            for (int i = 0 ; i < areaIdList.size() ; i++){
                //温度
                String sql = "SELECT LAST(val) FROM  datasheet WHERE areaId = '"+areaIdList.get(i)+"' AND channelId = 'W' ";
                QueryResult query = influxdbService.query(sql);
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
                //转换成实体对象
                List<TemAndHumVo> data = JSONObject.parseArray(JSONObject.toJSONString(maps),TemAndHumVo.class);
                if (CollectionUtils.isNotEmpty(data)){
                    TemAndHumVo temAndHumVo = data.get(0);
                    temAndHumVo.setAreaId(areaIdList.get(i));
                    temList.add(temAndHumVo);
                }

                //湿度
                String sql1 = "SELECT LAST(val) FROM  datasheet WHERE areaId = '"+areaIdList.get(i)+"' AND channelId = 'S' ";
                QueryResult query1 = influxdbService.query(sql1);
                List<Map<String, Object>> maps1 = influxdbService.queryResultProcess(query1);
                //转换成实体对象
                List<TemAndHumVo> data1 = JSONObject.parseArray(JSONObject.toJSONString(maps1),TemAndHumVo.class);
                if (CollectionUtils.isNotEmpty(data1)){
                    TemAndHumVo temAndHumVo = data1.get(0);
                    temAndHumVo.setAreaId(areaIdList.get(i));
                    humList.add(temAndHumVo);
                }
            }
        }

        //取平均值
        double tem = 0;
        double hum = 0;
        if (CollectionUtils.isNotEmpty(temList)){
            for (TemAndHumVo temp : temList){
                tem = tem+temp.getVal();
            }
            tem = tem/temList.size();
        }
        if (CollectionUtils.isNotEmpty(humList)){
            for (TemAndHumVo temp : temList){
                hum = hum+temp.getVal();
            }
            hum = hum/temList.size();
        }
        TemAndHumVo bean = new TemAndHumVo();
        bean.setTemperature(tem);
        bean.setHumidity(hum);
        return bean;
    }

    /**
     * 获取楼层
     * @return
     */
    public List<SysBuildFloor> getFloorList(){

        SysBuild sysBuild=sysBuildMapper.selectList(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel,0).orderByDesc(SysBuild::getCreateTime)).get(0);

        List<SysBuildFloor> list = sysBuildFloorMapper.selectList(Wrappers.<SysBuildFloor>lambdaQuery()
                .eq(SysBuildFloor::getIsDel,0)
                .eq(SysBuildFloor::getDictBuilding,sysBuild.getId())
                .orderByAsc(SysBuildFloor::getSort)
        );

        return list;
    }


    /**
     * 获取楼层
     * @return
     */
    public List<SysBuildFloor> getGisFloorList(){

        SysBuild sysBuild=sysBuildMapper.selectList(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel,0).orderByDesc(SysBuild::getCreateTime)).get(0);

        List<SysBuildFloor> list = sysBuildFloorMapper.selectList(Wrappers.<SysBuildFloor>lambdaQuery()
                .eq(SysBuildFloor::getIsDel,0)
                .eq(SysBuildFloor::getDictBuilding,sysBuild.getId())
                .orderByAsc(SysBuildFloor::getSort).eq(SysBuildFloor::getIsLargeScreenDisplay,1)
        );

        return list;
    }

    public List<SysBuildFloor> getFloorListByBuild(){
        List<SysBuildFloor> list = sysBuildFloorMapper.selectList(Wrappers.<SysBuildFloor>lambdaQuery()
                .eq(SysBuildFloor::getIsDel,0)
                // 先写死, 从配置文件中取
//                .eq(SysBuildFloor::getDictBuilding, 122)

                .orderBy(true, true, SysBuildFloor::getSort));


        return list;
    }

    public Map<String,Object> personInOutCount() throws ClientException {
        Map map=new HashMap();
        return map;
    }
}
