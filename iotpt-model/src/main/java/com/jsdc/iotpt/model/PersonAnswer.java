package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @Description：人员答案
 * @Date ：2025/1/8 16:14
 * @Modified By：
 */
@Data
@Accessors
@Entity
@TableName("PERSON_ANSWER")
@Table(name = "PERSON_ANSWER")
@KeySequence(value = "SEQ_PERSON_ANSWER_ID")
public class PersonAnswer  extends Model<PersonAnswer> implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_PERSON_ANSWER_ID", sequenceName = "SEQ_PERSON_ANSWER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_PERSON_ANSWER_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "人员答案id")
    private Integer id;

    @ApiModelProperty(value = "调查问卷 id")
    private String questionId;

    @ApiModelProperty(value = "题目 id")
    private String subjectId;

    @ApiModelProperty(value = "排序")
    private String sort;

    @ApiModelProperty(value = "答案详情")
    private String answer;

    @ApiModelProperty(value = "答题人")
    private String userId;

    @ApiModelProperty(value = "部门")
    private String deptId;

    @ApiModelProperty(value = "答题人编号")
    private String userCode;

    @ApiModelProperty(value = "答案id")
    private String answerId;

    @ApiModelProperty(value = "删除")
    private String isDel;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "修改人")
    private String updateUser;
}
