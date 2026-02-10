package com.jsdc.iotpt.vo;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;

@Data
public class AirConditionCMDVo implements Serializable {

    private int acType;  //0 格力(默认) ,1 大金空调
    //设备类型  网关类型（0：多联机控制器；1：热水机控制器；2：u-match；3：屋顶机；4：智慧控制器
    private Integer deviceType;//默认为4

    private Boolean softStartup;//是否软启动

    private Integer deviceId;
    //    //网关号
//    private int gatewayNum;
//    //网关mac
    private String gatewayMac;
//    //系统号
//    private int systemNum;
    private int indoorNum;//内机号

    // 空调控制类型-面板锁定
    private int acAirType;

    // 空调性质-1：员工，2：领导
    private Integer acAirProperties;

    // 内机标识= 网关编号-系统编号-内机号 = gatewayNum-systemNum-indoorNum
    //1-4-78
    private String innerMachineNum;
    private String name;
    private int scene;//场景联动 0无 1开启 2关闭
    private Integer onOff;//control_t31开关机 0：关机；1：开机
    private Integer lockLineControl;//control_t40锁定线控器，0：不锁定；1：锁定
    private Integer modal;//control_t51模式，0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动
    private Integer windSpeed;//control_t52 风速，无效；1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档；
    private Integer temp;//param_t1 温度设置，摄氏度（乘10下发）
    //此值最好不要传
    private Integer highWindSpeed;//control_t87 超高风速，0：不设置；1：设置超高风速
    private Integer energySwitch;//control_t33 节能开关 【0】关【1】开 ,如果是1则 制冷下限 制热上限 除湿下限 生效
    private Integer coolLowerLimit = 18;//param_t2 制冷下限  摄氏度（乘10下发）
    private Integer heatUpperLimit = 30;//param_t3 制热上限  摄氏度（乘10下发）
    private Integer dehumidificationLowerLimit =18 ;//param_t4 除湿下限 摄氏度（乘10下发）
    private JSONObject cmd;//根据不同的空调 组装不同的控制命令

    private String cmdStr;
    //用来记录组装控制命令的日志
    private JSONObject logStr;

    private String mainEquipment; // 大金空调是否是主机
    /**
     * <p>
     * <p>
     * {
     * "deviceType": 4,
     * "softStartup": true,
     * "params": {
     * "control_t31": 0,
     * "control_t40": 0,
     * "control_t51": 1,
     * "control_t52": 6,
     * "param_t1": 250,
     * "control_t32": 1
     * },
     * "gatewayNum": 1,
     * "gatewayMac": "da:fa:53:24:89:98",
     * "systemNum": 4,
     * "indoorNum": 78
     * }
     * </p>
     *
     * @return
     */
    public JSONObject getCmd() {
        JSONObject cmd = new JSONObject();

        if (this.getAcType() == 1){//
            return cmd;
        }

        if("".equals(StringUtils.trimToEmpty(getInnerMachineNum()))){
            return cmd;
        }
        if (null == getDeviceType()) {
            cmd.put("deviceType", 4);
        } else {
            cmd.put("deviceType", getDeviceType());
        }


        if (null == getSoftStartup()) {
            cmd.put("softStartup", true);
        } else {
            cmd.put("softStartup", getSoftStartup());
        }

        String[] indoorNums = getInnerMachineNum().split("-");

        cmd.put("gatewayNum", NumberUtils.toInt(indoorNums[0]));
        cmd.put("systemNum", NumberUtils.toInt(indoorNums[1]));
        cmd.put("indoorNum", NumberUtils.toInt(indoorNums[2]));
        if (null == getGatewayMac()) {
            cmd.put("gatewayMac", "da:fa:53:24:89:98");
        } else {
            cmd.put("gatewayMac", getGatewayMac());
        }

        JSONObject params = new JSONObject();
        if (null == getOnOff()) {
//            params.put("control_t31", 0); //非必填 不给默认值
        } else {
            params.put("control_t31", getOnOff());
        }

        if (null == getLockLineControl()) {
//            params.put("control_t40", 0);//非必填 不给默认值
        } else {
            params.put("control_t40", getLockLineControl());
        }

        if (null == getModal()) {
//            params.put("control_t51", 1);//非必填 不给默认值
        } else {
            params.put("control_t51", getModal());

        }

        if (null == getWindSpeed()) {
//            params.put("control_t52", 6);//非必填 不给默认值
        } else {
            params.put("control_t52", getWindSpeed());
        }
        if (null == getTemp()) {
//            params.put("param_t1", 250);//非必填 不给默认值
        } else {
            params.put("param_t1", getTemp() * 10);
        }

        if (null == getHighWindSpeed()) {
            //超高风速 默认值要给0(不设置)  如果给1 则风速(control_t52) 温度不起作用
           // params.put("control_t87", 0);
        } else {
            params.put("control_t87", getHighWindSpeed());
        }

        if(null !=  getEnergySwitch()){
            params.put("control_t33", getEnergySwitch());
        }

        if (null != getCoolLowerLimit()) {
            params.put("param_t2", getCoolLowerLimit() * 10);
        }

        if (null != getHeatUpperLimit()) {
            params.put("param_t3", getHeatUpperLimit() * 10);
        }

        if (null != getDehumidificationLowerLimit()) {
            params.put("param_t4", getDehumidificationLowerLimit() * 10);
        }

        cmd.put("params", params);

        return cmd;
    }


    public String getCmdStr() {
        return getCmd().toJSONString();
    }

    public static void main(String[] args) {
        AirConditionCMDVo vo = new AirConditionCMDVo();
        vo.setInnerMachineNum("1-4-88");
        System.out.println(vo.getCmd());
    }
}
