package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.PersonAnswer;
import lombok.Data;

@Data
public class PersonAnswerVo extends PersonAnswer {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
