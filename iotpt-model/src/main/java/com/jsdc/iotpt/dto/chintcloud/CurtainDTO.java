package com.jsdc.iotpt.dto.chintcloud;

import com.jsdc.iotpt.model.curtain.Curtain;
import lombok.Data;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-26 09:58
 **/
@Data
public class CurtainDTO extends Curtain {
    private Integer pageIndex;
    private Integer pageSize;
}
