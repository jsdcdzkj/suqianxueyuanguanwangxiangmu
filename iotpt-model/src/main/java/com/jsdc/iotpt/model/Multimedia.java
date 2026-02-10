package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 多媒体 假model用于创建场景方案时使用
 *
 * @Author：jxl
 * @Date：2024/6/14 17:25
 * @FileDesc：
 */
@Entity
@TableName("MULTIMEDIA")
@Table(name = "MULTIMEDIA")
@KeySequence(value = "SEQ_MULTIMEDIA_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Multimedia extends Model<Multimedia> {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @SequenceGenerator(name = "multimediaId", sequenceName = "SEQ_MULTIMEDIA_ID", allocationSize = 1)
    @GeneratedValue(generator = "multimediaId", strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer meetingId;

    private String name;

    private String mediaIp;

    private String mediaPort;
    private String meetingName;
    @ApiModelProperty(value = "是否引用 1是0否")
    private Integer isReference;

    // 0 大屏
    private String meidaType;

    private Integer isDel;

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
}
