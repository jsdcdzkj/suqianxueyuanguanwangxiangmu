package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 摄像机设备表
 */
@TableName("walk_equipment")
@Entity
@Table(name = "walk_equipment")
@Data
public class WalkEquipment  implements Serializable {
    /**
     * 自增id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_EQUIPMENT_ID", sequenceName = "SEQ_WALK_EQUIPMENT_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_EQUIPMENT_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 设备编码
     */
    private String equipmentCode;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 设备型号
     */
    private String equipmentModel;

    /**
     * 设备通道号
     */
    private String channel;

    /**
     *  NVR通道号
     */
    private Integer nvr_channel;

    // 窗口号
    private Integer window_num;

    /**
     * 设备安装地址
     */
    private String addressW;

    /**
     * 摄像机接入RTSP地址
     */
    private String addressF;

    /**
     * 设备经度
     */
    private String pointX;

    /**
     * 设备纬度
     */
    private String pointY;
    /**
     * 拍摄区域横坐标
     */
    private String catchX;
    /**
     * 拍摄区域纵坐标
     */
    private String catchY;

    /**
     * 设备ip
     */
    private String ip;

    /**
     * 设备端口
     */
    private Integer port;

    /**
     * 所属部门id
     */
    private Integer deptId;

    private Integer nvrId;

    /**
     * 厂家
     */
    private String manufactor;

    /**
     * 设备类型
     */
    private String equipmentType;

    /**
     * 所属区域
     */
    private Integer areaId;

    /**
     * 设备用途
     * 0:登记设备 1:离开设备 2.区域内监控 3.区域外监控 4.非人脸识别设备
     */
    private Integer useway;

    /**
     * 状态： 0未启用  1：已启用
     */
    private Integer status;

    /**
     * 状态： 0离线  1：在线
     */
    private Integer isOnline;

    /**
     * 逻辑删除标志 0：未删除   1：已删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     *  账户
     */
    private String equipmentAccount;

    /**
     *  密码
     */
    private String equipmentPwd;

    /**
     *  token
     */
    private String token;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Transient
    @TableField(exist = false)
    private Integer floor;

}
