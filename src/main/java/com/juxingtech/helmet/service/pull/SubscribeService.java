package com.juxingtech.helmet.service.pull;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Service
@Slf4j
public class SubscribeService {

    String nextMsgId = "-1";

    @Value(value = "${pull-server.ip}")
    private String ip;

    @Value(value = "${pull-server.port}")
    private Integer port;

    @Autowired
    private MqttOutboundService mqttOutputService;

    public static final String SUBSCRIBE_ADDRESS_ACTION = "/videoService/eventCenter/messages/subscribeAddress";

    public static final String SUBSCRIBE_ACTION = "/videoService/eventCenter/messages/subscribe";

    @Value(value = "${cron}")
    private String cron;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private IHmsFaceRecordService iHmsFaceRecordService;

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

        // 定时拉取人脸识别消息
        threadPoolTaskScheduler.schedule(
                () -> {
                    relayFaceRecognitionMessage(ip, port, token);
                },
                triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext)
        );
    }


    // 人脸识别消息
    public void relayFaceRecognitionMessage(String ip, int port, String token) {
        log.info("消息ID:{}",nextMsgId);
        String content = "?msgId=" + (nextMsgId == null ? 0 : nextMsgId) + "&msgNum=64&type=8";
        String responseJson = HttpTestUtils.httpRequest(HttpEnum.GET, ip, port, SUBSCRIBE_ACTION, token, content);
        // String responseJson = "{\t\"nextMsgId\":\"232968\",\t\"results\":[\t\t\"{\\\"method\\\":\\\"fcs.faceAlarmEx\\\",\\\"id\\\":1,\\\"info\\\":{\\\"age\\\":38,\\\"alarmCode\\\":\\\"520000000000022020071712093535988\\\",\\\"alarmId\\\":\\\"520000000000022020071712093535988\\\",\\\"alarmSource\\\":0,\\\"alarmTime\\\":1594958996,\\\"alarmType\\\":\\\"903\\\",\\\"beard\\\":0,\\\"capTime\\\":1594959097000,\\\"channelCode\\\":\\\"AH33EDBFA1C7RM49V90F26\\\",\\\"channelId\\\":\\\"AH33EDBFA1C7RM49V90F26\\\",\\\"channelName\\\":\\\"交警智能头盔1\\\",\\\"dataSource\\\":0,\\\"deviceId\\\":\\\"AH33EDBFA1C7RM49V81UB0\\\",\\\"emotion\\\":7,\\\"event\\\":\\\"faceAlarmEx\\\",\\\"extParam\\\":\\\"\\\",\\\"eye\\\":1,\\\"faceBottom\\\":100.0,\\\"faceImgId\\\":\\\"515949589964824382\\\",\\\"faceImgUrl\\\":\\\"http://13.65.33.32:38498/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg\\\",\\\"faceImgUrlEx\\\":\\\"/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg\\\",\\\"faceLeft\\\":100.0,\\\"faceRecordId\\\":\\\"130304211911902010610220200717121137000010600001\\\",\\\"faceRight\\\":100.0,\\\"faceTop\\\":100.0,\\\"fringe\\\":2,\\\"gender\\\":1,\\\"glasses\\\":0,\\\"imgUrl\\\":\\\"\\\",\\\"imgUrlEx\\\":\\\"\\\",\\\"mask\\\":0,\\\"mouth\\\":0,\\\"race\\\":0,\\\"recordId\\\":\\\"130304211911902010610220200717121137000010600001\\\",\\\"similarFaces\\\":[{\\\"gender\\\":1,\\\"idNumber\\\":\\\"130304199003078594\\\",\\\"idType\\\":111,\\\"name\\\":\\\"郝先瑞\\\",\\\"repositoryId\\\":\\\"1218217277\\\",\\\"repositoryName\\\":\\\"智慧头盔测试库\\\",\\\"similarity\\\":0.9998998641967773,\\\"targetFaceImgId\\\":\\\"MyNdYt7tjoM11ZELS7SAGtVya9zo7eS9\\\",\\\"targetFaceImgUrl\\\":\\\"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\\\",\\\"targetFaceImgUrlEx\\\":\\\"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\\\",\\\"targetImgUrl\\\":\\\"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\\\",\\\"targetImgUrlEx\\\":\\\"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\\\"}],\\\"status\\\":1,\\\"uid\\\":\\\"13\\\"}}\"\t],\t\"totalCount\":1}\n";
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
                hmsFaceRecord.setAlarmTime(new Date());
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
                String message = JSONUtil.toJsonStr(entry.getValue());
                log.info("推送人脸消息 队列：{} 消息：{}","face_"+queueName,message);
                mqttOutputService.sendToMqtt("face_"+queueName, message);
            }
            if (CollectionUtil.isNotEmpty(faceRecordList)) {
                iHmsFaceRecordService.saveBatch(faceRecordList);
            }
        }
    }
}
