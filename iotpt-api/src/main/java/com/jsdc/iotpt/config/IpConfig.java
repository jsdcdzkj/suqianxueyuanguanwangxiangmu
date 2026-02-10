package com.jsdc.iotpt.config;

import com.jsdc.iotpt.service.IpRegionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class IpConfig {

    @Bean
    public IpRegionService ipRegionService() throws IOException {
        IpRegionService service = new IpRegionService();
        service.init();
        return service;
    }

    @PreDestroy
    public void destroy() throws IOException {
        // 清理资源
    }
}