package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigTopic;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigTopicVo;

/**
 * @authon thr
 * @describe 主题管理
 */
public class ConfigTopicDao {

    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigTopicVo bean) {
        String sql = " SELECT t.*,d.linkName linkName " +
                " FROM config_topic t " +
                " left join config_link d on d.id = t.linkId" +
                " WHERE " +
                " t.isDel=0 ";
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotEmpty(bean.getTopicName())) {
                sql += " and t.topicName like '%" + bean.getTopicName() + "%'";
            }
            if (StringUtils.isNotEmpty(bean.getTopicCode())) {
                sql += " and t.topicCode like '%" + bean.getTopicCode() + "%'";
            }
            if (StringUtils.isNotNull(bean.getTopicType())) {
                sql += " and t.topicType = " + bean.getTopicType();
            }
            if (StringUtils.isNotNull(bean.getLinkId())) {
                sql += " and t.linkId = " + bean.getLinkId();
            }
        }
        sql += " order by t.id desc";
        return sql;
    }


    public String cacheTopic(ConfigTopic configTopic){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT TOPIC.TOPICNAME,TOPIC.TRANSFERID TRANSFERID from CONFIG_TOPIC topic ");
        sql.append("INNER JOIN CONFIG_DATA_TRANSFER transfer ON TOPIC.TRANSFERID=TRANSFER.ID ");
        sql.append("where TOPIC.TOPICTYPE=1 and TOPIC.topicStatus=1 and TOPIC.ISDEL=0 AND TRANSFER.ISDEL=0 ");

        return sql.toString();

    }

}
