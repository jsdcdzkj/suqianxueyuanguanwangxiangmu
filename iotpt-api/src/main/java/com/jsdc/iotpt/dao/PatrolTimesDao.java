package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.PatrolTimes;
import org.springframework.stereotype.Repository;

/**
 * ClassName: PatrolTimesDao
 * Description:
 * date: 2024/1/11 15:22
 *
 * @author bn
 */
@Repository
public class PatrolTimesDao {



    public String getList(PatrolTimes patrolTime){

        StringBuilder sql=new StringBuilder();

        sql.append(" select * from PATROL_TIMES pt ");
        sql.append(" ");
        sql.append(" ");
        sql.append(" where pt.isDel=0 and pt.planId ="+patrolTime.getPlanId());

        return sql.toString();
    }
}
