package com.jsdc.iotpt.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class MerchantPrepaidRecordDao {

    public String selectMonth(Integer orgId, DateTime beginTime, DateTime endTime) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT createTime FROM merchant_prepaid_record WHERE ");
        sql.append(" sysOrgId = ").append(orgId);
        sql.append(" AND isSuccess = 1");
        if (Objects.nonNull(beginTime)) {
            sql.append(" AND createTime >= TO_DATE('").append(DateUtil.formatDateTime(beginTime)).append("', 'YYYY-MM-DD HH24:MI:SS') ");
        }
        if (Objects.nonNull(endTime)) {
            sql.append(" AND createTime <= TO_DATE('").append(DateUtil.formatDateTime(endTime)).append("', 'YYYY-MM-DD HH24:MI:SS') ");
        }
        return sql.toString();
    }


    public String selectPayment(Integer orgId, DateTime start, DateTime end) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM merchant_prepaid_record WHERE ");
        sql.append(" sysOrgId = ").append(orgId);
        sql.append(" AND isSuccess = 1");
        if (Objects.nonNull(start)) {
            sql.append(" AND createTime >= TO_DATE('").append(DateUtil.formatDateTime(start)).append("', 'YYYY-MM-DD HH24:MI:SS') ");
        }
        if (Objects.nonNull(end)) {
            sql.append(" AND createTime <= TO_DATE('").append(DateUtil.formatDateTime(end)).append("', 'YYYY-MM-DD HH24:MI:SS') ");
        }
        return sql.toString();
    }

}
