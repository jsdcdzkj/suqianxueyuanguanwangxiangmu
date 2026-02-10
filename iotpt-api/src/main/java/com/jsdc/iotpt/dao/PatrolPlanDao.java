package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.PatrolPlan;
import org.springframework.stereotype.Repository;

/**
 * ClassName: PatrolPlanDao
 * Description:
 * date: 2024/1/9 16:04
 *
 * @author bn
 */
@Repository
public class PatrolPlanDao {

    public String getPatrolPlanById(PatrolPlan bean){

        StringBuilder sql=new StringBuilder();
        sql.append(" select * from PATROL_PLAN pp");
        sql.append(" ");
        sql.append(" ");
        sql.append(" ");
        sql.append(" where pp.isDel=0 and pp.id = "+bean.getId());

        return sql.toString();
    }




}
