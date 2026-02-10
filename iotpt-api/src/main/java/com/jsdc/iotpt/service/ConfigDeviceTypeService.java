package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.ConfigDeviceTypeMapper;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigDeviceTypeVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ConfigDeviceTypeService extends BaseService<ConfigDeviceType> {

    @Autowired
    private ConfigDeviceTypeMapper configDeviceTypeMapper;

    @Autowired
    private SysFileService fileService;

    @Autowired
    private MemoryCacheService memoryCacheService;

    @Value("${jsdc.nginxPath}")
    private String nginxPath;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigDeviceType> getPageList(ConfigDeviceTypeVo vo) {
        QueryWrapper<ConfigDeviceType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0).like(StringUtils.isNotEmpty(vo.getDeviceTypeName()), "deviceTypeName", vo.getDeviceTypeName())
                .like(StringUtils.isNotEmpty(vo.getDeviceTypeCode()), "deviceTypeCode", vo.getDeviceTypeCode());
        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        Page<ConfigDeviceType> configDeviceTypePage = configDeviceTypeMapper.selectPage(new Page<>(vo.getPageNo(),
                vo.getPageSize()), queryWrapper);
        configDeviceTypePage.getRecords().forEach(a -> {
            LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
            wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, a.getId() + "");
            SysFile file = fileService.getOne(wrapper);
            if (StringUtils.isNotNull(file)) {
                file.setFilePath(nginxPath + file.getFileUrl());
            }
            a.setFile(file);
        });
        return configDeviceTypePage;
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigDeviceType> getList(ConfigDeviceType entity) {
        QueryWrapper<ConfigDeviceType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0)
                .eq(StringUtils.isNotEmpty(entity.getDeviceTypeName()), "deviceTypeName", entity.getDeviceTypeName())
                .eq(StringUtils.isNotEmpty(entity.getDeviceTypeCode()), "deviceTypeCode", entity.getDeviceTypeCode())
                .in(StringUtils.isNotEmpty(entity.getDeviceTypeCodes()) && entity.getDeviceTypeCodes().size() > 0, "deviceTypeCode", entity.getDeviceTypeCodes());
        return configDeviceTypeMapper.selectList(queryWrapper);
    }

    public Map<String, String> getMapList(ConfigDeviceType entity) {
        Map<String, String> map = new HashMap<>();
        List<ConfigDeviceType> types = getList(entity);
        for (ConfigDeviceType type : types) {
            map.put(type.getDeviceTypeCode(), type.getDeviceTypeName());
        }
        return map;
    }

    public Map<String, String> getConfigDeviceTypeMap(ConfigDeviceType entity) {
        Map<String, String> map = new HashMap<>();
        List<ConfigDeviceType> types = getList(entity);
        for (ConfigDeviceType type : types) {
            map.put(String.valueOf(type.getId()), type.getDeviceTypeName());
        }
        return map;
    }


    public Map<Integer, String> getConfigDeviceTypeMapDesc(ConfigDeviceType entity) {
        Map<Integer, String> map = new HashMap<>();
        List<ConfigDeviceType> types = getList(entity);
        for (ConfigDeviceType type : types) {
            map.put(type.getId(), type.getDeviceTypeDesc());
        }
        return map;
    }

    public Map<Integer, String> getConfigDeviceTypeMapCode(ConfigDeviceType entity) {
        Map<Integer, String> map = new HashMap<>();
        List<ConfigDeviceType> types = getList(entity);
        for (ConfigDeviceType type : types) {
            map.put(type.getId(), type.getDeviceTypeCode());
        }
        return map;
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigDeviceType(ConfigDeviceType bean) {
        if (!StringUtils.isNotNull(bean.getId())) {
            bean.setIsDel(0);
            bean.setCreateTime(new Date());
        } else {
            bean.setUpdateTime(new Date());
        }
        saveOrUpdate(bean);

//        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
//        wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, bean.getId());
//        fileService.remove(wrapper);
//        if (StringUtils.isNotNull(bean.getFile())) {
//            bean.getFile().setBizType("6");
//            bean.getFile().setBizId(bean.getId() + "");
//            fileService.save(bean.getFile());
//        }

        memoryCacheService.putAllDeviceType();
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ConfigDeviceType getEntityById(Integer id) {
        ConfigDeviceType configDeviceType = getById(id);
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysFile::getBizType, "6").eq(SysFile::getBizId, id + "");
        SysFile file = fileService.getOne(wrapper);
        file.setFileUrl(nginxPath + file.getFileUrl());
        configDeviceType.setFile(file);
        return configDeviceType;
    }

    /**
     * 根据id删除 todo
     *
     * @param id
     * @return
     */
    public ResultInfo delConfigDeviceType(Integer id) {
        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        configDeviceType.setId(id);
        configDeviceType.setIsDel(1);
        updateById(configDeviceType);
        return ResultInfo.success();
    }

    public SysFile uploadDeviceTypeFile(MultipartFile files) {
//        SysFile file = fileService.uploadFiles(new UploadParams("/deviceType", files));
//        file.setFilePath(nginxPath + file.getFileUrl());
//        return file;
        return fileService.minioReturnFile(new UploadParams("/deviceType", files));
    }

}


