package com.jsdc.iotpt.smartsecurity.controller;


import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.service.SysFileService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.Cleanup;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * minio 文件服务
 *
 * @author zdq
 */
@RestController
@RequestMapping("/minio")
public class MinioFileController {

    @Autowired
    private MinioTemplate minioTemplate;

    private final static String spaceName2 = "videowarn";

    @Autowired
    private SysFileService sysFileService;

    /**
     * 上传附件
     * //     * @param spaceName minio
     * @param file 文件
     */
    @PostMapping("/{spaceName}/importFile")
    public ResultInfo importBugs(MultipartFile file) {
        try {
            String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);

            String suffixName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = ymd + suffixName;
            minioTemplate.putObject(G.MINIO_BUCKET, filename, file.getInputStream());

            Map<String, String> map = new HashMap<>();
            map.put("filename", filename);
            map.put("originalFilename", file.getOriginalFilename());
            return ResultInfo.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("上传文件错误!");
        }
    }

    /**
     * 上传附件
     * //     * @param spaceName minio的空间名称
     *
     * @param file 文件
     */
    @PostMapping("/importFile")
    public ResultInfo importFile(MultipartFile file) {
        try {
            String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);

            String suffixName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = ymd + suffixName;
            minioTemplate.putObject(G.MINIO_BUCKET, filename, file.getInputStream());

            Map<String, String> map = new HashMap<>();
            map.put("filename", filename);
            map.put("originalFilename", file.getOriginalFilename());
            return ResultInfo.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("上传文件错误!");
        }
    }

    /**
     * 上传文件
     * 如果上传的文件是图片就会生成缩略图
     * 如果上传的文件是图片就会生成缩略图
     *
     * @param files 文件
     */
    @PostMapping("/importFile/fileThumbFiles")
    public ResultInfo fileThumbFiles(List<MultipartFile> files) {
        try {
            List<SysFile> list = new ArrayList<>();
            for (MultipartFile file : files) {
                String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);

                String suffixName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                String filename = ymd + suffixName;
                minioTemplate.putObject(G.MINIO_BUCKET, filename, file.getInputStream());

                String contentType = file.getContentType();
                String thumbFileName = null;

                if (contentType != null && contentType.startsWith("image/")) {
                    thumbFileName = "thumb_" + filename;
                    try (InputStream inputStream = file.getInputStream()) {
                        BufferedImage originalImage = ImageIO.read(inputStream);
                        if (originalImage != null) {
                            BufferedImage thumbnail = createThumbnail(originalImage, 200, 200);
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                ImageIO.write(thumbnail, suffixName.replace(".", ""), baos);
                                try (InputStream thumbInputStream = new ByteArrayInputStream(baos.toByteArray())) {
                                    minioTemplate.putObject(G.MINIO_BUCKET, thumbFileName, thumbInputStream);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("缩略图生成失败: " + filename);
                        e.printStackTrace();
                    }
                }

                SysFile sysFile = new SysFile();
                sysFile.setFileName(filename);
                sysFile.setFileType(file.getContentType());
                sysFile.setFileUrl("/" + G.MINIO_BUCKET + "/" + filename);
                if (!"".equals(StringUtils.trimToEmpty(thumbFileName))) {
                    sysFile.setThumbnailUrl("/" + G.MINIO_BUCKET + "/" + thumbFileName);
                }
                sysFile.setFileSize(String.valueOf(file.getSize()));
                sysFile.setCreateTime(new Date());
                sysFile.setIsDel(0);
                sysFile.setUpdateTime(new Date());
                sysFile.setOriginalFilename(file.getOriginalFilename());
                sysFile.setThumbnailName(thumbFileName);

                sysFile.insert();
                list.add(sysFile);
            }

            return ResultInfo.success(list);
        } catch (Exception e) {
            return ResultInfo.error("上传文件错误!");
        }

    }

    /**
     * 创建缩略图（保持比例）
     */
    private BufferedImage createThumbnail(BufferedImage src, int width, int height) {
        int originalWidth = src.getWidth();
        int originalHeight = src.getHeight();
        float scale = Math.min((float) width / originalWidth, (float) height / originalHeight);

        int newWidth = Math.round(originalWidth * scale);
        int newHeight = Math.round(originalHeight * scale);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(src, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resized;
    }


    /**
     * 上传附件
     * //     * @param spaceName minio的空间名称
     *
     * @param files 文件
     */
    @PostMapping("/importFile/returnFiles")
    public ResultInfo importFileReturnFiles(List<MultipartFile> files) {
        try {
            List<SysFile> list = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() < 1) {
                    throw new CustomException("文件不能为空!");
                }
                String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);

                String suffixName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                String filename = ymd + suffixName;
                minioTemplate.putObject(G.MINIO_BUCKET, filename, file.getInputStream());

                SysFile sysFile = new SysFile();
                sysFile.setFileName(filename);
                sysFile.setFileType(file.getContentType());
                sysFile.setFileUrl("/" + G.MINIO_BUCKET + "/" + filename);
                sysFile.setFileSize(String.valueOf(file.getSize()));
                sysFile.setCreateTime(new Date());
                sysFile.setOriginalFilename(file.getOriginalFilename());
                sysFile.setIsDel(0);
                sysFile.setUpdateTime(new Date());
                sysFile.insert();
                list.add(sysFile);
            }
            return ResultInfo.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("上传文件错误!");
        }
    }

    /**
     * 上传附件
     * //     * @param spaceName minio的空间名称
     *
     * @param file 文件
     */
    @PostMapping("/importFile/returnFile")
    public ResultInfo importFileReturnFile(MultipartFile file) {
        if (file.getSize() < 1) {
            throw new CustomException("文件不能为空!");
        }
        try {
            String ymd = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);

            String suffixName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = ymd + suffixName;
            minioTemplate.putObject(G.MINIO_BUCKET, filename, file.getInputStream());

            SysFile sysFile = new SysFile();
            sysFile.setFileName(filename);
            sysFile.setFileType(file.getContentType());
            sysFile.setFileUrl("/" + G.MINIO_BUCKET + "/" + filename);
            sysFile.setFileSize(String.valueOf(file.getSize()));
            sysFile.setOriginalFilename(file.getOriginalFilename());
            sysFile.setCreateTime(new Date());
            sysFile.setIsDel(0);
            sysFile.setUpdateTime(new Date());
            sysFile.insert();
            return ResultInfo.success(sysFile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("上传文件错误!");
        }
    }

    /**
     * 下载附件
     */
    @GetMapping("/downMinio")
    public void getMinioObject(@RequestParam String fileName,

                               final HttpServletResponse response) {
        try {
            @Cleanup InputStream inputStream = minioTemplate.getObject(G.MINIO_BUCKET, fileName);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            byte[] data = os.toByteArray();
            response.resetBuffer();
            response.resetBuffer();
            response.setHeader("Content-Disposition", "attachment");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            IOUtils.write(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载附件
     */
    @GetMapping("/downMinioByOriginalFilename")
    public void getMinioObjectByOriginalFilename(@RequestParam Integer id,

                               final HttpServletResponse response) {
        try {
            SysFile byId = sysFileService.getById(id);
            @Cleanup InputStream inputStream = minioTemplate.getObject(G.MINIO_BUCKET, byId.getFileName());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            byte[] data = os.toByteArray();
            response.resetBuffer();
            response.resetBuffer();
            response.setHeader("Content-Disposition", "attachment");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + (URLEncoder.encode((StringUtils.isBlank(byId.getOriginalFilename()) ?
                            byId.getFileName() : byId.getOriginalFilename()),"UTF-8")));
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            IOUtils.write(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除附件
     */
    @GetMapping("/{bucketName}/delete")
    public ResultInfo deleteMinioObject(@PathVariable String bucketName, @RequestParam String fileName) {
        try {
            minioTemplate.removeObject(bucketName, fileName);
            return ResultInfo.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("删除文件错误!");
        }
    }

    /**
     * 预览地址
     */
    @GetMapping("/preview")
    public String preview(@RequestParam(name = "bucketName") String bucketName, @RequestParam(name = "fileName") String fileName) {
        System.out.println("bucketName:" + bucketName);
        try {
            return minioTemplate.getPreviewFileUrl(bucketName, fileName);
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping("/previewCheck")
    public void previewCheck(@RequestParam String fileName, HttpServletResponse response, Integer fileId) throws
            IOException {
        @Cleanup InputStream is = minioTemplate.getObject(G.MINIO_BUCKET, fileName);
        // 清空response
        response.reset();
        //2、设置文件下载方式
        response.setCharacterEncoding("utf-8");
//        setPreviewContentTypeByFileExtension(response, fileName);
        response.setContentType("application/pdf");
        OutputStream outputStream = response.getOutputStream();
        int count = 0;
        byte[] buffer = new byte[1024 * 1024];
        while ((count = is.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
        outputStream.flush();

    }

    //type 1压缩图
    @RequestMapping("/previewFile")
    public void previewFile(@RequestParam String fileName,
                            @RequestParam(required = false) String spaceName,
                            HttpServletResponse response, Integer fileId,String type) throws
            IOException {
        String space = G.MINIO_BUCKET;
        if (StringUtils.isNotBlank(spaceName)){
            space = spaceName;
        }
        @Cleanup InputStream is = minioTemplate.getObject(space, fileName);
        if("1".equals(type)){
            response.setContentType("image/jpeg");
            OutputStream out = response.getOutputStream();
            Thumbnails.of(is).scale(1.0).outputQuality(0.6f).toOutputStream(out);
        }else {
            // 清空response
            response.reset();
            //2、设置文件下载方式
            response.setCharacterEncoding("utf-8");
            setPreviewContentTypeByFileExtension(response, fileName);
//        response.setContentType("application/pdf");
            OutputStream outputStream = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((count = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        }


    }

    public static byte[] compressImage(InputStream inputStream, int maxWidth, float quality) throws IOException {
        // 使用 ByteArrayOutputStream 接收压缩后的数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream is = inputStream) { // 自动关闭 inputStream
            Thumbnails.of(inputStream)
                    .size(maxWidth, maxWidth)           // 按最大宽度缩放，保持宽高比
                    .outputQuality(quality)             // 输出质量
                    .outputFormat("jpg")                // 输出格式
                    .toOutputStream(outputStream);      // 写入 ByteArrayOutputStream
        }

        return outputStream.toByteArray();
    }
    @RequestMapping("/previewCheck2")
    public void previewCheck2(@RequestParam String fileName, HttpServletResponse response, Integer fileId) throws
            IOException {
        @Cleanup InputStream is = minioTemplate.getObject(spaceName2, fileName);
        String fileType = fileName.substring(fileName.indexOf(".") + 1);
        // 清空response
        response.reset();
        //2、设置文件下载方式
        response.setContentType("image/" + fileType);
        OutputStream outputStream = response.getOutputStream();
        int count = 0;
        byte[] buffer = new byte[1024 * 1024];
        while ((count = is.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
        outputStream.flush();
    }


    /**
     * 下载附件
     */
    @GetMapping("/{space}/downMinio")
    public void getMinioObject(@PathVariable String space, @RequestParam String fileName,
                               final HttpServletResponse response) {
        try {
            @Cleanup InputStream inputStream = minioTemplate.getObject(space, fileName);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            byte[] data = os.toByteArray();
            response.resetBuffer();
            response.resetBuffer();
            response.setHeader("Content-Disposition", "attachment");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("image/png");
            IOUtils.write(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setPreviewContentTypeByFileExtension(HttpServletResponse response, String fileName) {
        String fileExtension = getFileExtension(fileName);
        String contentType = getContentTypeByFileExtension(fileExtension);

        if (contentType != null) {
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return null;
    }

    private String getContentTypeByFileExtension(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            // Add more cases for other file extensions as needed
            default:
                return null;
        }
    }

}
