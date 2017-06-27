package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 给指定字符串按照MD5算法加密
 * Created by jules on 2017/6/27.
 */
public class MD5util {
    private static String salt="kfjakdfjad";//加盐,增加密码破解难度
    public static  void encoder(String psd){
        psd=psd+salt;
        try {
            //指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //将需要加密的字符串转换为byte类型数组，然后进行随机哈希过程
            byte[] bs = digest.digest(psd.getBytes());
            System.out.print(bs.length);
            //拼接字符串
            StringBuffer sb= new StringBuffer();
            //循环遍历bs，然后让其生成32位字符串，固定写法
            for (byte b:bs) {
                int i= b & 0xff;
                //int 类型的i需要转换成16进制字符串
                String hexString = Integer.toHexString(i);
                if (hexString.length()<2){
                    hexString="0"+hexString;
                }
                sb.append(hexString);
            }
            System.out.print(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }
}
