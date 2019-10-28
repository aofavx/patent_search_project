package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.support.JsonModel;
import com.blcultra.support.PatentConstant;
import com.blcultra.util.DateFormatUtil;
import com.blcultra.util.ESUtil;
import com.blcultra.util.FileUtil;
import com.blcultra.util.ShellUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by sgy05 on 2019/10/18.
 */
@Service(value = "evaluateService")
public class EvaluateService {
    private static final Logger log = LoggerFactory.getLogger(EvaluateService.class);

    @Value(value = "${spring.elasticsearch.index}")
    private String index;
    @Value(value = "${spring.elasticsearch.type}")
    private String type;


    @Value(value = "${bcc.api.patent.bm25doc.api}")
    private String bm25doc_api;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    public static final String IP = "222.28.84.124";

    public static final int PORT = 22;

    public static final String USERNAME = "ky";

    public static final String PASSWORD = "12345";

    public static final String REMOTEURL = "/home/ky/suda_test/data/test";

//    @Value("${es.cn.jdbc.url}")
    private String cn_es_jdbcurl = PatentConstant.cn_es_jdbcurl;
//    @Value("${threadpool.size}")
    private String threadpoolsize = "10";
    /**
     * 评测逻辑路线
     * @param list  文献号id列表数据
     * @param usetype  1:评测时使用BCC进行粗检索    2：评测时使用ES进行粗检索
     * @return
     */
    public String evaluate(List<String> list,String usetype){


        try {
            SearchResponse searchResponse = ESUtil.searchByPidsAndSize(restHighLevelClient, list, index, type, list.size());
            SearchHits hits = searchResponse.getHits();
            long total = hits.totalHits;
            log.info("【EvaluateService evaluate method  ES search result total】 :"+total);
            SearchHit[] searchHits = hits.getHits();

            List<Map<String,String>> qlist = new ArrayList<>();
            //考题的详细信息列表
            for (SearchHit hit : searchHits) {

                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                log.info("ES data ,search fileds:{},search result :{}",list, JSON.toJSONString(sourceAsMap));
                String docid = (String) sourceAsMap.get("docid");//公开号
                String title = (String) sourceAsMap.get("title");
                String abs = (String) sourceAsMap.get("abs");
                String claims = (String) sourceAsMap.get("claims");//权利声明

                Map<String,String> patent = new HashMap<>();
                patent.put("docid",docid);
                patent.put("title",title);
                patent.put("abs",abs);
                patent.put("claims",claims);
                qlist.add(patent);
            }
            if (usetype.equals(PatentConstant.PATENT_BCC_SEARCH)){
                //bcc粗检逻辑
                for (Map<String, String> stringStringMap : qlist) {
                    String qdocid = stringStringMap.get("docid");//公开号
                    String qtitle = stringStringMap.get("title");
                    String qabs = stringStringMap.get("abs");
                    String qclaims =  stringStringMap.get("claims");//权利声明

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title",qtitle);
                    jsonObject.put("claim",qclaims);
                    jsonObject.put("abstract",qabs);

                    JSONObject jo = restTemplate.postForEntity(bm25doc_api, jsonObject, JSONObject.class).getBody();
                    List<String> pids = (List<String>) jo.get("Pids");
                    log.info("BCC search pids total num :"+pids.size());
                    SearchResponse searchs = ESUtil.searchByPidsAndSize(restHighLevelClient, pids, index, type, pids.size());
                    SearchHits shits = searchs.getHits();
                    long totalHits = shits.totalHits;
                    log.info("BCC SEARCH RESULT PIDS  for ES search result total:"+totalHits);
                    SearchHit[] hits1 = shits.getHits();
                    //答案的详细信息
                    StringBuilder builder = new StringBuilder("");
                    for (SearchHit hit : hits1) {
                        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                        log.info("ES data ,search fileds:{},search result :{}",list, JSON.toJSONString(sourceAsMap));
                        String adocid = (String) sourceAsMap.get("docid");//公开号
                        String atitle = (String) sourceAsMap.get("title");
                        String aabs = (String) sourceAsMap.get("abs");
                        String aclaims = (String) sourceAsMap.get("claims");//权利声明

                        builder.append(qdocid + "\t");
                        builder.append(adocid + "\t");
                        builder.append(qtitle + "\t");
                        builder.append(atitle + "\t");
                        builder.append(qabs + "\t");
                        builder.append(aabs + "\t");
                        builder.append(qclaims + "\t");
                        builder.append(aclaims + "\n");
                    }
                    //输出文件路径： /home/suda_bert/data/all.txt
                    String path = PatentConstant.txtfilepath + PatentConstant.result_all +".txt";
                    FileUtil.writeContentAppend(path,builder.toString());
                }
                //TODO:调用苏大模型服务
                callModel();

                JsonModel resjson = new JsonModel(true, "评测执行成功","200",null);
                return JSON.toJSONString(resjson);
            }else {
//                //ES粗检逻辑
                Set<String> keynums = new HashSet<>(list);
                for (Map<String, String> stringStringMap : qlist) {
                    String qdocid = stringStringMap.get("docid");//公开号
                    String qtitle = stringStringMap.get("title");
                    String qabs = stringStringMap.get("abs");
                    String qclaims = stringStringMap.get("claims");//权利声明

                    StringBuilder stringBuilder = new StringBuilder();
                    StringBuilder append = stringBuilder.append(qtitle).append(qabs).append(qclaims);
                    String pcontent = append.toString();
                    log.info("【【【【【评测】】 ES BERT begin to search from ES 【【begintime】】："+DateFormatUtil.DateFormat());
                    SearchResponse search = ESUtil.matchQuery(restHighLevelClient, pcontent, index, type);
                    SearchHits searchs = search.getHits();
                    long totalHits = searchs.totalHits;
                    log.info("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆dataprocess ES hits total size :"+totalHits);

                    SearchHit[] hs = searchs.getHits();
                    StringBuilder builder = new StringBuilder("");
                    for (SearchHit h : hs) {
                        Map<String, Object> sourceAsMap = h.getSourceAsMap();
                        String adocid = (String) sourceAsMap.get("docid");//公开号
                        String atitle = (String) sourceAsMap.get("title");
                        String aabs = (String) sourceAsMap.get("abs");
                        String aclaims = (String) sourceAsMap.get("claims");//权利声明

                        builder.append(qdocid + "\t");
                        builder.append(adocid + "\t");
                        builder.append(qtitle + "\t");
                        builder.append(atitle + "\t");
                        builder.append(qabs + "\t");
                        builder.append(aabs + "\t");
                        builder.append(qclaims + "\t");
                        builder.append(aclaims + "\n");
                    }
                    //输出文件路径： /home/suda_bert/data/all.txt
                    String path = PatentConstant.txtfilepath + PatentConstant.result_all +".txt";
                    FileUtil.writeContentAppend(path,builder.toString());

                }

               //调用模型
                callModel();
                JsonModel resjson = new JsonModel(true, "评测执行成功","200",null);
                return JSON.toJSONString(resjson);
            }

        } catch (Exception e) {
            log.error("EvaluateService evaluate method error:{}",e);
        }
        JsonModel resjson = new JsonModel(false, "评测执行失败","400",null);
        return JSON.toJSONString(resjson);
    }
    //调用模型
    public void callModel() throws Exception {
        String command = "sh /home/suda_bert/py.sh";
        log.info("【【【【【【begin suda bert model ......】】】】begintime：{}", DateFormatUtil.DateFormat());

        Process pr=Runtime.getRuntime().exec(command);
        int m = pr.waitFor();

        log.info("runtime  call python code result waitfor:"+m);
        log.info("【【【【【【END!!! suda bert model ......】】】】Endtime：{}",DateFormatUtil.DateFormat());

    }

}
