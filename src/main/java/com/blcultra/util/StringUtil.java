package com.blcultra.util;

import java.util.UUID;

/**
 * 功能描述:
 * 关于String 类型的操作的util方法
 * @param:
 * @return:
 * @auther: guyuefei
 * @date:
 */

public class StringUtil {

    /*
         * 是否为空字符串
         * @param str
         * @return
         */
    public static boolean isBlank(String str){
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }
    /**
     * 验证字符串是否为空
     *
     * @param param
     * @return
     */
    public static boolean empty(String param) {
        return param == null || "".equals(param) || param.trim().length() < 1 || param.equals("null");
    }

    /**
     * 比较两个字符串是否相等
     * @param str1、str2  待比较字符窜
     */
    public static boolean isSame(String str1, String str2){
        if(empty(str1) && empty(str2))
            return true;
        else if(!empty(str1) && !empty(str2))
            return str1.equals(str2);
        else
            return false;
    }

    /**
     * 生成主键uuid
     *
     * @return
     */
    public static String getUUID() {
        //UUID.randomUUID()生成的32位字符串每隔8位中间以 “-”连接，去掉“-”换成“”
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 替换特殊字符
     * @param word
     * @return
     */
    public static String regAndReplace(String word) {
//        String patten = "/[a-zA-Z]+";
        String patten = "/[m]+";
        //String patten2 = "/[，、。！？“”《》-]+";
        String patten2 = "./w";
        return word.replaceAll(patten,"")
                //.replaceAll("XML/n","")
                //.replaceAll("_/n","")
                //.replaceAll("\\pP","").
                .replaceAll(patten2,"")
                .trim();
    }

}
