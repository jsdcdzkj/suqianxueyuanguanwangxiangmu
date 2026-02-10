package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.InvestmentPersonDao;
import com.jsdc.iotpt.model.InvestmentPerson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface InvestmentPersonMapper extends BaseMapper<InvestmentPerson> {

    @SelectProvider(type = InvestmentPersonDao.class, method = "investmentPersonList")
    Page<InvestmentPerson> investmentPersonList(Page<InvestmentPerson> page,InvestmentPerson investmentPerson);

    @SelectProvider(type = InvestmentPersonDao.class, method = "investmentPersonAll")
    List<InvestmentPerson> investmentPersonAll( );
}
