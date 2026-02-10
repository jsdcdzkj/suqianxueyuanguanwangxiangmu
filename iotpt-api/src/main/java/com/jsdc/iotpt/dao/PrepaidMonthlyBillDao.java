package com.jsdc.iotpt.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class PrepaidMonthlyBillDao {

    public String selectMonth(Integer orgId, DateTime beginDate, DateTime endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT MONTH FROM PREPAID_MONTHLY_BILL WHERE ");
        sql.append(" SYSORGID = ").append(orgId);
        sql.append(" AND STATE = 1");
        sql.append(" AND EXPENSETYPE = '1'");
        if (Objects.nonNull(beginDate)) {
            sql.append(" AND month >= '").append(DateUtil.format(beginDate, "yyyy-MM")).append("'");
        }
        if (Objects.nonNull(endDate)) {
            sql.append(" AND month <= '").append(DateUtil.format(endDate, "yyyy-MM")).append("'");
        }
        return sql.toString();
    }

    public String selectPayment(Integer orgId, String month) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM PREPAID_MONTHLY_BILL WHERE ");
        sql.append(" SYSORGID = ").append(orgId);
        sql.append(" AND STATE = 1");
        sql.append(" AND EXPENSETYPE = '1'");
        if (StringUtils.isNotBlank(month)) {
            sql.append(" AND month = '").append(month).append("'");
        }
        return sql.toString();
    }

}
