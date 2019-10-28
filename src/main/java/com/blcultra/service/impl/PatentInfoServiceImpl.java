package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.constants.PatentSearchConstant;
import com.blcultra.dao.*;
import com.blcultra.dto.SearchDto;
import com.blcultra.model.*;
import com.blcultra.service.PatentInfoService;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.util.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 专利信息服务接口
 * Created by sgy05 on 2019/8/5.
 */
@Service(value = "patentInfoService")
public class PatentInfoServiceImpl implements PatentInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value(value = "${spring.elasticsearch.index}")
    private String index;
    @Value(value = "${spring.elasticsearch.type}")
    private String type;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Value(value = "${bcc.api.patent.extractor.api}")
    private String patent_extractor_api;
    @Value(value = "${bcc.api.patent.term2doc.api}")
    private String patent_term2doc_api;
    @Value(value = "${bcc.api.patent.buer.api}")
    private String patent_buer_api;
    @Value(value = "${bcc.api.patent.jiegou.api}")
    private String patent_jiegou_api;

    @Autowired
    private PatentInfoMapper patentInfoMapper;
    @Autowired
    private PatentSearchHistoryMapper patentSearchHistoryMapper;

    @Autowired
    private PatentCoreWordMapper patentCoreWordMapper;
    @Autowired
    private PatentTermWordMapper patentTermWordMapper;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 首页加载数据
     * @param uid
     * @param pageNow
     * @param pageSize
     * @return
     */
    @Override
    public String index(String uid, Integer pageNow, Integer pageSize) {

        try {
            Map<String,Object> map = new HashMap<>();
            map.put("uploader",uid);
            map.put("pstate","1");

            Callable<Integer> checkedCountCallable = ()-> patentInfoMapper.count(map);

            Map<String,Object> pmap = new HashMap<>();
            pmap.put("uploader",uid);
            pmap.put("pstate","0");
            Callable<Integer> uncheckedCountCallable = ()-> patentInfoMapper.count(pmap);

            Page page = new Page();
            page.setPageSize(pageSize);
            page.setPageNow(pageNow);

            Map<String,Object> allparam = new HashMap<>();
            allparam.put("uploader",uid);
            allparam.put("queryStart",page.getQueryStart());
            allparam.put("pageSize",page.getPageSize());

            Callable<List> allCallable = ()-> patentInfoMapper.getPList(allparam);

            Map<String,Object> param = new HashMap<>();
            param.put("uploader",uid);
            param.put("queryStart",page.getQueryStart());
            param.put("pageSize",page.getPageSize());
            param.put("pstate","1");
            Callable<List> checkedCallable = ()-> patentInfoMapper.getPList(param);


            Map<String,Object> pparam = new HashMap<>();
            pparam.put("uploader",uid);
            pparam.put("queryStart",page.getQueryStart());
            pparam.put("pageSize",page.getPageSize());
            pparam.put("pstate","0");

            Callable<List> unCheckedCallable = ()-> patentInfoMapper.getPList(pparam);


            FutureTask<Integer> checkedCountTask= new FutureTask<Integer>(checkedCountCallable);
            FutureTask<Integer> uncheckedCountCountTask= new FutureTask<Integer>(uncheckedCountCallable);

            FutureTask<List> allTask = new FutureTask<List>(allCallable);
            FutureTask<List> checkedTask = new FutureTask<List>(checkedCallable);
            FutureTask<List> unCheckedTask = new FutureTask<List>(unCheckedCallable);

            executorService.submit(checkedCountTask);
            executorService.submit(uncheckedCountCountTask);

            executorService.submit(allTask);
            executorService.submit(checkedTask);
            executorService.submit(unCheckedTask);

            Map<String, Object> data = new HashMap<>();


            int checkcount = checkedCountTask.get();
            int uncheckcount = uncheckedCountCountTask.get();
            int total = checkcount+uncheckcount;

            data.put("totalpatentcount",total);
            data.put("checkedpatentcount", checkedCountTask.get());
            data.put("uncheckedpatentcount", uncheckedCountCountTask.get());

            page.setResultList(allTask.get());
            page.setTotal(total);
            data.put("allpatents", JSONObject.toJSON(page));

            page.setResultList(checkedTask.get());
            page.setTotal(checkcount);
            data.put("checkedpatents", JSONObject.toJSON(page));


            page.setResultList(unCheckedTask.get());
            page.setTotal(uncheckcount);
            data.put("uncheckedpatents", JSONObject.toJSON(page));

            JsonModel jm = new JsonModel(true,"首页数据加载成功","200",data);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            logger.error("patent index service error  ====> ",e);

        }
        JsonModel jm = new JsonModel(false,"首页数据加载失败","400",null);
        return JSON.toJSONString(jm);
    }

    /**
     * 查询审查专利列表
     * @param pageNow
     * @param pageSize
     * @return
     */
    @Override
    public String listPatents(String uid, String pstate,Integer pageNow,Integer pageSize) {


        Page page = new Page();
        page.setPageSize(pageSize);
        page.setPageNow(pageNow);
        Map<String,Object> map = new HashMap<>();
        map.put("queryStart",page.getQueryStart());
        map.put("pageSize",page.getPageSize());
        map.put("uploader",uid);
        if (!StringUtils.isEmpty(pstate) && !Objects.equals("2",pstate)){
            map.put("pstate",pstate);
        }

        List<Map<String, Object>> patentList = patentInfoMapper.getPatentList(map);

        //计算总条数
        Map<String,Object> param = new HashMap<>();
        param.put("uploader",uid);
        int total = patentInfoMapper.count(param);
        page.setTotal(total);

        page.setResultList(patentList);
        JsonModel jm = new JsonModel(true,"查询专利审查列表成功","200",page);
        return JSON.toJSONString(jm);
    }

    /**
     * 根据pid删除专利信息
     * @param pids
     * @return
     */
    @Override
    public String delete(String pids,String uid) {

        if (StringUtils.isEmpty(pids) ){
            JsonModel jm = new JsonModel(false,"请选择要删除的专利","400",null);
            return JSON.toJSONString(jm);
        }
        if (StringUtils.isEmpty(uid)){
            JsonModel jm = new JsonModel(false,"参数错误","400",null);
            return JSON.toJSONString(jm);
        }
        String[] ids = pids.split(",");
        List<String> ps = Arrays.asList(ids);
        Map<String,Object> param = new HashMap<>();
        param.put("uploader",uid);
        param.put("list",ps);
        int m = patentInfoMapper.batchUpdate(param);
        JsonModel jm = new JsonModel(true,"删除专利成功","200",null);
        return JSON.toJSONString(jm);
    }

    /**
     * 查询专利审核和未审核的数量
     * @return
     */
    @Override
    public String countPatent() {

        int m = patentInfoMapper.countChecked();
        int n = patentInfoMapper.countUnchecked();

        Map<String,Object> resmap = new HashMap<>();
        resmap.put("checked",m);//已审
        resmap.put("unchecked",n);//未审核
        resmap.put("all",m+n);
        JsonModel json = new JsonModel(true, "查询成功","200",resmap);
        return JSON.toJSONString(json);
    }

    /**
     * 审查服务接口
     * @param patentid
     * @param checktype  1:一站式检索（人机交互）  2：自动检索
     * @return
     */
    @Override
    public String check(String uid,String patentid,String checktype) {

        if (null == patentid || "".equals(patentid)){
            JsonModel jm = new JsonModel(false,"请选择要审查的专利","400",null);
            return JSON.toJSONString(jm);
        }
        try {
            PatentInfoWithBLOBs patentInfo = patentInfoMapper.selectByPrimaryKey(patentid);
            String pid = patentInfo.getPid();
            String pname = patentInfo.getTitle();
            String abs = patentInfo.getAbs();
            String claims = patentInfo.getClaims();
            String category = patentInfo.getCategory();//IPC分类号


            Map<String,String> param = new HashMap<>();
            param.put("abstract",abs);
            param.put("title",pname);
            param.put("claim",claims);
            param.put("ipc",category);

//            param.put("abstract","本发明提供了多语言站点开发系统及其实现方法，其中，多语言站点开发系统，包括：多语言站点开发的数据系统，用于提供进行多语言站点开发的数据；多语言站点开发的程序系统，用于程序员提供多语言站点开发的程序的源代码及对多语言站点开发的数据库模块中的数据进行数据更新的服务；多语言站点开发的翻译系统，用于翻译人员通过多语言站点开发的数据库模块中的语言进行对应翻译的操作及相应更新缓存数据集中的缓存数据的操作；本发明可以解决现有多语言站点开发的实现方法中的调用困难的缺陷。");
//            param.put("title","多语言站点开发系统及其实现方法");
//            param.put("claim","1.多语言站点开发系统，其特征在于，包括：       多语言站点开发的数据系统，用于提供进行多语言站点开发的数据；       多语言站点开发的程序系统，用于程序员提供多语言站点开发的程序的源代码及对多语言站点开发的数据库模块中的数据进行数据更新的服务；       多语言站点开发的翻译系统，用于翻译人员通过多语言站点开发的数据库模块中的语言进行对应翻译的操作及相应更新缓存数据集中的缓存数据的操作。            2.根据权利要求1所述的多语言站点开发系统，其特征在于，所述多语言站点开发的数据系统，包括：       多语言站点开发的数据库模块，用于提供多语言的原代码数据及多种语言的源代码数据的匹配数据；       多语言站点开发的缓存数据集模块，用于提供多语言站点的缓存数据集中的缓存数据。");
//            param.put("ipc","G06F9/44");
            //调用提取特征词服务
            String j = JSON.toJSONString(param);//map转String
            logger.info("参数："+j);
            JSONObject jsonObject = JSON.parseObject(j);//String转json

            List<PatentCoreWord> patentCoreWords = patentCoreWordMapper.selectByPid(patentid);
            List<PatentTermWord> patentTermWords = patentTermWordMapper.selectTermsByPid(patentid);

//            Map<String, Object> pfmap = patentFeatureWordMapper.selectByPid(patentid);
            List<PatentCoreWord> corelist = new ArrayList<>();
            List<PatentTermWord> termlist = new ArrayList<>();
            String fid = "";
            List<Map<String,Object>> terms = new ArrayList<>();
            List<Map<String,Object>> cores = new ArrayList<>();
            //判断是否提取过特征词，若提取过直接使用，若未提取过，则需调用提取特征词服务API
            if (null != patentCoreWords && patentCoreWords.size()>0){
                corelist = patentCoreWords;
                for (PatentCoreWord patentCoreWord : corelist) {
                    String cid = patentCoreWord.getCid();
                    String coreword = patentCoreWord.getCoreword();
                    String cgrade = patentCoreWord.getCgrade();
                    Map<String,Object> c = new HashMap<>();
                    c.put("id",cid);
                    c.put("word",coreword);
                    c.put("cgrade",cgrade);
                    cores.add(c);
                }

                termlist= patentTermWords;
                //前端要求需要把核心词和特征词字段设置成一样的
                for (PatentTermWord patentTermWord : termlist) {
                    String tid = patentTermWord.getTid();
                    String term = patentTermWord.getTerm();
                    Map<String,Object> m = new HashMap<>();
                    m.put("id",tid);
                    m.put("word",term);
                    terms.add(m);

                }


            }else {
                RestTemplate restTemplate = new RestTemplate();
                logger.info("☆☆☆☆☆☆☆☆patent extractor api param :"+jsonObject);
                Map<String,Object> map = restTemplate.postForEntity(patent_extractor_api, jsonObject, Map.class).getBody();
                logger.info("http post result extractor :{}"+map);
                List<String> corewords = (List<String>) map.get("Corewords");
                List<String> fwords = (List<String>) map.get("Featurewords");
                String status = String.valueOf(map.get("Status"));
                if (!status.equals("500")){
                    for (String coreword : corewords) {
                        String[] carr = coreword.split("\t");
                        String cgrade ="";
                        if (carr.length>=2){
                            cgrade = carr[1];
                        }
                        String cword = carr[0];
                        PatentCoreWord patentCoreWord = new PatentCoreWord();
                        patentCoreWord.setCgrade(cgrade);
                        patentCoreWord.setCoreword(cword);
                        patentCoreWord.setPid(patentid);
                        String id = StringUtil.getUUID();
                        patentCoreWord.setCid(id);
                        patentCoreWord.setUid(uid);
                        corelist.add(patentCoreWord);
                        Map<String,Object> c = new HashMap<>();
                        c.put("id",id);
                        c.put("word",cword);
                        c.put("cgrade",cgrade);
                        cores.add(c);
                    }
                    //批量插入核心词
                    patentCoreWordMapper.batchInsert(corelist);

                    for (String fword : fwords) {
                        String[] farr = fword.split("\t");
                        String tgrade ="";
                        if (farr.length>=2){
                            tgrade = farr[1];
                        }
                        String tword = farr[0];
                        PatentTermWord patentTermWord = new PatentTermWord();
                        patentTermWord.setPid(patentid);
                        String id = StringUtil.getUUID();
                        patentTermWord.setTid(id);
                        patentTermWord.setTerm(tword);
                        patentTermWord.setTgrade(tgrade);
                        patentTermWord.setUid(uid);
                        termlist.add(patentTermWord);

                        Map<String,Object> m = new HashMap<>();
                        m.put("id",id);
                        m.put("word",tword);
                        terms.add(m);

                    }
                    patentTermWordMapper.batchInsertTerms(termlist);
                }
            }

            //剪切走了

            List<Map<String, Object>> list = sortList(cores);
            Map<String, Object> res = new HashMap<>();
            res.put("corewords",list);
            res.put("featurewords",terms);
            res.put("fid",fid);
            JsonModel json = new JsonModel(true, "提取特征词成功","200",res);
            return JSON.toJSONString(json);

        } catch (Exception e) {
            logger.error("PatentInfoServiceImpl  check method error :",e);
        }
        JsonModel json = new JsonModel(false, "审查操作失败","400",null);
        return JSON.toJSONString(json);
    }
    public List<Map<String,Object>> sortList(List<Map<String,Object>> sortlist){
        //开始排序
        Collections.sort(sortlist, new Comparator<Map<String,Object>>(){
            public int compare(Map<String,Object> o1, Map<String,Object> o2) {
                //排序属性
                if(Double.valueOf(String.valueOf(o1.get("cgrade"))) < Double.valueOf(String.valueOf(o2.get("cgrade")))){
                    return 1;
                }
                if(Double.valueOf(String.valueOf(o1.get("cgrade"))) == Double.valueOf(String.valueOf(o2.get("cgrade")))){
                    return 0;
                }
                return -1;
            }
        });
        return sortlist;
    }
    public  Map<String,Object> sortToReuslt(List<String> corewords,List<String> fwords){
        List<String> clist = new ArrayList<>();
        List<String> flist = new ArrayList<>();
        List<Map<String,Object>> sortlist = new ArrayList<>();

        for (String coreword : corewords) {
            String[] split = coreword.split("\t");
            clist.add(split[0]);
            Map<String,Object> m = new HashMap<>();
            m.put("word",split[0]);
            m.put("rate",split[1]);
            sortlist.add(m);
        }
        for (String fword : fwords) {
            String[] split = fword.split("\t");
            flist.add(split[0]);
        }
        //开始排序
        Collections.sort(sortlist, new Comparator<Map<String,Object>>(){
            public int compare(Map<String,Object> o1, Map<String,Object> o2) {
                //排序属性
                if(Double.valueOf(String.valueOf(o1.get("rate"))) < Double.valueOf(String.valueOf(o2.get("rate")))){
                    return 1;
                }
                if(Double.valueOf(String.valueOf(o1.get("rate"))) == Double.valueOf(String.valueOf(o2.get("rate")))){
                    return 0;
                }
                return -1;
            }
        });
        Map<String,Object> resmap = new HashMap<>();
        resmap.put("corewords",clist);
        resmap.put("featurewords",flist);
        resmap.put("corewordorder",sortlist);
        return resmap;

    }

    /**
     * 用户点击【检索对比文献按钮】调用此接口，查询相似专利文本
     * @param searchDto  此参数体中包含：待审核的专利id，用户选中的检索关键词（核心词、特征词）
     * @return
     */
    @Override
    public String searchComparisonPatent(String uid ,SearchDto searchDto) {

        try {
            String searchtype = searchDto.getSearchtype();
            if (StringUtils.isEmpty(searchtype)){
                JsonModel jm = new JsonModel(false,"参数校验错误","400",null);
                return JSON.toJSONString(jm);
            }
            Map<String,Object> wordparam = new HashMap<>();
            Map<String,Object> resultmap = new HashMap<>();
            //基础检索逻辑
            if (searchtype.equals(PatentSearchConstant.basesearchtype)){
                Map<String,Object> searchparam = new HashMap<>();
                logger.info("*************searchComparisonPatent param :"+JSON.toJSONString(searchDto));

                wordparam.put("corewords",searchDto.getCorewords());
                wordparam.put("featurewords",searchDto.getFeaturewords());

                searchparam.put("term",wordparam);
                searchparam.put("ipc",searchDto.getCategory());
                RestTemplate restTemplate = new RestTemplate();
                logger.info("☆☆☆☆☆☆☆☆【searchComparisonPatent】 patent  【term2doc】   api param :"+searchparam);
                JSONObject json = restTemplate.postForEntity(patent_term2doc_api, searchparam, JSONObject.class).getBody();
                List<String> pids = (List<String>) json.get("Pids");

                if (null == pids){
                    JsonModel resjson = new JsonModel(true, "暂无检索结果信息","200",null);
                    return JSON.toJSONString(resjson);
                }
                //检索信息
                List<Map<String, Object>> plist = doSearch(pids);
                //提取特征词后--->检索对比文献--->封装检索结果列表返回页面

                resultmap.put("total",plist.size());
                resultmap.put("data",plist);

            }else if (searchtype.equals(PatentSearchConstant.boolsearchtype)){
                //布尔检索
                String expression = searchDto.getExpression();//检索表达式
                List<String> ipcs = searchDto.getIpcs();//用户选择的IPC分类列表
                List<String> localtions = searchDto.getLocaltions();//检索位置

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isFirst",searchDto.getIsFirst().equals("1")?true:false);
                if (searchDto.getIsFirst().equals("0")){
                    Map<String,Object> map = new HashMap<>();
                    String wrod = searchDto.getSecondsearchword();
                    String[] split = wrod.split(",");
                    List<String> list = Arrays.asList(split);
                    map.put("type",Integer.parseInt(searchDto.getSecondsearchpattern()));
                    map.put("word",list);
                    jsonObject.put("setbase",map);
                }

                jsonObject.put("condition",localtions);
                jsonObject.put("ipc",ipcs);

                Map<String,String> param = new HashMap<>();
                param.put("buer",expression);

                jsonObject.put("param",param);
                jsonObject.put("limit",200);

                JSONObject body = restTemplate.postForEntity(patent_buer_api, jsonObject, JSONObject.class).getBody();
                List<String> pids = (List<String>) body.get("Pids");
                if (CollectionUtils.isEmpty(pids)){
                    JsonModel resjson = new JsonModel(true, "暂无数据","200",null);
                    return JSON.toJSONString(resjson);
                }
                List<Map<String, Object>> plist = doSearch(pids);

                resultmap.put("total",plist.size());
                resultmap.put("data",plist);
                //结构检索
            }else if (searchtype.equals(PatentSearchConstant.structsearchtype)){
                String expression = searchDto.getExpression();//检索表达式
                List<String> ipcs = searchDto.getIpcs();//用户选择的IPC分类列表
                List<String> localtions = searchDto.getLocaltions();//检索位置

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isFirst",searchDto.getIsFirst().equals("1")?true:false);
                if (searchDto.getIsFirst().equals("0")){
                    Map<String,Object> map = new HashMap<>();
                    String wrod = searchDto.getSecondsearchword();
                    String[] split = wrod.split(",");
                    List<String> list = Arrays.asList(split);
                    map.put("type",Integer.parseInt(searchDto.getSecondsearchpattern()));
                    map.put("word",list);
                    jsonObject.put("setbase",map);
                }

                jsonObject.put("condition",localtions);
                jsonObject.put("ipc",ipcs);

                Map<String,String> param = new HashMap<>();
                param.put("jiegou",expression);

                jsonObject.put("param",param);
                jsonObject.put("limit",50);

                logger.info("***********patent jiegou api param :"+JSON.toJSONString(jsonObject));
                String body = restTemplate.postForEntity(patent_jiegou_api, jsonObject, String.class).getBody();
                logger.info("##################body:"+body);
                JSONObject json = JSON.parseObject(body);
                //
                List<Map<String,Object>> datas = (List<Map<String, Object>>) json.get("Pids");
                if (CollectionUtils.isEmpty(datas)){
                    JsonModel resjson = new JsonModel(true, "暂无数据","200",null);
                    return JSON.toJSONString(resjson);
                }
                //检索ES获取专利信息列表
                List<Map<String, Object>> plist = searchPinfoFromES(datas);
                resultmap.put("total",plist.size());
                resultmap.put("data",plist);
            }

            //记录检索记录，保存至检索历史记录表中
            String pid = String.valueOf(searchDto.getPid());//待审核的专利id
            PatentSearchHistory patentSearchHistory = new PatentSearchHistory();
            patentSearchHistory.setPhisid(StringUtil.getUUID());
            patentSearchHistory.setPid(pid);
            patentSearchHistory.setSearcher(uid);
            patentSearchHistory.setSearchparam(JSON.toJSONString(wordparam));

            String time = DateFormatUtil.DateFormat();
            patentSearchHistory.setSearchtime(time);
            patentSearchHistory.setUpdatetime(time);

            int m = patentSearchHistoryMapper.insertSelective(patentSearchHistory);

            JsonModel resjson = new JsonModel(true, "检索成功","200",resultmap);
            return JSON.toJSONString(resjson);
        } catch (Exception e) {
            logger.error("searchComparisonPatent method error :",e);
        }
        JsonModel jm = new JsonModel(false,"检索对比文献失败","400",null);
        return JSON.toJSONString(jm);
    }
    private  List<Map<String,Object>> searchPinfoFromES(List<Map<String,Object>> datas) throws IOException {
        List<Map<String,Object>> plist = new ArrayList<>();
        for (Map<String, Object> data : datas) {

            String pid = String.valueOf(data.get("Pid"));
            List<String> sents = (List<String>) data.get("Sents");

            SearchResponse searchResponse = ESUtil.searchByPid(restHighLevelClient, pid, index, type);

            SearchHits hits = searchResponse.getHits();
            long total = hits.totalHits;
            logger.info("searchComparisonPatent method search ES result total:"+total);
            SearchHit[] searchHits = hits.getHits();

            SearchHit searchHit = searchHits[0];
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();

            String docid = (String) sourceAsMap.get("docid");//公开号
            String adate = (String) sourceAsMap.get("adate");//申请时间
            String ipc = (String) sourceAsMap.get("category");//类别

            Map<String,Object> rmap = new HashMap<>();
            rmap.put("docnum",docid);
            rmap.put("date",adate);
            rmap.put("IPC",ipc);
            rmap.put("sents",sents);//结构检索命中的符合表达式句式的句子
            rmap.put("tags",PatentSearchConstant.file_tags);
            plist.add(rmap);

        }
        return plist;
    }
    private List<Map<String,Object>> doSearch(List<String> pids) throws IOException {
        Set<String> set = new HashSet<>(pids);
        int size = pids.size();
        logger.info("search comparision method term2doc result pid totalnum:"+size);
        SearchResponse search =  ESUtil.searchByPidsAndSize(restHighLevelClient,new ArrayList<>(set), index, type,size);
        SearchHits hits = search.getHits();
        long total = hits.totalHits;
        logger.info("searchComparisonPatent method search ES result total:"+total);
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


    /**
     * 首页---专利申请列表  搜索框中输入检索关键词对专利列表进行模糊搜索【需求：按申请号、时间、申请人 ，进行检索】
     * @param keyword
     * @return
     */
    @Override
    public String likeSearchPatents(String uid, String pstate, String keyword,Integer pageNow,Integer pageSize) {

        try {
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setPageNow(pageNow);
            Map<String,Object> map = new HashMap<>();
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());
            map.put("uploader",uid);
            if (!StringUtils.isEmpty(pstate) && !Objects.equals("2",pstate)){
                map.put("pstate",pstate);
            }
            map.put("keyword",keyword);
            List<Map<String, Object>> list = patentInfoMapper.likeSearchPatents(map);

            page.setResultList(list);

            JsonModel json = new JsonModel(true, "查询成功","200",page);
            return JSON.toJSONString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel json = new JsonModel(false, "查询失败","400",null);
        return JSON.toJSONString(json);
    }

    /**
     * 浏览要审核的专利正文----【点击特征词提取页面中的“浏览”按钮，触发此接口查询该专利的正文信息】
     * @param pid
     * @return
     */
    @Override
    public String browsePatent(String uid ,String pid) {

        try {
            if (StringUtils.isEmpty(pid)){
                JsonModel json = new JsonModel(false, "参数错误","400",null);
                return JSON.toJSONString(json);
            }
            Map<String,Object> param = new HashMap<>();
            param.put("pid",pid);
            param.put("uploader",uid);

            Map<String, Object> browse = patentInfoMapper.browse(pid);
            JsonModel json = new JsonModel(true, "查询成功","200",browse);
            return JSON.toJSONString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel json = new JsonModel(false, "查询失败","400",null);
        return JSON.toJSONString(json);
    }

    /**
     * 查看原文   在检索结果页面中点击全部对比文献列表中的【查看】、收藏列表中的【原文】按钮调用此接口
     * @param uid
     * @param docnum
     * @return
     */
    @Override
    public String viewOriginalPatent(String uid, String docnum) {


//        Map<String,Object> res = new HashMap<>();
//        res.put("pdetail","具有内部滗析、过滤、混合和充气单元的饮料杯  " +
//                "一种用于对饮料进行滗析、过滤、混合和/或充气的饮用容器。饮用容器包括碗和充气单元，碗具有上部开口，充气单元具有壁，沿着单元顶部的上部开口和穿过壁的多个开口或穿孔。杯形单元具有可选地带有中心的向上延伸的突起部的面向上的中空或凹进构造，并附连到碗且处于碗内，以便对饮料进行滗析、过滤、混合和/或充气，饮料通过碗上部开口斟入单元上部开口内，并随后通过至少一个穿孔流入碗内。优选地，饮用容器是具有基部和支承碗的柄部的酒杯并且饮料是酒。" +
//                "技术领域" +
//                "本发明涉及个人食用分量的(individual serving-sized)饮料容器，特别是葡萄酒和马提尼酒的酒杯，其含有当液体饮料斟入杯中时用于对其进行滗析、过滤、混合和/或充气的内部单元。");

        try {
            List<String> pids = new ArrayList<>();
            pids.add(docnum);
            SearchResponse searchResponse = ESUtil.searchByPids(restHighLevelClient, pids, index, type);
            SearchHits hits = searchResponse.getHits();
            long total = hits.totalHits;
            SearchHit[] searchHits = hits.getHits();

            List<Map<String,Object>> plist = new ArrayList<>();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();

                String title = (String) sourceAsMap.get("title");//专利名
                String abs = (String) sourceAsMap.get("abs");//摘要
                String claim = (String) sourceAsMap.get("claims");//声明
                String desc = (String) sourceAsMap.get("desc");//声明


                Map<String,Object> data = new HashMap<>();
                data.put("title",title);
                data.put("abs",abs);
                data.put("claim",claim);
                plist.add(data);
            }

            JsonModel json = new JsonModel(true, "查询成功","200",plist.get(0));
            return JSON.toJSONString(json);

        } catch (IOException e) {
            e.printStackTrace();
        }


        JsonModel json = new JsonModel(true, "查询失败","400",null);
        return JSON.toJSONString(json);
    }


}
