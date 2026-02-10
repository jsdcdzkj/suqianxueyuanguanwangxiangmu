package com.jsdc.iotpt.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.util.TreeParserUtils;
import com.jsdc.iotpt.vo.SysMenuVo;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @projectName: IOT
 * @className: SysMenuService
 * @author: wp
 * @description:
 * @date: 2023/5/8 17:03
 */
@Service
@Transactional
public class SysMenuService extends BaseService<SysMenu> {

    @Autowired
    SysUserService userService;

    /**
     * 列表查询
     * @param menu
     * @return
     */
    public List<SysMenu> getMenuList(SysMenu menu){
        LambdaQueryWrapper<SysMenu> wrapper = getWrapper(menu);
        List<SysMenu> list = baseMapper.selectList(wrapper);
        return list;
    }

    /**
     * 分页查询
     * @param menu
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<SysMenu> getMenuPage(SysMenu menu, Integer pageIndex, Integer pageSize){
        Page page = new Page(pageIndex, pageSize);
        Page<SysMenu> p = baseMapper.selectPage(page, getWrapper(menu));
        return p;
    }

    /**
     * 查询菜单信息
     * @param menu
     * @return
     */
    public SysMenu getMenu(SysMenu menu){
        LambdaQueryWrapper<SysMenu> wrapper = getWrapper(menu);
        SysMenu m = getOne(wrapper);
        return m;
    }

    /**
     * 查询菜单树
     * @param menu
     * @return
     */
    public List<Tree<String>> getMenuTree(SysMenu menu){
        List<SysMenu> list = baseMapper.selectList(getWrapper(menu));
        TreeNodeConfig config = new TreeNodeConfig();
        config.setNameKey("id");
        config.setNameKey("label");
        config.setChildrenKey("children");
        List<Tree<String>> treeNodes =
                TreeUtil.build(list, "0", config, (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setName(treeNode.getTitle());
                    tree.setParentId(treeNode.getParentId().toString());
                });
        return treeNodes;
    }

    public List<SysMenuVo> getMenuTree1(SysMenu menu){
        List<SysMenu> list = baseMapper.selectList(getWrapper(menu).orderByAsc(SysMenu::getSort));
        List<SysMenuVo> menus1 = new ArrayList<>();
        for (SysMenu menu1 : list) {
            SysMenuVo menuVo = new SysMenuVo();
            BeanUtil.copyProperties(menu1,menuVo);
            menus1.add(menuVo);
        }
        int topId = 0;
        if(CollectionUtils.isNotEmpty(menus1)){
            topId = Integer.parseInt(menus1.get(0).getParentId());

            for(SysMenuVo sysMenuVo : menus1){
                if(Integer.parseInt(sysMenuVo.getParentId()) < topId){
                    topId = Integer.parseInt(sysMenuVo.getParentId());
                }
            }
        }
        return TreeParserUtils.getTreeList(String.valueOf(topId),menus1);
    }

    /**
     * 新增菜单
     * @param menu
     * @return
     */
    public Boolean addMenu(@NonNull SysMenu menu){
        menu.setIsDel(G.ISDEL_NO);
        if(null == menu.getParentId()){
            menu.setParentId(G.NULL_PARENT_ID);
        }
//        menu.setCreateUser(userService.getUser().getId());
//        menu.setUpdateUser(userService.getUser().getId());
        menu.setCreateTime(new Date());
        menu.setUpdateTime(new Date());
        return save(menu);
    }

    /**
     * 编辑菜单
     * @param menu
     * @return
     */
    public Boolean editMenu(@NonNull SysMenu menu){
        int id = menu.getId();
        SysMenu orignal = getById(id);
        BeanUtil.copyProperties(menu, orignal, CopyOptions.create().setIgnoreCase(true).ignoreNullValue());
        orignal.setParentId(null == menu.getParentId() ? G.NULL_PARENT_ID : menu.getParentId());
        //更新子菜单的系统id
        update(null, Wrappers.<SysMenu>lambdaUpdate()
                .eq(SysMenu::getParentId, id)
                .eq(SysMenu::getIsDel, G.ISDEL_NO)
                .set(SysMenu::getSystemId, menu.getSystemId()));

        return updateById(orignal);
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    public Boolean delMenu(Integer id){
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysMenu::getIsDel, G.ISDEL_YES)
                .eq(SysMenu::getId, id);
        return update(updateWrapper);
    }

    /**
     * 封装查询条件
     * @param menu
     * @return
     */
    private LambdaQueryWrapper<SysMenu> getWrapper(SysMenu menu){
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if(null != menu){
            wrapper.eq(null != menu.getId(), SysMenu::getId, menu.getId());
            wrapper.like(StringUtils.isNotEmpty(menu.getTitle()), SysMenu::getTitle, menu.getTitle());
            wrapper.eq(null != menu.getSystemId(), SysMenu::getSystemId, menu.getSystemId());
            wrapper.eq(null != menu.getParentId(), SysMenu::getParentId, menu.getParentId());
            wrapper.eq(null != menu.getMenuType(), SysMenu::getMenuType, menu.getMenuType());
        }
        wrapper.eq(SysMenu::getIsDel, G.ISDEL_NO);
        return wrapper;
    }

    /**
     * 修改菜单状态
     * @param menu
     * @return
     */
    public boolean doChangeStatus(SysMenu menu) {
        List<Integer> ids = new ArrayList<>();

        // 如果是菜单隐藏
        if (null != menu && null != menu.getIsShow() && 0 == menu.getIsShow()) {
            // 查询当前菜单下面的所有多级子菜单
            List<SysMenu> list = list(Wrappers.<SysMenu>lambdaQuery()
                    .eq(SysMenu::getParentId, menu.getId())
                    .eq(SysMenu::getIsDel, G.ISDEL_NO)
            );
            if (CollUtil.isNotEmpty(list)) {
                // 查询当前菜单下面的所有多级子菜单
                List<Integer> tempIds = list.stream().map(SysMenu::getId).collect(Collectors.toList());
                List<Integer> finalIds = ids;
                list(Wrappers.<SysMenu>lambdaQuery().in(SysMenu::getParentId, tempIds).eq(SysMenu::getIsDel, G.ISDEL_NO))
                        .forEach(m -> {
                            finalIds.add(m.getId());
                        });
                ids.addAll(tempIds);
                ids.addAll(finalIds);
            }
            ids = ids.stream().distinct().collect(Collectors.toList());
        }else {
            // 显示菜单,查看父级菜单是否隐藏
            SysMenu sysMenu = getById(menu.getId());
            if (null != sysMenu && null != sysMenu.getParentId()) {
                SysMenu parentMenu = getById(sysMenu.getParentId());
                if (null != parentMenu && null != parentMenu.getIsShow() && 0 == parentMenu.getIsShow()) {
                    // 父级菜单隐藏,则将父级菜单显示
                    ids.add(parentMenu.getId());
                }
            }
        }
        ids.add(menu.getId());

        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysMenu::getIsShow, menu.getIsShow())
                .in(SysMenu::getId,ids);
        return update(updateWrapper);
    }

    /**
     * 根据类型得到菜单树
     * @param menu
     * @return
     */
    public Object getMenuTypeTree(SysMenu menu) {
        List<SysMenu> list = baseMapper.selectList(getWrapper(menu).orderByAsc(SysMenu::getSort));
        TreeNodeConfig config = new TreeNodeConfig();
        config.setNameKey("id");
        config.setNameKey("label");
        config.setChildrenKey("children");
        List<Tree<String>> treeNodes =
                TreeUtil.build(list, "0", config, (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setName(treeNode.getTitle());
                    tree.setParentId(treeNode.getParentId().toString());
                });
        return treeNodes;
    }
}
