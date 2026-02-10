package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.PatrolData;
import lombok.Data;

/**
 * ClassName: PatrolDataVo
 * Description:
 * date: 2024/11/28 9:59
 *
 * @author bn
 */
@Data
public class PatrolDataVo extends PatrolData {

    private Integer pageIndex;

    private Integer pageSize;
}
