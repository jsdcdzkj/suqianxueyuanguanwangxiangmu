package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@TableName("SCENEDEVICE")
@Table(name = "SCENEDEVICE")
@KeySequence(value = "SCENEDEVICE_ID")
@Data
@Deprecated
public class SceneDevice extends Model<SceneDevice> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "scenedeviceId", sequenceName = "SCENEDEVICE_ID", allocationSize = 1)
    @GeneratedValue(generator = "scenedeviceId", strategy = GenerationType.SEQUENCE)
    private Integer id;


    //场景Id
    private Integer sceneId;


    //设备ID
    private String deviceId ;
    //设备名称
    private String name ;

    //类型名称
    private String deviceTypeName ;

    //开关状态
    private String status ; //0：关机；1：开机

    //模式
    private String pattern ;//模式 0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动


    //温度
    private int temperature ;

    //风速
    private String windSpeed ;//无效；1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档；


    //是否锁定 0否 1是
    private String isLock ; //0：不锁定；1：锁定



    //模式
    @Transient
    @TableField(exist = false)
    private List<ConfigSignalTypeItem> configSignalTypeItems ;


    //风速
    @Transient
    @TableField(exist = false)
    private List<ConfigSignalTypeItem> configSignalTypeItems2 ;


}
