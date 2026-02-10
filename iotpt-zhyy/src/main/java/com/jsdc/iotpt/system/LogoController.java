package com.jsdc.iotpt.system;

import com.jsdc.iotpt.model.sys.SysLogo;
import com.jsdc.iotpt.service.LogoService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/logo")
@Api(tags = "logo管理")
public class LogoController {
    @Autowired
    private LogoService logoService;
    /**
     * 获取logo
     * @return
     */
    @PostMapping("/logoList")
    @ApiOperation("获取logo")
    public ResultInfo logoList() {
        return ResultInfo.success(logoService.logoList());
    }

     /**
     * 更新logo
     * @return
     */
    @PostMapping("/updateLogo")
    @ApiOperation("更新logo")
    public ResultInfo updateLogo(@RequestBody List<SysLogo> sysLogs) {
        return logoService.updateLogo(sysLogs);
    }

}
