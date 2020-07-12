package com.juxingtech.helmet.bean;

import lombok.Data;

import java.util.List;

@Data
public class ResponseStatusListObject {

    private ResponseStatusList ResponseStatusList;

    @Data
    public class ResponseStatusList {
        private List<ResponseStatus> ResponseStatusObject;
    }

    @Data
    public static class ResponseStatus {
        private String Id;

        private String LocalTime;

        private String RequestURL;

        private String StatusCode;

        private String StatusString;
    }
}
