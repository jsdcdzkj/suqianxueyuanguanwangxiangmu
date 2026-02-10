package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.DaPingConfigMapper;
import com.jsdc.iotpt.model.DaPingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DaPingConfigService extends BaseService<DaPingConfig> {

    @Autowired
    private DaPingConfigMapper daPingConfigMapper;

    public DaPingConfig latest() {
        return daPingConfigMapper.selectOne(new LambdaQueryWrapper<DaPingConfig>()
                .eq(DaPingConfig::getIsDel, 0));
    }

}
