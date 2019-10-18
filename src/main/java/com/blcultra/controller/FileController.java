package com.blcultra.controller;

import com.blcultra.service.FileService;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sgy05 on 2019/7/31.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/patent/api/")
public class FileController extends BaseController{

    @Autowired
    private FileService fileService;

    /**
     * 上传需要审核的专利文件
     * @param uid
     * @param files
     * @return
     */
    @PostMapping(value = "/uploadxml",produces = "application/json;charset=UTF-8")
    public  String uploadXml(@RequestData String uid, @RequestParam("files") MultipartFile[] files) {

        String res = fileService.uploadXml(uid,files);
        return res;
    }

    /**
     * 上传zip或rar压缩包文件或者单个xml文件
     * @param uid
     * @param file
     * @return
     */
    @PostMapping(value = "/upload",produces = "application/json;charset=UTF-8")
    public  String upload(@RequestData String uid, @RequestParam("file") MultipartFile file) {

        String res = fileService.upload(uid,file);
        return res;
    }

    /**
     * 上传Excel文件，文件内存有专利的公开号
     * @param uid
     * @param file
     * @param checktype 1:一站式检索   2：评测检索   3：分类
     * @return
     */
    @PostMapping(value = "/uploadExcel",produces = "application/json;charset=UTF-8")
    public  String uploadExcel(@RequestData String uid,
                               @RequestParam("file") MultipartFile file,
                               String checktype) {

        String res = fileService.uploadExcel(uid,file,checktype);
        return res;
    }

    /**
     * 上传表单数据  docid
     * @param uid
     * @param docids
     * @return
     */
    @PostMapping(value = "/uploadFormData",produces = "application/json;charset=UTF-8")
    public String uploadFormDataDocId(@RequestData String uid,
                                      @RequestParam("docids") String docids){
        String res = fileService.uploadFormDataDocId(uid,docids);
        return res;
    }



    /**
     * 用户在上传Excel文件tab页面中点击确认弹出选择点击【一站式检索】或【评测检索】按钮，触发此接口
     * @param uid
     * @param filename
     * @param type  1:一站式检索    2 ：评测检索
     * @return
     */
    @PostMapping(value = "/loadExcelData",produces = "application/json;charset=UTF-8")
    public String loadExcelFileData(@RequestData String uid ,
                                    @RequestParam(value = "filename",required = true) String filename,
                                    @RequestParam(value = "type",required = true,defaultValue = "1") String type){

        String res = fileService.loadExcelFileData(uid,filename, type);

        return res;
    }

    /**
     * 导出检索报告
     *
     * @return
     */
    @GetMapping(value = "/import")
    public  String importReport(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestData String uid,
                                @RequestParam(value = "pid",required = true) String pid) {

        String res = fileService.importReport(request,response,uid,pid);
        return res;
    }

//    @PostMapping(value = "/init",produces = "application/json;charset=UTF-8")
//    public  String init(@RequestParam("xmlpath") String xmlpath) {
//
//        String res = dataInitService.init(xmlpath);
//        return res;
//    }


}
