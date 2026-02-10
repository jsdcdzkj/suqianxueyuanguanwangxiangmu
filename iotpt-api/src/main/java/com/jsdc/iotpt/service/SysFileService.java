package com.jsdc.iotpt.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.enums.CommonEnum;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * (SysDict)表服务接口
 *
 * @author wangYan
 * @since 2023-05-09
 */
@Service
@Slf4j
@Transactional
public class SysFileService extends BaseService<SysFile> {

    @Value("${jsdc.uploadPath}")
    private String uploadPath;

    @Value("${jsdc.nginxPath}")
    private String nginxPath;
    @Value("${jsdc.nginxInnerPath}")
    private String nginxInnerPath;

    @Autowired
    private MinioTemplate minioTemplate;

    public ResultInfo uploadFile(UploadParams params) {
        try {
            MultipartFile multipartFile = params.getFile();
            String originalFilename = params.getFile().getOriginalFilename();
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            File parentFile = new File(uploadPath + params.getUploadPath());
            if (!parentFile.exists()) {
                //如果文件存储路径，则创建
                parentFile.mkdirs();
            }
            String newFilename = IdUtil.fastSimpleUUID() + fileSuffix;
            multipartFile.transferTo(new File(parentFile, newFilename));
            SysFile sysFile = new SysFile();
            sysFile.setFileName(originalFilename);
            sysFile.setFileType(multipartFile.getContentType());
            sysFile.setFileSize(multipartFile.getSize() + "");
            sysFile.setFileUrl(params.getUploadPath() + "/" + newFilename);
            sysFile.setBizType(params.getBizType());
            sysFile.setBizId(params.getBizId());
            sysFile.setCreateTime(new Date());
            sysFile.insert();
            return ResultInfo.success(sysFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回文件信息数据
     *
     * @param params
     * @return
     */
    public SysFile uploadFiles(UploadParams params) {
        try {
            MultipartFile multipartFile = params.getFile();
            String originalFilename = params.getFile().getOriginalFilename();
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            File parentFile = new File(uploadPath + params.getUploadPath());
            if (!parentFile.exists()) {
                //如果文件存储路径，则创建
                parentFile.mkdirs();
            }
            String newFilename = IdUtil.fastSimpleUUID() + fileSuffix;
            multipartFile.transferTo(new File(parentFile, newFilename));
            SysFile sysFile = new SysFile();
            sysFile.setFileName(originalFilename);
            sysFile.setFileType(multipartFile.getContentType());
            sysFile.setFileSize(multipartFile.getSize() + "");
            sysFile.setFileUrl(params.getUploadPath() + "/" + newFilename);
            sysFile.setBizType(params.getBizType());
            sysFile.setBizId(params.getBizId());
            sysFile.setCreateTime(new Date());
            return sysFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回文件但不进行保存
     *
     * @param params
     * @return
     */
    public SysFile minioReturnFile(UploadParams params) {
        try {
            MultipartFile multipartFile = params.getFile();
            String originalFilename = params.getFile().getOriginalFilename();
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            String newFilename = IdUtil.fastSimpleUUID() + fileSuffix;
            minioTemplate.putObject(G.MINIO_BUCKET, newFilename, params.getFile().getInputStream());
            SysFile sysFile = new SysFile();
            sysFile.setFileName(originalFilename);
            sysFile.setFileType(multipartFile.getContentType());
            sysFile.setFileSize(multipartFile.getSize() + "");
            sysFile.setFileUrl(newFilename);
            sysFile.setBizType(params.getBizType());
            sysFile.setBizId(params.getBizId());
            sysFile.setCreateTime(new Date());
            return sysFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String previewUrl(String fileName, boolean inNet) {
        if (inNet) {
            return nginxInnerPath + G.MINIO_BUCKET + (fileName.startsWith("/") ? fileName : "/" + fileName);
        }
        return nginxPath + G.MINIO_BUCKET + (fileName.startsWith("/") ? fileName : "/" + fileName);
    }

    public String uploadUrlFile(String bizId, String bizType, List<String> urls) {
//        String filePath = "";
//        for (int i = 0; i < urls.size(); i++) {
//            String url = urls.get(i);
//            try {
//                log.info("url:{}", url);
//                System.out.println(url);
//                // 创建 URL 对象
//                URL fileUrl = new URL(url);
//                // 打开连接
//                URLConnection connection = fileUrl.openConnection();
//                // 获取输入流
//                InputStream inputStream = connection.getInputStream();
//
//                // 使用正则表达式匹配文件名和文件类型
//                Pattern pattern = Pattern.compile(".*/([^/]+)\\.([^.]+)$");
//                Matcher matcher = pattern.matcher(url);
//                // 如果匹配成功，则提取文件名和文件类型
//                if (matcher.find()) {
//                    filePath = fileUrl.getPath();
//                    minioTemplate.putObject(G.MINIO_BUCKET, fileUrl.getPath(), inputStream);
//                } else {
//                    throw new RuntimeException("File name and type not found in URL");
//                }
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
        return getPic(urls);
    }

    public SysFile uploadFile(String bizId, String bizType, String bucket, MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            Date date = new Date();
            minioTemplate.putObject(bucket, filename, file.getInputStream());

            SysFile sysFile = new SysFile();
            sysFile.setFileName(filename);
            sysFile.setFileType(file.getContentType());
            sysFile.setFileUrl("/" + bucket + "/" + filename);
            sysFile.setBizId(bizId);
            sysFile.setBizType(bizType);
            sysFile.setFileSize(String.valueOf(file.getSize()));
            sysFile.setCreateTime(date);
            sysFile.setIsDel(0);
            sysFile.setUpdateTime(date);
            sysFile.insert();
            return sysFile;
        }catch (Exception e ){
            log.error("文件上传Minio失败：", e);
            throw new CustomException("文件上传Minio失败");
        }
    }

    public String uploadBase64Img(String base64) {
        String filename = IdUtil.fastSimpleUUID() + ".jpg";
        InputStream inputStream = base64ToInputStream(base64);
        try {
            minioTemplate.putObject(G.MINIO_BUCKET, filename, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return filename;
    }

    //删除文件
    public void deleteFiles(Integer bizId, String bizType) {
        long count = count(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, bizId).eq(SysFile::getBizType, CommonEnum.MISSION_SB_FILE));
        if (count > 0) {
            List<SysFile> list = list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, bizId+"").eq(SysFile::getBizType, bizType));
            list.forEach(a -> {
                removeById(a.getId());
            });
        }
    }

    public String getPic(List<String> urls) {
        String filePath = "";
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            log.info("url:{}", url);
            InputStream inputStream = null;
            try {
                String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);
                boolean contains = url.contains("?");
                String suffixName = "";
                if (contains){
                    suffixName = url.substring(url.lastIndexOf("."), url.lastIndexOf("?"));
                }else {
                    suffixName = url.substring(url.lastIndexOf("."));
                }
                if (suffixName.length() >8){
                    suffixName = ".jpg";
                }
                String filename = ymd + suffixName;
//                D:\dingchi\tomcat\apache-tomcat-9.0.88-zhyy-7072\bin\..\..\tempPic\202405310829594565.jpg
                File file = new File("/temp/"+filename);
                log.info("filePath:{}", file.getAbsoluteFile());
                FileUtils.copyURLToFile(new URL(url), file);
                filePath = filename;
                inputStream = new FileInputStream(file);
                minioTemplate.putObject(G.MINIO_BUCKET, filename, inputStream);
                inputStream.close();
                file.delete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filePath;
    }


    private static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64.replace("\r\n", ""));
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public List<SysFile> getFileList(String bizId, String bizType) {
        return list(Wrappers.lambdaQuery(SysFile.class).eq(SysFile::getBizId, bizId).eq(SysFile::getBizType, bizType).eq(SysFile::getIsDel, 0));
    }

    public void download(Integer fileId, HttpServletResponse response) {
        SysFile file = getById(fileId);
        if (Objects.isNull(file)) {
            throw new CustomException("下载失败，文件不存在");
        }
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            fileName = file.getFileName();
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            inputStream = minioTemplate.getObject(G.MINIO_BUCKET, file.getFileName());
            byte[] buf = new byte[1024];
            int length;
            response.reset();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Max-Age", "*");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            // 输出文件
            while ((length = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
        } catch (Exception e) {
            log.error("download file error", e);
            throw new CustomException("下载失败");
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {}
        }

    }
}

