package com.jsdc.iotpt.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.service.SysFileService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.*;
import java.net.URLEncoder;

/**
 * 文件操作
 * @author wangYan
 * @since 2023-05-10前端获取后端文件
 */
@Controller
@RequestMapping("/app/sysFile")
@Api(tags = "文件操作")
public class SysFileController {

    @Value("${jsdc.uploadPath}")
    private String uploadPath;

    @Autowired
    private SysFileService sysFileService;

    @RequestMapping("/readFile")
    public void readFile(HttpServletResponse response, Integer fileId) {
        SysFile sysFile = sysFileService.getById(fileId);
        File file = new File(uploadPath + sysFile.getFileUrl());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(sysFile.getFileName(), "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/readBizFile")
    public void readBizFile(HttpServletResponse response, Integer bizId, Integer bizType) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getBizId,bizId).eq(SysFile::getBizType,bizType+"");
        SysFile sysFile = sysFileService.getOne(wrapper);
        File file = new File(uploadPath + sysFile.getFileUrl());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置响应头，允许跨域访问
            response.setHeader("Access-Control-Allow-Origin", "*");
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(sysFile.getFileName(), "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @RequestMapping("/previewCheck")
    public void previewCheck(HttpServletRequest request, HttpServletResponse response, Integer fileId) throws
            IOException {
        SysFile sysFile = sysFileService.getById(fileId);
        FileInputStream is = new FileInputStream(new File(uploadPath + sysFile.getFileUrl()));
        // 清空response
        response.reset();
        //2、设置文件下载方式
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/pdf");
        OutputStream outputStream = response.getOutputStream();
        int count = 0;
        byte[] buffer = new byte[1024 * 1024];
        while ((count = is.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
        outputStream.flush();

    }


    /**
     * 文件列表
     * @return
     */
    @ApiOperation("文件列表")
    public ResultInfo getList(SysFile sysFile){
        return ResultInfo.success(sysFileService.list());
    }

}

