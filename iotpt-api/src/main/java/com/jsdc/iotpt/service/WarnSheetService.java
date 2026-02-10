package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.ReportTempTypeMapper;
import com.jsdc.iotpt.mapper.WarnSheetMapper;
import com.jsdc.iotpt.model.ReportTempType;
import com.jsdc.iotpt.model.WarnSheet;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.WarnSheetVo;
import com.jsdc.iotpt.vo.WarningConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
/**
 * ClassName: WarnSheetService
 * Description:
 * date: 2023/7/20 13:45
 *
 * @author bn
 */
@Service
@Transactional
public class WarnSheetService extends BaseService<WarnSheet> {

    @Autowired
    private WarnSheetMapper warnSheetMapper;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private ReportTempTypeMapper tempTypeMapper;
    @Autowired
    private SysBuildAreaService sysBuildAreaService;

    /**
     * 告警等級統計
     * Author wzn
     * Date 2023/7/21 9:00
     */
    public List<WarnSheetVo> warnList() {
        List<SysDict> sysDictList = sysDictService.selectByType("warnLevel");
        Map<String,String> map = new HashMap<>() ;
        for (SysDict sysDict : sysDictList) {
            map.put(sysDict.getDictValue(),sysDict.getDictLabel()) ;
        }
        List<WarnSheetVo> warnSheetVos =warnSheetMapper.warnList();
        if(CollectionUtil.isNotEmpty(warnSheetVos)){
            for(WarnSheetVo w:warnSheetVos){
                w.setName(map.get(w.getName()));
            }
        }
        return warnSheetVos ;

    }

    /**
     * 告警来源统计
     * Author wzn
     * Date 2023/7/21 15:47
     */
    public List<WarnSheetVo> warnSourceList() {


        return warnSheetMapper.warnSourceList() ;

    }



    /**
     * 安防看板  告警重点设备排行
     *
     * @return
     */
    public List<WarnSheetVo> getCollectWarningTop() {
        return warnSheetMapper.getCollectWarningTop();
    }



    /**
     * 智慧安防 告警中心 历史告警接口
     * @param vo
     * @return
     */
    public Page<WarnSheetVo> getHistoryWarning(WarnSheetVo vo) {
        return warnSheetMapper.getHistoryWarning(vo);
    }

    /**
     * 安防看板 重点告警区域
     *
     * @return
     */
    public List<WarnSheetVo> getKeyAreaWarning() {
        return warnSheetMapper.getKeyAreaWarning();
    }


    public Page<WarnSheetVo> getRealTimeWarning(WarnSheetVo vo) {

        Page<WarnSheetVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<WarnSheetVo> pageList = warnSheetMapper.getRealTimeWarning(page, vo);

        HashMap<String, SysDict> warnTypes = RedisDictDataUtil.getDictByType("warnType");

        HashMap<String, SysDict> warnStatus = RedisDictDataUtil.getDictByType("warnStatus");


        pageList.getRecords().forEach(x -> {

            if (warnTypes != null) {
                x.setWarnLevelName(warnTypes.get(x.getWarnLevel()) == null ? "未知" : warnTypes.get(x.getWarnLevel()).getDictLabel());
            }

            if (warnStatus != null) {
                x.setStatusName(warnStatus.get(x.getStatus()) == null ? "未知" : warnStatus.get(x.getStatus()).getDictLabel());
            }

        });

        return pageList;

    }

    /**
     * 告警类型统计
     *
     * @param vo
     * @return
     */
    public List<Map<String,String>> getWarningType1(WarningConfigVo vo) {
        List<ReportTempType> tempTypes=tempTypeMapper.selectList(Wrappers.<ReportTempType>lambdaQuery().eq(ReportTempType::getTempId,vo.getTempId()));
        List<Map<String,String>> list = new ArrayList<>();
        for (int i = 0; i < tempTypes.size(); i++) {
            if (tempTypes.get(i).getReportTypeId().contains("dict")){
                SysDict sysDict = sysDictService.
                        getOne(Wrappers.<SysDict>lambdaQuery().
                                eq(SysDict::getDictType,"warnType").
                                eq(SysDict::getDictValue,tempTypes.get(i).getReportTypeId().split("_")[1]));
                String dictLabel = sysDict.getDictLabel();
                String key = sysDict.getDictValue();
                Map<String,String> map = new HashMap<>();

                long totalNum = 1;

                map.put("name",dictLabel);
                map.put("value",String.valueOf(totalNum));
                list.add(map);
            }
        }
        return list;
    }
}
