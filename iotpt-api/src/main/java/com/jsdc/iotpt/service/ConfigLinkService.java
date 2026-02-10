package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.ConfigLinkMapper;
import com.jsdc.iotpt.model.ConfigLink;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigLinkVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @authon thr
 * @describe 连接管理
 */
@Service
@Transactional
public class ConfigLinkService extends BaseService<ConfigLink> {

    @Autowired
    private ConfigLinkMapper configLinkMapper;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigLinkVo> getPageList(ConfigLinkVo vo) {
//        QueryWrapper<ConfigLink> queryWrapper = new QueryWrapper<>();
//
//        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
//        return configLinkMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

        Page<ConfigLinkVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<ConfigLinkVo> pageList = configLinkMapper.getEntityList(page, vo);
        return pageList;
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigLink> getList(ConfigLink entity) {
        QueryWrapper<ConfigLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", "0");
        if (Strings.isNotEmpty(entity.getProtocolId())) {
            queryWrapper.eq("protocolId", entity.getProtocolId());
        }
        if (null != entity.getConnectionStatus()) {
            queryWrapper.eq("connectionStatus", entity.getConnectionStatus());
        }
        queryWrapper.orderByDesc("id");
        return configLinkMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigLink(ConfigLink bean) {
        String msg = "";
        SysUser sysUser = sysUserService.getUser();
        //连接状态 1断开 2连接
        if (StringUtils.isNull(bean.getConnectionStatus())) {
            bean.setConnectionStatus(1);
        }
        if (StringUtils.isNotNull(bean.getId())) {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUser.getId());
            msg = "修改";
            if (StringUtils.isNotNull(bean.getIsDel()) && bean.getIsDel() == 1) {
                msg = "删除";
            }
        } else {
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUser.getId());
            bean.setIsDel(0);
            msg = "新增";
        }
        saveOrUpdate(bean);
        return ResultInfo.success("操作成功", new LogVo(msg));
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        ConfigLink bean = getById(id);

        ConfigLinkVo vo = new ConfigLinkVo();
        BeanUtils.copyProperties(bean, vo);

        //传输协议
        HashMap<String, SysDict> protocol = RedisDictDataUtil.getDictByType("config_protocol");
        vo.setProtocolName(protocol.get(vo.getProtocolId()).getDictLabel());

        return ResultInfo.success(vo);
    }

}


