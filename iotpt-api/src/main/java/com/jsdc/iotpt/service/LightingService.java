package com.jsdc.iotpt.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.dto.chintcloud.*;
import com.jsdc.iotpt.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class LightingService {


    @Value("${chintCloud.url}")
    private String baseUrl;

    @Value("${chintCloud.loginName}")
    private String loginName;

    @Value("${chintCloud.password}")
    private String password;

    @Value("${chintCloud.projectId}")
    private String projectId;

    private static final String port1 = "8003";

    private static final String port2 = "8004";


    // Redis存放请求token Key
    private static final String REDIS_ACCESS_TOKEN_KEY = "chintcloud:accesstoken";
    // Redis存放刷新token Key
    private static final String REDIS_REFRESH_TOKEN_KEY = "chintcloud:refreshtoken";
    // 接口超时时间（毫秒）
    private static final int TIMEOUT = 60000;
    // API 获取Token
    private static final String LOGIN_URL = "/api/admin/Admin/LogIn";
    // API 获取所有网关设备信息
    private static final String GATEWAY_LIST_URL = "/api/datacenter/GatewayDevice/GetList";
    // API 获取所有组合设备信息
    private static final String TERMINAL_DEVICE_TREE_URL = "/api/datacenter/TerminalDevice/Gettree";
    // API 获取设备的最后上传数据时间
    private static final String GET_DEVICE_STATE_URL = "/api/DeviceInfo/GetDeviceStateByProject";
    // API 获取网关设备所有点位列表
    private static final String POINT_LIST_URL = "/api/datacenter/GatewayDevicePoint/GetPages";
    // API 获取单点位实时值
    private static final String SINGLE_POINT_REALTIME_DATA_URL = "/api/RealTimeData/Get";
    // API 获取批量点位实时值
    private static final String BATCH_POINT_REALTIME_DATA_URL = "/api/RealTimeData/BulkGet";
    // API 设置值，单点
    private static final String SET_SINGLE_POINT_REALTIME_DATA_URL = "/api/RealTimeData/Set";
    // API 批量设置值，多点设置相同值
    private static final String SET_BATCH_POINT_REALTIME_DATA_URL = "/api/RealTimeData/Sets";
    // API 批量设置值。多点设置不同值
    private static final String SET_SINGLE_POINT_DIFFERENT_VALUE_URL = "/api/RealTimeData/BulkSet";
    // API 多点设置相同值,多个组
    private static final String SET_BATCH_POINT_DIFFERENT_VALUE_URL = "/api/RealTimeData/BulkSets";
    // API 获取某段时间的历史数据
    private static final String TIME_PERIOD_HISTORY_DATA_URL = "/api/HistoryData/GetPages";
    // API 获取单点/多点统计数据
    private static final String STATISTICS_DATA_URL = "/api/StatisticsData/BulkGet";

    /**
     * 获取Token
     * @param redisKey redisKey
     * @param force 是否强制刷新
     * @return token
     */
    private String getToken(String redisKey, boolean force) {
        Object value = RedisUtils.getBeanValue(redisKey);
        if (StringUtils.isNull(value) || force) {
            JSONObject args = new JSONObject();
            args.put("loginName", loginName);
            args.put("password", password);
            args.put("verifyCode", DateUtils.toDay("YYMMdd"));
            String response = post(LOGIN_URL, port1, args.toJSONString(), null);
            ChintCloudDto<TokenDto> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<TokenDto>>() {});
            assert dto != null : "请求失败";
            if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
                log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
                throw new RuntimeException("请求失败");
            }
            TokenDto data = dto.getData();
            TokenDto.TokenContent accessToken = data.getAccessToken();
            TokenDto.TokenContent refreshToken = data.getRefreshToken();
            RedisUtils.setBeanValue(REDIS_ACCESS_TOKEN_KEY, accessToken.getTokenContent(), minuteDifference(DateUtil.parseDateTime(accessToken.getExpires())));
            RedisUtils.setBeanValue(REDIS_REFRESH_TOKEN_KEY, refreshToken.getTokenContent(), minuteDifference(DateUtil.parseDateTime(refreshToken.getExpires())));
            value = RedisUtils.getBeanValue(redisKey);
        }
        return value.toString();
    }


    /**
     * 获取所有网关设备信息
     * @return list
     */
    public List<GatewayDto> gatewayList() {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("projectId", projectId);
        String response = post(GATEWAY_LIST_URL, port1, args.toJSONString(), token);
        ChintCloudDto<ChintCloudPageDto<GatewayDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<ChintCloudPageDto<GatewayDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        if (StringUtils.isNull(dto.getData())) {
            return Collections.emptyList();
        }
        return dto.getData().getItems();
    }


    /**
     * 获取所有组合设备信息
     * @return list
     */
    public List<TerminalDeviceTreeDto> terminalDeviceTree() {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("pageIndex", 1);
        args.put("pageSize", 1000);
        args.put("projectId", projectId);
        String response = post(TERMINAL_DEVICE_TREE_URL, port1, args.toJSONString(), token);
        ChintCloudDto<List<TerminalDeviceTreeDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<List<TerminalDeviceTreeDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        if (StringUtils.isNull(dto.getData())) {
            return Collections.emptyList();
        }
        return dto.getData();
    }

    /**
     * 获取设备的最后上传数据时间
     */
    public DeviceStateDto GetDeviceStateByProject() {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("projectId", projectId);
        String response = post(GET_DEVICE_STATE_URL, port2, args.toJSONString(), token);
        ChintCloudDto<DeviceStateDto> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<DeviceStateDto>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        return dto.getData();
    }




    /**
     * 获取网关设备所有点位列表
     * @param deviceId 网关设备Id
     * @return list
     */
    public List<DevicePointDto> getDevicePointList(String deviceId) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("pageIndex", 1);
        args.put("pageSize", 1000);
        args.put("deviceId", deviceId);
        args.put("projectId", projectId);
        String response = post(POINT_LIST_URL, port1, args.toJSONString(), token);
        ChintCloudDto<ChintCloudPageDto<DevicePointDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<ChintCloudPageDto<DevicePointDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        if (StringUtils.isNull(dto.getData())) {
            return Collections.emptyList();
        }
        return dto.getData().getItems();
    }

    /**
     * 获取实时值，单点
     * @param pointId 点位id,网关点位id和组合点位id均可
     * @return PointRealtimeDataVo
     */
    public PointRealtimeDataDto getPointRealtimeData(String pointId) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("pointId", pointId);
        String response = post(SINGLE_POINT_REALTIME_DATA_URL, port2, args.toJSONString(), token);
        ChintCloudDto<PointRealtimeDataDto> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<PointRealtimeDataDto>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        return dto.getData();
    }

    /**
     * 获取实时值，多点批量
     * @param pointIds 点位Id列表 对应点位列表的 `storagePoint`
     * @return List<PointRealtimeDataVo>
     */
    public List<PointRealtimeDataDto> getPointRealtimeData(List<String> pointIds) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("pointIds", pointIds);
        String response = post(BATCH_POINT_REALTIME_DATA_URL, port2, args.toJSONString(), token);
        ChintCloudDto<List<PointRealtimeDataDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<List<PointRealtimeDataDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        return dto.getData();
    }


    /**
     * 设置值，单点
     * @param pointId 点位id
     * @param value 0-关，1-开
     */
    public void setPointRealtimeData(String pointId, String value) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("pointId", pointId);
        args.put("value", value);
        String response = post(SET_SINGLE_POINT_REALTIME_DATA_URL, port2, args.toJSONString(), token);
        ChintCloudDto<PointValueStatusDto> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<PointValueStatusDto>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        PointValueStatusDto data = dto.getData();
        assert data != null : "请求失败";
        switch (data.getState()) {
            case 2:
                throw new RuntimeException("影子状态");
            case 3:
                throw new RuntimeException("虚拟点，无法设置");
            case 4:
                throw new RuntimeException("点不存在");
        }
    }

    /**
     * 批量设置值，多点设置相同值
     * @param pointIds 各点位id集合
     * @param value 0-关，1-开
     */
    public boolean setPointRealtimeData(List<String> pointIds, String value) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        JSONObject args = new JSONObject();
        args.put("pointIds", pointIds);
        args.put("value", value);
        String response = post(SET_BATCH_POINT_REALTIME_DATA_URL, port2, args.toJSONString(), token);
        ChintCloudDto<List<PointValueStatusDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<List<PointValueStatusDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        List<PointValueStatusDto> data = dto.getData();
        for (PointValueStatusDto item : data) {
            if (!Objects.equals(item.getState(), 1)) {
                log.error("【泰杰赛智慧照明系统】点位 {} 设置失败, state = {}", item.getPointId(), item.getState());
                return false;
            }
        }
        return true;
    }


    /**
     * 批量设置值。多点设置不同值
     * @param list List<PointValue> list
     */
    public void setPointRealtimeData(List<PointValue> list) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        String response = post(SET_SINGLE_POINT_DIFFERENT_VALUE_URL, port2, JSON.toJSONString(list), token);
        ChintCloudDto<List<PointValueStatusDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<List<PointValueStatusDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        List<PointValueStatusDto> data = dto.getData();
        for (PointValueStatusDto item : data) {
            if (!Objects.equals(item.getState(), 1)) {
                log.error("【泰杰赛智慧照明系统】点位 {} 设置失败, state = {}", item.getPointId(), item.getState());
            }
        }
    }

    /**
     * 多点设置相同值,多个组
     * @param list Collection<PointsValue> list
     */
    public void setPointRealtimeData(Collection<PointsValue> list) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        String response = post(SET_BATCH_POINT_DIFFERENT_VALUE_URL, port2, JSON.toJSONString(list), token);
        ChintCloudDto<List<PointValueStatusDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<List<PointValueStatusDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        List<PointValueStatusDto> data = dto.getData();
        for (PointValueStatusDto item : data) {
            if (!Objects.equals(item.getState(), 1)) {
                log.error("【泰杰赛智慧照明系统】点位 {} 设置失败, state = {}", item.getPointId(), item.getState());
            }
        }
    }

    /**
     * 获取某段时间的历史数据
     * @param queryDto queryDto
     * @return ChintCloudPageDto<HistoryDataDto>
     */
    public ChintCloudPageDto<HistoryDataDto> historyData(HistoryDataPageQueryDto queryDto) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        String response = post(TIME_PERIOD_HISTORY_DATA_URL, port2, JSON.toJSONString(queryDto), token);
        ChintCloudDto<ChintCloudPageDto<HistoryDataDto>> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<ChintCloudPageDto<HistoryDataDto>>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        return dto.getData();
    }

    /**
     * 获取单点/多点统计数据
     * @param queryDto queryDto
     */
    public StatisticsDataDto statisticsData(StatisticsDataQueryDto queryDto) {
        String token = getToken(REDIS_ACCESS_TOKEN_KEY, false);
        String response = post(STATISTICS_DATA_URL, port2, JSON.toJSONString(queryDto), token);
        ChintCloudDto<StatisticsDataDto> dto = JSONObject.parseObject(response, new TypeReference<ChintCloudDto<StatisticsDataDto>>() {});
        assert dto != null : "请求失败";
        if (dto.getStatusCode() == null || !Objects.equals(200, dto.getStatusCode())) {
            log.error("【泰杰赛智慧照明系统】接口调用失败：{}", response);
            throw new RuntimeException("请求失败");
        }
        return dto.getData();
    }


    /**
     * 发送POST请求
     * @param url API接口
     * @param body 请求体
     * @param token token
     * @return response
     */
    private String post(String url, String port, String body, String token) {
        try {
            url = baseUrl + ":" + port + url;
            HttpRequest request = HttpRequest.post(url);
            request.timeout(TIMEOUT);
            request.body(body);
            if (StringUtils.isNotBlank(token)) {
                request.header("Authorization", "bearer " + token);
            }
            return request.execute().body();
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("泰杰照明系统连接异常，请联系管理员");
        }
    }

    /**
     * 与当前时间的分钟差
     * @param expiresTime 过期时间
     * @return 分钟
     */
    private long minuteDifference(Date expiresTime) {
        Date now = new Date();
        Instant startInstant = now.toInstant();
        Instant endInstant = expiresTime.toInstant();
        Duration duration = Duration.between(startInstant, endInstant);
        return duration.toMinutes();
    }



}
