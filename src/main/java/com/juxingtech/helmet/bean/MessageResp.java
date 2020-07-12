package com.juxingtech.helmet.bean;

import lombok.Data;

import java.util.List;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class MessageResp<T> {

    private Integer totalCount;

    private String nextMsgId;

    private List<T> results;

}
