package com.jsdc.iotpt.model;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@TableName("GROUND_LOCKS")
@Table(name = "GROUND_LOCKS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value = "地锁")
public class GroundLocks implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "地锁id")
    private String id;

    private String sign;
    /**
     * 消息类型
     */
    private String msg_type;

    private String msg_id;

    private String timestamp;

    private String serial;
    private String time_s;
    /**
     * 触发类型
     * 5:虚拟线圈触发; 6:稳定触发; 7:IO强制触发; 8:⼿动触发; 9:SDK抓拍; 10:实时结果（⻋辆实时⼊场事件） 12:单帧识别结果; 13:定时触发;
     */
    private Integer trigger_type;

    //"device_info": {},
    /**
     * 设备名，UTF8+BASE64编码
     */
    private String dev_name;
    private String ip;
    private String sn;

    //"product_h": {},"parking": {},
    /**
     * ⻋位id
     */
    private Integer parking_zone_id;
    /**
     * ⻋位名，UTF8+BASE64编码
     */
    private String parking_zone_name;
    /**
     * ⻋位状态
     * 1：⼊场； 2：在场； 4：出场； 8：空场； 16：⻋位异常； 32：延迟上报出场； 64：合并出⼊场； 128：预⼊场； 256：预出场； 512：⼊场修正
     */
    private Integer parking_parking_state;
    //"product_h": {},"plate": {},
    /**
     * ⻋牌颜⾊
     * 0:未知; 1:蓝⾊; 2:⻩⾊; 3:⽩⾊; 4:⿊⾊; 5:绿⾊;
     */
    private Integer plate_plate_color;
    /**
     * ⻋牌置信度
     */
    private Integer plate_confidence;
    /**
     * ⻋牌
     * UTF8后再BASE64编码 如果有⻋牌:真实⻋牌号 如果有⻋⽆牌:⽆牌⻋ 如果⽆⻋⽆牌:__⽆__ ⾮机动⻋:⾮机动⻋
     */
    private String plate_plate;
    /**
     * ⻋牌类型 int 是
     */
    private Integer plate_plate_type;
    /**
     * ⻋牌类型
     * 0:未知⻋牌 1:蓝牌⼩汽⻋ 2:⿊牌⼩汽⻋ 3:单排⻩牌 4:双排⻩牌(⼤⻋尾牌，农⽤⻋) 5:警⻋⻋牌 6:武警⻋牌 7:个性化⻋牌 8:单排军⻋牌 9:双排军⻋牌 10:使馆⻋牌 11:⾹港进出中国⼤陆⻋牌 12:农⽤⻋牌 13:教练⻋牌 14:澳门进出中国⼤陆⻋牌 15:双层武警⻋牌 16:武警总队⻋牌 17:双层武警总队⻋牌 18:⺠航⻋牌 19:新能源⻋牌 20:新能源⻋牌⼤ 21:应急 22:领馆 31:⽆牌⻋
     */
    private Integer plate_type;
    //"product_h": {},"reco": {},
    /**
     * 识别标志
     */
    private Integer reco_flag;
    /**
     * 识别组id
     */
    private Integer reco_group_id;
    private String reco_car_brand;
    private String reco_time;
    private Integer reco_id;
    private String reco_car_type;
    //"product_h": {},"car_pos
    private Integer car_pos;
    //"trigger_type": 5,
    //"bg_img": [],
    private JSONArray bg_img;

    //"alarm_info": {},
    /**
     * 报警状态
     * 0:⽆报警,默认值(取消报警) 1:视频遮挡 3:占⽤双⻋位/压线停⻋ 4:⾮机动⻋/摩托⻋ 5:⻋头/⻋尾⽅向不⼀致 7:⽆牌⻋ 9：⾏⼈滞留(预留) （10-99:保留） ==“占⽤双⻋位/压线停⻋”和“⻋头/⻋尾⽅向不⼀致”,报警 属于证据链
     */
    private Integer alarm_status;
    /**
     * ⽩名单/准⼊⻋型状态
     * 1:在⽩名单/准⼊⻋型内，且有效
     * 2:不在⽩名单/准⼊⻋型内，或验证未通过
     * ==启⽤⽩名单后，优先判断⽩名单==
     */
    private Integer alarm_whitelist;

    /**
     * 识别组id
     */
    private Integer group_id;
}
