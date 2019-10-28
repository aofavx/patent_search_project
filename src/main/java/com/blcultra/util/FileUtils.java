/**
 * ProjectName:    patent-test
 * PackageName:    com.ultrapower.bdpm.util
 * FileName：      FileUtil.java
 * Copyright:      Copyright(C) 2018
 * Company:        北京神州泰岳软件股份有限公司
 * Author:         admin
 * CreateDate:     2018/9/20 13:48
 */

package com.blcultra.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static File thisfile;
    private static File[] files;

    /**
     * 遍历文件目录
     * @param dir
     * @param pathName
     * @return
     */
    public static List<String> iteratorPath(String dir, List<String> pathName){
        thisfile = new File(dir);
        files = thisfile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pathName.add(file.getName());
                } else if (file.isDirectory()) {
                    iteratorPath(file.getAbsolutePath(),pathName);
                }
            }
        }
        return pathName;
    }

    public static List<String> iteratorPath(String dir){
        List<String> pathName = new ArrayList<>();
        return iteratorPath(dir,pathName);
    }

    /**
     * 读取文件，将文件中的内容装在List中，返回
     * @param filePath
     * @return
     * @throws Exception
     */
    public static List<String> ReadFile(String filePath,String charsetName) throws  Exception{
        List<String> returnList = new ArrayList<>();
        File filename = new File(filePath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename),charsetName);
        BufferedReader bufferReader = new BufferedReader(reader);
        String line = "";
        while((line = bufferReader.readLine()) != null) {
            returnList.add(line);
        }
        reader.close();
        bufferReader.close();
        return returnList;
    }
}
