package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.AppMessageConfig;
import lombok.Data;

/**
 * AppMessageConfigVo
 *
 * @author 许森森
 * @date 2024/11/18
 */
@Data
public class AppMessageConfigVo extends AppMessageConfig {

    private Integer pageIndex;

    private Integer pageSize;

}
