package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.service.SysOrgDeptService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门
 * Description:
 * date: 2023/11/28 11:39
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/sysOrgDept")
public class SysOrgDeptController {

    @Autowired
    private SysOrgDeptService sysOrgDeptService;

    /**
     *  部门列表
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation(value = "部门列表", httpMethod = "POST")
    public ResultInfo getList(SysOrgDept bean){


        try {
            return ResultInfo.success(sysOrgDeptService.getList(bean));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取列表失败");
        }
    }


}
