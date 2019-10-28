package com.blcultra.util;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class JsonFileUtil {

    public  static JSONObject ReadJsonFile(String jsonfilepath) throws Exception {
        InputStream ins  = new FileInputStream(new File(jsonfilepath));
        JSONObject json = JsonFileUtil.ReadJsonFile(ins);//解析json文件
        return json;
    }

    /**
     * 读取json文本
     * @param inputStream 文件
     * @return
     */
    public static JSONObject ReadJsonFile(InputStream inputStream) throws Exception{
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        str = StringUtil.replaceBlank(str);
        JSONObject jsonObject = JSONObject.parseObject(str);
        return jsonObject;

    }

    public static String ReadJsonFileToJsonString(String jsonfilepath) throws Exception{
        InputStream inputStream  = new FileInputStream(new File(jsonfilepath));
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        return str;
    }

}
