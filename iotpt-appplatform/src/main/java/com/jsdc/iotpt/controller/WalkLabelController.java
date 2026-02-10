package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.WalkLabel;
import com.jsdc.iotpt.service.WalkLabelService;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WalkLabelVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("walkLabel")
@Api(tags = "标签管理")
public class WalkLabelController {

    @Autowired
    private WalkLabelService labelService;


    @RequestMapping("/pageList.do")
    @ApiOperation("分页查询")
    public ResultInfo selectPageList(WalkLabelVo bean) {
        return ResultInfo.success(labelService.getPageList(bean));
    }


    @RequestMapping("/detailsById.do")
    @ApiOperation("根据id查询详情")
    public ResultInfo getLabelById(@RequestParam Integer labelId) {
        return ResultInfo.success(labelService.getById(labelId));
    }

    @RequestMapping("/saveOrUpd.do")
    @ApiOperation("新增或修改")
    public ResultInfo updOrDelete(WalkLabel bean) {
        if (null != bean.getId()) {
            return ResultInfo.success(labelService.updateById(bean));
        } else {
            return ResultInfo.success(labelService.save(bean));
        }
    }

    @RequestMapping("/deleteById.do")
    @ApiOperation("删除")
    public ResultInfo addLabel(WalkLabel bean) {
        WalkLabel walkLabel = labelService.getById(bean.getId());
        walkLabel.setIsDelete(0);
        return ResultInfo.success(labelService.updateById(walkLabel));
    }
}
