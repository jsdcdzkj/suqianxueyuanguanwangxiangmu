package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.new_alarm.AlarmCategory;
import com.jsdc.iotpt.model.new_alarm.AlarmContentConfig;
import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.ExportDataUtils;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.UploadUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.AlarmContentConfigVO;
import com.jsdc.iotpt.vo.AlarmGroupTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AlarmContentConfigService extends BaseService<AlarmContentConfig> {

    @Autowired
    private AlarmContentConfigMapper alarmContentConfigMapper;

    @Autowired
    private SysUserService userService;

    @Autowired
    private ConfigDeviceTypeMapper configDeviceTypeMapper;

    @Autowired
    private AlarmGroupMapper alarmGroupMapper;

    @Autowired
    private AlarmCategoryMapper alarmCategoryMapper;

    @Autowired
    private AlarmPlanConfigMapper alarmPlanConfigMapper;

    @Value("${jsdc.uploadPath}")
    private String FilePath;


    public Page<AlarmContentConfig> getPageList(AlarmContentConfigVO vo) {
        Page<AlarmContentConfig> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<AlarmContentConfig> selectPage = alarmContentConfigMapper.selectPage(page, new LambdaQueryWrapper<AlarmContentConfig>()
                .eq(AlarmContentConfig::getIsDel, 0)
                .eq(Objects.nonNull(vo.getAlarmCategory()), AlarmContentConfig::getAlarmCategory, vo.getAlarmCategory())
                .eq(Objects.nonNull(vo.getAlarmLevel()), AlarmContentConfig::getAlarmLevel, vo.getAlarmLevel())
                .eq(Objects.nonNull(vo.getAlarmGroup()), AlarmContentConfig::getAlarmGroup, vo.getAlarmGroup())
                .eq(Objects.nonNull(vo.getDeviceType()), AlarmContentConfig::getDeviceType, vo.getDeviceType())
                .eq(Objects.nonNull(vo.getEnable()), AlarmContentConfig::getEnable, vo.getEnable())
                .and(StrUtil.isNotBlank(vo.getKeyword()), i -> i.like(AlarmContentConfig::getAlarmContent, vo.getKeyword()).or().like(AlarmContentConfig::getDeviceTypeName, vo.getKeyword()))
        );
        List<AlarmContentConfig> records = selectPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            Map<String, HashMap<String, SysDict>> map = (Map<String, HashMap<String, SysDict>>)RedisUtils.getBeanValue("dictData");
            HashMap<String, SysDict> alarmLevel = map.get("alarmLevel");
            HashMap<String, SysDict> alarmType = map.get("alarmType");
            List<AlarmGroup> alarmGroups = alarmGroupMapper.selectList(new LambdaQueryWrapper<AlarmGroup>().eq(AlarmGroup::getIsDel, 0));
            Map<Integer, String> groupMap = alarmGroups.stream().collect(Collectors.toMap(AlarmGroup::getId, AlarmGroup::getName));
            List<AlarmCategory> alarmCategories = alarmCategoryMapper.selectList(new LambdaQueryWrapper<AlarmCategory>().eq(AlarmCategory::getIsDel, 0));
            Map<Integer, String> categoryMap = alarmCategories.stream().collect(Collectors.toMap(AlarmCategory::getId, AlarmCategory::getName));

            for (AlarmContentConfig record : records) {
                record.setAlarmGroupLabel(groupMap.get(record.getAlarmGroup()));
                record.setAlarmCategoryName(categoryMap.get(record.getAlarmCategory()));
                record.setAlarmLevelName(alarmLevel.get(String.valueOf(record.getAlarmLevel())).getDictLabel());
                record.setAlarmTypeName(alarmType.get(String.valueOf(record.getAlarmType())).getDictLabel());
            }
        }
        return selectPage;
    }

    public List<AlarmContentConfig> getList() {
        return alarmContentConfigMapper.selectList(new LambdaQueryWrapper<AlarmContentConfig>().eq(AlarmContentConfig::getIsDel, 0));
    }

    public void saveOrUp(AlarmContentConfig entity) {
        SysUser loginUser = userService.getUser();
        Date now = new Date();
        if (Objects.nonNull(entity.getId()) && Objects.nonNull(entity.getIsDel())) {
            if (entity.getIsDel() == 1) {
                alarmContentConfigMapper.deleteById(entity.getId());
                return;
            }
        }
        entity.setUpdateUser(loginUser.getId());
        entity.setUpdateTime(now);
        if (Objects.isNull(entity.getId())) {
            entity.setCreateUser(loginUser.getId());
            entity.setCreateTime(now);
            entity.setIsDel(0);
            alarmContentConfigMapper.insert(entity);
        }else {
            alarmContentConfigMapper.updateById(entity);
        }
    }


    public void downloadTemplate(HttpServletResponse response) {
        //导出excel模板
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("告警内容", StringUtils.EMPTY);
            row.put("告警编号", StringUtils.EMPTY);
            row.put("告警等级", StringUtils.EMPTY);
            row.put("告警类型", StringUtils.EMPTY);
            row.put("告警分组", StringUtils.EMPTY);
            row.put("告警种类", StringUtils.EMPTY);
            row.put("归属对象", StringUtils.EMPTY);
            row.put("内容备注", StringUtils.EMPTY);
            row.put("是否启用",StringUtils.EMPTY);
            list.add(row);
        }
        ExcelWriter writer = ExcelUtil.getWriter();
        // 设置只导出有别名的字段
        writer.setOnlyAlias(true);
        // 设置默认行高
        writer.setDefaultRowHeight(20);
        // 设置冻结行
        writer.setFreezePane(1);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        // 需要必填的字段名称
        List<String> requiredFields = Arrays.asList("告警内容", "告警编号", "告警等级", "告警类型", "告警分组", "告警种类", "归属对象", "是否启用");
        // 需要下拉的字段名称
        List<String> dictFields = Arrays.asList("告警等级", "告警类型", "告警分组", "告警种类", "归属对象", "是否启用");
        // 需要组装的 List<List<?>> rows
        List<List<?>> rows = new ArrayList<>();
        // 需要下拉的列
        List<Integer> firstCols = new ArrayList<>();
        // 得到map
        Map<String, Object> demo = list.get(0);
        // 获取 Map 的键集合
        Object[] keys = demo.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];

            ExportDataUtils.writeCell(writer, i, key, requiredFields.contains(key));

            if (dictFields.contains(key)) {
                List<String> colList = getColList(key);
                rows.add(colList);
                firstCols.add(i);
            }
        }
        // firstCols  转换为数组
        Integer[] firstCol = firstCols.toArray(new Integer[0]);

        ExportDataUtils.writeData(rows, writer, firstCol);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("告警内容导入模板.xlsx", "UTF-8"));
            writer.flush(outputStream, true);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("告警内容导入模板下载失败");
        }
    }

    private List<String> getColList(String type) {
        List<String> list = new ArrayList<>();
        Map<String, List<SysDict>> dictLists = (Map<String, List<SysDict>>) RedisUtils.getBeanValue(MemoryCacheService.allDictListPrefix);
        switch (type) {
            case "告警等级":
                List<SysDict> level = dictLists.get("alarmLevel");
                list = level.stream().distinct().map(SysDict::getDictLabel).collect(Collectors.toList());
                break;
            case "告警类型":
                List<SysDict> category = dictLists.get("alarmCategory");
                list = category.stream().distinct().map(SysDict::getDictLabel).collect(Collectors.toList());
                break;
            case "告警分组":
                List<SysDict> group = dictLists.get("alarmGroup");
                list = group.stream().distinct().map(SysDict::getDictLabel).collect(Collectors.toList());
                break;
            case "告警种类":
                List<SysDict> alarmType = dictLists.get("alarmType");
                list = alarmType.stream().distinct().map(SysDict::getDictLabel).collect(Collectors.toList());
                break;
            case "归属对象":
                List<ConfigDeviceType> configDeviceTypes = configDeviceTypeMapper.selectList(Wrappers.<ConfigDeviceType>lambdaQuery().eq(ConfigDeviceType::getIsDel, G.ISDEL_NO));
                list = configDeviceTypes.stream().distinct().map(ConfigDeviceType::getDeviceTypeName).collect(Collectors.toList());
                break;
            case "是否启用":
                list.add("是");
                list.add("否");
                break;
        }
        return list;
    }


    public void uploadData(MultipartFile file) {
        boolean flag = FileUtils.checkFileSize(file.getSize(), 10, "M");
        if (!flag) {
            throw new CustomException("文件过大，最大支持10M");
        }
        String path = UploadUtils.uploadUUIDFile(file, FilePath);
        if (StrUtil.isBlank(path)) {
            throw new CustomException("上传失败");
        }
        File localFile = new File(path);
        if (!localFile.exists()) {
            throw new CustomException("上传失败");
        }
        ExcelReader reader = ExcelUtil.getReader(localFile);
        List<Map<String, Object>> readAll = reader.readAll();
        if (CollectionUtils.isEmpty(readAll)) {
            throw new RuntimeException("导入的数据为空");
        }
        // 得到字典匹配的集合
        Map<String, List<?>> dictsMap = getDictsMap();
        // 需要必填的字段名称
        List<String> requiredFields = Arrays.asList("告警内容", "告警编号", "告警等级", "告警类型", "告警分组", "告警种类", "归属对象", "是否启用");
        // 需要下拉的字段名称
        List<String> dictFields = Arrays.asList("告警等级", "告警类型", "告警分组", "告警种类", "归属对象", "是否启用");

        SysUser loginUser = userService.getUser();
        Date now = new Date();

        for (Map<String, Object> map : readAll) {
            // map的所有value 去除前后空格
            map.forEach((k, v) -> map.put(k, null == v ? "" : v.toString().trim()));
            // 行号
            int rowNum = readAll.indexOf(map) + 1;

            AlarmContentConfig config = new AlarmContentConfig();
            for (String key : map.keySet()) {
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                String value;
                // 判断是否验证必填字段
                if (requiredFields.contains(key)) {
                    if (StringUtils.isBlank(MapUtils.getString(map, key))) {
                        throw new CustomException("第" + rowNum + "行，" + key + "不能为空");
                    }
                }
                value = MapUtils.getString(map, key);
                if ("告警编号".equals(key)) {
                    AlarmContentConfig selectOne = alarmContentConfigMapper.selectOne(new LambdaQueryWrapper<AlarmContentConfig>().eq(AlarmContentConfig::getAlarmCode, value).eq(AlarmContentConfig::getIsDel, 0));
                    if (Objects.nonNull(selectOne)) {
                        throw new CustomException("第" + rowNum + "行，告警编号已存在");
                    }
                }
                // 验证类型字段是否可以在数据库中找得到
                if (StringUtils.isNotBlank(value) && dictFields.contains(key)) {
                    try {
                        if ("归属对象".equals(key)) {
                            config.setDeviceTypeName(value);
                        }
                        value = getImportVerifySelect(key, value, dictsMap.get(key));
                    } catch (Exception e) {
                        throw new CustomException("第" + rowNum + "行," + key + "不存在");
                    }
                }
                getImportData(key, value, config);
            }
            config.setIsDel(0);
            config.setCreateTime(now);
            config.setUpdateTime(now);
            config.setCreateUser(loginUser.getId());
            config.setUpdateUser(loginUser.getId());
            alarmContentConfigMapper.insert(config);
        }
    }

    private Map<String, List<?>> getDictsMap() {
        Map<String, List<?>> map = new HashMap<>();
        Map<String, List<SysDict>> dictLists = (Map<String, List<SysDict>>) RedisUtils.getBeanValue(MemoryCacheService.allDictListPrefix);
        List<ConfigDeviceType> configDeviceTypes = configDeviceTypeMapper.selectList(Wrappers.<ConfigDeviceType>lambdaQuery().eq(ConfigDeviceType::getIsDel, G.ISDEL_NO));
        map.put("告警等级", dictLists.get("alarmLevel"));
        map.put("告警类型", dictLists.get("alarmCategory"));
        map.put("告警分组", dictLists.get("alarmGroup"));
        map.put("告警种类", dictLists.get("alarmType"));
        map.put("归属对象", configDeviceTypes);
        List<SysDict> enableList = new ArrayList<>();
        SysDict yes = new SysDict();
        yes.setDictValue("1");
        yes.setDictLabel("是");
        SysDict no = new SysDict();
        no.setDictValue("0");
        no.setDictLabel("否");
        enableList.add(yes);
        enableList.add(no);
        map.put("是否启用", enableList);
        return map;
    }

    private String getImportVerifySelect(String type, String value, List<?> list) throws RuntimeException {
        String val = "";
        switch (type) {
            case "告警等级":
                val = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
            case "告警类型":
                val = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
            case "告警分组":
                val = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
            case "告警种类":
                val = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
            case "归属对象":
                val = ExportDataUtils.getListValue(list, "deviceTypeName", value, "id");
                break;
            case "是否启用":
                val = ExportDataUtils.getListValue(list, "dictLabel", value, "dictValue");
                break;
        }
        return val;
    }

    private AlarmContentConfig getImportData(String type, String value, AlarmContentConfig config) {
        if (StrUtil.isBlank(type)) {
            return config;
        }
        switch (type) {
            case "告警内容":
                config.setAlarmContent(value);
                break;
            case "告警编号":
                config.setAlarmCode(value);
                break;
            case "告警等级":
                config.setAlarmLevel(Integer.parseInt(value));
                break;
            case "告警类型":
                config.setAlarmCategory(Integer.parseInt(value));
                break;
            case "告警分组":
                config.setAlarmGroup(Integer.parseInt(value));
                break;
            case "告警种类":
                config.setAlarmType(Integer.parseInt(value));
                break;
            case "归属对象":
                config.setDeviceType(Integer.parseInt(value));
                break;
            case "内容备注":
                config.setRemark(value);
                break;
            case "是否启用":
                config.setEnable(Integer.parseInt(value));
                break;
        }
        return config;
    }

    public List<AlarmGroupTreeVO> treeByGroup(Integer planId, String disable) {
        List<String> checkedList = new ArrayList<>();
        if (Objects.nonNull(planId)) {
            AlarmPlanConfig plan = alarmPlanConfigMapper.selectById(planId);
            checkedList = Arrays.asList(plan.getAlarmContent().split(","));
        }
        List<AlarmGroup> groups = alarmGroupMapper.selectList(new LambdaQueryWrapper<AlarmGroup>().eq(AlarmGroup::getIsDel, 0));
        List<AlarmGroupTreeVO> result = new ArrayList<>();
        for (AlarmGroup group : groups) {
            AlarmGroupTreeVO groupVO = new AlarmGroupTreeVO();
            groupVO.setId("group" + group.getId());
            groupVO.setName(group.getName());
            List<AlarmCategory> categoryList = alarmCategoryMapper.selectList(new LambdaQueryWrapper<AlarmCategory>().eq(AlarmCategory::getIsDel, 0).eq(AlarmCategory::getAlarmGroup, group.getId()));
            for (AlarmCategory category : categoryList) {
                AlarmGroupTreeVO categoryVO = new AlarmGroupTreeVO();
                categoryVO.setId("category" + category.getId());
                categoryVO.setName(category.getName());
                List<AlarmContentConfig> contentList = alarmContentConfigMapper.selectList(new LambdaQueryWrapper<AlarmContentConfig>().eq(AlarmContentConfig::getIsDel, 0).eq(AlarmContentConfig::getAlarmCategory, category.getId()));
                for (AlarmContentConfig content : contentList) {
                    AlarmGroupTreeVO contentVO = new AlarmGroupTreeVO();
                    contentVO.setId(String.valueOf(content.getId()));
                    contentVO.setName(content.getAlarmContent());
                    contentVO.setChildren(null);
                    boolean check = checkedList.contains(String.valueOf(content.getId()));
                    if (check) {
                        contentVO.setCheck(true);
                        categoryVO.setCheck(true);
                        groupVO.setCheck(true);
                    }else {
                        if ("1".equals(disable)) {
                            AlarmPlanConfig select = alarmPlanConfigMapper.selectByContentId(String.valueOf(content.getId()));
                            contentVO.setDisable(Objects.nonNull(select));
                        }
                    }
                    categoryVO.add(contentVO);
                }
                long count = categoryVO.getChildren().stream().filter(AlarmGroupTreeVO::isDisable).count();
                if (count == categoryVO.getChildren().size()) {
                    categoryVO.setDisable(true);
                }
                groupVO.add(categoryVO);
            }
            long count = groupVO.getChildren().stream().filter(AlarmGroupTreeVO::isDisable).count();
            if (count == groupVO.getChildren().size()) {
                groupVO.setDisable(true);
            }
            result.add(groupVO);
        }

        return result;
    }

    public void exportData(AlarmContentConfigVO vo, HttpServletResponse response) {
        List<AlarmContentConfig> list = alarmContentConfigMapper.selectList(new LambdaQueryWrapper<AlarmContentConfig>()
                .eq(AlarmContentConfig::getIsDel, 0)
                .eq(Objects.nonNull(vo.getAlarmCategory()), AlarmContentConfig::getAlarmCategory, vo.getAlarmCategory())
                .eq(Objects.nonNull(vo.getAlarmLevel()), AlarmContentConfig::getAlarmLevel, vo.getAlarmLevel())
                .eq(Objects.nonNull(vo.getAlarmGroup()), AlarmContentConfig::getAlarmGroup, vo.getAlarmGroup())
                .eq(Objects.nonNull(vo.getDeviceType()), AlarmContentConfig::getDeviceType, vo.getDeviceType())
                .eq(Objects.nonNull(vo.getEnable()), AlarmContentConfig::getEnable, vo.getEnable())
                .and(StrUtil.isNotBlank(vo.getKeyword()), i -> i.like(AlarmContentConfig::getAlarmContent, vo.getKeyword()).or().like(AlarmContentConfig::getDeviceTypeName, vo.getKeyword()))
        );
        if (CollUtil.isNotEmpty(list)) {
            Map<String, HashMap<String, SysDict>> map = (Map<String, HashMap<String, SysDict>>)RedisUtils.getBeanValue("dictData");
            HashMap<String, SysDict> alarmLevel = map.get("alarmLevel");
            HashMap<String, SysDict> alarmType = map.get("alarmType");
            List<AlarmGroup> alarmGroups = alarmGroupMapper.selectList(new LambdaQueryWrapper<AlarmGroup>().eq(AlarmGroup::getIsDel, 0));
            Map<Integer, String> groupMap = alarmGroups.stream().collect(Collectors.toMap(AlarmGroup::getId, AlarmGroup::getName));
            List<AlarmCategory> alarmCategories = alarmCategoryMapper.selectList(new LambdaQueryWrapper<AlarmCategory>().eq(AlarmCategory::getIsDel, 0));
            Map<Integer, String> categoryMap = alarmCategories.stream().collect(Collectors.toMap(AlarmCategory::getId, AlarmCategory::getName));

            for (AlarmContentConfig record : list) {
                record.setAlarmGroupLabel(groupMap.get(record.getAlarmGroup()));
                record.setAlarmCategoryName(categoryMap.get(record.getAlarmCategory()));
                record.setAlarmLevelName(alarmLevel.get(String.valueOf(record.getAlarmLevel())).getDictLabel());
                record.setAlarmTypeName(alarmType.get(String.valueOf(record.getAlarmType())).getDictLabel());
                record.setEnableName(Objects.equals(1, record.getEnable()) ? "启用" : "禁用");
            }
        }
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("告警内容导出.xlsx", "UTF-8"));
            EasyExcel.write(response.getOutputStream(), AlarmContentConfig.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet()
                    .doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("告警内容数据导出失败");
        }

    }

    public AlarmContentConfig getByAlgId(String algId) {
        return alarmContentConfigMapper.selectOne(new LambdaQueryWrapper<AlarmContentConfig>()
                .eq(AlarmContentConfig::getIsDel, 0)
                .eq(AlarmContentConfig::getAlgId, algId)
        );
    }
}
