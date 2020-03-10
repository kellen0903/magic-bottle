package com.teorange.magic.bottle.admin.modules.job.utils;

import cn.teorange.framework.core.exception.RRException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by admin on 2016/1/23.
 */
public class HttpRequestUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    private HttpRequestUtil() {

    }

    /**
     * 连接时间
     */
    private static int connectTimeout = 30000;

    /**
     * 响应时间
     */
    private static int readTimeout = 60000;

    protected static HttpClient mClient;

    public static String transPost(String url, File files, String fileName) throws IOException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.socket.timeout", Integer.valueOf(5000));
        HttpPost httpPost = new HttpPost(url);
        String message = "This is a multipart post";
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("upfile", files, ContentType.MULTIPART_FORM_DATA, fileName);
        builder.addTextBody("text", message, ContentType.TEXT_PLAIN);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "utf-8");
    }

    public static String post(String url, Map<String, String> header, Map<String, Object> body, File in, String fileName)
            throws IOException {
        mClient = new DefaultHttpClient();
        mClient.getParams().setParameter("http.socket.timeout", 5 * 1000);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("accept", "*/*");
        httpPost.setHeader("connection", "Keep-Alive");
        httpPost.setHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        if (null != header) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        if (null != header && (false == header.containsKey("Content-Type")
                || header.get("Content-Type").equals("multipart/form-data"))) {
            MultipartEntity multipartEntity = new MultipartEntity();
            if (body != null) {
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    multipartEntity.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.MULTIPART_FORM_DATA));
                }
            }

            if (in != null) {
                ContentBody contentBody = new FileBody(in, ContentType.MULTIPART_FORM_DATA);
                multipartEntity.addPart(fileName, contentBody);
            }
            httpPost.setEntity(multipartEntity);
        } else {
            if (in != null) {
                String strBody = new String(in.toString());
                StringEntity stringEntity = new StringEntity(strBody);
                httpPost.setEntity(stringEntity);
            }
        }

        HttpResponse httpResponse = mClient.execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
    }

    /**
     * MAP数据转换成HTTP参数格式的字符串
     *
     * @param requestMap
     * @return String
     */
    public static String toHttpParamString(Map<String, Object> requestMap) {
        StringBuilder result = new StringBuilder();
        if (requestMap != null && !requestMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
                if (!StringUtils.isEmpty(entry.getKey())) {
                    result.append(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        return sendGet(url, param, "application/x-www-form-urlencoded");
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendJsonGet(String url, String param) {
        return sendGet(url, param, "application/json");
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url         发送请求的URL
     * @param param       请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param contentType 媒体类型
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String contentType) {
        return sendGet(url, param, contentType, connectTimeout, readTimeout);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url            发送请求的URL
     * @param param          请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param contentType    媒体类型
     * @param connectTimeout 连接时间
     * @param readTimeout    响应时间
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String contentType, int connectTimeout, int readTimeout) {
        String result = "";
        BufferedReader in = null;
        try {
            if (StringUtils.isNotEmpty(param)) {
                if (url.indexOf("?") > 0) {
                    url = url + "&" + param;
                } else {
                    url = url + "?" + param;
                }
            }
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            // 建立实际的连接
            conn.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            result = "ERROR";
            throw new RRException("sendGet method error: " + e);
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                logger.error("sendGet method close BufferedReader " + e);
            }
        }
        return result;
    }

    /**
     * 向指定微信平台URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendHttpsGet(String url, String param) {
        HttpClientBuilder httpClient = HttpClients.custom();
        httpClient.setSSLHostnameVerifier(new NoopHostnameVerifier());
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient.build());

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        if (StringUtils.isNotEmpty(param)) {
            if (url.indexOf("?") > 0) {
                url = url + "&" + param;
            } else {
                url = url + "?" + param;
            }
        }

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String result = response.getBody();

        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        return sendPost(url, param, "application/x-www-form-urlencoded");
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String sendJsonPost(String url, String param) {
        return sendPost(url, param, "application/json");
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url         发送请求的 URL
     * @param param       请求参数
     * @param contentType 媒体类型
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, String contentType) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", contentType);
        return sendPost(url, param, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url            发送请求的 URL
     * @param param          请求参数
     * @param contentType    媒体类型
     * @param connectTimeout 连接时间
     * @param readTimeout    响应时间
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, String contentType, int connectTimeout, int readTimeout) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", contentType);
        return sendPost(url, param, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url       发送请求的 URL
     * @param param     请求参数
     * @param headerMap 请求头
     * @return 所代表远程资源的响应结果
     */
    public static String sendJsonPost(String url, String param, Map<String, String> headerMap) {
        if (null == headerMap) {
            headerMap = new HashMap<String, String>();
        }
        headerMap.put("Content-Type", "application/json");
        return sendPost(url, param, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url            发送请求的 URL
     * @param param          请求参数
     * @param connectTimeout 连接时间
     * @param readTimeout    响应时间
     * @param headerMap      请求头
     * @return 所代表远程资源的响应结果
     */
    public static String sendJsonPost(String url, String param, int connectTimeout, int readTimeout, Map<String, String> headerMap) {
        if (null == headerMap) {
            headerMap = new HashMap<String, String>();
        }
        headerMap.put("Content-Type", "application/json");
        return sendPost(url, param, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url            发送请求的 URL
     * @param param          请求参数
     * @param connectTimeout 连接时间
     * @param readTimeout    响应时间
     * @param headerMap      请求头
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, int connectTimeout, int readTimeout, Map<String, String> headerMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (null != headerMap && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getValue())) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            result = "ERROR";
            throw new RRException("sendPost method error: " + e);
        } finally {
            //使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error("sendPost method close PrintWriter or BufferedReader " + ex);
            }
        }
        return result;
    }

    /**
     * 发送HTTP POST请求
     *
     * @param url    发送请求的 URL
     * @param params 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String httpPost(String url, Map<String, Object> params) {
        return httpPost(url, params, connectTimeout, readTimeout);
    }

    /**
     * 发送HTTP POST请求
     *
     * @param url            发送请求的 URL
     * @param params         请求参数
     * @param connectTimeout 连接时间
     * @param readTimeout    响应时间
     * @return 所代表远程资源的响应结果
     */
    public static String httpPost(String url, Map<String, Object> params, int connectTimeout, int readTimeout) {
        // 读取返回内容
        StringBuilder buffer = new StringBuilder();
        // 构建请求参数
        String paramStr = toHttpParamString(params);
        // 尝试发送请求
        HttpURLConnection con = null;
        OutputStreamWriter osw = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            con = (HttpURLConnection) realUrl.openConnection();
            // POST 只能为大写，严格限制，post会不识别
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setConnectTimeout(connectTimeout);
            con.setReadTimeout(readTimeout);
            // 获取URLConnection对象对应的输出流
            osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            // 发送请求参数
            osw.write(paramStr);
            // flush输出流的缓冲
            osw.flush();
            //一定要有返回值，否则无法把请求发送给server端。
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = in.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
        } catch (Exception e) {
            buffer = new StringBuilder("ERROR");
            throw new RRException("httpPost method error: "+e);
        } finally {
            //使用finally块来关闭连接、输出流、输入流
            try {
                if (osw != null) {
                    osw.close();
                }
                if (in != null) {
                    in.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (IOException ex) {
                logger.error("httpPost method close OutputStreamWriter or BufferedReader or HttpURLConnection " + ex);
            }
        }
        return buffer.toString();
    }

}
