package com.jsdc.iotpt.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadParams {
    private String uploadPath;
    private MultipartFile file;
    /**
     * 业务类型 1.楼宇图片 2.应急方案pdf 3、运维管理-我的任务-任务记录图片  4、我的任务图片 5.黑白名单 6. 巡更任务图片 7. 预留（车辆布控）
     */
    private String bizType;
    private String bizId;//业务数据ID

    public UploadParams(String uploadPath, MultipartFile file, String bizType, String bizId) {
        this.uploadPath = uploadPath;
        this.file = file;
        this.bizType = bizType;
        this.bizId = bizId;
    }

    public UploadParams(String uploadPath, MultipartFile file) {
        this.uploadPath = uploadPath;
        this.file = file;
    }
}
