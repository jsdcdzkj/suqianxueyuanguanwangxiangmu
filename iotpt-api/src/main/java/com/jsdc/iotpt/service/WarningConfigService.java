package com.jsdc.iotpt.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.ConfigDeviceTypeMapper;
import com.jsdc.iotpt.mapper.WarningConfigMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.JsdcDateUtil;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 告警配置 Service
 *
 * @author lb
 */
@Service
@Transactional
public class WarningConfigService extends BaseService<WarningConfig> {

    @Autowired
    private WarningConfigMapper warningConfigMapper;

    @Autowired
    private WarningSignDetailsService warningSignDetailsService;
    @Autowired
    private WarningDeviceMapService warningDeviceMapService;
    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;

    @Autowired
    MemoryCacheService memoryCacheService;
    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private SysDictService sysDictService;
    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<WarningConfigVo> getPageList(WarningConfigVo vo) {
        Page<WarningConfigVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<WarningConfigVo> pageList = warningConfigMapper.getEntityList(page, vo);
        setDictVal(pageList);
        return pageList;
    }

    private void setDictVal(Page<WarningConfigVo> pageList) {
        Map<String, SysDict> dictMap = RedisDictDataUtil.getDictByType("deviceType");
        Map<String, SysDict> warnConfigTypeMap = RedisDictDataUtil.getDictByType("warnConfigType");


        Map<String,String> deviceTypeMap = configDeviceTypeService.getConfigDeviceTypeMap(new ConfigDeviceType());
        for (WarningConfigVo vo : pageList.getRecords()) {
            vo.setDeviceTypeName(StringUtils.trimToEmpty(deviceTypeMap.get(vo.getDeviceType())));
            SysDict sdConfigType = warnConfigTypeMap.get(vo.getConfigType());
            vo.setConfigTypeName(sdConfigType.getDictLabel());
        }
    }

    /**
     * 查询
     *
     * @return
     */
    public List<WarningConfig> getList(WarningConfig entity) {
        QueryWrapper<WarningConfig> queryWrapper = new QueryWrapper<>();
        return warningConfigMapper.selectList(queryWrapper);
    }

    /**
     * 添加告警配置
     *
     * @param bean
     * @return
     */
    public ResultInfo saveWarningConfig(WarningConfigVo bean) {
        Date currentDate = new Date();
        SysUser currentUser = sysUserService.getUser();
        //保存配置表
        if(bean.getId() == null ){
            bean.setCreateTime(currentDate);
            bean.setUpdateTime(currentDate);
            bean.setCreateUser(currentUser.getId());
            bean.setUpdateUser(currentUser.getId());
            bean.setIsDel(G.ISDEL_NO);
        }
        saveOrUpdate(bean);
        Integer configId = bean.getId();
        List<WarningSignDetails> details = getWarningSignDetails(bean.getWarnDetailsList(),configId);

        //添加告警配置详情表
        //warningSignDetailsService.saveOrUpdateBatch(details);
        for (WarningSignDetails d:details){
            warningSignDetailsService.saveOrUpdate(d);
        }

        //添加告警配置设备关联表
        List<DeviceCollect> collectList = deviceCollectService.getDeviceCollects(bean.getDeviceType(), bean.getFloor(), bean.getDeviceId());
        List<WarningDeviceMap> wdList = new ArrayList<>();
        for (DeviceCollect dc : collectList) {
            WarningDeviceMap wd = new WarningDeviceMap();
            wd.setWarningConfigId(configId);
            wd.setDeviceId(dc.getId());
            warningDeviceMapService.saveOrUpdate(wd);
        }
        //warningDeviceMapService.saveOrUpdateBatch(wdList);



        memoryCacheService.changeWarningConfig();
        return ResultInfo.success();
    }

    private List<WarningSignDetails> getWarningSignDetails(List<WarningSignDetailsVo> voList ,int configId) {
        SysUser currentUser = sysUserService.getUser();
        List<WarningSignDetails> details = new ArrayList<>();
        for (WarningSignDetailsVo vo: voList){
            List<WarningSignDetails> temps = new ArrayList<>();
            if("0".equals(vo.getValueType())){
                temps = vo.getDomains();
            }else{
                temps = vo.getBooleans();
            }
            for (WarningSignDetails wsd : temps) {
                Date d = new Date();
                WarningSignDetails detail = new WarningSignDetails();
                BeanUtils.copyProperties(wsd, detail);
                if("0".equals(vo.getValueType())){
                    detail.setValueBegin(wsd.getValueBegin());
                    detail.setValueEnd(wsd.getValueEnd());
                }else{
                    detail.setValueBoolen(wsd.getValueBoolen());
                    detail.setNumberBool(wsd.getNumberBool());
                }

                detail.setWarnLevel(wsd.getWarnLevel());
                detail.setLinkageId(wsd.getLinkageId());
                detail.setIsEnable(wsd.getIsEnable());

                detail.setSignName(vo.getSignName());
                detail.setSignType(vo.getSignType());
                detail.setValueType(vo.getValueType());
                detail.setConfigId(configId);
                detail.setCreateTime(d);
                detail.setUpdateTime(d);
                detail.setCreateUser(currentUser.getId());
                detail.setUpdateUser(currentUser.getId());
                detail.setIsDel(G.ISDEL_NO);
                details.add(detail);
                System.out.println(detail);
            }
        }

        return details;
    }

    /**
     * 编辑告警配置
     *
     * @param bean
     * @return
     */
    public ResultInfo editWarningConfig(WarningConfigVo bean) {
        saveOrUpdate(bean);
        //先删除原有的关联
        List<WarningSignDetails> dbDetails = warningSignDetailsService.getDelSignDetailsIds(bean.getId());
       // warningSignDetailsService.removeBatchByIds(dbDetails);
        for(WarningSignDetails wsd : dbDetails){
            warningSignDetailsService.removeById(wsd.getId());
        }

        //配置新的关联
        //List<WarningSignDetails> details = bean.getDetails();
        Integer configId = bean.getId();
        List<WarningSignDetails> details = getWarningSignDetails(bean.getWarnDetailsList(),configId);
        for (WarningSignDetails d:details){
            warningSignDetailsService.saveOrUpdate(d);
        }

        List<WarningDeviceMap> warningDeviceList = warningDeviceMapService.getWarningDevices(bean.getId());
       // warningDeviceMapService.removeBatchByIds(warningDeviceList);
        for(WarningDeviceMap wdm : warningDeviceList){
            warningDeviceMapService.removeById(wdm.getId());
        }

        //添加告警配置设备关联表
        List<DeviceCollect> collectList = deviceCollectService.getDeviceCollects(bean.getDeviceType(), bean.getFloor(), bean.getDeviceId());
        List<WarningDeviceMap> wdList = new ArrayList<>();
        for (DeviceCollect dc : collectList) {
            WarningDeviceMap wd = new WarningDeviceMap();
            wd.setWarningConfigId(bean.getId());
            wd.setDeviceId(dc.getId());
            warningDeviceMapService.saveOrUpdate(wd);
        }
       // warningDeviceMapService.saveOrUpdateBatch(wdList);

        memoryCacheService.changeWarningConfig();
        return ResultInfo.success();
    }

    public ResultInfo saveOrUpdateBatchEntity(List<WarningConfig> beans) {
        saveOrUpdateBatch(beans);
        return ResultInfo.success();
    }

    /**
     * 根据id获取类
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        return ResultInfo.success(getById(id));
    }

    public ResultInfo delEntity(Integer id) {
        WarningConfig wc =  getById(id);
        wc.setIsDel(1);
        return ResultInfo.success(saveOrUpdate(wc));
    }

    public ResultInfo getWarningConfigEdit(WarningConfigVo bean) {
        Map<String, List<WarningSignDetails>> map = new HashMap<>();
        WarningConfigVo vo = new WarningConfigVo();
        BeanUtils.copyProperties(getById(bean.getId()), vo);
        List<WarningSignDetails> details = warningSignDetailsService.getDelSignDetailsIds(bean.getId());
        for (WarningSignDetails wsd : details) {
            String sType = wsd.getSignType();
            List<WarningSignDetails> tempDetails = map.get(sType);
            if (null == tempDetails) {
                tempDetails = new ArrayList<>();
            }
            tempDetails.add(wsd);
            map.put(sType,tempDetails);
        }

        List<WarningSignDetailsVo> voList = new ArrayList<>();
        for (String key : map.keySet()) {
            WarningSignDetailsVo wsdVo = new WarningSignDetailsVo();
            List<WarningSignDetails> wsd = map.get(key);
            WarningSignDetails tempDetails = wsd.get(0);
            wsdVo.setSignName(tempDetails.getSignName());
            //值类型
            wsdVo.setValueType(tempDetails.getValueType());
            wsdVo.setSignType(key);
            if("0".equals(tempDetails.getValueType())){
                wsdVo.setDomains(wsd);
            }else{
                wsdVo.setBooleans(wsd);
            }
            voList.add(wsdVo);
        }
        vo.setWarnDetailsList(voList);
        System.out.println("=============>>"+vo);
        return ResultInfo.success(vo);
    }

    /**
     * 告警类型统计
     * @param vo
     * @return
     */
    public List<WarningConfig> getWarningType(WarningConfigVo vo) {
        return warningConfigMapper.getWarningType(vo);
    }


    /**
     *
     * @return
     */
    public List<WarningConfigVo> getAllWarningConfig() {
        return warningConfigMapper.getAllWarningConfig();
    }


    /**
     * 环境区域排名
     * xujian
     */

    public ResultInfo getEnvironmentRank(){
        List<HashMap<String,Object>> result = new ArrayList<>();
        //todo 查询最新环境监测数据 后面channelId要改成环境监测信号
        String sql = "select last(val) as val from datasheet where channelId='PM2.5' GROUP BY  areaId";
        QueryResult query = influxdbService.query(sql);
        List<QueryResult.Result> resultList = query.getResults();
        for(QueryResult.Result r : resultList){
            List<QueryResult.Series> seriesList = r.getSeries();
            if(seriesList == null){
                continue;
            }
            for(QueryResult.Series series : seriesList){
                Map<String, String> tags = series.getTags();
                List<String> columns = series.getColumns();
                String[] keys = columns.toArray(new String[columns.size()]);
                List<List<Object>> values = series.getValues();
                //封装
                if(values != null && values.size() != 0) {
                    for(List<Object> value : values){
                        HashMap<String, Object> map = new HashMap();
                        for (int i = 0; i < keys.length; i++) {
                            map.put("areaId", tags.get("areaId"));
                            map.put(keys[i], value.get(i));
                        }
                        result.add(map);
                    }
                }
            }
        }
        //排序 倒序
        Comparator<Map<String, Object>> comparator = (map1, map2) -> {
            // 假设属性值为Integer类型
            BigDecimal value1 = new BigDecimal(map1.get("val")+"") ;
            BigDecimal value2 = new BigDecimal(map2.get("val")+"") ;
            return value2.compareTo(value1);
        };

        // 使用Collections.sort()方法进行排序
        Collections.sort(result, comparator);
        Map<String,Object> areaMap = (Map<String, Object>) RedisUtils.getBeanValue("prefix_sysarea_map");//区域
        for (HashMap<String, Object> o : result) {
            o.put("value",new BigDecimal(o.get("val")+"").setScale(4,BigDecimal.ROUND_HALF_UP));
            SysBuildArea area = (SysBuildArea) areaMap.get(Integer.parseInt(o.get("areaId")+""));
            o.put("name",area.getAreaName());
        }
        if(result.size() > 5){
            result = result.subList(0,5);
        }
        return ResultInfo.success(result);
    }


}


