package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.constants.PatentSearchConstant;
import com.blcultra.dao.PatentInfoMapper;
import com.blcultra.dao.PatentSearchHistoryMapper;
import com.blcultra.model.PatentInfoWithBLOBs;
import com.blcultra.service.AutoSearchService;
import com.blcultra.support.JsonModel;
import com.blcultra.support.PatentConstant;
import com.blcultra.util.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * 自动检索服务接口
 * Created by sgy05 on 2019/10/23.
 */
@Service(value = "autoSearchService")
public class AutoSearchServiceImpl implements AutoSearchService {
    private static final Logger log = LoggerFactory.getLogger(AutoSearchServiceImpl.class);

    @Autowired
    private PatentInfoMapper patentInfoMapper;
    @Autowired
    private PatentSearchHistoryMapper patentSearchHistoryMapper;

    public static final String IP = "222.28.84.124";

    public static final int PORT = 22;

    public static final String USERNAME = "root";

    public static final String PASSWORD = "123456";

    @Value(value = "${bcc.api.patent.bm25doc.api}")
    private String patent_bm25_api;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Value(value = "${spring.elasticsearch.index}")
    private String index;
    @Value(value = "${spring.elasticsearch.type}")
    private String type;
    @Value(value = "${suda_bert_out_file_path}")
    private String sudaoutfilepath;
    @Value(value = "${auto_search_type}")
    private String autosearchtype;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String autoCheck(String uid, String patentid, String checktype) {

        try {
            PatentInfoWithBLOBs patent = patentInfoMapper.selectByPrimaryKey(patentid);
            String qdocid = patent.getPid();
            String qtitle = patent.getTitle();
            String qabs = patent.getAbs();
            String qclaims = patent.getClaims();
            String category = patent.getCategory();//IPC分类号

            if (autosearchtype.equals(PatentSearchConstant.autosearch_bm25_bert)){
                //TODO:自动检索  bm25 ------> bert model

                Map<String,Object> map = new HashMap<>();
                map.put("title",qtitle);
                map.put("claim",qclaims);
                map.put("abstract",qabs);
                map.put("ipc",category);
                map.put("limit",200);

                log.info("******Auto Search  BM25 API param :{}",JSON.toJSONString(map));
                JSONObject body = restTemplate.postForEntity(patent_bm25_api, map, JSONObject.class).getBody();

                List<String> ids = (List<String>) body.get("Pids");
                SearchResponse searchResponse = ESUtil.searchByPidsAndSize(restHighLevelClient, ids, index, type, ids.size());

                List<Map<String, Object>> reslist = dataProcess(patent, searchResponse);
                Map<String,Object> resmap = new HashMap<>();
                resmap.put("data",reslist);
                resmap.put("total",reslist.size());
                JsonModel resjson = new JsonModel(true, "检索成功","200",resmap);
                return JSON.toJSONString(resjson);
            }else if (autosearchtype.equals(PatentSearchConstant.autosearch_bm25)){

                Map<String,Object> map = new HashMap<>();
                map.put("title",qtitle);
                map.put("claim",qclaims);
                map.put("abstract",qabs);
                map.put("ipc",category);

                log.info("******Auto Search  BM25 API param :{}",JSON.toJSONString(map));
                JSONObject body = restTemplate.postForEntity(patent_bm25_api, map, JSONObject.class).getBody();

                List<String> ids = (List<String>) body.get("Pids");
                SearchResponse searchResponse = ESUtil.searchByPidsAndSize(restHighLevelClient, ids, index, type, ids.size());
                SearchHits searchs = searchResponse.getHits();
                long totalHits = searchs.totalHits;

                SearchHit[] hs = searchs.getHits();
                List<Map<String,Object>> plist = new ArrayList<>();
                for (SearchHit h : hs) {
                    Map<String, Object> sourceAsMap = h.getSourceAsMap();
                    String docid = (String) sourceAsMap.get("docid");//公开号
                    String adate = (String) sourceAsMap.get("adate");//申请时间
                    String ipc = (String) sourceAsMap.get("category");//类别

                    Map<String,Object> data = new HashMap<>();
                    data.put("docnum",docid);
                    data.put("date",adate);
                    data.put("IPC",ipc);
                    data.put("tags",PatentSearchConstant.file_tags);
                    plist.add(data);
                }
                Map<String,Object> resmap = new HashMap<>();
                resmap.put("data",plist);
                resmap.put("total",plist.size());
                JsonModel resjson = new JsonModel(true, "检索成功","200",resmap);
                return JSON.toJSONString(resjson);
            }else if (autosearchtype.equals(PatentSearchConstant.autosearch_es_bert)){
                // ES--->BERT MODEL
                StringBuilder stringBuilder = new StringBuilder();
                StringBuilder append = stringBuilder.append(qtitle).append(qabs).append(qclaims);
                String pcontent = append.toString();
                log.info("autosearch ES BERT begin to search from ES 【【begintime】】："+DateFormatUtil.DateFormat());
                SearchResponse searchResponse = ESUtil.matchQuery(restHighLevelClient, pcontent, index, type);
                List<Map<String, Object>> reslist = dataProcess(patent, searchResponse);
                Map<String,Object> resmap = new HashMap<>();
                resmap.put("data",reslist);
                resmap.put("total",reslist.size());
                JsonModel resjson = new JsonModel(true, "检索成功","200",resmap);
                return JSON.toJSONString(resjson);
            }else {
                JsonModel resjson = new JsonModel(false, "暂无此种检索方式","400",null);
                return JSON.toJSONString(resjson);
            }
        } catch (Exception e) {
            log.error("AutoSearchServiceImpl  autoSearch method error :{}",e);
        }

        JsonModel resjson = new JsonModel(false, "检索失败","400",null);
        return JSON.toJSONString(resjson);
    }
    private List<Map<String,Object>> dataProcess(PatentInfoWithBLOBs patent , SearchResponse searchResponse) throws Exception {
        String qdocid = patent.getDocid();
        String qtitle = patent.getTitle();
        String qabs = patent.getAbs();
        String qclaims = patent.getClaims();
        String category = patent.getCategory();//IPC分类号

        SearchHits searchs = searchResponse.getHits();
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
        String command = "sh /home/suda_bert/py.sh";
        log.info("【【【【【【begin suda bert model ......】】】】begintime：{}",DateFormatUtil.DateFormat());

        Process pr=Runtime.getRuntime().exec(command);
        int m = pr.waitFor();

        log.info("runtime  call python code result waitfor:"+m);
        log.info("【【【【【【END!!! suda bert model ......】】】】Endtime：{}",DateFormatUtil.DateFormat());

        List<String> list = FileUtil.ReadFile(sudaoutfilepath, "UTF-8");
        List<String> plist = new ArrayList<>();
        for (int i = 1; i <list.size() ; i++) {
            String s = list.get(i);
            String[] split = s.split("\t");
            String pid = split[3];//获取检索结果的答案专利docid
            plist.add(pid);
        }
//        log.info("☆☆☆☆☆苏大模型输出的结果文件中，答案的docid列表:{}",plist.toString());
        List<Map<String, Object>> reslist = doSearch(plist);
        return reslist;

    }
    private List<Map<String,Object>> doSearch(List<String> pids) throws IOException {
        Set<String> set = new HashSet<>(pids);
        int size = pids.size();
        log.info("search comparision method term2doc result pid totalnum:"+size);
        SearchResponse search =  ESUtil.searchByPidsAndSize(restHighLevelClient,new ArrayList<>(set), index, type,size);
        SearchHits hits = search.getHits();
        long total = hits.totalHits;
        log.info("searchComparisonPatent method search ES result total:"+total);
        SearchHit[] searchHits = hits.getHits();

        List<Map<String,Object>> plist = new ArrayList<>();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();

            String docid = (String) sourceAsMap.get("docid");//公开号
//                    String appid = (String) sourceAsMap.get("appid");//申请号
            String adate = (String) sourceAsMap.get("adate");//申请时间
            String ipc = (String) sourceAsMap.get("category");//类别

            Map<String,Object> data = new HashMap<>();
            data.put("docnum",docid);
            data.put("date",adate);
            data.put("IPC",ipc);
            data.put("tags",PatentSearchConstant.file_tags);
            plist.add(data);
        }
        return plist;
    }
}
