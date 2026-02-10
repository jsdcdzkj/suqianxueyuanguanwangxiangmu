package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.DeviceGateway;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceGatewayVo;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceGatewayDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, DeviceGateway bean) {
        String sql = " SELECT *  "+
                " FROM device_gateway  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }


    public String getPageList(DeviceGatewayVo vo, Page page){

        StringBuilder sql=new StringBuilder();
        sql.append("SELECT gate.*,sba.AREANAME,csu.supplierName,cmo.modelName,build.BUILDNAME,floor.FLOORNAME FROM DEVICE_GATEWAY gate  ");
        sql.append("LEFT JOIN SYS_BUILD_AREA sba ON gate.AREAID = sba.id ");
        sql.append("LEFT JOIN CONFIG_SUPPLIER csu ON gate.SUPPLIERID=csu.id ");
        sql.append("LEFT JOIN CONFIG_MODEL cmo ON gate.modelId=cmo.id ");
        sql.append("LEFT JOIN SYS_BUILD build ON gate.BUILDID = build.id ");
        sql.append("LEFT JOIN SYS_BUILD_FLOOR floor ON floor.ID=gate.FLOORID ");
        sql.append("where gate.isDel=0 ");

        if (null!=vo.getAreaId()){
            sql.append(" and gate.areaId="+vo.getAreaId());
        }
        if (null!=vo.getFloorId()){
            sql.append(" and gate.floorId="+vo.getFloorId());
        }
        if (null!=vo.getBuildId()){
            sql.append(" and gate.buildId="+vo.getBuildId());
        }

        if (StringUtils.isNotEmpty(vo.getName())){
            sql.append(" and gate.name like '%"+vo.getName()+"%'");
        }

        if (null != vo.getSupplierId()){
            sql.append(" and gate.supplierId="+vo.getSupplierId());
        }

        return sql.toString();
    }


    public String getEntityById(Integer id){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT GATE.*, SBUILD.BUILDNAME,sbfloor.floorName,sbarea.areaName, ");
        sql.append("SUPPLIER.supplierName,cmodel.modelName,topicP.topicName topicNameP, ");
        sql.append("topicS.topicName topicNameS,protocol.protocolName, ");
        sql.append("clink.linkName,TRANSFER.modelName transferName ");
        sql.append("FROM DEVICE_GATEWAY gate LEFT JOIN SYS_BUILD sbuild ON GATE.BUILDID = SBUILD. ID ");
        sql.append("LEFT JOIN SYS_BUILD_FLOOR sbfloor ON gate.floorid = sbfloor. ID ");
        sql.append("LEFT JOIN SYS_BUILD_AREA sbarea ON gate.areaid = sbarea. ID ");
        sql.append("LEFT JOIN CONFIG_SUPPLIER supplier ON gate.supplierid = supplier. ID ");
        sql.append("LEFT JOIN CONFIG_MODEL cmodel ON gate.MODELID=cmodel.id ");
        sql.append("LEFT JOIN CONFIG_TOPIC topicP ON gate.publishtopicid=topicP.id ");
        sql.append("LEFT JOIN CONFIG_TOPIC topicS ON gate.SubscribeTopicid=topicS.id ");
        sql.append("LEFT JOIN CONFIG_PROTOCOL protocol ON gate.configprotocolid=protocol.id ");
        sql.append("LEFT JOIN CONFIG_LINK clink ON gate.configlinkid=clink.id ");
        sql.append("LEFT JOIN CONFIG_DATA_TRANSFER transfer ON gate.transferId=transfer.id  ");
        sql.append("WHERE GATE.ISDEL=0 AND GATE.ID= "+id);

        return sql.toString();
    }
}
