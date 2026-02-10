package com.jsdc.iotpt.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DESUtil {
    private static String IV = "union968";

    public static String encryptDES(String src, String key) {
        String s = "";
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] b = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));

            s = Base64.getEncoder().encodeToString(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    public static String decryptDES(String src, String key) {
        String s = "";
        try {
            byte[] bytesrc = Base64.getDecoder().decode(src.getBytes(StandardCharsets.UTF_8));
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] retbyte = cipher.doFinal(bytesrc);
            s = new String(retbyte);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
