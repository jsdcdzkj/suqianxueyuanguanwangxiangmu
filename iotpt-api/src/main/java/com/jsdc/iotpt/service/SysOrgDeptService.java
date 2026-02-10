package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.DefaultClient;
import com.dahuatech.icc.oauth.http.IClient;
import com.dahuatech.icc.oauth.model.v202010.GeneralRequest;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.SysOrgDeptMapper;
import com.jsdc.iotpt.mapper.SysOrgManageMapper;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysOrgDeptService extends BaseService<SysOrgDept> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOrgDeptMapper sysOrgDeptMapper;

    @Autowired
    private SysOrgManageMapper sysOrgManageMapper;

    @Autowired
    private SysUserMapper sysUserMapper;


    /**
     * 部门新增
     * Author wzn
     * Date 2023/5/9 9:01
     */
    public ResultInfo addOrgDept(SysOrgDept sysOrgDept) {
        boolean check = checkOnly(sysOrgDept);
        if (!check) {
            return ResultInfo.error("部门已存在,禁止重复添加！");
        }

        if (null == sysOrgDept.getParentId()) {
            sysOrgDept.setParentId(0);
        }
        SysUser sysUser = sysUserService.getUser();
        sysOrgDept.setCreateUser(sysUser.getId());
        sysOrgDept.setCreateTime(new Date());
        sysOrgDept.setIsDel(0);
        sysOrgDept.insert();
        return ResultInfo.success();
    }

    /**
     * 部门修改
     * Author wzn
     * Date 2023/5/9 9:06
     */
    public ResultInfo updateOrgDept(SysOrgDept sysOrgDept) {

        boolean check = checkOnly(sysOrgDept);
        if (!check) {
            return ResultInfo.error("部门已存在,禁止重复添加！");
        }

        SysUser sysUser = sysUserService.getUser();
        sysOrgDept.setUpdateUser(sysUser.getId());
        sysOrgDept.setUpdateTime(new Date());
        sysOrgDept.updateById();
        return ResultInfo.success();
    }


    /**
     * 部门列表
     * Author wzn
     * Date 2023/5/9 9:09
     */
    public Page<SysOrgDept> selectOrgDeptList(SysOrgDept sysOrgDept) {
        Page<SysOrgDept> page = new Page<>(sysOrgDept.getPageNo(), sysOrgDept.getPageSize());


        //数据查询
        QueryWrapper<SysOrgManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<SysOrgManage> list = sysOrgManageMapper.selectList(queryWrapper);
        //新建一个用于接收数据的list
        List<SysOrgManage> resultList = new ArrayList<>();
        for (SysOrgManage result : list) {
            if (result.getId().equals(sysOrgDept.getOrgId())) {
                //调用方法给子类添加数据
                resultList.add(result);
                tree(result, list, resultList);
            }
        }
        List<String> strings = resultList.stream().map(p -> p.getId() + "").collect(Collectors.toList());
        String lettersCommaSeparated = String.join(",", strings);
        Page<SysOrgDept> sysOrgDeptPage = sysOrgDeptMapper.getList(page, sysOrgDept, lettersCommaSeparated);
        return sysOrgDeptPage;
    }




    private SysOrgManage tree(SysOrgManage result, List<SysOrgManage> list, List<SysOrgManage> resultList) {
        for (SysOrgManage menu : list) {
            //如果父类主键等于传过来实体类的ID
            if (menu.getParentId().equals(result.getId())) {
                // 递归调用
                resultList.add(tree(menu, list, resultList));
            }
        }
        return result;
    }


    /**
     * 部门详情
     * Author wzn
     * Date 2023/5/9 9:10
     */
    public SysOrgDept info(String id) {
        return sysOrgDeptMapper.selectById(id);
    }


    public boolean exist(Integer id) {
        boolean exist = false;
        //查儿子
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deptId", id);
        queryWrapper.eq("isDel", 0);
        Long count = sysUserMapper.selectCount(queryWrapper);
        if (count != 0) {
            exist = true;
        }
        return exist;
    }


    /**
     * 部门无限极
     * Author wzn
     * Date 2023/7/17 10:45
     */
    public List<SysOrgDept> findDept() {
        //数据查询
        QueryWrapper<SysOrgDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<SysOrgDept> list = baseMapper.selectList(queryWrapper);
        //新建一个用于接收数据的list
        List<SysOrgDept> resultList = new ArrayList<>();
        for (SysOrgDept result : list) {
            if (result.getParentId() == 0) {
                //调用方法给子类添加数据
                resultList.add(getDeptTree(result, list));
            }
        }
        return resultList;
    }

    private SysOrgDept getDeptTree(SysOrgDept result, List<SysOrgDept> list) {
        for (SysOrgDept menu : list) {
            //如果父类主键等于传过来实体类的ID
            if (menu.getParentId() == result.getId()) {
                if (result.getChildren() == null) {
                    result.setChildren(new ArrayList<>());
                }
                // 递归调用
                result.getChildren().add(getDeptTree(menu, list));
            }
        }
        return result;
    }

    public List<SysOrgDept> getList(SysOrgDept bean) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("isDel", 0);
        if (null != bean.getOrgId()) {
            queryWrapper.eq("orgId", bean.getOrgId());
        }

        List<SysOrgDept> sysOrgDepts = sysOrgDeptMapper.selectList(queryWrapper);

        List<SysOrgDept> resultList = new ArrayList<>();

        for (SysOrgDept result : sysOrgDepts) {
            if (result.getParentId() == 0) {
                //调用方法给子类添加数据
                resultList.add(getDeptTree(result, sysOrgDepts));
            }
            result.setPeopleCount(sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getDeptId, result.getId())));
        }
        return resultList;

    }


    public List<Map<String, Object>> deptTreeList() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = null;
        Map<String, Object> map2 = null;
        //查询部门列表数据
        List<SysOrgDept> depts = list(new LambdaQueryWrapper<SysOrgDept>().eq(SysOrgDept::getIsDel, 0));
        if (CollectionUtil.isNotEmpty(depts)) {
            for (SysOrgDept s : depts) {
                map = new HashMap<>();
                map.put("value", s.getId() + "");
                map.put("label", s.getDeptName());
                //用户列表
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding", s.getId());
                queryWrapper2.eq("isDel", 0);
                queryWrapper2.orderByAsc("sort");
                List<SysUser> users = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getDeptId, s.getId()));
                if (CollectionUtil.isEmpty(users)) {
                    map.put("children", new ArrayList<>());
                } else {
                    List<Map<String, Object>> mapList2 = new ArrayList<>();
                    for (SysUser sb : users) {
                        map2 = new HashMap<>();
                        map2.put("value", sb.getId() + "");
                        map2.put("label", sb.getRealName());
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
     * 无结构
     * 查询所有部门 查询列表无结构
     * @return
     */
    public List<SysOrgDept> findAllDept(SysOrgDept dept) {
        //数据查询
        QueryWrapper<SysOrgDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq(null != dept.getOrgId(),"orgId", dept.getOrgId());
        List<SysOrgDept> list = baseMapper.selectList(queryWrapper);

        return list;
    }



    public String departmentList(String iccId) throws ClientException {
        IClient iClient = new DefaultClient();

        GeneralRequest generalRequest =
                new GeneralRequest("/evo-apigw/evo-brm/1.2.0/department/tree", Method.POST);
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",1);
        map.put("parentId",iccId);
        map.put("checkStat","1");

        // 设置参数
        generalRequest.body(JSONUtil.toJsonStr(map));
        GeneralResponse response = iClient.doAction(generalRequest, generalRequest.getResponseClass());
        return JSONUtil.toJsonStr(response) ;
    }

    public void synchronization(String orgId,String iccId) {
        try {
            String response = departmentList(iccId) ;
            // 第一步：解析外层 JSON
            JSONObject outerJson = JSON.parseObject(response);
            String resultStr = outerJson.getString("result");  // 获取被转义的 result 字符串

            // 第二步：解析 result 中的 JSON 字符串
            JSONObject resultJson = JSON.parseObject(resultStr);
            JSONArray valueArray = resultJson.getJSONObject("data").getJSONArray("value");

            // 第三步：遍历并提取 id 和 name
            for (int i = 0; i < valueArray.size(); i++) {
                JSONObject item = valueArray.getJSONObject(i);
                int id = item.getIntValue("id");
                String name = item.getString("name");

                //如果本地没有则新增
                QueryWrapper<SysOrgDept> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("orgId", orgId);
                queryWrapper.eq("isDel", 0);
                queryWrapper.eq("deptName", name);
                List<SysOrgDept> sysOrgDeptList = sysOrgDeptMapper.selectList(queryWrapper);
                if(CollectionUtil.isEmpty(sysOrgDeptList)){
                    SysOrgDept sysOrgDept = new SysOrgDept();
                    sysOrgDept.setOrgId(Integer.valueOf(orgId));
                    sysOrgDept.setDeptName(name);
                    sysOrgDept.setDahuaDeptId(Long.valueOf(id));
                    sysOrgDept.setParentId(0);
                    SysUser sysUser = sysUserService.getUser();
                    sysOrgDept.setCreateUser(sysUser.getId());
                    sysOrgDept.setCreateTime(new Date());
                    sysOrgDept.setIsDel(0);
                    sysOrgDept.insert();
                }else {
                    for(SysOrgDept  sysOrgDept : sysOrgDeptList){
                        sysOrgDept.setDahuaDeptId(Long.valueOf(id));
                        sysOrgDept.updateById() ;
                    }
                }
            }
        } catch (ClientException e) {
            throw new CustomException(e.getErrMsg());
        }
    }



    /**
     * 部门唯一性校验
     * Author wzn
     * Date 2023/7/12 15:07
     */
    public boolean checkOnly(SysOrgDept sysOrgDept) {
        boolean checkOnly = true; //没有重复
        QueryWrapper<SysOrgDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deptName", sysOrgDept.getDeptName());
        queryWrapper.eq("orgId", sysOrgDept.getOrgId());
        queryWrapper.eq("isDel", 0);
        List<SysOrgDept> sysOrgManageList = sysOrgDeptMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sysOrgManageList)) {
            checkOnly = false;
            if (null != sysOrgDept.getId()) {
                if (sysOrgDept.getId().equals(sysOrgManageList.get(0).getId())) {
                    checkOnly = true;
                }
            }
        }
        return checkOnly;
    }
}