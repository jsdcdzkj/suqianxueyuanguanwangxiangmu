package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.MsgMessageInfo;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * ClassName: MsgMessageInfoDao
 * Description:
 * date: 2023/5/8 16:52
 *
 * @author bn
 */
@Repository
public class MsgMessageInfoDao {

    public String updateOne(MsgMessageInfo bean){
        StringBuilder sql = new StringBuilder();
        sql.append(" update msg_message_info set msgName = '"+bean.getMsgName()+"', " );
        sql.append(" msgType = '"+bean.getMsgType()+"', ");

        String value = null;
        Integer temp = null;
        if (StringUtils.isEmpty(bean.getEmailType())){
            sql.append(" emailType = "+value+", ");
        }else {
            sql.append(" emailType = '"+bean.getEmailType()+"', ");
        }

        if (StringUtils.isEmpty(bean.getProtocol())){
            sql.append(" protocol = "+value+", ");
        }else {
            sql.append(" protocol = '"+bean.getProtocol()+"', ");
        }

        if (StringUtils.isEmpty(bean.getServer())){
            sql.append(" server = "+value+", ");
        }else {
            sql.append(" server = '"+bean.getServer()+"', ");
        }

        if (null == bean.getPort()){
            sql.append(" port = "+temp+", ");
        }else {
            sql.append(" port = "+bean.getPort()+", ");
        }

        if (StringUtils.isEmpty(bean.getName())){
            sql.append(" name = "+value+", ");
        }else {
            sql.append(" name = '"+bean.getName()+"', ");
        }

        if (StringUtils.isEmpty(bean.getPwd())){
            sql.append(" pwd = "+value );
        }else {
            sql.append(" pwd = '"+bean.getPwd()+"' ");
        }

        sql.append(" where id = "+bean.getId());
        return sql.toString();
    }


}
