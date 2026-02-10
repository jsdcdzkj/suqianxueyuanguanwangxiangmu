package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.curtain.Curtain;
import lombok.Data;

/**
 * ClassName: CurtainVo
 * Description:
 * date: 2024/11/28 8:43
 *
 * @author bn
 */
@Data
public class CurtainVo extends Curtain {

    // 控制类型 0升 1 停 2 降
    private Integer controlType;


    private Integer control_type;

    private Integer area_id;

    private Integer curtain_id;

    private Integer all;

}
