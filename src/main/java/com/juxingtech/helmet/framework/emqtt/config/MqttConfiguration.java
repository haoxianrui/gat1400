package com.juxingtech.helmet.framework.emqtt.config;

import com.juxingtech.helmet.framework.emqtt.callback.PushCallBack;
import com.juxingtech.helmet.service.IHmsFaceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

 @Configuration
@Slf4j
public class MqttConfiguration {

    @Autowired
    private MqttProperties mqttProperties;
    /**
     * 工厂配置
     */
    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(mqttProperties.getUsername());
        mqttConnectOptions.setPassword(mqttProperties.getPassword().toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{mqttProperties.getUrl()});
        mqttConnectOptions.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        mqttConnectOptions.setCleanSession(mqttProperties.isCleanSession());
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        try {
            String url = mqttProperties.getUrl();
            String pubClientId = mqttProperties.getPubClientId();
            PushCallBack pushCallBack = new PushCallBack();
            factory.getClientInstance(url, pubClientId).setCallback(pushCallBack);
            factory.getAsyncClientInstance(url, pubClientId).setCallback(pushCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory;
    }

    /**
     * 接出配置
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                mqttProperties.getPubClientId(),
                mqttClientFactory());
        messageHandler.setAsync(mqttProperties.isAsync());
        messageHandler.setDefaultTopic(mqttProperties.getDefaultTopic());
        messageHandler.setDefaultQos(mqttProperties.getDefaultQos());
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 接入配置
     *
     * @return
     */
    @Bean
    public MessageProducer inbound() {
        String[] subTopics = mqttProperties.getSubTopics().split(",");
        MqttPahoMessageDrivenChannelAdapter adapter;
        adapter = new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getUrl(),
                mqttProperties.getSubClientId(),
                mqttClientFactory(),
                subTopics);
        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(mqttProperties.getDefaultQos());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    /**
     * 接收前端头盔传递消息并处理
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            String payload = message.getPayload().toString();
            switch (topic) {
                default:
                    log.info("{}：丢弃消息 {}", topic, payload);
            }
        };
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

}
