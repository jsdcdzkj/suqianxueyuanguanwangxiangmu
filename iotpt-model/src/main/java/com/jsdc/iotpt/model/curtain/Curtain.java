package com.jsdc.iotpt.model.curtain;

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
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-26 08:41
 **/
@Entity
@TableName("curtain")
@Table(name = "curtain")
@KeySequence(value = "SEQ_CURTAIN_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "窗帘表")
public class Curtain extends Model<Curtain> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CURTAIN_ID", sequenceName = "SEQ_CURTAIN_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CURTAIN_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "窗帘id")
    private Integer id;
    /**
     * 所属楼宇
     * 关联楼宇表id
     */
    @ApiModelProperty(value = "所属楼宇")
    private Integer buildId;

    /**
     * 所属楼层
     * 关联楼层表id
     */
    @ApiModelProperty(value = "所属楼层")
    private Integer floorId;

    /**
     * 物理位置
     * 所属区域
     * 关联区域表id
     */
    @ApiModelProperty(value = "物理位置")
    private Integer areaId;

    @ApiModelProperty(value = "窗帘名称")
    private String curtainName;

    @ApiModelProperty(value = "窗帘所属IP")
    private String ip;
    @ApiModelProperty(value = "窗帘所属端口")
    private String curtainPort;
    @ApiModelProperty(value = "升")
    private String up;

    @ApiModelProperty(value = "降")
    private String down;

    @ApiModelProperty(value = "停")
    private String stop;

    @ApiModelProperty(value = "是否总控 1是 0否")
    private Integer generalControl;
    /**
     * 逻辑位置
     * 所属区域
     * 关联区域表id
     */
    @ApiModelProperty(value = "逻辑位置-所属区域")
    private Integer logicalAreaId;
    @ApiModelProperty(value = "逻辑位置-所属楼层")
    private Integer logicalFloorId;
    @ApiModelProperty(value = "逻辑位置-所属楼宇")
    private Integer logicalBuildId;

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

    @ApiModelProperty(value = "是否删除 1是0否")
    private Integer isDel;

}
