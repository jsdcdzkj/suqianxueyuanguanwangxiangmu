package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.PersonQuestionMapper;
import com.jsdc.iotpt.model.PersonQuestion;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.PersonQuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PersonQuestionService extends BaseService<PersonQuestion> {

    @Autowired
    private PersonQuestionMapper personQuestionMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<PersonQuestion> getPageList(PersonQuestionVo vo) {
        QueryWrapper<PersonQuestion> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return personQuestionMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<PersonQuestion> getList(PersonQuestion entity) {
        QueryWrapper<PersonQuestion> queryWrapper = new QueryWrapper<>();
        return personQuestionMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdatePersonQuestion(PersonQuestion bean) {
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

    public PersonQuestion getByQuestionnaire(Integer userId, Integer questionId) {
        return personQuestionMapper.selectOne(new LambdaQueryWrapper<PersonQuestion>()
                .eq(PersonQuestion::getIsDel, 0)
                .eq(PersonQuestion::getUserId, userId)
                .eq(PersonQuestion::getQuestionId, questionId)
        );
    }
}


