package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.PersonQuestionDao;
import com.jsdc.iotpt.model.PersonQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface PersonQuestionMapper extends BaseMapper<PersonQuestion> {

    @SelectProvider(type = PersonQuestionDao.class, method = "getEntityList")
    Page<PersonQuestion> getEntityList(Page page, PersonQuestion bean);
}
