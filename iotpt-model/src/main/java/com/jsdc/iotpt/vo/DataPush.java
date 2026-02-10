package com.jsdc.iotpt.vo;

import lombok.Data;

/**
 * 参考 doc文件夹下 NBIOT数据格式
 */
@Data
public class DataPush {
    private String messageID;//帧头，固定为0x55
    private String pkgSn;//数据包编号，每发一包数据自动加1，连续的两包不重复,范围:0~99
    private String length;//后面业务数据长度
    private String devType;//"产品类型: FF水压
    private String iMEI;//0x01门磁探测器；0x02水浸；0x03烟感；0x04燃气；0x05红外感应；0x06CO传感器；0x07紧急按钮；0x08温湿度传感器；"
    private String iMSI;//使用BCD码方式传送，高位补0.
    private String cSQ;//数据格式同IMEI
    private String rSRP;//信号强度
    private String rSRQ;//参考信号接收功率（心跳事件有效，其它事件为0）
    private String sNR;//参考信号接收质量（心跳事件有效，其它事件为0）
    private String txPower;//信噪比（心跳事件有效，其它事件为0）
    private String pIC;//当前发射功率（心跳事件有效，其它事件为0）
    private String eCL;//服务小区物理小区识别码,范围：0-503（心跳事件有效，其它事件为0）
    private String cellID;//小区的最新增强覆盖等级，范围:0~2（心跳事件有效，其它事件为0）
    private String batvoltage1;//设备的小区号（心跳事件有效，其它事件为0）
    private String battery_value;//0.1v为单位，主电池电压，如0x1E，表示3.0V,使用市电产品传输数据0xFF
    private String temperature;//电池电量，取值范围 : 1-100，单位（X %）
    private String humidness;//温度，单位:摄氏度。有符号字符型，范围-127~127℃。无温度传感器的产品传输数据0x80
    private String status;//湿度，%（0-100)。无湿度传感器的产品传输数据0x00
    /**
     * 0x00无事件；
     * 0x01心跳；
     * 0x02报警；
     * 0x03报警恢复；
     * 0x04防拆报警；
     * 0x05防拆报警恢复；
     * 0x06电池低压；
     * 0x07电池电压恢复；
     * 0x08电池2低压(如果有双电池)；
     * 0x09电池2电压恢复；
     * 0x0a自检；
     * 0x0b通讯板自检；
     * 0x0c内部通讯异常；
     * 0x0d内部通讯恢复；
     * 0x0e探测器故障；
     * 0x0f探测器故障恢复；
     * 0x10温度报警（默认高温报警）
     * 0x11温度报警恢复；
     * 0x12气体高浓度报警；
     * 0x13人体红外探测（有人）；
     * 0x14人体红外探测（无人）；
     * 0x15：低温报警（温度设定下限）；
     * 0x16：湿度低报警
     * 0x17：湿度高报警
     * 0x18：湿度报警恢复
     * 0x20下发命令回复；
     * 0x21：回复设定报警温湿度下限（设定温度：第40字节，设定湿度：第41字节）
     * 0x22：回复设定报警温湿度上限（设定温度：第40字节，设定湿度：第41字节）"
     */
    private String evt;//当前触发事件
    private String err_evt;//上次通讯失败的故障代码。暂无，发0
    private String iCCID;//设备卡的ICCID
    private String softversion;//软件版本（如V1.0，为0x10)
    private String hardversion;//硬件版本（如V1.0，为0x10)
    private String subNum;//子设备编号(当为主机时使用，如为NB设备本身为：0x00)
    private String reserved;//保留2个字节（0x0000)
    private String cRC16;//CRC16校验，该校验是整个数据包的校验，见CRC算法

    private String waterVal;//水压值
}


