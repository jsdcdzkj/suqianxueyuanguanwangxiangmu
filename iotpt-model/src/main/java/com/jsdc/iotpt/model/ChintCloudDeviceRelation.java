package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 会议 人员创建组 成员
 */
@Entity
@TableName("CHINT_CLOUD_DEVICE_RELATION")
@Table(name = "CHINT_CLOUD_DEVICE_RELATION")
@KeySequence(value = "CHINT_CLOUD_DEVICE_RELATION_ID")
@Data
public class ChintCloudDeviceRelation extends Model<ChintCloudDeviceRelation> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "CHINT_CLOUD_DEVICE_RELATION_ID", sequenceName = "CHINT_CLOUD_DEVICE_RELATION_ID", allocationSize = 1)
    @GeneratedValue(generator = "CHINT_CLOUD_DEVICE_RELATION_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "采集终端ID")
    private Integer deviceCollectId;

    /**
     * 网关点位查数id，注意：查询点位的实时值、历史值还有做点位控制用这个id，所以一般要取这个id。注意！
     */
    @ApiModelProperty(value = "网关点位查数id")
    private String storagePoint;

    /**
     * 网关设备id
     */
    @ApiModelProperty(value = "网关设备id")
    private String deviceId;

}
