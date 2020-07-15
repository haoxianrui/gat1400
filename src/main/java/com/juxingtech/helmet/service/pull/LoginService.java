package com.juxingtech.helmet.service.pull;

import com.google.gson.Gson;
import com.juxingtech.helmet.bean.LoginFirst;
import com.juxingtech.helmet.bean.LoginSecond;
import com.juxingtech.helmet.common.enums.HttpEnum;
import com.juxingtech.helmet.common.util.HttpTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Service
@Slf4j
public class LoginService {

    public static final String LOGIN_ACTION = "/videoService/accounts/authorize";

    public static final String KEEP_ALIVE_ACTION = "/videoService/accounts/token/keepalive";

    public static final String LOGOUT_ACTION = "/videoService/accounts/unauthorize";


    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    //第一次登陆，客户端只传用户名，服务端返回realm、readomKey和encryptType信息。
    private static String firstLogin(String ip, int port, String userName) {
        LoginFirst loginFirst = new LoginFirst();
        loginFirst.setClientType("winpc");
        loginFirst.setUserName(userName);
        String rsp = HttpTestUtils.httpRequest(HttpEnum.POST, ip, port, LOGIN_ACTION, "", new Gson().toJson(loginFirst));
        return rsp;
    }

    //第二次登录，客户端根据返回的信息，按照指定的加密算法计算签名，再带着用户名和签名登陆一次。
    private static String secondLogin(String ip, int port, String userName, String password, String realm, String randomKey) throws Exception {
        LoginSecond snd = new LoginSecond();
        snd.setUserName(userName);
        snd.setClientType("winpc");
        snd.setRandomKey(randomKey);
        snd.setEncryptType("MD5");
        String signature = snd.calcSignature(password, realm);
        snd.setSignature(signature);
        Gson gson = new Gson();
        String ctx = gson.toJson(snd);
        String rsp = HttpTestUtils.httpRequest(HttpEnum.POST, ip, port, LOGIN_ACTION, "", ctx);
        return rsp;
    }

    public String login(String ip, int port, String username, String password) throws Exception {

        String response = firstLogin(ip, port, username);
        Map<String, String> responseMap = new Gson().fromJson(response, Map.class);
        String random = responseMap.get("randomKey");
        String realm = responseMap.get("realm");
        response = secondLogin(ip, port, username, password, realm, random);
        log.info("登陆结果：{}", response);

        Map<String, Object> rsp = new Gson().fromJson(response, Map.class);
        String message = (String) rsp.get("message");
        if (message != null && !"".equals(message)) {
            log.info(message);
            throw new Exception("未获取到token");
        }
        String token = (String) rsp.get("token");
        if (token == null || "".equals(token)) {
            log.info("获取到的token为空");
            throw new Exception("获取到的token为空");
        }

        // 轮询请求保活
        double duration = Double.valueOf(String.valueOf(rsp.get("duration")));
        Double time = duration * 3 / 4;
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String format = decimalFormat.format(time);
        log.info(format);

        String cron = "0/" + format + " * * * * ?";
        threadPoolTaskScheduler.schedule(() -> {
            log.info("定时保活中:{}", new Date());
            String content = "{\"token\":\"" + token + "\"}";
            String keepAliveResp = HttpTestUtils.httpRequest(HttpEnum.PUT, ip, port, KEEP_ALIVE_ACTION, token, content);
            log.info(keepAliveResp);

        }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));

        return token;
    }

    public void logout(String ip, int port, String token) {
        String content = "{\"token\":\"" + token + "\"}";
        HttpTestUtils.httpRequest(HttpEnum.POST, ip, port, LOGOUT_ACTION, token, content);
        Properties pro = new Properties();
        pro.setProperty("token", "");
        try {
            File file = new File("src/main/resources/token.properties");
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            pro.store(writer, "setToken");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("退出成功");
    }
}
