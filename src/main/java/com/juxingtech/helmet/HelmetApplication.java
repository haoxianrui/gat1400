package com.juxingtech.helmet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动入口
 *
 * @author haoxr
 * @date 2020-05-11
 **/
@SpringBootApplication
@EnableScheduling
public class HelmetApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelmetApplication.class, args);
    }

}
