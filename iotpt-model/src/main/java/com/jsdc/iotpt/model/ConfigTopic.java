package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @authon thr
 * @describe 主题管理
 */
@Entity
@TableName("config_topic")
@Table(name = "config_topic")
@KeySequence(value = "SEQ_CONFIG_TOPIC_ID")
@Data
@ApiModel(value = "主题管理")
public class ConfigTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configTopicId", sequenceName = "SEQ_CONFIG_TOPIC_ID", allocationSize = 1)
    @GeneratedValue(generator = "configTopicId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主题管理id")
    private Integer id;
    //
    @ApiModelProperty(value = "主题Topic")
    private String topicName;
    @ApiModelProperty(value = "主题名称")
    private String topicCode;
    //主题类型  1：订阅主题  2：发布主题
    @ApiModelProperty(value = "主题类型")
    private Integer topicType;
    //
    @ApiModelProperty(value = "主题描述")
    private String topicDesc;
    //主题状态 0：禁用 1:启用
    @ApiModelProperty(value = "主题状态")
    private Integer topicStatus;

    @ApiModelProperty(value = "转换模板表的主键")
    private Integer transferId;

    @ApiModelProperty(value = "连接表的主键")
    private Integer linkId;

    /**
     * 类型：1数据 2告警 3心跳
     */
    @ApiModelProperty(value = "数据识别码")
    private String templateKey;
    @ApiModelProperty(value = "数据码值")
    private String dataVal;
    @ApiModelProperty(value = "告警码值")
    private String alarmVal;
    @ApiModelProperty(value = "心跳码值")
    private String heartVal;
    @ApiModelProperty(value = "控制码值")
    private String controlVal;
    @ApiModelProperty(value = "变更码值")
    private String changeVal;


    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;


}
