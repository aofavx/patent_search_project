//package com.blcultra.task;
//
//import com.alibaba.fastjson.JSON;
//import com.blcultra.support.PatentConstant;
//import com.blcultra.util.FileUtil;
//import com.blcultra.util.JsonFileUtil;
//import com.blcultra.util.PatentSearchUtil;
//import com.blcultra.util.StringUtil;
//import com.bonc.usdp.sql4es.jdbc.ESConnection;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.sql.DriverManager;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Callable;
//
//public class FindSimilarDoc2 implements Callable<Map>{
//
//    Log log = LogFactory.getLog(FindSimilarDoc2.class);
//
//    private ESConnection esConnection;
//    private ESConnection es_cn_Connection;
//    private String sequence;
//    private String docid;
//    private Integer num;
//    private String date;
//    private String doctype;
//    private boolean googleflag;
//
//    @Value("${es.jdbc.url}")
//    private String esjdbcurl = PatentConstant.esjdbcurl;
//    @Value("${es.cn.jdbc.url}")
//    private String cn_es_jdbcurl = PatentConstant.cn_es_jdbcurl;
//    @Value("${csv.out.dir.path}")
//    private String csvoutdirpath = PatentConstant.csvoutdirpath;
//    private static final String jsonfilepath = PatentConstant.jsonfilepath;
//
//    public FindSimilarDoc2(ESConnection esConnection, ESConnection es_cn_Connection, String doctype,
//                           String sequence, String docid, Integer num, String date, boolean googleflag){
//        this.sequence = sequence;
//        this.esConnection = esConnection;
//        this.es_cn_Connection = es_cn_Connection;
//        this.docid = docid;
//        this.num = num;
//        this.date = date;
//        this.doctype = doctype;
//        this.googleflag = googleflag;
//    }
//    public FindSimilarDoc2(ESConnection es_cn_Connection, String docid){
//        this.sequence = sequence;
//        this.esConnection = esConnection;
//        this.es_cn_Connection = es_cn_Connection;
//        this.docid = docid;
//        this.num = num;
//        this.date = date;
//        this.doctype = doctype;
//        this.googleflag = googleflag;
//    }
//    @Override
//    public Map call() throws Exception {
//
//        if((null == es_cn_Connection || es_cn_Connection.isClosed()) && PatentConstant.doctype_cn.equals(doctype) && ! googleflag){
//            Class.forName("com.bonc.usdp.sql4es.jdbc.ESDriver");
//            es_cn_Connection = (ESConnection) DriverManager.getConnection(cn_es_jdbcurl);
//        }
//        //输出tsv文件
//        System.out.println("==================== get origin file content ===================");
////        Map<String,String> contents = new HashMap<>();
//
//        Map<String,String> contents = PatentSearchUtil.getContents2(es_cn_Connection,docid);
//
//        System.out.println("==================== get search file content ===================");
//        List<Map<String,String>> searchRes = PatentSearchUtil.getCompareDocIds2(esConnection,contents,num);
//
//
//        StringBuilder allbuilder = new StringBuilder("");
//        for(Map<String,String> resdocid : searchRes){
//
//            allbuilder.append(docid + "\t");
//            allbuilder.append(resdocid.get(PatentConstant.finaldocid) + "\t");
//            allbuilder.append(contents.get(PatentConstant.title) + "\t");
//            allbuilder.append(resdocid.get(PatentConstant.title) + "\t");
//            allbuilder.append(contents.get(PatentConstant.abs) + "\t");
//            allbuilder.append(resdocid.get(PatentConstant.abs) + "\t");
//            allbuilder.append(contents.get(PatentConstant.claims) + "\t");
//            allbuilder.append(resdocid.get(PatentConstant.claims) + "\n");
//        }
//        //输出文件路径： /home/suda_bert/data/all.txt
//        String path = PatentConstant.txtfilepath + PatentConstant.result_all +".txt";
//        FileUtil.writeContentAppend(path,allbuilder.toString());
//
//        return null;
//    }
//
//}
