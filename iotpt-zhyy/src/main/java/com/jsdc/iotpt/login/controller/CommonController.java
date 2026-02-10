package com.jsdc.iotpt.login.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.SysBuildAreaMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.service.ConfigDeviceTypeService;
import com.jsdc.iotpt.service.SysOrgDeptService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.ConfigDeviceTypeVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (SysDict)表控制层
 *
 * @author wangYan
 * @since 2023-05-10
 */
@RestController
@RequestMapping("common")
@Api(tags = "公用数据接口")
public class CommonController {
    /**
     * 服务对象
     */
    @Resource
    private ConfigDeviceTypeService configDeviceTypeService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOrgDeptService sysOrgDeptService;

    @Autowired
    private SysBuildAreaMapper buildAreaMapper;



    /**
     * 得到redis数据字典返回map
     */
    @PostMapping("/getRedisDictMap")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisDictMap() {
        return ResultInfo.success(RedisUtils.getBeanValue("dictData"));
    }

    /**
     * 查询设备类型列表
     * @param bean
     * @return
     */
    @PostMapping("/getDeviceTypeList")
    @ApiOperation("查询设备类型列表")
    public ResultInfo getList(ConfigDeviceTypeVo bean) {
        return ResultInfo.success(configDeviceTypeService.getList(bean));
    }

    @PostMapping("/userList")
    @ApiOperation("用户列表")
    public ResultInfo getUserList(@RequestBody SysUser user) {
        return ResultInfo.success(sysUserService.getList(user));
    }

    @PostMapping("/userListDC")
    @ApiOperation("用户列表")
    public ResultInfo getUserListDC(@RequestBody SysUser user) {
        user.setUnitId(135);
        return ResultInfo.success(sysUserService.getList(user));
    }

    /**
     *  部门列表
     * @return
     */
    @PostMapping("/getDeptListDC")
    @ApiOperation(value = "部门列表", httpMethod = "POST")
    public ResultInfo getDeptListDC(@RequestBody SysOrgDept bean){
        List<SysOrgDept> list = sysOrgDeptService.getBaseMapper().selectList(new LambdaQueryWrapper<SysOrgDept>()
                .eq(SysOrgDept::getIsDel, 0)
                .eq(SysOrgDept::getOrgId, 135)
        );
        for (SysOrgDept dept : list) {
            if (StringUtils.isNotBlank(dept.getIds())) {
                List<String> areaIds = Arrays.asList(dept.getIds().split(","));
                List<SysBuildArea> areaList = buildAreaMapper.selectByIds(areaIds);
                List<String> collect = areaList.stream().map(i -> i.getFloorName() + "\\" + i.getAreaName()).collect(Collectors.toList());
                dept.setAddress(String.join("，", collect));
            }
        }
        return ResultInfo.success(list);
    }

    /**
     *  部门用户列表
     * @return
     */
    @PostMapping("/getDeptUserListDC")
    @ApiOperation(value = "部门用户列表", httpMethod = "POST")
    public ResultInfo getDeptUserListDC(@RequestBody SysOrgDept bean){
        List l = new ArrayList<>();
        // 部门
        List<SysOrgDept> depts = sysOrgDeptService.list(Wrappers.<SysOrgDept>lambdaQuery().eq(SysOrgDept::getIsDel, 0).eq(SysOrgDept::getOrgId, 135));
        // 获取有效的部门 ID 集合
        Set<Integer> validDeptIds = depts.stream()
                .map(SysOrgDept::getId)
                .collect(Collectors.toSet());
        // 人员
        List<SysUser> users = sysUserService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, 0));

        Map<Integer, List<SysUser>> map = users.stream()
                // 过滤掉 deptId 为空值的用户
                .filter(user -> user.getDeptId() != null)
                // 过滤掉 deptId 在 depts 中不存在的用户
                .filter(user -> validDeptIds.contains(user.getDeptId()))
                .collect(Collectors.groupingBy(SysUser::getDeptId));

        for (SysOrgDept dept : depts) {
            HashMap<Object, Object> tree = new HashMap<>();
            tree.put("value", dept.getId());
            tree.put("name",dept.getDeptName());
            tree.put("label",dept.getId()+"dept");
            List<Object> list = new ArrayList<>();
            // 与当前部门
            List<SysUser> collect = map.get(dept.getId());
            if (collect!= null){
                for (SysUser u :collect){
                    Map<String, Object> m = new HashMap<>();
                    m.put("value",  u.getId());
                    m.put("name",u.getRealName());
                    m.put("label",u.getId()+"user");
                    list.add(m);
                }
                tree.put("children", list);
            }
            l.add(tree);
        }
        return ResultInfo.success(l);
    }
}

