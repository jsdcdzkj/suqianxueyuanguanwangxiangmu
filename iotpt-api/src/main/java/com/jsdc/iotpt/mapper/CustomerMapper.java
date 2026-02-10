package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.CustomerDao;
import com.jsdc.iotpt.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    @SelectProvider(type = CustomerDao.class, method = "getEntityList")
    Page<Customer> getPageList(Page page, Customer bean);

    @SelectProvider(type = CustomerDao.class, method = "getHistoryList")
    List<Customer> getHistoryList(Customer bean);
}
