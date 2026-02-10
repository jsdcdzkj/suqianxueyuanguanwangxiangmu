package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.vo.SysBuildAreaVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SysBuildAreaDao {


    public String getAllList(SysBuildAreaVo vo){
        StringBuilder sql=new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(" sys_build_area area ");
        sql.append(" LEFT JOIN sys_build_floor floor ON area.floorId = floor.id  ");
        sql.append(" LEFT JOIN sys_build build ON floor.dictBuilding = build.id  ");
        sql.append(" where area.isDel=0 and floor.isDel=0 and build.isDel=0 ");
        if (null!=vo.getBuildId()){
            sql.append(" and build.id='"+vo.getBuildId()+"' ");
        }
        if (null!=vo.getFloorId()){
            sql.append(" and floor.id='"+vo.getFloorId()+"' ");
        }
        if (null!=vo.getId()){
            sql.append(" and area.id='"+vo.getId()+"' ");
        }

        return sql.toString();
    }


    public String selectByIds(List<String> areaIds) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sba.AREANAME, SBF.FLOORNAME FROM SYS_BUILD_AREA sba");
        sql.append(" LEFT JOIN SYS_BUILD_FLOOR sbf ON SBA.FLOORID = SBF.ID");
        sql.append(" WHERE SBA.ID IN (").append(String.join(",", areaIds)).append(")");
        return sql.toString();
    }
}
