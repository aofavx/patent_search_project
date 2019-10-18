package com.blcultra.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClientUtils {
    /**
     * 连接超时时间
     */
    public static final int CONNECTION_TIMEOUT_MS = 360000;

    /**
     * 读取数据超时时间
     */
    public static final int SO_TIMEOUT_MS = 360000;

    public static final String CONTENT_TYPE_JSON_CHARSET = "application/json;charset=utf-8";

    public static final String CONTENT_TYPE_XML_CHARSET = "application/xml;charset=utf-8";


    public static final String CONTENT_TYPE_JSON_CHARSET_UTF8 = "application/json;charset=utf-8";

    public static final String CONTENT_TYPE_XML_CHARSET_UTF8 = "application/xml;charset=utf-8";


    /**
     * httpclient读取内容时使用的字符集
     */
    public static final String CHARSET_GBK = "GBK";

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final Charset UTF_8 = Charset.forName(CHARSET_UTF8);

    public static final Charset GBK = Charset.forName(CHARSET_GBK);




    public static String JsonPostInvoke(String url, Map<String, Object> params, String charsets)
            throws Exception{
        System.out.println("post方式提交json数据开始......");
        // 接收参数json列表
        JSONObject jsonParam = new JSONObject();
        // http客户端
        HttpClient httpClient = buildHttpClient(false);
        // post请求
        HttpPost method = new HttpPost(url);
        if(null != params){
            for(String key : params.keySet()){
                jsonParam.put(key, params.get(key));
            }
            // 参数实体
            StringEntity entity = new StringEntity(jsonParam.toString(), CHARSET_UTF8);//解决中文乱码问题
            entity.setContentEncoding(CHARSET_UTF8);
            entity.setContentType(CONTENT_TYPE_JSON_CHARSET_UTF8);
            method.setEntity(entity);
        }
        // 执行响应操作
        HttpResponse result = httpClient.execute(method);
        // 请求结束，返回结果
        String data = EntityUtils.toString(result.getEntity());
        System.out.println(data);
        System.out.println("post方式提交json数据结束......");
        return data;
    }
    public static String JsonGetInvoke(String url, String charsets)
            throws ClientProtocolException, IOException{
        System.out.println("get方式提交json数据开始......");
        // 接收参数json列表
        JSONObject jsonParam = new JSONObject();
        // http客户端
        HttpClient httpClient = buildHttpClient(false);
        // post请求
        HttpGet method = new HttpGet(url);

        // 执行响应操作
        HttpResponse result = httpClient.execute(method);
        // 请求结束，返回结果
        String data = EntityUtils.toString(result.getEntity());
        System.out.println(data);
        System.out.println("get方式提交json数据结束......");
        return data;
    }

    /***
     * 只传入url和参数不需要编码
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String JsonPostInvoke(String url, Map<String, Object> params)
            throws ClientProtocolException, IOException{
        System.out.println("post方式提交json数据开始......");
        // 接收参数json列表
        JSONObject jsonParam = new JSONObject();
        // http客户端
        HttpClient httpClient = buildHttpClient(false);
        // post请求
        HttpPost method = new HttpPost(url);
        if(null != params){
            for(String key : params.keySet()){
                jsonParam.put(key, params.get(key));
            }
            // 参数实体
            StringEntity entity = new StringEntity(jsonParam.toString(), CHARSET_UTF8);//解决中文乱码问题
            entity.setContentEncoding(CHARSET_UTF8);
            entity.setContentType(CONTENT_TYPE_JSON_CHARSET_UTF8);
            method.setEntity(entity);
        }
        // 执行响应操作
        HttpResponse result = null;
        String data="";
        try {
            result = httpClient.execute(method);
            data= EntityUtils.toString(result.getEntity());
        } catch (Exception e) {
            data="请求错误";
            e.printStackTrace();
        }
        // 请求结束，返回结果

//        System.out.println(data);
//        System.out.println("post方式提交json数据结束......");
        return data;
    }



    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String simpleGetInvoke(String url, Map<String, String> params)
            throws ClientProtocolException, IOException, URISyntaxException {
        return simpleGetInvoke(url, params, CHARSET_UTF8);
    }

    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String simpleGetInvoke(String url, Map<String, String> params, String charset)
            throws ClientProtocolException, IOException, URISyntaxException {
        HttpClient client = buildHttpClient(false);
        HttpGet get = buildHttpGet(url, params);
        HttpResponse response = client.execute(get);
        assertStatus(response);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String returnStr = EntityUtils.toString(entity, charset);
            return returnStr;
        }
        return null;
    }

    /**
     * 简单post调用
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String simplePostInvoke(String url, Map<String, String> params)
            throws URISyntaxException, ClientProtocolException, IOException {
        return simplePostInvoke(url, params, CHARSET_UTF8);
    }

    /**
     * 简单post调用
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String simplePostInvoke(String url, Map<String, String> params, String charset)
            throws URISyntaxException, ClientProtocolException, IOException {
        HttpClient client = buildHttpClient(false);
        HttpPost postMethod = buildHttpPost(url, params, charset);
        HttpResponse response = client.execute(postMethod);
        assertStatus(response);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String returnStr = EntityUtils.toString(entity, charset);
            return returnStr;
        }
        return null;
    }

    /**
     * 创建HttpClient
     *
     * @param isMultiThread
     * @return
     */
    public static HttpClient buildHttpClient(boolean isMultiThread) {
        CloseableHttpClient client;
        if (isMultiThread)
            client = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
        else
            client = HttpClientBuilder.create().build();
        // 设置代理服务器地址和端口
        // client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
        return client;
    }

    /**
     * 构建httpPost对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, Map<String, String> params, String charset)
            throws UnsupportedEncodingException, URISyntaxException {
        Assert.notNull(url, "构建HttpPost时,url不能为null");
        HttpPost post = new HttpPost(url);
        setCommonHttpMethod(post);
        HttpEntity he = null;
        if (params != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
            he = new UrlEncodedFormEntity(formparams, Charset.forName(charset));
            post.setEntity(he);
        }
        // 在RequestContent.process中会自动写入消息体的长度，自己不用写入，写入反而检测报错
        // setContentLength(post, he);
        return post;

    }

    /**
     * 构建httpGet对象
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpGet(String url, Map<String, String> params) throws URISyntaxException {
        Assert.notNull(url, "构建HttpGet时,url不能为null");
        HttpGet get = new HttpGet(buildGetUrl(url, params));
        return get;
    }

    /**
     * build getUrl str
     *
     * @param url
     * @param params
     * @return
     */
    private static String buildGetUrl(String url, Map<String, String> params) {
        StringBuffer uriStr = new StringBuffer(url);
        if (params != null) {
            List<NameValuePair> ps = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                ps.add(new BasicNameValuePair(key, params.get(key)));
            }
            uriStr.append("?");
            uriStr.append(URLEncodedUtils.format(ps, UTF_8));
        }
        return uriStr.toString();
    }

    /**
     * 设置HttpMethod通用配置
     *
     * @param httpMethod
     */
    public static void setCommonHttpMethod(HttpRequestBase httpMethod) {
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, CHARSET_UTF8);// setting
        // contextCoding
        // httpMethod.setHeader(HTTP.CHARSET_PARAM, CONTENT_CHARSET);
        httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON_CHARSET);
        // httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_XML_CHARSET);
    }

    /**
     * 设置成消息体的长度 setting MessageBody length
     *
     * @param httpMethod
     * @param he
     */
    public static void setContentLength(HttpRequestBase httpMethod, HttpEntity he) {
        if (he == null) {
            return;
        }
        httpMethod.setHeader(HTTP.CONTENT_LEN, String.valueOf(he.getContentLength()));
    }

    /**
     * 构建公用RequestConfig
     *
     * @return
     */
    public static RequestConfig buildRequestConfig() {
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
        return requestConfig;
    }

    /**
     * 强验证必须是200状态否则报异常
     *
     * @param res
     * @throws HttpException
     */
    static void assertStatus(HttpResponse res) throws IOException {
        Assert.notNull(res, "http响应对象为null");
        Assert.notNull(res.getStatusLine(), "http响应对象的状态为null");
        switch (res.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                // case HttpStatus.SC_CREATED:
                // case HttpStatus.SC_ACCEPTED:
                // case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
                // case HttpStatus.SC_NO_CONTENT:
                // case HttpStatus.SC_RESET_CONTENT:
                // case HttpStatus.SC_PARTIAL_CONTENT:
                // case HttpStatus.SC_MULTI_STATUS:
                break;
            default:
                throw new IOException("服务器响应状态异常,失败.");
        }
    }

    private HttpClientUtils() {
    }
    public static String getContent(String url) throws Exception{
        String backContent = null;
        //先建立一个客户端实例，将模拟一个浏览器
        HttpClient httpclient = null;
        HttpGet httpget = null;
        HttpPost httpPost=null;
        try {
            //************************************************************
            // 设置超时时间
            // 创建 HttpParams 以用来设置 HTTP 参数
            HttpParams params = new BasicHttpParams();
            // 设置连接超时和 Socket 超时，以及 Socket 缓存大小
            HttpConnectionParams.setConnectionTimeout(params, 180 * 1000);
            HttpConnectionParams.setSoTimeout(params, 180 * 1000);
            HttpConnectionParams.setSocketBufferSize(params, 8192);
            // 设置重定向，缺省为 true
            HttpClientParams.setRedirecting(params, false);
            //************************************************************
            httpclient = new DefaultHttpClient(params);
//          httpclient = new DefaultHttpClient();
            // 建立一个get方法请求，提交刷新
            httpget = new HttpGet(url);

            HttpResponse response = httpclient.execute(httpget);
            //HttpStatus.SC_OK(即:200)服务器收到并理解客户端的请求而且正常处理了
//          if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//              //对象呼叫中止
//              httpget.abort();
//              backContent = "获取不到";
//          }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //start 读取整个页面内容
                InputStream is = entity.getContent();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
                //end 读取整个页面内容
                backContent = buffer.toString();
            }
        } catch (Exception e) {
            httpget.abort();
            backContent = "有异常,获取不到";
            System.out.println("-------------异常开始");
            e.printStackTrace();
            System.out.println("-------------异常结束");
        }finally{
            //HttpClient的实例不再需要时，降低连接，管理器关闭，以确保立即释放所有系统资源
            if(httpclient != null)
                httpclient.getConnectionManager().shutdown();
        }
        //返回结果
        return backContent;
    }

    //post提交方式（request.getParameter(key)接收）
    public static String postStringInvoke(String url,Map<String,String> params){
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, CHARSET_UTF8);
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    String result = EntityUtils.toString(entity, CHARSET_UTF8);
                    System.out.println("Response content: " + result);
                    System.out.println("--------------------------------------");
                    return  result;
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //post提交方式（request.getParameter(key)接收）
    public static String postObjectInvoke(String url,Map<String,Object> params){
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        // 接收参数json列表
        JSONObject jsonParam = new JSONObject();
        if(null != params){
            for(String key : params.keySet()){
                jsonParam.put(key, params.get(key));
            }
            // 参数实体
            StringEntity entity = new StringEntity(jsonParam.toString(), CHARSET_UTF8);//解决中文乱码问题
            entity.setContentEncoding(CHARSET_UTF8);
            entity.setContentType(CONTENT_TYPE_JSON_CHARSET_UTF8);
            httppost.setEntity(entity);
        }
        try {
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    String result = EntityUtils.toString(entity, CHARSET_UTF8);
                    System.out.println("Response content: " + result);
                    System.out.println("--------------------------------------");
                    return  result;
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args)
            throws Exception {
//        long m = 5*60*1000;
//        while(m > 0){
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("appKey","bb024383bc3d4750a99ae857eee4052a");
//            map.put("appSecret","d26b7b9cb6bf4e78a795728c7528928f");
//            map.put("param","{\"text\":\"CS502772 客户非常不满我行的处理时间，客户有投诉倾向，要求尽快在个工作日内回复客户。麻烦尽快回复。发重庆\"}");
//            String result = HttpClientUtils.postStringInvoke("http://www.china-nlp.com/api/nlpapp/v2/complaint", map);
//            System.out.println("result:"+result);
//            System.out.println("--------执行次数 ："+m +"----------");
//            m--;
//            Thread.sleep(1000);
//        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("keyword","乳腺癌是什么癌症？");
        map.put("size","1");
//        String s = simpleGetInvoke("http://202.112.195.47:5001/sim/sentence_topn/", map);
        String s = simpleGetInvoke("http://jp.blcu.edu.cn/BccSDKUI/public/jubc", map);
        System.out.println(s);


    }


}
