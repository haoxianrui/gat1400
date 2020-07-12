package com.juxingtech.helmet.controller.api;


import cn.hutool.json.JSONUtil;
import com.juxingtech.helmet.bean.KeepaliveRequestObject;
import com.juxingtech.helmet.bean.RegisterRequestObject;
import com.juxingtech.helmet.bean.ResponseStatusObject;
import com.juxingtech.helmet.bean.SystemTimeObject;
import com.juxingtech.helmet.common.result.IResultCode;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.common.result.ResultCodeEnum;
import com.juxingtech.helmet.util.DigestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Api(tags = "GAT/1400 会话管理接口")
@RestController
@RequestMapping("/api/v1/system")
@Slf4j
public class SystemController {


    @Value(value = "${dahua-server.ip}")
    private String ip;

    @Value(value = "${dahua-server.port}")
    private Integer port;

    @Value(value = "${dahua-server.username}")
    private String username;

    @Value(value = "${dahua-server.password}")
    private String password;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/register")
    @ApiOperation(value = "注册", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "230102999011190100011", required = true, paramType = "query")
    })
    public Result register(
            String deviceId
    ) {

        // 摘要认证：

        //  第一次客户端请求，服务器产生一个随机数nonce
        //      HTTP /1.1 401 Unauthorized
        //      WWW-Authenticate: Digest
        //      realm="realm",      -- 认证的域
        //      qop="auth",         -- 认证（校验）的方式
        //      nonce="MTU2NDcwODUxNDE3OTphN2NiNGU2MTI0YjMzMjUyZWMwZDUwNzVmZDhkMWFjMA=="  -- 随机数 可以用GUID

        //  第二次请求
        //      Authorization:Digest
        //      username="admin",
        //      realm="security",
        //      nonce="MTU2NDcwODUxNDE3OTphN2NiNGU2MTI0YjMzMjUyZWMwZDUwNzVmZDhkMWFjMA==",
        //      uri="/VIID/System/Register",
        //      response="1571f59cc22a9699e944048e6f9362a7",
        //      algorithm="MD5",
        //      qop=auth,
        //      nc=00000001,
        //      cnonce="8c28955e90665322"



  /*      String uri = "/VIID/System/Register";
        String url = "http://" + ip + ":" + port + uri;

        log.info("Post请求url:[{}]", url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            // 请求头
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.setHeader("Connection", "Close");

            // 请求配置
            RequestConfig.Builder builder = RequestConfig.custom();
            builder.setSocketTimeout(3000);      // 设置请求时间
            builder.setConnectTimeout(5000);     // 设置超时时间
            builder.setRedirectsEnabled(false);  // 设置是否跳转链接(反向代理)
            httpPost.setConfig(builder.build()); // 设置 连接 属性

            // 请求参数
            RegisterRequestObject registerRequestObject = new RegisterRequestObject();
            RegisterRequestObject.RegisterObject registerObject = new RegisterRequestObject.RegisterObject();
            registerRequestObject.setRegisterObject(registerObject);
            registerObject.setDeviceID(deviceId);
            StringEntity stringEntity = new StringEntity(JSONUtil.toJsonStr(registerRequestObject), "UTF-8");
            httpPost.setEntity(stringEntity);

            // 第一次请求
            httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            log.error("第一次请求结果:{}", EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (401 == statusCode) {
                // 第二次请求
                Header[] headers = httpResponse.getHeaders("WWW-Authenticate");
                HeaderElement[] elements = headers[0].getElements();
                String realm = null;
                String qop = null;
                String nonce = null;
                String opaque = null;
                String method = "POST";

                for (HeaderElement element : elements) {
                    if (element.getName().equals("Digest realm")) {
                        realm = element.getValue();
                    } else if (element.getName().equals("qop")) {
                        qop = element.getValue();
                    } else if (element.getName().equals("nonce")) {
                        nonce = element.getValue();
                    } else if (element.getName().equals("opaque")) {
                        opaque = element.getValue();
                    }
                }
                String nc = "00000001";
                String cnonce = DigestUtils.generateSalt2(8);
                String response = DigestUtils.getResponse(username, realm, password, nonce, nc, cnonce, qop, method, uri, null);
                httpPost.addHeader(
                        "Authorization",
                        "Digest" +
                                " username=\"" + username + "\"" +
                                ",realm=\"" + realm + "\"" +
                                ",nonce=\"" + nonce + "\"" +
                                ",uri=\"" + uri + "\"" +
                                ",qop=\"" + qop + "\"" +
                                ",nc=\"" + nc + "\"" +
                                ",cnonce=\"" + cnonce + "\"" +
                                ",response=\"" + response + "\"" +
                                ",opaque=\"" + opaque
                );

                // 第二次请求
                httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                log.info("第二次请求结果：{}", EntityUtils.toString(entity, StandardCharsets.UTF_8));
                statusCode = httpResponse.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    return Result.success();
                }
            }
            return Result.error();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error();
        } finally {
            if (null != httpPost) {
                httpPost.releaseConnection();
            }
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }*/

        return Result.custom(new IResultCode() {
            @Override
            public String getCode() {
                return "00000";
            }

            @Override
            public String getMsg() {
                return "注册成功";
            }
        });
    }

    @PostMapping("/keepalive")
    @ApiOperation(value = "保活", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "230102999011190100011", required = true, paramType = "query")
    })
    public Result keepalive(
            String deviceId
    ) {
        /*String url = "http://" + ip + ":" + port + "/VIID/System/Keepalive";
        KeepaliveRequestObject keepaliveRequestObject = new KeepaliveRequestObject();
        KeepaliveRequestObject.KeepaliveObject keepaliveObject = new KeepaliveRequestObject.KeepaliveObject();
        keepaliveObject.setDeviceID(deviceId);
        keepaliveRequestObject.setKeepaliveObject(keepaliveObject);
        log.info("保活请求 url:{} ,参数：{}", url, keepaliveRequestObject);
        ResponseStatusObject responseStatusObject = restTemplate.postForObject(url, keepaliveRequestObject, ResponseStatusObject.class);
        log.info("保活响应 {}", responseStatusObject.toString());
        return Result.success();*/
        return Result.custom(new IResultCode() {
            @Override
            public String getCode() {
                return "00000";
            }

            @Override
            public String getMsg() {
                return "保活成功";
            }
        });
    }


    @PostMapping("/unregister")
    @ApiOperation(value = "注销", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "230102999011190100011", required = true, paramType = "query")
    })
    public Result unregister(
            String deviceId
    ) {

        /*String uri = "/VIID/System/UnRegister";
        String url = "http://" + ip + ":" + port + uri;

        log.info("Post请求url:[{}]", url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            // 请求头
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.setHeader("Connection", "Close");

            // 请求配置
            RequestConfig.Builder builder = RequestConfig.custom();
            builder.setSocketTimeout(3000);      // 设置请求时间
            builder.setConnectTimeout(5000);     // 设置超时时间
            builder.setRedirectsEnabled(false);  // 设置是否跳转链接(反向代理)
            httpPost.setConfig(builder.build()); // 设置 连接 属性

            // 请求参数
            RegisterRequestObject registerRequestObject = new RegisterRequestObject();
            RegisterRequestObject.RegisterObject registerObject = new RegisterRequestObject.RegisterObject();
            registerRequestObject.setRegisterObject(registerObject);
            registerObject.setDeviceID(deviceId);
            StringEntity stringEntity = new StringEntity(JSONUtil.toJsonStr(registerRequestObject), "UTF-8");
            httpPost.setEntity(stringEntity);

            // 第一次请求
            httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            log.error("第一次请求结果:{}", EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (401 == statusCode) {
                // 第二次请求
                Header[] headers = httpResponse.getHeaders("WWW-Authenticate");
                HeaderElement[] elements = headers[0].getElements();
                String realm = null;
                String qop = null;
                String nonce = null;
                String opaque = null;
                String method = "POST";

                for (HeaderElement element : elements) {
                    if (element.getName().equals("Digest realm")) {
                        realm = element.getValue();
                    } else if (element.getName().equals("qop")) {
                        qop = element.getValue();
                    } else if (element.getName().equals("nonce")) {
                        nonce = element.getValue();
                    } else if (element.getName().equals("opaque")) {
                        opaque = element.getValue();
                    }
                }
                String nc = "00000001";
                String cnonce = DigestUtils.generateSalt2(8);
                String response = DigestUtils.getResponse(username, realm, password, nonce, nc, cnonce, qop, method, uri, null);
                httpPost.addHeader(
                        "Authorization",
                        "Digest" +
                                " username=\"" + username + "\"" +
                                ",realm=\"" + realm + "\"" +
                                ",nonce=\"" + nonce + "\"" +
                                ",uri=\"" + uri + "\"" +
                                ",qop=\"" + qop + "\"" +
                                ",nc=\"" + nc + "\"" +
                                ",cnonce=\"" + cnonce + "\"" +
                                ",response=\"" + response + "\"" +
                                ",opaque=\"" + opaque
                );

                // 第二次请求
                httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                log.info("第二次请求结果：{}", EntityUtils.toString(entity, StandardCharsets.UTF_8));
                statusCode = httpResponse.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    return Result.success();
                }
            }
            return Result.error();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error();
        } finally {
            if (null != httpPost) {
                httpPost.releaseConnection();
            }
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }*/

        return Result.custom(new IResultCode() {
            @Override
            public String getCode() {
                return "00000";
            }

            @Override
            public String getMsg() {
                return "注销成功";
            }
        });
    }


    @GetMapping("/time")
    @ApiOperation(value = "校时", httpMethod = "GET")
    public Result time(
    ) {
       /* String url = "http://" + ip + ":" + port + "/VIID/System/Time";
        ResponseEntity<SystemTimeObject> entity = restTemplate.getForEntity(url, SystemTimeObject.class);
        SystemTimeObject.SystemTime systemTime = entity.getBody().getSystemTime();
        return Result.success(systemTime.getLocalTime());*/
        return Result.success(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }
}
