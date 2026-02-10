package com.jsdc.iotpt.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.model.ConfigTopic;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class RemoteControl implements Serializable {
    private static final long serialVersionUID = 14345666544L;
    private Commend commend = new Commend();
    private List<ConfigTopic> topicList;

    public class Commend {
        public String com = "WriteDate";
        public String channelName;
        public String devName;

        public List<HashMap<String, Object>> values = new ArrayList<>();

        public String getCom() {
            return com;
        }

        public void setCOM(String com) {
            this.com = com;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getDevName() {
            return devName;
        }

        public void setDevName(String devName) {
            this.devName = devName;
        }

        public List<HashMap<String, Object>> getValues() {
            return values;
        }

        public void setValues(List<HashMap<String, Object>> values) {
            this.values = values;
        }
    }


}
