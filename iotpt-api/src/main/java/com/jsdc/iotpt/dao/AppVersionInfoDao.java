package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.AppVersionInfo;
import org.springframework.stereotype.Repository;

@Repository
public class AppVersionInfoDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, AppVersionInfo bean) {
        String sql = " SELECT *  "+
                " FROM APP_VERSION_INFO  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }

    public String getLastOne() {

        String sql = " SELECT\n" +
                "\t* \n" +
                "FROM\n" +
                "\t( SELECT * FROM APP_VERSION_INFO ORDER BY id DESC ) \n" +
                "WHERE\n" +
                "\tROWNUM <= 1";

        return sql;
    }
}
