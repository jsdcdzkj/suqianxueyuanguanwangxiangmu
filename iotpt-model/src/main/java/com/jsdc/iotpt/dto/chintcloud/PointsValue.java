package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

import java.util.List;

@Data
public class PointsValue {

    private List<String> pointIds;

    private String value;

}
