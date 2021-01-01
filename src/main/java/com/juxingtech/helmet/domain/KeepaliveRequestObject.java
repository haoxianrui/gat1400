package com.juxingtech.helmet.domain;

import lombok.Data;

@Data
public class KeepaliveRequestObject {

    private KeepaliveObject KeepaliveObject;
    @Data
    public static class KeepaliveObject {
        private String DeviceID;
    }
}
