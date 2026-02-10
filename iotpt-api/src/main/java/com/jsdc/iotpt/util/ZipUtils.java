package com.jsdc.iotpt.util;

import cn.hutool.core.net.URLEncodeUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     *
     *
     * Title: toZip Description: 通过指定磁盘路径打包文件
     *
     * @param sourceFiles
     * @param fos
     *
     */
    public static void toZip(List<File> sourceFiles, OutputStream fos) {
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        try {
            // 压缩输出流
            zos = new ZipOutputStream(fos);
            if (!sourceFiles.isEmpty()) {
                for (File file : sourceFiles) {
                    byte[] buf = new byte[BUFFER_SIZE];// 缓存大小
                    // 开始编写新的ZIP文件条目，并将流定位到条目数据的开头。
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    fis = new FileInputStream(file);
                    int len;
                    // read 读取缓冲区数据
                    while ((len = fis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    zos.closeEntry();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Compressed file error!!!", e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (zos != null) {
                    zos.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 文件url
     */
    public static final String FILE_URL = "FILE_URL";
    /**
     * 文件别名
     */
    public static final String FILE_NAME = "FILE_NAME";

    /**
     * 文件目录
     */
    public static final String FILE_DIRECTORY = "FILE_DIRECTORY";

    /**
     * 目录分隔符号
     */
    public static final String SLASH = "/";

    /**
     *
     *
     * Title: getToZipUrlMap Description: 封装文件数据
     *
     * @param fileUrl  文件url 必填
     * @param fileName 如果为空则使用原文件名
     * @param fileDirectory 自定义文件目录(注意以一定要以‘/’结尾否则结构出错) 列：一级目录    test/ 多级 test/test1/...
     * @return
     * @throws Exception
     *
     */
    public static Map<String, Object> getToZipUrlMap(String fileUrl, String fileName,String fileDirectory){
        Map<String, Object> map = new HashMap<String, Object>();
        if (fileUrl != null) {
            StringBuffer buffer=new StringBuffer();
            String[] urls = fileUrl.split("/");
            String[] urlss = urls[urls.length - 1].split("\\.");
            map.put(FILE_URL, fileUrl);
            buffer.append((fileName == null ? urlss[urlss.length - 2] : fileName));
            buffer.append("." + urlss[urlss.length - 1]);
            map.put(FILE_NAME, buffer.toString());
            if(fileDirectory != null) {
                map.put(FILE_DIRECTORY, fileDirectory+SLASH);
            }
        }
        return map;
    }

    /**
     *
     *
     * Title: toZipHTTP Description: 通过http下载打包文件
     *
     * @param fileHttps
     * @param os
     *
     */
    public static void toZipHTTP(List<Map<String, Object>> fileHttps, OutputStream os) {
        ZipOutputStream zos = null;
        URL url = null;
        URLConnection con = null;
        InputStream fis = null;
        try {
            if (!fileHttps.isEmpty()) {
                zos = new ZipOutputStream(os);
                for (Map<String, Object> map : fileHttps) {
                    if (map.containsKey(FILE_URL) && map.containsKey(FILE_NAME)) {
                        byte[] buf = new byte[BUFFER_SIZE];// 缓存大小
                        String urls = map.get(FILE_URL).toString();
                        // 有层级结构，就先创建目录
                        if (map.containsKey(FILE_DIRECTORY)) {
                            zos.putNextEntry(new ZipEntry(map.get(FILE_DIRECTORY).toString() + map.get(FILE_NAME).toString()));
                        } else {
                            zos.putNextEntry(new ZipEntry(map.get(FILE_NAME).toString()));
                        } 
                        url = new URL(URLEncodeUtil.encode(urls, Charset.forName("UTF-8")));
                        con = url.openConnection();
                        con.connect();
                        fis = con.getInputStream();
                        int len;
                        while ((len = fis.read(buf)) != -1) {
                            zos.write(buf, 0, len);
                        }
                        zos.closeEntry();
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Packaged successfully!");
                fis.close();
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}