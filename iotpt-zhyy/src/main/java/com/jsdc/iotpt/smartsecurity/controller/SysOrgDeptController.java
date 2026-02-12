//package com.jsdc.iotpt.smartsecurity.controller;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.jsdc.iotpt.mapper.SysBuildAreaMapper;
//import com.jsdc.iotpt.model.sys.SysBuildArea;
//import com.jsdc.iotpt.model.sys.SysOrgDept;
//import com.jsdc.iotpt.service.SysOrgDeptService;
//import com.jsdc.iotpt.vo.ResultInfo;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("sysDept")
//@Api(tags = "系统用户管理")
//public class SysOrgDeptController {
//
//
//    @Autowired
//    private SysOrgDeptService sysOrgDeptService;
//
//    @Autowired
//    private SysBuildAreaMapper buildAreaMapper;
//
//    /**
//     *  部门列表
//     * @return
//     */
//    @PostMapping("/getList")
//    @ApiOperation(value = "部门列表", httpMethod = "POST")
//    public ResultInfo getList(@RequestBody SysOrgDept bean){
//        List<SysOrgDept> list = sysOrgDeptService.getBaseMapper().selectList(new LambdaQueryWrapper<SysOrgDept>()
//                .eq(SysOrgDept::getIsDel, 0)
//                .eq(SysOrgDept::getParentId, bean.getParentId())
//        );
//        for (SysOrgDept dept : list) {
//            if (StringUtils.isNotBlank(dept.getIds())) {
//                List<String> areaIds = Arrays.asList(dept.getIds().split(","));
//                List<SysBuildArea> areaList = buildAreaMapper.selectByIds(areaIds);
//                List<String> collect = areaList.stream().map(i -> i.getFloorName() + "\\" + i.getAreaName()).collect(Collectors.toList());
//                dept.setAddress(String.join("，", collect));
//            }
//        }
//        return ResultInfo.success(list);
//    }
//
//
//}
