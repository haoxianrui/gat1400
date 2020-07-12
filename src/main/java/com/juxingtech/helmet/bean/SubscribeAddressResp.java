package com.juxingtech.helmet.bean;

import lombok.Data;

import java.util.List;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class SubscribeAddressResp {

    @Data
    public class MessageInfo{
        private Integer type;
        private String msgId;
    }

    private Integer subscribeAddrType;

    private String subscribeUser;

    private String  subscribePassword;

    private String subscribeAddress;

    private String imageAddress;

    private String uid;

    private List<MessageInfo> initMessageId;

}
