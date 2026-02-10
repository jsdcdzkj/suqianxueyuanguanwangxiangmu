package com.jsdc.iotpt.water;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Base64;

public class StringUtil {

	/**
	 * 字节数组转成16进制表示格式的字符串
	 *
	 * @param byteArray 需要转换的字节数组
	 * @return 16进制表示格式的字符串
	 **/
	public static String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException("this byteArray must not be null or empty");

		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)// 0~F前面不零
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase();
	}

	/**
	 * base64 字符串转换为16进字符串
	 * @param base64Str
	 * @return
	 */
	public static String base64StrToHexStr(String base64Str){
		byte[] decoded = Base64.getDecoder().decode(base64Str);
		return toHexString(decoded);
	}

	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * @Title:hexString2Bytes
	 * @Description:16进制字符串转字节数组
	 * @param src  16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexString2Bytes(String src) {
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}

	/**
	 * 判断两字符串相同
	 *
	 * @param one
	 * @param two
	 * @return
	 */
	public static boolean isEqual(String one, String two) {
		return one.equals(two) && isNotNullStr(one) && isNotNullStr(two);
	}

	/**
	 * 判断是否为空字符串
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNotNullStr(String str) {
		return null != str && !str.trim().equals("") && !str.trim().equals("null") ? true : false;
	}


	/**
	 * 16进制累加和，校验码 checksum-8算法
	 * @param data
	 * @return
	 */
	public static String makeChecksum(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		 * 用256求余最大是255，即16进制的FF
		 */
		int mod = total % 256;
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0,这里用的是两位校验
		if (len < 2) {
			hex = "0" + hex;
		}
		return hex;
	}

	/**
	 * 10进制累加和
	 * @param data
	 * @return
	 */
	public static String makeChecksum2(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		int sum = 0;
		for (int i = 0; i <= data.length() - 2; i = i + 2 ) {
			String s = data.substring(i,i + 2);
			int v = Integer.parseInt(s,16);
			sum += v;
		}
		if(sum > 0xff){
			sum = ~sum;
			sum = sum + 1;
		}
		return String.format("%x",sum & 0xff);
	}

	/**
	 * 计算CheckSum
	 *
	 * @param src
	 * @return
	 */
	public static int checksum(byte[] src) {
		int sum = 0;
		for (int i = 0; i < src.length; i++) {
			sum += src[i];
		}
		sum=sum&0xff;
		sum=0x100-sum;
		return sum&0xff;
	}

	/**
	 * 16进制字符串转换成字节数组
	 * @param hexStr 16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexDecode(String hexStr) {
		if (hexStr == null || "".equals(hexStr)) {
			return null;
		}
		try {
			char[] cs = hexStr.toCharArray();
			return Hex.decodeHex(cs);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}



}