package com.jsdc.iotpt;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.AppPushMsgService;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MainTest {

    @Autowired
    private AppPushMsgService appPushMsgService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test1() {
        System.out.println("");
    }

    @Test
    public void testAppPushMsg() {
        AppPushMsgVo appPushMsgVo = new AppPushMsgVo();
        appPushMsgVo.setTitle("用车维修");
        appPushMsgVo.setName("张三");
        appPushMsgVo.setReasons("不喜欢，需要更换！");
        appPushMsgService.test(appPushMsgVo);
    }

    @Test
    public void testLiftFollow2() {
        SysUser sysUser = new SysUser();
        sysUser.setId(22400);
        if(Objects.equals(sysUser.getId(),22400) || sysDictService.compareDictValueIsContains("liftFollow",String.valueOf(sysUser.getId()))){
            System.out.println("true");
        }else {
            System.out.println("false");
        }

    }
    @Test
    public void testMeetingOptionalRange() {
        String time = "14:49~14:58,15:08~15:14,15:21~15:59,17:31~23:59";
        String s = filterShortTimeRanges(time, 10);
        System.out.println(s);

    }
    public static String filterShortTimeRanges(String time, int minDurationMinutes) {
        // 拆分时间段
        String[] timeRanges = time.split(",");
        List<String> validRanges = new ArrayList<>();

        for (String range : timeRanges) {
            String[] times = range.split("~");
            if (times.length == 2) {
                // 解析时间
                LocalTime startTime = LocalTime.parse(times[0]);
                LocalTime endTime = LocalTime.parse(times[1]);

                // 计算时间差
                long durationMinutes = Duration.between(startTime, endTime).toMinutes();

                // 过滤符合条件的时间段
                if (durationMinutes >= minDurationMinutes) {
                    validRanges.add(range);
                }
            }
        }
        // 拼接过滤后的时间段
        return String.join(",", validRanges);
    }
    @Test
    public void testRestTemplate() {
        SysUser byId = userService.getById(22487);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", byId.getPhone());
        jsonObject.put("oldPassWord", "Pwd@123456");

        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://127.0.0.1:9098/getUserByPhoneAndPassword/", JSONObject.toJSONString(jsonObject), String.class);
        String msg = JSONObject.parseObject(stringResponseEntity.getBody()).getString("msg");
        if (msg == null || msg.equals("false")) {
            System.out.println("密码错误");
        }
    }

    @Test
    public void testAIUserAuth() {
//        List<SysUser> list = userService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel,0));
//        List<Integer> a = Stream.of(5, 9, 19, 4, 13, 18, 20, 10, 7, 8, 11, 17).collect(Collectors.toList());
//        list.stream().map(SysUser::getId).forEach(id -> {
//            SysAIUserMenu sysAIUserMenu = new SysAIUserMenu();
//            sysAIUserMenu.setUserId(id);
//            sysAIUserMenu.setIsDel(0);
//            for (Integer x: a) {
//                sysAIUserMenu.setMenuId(x);
//                sysAIUserMenu.insert();
//            }
//        });


    }
}
