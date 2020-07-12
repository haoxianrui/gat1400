package com.juxingtech.helmet.bean;

import lombok.Data;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class MessageRespResult<T> {
    private Long id;

    private String method;

    private T info;
}
