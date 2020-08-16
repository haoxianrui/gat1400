package com.juxingtech.helmet.framework.emqtt.callback;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;


@Slf4j
// @Service
public class PushCallBack implements MqttCallback {

    @Override
    public void connectionLost(Throwable throwable) {
        log.info("当前链接断开"+throwable.getMessage());
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.info("接受消息的主题:"+s);
        log.info("接受消息Qos:"+mqttMessage.getQos());
        log.info("接受消息内容:"+ new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("token内容为:"+iMqttDeliveryToken.isComplete());
    }
}
