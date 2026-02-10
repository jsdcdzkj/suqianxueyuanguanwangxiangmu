package com.jsdc.iotpt;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;

/**
 * @ClassName GeneratorUtil
 * @Description TODO
 * @Author xujian
 * @Date 2022/3/29 10:32
 * @Version 1.0
 */
public class GeneratorUtil {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "")
                .globalConfig(builder -> {
                    builder.author("wangYan") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.jsdc") // 设置父包名
                            .moduleName("ybpt"); // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D://")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder
                        .addInclude("autonomous_medical")
                        .addInclude("exam_config")
                        .addInclude("pack_info")
                        .addInclude("organization_info")
                        .addInclude("medical_item")
                        .addInclude("phys_exam_config")
                    ; // 设置需要生成的表名
//                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}


