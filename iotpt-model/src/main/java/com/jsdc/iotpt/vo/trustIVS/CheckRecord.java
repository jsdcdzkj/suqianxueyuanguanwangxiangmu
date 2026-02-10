package com.jsdc.iotpt.vo.trustIVS;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频违规信息
 */

@Data
public class CheckRecord implements Serializable {

    /**
     * fid
     */
    private String fid;

    /**
     * 创建时间
     */
    private Date fcreatetime;

    /**
     * 单位名称
     */
    private String fcompany;

    /**
     * 单位编号
     */
    private String fcompanycode;

    /**
     * 编号
     */
    private String fnumber;

    /**
     * 通道 id
     */
    private String faisleinfoid;

    /**
     * 通道名称
     */
    private String faislename;

    /**
     * 通道国标编号
     */
    private String faislenationCode;

    /**
     * 原图片路径
     */
    private String fimgpath;

    /**
     * 原图片 base64
     */
    private String oriImgBase64;

    /**
     * 标记图片路径
     */
    private String fmarkedimgpath;

    /**
     * 标记图片 base64
     */
    private String imgBase64;

    /**
     * 图片编号
     */
    private Integer fpicnum;

    /**
     * 短视频地址
     */
    private String fvideopath;

    /**
     * 网关设备号
     */
    private String fnetgateno;

    /**
     * 摄像头 SN
     */
    private String fcamerasn;

    /**
     * 分析类型
     * 1 口罩检测 2 老鼠检测 3 帽子检测
     * 4 人脸检测 5 抽烟检测 6 电动车识别
     * 7 头盔识别 8 安全帽识别 9 周界闯入
     * 10 人员聚集 11 车牌识别 12 厨师服检测
     * 13 打手机检测 14 防护服检测
     * 15 货柜号检测 16 离岗检测
     * 17 工作服检测 18 人员倒地检测
     * 19 烟雾检测 20 未关门检测
     * 21 举手识别 22 火焰识别
     * 23 垃圾桶盖识别 24 客流统计
     * 25 动火离人 26 举手呼叫识别
     * 27 未穿劳保服识别 28 车辆闯入识别
     * 98 人脸抓拍 106 车辆违停识别
     * 115 翻越围栏 117 攀爬识别
     * 119 未穿反光衣识别 122 玩手机识别
     */
    private String fanlysistypeid;

    /**
     * 分数
     */
    private String fscore;

    /**
     * 质量分数
     */
    private String fmassfraction;

    /**
     * 区域名称
     */
    private String frectname;

    /**
     * 备注
     */
    private String fdescription;

    private String fpersonqty;


}
