package com.blcultra.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sgy05 on 2019/7/31.
 */
public interface FileService {

    String uploadXml(String uid , MultipartFile[] file);

    String upload(String uid , MultipartFile file);

    String uploadExcel(String uid ,MultipartFile file,String checktype,String usetype);

    String uploadFormDataDocId(String uid,String docids);

    String loadExcelFileData(String uid ,String filename,String type);

    String importReport(HttpServletRequest request, HttpServletResponse response, String uid, String pid);

    String getResultFile(HttpServletRequest request, HttpServletResponse response,  String file);
}
