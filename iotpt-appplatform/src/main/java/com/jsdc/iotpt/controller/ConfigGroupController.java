package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.ConfigGroup;
import com.jsdc.iotpt.service.ConfigGroupService;
import com.jsdc.iotpt.service.ConfigLinkService;
import com.jsdc.iotpt.service.SysOrgManageService;
import com.jsdc.iotpt.vo.ConfigGroupVo;
import com.jsdc.iotpt.vo.ConfigLinkVo;
import com.jsdc.iotpt.vo.JobPlanVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: ConfigGroupController
 * Description: 分组管理
 * date: 2024/2/8 14:07
 *
 * @author bn
 */
@RestController
@RequestMapping("/configGroup")
@Api(tags = "分组管理")
public class ConfigGroupController {

    @Autowired
    ConfigGroupService configGroupService;
    @Autowired
    private SysOrgManageService sysOrgManageService;


    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping(value = "/getPageList",method = RequestMethod.POST)
    @ApiOperation("分组管理-分页查询")
    public ResultInfo getPageList(ConfigGroupVo bean, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {

        return ResultInfo.success(configGroupService.getPageList(bean,pageIndex,pageSize));
    }


    /**
     *  根据id获取数据
     * @return
     */
    @RequestMapping(value = "getGroupById.do",method = RequestMethod.POST)
    public ResultInfo getGroupById(Integer id){

        return ResultInfo.success(configGroupService.getGroupById(id));
    }


    /**
     * 获取全部单位部门人员
     * @return
     */
    @RequestMapping(value = "getManageDeptUserTree.do",method = RequestMethod.POST)
    public ResultInfo getManageDeptUserTree(){

        return ResultInfo.success(sysOrgManageService.getManageDeptUser());
    }



    /**
     *  新增
     * @return
     */
    @RequestMapping(value = "toAdd.do",method = RequestMethod.POST)
    public ResultInfo toAdd(@RequestBody ConfigGroupVo bean){

        return configGroupService.toAdd(bean);

    }


    /**
     *  新增
     * @return
     */
    @RequestMapping(value = "toEdit.do",method = RequestMethod.POST)
    public ResultInfo toEdit(@RequestBody ConfigGroupVo bean){

        return configGroupService.toEdit(bean);

    }

}
