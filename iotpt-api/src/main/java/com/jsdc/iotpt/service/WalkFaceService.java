package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.common.WalkFaceConfig;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WalkFaceService {
    @Autowired
    private WalkFaceConfig walkFaceConfig;

    public JSONObject search(MultipartFile image, String groupIds, String maxNum, String userId) {
        try {
            Optional<ResultInfo> o = Optional.ofNullable(walkFaceConfig.search(image, null, groupIds, userId, maxNum));
            if (o.isPresent()) {
                ResultInfo result = o.get();
                JSONObject jsonObject = new JSONObject();
                if (StringUtils.equals(result.getCode().toString(), "200")) {
                    Map<String, String> data = (HashMap<String, String>) result.getData();
                    jsonObject.put("hasMatch", data.get("hasMatch"));
                    jsonObject.put("faceInfo", data.get("faceInfo"));
                    jsonObject.put("code", "0");
                    return jsonObject;
                } else {
                    jsonObject.put("code", "1");
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject searchForTdh(MultipartFile image, String base64, String groupIds, String maxNum, String userId, Integer Similarity) {
        try {
            Optional<ResultInfo> o = Optional.ofNullable(walkFaceConfig.searchForTdh(image, base64, groupIds, userId, maxNum, Similarity));
            if (o.isPresent()) {
                ResultInfo result = o.get();
                JSONObject jsonObject = new JSONObject();
                if (StringUtils.equals(result.getCode().toString(), "200")) {
                    Map<String, String> data = (HashMap<String, String>) result.getData();
                    jsonObject.put("hasMatch", data.get("hasMatch"));
                    jsonObject.put("faceInfo", data.get("faceInfo"));
                    jsonObject.put("code", "0");
                    return jsonObject;
                } else {
                    jsonObject.put("code", "1");
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
