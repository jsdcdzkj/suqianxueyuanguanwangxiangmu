package com.jsdc.iotpt.util;

// IP归属地实体类
public class IpRegion {
    private String country;   // 国家
    private String region;    // 区域
    private String province;  // 省份
    private String city;      // 城市
    private String isp;       // 运营商

    // getters and setters
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getIsp() { return isp; }
    public void setIsp(String isp) { this.isp = isp; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (country != null && !country.isEmpty()) sb.append(country);
        if (province != null && !province.isEmpty() && !"0".equals(province)) sb.append(" ").append(province);
        if (city != null && !city.isEmpty() && !"0".equals(city)) sb.append(" ").append(city);
        if (isp != null && !isp.isEmpty() && !"0".equals(isp)) sb.append(" ").append(isp);
        return sb.toString();
    }
}
