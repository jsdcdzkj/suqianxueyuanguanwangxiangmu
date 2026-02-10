package com.jsdc.iotpt.dao;

/**
 * @projectName: IOT
 * @className: ConfigDeviceSubitemDao
 * @author: wp
 * @description:
 * @date: 2023/5/9 8:51
 */
public class ConfigDeviceSubitemDao {

    public String getSubValue(String year){
        String sql = "SELECT\n" +
                "  * \n" +
                "FROM\n" +
                "  CONFIG_DEVICE_SUBITEM cds\n" +
                "  LEFT JOIN CONFIG_DEVICE_SUBITEM_VALUE cdsv ON cds.id = cdsv.subitemId \n" +
                "WHERE\n" +
                "  cdsv.year = '"+year+"' and cds.isDel=0 and cds.energy_type=1" ;
        return sql ;
    }


    public String existenceOrNot(String year){
        String sql = "SELECT\n" +
                "  *\n" +
                "    FROM\n" +
                "    CONFIG_DEVICE_SUBITEM cds\n" +
                "    LEFT JOIN CONFIG_DEVICE_SUBITEM_VALUE cdsv ON cds.id = cdsv.subitemId and  cdsv.year = '"+year+"'\n" +
                "    WHERE\n" +
                "    cds.isDel=0 and cds.energy_type=1     and cdsv.id is null" ;
        return sql ;
    }

}
