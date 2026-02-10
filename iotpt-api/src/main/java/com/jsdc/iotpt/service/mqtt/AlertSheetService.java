package com.jsdc.iotpt.service.mqtt;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.AlertSheetMapper;
import com.jsdc.iotpt.mapper.ConfigDataTransferMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.service.MemoryCacheService;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.WarningConfigVo;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: AlertSheetService
 * Description: 告警数据
 * date: 2023/5/19 11:54
 *
 * @author bn
 */
@Service
@Transactional
public class AlertSheetService extends BaseService<AlertSheet> {

    @Autowired
    private AlertSheetMapper alertSheetMapper;


    /**
     *  领祺网关下发告警信息方案
     *  系统配置的告警配置会下发到领祺网关中 。网关下发的报警 都是应该入库
     *  返回结果一定为true
     * @param collectKey
     * @param collect
     * @param ds
     * @return
     */
    public Map<String, Object> hasWarn(String collectKey, DeviceCollect collect, AlertSheet ds) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        Map<Integer, List<WarningConfigVo>> warningConfig = new HashMap<>();
        Object warningConfigObj = RedisUtils.getBeanValue(MemoryCacheService.allWarningConfigMapPrefix);


        if (null != warningConfigObj && warningConfigObj instanceof Map) {
            warningConfig =( Map<Integer, List<WarningConfigVo>>) warningConfigObj;
        }else{
            map.put("flag", false);
            return map;
        }

        if(null == warningConfig || warningConfig.size() == 0 || !warningConfig.containsKey(collect.getId())){
            map.put("flag", false);
            return map;
        }
        List<WarningConfigVo> wcLists = warningConfig.get(collect.getId());

        if(null == wcLists || wcLists.size() == 0){
            map.put("flag", false);
            return map;
        }

        for (WarningConfigVo vo : wcLists){
            double val =  NumberUtils.toDouble(ds.getVal());
            if(vo.getSignType().equals(ds.getSignalId())  &&  val > vo.getValueBegin() && val < vo.getValueEnd()){
                map.put("flag", true);
                map.put("data", vo);
                return map;
            }
        }


        return map;
    }

}
