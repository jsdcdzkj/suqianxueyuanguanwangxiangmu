package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ConfigSignalFieldMapper;
import com.jsdc.iotpt.mapper.ConfigSignalTypeItemMapper;
import com.jsdc.iotpt.model.ConfigSignalField;
import com.jsdc.iotpt.model.ConfigSignalTypeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: ConfigSignalTypeItemService
 * Description:
 * date: 2023/12/19 10:26
 *
 * @author bn
 */
@Service
@Transactional
public class ConfigSignalTypeItemService extends BaseService<ConfigSignalTypeItem> {

    @Autowired
    private ConfigSignalTypeItemMapper mapper;
}
