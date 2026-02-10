package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@TableName("VEGETABLE")
@Table(name = "CVEGETABLEAR")
@KeySequence(value = "VEGETABLE_ID")
@Data
public class Vegetable extends Model<Vegetable> {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "VEGETABLEID", sequenceName = "VEGETABLE_ID", allocationSize = 1)
    @GeneratedValue(generator = "VEGETABLEID", strategy = GenerationType.SEQUENCE)
    private Integer id;


    //菜名
    private String dishName ;

    //上菜时间
    private String time ;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;
}
