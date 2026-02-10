package com.jsdc.iotpt.util;

import org.junit.Test;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;

public class PicDownload {

    @Test
    public void test() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("D://temp/aaaaa.jpg");
        ByteArrayOutputStream byteArrayOutputStream = download("https://10.35.239.168/evo-apigw/evo-oss/10189673-edf6-11eb-bb8f-101965e7ed70/20220414/1/c7de5271-bba0-11ec-8f65-101965e7ed70.jpg?token=1:946a0391-dddf-4ab7-b666-381f7338252f");
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.close();
    }

    public void download(String picUrl,String targetPath){
        try {
            // 构造URL
            URL url = new URL(picUrl);
            trustAllHosts();
            // 打开连接
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            String url1 = picUrl.substring(picUrl.lastIndexOf("/"));
            String url2 = url1.substring(1,url1.lastIndexOf("?"));
            OutputStream os = new FileOutputStream(targetPath + "/" + url2);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream download(String picUrl){
        ByteArrayOutputStream os = null;
        InputStream is = null;
        try {
            // 构造URL
            URL url = new URL(picUrl);
            trustAllHosts();
            // 打开连接
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            os = new ByteArrayOutputStream();
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            return os;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 完毕，关闭所有链接
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
