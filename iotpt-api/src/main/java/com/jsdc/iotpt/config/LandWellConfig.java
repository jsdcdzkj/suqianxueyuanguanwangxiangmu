package com.jsdc.iotpt.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.mapper.PatrolDataMapper;
import com.jsdc.iotpt.mapper.PatrolStatisticMapper;
import com.jsdc.iotpt.mapper.PlaceModelMapper;
import com.jsdc.iotpt.model.PatrolData;
import com.jsdc.iotpt.model.PatrolStatistic;
import com.jsdc.iotpt.model.PlaceModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: LandWellConfig
 * Description: 大楼巡检记录
 * date: 2024/11/20 10:08
 *
 * @author bn
 */
@Component
public class LandWellConfig {


    private static String URL="http://172.168.80.250:8010";
//    private static String URL="http://47.104.83.26:9523";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PlaceModelMapper placeModelMapper;
    @Autowired
    private PatrolDataMapper dataMapper;
    @Autowired
    private PatrolStatisticMapper statisticMapper;

    public static void main(String[] args) {
        String[] arrs={"17F电梯机房门口", "17F健身房内", "17F员工宿舍门口", "16F东南角", "16F东北角", "16F西北角", "16F西南角", "15F东南角", "15F东北角", "15F西北角",
        "15F西南角", "12F东南角", "12F东北角", "12F西北角", "12F西南角", "11F东南角", "11F东北角", "11F西北角", "11F西南角", "10F东南角", "10F东北角", "10F西北角",
                "10F西南角", "9F东侧消防通道", "9F西侧消防通道", "8F东南角", "8F东北角", "8F西北角", "8F西南角", "7F东侧消防通道", "7F西侧消防通道", "6F东侧消防通道", "6F西侧消防通道", "5F东侧消防通道", "5F西侧消防通道", "4F东侧消防通道", "4F西侧消防通道", "3F东侧消防通道", "3F西侧消防通道", "2F展厅机房", "2F展厅最远端", "-1F配电间", "-1F核心机房",
                "-1F采购仓库",
                "-1F酒水库",
        "1F车库最远端"};

        List<String> points= Arrays.asList(arrs);
        LandWellConfig landWellConfig=new LandWellConfig();
        for (int i = 0; i <points.size() ; i++) {
            landWellConfig.addPlace(points.get(i),String.format("%02d",i+1));
        }

    }

    /**
     *  部门查询
     */
    public void getDept(){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            URIBuilder builder=new URIBuilder(URL+"/api/dept");
            builder.setParameter("deptNumber", "01");
            builder.setParameter("deptName", "");
            builder.setParameter("searchChild", "true");

            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            // 发送请求并获取响应
            HttpResponse httpResponse = httpClient.execute(httpGet);

            JSONObject result = JSON.parseObject(EntityUtils.toString(httpResponse.getEntity()));

            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     *  部门新增
     */
    public void addDept(){
        try {
            // 构建URL对象
            URL url = new URL(URL+"/api/dept");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            // 设置请求头，表明发送的是表单数据
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("Connection", "close");
            connection.setDoOutput(true);

            // 构建参数字符串
            String params = URLEncoder.encode("Department_Name", StandardCharsets.UTF_8.toString()) + "=" +
                    URLEncoder.encode("江苏鼎驰物业公司", StandardCharsets.UTF_8.toString()) + "&" +
                    URLEncoder.encode("Department_Parent", StandardCharsets.UTF_8.toString()) + "=" +
                    URLEncoder.encode("01", StandardCharsets.UTF_8.toString());


            // 发送请求参数
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Response: " + response.toString());
        }catch (Exception e){

        }
    }

    /**
     *  部门新增
     */
    public void addPlace(String placeName,String card){
        try {
            // 构建URL对象
            URL url = new URL(URL+"/api/place");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            // 设置请求头，表明发送的是表单数据
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("Connection", "close");
            connection.setDoOutput(true);

            // 构建参数字符串
            String params = URLEncoder.encode("Checkpoint_Name", StandardCharsets.UTF_8.toString()) + "=" +
                    URLEncoder.encode(placeName, StandardCharsets.UTF_8.toString()) + "&" +
                    URLEncoder.encode("Checkpoint_Card", StandardCharsets.UTF_8.toString()) + "=" +
                    URLEncoder.encode(card, StandardCharsets.UTF_8.toString())+ "&" +
                    URLEncoder.encode("Department_Number", StandardCharsets.UTF_8.toString()) + "=" +
                    URLEncoder.encode("0101", StandardCharsets.UTF_8.toString());


            // 发送请求参数
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Response: " + response.toString());
        }catch (Exception e){

        }
    }


    /**
     *  同步巡检地点
     */
    public void getPlaceModel() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder(URL + "/api/place");
            builder.setParameter("deptNumber", "0101");
            builder.setParameter("placeCard", "");
            builder.setParameter("placeName", "");
            builder.setParameter("searchChild", "true");

            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            // 发送请求并获取响应
            HttpResponse httpResponse = httpClient.execute(httpGet);

            JSONObject result = JSON.parseObject(EntityUtils.toString(httpResponse.getEntity()));

            if (result.containsKey("success")&&1==result.getInteger("success")){
                JSONArray jsonArray=result.getJSONArray("data");

                jsonArray.forEach(x->{
                    JSONObject item= (JSONObject) x;
                    String checkPointCard=item.getString("CheckPoint_Card");
                    Integer checkPointNumber=item.getInteger("CheckPoint_Number");

                    PlaceModel modelData=placeModelMapper.
                            selectOne(Wrappers.<PlaceModel>lambdaQuery().
                                    eq(PlaceModel::getCheckPointCard,checkPointCard).
                                    eq(PlaceModel::getCheckPointNumber,checkPointNumber).
                                    eq(PlaceModel::getIsDel,0));

                    if (null==modelData){
                        PlaceModel placeModel=new PlaceModel();
                        placeModel.setCheckPointCard(checkPointCard);
                        placeModel.setCheckPointName(item.getString("CheckPoint_Name"));
                        placeModel.setCheckPointNumber(item.getInteger("CheckPoint_Number"));
                        placeModel.setDepartMentName(item.getString("DepartMent_Name"));
                        placeModel.setDepartMentNumber(item.getString("DepartMent_Number"));
                        placeModel.setIsDel(0);
                        placeModelMapper.insert(placeModel);
                    }else {
                        modelData.setCheckPointName(item.getString("CheckPoint_Name"));
                        modelData.setDepartMentName(item.getString("DepartMent_Name"));
                        modelData.setDepartMentNumber(item.getString("DepartMent_Number"));
                        placeModelMapper.updateById(modelData);
                    }
                });
            }

            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     *  历史数据
     *  按天生成计划，计划点位默认置为未巡
     */
    public void getPatrolData(){

        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            URIBuilder builder=new URIBuilder(URL+"/api/data");

            Integer maxVal=dataMapper.getMaxValueOfColumn();

            builder.setParameter("dataID", null==maxVal?"0":String.valueOf(maxVal));
//            builder.setParameter("dataID", "0");


            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            // 发送请求并获取响应
            HttpResponse httpResponse = httpClient.execute(httpGet);

            JSONObject result = JSON.parseObject(EntityUtils.toString(httpResponse.getEntity()));
            // 生成计划
            PatrolStatistic patrolStatistic=new PatrolStatistic();
            patrolStatistic.setPatrolDate(DateUtils.getLastday(new Date()));
            patrolStatistic.setLineName("消防安全线路");
            patrolStatistic.setPlanTime("60");
            patrolStatistic.setUserName("安全组");
            patrolStatistic.setActualTime("0");
            patrolStatistic.setPatrolResult(1);
            patrolStatistic.setSyncTime(new Date());
            statisticMapper.insert(patrolStatistic);

            // 所有点位
            List<PlaceModel> placeModels=placeModelMapper.
                    selectList(Wrappers.<PlaceModel>lambdaQuery().
                            eq(PlaceModel::getIsDel,0));

            // 当天所有数据标记为未巡
            placeModels.forEach(x->{
                PatrolData patrolData=new PatrolData();
                patrolData.setStatisticId(patrolStatistic.getId());
                patrolData.setPatrolDataId(0);
                patrolData.setIsPatrol(1);
                patrolData.setPlaceName(x.getCheckPointName());
                dataMapper.insert(patrolData);
            });

            if (result.containsKey("success")&&1==result.getInteger("success")){

                // 所有数据
                JSONArray data=result.getJSONArray("data");
                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                Set<String> dates=new HashSet<>();
                // 数据插入
                for (int i = 0; i < data.size(); i++) {
                    JSONObject item=(JSONObject) data.get(i);

                    if (maxVal<item.getInteger("id")){
                        maxVal=item.getInteger("id");
                    }

                    // 根据日期判断数据
                    List<PatrolData> patrolDatas=dataMapper.
                            selectList(Wrappers.<PatrolData>lambdaQuery().
                                    eq(PatrolData::getPlaceName, item.getString("Place_Name")).
                                    apply("PATROLTIME = TO_TIMESTAMP('"+item.getString("Patrol_Time")+"', 'YYYY-MM-DD HH24:MI:SS')"));
                    // 数据没有重复
                    if (patrolDatas.isEmpty()){
                        PatrolData patrolData=new PatrolData();
                        patrolData.setPatrolDataId(item.getInteger("id"));
                        patrolData.setIsPatrol(0);
                        patrolData.setUserName(item.getString("User_Name"));
                        patrolData.setPlaceName(item.getString("Place_Name"));
                        patrolData.setPatrolTime(dateFormat.parse(item.getString("Patrol_Time")));
                        dataMapper.insert(patrolData);
                        dates.add(dateFormat.format(patrolData.getPatrolTime()).substring(0,10));
                    }

                }
                // 日期遍历
                dates.forEach(x->{
                    PatrolStatistic bean=statisticMapper.
                            selectOne(Wrappers.<PatrolStatistic>lambdaQuery().
                                    apply("TRUNC(PATROLDATE) = TO_DATE('"+x+"', 'YYYY-MM-DD')"));

                    if (bean!=null){
                        List<PatrolData> patrolDataList=dataMapper.
                                selectList(Wrappers.<PatrolData>lambdaQuery().
                                        apply("TRUNC(PATROLTIME) = TO_DATE('"+x+"', 'YYYY-MM-DD')"));

                        Date maxDate=null;
                        Date minDate=null;
                        for (int i = 0; i < patrolDataList.size(); i++) {
                            PatrolData dd=patrolDataList.get(i);
                            dd.setStatisticId(bean.getId());
                            if (null==maxDate||null==minDate){
                                maxDate=dd.getPatrolTime();
                                minDate=dd.getPatrolTime();
                            }else {
                                if (maxDate.before(dd.getPatrolTime())){
                                    maxDate=dd.getPatrolTime();
                                }
                                if (minDate.after(dd.getPatrolTime())){
                                    minDate=dd.getPatrolTime();
                                }
                            }
                            dataMapper.updateById(dd);
                            // 删除此日期，该点位未巡检数据
                            dataMapper.delete(Wrappers.<PatrolData>lambdaQuery().
                                    eq(PatrolData::getIsPatrol,1).
                                    eq(PatrolData::getPlaceName,dd.getPlaceName()).
                                    eq(PatrolData::getStatisticId,bean.getId()));
                        }

                        if (null==maxDate||null==minDate){
                            bean.setActualTime("0");
                        }else {
                            long diffInMillis = maxDate.getTime() - minDate.getTime();
                            bean.setActualTime(String.valueOf(diffInMillis / (60 * 1000)));
                        }

                        List<PatrolData> patrolDatas=dataMapper.
                                selectList(Wrappers.<PatrolData>lambdaQuery().
                                        eq(PatrolData::getStatisticId,bean.getId()).
                                        eq(PatrolData::getIsPatrol,1));

                        if (patrolDatas.isEmpty()){
                            bean.setPatrolResult(0);
                        }

                        statisticMapper.updateById(bean);
                    }

                });

                System.out.println();


                patrolStatistic.setPatrolDataId(maxVal);
                statisticMapper.updateById(patrolStatistic);

            }

            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     *  同步线路
     */
    public void getLineextd(){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            URIBuilder builder=new URIBuilder(URL+"/api/lineextd");
            builder.setParameter("deptNumber", "01");
            builder.setParameter("lineName", "");
            builder.setParameter("searchChild", "true");

            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            // 发送请求并获取响应
            HttpResponse httpResponse = httpClient.execute(httpGet);

            JSONObject result = JSON.parseObject(EntityUtils.toString(httpResponse.getEntity()));

            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }








}
