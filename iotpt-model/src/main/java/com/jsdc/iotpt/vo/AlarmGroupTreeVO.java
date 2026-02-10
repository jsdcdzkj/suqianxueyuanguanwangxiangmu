package com.jsdc.iotpt.vo;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmGroupTreeVO {

    private String id;

    private String name;

    private boolean check = false;

    private boolean disable = false;

    private List<AlarmGroupTreeVO> children = new ArrayList<>();

    public void add(AlarmGroupTreeVO vo) {
        this.children.add(vo);
    }

}
