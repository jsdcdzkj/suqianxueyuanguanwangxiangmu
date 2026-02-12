package com.jsdc.iotpt.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.service.SysMenuService;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @projectName: IOT
 * @className: SysMenuController
 * @author: wp
 * @description:
 * @date: 2023/5/9 14:47
 */
@RestController
@RequestMapping("sysmenu")
@Api(tags = "菜单管理")
public class SysMenuController {

    @Autowired
    SysMenuService menuService;

    @PostMapping(value = "getMenu.do")
    @ApiOperation("查询菜单信息")
    public ResultInfo getMenu(SysMenu menu) {
        SysMenu m = menuService.getMenu(menu);
        return ResultInfo.success(m);
    }

    @PostMapping(value = "getMenuList.do")
    @ApiOperation("查询菜单列表")
    public ResultInfo getMenuList(SysMenu menu) {
        List<SysMenu> list = menuService.getMenuList(menu);
        return ResultInfo.success(list);
    }

    @PostMapping(value = "getMenuPage.do")
    @ApiOperation("分页查询菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo getPage(SysMenu menu, @RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {
        Page page = menuService.getMenuPage(menu, pageIndex, pageSize);
        return ResultInfo.success(page);
    }

    @PostMapping(value = "getMenuTree.do")
    @ApiOperation("查询菜单树")
    public ResultInfo getMenuTree(SysMenu menu) {
//        List<Tree<String>> tree = menuService.getMenuTree(menu);
        return ResultInfo.success(menuService.getMenuTree1(menu));
    }

    @PostMapping(value = "saveMenu.do")
    @ApiOperation("保存菜单")
    @LogInfo(value = LogEnums.LOG_SYSTEM_MENU_SAVE, model = Constants.MODEL_YYZT)
    public ResultInfo addMenu(@RequestBody SysMenu menu) {
        if (null != menu.getId() && menuService.getBaseMapper().selectById(menu.getId()) != null) {
            if (menuService.editMenu(menu)) {
                return ResultInfo.success("编辑菜单成功");
            } else {
                return ResultInfo.error("编辑菜单失败");
            }
        } else {
            if (menuService.addMenu(menu)) {
                return ResultInfo.success("保存菜单成功");
            } else {
                return ResultInfo.error("新增菜单失败");
            }
        }
    }

//    @PostMapping(value = "editMenu.do")
//    @ApiOperation("编辑菜单")
//    public ResultInfo editMenu(SysMenu menu){
//        if(menuService.editMenu(menu)){
//            return ResultInfo.success();
//        }else{
//            return ResultInfo.error("编辑菜单失败");
//        }
//    }

    @PostMapping(value = "delMenu.do")
    @ApiOperation("删除菜单")
    @LogInfo(value = LogEnums.LOG_SYSTEM_MENU_DEL, model = Constants.MODEL_YYZT)
    public ResultInfo delMenu(SysMenu menu) {
        if (menuService.delMenu(menu.getId())) {
            return ResultInfo.success("删除菜单成功");
        } else {
            return ResultInfo.error("删除菜单失败");
        }
    }

    @PostMapping(value = "doChangeStatus")
    @ApiOperation("修改显示隐藏菜单状态")
    @LogInfo(value = LogEnums.LOG_SYSTEM_MENU_SHOW, model = Constants.MODEL_YYZT)
    public ResultInfo doChangeStatus(SysMenu menu) {
        if (menuService.doChangeStatus(menu)) {
            return ResultInfo.success(new LogVo("修改显示隐藏菜单状态成功"));
        } else {
            return ResultInfo.error("修改显示隐藏菜单状态失败");
        }
    }

    /**
     * 组装菜单树
     */
    @PostMapping(value = "getMenuTypeTree.do")
    @ApiOperation("查询菜单树")
    public ResultInfo getMenuTypeTree(SysMenu menu) {
        return ResultInfo.success(menuService.getMenuTypeTree(menu));
    }

}
