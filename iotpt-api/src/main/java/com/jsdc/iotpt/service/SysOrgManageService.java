package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.*;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysOrgManageService extends BaseService<SysOrgManage> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOrgManageMapper sysOrgManageMapper;

    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;

    @Autowired
    private SysBuildMapper sysBuildMapper;

    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;

    @Autowired
    private SysOrgDeptMapper sysOrgDeptMapper;
    /**
     * 组织单位新增
     * Author wzn
     * Date 2023/5/9 9:01
     */
    public SysOrgManage addOrgManage(SysOrgManage sysOrgManage) {


        //没所属组织默认为父级
        if (null == sysOrgManage.getParentId()) {
            sysOrgManage.setParentId(0);
        }
        SysUser sysUser = sysUserService.getUser();
        sysOrgManage.setCreateUser(sysUser.getId());
        sysOrgManage.setCreateTime(new Date());
        sysOrgManage.setIsDel(0);
        sysOrgManage.insert();
        return sysOrgManage;
    }

    /**
     * 单位唯一性校验
     * Author wzn
     * Date 2023/7/12 15:07
     */
    public boolean checkOnly(SysOrgManage sysOrgManage) {
        boolean checkOnly = true; //没有重复
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orgName", sysOrgManage.getOrgName());
        queryWrapper.eq("isDel", 0);
        List<SysOrgManage> sysOrgManageList = sysOrgManageMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sysOrgManageList)) {
            checkOnly = false;
            if (null != sysOrgManage.getId()) {
                if (sysOrgManage.getId().equals(sysOrgManageList.get(0).getId())) {
                    checkOnly = true;
                }
            }
        }
        return checkOnly;
    }

    /**
     * 组织单位修改
     * Author wzn
     * Date 2023/5/9 9:06
     */
    public void updateOrgManage(SysOrgManage sysOrgManage) {

        SysUser sysUser = sysUserService.getUser();
        sysOrgManage.setUpdateUser(sysUser.getId());
        sysOrgManage.setUpdateTime(new Date());
        sysOrgManage.updateById();
    }


    /**
     * 组织单位列表
     * Author wzn
     * Date 2023/5/9 9:09
     */
    public Page<SysOrgManage> selectOrgManageList(SysOrgManage sysOrgManage) {
        Page<SysOrgManage> page = new Page<>(sysOrgManage.getPageNo(), sysOrgManage.getPageSize());
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (!"".equals(sysOrgManage.getOrgName()) && null != sysOrgManage.getOrgName()) {
            queryWrapper.like("orgName", sysOrgManage.getOrgName());
        }
        queryWrapper.orderByDesc("createTime");
        Page<SysOrgManage> sysOrgManagePage = sysOrgManageMapper.selectPage(page, queryWrapper);
        return sysOrgManagePage;
    }


    public List<SysOrgManage> getList(SysOrgManage sysOrgManage) {
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        return sysOrgManageMapper.selectList(queryWrapper);
    }


    /**
     * 组织单位详情
     * Author wzn
     * Date 2023/5/9 9:10
     */
    public SysOrgManage info(String id) {
        return sysOrgManageMapper.selectById(id);
    }


    /**
     * 单位树形图结构
     * Author wzn
     * Date 2023/6/30 8:58
     */
    public List<Map<String, Object>> orgTreeList() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        //查单位父级
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("parentId", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysOrgManage> sysOrgManages = sysOrgManageMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysOrgManages)) {
            for (SysOrgManage s : sysOrgManages) {
                map = new HashMap<>();
                map.put("id", s.getId() + "");
                map.put("label", s.getOrgName());
                map.put("type", "0");
                //二级
                QueryWrapper<SysOrgManage> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("parentId", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByDesc("createTime");
                List<SysOrgManage> sysOrgManageList = sysOrgManageMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysOrgManageList)) {
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysOrgManage sb : sysOrgManageList) {
                        map2 = new HashMap<>();
                        map2.put("id", sb.getId() + "");
                        map2.put("label", sb.getOrgName());
                        map2.put("type", "1");
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }


    /**
     * 区域三级树形图
     * Author wzn
     * Date 2023/6/30 10:11
     */
    public List<Map<String, Object>> areaTreeList() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("value", s.getId() + "");
                map.put("label", s.getBuildName());
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("value", sb.getId() + "");
                        map2.put("label", sb.getFloorName());

                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.eq("floorId", sb.getId());
                        queryWrapper3.eq("isDel", 0);
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("value", sa.getId() + "");
                                map3.put("label", sa.getAreaName());
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }


        }
        return mapList;
    }

    /**
     * 区域三级树形图
     * Author wzn
     * Date 2023/6/30 10:11
     */
    public List<Map<String, Object>> areaTreeList2() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("value", s.getId() + "");
                map.put("label", s.getBuildName());
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("value", sb.getId() + "");
                        map2.put("label", sb.getFloorName());

                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.eq("floorId", sb.getId());
                        queryWrapper3.eq("isDel", 0);
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
//                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("value", sa.getId() + "");
                                map3.put("label", sa.getAreaName());
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }


        }
        return mapList;
    }

    /**
     * 区域二级树形图
     */
    public List<Map<String, Object>> buildFloorTreeList() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("value", s.getId() + "");
                map.put("label", s.getBuildName());
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("value", sb.getId() + "");
                        map2.put("label", sb.getFloorName());

                        //区域
//                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
//                        queryWrapper3.eq("floorId", sb.getId());
//                        queryWrapper3.eq("isDel", 0);
//                        queryWrapper3.orderByDesc("createTime");
//                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
//                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
//                            map2.put("children", new ArrayList<>());
////                            map2.put("disabled", true);
//                        } else {
//                            List<Map<String, Object>> mapList3 = new ArrayList<>();
//                            for (SysBuildArea sa : sysBuildAreaList) {
//                                map3 = new HashMap<>();
//                                map3.put("value", sa.getId() + "");
//                                map3.put("label", sa.getAreaName());
//                                mapList3.add(map3);
//                            }
//                            map2.put("children", mapList3);
//                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }


        }
        return mapList;
    }


    /**
     * 区域三级树形图
     * Author thr
     * Date 2023/8/2 10:11
     */
    public List<Map<String, Object>> areaTreeList3(Integer buildId) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(buildId)) {
            queryWrapper.eq("id", buildId);
        }
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("id", "build_" + s.getId());
                map.put("label", s.getBuildName());
                map.put("level", "1");
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("id", "floor_" + sb.getId());
                        map2.put("label", sb.getFloorName());
                        map.put("level", "2");

                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.eq("floorId", sb.getId());
                        queryWrapper3.eq("isDel", 0);
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("id", "area_" + sa.getId());
                                map3.put("label", sa.getAreaName());
                                map.put("level", "3");
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 区域三级树形图 楼宇楼层+总表分表
     * Author thr
     * Date 2024/10/17 09:11
     */
    public List<Map<String, Object>> areaTreeZfList(Integer buildId) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(buildId)) {
            queryWrapper.eq("id", buildId);
        }
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("id", "build_" + s.getId());
                map.put("label", s.getBuildName()+"(总表)");
                map.put("level", "1");
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("id", "floor_" + sb.getId());
                        map2.put("label", sb.getFloorName()+"(分表)");
                        map.put("level", "2");

                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.eq("floorId", sb.getId());
                        queryWrapper3.eq("isDel", 0);
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("id", "area_" + sa.getId());
                                map3.put("label", sa.getAreaName());
                                map.put("level", "3");
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }
        }
        return mapList;
    }


    /**
     * 查单位是否存在儿子
     * Author wzn
     * Date 2023/7/11 9:34
     */
    public boolean exist(String id) {
        boolean exist = false;
        //查儿子
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parentId", id);
        queryWrapper.eq("isDel", 0);
        Long count = sysOrgManageMapper.selectCount(queryWrapper);
        if (count != 0) {
            exist = true;
        } else {
            //查是否有部门
            QueryWrapper<SysOrgDept> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("orgId", id);
            queryWrapper2.eq("isDel", 0);
            Long count2 = sysOrgDeptMapper.selectCount(queryWrapper2);
            if (count2 != 0) {
                exist = true;
            }
        }
        return exist;
    }

    public List<SysOrgManage> getManageDeptUser() {
        //数据查询
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<SysOrgManage> list = baseMapper.selectList(queryWrapper);
        //新建一个用于接收数据的list
        List<SysOrgManage> resultList = new ArrayList<>();
        for (SysOrgManage result : list) {
            if (result.getParentId() == 0) {
                //调用方法给子类添加数据
                resultList.add(getManageDeptUserTree(result, list));
            }
        }
        return resultList;
    }

    private SysOrgManage getManageDeptUserTree(SysOrgManage result, List<SysOrgManage> list) {
        for (SysOrgManage menu : list) {
            menu.setLabel(menu.getOrgName());
            List<SysOrgDept> sysOrgDepts = sysOrgDeptMapper.
                    selectList(Wrappers.<SysOrgDept>lambdaQuery().
                            eq(SysOrgDept::getIsDel, 0).
                            eq(SysOrgDept::getOrgId, menu.getId()));

            sysOrgDepts.forEach(x -> {
                List<SysUser> sysUserList = sysUserService.
                        list(Wrappers.<SysUser>lambdaQuery().
                                eq(SysUser::getIsDel, 0).
                                eq(SysUser::getDeptId, x.getId()));
                x.setSysUserList(sysUserList);
            });
            menu.setDeptList(sysOrgDepts);
            //如果父类主键等于传过来实体类的ID
            if (menu.getParentId().equals(result.getId())) {
                if (result.getChildren() == null) {
                    result.setChildren(new ArrayList<>());
                }
                // 递归调用
                result.getChildren().add(getManageDeptUserTree(menu, list));
            }
        }
        return result;
    }

    /**
     * 公司无限极
     * Author wzn
     * Date 2023/7/17 10:45
     */
    public List<SysOrgManage> findOrg() {
        //数据查询
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByAsc("createTime") ;

        List<SysOrgManage> list = baseMapper.selectList(queryWrapper);
        //新建一个用于接收数据的list
        List<SysOrgManage> resultList = new ArrayList<>();
        for (SysOrgManage result : list) {
            if (result.getParentId() == 0) {
                //调用方法给子类添加数据
                resultList.add(getOrgTree(result, list));
            }
        }
        return resultList;
    }

    public List<SysOrgManage> findOrg2(String orgName) {
        //数据查询
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("isItATenant", 1);
        if(StringUtils.isNotBlank(orgName)){
            queryWrapper.like("orgName", orgName);
        }

        List<SysOrgManage> list = baseMapper.selectList(queryWrapper);
        //新建一个用于接收数据的list
        List<SysOrgManage> resultList = new ArrayList<>();
        for (SysOrgManage result : list) {
            if (result.getParentId() == 0) {
                //调用方法给子类添加数据
                resultList.add(getOrgTree(result, list));
            }
        }
        return resultList;
    }

    private SysOrgManage getOrgTree(SysOrgManage result, List<SysOrgManage> list) {
        for (SysOrgManage menu : list) {
            menu.setLabel(menu.getOrgName());
            //如果父类主键等于传过来实体类的ID
            if (menu.getParentId().equals(result.getId())) {
                if (result.getChildren() == null) {
                    result.setChildren(new ArrayList<>());
                }
                // 递归调用
                result.getChildren().add(getOrgTree(menu, list));
            }
        }
        return result;
    }


    /**
     * 区域三级树形图视频监控
     * Author wzn
     * Date 2023/6/30 10:11
     */
    public List<Map<String, Object>> areaTreeListVideo(String name) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        Map<String, Object> map4 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("value", s.getId() + "");
                map.put("label", s.getBuildName());
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("value", sb.getId() + "");
                        map2.put("label", sb.getFloorName());

                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.eq("floorId", sb.getId());
                        queryWrapper3.eq("isDel", 0);
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("value", sa.getId() + "");
                                map3.put("label", sa.getAreaName());
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }


        }
        return mapList;
    }

    public List<Map<String, Object>> areaTreeListVideoH(String name) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        Map<String, Object> map4 = null;
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("value", "b_" + s.getId());
                map.put("label", s.getBuildName());
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("value", "f_" + sb.getId());
                        map2.put("label", sb.getFloorName());

                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.eq("floorId", sb.getId());
                        queryWrapper3.eq("isDel", 0);
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("value", "a_" + sa.getId());
                                map3.put("label", sa.getAreaName());
                                //视频设备
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }


        }
        return mapList;
    }

    /**
     * 微信端根据区域获取设备
     *
     * @param foolId
     * @param areaId
     * @param name
     * @return
     */
    public List<Map<String, Object>> wxAreaTreeListVideo(Integer foolId, Integer areaId, String name) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        Map<String, Object> map3 = null;
        Map<String, Object> map4 = null;
        String areaIds = "";
        String floorIds = "";
        String buildId = "";
        if (StringUtils.isNotEmpty(name)) {
        }
        //先查楼宇
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(buildId)) {
            queryWrapper.in("id", Arrays.asList(buildId.split(", ")));
        }
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysBuildList)) {
            for (SysBuild s : sysBuildList) {
                map = new HashMap<>();
                map.put("devId", "b_" + s.getId());
                map.put("value", s.getId() + "");
                map.put("label", s.getBuildName());
                //楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(floorIds)) {
                    queryWrapper2.in("id", Arrays.asList(floorIds.split(", ")));
                } else {
                    queryWrapper2.eq("dictBuilding", s.getId());
                    if (null != foolId) {
                        queryWrapper2.eq("id", foolId);
                    }
                }
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2);

                if (CollectionUtil.isEmpty(sysBuildFloorList)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysBuildFloor sb : sysBuildFloorList) {
                        map2 = new HashMap<>();
                        map2.put("devId", "f_" + sb.getId());
                        map2.put("value", sb.getId() + "");
                        map2.put("label", sb.getFloorName());
                        //区域
                        QueryWrapper<SysBuildArea> queryWrapper3 = new QueryWrapper<>();
                        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(areaIds)) {
                            queryWrapper3.in("id", Arrays.asList(areaIds.split(", ")));
                        } else {
                            queryWrapper3.eq("floorId", sb.getId());
                            queryWrapper3.eq("isDel", 0);
                            if (null != areaId) {
                                queryWrapper3.eq("id", areaId);
                            }
                        }
                        queryWrapper3.orderByDesc("createTime");
                        List<SysBuildArea> sysBuildAreaList = sysBuildAreaMapper.selectList(queryWrapper3);
                        if (CollectionUtil.isEmpty(sysBuildAreaList)) {
                            map2.put("children", new ArrayList<>());
                            map2.put("disabled", true);
                        } else {
                            List<Map<String, Object>> mapList3 = new ArrayList<>();
                            for (SysBuildArea sa : sysBuildAreaList) {
                                map3 = new HashMap<>();
                                map3.put("devId", "a_" + sa.getId());
                                map3.put("value", sa.getId() + "");
                                map3.put("label", sa.getAreaName());
                                //视频设备
                                mapList3.add(map3);
                            }
                            map2.put("children", mapList3);
                        }
                        mapList2.add(map2);
                    }
                    map.put("children", mapList2);
                }
                //区域
                mapList.add(map);
            }
        }
        return mapList;
    }

    public List<SysOrgDept> selectOrgManageDeptUserList() {
        //数据查询
        QueryWrapper<SysOrgDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<SysOrgDept> list = sysOrgDeptMapper.selectList(queryWrapper);
        return list;
    }
}
