package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.vo.WalkRetentionRulesVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

@Repository
public class WalkRetentionRulesDao {

    public String findAll(Page page, WalkRetentionRulesVo retentionRules){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT ");
        sql.append("r.*, t.timesName ");
        sql.append("FROM ");
        sql.append("walk_retention_rules r, ");
        sql.append("times t ");
        sql.append("WHERE ");
        sql.append("r.timesId = t.id ");
        if (retentionRules.getDeptId()!=null){
            sql.append("AND r.deptId= "+retentionRules.getDeptId());
        }
        if (retentionRules.getIsFlag()!=null){
            sql.append(" AND r.isFlag= "+retentionRules.getIsFlag());
        }
        if (Strings.isNotEmpty(retentionRules.getRetentionName())){
            sql.append(" and r.retentionName like '%"+retentionRules.getRetentionName()+"%'");
        }
        sql.append(" and r.isDelete=0 ");


        return sql.toString();
    }

}
