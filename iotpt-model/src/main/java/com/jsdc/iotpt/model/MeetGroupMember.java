package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 会议 人员创建组 成员
 */
@Entity
@TableName("MEETGROUPMEMBER")
@Table(name = "MEETGROUPMEMBER")
@KeySequence(value = "MEET_GROUP_MEMBER_ID")
@Data
public class MeetGroupMember extends Model<MeetGroupMember> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "MEETGROUPMEMBERID", sequenceName = "MEET_GROUP_MEMBER_ID", allocationSize = 1)
    @GeneratedValue(generator = "MEETGROUPMEMBERID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "成员子表id")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "组id")
    private Integer groupId;

}
