package com.jsdc.iotpt.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author 季鑫利
 * @date 2024年6月12日15:38:17
 * @note
 */
@Log4j2
public class EasyExcelUtils {
    //文件导出
    public static <T> void writeExcel(HttpServletResponse response, Class<T> tClass, List<T> datas, String fileName) throws IOException, IllegalAccessException, NoSuchFieldException {
        TimeInterval timer = DateUtil.timer();
        if (ObjectUtil.isNull(response) || ObjectUtil.isNull(tClass)) {
            return;
        }

        if (StrUtil.isBlank(fileName)) {
            fileName = "excel.xlsx";
        } else {
            if (!fileName.contains(".xlsx")) {
                fileName = fileName + ".xlsx";
            }
        }

        //编码设置成UTF-8，excel文件格式为.xlsx
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        // 这里URLEncoder.encode可以防止中文乱码 和easyexcel本身没有关系
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream(), tClass)
                .excelType(ExcelTypeEnum.XLSX)
                //设置自动列宽
                .registerWriteHandler(new Custemhandler())
                //设置样式
                .registerWriteHandler(getStyleStrategy())
                .sheet("sheet")
                .doWrite(datas);

//        WriteSheet writeSheet = new WriteSheet();
//        writeSheet.setSheetName("sheet");
//        excelWriter.write(datas, writeSheet);

//        excelWriter.finish();
        log.info("导出exlce数据：{}条，耗时：{}秒！", datas.size(), timer.intervalSecond());
    }

    public static HorizontalCellStyleStrategy getStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        // 字体样式
        headWriteFont.setFontName("宋体");
        headWriteCellStyle.setWriteFont(headWriteFont);
        //自动换行
        headWriteCellStyle.setWrapped(false);
        // 水平对齐方式
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
//        contentWriteCellStyle.setFillPatternType(FillPatternType.SQUARES);
        // 背景白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 12);
        // 字体样式
        contentWriteFont.setFontName("Calibri");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }


    /**
     * @param response
     * @param inputStream
     */
    @SneakyThrows
    public static ExcelWriter writeExcelWithTemplate(HttpServletResponse response, InputStream inputStream, String fileName) {
        if (StrUtil.isBlank(fileName)) {
            fileName = "excel.xlsx";
        } else {
            if (!fileName.contains(".xlsx")) {
                fileName = fileName + ".xlsx";
            }
        }
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/octet-stream; charset=utf-8");
        return EasyExcel.write(response.getOutputStream()).withTemplate(inputStream).excelType(ExcelTypeEnum.XLSX).build();

    }
}
