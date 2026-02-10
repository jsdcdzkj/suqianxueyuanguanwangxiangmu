package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkActionLocus;
import com.jsdc.iotpt.model.WalkRetentionRules;
import lombok.Data;


@Data
public class WalkRetentionRulesVo extends WalkRetentionRules {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
