package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.PatrolDeviceOrPoint;
import com.jsdc.iotpt.model.PatrolPeople;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: PatrolPeopleService
 * Description:
 * date: 2024/1/9 15:55
 *
 * @author bn
 */
@Service
@Transactional
public class PatrolPeopleService extends BaseService<PatrolPeople> {
}
