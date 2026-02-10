package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WarningInfoDetailsMapper;
import com.jsdc.iotpt.model.WarningInfoDetails;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningInfoDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class WarningInfoDetailsService extends BaseService<WarningInfoDetails> {

    @Autowired
    private WarningInfoDetailsMapper warningInfoDetailsMapper;

    /**
     * 分页查询
     *
     * @return
     */
    public Page<WarningInfoDetails> getPageList(WarningInfoDetailsVo vo) {
        QueryWrapper<WarningInfoDetails> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return warningInfoDetailsMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询
     *
     * @return
     */
    public List<WarningInfoDetailsVo> getList(WarningInfoDetails bean) {

        List<WarningInfoDetailsVo> detailsVos=warningInfoDetailsMapper.getList(bean);

        HashMap<String, SysDict> warnTypes = RedisDictDataUtil.getDictByType("warnType");

        detailsVos.forEach(x->{
            if (warnTypes!=null) {
                x.setWarnLevelName(warnTypes.get(x.getWarnLevel()) == null ? "未知" : warnTypes.get(x.getWarnLevel()).getDictLabel());
            }
        });

        return detailsVos;
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateWarningInfoDetails(WarningInfoDetails bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }


    public ResultInfo saveOrUpdateBatchEntity(List<WarningInfoDetails> beans) {
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


