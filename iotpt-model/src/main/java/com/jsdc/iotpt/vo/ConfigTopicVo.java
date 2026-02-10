package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigTopic;
import lombok.Data;

import java.util.List;

/**
 * @authon thr
 * @describe 主题管理
 */
@Data
public class ConfigTopicVo extends ConfigTopic {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String linkName;
    private List<Integer> idList;
    private List<Integer> linkIdList;
}
