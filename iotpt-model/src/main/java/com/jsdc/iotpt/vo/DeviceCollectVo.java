package com.jsdc.iotpt.vo;

import cn.hutool.core.util.StrUtil;
import com.jsdc.iotpt.model.ConfigSignalTypeItem;
import com.jsdc.iotpt.model.DeviceCollect;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * @authon thr
 * @describe 采集设备表
 */
@Data
public class DeviceCollectVo extends DeviceCollect implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNo = 1;
    private Integer pageSize = 10;


    private String floors;
    private Integer timeType;
    private String areas;
    private String builds;

    // 供应商名称
    private String supplierName;
    // 楼宇名称
    private String buildName;
    // 楼层名称
    private String floorName;
    // 区域名称
    private String areaName;
    // 设备管理员名称
    private String userName;
    // 维保公司名称
    private String companyName;
    // 网关设备名称
    private String gatewayName;
    // 网关设备编码
    private String gatewayCode;
    // 设备状态名称
    private String statusName;
    // 所属分项名称
    private String subitemName;
    // 设备类型名称
    private String deviceTypeName;
    // 设备类型编码
    private String deviceTypeCode;
    // 设备型号名称
    private String modelName;
    //位置
    private String location;

    // 是否排除gatewayid 0
    private String isGateway;

    private String checkIds;

    /**
     * 安装日期
     */
    private String installationDateStr;

    /**
     * 使用日期
     */
    private String useTimeStr;
    /**
     * 年检日期
     */
    private String inspectionDateStr;

    /**
     * 采购日期
     */
    private String procureDateStr;

    /**
     * 过保日期
     */
    private String expirationDateStr;

    private List<DeviceCollectVo> voList;
    // 设备型号的信号类型编码
    private String signalTypeCode;
    //设备型号的信号类型名称
    private String signalTypeName;

    //物理位置
    private List<String> areaIds;
    //逻辑位置
    private List<String> logicalAreaIds;

    //物理位置
    private String areaNames;
    //逻辑位置
    private String logicalAreaNames;
    private String logicalAreaName;

    private String logicalLocation;

    public String getLogicalLocation() {
        String str = buildName;
        if(StrUtil.isNotBlank(floorName)){
            str += "/" + floorName;
        }
        if(StrUtil.isNotBlank(areaName)){
            str += "/" + areaName;
        }
        return str;
    }

    /**
     * 逻辑位置
     * 是否总设备 0否 1是
     */
    private String isTotalDeviceName;

    //逻辑位置-所属楼宇
    private List<Integer> logicalBuildIds;
    private List<Integer> logicalFloorIds;
    private List<Integer> logicalAreaIdList;
    //设备在线状态统计 正常
    private String zc;
    //设备在线状态统计 异常
    private String yc;
    //设备在线状态统计 离线
    private String lx;
    //设备在线状态统计 样式图标
    private String ys;

    private List<HashMap<String, Object>> mapList;

    //模式
    private List<ConfigSignalTypeItem> configSignalTypeItems ;


    //风速
    private List<ConfigSignalTypeItem> configSignalTypeItems2 ;

    private String file;

    /**
     * 用来拼接 设备类型 与 过电流保护电流定值
     */
    private List<ConfigDeviceSignalMapVo> cdsm;

    private String tab;

    //用作回显设备图片
    private String fileName;


    private  List<String> stringList ;

    private  AirConditionCMDVo airConditionCMD;

    private String roomDeviceId;//房间设备关联id

}
