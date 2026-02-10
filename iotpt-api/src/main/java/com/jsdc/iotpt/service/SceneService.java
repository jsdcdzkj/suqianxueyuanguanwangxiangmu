//package com.jsdc.iotpt.service;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.jsdc.iotpt.base.BaseService;
//import com.jsdc.iotpt.enums.LogEnums;
//import com.jsdc.iotpt.mapper.*;
//import com.jsdc.iotpt.model.*;
//import com.jsdc.iotpt.util.StringUtils;
//import com.jsdc.iotpt.util.exception.CustomException;
//import com.jsdc.iotpt.vo.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Service
//@Transactional
//@Deprecated
//public class SceneService extends BaseService<Scene> {
//    @Autowired
//    private SysUserService sysUserService;
//
//    @Autowired
//    private SceneMapper sceneMapper;
//
//    @Autowired
//    DeviceCollectMapper deviceCollectMapper;
//
//    @Autowired
//    private ConfigSignalTypeItemMapper itemMapper;
//
//    @Autowired
//    private ConfigSignalTypeMapper configSignalTypeMapper ;
//
//    @Autowired
//    private SceneDeviceMapper sceneDeviceMapper ;
//
//    @Autowired
//    private ConfigDeviceTypeMapper configDeviceTypeMapper  ;
//
//    @Autowired
//    private ConfigDeviceSignalMapMapper configDeviceSignalMapMapper;
//    @Autowired
//    DeviceCollectService deviceCollectService;
//
//
//    public ResultInfo saveOrUpdateScene(Scene bean) {
//        boolean isSave =false;
//        if(null == bean.getId()){
//            isSave =true;
//            bean.setIsDel(0);
//            bean.setCreateTime(new Date());
//            bean.setUpdateTime(new Date());
//        }
//        saveOrUpdate(bean);
//
//        if(isSave){
//            //创建场景的时候 把会议室所有空调设备加入关联表中
//            List<DeviceCollectVo> dcVos = getDeviceMonitorins(bean);
//            List<SceneDevice> array = new ArrayList<>();
//            for (DeviceCollectVo vo: dcVos) {
//                SceneDevice sd = new SceneDevice();
//                sd.setDeviceId(String.valueOf(vo.getId()));
//                sd.setDeviceTypeName("空调");
//                sd.setName(vo.getName());
//                sd.setSceneId(bean.getId());
//                sd.setStatus("0");
//                sd.setPattern("3");
//                sd.setTemperature(25);
//                sd.setWindSpeed("1");
//                array.add(sd);
//            }
//            saveSceneDevice(array);
//        }
//        return ResultInfo.success();
//    }
//
//    /**
//     *  把本区域的空调设备找出来
//     * @param bean
//     * @return
//     */
//    private List<DeviceCollectVo> getDeviceMonitorins(Scene bean) {
//        Page<DeviceCollectVo> vos = deviceCollectService.deviceMonitoring(null, null, null,
//                null, "",
//                10002, 1, 30, "", null,
//                String.valueOf(bean.getLogicalAreaId()), "", "", null,null,null);
//
//        return vos.getRecords();
//    }
//
//
//    public Integer addScene(Scene scene) {
//        SysUser sysUser = sysUserService.getUser();
//        scene.setCreateUser(sysUser.getId());
//        scene.setCreateTime(new Date());
//        scene.setIsDel(0);
////        scene.setIsCheck("0");
//        scene.insert();
//
//        if(CollectionUtils.isNotEmpty(scene.getSceneDeviceList())){
//            for(SceneDevice s:scene.getSceneDeviceList()){
//                s.setSceneId(scene.getId());
//                s.insert() ;
//            }
//        }
//
//
//        return scene.getId();
//    }
//
//
//
//    public void updateScene(Scene scene) {
//        SysUser sysUser = sysUserService.getUser();
//        scene.setUpdateUser(sysUser.getId());
//        scene.setUpdateTime(new Date());
//        scene.updateById();
//
//        //删除之前的，重新新增
//        QueryWrapper<SceneDevice> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("sceneId",scene.getId()) ;
//        sceneDeviceMapper.delete(queryWrapper) ;
//
//        if(CollectionUtils.isNotEmpty(scene.getSceneDeviceList())){
//            for(SceneDevice s:scene.getSceneDeviceList()){
//                s.setSceneId(scene.getId());
//                s.insert() ;
//            }
//        }
//    }
//
//
//
//    public List<Scene> selectSceneList(DeviceCollect deviceCollect) {
//        QueryWrapper<Scene> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("logicalBuildId",deviceCollect.getLogicalBuildId()) ;
//        queryWrapper.eq("logicalFloorId",deviceCollect.getLogicalFloorId()) ;
//        queryWrapper.eq("logicalAreaId",deviceCollect.getLogicalAreaId()) ;
//        queryWrapper.eq("isDel","0") ;
//        List<Scene> sceneList = sceneMapper.selectList(queryWrapper);
//        return sceneList;
//    }
//
//
//    public Scene info(String id) {
//        Scene scene = sceneMapper.selectById(id);
//        //查子表设备
//        QueryWrapper<SceneDevice> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("sceneId",scene.getId()) ;
//        List<SceneDevice> sceneDeviceList = sceneDeviceMapper.selectList(queryWrapper) ;
//        if(CollectionUtils.isNotEmpty(sceneDeviceList)){
//            scene.setSceneDeviceList(sceneDeviceList);
//            for(SceneDevice d:sceneDeviceList){
//                if("空调".equals(d.getDeviceTypeName())){
//                    QueryWrapper<ConfigSignalType> queryWrapper3 = new QueryWrapper<>() ;
//                    queryWrapper3.eq("signalTypeCode","MODE") ;
//                    ConfigSignalType configSignalType =configSignalTypeMapper.selectOne(queryWrapper3) ;
//
//
//                    QueryWrapper<ConfigSignalType> queryWrapper2 = new QueryWrapper<>() ;
//                    queryWrapper2.eq("signalTypeCode","WIND_SPEED") ;
//                    ConfigSignalType configSignalType2 =configSignalTypeMapper.selectOne(queryWrapper2) ;
//
//
//                    List<ConfigSignalTypeItem> configSignalTypeItems=itemMapper.
//                            selectList(Wrappers.<ConfigSignalTypeItem>lambdaQuery().
//                                    eq(ConfigSignalTypeItem::getTypeId,configSignalType.getId()).
//                                    eq(ConfigSignalTypeItem::getIsDel,0));
//
//                    List<ConfigSignalTypeItem> configSignalTypeItems2=itemMapper.
//                            selectList(Wrappers.<ConfigSignalTypeItem>lambdaQuery().
//                                    eq(ConfigSignalTypeItem::getTypeId,configSignalType2.getId()).
//                                    eq(ConfigSignalTypeItem::getIsDel,0));
//                    d.setConfigSignalTypeItems(configSignalTypeItems) ;
//                    d.setConfigSignalTypeItems2(configSignalTypeItems2) ;
//                }
//            }
//
//
//        }
//
//
//
//
//        return scene ;
//    }
//
//
//    public Map<String,List> getList(DeviceCollectVo vo) {
//        Map<String,List> map = new HashMap<>() ;
//        List<DeviceCollectVo> deviceList = deviceCollectMapper.getList(vo);
//
//        List<Scene> sceneList = this.selectSceneList(vo);
//
//        if(CollectionUtils.isNotEmpty(deviceList)){
//            for(int i = 0 ;i<deviceList.size();i++){
//                DeviceCollectVo d = deviceList.get(i) ;
//                //设备只查事件  2  3  先查设备类型
//                ConfigDeviceType configDeviceType = configDeviceTypeMapper.selectById(d.getDeviceType()) ;
//                if(null != configDeviceType){
//
//                    List<ConfigDeviceSignalMapVo> configDeviceSignalMapVos = configDeviceSignalMapMapper.getEntityByTId(configDeviceType.getId()) ;
//                    boolean isshow = false ;
//                    if(CollectionUtils.isNotEmpty(configDeviceSignalMapVos)){
//                        for(ConfigDeviceSignalMapVo c:configDeviceSignalMapVos){
//                            if("2".equals(c.getSignalType()) || "3".equals(c.getSignalType())){
//                                isshow = true ;
//                            }
//                        }
//                    }
//                    if(!isshow){
//                        deviceList.remove(d) ;
//                        i-- ;
//                        continue;
//                    }
//
//
//                            if("空调".equals(d.getDeviceTypeName())){
//                                //查模式
//                                //空调类型的
//
//                                QueryWrapper<ConfigSignalType> queryWrapper3 = new QueryWrapper<>() ;
//                                queryWrapper3.eq("signalTypeCode","MODE") ;
//                                ConfigSignalType configSignalType =configSignalTypeMapper.selectOne(queryWrapper3) ;
//
//
//                                QueryWrapper<ConfigSignalType> queryWrapper2 = new QueryWrapper<>() ;
//                                queryWrapper2.eq("signalTypeCode","WIND_SPEED") ;
//                                ConfigSignalType configSignalType2 =configSignalTypeMapper.selectOne(queryWrapper2) ;
//
//
//                                List<ConfigSignalTypeItem> configSignalTypeItems=itemMapper.
//                                        selectList(Wrappers.<ConfigSignalTypeItem>lambdaQuery().
//                                                eq(ConfigSignalTypeItem::getTypeId,configSignalType.getId()).
//                                                eq(ConfigSignalTypeItem::getIsDel,0));
//
//                                List<ConfigSignalTypeItem> configSignalTypeItems2=itemMapper.
//                                        selectList(Wrappers.<ConfigSignalTypeItem>lambdaQuery().
//                                                eq(ConfigSignalTypeItem::getTypeId,configSignalType2.getId()).
//                                                eq(ConfigSignalTypeItem::getIsDel,0));
//                                d.setConfigSignalTypeItems(configSignalTypeItems) ;
//                                d.setConfigSignalTypeItems2(configSignalTypeItems2) ;
//                            }
//
//
//                }
//
//
//
//            }
//        }
//
//
//        map.put("deviceList",deviceList) ;
//        map.put("sceneList",sceneList) ;
//
//        return map ;
//    }
//
//    public ResultInfo saveSceneDevice(List<SceneDevice> sceneDevices) {
//        for (SceneDevice sd: sceneDevices) {
//            sceneDeviceMapper.insert(sd);
//        }
//        return ResultInfo.success();
//    }
//
//    public ResultInfo editSceneDevice(List<SceneDevice> sceneDevices) {
//        for (SceneDevice sd: sceneDevices) {
//            sceneDeviceMapper.updateById(sd);
//        }
//        return ResultInfo.success();
//    }
//    public List<Scene> getSceneList(Scene scene) {
//        QueryWrapper<Scene> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("roomId",scene.getRoomId()) ;
//        queryWrapper.eq("isDel","0") ;
//        List<Scene> sceneList = sceneMapper.selectList(queryWrapper);
//        return sceneList;
//    }
//
//    public List<SceneDevice> getSceneDeviceList(SceneDevice vo) {
//        QueryWrapper<SceneDevice> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("sceneId",vo.getSceneId()) ;
//        List<SceneDevice> sceneDeviceList = sceneDeviceMapper.selectList(queryWrapper);
//        return sceneDeviceList;
//    }
//}
