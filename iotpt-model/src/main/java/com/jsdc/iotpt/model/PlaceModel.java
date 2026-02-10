package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: PlaceModel
 * Description: 巡检点位
 * date: 2024/11/25 13:44
 *
 * @author bn
 */
@Entity
@TableName("PLACE_MODEL")
@Table(name = "PLACE_MODEL")
@KeySequence(value = "PLACE_MODEL_ID")
@Data
public class PlaceModel extends Model<PlaceModel> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PLACE_MODEL_ID", sequenceName = "PLACE_MODEL_ID", allocationSize = 1)
    @GeneratedValue(generator = "PLACE_MODEL_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "id")
    private Integer id;

    // 点位名称
    private String checkPointName;

    // 部门名称
    private String departMentName;

    // 部门编号
    private String departMentNumber;

    // 点位编号
    private Integer checkPointNumber;

    // 点位钮号
    private String checkPointCard;

    private Integer isDel;
}
