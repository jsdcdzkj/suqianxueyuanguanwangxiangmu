package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.WalkFaceConfig;
import com.jsdc.iotpt.mapper.WalkPersonMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.WalkLabel;
import com.jsdc.iotpt.model.WalkPerson;
import com.jsdc.iotpt.model.WalkUserFile;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.util.AjaxResult;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WalkPersonVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalkPersonService extends BaseService<WalkPerson> {

    @Autowired
    private WalkPersonMapper personMapper;
    @Autowired
    private WalkUserFileService userFileService;
    @Autowired
    private WalkLabelService labelService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysOrgDeptService departmentService;
    @Autowired
    private WalkFaceConfig faceConfig;
    @Autowired
    private WalkFaceService faceService;

    /**
     * 人脸信息管理分页
     *
     * @param person
     * @return
     */
    public Page<WalkPerson> selectGroupUsersPageList(WalkPersonVo person) {
        if (null != person.getDeptId()) {
            person.setDeptId(person.getDeptId());
        } else {
            person.setDeptId(sysUserService.getUser().getDeptId());
        }
        LambdaQueryWrapper<WalkPerson> queryWrapper = Wrappers.<WalkPerson>lambdaQuery().eq(WalkPerson::getIsDelete, 0)
                .eq(StringUtils.isNotEmpty(person.getIdCard()), WalkPerson::getIdCard, person.getIdCard())
                .eq(StringUtils.isNotEmpty(person.getPhone()), WalkPerson::getPhone, person.getPhone())
                .like(StringUtils.isNotEmpty(person.getRealName()), WalkPerson::getRealName, person.getRealName())
                .eq(StringUtils.isNotEmpty(person.getUserType()), WalkPerson::getUserType, person.getUserType())
                .eq(null != person.getSex(), WalkPerson::getSex, person.getSex())
                .eq(null != person.getDeptId(), WalkPerson::getDeptId, person.getDeptId())
                .eq(null != person.getStartAge(), WalkPerson::getStartAge, person.getStartAge())
                .eq(null != person.getEndAge(), WalkPerson::getEndAge, person.getEndAge())
                .eq(StringUtils.isNotEmpty(person.getStartTime()), WalkPerson::getStartTime, person.getStartTime())
                .eq(StringUtils.isNotEmpty(person.getEndTime()), WalkPerson::getEndTime, person.getEndTime());
        Page<WalkPerson> page = personMapper.selectPage(new Page<>(person.getPageNo(), person.getPageSize()), queryWrapper);
        page.getRecords().forEach(a -> {
            if (StringUtils.isNotEmpty(a.getFaceId())) {
                Optional<WalkUserFile> one = Optional.ofNullable(userFileService.selectByFaceId(a.getFaceId()));
                if (one.isPresent()) {
                    try {
                        String base64 = FileUtils.filePathToBase64(one.get().getFilePath());
                        a.setImgPath(base64);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            List<String> labelNames = labelService.list(new QueryWrapper<WalkLabel>().in("id",
                    Arrays.asList(StringUtils.split(a.getUserType(), ","))))
                    .stream().map(x -> x.getLabelName()).collect(Collectors.toList());
            a.setUsertypeEnname(StringUtils.join(labelNames, ","));
        });
        return page;
    }

    /**
     * 保存数据
     *
     * @param person
     * @param image
     * @return
     */
    public ResultInfo savePerson(WalkPerson person, MultipartFile image) {
        try {
            SysUser user = sysUserService.getUser();
            SysOrgDept department = departmentService.getById(user.getDeptId());
            if (!image.isEmpty()) {
                // 1.人脸搜索
                ResultInfo resultInfo = faceConfig.search(image, null, department.getOrgId().toString(), null, null);
                if (200 == resultInfo.getCode()) {
                    Map<String, String> data = (HashMap<String, String>) resultInfo.getData();
                    // true:更新百度用户库中的照片,新增访客库,
                    if (data.get("hasMatch").equals("true")) {
                        JSONObject faceInfo = JSON.parseObject(data.get("faceInfo"));
                        // 百度人脸库修改
                        if (faceInfo.containsKey("user_list") && !faceInfo.getJSONArray("user_list").isEmpty()) {
                            JSONObject user_list = faceInfo.getJSONArray("user_list").getJSONObject(0);
                            ResultInfo info = faceConfig.updateUser(image, null, user_list.getString("user_id"), department.getOrgId().toString());
                            if (200 != info.getCode()) {
                                return ResultInfo.error("人脸修改失败,可能是服务器失连");
                            }
                            // 根据当前faceId筛选对应用户信息
                            WalkPerson person1 = new WalkPerson();
                            person1.setDeptId(department.getId());
                            person1.setFaceId(user_list.getString("user_id"));
                            WalkPerson personData = selectByFind(person);
                            // 用户表中不存在当前访客
                            if (null == personData) {
                                person1.setFaceToken(faceInfo.getString("face_token"));
                                person1.setCreateTime(LocalDateTime.now());
                                person1.setUpdateTime(LocalDateTime.now());
                                person1.setIsDelete(0);
                                person1.setRealName(person.getRealName());
                                person1.setIdCard(person.getIdCard());
                                person1.setAge(person.getAge());
                                person1.setSex(person.getSex());
                                person1.setMobile(person.getMobile());
                                person1.setPhone(person.getPhone());
                                person1.setDeptId(department.getId());
                                person1.setUserType(person.getUserType());
                                person1.setIsInsider(person.getIsInsider());
                                person1.setSectionId(person.getSectionId());
                                //TODO 用户标签修改
                                if (StringUtils.isEmpty(person.getUserType())) {
                                    person1.setUserType(String.valueOf(10));
                                }
                                BeanUtils.copyProperties(person1, personData);
                                save(personData);
                            } else {
                                personData.setFaceToken(faceInfo.getString("face_token"));
                                personData.setUpdateTime(LocalDateTime.now());
                                personData.setRealName(person.getRealName());
                                personData.setIdCard(person.getIdCard());
                                personData.setAge(person.getAge());
                                personData.setSex(person.getSex());
                                personData.setMobile(person.getMobile());
                                personData.setPhone(person.getPhone());
                                personData.setDeptId(department.getId());
                                personData.setUserType(person.getUserType());
                                personData.setIsInsider(person.getIsInsider());
                                personData.setSectionId(person.getSectionId());
                                updateById(personData);
                            }
                            // 将图片保存至本地并写入图片表
                            String targetFile = "";
//                            String targetFile = FileUtils.copyFile(image, null, null, null, user_list.getString("user_id"), null);
                            WalkUserFile userFile = new WalkUserFile();
                            userFile.setCreateTime(LocalDateTime.now());
                            userFile.setFaceId(user_list.getString("user_id"));
                            userFile.setUserId(personData == null ? person1.getId() : personData.getId());
                            userFile.setFilePath(targetFile);
                            WalkUserFile userFileData = userFileService.selectByFaceId(userFile.getFaceId());
                            if (null == userFileData) {
                                userFileService.save(userFile);
                            } else {
                                BeanUtils.copyProperties(userFileData, userFile);
                                userFileService.updateById(userFile);
                            }
                        }
                    } else {// 新增百度用户库，新增访客库
                        // 图片存储百度服务
                        ResultInfo addUser = faceConfig.addUser(image, null, (null == person) ? "" : JSON.toJSONString(person), department.getOrgId().toString());
                        if (200 != addUser.getCode()) {
                            return ResultInfo.error("人脸新增失败,可能是服务器失连");
                        }
                        Map<String, String> dataUser = (HashMap<String, String>) addUser.getData();
                        // 根据当前faceId筛选对应用户信息
                        WalkPerson p = new WalkPerson();
                        p.setDeptId(person.getDeptId());
                        p.setFaceId(dataUser.get("userId"));
                        WalkPerson personData = selectByFind(p);
                        // 用户表中不存在当前访客
                        if (personData == null) {
                            personData = new WalkPerson();
                            p.setFaceToken(dataUser.get("faceToken"));
                            p.setCreateTime(LocalDateTime.now());
                            p.setUpdateTime(LocalDateTime.now());
                            p.setIsDelete(0);
                            p.setRealName(person.getRealName());
                            p.setAge(person.getAge());
                            p.setSex(person.getSex());
                            p.setMobile(person.getMobile());
                            p.setPhone(person.getPhone());
                            p.setDeptId(department.getId());
                            p.setUserType(person.getUserType());
                            p.setIsInsider(person.getIsInsider());
                            p.setSectionId(person.getSectionId());
                            //TODO 用户标签修改
                            p.setUserType(String.valueOf(10));
                            p.setIdCard(person.getIdCard());
                            BeanUtils.copyProperties(p, personData);
                            save(personData);
                        } else {
                            personData.setFaceToken(dataUser.get("face_token"));
                            personData.setUpdateTime(LocalDateTime.now());
                            personData.setRealName(person.getRealName());
                            personData.setIdCard(person.getIdCard());
                            personData.setAge(person.getAge());
                            personData.setSex(person.getSex());
                            personData.setMobile(person.getMobile());
                            personData.setPhone(person.getPhone());
                            personData.setDeptId(department.getId());
                            personData.setUserType(person.getUserType());
                            personData.setIsInsider(person.getIsInsider());
                            personData.setSectionId(person.getSectionId());
                            updateById(personData);
                        }
                        // 将图片保存至本地并写入图片表
                        String targetFile = "";
//                        String targetFile = FileUtils.copyFile(image, null, null, null, dataUser.get("userId"), null);
                        WalkUserFile userFile = new WalkUserFile();
                        userFile.setCreateTime(LocalDateTime.now());
                        userFile.setFaceId(dataUser.get("userId"));
                        userFile.setUserId(personData == null ? person.getId() : personData.getId());
                        userFile.setFilePath(targetFile);
                        WalkUserFile userFileData = userFileService.selectByFaceId(userFile.getFaceId());
                        if (userFileData == null) {
                            userFileService.save(userFile);
                        } else {
                            BeanUtils.copyProperties(userFileData, userFile);
                            userFileService.updateById(userFile);
                        }
                    }
                } else {
                    return ResultInfo.error("人脸新增失败,可能是服务器失连");
                }
            } else {
                WalkPerson wp = new WalkPerson();
                wp.setDeptId(person.getDeptId());
                wp.setIdCard(person.getIdCard());
                WalkPerson personData = selectByFind(wp);
                // 用户表中不存在当前访客
                if (personData == null) {
                    personData = new WalkPerson();
                    wp.setCreateTime(LocalDateTime.now());
                    wp.setUpdateTime(LocalDateTime.now());
                    wp.setIsDelete(0);
                    wp.setRealName(person.getRealName());
                    wp.setAge(person.getAge());
                    wp.setSex(person.getSex());
                    wp.setMobile(person.getMobile());
                    wp.setPhone(person.getPhone());
                    wp.setDeptId(department.getId());
                    wp.setUserType(person.getUserType());
                    wp.setIsInsider(person.getIsInsider());
                    wp.setSectionId(person.getSectionId());
                    //TODO 用户标签修改
                    wp.setUserType(String.valueOf(10));
                    wp.setIdCard(person.getIdCard());
                    BeanUtils.copyProperties(wp, personData);
                    save(personData);
                } else {
                    personData.setUpdateTime(LocalDateTime.now());
                    personData.setRealName(person.getRealName());
                    personData.setIdCard(person.getIdCard());
                    personData.setAge(person.getAge());
                    personData.setSex(person.getSex());
                    personData.setMobile(person.getMobile());
                    personData.setPhone(person.getPhone());
                    personData.setDeptId(department.getId());
                    personData.setUserType(person.getUserType());
                    personData.setIsInsider(person.getIsInsider());
                    personData.setSectionId(person.getSectionId());
                    updateById(personData);
                }
            }
        } catch (Exception e) {
            return ResultInfo.error("访客登记失败,可能是服务器失连");
        }
        return ResultInfo.success();
    }

    /**
     * 获取用户表中是否有访客
     *
     * @param person
     * @return
     */
    public WalkPerson selectByFind(WalkPerson person) {
        return getOne(Wrappers.<WalkPerson>lambdaQuery().eq(WalkPerson::getIsDelete, 0)
                .eq(StringUtils.isNotEmpty(person.getFaceId()), WalkPerson::getFaceId, person.getFaceId())
                .eq(null != person.getDeptId(), WalkPerson::getDeptId, person.getDeptId())
                .eq(StringUtils.isNotEmpty(person.getIdCard()), WalkPerson::getIdCard, person.getIdCard()));
    }

    /**
     * 人脸搜索
     *
     * @param image
     * @param maxNum
     * @return
     */
    public ResultInfo faceSearch(MultipartFile image, String maxNum) {
        SysUser user = sysUserService.getUser();
        SysOrgDept department = departmentService.getById(user.getDeptId());
        Optional<JSONObject> o = Optional.ofNullable(faceService.search(image, department.getOrgId().toString(), maxNum, null));
        if (o.isPresent()) {
            JSONObject result = o.get();
            if (StringUtils.equals(result.getString("code"), "0")) {
                String faceInfo = result.getString("faceInfo");
                JSONArray faceList = JSON.parseObject(faceInfo).getJSONArray("user_list");
                List<String> faceIdList = new ArrayList<>();
                for (int i = 0; i < faceList.size(); i++) {
                    JSONObject face = faceList.getJSONObject(i);
                    faceIdList.add(face.getString("user_id"));
                }
                StringBuilder sb = new StringBuilder();
                faceIdList.forEach(x -> {
                    sb.append("'");
                    sb.append(x);
                    sb.append("'");
                    sb.append(",");
                });
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                List<WalkPerson> persons = personMapper.getPersonsByIds(sb.toString());
                persons.forEach(x -> {
                    WalkUserFile uf = userFileService.selectByFaceId(x.getFaceId());
                    x.setFaceBase64(FileUtils.getImgFileToBase64(uf.getFilePath()).replaceAll("\r|\n", ""));
                });
                return ResultInfo.success(persons);
            }
        }
        return ResultInfo.error("未查询到结果");
    }

    /**
     * 获取登记人脸总人数
     *
     * @return
     */
    public Long selectPersonCount() {
        return personMapper.selectCount(new LambdaQueryWrapper<WalkPerson>().eq(WalkPerson::getIsDelete, 0));
    }
}
