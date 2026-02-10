package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingUserGroupDao {

    public String selectByLoginUser(String groupName, String userName, Integer userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT a.* FROM MEETINGUSERGROUP a ");
        sb.append("LEFT JOIN MEETGROUPMEMBER b ON a.ID = b.GROUPID ");
        sb.append("LEFT JOIN SYS_USER c ON b.USERID = c.ID WHERE a.ISDEL = 0 ");
        if (StringUtils.isNotNull(userId)) {
            sb.append("AND a.CREATEUSER = ").append(userId);
        }
        if (StringUtils.isNotBlank(groupName)) {
            sb.append("AND a.GROUPNAME LIKE '%").append(groupName).append("%' ");
        }
        if (StringUtils.isNotBlank(userName)) {
            sb.append("AND c.REALNAME LIKE '%").append(userName).append("%' ");
        }
        sb.append("ORDER BY a.CREATETIME DESC");
        return sb.toString();
    }

}
