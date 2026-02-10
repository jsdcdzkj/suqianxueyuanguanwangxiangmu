package com.jsdc.iotpt.service.curtain;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.tcpClient.BootNettyClient;
import com.jsdc.iotpt.common.tcpClient.CommandConfig;
import com.jsdc.iotpt.dto.chintcloud.CurtainDTO;
import com.jsdc.iotpt.mapper.CurtainMapper;
import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.model.curtain.Curtain;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.CurtainVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-26 09:06
 **/
@Slf4j
@Service
public class CurtainService extends BaseService<Curtain> {
    @Autowired
    private CurtainMapper curtainMapper;
    @Autowired
    private SysUserService userService;


    public void curtainControl(CurtainVo bean) {
        Map<String, Channel> channelMap = CommandConfig.getInstance().getChannels();

        Curtain curtain = curtainMapper.selectById(bean.getId());

        if (!channelMap.containsKey(curtain.getIp())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BootNettyClient client = new BootNettyClient();
                    try {
                        client.connect(curtain.getIp(), 8889);
                        log.error("窗帘设备====连接：" + curtain.getIp() + "====成功");
                    } catch (Exception e) {
                        log.error("窗帘设备====连接：" + curtain.getIp() + "====失败");
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(1000);
                    // 开启
                    if (channelMap.containsKey(curtain.getIp())) {
                        // 任务补偿：没有反馈就连续下发3次，或者关机
                        if (0 == bean.getControlType()) {
                            for (int i = 0; i < 3; i++) {
                                channelMap.get(curtain.getIp()).writeAndFlush("FF06010A" + curtain.getUp() + "0001FA");
                                Thread.sleep(1000);
                            }
                        } else if (1 == bean.getControlType()) {
                            for (int i = 0; i < 3; i++) {
                                channelMap.get(curtain.getIp()).writeAndFlush("FF06010A" + curtain.getStop() + "0001FA");
                                Thread.sleep(1000);
                            }
                        } else {
                            for (int i = 0; i < 3; i++) {
                                channelMap.get(curtain.getIp()).writeAndFlush("FF06010A" + curtain.getDown() + "0001FA");
                                Thread.sleep(1000);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("窗帘设备====7：" + curtain.getIp() + "====失败");
                    e.printStackTrace();
                }
            }
        }).start();


    }

    //根据会议室Id获取对应的窗帘列表
    public List<Curtain> selectByMeetingId(Integer meetingId) {
        List<Curtain> curtains = curtainMapper.selectByMeetingId(meetingId);
        // 自定义Comparator来按照名字中的数字进行排序
        curtains.sort(new Comparator<Curtain>() {
            @Override
            public int compare(Curtain c1, Curtain c2) {
                // 提取名字中的数字
                int num1 = extractNumber(c1.getCurtainName());
                int num2 = extractNumber(c2.getCurtainName());

                // 按照数字进行比较
                return Integer.compare(num1, num2);
            }

            // 辅助方法，用于从字符串中提取数字
            private int extractNumber(String s) {
                // 使用正则表达式提取数字
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(s);
                if (m.find()) {
                    return Integer.parseInt(m.group());
                } else {
                    // 如果没有找到数字，返回一个默认值，比如0
                    return 0;
                }
            }
        });
        return curtains;
    }
    //新增、修改
    public void saveOrUpd(Curtain bean) {
        if (null != bean.getId()) {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(userService.getUser().getId());
            updateById(bean);
        } else {
            bean.setIsDel(0);
            bean.setCreateTime(new Date());
            bean.setCreateUser(userService.getUser().getId());
            save(bean);
        }
    }

    public Long isExistJudgeCount(Curtain bean) {
        LambdaQueryWrapper<Curtain> wrapper = new LambdaQueryWrapper<Curtain>()
                .eq(Curtain::getIsDel, 0)
                .eq(Curtain::getCurtainName, bean.getCurtainName())
                .ne(null != bean.getId(), Curtain::getId, bean.getId());
        return count(wrapper);
    }

    public void deleteById(Integer id) {
        curtainMapper.update(null, Wrappers.<Curtain>lambdaUpdate()
                .eq(Curtain::getId, id)
                .set(Curtain::getIsDel, 1)
                .set(Curtain::getUpdateTime, new Date())
                .set(Curtain::getUpdateUser, userService.getUser().getId())
        );
    }


    public Page<Curtain> selectPageList(CurtainDTO bean) {
        Page<Curtain> page = curtainMapper.selectPage(new Page<>(bean.getPageIndex(), bean.getPageSize()),
                new LambdaQueryWrapper<Curtain>()
                        .eq(StrUtil.isNotEmpty(bean.getCurtainName()), Curtain::getCurtainName, bean.getCurtainName()));
        return page;
    }

    public Curtain getCurtainById(Integer id) {
        return getById(id);
    }

    public JSONArray getCurtain() {
        JSONArray result=new JSONArray();

        List<Curtain> curtains=curtainMapper.selectList(Wrappers.<Curtain>lambdaQuery().
                eq(Curtain::getIsDel,0));
        for (Curtain item:curtains) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("curtain_name",item.getCurtainName());
            jsonObject.put("curtain_id",item.getId());
            jsonObject.put("area_id",item.getAreaId());

            result.add(jsonObject);
        }

        return result;
    }

    public ResultInfo getCurtains() {
       JSONArray result=new JSONArray();
       List<Curtain> curtains = curtainMapper.selectList(Wrappers.<Curtain>lambdaQuery().eq(Curtain::getIsDel,0).eq(Curtain::getGeneralControl,1));

       curtains.forEach(x->{
           JSONObject item=new JSONObject();
           item.put("id",x.getId());
           item.put("curtainName",x.getCurtainName());
           result.add(item);
       });


       return ResultInfo.success(result);
    }
}
