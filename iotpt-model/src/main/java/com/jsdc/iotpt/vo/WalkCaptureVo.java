package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkCapture;
import lombok.Data;

@Data
public class WalkCaptureVo extends WalkCapture {
    private Integer id;
    private String filePath;
    private String shootTime;
    private String base64;
    private String address;//位置
    private Integer pageNo;
    private Integer pageSize;


}
