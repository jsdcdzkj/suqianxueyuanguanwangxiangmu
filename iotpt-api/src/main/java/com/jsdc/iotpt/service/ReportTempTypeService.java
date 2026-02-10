package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.ReportTempType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ReportTempTypeService extends BaseService<ReportTempType> {

    public void saveTempType(List<String> list, Integer tempId) {
        if (null != list) {
            list.forEach(a -> {
                ReportTempType type = new ReportTempType();
                type.setReportTypeId(a);
                type.setTempId(tempId);
                save(type);
            });
        }
    }
}
