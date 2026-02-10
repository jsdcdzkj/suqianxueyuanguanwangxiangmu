package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ConfigDataTransferMapper;
import com.jsdc.iotpt.mapper.ConfigTopicMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigDataTransferVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @authon thr
 * @describe 数据转换模板管理
 */
@Service
@Transactional
public class ConfigDataTransferService extends BaseService<ConfigDataTransfer> {

    @Autowired
    private ConfigDataTransferMapper configDataTransferMapper;
    @Autowired
    private ConfigSignalFieldService configSignalFieldService;
    @Autowired
    private ConfigTemplateFieldService configTemplateFieldService;
    @Autowired
    private ConfigTopicMapper configTopicMapper;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigDataTransferVo> getPageList(ConfigDataTransferVo vo) {
//        QueryWrapper<ConfigDataTransfer> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("isDel",0).eq(StringUtils.isNotEmpty(vo.getModelName()),"modelName",vo.getModelName())
//                .eq(StringUtils.isNotEmpty(vo.getModelCode()),"modelCode",vo.getModelCode());
//        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
//        return configDataTransferMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

        Page<ConfigDataTransferVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<ConfigDataTransferVo> pageList = configDataTransferMapper.getEntityList(page, vo);
        return pageList;
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigDataTransfer> getList(ConfigDataTransferVo vo) {
        QueryWrapper<ConfigDataTransfer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getModelType())) {
                queryWrapper.eq("modelType", vo.getModelType());
            }
        }
        queryWrapper.orderByDesc("createTime");
        return configDataTransferMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigDataTransfer(ConfigDataTransferVo bean) {
        String msg = "";
        SysUser sysUser = sysUserService.getUser();
        if (StringUtils.isNotNull(bean.getId())) {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUser.getId());

            LambdaQueryWrapper<ConfigDataTransfer> wrapper = new LambdaQueryWrapper();
            wrapper.eq(ConfigDataTransfer::getModelCode, bean.getModelCode())
                    .eq(ConfigDataTransfer::getIsDel, 0)
                    .notIn(ConfigDataTransfer::getId, bean.getId());
            List<ConfigDataTransfer> list = configDataTransferMapper.selectList(wrapper);
            if (list.size() > 0) {
                return ResultInfo.error("该模板编码已存在");
            }

            LambdaQueryWrapper<ConfigDataTransfer> wrapper2 = new LambdaQueryWrapper();
            wrapper2.eq(ConfigDataTransfer::getModelName, bean.getModelName())
                    .eq(ConfigDataTransfer::getIsDel, 0)
                    .notIn(ConfigDataTransfer::getId, bean.getId());
            List<ConfigDataTransfer> list2 = configDataTransferMapper.selectList(wrapper2);
            if (list2.size() > 0) {
                return ResultInfo.error("该模板名称已存在");
            }

            msg = "修改";
        } else {
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUser.getId());
            bean.setIsDel(0);

            LambdaQueryWrapper<ConfigDataTransfer> wrapper = new LambdaQueryWrapper();
            wrapper.eq(ConfigDataTransfer::getModelCode, bean.getModelCode())
                    .eq(ConfigDataTransfer::getIsDel, 0);
            List<ConfigDataTransfer> list = configDataTransferMapper.selectList(wrapper);
            if (list.size() > 0) {
                return ResultInfo.error("该模板编码已存在");
            }

            LambdaQueryWrapper<ConfigDataTransfer> wrapper2 = new LambdaQueryWrapper();
            wrapper2.eq(ConfigDataTransfer::getModelName, bean.getModelName())
                    .eq(ConfigDataTransfer::getIsDel, 0);
            List<ConfigDataTransfer> list2 = configDataTransferMapper.selectList(wrapper2);
            if (list2.size() > 0) {
                return ResultInfo.error("该模板名称已存在");
            }

            msg = "新增";
        }

        saveOrUpdate(bean);

        //订阅主题模板
        if (bean.getModelType().equals("1")) {
            if (bean.getDataList().size() > 0) {
                for (ConfigSignalField configSignalField : bean.getDataList()) {
                    configSignalField.setModelId(bean.getId());
                    configSignalField.setType("1");
                    configSignalFieldService.saveOrUpdateConfigSignalField(configSignalField);
                }
            }
            if (bean.getAlarmList().size() > 0) {
                for (ConfigSignalField configSignalField : bean.getAlarmList()) {
                    configSignalField.setModelId(bean.getId());
                    configSignalField.setType("2");
                    configSignalFieldService.saveOrUpdateConfigSignalField(configSignalField);
                }
            }
            if (bean.getHeartList().size() > 0) {
                for (ConfigSignalField configSignalField : bean.getHeartList()) {
                    configSignalField.setModelId(bean.getId());
                    configSignalField.setType("3");
                    configSignalFieldService.saveOrUpdateConfigSignalField(configSignalField);
                }
            }
            if (bean.getControlList().size() > 0) {
                for (ConfigSignalField configSignalField : bean.getControlList()) {
                    configSignalField.setModelId(bean.getId());
                    configSignalField.setType("4");
                    configSignalFieldService.saveOrUpdateConfigSignalField(configSignalField);
                }
            }


//            if (StringUtils.isNotNull(bean.getTemplateField1())) {
//                ConfigTemplateField configTemplateField = bean.getTemplateField1();
//                configTemplateField.setModelId(bean.getId());
//                configTemplateField.setType("1");
//                configTemplateFieldService.saveOrUpdateConfigTemplateField(configTemplateField);
//            }
//            if (StringUtils.isNotNull(bean.getTemplateField2())) {
//                ConfigTemplateField configTemplateField = bean.getTemplateField2();
//                configTemplateField.setModelId(bean.getId());
//                configTemplateField.setType("2");
//                configTemplateFieldService.saveOrUpdateConfigTemplateField(configTemplateField);
//            }
//            if (StringUtils.isNotNull(bean.getTemplateField3())) {
//                ConfigTemplateField configTemplateField = bean.getTemplateField3();
//                configTemplateField.setModelId(bean.getId());
//                configTemplateField.setType("3");
//                configTemplateFieldService.saveOrUpdateConfigTemplateField(configTemplateField);
//            }

            //主题管理id
            if (StringUtils.isNotNull(bean.getConfigTopicId())) {
                ConfigTopic configTopic = new ConfigTopic();
                configTopic.setId(bean.getConfigTopicId());
                configTopic.setTransferId(bean.getId());
                configTopicMapper.updateById(configTopic);
            }
        }

        //数据模板绑定主题id
        if (bean.getModelType().equals("1") || bean.getModelType().equals("2")) {
            //主题管理id
            if (StringUtils.isNotNull(bean.getConfigTopicId())) {
                ConfigTopic configTopic = new ConfigTopic();
                configTopic.setId(bean.getConfigTopicId());
                configTopic.setTransferId(bean.getId());
                configTopicMapper.updateById(configTopic);
            }
        }

        return ResultInfo.success("操作成功", new LogVo(msg));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo delConfigDataTransfer(ConfigDataTransferVo bean) {
        bean.setUpdateTime(new Date());
        updateById(bean);

        return ResultInfo.success("删除成功", new LogVo("删除"));
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        ConfigDataTransfer bean = getById(id);

        ConfigDataTransferVo vo = new ConfigDataTransferVo();
        BeanUtils.copyProperties(bean, vo);

        //模板类型
        HashMap<String, SysDict> protocol = RedisDictDataUtil.getDictByType("config_data_transfer");
        vo.setModelTypeName(protocol.get(vo.getModelType()).getDictLabel());

        //订阅主题模板
        if (vo.getModelType().equals("1")) {
            ConfigSignalField configSignalField = new ConfigSignalField();
            configSignalField.setModelId(vo.getId());
            configSignalField.setType("1");
            List<ConfigSignalField> list = configSignalFieldService.getList(configSignalField);
            vo.setDataList(list);

            configSignalField.setType("2");
            List<ConfigSignalField> list2 = configSignalFieldService.getList(configSignalField);
            vo.setAlarmList(list2);

            configSignalField.setType("3");
            List<ConfigSignalField> list3 = configSignalFieldService.getList(configSignalField);
            vo.setHeartList(list3);

            configSignalField.setType("4");
            List<ConfigSignalField> list4 = configSignalFieldService.getList(configSignalField);
            vo.setControlList(list4);

            ConfigTemplateField configTemplateField = new ConfigTemplateField();
            configTemplateField.setModelId(vo.getId());
            configTemplateField.setType("1");
            ConfigTemplateField configTemplateField1 = configTemplateFieldService.getEntityByModelId(configTemplateField);
            vo.setTemplateField1(configTemplateField1);

            configTemplateField.setType("2");
            ConfigTemplateField configTemplateField2 = configTemplateFieldService.getEntityByModelId(configTemplateField);
            vo.setTemplateField2(configTemplateField2);

            configTemplateField.setType("3");
            ConfigTemplateField configTemplateField3 = configTemplateFieldService.getEntityByModelId(configTemplateField);
            vo.setTemplateField3(configTemplateField3);

            configTemplateField.setType("4");
            ConfigTemplateField configTemplateField4 = configTemplateFieldService.getEntityByModelId(configTemplateField);
            vo.setTemplateField4(configTemplateField4);
        }
        return ResultInfo.success(vo);
    }

    // 网关解析
    private JSONObject wgjx;
    // 设备解析
    private JSONObject sbjx;
    // 信号解析
    private JSONObject xhjx;
    // 心跳解析
    private JSONObject xtjx;
    // 报警解析
    private JSONObject bjjx;


    /**
     * type：1数据 2告警 3心跳
     * 数据模板解析成网关，信号，设备，心跳，报警模板
     * @param configSignalFields
     */
    public void telData(Integer type, List<ConfigSignalField> configSignalFields){
        if (configSignalFields.isEmpty()){
            return;
        }
        if (1==type&&null!=wgjx&&null!=sbjx&&null!=xhjx){
            wgjx.clear();
            sbjx.clear();
            xhjx.clear();
        }else if (2==type&&null!=bjjx){
            bjjx.clear();
        }else if (3==type&&null!=xtjx){
            xtjx.clear();
        }
        configSignalFields.forEach(x->{
            if (1==type){
                SystemKey systemKey=SystemKey.getSeemingKey(x.getSystemKey());
                switch (systemKey){
                    case GATEWAY_INFO:
                        if (null == wgjx)
                        {
                            wgjx=new JSONObject();
                        }
                        wgjx.put(x.getGatewayKey(),x.getSystemKey());
                        break;
                    case DEVICE_INFO:
                        if (null == sbjx)
                        {
                            sbjx=new JSONObject();
                        }
                        sbjx.put(x.getGatewayKey(),x.getSystemKey());
                        break;
                    case SIGNAL_INFO:
                        if (null == xhjx)
                        {
                            xhjx=new JSONObject();
                        }
                        xhjx.put(x.getGatewayKey(),x.getSystemKey());
                        break;
                    default:
                        break;
                }
            }else if (2==type){
                if (null==bjjx){
                    bjjx=new JSONObject();
                }
                bjjx.put(x.getGatewayKey(),x.getSystemKey());
            }else if (3==type){
                if(null==xtjx){
                    xtjx=new JSONObject();
                }
                xtjx.put(x.getGatewayKey(),x.getSystemKey());
            }

        });
    }


    public ResultInfo parseTest(ConfigDataTransferVo bean){

        try {
            String msgType=null;
            if (bean.getJsonData().indexOf(":")==-1){
                return ResultInfo.error("json数据格式错误！");
            }

            JSONObject fromObject = JSONObject.parseObject(bean.getJsonData());

            //1数据 2告警 3心跳
            if (bean.getDataType().equals("1")){
                // 对比数据拆分成网关解析，信号解析，设备解析
                telData(1,bean.getDataList());
                // 数据解析
                Map map = new HashMap();
                DataSheet dataSheet = new DataSheet();
                ArrayList<DataSheet> dataSheets = new ArrayList<>();
                testData(fromObject,map,dataSheet,dataSheets,wgjx,sbjx,xhjx);

                return ResultInfo.success(dataSheets);
            }else if (bean.getDataType().equals("2")){
                // 对比数据拆分成网关解析，信号解析，设备解析
                telData(2,bean.getAlarmList());
                Map map = new HashMap();
                AlertSheet alertSheet = new AlertSheet();
                ArrayList<AlertSheet> alertSheets = new ArrayList<>();
                boolean isArray=testAlert(fromObject,map,alertSheet,alertSheets,bjjx);

                if (!isArray){
                    alertSheets.add(alertSheet);
                }

                return ResultInfo.success(alertSheets);
            }else{
                // 对比数据拆分成网关解析，信号解析，设备解析
                telData(3, bean.getHeartList());
                Map map = new HashMap();
                HeartSheet heartSheet = new HeartSheet();
                testHeart(fromObject,map,heartSheet,xtjx);
                return ResultInfo.success(heartSheet);
            }
        }catch (Exception e){
            return ResultInfo.error("解析错误");
        }
    }


    /**
     *  数据解析
     */
    public boolean testData(JSONObject fromObject, Map map, DataSheet dataSheet, List<DataSheet> list, JSONObject wabc, JSONObject sabc,JSONObject xabc) throws Exception{
        Set<String> keys = fromObject.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (wabc.getString(key) != null) {
                        dataSheet = setVal(wabc.getString(key), val, dataSheet);
                    }
                    if (sabc.getString(key) != null) {
                        dataSheet = setVal(sabc.getString(key), val, dataSheet);
                    }
                } else {
                    iteraJson(val, map, dataSheet,wabc);
                }
            }
        }

        Iterator<String> iterator2 = keys.iterator();
        while (iterator2.hasNext()) {
            String key = iterator2.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") != -1) {
                //包含随机ID
                if (key.contains("random")) {
                    if (wabc.getString("random") != null) {
                        setVal(wabc.getString("random"), key.split("_")[1], dataSheet);
                    }
                    if (sabc.getString("random") != null) {
                        setVal(sabc.getString("random"), key.split("_")[1], dataSheet);
                    }
                }
                //说明存在数组json即格式为：[{开头的json数组
                if (val.indexOf("[{") == 0) {
                    //说明当前value就是一个json数组
                    //去除[括号
                    JSONArray jsonArray = JSONArray.parseArray(val);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
                        Iterator<String> iterator1 = xabc.keySet().iterator();
                        while (iterator1.hasNext()) {
                            //厂商参数key
                            String next = iterator1.next();
                            //对应数据值参数
                            String xkey = xabc.getString(next);
                            setVal(xkey, jsonObject.getString(next), dataSheet);
                        }
                        Iterator<String> iterator3 = sabc.keySet().iterator();
                        while (iterator3.hasNext()) {
                            //厂商参数key
                            String next = iterator3.next();
                            //对应数据值参数
                            String skey = sabc.getString(next);
                            setVal(skey, jsonObject.getString(next), dataSheet);
                        }
                        DataSheet sheet = new DataSheet();
                        BeanUtils.copyProperties(dataSheet, sheet);
                        list.add(sheet);
                    }
                    map.put(key.replaceAll("\"", ""), jsonArray);
                } else {
                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
                    if (val.indexOf(":") == -1) {
                        return true;
                    }
                    JSONObject valObject = JSONObject.parseObject(val);
                    testData(valObject, map, dataSheet, list,wabc,sabc,xabc);//符合当前递归条件
                }
            }
        }
        return false;
    }


    /**
     *  告警解析
     * @param fromObject
     * @param map
     * @param alertSheet
     * @param list
     * @param jabc
     */
    public boolean testAlert(JSONObject fromObject, Map map, AlertSheet alertSheet, ArrayList<AlertSheet> list, JSONObject jabc)throws Exception{

        boolean isArray=false;
        Set<String> keys = fromObject.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (jabc.getString(key) != null) {
                        alertSheet = setVal(jabc.getString(key), val, alertSheet);
                    }
                } else {
                    iteraJson(val, map, alertSheet,jabc);
                }
            }
        }

        Iterator<String> iterator2 = keys.iterator();
        while (iterator2.hasNext()) {
            String key = iterator2.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") != -1) {
                isArray=true;
                //包含随机ID
                if (key.contains("random")) {
                    if (jabc.getString("random") != null) {
                        setVal(jabc.getString("random"), key.split("_")[1], alertSheet);
                    }
                }
                //说明存在数组json即格式为：[{开头的json数组
                if (val.indexOf("[{") == 0) {
                    //说明当前value就是一个json数组
                    //去除[括号
                    JSONArray jsonArray = JSONArray.parseArray(val);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
                        Iterator<String> iterator1 = jabc.keySet().iterator();
                        while (iterator1.hasNext()) {
                            //厂商参数key
                            String next = iterator1.next();
                            //对应数据值参数
                            String xkey = jabc.getString(next);
                            setVal(xkey, jsonObject.getString(next), alertSheet);
                        }
                        AlertSheet sheet = new AlertSheet();
                        BeanUtils.copyProperties(alertSheet, sheet);
                        list.add(sheet);
                    }
                    map.put(key.replaceAll("\"", ""), jsonArray);
                } else {
                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
                    if (val.indexOf(":") == -1) {
                        return true;
                    }
                    JSONObject valObject = JSONObject.parseObject(val);
                    testAlert(valObject, map, alertSheet, list,jabc);//符合当前递归条件
                }
            }
        }
        return isArray;


    }

    /**
     *  心跳解析
     * @param fromObject
     * @param map
     * @param heartSheet
     * @param xabc
     */
    public boolean testHeart(JSONObject fromObject, Map map, HeartSheet heartSheet, JSONObject xabc) throws Exception{
        Set<String> keys = fromObject.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (xabc.getString(key) != null) {
                        heartSheet = setVal(xabc.getString(key), val, heartSheet);
                    }
                } else {
                    iteraJson(val, map, heartSheet,xabc);
                }
            }
        }

        // 正常心跳只有一层 如果有多层 将此处注释释放
//        Iterator<String> iterator2 = keys.iterator();
//        while (iterator2.hasNext()) {
//            String key = iterator2.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") != -1) {
//                //包含随机ID
//                if (key.contains("random")) {
//                    if (xabc.getString("random") != null) {
//                        setVal(xabc.getString("random"), key.split("_")[1], heartSheet);
//                    }
//                    if (xabc.getString("random") != null) {
//                        setVal(xabc.getString("random"), key.split("_")[1], heartSheet);
//                    }
//                }
//                //说明存在数组json即格式为：[{开头的json数组
//                if (val.indexOf("[{") == 0) {
//                    //说明当前value就是一个json数组
//                    //去除[括号
//                    JSONArray jsonArray = JSONArray.parseArray(val);
//                    for (Object o : jsonArray) {
//                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
//                        Iterator<String> iterator1 = xabc.keySet().iterator();
//                        while (iterator1.hasNext()) {
//                            //厂商参数key
//                            String next = iterator1.next();
//                            //对应数据值参数
//                            String xkey = xabc.getString(next);
//                            setVal(xkey, jsonObject.getString(next), heartSheet);
//                        }
//                        HeartSheet sheet = new HeartSheet();
//                        BeanUtils.copyProperties(sheet, heartSheet);
//                        list.add(sheet);
//                    }
//                    map.put(key.replaceAll("\"", ""), jsonArray);
//                } else {
//                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
//                    if (val.indexOf(":") == -1) {
//                        return true;
//                    }
//                    JSONObject valObject = JSONObject.parseObject(val);
//                    testHeart(valObject, map, heartSheet, list,xabc);//符合当前递归条件
//                }
//            }
//        }






        return false;

    }


    private DataSheet setVal(String key, String value, DataSheet dataSheet) throws Exception {
        Field nameField = dataSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = dataSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (nameField.getType().getName().contains("String")) {
                set.invoke(dataSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(dataSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(dataSheet, date);
                }else if(value.matches("[\\d]{10}|[\\d]{13}")){ // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")){
                        value=value+"000";
                    }
                    Date date=new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(dataSheet, date);
                }
            }
        }

        return dataSheet;
    }

    private HeartSheet setVal(String key, String value, HeartSheet heartSheet) throws Exception {
        Field nameField = heartSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = heartSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (nameField.getType().getName().contains("String")) {
                set.invoke(heartSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(heartSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(heartSheet, date);
                }else if(value.matches("[\\d]{10}|[\\d]{13}")){ // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")){
                        value=value+"000";
                    }
                    Date date=new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(heartSheet, date);
                }
            }
        }

        return heartSheet;
    }

    private AlertSheet setVal(String key, String value, AlertSheet alertSheet) throws Exception {
        Field nameField = alertSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = alertSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (nameField.getType().getName().contains("String")) {
                set.invoke(alertSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(alertSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(alertSheet, date);
                }else if(value.matches("[\\d]{10}|[\\d]{13}")){ // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")){
                        value=value+"000";
                    }
                    Date date=new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(alertSheet, date);
                }
            }
        }

        return alertSheet;
    }


    public boolean iteraJson(String str, Map res, DataSheet dataSheet, JSONObject wabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, dataSheet,wabc)) {
                res.put(key, value);
                if (wabc.get(key) != null) {
                    setVal(wabc.get(key).toString(), value.toString(), dataSheet);
                }
            }
        }
        return false;
    }

    public boolean iteraJson(String str, Map res, HeartSheet dataSheet, JSONObject wabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, dataSheet,wabc)) {
                res.put(key, value);
                if (wabc.get(key) != null) {
                    setVal(wabc.get(key).toString(), value.toString(), dataSheet);
                }
            }
        }
        return false;
    }

    public boolean iteraJson(String str, Map res, AlertSheet dataSheet, JSONObject jabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, dataSheet,jabc)) {
                res.put(key, value);
                if (jabc.get(key) != null) {
                    setVal(jabc.get(key).toString(), value.toString(), dataSheet);
                }
            }
        }
        return false;
    }





}

enum SystemKey {
    UNKNOWN(""),
    GATEWAY_INFO("gateWayId"),
    DEVICE_INFO("deviceId"),
    SIGNAL_INFO("channelId|val|time");


    // 类型对应后缀
    private Map<String, Boolean> mSuffixMap;


    // suffix, 类型对应的后缀，用;分割
    private SystemKey(String keys) {
        this.mSuffixMap = new HashMap<String, Boolean>();
        String[] splits = keys.split("\\|");
        for (String suffix : splits) {
            if (suffix.isEmpty())
                continue;
            this.mSuffixMap.put(suffix, true);
        }
        return;
    }


    public boolean isMatch(String suffix) {
        if (this.mSuffixMap.containsKey(suffix))
            return true;
        return false;
    }

    public static SystemKey getSeemingKey(String suffix) {
        if (null == suffix){
            return SystemKey.UNKNOWN;
        }
        // 逐一判断
        if (SystemKey.GATEWAY_INFO.isMatch(suffix))
            return SystemKey.GATEWAY_INFO;
        if (SystemKey.DEVICE_INFO.isMatch(suffix))
            return SystemKey.DEVICE_INFO;
        if (SystemKey.SIGNAL_INFO.isMatch(suffix))
            return SystemKey.SIGNAL_INFO;
        return SystemKey.UNKNOWN;
    }




}



