package com.jsdc.iotpt.water;

public class ByteUtils {

    /**
     * 转换字节排序 大端小端互相转换
     * @param bytes
     * @return
     */
    public static byte[] converByteSort(byte[] bytes) {
        byte[] res = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            res[i] = bytes[bytes.length - i - 1];
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println();
    }

    /**
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     * @param src  16进制字符串
     * @return 字节数组
     */
    public static byte[] parseByteArrayForBase64(String src) {
        return parseByteArrayForBase64(src,false);
    }

    public static byte[] parseByteArrayForBase64(String src,Boolean isConverByteSort) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return isConverByteSort?converByteSort(ret):ret;
    }

    public static String bytesToHex(byte[] bytes,boolean addZero) {
//        return new BigInteger(1, bytes).toString(16);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] > 0){
                addZero = true;
            }
            if(bytes[i] == 0 && addZero) {
                sb.append(String.format("%02x", bytes[i]));
            }else {
                sb.append(String.format("%02x", bytes[i]));
            }
        }

//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
        return sb.toString();
    }

    }
