package com.juxingtech.helmet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动入口
 *
 * @author haoxr
 * @date 2020-05-11
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
@Slf4j
public class HelmetApplication {
    public static void main(String[] args) {

        try {
            SpringApplication.run(HelmetApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("the exception is {}", e.getMessage(), e);
        }
    }
}
