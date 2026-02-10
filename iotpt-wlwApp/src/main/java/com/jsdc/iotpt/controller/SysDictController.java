package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.MemoryCacheService;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 字典
 * Description:
 * date: 2023/11/28 16:32
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/sysDict")
@Api(tags = "数据字典")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    /**
     *  获取字典数据
     * @param sysDict
     * @return
     */
    @PostMapping("getList")
    @ApiOperation(value = "获取字典数据", httpMethod = "POST")
    public ResultInfo getList(SysDict sysDict){
        try {
            return ResultInfo.success(sysDictService.getList(sysDict));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取字典数据失败");
        }
    }


    @PostMapping("/getRedisDictList")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisDictList(@RequestBody SysDict bean) {
        try {
            Map<String, List<SysDict>> dictLists= (Map<String, List<SysDict>>) RedisUtils.getBeanValue(MemoryCacheService.allDictListPrefix);
            List<SysDict> sdList = dictLists.get(bean.getDictType());
            return ResultInfo.success(sdList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取数据字典列表失败");
        }
    }

}
