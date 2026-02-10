package com.jsdc.iotpt.water;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaterUtils {

    /**
     * 设备上报主题：SENSOR/ould/{设备号}/report
     * 下发主题  ：SENSOR/ould/{设备号}/command
     */

    public static void main(String[] args) {

        //设备上报数据解析

        /**
         * @param base64UpData   设备数据
         * @return
         * batterys  电池电压 1 正常 0 低压
         * volvalue 电池百分比
         * dsignal  信号值
         * collectiontime  监测时间  2024-01-19 14:28:29
         * monitorname  监测名称  压力
         * dataunit  监测单位  MPa
         * data  监测值
         * wpstatus;   //压力或水位状态  0：水压正常   1:欠压警告    2:过压警告
         * wrstatus;  // 监测变化率状态  0：正常状态  1：设备变化率报警
         */
//        System.out.println(parse("AAAAAGQfAR1nZmABAQgCmrnTPsI="));


        List<Map<String,Object>> dataSheets=parse("AAAAAGQfAeol3mAAAQACmZl/Pj0=");

        System.out.println(dataSheets);
        for (Map<String, Object> item:dataSheets) {
            for (Map.Entry<String,Object> x:item.entrySet()){
                if (x.getKey().equals("batterys")||
                        x.getKey().equals("data")||
                        x.getKey().equals("wpstatus")||
                        x.getKey().equals("wrstatus")||
                        x.getKey().equals("volvalue")){


                    //if (x.getValue() instanceof Integer){
                    System.out.println(x.getKey());
                        System.out.println((Float.parseFloat(String.valueOf(x.getValue()))));

                    //}else if (x.getValue() instanceof String){
                      //  System.out.println(x.getKey());
                    //}


                }
            }
        }
        //生成下发数据
        /**
         *  type=0  设置下限
         *  type=1  设置上限
         *  dvalue 设置参数  KPa  1000
         */
        System.out.println(cmdGetCode(0, "1000"));
    }


    public static List<Map<String, Object>> parse(String base64UpData) {
        List<Map<String, Object>> swMonitorList = new ArrayList<>();
        String hexUpData = StringUtil.base64StrToHexStr(base64UpData);
        String fragmentTypeBinary = StringUtil.hexString2binaryString(DeviceUtils.buileWei(hexUpData, 1, 0));//帧类型
        Integer command = Integer.parseInt(fragmentTypeBinary.substring(0, 6), 2);//指令
        Integer error = Integer.parseInt(fragmentTypeBinary.substring(0, 7), 2);//错误类型, 0:正常,1:数据溢出,2:读写错误

        //序号, 主动数据上报，序号为0，非0：服务器端发起指令
        Integer serialNumber = Integer.parseInt(
                DeviceUtils.buileWei(hexUpData, 2, 3),
                16);

        if (command == 0 && serialNumber == 0 && error == 0) {
            String fragmentStatus = DeviceUtils.buileWei(hexUpData, 4, 0);//帧状态
            Integer voltage = Integer.parseInt(DeviceUtils.buileWei(hexUpData, 5, 0), 16);//电量
            Integer rsrp = Integer.parseInt(DeviceUtils.buileWei(hexUpData, 6, 0), 16);//信号强度
            Integer recordNum = Integer.parseInt(DeviceUtils.buileWei(hexUpData, 7, 0), 16);//记录数

            int recordIndex = 8; //记录读取位置，起始位置为第8个字节
            final int gwsIndexOfRecord = 5; //记录中工位数的位置
            final int timeLengthOfRecord = 4; //记录中时间的长度（字节）
            final int warnOfRecord = 1; //记录中报警状态的长度（字节）
            final int gwsOfRecord = 1; //记录中工位数的长度（字节）
//                int readIndex = 0;//数据的读取位置
            for (int i = 0; i < recordNum; i++) {
                //当前记录的工位数
                int gws = Integer.parseInt(DeviceUtils.buileWei(hexUpData, recordIndex + gwsIndexOfRecord, 0), 16);
                String tims = DeviceUtils.buileWei(hexUpData, recordIndex, recordIndex + 3);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateTime cotime = DateTime.parse(AepHttpDeviceEncoder.aesTime(tims), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).plusHours(8);

                //读取位置移动到工位数据的开始位置
                recordIndex = recordIndex + timeLengthOfRecord + warnOfRecord + gwsOfRecord;

                for (int j = 0; j < gws; j++) {
                    String alarm = DeviceUtils.buileWei(hexUpData, recordIndex, 0);
                    recordIndex = recordIndex + 1;//向右移动1个字节，读取单位
                    String unit = DeviceUtils.buileWei(hexUpData, recordIndex, 0);
                    recordIndex = recordIndex + 1;//向右移动1个字节，读取数据
                    String value = DeviceUtils.buileWei(hexUpData, recordIndex, recordIndex + 3);
                    recordIndex = recordIndex + 3;//读完取数据，向右移动4个字节

//                        0000 0001  失效
//                        0000 0010  Alarm Dowm
//                        0000 0100  Alarm Up
//                        0000 1000  Alarm Delta

                    Map<String, Object> swMonitor = new HashMap<>();
//                        8:差值过大，4:高于上限，2:低于下限，1:传感器故障
                    swMonitor.put("collectiontime", cotime.toString("yyyy-MM-dd HH:mm:ss"));
                    swMonitor.put("wpstatus", 0);
                    swMonitor.put("wrstatus", 0);
                    if (StringUtil.isEqual(alarm, "08")) {//differ
                        swMonitor.put("wrstatus", 1);
                    } else if (StringUtil.isEqual(alarm, "04")) {//over
                        swMonitor.put("wpstatus", 2);//过压
                    } else if (StringUtil.isEqual(alarm, "02")) {//under
                        swMonitor.put("wpstatus", 1);//欠压
                    } else if (StringUtil.isEqual(alarm, "01")) {//lost
                        swMonitor.put("wpstatus", 3);
                    }


                    if (StringUtil.isEqual(unit, "00")) {//M
                        swMonitor.put("monitorname", "水位");
                        swMonitor.put("dataunit", "米");
                        swMonitor.put("data", String.format("%.2f", FloatUtils.base64ToFloat(value, false, 2)));
                    }

                    if (StringUtil.isEqual(unit, "01")) {//KPa

                        swMonitor.put("monitorname", "压力");
                        swMonitor.put("dataunit", "KPa");
                        swMonitor.put("data", String.format("%.2f", FloatUtils.base64ToFloat(value, false, 2)));

                    }

                    if (StringUtil.isEqual(unit, "02")) {//MPa

                        swMonitor.put("monitorname", "压力");
                        swMonitor.put("dataunit", "MPa");
                        swMonitor.put("data", String.format("%.2f", FloatUtils.base64ToFloat(value, false, 2)));

                    }

                    if (StringUtil.isEqual(unit, "05")) {//bar 1bar = 0.1Mpa
                        swMonitor.put("monitorname", "压力");
                        swMonitor.put("dataunit", "Mpa");
                        swMonitor.put("data", String.format("%.2f", FloatUtils.base64ToFloat(value, false, 2) * 0.1f));

                    }

                    swMonitor.put("batterys", 1);
                    swMonitor.put("volvalue", voltage);
                    swMonitor.put("dsignal", rsrp);
                    swMonitorList.add(swMonitor);
                }
                recordIndex = recordIndex + 1;//读完记录中的工位数据，向右移动1个字节，读取下一条记录

            }
        }
        return swMonitorList;
    }


    public static String cmdGetCode(Integer type, String dvalue) {
        String cmdData = "";
        if (type == 0) {//设置下限
            Float value = Float.parseFloat(dvalue);
            cmdData = AepHttpDeviceEncoder.getBuilder().setParams(AepHttpDeviceEncoder.ParamsConfig.Alarm_Down).serialNumber(10).value(value).getBase64String();
        } else if (type == 1) {//设置上限
            Float value = Float.parseFloat(dvalue);
            cmdData = AepHttpDeviceEncoder.getBuilder().setParams(AepHttpDeviceEncoder.ParamsConfig.Alarm_Upper).serialNumber(11).value(value).getBase64String();
        }
        return cmdData;
    }

}