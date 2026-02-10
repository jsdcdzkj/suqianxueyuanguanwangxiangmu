package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.PersonAnswerDao;
import com.jsdc.iotpt.model.PersonAnswer;
import com.jsdc.iotpt.vo.PersonAnswerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PersonAnswerMapper extends BaseMapper<PersonAnswer> {

    @SelectProvider(type = PersonAnswerDao.class, method = "getEntityList")
    Page<PersonAnswer> getEntityList(Page page, PersonAnswer bean);

    @SelectProvider(type = PersonAnswerDao.class, method = "getPersonQuestionCount")
    List<Integer> getPersonQuestionCount(PersonAnswerVo vo);
}
