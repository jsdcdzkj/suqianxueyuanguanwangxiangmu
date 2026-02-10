package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.PersonAnswerMapper;
import com.jsdc.iotpt.model.PersonAnswer;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.PersonAnswerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class PersonAnswerService extends BaseService<PersonAnswer> {

    @Autowired
    private PersonAnswerMapper personAnswerMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<PersonAnswer> getPageList(PersonAnswerVo vo) {
        QueryWrapper<PersonAnswer> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return personAnswerMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<PersonAnswer> getList(PersonAnswer entity) {
        QueryWrapper<PersonAnswer> queryWrapper = new QueryWrapper<>();
        return personAnswerMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdatePersonAnswer(PersonAnswer bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        return ResultInfo.success(getById(id));
    }

    public PersonAnswer getByPersonQuestion(Integer userId, Integer subjectId, Integer answerId) {
        return personAnswerMapper.selectOne(new LambdaQueryWrapper<PersonAnswer>()
                .eq(PersonAnswer::getIsDel, 0)
                .eq(PersonAnswer::getUserId, userId)
                .eq(PersonAnswer::getSubjectId, subjectId)
                .eq(Objects.nonNull(answerId), PersonAnswer::getAnswerId, answerId)
        );
    }
    public List<Integer> getPersonQuestionCount(PersonAnswerVo vo) {
        return personAnswerMapper.getPersonQuestionCount(vo);
    }
}


