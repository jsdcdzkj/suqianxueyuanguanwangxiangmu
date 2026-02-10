package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigSignalField;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigSignalFieldDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigSignalField bean) {
        String sql = " SELECT *  "+
                " FROM config_signal_field  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }
}
