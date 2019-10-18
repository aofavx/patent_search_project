package com.blcultra.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * Http client util class
 * @author sgy
 */
public final class HttpClientUtil {
    private static Log logger = LogFactory.getLog(HttpClientUtil.class);
    private static volatile HttpClient httpClient = null;

    /**
     * Constructor
     */
    private HttpClientUtil() {
    }

    /**
     * Download data
     * 
     * @param url url
     * @return bytes data
     */
    public static byte[] downloadData(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = getHttpClient().execute(httpGet);
            return responseToByte(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * Send get to URL.
     * 
     * @param url url
     * @return result content
     */
    public static String sendGet(String url) {
        return sendGet(url, "UTF-8");
    }

    /**
     * Send get to URL.
     * 
     * @param url url
     * @param charset charset
     * @return result content
     */
    public static String sendGet(String url, String charset) {
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = getHttpClient().execute(httpGet);
            return responseToString(response, charset);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        } finally {
            httpGet.abort();
        }
        return null;
    }

    /**
     * @param url url
     * @param charset charset
     * @return String
     * @throws HttpResponseException HttpResponseException
     */
    public static String sendGetWithRetry(String url, String charset) throws HttpResponseException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = getHttpClientWithRetry().execute(httpGet);
            return responseToString(response, charset);
        } catch (HttpResponseException e) {
            logger.error("Response error, res code: " + e.getStatusCode() + " url: " + url, e);
            throw e;
        } catch (Exception e1) {
            logger.error("Send Get request to url faild, url: " + url, e1);
        } finally {
            httpGet.abort();
        }
        return null;
    }

    /**
     * 带有用户名和密码的GET
     * 
     * @param url 访问的地址
     * @param userName 校验用户名
     * @param password 校验密码
     * @return String
     */
    public static String sendGetWithAuthor(String url, String userName, String password) {
        HttpGet httpGet = new HttpGet(url);
        try {
            DefaultHttpClient defaultHttpClient = (DefaultHttpClient) getHttpClient();
            defaultHttpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(userName, password));
            HttpResponse response = defaultHttpClient.execute(httpGet);
            return responseToString(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap) throws Exception {
        return sendPost(url, parameterMap, null, "UTF-8", false);
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap, boolean ssl) throws Exception {
        return sendPost(url, parameterMap, null, "UTF-8", ssl);
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @param encoding encoding
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap, String encoding, boolean ssl)
            throws Exception {
        return sendPost(url, parameterMap, null, encoding, ssl);
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @param headerMap headerMap
     * @param encoding encoding
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap, Map<String, String> headerMap,
            String encoding, boolean ssl) throws Exception {
        StringEntity entity = null;

        if (parameterMap != null && !parameterMap.isEmpty()) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : parameterMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            try {
                // entity = new UrlEncodedFormEntity(params, encoding);
                entity = new StringEntity(URLEncodedUtils.format(params, encoding));
                entity.setContentType(URLEncodedUtils.CONTENT_TYPE);
            } catch (UnsupportedEncodingException e) {
                logger.error("Encode the parameter failed!", e);
            }
        }

        return sendPostWithEntity(url, entity, headerMap, ssl);
    }

    private static String sendPostWithEntity(String url, HttpEntity entity, Map<String, String> headerMap, boolean ssl)
            throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (entity != null) {
            httpPost.setEntity(entity);
        }
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        HttpResponse response;
        if (ssl) {
            response = useTrustingTrustManager(getHttpClient()).execute(httpPost);
        } else {
            response = getHttpClient().execute(httpPost);
        }
        return responseToString(response);
    }

    private static String responseToString(HttpResponse response) throws Exception {
        return responseToString(response, "UTF-8");
    }

    private static String responseToString(HttpResponse response, String charset) throws Exception {
        HttpEntity entity = getHttpEntity(response);
        if (entity == null) {
            return null;
        }
        return EntityUtils.toString(entity, charset);
    }

    private static byte[] responseToByte(HttpResponse response) throws Exception {
        HttpEntity entity = getHttpEntity(response);
        if (entity == null) {
            return null;
        }
        return EntityUtils.toByteArray(entity);
    }

    private static HttpEntity getHttpEntity(HttpResponse response) throws Exception {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            logger.error(response);
            EntityUtils.consume(response.getEntity());
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(response);
            }
            return response.getEntity();
        }
    }

    /**
     * When HttpClient instance is no longer needed, shut down the connection manager to ensure immediate deallocation
     * of all system resources
     */
    public static void shutdown() {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
            httpClient = null;
        }
    }

    /**
     * Create an HttpClient with the ThreadSafeClientConnManager.
     * 
     * @return
     */
    private static HttpClient getHttpClient() {
        if (httpClient == null) {
            ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
            connectionManager.setDefaultMaxPerRoute(500); // 每个host最多50个连接
            connectionManager.setMaxTotal(5000); // 一共500个连接
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager);
            HttpConnectionParams.setConnectionTimeout(defaultHttpClient.getParams(), 30000); // 30秒连接超时
            HttpConnectionParams.setSoTimeout(defaultHttpClient.getParams(), 50000); // 50秒请求超时

            // 增加gzip支持
            defaultHttpClient.addRequestInterceptor(new AcceptEncodingRequestInterceptor());
            defaultHttpClient.addResponseInterceptor(new ContentEncodingResponseInterceptor());

            httpClient = defaultHttpClient;

        }

        // 模拟浏览器，解决一些服务器程序只允许浏览器访问的问题
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");

        return httpClient;
    }

    /**
     * Create an HttpClient with the ThreadSafeClientConnManager.
     * 
     * @return
     */
    private static HttpClient getHttpClientWithRetry() {
        if (httpClient == null) {
            ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
            connectionManager.setDefaultMaxPerRoute(500); // 每个host最多50个连接
            connectionManager.setMaxTotal(5000); // 一共500个连接
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager);
            HttpConnectionParams.setConnectionTimeout(defaultHttpClient.getParams(), 2000); // 5秒超时
            HttpConnectionParams.setSoTimeout(defaultHttpClient.getParams(), 2000);

            // 增加gzip支持
            defaultHttpClient.addRequestInterceptor(new AcceptEncodingRequestInterceptor());
            defaultHttpClient.addResponseInterceptor(new ContentEncodingResponseInterceptor());
            defaultHttpClient.setHttpRequestRetryHandler(requestRetryHandler);
            httpClient = defaultHttpClient;

        }

        // 模拟浏览器，解决一些服务器程序只允许浏览器访问的问题
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");

        return httpClient;
    }

    private static class AcceptEncodingRequestInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }
        }
    }

    private static class ContentEncodingResponseInterceptor implements HttpResponseInterceptor {
        public void process(final HttpResponse response, final HttpContext context) throws HttpException {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if ("gzip".equalsIgnoreCase(codecs[i].getName())) {
                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        }
    }

    private static class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException {
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

    // 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
        // 自定义的恢复策略
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            // 设置恢复策略，在发生异常时候将自动重试5次
            if (executionCount >= 3) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                return false;
            }
            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
            if (!idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }
    };

    /**
     * 返回请求响应的状态码
     * 
     * @param url url
     * @return status code
     */
    public static int getResponseStatusCode(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            return connection.getResponseCode();
        } catch (Exception e) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return -1;
    }

    /**
     * https 协议访问
     * 
     * @param httpClient
     * @return DefaultHttpClient
     */
    public static DefaultHttpClient useTrustingTrustManager(HttpClient httpClient) {
        try {
            // First create a trust manager that won't care.
            X509TrustManager trustManager = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // Don't do anything.
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // Don't do anything.
                }

                public X509Certificate[] getAcceptedIssuers() {
                    // Don't do anything.
                    return null;
                }
            };

            // Now put the trust manager into an SSLContext.
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { trustManager }, null);

            // Use the above SSLContext to create your socket factory
            SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // If you want a thread safe client, use the ThreadSafeConManager, but
            // otherwise just grab the one from the current client, and get hold of its
            // schema registry. THIS IS THE KEY THING.
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry schemeRegistry = ccm.getSchemeRegistry();

            // Register our new socket factory with the typical SSL port and the
            // correct protocol name.
            schemeRegistry.register(new Scheme("https", 443, sf));

            // Finally, apply the ClientConnectionManager to the Http Client
            // or, as in this example, create a new one.
            return new DefaultHttpClient(ccm, httpClient.getParams());
        } catch (Throwable t) {
            // AND NEVER EVER EVER DO THIS, IT IS LAZY AND ALMOST ALWAYS WRONG!
            t.printStackTrace();
            return null;
        }
    }
}
