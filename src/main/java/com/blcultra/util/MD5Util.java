package com.blcultra.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by sgy05 on 2019/3/8.
 */
public class MD5Util {
    /**
     * MD5方法
     *
     * @param text 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) throws Exception {
        //加密后的字符串
        String encodeStr= DigestUtils.md5Hex(text+key);
        return encodeStr;
    }

    public static void main(String[] args) throws Exception {
        String s = md5("123456", "wh");
        System.out.println(s);
    }
    /**
     * MD5验证方法
     *
     * @param text 明文;
     * @param key 密钥
     * @param md5 密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if(md5Text.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }

        return false;
    }
}
