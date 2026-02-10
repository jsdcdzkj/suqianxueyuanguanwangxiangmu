package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@TableName("SYS_FILE")
@Table(name = "SYS_FILE")
@KeySequence(value = "SEQ_SYS_FILE_ID")
@Data
@ApiModel(value = "系统用户")
public class SysFile extends Model<SysFile> {
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_FILE_ID", sequenceName = "SEQ_SYS_FILE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_FILE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "系统用户id")
    private Integer id;
    @ApiModelProperty(value = "原文件名")
    private String fileName;//原文件名
    @ApiModelProperty(value = "文件类型")
    private String fileType;//文件类型
    @ApiModelProperty(value = "文件地址")
    private String fileUrl;
    @ApiModelProperty(value = "缩略图文件地址")
    private String thumbnailUrl;//如果是图片，则保存缩略图 20251021之后的才有此值

    @ApiModelProperty(value = "缩略图文件名称")
    private String thumbnailName;
    @ApiModelProperty(value = "原文件名")
    private String originalFilename;//minIO文件的原文件 20251021之后的才有此值
    @ApiModelProperty(value = "业务关联ID")
    private String bizId;//业务关联ID
    //1.楼宇图片 2.应急方案pdf 3、运维管理-我的任务-巡检计划异常图片  4、我的任务-上报图片 5、访客照片 6、门禁人脸图片 7、通行记录表图片
    // 8.上报报修 9.菜品图片 10.巡更任务图片 11、我的任务-其他异常处理的图片，12我的任务--上传文件 13.门禁记录，14、卫生间公告图片 15.包厢封面，
    // 16：告警视频文件 17 会议室图片  18 客户跟进记录图片  19招商轮播图 20 (房源-招商)户型图 21 租赁合同文件 22 组合收款 23 (房间-租赁)户型图  24出门条物品照品
    @ApiModelProperty(value = "业务类型")
    private String bizType;//业务类型
    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "是否删除 1是 0否")
    private Integer isDel;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;
    //设备类型的图片
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String filePath;

}
