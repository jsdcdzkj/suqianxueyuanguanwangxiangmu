package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.PersonAnswer;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.PersonAnswerVo;
import org.springframework.stereotype.Repository;

@Repository
public class PersonAnswerDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, PersonAnswer bean) {
        String sql = " SELECT *  "+
                " FROM PERSON_ANSWER  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }

    public String getPersonQuestionCount(PersonAnswerVo vo){
        String sql = " SELECT COUNT(*) FROM PERSON_ANSWER WHERE  1=1 and isdel=0 " ;
        if(StringUtils.isNotNull(vo.getSubjectId())){
            sql+=" and SUBJECTID="+vo.getSubjectId();
        }
        if(StringUtils.isNotNull(vo.getQuestionId())){
            sql+=" and QUESTIONID="+vo.getQuestionId();
        }
        if(StringUtils.isNotEmpty(vo.getDeptId())){
            sql+=" and deptId="+vo.getDeptId();
        }
        sql+= " GROUP BY " +
                "USERID ";
        return sql;
    }
}
