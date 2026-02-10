package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 区域部门表(sys_area_dept)
 */
@Entity
@TableName("sys_area_dept")
@Table(name = "sys_area_dept")
@KeySequence(value = "SEQ_SYS_AREA_DEPT_ID")
@Data
@ApiModel(value = "区域部门")
public class SysAreaDept extends Model<SysAreaDept> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "areaDeptId", sequenceName = "SEQ_SYS_AREA_DEPT_ID", allocationSize = 1)
    @GeneratedValue(generator = "areaDeptId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    //部门Id
    @ApiModelProperty(value = "部门Id")
    private Integer deptId;
    //区域Id
    @ApiModelProperty(value = "区域Id")
    private Integer areaId;

}
