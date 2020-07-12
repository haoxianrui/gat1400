package com.juxingtech.helmet.service.pull;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juxingtech.helmet.bean.*;
import com.juxingtech.helmet.common.enums.GenderEnum;
import com.juxingtech.helmet.common.enums.HttpEnum;
import com.juxingtech.helmet.common.util.HttpTestUtils;
import com.juxingtech.helmet.framework.emqtt.service.MqttOutboundService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Service
@Slf4j
public class SubscribeService {

    @Value(value = "${dahua-server.ip}")
    private String ip;

    @Value(value = "${dahua-server.port}")
    private Integer port;

    @Autowired
    private MqttOutboundService mqttOutputService;

    public static final String SUBSCRIBE_ADDRESS_ACTION = "/videoService/eventCenter/messages/subscribeAddress";

    public static final String SUBSCRIBE_ACTION = "/videoService/eventCenter/messages/subscribe";

    @Value(value = "${cron}")
    private String cron;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 人脸识别和车牌识别消息订阅
     */
    public void subscribe(String token) {
        String content = Strings.EMPTY;
        String responseJson = HttpTestUtils.httpRequest(HttpEnum.GET, ip, port, SUBSCRIBE_ADDRESS_ACTION, token, content);
        Assert.notBlank(responseJson, "获取订阅地址失败");
        SubscribeAddressResp subscribeAddressResp = new Gson().fromJson(responseJson, SubscribeAddressResp.class);
        log.info("获取订阅地址结果：{}", subscribeAddressResp);

        List<SubscribeAddressResp.MessageInfo> messageInfos = subscribeAddressResp.getInitMessageId();

        Assert.isTrue(CollectionUtil.isNotEmpty(messageInfos), "获取订阅地址失败");


        log.info("最大消息ID结果:{}", messageInfos);

        String subscribeAddress = subscribeAddressResp.getSubscribeAddress();

        String[] arr = subscribeAddress.split(":");
        String ip = arr[0];
        int port = Integer.valueOf(arr[1]);


      /*  String nextFaceMsgId = messageInfos.stream().filter(item -> item.getType().equals(8))
                .map(messageInfo -> messageInfo.getMsgId()).findFirst().get(); // 人脸消息最大ID*/
        String nextFaceMsgId = "-1";
        // 定时拉取人脸识别消息
        threadPoolTaskScheduler.schedule(
                () -> {
                    log.info("定时拉取人脸识别消息");
                    relayFaceRecognitionMessage(ip, port, token, nextFaceMsgId);
                },
                triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext)
        );

        /*String nextPlateMsgId = messageInfos.stream().filter(item -> item.getType().equals(5))
                .map(messageInfo -> messageInfo.getMsgId()).findFirst().get(); // 车辆消息最大ID*/
        String nextPlateMsgId = "-1";
        // 定时拉取车牌识别消息
        threadPoolTaskScheduler.schedule(
                () -> {
                    log.info("定时拉取车牌识别消息");
                    relayLicensePlateRecognitionMessage(ip, port, token, nextPlateMsgId);
                },
                triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext)
        );
    }


    // 人脸识别消息
    public void relayFaceRecognitionMessage(String ip, int port, String token, String nextMsgId) {
        /*String content = "?msgId=" + (nextMsgId == null ? 0 : nextMsgId) + "&msgNum=4&type=8";
        String responseJson = HttpTestUtils.httpRequest(HttpEnum.GET, ip, port, SUBSCRIBE_ACTION, token, content);*/
        String responseJson = "{\"totalCount\":2,\"nextMsgId\":2,\"results\":[{\"method\":\"vms.xxx\",\"id\":1,\"info\":{\"event\":" +
                "\"faceAlarm\",\"channelId\":\"123456\",\"channelCode\":\"123456\",\"channelName\":\"123456\",\"gpsX\":3333.33333,\"gpsY\":222.222222,\"deviceId\":\"test_face_queue\",\"gbCode\":\"xxx\",\"uid\":\"xxx\",\"faceImgId\":\"xxx\"," +
                "\"faceImgUrl\":\"http://13.75.101.250:9000/face/face1.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200711%2F%2Fs3%2Faws4_request&X-Amz-Date=20200711T035141Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=6c48561b9db65bcde58b2de6ee770913370f493e56a982b51d7b20d163d03d6f\"," +
                "\"imgUrl\":\"xxxxxx\",\"faceImgUrlEx\":\"xxxxxx\",\"imgUrlEx\":\"xxxxxx\",\"alarmCode\":\"xxxx\",\"alarmId\":\"xxxx\"," +
                "\"faceRecordId\":\"xx\",\"recordId\":\"xx\",\"capTime\":1503709064010.0,\"alarmTime\":1503709064,\"faceLeft\":123,\"faceTop\":123,\"faceRight\":234,\"faceBottom\":333,\"extRecordId\":\"xxxxxx\",\"extParam\":\"xxxxxx\",\"dataSource\":1,\"age\":18,\"gender\":1,\"race\":1,\"ethnicCode\":\"01\",\"fringe\":1,\"eye\":1,\"mouth\":1,\"beard\":1,\"mask\":1,\"glasses\":1,\"emotion\":1," +
                "\"similarFaces\":[{\"targetFaceImgId\":\"xxxx\",\"targetFaceImgUrl\":\"xxxxxx\",\"targetImgUrl\":\"xxxxxx\"," +
                "\"similarity\":0.913,\"repositoryId\":\"xxxx\",\"repositoryName\":\"危险人员\",\"idNumber\":\"\"," +
                "\"idType\":0,\"passportType\":\"14\",\"name\":\"xxxx\",\"birthday\":\"1972-02-20\",\"ethnicCode\":\"01\"," +
                "\"gender\":1}]}},{\"method\":\"vms.xxx\",\"id\":1,\"info\":{\"event\":\"faceAlarm\",\"channelId\":\"123456\"," +
                "\"channelCode\":\"123456\",\"channelName\":\"123456\",\"gpsX\":3333.33333,\"gpsY\":222.222222,\"deviceId\":\"test_face_queue\"" +
                ",\"gbCode\":\"xxx\",\"uid\":\"xxx\",\"faceImgId\":\"xxx\"," +
                "\"faceImgUrl\":\"http://13.75.101.250:9000/face/face2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200711%2F%2Fs3%2Faws4_request&X-Amz-Date=20200711T035120Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=1333e5101f21755801b3f9f142b801df154f25d51b51f793b2841cc0a79b33f4\"," +
                "\"imgUrl\":\"xxxxxx\",\"faceImgUrlEx\":\"xxxxxx\",\"imgUrlEx\":\"xxxxxx\",\"alarmCode\":\"xxxx\",\"alarmId\":\"xxxx\",\"" +
                "faceRecordId\":\"xx\",\"recordId\":\"xx\",\"capTime\":1503709064010.0,\"alarmTime\":1503709064,\"faceLeft\":123," +
                "\"faceTop\":123,\"faceRight\":234,\"faceBottom\":333,\"extRecordId\":\"xxxxxx\",\"extParam\":\"xxxxxx\"," +
                "\"dataSource\":1,\"age\":18,\"gender\":1,\"race\":1,\"ethnicCode\":\"01\",\"fringe\":1,\"eye\":1,\"mouth\":1," +
                "\"beard\":1,\"mask\":1,\"glasses\":1,\"emotion\":1,\"similarFaces\":[{\"targetFaceImgId\":\"xxxx\",\"" +
                "targetFaceImgUrl\":\"xxxxxx\",\"targetImgUrl\":\"xxxxxx\",\"similarity\":0.913,\"repositoryId\":\"xxxx\"," +
                "\"repositoryName\":\"危险人员\",\"idNumber\":\"\",\"idType\":1,\"passportType\":\"14\",\"name\":\"xxxx\",\"birthday\":" +
                "\"1972-02-20\",\"ethnicCode\":\"01\",\"gender\":1}]}}]}";

        Type type = new TypeToken<MessageResp<MessageRespResult<FaceInfoResp>>>() {
        }.getType();
        MessageResp<MessageRespResult<FaceInfoResp>> resp = new Gson().fromJson(responseJson, type);

        resp.getResults().forEach(result -> {
            FaceInfoResp faceInfoResp = result.getInfo();
            if (faceInfoResp != null) {
                // emqtt队列名称
                String queueName = faceInfoResp.getDeviceId();
                log.info("推送人脸识别消息 主题：{}", queueName);
                if (StrUtil.isNotBlank(queueName)) {
                    Map<String, Object> map = new HashMap<>();
                    String faceImgUrl = faceInfoResp.getFaceImgUrl();
                    List<FaceInfoResp.SimilarFacesBean> similarFaces = faceInfoResp.getSimilarFaces();
                    if (CollectionUtil.isNotEmpty(similarFaces)) {
                        // 获取相似度最高的人脸信息并推送至emqtt
                        FaceInfoResp.SimilarFacesBean similarFacesBean = similarFaces.stream()
                                .max(Comparator.comparing(FaceInfoResp.SimilarFacesBean::getSimilarity))
                                .orElse(null);
                        String name = similarFacesBean.getName();
                        String gender = GenderEnum.getNameByCode(similarFacesBean.getGender());
                        String similarity = similarFacesBean.getSimilarity() * 100 + "%";
                        String repositoryName = similarFacesBean.getRepositoryName();
                        map.put("name", name);
                        map.put("gender", gender);
                        map.put("similarity", similarity);
                        map.put("alarmTime", "2020-07-06 12:00:00");
                        map.put("alarmContent", repositoryName);
                        map.put("faceImgUrl", faceImgUrl);
                        map.put("type", similarFacesBean.getIdType());
                        // 推送至头盔
                        String message = JSONUtil.toJsonStr(map);
                        log.info("推送人脸识别消息 消息：{}", message);
                        mqttOutputService.sendToMqtt(queueName, message);
                    }
                }
            }
        });

    }

    // 车牌识别消息
    public void relayLicensePlateRecognitionMessage(String ip, int port, String token, String nextMsgId) {
        /*String content = "?msgId=" + (nextMsgId == null ? 0 : nextMsgId) + "&msgNum=4&type=5";
        String responseJson = HttpTestUtils.httpRequest(HttpEnum.GET, ip, port, SUBSCRIBE_ACTION, token, content);*/

        String responseJson = "{\"totalCount\":2,\"nextMsgId\":2,\"results\":[{\"method\":\"vms.xxx\",\"id\":1,\"info\":{\"alarmTime\":1554168989841," +
                "\"capTime\":1554168950000,\"channelCode\":\"ALMURYNQA1B3H3C84OH00C\",\"channelId\":\"ALMURYNQA1B3H3C84OH00C\",\"channelName\":" +
                "\"54-37773aaaaaaaaaaaaaaaaaaaaaaaa\",\"createTime\":1554168989841,\"event\":\"multiAlarm\",\"imgUrl1\":" +
                "\"http://13.75.101.250:9000/license-plate/plate1.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200711%2F%2Fs3%2Faws4_request&X-Amz-Date=20200711T035858Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e7945dea03425f769079ad5fb528e310ca13b9b8826eb4bb33b566e3443485d3\",\"imgUrl2\":" +
                "\"http://13.75.101.250:9000/license-plate/plate1.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200711%2F%2Fs3%2Faws4_request&X-Amz-Date=20200711T035858Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e7945dea03425f769079ad5fb528e310ca13b9b8826eb4bb33b566e3443485d3\",\"objId\":\"KSK806_99\",\"objRecordId\":" +
                "\"MURYNQA1B3H3C84OH00C0220190402013550502560274187\",\"objType\":0," +
                "\"recordId\":\"be7113cb8c8a47e69afd4e964db0db1f\",\"recordType\":1,\"stat\":1,\"surveyRecordId\":\"3f2a92a6424a49a2ba3854a5dd141e24\"," +
                "\"surveySource\":1,\"surveyType\":1,\"tagCode\":\"100801\",\"userChannelCode\":\"test_plate_queue\"}}," +
                "{\"method\":\"vms.xxx\",\"id\":1,\"info\":{\"alarmTime\":1554168989841,\"capTime\":1554168950000,\"channelCode\":\"ALMURYNQA1B3H3C84OH00C\"," +
                "\"channelId\":\"ALMURYNQA1B3H3C84OH00C\",\"channelName\":\"54-37773aaaaaaaaaaaaaaaaaaaaaaaa\",\"createTime\":1554168989841,\"event\":" +
                "\"multiAlarm\",\"imgUrl1\":\"http://13.75.101.250:9000/license-plate/plate2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200711%2F%2Fs3%2Faws4_request&X-Amz-Date=20200711T035959Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=c159035fda3126b7943ce7336064a65065395d47998338a0b946a000f863f191\",\"imgUrl2\":" +
                "\"http://13.75.101.250:9000/license-plate/plate2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200711%2F%2Fs3%2Faws4_request&X-Amz-Date=20200711T040031Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=26c7f6fab9001a77bab743b6709a1dcaf87cabbdd4cec8481836f35af0e82762\",\"objId\":\"KSK806_99\",\"objRecordId\":" +
                "\"MURYNQA1B3H3C84OH00C0220190402013550502560274187\",\"objType\":1,\"recordId\":\"be7113cb8c8a47e69afd4e964db0db1f\",\"recordType\":1,\"stat\":1," +
                "\"surveyRecordId\":\"3f2a92a6424a49a2ba3854a5dd141e24\",\"surveySource\":1,\"surveyType\":1,\"tagCode\":\"100801\",\"userChannelCode\":" +
                "\"test_plate_queue\"}}]}";

        Type type = new TypeToken<MessageResp<MessageRespResult<LicensePlateInfoResp>>>() {
        }.getType();
        MessageResp<MessageRespResult<LicensePlateInfoResp>> resp = new Gson().fromJson(responseJson, type);
        // 推送至头盔
        resp.getResults().forEach(result -> {
            LicensePlateInfoResp plateResultInfo = result.getInfo();

            // emqtt队列名称
            String queueName = plateResultInfo.getUserChannelCode();
            log.info("推送车牌识别消息 主题：{}", queueName);
            if (StrUtil.isNotBlank(queueName)) {
                long alarmTime = plateResultInfo.getAlarmTime();
                String imgUrl1 = plateResultInfo.getImgUrl1();
                Map<String, Object> map = new HashMap<>();
                map.put("licensePlateNumber", "沪A88888");
                map.put("color", "蓝");
                map.put("imgUrl", imgUrl1);
                map.put("alarmTime", "2020-07-06 12:00:00");
                map.put("alarmContent", "车辆未年检");
                map.put("type", plateResultInfo.getObjType());
                String message = JSONUtil.toJsonStr(map);
                log.info("推送车牌识别消息 消息：{}", message);
                mqttOutputService.sendToMqtt(queueName, message);
            }
        });
    }
}
