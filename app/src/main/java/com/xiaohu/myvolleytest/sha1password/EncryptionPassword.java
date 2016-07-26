package com.xiaohu.myvolleytest.sha1password;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/7/22.
 */
public class EncryptionPassword {
    //郑斌文 16:36:41
//    先用UTF8转换为字节数组
//    郑斌文 16:36:53
//    然后用SHA1加密成20字节的字节数组
//    郑斌文 16:37:27
//    然后把20字节数组转换为40位字符串
//    郑斌文 16:37:50
//    然后把40位字符串再进行一次上边的流程
    public static String getSHA(String val) {
        MessageDigest md5;
        byte[] m = new byte[20];
        //md5.update(val.getBytes("UTF-8"));
        try {
            md5 = MessageDigest.getInstance("SHA-1");
            md5.update(val.getBytes("UTF-8"), 0, val.length());
            m = md5.digest();//加密
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return getString(m);
    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length);
        String sTemp;
        //16进制
        for (int i = 0; i < b.length; i++) {
            sTemp = Integer.toHexString(0xFF & b[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        System.out.println("sha1::" + sb.toString().length() + "**" + sb.toString());
        return sb.toString();
    }

}
