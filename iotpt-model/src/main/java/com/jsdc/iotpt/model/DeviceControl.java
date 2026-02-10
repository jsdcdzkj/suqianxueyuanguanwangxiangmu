package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: DeviceControl
 * Description:
 * date: 2023/12/20 13:41
 *
 * @author bn
 */
@Entity
@TableName("DEVICE_CONTROL_ABC")
@Table(name = "DEVICE_CONTROL_ABC")
@KeySequence(value = "SEQ_DEVICE_CONTROL_ABC_ID")
@Data
@ApiModel(value = "门禁设备")
public class DeviceControl implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_DEVICE_CONTROL_ABC_ID", sequenceName = "SEQ_DEVICE_CONTROL_ABC_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DEVICE_CONTROL_ABC_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;


    // 采集设备id
    private Integer collectId;

    // 设备类型id
    private Integer typeId;

    // 信号id
    private Integer signalId;

    // 设备状态 默认-1
    private String deviceState;

    // 删除标志
    private Integer isDel;

}
