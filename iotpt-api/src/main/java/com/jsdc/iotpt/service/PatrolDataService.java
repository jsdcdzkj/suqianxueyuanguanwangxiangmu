package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.PatrolDataMapper;
import com.jsdc.iotpt.model.PatrolData;
import com.jsdc.iotpt.vo.PatrolDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: PatrolDataService
 * Description:
 * date: 2024/11/25 15:28
 *
 * @author bn
 */
@Service
@Transactional
public class PatrolDataService  extends BaseService<PatrolData> {

    @Autowired
    private PatrolDataMapper patrolDataMapper;


}
