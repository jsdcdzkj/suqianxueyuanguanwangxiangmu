package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author ：苹果
 * @Description：人员问卷
 * @Date ：2025/1/8 16:14
 * @Modified By：
 */
@Data
@Accessors
@Entity
@TableName("PERSON_QUESTION")
@Table(name = "PERSON_QUESTION")
@KeySequence(value = "SEQ_PERSON_QUESTION_ID")
public class PersonQuestion extends Model<PersonQuestion> implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_PERSON_QUESTION_ID", sequenceName = "SEQ_PERSON_QUESTION_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_PERSON_QUESTION_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "人员问卷id")
    private Integer id;

    @ApiModelProperty(value = "调查问卷 id")
    private String questionId;

    @ApiModelProperty(value = "答题人")
    private String userId;

    @ApiModelProperty(value = "部门")
    private String deptId;

    @ApiModelProperty(value = "答题人编号")
    private String userCode;

    @ApiModelProperty(value = "是否答题  0没做1做")
    private String isFinish;

    @ApiModelProperty(value = "答案json")
    private String details;

    @ApiModelProperty(value = "删除")
    private String isDel;

    @ApiModelProperty(value = "提交时间")
    private String uploadTime;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "参与单位")
    private String orgName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "所属部门")
    private String deptName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "人员名称")
    private String userName;
}
