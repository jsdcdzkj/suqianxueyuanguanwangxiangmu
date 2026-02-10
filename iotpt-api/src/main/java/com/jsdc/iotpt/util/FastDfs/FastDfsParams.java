package com.jsdc.iotpt.util.FastDfs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FastDfsParams {
    private String uploadPath;
    private MultipartFile file;
    /**
     * 业务类型 1.商品稽核类型 2.考核 3.清算 4.医疗凭证 5.价格申报-pdf 6.耗材文件上传 7.门慢门特图片上传 8:价格申报-详情文件 9.集采缺货-采购
     */
    private String bizType;
    private String bizId;//业务数据ID
    private String fileName;//文件名


    public FastDfsParams(String uploadPath, MultipartFile file, String bizType, String bizId) {
        this.uploadPath = uploadPath;
        this.file = file;
        this.bizType = bizType;
        this.bizId = bizId;
    }
}
