package com.jsdc.iotpt.util;

import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MD5Utils {

    //springboot security 安全加密
    private static final SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();

    @Deprecated //不建议使用一次MD5加密，不安全。
    public static String getMD5(String content) {
        String result = "";
        try {
          MessageDigest md = MessageDigest.getInstance("md5");
          md.update(content.getBytes());
          byte[] bytes = md.digest();
          StringBuilder sb = new StringBuilder();
          for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xFF);
            if (str.length() == 1) {
              sb.append("0");
            }
            sb.append(str);
          }
          result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        }
        return result;
      }


    /**
     * springboot security 安全加密(建议使用)
     * @param rawPassword 原密码
     * @return 每一次执行相同的密码会被加密不同的密文
     */
    public static String hashPassword(String rawPassword) {
        return passwordEncoder.encode(getMD5(rawPassword));
    }

    /**
     * 验证密码
     * @param rawPassword 原密码
     * @param hashedPassword 随意一次加密后的密码
     * @return 用于验证密码是否正确和hashPassword成对使用
     */
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(getMD5(rawPassword), hashedPassword);
    }

    /**
     *  测试 hashPassword("iotpt-123456")其中一次加密码密文为 $e0801$E+Z2JgAQG1DceBOs08oh5QCBfQaAlWvJSFJYFM91xNSotzvdzoNizANPE5oDhkZ3TguyJgyFaJfFT67yIZQn3g==$qkrPPsKEGDdwVaa5BIONqe2WTCD+hqpdKxi29IyI5DU=
     *  用 verifyPassword("iotpt-123456","$e0801$E+Z2JgAQG1DceBOs08oh5QCBfQaAlWvJSFJYFM91xNSotzvdzoNizANPE5oDhkZ3TguyJgyFaJfFT67yIZQn3g==$qkrPPsKEGDdwVaa5BIONqe2WTCD+hqpdKxi29IyI5DU=")
     *  返回true验证成功
     * @param args
     */
//    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(hashPassword("iotpt-123456"));
//        }
//        System.out.println(verifyPassword("iotpt-123456","$e0801$E+Z2JgAQG1DceBOs08oh5QCBfQaAlWvJSFJYFM91xNSotzvdzoNizANPE5oDhkZ3TguyJgyFaJfFT67yIZQn3g==$qkrPPsKEGDdwVaa5BIONqe2WTCD+hqpdKxi29IyI5DU="));
//    }
}
