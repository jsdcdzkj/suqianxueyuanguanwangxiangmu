package com.jsdc.iotpt.service;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.StrangerMapper;
import com.jsdc.iotpt.model.Stranger;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class StrangerService extends BaseService<Stranger> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private StrangerMapper mapper;
    @Autowired
    private SysFileService fileService;

    @Value("${linkmqtt.ip}")
    private String mqttUrl;
    @Autowired
    private AlarmContentConfigService alarmContentConfigService;
    @Autowired
    private AlarmRecordsService alarmRecordsService;

    public Page<Stranger> selectStrangerPage(Stranger vo, Integer pageIndex, Integer pageSize) {
        Page page = new Page(pageIndex, pageSize);
        Page<Stranger> pages = new Page<>();
        if (StringUtils.isNotNull(vo)) {
            pages = mapper.selectStrangerPage(page, vo);
            pages.getRecords().forEach(a -> {
                a.setFrequency(1);
                //图片随便取值，后期根据插入的数据，进行更改
                List<SysFile> list = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, a.getId()+"").eq(SysFile::getBizType, 6));
                List<String> urls = new ArrayList<>();
                List<Integer> urlsId = new ArrayList<>();
                list.forEach(b -> {
                    urls.add( b.getFileUrl());
                    urlsId.add(b.getId());
                });
                if (urls.size()>0){
                    a.setFileName(urls.get(0));
                }
                a.setFileId(urlsId);
                a.setFileList(urls);
            });
        }
        return pages;
    }



    /**
     * 视频告警 右下角弹窗
     * @param paramMap
     * <pre>
     *
     *    paramMap.put("id",sba == null ? "" : info.getId());<br>
     *             paramMap.put("areaName",sba == null ? "" : sba.getAreaName());<br>
     *             paramMap.put("areaId",aep.getAreaId());<br>
     *             paramMap.put("deviceName","烟感");<br>
     *             paramMap.put("warnTypeName","烟雾报警");<br>
     *             paramMap.put("warnLevel",info.getWarnLevel());<br>
     *             paramMap.put("alertTime",info.getAlertTime());<br>
     *             paramMap.put("warnContent",info.getWarnContent());<br>
     * </pre>
     */
    public void sendMqtt(Map<String,Object> paramMap) {
        try{
            String result= HttpUtil.post( mqttUrl+"/mqtt/sendMsg", paramMap);
        }catch (Exception e){
            System.out.println("======2=====");
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
