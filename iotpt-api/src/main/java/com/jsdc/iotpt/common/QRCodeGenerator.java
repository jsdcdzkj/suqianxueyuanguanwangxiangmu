package com.jsdc.iotpt.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;


/**
 * ClassName: QRCodeGenerator
 * Description: 二维码生成工具
 * date: 2023/11/22 8:39
 *
 * @author bn
 */
@Component
public class QRCodeGenerator {

    private int width = 300; // 二维码的宽度
    private int height = 300; // 二维码的高度
    private String format = "png"; // 二维码图片的格式


    public void createORCode(String text,String filePath){
        try {
            // 创建QRCodeWriter对象
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // 设置二维码的编码格式
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            // 创建BufferedImage对象
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 将二维码的像素点颜色设置为黑色
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            // 保存二维码图片
            ImageIO.write(bufferedImage, format, new File(filePath));
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        QRCodeGenerator qrCodeGenerator=new QRCodeGenerator();
//        qrCodeGenerator.createORCode("1111","C:\\Users\\Administrator\\Desktop\\10.png");


        System.out.println(String.format("%d.png",System.currentTimeMillis()));
    }



}
