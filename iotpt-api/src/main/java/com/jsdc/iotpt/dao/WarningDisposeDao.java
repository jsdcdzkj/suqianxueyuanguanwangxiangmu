package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.WarningDispose;
import com.jsdc.iotpt.vo.WarningDisposeVo;
import org.springframework.stereotype.Repository;

@Repository
public class WarningDisposeDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, WarningDispose bean) {
        String sql = " SELECT *  "+
                " FROM WARNING_DISPOSE  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }

    public String getAllList(WarningDisposeVo bean) {
        StringBuilder sql=new StringBuilder();
        sql.append(" SELECT *   FROM WARNING_DISPOSE  d ");
        sql.append(" LEFT Join SYS_USER u on d.disposeUserId = u.id  ");
        sql.append(" WHERE  1=1 ");
        if (null!=bean.getWarningInfoId()){
            sql.append(" AND d.warningInfoId= "+bean.getWarningInfoId());
        }
        return sql.toString();
    }
}
