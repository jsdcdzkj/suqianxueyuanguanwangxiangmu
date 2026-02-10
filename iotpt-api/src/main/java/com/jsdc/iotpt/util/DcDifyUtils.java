package com.jsdc.iotpt.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 园区调用dify接口
 */
@Component
@Slf4j
public class DcDifyUtils {

    @Value("${dcDify.url}")
    private String dcDifyUrl ;
    private ObjectMapper mapper = new ObjectMapper();


    @Autowired
    SysDictService sysDictService;
//    public static void main(String[] args) {
//        DcDifyUtils utils = new DcDifyUtils();
//        System.out.println("sdfsdfsdfsdf===" + utils.sendRequest("你是一个智慧园区系统的运维人员，平台现在接收到了众多告警信息，告警类型分为：人员告警、厨房告警、车辆告警、设备运维告警、消防告警、电气告警、门禁告警。告警等级分为：警告、次要、重要、紧急。其中不同类型的告警都接收到了最新的告警信息，其中人员告警13个、厨房告警13个、车辆告警34个、设备运维告警43个、消防告警1个、电气告警7个、门禁告警26个。请从不同维度简要分析一下告警数据，生成告警报告", "22483"));
//
//    }

    /**
     * 园区告警
     * @param query
     * @return
     */
    public String sendRequest(String query) {
        List<SysDict> sysDicts = sysDictService.selectByType("warnReportKey");

        if(sysDicts.size() <= 0){
            return "请配置正确的模板";
        }
        return sendRequest(sysDicts.get(0).getDictValue(), query, "blocking", "");
    }

    public String sendRequest(String query,String user) {
        List<SysDict> sysDicts = sysDictService.selectByType("warnReportKey");

        if(sysDicts.size() <= 0){
            return "请配置正确的模板";
        }
        return sendRequest(sysDicts.get(0).getDictValue(), query, "blocking", user);
    }

    /**
     * 调用dify基础接口
     * @param apiKey 每个流程都有个唯一标识，请替换成你自己的apiKey .（不同的流程，请使用对应的apiKey）
     * @param query  用户提出的问题
     * @param responseMode 响应模式，可选值：blocking、streaming
     * @param user  用户标识 默认为11
     * @return
     */
    public String sendRequest(String apiKey,String query,String responseMode, String user) {
        // 配置参数获取（需补充实现）
        String apiUrl = dcDifyUrl;
        if(!"streaming".equalsIgnoreCase(StringUtils.trimToEmpty(responseMode))){
            responseMode = "blocking";
        }
        String defaultUser = "11";

        // 用户标识处理
        if (user == null || user.matches("null |undefined|^\\s*$")) {
            user = defaultUser;
        }

        // 构建请求体
        String jsonBody = buildRequestBody(query, responseMode, user);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            return processResponse(response, responseMode);
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            return "AI服务开小差了";
        }
    }


    private String buildRequestBody(String query, String responseMode, String user) {
        // 结构化构建嵌套JSON
        return "{"
                + "\"inputs\":{},"
                + "\"query\":\"" + query + "\","
                + "\"response_mode\":\"" + responseMode + "\","
                + "\"conversation_id\":\"\","
                + "\"user\":\"" + user + "\","
                + "\"files\":[{"
                + "   \"type\":\"image\","
                + "   \"transfer_method\":\"remote_url\","
                + "   \"url\":\"https://cloud.dify.ai/logo/logo-site.png\""
                + "}]"
                + "}";
    }

    private String processResponse(HttpResponse response, String responseMode) throws Exception {
        HttpEntity entity = response.getEntity();
        if (entity == null) return "Empty response";

        if ("streaming".equals(responseMode)) {
            return handleStreaming(entity.getContent());
        } else {
            return handleNormalResponse(EntityUtils.toString(entity));
        }
    }

    private String handleStreaming(InputStream stream) throws IOException {
        StringBuilder answer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:  ")) {
                    JsonNode node = mapper.readTree(line.substring(6));
                    if ("message".equals(node.path("event").asText())) {
                        answer.append(node.path("answer").asText());
                    }
                }
            }
        }
        return answer.toString().trim();
    }

    private String handleNormalResponse(String body) throws IOException {
        JsonNode root = mapper.readTree(body);
        return root.path("answer").asText();
    }
}
