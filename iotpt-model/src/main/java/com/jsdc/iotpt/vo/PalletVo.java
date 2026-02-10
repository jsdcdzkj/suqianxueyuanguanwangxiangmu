package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigSignalType;
import lombok.Data;

import java.util.List;

@Data
public class PalletVo {

    private String device_name;
    private Integer device_id;
    private List<ConfigSignalType> list;

}
