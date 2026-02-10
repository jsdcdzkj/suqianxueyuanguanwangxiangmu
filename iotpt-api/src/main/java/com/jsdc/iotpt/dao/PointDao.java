package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.CarVo;
import org.springframework.stereotype.Repository;


@Repository
public class PointDao {

    public String getList(String name, Integer logicalBuildId, Integer logicalFloorId, Integer logicalAreaId) {
        String sql = "SELECT p.ID,\n" +
                "       p.LOGICALAREANAMES,\n" +
                "       p.CREATETIME,\n" +
                "       p.CREATEUSER,\n" +
                "       p.AREAID,\n" +
                "       p.ISDEL,\n" +
                "       p.NAME,\n" +
                "       p.REMARKS,\n" +
                "       p.SORT,\n" +
                "       p.UPDATETIME,\n" +
                "       p.UPDATEUSER\n" +
                "FROM POINT p where p.isDel = 0\n";
        if (StringUtils.isNotNull(logicalBuildId)) {
            sql += " and p.logicalBuildId = " + logicalBuildId;
        }
        if (StringUtils.isNotNull(logicalFloorId)) {
            sql += " and p.logicalFloorId = " + logicalFloorId;
        }
        if (StringUtils.isNotNull(logicalAreaId)) {
            sql += " and p.logicalAreaId = " + logicalAreaId;
        }
        if (StringUtils.isNotEmpty(name)) {
            sql += " and p.name like '%" + name + "%'";
        }
        sql +=" order by p.SORT ASC" ;
        return sql;
    }

}
