package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigLinkVo;
import org.springframework.stereotype.Repository;

/**
 * @authon thr
 * @describe 连接管理
 */
@Repository
public class ConfigLinkDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigLinkVo bean) {
        String sql = " SELECT t.*,d.dictLabel protocolName " +
                " FROM config_link t " +
                " left join SYS_DICT d on d.dictType='config_protocol' and t.protocolId = d.dictValue" +
                " WHERE " +
                " t.isDel=0 ";
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotEmpty(bean.getLinkName())) {
                sql += " and t.linkName like '%" + bean.getLinkName() + "%'";
            }
            if (StringUtils.isNotEmpty(bean.getProtocolId())) {
                sql += " and t.protocolId = '" + bean.getProtocolId() + "'";
            }
        }
        sql += " order by t.createTime desc";
        return sql;
    }
}
