package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigProtocol;
import org.springframework.stereotype.Repository;

/**
 * @authon thr
 * @describe 协议管理
 */
@Repository
public class ConfigProtocolDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigProtocol bean) {
        String sql = " SELECT *  " +
                " FROM config_protocol  " +
                " WHERE " +
                " 1=1 ";

        return sql;
    }
}
