package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ReportBusinessMapper;
import com.jsdc.iotpt.model.ReportBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportBusinessService extends BaseService<ReportBusiness> {

    @Autowired
    private ReportBusinessMapper reportBusinessMapper;
}
