package com.juxingtech.helmet.common.util;

import com.juxingtech.helmet.common.enums.HttpEnum;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

public class HttpTestUtils {

    public static String httpRequest(HttpEnum method, String ip, int port, String action, String token, String content) {
        String responseJson = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        InputStream inputStream;
        try {
            httpClient = HttpClients.createDefault();
            String uri="http://" + ip + ":" + port + action;
            HttpRequestBase httpReq = getRequestEntity(method, token, uri, content);
            httpResponse = httpClient.execute(httpReq);
            inputStream = httpResponse.getEntity().getContent();
            responseJson = convertToString(inputStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }

    private static HttpRequestBase getRequestEntity(HttpEnum method, String token, String uri, String content) throws UnsupportedEncodingException {
        switch(method.getNum()){
            case 1:
                HttpGet httpGet = new HttpGet(uri+content);
                httpGet.addHeader("Content-type", "application/json");
                httpGet.addHeader("X-Subject-Token", token);
                return httpGet;
            case 2:
                HttpPost httpPost = new HttpPost(uri);
                httpPost.addHeader("Content-type", "application/json");
                httpPost.addHeader("X-Subject-Token", token);
                httpPost.setEntity(new StringEntity(content,"UTF-8"));
                return httpPost;
            case 3:
                HttpPut httpPut= new HttpPut(uri);
                httpPut.addHeader("Content-type", "application/json");
                httpPut.addHeader("X-Subject-Token", token);
                httpPut.setEntity(new StringEntity(content,"UTF-8"));
                return httpPut;
            case 4:
                HttpDelete httpDelete = new HttpDelete(uri+content);
                httpDelete.addHeader("Content-type", "application/json");
                httpDelete.addHeader("X-Subject-Token", token);
                return httpDelete;
            default:
                    System.out.println("请求方法不对");
        }
        return null;
    }

    private static String convertToString(InputStream is) {
        if (is == null) {
            return null;
        }
        BufferedReader bf = null;
        try {
            StringBuilder sb = new StringBuilder();
            String temp = "";
            bf = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while ((temp = bf.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStream(bf);
            closeStream(is);
        }
    }

    private static void closeStream(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

 /*   public static String getToken(String ip,int port,String userName,String password) throws Exception {
        String response="";
        String token="";
        response = Login.login(ip, port, userName, password);
        Map<String, Object> rsp = new Gson().fromJson(response, Map.class);
        String message= (String)rsp.get("message");
        if (message!=null&&!"".equals(message)){
            System.out.println(message);
            throw new Exception("未获取到token");
        }
        token = (String)rsp.get("token");
        if(token==null||"".equals(token)){
            System.out.println("获取到的token为空");
            throw new Exception("获取到的token为空");
        }
        return  token;
    }*/

}
