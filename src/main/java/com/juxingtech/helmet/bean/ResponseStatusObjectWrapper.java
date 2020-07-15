package com.juxingtech.helmet.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

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
