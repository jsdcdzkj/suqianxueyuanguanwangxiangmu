package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@TableName("daping_config")
@Table(name = "daping_config")
@KeySequence(value = "SEQ_DAPING_CONFIG_ID")
@Data
@ApiModel(value ="门禁设备")
public class DaPingConfig extends Model<DaPingConfig> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_DAPING_CONFIG_ID", sequenceName = "SEQ_DAPING_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DAPING_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="数据服务id")
    private Integer id;

    @ApiModelProperty(value ="中文标题")
    private String chineseTitle;

    @ApiModelProperty(value ="英文标题")
    private String englishTitle;

    @ApiModelProperty(value ="背景图")
    private String backgroundImage;

    @ApiModelProperty(value ="备用")
    private String spare;

    @ApiModelProperty(value ="是否删除 0:正常  1：删除")
    private Integer isDel;

}
