package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.DeviceControl;
import org.springframework.stereotype.Repository;

/**
 * ClassName: DeviceControlDao
 * Description:
 * date: 2023/12/20 13:53
 *
 * @author bn
 */
@Repository
public class DeviceControlDao {


    public String getSignalByDeviceTypeId(Integer deviceTypeId){
        StringBuilder sql=new StringBuilder();
        sql.append("select CST.* from CONFIG_SIGNAL_TYPE cst LEFT JOIN CONFIG_DEVICE_SIGNAL_MAP cdsm ON CST.\"ID\"=CDSM.SIGNALTYPEID WHERE CST.DATATYPE=4 ");
        sql.append("AND CDSM.DEVICETYPEID= "+deviceTypeId);
        return sql.toString();
    }

    public String getList(DeviceControl control){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT dc.*,CST.SIGNALTYPENAME,CST.SIGNALTYPECODE FROM DEVICE_CONTROL_abc dc ");
        sql.append("LEFT JOIN CONFIG_SIGNAL_TYPE cst ON DC.SIGNALID=CST.\"ID\" ");
        sql.append("WHERE DC.ISDEL=0  AND cst.signalTypeCode!='NowState' ");
        if (null!=control.getCollectId()){
            sql.append(" AND DC.COLLECTID ='"+control.getCollectId()+"' ");
        }
        return sql.toString();
    }
}
