package com.blcultra.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/10/14.
 */
public class ESUtil {


    public static SearchResponse searchByPid(RestHighLevelClient restHighLevelClient,String pid, String index, String type) throws IOException {

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("docid",pid));
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return search;
    }
    //根据公开号查询专利信息
    public static SearchResponse searchByPids(RestHighLevelClient restHighLevelClient,List<String> pids, String index, String type) throws IOException {

        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索类型
        searchRequest.types(type);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termsQuery根据多个id 查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("docid",pids));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();

        return search;
    }

    public static SearchResponse searchByPidsAndSize(RestHighLevelClient restHighLevelClient,List<String> pids, String index, String type,int size) throws IOException {

        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索类型
        searchRequest.types(type);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termsQuery根据多个id 查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("docid",pids));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(size);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();

        return search;


    }
    public static SearchResponse  matchQuery(RestHighLevelClient restHighLevelClient,String pcontent,String index,String type) throws IOException {
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索类型
        searchRequest.types(type);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);
        String[] excludes = Strings.EMPTY_ARRAY;
        //设置返回结果中包含哪些字段，并且按评分倒序排列返回
        searchSourceBuilder.fetchSource(new String[]{"title","abs","claims"},excludes).
                sort(new ScoreSortBuilder().order(SortOrder.DESC));
        //minimumShouldMatch:匹配的分词最小数量
        MultiMatchQueryBuilder matchQueryBuilder =
                QueryBuilders.multiMatchQuery(pcontent,"title","abs","claims","description").minimumShouldMatch("70%");
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return search;
    }


    public static void main(String[] args) throws Exception {

//        Map<String,String> param = new HashMap<>();
//        param.put("title","多语言站点开发系统及其实现方法");
//        param.put("claim","1.多语言站点开发系统，其特征在于，包括：       多语言站点开发的数据系统，用于提供进行多语言站点开发的数据；       多语言站点开发的程序系统，用于程序员提供多语言站点开发的程序的源代码及对多语言站点开发的数据库模块中的数据进行数据更新的服务。");
//        param.put("abstract","本发明提供了多语言站点开发系统及其实现方法，其中，多语言站点开发系统，包括：多语言站点开发的数据系统，用于提供进行多语言站点开发的数据；多语言站点开发的程序系统，用于程序员提供多语言站点开发的程序的源代码及对多语言站点开发的数据库模块中的数据进行数据更新的服务；多语言站点开发的翻译系统，用于翻译人员通过多语言站点开发的数据库模块中的语言进行对应翻译的操作及相应更新缓存数据集中的缓存数据的操作；本发明可以解决现有多语言站点开发的实现方法中的调用困难的缺陷。");
//        param.put("ipc","G06F9/44");
//        String json = JSON.toJSONString(param);//map转String
//        JSONObject jsonObject = JSON.parseObject(json);//String转json
//        // 要调用的接口方法
//        RestTemplate client = new RestTemplate();
//        JSONObject r = client.postForEntity("http://202.112.195.26:8086/patent/extractor", jsonObject, JSONObject.class).getBody();
//
//        System.out.println(r);

    }


}
