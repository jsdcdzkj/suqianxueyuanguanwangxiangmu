package com.jsdc.iotpt.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.SysOrgDeptMapper;
import com.jsdc.iotpt.mapper.SysOrgManageMapper;
import com.jsdc.iotpt.mapper.SysRoleMapper;
import com.jsdc.iotpt.model.SysRole;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.MD5Utils;
import com.jsdc.iotpt.util.StrongPasswordValidatorUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @projectName: IOT
 * @className: SysUserController
 * @author: wp
 * @description:
 * @date: 2023/5/8 14:12
 */
@RestController
@RequestMapping("sysuser")
@Api(tags = "系统用户管理")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysOrgDeptMapper sysOrgDeptMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysOrgManageMapper sysOrgManageMapper;
    @Autowired
    private MinioTemplate minioTemplate;


    @GetMapping("getUserPage.do")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo getUserPage(SysUser user, @RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {
        try {
            Page<SysUser> page = sysUserService.getUserPage(user, pageIndex, pageSize);
            return ResultInfo.success(page);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取列表失败!");
        }

    }

    @PostMapping("getUser.do")
    @ApiOperation("查询用户信息")
    public ResultInfo getUser(SysUser user) {
        return ResultInfo.success(sysUserService.getUser(user));
    }


    /**
     * 得到redis数据字典返回map
     */
    @PostMapping("/getRedisUserDictMap")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisUserDictMap() {
        return ResultInfo.success(RedisUtils.getBeanValue("userDict"));
    }

    @PostMapping("addUser.do")
    @ApiOperation("新增用户")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_SAVE, model = Constants.MODEL_YYZT)
    public ResultInfo addUser(@RequestBody SysUser user) throws ClientException, IOException {
        if (sysUserService.checkUserName(user)) {
            return ResultInfo.error("用户名已存在");
        }
        // 验证密码是否大于6位
        if (user.getPassword().length() < 6) {
            return ResultInfo.error("密码长度不能小于6位");
        }
        if (sysUserService.addUser(user)) {
            return ResultInfo.success(user.getId());
        } else {
            return ResultInfo.error("新增用户失败");
        }
    }

    @PostMapping("editUser.do")
    @ApiOperation("编辑用户")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_UPDATE, model = Constants.MODEL_YYZT)
    public ResultInfo editUser(@RequestBody SysUser user) throws ClientException, IOException {
        try {
            if (sysUserService.checkUserName(user)) {
                return ResultInfo.error("用户名已存在");
            }
            // 验证密码是否大于6位
            if (user.getPassword().length() < 6) {
                return ResultInfo.error("密码长度不能小于6位");
            }
            if (sysUserService.editUser(user)) {
                return ResultInfo.success(user.getId());
            } else {
                return ResultInfo.error("编辑用户失败");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("编辑用户失败!");
        }

    }

    @PostMapping("changePassword.do")
    @ApiOperation("修改密码")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER, model = Constants.MODEL_ZHYY)
    public ResultInfo changePassword(@RequestBody SysUser user) throws ClientException, IOException {
        try {
            if (StrUtil.isBlank(user.getOldPassWord())) {
                throw new CustomException("原密码不能为空");
            }
            // 得到当前登录的用户
            SysUser current = sysUserService.getUser();
            // 比较原密码 和当前用户密码是否一样
            if (!StrUtil.equals(MD5Utils.getMD5(G.PLATFORM + user.getOldPassWord()), current.getPassword())) {
//                SysUser sysUser = this.sysUserService.synchronizePassword(current.getId(), user.getOldPassWord());
//                if (sysUser == null) {
                throw new CustomException("原密码错误");
//                }
            }

            // 验证密码强度
            Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(user.getPassword());
            if (map.get("code").equals("-1")) {
                throw new CustomException(map.get("msg"));
            }
            try {
                sysUserService.update(Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getId, current.getId())
                        .set(SysUser::getPassword, MD5Utils.getMD5(G.PLATFORM + user.getPassword()))
                );
            } catch (Exception e) {
                throw new CustomException("修改密码失败, " + e.getMessage());
            }
            SysUser newUser = sysUserService.getBaseMapper().selectById(current.getId());
            newUser.setOldPassWord(user.getPassword());
            List<SysUser> objects = new ArrayList<>();
            objects.add(newUser);

            return ResultInfo.success("操作成功", new LogVo("修改密码成功"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("修改密码失败: " + e.getMessage());
        }
    }

    @PostMapping("delUser.do")
    @ApiOperation("删除用户")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_DELETE, model = Constants.MODEL_YYZT)
    public ResultInfo delUser(SysUser user) throws ClientException {
        if (sysUserService.delUser(user)) {
            return ResultInfo.success("删除成功", new LogVo("删除用户成功"));
        } else {
            return ResultInfo.error("删除用户失败");
        }
    }

    @PostMapping("doEnable.do")
    @ApiOperation("启用/禁用用户")
    @LogInfo(LogEnums.LOG_SYSTEM_USER)
    public ResultInfo doEnable(SysUser user) throws ClientException {
        if (sysUserService.doEnable(user)) {
            return ResultInfo.success("启动/禁用用户成功", new LogVo("启动/禁用用户成功"));
        } else {
            return ResultInfo.error("启用/禁用用户失败");
        }
    }

    @PostMapping("getUserTree.do")
    @ApiOperation("分组树")
    public ResultInfo getUserTree() {
        try {
            return ResultInfo.success(sysUserService.getUserTree());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取分组树失败");
        }
    }

    /**
     * 添加通行权限
     */
    @RequestMapping("/accessAuthority")
    @ApiOperation("添加通行权限")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_AUTHORITY, model = Constants.MODEL_YYZT)
    public ResultInfo accessAuthority(@RequestBody SysUser user) throws ClientException {
        return ResultInfo.success(sysUserService.accessAuthority(user));
    }


    private static final List<String> VALIDATION_FIELDS = Arrays.asList(
            "用户状态", "所属单位", "所属部门", "用户角色");
    public static final String[] COLUMN_TYPES = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");

    /**
     * 导出用户模版
     *
     * @param response
     */
    @PostMapping("/toExportTemplate")
    @ResponseBody
    public void exportTemplate(HttpServletResponse response) {
        // 1. 根据ids查询数据
        List<Map<String, Object>> list = new ArrayList<>();
        // 部门
        Map<Integer, SysOrgDept> sysDepartmentMap = new HashMap<>();
        sysOrgDeptMapper.selectList(Wrappers.<SysOrgDept>lambdaQuery().eq(SysOrgDept::getIsDel, G.ISDEL_NO)).forEach(sysOrgDept -> {
            sysDepartmentMap.put(sysOrgDept.getId(), sysOrgDept);
        });
        // 角色
        Map<Integer, SysRole> sysRoleMap = new HashMap<>();
        sysRoleMapper.selectList(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getIsDel, G.ISDEL_NO)).forEach(sysRole -> {
            sysRoleMap.put(sysRole.getId(), sysRole);
        });
        // 状态
        Map<Integer, String> statusMap = MapUtil.<Integer, String>builder().put(0, "停用").put(1, "启用").map();
        // 所属单位
        Map<Integer, SysOrgManage> orgMap = new HashMap<>();
        sysOrgManageMapper.selectList(Wrappers.<SysOrgManage>lambdaQuery().eq(SysOrgManage::getIsDel, G.ISDEL_NO)).forEach(sysOrgManage -> {
            orgMap.put(sysOrgManage.getId(), sysOrgManage);
        });


        for (int i = 0; i < 10; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("用户名称", StrUtil.EMPTY);
            row.put("登录名称", StrUtil.EMPTY);
            row.put("联系方式", StrUtil.EMPTY);
            row.put("用户密码", StrUtil.EMPTY);
            row.put("用户状态", StrUtil.EMPTY);
            row.put("所属单位", StrUtil.EMPTY);
            row.put("所属部门", StrUtil.EMPTY);
            row.put("用户角色", StrUtil.EMPTY);
            row.put("备注", StrUtil.EMPTY);
            list.add(row);
        }
        // 2. 生成Excel文件
        ExcelWriter writer = ExcelUtil.getWriter();
        // 设置只导出有别名的字段
        writer.setOnlyAlias(true);
        // 设置默认行高
        writer.setDefaultRowHeight(20);
        // 设置冻结行
        writer.setFreezePane(1);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);

        List<Object[]> COLUMN_CONFIG = Arrays.asList(
                new Object[]{"用户名称", true},
                new Object[]{"登录名称", true},
                new Object[]{"联系方式", false},
                new Object[]{"用户密码", true},
                new Object[]{"用户状态", true},
                new Object[]{"所属单位", true},
                new Object[]{"所属部门", true},
                new Object[]{"用户角色", true},
                new Object[]{"备注", false}
        );

        Map<String, Integer> sy = new HashMap<>();
        for (int index = 0; index < COLUMN_CONFIG.size(); index++) {
            Object[] config = COLUMN_CONFIG.get(index);
            writeCell(writer,
                    index,                       // 自动递增的列索引
                    (String) config[0],          // 列标题
                    (Boolean) config[1]          // 必填属性
            );
            sy.put((String) config[0], index);
        }

        // 构建动态列映射
        Map<String, Integer> columnIndexMap = new LinkedHashMap<>();
        for (int i = 0; i < COLUMN_CONFIG.size(); i++) {
            String columnName = (String) COLUMN_CONFIG.get(i)[0];
            columnIndexMap.put(columnName, i);
        }

        // 生成验证字段索引列表（动态匹配）
        List<Integer> validationIndexes = VALIDATION_FIELDS.stream()
                .map(field -> {
                    Integer index = columnIndexMap.get(field);
                    if (index == null) {
                        throw new IllegalStateException("未找到验证字段: " + field);
                    }
                    return index;
                })
                .collect(Collectors.toList());


        List<List<?>> validationData = Arrays.asList(
                extractMapValues(statusMap, String::toString),
                extractMapValues(orgMap, SysOrgManage::getOrgName),
                extractMapValues(sysDepartmentMap, SysOrgDept::getDeptName),
                extractMapValues(sysRoleMap, SysRole::getRoleName)
        );
//
        Integer[] firstCol = {MapUtil.getInt(sy, "用户状态"),
                MapUtil.getInt(sy, "所属单位"),
                MapUtil.getInt(sy, "所属部门"),
                MapUtil.getInt(sy, "用户角色")
        };

        // 动态规则构建（确保顺序对齐）
        IntStream.range(0, validationIndexes.size()).forEach(i -> {
            List<?> cols = validationData.get(i);
            String dictSheet = "dict" + i;
            //创建第二个Sheet
            writer.setSheet(dictSheet);
            //将Sheet2中的数据引用到Sheet1中的下拉框
            Workbook workbook = writer.getWorkbook();
            Name namedCell = workbook.createName();
            namedCell.setNameName(dictSheet);
            //加载数据,将名称为hidden的
            DVConstraint constraint = DVConstraint.createFormulaListConstraint(dictSheet);
            for (int j = 0; j < cols.size(); j++) {
                writer.writeCellValue(i, j, cols.get(j));
            }
            if (CollUtil.isEmpty(cols)) {
                return;
            }
            namedCell.setRefersToFormula(dictSheet + "!$" + COLUMN_TYPES[i] + "$1:$" + COLUMN_TYPES[i] + "$" + cols.size());
            // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
            HSSFDataValidation validation = new HSSFDataValidation(new CellRangeAddressList(1, 1000, firstCol[i], firstCol[i]), constraint);
            writer.getSheets().get(0).addValidationData(validation);

            workbook.setSheetHidden(i + 1, true);
        });

        // 3. 通过response输出流返回文件
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("资产登记模板.xls", "UTF-8"));
            writer.flush(outputStream, true);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/importUser")
    @ResponseBody
    public ResultInfo importUser(MultipartFile file) {
        return sysUserService.importUser(file);
    }


    /**
     * 输入标题到excel
     *
     * @param writer       excel对象
     * @param column       当前列位置
     * @param cellValue    标题内容
     * @param requiredFlag 是否标红
     */
    public void writeCell(ExcelWriter writer, int column, String cellValue, Boolean requiredFlag) {
        writeCell(writer, column, cellValue, requiredFlag, 15);
    }

    private void writeCell(ExcelWriter writer, int column, String cellValue, Boolean requiredFlag, int width) {
        // 根据x,y轴设置单元格内容
        writer.writeCellValue(column, 0, cellValue);
        Font font = writer.createFont();
        font.setBold(true);
        if (requiredFlag) {
            // 根据x,y轴获取当前单元格样式
            CellStyle cellStyle = writer.createCellStyle(column, 0);
            // 内容水平居中
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            // 内容垂直居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置边框
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // 设置高度
            writer.setColumnWidth(column, width);
            // 字体颜色标红
            font.setColor(Font.COLOR_RED);
            cellStyle.setFont(font);
            // 填充前景色
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else {
            // 根据x,y轴获取当前单元格样式
            CellStyle cellStyle = writer.createCellStyle(column, 0);
            // 内容水平居中
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            // 内容垂直居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置边框
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            // 设置高度
            writer.setColumnWidth(column, width);
            // 填充前景色
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cellStyle.setFont(font);
        }
    }

    // 泛型数据提取方法
    public <T> List<String> extractMapValues(Map<?, T> sourceMap, Function<T, String> mapper) {
        return Optional.ofNullable(sourceMap)
                .map(Map::values)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(mapper)
                .collect(Collectors.toList());
    }


}
