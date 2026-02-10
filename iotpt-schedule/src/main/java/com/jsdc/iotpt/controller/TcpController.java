package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.tcpClient.CommandConfig;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TcpController
 * Description:
 * date: 2024/12/6 10:29
 *
 * @author bn
 */
@RestController
@RequestMapping("tcp")
public class TcpController {


    /**
     * tcp 服务调用
     */
    @GetMapping("/control")
    public ResultInfo control(String ip,String cmd) {

        if (CommandConfig.getInstance().getChannels().containsKey(ip)){
            CommandConfig.getInstance().getChannels().get(ip).writeAndFlush(cmd);
        }
        return ResultInfo.success();
    }
}
