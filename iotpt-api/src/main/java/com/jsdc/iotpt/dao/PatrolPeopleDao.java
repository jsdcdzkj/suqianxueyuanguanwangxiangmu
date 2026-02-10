package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.PatrolPeople;
import org.springframework.stereotype.Repository;

/**
 * ClassName: PatrolPeopleDao
 * Description:
 * date: 2024/1/9 16:04
 *
 * @author bn
 */
@Repository
public class PatrolPeopleDao {


    public String getList(PatrolPeople patrolPeople){
        StringBuilder sql=new StringBuilder();
        sql.append(" select * from PATROL_PEOPLE pp ");

        sql.append(" where pp.isDel=0 and pp.planId ="+patrolPeople.getPlanId());

        return sql.toString();
    }
}
