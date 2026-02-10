package com.jsdc.iotpt.reportmgr;

import com.jsdc.iotpt.model.ReportTemplate;
import com.jsdc.iotpt.model.ReportType;
import com.jsdc.iotpt.service.ReportTemplateService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/report")
@Api(tags = "报告模版接口")
public class ReportTemplateController {

    @Autowired
    private ReportTemplateService templateService;

    @PostMapping("selectReportTypeTree")
    @ApiOperation(value = "报告类型树形菜单查询", notes = "名称：typeName(选填)、模版类型：modelType(选填)")
    public ResultInfo selectReportTypeTree(@RequestBody ReportType bean) {
        try {
            return ResultInfo.success(templateService.selectReportTypeTree(bean));
        } catch (Exception e) {
            throw new CustomException("报告类型树形菜单查询失败");
        }
    }


    @PostMapping("selectPageList")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({@ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"), @ApiImplicitParam(name = "pageSize",
            value = "每页条数", dataType = "int")})
    public ResultInfo selectPageList(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize,
                                     ReportTemplate bean) {
        try {
            return ResultInfo.success(templateService.selectPageList(pageIndex,pageSize,bean));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    @PostMapping("saveTemplate")
    @ApiOperation(value = "保存功能", notes = "模版名称：tempName(必填项)、模版类型：tempType(必填项)、所勾选的模块：reportTypeIds(必填项)")
    public ResultInfo saveTemplate(@RequestBody ReportTemplate bean) {
        try {
            templateService.saveTemplate(bean);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            throw new CustomException("保存失败");
        }
    }


    @PostMapping("updTemplate")
    @ApiOperation(value = "修改功能", notes = "id:(必填项)、版名称：tempName(必填项)、模版类型：tempType(必填项)、所勾选的模块：reportTypeIds(必填项)")
    public ResultInfo updTemplate(@RequestBody ReportTemplate bean) {
        try {
            templateService.saveTemplate(bean);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            throw new CustomException("修改失败");
        }
    }


    @PostMapping("deleteTemplate")
    @ApiOperation(value = "删除功能", notes = "id:必填项")
    public ResultInfo deleteTemplate(Integer id) {
        ReportTemplate bean = templateService.getById(id);
        if (null != bean) {
            bean.setIsDel(1);
            templateService.updateById(bean);
            return ResultInfo.success("操作成功");
        }
        throw new CustomException("删除失败");
    }
}
