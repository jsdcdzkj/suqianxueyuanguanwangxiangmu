package com.jsdc.iotpt.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.SysDictMapper;
import com.jsdc.iotpt.model.ReportTempType;
import com.jsdc.iotpt.model.ReportTemplate;
import com.jsdc.iotpt.model.ReportType;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ReportTemplateService extends BaseService<ReportTemplate> {


    @Autowired
    private ReportTypeService typeService;
    @Autowired
    private ReportTempTypeService tempTypeService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysDictMapper dictMapper;


    //报告类型树形菜单查询
    public List<Tree<String>> selectReportTypeTree(ReportType type) {
        LambdaQueryWrapper<ReportType> queryWrapper = new LambdaQueryWrapper<ReportType>().eq(ReportType::getIsDel, 0);
        if (null != type) {
            if (null != type.getModelType()) {
                queryWrapper.eq(ReportType::getModelType, type.getModelType());
            }
            if (null != type.getTypeName()) {
                queryWrapper.like(ReportType::getTypeName, type.getTypeName());
            }
        }
        List<ReportType> list = typeService.list(queryWrapper);

        // 使用Stream API查找第一个名称等于"test"的ReportType
        Optional<ReportType> result = list.stream()
                .filter(report -> "告警类型统计".equals(report.getTypeName()))
                .findFirst();

        if (result.isPresent()){
            List<SysDict> sysDicts=dictMapper.
                    selectList(Wrappers.<SysDict>lambdaQuery().
                            eq(SysDict::getIsDel,0).
                            eq(SysDict::getDictType,"warnType"));

            sysDicts.forEach(x->{
                ReportType reportType=new ReportType();
                reportType.setAppendId("dict_"+x.getDictValue());
                reportType.setTypeName(x.getDictLabel());
                reportType.setAppendParentId("type_"+result.get().getId());
                reportType.setSort(x.getSort());
                list.add(reportType);
            });

        }
        TreeNodeConfig config = new TreeNodeConfig();
        config.setWeightKey("id");
        config.setWeightKey("label");
        config.setChildrenKey("children");
        config.setWeightKey("sort");
        List<Tree<String>> treeNodes =
                TreeUtil.build(list, "type_-1", config, (treeNode, tree) -> {
                    tree.setId(null!=treeNode.getAppendId()?treeNode.getAppendId():"type_"+treeNode.getId());
                    tree.setName(treeNode.getTypeName());
                    tree.setParentId(null!=treeNode.getAppendParentId()?treeNode.getAppendParentId():"type_"+treeNode.getParentId());
                    tree.putExtra("sort", treeNode.getSort());
                });
        return treeNodes;
    }

    //分页查询
    public Page<ReportTemplate> selectPageList(Integer pageIndex, Integer pageSize, ReportTemplate bean) {
        LambdaQueryWrapper<ReportTemplate> queryWrapper = new LambdaQueryWrapper<ReportTemplate>().eq(ReportTemplate::getIsDel, 0);
        if (null != bean) {
            if (null != bean.getTempName()) {
                queryWrapper.like(ReportTemplate::getTempName, bean.getTempName());
            }
            if (null != bean.getCreateUser()) {
                queryWrapper.like(ReportTemplate::getCreateUser, bean.getCreateUser());
            }
        }
        Page<ReportTemplate> p = baseMapper.selectPage(new Page<>(pageIndex, pageSize), queryWrapper);
        p.getRecords().forEach(a -> {
            if (StringUtils.isNotEmpty(a.getCreateUser())) {
                SysUser user = userService.getById(a.getCreateUser());
                if (null != user) {
                    a.setCreateUserName(user.getRealName());
                }
                if (1 == a.getTempType()) {//单
                    a.setTempTypeName("单区域分析报告");
                } else {//双
                    a.setTempTypeName("多区域分析报告");
                }
            }
            List<ReportTempType> list = tempTypeService.list(new LambdaQueryWrapper<ReportTempType>().eq(ReportTempType::getTempId, a.getId()));
            List<String> typeIds = list.stream().map(ReportTempType::getReportTypeId).collect(Collectors.toList());
            a.setReportTypeIds(typeIds);
        });
        return p;
    }


    //保存功能
    public void saveTemplate(@RequestBody ReportTemplate bean) {
        SysUser user = userService.getUser();
        if (null != bean.getId()) {
            bean.setUpdateUser(user.getId().toString());
            bean.setUpdateTime(new Date());
            updateById(bean);
            if (null != bean.getReportTypeIds()) {
                //先进行删除关联
                tempTypeService.remove(new LambdaQueryWrapper<ReportTempType>().eq(ReportTempType::getTempId, bean.getId()));
                //保存关联
                tempTypeService.saveTempType(bean.getReportTypeIds(), bean.getId());
            }
        } else {
            bean.setIsDel(0);
            bean.setCreateTime(new Date());
            bean.setCreateUser(user.getId().toString());
            save(bean);
            if (null != bean.getReportTypeIds()) {
                tempTypeService.saveTempType(bean.getReportTypeIds(), bean.getId());
            }
        }
    }

    //根据id获取模版类型
    public ReportTemplate selectByTemplate(Integer id) {
        ReportTemplate bean = getById(id);
        Integer tempType = bean.getTempType();
        if (1 == tempType) {//单
            bean.setTempTypeName("单区域分析报告");
        } else {//双
            bean.setTempTypeName("多区域分析报告");
        }
        return bean;
    }
}
