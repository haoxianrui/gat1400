package com.youlai.gat1400.domain;

import lombok.Data;

@Data
public class ResponseStatusObjectWrapper {

    private ResponseStatusObject ResponseStatusObject;

    @Data
    public class ResponseStatusObject {
        private String Id;

        private String LocalTime;

        private String RequestURL;

        private int StatusCode;

        private String StatusString;

    }
}
