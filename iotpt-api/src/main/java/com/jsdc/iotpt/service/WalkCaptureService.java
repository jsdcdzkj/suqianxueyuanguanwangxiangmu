package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.WalkFaceConfig;
import com.jsdc.iotpt.mapper.WalkCaptureMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.util.AjaxResult;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WalkCaptureVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
public class WalkCaptureService extends BaseService<WalkCapture> {

    @Autowired
    private WalkCaptureMapper walkCaptureMapper;
    @Autowired
    private WalkActionLocusService actionLocusService;
    @Autowired
    private WalkCaptureService captureService;
    @Autowired
    private WalkEquipmentService walkEquipmentService;
    @Autowired
    private SysOrgDeptService deptService;
    @Autowired
    private WalkFaceService faceService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private WalkFaceConfig faceConfig;
    @Autowired
    private WalkPersonService personService;
    @Autowired
    private WalkUserFileService userFileService;
    @Autowired
    private WalkFocalPersonEarlyWarnService focalPersonEarlyWarnService;

    public Page<WalkCapture> getPageList(WalkCaptureVo vo) {
        LambdaQueryWrapper wrapper = Wrappers.<WalkCapture>lambdaQuery()
                .eq(null != vo.getDeptId(), WalkCapture::getDeptId, vo.getDeptId())
                .gt(StringUtils.isNotEmpty(vo.getStartTime()), WalkCapture::getStartTime, vo.getStartTime())
                .lt(StringUtils.isNotEmpty(vo.getStartTime()), WalkCapture::getEndTime, vo.getEndTime())
                .eq(WalkCapture::getIsDelete, G.ISDEL_NO);
        return walkCaptureMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), wrapper);
    }

    /**
     * 抓拍记录入库
     *
     * @param image
     * @param equipmentCode
     * @param actionLocus
     */
    public Integer captureIn(MultipartFile image, String equipmentCode, WalkActionLocus actionLocus) {
        WalkEquipment e = new WalkEquipment();
        e.setEquipmentCode(equipmentCode);
        List<WalkEquipment> equipments = walkEquipmentService.getList(e);
        if (CollectionUtils.isNotEmpty(equipments)) {
            WalkEquipment equipment = equipments.get(0);
            SysOrgDept department = deptService.getById(equipment.getDeptId());
            actionLocus.setDeptId(department.getId());
            Optional<JSONObject> o = Optional.ofNullable(faceService.search(image, department.getOrgId().toString(), null, null));
            if (o.isPresent()) {
                JSONObject result = o.get();
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(result.getString("code"), "0")) {
                    //若人脸库中已存在，则入轨迹库
                    if (com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(result.getString("hasMatch"), "true")) {
                        String faceInfo = result.getString("faceInfo");
                        JSONArray faceList = JSON.parseObject(faceInfo).getJSONArray("user_list");
                        String faceId = faceList.getJSONObject(0).getString("user_id");
                        actionLocus.setFaceId(faceId);
                        actionLocusService.save(actionLocus);
                        System.out.println("人脸存在，轨迹信息已入库");
                        return 1;
                    } else {//若人脸库不存在，入抓拍库
                        WalkCapture capture = new WalkCapture();
                        capture.setEquipmentId(equipment.getId());
                        capture.setCaptureTime(LocalDateTime.now());
//                        capture.setFilePath(FileUtils.copyFile(image, null, null, null, UUID.randomUUID().toString().replaceAll("-", ""), null));
                        capture.setHasDeal(0);
                        capture.setCreateTime(LocalDateTime.now());
                        capture.setUpdateTime(LocalDateTime.now());
                        capture.setIsDelete(0);
                        captureService.save(capture);
                        System.out.println("人脸不存在，已入抓拍库");
                        return 1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 保存功能
     *
     * @param person
     * @param base64
     * @return
     */
    public ResultInfo saveCapture(WalkPerson person, String base64) {
        Integer deptId = userService.getUser().getDeptId();
        SysOrgDept department = deptService.getById(deptId);
        try {
            ResultInfo addUser = faceConfig.addUser(null, base64.replaceAll(" ", "+"), (null == person) ? "" : JSON.toJSONString(person), department.getOrgId().toString());
            if (200 != addUser.getCode()) {
                return ResultInfo.error("人脸新增失败,可能是服务器失连");
            }
            // 新增访客信息
            Map<String, String> dataUser = (HashMap<String, String>) addUser.getData();
            // 根据当前faceId筛选对应用户信息
            person.setDeptId(deptId);
            person.setFaceId(dataUser.get("userId"));
            WalkPerson personData = personService.selectByFind(person);
            // 用户表中不存在当前访客
            if (personData == null) {
                personData = new WalkPerson();
                person.setFaceToken(dataUser.get("faceToken"));
                person.setCreateTime(LocalDateTime.now());
                person.setUpdateTime(LocalDateTime.now());
                person.setIsDelete(0);
                BeanUtils.copyProperties(person, personData);
                personService.save(personData);
            } else {
                personData.setUpdateTime(LocalDateTime.now());
                personService.updateById(personData);
            }
            // 将图片保存至本地并写入图片表
            //String targetFile = FileUtils.copyFile(null, base64, null, null, dataUser.get("userId"), null);
            WalkUserFile userFile = new WalkUserFile();
            userFile.setCreateTime(LocalDateTime.now());
            userFile.setFaceId(dataUser.get("userId"));
            userFile.setUserId(personData == null ? person.getId() : personData.getId());
            //userFile.setFilePath(targetFile);
            WalkUserFile userFileData = userFileService.selectByFaceId(userFile.getFaceId());
            if (userFileData == null) {
                userFileService.save(userFile);
            } else {
                BeanUtils.copyProperties(userFileData, userFile);
                userFileService.updateById(userFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.success("人脸新增失败,可能是服务器失连");
        }
        return ResultInfo.success("新增人脸库数据成功");
    }

    /**
     * 抓拍准备数据
     *
     * @param id
     * @return
     */
    public String captureData(Integer id) {
        WalkFocalPersonEarlyWarn fr = focalPersonEarlyWarnService.getById(id);
        WalkUserFile u = userFileService.selectByFaceId(fr.getFaceId());
        return FileUtils.filePathToBase64(u.getFilePath());
    }
}
