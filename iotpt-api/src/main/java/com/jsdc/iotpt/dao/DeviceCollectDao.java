package com.jsdc.iotpt.dao;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DataSheetVo;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import com.jsdc.iotpt.vo.ReportManageVo;

import java.util.List;
import java.util.Objects;

/**
 * @projectName: IOT
 * @className: DeviceCollectDao
 * @author: wp
 * @description:
 * @date: 2023/5/9 9:49
 */
public class DeviceCollectDao {

    public String getDeviceByAreas(ReportManageVo bean){
        StringBuilder sql=new StringBuilder();

        sql.append("SELECT DC.id,DC.name,DC.deviceType,DC.ONLINESTATUS,SBA.AREANAME areaName,sbf.floorName floorName FROM DEVICE_COLLECT dc   ");
        sql.append("LEFT JOIN SYS_BUILD_AREA sba ON DC.AREAID=SBA.\"ID\" ");
        sql.append("LEFT JOIN SYS_BUILD_FLOOR sbf ON dc.floorId=sbf.\"ID\" ");
        sql.append("WHERE  ");
        sql.append("DC.ISDEL=0 AND DC.AREAID IN ("+ StringUtils.join(bean.getAreaIds(),",") +")");


        return sql.toString();
    }

    /**
     *  获取设备类型下不同能源类型的采集设备
     * @param bean
     * @return
     */
    public String getDeviceByType(DataSheetVo bean){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT DC.\"ID\",DC.isTotalDevice,cdt.deviceTypeName,sbf.floorname,dc.LOGICALFLOORID,cds.subitemName FROM DEVICE_COLLECT dc ");
        sql.append("LEFT JOIN CONFIG_DEVICE_SUBITEM cds ON DC.SUBITEM = CDS.\"ID\" ");
        sql.append("LEFT JOIN config_device_type cdt on DC.DEVICETYPE=cdt.id ");
        sql.append("LEFT JOIN SYS_BUILD_FLOOR sbf ON dc.LOGICALFLOORID=sbf.id ");
        sql.append("WHERE DC.ISDEL='0' and cds.isDel=0  and dc.isTotalDevice = 1 ");
        // 能源类型
        if (null!=bean.getEnergyType()){
            sql.append(" AND CDS.ENERGY_TYPE='"+bean.getEnergyType()+"'");
        }
        // 设备类型
        if (null!=bean.getDeviceType()){
            sql.append(" AND DC.DEVICETYPE='"+bean.getDeviceType()+"'");
        }
        // 楼宇
        if (null!=bean.getBuildId()){
            sql.append(" AND DC.LOGICALBUILDID='"+bean.getBuildId()+"'");
        }
        // 楼层
        if (null!=bean.getFloorId()){
            sql.append(" AND DC.LOGICALFLOORID='"+bean.getFloorId()+"'");
        }
        // 区域
        if (null!=bean.getAreaId()){
            sql.append(" AND DC.LOGICALAREAID ='"+bean.getAreaId()+"'");
        }

        if (!bean.getFloorIdList().isEmpty()){
            sql.append(" AND DC.LOGICALFLOORID in ("+StringUtils.join(bean.getFloorIdList(),",")+")");
        }



        return sql.toString();
    }


    public String getDeviceByFloor(DataSheetVo bean){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT DC.\"ID\",DC.isTotalDevice,cdt.deviceTypeName,sbf.floorname,dc.LOGICALFLOORID,cds.subitemName FROM DEVICE_COLLECT dc ");
        sql.append("LEFT JOIN CONFIG_DEVICE_SUBITEM cds ON DC.SUBITEM = CDS.\"ID\" ");
        sql.append("LEFT JOIN config_device_type cdt on DC.DEVICETYPE=cdt.id ");
        sql.append("LEFT JOIN SYS_BUILD_FLOOR sbf ON dc.LOGICALFLOORID=sbf.id ");
        sql.append("WHERE DC.ISDEL='0' and (cds.isDel=0 or cds.subitemcode='RESERVE' ) and DC.LOGICALAREAID ='0' AND DC.LOGICALFLOORID !='0'   and dc.isTotalDevice = 1 ");
        // 能源类型
        if (null!=bean.getEnergyType()){
            sql.append(" AND CDS.ENERGY_TYPE='"+bean.getEnergyType()+"'");
        }
        // 设备类型
        if (null!=bean.getDeviceType()){
            sql.append(" AND DC.DEVICETYPE='"+bean.getDeviceType()+"'");
        }
        // 楼宇
        if (null!=bean.getBuildId()){
            sql.append(" AND DC.LOGICALBUILDID='"+bean.getBuildId()+"'");
        }
        // 楼层
        if (null!=bean.getFloorId()){
            sql.append(" AND DC.LOGICALFLOORID='"+bean.getFloorId()+"'");
        }
        // 区域
        if (null!=bean.getAreaId()){
            sql.append(" AND DC.LOGICALAREAID ='"+bean.getAreaId()+"'");
        }

        if (!bean.getFloorIdList().isEmpty()){
            sql.append(" AND DC.LOGICALFLOORID in ("+StringUtils.join(bean.getFloorIdList(),",")+")");
        }



        return sql.toString();
    }

    /**
     *  获取区域下不同能源类型的采集设备
     * @param bean
     * @return
     */
    public String getDeviceByArea(DataSheetVo bean){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT DC.* FROM DEVICE_COLLECT dc ");
        sql.append("LEFT JOIN CONFIG_DEVICE_SUBITEM cds ON DC.SUBITEM = CDS.\"ID\" ");
        sql.append("WHERE DC.ISDEL='0' \n" +
                "\t AND DC.status = 2  ");
        if (null != bean.getSubitem()) {
            sql.append(" AND DC.SUBITEM ='" + bean.getSubitem() + "'");
        }

        if (null != bean.getId()) {
            sql.append(" AND DC.id ='" + bean.getId() + "'");
        }

        // 能源类型
        if (null!=bean.getEnergyType()){
            sql.append(" AND CDS.ENERGY_TYPE='"+bean.getEnergyType()+"'");
        }
        // 区域collectVo.setLogicalFloorId(0);//大楼总电表
        if (null!=bean.getAreaId()){
            sql.append(" AND DC.LOGICALAREAID ='"+bean.getAreaId()+"'");
        }
        // 是否总设备
        if (null!=bean.getIsTotalDevice()){
            sql.append(" AND dc.isTotalDevice ='"+bean.getIsTotalDevice()+"' ");
        }

        // 是否空开
        if (null!=bean.getDeviceType()){
            sql.append(" AND dc.deviceType ='"+bean.getDeviceType()+"' ");
        }

        // 楼宇
        if (null!=bean.getBuildId()){
            sql.append(" AND dc.logicalBuildId ='"+bean.getBuildId()+"' ");
        }

        // 楼层
        if (null!=bean.getFloorId()){
            sql.append(" AND dc.logicalFloorId ='"+bean.getFloorId()+"' ");
        }
        //楼层
        if (CollectionUtils.isNotEmpty(bean.getFloorIdList())){
            sql.append(" AND DC.LOGICALFLOORID in ("+StringUtils.join(bean.getFloorIdList(),",")+")");
        }


        if (bean.getBuildIds() == null && bean.getFloorIds() == null && bean.getAreaIds() == null) {
        } else {
            if (bean.getBuildIds() != null && bean.getBuildIds().size() > 0
                    && bean.getFloorIds() != null && bean.getFloorIds().size() > 0
                    && bean.getAreaIds() != null && bean.getAreaIds().size() > 0) {
                sql.append(" and (DC.logicalBuildId in( " + StringUtils.join(bean.getBuildIds(), ",") + ")");
                sql.append(" or DC.logicalFloorId in( " + StringUtils.join(bean.getFloorIds(), ",") + ")");
                sql.append(" or DC.logicalAreaId in( " + StringUtils.join(bean.getAreaIds(), ",") + ") )");
            } else if ((bean.getBuildIds() == null || bean.getBuildIds().size() == 0)
                    && bean.getFloorIds() != null && bean.getFloorIds().size() > 0
                    && bean.getAreaIds() != null && bean.getAreaIds().size() > 0) {
                sql.append(" and (DC.logicalFloorId in( " + StringUtils.join(bean.getFloorIds(), ",") + ") AND DC.logicalAreaId = 0 ");
                sql.append(" or DC.logicalAreaId in( " + StringUtils.join(bean.getAreaIds(), ",") + ") )");
            } else if ((bean.getBuildIds() == null || bean.getBuildIds().size() == 0)
                    && bean.getFloorIds() != null && bean.getFloorIds().size() > 0
                    && (bean.getAreaIds() == null || bean.getAreaIds().size() == 0)) {
                sql.append(" and (DC.logicalFloorId in( " + StringUtils.join(bean.getFloorIds(), ",") + " ) AND DC.logicalAreaId = 0 )");
            } else if ((bean.getBuildIds() == null || bean.getBuildIds().size() == 0)
                    && (bean.getFloorIds() == null || bean.getFloorIds().size() == 0)
                    && bean.getAreaIds() != null && bean.getAreaIds().size() > 0) {
                sql.append(" and (DC.logicalAreaId in( " + StringUtils.join(bean.getAreaIds(), ",") + ") )");
            }
        }
        return sql.toString();
    }


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param vo
     * @return
     */
    public String getEntityList(Page page, DeviceCollectVo vo) {
        String sql = " SELECT t.*," +
                "CASE\n" +
                "\t\tt.isTotalDevice \n" +
                "\t\tWHEN 0 THEN\n" +
                "\t\t'否' \n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t'是' ELSE '否' \n" +
                "\tEND isTotalDeviceName , " +
                "s.supplierName, " +
//                "b.buildName, " +
//                "f.floorName, " +
//                "a.areaName, " +
                "u.realName userName, " +
                "cds.subitemName, " +
                "g.name gatewayName, " +
                "g.deviceCode gatewayCode, " +
                "d.dictLabel companyName, " +
                "ds.dictLabel statusName, " +
                "dt.deviceTypeName deviceTypeName, " +
                "m.modelName " +

                " FROM DEVICE_COLLECT t " +
                " left join CONFIG_SUPPLIER s on s.id = t.supplierId" +
//                " left join sys_build b on b.id = t.buildId" +
//                " left join sys_build_floor f on f.id = t.floorId" +
//                " left join sys_build_area a on a.id = t.areaId" +
                " left join SYS_USER u on u.id = t.userId" +
                " left join CONFIG_DEVICE_SUBITEM cds on cds.id = t.subitem" +
                " left join device_gateway g on g.id = t.gatewayId" +
                " left join sys_dict d on d.dictType = 'supplierType' and t.companyId = d.dictValue" +
                " left join sys_dict ds on ds.dictType = 'deviceStatus' and t.status = ds.dictValue" +
                " left join config_device_type dt on dt.id = t.deviceType" +
                " left join config_model m on m.id = t.modelNum" +
                " WHERE " +
                " t.isDel=0 ";
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getName())) {
                sql += " and t.name like '%" + vo.getName() + "%'";
            }
            if (StringUtils.isNotNull(vo.getDeviceType())) {
                sql += " and t.deviceType = " + vo.getDeviceType();
            }
            if (StringUtils.isNotNull(vo.getSubitem())) {
                sql += " and t.subitem = " + vo.getSubitem();
            }
            if (StringUtils.isNotNull(vo.getAreaId())) {
                sql += " and t.areaId = " + vo.getAreaId();
            }
            if (StringUtils.isNotNull(vo.getFloorId())) {
                sql += " and t.floorId = " + vo.getFloorId();
            }
            if (StringUtils.isNotNull(vo.getBuildId())) {
                sql += " and t.buildId = " + vo.getBuildId();
            }
            if (StringUtils.isNotNull(vo.getLogicalAreaId()) && vo.getLogicalAreaId() != 0) {
                sql += " and t.logicalAreaId = " + vo.getLogicalAreaId();
            }
            if (StringUtils.isNotNull(vo.getLogicalFloorId()) && vo.getLogicalFloorId() != 0) {
                sql += " and t.logicalFloorId = " + vo.getLogicalFloorId();
            }
            if (StringUtils.isNotNull(vo.getLogicalBuildId()) && vo.getLogicalBuildId() != 0) {
                sql += " and t.logicalBuildId = " + vo.getLogicalBuildId();
            }
            if (StringUtils.isNotEmpty(vo.getStatus())) {
                sql += " and t.status = '" + vo.getStatus() + "'";
            }
            if (StringUtils.isNotEmpty(vo.getOnLineStatus())) {
                sql += " and t.onLineStatus = '" + vo.getOnLineStatus() + "'";
            }
            if (StringUtils.isNotEmpty(vo.getIsGateway())) {
                if (vo.getIsGateway().equals("0")) {
                    sql += " and t.gatewayId is null ";
                }
            }
            if (StringUtils.isNotNull(vo.getSupplierId())) {
                sql += " and t.supplierId = " + vo.getSupplierId();
            }

        }
        sql += " order by t.createTime desc,t.id desc";
        return sql;
    }

    public String getList(DeviceCollectVo vo) {
        String sql = " SELECT t.*," +
                "CASE\n" +
                "\t\tt.isTotalDevice \n" +
                "\t\tWHEN 0 THEN\n" +
                "\t\t'否' \n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t'是' ELSE '否' \n" +
                "\tEND isTotalDeviceName , " +
                "s.supplierName, " +
//                "b.buildName, " +
//                "f.floorName, " +
//                "a.areaName, " +
                "u.realName userName, " +
                "cds.subitemName, " +
                "g.name gatewayName, " +
                "g.deviceCode gatewayCode, " +
                "d.dictLabel companyName, " +
                "ds.dictLabel statusName, " +
                "dt.deviceTypeName deviceTypeName, " +
                "m.modelName, " +
                "TO_CHAR(t.installationDate, 'yyyy-MM-dd') installationDateStr, " +
                "TO_CHAR(t.useTime, 'yyyy-MM-dd') useTimeStr, " +
                "TO_CHAR(t.inspectionDate, 'yyyy-MM-dd') inspectionDateStr, " +
                "TO_CHAR(t.procureDate, 'yyyy-MM-dd') procureDateStr, " +
                "TO_CHAR(t.expirationDate, 'yyyy-MM-dd') expirationDateStr " +

                " FROM DEVICE_COLLECT t " +
                " left join CONFIG_SUPPLIER s on s.id = t.supplierId" +
//                " left join sys_build b on b.id = t.buildId" +
//                " left join sys_build_floor f on f.id = t.floorId" +
//                " left join sys_build_area a on a.id = t.areaId" +
                " left join SYS_USER u on u.id = t.userId" +
                " left join CONFIG_DEVICE_SUBITEM cds on cds.id = t.subitem" +
                " left join device_gateway g on g.id = t.gatewayId" +
                " left join sys_dict d on d.dictType = 'supplierType' and t.companyId = d.dictValue" +
                " left join sys_dict ds on ds.dictType = 'deviceStatus' and t.status = ds.dictValue" +
                " left join config_device_type dt on dt.id = t.deviceType" +
                " left join config_model m on m.id = t.modelNum" +
                " WHERE " +
                " t.isDel=0 ";
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getName())) {
                sql += " and t.name like '%" + vo.getName() + "%'";
            }
            if (StringUtils.isNotNull(vo.getDeviceType())) {
                sql += " and t.deviceType = " + vo.getDeviceType();
            }
            if (StringUtils.isNotNull(vo.getSubitem())) {
                sql += " and t.subitem = " + vo.getSubitem();
            }
            if (StringUtils.isNotNull(vo.getAreaId())) {
                sql += " and t.areaId = " + vo.getAreaId();
            }
            if (StringUtils.isNotNull(vo.getLogicalAreaId()) && vo.getLogicalAreaId() != 0) {
                sql += " and t.logicalAreaId = " + vo.getLogicalAreaId();
            }
            if (StringUtils.isNotNull(vo.getLogicalFloorId()) && vo.getLogicalFloorId() != 0) {
                sql += " and t.logicalFloorId = " + vo.getLogicalFloorId();
            }
            if (StringUtils.isNotNull(vo.getLogicalBuildId()) && vo.getLogicalBuildId() != 0) {
                sql += " and t.logicalBuildId = " + vo.getLogicalBuildId();
            }
            if (StringUtils.isNotEmpty(vo.getStatus())) {
                sql += " and t.status = '" + vo.getStatus() + "'";
            }

            if (StringUtils.isNotNull(vo.getGatewayId())) {
                sql += " and t.gatewayId=" + vo.getGatewayId();
            }

            if (StringUtils.isNotNull(vo.getFloorId())) {
                sql += " and t.floorId=" + vo.getFloorId();
            }

            if (StringUtils.isNotNull(vo.getBuildId())) {
                sql += " and t.buildId=" + vo.getBuildId();
            }

            if (StringUtils.isNotNull(vo.getIsTenant())) {
                sql += " and t.isTenant=" + vo.getIsTenant();
            }

            if (StringUtils.isNotNull(vo.getDeviceIds())) {
                sql += " and t.id not in (" + vo.getDeviceIds()+")";
            }

            if (CollectionUtils.isNotEmpty(vo.getDeviceTypes())){
                sql += " AND t.deviceType in ("+StringUtils.join(vo.getDeviceTypes(),",")+")";
            }
        }
        sql += " order by t.createTime desc,t.id desc";
        return sql;
    }


    public String getFileOut(Integer id) {
        String sql = " SELECT\n" +
                "\ta.DEVICECODE,\n" +
                "\td.SIGNALTYPECODE,\n" +
                "\td.SIGNALTYPENAME \n" +
                "FROM\n" +
                "\tDEVICE_COLLECT a\n" +
                "\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\tLEFT JOIN CONFIG_DEVICE_SIGNAL_MAP c ON b.id = c.DEVICETYPEID\n" +
                "\tLEFT JOIN CONFIG_SIGNAL_TYPE d ON c.SIGNALTYPEID = d.id \n" +
                "WHERE\n";
        if (StringUtils.isNotNull(id)) {
            sql += " \ta.GATEWAYID =  " + id;
        }
        sql += " \tAND a.ISDEL=0\n" +
                "\tAND c.ISDEL=0";
        sql += " order by a.DEVICECODE";
        return sql;
    }

    public String allDevice() {
        String sql = "(SELECT NAME,deviceCode FROM DEVICE_COLLECT " +
                "UNION ALL SELECT NAME,deviceCode FROM DEVICE_GATEWAY " +
                "UNION ALL SELECT NAME,deviceCode FROM DEVICE_ACCESS_CONTROL " +
                "UNION ALL SELECT NAME,deviceCode FROM DEVICE_VIDEO) temp ";
        return sql;
    }

    public String getDeviceStatus(DeviceCollectVo bean) {
//        设备在线状态（1正常、2离线、3异常）
        String sql = "SELECT dc.deviceType,\n" +
                "       SUM(CASE dc.onLineStatus WHEN '1' THEN 1 ELSE 0 END)        zc,\n" +
                "       SUM(CASE dc.onLineStatus WHEN '2' THEN 1 ELSE 0 END)        lx,\n" +
                "       SUM(CASE dc.onLineStatus WHEN '3' THEN 1 ELSE 0 END)        yc,\n" +
                "       dt.deviceTypeName deviceTypeName\n" +
                "FROM DEVICE_COLLECT dc\n" +
                "         LEFT JOIN config_device_type dt ON dt.id = dc.deviceType\n";

        sql += "WHERE dc.isDel = 0 AND dt.deviceTypeName IS NOT NULL\n";

        sql += "GROUP BY dc.deviceType,\n" +
                "         dt.deviceTypeName\n";

        sql += "UNION ALL\n";

        sql += "SELECT dc.deviceType,\n" +
                "       SUM(CASE dc.onLineStatus WHEN '1' THEN 1 ELSE 0 END)        zc,\n" +
                "       SUM(CASE dc.onLineStatus WHEN '2' THEN 1 ELSE 0 END)        lx,\n" +
                "       SUM(CASE dc.onLineStatus WHEN '3' THEN 1 ELSE 0 END)        yc,\n" +
                "       dt.deviceTypeName deviceTypeName\n" +
                "FROM DEVICE_AEP dc\n" +
                "         LEFT JOIN config_device_type dt ON dt.id = dc.deviceType\n";

        sql += "WHERE dc.isDel = 0 AND dt.deviceTypeName IS NOT NULL\n";

        sql += "GROUP BY dc.deviceType,\n" +
                "         dt.deviceTypeName\n";


        return sql;
    }

    public String getDeviceInfo(Page page, DeviceQueryVo queryVo){
        String sql = "select a.* ,c.deviceTypeName,b.subitemName,d.BUILDNAME,e.FLOORNAME,f.AREANAME,c.FILENAME from DEVICE_COLLECT a\n" +
                "INNER JOIN config_device_subitem b\n" +
                "on a.subitem = b.id\n" +
                "LEFT JOIN CONFIG_DEVICE_TYPE c ON a.DEVICETYPE = c.ID\n" +
                "\tLEFT JOIN SYS_BUILD d ON a.LOGICALBUILDID = d.id\n" +
                "\tLEFT JOIN SYS_BUILD_FLOOR e ON a.LOGICALFLOORID = e.id\n" +
                "\tLEFT JOIN SYS_BUILD_AREA f ON f.id = a.LOGICALAREAID \n" +
                "where 1=1 and a.isDel = 0 \n" +
                "and a.status = 2 \n" ;
        if(StringUtils.isNotEmpty(queryVo.getEnergyType())){
            sql +=" and b.energy_type='"+queryVo.getEnergyType()+"'";
        }
            if(StringUtils.isNotEmpty(queryVo.getDeviceName())){
                sql +="and a.name like '%"+queryVo.getDeviceName()+"%'";
            }
            if(queryVo.getBuildId() != null){
                sql +=" and a.LOGICALBUILDID = "+queryVo.getBuildId();
            }
            if(queryVo.getFloorId() != null){
                sql +=" and a.LOGICALFLOORID = "+queryVo.getFloorId();
            }
            if(queryVo.getAreaId() != null){
                sql +=" and a.LOGICALAREAID = "+queryVo.getAreaId();
            }
            if(StringUtils.isNotEmpty(queryVo.getDeviceType())){
                sql +=" and a.DEVICETYPE = '"+queryVo.getDeviceType()+"'";
            }
            if(StringUtils.isNotEmpty(queryVo.getOnLineStatus())){
                sql +=" and a.onLineStatus = '"+queryVo.getOnLineStatus()+"'";
            }
        if (queryVo.getBuildIds() == null && queryVo.getFloorIds() == null && queryVo.getAreaIds() == null) {
        } else {
            if (queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ")";
                sql += " or a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ")";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ") AND a.logicalAreaId = 0 ";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && (queryVo.getAreaIds() == null || queryVo.getAreaIds().size() == 0)) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + " ) AND a.logicalAreaId = 0 )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && (queryVo.getFloorIds() == null || queryVo.getFloorIds().size() == 0)
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0)
                    && (queryVo.getFloorIds() == null || queryVo.getFloorIds().size() == 0)
                    && (queryVo.getAreaIds() == null || queryVo.getAreaIds().size() == 0)) {
                sql += " and (a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ") )";
                sql += " and a.logicalFloorId = 0 and a.logicalAreaId = 0 ";
            } else if ((queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && (queryVo.getAreaIds() == null || queryVo.getAreaIds().size() == 0)) {
                sql += " and ((a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ") and a.logicalFloorId = 0 and a.logicalAreaId = 0) ";
                sql += " or (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ") AND a.logicalAreaId = 0 ) )";
            } else if ((queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0)
                    && (queryVo.getFloorIds() == null || queryVo.getFloorIds().size() == 0)
                    && (queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0)) {
                sql += " and ((a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ") and a.logicalFloorId = 0 and a.logicalAreaId = 0) ";
                sql += " or (a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") ) )";
            }
        }
            if(queryVo.getSubitemId() != null){
                sql +=" and a.subitem = "+queryVo.getSubitemId();
            }
        sql +=" and a.deviceType in (10007,10005,10006,200084,200124,200166)";
            sql += " order by e.sort asc";
        return sql;
    }

    public String getSubitemEnergy(DeviceQueryVo queryVo){
        String sql = "select a.* ,c.deviceTypeName,d.BUILDNAME,e.FLOORNAME,f.AREANAME from DEVICE_COLLECT a\n" +
                "INNER JOIN config_device_subitem b\n" +
                "on a.subitem = b.id\n" +
                "LEFT JOIN CONFIG_DEVICE_TYPE c ON a.DEVICETYPE = c.ID\n" +
                "\tLEFT JOIN SYS_BUILD d ON a.LOGICALBUILDID = d.id\n" +
                "\tLEFT JOIN SYS_BUILD_FLOOR e ON a.LOGICALFLOORID = e.id\n" +
                "\tLEFT JOIN SYS_BUILD_AREA f ON f.id = a.LOGICALAREAID \n" +
                "where 1=1 and a.isDel = 0 \n" +
                "and a.status = 2 \n" +
                "and b.energy_type='"+queryVo.getEnergyType()+"'\n" ;
        if(StringUtils.isNotEmpty(queryVo.getDeviceName())){
            sql +="and a.name like '%"+queryVo.getDeviceName()+"%'";
        }
        if(StringUtils.isNotEmpty(queryVo.getDeviceId())){
            sql +="and a.id ="+queryVo.getDeviceId()+"";
        }
        if(queryVo.getBuildId() != null){
            sql +=" and a.LOGICALBUILDID = "+queryVo.getBuildId();
        }
        if(queryVo.getFloorId() != null){
            sql +=" and a.LOGICALFLOORID = "+queryVo.getFloorId();
        }
        if(queryVo.getAreaId() != null){
            sql +=" and a.LOGICALAREAID = "+queryVo.getAreaId();
        }
        if(StringUtils.isNotEmpty(queryVo.getDeviceType())){
            sql +=" and a.DEVICETYPE = '"+queryVo.getDeviceType()+"'";
        }
        if(StringUtils.isNotEmpty(queryVo.getOnLineStatus())){
            sql +=" and a.onLineStatus = '"+queryVo.getOnLineStatus()+"'";
        }
        if (queryVo.getBuildIds() == null && queryVo.getFloorIds() == null && queryVo.getAreaIds() == null) {
        } else {
            if (queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ")";
                sql += " or a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ")";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ") AND a.logicalAreaId = 0 ";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && (queryVo.getAreaIds() == null || queryVo.getAreaIds().size() == 0)) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + " ) AND a.logicalAreaId = 0 )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && (queryVo.getFloorIds() == null || queryVo.getFloorIds().size() == 0)
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            }
        }
        if(queryVo.getSubitemId() != null){
            sql +=" and a.subitem = "+queryVo.getSubitemId();
        }
//        sql +=" and a.deviceType in (10007,10005)";
        sql += " order by e.sort asc";
        return sql;
    }

    public String getDeviceList(DeviceQueryVo queryVo){
        String sql = "select a.* from DEVICE_COLLECT a\n" +
                "INNER JOIN config_device_subitem b\n" +
                "on a.subitem = b.id\n" +
                "where a.isDel = 0\n";
        if(StringUtils.isNotEmpty(queryVo.getDeviceName())){
            sql +="and a.name like '%"+queryVo.getDeviceName()+"%'";
        }
        if(queryVo.getBuildId() != null){
            sql +=" and a.BUILDID = "+queryVo.getBuildId();
        }
        if(queryVo.getFloorId() != null){
            sql +=" and a.FLOORID = "+queryVo.getFloorId();
        }
        if(queryVo.getAreaId() != null){
            sql +=" and a.AREAID = "+queryVo.getAreaId();
        }
        if(StringUtils.isNotEmpty(queryVo.getDeviceId())){
            sql +=" and a.id = '"+queryVo.getDeviceId()+"'";
        }
        return sql;
    }

    public String getDeviceHarmonics(DeviceQueryVo queryVo){
        String sql = "select a.id,a.name from DEVICE_COLLECT a\n" +
                "INNER JOIN config_device_subitem b on a.subitem = b.id\n" +
                "LEFT JOIN config_device_type c ON a.deviceType = c.id AND c.isdel=0\n" +
                "LEFT JOIN config_device_signal_map d ON c.id = d.devicetypeid AND d.isdel=0\n" +
                "LEFT JOIN config_signal_type f ON f.id = d.signalTypeId AND d.isdel=0\n" +
                "where a.isDel = 0\n";
        if(StringUtils.isNotEmpty(queryVo.getDeviceId())){
            sql +="and a.id = '"+queryVo.getDeviceId()+"'";
        }
        if(StringUtils.isNotEmpty(queryVo.getDeviceType())){
            sql +="and a.deviceType = '"+queryVo.getDeviceType()+"'";
        }
        if(StringUtils.isNotEmpty(queryVo.getDeviceName())){
            sql +="and a.name like '%"+queryVo.getDeviceName()+"%'";
        }
        if(queryVo.getLogicalBuildId() != null && queryVo.getLogicalBuildId() != 0){
            sql +=" and a.LOGICALBUILDID = "+queryVo.getLogicalBuildId();
        }
        if(queryVo.getLogicalFloorId() != null && queryVo.getLogicalFloorId() != 0){
            sql +=" and a.LOGICALFLOORID = "+queryVo.getLogicalFloorId();
        }
        if(queryVo.getLogicalAreaId() != null && queryVo.getLogicalAreaId() != 0){
            sql +=" and a.LOGICALAREAID = "+queryVo.getLogicalAreaId();
        }

        if (queryVo.getBuildIds() == null && queryVo.getFloorIds() == null && queryVo.getAreaIds() == null) {
        } else {
            if (queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ")";
                sql += " or a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ")";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ") AND a.logicalAreaId = 0 ";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && (queryVo.getAreaIds() == null || queryVo.getAreaIds().size() == 0)) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + " ) AND a.logicalAreaId = 0 )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && (queryVo.getFloorIds() == null || queryVo.getFloorIds().size() == 0)
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            }
        }

        if(StringUtils.isNotEmpty(queryVo.getSignalTypeCode())){
            if("1".equals(queryVo.getSignalTypeCode())){//电流
                sql +=" and f.signalTypeCode in('I_A','I_B','I_C')";
            }else if("2".equals(queryVo.getSignalTypeCode())){//电压
                sql +=" and f.signalTypeCode in('U_A','U_B','U_C')";
            } else {
                sql +=" and f.signalTypeCode = '" + queryVo.getSignalTypeCode() + "'";
            }
        }
        sql += "GROUP BY a.id,a.name\n";
        return sql;
    }

    //峰谷统计
    public String coulometry(Integer buildId, Integer floorId, Integer areaId, Integer deviceType, Integer energyType) {
        StringBuilder str = new StringBuilder();
        str.append("SELECT\n" +
                "\ta.*,\n" +
                "\tb.SUBITEMNAME \n" +
                "FROM\n" +
                "\tDEVICE_COLLECT a\n" +
                "\tLEFT JOIN CONFIG_DEVICE_SUBITEM b ON a.SUBITEM = b.ID \n" +
                "WHERE\n" +
                "\ta.ISDEL = 0 \n");
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.LOGICALBUILDID = " + buildId + " \n");
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.LOGICALFLOORID = " + floorId + " \n");
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.LOGICALAREAID = " + areaId + " \n");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE = '" + deviceType + "' \n");
        }
        if (StringUtils.isNotNull(energyType)) {
            str.append("\tAND b.ENERGY_TYPE = " + energyType + " \n");
        }
        return str.toString();
    }

//    /**
//     * @Description: 设备监控
//     * @param: [areaId, name, type]
//     * @return: java.lang.String
//     * @author: 苹果
//     * @date: 2023/7/26
//     * @time: 9:18
//     */
//    public String deviceMonitoring(Integer onLineStatus, Integer areaId, Integer floorId, Integer buildId, String name,
//                                   Integer type, String areaName, Integer deviceType, String areaIds ,
//                                   String buildIds , String floorIds,String id) {
//       return deviceMonitoring(onLineStatus,areaId,floorId,buildId,name,type,areaName,deviceType,areaIds,buildIds,floorIds,id,null);
//    }

    /**
     * @Description: 设备监控
     * @param: [areaId, name, type]
     * @return: java.lang.String
     * @author: 苹果
     * @date: 2023/7/26
     * @time: 9:18
     */
    public String deviceMonitoring(Integer onLineStatus, Integer areaId, Integer floorId, Integer buildId, String name,
                                   Integer type, String areaName, Integer deviceType, String areaIds ,
                                   String buildIds , String floorIds, String id, List<Integer> deviceTypes) {
        StringBuilder str = new StringBuilder(" select * from (\n" +
                "\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\ta.AREAID,\n" +
                "\t\ta.FLOORID,\n" +
                "\t\ta.BUILDID,\n" +
                "\t\ta.CREATETIME,\n" +
                "\t\ta.CREATEUSER,\n" +
                "\t\ta.DEVICECODE,\n" +
                "\t\ta.COMPANYID,\n" +
                "\t\ta.DEVICETYPE,\n" +
                "\t\ta.ISDEL,\n" +
                "\t\ta.ISTOTALDEVICE,\n" +
                "\t\ta.LOAD,\n" +
                "\t\ta.LOGICALAREAID,\n" +
                "\t\ta.LOGICALBUILDID,\n" +
                "\t\ta.LOGICALFLOORID,\n" +
                "\t\ta.MODELNUM,\n" +
                "\t\ta.NAME,\n" +
                "\t\ta.ONLINESTATUS,\n" +
                "\t\ta.STATUS,\n" +
                "\t\ta.SUBITEM,\n" +
                "\t\ta.SUPPLIERID,\n" +
                " \t'co' as tab,  " +
                "\t\tb.DEVICETYPENAME,\n" +
                "\t\tb.DEVICETYPECODE,\n" +
                "\t\tc.BUILDNAME,\n" +
                "\t\td.FLOORNAME,\n" +
                "\t\tb.FILENAME,\n" +
                "\t\te.AREANAME \n" +
                "\tFROM\n" +
                "\t\tDEVICE_COLLECT a\n" +
                "\t\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\t\tLEFT JOIN SYS_BUILD c ON a.BUILDID = c.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_FLOOR d ON a.FLOORID = d.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_AREA e ON e.id = a.AREAID \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.isDel = 0 \n" +
                "\t\tAND a.status = 2 ");
        if (StringUtils.isNotNull(onLineStatus)) {
            str.append("\tAND a.onLineStatus=" + onLineStatus);
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.AREAID=" + areaId);
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.FLOORID=" + floorId);
        }
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.BUILDID=" + buildId);
        }
        if (StringUtils.isNotEmpty(name)) {
            str.append("\tAND a.NAME like '%" + name + "%'");
        }
        if (StringUtils.isNotNull(type)) {
            str.append("\tAND a.DEVICETYPE=" + type);
        }
        if (StringUtils.isNotNull(id)) {
            str.append("\tAND a.id=" + id);
        }
        if (StringUtils.isNotEmpty(areaName)) {
            str.append("\tAND e.AREANAME like '%" + areaName +"%'");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE=" + deviceType);
        }
        if (CollUtil.isNotEmpty(deviceTypes)) {
            str.append(" AND a.DEVICETYPE IN (");
            String join = StringUtils.join(deviceTypes, ",");
            str.append(join).append(")");
        }
        if (StringUtils.isNotEmpty(areaIds) && !Objects.equals(areaIds,"")) {
            str.append("\tAND a.AREAID in(" + areaIds+")");
        }
        if (StringUtils.isNotEmpty(buildIds) && !Objects.equals(buildIds,"")) {
            str.append("\tAND a.BUILDID in(" + buildIds+")");
        }
        if (StringUtils.isNotEmpty(floorIds) && !Objects.equals(floorIds,"")) {
            str.append("\tAND a.FLOORID in(" + floorIds+")");
        }
        str.append(" UNION\n" +
                "\tALL ");
        str.append("\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\ta.AREAID,\n" +
                "\t\ta.FLOORID,\n" +
                "\t\ta.BUILDID,\n" +
                "\t\ta.CREATETIME,\n" +
                "\t\ta.CREATEUSER,\n" +
                "\t\ta.DEVICECODE,\n" +
                "\t\ta.COMPANYID,\n" +
                "\t\ta.DEVICETYPE,\n" +
                "\t\ta.ISDEL,\n" +
                "\t\ta.ISTOTALDEVICE,\n" +
                "\t\ta.LOAD,\n" +
                "\t\ta.LOGICALAREAID,\n" +
                "\t\ta.LOGICALBUILDID,\n" +
                "\t\ta.LOGICALFLOORID,\n" +
                "\t\ta.MODELNUM,\n" +
                "\t\ta.NAME,\n" +
                "\t\ta.ONLINESTATUS,\n" +
                "\t\ta.STATUS,\n" +
                "\t\ta.SUBITEM,\n" +
                "\t\ta.SUPPLIERID,\n" +
                " \t'aep' as tab,  " +
                "\t\tb.DEVICETYPENAME,\n" +
                "\t\tb.DEVICETYPECODE,\n" +
                "\t\tc.BUILDNAME,\n" +
                "\t\td.FLOORNAME,\n" +
                "\t\tb.FILENAME,\n" +
                "\t\te.AREANAME \n" +
                "\tFROM\n" +
                "\t\tDEVICE_AEP a\n" +
                "\t\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\t\tLEFT JOIN SYS_BUILD c ON a.BUILDID = c.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_FLOOR d ON a.FLOORID = d.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_AREA e ON e.id = a.AREAID \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.isDel = 0 \n" +
                "\tAND a.status = 2 ");
        if (StringUtils.isNotNull(onLineStatus)) {
            str.append("\tAND a.onLineStatus=" + onLineStatus);
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.AREAID=" + areaId);
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.FLOORID=" + floorId);
        }
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.BUILDID=" + buildId);
        }
        if (StringUtils.isNotEmpty(name)) {
            str.append("\tAND a.NAME like '%" + name + "%'");
        }

        if (StringUtils.isNotNull(type)) {
            str.append("\tAND a.DEVICETYPE=" + type);
        }
        if (StringUtils.isNotNull(id)) {
            str.append("\tAND a.id=" + id);
        }
        if (StringUtils.isNotEmpty(areaName)) {
            str.append("\tAND e.AREANAME like '%" + areaName +"%'");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE=" + deviceType);
        }
        if (CollUtil.isNotEmpty(deviceTypes)) {
            str.append(" AND a.DEVICETYPE IN (");
            String join = StringUtils.join(deviceTypes, ",");
            str.append(join).append(")");
        }
        if (StringUtils.isNotEmpty(areaIds) && !Objects.equals(areaIds,"")) {
            str.append("\tAND a.AREAID in(" + areaIds+")");
        }
        if (StringUtils.isNotEmpty(buildIds) && !Objects.equals(buildIds,"")) {
            str.append("\tAND a.BUILDID in(" + buildIds+")");
        }
        if (StringUtils.isNotEmpty(floorIds) && !Objects.equals(floorIds,"")) {
            str.append("\tAND a.FLOORID in(" + floorIds+")");
        }
        str.append(" UNION\n" +
                "\tALL ");
        str.append("\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\ta.AREAID,\n" +
                "\t\ta.FLOORID,\n" +
                "\t\ta.BUILDID,\n" +
                "\t\ta.CREATETIME,\n" +
                "\t\ta.CREATEUSER,\n" +
                "\t\ta.DEVICECODE,\n" +
                "\t\ta.COMPANYID,\n" +
                "\t\ta.DEVICETYPE,\n" +
                "\t\ta.ISDEL,\n" +
                "\t\ta.ISTOTALDEVICE,\n" +
                "\t\ta.LOAD,\n" +
                "\t\ta.LOGICALAREAID,\n" +
                "\t\ta.LOGICALBUILDID,\n" +
                "\t\ta.LOGICALFLOORID,\n" +
                "\t\ta.MODELNUM,\n" +
                "\t\ta.NAME,\n" +
                "\t\ta.ONLINESTATUS,\n" +
                "\t\ta.STATUS,\n" +
                "\t\ta.SUBITEM,\n" +
                "\t\ta.SUPPLIERID,\n" +
                " \t'vi' as tab,  " +
                "\t\tb.DEVICETYPENAME,\n" +
                "\t\tb.DEVICETYPECODE,\n" +
                "\t\tc.BUILDNAME,\n" +
                "\t\td.FLOORNAME,\n" +
                "\t\tb.FILENAME,\n" +
                "\t\te.AREANAME \n" +
                "\tFROM\n" +
                "\t\tDEVICE_VIDEO a\n" +
                "\t\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\t\tLEFT JOIN SYS_BUILD c ON a.BUILDID = c.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_FLOOR d ON a.FLOORID = d.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_AREA e ON e.id = a.AREAID \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.isDel = 0 \n" +
                "\tAND a.status = 1 ");
        if (StringUtils.isNotNull(onLineStatus)) {
            str.append("\tAND a.onLineStatus=" + onLineStatus);
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.AREAID=" + areaId);
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.FLOORID=" + floorId);
        }
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.BUILDID=" + buildId);
        }
        if (StringUtils.isNotEmpty(name)) {
            str.append("\tAND a.NAME like '%" + name + "%'");
        }
        if (StringUtils.isNotNull(type)) {
            str.append("\tAND a.DEVICETYPE=" + type);
        }
        if (StringUtils.isNotNull(id)) {
            str.append("\tAND a.id=" + id);
        }
        if (StringUtils.isNotEmpty(areaName)) {
            str.append("\tAND e.AREANAME like '%" + areaName +"%'");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE=" + deviceType);
        }
        if (CollUtil.isNotEmpty(deviceTypes)) {
            str.append(" AND a.DEVICETYPE IN (");
            String join = StringUtils.join(deviceTypes, ",");
            str.append(join).append(")");
        }
        if (StringUtils.isNotEmpty(areaIds) && !Objects.equals(areaIds,"")) {
            str.append("\tAND a.AREAID in(" + areaIds+")");
        }
        if (StringUtils.isNotEmpty(buildIds) && !Objects.equals(buildIds,"")) {
            str.append("\tAND a.BUILDID in(" + buildIds+")");
        }
        if (StringUtils.isNotEmpty(floorIds) && !Objects.equals(floorIds,"")) {
            str.append("\tAND a.FLOORID in(" + floorIds+")");
        }
        str.append(") a \torder by a.AREAID");
        return str.toString();
    }


    public String deviceMonitoring2(Integer onLineStatus, Integer areaId, Integer floorId, Integer buildId, String name,
                                   Integer type, String areaName, Integer deviceType, String areaIds ,
                                   String buildIds , String floorIds, String id, List<String> deviceTypes) {
        StringBuilder str = new StringBuilder(" select * from (\n" +
                "\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\ta.AREAID,\n" +
                "\t\ta.FLOORID,\n" +
                "\t\ta.BUILDID,\n" +
                "\t\ta.CREATETIME,\n" +
                "\t\ta.CREATEUSER,\n" +
                "\t\ta.DEVICECODE,\n" +
                "\t\ta.COMPANYID,\n" +
                "\t\ta.DEVICETYPE,\n" +
                "\t\ta.ISDEL,\n" +
                "\t\ta.ISTOTALDEVICE,\n" +
                "\t\ta.LOAD,\n" +
                "\t\ta.LOGICALAREAID,\n" +
                "\t\ta.LOGICALBUILDID,\n" +
                "\t\ta.LOGICALFLOORID,\n" +
                "\t\ta.MODELNUM,\n" +
                "\t\ta.NAME,\n" +
                "\t\ta.ONLINESTATUS,\n" +
                "\t\ta.STATUS,\n" +
                "\t\ta.SUBITEM,\n" +
                "\t\ta.SUPPLIERID,\n" +
                " \t'co' as tab,  " +
                "\t\tb.DEVICETYPENAME,\n" +
                "\t\tb.DEVICETYPECODE,\n" +
                "\t\tc.BUILDNAME,\n" +
                "\t\td.FLOORNAME,\n" +
                "\t\tb.FILENAME,\n" +
                "\t\te.AREANAME \n" +
                "\tFROM\n" +
                "\t\tDEVICE_COLLECT a\n" +
                "\t\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\t\tLEFT JOIN SYS_BUILD c ON a.BUILDID = c.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_FLOOR d ON a.FLOORID = d.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_AREA e ON e.id = a.AREAID \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.isDel = 0 \n" +
                "\t\tAND a.status = 2 ");
        if (StringUtils.isNotNull(onLineStatus)) {
            str.append("\tAND a.onLineStatus=" + onLineStatus);
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.AREAID=" + areaId);
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.FLOORID=" + floorId);
        }
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.BUILDID=" + buildId);
        }
        if (StringUtils.isNotEmpty(name)) {
            str.append("\tAND a.NAME like '%" + name + "%'");
        }
        if (StringUtils.isNotNull(type)) {
            str.append("\tAND a.DEVICETYPE=" + type);
        }
        if (StringUtils.isNotNull(id)) {
            str.append("\tAND a.id=" + id);
        }
        if (StringUtils.isNotEmpty(areaName)) {
            str.append("\tAND e.AREANAME like '%" + areaName +"%'");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE=" + deviceType);
        } 
        if (CollUtil.isNotEmpty(deviceTypes)) {
            str.append(" AND a.DEVICETYPE IN (");
            String join = StringUtils.join(deviceTypes, ",");
            str.append(join).append(")");
        }
        if (StringUtils.isNotEmpty(areaIds) && !Objects.equals(areaIds,"")) {
            str.append("\tAND a.AREAID in(" + areaIds+")");
        }
        if (StringUtils.isNotEmpty(buildIds) && !Objects.equals(buildIds,"")) {
            str.append("\tAND a.BUILDID in(" + buildIds+")");
        }
        if (StringUtils.isNotEmpty(floorIds) && !Objects.equals(floorIds,"")) {
            str.append("\tAND a.FLOORID in(" + floorIds+")");
        }
        str.append(" UNION\n" +
                "\tALL ");
        str.append("\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\ta.AREAID,\n" +
                "\t\ta.FLOORID,\n" +
                "\t\ta.BUILDID,\n" +
                "\t\ta.CREATETIME,\n" +
                "\t\ta.CREATEUSER,\n" +
                "\t\ta.DEVICECODE,\n" +
                "\t\ta.COMPANYID,\n" +
                "\t\ta.DEVICETYPE,\n" +
                "\t\ta.ISDEL,\n" +
                "\t\ta.ISTOTALDEVICE,\n" +
                "\t\ta.LOAD,\n" +
                "\t\ta.LOGICALAREAID,\n" +
                "\t\ta.LOGICALBUILDID,\n" +
                "\t\ta.LOGICALFLOORID,\n" +
                "\t\ta.MODELNUM,\n" +
                "\t\ta.NAME,\n" +
                "\t\ta.ONLINESTATUS,\n" +
                "\t\ta.STATUS,\n" +
                "\t\ta.SUBITEM,\n" +
                "\t\ta.SUPPLIERID,\n" +
                " \t'aep' as tab,  " +
                "\t\tb.DEVICETYPENAME,\n" +
                "\t\tb.DEVICETYPECODE,\n" +
                "\t\tc.BUILDNAME,\n" +
                "\t\td.FLOORNAME,\n" +
                "\t\tb.FILENAME,\n" +
                "\t\te.AREANAME \n" +
                "\tFROM\n" +
                "\t\tDEVICE_AEP a\n" +
                "\t\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\t\tLEFT JOIN SYS_BUILD c ON a.BUILDID = c.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_FLOOR d ON a.FLOORID = d.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_AREA e ON e.id = a.AREAID \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.isDel = 0 \n" +
                "\tAND a.status = 2 ");
        if (StringUtils.isNotNull(onLineStatus)) {
            str.append("\tAND a.onLineStatus=" + onLineStatus);
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.AREAID=" + areaId);
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.FLOORID=" + floorId);
        }
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.BUILDID=" + buildId);
        }
        if (StringUtils.isNotEmpty(name)) {
            str.append("\tAND a.NAME like '%" + name + "%'");
        }

        if (StringUtils.isNotNull(type)) {
            str.append("\tAND a.DEVICETYPE=" + type);
        }
        if (StringUtils.isNotNull(id)) {
            str.append("\tAND a.id=" + id);
        }
        if (StringUtils.isNotEmpty(areaName)) {
            str.append("\tAND e.AREANAME like '%" + areaName +"%'");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE=" + deviceType);
        }
        if (CollUtil.isNotEmpty(deviceTypes)) {
            str.append(" AND a.DEVICETYPE IN (");
            String join = StringUtils.join(deviceTypes, ",");
            str.append(join).append(")");
        }
        if (StringUtils.isNotEmpty(areaIds) && !Objects.equals(areaIds,"")) {
            str.append("\tAND a.AREAID in(" + areaIds+")");
        }
        if (StringUtils.isNotEmpty(buildIds) && !Objects.equals(buildIds,"")) {
            str.append("\tAND a.BUILDID in(" + buildIds+")");
        }
        if (StringUtils.isNotEmpty(floorIds) && !Objects.equals(floorIds,"")) {
            str.append("\tAND a.FLOORID in(" + floorIds+")");
        }
        str.append(" UNION\n" +
                "\tALL ");
        str.append("\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\ta.AREAID,\n" +
                "\t\ta.FLOORID,\n" +
                "\t\ta.BUILDID,\n" +
                "\t\ta.CREATETIME,\n" +
                "\t\ta.CREATEUSER,\n" +
                "\t\ta.DEVICECODE,\n" +
                "\t\ta.COMPANYID,\n" +
                "\t\ta.DEVICETYPE,\n" +
                "\t\ta.ISDEL,\n" +
                "\t\ta.ISTOTALDEVICE,\n" +
                "\t\ta.LOAD,\n" +
                "\t\ta.LOGICALAREAID,\n" +
                "\t\ta.LOGICALBUILDID,\n" +
                "\t\ta.LOGICALFLOORID,\n" +
                "\t\ta.MODELNUM,\n" +
                "\t\ta.NAME,\n" +
                "\t\ta.ONLINESTATUS,\n" +
                "\t\ta.STATUS,\n" +
                "\t\ta.SUBITEM,\n" +
                "\t\ta.SUPPLIERID,\n" +
                " \t'vi' as tab,  " +
                "\t\tb.DEVICETYPENAME,\n" +
                "\t\tb.DEVICETYPECODE,\n" +
                "\t\tc.BUILDNAME,\n" +
                "\t\td.FLOORNAME,\n" +
                "\t\tb.FILENAME,\n" +
                "\t\te.AREANAME \n" +
                "\tFROM\n" +
                "\t\tDEVICE_VIDEO a\n" +
                "\t\tLEFT JOIN CONFIG_DEVICE_TYPE b ON a.DEVICETYPE = b.ID\n" +
                "\t\tLEFT JOIN SYS_BUILD c ON a.BUILDID = c.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_FLOOR d ON a.FLOORID = d.id\n" +
                "\t\tLEFT JOIN SYS_BUILD_AREA e ON e.id = a.AREAID \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.isDel = 0 \n" +
                "\tAND a.status = 1 ");
        if (StringUtils.isNotNull(onLineStatus)) {
            str.append("\tAND a.onLineStatus=" + onLineStatus);
        }
        if (StringUtils.isNotNull(areaId)) {
            str.append("\tAND a.AREAID=" + areaId);
        }
        if (StringUtils.isNotNull(floorId)) {
            str.append("\tAND a.FLOORID=" + floorId);
        }
        if (StringUtils.isNotNull(buildId)) {
            str.append("\tAND a.BUILDID=" + buildId);
        }
        if (StringUtils.isNotEmpty(name)) {
            str.append("\tAND a.NAME like '%" + name + "%'");
        }
        if (StringUtils.isNotNull(type)) {
            str.append("\tAND a.DEVICETYPE=" + type);
        }
        if (StringUtils.isNotNull(id)) {
            str.append("\tAND a.id=" + id);
        }
        if (StringUtils.isNotEmpty(areaName)) {
            str.append("\tAND e.AREANAME like '%" + areaName +"%'");
        }
        if (StringUtils.isNotNull(deviceType)) {
            str.append("\tAND a.DEVICETYPE=" + deviceType);
        }
        if (CollUtil.isNotEmpty(deviceTypes)) {
            str.append(" AND a.DEVICETYPE IN (");
            String join = StringUtils.join(deviceTypes, ",");
            str.append(join).append(")");
        }
        if (StringUtils.isNotEmpty(areaIds) && !Objects.equals(areaIds,"")) {
            str.append("\tAND a.AREAID in(" + areaIds+")");
        }
        if (StringUtils.isNotEmpty(buildIds) && !Objects.equals(buildIds,"")) {
            str.append("\tAND a.BUILDID in(" + buildIds+")");
        }
        if (StringUtils.isNotEmpty(floorIds) && !Objects.equals(floorIds,"")) {
            str.append("\tAND a.FLOORID in(" + floorIds+")");
        }
        str.append(") a \torder by a.AREAID");
        return str.toString();
    }

    public String getDeviceTypeCode(Integer id) {
        String sql = "SELECT\n" +
                "dc.DEVICECODE,\n" +
                "dc.DEVICETYPE,\n" +
                "cdt.DEVICETYPECODE\n" +
                "FROM\n" +
                "DEVICE_COLLECT dc\n" +
                "LEFT JOIN CONFIG_DEVICE_TYPE cdt ON dc.DEVICETYPE = cdt.ID\n" +
                "WHERE\n" +
                "dc.id = " + id;
        return sql;
    }

    public String getNotTotalDeviceList(Integer id) {
        String sql = "SELECT\n" +
                "\t* \n" +
                "FROM\n" +
                "\tDEVICE_COLLECT \n" +
                "WHERE\n" +
                "\tsubitem = " + id + "\n" +
                "\tAND (( logicalFloorId = 0 AND logicalAreaId <> 0 ) OR ( logicalFloorId <> 0 AND logicalAreaId = 0 ))";
        return sql;
    }

    public String getDeviceReport(DeviceQueryVo queryVo){
        String sql = "select a.* ,c.deviceTypeName,d.BUILDNAME,e.FLOORNAME,f.AREANAME,f.address from DEVICE_COLLECT a\n" +
                "INNER JOIN config_device_subitem b\n" +
                "on a.subitem = b.id\n" +
                "LEFT JOIN CONFIG_DEVICE_TYPE c ON a.DEVICETYPE = c.ID\n" +
                "\tLEFT JOIN SYS_BUILD d ON a.logicalBuildId = d.id\n" +
                "\tLEFT JOIN SYS_BUILD_FLOOR e ON a.logicalFloorId = e.id\n" +
                "\tLEFT JOIN SYS_BUILD_AREA f ON f.id = a.logicalAreaId \n" +
                "where 1=1 and a.isDel = 0\n" +
                "and a.status = 2 \n" +
                "and a.isTotalDevice = 0 \n" +
                "and b.energy_type='"+queryVo.getEnergyType()+"'\n" ;
        if(StringUtils.isNotEmpty(queryVo.getDeviceName())){
            sql +="and a.name like '%"+queryVo.getDeviceName()+"%'";
        }

        if (queryVo.getBuildIds() == null && queryVo.getFloorIds() == null && queryVo.getAreaIds() == null) {
        } else {
            if (queryVo.getBuildIds() != null && queryVo.getBuildIds().size() > 0
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalBuildId in( " + StringUtils.join(queryVo.getBuildIds(), ",") + ")";
                sql += " or a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ")";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + ") AND a.logicalAreaId = 0 ";
                sql += " or a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && queryVo.getFloorIds() != null && queryVo.getFloorIds().size() > 0
                    && (queryVo.getAreaIds() == null || queryVo.getAreaIds().size() == 0)) {
                sql += " and (a.logicalFloorId in( " + StringUtils.join(queryVo.getFloorIds(), ",") + " ) AND a.logicalAreaId = 0 )";
            } else if ((queryVo.getBuildIds() == null || queryVo.getBuildIds().size() == 0)
                    && (queryVo.getFloorIds() == null || queryVo.getFloorIds().size() == 0)
                    && queryVo.getAreaIds() != null && queryVo.getAreaIds().size() > 0) {
                sql += " and (a.logicalAreaId in( " + StringUtils.join(queryVo.getAreaIds(), ",") + ") )";
            }
        }

        if (StringUtils.isNotEmpty(queryVo.getDeviceType())) {
            sql += " and a.DEVICETYPE = '" + queryVo.getDeviceType() + "'";
        }
        if(queryVo.getSubitemId() != null){
            sql +=" and a.subitem = "+queryVo.getSubitemId();
        }
        return sql;
    }

    public String deviceByType(String type){
        StringBuilder str=new StringBuilder();
        str.append("SELECT\n" +
                "\tb.DEVICECOLLECTID \n" +
                "FROM\n" +
                "\tDEVICE_COLLECT a\n" +
                "\tRIGHT JOIN WARNING_INFO b ON a.ID = b.DEVICECOLLECTID \n" +
                "WHERE\n" +
                "\t1 = 1 \n" +
                "\tAND a.ISDEL = 0 \n" +
                "\tAND b.STATUS = 0 ");
        str.append("\tAND a.DEVICETYPE = "+type);
        str.append("GROUP BY\n" +
                "\tb.DEVICECOLLECTID");
        return str.toString();
    }

    public String selectTenantRoomDevice(Integer deviceType, Integer id, List<Integer> excludeIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT dc.ID as deviceId, dc.FLOORID, dc.AREAID, dc.LOGICALFLOORID, dc.LOGICALAREAID, dc.NAME, dc.STATUS, dc.ONLINESTATUS, cdt.DEVICETYPENAME, cds.SUBITEMNAME");
        sb.append(" FROM DEVICE_COLLECT dc");
        sb.append(" LEFT JOIN CONFIG_DEVICE_TYPE cdt ON dc.DEVICETYPE = cdt.ID ");
        sb.append(" LEFT JOIN CONFIG_DEVICE_SUBITEM cds ON dc.SUBITEM = cds.ID ");
        sb.append(" WHERE dc.ISDEL = 0 AND dc.isTenant = 1");
        if (Objects.nonNull(deviceType)) {
            sb.append(" AND dc.DEVICETYPE = ").append(deviceType);
        }
        if (Objects.nonNull(id)) {
            sb.append(" AND dc.ID = ").append(id);
        }
        if (CollUtil.isNotEmpty(excludeIds)) {
            sb.append(" AND dc.ID NOT IN (").append(org.apache.commons.lang3.StringUtils.join(excludeIds, ",")).append(")");
        }
        return sb.toString();
    }

    public String selectNewTenantRoomDevice(Page page, List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT dc.ID as deviceId, dc.FLOORID, dc.AREAID, dc.LOGICALFLOORID, dc.LOGICALAREAID, dc.NAME, dc.STATUS, dc.ONLINESTATUS, cdt.DEVICETYPENAME, cds.SUBITEMNAME");
        sb.append(" FROM DEVICE_COLLECT dc");
        sb.append(" LEFT JOIN CONFIG_DEVICE_TYPE cdt ON dc.DEVICETYPE = cdt.ID ");
        sb.append(" LEFT JOIN CONFIG_DEVICE_SUBITEM cds ON dc.SUBITEM = cds.ID ");
        sb.append(" WHERE dc.ISDEL = 0 AND dc.isTenant = 1");
        if (CollUtil.isNotEmpty(ids)) {
            sb.append(" AND dc.ID IN (").append(org.apache.commons.lang3.StringUtils.join(ids, ",")).append(")");
        }
        return sb.toString();
    }

}
