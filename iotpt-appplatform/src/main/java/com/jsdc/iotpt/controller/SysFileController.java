package com.jsdc.iotpt.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.service.SysFileService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (SysDict)表控制层
 *
 * @author wangYan
 * @since 2023-05-10前端获取后端文件
 */
@Controller
@RequestMapping("/sysFile")
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


}

