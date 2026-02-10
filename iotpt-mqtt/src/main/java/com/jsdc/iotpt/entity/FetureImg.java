package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特征图信息对象(数组)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetureImg {

    /**
     * 图片数据  base64格式的数据
     */
    private String image;
    /**
     * 图片原始大小
     */
    private Integer image_length;
    /**
     * 图片发送标志
     */
    private Integer image_send_flag;
    /**
     * 图片的索引id
     */
    private String key;
    /**
     * 图片缓存的路径名
     */
    private String path;
    /**
     * 图片分辨率
     */
    private Resolution resolution;

    private Integer type;
}
