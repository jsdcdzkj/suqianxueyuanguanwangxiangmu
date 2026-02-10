package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@TableName("ALARM_GROUP")
@Table(name = "ALARM_GROUP")
@KeySequence(value = "SEQ_ALARM_GROUP_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警分组")
public class AlarmGroup {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_GROUP_ID", sequenceName = "SEQ_ALARM_GROUP_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_GROUP_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "分组编号")
    private String code;

    @ApiModelProperty(value = "分组名称")
    private String name;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer updateUser;

}
