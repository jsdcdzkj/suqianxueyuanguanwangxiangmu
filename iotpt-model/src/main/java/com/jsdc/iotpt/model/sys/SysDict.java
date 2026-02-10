package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统字典
 *
 * @author wangYan
 * @since 2023-05-08
 */
@Entity
@TableName(value = "SYS_DICT")
@Table(name = "SYS_DICT")
@KeySequence(value = "SEQ_SYS_DICT_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统字典")
public class SysDict extends Model<SysDict> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_DICT_ID", sequenceName = "SEQ_SYS_DICT_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_DICT_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "系统字典id")
    private Integer id;
    /**
     * 父类id
     */
    @Comment("父类id")
    @ApiModelProperty(value = "父类id")
    private Integer parentId;
    /**
     * 字典值
     */
    @Comment("字典值")
    @ApiModelProperty(value = "字典值")
    private String dictValue;
    /**
     * 字典类型
     */
    @Comment("字典类型")
    @ApiModelProperty(value = "字典类型")
    private String dictType;
    /**
     * 字典类型名称
     */
    @Comment("字典类型名称")
    @ApiModelProperty(value = "字典类型名称")
    private String dictTypeName;
    /**
     * 字典名称
     */
    @Comment("字典名称")
    @ApiModelProperty(value = "字典名称")
    private String dictLabel;
    /**
     * 描述
     */
    @Comment("描述")
    @ApiModelProperty(value = "描述")
    private String memo;

    @Comment("颜色")
    @ApiModelProperty(value = "颜色")
    private String colour;

    private Integer sort;

    @Comment("创建人")
    private Integer createUser;
    @Comment("更新人")
    private Integer updateUser;
    @Comment("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Comment("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @Comment("是否删除 0:正常  1：删除")
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    private String parentName;

    @Transient
    @TableField(exist = false)
    private List<SysDict> children;
}
