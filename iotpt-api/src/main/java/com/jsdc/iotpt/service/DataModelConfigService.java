package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.DataModelConfigMapper;
import com.jsdc.iotpt.model.DataModelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 大数据模型
 * 参数设置
 */
@Service
@Transactional
public class DataModelConfigService extends BaseService<DataModelConfig> {

    @Autowired
    private DataModelConfigMapper dataModelConfigMapper;

    /**
     * 详情
     */
    public DataModelConfig view(DataModelConfig bean) {
        LambdaQueryWrapper<DataModelConfig> wrapper = new LambdaQueryWrapper();
        wrapper.orderByDesc(DataModelConfig::getId);
        List<DataModelConfig> list = dataModelConfigMapper.selectList(wrapper);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            DataModelConfig dataModelConfig = new DataModelConfig();
            dataModelConfig.setWorkingHours("0");
            dataModelConfig.setMeeting_reservation_duration("0");
            dataModelConfig.setRoom_reserve_count(0);
            return dataModelConfig;
        }
    }

    /**
     * 新增、修改
     */
    public void onSaveOrUpd(DataModelConfig bean) {
        saveOrUpdate(bean);
    }

}
