package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.operate.CheckTemplateSub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class CheckTemplateSubService extends BaseService<CheckTemplateSub> {
}
