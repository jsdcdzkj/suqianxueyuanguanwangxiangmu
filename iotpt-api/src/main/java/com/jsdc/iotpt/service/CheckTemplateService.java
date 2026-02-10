package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.CheckTemplateMapper;
import com.jsdc.iotpt.mapper.CheckTemplateSubMapper;
import com.jsdc.iotpt.mapper.ConfigDeviceTypeMapper;
import com.jsdc.iotpt.mapper.SysDictMapper;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.CheckTemplate;
import com.jsdc.iotpt.model.operate.CheckTemplateSub;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.model.operate.TeamGroups;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CheckTemplateService  extends BaseService<CheckTemplate> {

    @Autowired
    private CheckTemplateMapper checkTemplateMapper;
    @Autowired
    private CheckTemplateSubMapper checkTemplateSubMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private ConfigDeviceTypeMapper configDeviceTypeMapper;

    public List<SysDict> getTypeDicts() {
        List<SysDict> list = new ArrayList<>();
        list = sysDictMapper.selectList(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictType,"template_type").eq(SysDict::getIsDel,G.ISDEL_NO));
        return list;
    }


    /**
     * 检查项模板分页查询
     * @param bean
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<CheckTemplate> getCheckTemPage(CheckTemplate bean, Integer pageIndex, Integer pageSize){
        Page<CheckTemplate> page = new Page(pageIndex, pageSize);
        Page<CheckTemplate> p = baseMapper.selectPage(page, Wrappers.<CheckTemplate>lambdaQuery()
                .like(StringUtils.isNotBlank(bean.getCheckName()),CheckTemplate::getCheckName,bean.getCheckName())
                .eq(StringUtils.isNotBlank(bean.getType()),CheckTemplate::getType,bean.getType())
                .eq(null != bean.getStatus(),CheckTemplate::getStatus,bean.getStatus())
                .eq(CheckTemplate::getIsDel, G.ISDEL_NO)
                .orderByDesc(CheckTemplate::getStatus,CheckTemplate::getCreateTime));

        List<SysDict> roleList = getTypeDicts();
        Map<String,String>  typeMap = roleList.stream().collect(Collectors.toMap(SysDict::getDictValue,SysDict::getDictLabel, (key1, key2) -> key2));

        p.getRecords().forEach(x -> {
            String type = x.getType();
            if (typeMap.containsKey(type)){
                x.setTypeName(typeMap.get(type));
            }
        });
        return p;
    }

    /**
     * 新增编辑模板
     * @param bean
     * @return
     */
    public ResultInfo editCheckTem(CheckTemplate bean){
        if (null == bean.getId()){
           //新增
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
            bean.setIsDel(G.ISDEL_NO);
            checkTemplateMapper.insert(bean);
        }else {
            //修改
            bean.setUpdateUser(sysUserService.getUser().getId());
            bean.setUpdateTime(new Date());
            checkTemplateMapper.updateById(bean);
            //删除关联子表的数据
            UpdateWrapper<CheckTemplateSub> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("isDel",G.ISDEL_YES).eq("checkId",bean.getId()).eq("isDel",G.ISDEL_NO);
            checkTemplateSubMapper.update(null,updateWrapper);
        }
        if (CollectionUtils.isNotEmpty(bean.getList())){
            for (int i = 0 ; i < bean.getList().size() ; i++){
                CheckTemplateSub checkTemplateSub = new CheckTemplateSub();
                checkTemplateSub.setSubName(bean.getList().get(i).getSubName());
                checkTemplateSub.setDeviceType(bean.getList().get(i).getDeviceType());
                checkTemplateSub.setSubContent(bean.getList().get(i).getSubContent());
                checkTemplateSub.setCheckId(bean.getId());
                checkTemplateSub.setCreateTime(new Date());
                checkTemplateSub.setCreateUser(sysUserService.getUser().getId());
                checkTemplateSub.setIsDel(G.ISDEL_NO);
                checkTemplateSubMapper.insert(checkTemplateSub);
            }
        }else {
            return ResultInfo.error("请添加检查项");
        }
        return ResultInfo.success();
    }

    /**
     * 设置启用禁用
     * @return
     */
    public ResultInfo setIsAble(CheckTemplate bean){
        CheckTemplate checkTemplate = checkTemplateMapper.selectById(bean.getId());
        checkTemplate.setStatus(bean.getStatus());
        checkTemplateMapper.updateById(checkTemplate);
        return ResultInfo.success();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    public ResultInfo delCheckTem(Integer id){
        CheckTemplate checkTemplate = checkTemplateMapper.selectById(id);
        checkTemplate.setIsDel(G.ISDEL_YES);
        checkTemplateMapper.updateById(checkTemplate);
        UpdateWrapper<CheckTemplateSub> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("isDel",G.ISDEL_YES).eq("checkId",id).eq("isDel",G.ISDEL_NO);
        checkTemplateSubMapper.update(null,updateWrapper);
        return ResultInfo.success();
    }

    /**
     * 详情
     * @param id
     * @return
     */
    public CheckTemplate getOneDetail(Integer id){
        CheckTemplate checkTemplate = checkTemplateMapper.selectById(id);
        //todo 所属类型

        if (null != checkTemplate){
            List<CheckTemplateSub> list = checkTemplateSubMapper.selectList(Wrappers.<CheckTemplateSub>lambdaQuery().eq(CheckTemplateSub::getIsDel,G.ISDEL_NO).eq(CheckTemplateSub::getCheckId,checkTemplate.getId()));
            checkTemplate.setList(list);
        }
        return checkTemplate;
    }

    /**
     * 复制模板
     * @param id
     * @return
     */
    public ResultInfo copyTemplate(Integer id){
        CheckTemplate checkTemplate = checkTemplateMapper.selectById(id);
        checkTemplate.setId(null);

        CheckTemplate bean = new CheckTemplate();
        BeanUtils.copyProperties(checkTemplate,bean);
        bean.setCheckName(bean.getCheckName()+"_副本");
        bean.setCreateTime(new Date());
        bean.setCreateUser(sysUserService.getUser().getId());
        checkTemplateMapper.insert(bean);

        List<CheckTemplateSub> list = checkTemplateSubMapper.selectList(Wrappers.<CheckTemplateSub>lambdaQuery().eq(CheckTemplateSub::getIsDel,G.ISDEL_NO).eq(CheckTemplateSub::getCheckId,id));
        if (CollectionUtils.isNotEmpty(list)){
            for (CheckTemplateSub temp : list){
                CheckTemplateSub checkTemplateSub = new CheckTemplateSub();
                temp.setId(null);
                BeanUtils.copyProperties(temp,checkTemplateSub);
                checkTemplateSub.setCheckId(bean.getId());
                checkTemplateSubMapper.insert(checkTemplateSub);
            }
        }

        return ResultInfo.success();
    }

    /**
     * 查询所有模板
     * @return
     */
    public List<CheckTemplate> getAllList(){
        List<CheckTemplate> list = checkTemplateMapper.selectList(Wrappers.<CheckTemplate>lambdaQuery().eq(CheckTemplate::getIsDel,G.ISDEL_NO).eq(CheckTemplate::getStatus,1).orderByDesc(CheckTemplate::getCreateTime));
        return list;
    }


    /**
     * 获取所有设备类型
     * @return
     */
    public List<ConfigDeviceType>  getAllDeviceType(){
        List<ConfigDeviceType> list = configDeviceTypeMapper.selectList(Wrappers.<ConfigDeviceType>lambdaQuery().eq(ConfigDeviceType::getIsDel,G.ISDEL_NO).orderByDesc(ConfigDeviceType::getCreateTime));
        return list;
    }





}
