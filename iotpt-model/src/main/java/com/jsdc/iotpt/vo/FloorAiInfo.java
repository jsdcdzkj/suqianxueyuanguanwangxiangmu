package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;


@Data
public class FloorAiInfo extends Model<FloorAiInfo> {

    private String id;
    private String name;


}
