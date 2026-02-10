package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigTemplateField;
import lombok.Data;

@Data
public class ConfigTemplateFieldVo extends ConfigTemplateField {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
