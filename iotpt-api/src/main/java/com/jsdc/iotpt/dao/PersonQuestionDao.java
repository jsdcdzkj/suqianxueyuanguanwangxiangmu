package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.PersonQuestion;
import org.springframework.stereotype.Repository;

@Repository
public class PersonQuestionDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, PersonQuestion bean) {
        String sql = " SELECT *  "+
                " FROM PERSON_QUESTION  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }
}
