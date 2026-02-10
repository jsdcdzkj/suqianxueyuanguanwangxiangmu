package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: SysMsgSetting
 * Description:
 * date: 2024/11/26 8:50
 *
 * @author bn
 */
@Entity
@TableName("SYS_MSG_SETTING")
@Table(name = "SYS_MSG_SETTING")
@KeySequence(value = "SEQ_SYS_MSG_SETTING_ID")
@Data
@ApiModel(value = "系统消息提送")
public class SysMsgSetting extends Model<SysMsgSetting> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_MSG_SETTING_ID", sequenceName = "SEQ_SYS_MSG_SETTING_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_MSG_SETTING_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "id")
    private Integer id;

    // 推送用户
    private Integer userId;

    // 是否接收消息 0拒绝，1 接收
    private Integer isMsg;

    // 是否接收系统设置消息 0拒绝，1接收
    private Integer isSys;

    // 是否接收协同办公 0拒绝，1接收
    private Integer isOff;

    // 是否接收itss消息，
    private Integer isItss;

    // 是否接收用车
    private Integer isCar;

    private Integer isRfid;

    // 接收开始时间
    private String startTime;

    // 接收结束时间
    private String endTime;


}
