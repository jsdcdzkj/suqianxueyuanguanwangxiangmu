package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 黑/白 名单
 *
 * @author wangYan
 * @date 2023/10/13
 */
@Entity
@TableName("NAME_LIST")
@Table(name = "NAME_LIST")
@KeySequence(value = "NAME_LIST_ID")
@Data
@ApiModel(value = "黑/白名单")
public class NameList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "NAME_LIST_ID", sequenceName = "NAME_LIST_ID", allocationSize = 1)
    @GeneratedValue(generator = "NAME_LIST_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "名单id")
    private Integer id;
    @ApiModelProperty(value = "类型 1.黑名单 2.白名单")
    private Integer type;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "性别 1：男 2：女")
    private Integer sex;
    @ApiModelProperty(value = "门禁ID")
    private String doorIds;
    @ApiModelProperty(value = "备注")
    private String memo;
    @ApiModelProperty(value = "证件号")
    private String cardId;
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


    @ApiModelProperty(value = "人像注册库id")
    private Integer groupid;


    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "大华id")
    private Integer iccUserId;
    /**
     * 是否启用 0:禁用  1：启用
     */
    @ApiModelProperty(value = "是否启用")
    private Integer isEnable;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    private String doorName;

    @Transient
    @TableField(exist = false)
    private Integer stranger;

    @Transient
    @TableField(exist = false)
    private Integer pageIndex;
    @Transient
    @TableField(exist = false)
    private Integer pageSize;
    //黑名单图片i
    @Transient
    @TableField(exist = false)
    private List<Integer> fileIds;
    @Transient
    @TableField(exist = false)
    private String fileUrl;

    @Transient
    @TableField(exist = false)
    private String fileOld;
    @Transient
    @TableField(exist = false)
    private String fileNew;
}
