package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.AppMessageConfigMapper;
import com.jsdc.iotpt.model.AppMessageConfig;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.AppMessageConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AppMessageConfigService
 *
 * @author 许森森
 * @date 2024/11/18
 */

@Service
@Slf4j
public class AppMessageConfigService extends BaseService<AppMessageConfig> {

    @Autowired
    private AppMessageConfigMapper appMessageConfigMapper;

    @Autowired
    private SysUserService sysUserService;

    public Page<AppMessageConfig> pageList(AppMessageConfigVo vo) {
        Page<AppMessageConfig> page = new Page<>(vo.getPageIndex(), vo.getPageSize());
        HashMap<String, SysDict> appMessageType = RedisDictDataUtil.getDictByType("appMessageType");
        Page<AppMessageConfig> result = page(page, new LambdaQueryWrapper<AppMessageConfig>()
                .eq(AppMessageConfig::getIsDel, 0)
                .eq(StringUtils.isNotBlank(vo.getTemplateCode()), AppMessageConfig::getTemplateCode, vo.getTemplateCode())
                .eq(StringUtils.isNotBlank(vo.getJumpType()), AppMessageConfig::getJumpType, vo.getJumpType())
                .like(StringUtils.isNotBlank(vo.getType()), AppMessageConfig::getType, vo.getType())
                .like(StringUtils.isNotBlank(vo.getTitle()), AppMessageConfig::getTitle, vo.getTitle())
                .like(StringUtils.isNotBlank(vo.getKeyword()), AppMessageConfig::getKeyword, vo.getKeyword())
                .orderByDesc(AppMessageConfig::getId)
        );
        if (CollUtil.isNotEmpty(result.getRecords())) {
            for (AppMessageConfig record : result.getRecords()) {
                SysDict dict = appMessageType.getOrDefault(record.getType(), new SysDict());
                record.setTypeName(dict.getDictLabel());
            }
        }
        return result;
    }

    public List<AppMessageConfig> list(AppMessageConfig config) {
        List<AppMessageConfig> list = list(new LambdaQueryWrapper<AppMessageConfig>()
                .eq(AppMessageConfig::getIsDel, 0)
                .eq(StringUtils.isNotBlank(config.getTemplateCode()), AppMessageConfig::getTemplateCode, config.getTemplateCode())
                .eq(StringUtils.isNotBlank(config.getJumpType()), AppMessageConfig::getJumpType, config.getJumpType())
                .like(StringUtils.isNotBlank(config.getType()), AppMessageConfig::getType, config.getType())
                .like(StringUtils.isNotBlank(config.getTitle()), AppMessageConfig::getTitle, config.getTitle())
                .like(StringUtils.isNotBlank(config.getKeyword()), AppMessageConfig::getKeyword, config.getKeyword())
        );
        if (CollUtil.isNotEmpty(list)) {
            HashMap<String, SysDict> appMessageType = RedisDictDataUtil.getDictByType("appMessageType");
            for (AppMessageConfig record : list) {
                SysDict dict = appMessageType.getOrDefault(record.getType(), new SysDict());
                record.setTypeName(dict.getDictLabel());
            }
        }
        return list;
    }

    public void edit(AppMessageConfig config) {
        Date now = new Date();
        SysUser loginUser = sysUserService.getUser();
        config.setIsDel(0);
        config.setUpdateUser(loginUser.getId());
        config.setUpdateTime(now);
        if (StringUtils.isBlank(config.getTemplateCode())) {
            throw new CustomException("模板代码不能为空");
        }
        AppMessageConfig selectCode = appMessageConfigMapper.selectOne(new LambdaQueryWrapper<AppMessageConfig>()
                .eq(AppMessageConfig::getIsDel, 0)
                .eq(AppMessageConfig::getTemplateCode, config.getTemplateCode()));
        // 新增
        if (Objects.isNull(config.getId())) {
            // code唯一性校验
            if (Objects.nonNull(selectCode)) {
                throw new CustomException("模板代码已存在");
            }
            config.setIsEnable(1);
            config.setCreateUser(loginUser.getId());
            config.setCreateTime(now);
            config.insert();
        }else {
            // code唯一性校验
            if (Objects.nonNull(selectCode) && !Objects.equals(config.getId(), selectCode.getId())) {
                throw new CustomException("模板代码已存在");
            }
            config.updateById();
        }
    }

    public void delete(Integer id) {
        AppMessageConfig select = appMessageConfigMapper.selectById(id);
        if (Objects.nonNull(select)) {
            select.setIsDel(1);
            select.updateById();
        }
    }

    public AppMessageConfig queryById(Integer id) {
        AppMessageConfig selected = appMessageConfigMapper.selectById(id);
        if (Objects.nonNull(selected)) {
            HashMap<String, SysDict> appMessageType = RedisDictDataUtil.getDictByType("appMessageType");
            SysDict dict = appMessageType.getOrDefault(selected.getType(), new SysDict());
            selected.setTypeName(dict.getDictLabel());
        }
        return selected;
    }

    public AppMessageConfig queryByCode(String templateCode) {
        return appMessageConfigMapper.selectOne(new LambdaQueryWrapper<AppMessageConfig>().eq(AppMessageConfig::getIsDel, 0).eq(AppMessageConfig::getTemplateCode, templateCode));
    }

    public List<SysDict> typeList() {
        HashMap<String, SysDict> appMessageType = RedisDictDataUtil.getDictByType("appMessageType");
        if (CollUtil.isNotEmpty(appMessageType)) {
            List<SysDict> list = new ArrayList<>();
            appMessageType.keySet().forEach(e -> list.add(appMessageType.get(e)));
            return list;
        }
        return Collections.emptyList();
    }
}
