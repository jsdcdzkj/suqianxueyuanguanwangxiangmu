package com.jsdc.iotpt.vo.operate;

import com.jsdc.iotpt.model.operate.MissionItemRecord;
import lombok.Data;

import java.util.List;


/**
 * 任务项记录保存的值
 */
@Data
public class MissionRecordVo{


    private Integer id;
    private Integer region;
    //区域名称
    private String regionName;
    // 楼层名称
    private String floorName;
    // 楼层id
    private Integer floorId;
    //任务记录表
    private List<MissionItemRecord> records;
    //0、未处理  1、已处理
    private Integer isHandle;
    private String detailAddress;
    // 楼层排序
    private Integer sort;
}
