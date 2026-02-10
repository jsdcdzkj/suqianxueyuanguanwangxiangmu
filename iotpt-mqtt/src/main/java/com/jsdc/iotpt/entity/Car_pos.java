package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 辆信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car_pos {

    private String name;
}
