package com.jsdc.iotpt.common;

import com.jsdc.iotpt.util.AjaxResult;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
public class WalkFaceConfig {


    // 人脸服务地址
//    @Value("${faceClient.host}")
    private String faceHost;
    // 人脸搜索
//    @Value("${faceClient.interface.search}")
    private String search;
//    @Value("${faceClient.interface.searchForTdh}")
    private String searchForTdh;
    // 人脸比较
//    @Value("${faceClient.interface.personVerify}")
    private String personVerify;
    // 用户新增
//    @Value("${faceClient.interface.addUser}")
    private String addUser;
    // 用户修改
//    @Value("${faceClient.interface.updateUser}")
    private String updateUser;
//    @Value("${faceClient.interface.getGroupUsers}")
    private String getGroupUsers;
    //调用接口
    @Autowired
    private RestTemplate restTemplate;

//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder.build();
//    }
    /**
     * 人脸比对
     */
    public AjaxResult personVerify(MultipartFile idCardImg, MultipartFile photo) throws Exception {
        String url = String.format("%s%s", faceHost, personVerify);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("imagea", FileUtils.multFileToBase64(idCardImg));
        request.add("imageb", FileUtils.multFileToBase64(photo));
        ResponseEntity<AjaxResult> resp = restTemplate.postForEntity(url, request, AjaxResult.class);
        return resp.getBody();
    }


    /**
     * 人脸搜索
     *  MultipartFile与base64二选一
     */
    public ResultInfo search(MultipartFile photo, String base, String groupId, String userId, String maxNum) throws Exception {
        String url = String.format("%s%s", faceHost, search);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        String imgBase64 = null;
        if (Strings.isNotEmpty(base)) {
            imgBase64 = base;
        } else {
            imgBase64 = FileUtils.multFileToBase64(photo);
        }

        request.add("imageType", "BASE64");
        request.add("image", imgBase64);
        request.add("groupIdList", groupId);
        request.add("userId", userId);
        request.add("maxNum", maxNum);
        ResponseEntity<ResultInfo> resp = restTemplate.postForEntity(url, request, ResultInfo.class);
        return resp.getBody();
    }

    /**
    * create by wp at 2021/10/28 10:46
    * description: 人脸搜索for通达海
    * @param photo
    * @param base
    * @param groupId
    * @param userId
    * @param maxNum
     *
    * @return com.jsdc.rlsb.common.utils.AjaxResult
    */
    public ResultInfo searchForTdh(MultipartFile photo, String base, String groupId, String userId, String maxNum, Integer Similarity) throws Exception {
        String url = String.format("%s%s", faceHost, searchForTdh);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        String imgBase64 = null;
        if (Strings.isNotEmpty(base)) {
            imgBase64 = base;
        } else {
            imgBase64 = FileUtils.multFileToBase64(photo);
        }

        request.add("imageType", "BASE64");
        request.add("image", imgBase64);
        request.add("groupIdList", groupId);
        request.add("userId", userId);
        request.add("maxNum", maxNum);
        request.add("Similarity", String.valueOf(Similarity));
        ResponseEntity<ResultInfo> resp = restTemplate.postForEntity(url, request, ResultInfo.class);
        return resp.getBody();
    }


    /**
     * 新增人脸基础服务库
     * MultipartFile与base64二选一
     * @return
     */
    public ResultInfo addUser(MultipartFile photo, String base, String userInfo, String groupId) throws Exception {
        String url = String.format("%s%s", faceHost, addUser);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        String imgBase64 = null;
        if (Strings.isNotEmpty(base)) {
            imgBase64 = base;
        } else {
            imgBase64 = FileUtils.multFileToBase64(photo);
        }
        request.add("imageType", "BASE64");
        request.add("image", imgBase64);
        request.add("groupId", groupId);
        request.add("userId", null);
        request.add("actionType", null);
        request.add("userInfo", userInfo);
        ResponseEntity<ResultInfo> resp = restTemplate.postForEntity(url, request, ResultInfo.class);
        return resp.getBody();
    }

    /**
     * 修改百度图片库
     *
     * @param photo
     * @param userId
     * @return
     */
    public ResultInfo updateUser(MultipartFile photo, String base64, String userId, String groupId) throws Exception {
        String url = String.format("%s%s", faceHost, updateUser);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        String imgBase64 = StringUtils.isNotEmpty(base64) ? base64 : FileUtils.multFileToBase64(photo);
        request.add("imageType", "BASE64");
        request.add("image", imgBase64);
        request.add("groupId", groupId);
        request.add("userId", userId);
        ResponseEntity<ResultInfo> resp = restTemplate.postForEntity(url, request, ResultInfo.class);
        return resp.getBody();
    }

    /**
     * 获取用户组所有用户信息
     *
     * @param groupId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public AjaxResult getGroupUsers(String groupId, Integer pageIndex, Integer pageSize) {
        String url = String.format("%s%s", faceHost, getGroupUsers);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("groupId", groupId);
        request.add("start", String.valueOf(pageSize * (pageIndex - 1)));
        request.add("length", String.valueOf(pageSize));
        ResponseEntity<AjaxResult> resp = restTemplate.postForEntity(url, request, AjaxResult.class);
        return resp.getBody();
    }


}
