package com.juxingtech.helmet.domain;

import lombok.Data;

@Data
public class UnRegisterRequestObject {
    private UnRegisterObject UnRegisterObject;

    @Data
    public static class UnRegisterObject {
        private String DeviceID;
    }
}
