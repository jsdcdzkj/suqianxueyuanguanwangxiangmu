package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.DataServiceMapper;
import com.jsdc.iotpt.model.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: DataServiceService
 * Description:
 * date: 2023/5/8 16:50
 *
 * @author bn
 */
@Service
@Transactional
public class DataServiceService extends BaseService<DataService> {

    @Autowired
    private DataServiceMapper dataServiceMapper;

    public List<DataService> getList(DataService dataService) {
        List<DataService> dataServices=dataServiceMapper.selectList(Wrappers.<DataService>lambdaQuery());

        return dataServices;
    }

    public void edit(DataService dataService) {



        updateById(dataService);
    }

    public void del(Integer id) {

        removeById(id);
    }
}
