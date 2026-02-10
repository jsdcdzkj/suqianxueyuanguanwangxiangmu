package com.jsdc.iotpt.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UploadUtils {

	/**
	 * 日期格式化对象
	 */
	public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("/yyyyMM/ddHHmmss");
	
	/**
	 * 创建路径
	 * @param dir
	 */
	public static void checkDirAndCreate(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	/**
	 * 生成文件名
	 * @param path
	 * @param ext
	 * @return
	 */
	public static String generateFilename(String path, String ext) {
		return path + MONTH_FORMAT.format(new Date()) + RandomStringUtils.random(4, Num62.N36_CHARS) + "." + ext;
	}

	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	public static String uploadFile(MultipartFile file,String FilePath){
		String filePath = FilePath + "/" + file.getOriginalFilename();
		try {
			String originalFilename = file.getOriginalFilename();
			int index = originalFilename.lastIndexOf(".");
			File targetFile = new File(filePath);

			if (!targetFile.getParentFile().exists()) {
				boolean mkdirs = targetFile.getParentFile().mkdirs();
				if (!mkdirs) {
					throw new Exception("创建文件夹失败");
				}
			}
			file.transferTo(targetFile);
		} catch (Exception e) {
			System.out.println(e);
		}
		return filePath;
	}

	/**
	 * 上传文件 uuid
	 * 在指定位置创建临时文件,文件名称取uuid 缓存数据
	 * @param file
	 * @return
	 */
	public static String uploadUUIDFile(MultipartFile file,String FilePath){
		String filePath = FilePath + "/" + UUID.randomUUID().toString();
		try {
			File targetFile = new File(filePath);
			if (!targetFile.getParentFile().exists()) {
				boolean mkdirs = targetFile.getParentFile().mkdirs();
				if (!mkdirs) {
					throw new Exception("创建文件夹失败");
				}
			}
			file.transferTo(targetFile);
		} catch (Exception e) {
			System.out.println(e);
		}
		return filePath;
	}
}
