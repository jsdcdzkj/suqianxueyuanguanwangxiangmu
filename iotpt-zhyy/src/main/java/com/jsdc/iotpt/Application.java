package com.jsdc.iotpt;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.DefaultClient;
import com.dahuatech.icc.oauth.http.IClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
@CrossOrigin
public class Application extends SpringBootServletInitializer {


    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(Application.class, args);
        Environment environment = configurableApplicationContext.getBean(Environment.class);
        System.out.println("\n============> 系统启动成功！后台地址：http://localhost:" + environment.getProperty("server.port")
                + "\n============> 数据库连接地址: " + environment.getProperty("spring.datasource.url")
                + "\n============> 数据库连接用户名: " + environment.getProperty("spring.datasource.username"));
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    public IClient iccDefaultClient() throws ClientException {
        return new DefaultClient();
    }


}


