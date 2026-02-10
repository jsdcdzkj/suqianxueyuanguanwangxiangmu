package com.jsdc.iotpt.vo.operate;

import com.jsdc.iotpt.model.operate.MissionAssign;
import lombok.Data;


/**
 * 任务指派信息表
 */
@Data
public class MissionAssignVo extends MissionAssign {

    //任务类型名称
    private String taskTypeName;
    //紧急程度名称
    private String urgencyName;
    //任务组名称
    private String teamGroupsName;
}
