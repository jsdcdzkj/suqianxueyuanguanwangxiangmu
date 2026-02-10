package com.jsdc.iotpt.common.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.tcpClient.BootNettyClient;
import com.jsdc.iotpt.mapper.MultimediaMapper;
import com.jsdc.iotpt.model.Multimedia;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BootNettyClientApplication implements ApplicationListener<ContextRefreshedEvent> {

    //    @Value("${tcp.enable}")
    private Boolean enable = true;
    //    @Value("${tcp.server}")
    private String server;

    //    @Value("${tcp.port}")
    private Integer port;

    @Autowired
    private MultimediaMapper multimediaMapper;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (enable) {
            List<Multimedia> multimedias=multimediaMapper.
                    selectList(Wrappers.<Multimedia>lambdaQuery().
                            eq(Multimedia::getIsDel,0));

            multimedias.forEach(x->{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BootNettyClient client = new BootNettyClient();
                        try {
                            client.connect(x.getMediaIp(), Integer.parseInt(x.getMediaPort()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            });
        }
    }




}
