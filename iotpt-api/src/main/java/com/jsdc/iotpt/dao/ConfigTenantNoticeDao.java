package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigTenantNotice;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigTenantNoticeDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigTenantNotice bean) {
        String sql = " SELECT *  "+
                " FROM CONFIG_TENANT_NOTICE  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }
}
