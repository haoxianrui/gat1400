package com.juxingtech.helmet.service.pull;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.juxingtech.helmet.bean.*;
import com.juxingtech.helmet.common.enums.GenderEnum;
import com.juxingtech.helmet.common.enums.HttpEnum;
import com.juxingtech.helmet.common.util.HttpTestUtils;
import com.juxingtech.helmet.entity.HmsFaceRecord;
import com.juxingtech.helmet.framework.emqtt.service.MqttOutboundService;
import com.juxingtech.helmet.service.IHmsFaceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Service
@Slf4j
public class PullService {

    public static final String LOGIN_ACTION = "/videoService/accounts/authorize";

    public static final String KEEP_ALIVE_ACTION = "/videoService/accounts/token/keepalive";

    public static final String LOGOUT_ACTION = "/videoService/accounts/unauthorize";

    String nextMsgId = "-1";

    @Autowired
    private MqttOutboundService mqttOutputService;

    public static final String SUBSCRIBE_ADDRESS_ACTION = "/videoService/eventCenter/messages/subscribeAddress";
    public static final String SUBSCRIBE_ACTION = "/videoService/eventCenter/messages/subscribe";
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private IHmsFaceRecordService iHmsFaceRecordService;


    //第一次登陆，客户端只传用户名，服务端返回realm、readomKey和encryptType信息。
    private static String firstLogin(String ip, int port, String userName) {
        LoginFirst loginFirst = new LoginFirst();
        loginFirst.setClientType("winpc");
        loginFirst.setUserName(userName);
        String rsp = HttpTestUtils.httpRequest(HttpEnum.POST, ip, port, LOGIN_ACTION, "", new Gson().toJson(loginFirst));
        return rsp;
    }

    //第二次登录，客户端根据返回的信息，按照指定的加密算法计算签名，再带着用户名和签名登陆一次。
    private static String secondLogin(String ip, int port, String userName, String password, String realm, String randomKey) {
        LoginSecond snd = new LoginSecond();
        snd.setUserName(userName);
        snd.setClientType("winpc");
        snd.setRandomKey(randomKey);
        snd.setEncryptType("MD5");
        String signature = null;
        try {
            signature = snd.calcSignature(password, realm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        snd.setSignature(signature);
        Gson gson = new Gson();
        String ctx = gson.toJson(snd);
        String rsp = HttpTestUtils.httpRequest(HttpEnum.POST, ip, port, LOGIN_ACTION, "", ctx);
        return rsp;
    }

    ScheduledFuture<?> keepAliveSchedule;
    ScheduledFuture<?> faceSchedule;

    public void login(String ip, int port, String username, String password){
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
        }
        String token = (String) rsp.get("token");
        log.info("token:{}", token);
        if (token == null || "".equals(token)) {
            log.info("获取到的token为空");
        }

        // 轮询请求保活
        double duration = Double.valueOf(String.valueOf(rsp.get("duration")));
        Double time = duration * 3 / 4;
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String cron = "0/" + decimalFormat.format(time) + " * * * * ?";
        keepAliveSchedule = threadPoolTaskScheduler.schedule(() -> {
            log.info("定时保活中:{}", new Date());
            String content = "{\"token\":\"" + token + "\"}";
            String keepAliveResp = HttpTestUtils.httpRequest(HttpEnum.PUT, ip, port, KEEP_ALIVE_ACTION, token, content);
            Map<String, Object> keepAliveRespMap = new Gson().fromJson(keepAliveResp, Map.class);
            log.info("保活响应:{}", keepAliveRespMap);
            String code =String.valueOf(keepAliveRespMap.get("code"));
            if (!code.equals("200.0")) {
                log.info("保活失败，重新登陆:{}", code);
                if (keepAliveSchedule != null) {
                    log.info("保活定时任务取消");
                    keepAliveSchedule.cancel(true);
                }
                if (faceSchedule != null) {
                    log.info("拉取定时任务取消");
                    faceSchedule.cancel(true);
                }
                // 重新登陆
                this.login(ip, port, username, password);
            }
            log.info(keepAliveResp);
        }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));

        // 获取订阅地址
        String subscribeAddressRespJson = HttpTestUtils.httpRequest(HttpEnum.GET, ip, port, SUBSCRIBE_ADDRESS_ACTION, token, "");
        SubscribeAddressResp subscribeAddressResp = new Gson().fromJson(subscribeAddressRespJson, SubscribeAddressResp.class);
        String subscribeAddress = subscribeAddressResp.getSubscribeAddress();
        String[] addressArr = subscribeAddress.split(":");

        // 定时拉取人脸识别消息
        String faceCron = "0/3 * * * * ?";
        faceSchedule = threadPoolTaskScheduler.schedule(
                () -> {
                    String content = "?msgId=" + (nextMsgId == null ? 0 : nextMsgId) + "&msgNum=64&type=8";
                    String responseJson = HttpTestUtils.httpRequest(HttpEnum.GET, addressArr[0], Integer.valueOf(addressArr[1]), SUBSCRIBE_ACTION, token, content);
                    log.info("拉取人脸消息：{}", responseJson);
                    MessageResp messageResp = JSONUtil.toBean(responseJson, MessageResp.class);
                    List<String> results = messageResp.getResults();
                    nextMsgId = messageResp.getNextMsgId();

                    if (CollectionUtil.isEmpty(results)) {
                        return;
                    }

                    List<HmsFaceRecord> faceRecordList = new ArrayList<>();
                    Map<String, List<FaceInfo>> map = new HashMap<>();

                    for (int i = 0; i < results.size(); i++) {
                        MessageRespResult messageRespResult = JSONUtil.toBean(results.get(i), MessageRespResult.class);
                        List<MessageRespResult.InfoBean.SimilarFacesBean> similarFaces = messageRespResult.getInfo().getSimilarFaces();
                        if (CollectionUtil.isNotEmpty(similarFaces)) {
                            MessageRespResult.InfoBean.SimilarFacesBean similarFacesBean = similarFaces.stream().max(Comparator.comparing(MessageRespResult.InfoBean.SimilarFacesBean::getSimilarity))
                                    .orElse(null);
                            String recordId = messageRespResult.getInfo().getRecordId();

                            // 相似度保留两位小数
                            double similarity = new BigDecimal(similarFacesBean.getSimilarity() * 100)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            String deviceId = recordId.substring(0, 20);
                            List<FaceInfo> list = map.get(deviceId);
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            FaceInfo faceInfo = new FaceInfo();
                            faceInfo.setName(similarFacesBean.getName());
                            faceInfo.setIdCardNo(similarFacesBean.getIdNumber());
                            faceInfo.setGender(GenderEnum.getNameByCode(similarFacesBean.getGender()));
                            faceInfo.setFaceImgUrl(messageRespResult.getInfo().getFaceImgUrl());
                            faceInfo.setAlarmContent(similarFacesBean.getRepositoryName());
                            faceInfo.setSimilarity(similarity + "%");

                            // 记录保存
                            HmsFaceRecord hmsFaceRecord = new HmsFaceRecord();
                            hmsFaceRecord.setImgUrl(messageRespResult.getInfo().getFaceImgUrl());
                            hmsFaceRecord.setIdCardNo(similarFacesBean.getIdNumber());
                            hmsFaceRecord.setName(similarFacesBean.getName());
                            hmsFaceRecord.setAlarmTime(new Date(messageRespResult.getInfo().getAlarmTime() * 1000L));
                            hmsFaceRecord.setGender(similarFacesBean.getGender());
                            hmsFaceRecord.setCreateTime(new Date());
                            hmsFaceRecord.setScore(similarity);
                            hmsFaceRecord.setAlarmContent(similarFacesBean.getRepositoryName());
                            hmsFaceRecord.setDeviceId(deviceId);
                            hmsFaceRecord.setTargetImgUrl(similarFacesBean.getTargetFaceImgUrl());
                            faceRecordList.add(hmsFaceRecord);

                            // 同一时刻 相同人只报警一次
                            boolean exist = false;
                            for (int k = 0; k < list.size(); k++) {
                                FaceInfo faceInfoItem = list.get(k);
                                if (StrUtil.isNotBlank(faceInfoItem.getIdCardNo()) &&
                                        faceInfoItem.getIdCardNo().equals(faceInfo.getIdCardNo())) {
                                    exist = true;
                                }
                            }
                            if (!exist) {
                                list.add(faceInfo);
                            }
                            map.put(deviceId, list);
                        }
                    }

                    if (CollectionUtil.isNotEmpty(map)) {
                        for (Map.Entry<String, List<FaceInfo>> entry : map.entrySet()) {
                            String queueName = entry.getKey();
                            String faceMessage = JSONUtil.toJsonStr(entry.getValue());
                            log.info("推送人脸消息 队列：{} 消息：{}", "face_" + queueName, faceMessage);
                            mqttOutputService.sendToMqtt("face_" + queueName, faceMessage);
                        }
                        if (CollectionUtil.isNotEmpty(faceRecordList)) {
                            iHmsFaceRecordService.saveBatch(faceRecordList);
                        }
                    }
                },
                triggerContext -> new CronTrigger(faceCron).nextExecutionTime(triggerContext)
        );
    }

    public void logout(String token) {
        String content = "{\"token\":\"" + token + "\"}";
        String s = HttpTestUtils.httpRequest(HttpEnum.POST, "13.65.33.11", 8314, LOGOUT_ACTION, token, content);
        log.info("退出结果:{}", s);
    }


}
