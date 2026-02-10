package com.jsdc.iotpt.dto.chintcloud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


/**
 * 泰杰赛智慧照明系统 历史数据分页查询请求体
 */

@Data
public class HistoryDataPageQueryDto {

    /**
     * 页码，默认1
     * 是否必传：否
     */
    private Integer pageIndex;

    /**
     * 页面大小，默认100
     * 是否必传：否
     */
    private Integer pageSize;

    /**
     * 开始时间 yyyy-MM-dd HH:mm:ss
     * 是否必传：是
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    /**
     * 结束时间 yyyy-MM-dd HH:mm:ss
     * 是否必传：是
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String endTime;

    /**
     * 时间格式化输出字符串
     * 是否必传：否
     */
    private String timeFormateString = "yyyy-MM-dd HH:mm:ss";

    /**
     * 终端点的ID
     * 是否必传：是
     */
    private String pointId;

    /**
     * 判断条件1，只能传递【大于、大于等于、小于、小于等于】
     * 是否必传：否
     */
    private String condition1;

    /**
     * 判断值1
     * 是否必传：否
     */
    private String value1;

    /**
     * 判断条件2，只能传递【大于、大于等于、小于、小于等于】
     * 是否必传：否
     */
    private String condition2;

    /**
     * 判断值2
     * 是否必传：否
     */
    private String value2;

}
