package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: SysUser
 * @author: wr
 * @description: 系统用户表
 * @date: 2025/11/12 9:38
 */
@Entity
@TableName("SYS_LOGO")
@Table(name = "SYS_LOGO")
@KeySequence(value = "SEQ_SYS_LOGO_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统logo")
public class SysLogo extends Model<SysLogo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_USER_ID", sequenceName = "SEQ_SYS_USER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_USER_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "系统用户id")
    private Integer id;

    @ApiModelProperty(value = "logo名称")
    private String logoName;

    @ApiModelProperty(value = "logo地址")
    private String logoUrl;

    @ApiModelProperty(value = "默认logo")
    private String defaultLogoUrl;

    @ApiModelProperty(value = "备注")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;


}
