package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.AppMessageConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: SysMessage
 * Description: 系统消息
 * date: 2024/11/20 16:45
 *
 * @author bn
 */
@Entity
@TableName("SYS_MESSAGE")
@Table(name = "SYS_MESSAGE")
@KeySequence(value = "SEQ_SYS_MESSAGE_ID")
@Data
@ApiModel(value = "系统消息提送")
public class SysMessage  extends Model<SysMessage> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_MESSAGE_ID", sequenceName = "SEQ_SYS_MESSAGE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_MESSAGE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "id")
    private Integer id;

    // 消息类型 1.系统通知，2.itss，3.固定资产,4.办公 5.用车
    private String msgType;

    // 菜单字典id
    private Integer menuId;

    // 表头
    private String title;

    // 详情id
    private String viewId;

    // 内容
    private String context;

    // 未读：0 已读1
    private Integer isRead;

    // 推送人
    private Integer pushUser;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // 是否删除
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    private AppMessageConfig appMessageConfig;

}
