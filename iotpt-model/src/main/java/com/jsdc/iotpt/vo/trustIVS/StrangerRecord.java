package com.jsdc.iotpt.vo.trustIVS;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StrangerRecord implements Serializable {

    /**
     * 主键 id
     */
    private String fid;


    /**
     * 创建时间
     */
    private Date fcreatetime;


    /**
     * 最后修改时间
     */
    private Date flastupdatetime;


    /**
     * 对比日期
     */
    private Date fcompdate;


    /**
     * 对比分数
     */
    private String fscore;


    /**
     * 截取图片路径
     */
    private String fheadpicpath;


    /**
     * 抓拍原图路径
     */
    private String foriginpicpath;


    /**
     * 经度
     */
    private String flongitude;


    /**
     * 纬度
     */
    private String fdimension;


    /**
     * 人脸 ID
     */
    private String fidnum;


    /**
     * 人脸姓名
     */
    private String fname;


    /**
     * 性别 0 男，1 女
     */
    private String fsex;



    /**
     * 年龄
     */
    private String fage;



    /**
     * 证件类型，参考附录 B 人脸属性说明
     */
    private String fidtype;



    /**
     * 证件号
     */
    private String fidno;



    /**
     * 居住地址
     */
    private String faddress;



    /**
     * 标签
     */
    private String flable;



    /**
     * 通道 id
     */
    private String faisleinfoid;



    /**
     * 摄像头 sn
     */
    private String fcamerasn;



    /**
     * 网关号
     */
    private String fnetgateno;



    /**
     * 对比结果 0 不通过，1 通过
     */
    private String fcompresult;



    /**
     * 人脸底库图片路径
     */
    private String fregpicpath;



    /**
     * 默认检测类型的阈值
     */
    private String fthreshold;



    /**
     * 抓拍图片的截取图
     */
    private String headPicBase64;



    /**
     * 抓拍原图片
     */
    private String originPicBase64;



    /**
     * 通道国标号
     */
    private String fnationcode;



    /**
     * 通道名称
     */
    private String faislename;



    /**
     * 人脸库别编号
     */
    private String facelibnumber;

}
