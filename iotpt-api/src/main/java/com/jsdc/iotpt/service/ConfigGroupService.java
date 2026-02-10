package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.ConfigGroupItemMapper;
import com.jsdc.iotpt.mapper.ConfigGroupMapper;
import com.jsdc.iotpt.mapper.SysOrgDeptMapper;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.model.ConfigGroup;
import com.jsdc.iotpt.model.ConfigGroupItem;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.model.operate.JobPlanArea;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.vo.ConfigGroupVo;
import com.jsdc.iotpt.vo.JobPlanVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ClassName: ConfigGroupService
 * Description:
 * date: 2024/2/8 14:08
 *
 * @author bn
 */
@Service
@Transactional
public class ConfigGroupService extends BaseService<ConfigGroup> {

    @Autowired
    private ConfigGroupMapper configGroupMapper;
    @Autowired
    private ConfigGroupItemMapper itemMapper;
    @Autowired
    private SysOrgDeptMapper deptMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserService sysUserService;

    public QueryWrapper getWrapper(ConfigGroupVo bean){
        QueryWrapper<ConfigGroup> queryWrapper=new QueryWrapper<>();

        if (Strings.isNotEmpty(bean.getGroupName())){
            queryWrapper.like("groupName",bean.getGroupName());
        }

        if (Strings.isNotEmpty(bean.getStartTime())){
            queryWrapper.apply("createTime >= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')",bean.getStartTime()+" 00:00:00");
        }
        if (Strings.isNotEmpty(bean.getEndTime())){
            queryWrapper.apply("createTime <= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')",bean.getEndTime()+" 23:59:59");
        }

        queryWrapper.eq("isDel",0);

        queryWrapper.orderByDesc("createTime");

        return queryWrapper;
    }


    public List<ConfigGroup> getList(ConfigGroupVo bean){

        List<ConfigGroup> groupList=configGroupMapper.selectList(getWrapper(bean));

        return groupList;
    }

    public Page getPageList(ConfigGroupVo bean, Integer pageIndex, Integer pageSize) {

        Page page = new Page(pageIndex, pageSize);

        Page<ConfigGroup> p = configGroupMapper.selectPage(page, getWrapper(bean));

        p.getRecords().forEach(x->{

            List<ConfigGroupItem> items = itemMapper.selectList(Wrappers.<ConfigGroupItem>lambdaQuery().
                    eq(ConfigGroupItem::getIsDel,0).
                    eq(ConfigGroupItem::getGroupId,x.getId()));

            items.forEach(y->{
                if (y.getDeptId()!=null){
                    SysOrgDept sysOrgDept=deptMapper.selectById(y.getDeptId());
                    y.setDept(sysOrgDept);
                }else {
                    SysUser sysUser=sysUserMapper.selectById(y.getUserId());
                    y.setSysUser(sysUser);
                }
            });

            x.setItemList(items);
        });

        return page;
    }


    public ConfigGroup getGroupById(Integer id) {
        ConfigGroup configGroup=configGroupMapper.selectById(id);

        List<ConfigGroupItem> items = itemMapper.selectList(Wrappers.<ConfigGroupItem>lambdaQuery().
                eq(ConfigGroupItem::getIsDel,0).
                eq(ConfigGroupItem::getGroupId,id));

        items.forEach(y->{
            if (y.getDeptId()!=null){
                SysOrgDept sysOrgDept=deptMapper.selectById(y.getDeptId());
                y.setDept(sysOrgDept);
            }
            if(y.getUserId()!=null) {
                SysUser sysUser=sysUserMapper.selectById(y.getUserId());
                y.setSysUser(sysUser);
            }
        });

        configGroup.setItemList(items);

        return configGroup;
    }

    public ResultInfo toAdd(ConfigGroupVo bean) {

        bean.setIsDel(0);
        bean.setCreateTime(new Date());
        bean.setUpdateTime(new Date());
        bean.setCreateUser(sysUserService.getUser().getId());
        bean.setUpdateUser(sysUserService.getUser().getId());
        configGroupMapper.insert(bean);

        if (bean.getItemList()!=null){
            bean.getItemList().forEach(x->{

                x.setIsDel(0);
                x.setGroupId(bean.getId());
                itemMapper.insert(x);
            });
        }


        return ResultInfo.success();

    }


    public ResultInfo toEdit(ConfigGroupVo bean) {

        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setUpdateTime(new Date());
        configGroupMapper.updateById(bean);


        itemMapper.update(null,Wrappers.<ConfigGroupItem>lambdaUpdate().
                eq(ConfigGroupItem::getIsDel,0).
                eq(ConfigGroupItem::getGroupId,bean.getId()).
                set(ConfigGroupItem::getIsDel,1));

        if (bean.getItemList()!=null){
            bean.getItemList().forEach(x->{
                x.setIsDel(0);
                x.setGroupId(bean.getId());
                itemMapper.insert(x);
            });
        }



        return ResultInfo.success("编辑成功！");
    }



}
