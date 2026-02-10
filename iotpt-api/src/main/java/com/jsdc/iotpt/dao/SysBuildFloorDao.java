package com.jsdc.iotpt.dao;

import org.springframework.stereotype.Repository;

@Repository
public class SysBuildFloorDao {
    public String selectFloorOrder(){
        String sql = "select sbf.FLOORNAME,sbf.ID FROM  SYS_BUILD_FLOOR sbf WHERE sbf.ISDEL = 0  AND sbf.ISLARGESCREENDISPLAY = 1 ORDER BY sbf.sort desc";
        return sql;
    }
}
