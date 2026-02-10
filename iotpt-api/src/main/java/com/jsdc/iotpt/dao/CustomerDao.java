package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.Customer;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, Customer bean) {
        String sql = " SELECT c.*,u.realName investmentPersonName,u.id userId," +
                "r.name hireRoomName,u2.realName allocateUserName " +
                " FROM customer c " +
                " left join investment_person p on c.investmentPersonId = p.id " +
                " left join sys_user u on u.id = p.user_id " +
                " left join hire_room r on r.id = c.hireRoomId " +
                " left join sys_user u2 on u2.id = c.allocateUser " +
                " WHERE " +
                " c.isDel = 0 ";
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotEmpty(bean.getName())) {
                sql += " and c.name like '%" + bean.getName() + "%'";
            }
            if (StringUtils.isNotEmpty(bean.getPhone())) {
                sql += " and c.phone like '%" + bean.getPhone() + "%'";
            }
            if (StringUtils.isNotEmpty(bean.getCompanyName())) {
                sql += " and c.companyName like '%" + bean.getCompanyName() + "%'";
            }
            if (StringUtils.isNotEmpty(bean.getStatus())) {
                sql += " and c.status = '" + bean.getStatus() + "'";
            }
            if (StringUtils.isNotEmpty(bean.getStartTime())) {
                sql += " and TO_CHAR(c.createTime,'YYYY-MM-DD') >= '" + bean.getStartTime() + "'";
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {
                sql += " and TO_CHAR(c.createTime,'YYYY-MM-DD') <= '" + bean.getEndTime() + "'";
            }
            if (StringUtils.isNotEmpty(bean.getIsHighSeas())) {
                sql += " and c.isHighSeas = '" + bean.getIsHighSeas() + "'";
            }
            if (StringUtils.isNotNull(bean.getInvestmentPersonId())) {
                sql += " and c.investmentPersonId = " + bean.getInvestmentPersonId();
            }
            if (StringUtils.isNotEmpty(bean.getSource())) {
                sql += " and c.source = '" + bean.getSource() + "'";
            }
        }
        sql += " order by c.createTime desc";
        return sql;
    }


    public String getHistoryList(Customer bean) {
        String sql = " SELECT DISTINCT c.id " +
                " FROM customer c " +
                " left join customer_follow_history h on h.customerId = c.id " +
                " WHERE " +
                " c.isDel = 0 and h.isDel = 0 ";
        sql += " and c.status <> '5'";
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotEmpty(bean.getStartTime())) {
                sql += " and TO_CHAR(c.createTime,'YYYY-MM-DD') >= '" + bean.getStartTime() + "'";
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {
                sql += " and TO_CHAR(c.createTime,'YYYY-MM-DD') <= '" + bean.getEndTime() + "'";
            }
        }
        return sql;
    }
}
