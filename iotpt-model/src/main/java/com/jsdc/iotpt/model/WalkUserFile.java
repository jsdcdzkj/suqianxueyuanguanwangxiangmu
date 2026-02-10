package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *  持久化人脸库，非法人脸库对应的照片
 */
@TableName("walk_user_file")
@Entity
@Table(name = "walk_user_file")
@Data
public class WalkUserFile {
    /**
     * 自增id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_USER_FILE_ID", sequenceName = "SEQ_WALK_USER_FILE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_USER_FILE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 人脸id
     */
    private String faceId;

    /**
     * 图片路径
     */
    private String filePath;

    /**
     *  抓拍次数 门禁事件,人脸检测事件 出现非法人员库的时候用到这个
     */
    private Integer warnCount;

    /**
     *  创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
