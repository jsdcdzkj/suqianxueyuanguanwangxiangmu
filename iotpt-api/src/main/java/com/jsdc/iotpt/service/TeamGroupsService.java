package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.SysRole;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.SysUserRole;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.model.operate.TeamGroups;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TeamGroupsService extends BaseService<TeamGroups> {

    @Autowired
    private TeamGroupsMapper teamGroupsMapper;

    @Autowired
    private TeamGroupUserMapper teamGroupUserMapper;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysOrgManageService sysOrgManageService;

    @Autowired
    private SysOrgDeptService orgDeptService;
    @Autowired
    private SysUserRoleService userRoleService;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 获取组员角色字典值
     *
     * @return
     */
    public List<SysDict> getroleDicts() {
        List<SysDict> list = new ArrayList<>();
        list = sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictType, "team_group_role").eq(SysDict::getIsDel, G.ISDEL_NO));
        return list;
    }


    /**
     * 分页查询班组列表
     *
     * @param bean
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<TeamGroups> getTeamGroupPage(TeamGroups bean, Integer pageIndex, Integer pageSize) {
        Page<TeamGroups> page = new Page(pageIndex, pageSize);
        Page<TeamGroups> p = baseMapper.selectPage(page, Wrappers.<TeamGroups>lambdaQuery()
                .like(StringUtils.isNotBlank(bean.getName()), TeamGroups::getName, bean.getName())
                .eq(TeamGroups::getIsDel, G.ISDEL_NO)
                .orderByDesc(TeamGroups::getCreateTime));

        List<SysDict> roleList = getroleDicts();
        Map<String, String> roleMap = roleList.stream().collect(Collectors.toMap(SysDict::getDictValue, SysDict::getDictLabel, (key1, key2) -> key2));


        p.getRecords().forEach(x -> {

            //组长
            List<TeamGroupUser> list = teamGroupUserMapper.selectList(Wrappers.<TeamGroupUser>lambdaQuery().eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getGroupId, x.getId()));
            for (int i = 0; i < list.size(); i++) {
                if (StringUtils.isNotBlank(list.get(i).getTeamRole())) {
                    if (roleMap.containsKey(list.get(i).getTeamRole()) && "1".equals(list.get(i).getTeamRole())) {
                        if (null != list.get(i).getUserId()) {
                            SysUser sysUser = sysUserMapper.selectById(list.get(i).getUserId());
                            if (null != sysUser) {
                                x.setGroupLeader(sysUser.getRealName());
                            }
                        }
                    }
                }
            }
            //人员数量
            x.setPeopleNum(list.size());
        });
        return p;
    }

    /**
     * 新增修改班组
     *
     * @param bean
     * @return
     */
    public ResultInfo editTeamGroup(TeamGroups bean) {
        if (null == bean.getId()) {
            //新增
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
            bean.setIsDel(G.ISDEL_NO);
            teamGroupsMapper.insert(bean);
        } else {
            //修改
            bean.setUpdateUser(sysUserService.getUser().getId());
            bean.setUpdateTime(new Date());
            teamGroupsMapper.updateById(bean);
            //删除人员班组关联表数据
            UpdateWrapper<TeamGroupUser> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("isDel", G.ISDEL_YES).eq("groupId", bean.getId()).eq("isDel", G.ISDEL_NO);
            teamGroupUserMapper.update(null, updateWrapper);
        }
        //添加人员班组关联数据
        List<TeamGroupUser> list = bean.getList();
        if (!CollectionUtils.isEmpty(list)) {
            for (int i = 0; i < bean.getList().size(); i++) {
                TeamGroupUser teamGroupUser = new TeamGroupUser();
                teamGroupUser.setUserId(bean.getList().get(i).getUserId());
                teamGroupUser.setTeamRole(bean.getList().get(i).getTeamRole());
                teamGroupUser.setGroupId(bean.getId());
                teamGroupUser.setCreateTime(new Date());
                teamGroupUser.setCreateUser(sysUserService.getUser().getId());
                teamGroupUser.setIsDel(G.ISDEL_NO);
                teamGroupUserMapper.insert(teamGroupUser);
            }
        } else {
            return ResultInfo.error("未选择班组人员");
        }
        return ResultInfo.success();
    }


    /**
     * 删除班组
     *
     * @param id
     * @return
     */
    public ResultInfo delTeamGroup(Integer id) {
        TeamGroups teamGroups = teamGroupsMapper.selectById(id);
        teamGroups.setIsDel(G.ISDEL_YES);
        Integer result = teamGroupsMapper.updateById(teamGroups);
        //删除人员班组关联表数据
        if (1 == result) {
            UpdateWrapper<TeamGroupUser> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("isDel", G.ISDEL_YES).eq("groupId", id).eq("isDel", G.ISDEL_NO);
            teamGroupUserMapper.update(null, updateWrapper);
        } else {
            return ResultInfo.error("删除失败");
        }

        return ResultInfo.success();
    }


    /**
     * 详情
     *
     * @param id
     * @return
     */
    public TeamGroups getOneDetail(Integer id) {
        TeamGroups x = teamGroupsMapper.selectById(id);
        List<SysDict> roleList = getroleDicts();
        Map<String, String> roleMap = roleList.stream().collect(Collectors.toMap(SysDict::getDictValue, SysDict::getDictLabel, (key1, key2) -> key2));
        //查询班组成员设置班组成员角色
        List<TeamGroupUser> list = teamGroupUserMapper.selectList(Wrappers.<TeamGroupUser>lambdaQuery().eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getGroupId, x.getId()).orderByAsc(TeamGroupUser::getTeamRole));

        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.isNotBlank(list.get(i).getTeamRole())) {
                if (roleMap.containsKey(list.get(i).getTeamRole())) {
                    list.get(i).setTeamRoleName(roleMap.get(list.get(i).getTeamRole()));
                }
            }

            if (null != list.get(i).getUserId()) {
                SysUser sysUser = sysUserMapper.selectById(list.get(i).getUserId());
                if (null != sysUser) {
                    list.get(i).setRealName(sysUser.getRealName());

                    Integer deptId = sysUser.getDeptId();
                    String orgName = "";
                    String deptName = "";
                    if (null != sysUser.getUnitId()) {
                        SysOrgManage org = sysOrgManageService.getById(sysUser.getUnitId());
                        orgName = null == org ? "" : org.getOrgName();
                    }
                    if (null != deptId) {
                        SysOrgDept dept = orgDeptService.getById(deptId);
                        if (null != dept) {
                            deptName = dept.getDeptName();
                        }
                    }
//                    list.get(i).setOrgName(orgName);
//                    list.get(i).setDeptName(deptName);
                    list.get(i).setPhone(sysUser.getPhone());
                    if (StringUtils.isNotBlank(orgName) && StringUtils.isNotBlank(deptName)) {
                        list.get(i).setDeptName(orgName + "-" + deptName);
                    } else if (StringUtils.isNotBlank(orgName)) {
                        list.get(i).setDeptName(orgName);
                    } else if (StringUtils.isNotBlank(deptName)) {
                        list.get(i).setDeptName(deptName);
                    }

                    // 角色
                    List<SysUserRole> userRoleList = userRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
                            .eq(SysUserRole::getUserId, sysUser.getId()).eq(SysUserRole::getIsDel, G.ISDEL_NO));
                    if (!CollectionUtils.isEmpty(userRoleList)) {
                        List<SysRole> roles = sysRoleMapper.selectList(Wrappers.<SysRole>lambdaQuery()
                                .in(SysRole::getId, userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()))
                                .eq(SysRole::getIsDel, G.ISDEL_NO)
                        );
                        if (!CollectionUtils.isEmpty(roles)) {
                            List<String> roleNames = roles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(roleNames)) {
                                // 用逗号分隔
                                list.get(i).setRoleNamesStr(String.join(",", roleNames));
                            }

                        }
                    }


                }
            }
        }
        x.setList(list);
        //人员数量
        x.setPeopleNum(list.size());
        return x;
    }

    /**
     * 查询所有班组
     *
     * @return
     */
    public List<TeamGroups> getAllList() {
        List<TeamGroups> list = teamGroupsMapper.selectList(Wrappers.<TeamGroups>lambdaQuery().eq(TeamGroups::getIsDel, G.ISDEL_NO).orderByDesc(TeamGroups::getCreateTime));
        return list;
    }


    /**
     * 根据当前登录用户获取对应的班组
     *
     * @return
     */
    public List<Integer> getGroupByUser() {
        Integer id = sysUserService.getUser().getId();
        List<TeamGroupUser> list = teamGroupUserMapper.selectList(Wrappers.<TeamGroupUser>lambdaQuery().eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getUserId, id));
        //添加到set集合 去重
        Set<Integer> set = list.stream().map(TeamGroupUser::getGroupId).collect(Collectors.toSet());
        List<Integer> ids = set.stream().collect(Collectors.toList());
        return ids;
    }

    /**
     * 根据当前登录用户以及班组ID 判断当前用户是否在这个班组
     *
     * @param id
     * @return
     */
    public boolean isInGroup(Integer id) {
        Integer userId = sysUserService.getUser().getId();
        long count = teamGroupUserMapper.selectCount(Wrappers.<TeamGroupUser>lambdaQuery().eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getUserId, userId).eq(TeamGroupUser::getGroupId, id));
        return 0 != count;
    }


    /**
     * 判断当前登录用户是不是组长
     */
    public Integer isGroupLeader(Integer groupId, Integer userId) {
        if (null != userId && null != groupId) {
            List<TeamGroupUser> list = teamGroupUserMapper.selectList(Wrappers.<TeamGroupUser>lambdaQuery()
                    .eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getUserId, userId).eq(TeamGroupUser::getGroupId, groupId));
            if (!CollectionUtils.isEmpty(list)) {
                TeamGroupUser teamGroupUser = list.get(0);
                if (StringUtils.equals("1", teamGroupUser.getTeamRole())) {
                    return 1;
                } else {
                    return 0;
                }
//            list.forEach(x -> {
//                if (StringUtils.equals("1", x.getTeamRole())) {
//                    map.put(x.getGroupId(), true);
//                } else {
//                    map.put(x.getGroupId(), false);
//                }
//            });
            }
        }
        return 0;
    }


    public List<TeamGroups> getTeamlist(){
        LambdaQueryWrapper<TeamGroups> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(TeamGroups::getIsDel,0);
        List<TeamGroups> list = this.list(wrapper);
        list.forEach(a->{
            List<TeamGroupUser> teamGroupUsers = teamGroupUserMapper.selectList(Wrappers.<TeamGroupUser>lambdaQuery().eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getGroupId, a.getId()).orderByAsc(TeamGroupUser::getTeamRole));
            a.setList(teamGroupUsers);
            a.getList().forEach(b->{
                SysUser sysUser = sysUserMapper.selectById(b.getUserId());
                if (null != sysUser) {
                    b.setRealName(sysUser.getRealName());
                }
            });
        });
        return list;
    }

}
