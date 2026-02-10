package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.UploadUtils;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.DeviceGatewayVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceGatewayService extends BaseService<DeviceGateway> {


    @Value("${jsdc.uploadPath}")
    private String FilePath;

    @Autowired
    private DeviceGatewayMapper deviceGatewayMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;
    @Autowired
    MemoryCacheService memoryCacheService;
    @Autowired
    private ConfigSupplierMapper supplierMapper;
    @Autowired
    private ConfigModelMapper configModelMapper;
    @Autowired
    private ConfigProtocolMapper configProtocolMapper;
    @Autowired
    private ConfigLinkMapper configLinkMapper;
    @Autowired
    private ConfigTopicMapper configTopicMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<DeviceGatewayVo> getPageList(DeviceGatewayVo vo) {
        Page page = new Page(vo.getPageNo(), vo.getPageSize());
        Page<DeviceGatewayVo> pageInfo = deviceGatewayMapper.getPageList(vo, page);
        for (DeviceGatewayVo record : pageInfo.getRecords()) {
            if (StringUtils.isNotEmpty(record.getBuildName()) && StringUtils.isNotEmpty(record.getFloorName()) && StringUtils.isNotEmpty(record.getAreaName())){
                record.setAreaName(record.getBuildName()+"/"+record.getFloorName()+"/"+record.getAreaName());
            }
        }

        return pageInfo;

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<DeviceGateway> getList(DeviceGatewayVo vo) {
        QueryWrapper<DeviceGateway> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        return deviceGatewayMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateDeviceGateway(DeviceGateway bean) {
        saveOrUpdate(bean);

        //当网关修改时更新redis中的信息
        memoryCacheService.changeDeviceGateway();
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public DeviceGatewayVo getEntityById(Integer id) {

        return deviceGatewayMapper.getEntityById(id);
    }

    /**
     * 根据id删除 todo
     *
     * @param id
     * @return
     */
    public ResultInfo delDeviceGateway(Integer id) {
        DeviceGateway deviceGateway = new DeviceGateway();
        deviceGateway.setId(id);
        deviceGateway.setIsDel(1);
        updateById(deviceGateway);
        return ResultInfo.success();
    }

    public ResultInfo edit(DeviceGatewayVo bean) {
        LambdaQueryWrapper<DeviceGateway> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceGateway::getDeviceCode, bean.getDeviceCode()).eq(DeviceGateway::getIsDel, 0).ne(DeviceGateway::getId,bean.getId());
        if (this.count(wrapper) > 0) {
            return ResultInfo.error("设备编码重复，修改失败！");
        }
        bean.setUpdateTime(new Date());
        updateById(bean);
        return ResultInfo.success("编辑成功！");
    }

    public ResultInfo add(DeviceGatewayVo bean) {
        LambdaQueryWrapper<DeviceGateway> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceGateway::getDeviceCode, bean.getDeviceCode()).eq(DeviceGateway::getIsDel, 0);
        if (this.count(wrapper) > 0) {
            return ResultInfo.error("设备编码重复，添加失败！");
        }
        LambdaQueryWrapper<DeviceGateway> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(DeviceGateway::getSubscribeTopicId,bean.getSubscribeTopicId()).eq(DeviceGateway::getIsDel,0);
        if (this.count(wrapper) > 0) {
            return ResultInfo.error("订阅主题重复，添加失败！");
        }
        bean.setCreateTime(new Date());
        bean.setIsDel(0);
        save(bean);
        return ResultInfo.success("新增成功！");
    }


    public ResultInfo uploadFile(List<MultipartFile> files, Integer id) throws Exception {
        SysUser sysUser = sysUserService.getUser();
        boolean falg = false;
        String url = null;
        for (MultipartFile file : files) {
            //判断文件是否大于200M
            falg = FileUtils.checkFileSize(file.getSize(), 200, "M");
            if (!falg) {
                return ResultInfo.error("文件过大，最大200M");
            }
            url = UploadUtils.uploadFile(file, FilePath);
        }
        if (StringUtils.isEmpty(url)) {
            return ResultInfo.error("上传失败");
        } else {
            File file = new File(url);
            if (!file.exists()) {
                throw new Exception("文件不存在!");
            }
            InputStream in = new FileInputStream(file);

            // 读取整个Excel
            XSSFWorkbook sheets = new XSSFWorkbook(in);
            // 获取第一个表单Sheet
            XSSFSheet sheetAt = sheets.getSheetAt(0);

            //默认第一行为标题行，i = 0
            XSSFRow titleRow = sheetAt.getRow(0);
            // 循环获取每一行数据
            for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = sheetAt.getRow(i);
                DeviceCollect collect = new DeviceCollect();
                // 读取每一格内容
//                StringBuilder sb = new StringBuilder();
//                for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
//                    XSSFCell titleCell = titleRow.getCell(index);
//                    XSSFCell cell = row.getCell(index);
//                    cell.setCellType(CellType.STRING);
//                    if (cell.getStringCellValue().equals("")) {
//                        continue;
//                    }
//                    sb.append(cell);
//                }
                XSSFCell cell = row.getCell(0);
                cell.setCellType(CellType.STRING);
                if (cell.getStringCellValue().equals("")) {
                    return ResultInfo.error("必填项不能为空");
                }
                collect.setName(cell.getStringCellValue());
                XSSFCell cell1 = row.getCell(1);
                cell1.setCellType(CellType.STRING);
                if (cell1.getStringCellValue().equals("")) {
                    return ResultInfo.error("必填项不能为空");
                }
                collect.setDeviceDesc(cell1.getStringCellValue());
                XSSFCell cell2 = row.getCell(2);
                cell2.setCellType(CellType.STRING);
                if (cell2.getStringCellValue().equals("")) {
                    return ResultInfo.error("必填项不能为空");
                }
//                collect.setDeviceDesc(cell2.getStringCellValue());
                XSSFCell cell3 = row.getCell(3);
                cell3.setCellType(CellType.STRING);
                if (cell3.getStringCellValue().equals("")) {
                    return ResultInfo.error("必填项不能为空");
                }
//                collect.setDeviceDesc(cell3.getStringCellValue());
                XSSFCell cell4 = row.getCell(4);
                cell4.setCellType(CellType.STRING);
                if (cell4.getStringCellValue().equals("")) {
                    return ResultInfo.error("必填项不能为空");
                }
                if (cell4.getStringCellValue().equals("有")) {
                    collect.setStatus("2");
                } else {
                    collect.setStatus("1");
                }
                XSSFCell cell5 = row.getCell(5);
                cell5.setCellType(CellType.STRING);
                if (cell5.getStringCellValue().equals("")) {
                    continue;
                }
//                collect.setDeviceDesc(cell5.getStringCellValue());
                XSSFCell cell6 = row.getCell(6);
                cell6.setCellType(CellType.STRING);
                if (cell6.getStringCellValue().equals("")) {
                    continue;
                }
//                collect.setDeviceDesc(cell6.toString());
                XSSFCell cell7 = row.getCell(7);
                cell7.setCellType(CellType.STRING);
//                if (cell7.getStringCellValue().equals("")) {
//                   continue;
//                }
//                collect.setDeviceDesc(cell7.toString());
                XSSFCell cell8 = row.getCell(8);
                cell8.setCellType(CellType.STRING);
//                if (cell8.getStringCellValue().equals("")) {
//                    continue;
//                }
//                collect.setDeviceDesc(cell8.toString());
                collect.setUserId(sysUser.getId());
                collect.setGatewayId(id);
                collect.setIsDel(0);
                collect.setPhone(sysUser.getPhone());
                collect.setCreateTime(new Date());
                collect.setCreateUser(sysUser.getId());
                deviceCollectMapper.insert(collect);
            }
        }
        return ResultInfo.success();
    }

    public void exportFile(Integer id, HttpServletResponse response) throws IOException {
        List<DeviceCollectVo> list = this.deviceCollectMapper.getFileOut(id);
        List<List<String>> transferVos = new ArrayList<>();
        List<String> title = new ArrayList<>();
        //封装数据
        for (DeviceCollectVo collect : list) {
            List<String> transferVo = new ArrayList<>();
//            transferVo.setDevName(collect.getName());
//            transferVo.setName(collect.getName());
//            transferVo.setDesc(collect.getDeviceDesc());
//            transferVo.setFunc("0x03,0x06号命令(读写保持寄存器)");
//            transferVo.setReadWriteAttribute("读写");
//            transferVo.setCoefficientK("1.0");
//            transferVo.setSkewB("0.0");
//            transferVo.setUnit("");
//            transferVo.setGroup("0");
//            transferVo.setRegisterAddress("");
//            transferVo.setType("16位有符号");
//            transferVo.setSequence("12");
//            transferVo.setInitialValue("0.0");
//            transferVo.setIsNegation("否");
//            transferVo.setTransform("否");
//            transferVo.setOriginalValue("0");
//            transferVo.setTransformValue("0");
//            transferVo.setSource("采集点");
//            transferVo.setFetchBit("否");
//            transferVo.setStartBit("0");
//            transferVo.setEndBit("0");
//            transferVo.setFetchBit("否");
//            transferVo.setIsAlarm("否");
//            transferVo.setIsTallTallAlarm("否");
//            transferVo.setIsTallAlarm("否");
//            transferVo.setIsLowAlarm("否");
//            transferVo.setIsLowLowAlarm("否");
//            transferVo.setTallTallLimitValue("100.0");
//            transferVo.setTallLimitValue("90.0");
//            transferVo.setLowValue("10.0");
//            transferVo.setLowLowValue("0.0");
//            transferVo.setTallTallMsg("高高报警");
//            transferVo.setTallMsg("高报警");
//            transferVo.setLowMsg("低报警");
//            transferVo.setLowLowMsg("低低报警");
//            transferVo.setSwitchAlarmType("模拟量报警");
//            transferVo.setSwitchAlarmMsg("开关变位");
//            transferVo.setAlarmLevel("通知");
//            transferVo.setStorage("否");
//            transferVo.setStorageType("周期存储");
//            transferVo.setStorageInterval("60");
//            transferVo.setCompressionAccuracy("0.5");
//            transferVo.setIsUpload("是");
//            transferVo.setAstrict("关");
//            transferVo.setMax("1000");
//            transferVo.setMin("0");
//            transferVo.setFormula("原始值 * 系数 + 偏移");
//            transferVo.setControlModel("禁用");
            transferVo.add(collect.getDeviceCode());
            transferVo.add(collect.getSignalTypeCode());
            transferVo.add(collect.getSignalTypeName());
            transferVo.add("0x03,0x06号命令(读写保持寄存器)");
            transferVo.add("读写");
            transferVo.add("1.0");
            transferVo.add("0.0");
            transferVo.add("");
            transferVo.add("0");
            transferVo.add("");
            transferVo.add("16位有符号");
            transferVo.add("12");
            transferVo.add("0.0");
            transferVo.add("否");
            transferVo.add("否");
            transferVo.add("0");
            transferVo.add("0");
            transferVo.add("采集点");
            transferVo.add("否");
            transferVo.add("0");
            transferVo.add("0");
            transferVo.add("否");
            transferVo.add("否");
            transferVo.add("否");
            transferVo.add("否");
            transferVo.add("否");
            transferVo.add("否");
            transferVo.add("100.0");
            transferVo.add("90.0");
            transferVo.add("10.0");
            transferVo.add("0.0");
            transferVo.add("高高报警");
            transferVo.add("高报警");
            transferVo.add("低报警");
            transferVo.add("低低报警");
            transferVo.add("模拟量报警");
            transferVo.add("开关变位");
            transferVo.add("通知");
            transferVo.add("否");
            transferVo.add("周期存储");
            transferVo.add("60");
            transferVo.add("0.5");
            transferVo.add("是");
            transferVo.add("关");
            transferVo.add("1000");
            transferVo.add("0");
            transferVo.add("原始值 * 系数 + 偏移");
            transferVo.add("禁用");
            transferVos.add(transferVo);
        }
        title.add("设备名称(DevName)");
        title.add("名称");
        title.add("描述");
        title.add("功能码");
        title.add("读写属性");
        title.add("系数K");
        title.add("偏移B");
        title.add("变量单位");
        title.add("测点分组");
        title.add("寄存器地址");
        title.add("数据类型");
        title.add("高低位顺序");
        title.add("初始值");
        title.add("是否取反");
        title.add("数值转换");
        title.add("原始值");
        title.add("转换值");
        title.add("来源");
        title.add("按位取值");
        title.add("取值起始位");
        title.add("取值终止位");
        title.add("报警使能");
        title.add("是否开启高高报警");
        title.add("是否开启高报警");
        title.add("是否开启低报警");
        title.add("是否开启低低报警");
        title.add("高高报警限值");
        title.add("高报警限值");
        title.add("低报警限值");
        title.add("低低报警限值");
        title.add("高高报警消息");
        title.add("高报警消息");
        title.add("低报警消息");
        title.add("低低报警消息");
        title.add("报警死区");
        title.add("开关报警类型");
        title.add("开关报警消息");
        title.add("报警等级");
        title.add("存储使能");
        title.add("存储类型");
        title.add("存储间隔(s)");
        title.add("压缩存储精度");
        title.add("是否上传");
        title.add("辅助参数");
        title.add("PV上传标识");
        title.add("PV上传系数");
        title.add("限制使能");
        title.add("最大值");
        title.add("最小值");
        title.add("公式");
        title.add("控制模式");
        title.add("遥调功能码");
        title.add("遥调寄存器");
        title.add("遥调数据类型");
        title.add("遥调字节序");
        title.add("定值组功能码");
        title.add("控分功能码");
        title.add("控分寄存器");
        title.add("控分选择值");
        title.add("控分执行值");
        title.add("控分撤销值");
        title.add("控合功能码");
        title.add("控合寄存器");
        title.add("控合选择值");
        title.add("控合执行值");
        title.add("控合撤销值");
        title.add("Pv码(优先)");
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        List<String> head = title;//取出表头数据
        int rowEleCount = title.size();//表体一共多少列
        //2、创建一个sheet对象并插入数据
        SXSSFSheet sheet = wb.createSheet();//创建一个工作页，并设置sheet的name
        //wb.setSheetName(0,name);//设置sheet名字，可以根据下标设置sheet的name
        sheet.setDefaultColumnWidth(20);//设置默认列宽
        List<CellStyle> headStyleList = getHeadCellStyleList(wb);// 获取一个表头样式集合
//        List<CellStyle> bodyStyleList = getBodyCellStyleList(wb);// 获取一个表体样式集合

        setExcelHead(sheet, head, headStyleList);// 插入表头数据
        if (transferVos.size() > 0) {
            setExcelBody(sheet, transferVos, headStyleList);// 插入表体数据
        }

        //设置content—type
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        //Content-disposition是MIME协议的扩展，MIME协议指示MIME用户代理如何显示附加的文件。
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("数据模板", "UTF-8") +
                ".xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        //将Writer刷新到OutPut
        wb.write(outputStream);
        outputStream.close();
        wb.close();
    }

    public static void setExcelHead(SXSSFSheet sheet, List<String> head, List<CellStyle> cellStyleList) {
        int rowNum = 1;//表头有几行
        int rowElementNum = head.size();//每行有几列
        SXSSFRow row = null;//声明一个行对象
        SXSSFCell cell = null;//声明一个列对象（即一个单元格对象）
        for (int i = 0; i < rowNum; i++) {
            row = sheet.createRow(i);//创建第 i + 1 行的行对象
            //设置行高
//            if(i == 1 || i == 2){//将第二行和第三行的行高设置为400
//                row.setHeight((short) 400);
//            }
            for (int j = 0; j < rowElementNum; j++) {
                cell = row.createCell(j);//创建第 i + 1 行，第 j + 1 列的单元格
                cell.setCellValue(head.get(j));//设置单元格内容（插入数据）
                //设置表头样式
                switch (j) {
                    case 0:
                    case 1:
                    case 2:
                    case 5:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        cell.setCellStyle(cellStyleList.get(0));//将第一行设置为上下左右居中，单元格无边框，字体：等线，字号：12
                        break;
                    default:
                        break;
                }
            }
        }


    }

    //获取表头样式集合
    private static List<CellStyle> getHeadCellStyleList(SXSSFWorkbook wb) {
        List<CellStyle> cellStyleList = new ArrayList<>();
        CellStyle bodyStyle1 = headStyle1(wb);
        cellStyleList.add(bodyStyle1);
        return cellStyleList;
    }

    //表头样式1
    public static CellStyle headStyle1(SXSSFWorkbook wb) {
        CellStyle style1 = wb.createCellStyle();// 样式对象
        // 设置单元格上、下、左、右的边框线
        style1.setBorderTop(BorderStyle.NONE);//NONE为不显示边框，THIN为显示边框
        style1.setBorderBottom(BorderStyle.NONE);
        style1.setBorderLeft(BorderStyle.NONE);
        style1.setBorderRight(BorderStyle.NONE);
        Font font = getFont(wb, "等线", 12);// 创建一个字体对象
        style1.setFont(font);// 设置style1的字体
        //style1.setWrapText(true);// 设置自动换行
//        style1.setAlignment(BorderStyle.ALIGN_CENTER);// 设置单元格字体显示居中（左右方向）
//        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 设置单元格字体显示居中(上下方向)
        return style1;
    }

    //字体
    public static Font getFont(SXSSFWorkbook wb, String fontName, int height) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) height);//设置字号
        font.setFontName(fontName);//设置字体（输入字体名）
        font.setColor(IndexedColors.RED.getIndex());
        return font;
    }

    public static void setExcelBody(SXSSFSheet sheet, List<List<String>> body, List<CellStyle> cellStyleList) {
        int rowNum = body.size();//表头有几行
        int rowElementNum = body.get(0).size();//每行有几列
        SXSSFRow row = null;//声明一个行对象
        SXSSFCell cell = null;//声明一个列对象（即一个单元格对象）
        for (int i = 0; i < rowNum; i++) {
            row = sheet.createRow(i + 1);//创建第 i + 1 行的行对象
            //设置行高
//            if(i == 1 || i == 2){//将第二行和第三行的行高设置为400
//                row.setHeight((short) 400);
//            }
            for (int j = 0; j < rowElementNum; j++) {
                cell = row.createCell(j);//创建第 i + 1 行，第 j + 1 列的单元格
                cell.setCellValue(body.get(i).get(j));//设置单元格内容（插入数据）
                //设置表头样式
                switch (j) {
                    case 0:
                    case 1:
                    case 2:
                    case 5:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        cell.setCellStyle(cellStyleList.get(0));//将第一行设置为上下左右居中，单元格无边框，字体：等线，字号：12
                        break;
                    default:
                        break;
                }
            }
        }
    }



    /**
     * 导出数据模板
     *
     * @param response
     */
    public ResultInfo downloadTemplate(HttpServletResponse response) {
        // 设备名称
        // 设备编码
        // 所属区域
        // 所属建筑
        // 所属楼层
        // 供应商
        // 品牌
        // 设备型号
        // 出厂编号
        // 传输协议
        // 服务连接
        // 发布主题
        // 订阅主题
        // 告警主题
        // 心跳主题
        // 设备描述

        //导出excel模板
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("设备名称",StringUtils.EMPTY);
            row.put("设备编码",StringUtils.EMPTY);
//            row.put("所属区域",StringUtils.EMPTY);
//            row.put("所属建筑",StringUtils.EMPTY);
//            row.put("所属楼层",StringUtils.EMPTY);
            row.put("供应商 ",StringUtils.EMPTY);
            row.put("品牌",StringUtils.EMPTY);
            row.put("设备型号",StringUtils.EMPTY);
            row.put("出厂编号",StringUtils.EMPTY);
            row.put("传输协议",StringUtils.EMPTY);
            row.put("服务连接",StringUtils.EMPTY);
            row.put("发布主题",StringUtils.EMPTY);
            row.put("订阅主题",StringUtils.EMPTY);
            row.put("告警主题",StringUtils.EMPTY);
            row.put("心跳主题",StringUtils.EMPTY);
            row.put("设备描述",StringUtils.EMPTY);
            list.add(row);
        }

        ExcelWriter writer = ExcelUtil.getWriter();
        StyleSet styleSet = writer.getStyleSet();
        Sheet sheet = writer.getSheet();
        //设置下拉数据 从第几行开始
        int firstRow = 1;

        // 设置只导出有别名的字段
        writer.setOnlyAlias(true);
        // 设置默认行高
        writer.setDefaultRowHeight(20);
        // 设置冻结行
        writer.setFreezePane(1);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);

        writeCell(writer, 0,  "设备名称", true);
        writeCell(writer, 1,  "设备编码", true);
//        writeCell(writer, 2,  "所属区域", true);
//        writeCell(writer, 3,  "所属建筑", true);
//        writeCell(writer, 4,  "所属楼层", true);
        writeCell(writer, 2,  "供应商", true);
        writeCell(writer, 3,  "品牌", true);
        writeCell(writer, 4,  "设备型号", true);
        writeCell(writer, 5,  "出厂编号", true);
        writeCell(writer, 6,  "传输协议", true);
        writeCell(writer, 7,  "服务连接", true);
        writeCell(writer, 8,  "发布主题", false);
        writeCell(writer, 9,  "订阅主题", false);
        writeCell(writer, 10, "告警主题", false);
        writeCell(writer, 11, "心跳主题", false);
        writeCell(writer, 12, "设备描述", true);

        // 需要下拉的列 5, 7, 9, 10, 11, 12, 13, 14

        // 供应商
        List<ConfigSupplier> suppliers = supplierMapper.selectList(Wrappers.<ConfigSupplier>lambdaQuery().eq(ConfigSupplier::getIsDel, 0));
        List<String> supplierList = suppliers.stream().distinct().map(ConfigSupplier::getSupplierName).collect(Collectors.toList());
        // 设备型号
        List<ConfigModel> models = configModelMapper.selectList(Wrappers.<ConfigModel>lambdaQuery().eq(ConfigModel::getIsDel, 0));
        List<String> modelList = models.stream().distinct().map(ConfigModel::getModelName).collect(Collectors.toList());
        // 传输协议
        List<ConfigProtocol> protocols = configProtocolMapper.selectList(Wrappers.<ConfigProtocol>lambdaQuery().eq(ConfigProtocol::getIsDel, 0).orderByDesc(ConfigProtocol::getId));
        List<String> protocolList = protocols.stream().distinct().map(ConfigProtocol::getProtocolName).collect(Collectors.toList());
        // 服务连接
        List<ConfigLink> links = configLinkMapper.selectList(Wrappers.<ConfigLink>lambdaQuery().eq(ConfigLink::getIsDel, "0").orderByDesc(ConfigLink::getId));
        List<String> linkList = links.stream().distinct().map(ConfigLink::getLinkName).collect(Collectors.toList());
        // 发布主题
        List<ConfigTopic> topics1 = configTopicMapper.selectList(Wrappers.<ConfigTopic>lambdaQuery().eq(ConfigTopic::getIsDel, 0).eq(ConfigTopic::getTopicType, 2).orderByDesc(ConfigTopic::getId));
        List<String> topicList1 = topics1.stream().distinct().map(ConfigTopic::getTopicCode).collect(Collectors.toList());
        // 订阅主题
        List<ConfigTopic> topics2 = configTopicMapper.selectList(Wrappers.<ConfigTopic>lambdaQuery().eq(ConfigTopic::getIsDel, 0).eq(ConfigTopic::getTopicType, 1).orderByDesc(ConfigTopic::getId));
        List<String> topicList2 = topics2.stream().distinct().map(ConfigTopic::getTopicCode).collect(Collectors.toList());
        // 告警主题
        List<String> topicList3 = topicList2;
        // 心跳主题
        List<String> topicList4 = topicList2;


        //构建数据
        List<List<?>> rows = CollUtil.newArrayList(supplierList, modelList, protocolList, linkList, topicList1, topicList2, topicList3, topicList4);

        String[] types = {"A", "B","C", "D", "E", "F", "G", "H", "I", "J", "K", "L","M", "N","O", "P", "Q", "R", "S", "T", "U", "V", "W","X","Y","Z"};

//        Integer[] firstCol = {5, 7, 9, 10, 11, 12, 13, 14};
        Integer[] firstCol = {2, 4, 6, 7, 8, 9, 10, 11};

        for (int i=0; i< rows.size(); i++){
            List<?> cols = rows.get(i);
            String dictSheet = "dict" + i;
            //创建第二个Sheet
            writer.setSheet(dictSheet);
            //将Sheet2中的数据引用到Sheet1中的下拉框
            Workbook workbook = writer.getWorkbook();
            Name namedCell = workbook.createName();
            namedCell.setNameName(dictSheet);
            //加载数据,将名称为hidden的
            DVConstraint constraint = DVConstraint.createFormulaListConstraint(dictSheet);
            for (int j=0; j<cols.size(); j++){
                writer.writeCellValue(i, j, cols.get(j));
            }

            if (CollectionUtils.isEmpty(cols)){
                continue;
            }
            namedCell.setRefersToFormula(dictSheet + "!$" + types[i] + "$1:$" + types[i] + "$" + cols.size());
            // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
            HSSFDataValidation validation = new HSSFDataValidation(new CellRangeAddressList(1, 1000, firstCol[i], firstCol[i]), constraint);
            writer.getSheets().get(0).addValidationData(validation);

            workbook.setSheetHidden(i+1, true);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try (OutputStream outputStream = response.getOutputStream()){
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("网关设备模板.xls", "UTF-8"));
            writer.flush(outputStream, true);
            outputStream.flush();
            outputStream.close();
            return ResultInfo.success(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 输入标题到excel
     * @param writer excel对象
     * @param column 当前列位置
     * @param cellValue 标题内容
     * @param requiredFlag 是否标红
     */
    private void writeCell(ExcelWriter writer, int column, String cellValue, Boolean requiredFlag){
        // 根据x,y轴设置单元格内容
        writer.writeCellValue(column , 0, cellValue);
        Font font = writer.createFont();
        font.setBold(true);
        if (requiredFlag){
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
            writer.setColumnWidth(column, 15);
            // 字体颜色标红
            font.setColor(Font.COLOR_RED);
            cellStyle.setFont(font);
            // 填充前景色
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }else {
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
            writer.setColumnWidth(column, 15);
            // 填充前景色
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cellStyle.setFont(font);
        }
    }

    /**
     * 设置下拉选项
     */
    private DataValidation setSelectCol(StyleSet styleSet, Sheet sheet, String[] capacityAvi, int firstRow, int firstCol) {

        CellStyle cellStyle = styleSet.getCellStyle();
        //规定格式
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));

        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 设置辅修下拉框数据
//        String[] capacityAvi = {"是", "否"};
        DataValidationConstraint capacityConstraint = helper.createExplicitListConstraint(capacityAvi);
        //需要被设置为下拉数据的单元格范围
        CellRangeAddressList capacityList = new CellRangeAddressList(firstRow, 5000, firstCol, firstCol);
        return helper.createValidation(capacityConstraint, capacityList);
    }

    public ResultInfo importUploadData(List<MultipartFile> files) throws Exception {
        boolean falg = false;
        String url = null;
        for (MultipartFile file : files) {
            //判断文件是否大于200M
            falg = FileUtils.checkFileSize(file.getSize(), 200, "M");
            if (!falg) {
                return ResultInfo.error("文件过大，最大200M");
            }
            url = UploadUtils.uploadFile(file, FilePath);
        }

        List<DeviceGatewayVo> list = new ArrayList<>();
        if (StringUtils.isEmpty(url)) {
            return ResultInfo.error("上传失败");
        } else {
            File file = new File(url);
            if (!file.exists()) {
                throw new Exception("文件不存在!");
            }
            ExcelReader reader = ExcelUtil.getReader(file);
            List<Map<String, Object>> readAll = reader.readAll();
            if (CollectionUtils.isEmpty(readAll)) {
                throw new RuntimeException("导入的数据为空");
            }


            // 设置errorList，加入校验失败的行号和原因
            List<Map<String, Object>> errorList = new ArrayList<>();
            for (Map<String, Object> map : readAll) {
                // map的所有value 去除前后空格
                map.forEach((k, v) -> map.put(k, null == v ? "" :v.toString().trim()));
                // 行号
                Integer rowNum = readAll.indexOf(map) + 2;
                // 设备名称
                String name = MapUtils.getString(map, "设备名称");
                if (StringUtils.isBlank(name)) {
                    throw new RuntimeException("第" + rowNum + "行,设备名称不能为空");
                }
                // 设备编码
                String deviceCode = MapUtils.getString(map, "设备编码");
                if (StringUtils.isBlank(deviceCode)) {
                    throw new RuntimeException("第" + rowNum + "行,设备编码不能为空");
                }
                // 供应商
                String supplierName = MapUtils.getString(map, "供应商");
                if (StringUtils.isBlank(supplierName)) {
                    throw new RuntimeException("第" + rowNum + "行,供应商不能为空");
                }
                List<ConfigSupplier> suppliers = supplierMapper.selectList(Wrappers.<ConfigSupplier>lambdaQuery()
                        .eq(ConfigSupplier::getSupplierName, supplierName).eq(ConfigSupplier::getIsDel, 0));
                if (CollectionUtils.isEmpty(suppliers)) {
                    throw new RuntimeException("第" + rowNum + "行,供应商不存在");
                }
                Integer supplierId = suppliers.get(0).getId();
                // 品牌
                String brand = MapUtils.getString(map, "品牌");
                if (StringUtils.isBlank(brand)) {
                    throw new RuntimeException("第" + rowNum + "行,品牌不能为空");
                }
                // 设备型号
                String modelName = MapUtils.getString(map, "设备型号");
                if (StringUtils.isBlank(modelName)) {
                    throw new RuntimeException("第" + rowNum + "行,设备型号不能为空");
                }
                List<ConfigModel> models = configModelMapper.selectList(Wrappers.<ConfigModel>lambdaQuery()
                        .eq(ConfigModel::getModelName, modelName).eq(ConfigModel::getIsDel, 0));
                if (CollectionUtils.isEmpty(models)) {
                    throw new RuntimeException("第" + rowNum + "行,设备型号不存在");
                }
                Integer modelId = models.get(0).getId();
                // 出厂编号
                String serialNum = MapUtils.getString(map, "出厂编号");
                if (StringUtils.isBlank(serialNum)) {
                    throw new RuntimeException("第" + rowNum + "行,出厂编号不能为空");
                }
                // 传输协议 configProtocolId
                String protocolName = MapUtils.getString(map, "传输协议");
                if (StringUtils.isBlank(protocolName)) {
                    throw new RuntimeException("第" + rowNum + "行,传输协议不能为空");
                }
                List<ConfigProtocol> protocols = configProtocolMapper.selectList(Wrappers.<ConfigProtocol>lambdaQuery()
                        .eq(ConfigProtocol::getProtocolName, protocolName).eq(ConfigProtocol::getIsDel, 0));
                if (CollectionUtils.isEmpty(protocols)) {
                    throw new RuntimeException("第" + rowNum + "行,传输协议不存在");
                }
                Integer configProtocolId = protocols.get(0).getId();
                // 服务连接
                String linkName = MapUtils.getString(map, "服务连接");
                if (StringUtils.isBlank(linkName)) {
                    throw new RuntimeException("第" + rowNum + "行,服务连接不能为空");
                }
                List<ConfigLink> links = configLinkMapper.selectList(Wrappers.<ConfigLink>lambdaQuery()
                        .eq(ConfigLink::getLinkName, linkName).eq(ConfigLink::getIsDel, 0));
                if (CollectionUtils.isEmpty(links)) {
                    throw new RuntimeException("第" + rowNum + "行,服务连接不存在");
                }
                Integer configLinkId = links.get(0).getId();
                // 发布主题 publishTopicId
                String topicName1 = MapUtils.getString(map, "发布主题");
                Integer publishTopicId = null;
                if (StringUtils.isNotBlank(topicName1)) {
                    List<ConfigTopic> topics1 = configTopicMapper.selectList(Wrappers.<ConfigTopic>lambdaQuery()
                            .eq(ConfigTopic::getTopicCode, topicName1).eq(ConfigTopic::getIsDel, 0));
                    if (!CollectionUtils.isEmpty(topics1)) {
                        publishTopicId = topics1.get(0).getId();
                    }
                }
                // 订阅主题 subscribeTopicId
                String topicName2 = MapUtils.getString(map, "订阅主题");
                Integer subscribeTopicId = null;
                if (StringUtils.isNotBlank(topicName2)) {
                    List<ConfigTopic> topics2 = configTopicMapper.selectList(Wrappers.<ConfigTopic>lambdaQuery()
                            .eq(ConfigTopic::getTopicCode, topicName2).eq(ConfigTopic::getIsDel, 0));
                    if (!CollectionUtils.isEmpty(topics2)) {
                        subscribeTopicId = topics2.get(0).getId();
                    }
                }

                // 告警主题 alarmTopicId
                String topicName3 = MapUtils.getString(map, "告警主题");
                Integer alarmTopicId = null;
                if (StringUtils.isNotBlank(topicName3)) {
                    List<ConfigTopic> topics3 = configTopicMapper.selectList(Wrappers.<ConfigTopic>lambdaQuery()
                            .eq(ConfigTopic::getTopicCode, topicName3).eq(ConfigTopic::getIsDel, 0));
                    if (!CollectionUtils.isEmpty(topics3)) {
                        alarmTopicId = topics3.get(0).getId();
                    }
                }
                // 心跳主题 heartTopicId
                String topicName4 = MapUtils.getString(map, "心跳主题");
                Integer heartTopicId = null;
                if (StringUtils.isNotBlank(topicName4)) {
                    List<ConfigTopic> topics4 = configTopicMapper.selectList(Wrappers.<ConfigTopic>lambdaQuery()
                            .eq(ConfigTopic::getTopicCode, topicName4).eq(ConfigTopic::getIsDel, 0));
                    if (!CollectionUtils.isEmpty(topics4)) {
                        heartTopicId = topics4.get(0).getId();
                    }
                }

                // 设备描述 deviceDesc
                String deviceDesc = MapUtils.getString(map, "设备描述");
                if (StringUtils.isBlank(deviceDesc)) {
                    throw new RuntimeException("第" + rowNum + "行,设备描述不能为空");
                }

                DeviceGatewayVo deviceGatewayVo = new DeviceGatewayVo();
                deviceGatewayVo.setName(name);
                deviceGatewayVo.setDeviceCode(deviceCode);
                deviceGatewayVo.setSupplierId(supplierId);
                deviceGatewayVo.setBrand(brand);
                deviceGatewayVo.setModelId(modelId);
                deviceGatewayVo.setSerialNum(serialNum);
                deviceGatewayVo.setConfigProtocolId(configProtocolId);
                deviceGatewayVo.setConfigLinkId(configLinkId);
                deviceGatewayVo.setPublishTopicId(publishTopicId);
                deviceGatewayVo.setSubscribeTopicId(subscribeTopicId);
                deviceGatewayVo.setAlarmTopicId(alarmTopicId);
                deviceGatewayVo.setHeartTopicId(heartTopicId);
                deviceGatewayVo.setDeviceDesc(deviceDesc);

                list.add(deviceGatewayVo);
            }
            for (DeviceGatewayVo vo : list) {
                add(vo);
            }
        }
        return ResultInfo.success(list);
    }
}


