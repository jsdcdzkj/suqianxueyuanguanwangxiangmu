package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class WalkFocalPersonEarlyWarnDao {
    public String getList(Page page, Integer deptId, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" 	f.filePath,");
        sql.append(" 	f.shootTime,");
        sql.append(" 	f.id");
        sql.append(" FROM");
        sql.append(" 	walk_focal_person_early_warn f");
        sql.append(" LEFT JOIN userfile u ON f.faceId = u.faceId");
        sql.append(" WHERE f.deptId =" + deptId);
        sql.append(" AND f.isDelete =" + 0);
        if (StringUtils.isNotEmpty(startTime)) {
            sql.append(" AND f.shootTime >= '" + startTime + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND f.shootTime <= '" + endTime + " 23:59:59'");
        }
        sql.append(" AND f.isDelete = 0");
        sql.append(" ORDER BY  f.shootTime DESC");
        return sql.toString();
    }

    public String getListInfo(Page page, Integer deptId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT f.filePath,f.shootTime,f.id,ar.address as address ");
        sql.append(" FROM");
        sql.append(" walk_focal_person_early_warn f,equipment e ,userfile u ,area ar ");
        sql.append(" WHERE f.deptId =" + deptId);
        sql.append(" AND f.isDelete =" + 0);
        sql.append("  AND  f.faceId = u.faceId");
        sql.append("  AND  f.equipmentId = e.id");
        sql.append("  AND  e.areaId = ar.id");
        sql.append(" ORDER BY  f.shootTime DESC");
        return sql.toString();
    }
}
