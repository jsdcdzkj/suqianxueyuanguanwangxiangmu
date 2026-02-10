package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@TableName("APP_VERSION_INFO")
@Table(name = "APP_VERSION_INFO")
@KeySequence(value = "SEQ_APP_VERSION_INFO_ID")
@Data
public class AppVersionInfo extends Model<AppVersionInfo> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_APP_VERSION_INFO_ID", sequenceName = "SEQ_APP_VERSION_INFO_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_APP_VERSION_INFO_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "app版本更新")
    private Integer id;

    //url
    @ApiModelProperty(value = "url")
    private String url;
    //更新内容
    @ApiModelProperty(value = "更新内容")
    private String updateContent;
    //版本号
    @ApiModelProperty(value = "版本号")
    private String version;

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

}
