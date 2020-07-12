package com.juxingtech.helmet.bean;

import lombok.Data;

@Data
public class ResponseStatusObject {

    private ResponseStatus ResponseStatus;

    @Data
    public static class ResponseStatus {
        private String Id;

        private String LocalTime;

        private String RequestURL;

        private String StatusCode;

        private String StatusString;
    }
}
