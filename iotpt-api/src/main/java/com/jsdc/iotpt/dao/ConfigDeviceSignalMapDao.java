package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigDeviceSignalMapDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigDeviceSignalMap bean) {
        String sql = " SELECT *  " +
                " FROM config_device_signal_map  " +
                " WHERE " +
                " 1=1 ";

        return sql;
    }


    public String getEntityByTId(Integer id) {
        String sql = "SELECT\n" +
                "\tc.id,\n" +
                "\tc.signaltypename,\n" +
                "\tc.signaltypedesc,\n" +
                "\tc.signalType,\n" +
                "\td.ITEMVAL AS unit, \n" +
                "\tc.signalType ,\n" +
                "\tc.signaltypecode\n" +
                "FROM\n" +
                "\tconfig_device_signal_map a\n" +
                "\tLEFT JOIN config_device_type b ON a.devicetypeid = b.id AND b.isdel=0\n" +
                "\tLEFT JOIN config_signal_type c ON a.signaltypeid= c.id AND c.isdel=0\n" +
                "\tLEFT JOIN CONFIG_SIGNAL_TYPE_ITEM d ON d.TYPEID= c.id AND d.isdel=0\n" +
        "\tWHERE \n" +
                "\t1=1\n" +
                "\tAND a.isdel=0\n" +
                " and c.isdel=0 ";
        if (StringUtils.isNotNull(id)) {
            sql += "\tAND a.devicetypeid=" + id;
        }


        return sql;
    }

    public String getEntityByTIdGroup(Integer id) {
        String sql = "SELECT\n" +
                "\tc.signalType,\n" +
                "\tsd.dictlabel dictlabel,\n" +
                "\tsd.sort\n" +
                "FROM\n" +
                "\tconfig_device_signal_map a\n" +
                "\tLEFT JOIN config_device_type b ON a.devicetypeid = b.id AND b.isdel=0\n" +
                "\tLEFT JOIN config_signal_type c ON a.signaltypeid= c.id AND c.isdel=0\n" +
                " left join sys_dict sd on sd.dictvalue = c.signalType "+
                "\tWHERE \n" +
                "\t1=1\n" +
                "\tAND a.isdel=0\n" +
                " and c.isdel=0 and sd.dicttype='wlSignalType'";
        if (StringUtils.isNotNull(id)) {
            sql += "\tAND a.devicetypeid=" + id;
        }

        sql += " group by c.signalType ,sd.dictlabel,sd.sort order by sd.sort" ;

        return sql;
    }

    /**
     * 根据设备类型编码获取信号类型
     */
    public String getEntityByTCode(ConfigDeviceType bean) {
        String sql = "SELECT\n" +
                "\tc.id,\n" +
                "\tc.signaltypename,\n" +
                "\tc.signaltypedesc,\n" +
                "\tc.signaltypecode\n" +
                "FROM\n" +
                "\tconfig_device_signal_map a\n" +
                "\tLEFT JOIN config_device_type b ON a.devicetypeid = b.id AND b.isdel=0\n" +
                "\tLEFT JOIN config_signal_type c ON a.signaltypeid= c.id AND c.isdel=0\n" +
                "\tWHERE \n" +
                "\t1=1\n" +
                "\tAND a.isdel=0\n";

        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getDeviceTypeCode())) {
                sql += "\tAND b.deviceTypeCode='" + bean.getDeviceTypeCode() + "'";
            }
            if (StringUtils.isNotNull(bean.getId())) {
                sql += "\tAND b.id=" + bean.getId();
            }
        }
        return sql;
    }

    public String getSignalListByDeviceCode(Integer deviceCode) {
        String sql = "SELECT\n" +
                "\t* \n" +
                "FROM\n" +
                "\tCONFIG_DEVICE_SIGNAL_MAP cdsm\n" +
                "\tLEFT JOIN CONFIG_SIGNAL_TYPE cst ON cdsm.SIGNALTYPEID = cst.ID \n" +
                "WHERE\n" +
                "\tcdsm.DEVICETYPEID = " + deviceCode + "\n" +
                "\tAND cdsm.ISDEL = 0";
        return sql;

    }
}
