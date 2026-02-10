package com.jsdc.iotpt.util;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

@Service
public class FileRepository implements ServletContextAware {

	private ServletContext ctx;

	public void setServletContext(ServletContext servletContext) {
		this.ctx = servletContext;
	}

	/**
	 * 存储文件
	 * 
	 * @param path
	 * @param ext
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String storeByExt(String path, String ext, MultipartFile file) throws IOException {
		String filename = UploadUtils.generateFilename(path, ext);
		File dest = new File(getRealPath(filename));
		store(file, dest);
		return filename;
	}

    /**
     * 存储文件
     *
     * @param file 文件
     * @param path 文件名
     * @return
     * @throws IOException
     */
    public void storePhotoByExt(MultipartFile file, String path) throws IOException {
        File dest = new File(getRealPath(path));
        store(file, dest);
    }

	/**
	 * 保存文件
	 * 
	 * @param file
	 * @param dest
	 * @throws IOException
	 */
	private void store(MultipartFile file, File dest) throws IOException {
		UploadUtils.checkDirAndCreate(dest.getParentFile());
		file.transferTo(dest);
	}

	/**
	 * 获取真实路径
	 * 
	 * @param name
	 * @return
	 */
	public String getRealPath(String name) {
		String realpath = ctx.getRealPath(name);
		if (realpath == null) {
			realpath = ctx.getRealPath("/") + name;
		}
		return realpath;
	}

}
