package com.jsdc.iotpt.util;

import java.math.BigDecimal;

public class MoneyUtils {

    /**
     * 加法运算
     *
     * @param amount1 第一个金额
     * @param amount2 第二个金额
     * @return 运算结果
     */
    public static String add(String amount1, String amount2) {
        BigDecimal a = new BigDecimal(StringUtils.trimToEmpty(amount1));
        BigDecimal b = new BigDecimal(StringUtils.trimToEmpty(amount2));
        return a.add(b).toPlainString();
    }

    /**
     * 减法运算
     *
     * @param amount1 被减金额
     * @param amount2 减去的金额
     * @return 运算结果
     */
    public static String subtract(String amount1, String amount2) {
        BigDecimal a = new BigDecimal(StringUtils.trimToEmpty(amount1));
        BigDecimal b = new BigDecimal(StringUtils.trimToEmpty(amount2));
        return a.subtract(b).toPlainString();
    }

    /**
     * 乘法运算
     *
     * @param amount1    金额
     * @param multiplier 乘数
     * @return 运算结果
     */
    public static String multiply(String amount1, String multiplier) {
        BigDecimal a = new BigDecimal(StringUtils.trimToEmpty(amount1));
        BigDecimal b = new BigDecimal(multiplier);
        return a.multiply(b).toPlainString();
    }

    /**
     * 除法运算
     *
     * @param amount1 被除金额
     * @param divisor 除数
     * @return 运算结果
     */
    public static String divide(String amount1, String divisor) {
        BigDecimal a = new BigDecimal(StringUtils.trimToEmpty(amount1));
        BigDecimal b = new BigDecimal(divisor);
        return a.divide(b, 2, BigDecimal.ROUND_HALF_UP).toPlainString(); // 保留两位小数
    }

    /**
     * 比较两个金额的大小
     *
     * @param amount1 第一个金额
     * @param amount2 第二个金额
     * @return 比较结果（-1：小于，0：等于，1：大于）
     */
    public static int compare(String amount1, String amount2) {
        BigDecimal a = new BigDecimal(StringUtils.trimToEmpty(amount1));
        BigDecimal b = new BigDecimal(StringUtils.trimToEmpty(amount2));
        return a.compareTo(b);
    }

    /**
     * 判断金额是否大于
     *
     * @param amount1 第一个金额
     * @param amount2 第二个金额
     * @return true 如果 amount1 > amount2
     */
    public static boolean greaterThan(String amount1, String amount2) {
        return compare(amount1, amount2) > 0;
    }

    /**
     * 判断金额是否小于
     *
     * @param amount1 第一个金额
     * @param amount2 第二个金额
     * @return true 如果 amount1 < amount2
     */
    public static boolean lessThan(String amount1, String amount2) {
        return compare(amount1, amount2) < 0;
    }

    /**
     * 判断金额是否大于等于
     *
     * @param amount1 第一个金额
     * @param amount2 第二个金额
     * @return true 如果 amount1 >= amount2
     */
    public static boolean greaterThanOrEqual(String amount1, String amount2) {
        return compare(amount1, amount2) >= 0;
    }

    /**
     * 判断金额是否小于等于
     *
     * @param amount1 第一个金额
     * @param amount2 第二个金额
     * @return true 如果 amount1 <= amount2
     */
    public static boolean lessThanOrEqual(String amount1, String amount2) {
        return compare(amount1, amount2) <= 0;
    }


    /**
     * 判断金额是否有效
     *
     * @param amount 金额字符串
     * @return true 表示有效；false 表示无效
     */
    public static boolean isValid(String amount) {
        try {
            new BigDecimal(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 测试示例
//    public static void main(String[] args) {
//        String amount1 = "100.50";
//        String amount2 = "200.75";
//
//        System.out.println("加法: " + add(amount1, amount2)); // 301.25
//        System.out.println("减法: " + subtract(amount2, amount1)); // 100.25
//        System.out.println("乘法: " + multiply(amount1, "2")); // 201.00
//        System.out.println("除法: " + divide(amount2, "3")); // 66.92
//        System.out.println("比较: " + compare(amount1, amount2)); // -1
//        System.out.println("比较: " + greaterThanOrEqual(amount1, amount2)); // false
//        System.out.println("比较: " + lessThanOrEqual(amount1, amount2)); // true
//        System.out.println("比较: " + lessThan(amount1, amount2)); // true
//        System.out.println("比较: " + greaterThan(amount1, amount2)); // false
//        System.out.println("有效性检查: " + isValid("invalid")); // false
//    }
}

