package com.jsdc.iotpt.smartsecurity.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jsdc.iotpt.model.DaPingConfig;
import com.jsdc.iotpt.service.DaPingConfigService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daping/config")
public class DaPingConfigController {

    @Autowired
    private DaPingConfigService daPingConfigService;

    @PostMapping("/saveOrUpdate")
    public ResultInfo saveOrUpdate(@RequestBody DaPingConfig daPingConfig) {
        if (daPingConfig.getIsDel() == null) {
            daPingConfig.setIsDel(0);
        }
        daPingConfigService.saveOrUpdate(daPingConfig);
        daPingConfigService.update(new LambdaUpdateWrapper<DaPingConfig>()
                .ne(DaPingConfig::getId, daPingConfig.getId())
                .set(DaPingConfig::getIsDel, 1));
        return ResultInfo.success();
    }

    @GetMapping("/latest")
    public ResultInfo latest() {
        return ResultInfo.success(daPingConfigService.latest());
    }

}
