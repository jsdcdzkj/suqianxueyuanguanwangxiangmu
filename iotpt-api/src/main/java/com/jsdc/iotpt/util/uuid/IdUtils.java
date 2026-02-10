package com.jsdc.iotpt.util.uuid;

/**
 * ID生成器工具类
 *
 * @author ruoyi
 */
public class IdUtils {

    /**
     * 生成编码
     *
     * @param str      拼接字符串
     * @param sequence 存在的编码数
     * @return
     */
    public static String generateNo(String str, Long sequence) {
        String format = String.format("%04d", Math.toIntExact(sequence));
        return str + format;
    }

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }
}
