package com.blcultra.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static File thisfile;
    private static File[] files;

    public static  List<String> selectAllFile(String xmlDir,String destpath) {

        List<String> pathName = new ArrayList<String>();
        List<String> filenames = iteratorPath1(xmlDir,pathName,destpath);//遍历xml所在目录，得到xml文件路径信息
        return filenames;
    }
    public static List<String> iteratorPath1(String dir, List<String> pathName,String destpath){
        thisfile = new File(dir);
        files = thisfile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pathName.add(destpath+File.separator+file.getName());
                } else if (file.isDirectory()) {
                    iteratorPath1(file.getAbsolutePath(),pathName,file.getAbsolutePath());
                }
            }
        }
        return pathName;
    }
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
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }
    /**
     * 创建文件夹
     * @param path
     */
    public static void createFile(String path) {
        File file = new File(path);
        //判断文件是否存在;
        if (!file.exists()) {
            //创建文件;
            file.mkdirs();
        }
    }

    /**
     * 判断文件或者文件夹是否存在，若不存在则创建
     * @param filepath
     * @param type
     */
    public  static  void checkFileOrDirExist(String filepath,String type){
        if(type.equals("file")){
            File file=new File(filepath);
            if(!file.exists())
            {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }else if(type.equals("dir")){
            File file =new File(filepath);
            //如果文件夹不存在则创建
            if(!file.exists()  && !file.isDirectory()){
                System.out.println("//文件夹不存在");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.mkdir();
            }else{
                System.out.println("//目录存在");
            }
        }
    }

    /**
     * 检查文件是否已经存在
     * @param filepath
     */
    public  static  boolean checkFileIfExsit(String filepath){
        File file = new File(filepath);
        if(file.exists())
            return true;
        else
            return false;
    }

    /**
     * multipartfile转换为file
     * @param file
     * @param filedir
     * @return
     * @throws Exception
     */
    public static  File MultipartFileTransforToFile(MultipartFile file, String filedir) throws Exception{
        File f = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else{
            InputStream ins = file.getInputStream();
            f=new File(filedir+file.getOriginalFilename());
            inputStreamToFile(ins,f,filedir);
        }

//        deleteFile(f);
        return f;
    }

    public static void inputStreamToFile(InputStream ins,File file,String originfilepath) {
        try {
            OutputStream os = null;
            if(file != null){
                os =new FileOutputStream(file);
            }else{
                os =new FileOutputStream(originfilepath);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除
     *
     * @param files
     */
    public static  void deleteFile(File... files) throws Exception{
        for (File file : files) {
            if (file.exists()) {
                if(file.isDirectory()){
                    deleteDir(file,file.listFiles());
                }  else{
                    boolean flag = file.delete();
                    System.out.println("======删除文件==== "+flag);
                }

            }
        }
    }

    private static  void deleteDir(File  parentDir,File... files) throws Exception{
        for (File file : files) {
            if (file.exists()) {
                if(file.isDirectory()){
                    deleteDir(file,file.listFiles());
                }else{
                    boolean flag1 = file.delete();
                    file = null;
                    System.out.println("======删除文件==== "+flag1);
                }
            }
        }
        boolean flag = parentDir.delete();
        System.out.println("======删除文件夹==== "+flag);
    }


    /**
     * 检查文件夹是否是空，如果不为空则清空
     * @param dir
     * @throws Exception
     */
    public static  void checkDirAndEmptyDir(String dir) throws Exception{
       File file = new File(dir);
       if(file.exists() && file.isDirectory()){
           File[] files = file.listFiles();
           if(null != files){
               for(File file1 : files){
                   boolean flag = file1.delete();
                   System.out.println(flag);
               }
           }
       }
    }

    /**
     * 文件复制
     * @param file
     * @throws Exception
     */
    public static File FileCopy(File file,String outdir) throws Exception{
        String outfilepath = outdir+file.getName();
        Files.copy(file.toPath(), new File(outfilepath).toPath());

        return new File(outfilepath);
    }
    /**
     * 文件复制
     * @param file
     * @throws Exception
     */
    public static File FileCopy(File file,long current,String outdir) throws Exception{
        String outfilepath = outdir+current+file.getName();
        Files.copy(file.toPath(), new File(outfilepath).toPath());

        return new File(outfilepath);
    }

    /**
     * 单文件下载
     * @param filepath
     * @param response
     * @throws Exception
     */
    public static void downloadSingleFile1(String filepath, HttpServletResponse response) throws Exception{
        InputStream in = new FileInputStream(filepath);
        String filename = filepath.substring(filepath.lastIndexOf(File.separator)+1);
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        byte buf[] = new byte[1024];//缓存作用
        //设置文件名及后缀
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);// 设置文件名
        os = response.getOutputStream();//输出流
        while( (len = in.read(buf)) > 0 ) //切忌这后面不能加 分号 ”;“
        {
            os.write(buf, 0, len);//向客户端输出，实际是把数据存放在response中，然后web服务器再去response中读取
        }
        in.close();
        os.close();
    }

    public static void downloadSingleFile(String filepath, HttpServletResponse response) throws Exception{
        FileInputStream fis = null;
        XWPFDocument document;

        try {
            File file = new File(filepath);

            String filename = file.getName();
            fis = new FileInputStream(file);
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            document = new XWPFDocument(OPCPackage.open(fis));
            document.write(response.getOutputStream());
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static void doExport(String aFileName, String aFilePath, HttpServletRequest request, HttpServletResponse response){
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File file = null;
        file = new File(aFilePath);
        try{
            request.setCharacterEncoding("UTF-8");
            String agent = request.getHeader("User-Agent").toUpperCase();
            if ((agent.indexOf("MSIE") > 0) || ((agent.indexOf("RV") != -1) && (agent.indexOf("FIREFOX") == -1)))
                aFileName = URLEncoder.encode(aFileName, "utf-8");
            else {
                aFileName = new String(aFileName.getBytes("utf-8"), "ISO8859-1");
            }
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            response.setHeader("Content-disposition", "attachment; filename=" + aFileName);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
                bos.write(buff, 0, bytesRead);
            System.out.println("success");
            bos.flush();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("导出文件失败！");
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
//                file.delete();
            } catch (Exception e) {
//               LOGGER.error("导出文件关闭流出错！", e);
            }
        }
    }
}
