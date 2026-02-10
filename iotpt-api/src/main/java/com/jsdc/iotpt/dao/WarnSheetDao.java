package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.vo.WarnSheetVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * ClassName: WarnSheetDao
 * Description:
 * date: 2023/7/20 13:46
 *
 * @author bn
 */
@Repository
public class WarnSheetDao {

    public String warnList() {
        String sql = "SELECT\n" + "\tCOUNT (1) value,\n" + "\twarnLevel name\n" + "FROM\n" + "\tWARNING_INFO\n" + "where status = '0'\n" + "GROUP BY\n" + "\twarnLevel\n";

        return sql;
    }


    public String warnSourceList() {
        String sql = "SELECT\n" + "count（1） value,\n" + " CASE\n" + "WHEN warnSource = 0 THEN\n" + "\t'告警的异常设备'\n" + "WHEN warnSource = 1 THEN\n" + "\t'上报的异常设备'\n" + "end as name\n" + "FROM\n" + "\tWARNING_INFO\n" + "WHERE\n" + "\tstatus = '0'\n" + "GROUP BY\n" + "\twarnSource";
        return sql;
    }


    /**
     * 安防看板  告警重点设备排行
     * 将大楼内所有设备的报警数量值倒序排列，取前5个设备用横柱图按顺序展示（设备+告警数值）
     *
     * @return
     */
    public String getCollectWarningTop() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT temp.*,collect.name FROM ");
        sql.append(" ( SELECT count( * ) AS warnNum, ws.DEVICECOLLECTID FROM WARN_SHEET ws GROUP BY ws.DEVICECOLLECTID ORDER BY count( * ) DESC )  temp ");
        sql.append(" LEFT JOIN DEVICE_COLLECT collect ON temp.DEVICECOLLECTID = collect.id ");
        sql.append(" WHERE ROWNUM <= 5 ");
        return sql.toString();
    }

    /**
     * 安防看板  实时告警列表
     * 底部实时告警列表区域，展示所有来源的设备告警信息，包括告警对象、告警位置、告警等级、告警来源、告警时间、告警内容等信息。告警消息列表动态向上滚动显示。
     * 弹窗提示：新的线上告警产生，可在右下角弹窗提示(弹窗内容：告警对象、告警位置、告警时间、告警等级)，同时可发出声光告警提示。
     *
     * @return
     */
    public String getRealTimeWarning(WarnSheetVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ws.*,collect.name ");
        sql.append(" from WARN_SHEET ws ");
        sql.append(" LEFT JOIN DEVICE_COLLECT collect ON ws.DEVICECOLLECTID = collect.id  ");
        sql.append(" WHERE 1=1 ");

        if (null != vo) {
            if (StringUtils.isNotEmpty(vo.getWarnLevel())) {
                sql.append(" AND ws.WARNLEVEL = '").append(vo.getWarnLevel()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getStatus())) {
                sql.append(" AND ws.status = '").append(vo.getStatus()).append("'");
            }


            if (StringUtils.isNotEmpty(vo.getHandleStatus())) {
                sql.append(" AND ws.handleStatus = '").append(vo.getHandleStatus()).append("'");
            }

            //来源
            if (StringUtils.isNotEmpty(vo.getWarnSource())) {
                sql.append(" AND ws.warnSource = '").append(vo.getHandleStatus()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getAlertTimeStart())) {
                sql.append(" AND ws.alertTime > '").append(vo.getAlertTimeStart()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getAlertTimeEnd())) {
                sql.append(" AND ws.alertTime < '").append(vo.getAlertTimeEnd()).append("'");
            }
            if (StringUtils.isNotEmpty(vo.getWarnTypes())) {
                sql.append(" AND ws.warnType in(").append(vo.getWarnTypes()).append(")");
            }
        }
        sql.append(" and ROWNUM <=10 ");
        System.out.println(sql.toString());
        return sql.toString();
    }

    /**
     * 安防看板  重点告警区域
     * * 将大楼内所有区域内设备的报警数量值倒序排列，取前5个区域用柱形图按顺序展示（区域+告警数值）
     *
     * @return
     */
    public String getKeyAreaWarning() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT temp.* , area.AREANAME  FROM  ");
        sql.append(" ( SELECT count( * ) AS warnNum, ws.AREAID FROM WARN_SHEET ws GROUP BY ws.AREAID ORDER BY count( * ) DESC ) temp ");
        sql.append(" LEFT JOIN SYS_BUILD_AREA area ON temp.AREAID = area.id  ");
        sql.append(" WHERE  ROWNUM <= 5  ");
        return sql.toString();
    }


    /**
     * 历史记录
     *
     * @param vo
     * @return
     */
    public String getHistoryWarning(WarnSheetVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ws.*,collect.name ");
        sql.append(" from WARN_SHEET ws ");
        sql.append(" LEFT JOIN DEVICE_COLLECT collect ON ws.DEVICECOLLECTID = collect.id  ");
        sql.append(" WHERE 1=1 ");

        if (null != vo) {
            if (StringUtils.isNotEmpty(vo.getWarnLevel())) {
                sql.append(" AND ws.WARNLEVEL = '").append(vo.getWarnLevel()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getStatus())) {
                sql.append(" AND ws.status = '").append(vo.getStatus()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getHandleStatus())) {
                sql.append(" AND ws.handleStatus = '").append(vo.getHandleStatus()).append("'");
            }

            //来源
            if (StringUtils.isNotEmpty(vo.getWarnSource())) {
                sql.append(" AND ws.handleStatus = '").append(vo.getHandleStatus()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getAlertTimeStart())) {
                sql.append(" AND ws.alertTime > '").append(vo.getAlertTimeStart()).append("'");
            }

            if (StringUtils.isNotEmpty(vo.getAlertTimeEnd())) {
                sql.append(" AND ws.alertTime < '").append(vo.getAlertTimeEnd()).append("'");
            }
        }
        return sql.toString();
    }





}
