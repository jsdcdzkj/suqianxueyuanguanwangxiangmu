package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WarningDeviceMapMapper;
import com.jsdc.iotpt.model.WarningDeviceMap;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningDeviceMapVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WarningDeviceMapService extends BaseService<WarningDeviceMap> {

    @Autowired
    private WarningDeviceMapMapper warningDeviceMapMapper;

    /**
     * 分页查询
     *
     * @return
     */
    public Page<WarningDeviceMap> getPageList(WarningDeviceMapVo vo) {
        QueryWrapper<WarningDeviceMap> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return warningDeviceMapMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<WarningDeviceMap> getList(WarningDeviceMap entity) {
        QueryWrapper<WarningDeviceMap> queryWrapper = new QueryWrapper<>();
        return warningDeviceMapMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateWarningDeviceMap(WarningDeviceMap bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    public ResultInfo saveOrUpdateBatchEntity(List<WarningDeviceMap> beans) {
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


    public List<WarningDeviceMap> getWarningDevices(Integer conifgId) {
        LambdaQueryWrapper<WarningDeviceMap> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(WarningDeviceMap::getWarningConfigId, conifgId);
        return warningDeviceMapMapper.selectList(queryWrapper);
    }

}


