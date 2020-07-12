package com.juxingtech.helmet.framework.emqtt.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.mqtt")
@Component
public class MqttProperties {

    private String url;

    private String username;

    private String password;

    private long completionTimeout;

    private int keepAliveInterval;

    private int defaultQos;

    private String defaultTopic;

    private String subTopics;

    private String pubClientId;

    private String subClientId;

    private boolean cleanSession;

    private boolean async;

}
