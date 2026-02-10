package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * ClassName: PeopleNum
 * Description:
 * date: 2024/6/18 9:45
 *
 * @author bn
 */
@Entity
@TableName("PEOPLE_NUM")
@Table(name = "PEOPLE_NUM")
@KeySequence(value = "PEOPLE_NUM_ID")
@Data
public class PeopleNum extends Model<PeopleNum> {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PEOPLE_NUM_ID", sequenceName = "PEOPLE_NUM_ID", allocationSize = 1)
    @GeneratedValue(generator = "PEOPLE_NUM_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    //人数
    private Integer enterNumber;

    //出去人数
    private Integer exitNumber;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


}
