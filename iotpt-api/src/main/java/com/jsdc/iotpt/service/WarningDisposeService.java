package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WarningDisposeMapper;
import com.jsdc.iotpt.model.WarningDispose;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningDisposeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WarningDisposeService extends BaseService<WarningDispose> {

    @Autowired
    private WarningDisposeMapper warningDisposeMapper;

    /**
     * 分页查询
     *
     * @return
     */
    public Page<WarningDispose> getPageList(WarningDisposeVo vo) {
        QueryWrapper<WarningDispose> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return warningDisposeMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询
     *
     * @return
     */
    public List<WarningDisposeVo> getList(WarningDisposeVo entity) {

        return warningDisposeMapper.getAllList(entity);
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateWarningDispose(WarningDispose bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    public ResultInfo saveOrUpdateBatchEntity(List<WarningDispose> beans) {
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

}


