package com.jsdc.iotpt.smartsecurity.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author lb
 * @Date 2023/8/2 15:27
 * @Description 类描述
 **/
@RestController
@RequestMapping("sysDict")
@Api(tags = "数据字典")
public class SysDictController {
    @Autowired
    private SysDictService dictService;

    @PostMapping("/getRedisDictList")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisDictList(SysDict bean) {
        Map<String, List<SysDict>> dictLists = (Map<String, List<SysDict>>) RedisUtils.getBeanValue(MemoryCacheService.allDictListPrefix);
        List<SysDict> sdList = dictLists.get(bean.getDictType());
        return ResultInfo.success(sdList);
    }

    @PostMapping("/update")
    @ApiOperation("编辑字典")
    public ResultInfo updateDict(SysDict bean) {
        dictService.saveDict(bean);
        return ResultInfo.success();
    }

    @PostMapping("/delete")
    @ApiOperation("编辑字典")
    public ResultInfo deleteDict(SysDict bean) {
        dictService.delete(bean);
        return ResultInfo.success();
    }

    /**
     * 得到redis数据字典返回map
     */
    @PostMapping("/getRedisDictMap")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisDictMap() {
        try {
            return ResultInfo.success(RedisUtils.getBeanValue("dictData"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取数据字典列表失败");
        }
    }


    @PostMapping("/selectMissionDictAll")
    @ApiOperation("获取任务类型")
    public ResultInfo selectMissionDictAll() {
        try {
            List<SysDict> list = dictService.list(new LambdaQueryWrapper<SysDict>()
                    .in(SysDict::getDictType, "planType", "taskTypes").eq(SysDict::getIsDel, 0));
            list.forEach(a -> {
                a.setParentName(a.getDictType() + "_" + a.getDictValue());
            });
            return ResultInfo.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取任务类型失败");
        }
    }


}
