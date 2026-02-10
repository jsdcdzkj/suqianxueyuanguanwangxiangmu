//package com.jsdc.iotpt.controller;
//
//import com.jsdc.iotpt.model.Scene;
//import com.jsdc.iotpt.model.SceneDevice;
//import com.jsdc.iotpt.service.SceneService;
//import com.jsdc.iotpt.service.SysOrgManageService;
//import com.jsdc.iotpt.vo.DeviceCollectVo;
//import com.jsdc.iotpt.vo.ResultInfo;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// *  场景管理
// *  @author lb
// */
//@RestController
//@RequestMapping("/scene")
//@Api(tags = "场景管理")
//@Deprecated
//public class SceneController {
//
//    @Autowired
//    private SysOrgManageService sysOrgManageService;
//
//    @Autowired
//    private SceneService sceneService;
//
//
//    /**
//     * 场景新增
//     * Author wzn
//     */
//    @PostMapping("/addScene")
//    @ApiOperation("场景新增")
//    public ResultInfo addScene(@RequestBody Scene scene) {
//        return ResultInfo.success( sceneService.saveOrUpdateScene(scene));
//    }
//
//
//    /**
//     * 场景修改
//     * Author wzn
//     */
//    @PostMapping("/updateScene")
//    @ApiOperation("场景修改")
//    public ResultInfo updateScene(@RequestBody Scene scene) {
//        sceneService.saveOrUpdateScene(scene);
//        return ResultInfo.success();
//    }
//
//    /**
//     * 场景删除
//     * Author wzn
//     */
//    @PostMapping("/delScene")
//    @ApiOperation("场景删除")
//    public ResultInfo delScene(Integer id) {
//        Scene scene = new Scene();
//        scene.setId(id);
//        scene.setIsDel(1);
//        sceneService.updateById(scene);
//        return ResultInfo.success();
//    }
//
//    /**
//     * 场景列表
//     * Author wzn
//     */
////    @PostMapping("/sceneList")
////    @ApiOperation("场景列表")
////    public ResultInfo sceneList(@RequestBody Scene scene) {
////        List<Scene> sceneList = sceneService.selectSceneList(scene);
////        return ResultInfo.success(sceneList);
////    }
//
//
//    /**
//     * 场景详情
//     * Author wzn
//     */
//    @PostMapping("/info")
//    @ApiOperation("场景详情")
//    public ResultInfo info(String id) {
//        Scene scene = sceneService.info(id) ;
//        return ResultInfo.success(scene);
//    }
//
//    /**
//     * 逻辑位置 任意层级可选择
//     * 区域三级树形图
//     */
//    @PostMapping("/areaTreeList2")
//    @ApiOperation("区域三级树形图")
//    public ResultInfo areaTreeList2() {
//        return ResultInfo.success(sysOrgManageService.areaTreeList2());
//    }
//
//
//
//
//    @PostMapping("/getList")
//    @ApiOperation(value = "根据逻辑位置查设备", notes = "选一级，二级时logicalAreaId传0  选3级时logicalAreaId传区域value值，选一级时logicalFloorId值为0  选二级或者三级时logicalFloorId传楼层value")
//    public ResultInfo getList(@RequestBody DeviceCollectVo vo) {
//        return ResultInfo.success(sceneService.getList(vo));
//    }
//
//
//    @PostMapping("/getSceneList")
//    @ApiOperation(value = "根据逻辑位置查设备", notes = "选一级，二级时logicalAreaId传0  选3级时logicalAreaId传区域value值，选一级时logicalFloorId值为0  选二级或者三级时logicalFloorId传楼层value")
//    public ResultInfo getSceneList(@RequestBody Scene vo) {
//        return ResultInfo.success(sceneService.getSceneList(vo));
//    }
//    @PostMapping("/addSceneDevice")
//    @ApiOperation("场景新增")
//    public ResultInfo addScene(@RequestBody List<SceneDevice> sceneDevice) {
//        return ResultInfo.success( sceneService.saveSceneDevice(sceneDevice));
//    }
//
//    /**
//     * 修改场景中的设备状态 如开头状态 风速 温度等
//     * @param sceneDevice
//     * @return
//     */
//    @PostMapping("/editSceneDevice")
//    @ApiOperation("场景新增")
//    public ResultInfo editSceneDevice(@RequestBody List<SceneDevice> sceneDevice) {
//        return ResultInfo.success( sceneService.editSceneDevice(sceneDevice));
//    }
//}
