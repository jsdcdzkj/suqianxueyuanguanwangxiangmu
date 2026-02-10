package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.new_alarm.FalseAlarmRecords;
import org.springframework.stereotype.Repository;

@Repository
public class FalseAlarmRecordsDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, FalseAlarmRecords bean) {
        String sql = " SELECT *  "+
                " FROM FALSE_ALARM_RECORDS  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }
}
