package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.DeviceAccessControl;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceAccessControlDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, DeviceAccessControl bean) {
        String sql = " SELECT *  "+
                " FROM device_access_control  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }
}
