package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.constants.PatentSearchConstant;
import com.blcultra.dao.PatentCollectInfoMapper;
import com.blcultra.dao.PatentInfoMapper;
import com.blcultra.dao.PatentTextDetailMapper;
import com.blcultra.dao.PatentUserMapper;
import com.blcultra.model.*;
import com.blcultra.service.FileService;
import com.blcultra.support.FileTypeEnum;
import com.blcultra.support.JsonModel;
import com.blcultra.support.PatentConstant;
import com.blcultra.util.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by sgy05 on 2019/7/31.
 */
@Service(value = "fileService")
public class FileServiceImpl implements FileService {
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value(value = "${spring.elasticsearch.index}")
    private String index;
    @Value(value = "${spring.elasticsearch.type}")
    private String type;

    @Value(value = "${patent.upload.check.xml.path}")
    private String xmluploadpath;
    @Value(value = "${patent.upload.unpack.xml.path}")
    private String xmlunpackagedestpath;
    @Value(value = "${patent.import.report.path}")
    private String importreportpath;
    @Value(value = "${patent.template.report.path}")
    private String templatereportpath;
    @Value(value = "${bcc.api.patent.classify.api}")
    private String classify_api;
    @Value(value = "${evaluate.search.usetype}")
    private String evaluatesearchusetype;



    @Autowired
    private PatentInfoMapper patentInfoMapper;
    @Autowired
    private PatentTextDetailMapper patentTextDetailMapper;

    @Autowired
    private PatentCollectInfoMapper patentCollectInfoMapper;
    @Autowired
    private PatentUserMapper patentUserMapper;
    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    RestHighLevelClient restHighLevelClient;
    /**
     *上传专利申请文件
     * @param files
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadXml(String uid, MultipartFile[] files) {

        try {
            if (files.length == 0 || StringUtils.isEmpty(uid) ){
                JsonModel json = new JsonModel(false, "参数错误","400",null);
                return JSON.toJSONString(json);
            }
            for (MultipartFile file : files) {
                Map<String,Object> map = new HashMap<>();
                //得到上传时的文件名
                String fileName = file.getOriginalFilename();

                FileUtil.uploadFile(file.getBytes(), xmluploadpath, fileName);
                map = PatentXmlAnalysis.AnalysisXmlToList(xmluploadpath+fileName,true,true,true);

                String applicant = String.valueOf(map.get(PatentConstant.PATENT_APPLICANT));//获取申请人姓名
                String applicantnum = String.valueOf(map.get(PatentConstant.PATENT_APPLICANTNUM));//获取专利申请号
                String applicanttime = String.valueOf(map.get(PatentConstant.PATENT_APPLICANTTIME));//获取专利申请时间
                String patentname = String.valueOf(map.get(PatentConstant.PATENT_NAME));//获取专利名
                String abs = String.valueOf(map.get(PatentConstant.PATENT_ABSTRACT));
                String claim = String.valueOf(map.get(PatentConstant.PATENT_ABSTRACT));
                String desc = String.valueOf(map.get(PatentConstant.PATENT_DESC));

//                List<String> list = (List<String>) map.get("all");
//                StringBuffer stringBuffer = new StringBuffer();
//                for (String s : list) {
//                    stringBuffer.append(s+"\r");
//                }
                String pid = StringUtil.getUUID();
                PatentInfoWithBLOBs patentInfo = new PatentInfoWithBLOBs();
                patentInfo.setAbs("");
                patentInfo.setApplicant(applicant);
                patentInfo.setAppid(applicantnum);
                patentInfo.setAdate(applicanttime);
                patentInfo.setTitle(patentname);
                patentInfo.setAbs(abs);
                patentInfo.setClaims(claim);
                patentInfo.setDescription(desc);
                patentInfo.setPid(pid);
                patentInfo.setUploadpath(xmluploadpath+fileName);
                patentInfo.setUploader(uid);
                patentInfo.setPstate("0");
                int m = patentInfoMapper.insertSelective(patentInfo);

//                PatentTextDetail patentTextDetail = new PatentTextDetail();
//                patentTextDetail.setPdetail(stringBuffer.toString());
//                patentTextDetail.setPid(pid);
//                patentTextDetail.setPtextid(StringUtil.getUUID());
//                int n = patentTextDetailMapper.insertSelective(patentTextDetail);

                JsonModel json = new JsonModel(true, "上传成功","200",null);
                return JSON.toJSONString(json);

            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
        }

        JsonModel json = new JsonModel(false, "上传失败","400",null);
        return JSON.toJSONString(json);
    }

    /**
     * 上传压缩包文件  zip 或 rar
     * @param uid
     * @param uploadfile
     * @return
     */
    @Override
    public String upload(String uid, MultipartFile uploadfile) {

        if (null == uploadfile){
            JsonModel json = new JsonModel(false, "请上传文件","400",null);
            return JSON.toJSONString(json);
        }

        String name = uploadfile.getOriginalFilename();
        String suffix = name.substring(name.lastIndexOf("."));
        boolean isZipPack = false;
        boolean isRarPack = false;
        boolean isXml = false;
        String packFilePath = xmluploadpath + File.separator + uploadfile.getOriginalFilename();
        if (suffix.equalsIgnoreCase(FileTypeEnum.FILE_TYPE_ZIP.fileSuffix)) {
            //zip解压缩处理
            isZipPack = true;
        } else if (suffix.equalsIgnoreCase(FileTypeEnum.FILE_TYPE_RAR.fileSuffix)) {
            //rar解压缩处理
            isRarPack = true;
        }else if (suffix.equalsIgnoreCase(FileTypeEnum.FILE_TYPE_XML.fileSuffix)) {
            isXml = true;
        } else {
            JsonModel json = new JsonModel(false, "上传的文件格式不正确,仅支持xml文件格式和rar或zip压缩包文件!","400",null);
            return JSON.toJSONString(json);
        }
        File file = new File(packFilePath);

        try {
            uploadfile.transferTo(file);

            String destpath = "";
            List<String> list = new ArrayList<>();
            if (isRarPack || isZipPack){
                String uuid = StringUtil.getUUID();//作为解压后存放xml文件的文件夹名称
                destpath = xmlunpackagedestpath+File.separator+uuid+File.separator;
                log.info("*************解压压缩包存放xml的路径"+destpath);
                File destFile = new File(destpath);
                if (!destFile.exists()){
                    destFile.mkdirs();
                }
                if (isZipPack) {
                    //zip压缩包
                    UnPackageUtil.unPackZip1(file, destpath);
                } else {
                    //rar压缩包
                    UnPackageUtil.unPackRar(file, destpath);
                }
                list = FileUtil.selectAllFile(destpath,destpath);
            }else if(isXml) {
                String fileName = uploadfile.getOriginalFilename();
                destpath = xmluploadpath+File.separator+fileName;
                list.add(destpath);
            }

            List<PatentInfoWithBLOBs> plist = new ArrayList<>();
            for (String fileName : list) {

                Map<String,Object> map = PatentXmlAnalysis.AnalysisXmlToList(fileName,true,true,true);

                String applicant = String.valueOf(map.get(PatentConstant.PATENT_APPLICANT));//获取申请人姓名
                String applicantnum = String.valueOf(map.get(PatentConstant.PATENT_APPLICANTNUM));//获取专利申请号
                String applicanttime = String.valueOf(map.get(PatentConstant.PATENT_APPLICANTTIME));//获取专利申请时间
                String patentname = String.valueOf(map.get(PatentConstant.PATENT_NAME));//获取专利名
                String abs = String.valueOf(map.get(PatentConstant.PATENT_ABSTRACT));//摘要
                String claims = String.valueOf(map.get(PatentConstant.PATENT_CLAIMS));//权利声明
                String category = String.valueOf(map.get(PatentConstant.PATENT_CATEGORY));//专利分类


//                List<String> listcontents = (List<String>) map.get("all");
//                StringBuffer stringBuffer = new StringBuffer();
//                for (String s : listcontents) {
//                    stringBuffer.append(s+"\r");
//                }
                String pid = StringUtil.getUUID();

                PatentInfoWithBLOBs patentInfo = new PatentInfoWithBLOBs();
                patentInfo.setApplicant(applicant);
                patentInfo.setAppid(applicantnum);
                patentInfo.setAdate(applicanttime);
                patentInfo.setTitle(patentname);
                patentInfo.setAbs(abs);
                patentInfo.setClaims(claims);
                patentInfo.setCategory(category);

                patentInfo.setPid(pid);
                patentInfo.setUploadpath(fileName);
                patentInfo.setUploader(uid);
                patentInfo.setPstate("0");
                patentInfo.setUploadtime(DateFormatUtil.DateFormat());
                int m = patentInfoMapper.insertSelective(patentInfo);

            }
            JsonModel json = new JsonModel(true, "上传成功","200",null);
            return JSON.toJSONString(json);
        } catch (Exception e) {
            log.error("zip file save to " + xmluploadpath + " error", e.getMessage(), e);
            JsonModel json = new JsonModel(false, "上传文件失败!","400",null);
            return JSON.toJSONString(json);
        }
    }

    /**
     * 上传Excel文件，包含专利公开号
     * @param uid
     * @param uploadfile
     * @return
     */
    @Override
    public String uploadExcel(String uid, MultipartFile uploadfile,String checktype,String usetype) {
        if (uploadfile.isEmpty()){
            log.error("上传文件为空！");
            JsonModel json = new JsonModel(false, "请上传文件","400",null);
            return JSON.toJSONString(json);
        }
        String filename = uploadfile.getOriginalFilename();
        if (!filename.matches("^.+\\.(?i)(xls)$") && !filename.matches("^.+\\.(?i)(xlsx)$")) {
            log.error("上传Excel文件格式不正确");
            JsonModel json = new JsonModel(false, "上传文件格式不正确","400",null);
            return JSON.toJSONString(json);
        }
        String excelFilePath = xmluploadpath + File.separator + uploadfile.getOriginalFilename();
        File file = new File(excelFilePath);

        try {
            uploadfile.transferTo(file);
            String filepath = xmluploadpath+File.separator+filename;
            //读取Excel文件获取第二列专利公开号list
            List<String> list = ExcelUtil.read_excel(filepath);
            log.info("*********解析excel文件读取id列表："+list);
            //一站式检索路线
            //创建搜索请求对象
            SearchRequest searchRequest = new SearchRequest(index);
            //设置搜索类型
            searchRequest.types(type);
            //定义SearchSourceBuilder
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //设置使用termsQuery根据多个id 查询
            searchSourceBuilder.query(QueryBuilders.termsQuery("docid",list));

            searchRequest.source(searchSourceBuilder);
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = search.getHits();
            long total = hits.totalHits;
            log.info("从ES中查询参数为list列表id："+list.toString()+"    查询后的总条数total："+total);
            SearchHit[] searchHits = hits.getHits();

            if (PatentSearchConstant.onestopsearch.equals(checktype)){

                List<Map<String,Object>> plist = new ArrayList<>();
                for (SearchHit hit : searchHits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    log.info("ES data ,search fileds:{},search result :{}",list,JSON.toJSONString(sourceAsMap));
                    String abs = (String) sourceAsMap.get("abs");
                    String prdate = (String) sourceAsMap.get("prdate");
                    String docid = (String) sourceAsMap.get("docid");//公开号
//                    String kind = (String) sourceAsMap.get("kind");
                    String appid = (String) sourceAsMap.get("appid");//申请号
                    String pdate = (String) sourceAsMap.get("pdate");//公开时间
                    String claims = (String) sourceAsMap.get("claims");//权利声明
                    String adate = (String) sourceAsMap.get("adate");//申请时间

                    String category = (String) sourceAsMap.get("category");//类别
                    String title = (String) sourceAsMap.get("title");

                    String pid = StringUtil.getUUID();

                    PatentInfoWithBLOBs patentInfo = new PatentInfoWithBLOBs();
                    patentInfo.setPid(pid);

                    patentInfo.setAppid(appid);
                    patentInfo.setAdate(adate);
                    patentInfo.setTitle(title);
                    patentInfo.setAbs(abs);
                    patentInfo.setClaims(claims);
                    patentInfo.setCategory(category);
                    patentInfo.setPrdate(prdate);
                    patentInfo.setPdate(pdate);
                    patentInfo.setDocid(docid);

                    patentInfo.setUploader(uid);
                    patentInfo.setPstate("0");
                    patentInfo.setUploadtime(DateFormatUtil.DateFormat());
                    int m = patentInfoMapper.insertSelective(patentInfo);
                }
                JsonModel json = new JsonModel(true, "专利数据加载成功","200",null);
                return JSON.toJSONString(json);
            }else if (PatentSearchConstant.autosearch.equals(checktype)){
                //评测路线，查询出专利详情数据，写入磁盘文件，调用模型文件
                evaluateService.evaluate(list,evaluatesearchusetype);

                JsonModel json = new JsonModel(true, "专利数据加载成功","200",null);
                return JSON.toJSONString(json);
            }else if (PatentSearchConstant.classify.equals(checktype)){
                //分类路线：上传docid--->查询出该专利的abs---->封装数据传给分类服务--->接收返回的分类结果数据--->渲染结果页面
                List<Object> plist = new ArrayList<>();
                for (SearchHit hit : searchHits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    log.info("ES data ,search fileds:{},search result :{}", list, JSON.toJSONString(sourceAsMap));
                    String abs = (String) sourceAsMap.get("abs");
                    String docid = (String) sourceAsMap.get("docid");//公开号
                    Map<String,Object> param = new HashMap<>();
                    param.put("id",docid);
                    param.put("content",abs);
                    param.put("topn",3);
                    String j = JSON.toJSONString(param);//map转String
                    JSONObject jsonObject = JSON.parseObject(j);//String转json
                    RestTemplate rest= new RestTemplate();
                    JSONObject jo = rest.postForEntity(classify_api, jsonObject, JSONObject.class).getBody();

                    Map<String,Object> result = (Map<String, Object>) jo.get("result");
                    plist.add(result);

                }
                JsonModel json = new JsonModel(true, "分类成功","200",plist);
                return JSON.toJSONString(json);
            }
        } catch (IOException e) {
            log.error("upload Excel file error：",e);
        }
        JsonModel json = new JsonModel(false, "上传文件失败","400",null);
        return JSON.toJSONString(json);
    }

    /**
     * 用户上传表单docid数据(分类)
     * @param uid
     * @param docids
     * @return
     */
    @Override
    public String uploadFormDataDocId(String uid, String docids) {


        try {
            String[] split = docids.split(",");
            List<String> list = Arrays.asList(split);
            SearchResponse searchResponse = ESUtil.searchByPids(restHighLevelClient, list, index, type);

            SearchHits hits = searchResponse.getHits();
            long total = hits.totalHits;
            SearchHit[] searchHits = hits.getHits();
            List< Map<String,Object>> plist = new ArrayList<>();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                log.info("ES data ,search fileds:{},search result :{}", list, JSON.toJSONString(sourceAsMap));
                String abs = (String) sourceAsMap.get("abs");
                String docid = (String) sourceAsMap.get("docid");//公开号
                Map<String,Object> param = new HashMap<>();
                param.put("id",docid);
                param.put("content",abs);
                param.put("topn",3);
                String j = JSON.toJSONString(param);//map转String
                JSONObject jsonObject = JSON.parseObject(j);//String转json
                RestTemplate rest= new RestTemplate();
                JSONObject jo = rest.postForEntity(classify_api, jsonObject, JSONObject.class).getBody();

                Map<String,Object> result = (Map<String, Object>) jo.get("result");
                plist.add(result);
            }
            if (total==0){
                JsonModel json = new JsonModel(true, "暂无分类数据","200",null);
                return JSON.toJSONString(json);
            }
            JsonModel json = new JsonModel(true, "分类成功","200",plist);
            return JSON.toJSONString(json);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 用户在上传Excel文件tab页面中点击确认弹出选择点击【一站式检索】或【评测检索】按钮，触发此接口
     * @param filename
     * @param checktype  1 ：一站式检索路线逻辑    2：评测检索路线逻辑
     * @return
     */
    @Override
    public String loadExcelFileData(String uid ,String filename, String checktype) {

        if (!filename.matches("^.+\\.(?i)(xls)$") && !filename.matches("^.+\\.(?i)(xlsx)$")) {
            log.error("上传Excel文件格式不正确");
            JsonModel json = new JsonModel(false, "上传文件格式不正确","400",null);
            return JSON.toJSONString(json);
        }
        try {
            String filepath = xmluploadpath+File.separator+filename;
            //读取Excel文件获取第二列专利公开号list
            List<String> list = ExcelUtil.read_excel(filepath);
            if (PatentSearchConstant.onestopsearch.equals(checktype)){
                //一站式检索路线
                //创建搜索请求对象
                SearchRequest searchRequest = new SearchRequest(index);
                //设置搜索类型
                searchRequest.types(type);
                //定义SearchSourceBuilder
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                //设置使用termsQuery根据多个id 查询
                searchSourceBuilder.query(QueryBuilders.termsQuery("docid",list));

                searchRequest.source(searchSourceBuilder);
                SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                SearchHits hits = search.getHits();
                long total = hits.totalHits;
                SearchHit[] searchHits = hits.getHits();

                List<Map<String,Object>> plist = new ArrayList<>();
                for (SearchHit hit : searchHits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    log.info("ES data ,search fileds:{},search result :{}",list,JSON.toJSONString(sourceAsMap));
                    String abs = (String) sourceAsMap.get("abs");
                    String prdate = (String) sourceAsMap.get("prdate");
                    String docid = (String) sourceAsMap.get("docid");//公开号
//                    String kind = (String) sourceAsMap.get("kind");
                    String appid = (String) sourceAsMap.get("appid");//申请号
                    String pdate = (String) sourceAsMap.get("pdate");//公开时间
                    String claims = (String) sourceAsMap.get("claims");//权利声明
                    String adate = (String) sourceAsMap.get("adate");//申请时间

                    String category = (String) sourceAsMap.get("category");//类别
                    String title = (String) sourceAsMap.get("title");

                    String pid = StringUtil.getUUID();

                    PatentInfoWithBLOBs patentInfo = new PatentInfoWithBLOBs();
                    patentInfo.setPid(pid);

                    patentInfo.setAppid(appid);
                    patentInfo.setAdate(adate);
                    patentInfo.setTitle(title);
                    patentInfo.setAbs(abs);
                    patentInfo.setClaims(claims);
                    patentInfo.setCategory(category);
                    patentInfo.setPrdate(prdate);
                    patentInfo.setPdate(pdate);
                    patentInfo.setDocid(docid);

                    patentInfo.setUploader(uid);
                    patentInfo.setPstate("0");
                    patentInfo.setUploadtime(DateFormatUtil.DateFormat());
                    int m = patentInfoMapper.insertSelective(patentInfo);

                    JsonModel json = new JsonModel(true, "专利数据加载成功","200",null);
                    return JSON.toJSONString(json);
                }

            }else if (PatentSearchConstant.autosearch.equals(checktype)){
                //评测路线


                JsonModel json = new JsonModel(true, "专利数据加载成功","200",null);
                return JSON.toJSONString(json);
            }



        } catch (Exception e) {
            log.error("loadExcelFileData method  File not Found error",e);
        }
        JsonModel json = new JsonModel(false, "专利数据加载失败","400",null);
        return JSON.toJSONString(json);
    }

    @Override
    public String importReport(HttpServletRequest request, HttpServletResponse response, String uid, String pid) {

        try {
            String uuid = StringUtil.getUUID();
            String path = importreportpath;
            String pactUrl = templatereportpath;

            Map<String,Object> map = new HashMap<>();
            map.put("pid",pid);
            map.put("collector",uid);
            //查询出收藏列表信息，然后封装导出
            PatentInfoWithBLOBs patent = patentInfoMapper.selectByPrimaryKey(pid);
            List<Map<String,Object>> patents = patentCollectInfoMapper.selectCollectInfos(map);
//collectid, ptype, pdocnum, pdate, pipc, prate, pid, collector, ptermwords, collectstate,
//            collecttime, updatetime
            String uploader = patent.getUploader();
            Map<String,Object> patentUser = patentUserMapper.selectByUserid(uploader);
            String username = String.valueOf(patentUser.get("username"));

            Map<String,Object> param = new HashMap<>();
            param.put("applynum",patent.getAppid());
            param.put("applicanttime",patent.getAdate());
            param.put("applicant",username);
            param.put("applicantnum",patent.getCategory());
//            param.put("keywords","高温 不锈钢 特质");

            List<Map<String,Object>> list = new ArrayList<>();
            for (Map<String, Object> p : patents) {
                Map<String,Object> map1 = new HashMap<>();
                map1.put("type",p.get("ptype"));
                map1.put("num",p.get("pdocnum"));
                map1.put("date",p.get("pdate"));
                map1.put("classifycode",p.get("pipc"));
                list.add(map1);
            }

            //参数填充
            String url = PoiUtil.replaceParams(pactUrl, param, list, path,uuid);
            //文件下载
            String filename = uuid +"report" + ".docx";
            FileUtil.doExport(filename,url,request,response);
            //TODO:导出检索报告成功后，更新被审核专利的状态值为已审核
            PatentInfo patentInfo = patentInfoMapper.selectByPrimaryKey(pid);
            patentInfo.setPstate("1");
            //更新专利状态为已审核状态：1
            int m = patentInfoMapper.updateByPrimaryKey(patentInfo);

            JsonModel json = new JsonModel(true, "导出成功","200",null);
            return JSON.toJSONString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel json = new JsonModel(false, "导出失败","400",null);
        return JSON.toJSONString(json);
    }

    @Override
    public String getResultFile(HttpServletRequest request, HttpServletResponse response, String filename) {
        String absolutefilepath = PatentConstant.txtfilepath + filename;
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);// 设置文件名
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(absolutefilepath));
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }

            os.flush();
            os.close();
            return "下载成功";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
