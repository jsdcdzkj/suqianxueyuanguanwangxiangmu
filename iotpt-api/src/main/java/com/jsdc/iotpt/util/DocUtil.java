package com.jsdc.iotpt.util;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Map;

/**
 * word合成工具类
 */
@Slf4j
public class DocUtil {

    private static final String licensePath = "word/license.xml";



    /**
     * @param content  公文地址
     * @param data     需要填写的参数
     * @param destDocx 保存的文件地址
     */
    public static void word2RedDocument(String content, Map<String, Object> data, String destDocx, String pdfPath) throws IOException {
        //模板文件 参数填写
        ConfigureBuilder builder = Configure.builder();
        //设置语法
        builder.buildGramer("${", "}");
        //设置模板文件地址
        XWPFTemplate template = XWPFTemplate.compile(content, builder.build());
        //参数处理
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            data.put(entry.getKey(), entry.getValue() == null ? "______" : entry.getValue());
        }
        //填充参数
        template.render(data);
        //获取模板文件  公文
        NiceXWPFDocument main = template.getXWPFDocument();
        //判断目录目录是否存在
        File docFile = new File(destDocx);
        if(!docFile.exists()){
            docFile.getParentFile().mkdirs();
        }
        //生成新文档
        FileOutputStream out = new FileOutputStream(destDocx);
        main.write(out);
        main.close();

        wordToPdf(destDocx, pdfPath);
        out.close();
    }

    /**
     *
     * @param content  公文地址
     * @param data     需要填写的参数
     * @param destDocx 保存的文件地址
     */
    public static void word2RedDocument(String content, Configure config, Map<String, Object> data, String destDocx, String pdfPath) throws IOException {
        //设置模板文件地址
        XWPFTemplate template = XWPFTemplate.compile(content, config);
        //填充参数
        template.render(data);
        //获取模板文件  公文
        NiceXWPFDocument main = template.getXWPFDocument();
        //判断目录目录是否存在
        File docFile = new File(destDocx);
        if(!docFile.exists()){
            docFile.getParentFile().mkdirs();
        }
        //生成新文档
        FileOutputStream out = new FileOutputStream(destDocx);
        main.write(out);
        main.close();

        wordToPdf(destDocx, pdfPath);
        out.close();
    }

    /**
     * 功能描述: <br>
     * 〈生成pdf〉
     *
     * @param content 字符串流 war 部署无法获取路径
     * @param config
     * @param data
     * @param destDocx
     * @param pdfPath
     * @Return: void
     * @Author: yc
     * @Date: 2024/5/21 15:56
     */
    public static void word2RedDocumentNw(InputStream content, Configure config, Map<String, Object> data, String destDocx, String pdfPath) throws IOException {
        //设置模板文件地址
        XWPFTemplate template = XWPFTemplate.compile(content, config);
        //填充参数
        template.render(data);
        //获取模板文件  公文
        NiceXWPFDocument main = template.getXWPFDocument();
        //判断目录目录是否存在
        File docFile = new File(destDocx);
        if(!docFile.exists()){
            docFile.getParentFile().mkdirs();
        }
        //生成新文档
        FileOutputStream out = new FileOutputStream(destDocx);
        main.write(out);
        main.close();

        wordToPdf(destDocx, pdfPath);
        out.close();
    }


    public static void wordToPdf(String docPath, String toPath){
        InputStream is = null;
        OutputStream os = null;
        File pdfFile = null;


        //填充doc下载pdf到本地
        try {
            pdfFile = new File(toPath);
            //凭证 不然切换后有水印
            is = new ClassPathResource(licensePath).getInputStream();
            License aposeLic = new License();
            aposeLic.setLicense(is);
            os = new FileOutputStream(pdfFile);
            //要转换的word文件
            Document doc = new Document(docPath);

            ////todo-yc 临时代码用于解决 241 乱码
//            FontSettings.setFontsFolder("/usr/share/fonts/windows"+File.separator, true);//此处处理乱码和小方块
            doc.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

