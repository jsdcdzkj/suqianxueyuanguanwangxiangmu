package com.jsdc.iotpt.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.util.MD5Utils;
import com.jsdc.iotpt.util.MybatisBatchUtils;
import com.jsdc.iotpt.util.TreeParserUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @projectName: IOT
 * @className: SysUserService
 * @author: wp
 * @description:
 * @date: 2023/5/8 16:13
 */
@Service
@Transactional
@Slf4j
public class SysUserService extends BaseService<SysUser> {

    Logger logger = LoggerFactory.getLogger(SysUserService.class);

    @Autowired
    SysOrgDeptService orgDeptService;
    @Autowired
    SysUserRoleService userRoleService;
    @Autowired
    SysRoleMenuService roleMenuService;
    @Autowired
    SysMenuService menuService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private RedisDataInit redisDataInit;


    @Autowired
    private SysOrgManageService sysOrgManageService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TeamGroupUserMapper teamGroupUserMapper;

    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private SysOrgDeptMapper sysOrgDeptMapper;
    @Autowired
    private SysOrgManageMapper sysOrgManageMapper;
    @Autowired
    private MybatisBatchUtils mybatisBatchUtils;

    /**
     * 分页查询
     *
     * @param user
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<SysUser> getUserPage(SysUser user, Integer pageIndex, Integer pageSize) {
        Page<SysUser> page = new Page(pageIndex, pageSize);
        Page<SysUser> p = baseMapper.selectPage(page, getWrapper(user).orderByAsc(SysUser::getId));

        Map<Integer, SysUser> userMap = null;
        try {
            userMap = (Map<Integer, SysUser>) RedisUtils.getBeanValue("userDict");
        } catch (Exception e) {
            // 重新初始化数据
            userMap = redisDataInit.usersInit();
        }
        Map<Integer, SysUser> finalUserMap = userMap;
        p.getRecords().forEach(x -> conventUser(x, finalUserMap));
        return p;
    }

    // 回填 user 信息
    public void conventUser(SysUser bean, Map<Integer, SysUser> userMap) {
        if (null == bean) {
            return;
        }
        String orgName = Optional.ofNullable(bean.getUnitId()).map(sysOrgManageService::getById).map(SysOrgManage::getOrgName).orElse("");
        String deptName = Optional.ofNullable(bean.getDeptId()).map(orgDeptService::getById).map(SysOrgDept::getDeptName).orElse("");

        if (StringUtils.isNotBlank(orgName) && StringUtils.isNotBlank(deptName)) {
            bean.setDeptName(orgName + "-" + deptName);
        } else if (StringUtils.isNotBlank(orgName)) {
            bean.setDeptName(orgName);
        } else if (StringUtils.isNotBlank(deptName)) {
            bean.setDeptName(deptName);
        }

        // 角色
        // 1. 提取用户关联的角色ID，自动过滤无效数据
        List<Integer> roleIds = userRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
                        .select(SysUserRole::getRoleId)
                        .eq(SysUserRole::getUserId, bean.getId())
                        .eq(SysUserRole::getIsDel, G.ISDEL_NO))
                .stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        // 2. 链式处理：仅当角色ID/角色列表非空时，才执行后续赋值操作
        Optional.of(roleIds)
                .filter(CollUtil::isNotEmpty)
                .map(ids -> sysRoleMapper.selectList(Wrappers.<SysRole>lambdaQuery()
                        .in(SysRole::getId, ids)
                        .eq(SysRole::getIsDel, G.ISDEL_NO)))
                .filter(CollUtil::isNotEmpty) // 替换原第二层if判断
                .ifPresent(roles -> {
                    // 一次性提取角色名、角色ID列表（避免重复遍历roles）
                    List<String> roleNames = roles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
                    List<Integer> roleIdList = roles.stream().map(SysRole::getId).collect(Collectors.toList());

                    bean.setRoleNames(roleNames);
                    bean.setRoleIds(roleIdList);

                    // 对齐原逻辑：仅当角色名非空时，才设置拼接字符串
                    Optional.of(roleNames)
                            .filter(CollUtil::isNotEmpty)
                            .map(names -> String.join(",", names))
                            .ifPresent(bean::setRoleNamesStr);
                });


        // 用户名字回显
        bean.setCreateUserName(getMapValueOrDefault(userMap, bean.getCreateUser(), SysUser::getRealName, ""));
        bean.setUpdateUserName(getMapValueOrDefault(userMap, bean.getUpdateUser(), SysUser::getRealName, ""));
        // 头像回显
        bean.setAvatarUrl(Optional.ofNullable(bean.getAvatar()).map(x -> "/minio/previewFile?fileName=" + x).orElse(""));
        bean.setAvatarDownUrl(Optional.ofNullable(bean.getAvatar()).map(x -> "/minio/downMinio?fileName=" + x).orElse(""));
    }

    public static <K, V, R> String getMapValueOrDefault(Map<K, V> map, K key, Function<V, R> getter, String defaultValue) {
        return Optional.ofNullable(map)
                .flatMap(m -> Optional.ofNullable(m.get(key)))
                .map(getter)
                .map(String::valueOf)
                .orElse(defaultValue);
    }


    /**
     * 查询用户信息
     *
     * @param user
     * @return
     */
    public SysUser getUser(SysUser user) {
        SysUser user1 = getOne(getWrapperByMeeting(user));
        // 头像回显
        if (StringUtils.isNotBlank(user1.getAvatar())) {
            user1.setAvatarUrl("/minio/previewFile?fileName=" + user1.getAvatar());
            user1.setAvatarDownUrl("/minio/downMinio?fileName=" + user1.getAvatar());
        }
        if (StringUtils.isNotBlank(user1.getFacePic())) {
            user1.setFacePicUrl("/minio/previewFile?fileName=" + user1.getFacePic());
            user1.setFacePicDownUrl("/minio/downMinio?fileName=" + user1.getFacePic());
        }
        return user1;
    }

    private Wrapper<SysUser> getWrapperByMeeting(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (null != user) {
            wrapper.eq(null != user.getId(), SysUser::getId, user.getId());
            wrapper.eq(null != user.getAreaId(), SysUser::getAreaId, user.getAreaId());
            wrapper.eq(null != user.getDeptId(), SysUser::getDeptId, user.getDeptId());
            wrapper.eq(null != user.getIsEnable(), SysUser::getIsEnable, user.getIsEnable());
            wrapper.like(StringUtils.isNotEmpty(user.getLoginName()), SysUser::getLoginName, user.getLoginName());
            wrapper.like(StringUtils.isNotEmpty(user.getRealName()), SysUser::getRealName, user.getRealName());
            wrapper.like(StringUtils.isNotEmpty(user.getPhone()), SysUser::getPhone, user.getPhone());
            wrapper.eq(null != user.getUnitId(), SysUser::getUnitId, user.getUnitId());
            if (null != user.getRoleId()) {
                List<SysUserRole> userRoles = userRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
                        .select(SysUserRole::getUserId).eq(SysUserRole::getRoleId, user.getRoleId()));
                if (CollectionUtils.isEmpty(userRoles)) {
                    wrapper.eq(SysUser::getId, -1);
                } else {
                    wrapper.in(SysUser::getId, userRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList()));
                }
            }
            if (StringUtils.isNotBlank(user.getNotIds())) {
                List<Integer> ids = Arrays.stream(user.getNotIds().split(",")).map(Integer::valueOf).collect(Collectors.toList());
                wrapper.notIn(SysUser::getId, ids);
            }

            if (!CollectionUtils.isEmpty(user.getIds())) {
                wrapper.in(SysUser::getId, user.getIds());
            }

        }
//        wrapper.eq(SysUser::getIsDel, G.ISDEL_NO);
        return wrapper;
    }


    public static String toPinyin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch > 128) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (pinyinArray != null) {
                        sb.append(pinyinArray[0]);
                    } else {
                        sb.append(ch);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }


    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    public Boolean addUser(SysUser user) throws ClientException, IOException {
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDel(G.ISDEL_NO);
        user.setPassword(MD5Utils.getMD5(G.PLATFORM + user.getPassword()));
//        user.setNameSpelling(PinyinUtil.getPinyin(user.getRealName(),""));
        user.setNameSpelling(toPinyin(user.getRealName()));

        if (save(user)) {
            return userRoleService.updateUserRole(user);
        }
        return false;
    }

    /**
     * 编辑用户
     *
     * @param user
     * @return
     */
    public Boolean editUser(SysUser user) throws ClientException, IOException {
        if (null == user) {
            return false;
        }
        if (null == user.getId()) {
            return false;
        }
        int userId = user.getId();
        SysUser original = getById(userId);
        // 禁用是否变动
        boolean b = false;
        if (!Objects.equals(user.getIsEnable(), original.getIsEnable())) {
            b = true;
        }
        // 头像是否变动
        boolean up = false;
        String url = "";
        if (!Objects.equals(user.getAvatar(), original.getAvatar())) {
            user.setFacePic(url);
        }
        String newPassword = user.getPassword();// 新密码
        String oldPassword = original.getPassword();// 旧密码
        user.setDahuaUserId(original.getDahuaUserId());
        BeanUtil.copyProperties(user, original, CopyOptions.create().ignoreNullValue().setIgnoreCase(true));

        if (StringUtils.equals(newPassword, oldPassword)) {
            user.setOldPassWord("");
            original.setPassword(null);
        } else {
            user.setOldPassWord(user.getPassword());
            user.setPassword(MD5Utils.getMD5(G.PLATFORM + newPassword));
            original.setPassword(MD5Utils.getMD5(G.PLATFORM + newPassword));
        }
        original.setUpdateUser(getUser().getId());
        original.setUpdateTime(new Date());
        original.setNameSpelling(toPinyin(original.getRealName()));
        if (updateById(original)) {
            redisDataInit.updateUsersCache(original);
            return userRoleService.updateUserRole(original);
        }


        return false;
    }


    /**
     * 编辑用户
     *
     * @param user
     * @return
     */
    public Boolean editAppUser(SysUser user) {
        if (null == user) {
            return false;
        }
        if (null == user.getId()) {
            return false;
        }
        int userId = user.getId();
        SysUser original = getById(userId);
        long dahuaUserId = original.getDahuaUserId();
        String oldPassword = original.getPassword();
        BeanUtil.copyProperties(user, original, CopyOptions.create().ignoreNullValue().setIgnoreCase(true));
        original.setPassword(null);
        original.setUpdateUser(getUser().getId());
        original.setUpdateTime(new Date());
        original.setDahuaUserId(dahuaUserId);
        if (updateById(original)) {
            redisDataInit.updateUsersCache(user);
            return true;
        }
        return false;
    }


    // 验证用户名是否存在, true 存在, false 不存在
    public boolean checkUserName(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getLoginName, user.getLoginName());
        wrapper.ne(null != user.getId(), SysUser::getId, user.getId());
        wrapper.eq(SysUser::getIsDel, G.ISDEL_NO);
        return count(wrapper) > 0;
    }

    /**
     * 启用禁用用户
     *
     * @param user
     * @return
     */
    public boolean doEnable(SysUser user) throws ClientException {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, user.getId());
        wrapper.set(SysUser::getIsEnable, user.getIsEnable());
        redisDataInit.updateUsersCache(user);
        return update(wrapper);
    }

    /**
     * 删除用户
     *
     * @param user
     * @return
     */
    public Boolean delUser(SysUser user) throws ClientException {
        user = getById(user.getId());
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, user.getId());
        wrapper.set(SysUser::getIsDel, G.ISDEL_YES);
        redisDataInit.updateUsersCache(user);
        //删除对应的班组人员

        UpdateWrapper<TeamGroupUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("isDel", G.ISDEL_YES).eq("userId", user.getId()).eq("isDel", G.ISDEL_NO);
        teamGroupUserMapper.update(null, updateWrapper);
        return update(wrapper);
    }

    /**
     * 封装查询条件
     *
     * @param user
     * @return
     */
    public LambdaQueryWrapper<SysUser> getWrapper(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (null != user) {
            wrapper.eq(null != user.getId(), SysUser::getId, user.getId());
            wrapper.eq(null != user.getAreaId(), SysUser::getAreaId, user.getAreaId());
            wrapper.eq(null != user.getDeptId(), SysUser::getDeptId, user.getDeptId());
            wrapper.eq(null != user.getIsEnable(), SysUser::getIsEnable, user.getIsEnable());
            wrapper.like(StringUtils.isNotEmpty(user.getLoginName()), SysUser::getLoginName, user.getLoginName());
            wrapper.like(StringUtils.isNotEmpty(user.getRealName()), SysUser::getRealName, user.getRealName());
            wrapper.like(StringUtils.isNotEmpty(user.getPhone()), SysUser::getPhone, user.getPhone());
            wrapper.eq(null != user.getUnitId(), SysUser::getUnitId, user.getUnitId());
            if (null != user.getRoleId()) {
                List<SysUserRole> userRoles = userRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
                        .select(SysUserRole::getUserId).eq(SysUserRole::getRoleId, user.getRoleId()));
                if (CollectionUtils.isEmpty(userRoles)) {
                    wrapper.eq(SysUser::getId, -1);
                } else {
                    wrapper.in(SysUser::getId, userRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList()));
                }
            }
            if (StringUtils.isNotBlank(user.getNotIds())) {
                List<Integer> ids = Arrays.stream(user.getNotIds().split(",")).map(Integer::valueOf).collect(Collectors.toList());
                wrapper.notIn(SysUser::getId, ids);
            }

            if (!CollectionUtils.isEmpty(user.getIds())) {
                wrapper.in(SysUser::getId, user.getIds());
            }

        }
        wrapper.eq(SysUser::getIsDel, G.ISDEL_NO);
        return wrapper;
    }


    /**
     * 获取用户
     *
     * @return
     */
    public SysUser getUser() {
////        if(StpUtil.getLoginId() != null){
////            return this.getById((Serializable) StpUtil.getLoginId());
////        }
//        SysUser sysUser = new SysUser();
//        sysUser.setId(1);
//        sysUser.setLoginName("admin");

        SysUser user = (SysUser) StpUtil.getSession().get(StpUtil.getLoginIdByToken(StpUtil.getTokenValue()).toString());
        // 头像回显
        if (StringUtils.isNotBlank(user.getAvatar())) {
            user.setAvatarUrl("/minio/previewFile?fileName=" + user.getAvatar());
            user.setAvatarDownUrl("/minio/downMinio?fileName=" + user.getAvatar());
        }
        if (StringUtils.isNotBlank(user.getFacePic())) {
            user.setFacePicUrl("/minio/previewFile?fileName=" + user.getFacePic());
            user.setFacePicDownUrl("/minio/downMinio?fileName=" + user.getFacePic());
        }
        return user;
    }

    /**
     * 登录验证账号密码
     *
     * @param username
     * @param password
     * @return
     */
    public SaTokenInfo login(String username, String password) {
        LambdaQueryWrapper<SysUser> queryHandler = Wrappers.<SysUser>lambdaQuery();
        queryHandler.eq(SysUser::getIsDel, 0);
        if (!StringUtils.isEmpty(password)) {
            queryHandler.eq(SysUser::getPassword, MD5Utils.getMD5(G.PLATFORM + password));
        }
        if (!StringUtils.isEmpty(username)) {
            queryHandler.and((wrapper -> {
                wrapper.eq(SysUser::getLoginName, username).or().eq(SysUser::getRealName, username);
            }));
        }
        SysUser user = baseMapper.selectOne(queryHandler);
        if (user == null) {
            return null;
        }

        StpUtil.login(user.getId());
        SaTokenInfo token = StpUtil.getTokenInfo();
        StpUtil.getSession().set(user.getId().toString(), user);
        return token;
    }

    public SysUser loginByCid(String cid) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getIsDel, 0)
                .eq(SysUser::getLoginCid, cid)
        );
    }

    public boolean token(String username, String password) {
        LambdaQueryWrapper<SysUser> queryHandler = Wrappers.<SysUser>lambdaQuery();
        queryHandler.eq(SysUser::getIsDel, 0);
        if (!StringUtils.isEmpty(password)) {
            queryHandler.eq(SysUser::getPassword, MD5Utils.getMD5(G.PLATFORM + password));
        }
        if (!StringUtils.isEmpty(username)) {
            queryHandler.and((wrapper -> {
                wrapper.eq(SysUser::getLoginName, username).or().eq(SysUser::getRealName, username);
            }));
        }
        SysUser user = baseMapper.selectOne(queryHandler);
        if (user == null) {
            return false;
        }
        return true;
    }

    private Integer selectSysMenuById(Integer user_id) {
        SysUser a = sysUserMapper.selectById(user_id);
        if (StrUtil.equals("oQ97D6wke124htKXX-bSR6K5U0ds", a.getWxOpenId())) {
            List<SysUser> b = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getLoginName, "zdq"));
            if (CollUtil.isNotEmpty(b)) {
                return b.get(0).getId();
            }
        }
        return user_id;
    }

    /**
     * 获取用户菜单
     *
     * @param id
     * @param appPlatform
     * @return
     */
    public List<SysMenu> selectSysMenuList(Integer id, Integer appPlatform) {
        // 根据用户得到角色
        List<SysUserRole> userRoles = userRoleService.getBaseMapper().selectList(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getUserId, selectSysMenuById(id)).eq(SysUserRole::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        List<Integer> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        // 根据角色得到菜单
        List<SysRoleMenu> roleMenus = roleMenuService.getBaseMapper().selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .in(SysRoleMenu::getRoleId, roleIds).eq(SysRoleMenu::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }
        List<Integer> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        // 根据菜单得到菜单信息 , 如果不是最上级菜单，需要查询父级菜单
        List<SysMenu> menus = menuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery()
                .in(SysMenu::getId, menuIds).eq(SysMenu::getIsDel, G.ISDEL_NO)
                // 查询已经显示的菜单,隐藏的菜单不显示
                .eq(SysMenu::getIsShow, 1)
                .eq(SysMenu::getSystemId, appPlatform)
                .eq(SysMenu::getMenuType, 1)
                .orderByAsc(SysMenu::getSort)
        );
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // 根据父级菜单查询
        List<Integer> parentIds = menus.stream().map(SysMenu::getParentId).collect(Collectors.toList());
        List<SysMenu> parentMenus = menuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery()
                .in(SysMenu::getId, parentIds).eq(SysMenu::getIsDel, G.ISDEL_NO)
                .orderByAsc(SysMenu::getSort)
        );
        if (!CollectionUtils.isEmpty(parentMenus)) {
            menus.addAll(parentMenus);
        }
        menus = menus.stream().distinct().collect(Collectors.toList());
        return menus;
    }


    public List<String> selectAppSysMenuList(Integer id, Integer appPlatform) {
        // 根据用户得到角色
        List<SysUserRole> userRoles = userRoleService.getBaseMapper().selectList(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getUserId, id).eq(SysUserRole::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        List<Integer> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        // 根据角色得到菜单
        List<SysRoleMenu> roleMenus = roleMenuService.getBaseMapper().selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .in(SysRoleMenu::getRoleId, roleIds).eq(SysRoleMenu::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }
        List<Integer> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        // 根据菜单得到菜单信息 , 如果不是最上级菜单，需要查询父级菜单
        List<SysMenu> menus = menuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery()
                .in(SysMenu::getId, menuIds).eq(SysMenu::getIsDel, G.ISDEL_NO)
                // 查询已经显示的菜单,隐藏的菜单不显示
                .eq(SysMenu::getIsShow, 1)
                .eq(SysMenu::getSystemId, appPlatform)
                .isNotNull(SysMenu::getCode)
        );
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        list = menus.stream().map(SysMenu::getCode).collect(Collectors.toList());
        return list;
    }

    /**
     * 退出登录
     *
     * @return
     */
    public ResultInfo logout() {
//        sysLogService.inset(LogEnums.LOG_EXIT.getValue(), systemName + "：退出登录");

        if (StpUtil.isLogin()) {
            SysUser user = getUser();
            baseMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                    .set(SysUser::getLoginCid, "")
                    .eq(SysUser::getId, user.getId())
            );
            StpUtil.logout();
        }

        return ResultInfo.success();
    }

    /**
     * 根据token得到用户
     *
     * @param accessToken
     * @return
     */
    public SysUser getUserByToken(String accessToken) {
        Object userId = StpUtil.getLoginIdByToken(accessToken);
        if (userId == null) {
            return null;
        }
        // Object 转 Integer
        Integer id = Integer.valueOf(userId.toString());

        SysUser user = getById(id);
        // 头像回显
        if (StringUtils.isNotBlank(user.getAvatar())) {
            user.setAvatarUrl("/minio/previewFile?fileName=" + user.getAvatar());
            user.setAvatarDownUrl("/minio/downMinio?fileName=" + user.getAvatar());
        }
        return user;
    }


    /**
     * 组装菜单到路由
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        // {
        //    path: '/',
        //    component: 'Layout',
        //    redirect: 'index',
        //    children: [
        //      {
        //        path: 'index',
        //        name: 'Index',
        //        component: '@/views/index/index',
        //        meta: {
        //          title: '首页',
        //          icon: 'home',
        //          affix: true,
        //        },
        //      },
        //    ],
        //  },

        for (SysMenu menu : menus) {
            RouterVo router = BeanUtil.copyProperties(menu, RouterVo.class);
//            router.setId(String.valueOf(menu.getId()));
//            router.setParentId(String.valueOf(menu.getParentId()));
            router.setPath(menu.getRouterUrl());
            router.setName(menu.getRouterName());
            router.setRedirect(menu.getRedirectType());
            router.setComponent(menu.getVueUrl());
            router.setComponentUrl(menu.getVueUrl());
            router.setMeta(new MetaVo(menu.getTitle(), menu.getIcon()));

            routers.add(router);
        }

        List<RouterVo> menus1 = TreeParserUtils.getTreeList("0", routers);

//        for (RouterVo routerVo : menus1) {
//            routerVo.setRedirect("noRedirect");
//            Map<String,Object> component = new HashMap<>();
//            component.put("name","Layout");
//            component.put("computed",new HashMap<>());
//            component.put("beforeDestroy",new ArrayList<>());
//            component.put("methods",new HashMap<>());
//            component.put("staticRenderFns",new ArrayList<>());
//            component.put("_compiled",true);
//            component.put("beforeCreate",new ArrayList<>());
//            component.put("__file","src/layouts/index.vue");
//            routerVo.setComponent(component);
//            if (!CollectionUtils.isEmpty(routerVo.getChildren())){
//                for (RouterVo child : routerVo.getChildren()) {
//                    child.setRedirect("noRedirect");
//                    child.setComponent(component);
//                }
//            }
//        }

        return menus1;
    }


    //查询是否登录
    public JSONObject checkOpenid(String openId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", openId);
        List<SysUser> list = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getWxOpenId, openId).eq(SysUser::getIsDel, 0));
        if (!list.isEmpty() && list.size() > 0) {
            SysUser sysUser = list.get(0);
            if (0 == sysUser.getIsEnable()) {
                jsonObject.put("status", "2");
            } else {
                List<SysMenu> authority = selectSysMenuList(sysUser.getId(), G.APP_WISDOM);
                jsonObject.put("status", "1");
                jsonObject.put("authority", authority);
                sysUser.setPassword("");
                jsonObject.put("user", sysUser);
            }
            return jsonObject;

        } else {
            jsonObject.put("status", "0");
            return jsonObject;
        }
    }


    /**
     * 忘记密码
     *
     * @param phone      手机号
     * @param code       验证码
     * @param pwd        密码
     * @param confirmPwd 确认密码
     * @return
     */
    public ResultInfo forgotPwd(String phone, String code, String pwd, String confirmPwd) {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getPhone, phone.trim()));
        if (!users.isEmpty() && users.size() > 0) {
            if (null != RedisUtils.getBeanValue(phone)) {
                String captcha = String.valueOf(RedisUtils.getBeanValue(phone));//生成验证码users.get(0).getTelephone()
                if (code.equals(captcha)) {//验证码输入正确的情况下
                    if (pwd.equals(confirmPwd)) {
                        SysUser sysUser = users.get(0);
                        sysUser.setPassword(MD5Utils.getMD5(G.PLATFORM + pwd));
                        updateById(sysUser);
                        return ResultInfo.success();
                    } else {
                        return ResultInfo.error("两次密码输入不一致");
                    }
                } else {
                    return ResultInfo.error("验证码输入不正确");
                }
            } else {
                return ResultInfo.error("验证码已过期");
            }
        }
        return ResultInfo.error("手机号码不存在");
    }


    //获取验证码
    public ResultInfo SMSMethod(String phone) {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getPhone, phone.trim()));
        if (!users.isEmpty() && users.size() > 0) {
            Integer captcha = (int) ((Math.random() * 9 + 1) * 10000);//生成验证码users.get(0).getTelephone()
            String result = SMSMethod(captcha.toString(), phone);//返回结果
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(result)) {
                JSONObject object = JSON.parseObject(result);
                String code = object.getString("code");
                if ("1".equals(code)) {
                    RedisUtils.setBeanValue(phone, captcha, 1);
                    return ResultInfo.success("短信发送成功");
                } else {
                    return ResultInfo.error("短信发送失败");
                }
            }
        }
        return ResultInfo.error("手机号码不存在");
    }


    /**
     * 发送短信验证码
     *
     * @param sms_code
     * @param sending_phone
     * @return
     */
    public String SMSMethod(String sms_code, String sending_phone) {
        String result = "";
        // 创建HttpClient实例
        HttpClient httpclient = HttpClients.createDefault();
        // 创建POST请求对象，并设置URL
        HttpPost httppost = new HttpPost("http://192.168.0.57:8081/aliSms/smsMethod");
        try {
            // 创建要传递的对象
            // 将对象转换为JSON格式的字符串
            JSONObject params = new JSONObject();
            params.put("sms_code", sms_code);
            params.put("sending_phone", sending_phone);
            // 设置请求头
            httppost.setHeader("Content-Type", "application/json");
            // 设置请求体
            StringEntity s = new StringEntity(params.toString(), "UTF-8");
            s.setContentType("application/json");
            httppost.setEntity(s);
            // 执行请求
            HttpResponse response = httpclient.execute(httppost);
            // 获取响应内容
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            // 处理响应结果
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public SysUser updateImage(MultipartFile files, Integer userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);

//        String url = UploadUtils.uploadFile(files, uploadPath);
        SysFile file = sysFileService.uploadFiles(new UploadParams("/headPortrait", files));
//        try {
//            fileRepository.storePhotoByExt(files, filePath + file_path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println(files.getOriginalFilename());
        sysUser.setHeadPortrait(file.getFileUrl());
        updateById(sysUser);
        return sysUser;
    }

    /**
     * 微信获取openid
     *
     * @param code
     * @return
     */
    @SneakyThrows
    public ResultInfo wxLoginMethond(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        url += "?appid=wx0dfb7e709220b23b";//自己的appid
        url += "&secret=e6693e9cda2d20c5f000fc0a49d3adf3";//自己的appSecret
        url += "&js_code=" + code;
        url += "&grant_type=authorization_code";
        url += "&connect_redirect=1";
        String res = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);    //GET方式
        CloseableHttpResponse response = null;
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom()          // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)                    // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)             // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)                    // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(false).build();           // 将上面的配置信息 运用到这个Get请求里
        httpget.setConfig(requestConfig);                         // 由客户端执行(发送)Get请求
        response = httpClient.execute(httpget);                   // 从响应模型中获取响应实体
        HttpEntity responseEntity = response.getEntity();
        System.out.println("响应状态为:" + response.getStatusLine());
        if (responseEntity != null) {
            res = EntityUtils.toString(responseEntity);
            System.out.println("响应内容长度为:" + responseEntity.getContentLength());
            System.out.println("响应内容为:" + res);
        }
        // 释放资源
        if (httpClient != null) {
            httpClient.close();
        }
        if (response != null) {
            response.close();
        }
        JSONObject jo = JSON.parseObject(res);
        String errcode = jo.getString("errcode");
        if (null != errcode) {
            return ResultInfo.error("服务器无法获取授权码,请使用账号重新登录");
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("openId", jo.getString("openid"));
            return ResultInfo.success(jsonObject);
        }
    }

    public ResultInfo queryInterviewee(String nameOrPhone) {
        List<SysUser> phone = list(Wrappers.<SysUser>lambdaQuery()
                // isdel = 0 and ( phone = nameOrPhone or realName = nameOrPhone or nameSpelling = nameOrPhone )
                .eq(SysUser::getIsDel, 0)
                .and(i ->
                        i.eq(SysUser::getPhone, nameOrPhone).or()
                                .eq(SysUser::getRealName, nameOrPhone).or()
                                .eq(SysUser::getNameSpelling, nameOrPhone)
                )
        );
        phone.forEach(x -> {
            Integer deptId = x.getDeptId();
            String orgName = "";
            String deptName = "";
            if (null != x.getUnitId()) {
                SysOrgManage org = sysOrgManageService.getById(x.getUnitId());
                orgName = null == org ? "" : org.getOrgName();
            }
            if (null != deptId) {
                SysOrgDept dept = orgDeptService.getById(deptId);
                if (null != dept) {
                    deptName = dept.getDeptName();
                }
            }
            if (StringUtils.isNotBlank(orgName) && StringUtils.isNotBlank(deptName)) {
                x.setDeptName(orgName + "-" + deptName);
            } else if (StringUtils.isNotBlank(orgName)) {
                x.setDeptName(orgName);
            } else if (StringUtils.isNotBlank(deptName)) {
                x.setDeptName(deptName);
            }
        });
        return ResultInfo.success(phone);
    }

    public SaTokenInfo skipLogin(String loginName, String phone) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getIsDel, 0);
        queryWrapper.eq(SysUser::getLoginName, loginName);
        queryWrapper.eq(SysUser::getPhone, phone);
        SysUser user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        StpUtil.login(user.getId());
        SaTokenInfo token = StpUtil.getTokenInfo();
        StpUtil.getSession().set(user.getId().toString(), user);
        return token;
    }

    public List<SysUser> getList(SysUser bean) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getIsDel, 0);
        if (bean.getDeptId() != null) {
            queryWrapper.eq(SysUser::getDeptId, bean.getDeptId());
        }
        if (bean.getUnitId() != null) {
            queryWrapper.eq(SysUser::getUnitId, bean.getUnitId());
        }
        return sysUserMapper.selectList(queryWrapper);
    }

    public List getUserTree() {
        List l = new ArrayList<>();
        // 部门
        List<SysOrgDept> depts = orgDeptService.list(Wrappers.<SysOrgDept>lambdaQuery().eq(SysOrgDept::getIsDel, 0).eq(SysOrgDept::getOrgId, 135));
        // 获取有效的部门 ID 集合
        Set<Integer> validDeptIds = depts.stream()
                .map(SysOrgDept::getId)
                .collect(Collectors.toSet());
        // 人员
        List<SysUser> users = this.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, 0));

        Map<Integer, List<SysUser>> map = users.stream()
                // 过滤掉 deptId 为空值的用户
                .filter(user -> user.getDeptId() != null)
                // 过滤掉 deptId 在 depts 中不存在的用户
                .filter(user -> validDeptIds.contains(user.getDeptId()))
                .collect(Collectors.groupingBy(SysUser::getDeptId));

        for (SysOrgDept dept : depts) {
            HashMap<Object, Object> tree = new HashMap<>();
            tree.put("id", dept.getId());
            tree.put("label", dept.getDeptName());
            List<Object> list = new ArrayList<>();
            // 与当前部门
            List<SysUser> collect = map.get(dept.getId());
//            List<SysUser> collect = users.stream().filter(x -> x.getDeptId().equals(dept.getId())).collect(Collectors.toList());
            if (collect != null) {
                for (SysUser u : collect) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("label", u.getRealName());
                    list.add(m);
                }
                tree.put("children", list);
            }
            l.add(tree);
        }
        return l;
    }

    /**
     * 去重后的手机号用户
     *
     * @return
     */
    public List<SysUser> distinctPhoneUsers() {
        return sysUserMapper.selectUserPhone();
    }


    public String accessAuthority(SysUser user) throws ClientException {
        if (null == user.getId()) {
            throw new CustomException("请先添加用户!");
        }
        //通行权限
        List<String> stringList = user.getDoorIds();
        if (!CollectionUtils.isEmpty(stringList)) {
            return "保存成功";
        } else {
            return "请选择权限";
        }
    }


    /**
     * 删除用户
     *
     * @param user
     * @return
     */
    public Boolean signOutUser(SysUser user) throws ClientException {
        user = getById(user.getId());
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, user.getId());
        wrapper.set(SysUser::getIsDel, G.ISDEL_YES);
        wrapper.set(SysUser::getLoginCid, null);
        redisDataInit.updateUsersCache(user);
        return update(wrapper);
    }

    /**
     * 根据用户id查询把权限存入缓存中 app app
     *
     * @param id
     * @param menuList
     */
    public void putSysMenuInRedis(int id, List<SysMenu> menuList, String key) {
        Set<String> set = new HashSet<>();
        if (null != menuList && menuList.size() > 0) {
            menuList.forEach(x -> {
                set.add(x.getCode());
            });
        }
        RedisUtils.setBeanValue(key + id, set);
    }

    /**
     * 验证当前登陆用户是否包含<code>code</code>权限
     *
     * @param code
     * @return
     */
    public boolean validationPermission(String code, String key) {
        return validationPermission(code, key, G.WLW_APP);
    }

    public boolean validationPermission(String code, String key, Integer platform) {
        try {
            SysUser user = getUser();
            //SysUser user = sysUserMapper.selectById(22483);
            Object pObj = RedisUtils.getBeanValue(key + user.getId());
            Set<String> set = new HashSet<>();
            if (null == pObj) {//未从缓存中获取权限
                List<SysMenu> menuList = selectSysMenuList(user.getId(), platform);
                putSysMenuInRedis(user.getId(), menuList, key);

                if (null != menuList && menuList.size() > 0) {
                    Set<String> setw = new HashSet<>();
                    menuList.forEach(x -> {
                        setw.add(x.getCode());
                    });
                    set = setw;
                }
            } else {
                if (pObj instanceof Set) {
                    set = (Set<String>) pObj;
                }
            }
            if (set.contains(code)) {
                log.error("{}权限验证成功===>{}", user.getRealName(), code);
                return true;
            }
        } catch (Exception e) {//当前登陆用户获取失败
            log.error("验证权限异常===>{}", e);
            return false;
        }
        return false;
    }


    public void register(UserRegisterVo vo) throws ClientException, IOException {
        if (StringUtils.isBlank(vo.getPhone())) {
            throw new CustomException("请填写登录手机号！");
        }
        if (StringUtils.isBlank(vo.getPassword()) || StringUtils.isBlank(vo.getRepassword())) {
            throw new CustomException("登录密码不能为空！");
        }
        if (vo.getPassword().length() < 6) {
            throw new CustomException("密码长度不能小于6位！");
        }
        if (!Objects.equals(vo.getPassword(), vo.getRepassword())) {
            throw new CustomException("两次密码输入不一致！");
        }
        SysUser selected = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getPhone, vo.getPhone()));
        if (selected != null) {
            throw new CustomException("手机号已被注册！");
        }
        Date now = new Date();
        SysUser user = new SysUser();
        user.setLoginName(vo.getPhone());
        user.setRealName(vo.getRealName());
        user.setNameSpelling(toPinyin(vo.getRealName()));
        user.setPhone(vo.getPhone());
        user.setIsEnable(1);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setIsDel(G.ISDEL_NO);
        user.setSex(vo.getSex());
        user.setCompany(vo.getCompany());
        user.setAvatar("youkedefault.jpg");
        user.setDeptId(3103);
        user.setUnitId(135);
        user.setPassword(MD5Utils.getMD5(G.PLATFORM + vo.getPassword()));
        user.setIsAppTourist("1");
        if (checkUserName(user)) {
            throw new CustomException("用户名已被注册！");
        }
        user.setCardId(generateIdCard());
        user.setRoleIds(Collections.singletonList(40167)); // 默认角色 访客用户
        user.insert();
        userRoleService.updateUserRole(user);
        user.setOldPassWord(vo.getPassword());
    }

    /**
     * 身份证号生成
     */
    public String generateIdCard() {
        Random random = new Random();
        // 生成随机的身份证号码前17位
        StringBuilder idNumber = new StringBuilder();
        for (int i = 0; i < 17; i++) {
            idNumber.append(random.nextInt(10)); // 随机生成0-9的数字
        }

// 计算身份证号码的校验码
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idNumber.charAt(i) - '0') * weight[i];
        }
        int checkCode = sum % 11;

// 根据计算结果获取校验码
        String[] checkCodes = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String randomIdNumber = idNumber.toString() + checkCodes[checkCode];
        return randomIdNumber;
    }

    /**
     * // 项目初始化时 两个系统的密码不一致 ，但是又要用随便一个密码修改密码为统一
     * // 如果园区密码不对 从统一门户中获取 验证对错
     * 同步密码
     */
    public SysUser synchronizePassword(Integer id, String oldPassWord) {
        SysUser sysUsers = this.getById(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", sysUsers.getPhone());
        jsonObject.put("oldPassWord", oldPassWord);
//        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://smartpark.dincher.cn/dctymh_api/getUserByPhoneAndPassword/", JSONObject.toJSONString(jsonObject), String.class);
//        String msg = JSONObject.parseObject(stringResponseEntity.getBody()).getString("msg");
//        if (msg == null || msg.equals("false")) {
//            return null;
//        }
        return sysUsers;
    }

    /**
     * 从文件导入用户
     *
     * @param minioId
     * @return
     */
    public Map<String, Object> trimAllValues(Map<String, Object> map) {
        map.forEach((k, v) -> map.put(k, null == v ? "" : v.toString().trim()));
        return map;
    }

    // 通用工具方法
    private String getString(Map<String, Object> map, String key) {
        return Optional.ofNullable(map.get(key)).map(Object::toString).orElse("");
    }

    public ResultInfo importUser(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("上传文件不能为空", new RuntimeException("上传文件不能为空"));
            return ResultInfo.error("上传文件不能为空");
        }
        try (ExcelReader reader = ExcelUtil.getReader(file.getInputStream())) {
            List<Map<String, Object>> readAll = reader.readAll().stream().map(this::trimAllValues).collect(Collectors.toList());
            if (CollUtil.isEmpty(readAll)) {
                throw new RuntimeException("导入的数据为空");
            }

            // 部门
            Map<String, Integer> sysDepartmentMap = new HashMap<>();
            sysOrgDeptMapper.selectList(Wrappers.<SysOrgDept>lambdaQuery().eq(SysOrgDept::getIsDel, G.ISDEL_NO)).forEach(sysOrgDept -> {
                sysDepartmentMap.put(sysOrgDept.getDeptName(), sysOrgDept.getId());
            });
            // 角色
            Map<String, Integer> sysRoleMap = new HashMap<>();
            sysRoleMapper.selectList(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getIsDel, G.ISDEL_NO)).forEach(sysRole -> {
                sysRoleMap.put(sysRole.getRoleName(), sysRole.getId());
            });
            // 状态
            Map<String, Integer> statusMap = MapUtil.<String, Integer>builder().put("停用", 0).put("启用", 1).map();
            // 所属单位
            Map<String, Integer> orgMap = new HashMap<>();
            sysOrgManageMapper.selectList(Wrappers.<SysOrgManage>lambdaQuery().eq(SysOrgManage::getIsDel, G.ISDEL_NO)).forEach(sysOrgManage -> {
                orgMap.put(sysOrgManage.getOrgName(), sysOrgManage.getId());
            });


            List<SysUser> list = CollUtil.newArrayList();
            List<Map<String, Object>> errorList = new ArrayList<>();
            for (Map<String, Object> map : readAll) {
                // 行号
                map.put("rowNum", readAll.indexOf(map) + 2);

                SysUser bean = new SysUser();

                // 错误集合
                List<String> errList = CollUtil.newArrayList();

                // 用户名称
                String readName = getString(map, "用户名称");
                if (StrUtil.isBlank(readName)) {
                    map.put("用户名称tip", "用户名称不能为空 ");
                    errList.add("用户名称不能为空 ");
                } else {
                    bean.setRealName(readName);
                }

                // 联系方式
                String readPhone = getString(map, "联系方式");
                bean.setPhone(readPhone);

                // 登录名称
                String readLoginName = getString(map, "登录名称");
                if (StrUtil.isBlank(readLoginName)) {
                    map.put("登录名称tip", "登录名称不能为空 ");
                    errList.add("登录名称不能为空 ");
                } else {
                    // 验证登录名是否存在
                    SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0)
                            .eq(SysUser::getLoginName, readLoginName)
                    );
                    if (sysUser != null) {
                        map.put("登录名称tip", "登录名称已存在 ");
                        errList.add("登录名称已存在 ");
                    } else {
                        bean.setLoginName(readLoginName);
                    }
                }

                // 用户密码
                String readPassword = getString(map, "用户密码");
                if (StrUtil.isBlank(readPassword)) {
                    map.put("用户密码tip", "用户密码不能为空 ");
                    errList.add("用户密码不能为空 ");
                } else {
                    bean.setPassword(MD5Utils.getMD5(G.PLATFORM + readPassword));
                }

                // 用户状态
                String readStatus = getString(map, "用户状态");
                if (StrUtil.isBlank(readStatus)) {
                    map.put("用户状态tip", "用户状态不能为空 ");
                    errList.add("用户状态不能为空 ");
                } else {
                    bean.setIsEnable(statusMap.get(readStatus));
                }

                // 所属单位
                String readOrg = getString(map, "所属单位");
                if (StrUtil.isBlank(readOrg)) {
                    map.put("所属单位tip", "所属单位不能为空 ");
                    errList.add("所属单位不能为空 ");
                } else {
                    bean.setUnitId(orgMap.get(readOrg));
                }

                // 所属部门
                String readDept = getString(map, "所属部门");
                if (StrUtil.isBlank(readDept)) {
                    map.put("所属部门tip", "所属部门不能为空 ");
                    errList.add("所属部门不能为空 ");
                } else {
                    bean.setDeptId(sysDepartmentMap.get(readDept));
                }

                // 用户角色
                String readRole = getString(map, "用户角色");
                if (StrUtil.isBlank(readRole)) {
                    map.put("用户角色tip", "用户角色不能为空 ");
                    errList.add("用户角色不能为空 ");
                } else {
                    bean.setRoleId(sysRoleMap.get(readRole));
                }

                // 备注
                String readRemark = getString(map, "备注");
                bean.setMemo(readRemark);

                if (CollectionUtils.isEmpty(errList)) {
                    bean.setCreateTime(new Date());
                    bean.setUpdateTime(new Date());
                    bean.setIsDel(G.ISDEL_NO);
                    bean.setNameSpelling(Optional.ofNullable(bean.getRealName()).map(SysUserService::toPinyin).orElse(""));

//                    if (save(user)) {
//                        return userRoleService.updateUserRole(user);
//                    }
                    list.add(bean);
                } else {
                    map.put("error", errList);
                    errorList.add(map);
                }
            }
            if (!CollectionUtils.isEmpty(errorList)) {
                ResultInfo resultInfo = ResultInfo.error("导入失败");
                resultInfo.setData(errorList);
                return resultInfo;
            }

//            mybatisBatchUtils.batchUpdateOrInsert(list, SysUserMapper.class, (item, mapper) -> mapper.insert(item));
//            mybatisBatchUtils.batchUpdateOrInsert(inventoryDetails, InventoryDetailMapper.class,  (item, uiConfigDetailMapper) -> uiConfigDetailMapper.insert(item));
            for (SysUser bean : list) {
                if (save(bean)) {
                    userRoleService.updateUserRole(bean);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("导入失败: ", e);
            return ResultInfo.error("导入失败: " + e.getMessage());
        }
        return ResultInfo.success();
    }
}
