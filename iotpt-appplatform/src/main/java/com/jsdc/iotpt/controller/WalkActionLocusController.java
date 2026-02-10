package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsdc.iotpt.common.WalkFaceConfig;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.WalkActionLocus;
import com.jsdc.iotpt.model.WalkPerson;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.service.SysBuildAreaService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.service.WalkActionLocusService;

import com.jsdc.iotpt.service.WalkPersonService;
import com.jsdc.iotpt.util.AjaxResult;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轨迹分析
 */
@Controller
@RequestMapping("actionLocus")
public class WalkActionLocusController {
    @Autowired
    private WalkActionLocusService actionLocusService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysBuildAreaService areaService;
    @Autowired
    private WalkFaceConfig faceConfig;
    @Autowired
    private WalkPersonService personService;



    /**
     * 查询轨迹信息
     * @param image
     * @param idcardNo
     * @param type
     * @param time
     * @return
     * @throws Exception
     */
    @RequestMapping("getActionsById.do")
    @ResponseBody
    public ResultInfo getActionsById(String image, String idcardNo, String type, String time, Integer floor) throws Exception {
        String  faceId = "";
        SysUser sysUser = sysUserService.getUser();
        if(StringUtils.equals(type, "0")){
            if(StringUtils.isNotEmpty(image)){
                ResultInfo result = faceConfig.search(null, URLDecoder.decode(image, "UTF-8"),null,null, null);
                if(StringUtils.equals(result.getCode().toString(), "200")){
                    Map<String, String> data = (HashMap<String, String>) result.getData();
                    if (data.get("hasMatch").equals("true")) {
                        JSONObject faceInfo = JSON.parseObject(data.get("faceInfo"));
                        if (faceInfo.containsKey("user_list") && !faceInfo.getJSONArray("user_list").isEmpty()) {
                            JSONObject userList = faceInfo.getJSONArray("user_list").getJSONObject(0);
                            faceId = userList.getString("user_id");
                            List<WalkActionLocus> actionLocusList = actionLocusService.getActionsById(faceId, time, floor);
                            actionLocusList.forEach(x -> {
                                String filepath = x.getFilePath();
                                try {
                                    x.setBase64(FileUtils.filePathToBase64(filepath));
                                    SysBuildArea area = areaService.getById(x.getAreaId());
                                    x.setAreaName(area.getAreaName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            WalkPerson person =
                                    personService.getOne(new QueryWrapper<WalkPerson>().eq("faceId", faceId).eq(
                                            "isDelete", 0));
                            Map<String, Object> map = new HashMap<>();
                            map.put("aclist", actionLocusList);
                            map.put("isWarn", null == person ? 0: person.getIsWarning());
                            map.put("person", person);
                            return ResultInfo.success(map);
                        }
                    }
                }
            }
        }else if(StringUtils.equals(type, "1")){
            if(StringUtils.isNotEmpty(idcardNo)){
                List<WalkPerson> persons =
                        personService.list(new QueryWrapper<WalkPerson>().eq("idCard", idcardNo).eq("isDelete", 0));

                WalkPerson person = persons.get(0);
                if(null != person){
                    faceId = person.getFaceId();
                    List<WalkActionLocus> actionLocusList = actionLocusService.getActionsById(faceId, time, floor);
                    actionLocusList.forEach(x -> {
                        String filepath = x.getFilePath();
                        try {
                            x.setBase64(FileUtils.filePathToBase64(filepath));
                            SysBuildArea area = areaService.getById(x.getAreaId());
                            x.setAreaName(area.getAreaName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    Map<String, Object> map = new HashMap<>();
                    map.put("aclist", actionLocusList);
                    map.put("isWarn", person.getIsWarning());
                    map.put("person", person);
                    return ResultInfo.success(map);
                }
            }
        }
        return ResultInfo.error("");
    }

    /**
     * 时间段查询轨迹信息
     * @param image
     * @param idcardNo
     * @param type
     * @param time
     * @return
     * @throws Exception
     */
    @RequestMapping("getActionsByTime.do")
    @ResponseBody
    public ResultInfo getActionsByTime(String image, String idcardNo, String type, String time) throws Exception {
        String  faceId = "";
        String startTime = time.split(" - ")[0];
        String endTime = time.split(" - ")[1];
        SysUser sysUser = sysUserService.getUser();
        if(StringUtils.equals(type, "0")){
            if(StringUtils.isNotEmpty(image)){
                ResultInfo result = faceConfig.search(null, URLDecoder.decode(image, "UTF-8"),null,null, null);
                if(StringUtils.equals(result.getCode().toString(), "200")){
                    Map<String, String> data = (HashMap<String, String>) result.getData();
                    if (data.get("hasMatch").equals("true")) {
                        JSONObject faceInfo = JSON.parseObject(data.get("faceInfo"));
                        if (faceInfo.containsKey("user_list") && !faceInfo.getJSONArray("user_list").isEmpty()) {
                            JSONObject userList = faceInfo.getJSONArray("user_list").getJSONObject(0);
                            faceId = userList.getString("user_id");
                            List<WalkActionLocus> actionLocusList = actionLocusService.getActionsByTime(faceId,
                                    startTime, endTime);
                            actionLocusList.forEach(x -> {
                                String filepath = x.getFilePath();
                                try {
                                    x.setBase64(FileUtils.filePathToBase64(filepath));
                                    SysBuildArea area = areaService.getById(x.getAreaId());
                                    x.setAreaName(area.getAreaName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            WalkPerson person =
                                    personService.getOne(new QueryWrapper<WalkPerson>().eq("faceId", faceId).eq("isDelete",
                                            0));
                            Map<String, Object> map = new HashMap<>();
                            map.put("aclist", actionLocusList);
                            map.put("isWarn", null == person ? 0: person.getIsWarning());
                            map.put("person", person);
                            return ResultInfo.success(map);
                        }
                    }
                }
            }
        }else if(StringUtils.equals(type, "1")){
            if(StringUtils.isNotEmpty(idcardNo)){
                List<WalkPerson> persons =
                        personService.list(new QueryWrapper<WalkPerson>().eq("idCard", idcardNo).eq("isDelete", 0));
                if(CollectionUtils.isNotEmpty(persons)){
                    WalkPerson person = persons.get(0);
                    faceId = person.getFaceId();
                    List<WalkActionLocus> actionLocusList = actionLocusService.getActionsByTime(faceId, startTime,
                            endTime);
                    actionLocusList.forEach(x -> {
                        String filepath = x.getFilePath();
                        try {
                            x.setBase64(FileUtils.filePathToBase64(filepath));
                            SysBuildArea area = areaService.getById(x.getAreaId());
                            x.setAreaName(area.getAreaName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    Map<String, Object> map = new HashMap<>();
                    map.put("aclist", actionLocusList);
                    map.put("isWarn", person.getIsWarning());
                    map.put("person", person);
                    return ResultInfo.success(map);
                }
            }
        }
        return ResultInfo.error("");
    }

    public ResultInfo getActionsByImg(MultipartFile image, String time, Integer floor) throws Exception {
        SysUser user = sysUserService.getUser();
        ResultInfo body = faceConfig.search(image,null, null, null, null);
        if ((int) body.getCode() == 200) {
            Map<String, String> data = (HashMap<String, String>) body.getData();
            if (data.get("hasMatch").equals("true")) {
                JSONObject faceInfo = JSON.parseObject(data.get("faceInfo"));
                JSONObject user_list = faceInfo.getJSONArray("user_list").getJSONObject(0);
                String faceId = user_list.getString("user_id");
                List<WalkActionLocus> actionsById = actionLocusService.getActionsById(faceId, time, floor);
                return ResultInfo.success(actionsById);
            }
        }
        return ResultInfo.error("人脸查询失败!");
    }
}
