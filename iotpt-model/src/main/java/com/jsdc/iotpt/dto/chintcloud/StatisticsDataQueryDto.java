package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsDataQueryDto {

    /**
     * 开始时间
     * 是否必传：是
     */
    private String beginTime;

    /**
     * 结束时间
     * 是否必传：是
     */
    private String endTime;

    /**
     * 时间间隔y-年，M-月，d-日，h-时，m-分，s-秒，q-季度，w-周
     * 是否必传：是
     */
    private String intervalUnit;

    /**
     * 时间间隔值
     * 是否必传：是
     */
    private String intervalValue;

    /**
     * 是否必传：否
     */
    private Object exceptionDisposal;

    /**
     * 是否必传：否
     */
    private Object timePickType;

    /**
     * 多点信息
     * 是否必传：否
     */
    private List<PointStatisticsData> points;

    /**
     * 时间格式化输出字符串
     * 是否必传：否
     */
    private String timeFormateString = "yyyy-MM-dd HH:mm:ss";


    @Data
    public static class PointStatisticsData {

        /**
         * 图形的ID
         * 是否必传：否
         */
        private String id;

        /**
         * 查询的终端点的ID
         * 是否必传：是
         */
        private String pointId;

        /**
         * 统计类型 ：-1-实时值；0-统计值，相减；1-耗能统计；2-供能统计；3-实际值；4-平均值，5-最大值，6-最小值，7-求和，8-计算
         * 是否必传：是
         */
        private Integer statisticsType;

    }

}
