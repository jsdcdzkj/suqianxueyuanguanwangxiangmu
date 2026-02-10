package com.jsdc.iotpt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 获取模板路径工具类
 * 解决 linux 获取模版报空指针异常
 *
 * @author wangyan
 * @version 1.0
 * @title TemplatePathUtils
 * @description
 * @date 2022/07/26 13:58
 */
@Slf4j
public class TemplatePathUtils {

    private TemplatePathUtils() {
    }

    private static final String OS_NAME = "os.name";

    private static final String OS_VERSION = "Windows";

    public static final String CATALINA_HOME = "catalina.home";

    public static String getTemplatePath(String path) {
        if (System.getProperties().getProperty(OS_NAME).contains(OS_VERSION)) {
            return path;
        }
        Resource resource = new ClassPathResource(path);
        FileOutputStream fileOutputStream = null;
        // 将模版文件写入到 tomcat临时目录
        String folder = System.getProperty(CATALINA_HOME);
        File tempFile = new File(folder + File.separator + path);
        // 文件存在时 不再写入
        if (tempFile.exists()) {
            return tempFile.getPath();
        }
        File parentFile = tempFile.getParentFile();
        // 判断父文件夹是否存在
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(resource.getInputStream());
            fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[10240];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            log.error("获取文件路径异常，异常原因{}", e, e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("获取文件路径关闭文件流异常，异常原因{}", e, e.getMessage());
                }
            }
        }
        return tempFile.getPath();
    }
}
