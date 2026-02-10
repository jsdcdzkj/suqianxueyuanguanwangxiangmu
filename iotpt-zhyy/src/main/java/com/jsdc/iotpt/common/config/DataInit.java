//package com.jsdc.iotpt.common.config;
//
//
//import com.jsdc.iotpt.init.RedisDataInit;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//
///**
// * @ClassName DataInit
// * @Description TODO 数据初始化
// * @Date 2022/1/5 9:52
// * @Version 1.0
// */
//@Component
//@Order(3)
//public class DataInit implements ApplicationRunner {
//    @Autowired
//    private RedisDataInit dataInit;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //缓存数据初始化
//        //获取h5stream令牌token放入缓存
//        dataInit.init();
//    }
//
//
//}
