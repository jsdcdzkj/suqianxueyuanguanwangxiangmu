package com.jsdc.iotpt.service;

import com.jsdc.iotpt.util.IpRegion;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import java.io.*;

public class IpRegionService {

    private Searcher searcher;

    /**
     * 初始化IP查询器
     */
    public void init() throws IOException {
        // 从resources目录加载IP数据库
        ClassPathResource resource = new ClassPathResource("ip2region_v4.xdb");
        InputStream inputStream = resource.getInputStream();

        // 将流转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] cBuff = outputStream.toByteArray();

        // 创建查询器
        searcher = Searcher.newWithBuffer(cBuff);
    }

    /**
     * 查询IP归属地
     */
    public String getRegion(String ip) {
        try {
            if (searcher == null) {
                init();
            }
            return searcher.search(ip);
        } catch (Exception e) {
            e.printStackTrace();
            return "未知";
        }
    }

    /**
     * 解析归属地信息
     */
    public IpRegion parseRegion(String regionStr) {
        if (regionStr == null || regionStr.isEmpty()) {
            return new IpRegion();
        }

        String[] parts = regionStr.split("\\|");
        IpRegion region = new IpRegion();
        if (parts.length > 0) region.setCountry(parts[0]);
        if (parts.length > 1) region.setRegion(parts[1]);
        if (parts.length > 2) region.setProvince(parts[2]);
        if (parts.length > 3) region.setCity(parts[3]);
        if (parts.length > 4) region.setIsp(parts[4]);

        return region;
    }

    /**
     * 完整查询方法
     */
    public IpRegion getIpRegion(String ip) {
        String regionStr = getRegion(ip);
        return parseRegion(regionStr);
    }

    public void destroy() {
        try {
            if (searcher != null) {
                searcher.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

