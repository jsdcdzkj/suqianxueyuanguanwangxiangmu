package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.WaterPrice;
import com.jsdc.iotpt.service.WaterPriceService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/waterPrice")
public class WaterPriceController {
    @Autowired
    private WaterPriceService waterPriceService;


    /**
     * 新增/修改价位
     * @author wzn
     * @date 2024/6/5 14:50
     */
    @PostMapping("/addWaterPrice")
    @LogInfo(value = LogEnums.LOG_WATER_CHARGE_UPD,model = Constants.MODEL_YYZT)
    public ResultInfo addWaterPrice(@RequestBody WaterPrice waterPrice) {
        waterPriceService.addWaterPrice(waterPrice);
        return ResultInfo.success();
    }


    /**
     * 查询价格
     * @author wzn
     * @date 2024/6/5 14:51
     */
    @PostMapping("/getWaterPrice")
    public ResultInfo getWaterPrice() {
        return ResultInfo.success(waterPriceService.getWaterPrice());
    }


}
