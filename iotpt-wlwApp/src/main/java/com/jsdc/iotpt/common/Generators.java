package com.jsdc.iotpt.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 根据实体类名 生成controller service mapper dao 类
 *
 * @Author libin
 * @create 2021/12/31 16:51
 */
public class Generators {
    //static String projectName = "test";
    static String workspacePath = "E:\\project\\园区物联网管控平台";
    static String projectName = "iotpt";
    static String author = "thr";//作者

    static String modelPath = workspacePath + "\\iotpt-model\\src\\main\\java\\com\\jsdc\\iotpt\\model\\{className}.java";
    static String voPath = workspacePath + "\\iotpt-model\\src\\main\\java\\com\\jsdc\\iotpt\\vo\\{className}Vo.java";
    static String daoPath = workspacePath + "\\iotpt-api\\src\\main\\java\\com\\jsdc\\iotpt\\dao\\{className}Dao.java";
    static String mapperPath = workspacePath + "\\iotpt-api\\src\\main\\java\\com\\jsdc\\iotpt\\mapper\\{className}Mapper.java";
    static String servicePath = workspacePath + "\\iotpt-api\\src\\main\\java\\com\\jsdc\\iotpt\\service\\{className}Service.java";
    static String controllerPath = workspacePath + "\\iotpt-web\\src\\main\\java\\com\\jsdc\\iotpt\\controller\\{className}Controller.java";

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {

        //单独生成
        generator("ConfigSignalField");
        //批量生成
//        generator("ConfigLink","ConfigProtocol","ConfigDataTransfer"); //换成自已要生成的类名
    }

    /**
     * 生成代码的主方法
     *
     * @param classNames
     * @throws Exception
     */
    private static void generator(String... classNames) throws Exception {

        for (String className : classNames) {
            //String className = "AssetsClGroup";//AssetsBackup  类名
            className = StringUtils.trimToEmpty(className);
            String classNameX = firstCharLower(className);//首字母小写

            Date currentTime = new Date();

            String[] objectName = new String[]{"vo", "dao", "mapper", "service", "controller"};

            for (String cName : objectName) {
                String template = getTemplate(cName);

                //=============================== start========================================
                String cStr = template.replace("{className}", className);
                cStr = cStr.replace("{classNameX}", classNameX);
                cStr = cStr.replace("{author}", author);
                cStr = cStr.replace("{projectName}", projectName);
                cStr = cStr.replace("{createDate}", sdf.format(currentTime));
                if ("dao".equals(cName)) {
                    cStr = cStr.replace("{tableName}", getTable(className));
                }

                String path = getPath(cName);
                String tempPath = path.replace("{className}", className);


                FileUtils.writeStringToFile(new File(tempPath), cStr, "utf-8");
                //=============================== end========================================
            }
        }

    }


    /**
     * 获取类模板
     *
     * @param cName
     * @return
     */
    private static String getTemplate(String cName) {
        if ("vo".equals(cName)) {
            return voTemplate;
        } else if ("dao".equals(cName)) {
            return daoTemplate;
        } else if ("service".equals(cName)) {
            return serviceTemplate;
        } else if ("controller".equals(cName)) {
            return controllerTemplate;
        } else if ("mapper".equals(cName)) {
            return mapperTemplate;
        } else {
            return null;
        }
    }

    /**
     * 获取类路径
     *
     * @param cName
     * @return
     */
    private static String getPath(String cName) {
        if ("vo".equals(cName)) {
            return voPath;
        } else if ("dao".equals(cName)) {
            return daoPath;
        } else if ("service".equals(cName)) {
            return servicePath;
        } else if ("controller".equals(cName)) {
            return controllerPath;
        } else if ("mapper".equals(cName)) {
            return mapperPath;
        } else {
            return null;
        }
    }

    /**
     * 根据实体类获取表名
     *
     * @param className
     * @return
     * @throws IOException
     */
    private static String getTable(String className) throws IOException {
        String model = modelPath.replace("{className}", className);
        List<String> recodesList = FileUtils.readLines(new File(model), "utf-8");
        String tableName = "";
        for (String row : recodesList) {
            row = StringUtils.trimToEmpty(row);
            if (row.startsWith("@TableName")) {
                tableName = row.replace("@TableName(\"", "").replace("\")", "");
                break;
            }
        }
        return StringUtils.trimToEmpty(tableName);
    }

    //首字母转小写
    public static String firstCharLower(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    //首字母转大写
    public static String firstCharUpper(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * controller模板
     */
    static String controllerTemplate = "package com.jsdc.{projectName}.controller;\n" +
            "\n" +
            "import com.jsdc.{projectName}.service.{className}Service;\n" +
            "import io.swagger.annotations.Api;\n" +
            "import io.swagger.annotations.ApiImplicitParam;\n" +
            "import io.swagger.annotations.ApiImplicitParams;\n" +
            "import io.swagger.annotations.ApiOperation;\n" +
            "import com.jsdc.{projectName}.vo.ResultInfo;\n" +
            "import com.jsdc.{projectName}.vo.{className}Vo;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.web.bind.annotation.RequestMapping;\n" +
            "import org.springframework.web.bind.annotation.RestController;\n" +
            "\n" +
            "/**\n" +
            " *  todo  \n" +
            " *  controller控制器\n" +
            " */\n" +
            "@RestController\n" +
            "@RequestMapping(\"/{classNameX}\")\n" +
            "@Api(tags = \"XXXXXXXXX\")\n" +
            "public class {className}Controller {\n" +
            "\n" +
            "    @Autowired\n" +
            "    {className}Service {classNameX}Service;\n" +
            "\n" +
            "    /**\n" +
            "     * 分页查询 todo\n" +
            "     *\n" +
            "     * @return\n" +
            "     */\n" +
            "    @RequestMapping(\"/getPageList\")\n" +
            "    @ApiOperation(\"XXXX\")\n" +
            "    public ResultInfo getPageList({className}Vo bean) {\n" +
            "        return ResultInfo.success({classNameX}Service.getPageList(bean));\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * 添加/编辑 todo\n" +
            "     *\n" +
            "     * @param bean\n" +
            "     * @return\n" +
            "     */\n" +
            "    @RequestMapping(\"/saveOrUpdate\")\n" +
            "    @ApiOperation(\"XXXX\")\n" +
            "    public ResultInfo saveOrUpdate{className}({className}Vo bean) {\n" +
            "        return {classNameX}Service.saveOrUpdate{className}(bean);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * 获取实体类\n" +
            "     * @param bean\n" +
            "     * @return\n" +
            "     */\n" +
            "    @RequestMapping(\"/getEntity\")\n" +
            "    @ApiOperation(\"XXXX\")\n" +
            "    public ResultInfo getEntity({className}Vo bean) {\n" +
            "        return {classNameX}Service.getEntityById(bean.getId());\n" +
            "    }\n" +
            "}\n";

    /**
     * service模板
     */
    static String serviceTemplate = "package com.jsdc.{projectName}.service;\n" +
            "\n" +
            "import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;\n" +
            "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n" +
            "import com.jsdc.{projectName}.base.BaseService;\n" +
            "\n" +
            "import com.jsdc.{projectName}.mapper.{className}Mapper;\n" +
            "import com.jsdc.{projectName}.model.{className};\n" +
            "import com.jsdc.{projectName}.vo.ResultInfo;\n" +
            "import com.jsdc.{projectName}.vo.{className}Vo;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.stereotype.Service;\n" +
            "import org.springframework.transaction.annotation.Transactional;\n" +
            "\n" +
            "import java.util.Date;\n" +
            "import java.util.List;\n" +
            "\n" +
            "@Service\n" +
            "@Transactional\n" +
            "public class {className}Service extends BaseService<{className}> {\n" +
            "\n" +
            "    @Autowired\n" +
            "    private {className}Mapper {classNameX}Mapper;\n" +
            "\n" +
            "    /**\n" +
            "     * 分页查询 todo\n" +
            "     *\n" +
            "     * @return\n" +
            "     */\n" +
            "    public Page<{className}> getPageList({className}Vo vo) {\n" +
            "        QueryWrapper<{className}> queryWrapper = new QueryWrapper<>();\n" +
            "\n" +
            "        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);\n" +
            "        return {classNameX}Mapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * 查询 todo\n" +
            "     *\n" +
            "     * @return\n" +
            "     */\n" +
            "    public List<{className}> getList({className} entity) {\n" +
            "        QueryWrapper<{className}> queryWrapper = new QueryWrapper<>();\n" +
            "        return {classNameX}Mapper.selectList(queryWrapper);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * 添加/编辑 todo\n" +
            "     *\n" +
            "     * @param bean\n" +
            "     * @return\n" +
            "     */\n" +
            "    public ResultInfo saveOrUpdate{className}({className} bean) {\n" +
            "        saveOrUpdate(bean);\n" +
            "        return ResultInfo.success();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * 根据id获取类 todo\n" +
            "     *\n" +
            "     * @param id\n" +
            "     * @return\n" +
            "     */\n" +
            "    public ResultInfo getEntityById(Integer id) {\n" +
            "        return ResultInfo.success(getById(id));\n" +
            "    }\n" +
            "\n" +
            "}\n" +
            "\n" +
            "\n";

    /**
     * mapper模板
     */
    static String mapperTemplate = "package com.jsdc.{projectName}.mapper;\n" +
            "\n" +
            "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
            "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n" +
            "import com.jsdc.{projectName}.dao.{className}Dao;\n" +
            "import com.jsdc.{projectName}.model.{className};\n" +
            "import org.apache.ibatis.annotations.Mapper;\n" +
            "import org.apache.ibatis.annotations.SelectProvider;\n" +
            "\n" +
            "@Mapper\n" +
            "public interface {className}Mapper extends BaseMapper<{className}> {\n" +
            "\n" +
            "    @SelectProvider(type = {className}Dao.class, method = \"getEntityList\")\n" +
            "    Page<{className}> getEntityList(Page page, {className} bean);\n" +
            "}\n";

    /**
     * dao模板
     */
    static String daoTemplate = "package com.jsdc.{projectName}.dao;\n" +

            "\n" +
            "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n" +
            "import com.jsdc.{projectName}.model.{className};\n" +
            "import org.springframework.stereotype.Repository;\n" +
            "\n" +
            "@Repository\n" +
            "public class {className}Dao {\n" +
            "\n" +
            "\n" +
            "    /**\n" +
            "     * 此方法为默认方法 请添加自己的逻辑代码\n" +
            "     * @param page\n" +
            "     * @param bean\n" +
            "     * @return\n" +
            "     */\n" +
            "    public String getEntityList(Page page, {className} bean) {\n" +
            "        String sql = \" SELECT *  \"+\n" +
            "                \" FROM {tableName}  \"+\n" +
            "                \" WHERE \"+ \n" +
            "                \" 1=1 \";\n" +
            "\n" +
            "        return sql;\n" +
            "    }\n" +
            "}" +
            "\n";

    static String voTemplate = "package com.jsdc.{projectName}.vo;\n" +
            "\n" +
            "import com.jsdc.{projectName}.model.{className};\n" +
            "import lombok.Data;\n" +
            "\n" +
            "@Data\n" +
            "public class {className}Vo extends {className} {\n" +
            "    private Integer pageNo = 1;\n" +
            "    private Integer pageSize = 10;\n" +
            "}\n";
}
