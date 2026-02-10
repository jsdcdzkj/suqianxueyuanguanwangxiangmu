package com.jsdc.iotpt.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;

/**
 * @Author lb
 * @Date 2023/11/1 10:11
 * @Description 类描述
 **/
public class QRCodeUtils {
    private static final int DEFAULT_QRCODE_SIZE = 300;

    public static String generateQRCodeAsBase64(String text) {
        return generateQRCodeAsBase64(text, DEFAULT_QRCODE_SIZE, DEFAULT_QRCODE_SIZE);
    }

    public static String generateQRCodeAsBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//            // BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
//
//            int width1 = bitMatrix.getWidth();
//            int height1 = bitMatrix.getHeight();
//            BufferedImage image = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
//
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "png", baos);
//
//            byte[] byteArray = baos.toByteArray();
//            String base64Image = Base64.getEncoder().encodeToString(byteArray);
//
//            return base64Image;
            // 手动创建 BufferedImage，并绘制像素
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF; // 黑色或白色
                    image.setRGB(x, y, rgb);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            byte[] byteArray = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(byteArray);

            return base64Image;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) {
        System.out.println(QRCodeUtils.generateQRCodeAsBase64("jskdfjlsdjflksdjlfkjsdfjklsdfjlsd"));
    }
}
