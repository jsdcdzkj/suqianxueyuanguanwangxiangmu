package com.jsdc.iotpt;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.model.DataSheet;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  influxDB使用demo
 */
@SpringBootTest
public class InfluxDBDemo {
    @Autowired
    private InfluxdbService influxdbService;

    /**
     * 插入方法
     */
    @Test
    public void testInsert(){
        //时间，不传则固定为当前时间
        long time = new Date().getTime();
        //tags 类型、id等需要频繁作为条件的字段，类似关系型数据库需要加索引的字段
        //必须为字符串
        Map<String,String> tagsMap = new HashMap<>();
        tagsMap.put("gateWayId","666888");
        tagsMap.put("deviceId","6DG8X5S6EF5D4S");
        tagsMap.put("channelId","");
        //fields 监测值
        Map<String,Object> fieldsMap = new HashMap<>();
        fieldsMap.put("val",Float.parseFloat("22.3"));
        fieldsMap.put("p_name","hahaxixi");
        //传入time
//        influxdbService.insert("testTable",time,tagsMap,fieldsMap);
        //不传time
        influxdbService.insert("testTable",tagsMap,fieldsMap);
    }

    /**
     * 列表查询方法
     */
    @Test
    public void queryList() {
        // TODO: 2023/7/18 根据东八区时间为条件查询，需要调用toInfluxDBTimeFormat方法转换成零时区进行查询
        Date date = DateUtil.parseDateTime("2023-07-15 11:49:00");
        // TODO: 2023/7/18 TimeUtil.toInfluxDBTimeFormat为本地时间转换成influx
        String s = TimeUtil.toInfluxDBTimeFormat(date.getTime());
        String sql = "select * from testTable where time >"+"'"+s+"'";
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        // TODO: 2023/7/18 当通过maps查询出结果，时间为influx时间，转成date类型会自动转换成东八区时间
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        // TODO: 2023/7/18 如果需要自己转换，可以调用TimeUtil.fromInfluxDBTimeFormat
//        TimeUtil.fromInfluxDBTimeFormat(timeStr);
        //转换成实体对象
        List<DataSheet> data = JSONObject.parseArray(JSONObject.toJSONString(maps),DataSheet.class);
        System.out.println("解析完毕");
        System.out.println(JSONObject.toJSONString(data));
    }

    /**
     * 列表分页查询方法
     */
    @Test
    public void queryListPage() {
        Integer pageSize = 10;
        Integer pageNo = 1;
        String pageSql = " limit "+pageSize+" offset "+(pageNo - 1)*pageSize;
        String sql = "select * from testTable"+pageSql;
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        //转换成实体对象
        List<DataSheet> data = JSONObject.parseArray(JSONObject.toJSONString(maps),DataSheet.class);
        System.out.println("");
    }
    /**
     * 分组统计方法
     */
    @Test
    public void queryReport() {
        //按照deviceId分组 求每一分钟的平均数
        String sql = "select mean(val) from testTable group by time(1m),deviceId";
        QueryResult query = influxdbService.query(sql);
        this.anyResult(query);
        System.out.println("");
    }

    public void anyResult(QueryResult queryResult){
        List<QueryResult.Result> resultList = queryResult.getResults();
        for(QueryResult.Result query : resultList){
            List<QueryResult.Series> seriesList = query.getSeries();
            for(QueryResult.Series series : seriesList){
                Map<String, String> tags = series.getTags();
                List<String> columns = series.getColumns();
                String[] keys = columns.toArray(new String[columns.size()]);
                List<List<Object>> values = series.getValues();
                System.out.println("测试");
            }
        }
    }

    public static void main(String[] args) {
        Date now = DateUtil.parseDateTime("2023-07-15 11:49:00");
        long now_time = now.getTime();
        System.out.println(TimeUtil.toInfluxDBTimeFormat(now_time));
        long time = TimeUtil.fromInfluxDBTimeFormat("2023-05-30T00:00:00Z");
        Date date = new Date(time);
        System.out.println(DateUtil.formatDateTime(date));

    }
}
