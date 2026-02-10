package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigTemplateField;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigTemplateFieldDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigTemplateField bean) {
        String sql = " SELECT *  "+
                " FROM config_template_field  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }
}
