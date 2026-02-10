package com.jsdc.iotpt.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.TreeParserUtils;
import com.jsdc.iotpt.vo.SysBuildAreaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysBuildAreaService extends BaseService<SysBuildArea> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;

    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper ;

    @Autowired
    private SysOrgDeptMapper sysOrgDeptMapper ;

    @Autowired
    private DeviceCollectMapper deviceCollectMapper ;

    @Autowired
    private DeviceAccessControlMapper deviceAccessControlMapper ;

    @Autowired
    private DeviceGatewayMapper deviceGatewayMapper ;

    @Autowired
    MemoryCacheService memoryCacheService;
    /**
     * 区域新增
     * Author wzn
     * Date 2023/5/9 9:01
     */
    public void addSysBuildArea(SysBuildArea sysBuildArea) {
        SysUser sysUser = sysUserService.getUser();
        sysBuildArea.setCreateUser(sysUser.getId());
        sysBuildArea.setCreateTime(new Date());
        sysBuildArea.setIsDel(0);
        sysBuildArea.insert();
        memoryCacheService.putAllArea();
    }

    /**
     * 区域修改
     * Author wzn
     * Date 2023/5/9 9:06
     */
    public void updateSysBuildArea(SysBuildArea sysBuildArea) {

        SysUser sysUser = sysUserService.getUser();
        sysBuildArea.setUpdateUser(sysUser.getId());
        sysBuildArea.setUpdateTime(new Date());
        sysBuildArea.updateById();
        memoryCacheService.putAllArea();
    }


    /**
     * 区域列表
     * Author wzn
     * Date 2023/5/9 9:09
     */
    public Page<SysBuildArea> selectBuildAreaList(SysBuildAreaVo sysBuildArea) {
        Page<SysBuildArea> page = new Page<>(sysBuildArea.getPageNo(), sysBuildArea.getPageSize());
        QueryWrapper<SysBuildArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List ids = new ArrayList();
        if (!"".equals(sysBuildArea.getAreaName()) && null != sysBuildArea.getAreaName()) {
            queryWrapper.like("areaName", sysBuildArea.getAreaName());
        }

        if (!"".equals(sysBuildArea.getFloorId()) && null != sysBuildArea.getFloorId()) {
            queryWrapper.eq("floorId", sysBuildArea.getFloorId());
        }
        if (!"".equals(sysBuildArea.getBuildId()) && null != sysBuildArea.getBuildId()) {
            //查所有楼层
            QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("dictBuilding",sysBuildArea.getBuildId()) ;
            List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper2) ;
            if(CollectionUtil.isNotEmpty(sysBuildFloorList)){
                ids = sysBuildFloorList.stream().map(SysBuildFloor::getId).collect(Collectors.toList());
                queryWrapper.in("floorId",ids) ;

            }else {
                return page ;
            }

        }
        queryWrapper.orderByDesc("createTime");
        Page<SysBuildArea> sysBuildAreaPage = sysBuildAreaMapper.selectPage(page, queryWrapper);
        return sysBuildAreaPage;
    }


    /**
     * 查询 todo
     *
     * @return
     */
    public List<SysBuildArea> getList(SysBuildAreaVo vo) {
        QueryWrapper<SysBuildArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotNull(vo.getFloorId())) {
                queryWrapper.eq("floorId", vo.getFloorId());
            }
        }
        return sysBuildAreaMapper.selectList(queryWrapper);
    }

    /**
     *
     * @param vo
     * @return
     */
    public List<SysBuildArea> getList4GS(SysBuildAreaVo vo) {
        QueryWrapper<SysBuildArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        //是否对GS展示
        queryWrapper.eq("isLargeScreenDisplay", 1);
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotNull(vo.getFloorId())) {
                queryWrapper.eq("floorId", vo.getFloorId());
            }
        }
        queryWrapper.orderByDesc("sort");
        return sysBuildAreaMapper.selectList(queryWrapper);
    }

    public List<SysBuildArea> getAllList(SysBuildAreaVo vo) {

        return sysBuildAreaMapper.getAllList(vo);
    }

    /**
     * 区域详情
     * Author wzn
     * Date 2023/5/9 9:10
     */
    public SysBuildArea info(String id) {
        return sysBuildAreaMapper.selectById(id);
    }



    /**
    *查区域是否被部门使用
    * Author wzn
    * Date 2023/7/14 17:13
    */
    public boolean exist(String id) {
        boolean exist = false ;
        Integer count = sysOrgDeptMapper.exist(id) ;
        if(count != 0 ){
            exist = true ;
        }
        return exist;
    }

    /**
    *查区域是否被采集终端使用
    * Author wzn
    * Date 2023/7/14 17:15
    */
    public boolean existDevice(String id) {
        boolean exist = false ;
        QueryWrapper<DeviceCollect> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("areaId",id) ;
        queryWrapper.eq("isDel",0) ;
        Long count = deviceCollectMapper.selectCount(queryWrapper) ;
        if(count != 0 ){
            exist = true ;
        }
        return exist;
    }


    /**
    *查区域是否被门禁设备使用
    * Author wzn
    * Date 2023/7/14 17:18
    */
    public boolean existDeviceAccess(String id) {
        boolean exist = false ;
        QueryWrapper<DeviceAccessControl> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("areaId",id) ;
        queryWrapper.eq("isDel",0) ;
        Long count = deviceAccessControlMapper.selectCount(queryWrapper) ;
        if(count != 0 ){
            exist = true ;
        }
        return exist;
    }


    /**
    *查区域是否被网关设备使用
    * Author wzn
    * Date 2023/7/14 17:23
    */
    public boolean existDeviceGateway(String id) {
        boolean exist = false ;
        QueryWrapper<DeviceGateway> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("areaId",id) ;
        queryWrapper.eq("isDel",0) ;
        Long count = deviceGatewayMapper.selectCount(queryWrapper) ;
        if(count != 0 ){
            exist = true ;
        }
        return exist;
    }


    /**
     * 根据类型得到菜单树
     * @param bean
     * @return
     */
    public Object getAreaTypeTree(SysBuildArea bean) {
        List<SysBuildArea> list = baseMapper.selectList(new LambdaQueryWrapper<SysBuildArea>()
                .eq(StrUtil.isNotEmpty(bean.getAreaName()),SysBuildArea::getAreaName,bean.getAreaName())
                .orderByAsc(SysBuildArea::getSort));
        TreeNodeConfig config = new TreeNodeConfig();
        config.setNameKey("id");
        config.setNameKey("label");
        config.setChildrenKey("children");
        List<Tree<String>> treeNodes =
                TreeUtil.build(list, "0", config, (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setName(treeNode.getAreaName());
                    tree.setParentId(treeNode.getBuildFloorId().toString());
                });
        return treeNodes;
    }
}
