package com.blcultra.support;

/**
 * 常量
 * Created by sgy05 on 2019/8/5.
 */
public class PatentConstant {

    //专利申请人
    public final static String PATENT_APPLICANT = "applicant";
    //专利申请号
    public final static String PATENT_APPLICANTNUM = "applicantnum";
    //专利申请时间
    public final static String PATENT_APPLICANTTIME = "applicanttime";
    //专利名
    public final static String PATENT_NAME = "patentname";
    //专利分类类别
    public final static String PATENT_CATEGORY = "category";

    public final static String PATENT_ABSTRACT = "abs";

    public final static String PATENT_DESC = "desc";
    public final static String PATENT_CLAIMS = "claims";


    public final static String PATENT_BCC_SEARCH = "1";
    public final static String PATENT_ES_SEARCH = "2";




    public static final String finaldocid = "docid";
    public static final String doctype_cn = "CN";
    public static final String title = "title";
    public static final String abs = "abs";
    public static final String claims = "claims";
    public static final String pdate = "pdate";
    public static final String description = "description";
    public static final String result_all = "all";

    public static final String doctype_en = "US";

    public static final String csvorigindirpath = "/data/disk1/patent/Django/media/filelist/";

    public static final String csvresultdirpath = "/tmp/";

    public static final String resultdirpath = "/data/disk1/patent/Django/media/csvout/";

    public static final String datefilterpath = "/data/disk1/patent/Django/media/cn_us_citation_publicdate_producedate";

    /**
     *
     */
    public static final String sgyesjdbcurl = "jdbc:sql4es://202.112.195.83:9300/cpatentv2?cluster.name=patent";


    /**
     * 英文专利检索ES库
     */
    public static final String esjdbcurl = "jdbc:sql4es://202.112.195.83:9300/patent821v9?cluster.name=patent";

    /**
     * 中文专利检索ES库
     */
    public static final String cn_es_jdbcurl = "jdbc:sql4es://202.112.195.83:9300/patent821v9?cluster.name=patent";

    /**
     * 调用苏大服务前生成title、claim、abs文本位置，作为苏大服务的输入
     */
    public static final String csvoutdirpath = "/data/disk1/patent/Django/media/csvout/";
//    public static final String csvoutdirpath = "/Users/guyuefei/testout/";//本地测试

    /**
     * 中文专利调用google翻译生成结果
     */
    public static final String jsonfilepath = "/data/disk1/patent/Django/media/json/result.json";
//    public static final String jsonfilepath = "/Users/guyuefei/test/result.json";//本地测试

    //2019-10-21
    public static final String txtfilepath = "/home/suda_bert/data/";



}