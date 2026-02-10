package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

import java.util.List;

@Data
public class DeviceStateDto {

    private Integer onlineCount;

    private Integer offlineCount;

    private Integer deviceCount;

    private Integer onlineRate;

    private List<State> deviceState;


    @Data
    public static class State {

        private String id;

        private String deviceName;

        private Integer state;

        private Long timeSpanSecond;

        private String time;

        private String timeSpan;

    }

}
