package com.jsdc.iotpt.service;

import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.vo.ConfigDeviceSubitemVo;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.WarningConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author lb
 * @Date 2023/7/1 11:50
 * @Description 缓存类
 **/
@Service
@DependsOn("redisUtils")
public class MemoryCacheService {

    @Autowired
    DeviceGatewayService deviceGatewayService;
    @Autowired
    DeviceCollectService deviceCollectService;
    @Autowired
    WarningConfigService warningConfigService;

    @Autowired
    WarningSignDetailsService warningSignDetailsService;
    @Autowired
    SysBuildService sysBuildService;
    @Autowired
    SysBuildFloorService sysBuildFloorService;
    @Autowired
    SysBuildAreaService sysBuildAreaService;
    @Autowired
    SysDictService sysDictService;

    @Autowired
    ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    ConfigDeviceSubitemService configDeviceSubitemService;
    @Autowired
    ConfigSignalTypeService configSignalTypeService;


    /**
     * 网关设备map
     * String key 是sn码
     */
    private static Map<String, DeviceGateway> gatewaySNMap = new ConcurrentHashMap<>();
    /**
     * 网关设备map
     * String key 是id
     */
    private static Map<String, DeviceGateway> gatewayMap = new ConcurrentHashMap<>();


    //网关设备 sn和id对应关系map
    public static Map<String, String> gatewaySN2IdMap = new ConcurrentHashMap<>();

    //网关设备 id和sn对应关系map
    public static Map<String, String> gatewayId2SNMap = new ConcurrentHashMap<>();

    public static String gatewayKeyPrefix = "gatewayKeyPrefix_";
    /**
     * 根据sn为key 网关为值
     */
    public static String gatewayKeyPrefixsn = "gatewayKeyPrefix_sn_";
    /**
     * 根据网关id为key 网关为值
     */
    public static String gatewayKeyPrefixid = "gatewayKeyPrefix_id_";

    /**
     * 根据网关sn码和id对应关系
     */
    public static String gatewayKeyPrefixSN2Id = "gatewayKeyPrefix_SN2Id_";
    /**
     * 根据网关id码和sn码对应关系
     */
    public static String gatewayKeyPrefixId2SN = "gatewayKeyPrefix_Id2SN_";


    /**
     * 采集设备map
     * String serial number
     */
    public static Map<String, DeviceCollectVo> collecSNMap = new ConcurrentHashMap<>();
    public static Map<String, DeviceCollectVo> collecIdMap = new ConcurrentHashMap<>();
    public static String deviceCollecKeyPrefix = "deviceCollecKeyPrefix_";

    /**
     * 根据采集设备id 对应 采集设备
     */
    public static String deviceCollecKeyPrefixid = "deviceCollecKeyPrefix_id_";
    /**
     * 根据采集设备sn码 对应 采集设备
     */
    public static String deviceCollecKeyPrefixsn = "deviceCollecKeyPrefix_sn_";

    /**
     * 根据网关sn码+采集设备sn码 对应 采集设备
     */
    public static String deviceCollecKeyPrefixSNSN = "deviceCollecKeyPrefix_SNSN_";


    /**
     * 采集设备map
     * String key 是网关sn + "_" 采集设备sn
     * value 是DeviceCollect
     */
    public static Map<String, DeviceCollectVo> collecSNIdMap = new ConcurrentHashMap<>();


    /**
     * 采集设备map
     * key 是网关sn + "_" 采集设备sn
     */
    public static Map<String, WarningConfig> warningMap = new ConcurrentHashMap<>();
    public static String warningConfigKeyPrefix = "warningConfigKeyPrefix_";

    /**
     * key 是网关sn + "_" 采集设备sn  对应告警规则
     */
    public static String warningConfigKeyPrefixSNSN = "warningConfigKeyPrefix_SNSN_";
    public static String warningSignDetailsKeyPrefixConfigId = "warningSignDetailsKeyPrefix_ConfigId_";

    public static String heartKey = "heartSheet_";

    public final static String allGatewayMapPrefix = "prefix_gateway_map";
    public final static String allCollectMapPrefix = "prefix_collect_map";
    public final static String allBuildMapPrefix = "prefix_sysbuild_map";
    public final static String allFloorMapPrefix = "prefix_sysfloor_map";
    public final static String allAreaMapPrefix = "prefix_sysarea_map";
    public final static String allWarningConfigMapPrefix = "prefix_warnconfig_map";


    //字典
    public final static String allDictMapPrefix = "prefix_dict_map";
    //返回是全部的数据
    public final static String allDictListPrefix = "prefix_dict_list";

    //返回是单个类型的list
    public final static String dictListPrefix = "prefix_dict_list_oneType_";
    public final static String allDictKeyPrefix = "prefix_dict_key";

    public final static String allDeviceTypeMapPrefix = "prefix_devicetype_map";
    public final static String allSubItemMapPrefix = "prefix_subItem_map";

    //信号类型 key
    public final static String allSignalTypePrefix = "prefix_signaltype_key";
    @PostConstruct
    @DependsOn("redisUtils")
    public void initData() {

        putAllBuild();
        putAllFloor();
        putAllArea();

        putAllDict();
        //信号类型
        putSignalType();

        putAllDeviceType();
        putAllSubitem();

        //网关
        changeDeviceGateway();
        //设备
        changeDeviceCollectVo();
        //告警
        changeWarningConfig();
        System.out.println("");

        //告警
    }

    /**
     * 信号类型放入缓存
     */
    private void putSignalType() {
        List<ConfigSignalType> signalTypeList = configSignalTypeService.getList(null);
        Map<String, String> signalTypeMap = new HashMap<>();
        for (ConfigSignalType st : signalTypeList) {
            signalTypeMap.put(st.getSignalTypeCode(), st.getSignalTypeName());
        }
        RedisUtils.setBeanValue(allSignalTypePrefix, signalTypeMap);
    }

    /**
     * 把字典数据塞入redis
     */
    public void putAllDict() {
        List<SysDict> dicts = sysDictService.getAllDicts();
        //根据字典类型分组
        Map<String, Map<String, String>> dictMap = new HashMap<>();
        Map<String, List<SysDict>> dictListMap = new HashMap<>();

        //key 是字典类型+字典值   value是label
        Map<String, String> map = new HashMap<>();

        for (SysDict dict : dicts) {
            String type = dict.getDictType();
            Map<String, String> tempMap = dictMap.get(type);
            if (null == tempMap) {
                tempMap = new HashMap<>();
                dictMap.put(type, tempMap);
            }
            tempMap.put(dict.getDictValue(), dict.getDictLabel());

            List<SysDict> dictList = dictListMap.get(type);
            if (null == dictList) {
                dictList = new ArrayList<>();
                dictListMap.put(type, dictList);
            }
            dictList.add(dict);

            map.put(type + "_" + dict.getDictValue(), dict.getDictLabel());
        }

        RedisUtils.setBeanValue(allDictMapPrefix, dictMap);
        RedisUtils.setBeanValue(allDictListPrefix, dictListMap);
        //每一种dictType类型的list
        for (String key : dictListMap.keySet()) {
            RedisUtils.setBeanValue(dictListPrefix + key, dictListMap.get(key));
        }
        RedisUtils.setBeanValue(allDictKeyPrefix, map);
        System.out.println("===");
    }

    /**
     * 设备类型
     */
    public void putAllDeviceType() {
        Map<Integer, ConfigDeviceType> map = new HashMap<>();
        List<ConfigDeviceType> cdTypeList = configDeviceTypeService.getList(new ConfigDeviceType());
        for (ConfigDeviceType cdt : cdTypeList) {
            map.put(cdt.getId(), cdt);
        }
        RedisUtils.setBeanValue(allDeviceTypeMapPrefix, map);
    }

    public void putAllSubitem() {
        Map<Integer, ConfigDeviceSubitem> map = new HashMap<>();
        List<ConfigDeviceSubitem> cdTypeList = configDeviceSubitemService.getList(new ConfigDeviceSubitemVo());
        for (ConfigDeviceSubitem cds : cdTypeList) {
            map.put(cds.getId(), cds);
        }
        RedisUtils.setBeanValue(allSubItemMapPrefix, map);
    }

    /**
     * 根据字典类型获取 字典map
     *
     * @param dictType
     * @return map
     */
    public Map<String, String> getDictMapByDicType(String dictType) {
        Object obj = RedisUtils.getBeanValue(allDictMapPrefix);
        if (null != obj && obj instanceof Map) {
            return (Map<String, String>) obj;
        }
        return new HashMap<>();
    }

    /**
     * 根据字典类型获取  val是List<SysDict>
     *
     * @param dictType
     * @return List<SysDict>
     */
    public List<SysDict> getDictListByDicType(String dictType) {
        Object obj = RedisUtils.getBeanValue(allDictListPrefix);
        if (null != obj && obj instanceof List) {
            return (List<SysDict>) obj;
        }
        return new ArrayList<>();
    }

    public String getDictByDicTypeAndDictVal(String dictType, String dictVal) {
        Object obj = RedisUtils.getBeanValue(allDictKeyPrefix);
        if (null != obj && obj instanceof Map) {
            Map<String, String> map = (Map<String, String>) obj;
            return map.get(dictType + "_" + dictVal);
        }
        return "";
    }

    /**
     * 区域数据塞入redis
     */
    public void putAllArea() {
        Map<Integer, SysBuildArea> allArea = new ConcurrentHashMap<>();
        //区域
        List<SysBuildArea> sbas = sysBuildAreaService.getList(null);
        for (SysBuildArea sba : sbas) {
            allArea.put(sba.getId(), sba);
        }

        RedisUtils.setBeanValue(allAreaMapPrefix, allArea);
    }

    /**
     * 楼层数据塞入redis
     */
    public void putAllFloor() {
        Map<Integer, SysBuildFloor> allFloor = new ConcurrentHashMap<>();
        //楼层
        List<SysBuildFloor> sysFloors = sysBuildFloorService.getList(null);
        for (SysBuildFloor sbf : sysFloors) {
            allFloor.put(sbf.getId(), sbf);
        }

        RedisUtils.setBeanValue(allFloorMapPrefix, allFloor);
    }

    /**
     * 楼宇数据塞入redis
     */
    public void putAllBuild() {
        Map<Integer, SysBuild> allSysBuild = new ConcurrentHashMap<>();
        //楼宇
        List<SysBuild> sysBuilds = sysBuildService.getList(null);
        for (SysBuild sb : sysBuilds) {
            allSysBuild.put(sb.getId(), sb);
        }

        RedisUtils.setBeanValue(allBuildMapPrefix, allSysBuild);
    }

    /**
     * 网关设备塞入redis
     */
    private void putAllGateway() {
        Map<Integer, DeviceGateway> allGateway = new ConcurrentHashMap<>();
        //网关
        List<DeviceGateway> gateways = deviceGatewayService.getList(null);
        for (DeviceGateway dg : gateways) {
            allGateway.put(dg.getId(), dg);
        }

        RedisUtils.setBeanValue(allGatewayMapPrefix, allGateway);
    }


    /**
     * 网关数据塞入redis
     */
    private void putAllCollect() {
        Map<Integer, DeviceCollectVo> allCollect = new ConcurrentHashMap<>();
        //采集设备
        List<DeviceCollectVo> deviceCollects = deviceCollectService.getList(null);
        for (DeviceCollectVo dc : deviceCollects) {
            allCollect.put(dc.getId(), dc);
        }

        RedisUtils.setBeanValue(allCollectMapPrefix, allCollect);
    }

    public void heartDevice(Integer gatewayId, Integer collectId) {
        RedisUtils.setBeanValue(String.format("%s%d_%d", heartKey, gatewayId, collectId), String.format("%s%d_%d", heartKey, gatewayId, collectId), 1);
    }

    public void changeDeviceGateway() {

        putAllGateway();

        try {
            //网关
            List<DeviceGateway> gateways = deviceGatewayService.getList(null);
            for (DeviceGateway dg : gateways) {
                //序列号为key 网关信息
                gatewaySNMap.put(dg.getSerialNum(), dg);

                //id为key 网关信息
                gatewayMap.put(String.valueOf(dg.getId()), dg);
                gatewaySN2IdMap.put(dg.getSerialNum(), String.valueOf(dg.getId()));
                gatewayId2SNMap.put(String.valueOf(dg.getId()), dg.getSerialNum());

                //序列号为key 网关信息
                RedisUtils.setBeanValue(gatewayKeyPrefixsn + dg.getSerialNum(), dg);
                //id为key 网关信息
                RedisUtils.setBeanValue(MemoryCacheService.gatewayKeyPrefixid + String.valueOf(dg.getId()), dg);
                //网关sn对id
                RedisUtils.setBeanValue(MemoryCacheService.gatewayKeyPrefixSN2Id + dg.getSerialNum(), String.valueOf(dg.getId()));
                //网关id对sn
                RedisUtils.setBeanValue(MemoryCacheService.gatewayKeyPrefixId2SN + String.valueOf(dg.getId()), dg.getSerialNum());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void changeDeviceCollectVo() {

        putAllCollect();

        try {
            //设备
            List<DeviceCollectVo> deviceCollects = deviceCollectService.getList(null);
            for (DeviceCollectVo dc : deviceCollects) {
                collecIdMap.put(String.valueOf(dc.getId()), dc);
                if (null != dc.getGatewayId()) {
                    DeviceGateway dg = gatewayMap.get(String.valueOf(dc.getGatewayId()));
                    String sn = dg.getSerialNum();
                    collecSNMap.put(sn, dc);
                    String key = String.format("%s_%s", sn, dc.getDeviceCode());
                    collecSNIdMap.put(key, dc);
                }


                RedisUtils.setBeanValue(MemoryCacheService.deviceCollecKeyPrefixid + String.valueOf(dc.getId()), dc);
                if (null != dc.getGatewayId()) {
                    DeviceGateway dg = (DeviceGateway) RedisUtils.getBeanValue(MemoryCacheService.gatewayKeyPrefixid + String.valueOf(dc.getGatewayId()));
                    String sn = dg.getSerialNum();
                    RedisUtils.setBeanValue(MemoryCacheService.deviceCollecKeyPrefixsn + sn, dc);
                    String key = String.format("%s_%s", sn, dc.getDeviceCode());
                    RedisUtils.setBeanValue(MemoryCacheService.deviceCollecKeyPrefixSNSN + key, dc);
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 返回map集合
     * key 采集设备id
     * value WarningConfigVo 包含 告警所有数据
     */
    public void changeWarningConfig() {
        try {
            //采集设备id为key
            Map<Integer, List<WarningConfigVo>> warningConfigs = new ConcurrentHashMap<>();
            List<WarningConfigVo> voList = warningConfigService.getAllWarningConfig();
            for (WarningConfigVo vo : voList) {
                Integer collectId = vo.getCollectId();
                List<WarningConfigVo> tempWCvo = warningConfigs.get(collectId);
                if (null == tempWCvo) {
                    tempWCvo = new ArrayList<>();
                    warningConfigs.put(collectId, tempWCvo);
                }
                tempWCvo.add(vo);
            }

            RedisUtils.setBeanValue(MemoryCacheService.allWarningConfigMapPrefix, warningConfigs);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
