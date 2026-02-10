package com.jsdc.iotpt.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.jsdc.iotpt.model.Knowledge;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.KnowledgeService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.KnowledgeVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @author wy
 */
@RestController
@RequestMapping("/app/knowledge")
@Api(tags = "知识库管理")
public class KnowledgeController {
    @Autowired
    private KnowledgeService KnowledgeService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 知识库分页查询
     *
     * @param pageIndex 当前页码
     * @param pageSize 每页显示条数
     * @param bean 查询条件
     * @return 查询结果Page对象
     */
    @PostMapping("/getPage")
    @ApiOperation(value = "知识库列表")
    public ResultInfo getPage(Integer pageIndex,Integer pageSize,Knowledge bean) {
        return ResultInfo.success(KnowledgeService.getPage(pageIndex,pageSize,bean));
    }

    /**
     * 知识库详情
     */
    @PostMapping("/getDetails")
    @ApiOperation(value = "知识库详情",notes = "传递参数：id：知识库id(必填)")
    public ResultInfo getDetails(Integer id) {
        return ResultInfo.success(KnowledgeService.getDetail(id));
    }

    /**
     * 知识库保存/编辑/删除
     * 传递参数（通过id判断是否编辑，传入isDel=1删除）
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "知识库保存/编辑/删除")
    public ResultInfo saveOrUpdate(@RequestBody KnowledgeVo vo) {
        Knowledge Knowledge = new Knowledge();
        BeanUtils.copyProperties(vo,Knowledge);
        SysUser sysUser = sysUserService.getUser();
        Date currentTime = new Date();

        if (Knowledge.getId() == null) {
            Knowledge.setIsDel(0);
            Knowledge.setCreateUser(sysUser.getId());
            Knowledge.setCreateUserName(sysUser.getLoginName());
            Knowledge.setCreateTime(currentTime);
        } else {
            Knowledge.setUpdateUser(sysUser.getId());
            Knowledge.setUpdateUserName(sysUser.getLoginName());
            Knowledge.setUpdateTime(currentTime);
        }

        return ResultInfo.success(KnowledgeService.saveOrUpdate(Knowledge));
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response, Knowledge query) throws Exception {
        List<Knowledge> list = KnowledgeService.getList(query);

        BigExcelWriter writer = ExcelUtil.getBigWriter();
        writer.addHeaderAlias("name", "文档名称");
        writer.addHeaderAlias("typeName", "知识类型");
        writer.addHeaderAlias("count", "内容描述");

        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }
}
