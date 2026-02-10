package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ConfigDeviceSubitemMapper;
import com.jsdc.iotpt.mapper.SysBuildAreaMapper;
import com.jsdc.iotpt.mapper.SysBuildFloorMapper;
import com.jsdc.iotpt.mapper.SysBuildMapper;
import com.jsdc.iotpt.model.ConfigDeviceSubitem;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.SpringUtils;
import com.jsdc.iotpt.vo.FloorAiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Transactional
public class SysBuildService extends BaseService<SysBuild> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysBuildMapper sysBuildMapper;

    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;

    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper ;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    MemoryCacheService memoryCacheService;

    @Autowired
    private ConfigDeviceSubitemMapper configDeviceSubitemMapper;
    /**
    *楼宇新增
    * Author wzn
    * Date 2023/5/9 9:01
    */
    public void addSysBuild(SysBuild sysBuild) {
        //没所属楼宇默认为父级
        if(null == sysBuild.getDictBuilding()){
            sysBuild.setDictBuilding(0);
        }
        SysUser sysUser = sysUserService.getUser();
        sysBuild.setCreateUser(sysUser.getId());
        sysBuild.setCreateTime(new Date());
        sysBuild.setIsDel(0);
        sysBuild.insert();
//        if(sysBuild.getFile() != null){
//            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId",sysBuild.getId()).eq("bizType","1"));
//            UploadParams params = new UploadParams("/build",sysBuild.getFile(),"1",sysBuild.getId()+"");
//            sysFileService.uploadFile(params);
//        }

        memoryCacheService.putAllBuild();
    }

    /**
    *楼宇修改
    * Author wzn
    * Date 2023/5/9 9:06
    */
    public void updateSysBuild(SysBuild sysBuild) {
        SysUser sysUser = sysUserService.getUser();
//        if(sysBuild.getFile() != null){
//            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId",sysBuild.getId()).eq("bizType","1"));
//            UploadParams params = new UploadParams("/build",sysBuild.getFile(),"1",sysBuild.getId()+"");
//            sysFileService.uploadFile(params);
//        }else {
//            if(sysBuild.getDelPic()){
//                sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId",sysBuild.getId()).eq("bizType","1"));
//            }
//        }
        sysBuild.setUpdateUser(sysUser.getId());
        sysBuild.setUpdateTime(new Date());
        sysBuild.updateById();

        memoryCacheService.putAllBuild();
    }


    /**
    *楼宇列表
    * Author wzn
    * Date 2023/5/9 9:09
    */
    public Page<SysBuild> selectBuildList(SysBuild sysBuild) {
        Page<SysBuild> page = new Page<>(sysBuild.getPageNo(), sysBuild.getPageSize());
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (!"".equals(sysBuild.getBuildName()) && null != sysBuild.getBuildName()) {
            queryWrapper.like("buildName", sysBuild.getBuildName());
        }
        queryWrapper.orderByDesc("createTime") ;
        Page<SysBuild> sysBuildPage = sysBuildMapper.selectPage(page, queryWrapper);
        return sysBuildPage;
    }


    /**
    *楼宇详情
    * Author wzn
    * Date 2023/5/9 9:10
    */
    public SysBuild info(String id) {

        return sysBuildMapper.selectById(id);
    }



    /**
    * 查是否存在楼层,区域
    * Author wzn
    * Date 2023/7/11 9:01
    */
    public boolean exist(String id) {
        boolean exist = false ;
        //查楼层
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("dictBuilding",id) ;
        queryWrapper.eq("isDel",0) ;
        Long count = sysBuildFloorMapper.selectCount(queryWrapper) ;
        if(count != 0 ){
            exist = true ;
        }
        return exist;
    }



    public List<Map<String,Object>>  bulidTreeList(){
        List<Map<String,Object>> mapList = new ArrayList<>() ;

        Map<String,Object> map =null;
        Map<String,Object> map2 =null;
        //查楼宇信息
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel",0) ;
        queryWrapper.orderByDesc("createTime") ;
        List<SysBuild> sysBuildList = sysBuildMapper.selectList(queryWrapper) ;
        if(CollectionUtil.isNotEmpty(sysBuildList)){
            for(SysBuild s:sysBuildList){
                map =  new HashMap<>() ;
                map.put("id",s.getId()+"") ;
                map.put("label",s.getBuildName()) ;
                map.put("type","0") ;
                //查楼层
                QueryWrapper<SysBuildFloor> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("dictBuilding",s.getId()) ;
                queryWrapper2.eq("isDel",0) ;
                queryWrapper2.orderByAsc("sort") ;
                List<SysBuildFloor> sysBuildFloors = sysBuildFloorMapper.selectList(queryWrapper2) ;

                if(CollectionUtil.isEmpty(sysBuildFloors)){
                    map.put("children",new ArrayList<>()) ;
                }else {
                    List<Map<String,Object>> mapList2 = new ArrayList<>() ;
                    for(SysBuildFloor sb:sysBuildFloors){
                        map2 =  new HashMap<>() ;
                        map2.put("id",sb.getId()+"") ;
                        map2.put("label",sb.getFloorName()) ;
                        map2.put("type","1") ;
                        mapList2.add(map2) ;
                    }

                    map.put("children",mapList2) ;
                }
                mapList.add(map) ;
            }


        }
        return mapList ;
    }

    public List<SysBuild> getList(SysBuild vo) {
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        return SpringUtils.getBean(SysBuildMapper.class).selectList(queryWrapper);
    }


    public List<FloorAiInfo>  aiBulidTreeList(){
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel",0) ;
        queryWrapper.orderByAsc("sort") ;
        List<SysBuildFloor> sysBuildFloorList = sysBuildFloorMapper.selectList(queryWrapper);
        List<FloorAiInfo> list = new ArrayList<>() ;
        if(!CollectionUtils.isEmpty(sysBuildFloorList)){
            for(SysBuildFloor sb:sysBuildFloorList){
                FloorAiInfo floorAiInfo = new FloorAiInfo() ;
                floorAiInfo.setId(sb.getId()+"") ;
                floorAiInfo.setName(sb.getFloorName());
                list.add(floorAiInfo) ;
            }
        }
        return list ;
    }


    public List<FloorAiInfo>  aiAreareeList(String floorId){
        QueryWrapper<SysBuildArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel",0) ;
        if(StrUtil.isNotEmpty(floorId)){
            queryWrapper.eq("floorId", floorId) ;
        }
        queryWrapper.orderByAsc("sort") ;
        List<SysBuildArea> sysBuildFloorList = sysBuildAreaMapper.selectList(queryWrapper);
        List<FloorAiInfo> list = new ArrayList<>() ;
        if(!CollectionUtils.isEmpty(sysBuildFloorList)){
            for(SysBuildArea sb:sysBuildFloorList){
                FloorAiInfo floorAiInfo = new FloorAiInfo() ;
                floorAiInfo.setId(sb.getId()+"") ;
                floorAiInfo.setName(sb.getAreaName());
                list.add(floorAiInfo) ;
            }
        }
        return list ;
    }


    public List<FloorAiInfo>  aiSubitemIdList(){
        QueryWrapper<ConfigDeviceSubitem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel",0) ;
        List<ConfigDeviceSubitem> sysBuildFloorList = configDeviceSubitemMapper.selectList(queryWrapper);
        List<FloorAiInfo> list = new ArrayList<>() ;
        if(!CollectionUtils.isEmpty(sysBuildFloorList)){
            for(ConfigDeviceSubitem sb:sysBuildFloorList){
                FloorAiInfo floorAiInfo = new FloorAiInfo() ;
                floorAiInfo.setId(sb.getId()+"") ;
                floorAiInfo.setName(sb.getSubitemName());
                list.add(floorAiInfo) ;
            }
        }
        return list ;
    }

    public List<FloorAiInfo>  aiSubitemIdList2(String type){
        QueryWrapper<ConfigDeviceSubitem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel",0) ;
        if(StringUtils.isNotEmpty(type)){
            queryWrapper.eq("energy_type",type) ;
        }
        List<ConfigDeviceSubitem> sysBuildFloorList = configDeviceSubitemMapper.selectList(queryWrapper);
        List<FloorAiInfo> list = new ArrayList<>() ;
        if(!CollectionUtils.isEmpty(sysBuildFloorList)){
            for(ConfigDeviceSubitem sb:sysBuildFloorList){
                FloorAiInfo floorAiInfo = new FloorAiInfo() ;
                floorAiInfo.setId(sb.getId()+"") ;
                floorAiInfo.setName(sb.getSubitemName());
                list.add(floorAiInfo) ;
            }
        }
        return list ;
    }
}
