package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsDataDto {

    private List<DataItem> datas;

    private List<String> values;

    private List<String> times;


    @Data
    public static class DataItem {

        private String id;

        private List<DbDataItem> dbDatas;

        private List<String> values;

    }

    @Data
    public static class DbDataItem {

        private String time;

        private String value;

        private String credibility;

    }

}
