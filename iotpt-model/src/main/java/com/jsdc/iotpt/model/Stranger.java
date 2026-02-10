package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName: DataService
 * Description: 数据服务
 * date: 2023/5/8 14:55
 *
 * @author zdq
 */
@Entity
@TableName("stranger")
@Table(name = "stranger")
@KeySequence(value = "SEQ_STRANGER_ID")
@Data
@ApiModel(value = "陌生人管理")
public class Stranger implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_STRANGER_ID", sequenceName = "SEQ_STRANGER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_STRANGER_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "数据服务id")
    private Integer id;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    //1：男 2：女
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "抓拍时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date captureTime;
    @ApiModelProperty(value = "抓拍位置")
    private String snapPosition;
    @ApiModelProperty(value = "抓拍次数")
    private Integer frequency;
    //1：忽略 2：正常
    @ApiModelProperty(value = "状态")
    private String states;
    @ApiModelProperty(value = "是否加入黑白名单")
    private Integer is_nameList;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;
    //开始时间
    @Transient
    @TableField(exist = false)
    private String startDate;
    //结束时间
    @Transient
    @TableField(exist = false)
    private String endDate;

    @Transient
    @TableField(exist = false)
    private List<String> fileList;
    //图片id
    @Transient
    @TableField(exist = false)
    private List<Integer> fileId;

    @Transient
    @TableField(exist = false)
    private String fileName;
}
