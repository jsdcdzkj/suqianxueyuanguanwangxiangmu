package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.Vehicle;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 车辆布控
 */
@Service
public class VehicleService extends BaseService<Vehicle> {
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysUserService sysUserService;
    @Value("${jsdc.nginxPath}")
    private String nginxPath;
    /**
     * 新增编辑
     *
     * @param vehicle
     * @param file
     * @return
     */
    public String insertOrUpdate(Vehicle vehicle, MultipartFile file) {
        String msg = "";
        if (vehicle.getId() == null) {
            vehicle.setCreateUser(sysUserService.getUser().getId());
            vehicle.setCreateTime(new Date());
            vehicle.setUpdateTime(new Date());
            vehicle.setIsDel(0);
            msg = "添加成功";
        } else {
            vehicle.setUpdateUser(sysUserService.getUser().getId());
            vehicle.setUpdateTime(new Date());
            msg = "编辑成功";
        }
        this.saveOrUpdate(vehicle);
        if (file != null) {
            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", vehicle.getId()+"").eq("bizType", "5"));
            UploadParams params = new UploadParams("/vehicle", file, "7", vehicle.getId() + "");
            sysFileService.uploadFile(params);
        }
        return msg;
    }

    public ResultInfo uploadFile(MultipartFile file, String id) {
        UploadParams params = null;
        if (file != null) {
            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", id+"").eq("bizType", "5"));
            params = new UploadParams("/vehicle", file, "7", id);
        }
        return sysFileService.uploadFile(params);
    }


    /**
     * 获取图片信息
     *
     * @param id
     * @return
     */
    public SysFile getFile(Integer id) {
        SysFile file = new SysFile();
        List<SysFile> sysFile = sysFileService.list(new QueryWrapper<SysFile>().eq("bizId", id+"").eq("bizType", "7"));
        if (sysFile != null && !sysFile.isEmpty()) {
            file = sysFile.get(sysFile.size() - 1);
            file.setFileUrl(nginxPath + sysFile.get(sysFile.size() - 1).getFileUrl());
        }
        return file;
    }
}

