package com.jsdc.iotpt.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InformationBulletinVo{
    @ApiModelProperty(value = "资讯id")
    private Integer id;

    /**
     * 公告标题
     */
    @ApiModelProperty(value = "公告标题")
    private String title;

    /**
     * 公告内容
     */
    @ApiModelProperty(value = "公告内容")
    private String content;
    /**
     * 公告状态 0：未发布 1：已发布
     */
    @ApiModelProperty(value = "公告状态 0：未发布 1：已发布")
    private Integer status;

    /**
     * 公告类型 0：系统公告 1：餐饮公告 2.资讯 3:新闻 4:管理制度 5:内刊
     */
    @ApiModelProperty(value = "公告类型 0：系统公告 1：餐饮公告 2.资讯 3:新闻 4:管理制度 5:内刊")
    private String informationBulletinType;

    /**
     * 展现人群
     */
    @ApiModelProperty(value = "展现人群 第一版本 写死内部员工")
    private String releaseUnit;


    /**
     * 公告开始时间
     */
    @ApiModelProperty(value = "公告开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String releaseStartTime;

    /**
     * 公告结束时间
     */
    @ApiModelProperty(value = "公告结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String releaseEndTime;


    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;
}
