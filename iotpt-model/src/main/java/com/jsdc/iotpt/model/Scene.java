package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@TableName("SCENE")
@Table(name = "SCENE")
@KeySequence(value = "SCENE_ID")
@Data
@Deprecated
public class Scene extends Model<Scene> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "sceneId", sequenceName = "SCENE_ID", allocationSize = 1)
    @GeneratedValue(generator = "sceneId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ApiModelProperty(value = "逻辑位置-所属区域")
    private Integer logicalAreaId;

    @ApiModelProperty(value = "逻辑位置-所属楼层")
    private Integer logicalFloorId;

    @ApiModelProperty(value = "逻辑位置-所属楼宇")
    private Integer logicalBuildId;

    //场景名称
    private String sceneName;
    //场景编码
    private String sceneCode;

    //是否默认场景  0否1是
    private String isCheck;

    //场景描述
    private String memo;
    //预启动时间
    private Integer preStartTime; //单位分钟
    /**
     * 会议室id
     */
    private Integer roomId;


    @Transient
    @TableField(exist = false)
    private List<SceneDevice> sceneDeviceList ;




    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
