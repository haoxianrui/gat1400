package com.juxingtech.helmet.controller.api;


import cn.hutool.json.JSONUtil;
import com.juxingtech.helmet.bean.*;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.util.DigestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result register(
            String deviceId
    ) {
        String uri = "/VIID/System/Register";
        String url = "http://" + ip + ":" + port + uri;

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            // 请求头
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("User-Identify", deviceId);
            httpPost.setHeader("Connection", "keepalive");

            // 请求参数
            RegisterRequestObject registerRequestObject = new RegisterRequestObject();
            RegisterRequestObject.RegisterObject registerObject = new RegisterRequestObject.RegisterObject();
            registerRequestObject.setRegisterObject(registerObject);
            registerObject.setDeviceID(deviceId);
            StringEntity stringEntity = new StringEntity(JSONUtil.toJsonStr(registerRequestObject), "UTF-8");
            httpPost.setEntity(stringEntity);

            // 第一次请求
            httpResponse = httpClient.execute(httpPost);
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
                String response = DigestUtils.getResponse(username, realm, password, nonce, nc, cnonce, qop, method, uri);

                String  authorization= DigestUtils.getAuthorization(username, realm, nonce, uri, qop, nc, cnonce, response, opaque);
                httpPost.addHeader("Authorization",authorization);

                // 第二次请求
                httpResponse = httpClient.execute(httpPost);
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
        }
    }

    @PostMapping("/keepalive")
    @ApiOperation(value = "保活", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result keepalive(
            String deviceId
    ) {
        String url = "http://" + ip + ":" + port + "/VIID/System/Keepalive";

        // 请求头设置
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.set("User-Identify", deviceId);
        headers.setConnection("keepalive");
        // 请求参数设置
        KeepaliveRequestObject keepaliveRequestObject = new KeepaliveRequestObject();
        KeepaliveRequestObject.KeepaliveObject keepaliveObject = new KeepaliveRequestObject.KeepaliveObject();
        keepaliveObject.setDeviceID(deviceId);
        keepaliveRequestObject.setKeepaliveObject(keepaliveObject);
        log.info("保活请求 url:{} ,参数：{}", url, keepaliveRequestObject);

        HttpEntity<KeepaliveRequestObject> httpEntity = new HttpEntity<>(keepaliveRequestObject, headers);
        // 请求执行
        ResponseStatusObject responseStatusObject = restTemplate.postForObject(url, httpEntity, ResponseStatusObject.class);
        log.info("保活响应 {}", responseStatusObject.toString());
        return Result.success();

    }


    @PostMapping("/unregister")
    @ApiOperation(value = "注销", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result unregister(
            String deviceId
    ) {

        String uri = "/VIID/System/UnRegister";
        String url = "http://" + ip + ":" + port + uri;

        // 请求头设置
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.set("User-Identify", deviceId);
        headers.setConnection("keepalive");

        // 请求参数设置
        UnRegisterRequestObject unRegisterRequestObject = new UnRegisterRequestObject();
        UnRegisterRequestObject.UnRegisterObject unRegisterObject = new UnRegisterRequestObject.UnRegisterObject();
        unRegisterObject.setDeviceID(deviceId);
        unRegisterRequestObject.setUnRegisterObject(unRegisterObject);

        HttpEntity<UnRegisterRequestObject> httpEntity = new HttpEntity<>(unRegisterRequestObject, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        int statusCode = responseEntity.getStatusCode().value();

        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            HttpHeaders responseHeaders = responseEntity.getHeaders();
            List<String> list = responseHeaders.get("WWW-Authenticate");
            String realm = null;
            String qop = null;
            String nonce = null;
            String opaque = null;
            String method = "POST";
            String nc = "00000001";
            String cnonce = DigestUtils.generateSalt2(8);
            String response = DigestUtils.getResponse(username, realm, password, nonce, nc, cnonce, qop, method, uri);
            String authorization = DigestUtils.getAuthorization(username, realm, nonce, uri, qop, nc, cnonce, response, opaque);
            headers.set("Authorization", authorization);
            httpEntity = new HttpEntity<>(unRegisterRequestObject, headers);
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            statusCode = responseEntity.getStatusCode().value();

            if (statusCode == HttpStatus.SC_OK) {
                return Result.success();
            }
        }
        return Result.error("注销失败");
    }


    @GetMapping("/time")
    @ApiOperation(value = "校时", httpMethod = "GET")
    public Result time(
            String deviceId
    ) {
        String url = "http://" + ip + ":" + port + "/VIID/System/Time";
        // 请求头设置
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Identify", deviceId);
        headers.setConnection("keepalive");
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SystemTimeObject> entity = restTemplate.getForEntity(url, SystemTimeObject.class,httpEntity);
        SystemTimeObject.SystemTime systemTime = entity.getBody().getSystemTime();
        DateTimeFormatter.ofPattern("yyyyMMdd").parse("");
        return Result.success(systemTime.getLocalTime());
    }
}
