package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WarningSignDetailsMapper;
import com.jsdc.iotpt.model.WarningSignDetails;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningSignDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WarningSignDetailsService extends BaseService<WarningSignDetails> {

    @Autowired
    private WarningSignDetailsMapper warningSignDetailsMapper;

    /**
     * 分页查询
     *
     * @return
     */
    public Page<WarningSignDetails> getPageList(WarningSignDetailsVo vo) {
        QueryWrapper<WarningSignDetails> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return warningSignDetailsMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询
     *
     * @return
     */
    public List<WarningSignDetails> getList(WarningSignDetails entity) {
        QueryWrapper<WarningSignDetails> queryWrapper = new QueryWrapper<>();
        return warningSignDetailsMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateWarningSignDetails(WarningSignDetails bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    public ResultInfo saveOrUpdateBatchEntity(List<WarningSignDetails> beans) {
        saveOrUpdateBatch(beans);
        return ResultInfo.success();
    }

    /**
     * 根据id获取类
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        return ResultInfo.success(getById(id));
    }

    /**
     * 根据<code>conifgId</code>获取告警详情
     *
     * @param conifgId
     * @return
     */
    public List<WarningSignDetails> getDelSignDetailsIds(Integer conifgId) {
        LambdaQueryWrapper<WarningSignDetails> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(WarningSignDetails::getConfigId, conifgId);
        return warningSignDetailsMapper.selectList(queryWrapper);
    }
}


