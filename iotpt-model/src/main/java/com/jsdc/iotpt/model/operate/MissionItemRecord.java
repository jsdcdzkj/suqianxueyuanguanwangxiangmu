package com.jsdc.iotpt.model.operate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: mission_item_record
 * @author: zln
 * @description: 我的任务—任务项记录
 * @date: 2023/8/23 13:55
 */
@Entity
@TableName("MISSION_ITEM_RECORD")
@Table(name = "MISSION_ITEM_RECORD")
@KeySequence(value = "SEQ_MISSION_ITEM_RECORD_ID")
@Data
@ApiModel(value = "任务项记录")
public class MissionItemRecord implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "itemRecordId", sequenceName = "SEQ_MISSION_ITEM_RECORD_ID", allocationSize = 1)
    @GeneratedValue(generator = "itemRecordId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "任务项记录ID")
    private Integer id;
    //关联任务id
    @ApiModelProperty(value = "任务ID", required = true)
    private Integer missionId;
    @ApiModelProperty(value = "所属区域ID", required = true)
    private Integer region;
    //严重程度
    @ApiModelProperty(value = "严重程度", required = true)
    private Integer levels;
    @ApiModelProperty(value = "模板ID", required = true)
    private Integer checkTemplateId;
    @ApiModelProperty(value = "检查项ID", required = true)
    private Integer checkTemplateSubId;
    @ApiModelProperty(value = "检查项内容", required = true)
    private String subContent;
    //1、正常  2、异常(必须上传异常图片)
    @ApiModelProperty(value = "执行结果", required = true)
    private Integer execution;
    @ApiModelProperty(value = "备注信息")
    private String description;
    @ApiModelProperty(value = "设备ID")
    private Integer deviceId;
    @ApiModelProperty(value = "设备类型")
    private Integer deviceType;
    //1、采集设备 2、视频设备 3、网关设备
    @ApiModelProperty(value = "类型标识")
    private Integer types;
    @ApiModelProperty(value = "异常设备名称")
    private String deviceName;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;
    //0、未处理  1、已处理
    @ApiModelProperty(value = "是否处理")
    private Integer isHandle;
    //多个图片
    @Transient
    @TableField(exist = false)
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private List<SysFile> files;

    @Transient
    @TableField(exist = false)
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private List<String> fileList;

    //详细地址
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String detailAddress;
    //商铺名称
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String regionName;
    //电话
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String phones;
    //严重程度名称
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String levelsName;
}
