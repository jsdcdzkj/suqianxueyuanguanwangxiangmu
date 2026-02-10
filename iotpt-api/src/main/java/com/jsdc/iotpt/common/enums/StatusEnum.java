package com.jsdc.iotpt.common.enums;

public class StatusEnum {
    public enum SysUser {
        TYPE1("1", "行政管理单位"),
        TYPE2("2", "医疗机构"),
        TYPE3("3", "零售药店"),
        TYPE4("4", "用人单位"),
        TYPE5("5", "体检机构"),
        TYPE6("6", "银行"),
        TYPE7("7", "非定点机构"),
        TYPE8("8", "非定点药店");
        private String type;
        private String desc;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        SysUser(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }
}
