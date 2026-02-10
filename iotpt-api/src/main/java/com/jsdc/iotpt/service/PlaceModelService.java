package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.PlaceModelMapper;
import com.jsdc.iotpt.model.PlaceModel;
import com.jsdc.iotpt.model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: PlaceModelService
 * Description:
 * date: 2024/11/25 14:38
 *
 * @author bn
 */
@Service
@Transactional
public class PlaceModelService extends BaseService<PlaceModel> {

    @Autowired
    private PlaceModelMapper modelMapper;


}
