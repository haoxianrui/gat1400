package com.youlai.gat1400.domain;

import lombok.Data;

@Data
public class RegisterRequestObject {

    private RegisterObject RegisterObject;
    @Data
    public static class RegisterObject {
        private String DeviceID;
    }
}
