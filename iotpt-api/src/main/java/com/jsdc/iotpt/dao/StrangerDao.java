package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.Stranger;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class StrangerDao {


    public String selectStrangerPage(Page page, Stranger bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("select * from stranger where isDel = 0 and is_nameList = 0 ");
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotEmpty(bean.getStartDate())) {
                builder.append("and  TO_CHAR(captureTime, 'yyyy-MM-dd' ) >= '" + bean.getStartDate() + "' ");
            }
            if (StringUtils.isNotEmpty(bean.getEndDate())) {
                builder.append("and  TO_CHAR(captureTime, 'yyyy-MM-dd' ) <= '" + bean.getEndDate() + "' ");
            }
            if (StringUtils.isNotEmpty(bean.getSnapPosition())) {
                builder.append("and snapPosition like '%" + bean.getSnapPosition() + "%'");
            }
            if (StringUtils.isNotEmpty(bean.getStates())) {
                builder.append("and states = '" + bean.getStates() + "'");
            }
        }
        builder.append(" order by captureTime  DESC ");
        return builder.toString();
    }

}
