package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: JobPlanArea
 * Description:
 * date: 2023/8/24 9:50
 *
 * @author bn
 */
@Entity
@TableName("job_plan_area")
@Table(name = "job_plan_area")
@KeySequence(value = "SEQ_JOB_PLAN_AREA_ID")
@Data
@ApiModel(value = "巡检范围")
public class JobPlanArea implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "jobPlanAreaId", sequenceName = "SEQ_JOB_PLAN_AREA_ID", allocationSize = 1)
    @GeneratedValue(generator = "jobPlanAreaId", strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer planId;
    //楼宇id
    private Integer buildId;
    //楼层id
    private Integer floorId;
    //区域id
    private Integer areaId;
    // 删除标志
    private Integer isDel;

    // 用于前端回显
    private String type;

    // 用于前端回显
    private Integer realId;





}
