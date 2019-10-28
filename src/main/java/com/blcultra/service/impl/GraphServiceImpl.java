package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.constants.PatentSearchConstant;
import com.blcultra.service.GraphService;
import com.blcultra.support.JsonModel;
import com.blcultra.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/10/23.
 */
@Service(value = "graphService")
public class GraphServiceImpl implements GraphService {

    private static final Logger log = LoggerFactory.getLogger(GraphServiceImpl.class);

    @Value(value = "${bcc.api.patent.graph.api}")
    private String patent_graph_api;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public String graphSearch(String uid, String expression,String searchword) {

        log.info("【【GraphServiceImpl  graphSearch param】】 , uid:{}, expression:{},searchword:{}",uid,expression,searchword);

        try {
            if (StringUtils.isEmpty(expression)){
                JsonModel jm = new JsonModel(false,"参数校验错误","400",null);
                return JSON.toJSONString(jm);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("query",expression);
            jsonObject.put("limit", PatentSearchConstant.graphsearchsize);
            JSONObject body = restTemplate.postForEntity(patent_graph_api, jsonObject, JSONObject.class).getBody();
            List<String> freq = (List<String>) body.get("Freq");
            if (CollectionUtils.isEmpty(freq)){
                JsonModel jm = new JsonModel(true,"暂无数据","200",null);
                return JSON.toJSONString(jm);
            }
            Map<String, Object> map = packageInfo(searchword, freq);
            JsonModel jm = new JsonModel(true,"知识图谱数据检索成功","200",map);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            log.error("GraphServiceImpl graphSearch error :{}",e);
        }
        JsonModel jm = new JsonModel(false,"知识图谱数据检索失败","400",null);
        return JSON.toJSONString(jm);
    }

    private Map<String,Object> packageInfo(String searchword,List<String> freq){

        Map<String,Object> coremap = new HashMap<>();
        coremap.put("name",searchword);
        String uuid = StringUtil.getUUID();
        coremap.put("uuid",uuid);
        List<Map<String,Object>> nodelist = new ArrayList<>();
        nodelist.add(coremap);
        List<Map<String,Object>> relationlist = new ArrayList<>();
        // 'freq': ['词1\t词频1', '词2\t词频2', '词3\t词频3']
        for (String s : freq) {
            String[] split = s.split("\t");
            Map<String,Object> wordmap = new HashMap<>();
            String targetid = StringUtil.getUUID();
            wordmap.put("name",split[0]);
            wordmap.put("uuid",targetid);
            nodelist.add(wordmap);

            Map<String,Object> rmap = new HashMap<>();
            rmap.put("sourceid",uuid);
            rmap.put("targetid",targetid);
            rmap.put("freq",split[1]);
            rmap.put("uuid", StringUtil.getUUID());
            relationlist.add(rmap);
        }
        Map<String,Object> resmap = new HashMap<>();
        resmap.put("node",nodelist);
        resmap.put("relationship",relationlist);
        return  resmap;

    }
}
