package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.ConfigTopicMapper;
import com.jsdc.iotpt.model.ConfigTopic;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigTopicVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @authon thr
 * @describe 主题管理
 */
@Service
@Transactional
public class ConfigTopicService extends BaseService<ConfigTopic> {

    @Autowired
    private ConfigTopicMapper configTopicMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigTopicVo> getPageList(ConfigTopicVo vo) {
//        QueryWrapper<ConfigTopic> queryWrapper = new QueryWrapper<>();
//        if (StringUtils.isNotNull(vo)){
//            if (StringUtils.isNotEmpty(vo.getTopicName())){
//                queryWrapper.like("topicName", vo.getTopicName());
//            }
//            if (StringUtils.isNotNull(vo.getTopicType())){
//                queryWrapper.eq("topicType", vo.getTopicType());
//            }
//        }
//        queryWrapper.eq("topicStatus", 1);
//        queryWrapper.orderByDesc("id");
//        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
//        return configTopicMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

        Page<ConfigTopicVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<ConfigTopicVo> pageList = configTopicMapper.getEntityList(page, vo);
        return pageList;
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigTopic> getList(ConfigTopic entity) {
        QueryWrapper<ConfigTopic> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotNull(entity)) {
            if (StringUtils.isNotNull(entity.getTopicType())) {
                queryWrapper.eq("topicType", entity.getTopicType());
            }
            if (null != entity.getLinkId()) {
                queryWrapper.eq("linkId", entity.getLinkId());
            }
            if (null != entity.getTopicStatus()) {
                queryWrapper.eq("topicStatus", 1);
            }
        }

        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("id");
        return configTopicMapper.selectList(queryWrapper);
    }


    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigTopic(ConfigTopicVo bean) {
        String msg = "";
        //主题状态 0：禁用 1:启用
        if (StringUtils.isNull(bean.getTopicStatus())) {
            bean.setTopicStatus(0);
        }
        if (StringUtils.isNull(bean.getIsDel())) {
            bean.setIsDel(0);
        }

        //删除操作判断
        if (StringUtils.isNotNull(bean.getId())) {
            if (StringUtils.isNotNull(bean.getIsDel()) && bean.getIsDel() == 1) {
                ConfigTopic configTopic = configTopicMapper.selectById(bean.getId());
                //主题状态 0：禁用 1:启用
                if (configTopic.getTopicStatus() == 1) {
                    return ResultInfo.error("请先停用再删除");
                } else {
                    msg = "删除";
                }
            } else {
                msg = "修改";
            }
        } else {
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
        return ResultInfo.success(getById(id));
    }

    public List<ConfigTopic> cacheTopic(ConfigTopic configTopic) {

        return configTopicMapper.cacheTopic(configTopic);
    }

    /**
     * 批量停用
     */
    public ResultInfo runStops(ConfigTopicVo bean) {
        //主题状态 0：禁用 1:启用
        LambdaUpdateWrapper<ConfigTopic> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(ConfigTopic::getId, bean.getIdList());
        wrapper.set(ConfigTopic::getTopicStatus, 0);
        update(wrapper);
        return ResultInfo.success();
    }

    /**
     * 批量停用
     */
    public ResultInfo runStarts(ConfigTopicVo bean) {
        //主题状态 0：禁用 1:启用
        LambdaUpdateWrapper<ConfigTopic> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(ConfigTopic::getId, bean.getIdList());
        wrapper.set(ConfigTopic::getTopicStatus, 1);
        update(wrapper);
        return ResultInfo.success();
    }

}
