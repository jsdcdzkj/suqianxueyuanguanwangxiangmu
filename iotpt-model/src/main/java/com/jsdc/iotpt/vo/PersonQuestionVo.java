package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.PersonQuestion;
import lombok.Data;

@Data
public class PersonQuestionVo extends PersonQuestion {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
