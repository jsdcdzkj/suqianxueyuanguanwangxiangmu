package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.Knowledge;
import com.jsdc.iotpt.model.sys.SysDict;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class KnowledgeService extends BaseService<Knowledge> {
    @Autowired
    private SysDictService sysDictService;

    /**
     * 知识库列表
     */
    public List<Knowledge> getList(Knowledge bean) {
        List<Knowledge> knowledgeList = baseMapper.selectList(
                Wrappers.<Knowledge>lambdaQuery()
                        .eq(null != bean.getName(), Knowledge::getName, bean.getName())
                        .eq(null != bean.getType(), Knowledge::getType, bean.getType())
                        .eq(Knowledge::getIsDel, 0)
                        .orderByDesc(Knowledge::getCreateTime)
        );
        SysDict sysDict = new SysDict();
        sysDict.setDictType("knowledgeType");
        List<SysDict> list = sysDictService.getList(sysDict);
        knowledgeList.forEach(i -> i.setTypeName(list.stream().filter(l -> l.getDictValue().equals(i.getType())).findFirst().get().getDictLabel()));
        return knowledgeList;
    }

    /**
     * 知识库详情
     */
    public Knowledge getDetail(Integer id) {
        Knowledge knowledge = baseMapper.selectById(id);
        return knowledge;
    }

    /**
     * 知识库分页查询
     */
    public Page<Knowledge> getPage(Integer pageIndex, Integer pageSize, Knowledge bean) {
        Page<Knowledge> knowledgePage = baseMapper.selectPage(
                new Page<>(pageIndex, pageSize),
                Wrappers.<Knowledge>lambdaQuery()
                        .eq(Knowledge::getIsDel, 0)
                        .eq(StringUtils.isNotEmpty(bean.getType()), Knowledge::getType, bean.getType())
                        .like(StringUtils.isNotEmpty(bean.getName()), Knowledge::getName, bean.getName())
                        .orderByDesc(Knowledge::getCreateTime)
        );

        SysDict sysDict = new SysDict();
        sysDict.setDictType("knowledgeType");
        List<SysDict> list = sysDictService.getList(sysDict);

        for (Knowledge i : knowledgePage.getRecords()) {
            if (i.getType() != null) {
                Optional<SysDict> matchingDict = list.stream()
                        .filter(l -> l.getDictValue().equals(i.getType()))
                        .findFirst();
                if (matchingDict.isPresent()) {
                    SysDict dict = matchingDict.get();
                    if (dict != null) {
                        i.setTypeName(dict.getDictLabel());
                    }
                } else {
                    // 可能需要记录日志或者处理没有找到匹配字典的情况
                    i.setTypeName("");
                }
            }
        }
        return knowledgePage;
    }

}
