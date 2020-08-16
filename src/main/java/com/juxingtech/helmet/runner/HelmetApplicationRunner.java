package com.juxingtech.helmet.runner;

import com.juxingtech.helmet.service.pull.PullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
// @Component
@Slf4j
public class HelmetApplicationRunner implements ApplicationRunner, Ordered {

    @Value(value = "${pull-server.ip}")
    private String ip;

    @Value(value = "${pull-server.port}")
    private Integer port;

    @Value(value = "${pull-server.username}")
    private String username;

    @Value(value = "${pull-server.password}")
    private String password;

    @Autowired
    private PullService pullService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 大华平台登录、定时保活、拉取人脸报警信息
        pullService.login(ip, port, username, password);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
