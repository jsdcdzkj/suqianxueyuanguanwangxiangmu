package com.jsdc.iotpt.util.aep;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AepHttpDeviceEncoder {

    public static String aesTime(String ast) {
        byte[] data = parseHexStr2Byte(ast);
        System.out.println(Byte.toUnsignedInt(data[2]));
        int year = Byte.toUnsignedInt(data[3]) >> 2;
        int month = ((Byte.toUnsignedInt(data[3]) & 0x03) << 2) + (Byte.toUnsignedInt(data[2]) >> 6);
        int day = (Byte.toUnsignedInt(data[2]) >> 1) & 0x1f;
        int hour = ((Byte.toUnsignedInt(data[2]) & 0x01) << 4) + (Byte.toUnsignedInt(data[1]) >> 4);
        int minute = ((Byte.toUnsignedInt(data[1]) & 0x0f) << 2) + (Byte.toUnsignedInt(data[0]) >> 6);
        int second = (Byte.toUnsignedInt(data[0]) & 0x3f);
        return String.format("20%d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private static String getCheckSum(String hexString) {
        List<Byte> b = new ArrayList<>();//byte[7];
        b.add((byte) 0x82);
        b.add((byte) 0x00);
        b.add((byte) 0x0a);
        b.add((byte) 0x5c);
        b.add((byte) 0x04);
        b.add((byte) 0x00);
        b.add((byte) 0x00);
        b.add((byte) 0x80);
        b.add((byte) 0x3f);
        byte result = sumCheck(b);

        return "";
    }

    private static byte sumCheck(List<Byte> b) {
        int sum = 0;
        for (int i = 0; i < b.size(); i++) {
            int v = b.get(i);
            sum = sum + b.get(i);
        }
        if (sum > 0xff) { //超过了255，使用补码（补码 = 原码取反 + 1）
            sum = ~sum;
            sum = sum + 1;
        }
        return (byte) (sum & 0xff);
    }


    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        private static final String modifyFragmeType = "82";//修改帧类型 （固定）
        private static final Integer configInfoLength = 72;//设备配置信息长度（dec）
        private Integer mvalue;

        private Float value;
        private Integer hexStartIndex;
        private Integer byteLength;
        private Integer serialNumber;

        public Builder setParams(ParamsConfig config) {
            this.hexStartIndex = config.hexStartIndex;
            this.byteLength = config.byteLength;
            return this;
        }

        public Builder serialNumber(Integer serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder value(Float value) {
            this.value = value;
            return this;
        }

        public Builder mvalue(Integer mvalue) {
            this.mvalue = mvalue;
            return this;
        }

        public String getHexString() {
//        设置报警差值是5.0的例子

//        82     固定的修改帧类型
//        000a    序号
//        58      起始寄存器
//        04      值的byte长度
//        0000803f   设置值     (设置下限1M)
//        59        校验和

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(modifyFragmeType);//固定的修改帧类型
            stringBuffer.append(IntegerUtils.integerTo2ByteHexString(serialNumber, false));//序号
            stringBuffer.append(IntegerUtils.integerTo1ByteHexString(configInfoLength + hexStartIndex));//起始寄存器
            stringBuffer.append(IntegerUtils.integerTo1ByteHexString(byteLength));//值的byte长度
            stringBuffer.append(FloatUtils.floatToHexString(value, false));//设置值
            stringBuffer.append(StringUtil.makeChecksum2(stringBuffer.toString()));//校验和
            return stringBuffer.toString();
        }

        public String getHexStringToTime() {

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(modifyFragmeType);//固定的修改帧类型
            stringBuffer.append(IntegerUtils.integerTo2ByteHexString(serialNumber, false));//序号
            stringBuffer.append(IntegerUtils.integerTo1ByteHexString(hexStartIndex));//起始寄存器
            stringBuffer.append(IntegerUtils.integerTo1ByteHexString(byteLength));//值的byte长度
            stringBuffer.append(IntegerUtils.integerTo2ByteHexString(mvalue, true));//设置值
            stringBuffer.append(IntegerUtils.integerTo1ByteHexString(StringUtil.checksum(StringUtil.hexDecode(stringBuffer.toString()))));//校验和

            return stringBuffer.toString();
        }

        public String getBase64String() {
            return Base64.getEncoder().encodeToString(StringUtil.hexString2Bytes(getHexString()));
        }

        public String getBase64StringToTime() {
            return Base64.getEncoder().encodeToString(StringUtil.hexString2Bytes(getHexStringToTime()));
        }
    }

    public enum ParamsConfig {
        Alarm_Down("报警下限", 26, 4),
        Alarm_Upper("报警上限", 30, 4),
        Alarm_Delta("报警差值", 34, 4),
        Sample("采样间隔", 24, 2),
        Transmit("传输间隔", 26, 2);

        ParamsConfig(String name, Integer hexStartIndex, Integer byteLength) {
            this.name = name;
            this.hexStartIndex = hexStartIndex;
            this.byteLength = byteLength;
        }

        private String name;
        private Integer hexStartIndex;
        private Integer byteLength;
    }


}
