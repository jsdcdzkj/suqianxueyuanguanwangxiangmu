package com.jsdc.iotpt.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class QRCodeUtil {

   static Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    private static void writeToFile(BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format "
                    + format + " to " + file);
        }
    }

    private static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    /**
     *
     * @param text  二维码内容
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     * @param format 图片格式
     */
    private static ByteArrayOutputStream generateQRCode(String text,int width,int height,String format) throws WriterException, IOException {
        // 内容所使用字符集编码
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        QRCodeUtil.writeToStream(bitMatrix,format,byteArrayOutputStream);
        return byteArrayOutputStream;
    }

    public static ByteArrayOutputStream generateQRCode(String text) throws Exception {
        return generateQRCode(text,300,300,"jpg");
    }

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = generateQRCode("二维码加密串");
        DateFormat dateFormat = new SimpleDateFormat("yyyyMddHHmmss");
        OutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream("D:" + File.separator +  dateFormat.format(new Date()) + "qrCode.jpg");
            outputStream.write(byteArrayOutputStream.toByteArray());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
}
