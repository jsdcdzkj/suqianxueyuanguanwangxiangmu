//package com.jsdc.iotpt.common.task;
//
//import com.jsdc.iotpt.common.DateUtils;
//import com.jsdc.iotpt.service.leniu.LeNiuService;
//import com.jsdc.iotpt.vo.LeNiuVo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
///**
// * ClassName: LeNiuTask
// * Description:
// * date: 2024/6/1 9:15
// *
// * @author bn
// */
//@Component
//public class LeNiuTask {
//
//
//    @Autowired
//    private LeNiuService leNiuService;
//
//    /**
//     *  同步充值记录
//     */
//    @Scheduled(cron = "0 */1 * * * ?")
//    private void recipeQuery(){
//        Date currentDate=new Date();
//        LeNiuVo bean=new LeNiuVo();
//        bean.setStartDate(DateUtils.getStartTimeOfCurrentDay(currentDate));
//
//        bean.setEndDate(DateUtils.getEndTimeOfCurrentDay(currentDate));
//
//
//
//
//        leNiuService.orderFlowQuery(bean);
//    }
//
//}
