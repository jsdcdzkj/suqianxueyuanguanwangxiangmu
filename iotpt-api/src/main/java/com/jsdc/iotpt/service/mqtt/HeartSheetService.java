package com.jsdc.iotpt.service.mqtt;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.DataSheetMapper;
import com.jsdc.iotpt.mapper.HeartSheetMapper;
import com.jsdc.iotpt.model.DataSheet;
import com.jsdc.iotpt.model.HeartSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: AlertSheetService
 * Description: 告警数据
 * date: 2023/5/19 11:54
 *
 * @author bn
 */
@Service
@Transactional
public class HeartSheetService extends BaseService<HeartSheet> {

    @Autowired
    private HeartSheetMapper heartSheetMapper;






}
