package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@TableName("GROUND_LOCKS_IMG")
@Table(name = "GROUND_LOCKS_IMG")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value = "地锁图片")
public class GroundLocksImg implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "地锁图片id")
    private String id;

    @ApiModelProperty(value = "serial")
    private String serial;

    @ApiModelProperty(value = "image")
    private String image;

    @ApiModelProperty(value = "image_length")
    private Integer image_length;

    @ApiModelProperty(value = "image_send_flag")
    private Integer image_send_flag;

    @ApiModelProperty(value = "img_key")
    private String img_key;

    @ApiModelProperty(value = "type")
    private Integer type;

    @ApiModelProperty(value = "reco_id")
    private Integer reco_id;

    @ApiModelProperty(value = "sn")
    private String sn;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "dev_name")
    private String dev_name;
    /**
     * 车牌
     */
    private String plate;
    /**
     * 车位编号
     */
    private String stall_code;
    /**
     * 车位状态
     * 1：⼊场； 2：在场； 4：出场； 8：空场； 16：⻋位异常； 32：延迟上报出场； 64：合并出⼊场； 128：预⼊场； 256：预出场； 512：⼊场修正
     */
    private Integer parking_state;
    @Transient
    @TableField(exist = false)
    private String parking_state_name;
    // 左右车位 0.左,1右
    private Integer zone_id;
    /**
     * 识别时间
     */
    private String reco_time;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private String time_s;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 查询开始时间
     */
    @Transient
    @TableField(exist = false)
    private String startTime;
    /**
     * 查询结束时间
     */
    @Transient
    @TableField(exist = false)
    private String endTime;





    @Transient
    @TableField(exist = false)
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    private Integer pageSize ;

    // 预览地址
    @Transient
    @TableField(exist = false)
    private String previewUrl ;
    // 下载地址
    @Transient
    @TableField(exist = false)
    private String downUrl ;

}
