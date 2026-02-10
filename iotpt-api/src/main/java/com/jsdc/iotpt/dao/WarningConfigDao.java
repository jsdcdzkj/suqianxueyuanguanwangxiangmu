package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.WarningConfig;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.WarningConfigVo;
import org.springframework.stereotype.Repository;

@Repository
public class WarningConfigDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, WarningConfig bean) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * ");
        sql.append(" FROM WARNING_CONFIG  ");
        sql.append(" WHERE 1=1 and isDel = 0 ");
        if (!"".equals(StringUtils.trim(bean.getDeviceType()))) {
            sql.append(" and deviceType like '%").append(bean.getDeviceType()).append("%'");
        }
        if (!"".equals(StringUtils.trim(bean.getConfigName()))) {
            sql.append(" and CONFIGNAME like '%").append(bean.getConfigName()).append("%'");
        }
        return sql.toString();
    }

    public String getWarningType(WarningConfigVo bean) {
        String sql = "SELECT wc.CONFIGNAME,\n" +
                "       wc.CONFIGTYPE,\n" +
                "       wc.CREATEUSER,\n" +
                "       wc.DEVICETYPE,\n" +
                "       wc.FLOOR,\n" +
                "       wc.SUBSET,\n" +
                "       wc.WARNTYPE,\n" +
                "       COUNT(wi.id) WARNING_INFO_NUMBER\n" +
                "FROM WARNING_CONFIG wc\n" +
                "LEFT JOIN WARNING_INFO wi ON wc.DEVICEID = wi.DEVICEID\n";

        sql += "WHERE wc.isDel = 0\n";

        sql += "GROUP BY wc.CONFIGNAME,\n" +
                "         wc.CONFIGTYPE,\n" +
                "         wc.CREATETIME,\n" +
                "         wc.CREATEUSER,\n" +
                "         wc.DEVICETYPE,\n" +
                "         wc.DEVICEID,\n" +
                "         wc.FLOOR,\n" +
                "         wc.SUBSET,\n" +
                "         wc.WARNTYPE\n";

        return sql;
    }

    public String getAllWarningConfig() {
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(" config. *, ");
        sql.append(" map.DEVICEID as COLLECTID, ");
        sql.append(" details.signName, ");
        sql.append(" details.signType, ");
        sql.append(" details.valueBegin, ");
        sql.append(" details.valueBegin, ");
        sql.append(" details.valueEnd, ");
        sql.append(" details.valueType, ");
        sql.append(" details.warnLevel, ");
        sql.append(" details.numberBool ");
        sql.append(" FROM ");
        sql.append(" WARNING_CONFIG config ");
        sql.append(" LEFT JOIN WARNING_DEVICE_MAP map ON config.id = map.WARNINGCONFIGID ");
        sql.append(" LEFT JOIN WARNING_SIGN_DETAILS details ON details.configid = config.id ");
        sql.append(" WHERE ");
        sql.append(" config.ISDEL = 0 ");
        sql.append(" AND details.ISDEL = 0 ");
        sql.append(" AND map.WARNINGCONFIGID IS NOT NULL ");
        return sql.toString();
    }



}
