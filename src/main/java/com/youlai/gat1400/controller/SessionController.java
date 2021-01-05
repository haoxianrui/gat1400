package com.youlai.gat1400.controller;

import cn.hutool.json.JSONUtil;
import com.youlai.gat1400.domain.SystemTimeObject;
import com.youlai.gat1400.common.result.Result;
import com.youlai.gat1400.common.util.DigestUtils;
import com.youlai.gat1400.domain.KeepaliveRequestObject;
import com.youlai.gat1400.domain.RegisterRequestObject;
import com.youlai.gat1400.domain.UnRegisterRequestObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Api(tags = "GAT/1400 会话管理接口")
@RestController
@RequestMapping("/system")
@Slf4j
public class SessionController {

    String registerUrl, unRegisterUrl, keepaliveUrl, timeUrl;

    public SessionController(
            @Value("${server1400.ip}") String ip,
            @Value("${server1400.port}") String port
    ) {
        registerUrl = "http://" + ip + ":" + port + "/VIID/System/Register";
        unRegisterUrl = "http://" + ip + ":" + port + "/VIID/System/UnRegister";
        keepaliveUrl = "http://" + ip + ":" + port + "/VIID/System/Keepalive";
        timeUrl = "http://" + ip + ":" + port + "/VIID/System/Time";
    }

    @Autowired
    private RestTemplate restTemplate;

    @Value(value = "${server1400.username}")
    private static String username;

    @Value(value = "${server1400.password}")
    private static String password;


    @ApiOperation(value = "测试", httpMethod = "GET")
    @GetMapping("/test")
    public Result test() {
        return Result.success("registerUrl:" + registerUrl + ",unRegisterUrl:" + unRegisterUrl + ",keepaliveUrl:" + keepaliveUrl + ",timeUrl:" + timeUrl);
    }


    @PostMapping("/register")
    @ApiOperation(value = "注册", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result register(String deviceId) {
        // 请求头设置
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.set("User-Identify", deviceId);
        headers.setConnection("keepalive");
        // 请求参数设置
        RegisterRequestObject registerRequestObject = new RegisterRequestObject();
        RegisterRequestObject.RegisterObject registerObject = new RegisterRequestObject.RegisterObject();
        registerObject.setDeviceID(deviceId);
        registerRequestObject.setRegisterObject(registerObject);

        HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(registerRequestObject), headers);
        // 第一次请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(registerUrl, HttpMethod.POST, httpEntity, String.class);
        if (org.apache.http.HttpStatus.SC_UNAUTHORIZED == responseEntity.getStatusCode().value()) {
            HttpHeaders responseEntityHeaders = responseEntity.getHeaders();
            String authenticate = responseEntityHeaders.get("WWW-Authenticate").get(0);
            String[] children = authenticate.split(",");
            // Digest realm="myrealm",qop="auth",nonce="dmktZGlnZXN0OjQzNTQyNzI3Nzg="
            String realm = null, qop = null, nonce = null, opaque = null, method = "POST";
            ;
            for (int i = 0; i < children.length; i++) {
                String item = children[i];
                String[] itemEntry = item.split("=");
                if (itemEntry[0].equals("Digest realm")) {
                    realm = itemEntry[1].replaceAll("\"", "");
                } else if (itemEntry[0].equals("qop")) {
                    qop = itemEntry[1].replaceAll("\"", "");
                } else if (itemEntry[0].equals("nonce")) {
                    nonce = itemEntry[1].replaceAll("\"", "");
                }
            }
            String nc = "00000001";
            String cnonce = DigestUtils.generateSalt2(8);
            String response = DigestUtils.getResponse(username, realm, password, nonce, nc, cnonce, qop, method, registerUrl);
            String authorization = DigestUtils.getAuthorization(username, realm, nonce, registerUrl, qop, nc, cnonce, response, opaque);
            headers.set("Authorization", authorization);

            // 第二次请求
            httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(registerRequestObject), headers);
            responseEntity = restTemplate.exchange(registerUrl, HttpMethod.POST, httpEntity, String.class);
            if (org.apache.http.HttpStatus.SC_OK == responseEntity.getStatusCode().value()) {
                return Result.success();
            }
        }
        return Result.failed("注册失败");

    }

    @PostMapping("/keepalive")
    @ApiOperation(value = "保活", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result keepalive(
            String deviceId
    ) {
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
        log.info("保活请求 url:{} ,参数：{}", keepaliveUrl, keepaliveRequestObject);

        HttpEntity<KeepaliveRequestObject> httpEntity = new HttpEntity<>(keepaliveRequestObject, headers);
        // 请求执行
        ResponseEntity<String> responseEntity = restTemplate.exchange(keepaliveUrl, HttpMethod.POST, httpEntity, String.class);
        if (org.apache.http.HttpStatus.SC_OK == responseEntity.getStatusCode().value()) {
            return Result.success();
        }
        return Result.failed("保活失败");
    }


    @PostMapping("/unregister")
    @ApiOperation(value = "注销", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result unregister(String deviceId) {
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

        HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(unRegisterRequestObject), headers);
        // 第一次请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(unRegisterUrl, HttpMethod.POST, httpEntity, String.class);
        if (org.apache.http.HttpStatus.SC_UNAUTHORIZED == responseEntity.getStatusCode().value()) {
            HttpHeaders responseEntityHeaders = responseEntity.getHeaders();
            String authenticate = responseEntityHeaders.get("WWW-Authenticate").get(0);
            String[] children = authenticate.split(",");
            // Digest realm="myrealm",qop="auth",nonce="dmktZGlnZXN0OjQzNTQyNzI3Nzg="
            String realm = null, qop = null, nonce = null, opaque = null, method = "POST";
            ;
            for (int i = 0; i < children.length; i++) {
                String item = children[i];
                String[] itemEntry = item.split("=");
                if (itemEntry[0].equals("Digest realm")) {
                    realm = itemEntry[1].replaceAll("\"", "");
                } else if (itemEntry[0].equals("qop")) {
                    qop = itemEntry[1].replaceAll("\"", "");
                } else if (itemEntry[0].equals("nonce")) {
                    nonce = itemEntry[1].replaceAll("\"", "");
                }
            }
            String nc = "00000001";
            String cnonce = DigestUtils.generateSalt2(8);
            String response = DigestUtils.getResponse(username, realm, password, nonce, nc, cnonce, qop, method, unRegisterUrl);
            String authorization = DigestUtils.getAuthorization(username, realm, nonce, unRegisterUrl, qop, nc, cnonce, response, opaque);
            headers.set("Authorization", authorization);

            // 第二次请求
            httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(unRegisterRequestObject), headers);
            responseEntity = restTemplate.exchange(unRegisterUrl, HttpMethod.POST, httpEntity, String.class);
            if (org.apache.http.HttpStatus.SC_OK == responseEntity.getStatusCode().value()) {
                return Result.success();
            }
        }
        return Result.failed("注销失败");
    }

    @GetMapping("/time")
    @ApiOperation(value = "校时", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", example = "13030421191190201061", required = true, paramType = "query")
    })
    public Result time(String deviceId) {
        // 请求头设置
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Identify", deviceId);
        headers.setConnection("keepalive");
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<SystemTimeObject> entity = restTemplate.getForEntity(timeUrl, SystemTimeObject.class, httpEntity);
        SystemTimeObject.SystemTime systemTime = entity.getBody().getSystemTime();

        String time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        return Result.success(time);
    }


}
