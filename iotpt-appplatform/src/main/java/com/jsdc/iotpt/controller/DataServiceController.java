package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.DataService;
import com.jsdc.iotpt.service.DataServiceService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: DataServiceController
 * Description: 数据服务
 * date: 2023/5/8 16:48
 *
 * @author bn
 */
@RestController
@RequestMapping("dataService")
@Api(tags = "数据服务管理")
public class DataServiceController {

    @Autowired
    private DataServiceService dataServiceService;


    @PostMapping("getList.do")
    @ApiOperation("获取数据服务集合")
    public ResultInfo getList(DataService dataService){

        return ResultInfo.success(dataServiceService.getList(dataService));
    }


    @GetMapping("getById.do")
    @ApiOperation("根据id获取数据服务")
    @ApiImplicitParam(name = "id",value = "数据服务主键",dataType = "Integer")
    public ResultInfo getById(Integer id){

        return ResultInfo.success(dataServiceService.getById(id));
    }

    @PostMapping("edit.do")
    @ApiOperation("编辑数据服务")
    public ResultInfo edit(DataService dataService){

        dataServiceService.edit(dataService);
        return ResultInfo.success();
    }

    @GetMapping("del.do")
    @ApiOperation("删除数据服务")
    @ApiImplicitParam(name = "id",value = "数据服务主键",dataType = "Integer")
    public ResultInfo del(Integer id){
        dataServiceService.del(id);
        return ResultInfo.success();
    }


}
