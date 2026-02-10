package com.jsdc.iotpt.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class QrCoder {

//    private static String gateOpenUrl = "https://smartpark.dincher.cn/wul/web/app";

//    @Value("${wx.cn}")
//    public void setGateOpenUrl(String gateOpenUrl) {
//        QrCoder.gateOpenUrl = gateOpenUrl;
//    }


    @Value("${wx.cn}")
    private String wxUrl;

    private static String gateOpenUrl;

    // Getter 和 Setter
    @PostConstruct
    public void getMyProperty() {
        gateOpenUrl = this.wxUrl;
    }



    /**
     * 图片的宽度
     */
    private static final int IMAGE_WIDTH = 550;
    /**
     * 图片的高度(需按实际内容高度进行调整)
     */
    private static final int IMAGE_HEIGHT = 550;
    /**
     * 二维码的宽度
     */
    private static final int QR_CODE_WIDTH = 430;
    /**
     * 二维码的宽度
     */
    private static final int QR_CODE_HEIGHT = 430;

    private static final String FORMAT_NAME = "JPG";

    /**
     * @param imgLogo logo图片
     * @param titles   头部标题
     * @param content 内容
     * @param footers  底部文字
     * @param data  二维内容
     */
    public static BufferedImage createQrCode(String imgLogo, List<String> titles, List<String> content, String sigId, List<String> footers, String data) {
        // 头部文字区域高度
        int titleHeight = 50;
        // 创建主模板图片
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D main = image.createGraphics();
        // 设置图片的背景色
        main.setColor(Color.white); //白色
        main.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        // 动态高度
        int height = 0;
        //***********************页面头部 文字****************
        Graphics2D titleRight = image.createGraphics();
        // 设置字体颜色 black黑 white白
        titleRight.setColor(Color.black);
        // 设置字体
        Font titleFont = new Font("宋体", Font.BOLD, 20);
        titleRight.setFont(titleFont);
        titleRight.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        //计算出字符数进行换行
        Integer ti = 0;
        for (String s : titles) {
            // x开始的位置：（图片宽度-字体大小*字的个数）/2
//            int strWidth = 135;
            // 总长度减去文字长度的一半  （居中显示）
            int startX = 100;
            ti++;
            if (ti == titles.size()) {
                height += 25;
                titleRight.drawString(s, startX, height);
            } else {
                height += 30;
                titleRight.drawString(s, startX, height);
            }
        }
//        // 居中 x开始的位置：（图片宽度-字体大小*字的个数）/2
//        int x = (IMAGE_WIDTH - (titleFont.getSize() * title.length())) / 2;
//        titleRight.drawString(title, x, (titleHeight) / 2 + 10);
        height += 5;
        //**********************中间文字部分*********
        Graphics2D centerWord = image.createGraphics();
        // 设置字体颜色，先设置颜色，再填充内容
        centerWord.setColor(Color.black);
        // 设置字体
        Font wordFont = new Font("宋体", Font.PLAIN, 20);
        centerWord.setFont(wordFont);
        centerWord.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        //计算出字符数进行换行
        Integer co = 0;
        if (null != content){
            for (String s : content) {
                // x开始的位置：（图片宽度-字体大小*字的个数）/2
                int strWidth = centerWord.getFontMetrics().stringWidth(s);
                // 总长度减去文字长度的一半  （居中显示）
                int startX = (IMAGE_WIDTH - strWidth) / 2;
                co++;
                if (co == content.size()) {
                    height += 5;
                    centerWord.drawString(s, startX, height);
                } else {
                    height += 30;
                    centerWord.drawString(s, startX, height);
                }
            }
        }
        //插入图片
        Graphics codePic = image.getGraphics();
        BufferedImage codeImg;
        QrConfig config = new QrConfig();
        config.setWidth(QR_CODE_WIDTH);
        config.setHeight(QR_CODE_HEIGHT);
        if (StrUtil.isNotBlank(imgLogo)) {
            config.setImg(imgLogo);
        }
        // null/meeting/sign.do?id=371849ehjui9fsh8fh230fjw
        String url = gateOpenUrl + "/meeting/sign.do?id=" + data;
        codeImg = QrCodeUtil.generate(url, config);
        // 绘制二维码
        codePic.drawImage(codeImg, (IMAGE_WIDTH - QR_CODE_WIDTH) / 2, height, QR_CODE_WIDTH, QR_CODE_HEIGHT, null);
        codePic.dispose();
        //底部文字
        Graphics2D typeLeft = image.createGraphics();
        // 设置字体颜色
        typeLeft.setColor(Color.black);
        // 设置字体
        Font footerFont = new Font("宋体", Font.PLAIN, 15);
        typeLeft.setFont(footerFont);
        typeLeft.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        // x开始的位置：（图片宽度-字体大小*字的个数）/2
        Integer fo = 0;
        height += QR_CODE_HEIGHT ;
        for (String s : footers) {
            // x开始的位置：（图片宽度-字体大小*字的个数）/2
            int strWidth = typeLeft.getFontMetrics().stringWidth(s);
            // 总长度减去文字长度的一半  （居中显示）
            int startX = (IMAGE_WIDTH - strWidth) / 2;
            fo++;
            if (fo == titles.size()) {
                height += 25;
                typeLeft.drawString(s, startX, height);
            } else {
                height += 30;
                typeLeft.drawString(s, startX, height);
            }
        }

//        int startX = (IMAGE_WIDTH - (footerFont.getSize() * footer.length())) / 2;
//        height += QR_CODE_HEIGHT + 20;
//        typeLeft.drawString(footer, startX,height);
        return image;
    }

    public static String BufferedImageToBase64(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        try {
            ImageIO.write(bufferedImage, "png", baos);//写入流中
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();//转换成字节
        BASE64Encoder encoder = new BASE64Encoder();
        String png_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        return "data:image/jpg;base64," + png_base64;
    }
}
