package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: ConfigSignalTypeItem
 * Description:
 * date: 2023/12/19 8:56
 *
 * @author bn
 */
@Entity
@TableName("CONFIG_SIGNAL_TYPE_ITEM")
@Table(name = "CONFIG_SIGNAL_TYPE_ITEM")
@KeySequence(value = "SEQ_CONFIG_SIGNAL_TYPE_ITEM_ID")
@Data
@ApiModel(value = "信号类型子项")
public class ConfigSignalTypeItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CONFIG_SIGNAL_TYPE_ITEM_ID", sequenceName = "SEQ_CONFIG_SIGNAL_TYPE_ITEM_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIG_SIGNAL_TYPE_ITEM_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer typeId;

    private String itemKey;

    private String itemVal;

    private String isDel;

}
