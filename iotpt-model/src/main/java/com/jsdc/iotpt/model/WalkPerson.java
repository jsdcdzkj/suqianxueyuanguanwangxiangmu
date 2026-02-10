package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人脸业务库
 */
@TableName("walk_person")
@Entity
@Table(name = "walk_person")
@Data
public class WalkPerson  implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_PERSON_ID", sequenceName = "SEQ_WALK_PERSON_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_PERSON_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    // 用户真实姓名
    private String realName;

    // 年龄
    private Integer age;

    // 起始年龄
    @Transient
    @TableField(exist = false)
    private Integer startAge;

    // 结束年龄
    @Transient
    @TableField(exist = false)
    private Integer endAge;

    // 起始日期
    @Transient
    @TableField(exist = false)
    private String startTime;

    // 结束日期
    @Transient
    @TableField(exist = false)
    private String endTime;

    // 人脸图片路径
    @Transient
    @TableField(exist = false)
    private String imgPath;

    @Transient
    @TableField(exist = false)
    private String usertypeEnname;

    // 电话
    private String phone;

    // 手机
    private String mobile;

    // 归属科室
    private Integer deptId;

    // 用户类型 10
    private String userType;

    // 性别：0：男，1：女
    private Integer sex;

    // 身份证号
    private String idCard;

    // 逻辑删除
    private Integer isDelete;

    // 入库时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 备注信息
    private String remarks;

    // 人脸id
    private String faceId;

    // 人脸token
    private String faceToken;

    //人脸BASE64编码
    private String faceBase64;

    //是否预警 0:不预警 1:预警
    private Integer isWarning;

    //是否是内部人员 1是
    private Integer isInsider;

    //部门ID
    private Integer sectionId;

    @Transient
    @TableField(exist = false)
    private Integer count;

}
